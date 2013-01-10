/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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

		return true;
	}


}
