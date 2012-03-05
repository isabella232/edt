/*******************************************************************************
 * Copyright 漏 2000, 2011 IBM Corporation and others.
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

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.wizards.EGLPartWizard;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
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
		// open the file
		openResource(configuration.getFile());

		return true;
	}

	protected ISchedulingRule getCurrentSchedulingRule(){
		return super.getCurrentSchedulingRule();
	}

	protected boolean canRunForked() {
		return super.canRunForked();
	}
}
