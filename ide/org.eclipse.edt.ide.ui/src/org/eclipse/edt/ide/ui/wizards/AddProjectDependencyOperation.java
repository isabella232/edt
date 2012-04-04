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
package org.eclipse.edt.ide.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class AddProjectDependencyOperation extends WorkspaceModifyOperation {

	private ProjectConfiguration projectConfiguration;
	private String widgetProject;

	public AddProjectDependencyOperation(ProjectConfiguration projectConfiguration, ISchedulingRule rule, 
			String widgetProject) {
		super(rule);
		this.widgetProject = widgetProject;
		this.projectConfiguration = projectConfiguration;
	}
	
	protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
		IWorkspaceRoot fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IEGLProject eglProject = EGLCore.create(fWorkspaceRoot.getProject(projectConfiguration.getProjectName()));
		
		if( this.widgetProject != null ) {
			final IProject widgetsProject = ResourcesPlugin.getWorkspace().getRoot().getProject(this.widgetProject);
			if (widgetsProject.exists()) {
				addRequiredProject(eglProject, fWorkspaceRoot.getProject(this.widgetProject), false);	
			}
		}
	}
	
	private static void addRequiredProject(IEGLProject sourceProject, IProject reqProject, boolean isExported) throws CoreException{
		String[] requiredProjectNames = sourceProject.getRequiredProjectNames();
		for(int i = 0; i < requiredProjectNames.length; i++) {
			if(requiredProjectNames[i].equals(reqProject.getName())) {
				return;
			}
		}
		
		IEGLPathEntry[] rawEGLPath = sourceProject.getRawEGLPath();
		IEGLPathEntry[] newEGLPath = new IEGLPathEntry[rawEGLPath.length + 1];
		System.arraycopy(rawEGLPath, 0, newEGLPath, 0, rawEGLPath.length);
		newEGLPath[newEGLPath.length - 1] = EGLCore.newProjectEntry(reqProject.getProject().getFullPath(), isExported);
		sourceProject.setRawEGLPath(newEGLPath, null);
	}

}
