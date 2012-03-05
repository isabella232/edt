/*******************************************************************************
 * Copyright 漏 2012 IBM Corporation and others.
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
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.wizards.EGLPartWizardPage;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.templates.wizards.TemplateWizard;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.edt.ide.ui.wizards.PartTemplateException;
import org.eclipse.jface.dialogs.ErrorDialog;

public class BasicServiceWizard extends TemplateWizard {
	private static final String WIZPAGENAME_BasicServiceInterfaceSelectionPage = "WIZPAGENAME_BasicServiceInterfaceSelectionPage"; //$NON-NLS-1$
	protected ServiceWizardPage inputPage;
	
	protected BasicServiceInterfaceSelectionPage intfSelectionPage;
	
	protected ServiceConfiguration configuration;
	protected String part;

	public BasicServiceWizard() {
		super();
		setWindowTitle(NewWizardMessages.NewBasicEGLServiceWizardPageTitle);
		setNeedsProgressMonitor(true);
		setDialogSettings(EDTUIPlugin.getDefault().getDialogSettings());
	}

	public ServiceConfiguration getConfiguration() {
		configuration = (ServiceConfiguration) ((ServiceWizard) getParentWizard()).getConfiguration();
		return configuration;
	}

	public void addPages() {
		intfSelectionPage = new BasicServiceInterfaceSelectionPage(WIZPAGENAME_BasicServiceInterfaceSelectionPage);
		addPage(intfSelectionPage);
	}

	public boolean performFinish() {
		getConfiguration().setSuperInterfaces(intfSelectionPage.getSuperInterfaces());

		try {
			getContainer().run(((ServiceWizard) getParentWizard()).canRunForked(), true, getServiceOperation());
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

		return true;
	}

	protected ServiceOperation getServiceOperation() {
		ISchedulingRule rule = ((ServiceWizard) getParentWizard()).getCurrentSchedulingRule();
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
