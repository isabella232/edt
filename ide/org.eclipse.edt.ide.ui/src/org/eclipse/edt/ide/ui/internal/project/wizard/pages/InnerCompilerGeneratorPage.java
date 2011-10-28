/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.project.wizard.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.compiler.IGenerator;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.preferences.CompilerSelectionPreferencePage;
import org.eclipse.edt.ide.ui.preferences.CompilerPropertyAndPreferencePage;
import org.eclipse.edt.ide.ui.preferences.IGeneratorTabProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.osgi.service.prefs.BackingStoreException;

public class InnerCompilerGeneratorPage extends
		CompilerPropertyAndPreferencePage {
	
	private Composite composite;
	private ProjectWizardPage parentWizardPage;
	public InnerCompilerGeneratorPage(
			ProjectWizardPage parentWizardPage) {
		super();
		this.parentWizardPage = parentWizardPage;
	}

	/**
	 * Returns the id of the compiler that this preference page is for.
	 */
	public String getPreferencePageCompilerId() {
		return "org.eclipse.edt.ide.compiler.edtCompiler";
	}
	
	protected boolean isValidWorkspaceExtensions() {
		// TODO if resource is null (new create project) always return true
		return true;
	}
	public Composite createContents(Composite parent) {
		if(composite == null )
			composite = super.createPreferenceContent(parent);
		return composite;
	}
	
	public List<String> getSelectedGenerators() {
		return super.getSelectedGenerators();
	}
	
	protected boolean doPerformOk( final boolean isApply ) {
		return true;
	}

	protected void setPreferenceContentStatus(IStatus status) {
		super.setPreferenceContentStatus(status);
		parentWizardPage.setErrorMessage(status.getMessage());
		parentWizardPage.getWizard().getContainer().updateButtons();
	}
	
	public boolean isValidPage(){
		return this.getPreferenceContentStatus().isOK() && latestStatus.isOK();
	}
}
