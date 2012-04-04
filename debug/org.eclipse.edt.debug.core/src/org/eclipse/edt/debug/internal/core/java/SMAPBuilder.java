/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.core.java;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;
import org.eclipse.edt.debug.core.SMAPTransformer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * Adds SMAP information from .eglsmap files into .class files.
 */
public class SMAPBuilder extends IncrementalProjectBuilder
{
	@Override
	protected IProject[] build( int kind, Map args, IProgressMonitor monitor ) throws CoreException
	{
		switch ( kind )
		{
			case FULL_BUILD:
				buildAll();
				break;
			default:
				IResourceDelta delta = getDelta( getProject() );
				if ( delta == null )
				{
					buildAll();
				}
				else
				{
					delta.accept( new ResourceDeltaVisitor() );
				}
				break;
		}
		return null;
	}
	
	private void buildAll() throws CoreException
	{
		// Performance: only visit the bin directories.
		IJavaProject project = JavaCore.create( getProject() );
		IClasspathEntry[] cp = project.getResolvedClasspath( true );
		
		Set<IPath> binDirectories = new HashSet<IPath>( cp.length );
		for ( IClasspathEntry entry : cp )
		{
			if ( entry.getEntryKind() == IClasspathEntry.CPE_SOURCE )
			{
				IPath outputLocation = entry.getOutputLocation();
				if ( outputLocation != null )
				{
					binDirectories.add( outputLocation );
				}
			}
		}
		
		IPath outputLocation = project.getOutputLocation();
		if ( outputLocation != null )
		{
			binDirectories.add( outputLocation );
		}
		
		ResourceVisitor visitor = new ResourceVisitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for ( IPath path : binDirectories )
		{
			IResource resource = root.findMember( path );
			if ( resource != null )
			{
				resource.accept( visitor );
			}
		}
	}
	
	private class ResourceVisitor implements IResourceVisitor
	{
		@Override
		public boolean visit( IResource resource ) throws CoreException
		{
			if ( resource.getType() == IResource.FILE )
			{
				transform( (IFile)resource );
			}
			return true;
		}
	}
	
	private class ResourceDeltaVisitor implements IResourceDeltaVisitor
	{
		@Override
		public boolean visit( IResourceDelta delta ) throws CoreException
		{
			if ( delta.getKind() == IResourceDelta.CHANGED || delta.getKind() == IResourceDelta.ADDED )
			{
				IResource resource = delta.getResource();
				if ( resource.getType() == IResource.FILE )
				{
					transform( (IFile)resource );
				}
			}
			return true;
		}
	}
	
	private void transform( IFile file ) throws CoreException
	{
		if ( "class".equals( file.getFileExtension() ) ) //$NON-NLS-1$
		{
			IResource smapFile = ResourcesPlugin.getWorkspace().getRoot()
					.findMember( file.getFullPath().removeFileExtension().addFileExtension( IEGLDebugCoreConstants.SMAP_EXTENSION ) );
			if ( smapFile != null && smapFile.getType() == IResource.FILE )
			{
				byte[] smapBytes = getBytes( (IFile)smapFile );
				if ( smapBytes != null )
				{
					byte[] classBytes = getBytes( file );
					if ( classBytes != null )
					{
						try
						{
							byte[] newBytes = new SMAPTransformer.TransformerWorker()
									.transform( file.getFullPath().toString(), classBytes, smapBytes );
							if ( newBytes != null )
							{
								file.setContents( new BufferedInputStream( new ByteArrayInputStream( newBytes ) ), true, false, null );
							}
						}
						catch ( IllegalClassFormatException e )
						{
							EDTDebugCorePlugin.log( e );
						}
					}
				}
			}
		}
	}
	
	private byte[] getBytes( IFile file )
	{
		InputStream is = null;
		try
		{
			is = new BufferedInputStream( file.getContents() );
			byte[] contents = new byte[ is.available() ];
			is.read( contents );
			return contents;
		}
		catch ( IOException e )
		{
		}
		catch ( CoreException e )
		{
		}
		finally
		{
			if ( is != null )
			{
				try
				{
					is.close();
				}
				catch ( IOException e )
				{
				}
			}
		}
		return null;
	}
}
