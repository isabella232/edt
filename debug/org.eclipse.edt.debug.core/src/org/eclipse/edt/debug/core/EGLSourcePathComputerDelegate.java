/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.core;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.AbstractSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.ArchiveSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.ExternalArchiveSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;

public abstract class EGLSourcePathComputerDelegate implements ISourcePathComputerDelegate
{
	protected void buildContainers( IEGLProject project, List allEntries, Set<IEGLProject> seen )
	{
		if ( seen.contains( project ) )
		{
			return;
		}
		seen.add( project );
		
		try
		{
			// Add all referenced EGL projects, EGLARS, and source folders.
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IEGLPathEntry[] entries = project.getResolvedEGLPath( true );
			for ( int i = 0; i < entries.length; i++ )
			{
				switch ( entries[ i ].getEntryKind() )
				{
					case IEGLPathEntry.CPE_PROJECT:
					{
						IResource member = root.findMember( entries[ i ].getPath() );
						if ( member != null && member.getType() == IResource.PROJECT )
						{
							IProject p = (IProject)member;
							try
							{
								if ( p.hasNature( EGLCore.NATURE_ID ) )
								{
									buildContainers( (IEGLProject)EGLCore.create( p ), allEntries, seen );
								}
							}
							catch ( CoreException e )
							{
							}
						}
						break;
					}
					
					case IEGLPathEntry.CPE_SOURCE:
					{
						IPackageFragmentRoot pkgRoot = project.getPackageFragmentRoot( entries[ i ].getPath() );
						if ( pkgRoot != null )
						{
							EGLPackageFragmentRootSourceContainer container = new EGLPackageFragmentRootSourceContainer( pkgRoot );
							if ( !allEntries.contains( container ) )
							{
								allEntries.add( container );
							}
						}
						else
						{
							IResource member = root.findMember( entries[ i ].getPath() );
							if ( member != null && member.getType() == IResource.FOLDER )
							{
								FolderSourceContainer container = new FolderSourceContainer( (IFolder)member, true );
								if ( !allEntries.contains( container ) )
								{
									allEntries.add( container );
								}
							}
						}
						break;
					}
					
					case IEGLPathEntry.CPE_LIBRARY:
					{
						// Only add if there was source attachment. The debug 'no source found' editor is better than the egl version - it lets you
						// edit source lookup.
						IPath sourcePath = entries[ i ].getSourceAttachmentPath();
						if ( sourcePath != null && !sourcePath.isEmpty() )
						{
							IPackageFragmentRoot pkgRoot = project.getPackageFragmentRoot( entries[ i ].getPath() );
							if ( pkgRoot != null )
							{
								EGLPackageFragmentRootSourceContainer container = new EGLPackageFragmentRootSourceContainer( pkgRoot );
								if ( !allEntries.contains( container ) )
								{
									allEntries.add( container );
								}
							}
							else
							{
								IResource file = root.findMember( sourcePath );
								AbstractSourceContainer container;
								if ( file != null && file.getType() == IResource.FILE )
								{
									container = new ArchiveSourceContainer( (IFile)file, true );
								}
								else
								{
									container = new ExternalArchiveSourceContainer( sourcePath.toOSString(), true );
								}
								
								if ( !allEntries.contains( container ) )
								{
									allEntries.add( container );
								}
							}
						}
					}
				}
			}
		}
		catch ( EGLModelException e )
		{
		}
	}
}
