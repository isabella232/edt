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
package org.eclipse.edt.ide.deployment.solution;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.deployment.operation.IDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;

public interface IDeploymentSolution {
	
	public void addOperation( IDeploymentOperation operation );
	
	/**
	 * Ask the deployment solution to do its thing and deploy
	 * 
	 * @param monitor Progress monitor
	 * @throws CoreException
	 */
	public void execute(IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Model setter
	 * @param model
	 */
	public void setContext(DeploymentContext model);
}
