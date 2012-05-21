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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * A filter that uses the classpath of a project from within the workspace. Currently only changes to source entries are updated in the cache.
 */
public abstract class WorkspaceProjectClassFilter extends ClasspathEntryFilter implements IElementChangedListener
{
	public WorkspaceProjectClassFilter()
	{
		JavaCore.addElementChangedListener( this, ElementChangedEvent.POST_CHANGE );
	}
	
	@Override
	public void dispose()
	{
		JavaCore.removeElementChangedListener( this );
		super.dispose();
	}
	
	@Override
	protected IClasspathEntry[] getCommonClasspathEntries()
	{
		String[] projects = getProjectNames();
		if ( projects != null && projects.length > 0 )
		{
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
			for ( String project : projects )
			{
				IProject proj = root.getProject( project );
				try
				{
					if ( proj.isAccessible() && proj.hasNature( JavaCore.NATURE_ID ) )
					{
						for ( IClasspathEntry entry : JavaCore.create( proj ).getResolvedClasspath( true ) )
						{
							switch ( entry.getEntryKind() )
							{
								case IClasspathEntry.CPE_LIBRARY:
									if ( includeLibraries() )
									{
										list.add( entry );
									}
									break;
								
								case IClasspathEntry.CPE_PROJECT:
									if ( includeReferencedProjects() )
									{
										list.add( entry );
									}
									break;
								
								case IClasspathEntry.CPE_CONTAINER:
									if ( includeContainers() )
									{
										list.add( entry );
									}
									break;
								
								case IClasspathEntry.CPE_SOURCE:
									if ( includeSource() )
									{
										list.add( entry );
									}
									break;
							}
						}
					}
				}
				catch ( CoreException ce )
				{
					EDTDebugCorePlugin.log( ce );
				}
			}
			return list.toArray( new IClasspathEntry[ list.size() ] );
		}
		return null;
	}
	
	@Override
	public void elementChanged( ElementChangedEvent event )
	{
		if ( event.getDelta() != null )
		{
			String[] projectNames = getProjectNames();
			for ( IJavaElementDelta kid : event.getDelta().getAffectedChildren() )
			{
				if ( kid.getElement() != null && kid.getElement().getElementType() == IJavaElement.JAVA_PROJECT && kid.getAffectedChildren() == null
						|| kid.getAffectedChildren().length == 0 )
				{
					// When a monitored project is deleted or added, recalculate the cache. A rename will come in as delete & add.
					if ( kid.getKind() == IJavaElementDelta.ADDED || kid.getKind() == IJavaElementDelta.REMOVED )
					{
						for ( int i = 0; i < projectNames.length; i++ )
						{
							if ( projectNames[ i ].equals( kid.getElement().getElementName() ) )
							{
								commonClassesToFilter = new HashMap<String, Object>( 100 );
								try
								{
									processEntries( getCommonClasspathEntries(), (IJavaProject)kid.getElement(), commonClassesToFilter );
								}
								catch ( CoreException ce )
								{
									EDTDebugCorePlugin.log( ce );
								}
								
								// We've completely recalculated, no other deltas matter.
								return;
							}
						}
					}
				}
				else
				{
					IResource resource = kid.getElement().getResource();
					if ( resource != null )
					{
						for ( int i = 0; i < projectNames.length; i++ )
						{
							if ( projectNames[ i ].equals( resource.getProject().getName() ) )
							{
								processDelta( kid );
								break;
							}
						}
					}
				}
			}
		}
	}
	
	protected void processDelta( IJavaElementDelta delta )
	{
		IJavaElementDelta[] kids = delta.getAffectedChildren();
		if ( kids == null || kids.length == 0 )
		{
			IJavaElement element = delta.getElement();
			if ( element != null && element.getElementType() == IJavaElement.COMPILATION_UNIT )
			{
				try
				{
					switch ( delta.getKind() )
					{
						case IJavaElementDelta.ADDED:
							compilationUnitAdded( delta, (ICompilationUnit)delta.getElement() );
							break;
						case IJavaElementDelta.REMOVED:
							compilationUnitRemoved( delta, (ICompilationUnit)delta.getElement() );
							break;
						case IJavaElementDelta.CHANGED:
							compilationUnitChanged( delta, (ICompilationUnit)delta.getElement() );
							break;
					}
				}
				catch ( JavaModelException jme )
				{
					EDTDebugCorePlugin.log( jme );
				}
			}
			else if ( element != null && element.getElementType() == IJavaElement.PACKAGE_FRAGMENT )
			{
				try
				{
					switch ( delta.getKind() )
					{
						case IJavaElementDelta.ADDED:
							packageFragmentAdded( delta, (IPackageFragment)element );
							break;
						case IJavaElementDelta.REMOVED:
							packageFragmentRemoved( delta, (IPackageFragment)element );
							break;
					}
				}
				catch ( JavaModelException jme )
				{
					EDTDebugCorePlugin.log( jme );
				}
			}
		}
		else
		{
			for ( int i = 0; i < kids.length; i++ )
			{
				processDelta( kids[ i ] );
			}
		}
	}
	
