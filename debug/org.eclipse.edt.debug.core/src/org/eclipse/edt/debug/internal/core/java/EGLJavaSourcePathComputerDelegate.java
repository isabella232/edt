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
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;
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
			if ( project != null && project.exists() )
			{
				if ( project.hasNature( EGLCore.NATURE_ID ) )
				{
					buildContainers( (IEGLProject)EGLCore.create( project ), containers, new HashSet<IEGLProject>() );
				}
			}
		}
		
		return containers.toArray( new ISourceContainer[ containers.size() ] );
	}
}
