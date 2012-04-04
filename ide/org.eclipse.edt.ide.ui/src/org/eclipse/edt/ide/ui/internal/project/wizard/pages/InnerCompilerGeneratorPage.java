/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.project.wizards.BasicProjectTemplateWizard;
import org.eclipse.edt.ide.ui.internal.project.wizards.NewEGLProjectWizard;
import org.eclipse.edt.ide.ui.preferences.CompilerPropertyAndPreferencePage;
import org.eclipse.edt.ide.ui.preferences.IGeneratorTabProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class InnerCompilerGeneratorPage extends
		CompilerPropertyAndPreferencePage {
	
	private Composite composite;
	private ProjectWizardPage parentWizardPage;
	public InnerCompilerGeneratorPage(
			ProjectWizardPage parentWizardPage) {
		super();
		this.parentWizardPage = parentWizardPage;
		updateResource();
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
	
	public void updateResource(){
		String projName = ((NewEGLProjectWizard)((BasicProjectTemplateWizard)this.parentWizardPage.getWizard()).getParentWizard()).getModel().getProjectName();
		
		IWorkspaceRoot fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project= fWorkspaceRoot.getProject(projName);

		this.resource = project;
	}
	
	protected boolean doPerformOk( final boolean isApply ) {
		updateResource();
		for ( IGeneratorTabProvider currProvider : currTabProviders ) {
			currProvider.setResource(this.resource);
		}

		return super.doPerformOk(isApply);	
	}

	protected void setPreferenceContentStatus(IStatus status) {
		super.setPreferenceContentStatus(status);
		if(!status.isOK()){
			parentWizardPage.setErrorMessage(status.getMessage());
		}else{
			parentWizardPage.setErrorMessage(null);
			parentWizardPage.setMessage(status.getMessage());
		}
		parentWizardPage.getWizard().getContainer().updateButtons();
	}
	
	public boolean isValidPage(){
		return this.getPreferenceContentStatus().isOK() && latestStatus.isOK();
	}
}
