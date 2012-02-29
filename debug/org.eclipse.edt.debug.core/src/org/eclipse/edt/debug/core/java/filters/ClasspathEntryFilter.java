/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.core.java.filters;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.edt.debug.core.EDTDebugCoreMessages;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.debug.core.java.IEGLJavaDebugTarget;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.NLS;

/**
 * Filter that uses classpath entries to build the list of classes to be filtered. Only {@link IClasspathEntry#CPE_LIBRARY} and
 * {@link IClasspathEntry#CPE_CONTAINER} types are supported at this time.
 */
public abstract class ClasspathEntryFilter extends AbstractTypeFilter
{
	/**
	 * Placeholder for when processing a container entry failed.
	 */
	protected static final Object COULD_NOT_PARSE_CONTAINER = new Object();
	
	/**
	 * Class map for libraries which are the same among all debug targets.
	 */
	protected Map<String, Object> libraryClassesToFilter;
	
	/**
	 * Map of debug targets to cached objects, the type of which depend on subclasses.
	 * 
	 * @see #getContainerClassMapKey(IClasspathEntry, IEGLJavaDebugTarget)
	 */
	private Map<IEGLJavaDebugTarget, Object> targetContainerMap;
	
	/**
	 * Class map for container entries.
	 * 
	 * @see #targetContainerMap
	 */
	private Map<Object, Map<String, Object>> containerClassMap;
	
	@Override
	public boolean filter( IJavaStackFrame frame, IEGLJavaDebugTarget target )
	{
		try
		{
			String typeName = frame.getReferenceType().getName(); // This way avoids '<>' from generics.
			
			// Try the libraries.
			if ( libraryClassesToFilter != null && libraryClassesToFilter.containsKey( typeName ) )
			{
				return true;
			}
			
			// Try the containers.
			if ( targetContainerMap != null && containerClassMap != null )
			{
				Object key = targetContainerMap.get( target );
				if ( key == null || key == COULD_NOT_PARSE_CONTAINER )
				{
					return false;
				}
				
				Map<String, Object> classMap = containerClassMap.get( key );
				if ( classMap != null )
				{
					return classMap.containsKey( typeName );
				}
			}
		}
		catch ( DebugException de )
		{
		}
		return false;
	}
	
	@Override
	public void dispose()
	{
		this.libraryClassesToFilter = null;
		this.targetContainerMap = null;
		this.containerClassMap = null;
	}
	
	@Override
	public void dispose( IEGLJavaDebugTarget target )
	{
		super.dispose( target );
		if ( targetContainerMap != null )
		{
			Object removedKey = targetContainerMap.remove( target );
			if ( containerClassMap != null && removedKey == Integer.valueOf( target.hashCode() ) )
			{
				// The class map is specific to this target (default implementation), remove it too.
				containerClassMap.remove( removedKey );
			}
		}
	}
	
	@Override
	public synchronized void initialize( IEGLJavaDebugTarget target )
	{
		// For library entries they only get initialized the once, but for containers entries they are calculated
		// based on the root Java project and can vary from project to project.
		boolean needToInitLibraries = false;
		if ( libraryClassesToFilter == null )
		{
			needToInitLibraries = true;
		}
		
		try
		{
			IClasspathEntry[] entries = getClasspathEntries( target );
			if ( entries == null )
			{
				entries = new IClasspathEntry[ 0 ];
			}
			
			if ( entries.length == 0 )
			{
				return;
			}
			
			for ( IClasspathEntry entry : entries )
			{
				switch ( entry.getEntryKind() )
				{
					case IClasspathEntry.CPE_LIBRARY:
						if ( needToInitLibraries )
						{
							if ( libraryClassesToFilter == null )
							{
								libraryClassesToFilter = new HashMap<String, Object>( 100 );
							}
							processLibraryEntry( entry, target );
						}
						break;
					
					case IClasspathEntry.CPE_CONTAINER:
						if ( targetContainerMap == null )
						{
							targetContainerMap = new HashMap<IEGLJavaDebugTarget, Object>();
						}
						if ( containerClassMap == null )
						{
							containerClassMap = new HashMap<Object, Map<String, Object>>( 100 );
						}
						processContainerEntry( entry, target );
						break;
					
					default:
						EDTDebugCorePlugin.log( new Status( IStatus.WARNING, EDTDebugCorePlugin.PLUGIN_ID, NLS.bind(
								EDTDebugCoreMessages.TypeFilterClasspathEntryNotSupported, new Object[] { entry.getEntryKind(), getId() } ) ) );
						break;
				}
			}
		}
		catch ( CoreException ce )
		{
			EDTDebugCorePlugin.log( ce );
		}
	}
	
	protected void processLibraryEntry( IClasspathEntry entry, IEGLJavaDebugTarget target )
	{
		File file = entry.getPath().toFile();
		if ( file.exists() )
		{
			if ( file.isDirectory() )
			{
				String rootPath = file.getPath();
				if ( !rootPath.endsWith( File.separator ) )
				{
					rootPath = rootPath + File.separator;
				}
				processLibraryDirectory( file, rootPath );
			}
			else if ( file.getName().endsWith( ".jar" ) ) //$NON-NLS-1$
			{
				processJar( file, null, libraryClassesToFilter );
			}
		}
	}
	
