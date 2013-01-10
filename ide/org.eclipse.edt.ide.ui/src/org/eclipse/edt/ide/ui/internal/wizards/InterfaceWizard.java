/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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
import org.eclipse.edt.ide.ui.wizards.InterfaceConfiguration;
import org.eclipse.edt.ide.ui.wizards.InterfaceOperation;
import org.eclipse.edt.ide.ui.wizards.PartTemplateException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class InterfaceWizard extends EGLPartWizard {
	private static final String WIZPAGENAME_InterfaceWizardPage = "WIZPAGENAME_InterfaceWizardPage"; //$NON-NLS-1$
	InterfaceConfiguration configuration;    
    InterfaceWizardPage interfacePage;
    
	public InterfaceWizard() {
		super();
		
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_NEWEGLINTERFACE);
		interfacePage = new InterfaceWizardPage(WIZPAGENAME_InterfaceWizardPage); 
	}

	public EGLPackageConfiguration getConfiguration() {
		if (configuration == null)
			configuration = new InterfaceConfiguration();
		return configuration;
	}
    
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		getConfiguration().init(workbench, selection);
		setWindowTitle(NewWizardMessages.NewEGLInterfaceWizardPageTitle);
	}	
	
	public void addPages() {
		addPage(interfacePage); //$NON-NLS-1$
	}
	
	private IRunnableWithProgress getOperation()
	{
		ISchedulingRule rule= getCurrentSchedulingRule();
		InterfaceOperation operation = null;
		if (rule != null){
			operation = new InterfaceOperation((InterfaceConfiguration)getConfiguration(), interfacePage.getSuperInterfaces(), rule);
		}else{
			operation = new InterfaceOperation((InterfaceConfiguration)getConfiguration(), interfacePage.getSuperInterfaces());
		}
		
	    return operation;
	} 
	
	@Override
	public boolean performFinish() {
		if (!super.performFinish())
			return false;

		try {
			getContainer().run(canRunForked(), true, getOperation());
		} catch (InterruptedException e) {
			boolean dialogResult = false;
			if(e.getMessage().indexOf(':')!=-1) {
				PartTemplateException pe = new PartTemplateException(e.getMessage());
				if(pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_NOT_FOUND)==0) {
					dialogResult = ((EGLPartWizardPage)this.getPage(WIZPAGENAME_InterfaceWizardPage)).handleTemplateError(pe.getPartType(), pe.getPartDescription());
				} else if(pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_DISABLED)==0) {
					//is there a way to tell this?
				} else if(pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_CORRUPTED)==0) {
					dialogResult = ((EGLPartWizardPage)this.getPage(WIZPAGENAME_InterfaceWizardPage)).handleTemplateError(pe.getPartType(), pe.getPartDescription());
				}
				
				if(dialogResult)
					return performFinish();
				else
					return false;
			} else {
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
				EGLLogger.log(this, e);
			}
			return false;
		}
		
		//update the dialog settings
		((InterfaceWizardPage)getPage(WIZPAGENAME_InterfaceWizardPage)).finishPage();
		
		//open the file
		openResource(configuration.getFile());
		return true;
	}
}
