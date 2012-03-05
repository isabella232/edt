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
package org.eclipse.edt.ide.deployment.rui.operation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.operation.AbstractDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.rui.ResourceDeploymentModel;
import org.eclipse.edt.ide.deployment.rui.tasks.ResourceCopyOperation;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;

public class CopyResourcesOperation extends AbstractDeploymentOperation {

	private String targetProjectName;
	private DeploymentDesc model;
	private DeploymentContext context;
	
	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor)
			throws CoreException {
		this.context = context;
		model = context.getDeploymentDesc();
		targetProjectName = DeploymentUtilities.getDeploymentTargetId(model.getDeploymentTarget(), null, model.getName());
		IProject targetProject = ResourcesPlugin.getWorkspace().getRoot().getProject(targetProjectName);
		
		ResourceCopyOperation resourceCopyOperation = new ResourceCopyOperation(targetProject);
//		if( !monitor.isCanceled() ){
//			resourceCopyOperation.updateFda7Jar(monitor);
//		}
		if( !monitor.isCanceled() ){
			resourceCopyOperation.copyModelResources(new ResourceDeploymentModel(context.getSourceProject(), model.getResourceOmissions()), 
																				monitor, resultsCollector);
		}

	}
}
