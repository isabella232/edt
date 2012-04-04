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
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.wizards.EGLSourceFolderConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLSourceFolderOperation;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class EGLSourceFolderWizard extends Wizard implements INewWizard {
	
	EGLSourceFolderConfiguration configuration;
	
	public EGLSourceFolderWizard() {
		super();
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_NEWSRCFOLDR);
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {

		try{
			getContainer().run(canRunForked(), true, getOperation());
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
		setWindowTitle(NewWizardMessages.NewSourceFolderCreationWizardTitle);
	}
	
	public EGLSourceFolderConfiguration getConfiguration() {
		if (configuration == null)
			configuration = new EGLSourceFolderConfiguration();
		return configuration;
	}
	
	public void addPages() {
		addPage(new EGLSourceFolderWizardPage("Define this string!")); //$NON-NLS-1$
	}
	
	protected ISchedulingRule getCurrentSchedulingRule(){
		Job job= Job.getJobManager().currentJob();
		if (job != null){
			return(job.getRule());
		}
			
		return null;
	}
	

	private EGLSourceFolderOperation getOperation(){
		ISchedulingRule rule= getCurrentSchedulingRule();
		EGLSourceFolderOperation operation = null;
		if (rule != null){
			operation = new EGLSourceFolderOperation(getConfiguration(), rule);
		}else{
			operation = new EGLSourceFolderOperation(getConfiguration());
		}
		
	    return operation;
	}
	
	protected boolean canRunForked() {
		return true;
	}

}
