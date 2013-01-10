/*******************************************************************************
 * Copyright © 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.internal.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.deployment.Activator;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.internal.nls.Messages;
import org.eclipse.edt.ide.deployment.internal.registry.ContributionsRegistry;
import org.eclipse.edt.ide.deployment.results.DeploymentResultsCollectorManager;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.deployment.solution.IDeploymentSolution;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;


public class DeployOperation {
	
	public void deploy(DeploymentContext[] models, IProgressMonitor pm) throws Exception{
		for (int i = 0; i < models.length; i++) {
			DeploymentContext deploymentContext = models[i];
			
			try{
				ContributionsRegistry registry = ContributionsRegistry.singleton;
				IConfigurationElement contribution = registry.getContributionForId(DeploymentUtilities.getDeploymentTargetType(deploymentContext.getDeploymentDesc().getDeploymentTarget()));
				if (contribution != null){
					IDeploymentSolution deploymentSolution = registry.getDeploymentSolution(contribution);
					deploymentSolution.setContext(deploymentContext);
					try {
						String targetProjectName = DeploymentUtilities.getDeploymentTargetId(deploymentContext.getDeploymentDesc().getDeploymentTarget(), null, deploymentContext.getDeploymentDesc().getName());
						IDeploymentResultsCollector resultsCollector = DeploymentResultsCollectorManager.getInstance().getCollector(targetProjectName, deploymentContext.getDeploymentDesc().getName(), false, false /*model.isCMDMode() TODO - EDT*/);

						deploymentSolution.execute(resultsCollector, pm);
					} catch (CoreException e) {
						Activator.getDefault().log("Error processing EGL Deployment Models", e);
					}
				}
			}finally{
				DeploymentUtilities.finalize(
						DeploymentResultsCollectorManager.getInstance().getCollector(DeploymentUtilities.getDeploymentTargetId(models[i].getDeploymentDesc().getDeploymentTarget(), null, models[i].getDeploymentDesc().getName()), models[i].getDeploymentDesc().getName(), false, false /*models[i].isCMDMode() TODO - EDT*/), true, models[i].getDeploymentDesc().getName());
			}
		}
	}

}
