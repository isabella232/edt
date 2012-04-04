/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.deployment.internal.nls.Messages;
import org.eclipse.edt.ide.deployment.operation.IDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.DeploymentResultsCollectorManager;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;

public class AbstractDeploymentSolution implements IDeploymentSolution {

	protected DeploymentContext context;
	
	private List<IDeploymentOperation> operations = new ArrayList<IDeploymentOperation>();


	public void addOperation( IDeploymentOperation operation ) {
		operations.add( operation );
	}

	
	@Override
	public void execute(IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor) throws CoreException {

		for ( int i = 0; i < operations.size(); i ++ ) {
			IDeploymentOperation operation = operations.get( i );
			operation.preCheck( context, resultsCollector, monitor );
		}
		
		if ( context.getStatus() == DeploymentContext.STATUS_STOP ) {
			return;
		}
		
		if ( context.getStatus() != DeploymentContext.STATUS_SHOULD_RUN ) {
			context.showMessage( Messages.deployment_action_no_parts_found );
			DeploymentResultsCollectorManager.getInstance().getCollector(DeploymentUtilities.getDeploymentTargetId(context.getDeploymentDesc().getDeploymentTarget(), null, context.getDeploymentDesc().getName()), context.getDeploymentDesc().getName(), false, false).addMessage(
					DeploymentUtilities.createDeployMessage(IStatus.WARNING, Messages.bind(Messages.deployment_no_parts_found, context.getDeploymentDesc().getName())));
			return;
		}
		
		for ( int i = 0; i < operations.size(); i ++ ) {
			IDeploymentOperation operation = operations.get( i );
			operation.execute( context, resultsCollector, monitor );
			monitor.worked(2);
		}
	}

	@Override
	public void setContext(DeploymentContext context) {
		this.context = context;
	}

}
