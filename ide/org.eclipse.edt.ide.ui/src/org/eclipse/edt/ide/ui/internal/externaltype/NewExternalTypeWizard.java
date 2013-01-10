/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.externaltype;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.wizards.EGLFileWizard;
import org.eclipse.edt.ide.ui.internal.wizards.EGLPartWizardPage;
import org.eclipse.edt.ide.ui.templates.wizards.TemplateWizardNode;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.edt.ide.ui.wizards.PartTemplateException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class NewExternalTypeWizard extends EGLFileWizard {
	private static final String WIZPAGENAME_ExternalTypeWizardPage = "WIZPAGENAME_ExternalTypeWizardPage"; //$NON-NLS-1$
	
	private NewExternalTypeWizardPage mainPage;
	private ISelection selection;
	private Object contentObj; // represents

	public NewExternalTypeWizard() {
		super();
		setWindowTitle(NewExternalTypeWizardMessages.NewExternalTypeWizard_title);
	}

	@Override
	public void addPages() {
		mainPage = new NewExternalTypeWizardPage(selection, WIZPAGENAME_ExternalTypeWizardPage);
		addPage(mainPage);
	}
	
	public boolean needsPreviousAndNextButtons() {
		return true;
	}
	
	@Override
	public boolean performFinish() {
		if (!super.performFinish())
			return false;

		// If a page of the dynamically embedded template wizard is not
		// currently being displayed, the performFinish() on this wizard will
		// not get displayed. This code ensures this happens.
		IWizard wizard = getContainer().getCurrentPage().getWizard();
		if (wizard instanceof NewExternalTypeWizard) {		
			IWizardNode node = mainPage.getSelectedNode();
			if (node instanceof TemplateWizardNode) {
				TemplateWizardNode twn = (TemplateWizardNode) node;
				if (twn.getTemplate().hasWizard()) {
					if (!twn.getWizard().performFinish()) {
						return false;
					}
				}
			}
		}		
		
		IRunnableWithProgress operation = getOperation();

		try {
			getContainer().run(false, true, operation);
		} catch (InterruptedException e) {
			boolean dialogResult = false;
			if (e.getMessage().indexOf(':') != -1) {
				PartTemplateException pe = new PartTemplateException(e.getMessage());
				if (pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_NOT_FOUND) == 0) {
					dialogResult = ((EGLPartWizardPage) this.getPage(WIZPAGENAME_ExternalTypeWizardPage)).handleTemplateError(pe.getPartType(),
							pe.getPartDescription()); //$NON-NLS-1$
				} else if (pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_DISABLED) == 0) {
					// is there a way to tell this?
				} else if (pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_CORRUPTED) == 0) {
					dialogResult = ((EGLPartWizardPage) this.getPage(WIZPAGENAME_ExternalTypeWizardPage)).handleTemplateError(pe.getPartType(),
							pe.getPartDescription()); //$NON-NLS-1$
				}

				if (dialogResult)
					return performFinish();
				else
					return false;
			} else {
				EGLLogger.log(this, e);
				return false;
			}
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof CoreException) {
				ErrorDialog.openError(getContainer().getShell(), null, null, ((CoreException) e.getTargetException()).getStatus());
			} else {
				EGLLogger.log(this, e);
			}
			return false;
		}

		// open the file
		openResource(configuration.getFile());

		return true;
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		getConfiguration().init(workbench, selection);
	}

	public void setContentObj(Object obj) {
		this.contentObj = obj;
	}

	public EGLPackageConfiguration getConfiguration() {
		if (configuration == null)
			configuration = new ExternalTypeConfiguration();
		return configuration;
	}

	protected IRunnableWithProgress getOperation() {
		String codeTemplateId = null;

		IWizardNode node = mainPage.getSelectedNode();
		if (node instanceof TemplateWizardNode) {
			TemplateWizardNode twn = (TemplateWizardNode) node;
			if (!twn.getTemplate().hasWizard() && twn.getTemplate().getCodeTemplateId() != null) {
				codeTemplateId = twn.getTemplate().getCodeTemplateId();
			}
		}

		return (new ExternalTypeOperation((ExternalTypeConfiguration) getConfiguration(), codeTemplateId, contentObj));
	}
}
