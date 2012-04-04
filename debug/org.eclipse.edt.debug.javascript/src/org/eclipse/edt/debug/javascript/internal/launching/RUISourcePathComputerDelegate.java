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
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.ProjectSourceContainer;
import org.eclipse.edt.debug.core.EGLSourcePathComputerDelegate;
import org.eclipse.edt.debug.javascript.internal.model.IRUILaunchConfigurationConstants;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;

public class RUISourcePathComputerDelegate extends EGLSourcePathComputerDelegate
{
	@Override
	public ISourceContainer[] computeSourceContainers( ILaunchConfiguration configuration, IProgressMonitor monitor ) throws CoreException
	{
		List<ISourceContainer> containers = new ArrayList<ISourceContainer>();
		
		// First add containers for package fragment roots, to handle relative file names.
		String projectName = configuration.getAttribute( IRUILaunchConfigurationConstants.ATTR_PROJECT_NAME, "" ); //$NON-NLS-1$
		if ( projectName.length() != 0 )
		{
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject( projectName );
			if ( project != null && project.exists() )
			{
				if ( project.hasNature( EGLCore.NATURE_ID ) )
				{
					buildContainers( (IEGLProject)EGLCore.create( project ), containers, new HashSet<IEGLProject>() );
				}
				else
				{
					containers.add( new ProjectSourceContainer( project, true ) );
				}
			}
		}
		
		// Add workspace container to handle workspace-absolute file names.
		containers.add( new RUIWorkspaceSourceContainer() );
		
		return containers.toArray( new ISourceContainer[ containers.size() ] );
	}
}
