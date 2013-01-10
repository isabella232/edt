/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class ProjectCreationOperation extends WorkspaceModifyOperation {

	private ProjectConfiguration configuration;

	/**
	 * 
	 */
	public ProjectCreationOperation(ProjectConfiguration configuration) {
		super();
		this.configuration = configuration;
	}
	
	public ProjectCreationOperation(ProjectConfiguration configuration, ISchedulingRule rule) {
		super(rule);
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void execute(IProgressMonitor monitor)
		throws CoreException, InvocationTargetException, InterruptedException {
		
		IWorkspaceRoot fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		
		IProject currentProject = null;
		IPath currentLocation = null;
		
		currentProject = fWorkspaceRoot.getProject(configuration.getProjectName());
		if(configuration.isUseDefaults()){
			currentLocation = new Path(configuration.getInitialProjectLocation());
		}
		else{
			currentLocation = new Path(configuration.getCustomProjectLocation());
		}
		
		if(monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask(NewWizardMessages.BuildPathsBlockOperationdesc_project, 10);

		//create the project
		try {
			if (!currentProject.exists()) {
				IProjectDescription desc= currentProject.getWorkspace().newProjectDescription(currentProject.getName());
				if (Platform.getLocation().equals(currentLocation)) {
					currentLocation= null;
				}
				desc.setLocation(currentLocation);
				currentProject.create(desc, monitor);
				EGLCore.create(currentProject);
				monitor= null;
			}
			if (!currentProject.isOpen()) {
				currentProject.open(monitor);
				monitor= null;
			}

		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}	
}
