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
package org.eclipse.edt.debug.javascript.internal.launching;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceContainerType;
import org.eclipse.debug.core.sourcelookup.containers.CompositeSourceContainer;

/**
 * All projects in the workspace.
 * <p>
 * Clients may instantiate this class. This class is not intended to be subclassed.
 * </p>
 */
public class RUIWorkspaceSourceContainer extends CompositeSourceContainer
{
	
	/**
	 * Unique identifier for the workspace source container type (value <code>org.eclipse.debug.core.containerType.workspace</code>).
	 */
	public static final String TYPE_ID = DebugPlugin.getUniqueIdentifier() + ".containerType.workspace"; //$NON-NLS-1$
	
	public RUIWorkspaceSourceContainer()
	{
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getName()
	 */
	@Override
	public String getName()
	{
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( Object obj )
	{
		return obj instanceof RUIWorkspaceSourceContainer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return ResourcesPlugin.getWorkspace().hashCode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getType()
	 */
	@Override
	public ISourceContainerType getType()
	{
		return getSourceContainerType( TYPE_ID );
	}
	
	@Override
	protected ISourceContainer[] createSourceContainers() throws CoreException
	{
		return new ISourceContainer[ 0 ];
	}
	
	@Override
	public Object[] findSourceElements( String name ) throws CoreException
	{
		ArrayList<IFile> sources = new ArrayList<IFile>();
		
		if ( ResourcesPlugin.getWorkspace().validatePath( name, IResource.FILE ).isOK() )
		{
			String projectName = name.substring( 1, name.indexOf( "/", 2 ) ); //$NON-NLS-1$
			String fileName = name.substring( projectName.length() + 1 );
			
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject( projectName );
			IFile file = project.getFile( fileName );
			if ( file.exists() )
			{
				sources.add( file );
			}
		}
		
		return sources.toArray();
	}
	
}
