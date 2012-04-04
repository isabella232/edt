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
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.edt.ide.ui.wizards.PartTemplateException;
import org.eclipse.edt.ide.ui.wizards.ProgramConfiguration;
import org.eclipse.edt.ide.ui.wizards.ProgramOperation;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class ProgramWizard extends EGLPartWizard implements INewWizard {
	
	private static final String WIZPAGENAME_ProgramWizardPage = "WIZPAGENAME_ProgramWizardPage"; //$NON-NLS-1$
	ProgramConfiguration configuration;

	public ProgramWizard() {
		super();
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_NEWPROGRAM);
	}

	protected IRunnableWithProgress getOperation()
	{
		ISchedulingRule rule= getCurrentSchedulingRule();
		ProgramOperation operation = null;
		if (rule != null){
			operation = new ProgramOperation((ProgramConfiguration)getConfiguration(), rule);
		}else{
			operation = new ProgramOperation((ProgramConfiguration)getConfiguration());
		}
		
	    return operation;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		if (!super.performFinish())
			return false;

		try{
			getContainer().run(canRunForked(), true, getOperation());
		}
		catch (InterruptedException e) {
			boolean dialogResult = false;
			if(e.getMessage().indexOf(':')!=-1){
				PartTemplateException pe = new PartTemplateException(e.getMessage());
				if(pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_NOT_FOUND)==0){
					dialogResult = ((EGLPartWizardPage)this.getPage(WIZPAGENAME_ProgramWizardPage)).handleTemplateError(pe.getPartType(), pe.getPartDescription());
				}
				else if(pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_DISABLED)==0){
					//is there a way to tell this?
				}
				else if(pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_CORRUPTED)==0){
					dialogResult = ((EGLPartWizardPage)this.getPage(WIZPAGENAME_ProgramWizardPage)).handleTemplateError(pe.getPartType(), pe.getPartDescription());
				}
				
				if(dialogResult)
					return performFinish();
				else
					return false;
			}
			else{
				EGLLogger.log(this, e);
				return false;
			}
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
		
		//update the dialog settings
		((ProgramWizardPage)getPage(WIZPAGENAME_ProgramWizardPage)).finishPage();
		
		//open the file
		// KCS - pulled this out so that subclasses could override
		openResourceFile();
		
		return true;
	}
	
	protected void openResourceFile()
	{
		openResource(configuration.getFile());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		getConfiguration().init(workbench, selection);
		setWindowTitle(NewWizardMessages.NewEGLProgramWizardPageTitle);
	}
	
	public EGLPackageConfiguration getConfiguration() {
		if (configuration == null)
			configuration = new ProgramConfiguration();
		return configuration;
	}
	
	public void addPages() {
		addPage(new ProgramWizardPage(WIZPAGENAME_ProgramWizardPage));
	}
}
