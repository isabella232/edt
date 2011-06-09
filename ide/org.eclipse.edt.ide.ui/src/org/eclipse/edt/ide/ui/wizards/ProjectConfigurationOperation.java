/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class ProjectConfigurationOperation extends WorkspaceModifyOperation {
	
	private ProjectConfiguration configuration;

	public ProjectConfigurationOperation(ProjectConfiguration configuration) {
		super();
		this.configuration = configuration;
	}
	
	public ProjectConfigurationOperation(ProjectConfiguration configuration, ISchedulingRule rule){
		super(rule);
		this.configuration = configuration;
	}
	protected void execute(IProgressMonitor monitor) throws CoreException {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = workspaceRoot.getProject(this.configuration.getProjectName());
		
		// Add EGL nature to the .project file
		EGLProjectUtility.addEGLNature( project, monitor );
			
		// configure EGL build path
		EGLProjectUtility.createEGLConfiguration( this.configuration, monitor);
		
		// Add the Java nature, for now
		EGLProjectUtility.createJavaConfiguration( project, monitor );
	}
}
