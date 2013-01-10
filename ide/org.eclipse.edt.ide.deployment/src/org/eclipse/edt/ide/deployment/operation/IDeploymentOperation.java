/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.operation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;

/**
*
*/
public interface IDeploymentOperation {
	
	/**
	 * Ask the deployment operation to do a check before execution
	 * @param resultsCollector TODO
	 * @param monitor Progress monitor
	 * 
	 * @throws CoreException
	 */
	public void preCheck(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor) throws CoreException;

	
	/**
	 * Ask the deployment operation to do its thing and deploy
	 * @param resultsCollector TODO
	 * @param monitor Progress monitor
	 * 
	 * @throws CoreException
	 */
	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor) throws CoreException;
}
