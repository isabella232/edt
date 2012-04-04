/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.services.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.services.wizards.ServiceConfiguration.ETemplateType;
import org.eclipse.edt.ide.ui.internal.wizards.EGLPartWizard;
import org.eclipse.edt.ide.ui.internal.wizards.EGLPartWizardPage;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.edt.ide.ui.wizards.PartTemplateException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class ServiceWizard extends EGLPartWizard implements INewWizard {

	public static final String WIZPAGENAME_ServiceWizardPage = "WIZPAGENAME_ServiceWizardPage"; //$NON-NLS-1$
	private ServiceConfiguration configuration;
	private ServiceWizardPage servicewizPage;
	private ISelection selection;

	public ServiceWizard() {
		super();
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_NEWEGLSERVICE);
		servicewizPage = new ServiceWizardPage(selection,
				WIZPAGENAME_ServiceWizardPage);
	}

	public EGLPackageConfiguration getConfiguration() {
		if (configuration == null)
			configuration = new ServiceConfiguration();
		return configuration;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		getConfiguration().init(workbench, selection);
		setWindowTitle(NewWizardMessages.NewEGLServiceWizardPageTitle);
	}

	public boolean needsPreviousAndNextButtons() {
		return true;
	}

	public void addPages() {
		addPage(servicewizPage); //$NON-NLS-1$
	}

	@Override
	public boolean performFinish() {
		if (!super.performFinish())
			return false;
		
		if(configuration.getTemplateType().equals(ETemplateType.BASIC)){
			if(!performBasicServiceOperation()){
				return false;
			}
		}
		// open the file
		openResource(configuration.getFile());

		return true;
	}

	public boolean performBasicServiceOperation(){
		try {
			getContainer().run(canRunForked(), true, getServiceOperation());
			return true;
		} catch (InterruptedException e) {
			boolean dialogResult = false;
			if (e.getMessage().indexOf(':') != -1) {
				PartTemplateException pe = new PartTemplateException(e.getMessage());
				if (pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_NOT_FOUND) == 0) {
					dialogResult = ((EGLPartWizardPage) this.getPage(ServiceWizard.WIZPAGENAME_ServiceWizardPage)).handleTemplateError(pe.getPartType(),
							pe.getPartDescription()); //$NON-NLS-1$
				} else if (pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_DISABLED) == 0) {
					// is there a way to tell this?
				} else if (pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_CORRUPTED) == 0) {
					dialogResult = ((EGLPartWizardPage) this.getPage(ServiceWizard.WIZPAGENAME_ServiceWizardPage)).handleTemplateError(pe.getPartType(),
							pe.getPartDescription()); //$NON-NLS-1$
				}

				if (dialogResult)
					return performFinish();
				else
					return false;
			} else {
				e.printStackTrace();
				EGLLogger.log(this, e);
				return false;
			}
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof CoreException) {
				ErrorDialog.openError(getContainer().getShell(), null, null, ((CoreException) e.getTargetException()).getStatus());
			} else {
				e.printStackTrace();
				EGLLogger.log(this, e);
			}
			return false;
		}
	}
	protected ServiceOperation getServiceOperation() {
		ISchedulingRule rule = getCurrentSchedulingRule();
		ServiceOperation operation = null;
		ServiceConfiguration configuration = (ServiceConfiguration) getConfiguration();
		if (rule != null) {
			operation = new ServiceOperation(configuration, configuration.getSuperInterfaces(), new ArrayList() /*
																												 * Not
																												 * support
																												 * called
																												 * basic
																												 * program
																												 * in
																												 * EDT
																												 * 0.8
																												 */, rule);
		} else {
			operation = new ServiceOperation(configuration, configuration.getSuperInterfaces(), new ArrayList() /*
																												 * Not
																												 * support
																												 * called
																												 * basic
																												 * program
																												 * in
																												 * EDT
																												 * 0.8
																												 */);
		}
		return (operation);
	}
	
}
