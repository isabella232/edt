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
package org.eclipse.edt.ide.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLPackageOperation;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class EGLPackageWizard extends Wizard implements INewWizard {
	
	private static final String WIZPAGENAME_EGLPackageWizardPage = "WIZPAGENAME_EGLPackageWizardPage"; //$NON-NLS-1$
	
	EGLPackageConfiguration configuration;

	private IPackageFragment newPackageFragment;
	
	public EGLPackageWizard() {
		super();
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_NEWPACK);
		setNeedsProgressMonitor(true);
	}	

	private EGLPackageOperation getOperation(){
		ISchedulingRule rule= getCurrentSchedulingRule();
		EGLPackageOperation operation = null;
		if (rule != null){
			operation = new EGLPackageOperation(getConfiguration(), rule);
		}else{
			operation = new EGLPackageOperation(getConfiguration());
		}
		
	    return operation;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		EGLPackageOperation operation = getOperation();
		
		try{
			getContainer().run(canRunForked(), true, operation);
			newPackageFragment = operation.getNewPackageFragment();
		}
		catch (InterruptedException e) {
			return false;
		}
		catch (InvocationTargetException e) {
			if(e.getTargetException() instanceof CoreException) {
				ErrorDialog.openError(
					getContainer().getShell(),
					null,
					null,
					((CoreException) e.getTargetException()).getStatus());
			}
			else {
				EGLLogger.log(this, e);
			}
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		getConfiguration().init(workbench, selection);
		setWindowTitle(NewWizardMessages.NewPackageCreationWizardTitle);
	}
	
	public EGLPackageConfiguration getConfiguration() {
		if (configuration == null)
			configuration = new EGLPackageConfiguration();
		return configuration;
	}
	
	public EGLPackageConfiguration getConfiguration(String pageName)
	{
		return getConfiguration();
	}
	
	public void addPages() {
		addPage(new EGLPackageWizardPage(WIZPAGENAME_EGLPackageWizardPage)); //$NON-NLS-1$
	}

	public IPackageFragment getNewPackageFragment() {
		return newPackageFragment;
	}
	
	protected ISchedulingRule getCurrentSchedulingRule(){
		Job job= Job.getJobManager().currentJob();
		if (job != null){
			return(job.getRule());
		}
			
		return null;
	}
	
	protected boolean canRunForked() {
		return true;
	}
	
}
