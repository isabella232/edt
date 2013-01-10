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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.edt.ide.deployment.Activator;
import org.eclipse.edt.ide.deployment.internal.nls.Messages;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;


public class DeployJob extends WorkspaceJob {

	private DeploymentContext[] models;
	
	private Object jobFamily;

	public DeployJob() {
		super(Messages.deploy_job_name);
		setUser(true);
		setPriority(Job.LONG);
		setRule(ResourcesPlugin.getWorkspace().getRoot());
	}
	
	public DeployJob(Object jobFamily){
		this();
		this.jobFamily = jobFamily;
	}

	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(Messages.deploy_operation_task_name, 100);
		try {
			new DeployOperation().deploy(models, monitor);
		} catch (Exception e) {
			Activator.getDefault().log("Error running deploy job", e);
		}finally{
			monitor.done();
		}

	  	return Status.OK_STATUS;
	}

	public void setModels(DeploymentContext[] models) {
		this.models = models;		
	}
	
	public boolean belongsTo(Object family){
		if(jobFamily != null){
			return family.equals(jobFamily);
		}
		return super.belongsTo(family);
	}
}