	protected void compilationUnitAdded( IJavaElementDelta delta, ICompilationUnit cu ) throws JavaModelException
	{
		for ( IType type : cu.getAllTypes() )
		{
			commonClassesToFilter.put( type.getFullyQualifiedName(), null );
		}
	}
	
	protected void compilationUnitRemoved( IJavaElementDelta delta, ICompilationUnit cu ) throws JavaModelException
	{
		if ( cu.exists() )
		{
			for ( IType type : cu.getAllTypes() )
			{
				commonClassesToFilter.remove( type.getFullyQualifiedName() );
			}
		}
		else
		{
			// It's already been deleted, so we can't get any of its children. We'll remove the main type, then iterate over the keys
			// to see if there's any inner classes to remove.
			StringBuilder buf = new StringBuilder( 50 );
			IJavaElement current = cu.getParent();
			while ( current != null && current.getElementType() == IJavaElement.PACKAGE_FRAGMENT )
			{
				if ( buf.length() > 0 )
				{
					buf.insert( 0, '.' );
				}
				buf.insert( 0, current.getElementName() );
				current = current.getParent();
			}
			if ( buf.length() > 0 )
			{
				buf.append( '.' );
			}
			buf.append( cu.getElementName().substring( 0, cu.getElementName().length() - 5 ) ); // Remove ".java"
			commonClassesToFilter.remove( buf.toString() );
			
			buf.append( '$' );
			String keyPrefix = buf.toString();
			for ( Iterator<String> it = commonClassesToFilter.keySet().iterator(); it.hasNext(); )
			{
				if ( it.next().startsWith( keyPrefix ) )
				{
					it.remove();
				}
			}
		}
	}
	
	protected void compilationUnitChanged( IJavaElementDelta delta, ICompilationUnit cu ) throws JavaModelException
	{
		if ( (delta.getFlags() & IJavaElementDelta.F_PRIMARY_RESOURCE) != 0 )
		{
			// Inner classes might have changed - remove and then re-add.
			compilationUnitRemoved( delta, cu );
			compilationUnitAdded( delta, cu );
		}
	}
	
	protected void packageFragmentAdded( IJavaElementDelta delta, IPackageFragment fragment ) throws JavaModelException
	{
		for ( ICompilationUnit cu : fragment.getCompilationUnits() )
		{
			compilationUnitAdded( delta, cu );
		}
	}
	
	protected void packageFragmentRemoved( IJavaElementDelta delta, IPackageFragment fragment ) throws JavaModelException
	{
		if ( fragment.exists() )
		{
			for ( ICompilationUnit cu : fragment.getCompilationUnits() )
			{
				compilationUnitRemoved( delta, cu );
			}
		}
		else
		{
			// It's already been deleted, so we can't get any of its children. We'll iterate over the keys removing those inside this fragment.
			StringBuilder buf = new StringBuilder( 50 );
			IJavaElement current = fragment;
			while ( current != null && current.getElementType() == IJavaElement.PACKAGE_FRAGMENT )
			{
				if ( buf.length() > 0 )
				{
					buf.insert( 0, '.' );
				}
				buf.insert( 0, current.getElementName() );
				current = current.getParent();
			}
			
			if ( buf.length() > 0 )
			{
				buf.append( '.' );
				String keyPrefix = buf.toString();
				for ( Iterator<String> it = commonClassesToFilter.keySet().iterator(); it.hasNext(); )
				{
					if ( it.next().startsWith( keyPrefix ) )
					{
						it.remove();
					}
				}
			}
		}
	}
	
	/**
	 * @return true if dependent project classpaths should be included; default is false, and subclasses may override this.
	 */
	protected boolean includeReferencedProjects()
	{
		return false;
	}
	
	/**
	 * @return true if library classpath entries should be included; default is false, and subclasses may override this.
	 */
	protected boolean includeLibraries()
	{
		return false;
	}
	
	/**
	 * @return true if container classpath entries should be included; default is false, and subclasses may override this.
	 */
	protected boolean includeContainers()
	{
		return false;
	}
	
	/**
	 * @return true if source classpath entries should be included; default is true, and subclasses may override this.
	 */
	protected boolean includeSource()
	{
		return true;
	}
	
	/**
	 * @return the names of the workspace projects whose classes should be filtered.
	 */
	public abstract String[] getProjectNames();
}
