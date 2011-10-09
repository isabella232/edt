/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
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
import org.eclipse.edt.ide.ui.wizards.ServiceConfiguration;
import org.eclipse.edt.ide.ui.wizards.ServiceOperation;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class ServiceWizard extends EGLPartWizard implements INewWizard {

	 private static final String WIZPAGENAME_ServiceWizardPage = "WIZPAGENAME_ServiceWizardPage"; //$NON-NLS-1$
		ServiceConfiguration configuration;
	    ServiceWizardPage servicewizPage;
	    
		public ServiceWizard() {
			super();
			setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_NEWEGLSERVICE);
			servicewizPage = new ServiceWizardPage(WIZPAGENAME_ServiceWizardPage);		
		}
		
	 public EGLPackageConfiguration getConfiguration() {
			if (configuration == null)
				configuration = new ServiceConfiguration();
			return configuration;
	}	
	 
	 public void init(IWorkbench workbench, IStructuredSelection selection) {
			getConfiguration().init(workbench, selection);
			setWindowTitle(NewWizardMessages.NewEGLServiceWizardPageTitle);		
	}	
		
	public void addPages() {	    
		addPage(servicewizPage); //$NON-NLS-1$
	}
	
	protected ServiceOperation getServiceOperation() {
		ISchedulingRule rule= getCurrentSchedulingRule();
		ServiceOperation operation = null;
		if (rule != null){
			operation = new ServiceOperation((ServiceConfiguration)getConfiguration(), 
		    		servicewizPage.getSuperInterfaces(), servicewizPage.getCalledBasicPrograms(), rule);
		}else{
			operation = new ServiceOperation((ServiceConfiguration)getConfiguration(), 
		    		servicewizPage.getSuperInterfaces(), servicewizPage.getCalledBasicPrograms());
		}
		
	    return(operation);
	}
	
	@Override
	public boolean performFinish() {
		if (!super.performFinish())
			return false;
		
		try {
			getContainer().run(canRunForked(), true,  getServiceOperation());
		} catch (InterruptedException e) {
			boolean dialogResult = false;
			if(e.getMessage().indexOf(':')!=-1){
				PartTemplateException pe = new PartTemplateException(e.getMessage());
				if(pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_NOT_FOUND)==0){
					dialogResult = ((EGLPartWizardPage)this.getPage(WIZPAGENAME_ServiceWizardPage)).handleTemplateError(pe.getPartType(), pe.getPartDescription()); //$NON-NLS-1$
				}
				else if(pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_DISABLED)==0){
					//is there a way to tell this?
				}
				else if(pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_CORRUPTED)==0){
					dialogResult = ((EGLPartWizardPage)this.getPage(WIZPAGENAME_ServiceWizardPage)).handleTemplateError(pe.getPartType(), pe.getPartDescription()); //$NON-NLS-1$
				}
				
				if(dialogResult)
					return performFinish();
				else
					return false;
			} else {
				e.printStackTrace();
				EGLLogger.log(this, e);
				return false;
			}
		} catch (InvocationTargetException e) {
			if(e.getTargetException() instanceof CoreException) {
				ErrorDialog.openError(
					getContainer().getShell(),
					null,
					null,
					((CoreException) e.getTargetException()).getStatus());
			} else {
				e.printStackTrace();
				EGLLogger.log(this, e);
			}
			return false;
		}
		
		//open the file
		openResource(configuration.getFile());
		
		return true;
	}
}
