/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.edt.debug.core.EGLSourcePathComputerDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

public class EGLJavaSourcePathComputerDelegate extends EGLSourcePathComputerDelegate
{
	@Override
	public ISourceContainer[] computeSourceContainers( ILaunchConfiguration configuration, IProgressMonitor monitor ) throws CoreException
	{
		List<ISourceContainer> containers = new ArrayList<ISourceContainer>();
		
		// Add containers for package fragment roots, to handle relative file names.
		String projectName = configuration.getAttribute( IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "" ); //$NON-NLS-1$
		if ( projectName.length() != 0 )
		{
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject( projectName );
			if ( project != null && project.isAccessible() )
			{
				buildContainers( project, containers, new HashSet<IProject>() );
			}
		}
		
		return containers.toArray( new ISourceContainer[ containers.size() ] );
	}
}
