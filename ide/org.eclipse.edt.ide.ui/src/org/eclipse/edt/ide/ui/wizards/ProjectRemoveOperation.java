/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class ProjectRemoveOperation extends WorkspaceModifyOperation {
	
	private ProjectConfiguration configuration;
	
	/**
	 * 
	 */
	public ProjectRemoveOperation(ProjectConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void execute(IProgressMonitor monitor)
		throws CoreException, InvocationTargetException, InterruptedException {
			
		//Initialize some variables
		IWorkspaceRoot fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			
		IProject fCurrProject = null;
		IPath fCurrProjectLocation = null;
		
		fCurrProject = fWorkspaceRoot.getProject(configuration.getProjectName());
		if(configuration.isUseDefaults()){
			fCurrProjectLocation = new Path(configuration.getInitialProjectLocation());
		}
		else{
			fCurrProjectLocation = new Path(configuration.getCustomProjectLocation());
		}
		
		//Begin Deletion
		if (fCurrProject == null || !fCurrProject.exists()) {
			return;
		}
		
		boolean noProgressMonitor= Platform.getLocation().equals(fCurrProjectLocation);
		if (monitor == null || noProgressMonitor) {
			monitor= new NullProgressMonitor();
		}
		monitor.beginTask(NewWizardMessages.NewProjectCreationWizardPageRemoveprojectDesc, 3);

		try {
			fCurrProject.delete(true, false, monitor);
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		} finally {
			monitor.done();
			fCurrProject= null;
		}
	}

}
