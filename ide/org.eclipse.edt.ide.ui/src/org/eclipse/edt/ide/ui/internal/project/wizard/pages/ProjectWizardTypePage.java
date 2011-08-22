/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
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

import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.internal.EGLAliasJsfNamesSetting;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.project.wizard.fragments.SourceProjectContentFragment;
import org.eclipse.edt.ide.ui.internal.project.wizards.NewEGLProjectWizard;
import org.eclipse.edt.ide.ui.internal.project.wizards.ProjectWizardUtils;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.wizards.EGLWizardUtilities.NameValidatorProblemRequestor;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class ProjectWizardTypePage extends ProjectWizardPage {
	
	private static final String BASE_PACKAGE_HINT = "org.mycompany.myapp"; //$NON-NLS-1$

	public static IStatus OK_STATUS = new Status(IStatus.OK, "org.eclipse.edt.ide.ui", 0, "OK", null); //$NON-NLS-1$
	
	private Label projectNameLabel;
	private Text projectName;
	private Label basePackageLabel;
	private Text basePackage;
	private Composite contentSection;
	private SourceProjectContentFragment contentFragment;
	private ProjectConfiguration model;	

	/**
	 * @param pageName
	 */
	public ProjectWizardTypePage(String pageName, ProjectConfiguration model) {
		super(pageName);
		this.model = model;
		setTitle(NewWizardMessages.EGLNewProjectWizard_0);
		setDescription(NewWizardMessages.EGLProjectWizardTypePage_1);
		setImageDescriptor(PluginImages.DESC_WIZBAN_NEWEGLPROJECT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createContents(Composite parent) {
		parent.setLayoutData( new GridData(GridData.FILL_BOTH));		
		parent.setLayout(new FormLayout());
		
		createProjectNameEntry(parent);
		createContentFragment(this.parent);
		createBasePackageEntry(parent);
		setControl(parent);
		this.projectName.setFocus();

		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IUIHelpConstants.NEW_PROJECT_WIZARD_TYPE_PAGE);
		Dialog.applyDialogFont(parent);
		/**
		 * if the wizard has been invoked from the tool bar then the current page will be null
		 * We need to check for this situation before updating the buttons
		 */
		if (getContainer().getCurrentPage() != null) {
			getContainer().updateButtons();
		}
	}

	private void createBasePackageEntry(Composite parent) {
		this.basePackageLabel = new Label(parent, SWT.NULL);
		this.basePackageLabel.setText(NewWizardMessages.EGLProjectWizardTypePage_BasePackage);
		this.basePackage = new Text(parent, SWT.BORDER);
		this.basePackage.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String name = ((Text)e.widget).getText();
				model.setBasePackageName(name);				
			}
			
		});
		
		FormData data = new FormData();
		data.left = new FormAttachment(0, 10);
		data.top = new FormAttachment(contentSection, 20);
		this.basePackageLabel.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(this.basePackageLabel, 10);
		data.top = new FormAttachment(contentSection, 20);
		data.right = new FormAttachment(100, -10);
		this.basePackage.setLayoutData(data);
		basePackage.setMessage(BASE_PACKAGE_HINT);
		hookListenerPackageName(basePackage);
	}
	
	private void createProjectNameEntry(Composite parent) {
		this.projectNameLabel = new Label(parent, SWT.NULL);
		this.projectNameLabel.setText(NewWizardMessages.EGLProjectWizardTypePage_2);
		this.projectName = new Text(parent, SWT.BORDER);
		this.projectName.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String name = ((Text)e.widget).getText();
				model.setProjectName(name);
				getContainer( ).updateButtons();
			}
			
		});
		
		FormData data = new FormData();
		data.left = new FormAttachment(0, 10);
		data.top = new FormAttachment(0, 10);
		this.projectNameLabel.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(this.projectNameLabel, 10);
		data.top = new FormAttachment(0, 10);
		data.right = new FormAttachment(100, -10);
		this.projectName.setLayoutData(data);
		
		// Add listener for any text change in project name field
		hookListenerProjectName(projectName);
	}
	
	private void createContentFragment(Composite parent) {
		contentFragment = new SourceProjectContentFragment(parent, this);
		contentSection = contentFragment.renderSection();
		registerFragment(contentFragment);
		contentFragment.registerIsCompleteListener(this);
		
		FormData data = new FormData();
		data.left = new FormAttachment(0, 10);
		data.top = new FormAttachment(projectName, 20);
		data.right = new FormAttachment(100, -10);
		contentSection.setLayoutData(data);		
	}

	/**
	 * @param text - projectName
	 */
	private void hookListenerProjectName(Text text) {
		
		text.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				IStatus status = validateName(projectName.getText());
				// Check whether the project name is valid
				if (status != OK_STATUS) {
					setErrorMessage(status.getMessage());
				} else {
					setErrorMessage(null);
				}
			}	
		});
	}
	
	private void hookListenerPackageName(Text text) {
		text.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				IStatus status = validatePackageName(basePackage.getText());
				// Check whether the project name is valid
				if (status != OK_STATUS) {
					setErrorMessage(status.getMessage());
				} else {
					setErrorMessage(null);
				}
			}	
		});
	}

	
	public boolean isPageComplete() {
		return super.isPageComplete() && validatePage();
	}
		
	private boolean validatePage() {
		// This method is invoked before modifyText listener, so we need to check the project name
		IStatus status = validateName(projectName.getText());
		if (status != OK_STATUS) {
			contentFragment.specifyProjectDirectory.setEnabled(false);
			return false;
		} else {
			contentFragment.specifyProjectDirectory.setEnabled(true);
			status = validatePackageName(basePackage.getText());
			if(status != OK_STATUS)
				return false;
			else return true;
		}
	}
	
	public static IStatus validateName(String name) {
		IStatus status = validateProjectName(name);
		if (!status.isOK())
			return status;
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		if (project.exists()) {
			return ProjectWizardUtils.createErrorStatus(NewWizardMessages.error_project_already_exists);
		}
		if (!ProjectWizardUtils.isPlatformCaseSensitive()) {
			// now look for a matching case variant in the tree
			IResource variant = ((Resource) project).findExistingResourceVariant(project.getFullPath());
			if (variant != null) {
				return ProjectWizardUtils.createErrorStatus(NewWizardMessages.error_project_exists_different_case);
			}
		}		
		return OK_STATUS;
	}
	
	public static IStatus validateProjectName(String projectName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.validateName(projectName, IResource.PROJECT);
	}
	
	public IStatus validatePackageName(String packageName) {		
		if(packageName.length() > 0){
			if(packageName.length() != packageName.trim().length()){
				return ProjectWizardUtils.createErrorStatus(NewWizardMessages.error_basepackage_spaces);
			}
			StatusInfo pkgStatus= new StatusInfo();
			ICompilerOptions compilerOption = new ICompilerOptions(){
	            public boolean isVAGCompatible() {
	            	//TODO EDT Remove isVAGCompatibility	            	
	            	return false;
	            }
				public boolean isAliasJSFNames() {
					return EGLAliasJsfNamesSetting.isAliasJsfNames();
				}            
	        };
	        NameValidatorProblemRequestor nameValidaRequestor = new NameValidatorProblemRequestor(pkgStatus);
			EGLNameValidator.validate(packageName, EGLNameValidator.PACKAGE, nameValidaRequestor, compilerOption);
			if(!pkgStatus.isOK())
				return ProjectWizardUtils.createErrorStatus(pkgStatus.getMessage());
		}
		
		return OK_STATUS;
	}
	
	public String getProjectName() {
		return projectName.getText();
	}
	
	public ProjectConfiguration getModel() {
		return model;
	}

	public void setProjectName(String projectName) {
		((NewEGLProjectWizard)getWizard()).getModel().setProjectName(projectName);
	}

}