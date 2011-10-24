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
package org.eclipse.edt.ide.rui.wizards.pages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.internal.EGLAliasJsfNamesSetting;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.rui.wizards.WebClientProjectTemplateWizard;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.project.wizards.NewEGLProjectWizard;
import org.eclipse.edt.ide.ui.internal.project.wizards.ProjectWizardUtils;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.wizards.EGLWizardUtilities.NameValidatorProblemRequestor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ProjectWizardRUILibraryAndBasePackagePage extends ProjectWizardRUILibraryPage {
	
	private static final String BASE_PACKAGE_HINT = "com.mycompany.myapp"; //$NON-NLS-1$
	public static IStatus OK_STATUS = new Status(IStatus.OK, "org.eclipse.edt.ide.rui", 0, "OK", null); //$NON-NLS-1$
	
	private Label basePackageLabel;
	private Text basePackage;

	public ProjectWizardRUILibraryAndBasePackagePage(String pageName) {
		super(pageName);
	}
	
	protected void createBasePackageEntry(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData( new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout(2,false);
//		layout.verticalSpacing = layout.verticalSpacing * 2;
		c.setLayout(layout);
		
		this.basePackageLabel = new Label(c, SWT.NULL);
		this.basePackageLabel.setText(NewWizardMessages.EGLProjectWizardTypePage_BasePackage);
		this.basePackage = new Text(c, SWT.BORDER);
		this.basePackage.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String name = ((Text)e.widget).getText();
				((NewEGLProjectWizard)((WebClientProjectTemplateWizard)getWizard()).getParentWizard()).getModel().setBasePackageName(name);		
			}
			
		});
		
		this.basePackage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		String defaultBasePackageName = ((NewEGLProjectWizard)((WebClientProjectTemplateWizard)getWizard()).getParentWizard()).getModel().getBasePackageName();
		if(defaultBasePackageName != null && !defaultBasePackageName.isEmpty()){
			basePackage.setText(defaultBasePackageName);
		}else{
			basePackage.setMessage(BASE_PACKAGE_HINT);
		}		
		
		hookListenerPackageName(basePackage);
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
			    getWizard().getContainer().updateButtons();
			}	
		});
	}
	
	public boolean isPageComplete() {
		return super.isPageComplete() && validatePage();
	}
		
	private boolean validatePage() {
		// This method is invoked before modifyText listener, so we need to check the project name
		IStatus status = validatePackageName(basePackage.getText());
		if(status != OK_STATUS)
			return false;
		return true;
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
}
