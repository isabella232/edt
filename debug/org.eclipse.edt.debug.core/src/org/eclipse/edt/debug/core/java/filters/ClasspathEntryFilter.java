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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.NLS;

/**
 * Filter that uses classpath entries to build the list of classes to be filtered. The classpath entries must be fully resolved, meaning variable
 * entries will be ignored.
 */
public abstract class ClasspathEntryFilter extends AbstractTypeFilter
{
	/**
	 * Flag indicating if the common classes were already processed for this filter.
	 */
	private boolean commonClassesProcessed;
	
	/**
	 * Class map for entries which are always the same among all debug targets.
	 */
	protected Map<String, Object> commonClassesToFilter;
	
	/**
	 * Class map for entries which can differ among debug targets.
	 */
	protected Map<Object, Map<String, Object>> targetClassMap = new HashMap<Object, Map<String, Object>>();
	
	@Override
	public boolean filter( IJavaStackFrame frame, IEGLJavaDebugTarget target )
	{
		try
		{
			String typeName = frame.getReferenceType().getName(); // This way avoids '<>' from generics.
			
			// Try the common classes.
			if ( commonClassesToFilter != null && commonClassesToFilter.containsKey( typeName ) )
			{
				return true;
			}
			
			// Try the target-specific classes.
			Object targetKey = getTargetClassmapKey( target );
			if ( targetKey != null )
			{
				Map<String, Object> classMap = targetClassMap.get( targetKey );
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
		super.dispose();
		this.commonClassesToFilter = null;
		this.targetClassMap = null;
	}
	
	@Override
	public void dispose( IEGLJavaDebugTarget target )
	{
		super.dispose( target );
		if ( targetClassMap != null )
		{
			targetClassMap.remove( target );
		}
	}
	
	@Override
	public synchronized void initialize( IEGLJavaDebugTarget target )
	{
		IJavaProject jp = null;
		if ( !commonClassesProcessed )
		{
			commonClassesProcessed = true;
			try
			{
				IClasspathEntry[] entries = getCommonClasspathEntries();
				if ( entries != null && entries.length > 0 )
				{
					jp = JavaRuntime.getJavaProject( target.getJavaDebugTarget().getLaunch().getLaunchConfiguration() );
					commonClassesToFilter = new HashMap<String, Object>( 100 );
					processEntries( entries, jp, commonClassesToFilter );
				}
			}
			catch ( Exception e )
			{
				EDTDebugCorePlugin.log( e );
			}
		}
		
		Object targetKey = getTargetClassmapKey( target );
		if ( targetKey == null || targetClassMap.containsKey( targetKey ) )
		{
			return;
		}
		
		targetClassMap.put( targetKey, null );
		try
		{
			IClasspathEntry[] entries = getTargetClasspathEntries( target );
			if ( entries != null && entries.length > 0 )
			{
				if ( jp == null )
				{
					jp = JavaRuntime.getJavaProject( target.getJavaDebugTarget().getLaunch().getLaunchConfiguration() );
				}
				HashMap<String, Object> targetClasses = new HashMap<String, Object>( 100 );
				targetClassMap.put( target, targetClasses );
				processEntries( entries, jp, targetClasses );
			}
		}
		catch ( Exception e )
		{
			EDTDebugCorePlugin.log( e );
		}
	}
	
	protected void processEntries( IClasspathEntry[] entries, IJavaProject project, Map<String, Object> classMap ) throws CoreException
	{
		for ( IClasspathEntry entry : entries )
		{
			switch ( entry.getEntryKind() )
			{
				case IClasspathEntry.CPE_LIBRARY:
					processLibraryEntry( entry, classMap );
					break;
				
				case IClasspathEntry.CPE_CONTAINER:
					processContainerEntry( entry, project, classMap );
					break;
				
				case IClasspathEntry.CPE_SOURCE:
					processSourceEntry( entry, classMap );
					break;
				
				case IClasspathEntry.CPE_PROJECT:
					IProject depProject = ResourcesPlugin.getWorkspace().getRoot().getProject( entry.getPath().lastSegment() );
					if ( depProject.isAccessible() && depProject.hasNature( JavaCore.NATURE_ID ) )
					{
						IJavaProject jp = JavaCore.create( depProject );
						processEntries( jp.getResolvedClasspath( true ), jp, classMap );
					}
					break;
				
				default:
					EDTDebugCorePlugin.log( new Status( IStatus.WARNING, EDTDebugCorePlugin.PLUGIN_ID, NLS.bind(
							EDTDebugCoreMessages.TypeFilterClasspathEntryNotSupported, new Object[] { entry.getEntryKind(), getId() } ) ) );
					break;
			}
		}
	}
	
	protected void processSourceEntry( IClasspathEntry entry, Map<String, Object> classMap )
	{
		IPath output = entry.getOutputLocation();
		if ( output == null )
		{
			// Check Java project's default.
			try
			{
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject( entry.getPath().segment( 0 ) );
				if ( project.isAccessible() && project.hasNature( JavaCore.NATURE_ID ) )
				{
					output = JavaCore.create( project ).getOutputLocation();
				}
			}
			catch ( CoreException ce )
			{
				EDTDebugCorePlugin.log( ce );
			}
		}
		
		if ( output != null )
		{
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember( output );
			if ( resource != null )
			{
				IPath abs = resource.getLocation();
				if ( abs != null )
				{
					File file = abs.toFile();
					if ( file.isDirectory() )
					{
						String rootPath = file.getPath();
						if ( !rootPath.endsWith( File.separator ) )
						{
							rootPath = rootPath + File.separator;
						}
						processDirectory( file, rootPath, classMap );
					}
				}
			}
		}
	}
	
	protected void processLibraryEntry( IClasspathEntry entry, Map<String, Object> classMap )
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
				processDirectory( file, rootPath, classMap );
			}
			else if ( file.getName().endsWith( ".jar" ) ) //$NON-NLS-1$
			{
				processJar( file, null, classMap );
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
	
	protected void processDirectory( File dir, String pathRoot, Map<String, Object> classMap )
	{
		for ( File file : dir.listFiles() )
		{
			if ( file.isFile() )
			{
				String path = file.getPath();
				if ( path.endsWith( ".class" ) ) //$NON-NLS-1$
				{
					String className = path.substring( pathRoot.length(), path.length() - 6 ).replace( File.separatorChar, '.' );
					classMap.put( className, null );
				}
			}
			else
			{
				processDirectory( file, pathRoot, classMap );
			}
		}
	}
	
	/**
	 * Subclasses are free to override this to provide more specific caching.
	 */
	protected void processContainerEntry( IClasspathEntry entry, IJavaProject project, Map<String, Object> classMap )
	{
		if ( project == null )
		{
			// Can't calulate the entry without an IJavaProject.
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
	 * Returns the array of common classpath entries to be processed. This will only be called once.
	 * 
	 * @return the common classpath entries, possibly null.
	 * @throws CoreException
	 */
	protected IClasspathEntry[] getCommonClasspathEntries() throws CoreException
	{
		return null;
	}
	
	/**
	 * Returns the array of classpath entries that are specific to the debug target. This will be called only once per target.
	 * 
	 * @param target The EGL debug target.
	 * @return the target-specific classpath entries, possibly null.
	 * @throws CoreException
	 */
	protected IClasspathEntry[] getTargetClasspathEntries( IEGLJavaDebugTarget target ) throws CoreException
	{
		return null;
	}
	
	/**
	 * Returns the key for the target map. By default the target will be the key, but subclasses can use some other caching mechanism (for example if
	 * multiple targets have the same entries, like if they're using the same JRE).
	 * 
	 * @param target The EGL debug target.
	 * @return the key for the target map, or null if no target processing should be performed.
	 */
	protected Object getTargetClassmapKey( IEGLJavaDebugTarget target )
	{
		return target;
	}
}
