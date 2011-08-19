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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.deployment.operation.IDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;

public class AbstractDeploymentSolution implements IDeploymentSolution {

	private DeploymentContext context;
	
	private List<IDeploymentOperation> operations = new ArrayList<IDeploymentOperation>();


	public void addOperation( IDeploymentOperation operation ) {
		operations.add( operation );
	}

	
	@Override
	public void execute(IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor) throws CoreException {

		for ( int i = 0; i < operations.size(); i ++ ) {
			IDeploymentOperation operation = operations.get( i );
			operation.execute( context, resultsCollector, monitor );
		}
	}

	@Override
	public void setContext(DeploymentContext context) {
		this.context = context;
	}

}