	protected void processJar( File file, String rootPath, Map<String, Object> classMap )
	{
		int rootPathLen = rootPath == null
				? 0
				: rootPath.length();
		try
		{
			JarFile jar = new JarFile( file );
			Enumeration<JarEntry> entries = jar.entries();
			while ( entries.hasMoreElements() )
			{
				JarEntry entry = entries.nextElement();
				if ( !entry.isDirectory() )
				{
					String path = entry.getName();
					if ( path.endsWith( ".class" ) ) //$NON-NLS-1$
					{
						// Jar always uses '/' as the separator
						String className = path.substring( rootPathLen, path.length() - 6 ).replace( '/', '.' );
						classMap.put( className, null );
					}
				}
			}
		}
		catch ( IOException ioe )
		{
			EDTCoreIDEPlugin.log( ioe );
		}
	}
	
	protected void processLibraryDirectory( File dir, String pathRoot )
	{
		for ( File file : dir.listFiles() )
		{
			if ( file.isFile() )
			{
				String path = file.getPath();
				if ( path.endsWith( ".class" ) ) //$NON-NLS-1$
				{
					String className = path.substring( pathRoot.length(), path.length() - 6 ).replace( File.separatorChar, '.' );
					libraryClassesToFilter.put( className, null );
				}
			}
			else
			{
				processLibraryDirectory( file, pathRoot );
			}
		}
	}
	
	/**
	 * Subclasses are free to override this to provide more specific caching.
	 */
	protected void processContainerEntry( IClasspathEntry entry, IEGLJavaDebugTarget target )
	{
		Object classMapKey = targetContainerMap.get( target );
		if ( classMapKey == null )
		{
			try
			{
				classMapKey = getContainerClassMapKey( target );
			}
			catch ( CoreException ce )
			{
				EDTDebugCorePlugin.log( ce );
			}
			
			if ( classMapKey == null )
			{
				classMapKey = COULD_NOT_PARSE_CONTAINER;
			}
			
			targetContainerMap.put( target, classMapKey );
		}
		
		if ( classMapKey != COULD_NOT_PARSE_CONTAINER )
		{
			Map<String, Object> classMap = containerClassMap.get( classMapKey );
			if ( classMap == null )
			{
				classMap = new HashMap<String, Object>( 200 );
				containerClassMap.put( classMapKey, classMap );
				
				try
				{
					processClasspath( entry, target, classMap );
				}
				catch ( CoreException ce )
				{
					EDTDebugCorePlugin.log( ce );
				}
			}
		}
	}
	
	protected void processClasspath( IClasspathEntry entry, IEGLJavaDebugTarget target, Map<String, Object> classMap ) throws CoreException
	{
		IJavaProject project = JavaRuntime.getJavaProject( target.getJavaDebugTarget().getLaunch().getLaunchConfiguration() );
		if ( project == null )
		{
			return;
		}
		
		IPackageFragmentRoot[] roots = project.findPackageFragmentRoots( entry );
		for ( IPackageFragmentRoot root : roots )
		{
			try
			{
				for ( IJavaElement element : root.getChildren() )
				{
					if ( element.getElementType() == IJavaElement.PACKAGE_FRAGMENT )
					{
						try
						{
							IPackageFragment pkg = (IPackageFragment)element;
							StringBuilder pkgBuf = new StringBuilder( 50 );
							IJavaElement current = pkg;
							while ( current != null && current.getElementType() == IJavaElement.PACKAGE_FRAGMENT )
							{
								if ( pkgBuf.length() > 0 )
								{
									pkgBuf.insert( 0, '.' );
								}
								pkgBuf.insert( 0, current.getElementName() );
								current = current.getParent();
							}
							
							String pkgString = pkgBuf.toString();
							IClassFile[] classes = pkg.getClassFiles();
							for ( IClassFile file : classes )
							{
								String className = file.getElementName();
								className = className.substring( 0, className.length() - 6 ); // remove ".class"
								StringBuilder buf = new StringBuilder( pkgString.length() + className.length() + 1 );
								buf.append( pkgString );
								if ( pkgString.length() > 0 )
								{
									buf.append( '.' );
								}
								buf.append( className );
								classMap.put( buf.toString(), null );
							}
						}
						catch ( JavaModelException jme )
						{
							EDTDebugCorePlugin.log( jme );
						}
					}
				}
			}
			catch ( JavaModelException jme )
			{
				EDTDebugCorePlugin.log( jme );
			}
		}
	}
	
	/**
	 * Subclasses should override this if they wish to use an alternate key for the class map. By default this will return the hash code of the
	 * target. A reason you want to override is if the class map is based on an attribute of the target, such as its active JRE, which can be the same
	 * for multiple targets (and you'd want it to stay cached after the target ends).
	 * 
	 * @param target The EGL debug target.
	 * @return the key used for storing the debug target's class map.
	 * @throws CoreException
	 */
	protected Object getContainerClassMapKey( IEGLJavaDebugTarget target ) throws CoreException
	{
		return target.hashCode();
	}
	
	/**
	 * Returns the array of classpath entries that are to be processed. This will only be called once and the result will be cached.
	 * 
	 * @param target The EGL debug target.
	 * @return the classpath entries to be processed.
	 * @throws CoreException
	 */
	protected abstract IClasspathEntry[] getClasspathEntries( IEGLJavaDebugTarget target ) throws CoreException;
}
