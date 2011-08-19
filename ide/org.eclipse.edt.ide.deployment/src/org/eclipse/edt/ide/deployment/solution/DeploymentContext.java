/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.solution;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class DeploymentContext {

	private DeploymentDesc model;
	private IProject sourceProject;
	private IProject targetProject;
	private ProjectEnvironment environment;
	private IProgressMonitor monitor;

	public IProgressMonitor getMonitor() {
		return monitor;
	}

	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public IProject getTargetProject() {
		return targetProject;
	}

	public void setTargetProject(IProject targetProject) {
		this.targetProject = targetProject;
	}

	public DeploymentContext( DeploymentDesc model ) {
		this.model = model;
		String targetProjectName = DeploymentUtilities.getDeploymentTargetId(model.getDeploymentTarget(), null, model.getName());
		targetProject = ResourcesPlugin.getWorkspace().getRoot().getProject(targetProjectName);
	}
	
	public IProject getSourceProject() {
		return sourceProject;
	}

	public void setSourceProject(IProject sourceProject) {
		this.sourceProject = sourceProject;

		environment = ProjectEnvironmentManager.getInstance().getProjectEnvironment(this.sourceProject);
//			Environment.pushEnv(environment.getIREnvironment());			
//			environment.getIREnvironment().initSystemEnvironment(environment.getSystemEnvironment()); 
			

	}
	
	public DeploymentDesc getDeploymentDesc() {
		return model;
	}
	
	public Part findPart( String qualifiedPartName ) throws PartNotFoundException {
		String[] splits = qualifiedPartName.split("\\.");
		String[] packageName = new String[splits.length-1];
		for(int i=0; i<splits.length-1; i++){
			packageName[i] = splits[i];
		}
		String partName = splits[splits.length-1];

		return environment.findPart(InternUtil.intern(packageName), InternUtil.intern(partName));
	}
}
