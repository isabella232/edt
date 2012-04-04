/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.internal.model.SourcePartElementInfo;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.BindingBaseConfiguration;
import org.eclipse.edt.ide.ui.wizards.BindingEGLConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class EGLBindingWizardPage extends EGLDDBindingWizardPage {
	public static final String WIZPAGENAME_EGLBindingWizardPage = "WIZPAGENAME_EGLBindingWizardPage"; //$NON-NLS-1$
	private StringDialogField fServiceNameField;
	private StringDialogField fAliasField;

	private EGLBindingFieldAdapter adapter = new EGLBindingFieldAdapter();
	private StatusInfo fServiceNameStatus;
	
	private class EGLBindingFieldAdapter implements IStringBrowseButtonFieldAdapter{
		public void dialogFieldChanged(DialogField field) {
			if(field == fNameField)
				HandleBindingNameChanged(getBindingEGLConfiguration());
			else if(field == fAliasField)
				HandleAliasFieldChanged();
			else if(field == fServiceNameField)
				HandleServiceNameFieldChanged();
		}

		public void changeControlPressed(DialogField field) {
			if(field == fNameField)
				HandleEGLBindingNameBrowsePressed();			
		}
	}
	
	public EGLBindingWizardPage(String pageName) {
		super(pageName);
		setTitle(NewWizardMessages.EGLBindingWizPageTitle);
		setDescription(NewWizardMessages.EGLBindingWizPageDescription);
		nColumns = 4;
		fServiceNameStatus = new StatusInfo();
	}
	
	public void HandleEGLBindingNameBrowsePressed() {
		IPart servicePart = browsedEGLPartFQValue(getBindingEGLConfiguration().getProject(), IEGLSearchConstants.SERVICE|IEGLSearchConstants.INTERFACE, true);
		if(servicePart != null){
			SourcePart sourcePart = (SourcePart)servicePart;
			
			//set to be the simple name
			fNameField.setText(servicePart.getElementName());
			
			try {
				SourcePartElementInfo elemInfo = (SourcePartElementInfo)sourcePart.getElementInfo();
				
				//if it's interface, do not set these field
				if(elemInfo.isService()){
					fServiceNameField.setText(servicePart.getFullyQualifiedName());
					fAliasField.setText(getBindingEGLConfiguration().getAliasFrServicePart(servicePart));					
				}					
			} catch (EGLModelException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void HandleBindingNameChanged(BindingBaseConfiguration esConfig) {
		super.HandleBindingNameChanged(esConfig);
		validatePage();
	}
	
	protected boolean validatePage(){
		fNameStatus.setOK();
		fServiceNameStatus.setOK();
		
		validateBindingName(fNameStatus);
		
		if(fServiceNameField != null){
			String serviceName = fServiceNameField.getText();
			if(serviceName == null || serviceName.trim().length() ==0){
				fServiceNameStatus.setError(NewWizardMessages.EGLBindingValidationMsgFQServiceName);
			}
		}
		
		updateStatus(new IStatus[]{fNameStatus, fServiceNameStatus});
		return (!fNameStatus.isError() && !fServiceNameStatus.isError());
	}

	protected BindingEGLConfiguration getBindingEGLConfiguration(){
		return (BindingEGLConfiguration)((EGLPartWizard)getWizard()).getConfiguration(EGLBindingWizardPage.WIZPAGENAME_EGLBindingWizardPage);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGLDDWIZ_ADDBINDING_EGL);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		createBindingControls(composite);
		setControl(composite);
		validatePage();
		Dialog.applyDialogFont(parent);
	}
	
	protected void createBindingControls(Composite composite){
		EGLDeploymentRoot deploymentRoot = getEGLDDBindingConfiguration().getDeploymentRoot();
		createBindingControls(composite, getEGLDDBindingConfiguration().getBindingEGLConfiguration(), deploymentRoot, false);		
	}

	protected void createBindingControls(Composite composite, BindingEGLConfiguration bindingEGLConfig, EGLDeploymentRoot deploymentRoot, boolean isReadOnly) {
		createComponentNameControl(composite, NewWizardMessages.EGLBindingNameLabel, bindingEGLConfig);
		createServiceNameControl(composite, isReadOnly);
		createAliasControl(composite, isReadOnly);
	}
	
	protected void createComponentNameControl(Composite parent, String labelName, final BindingBaseConfiguration esConfig) {		
		fNameField = createStringBrowseButtonDialogField(parent, adapter, labelName, esConfig.getBindingName(), nColumns-1);
	}	

	private void createServiceNameControl(Composite composite, boolean isReadOnly) {
		fServiceNameField = new StringDialogField();
		fServiceNameField.setLabelText(NewWizardMessages.ServiceNameLabel);
		fServiceNameField.setText(getBindingEGLConfiguration().getEGLServiceOrInterface());	
		createStringDialogField(composite, fServiceNameField, adapter);
		fServiceNameField.getTextControl(composite).setEditable(!isReadOnly);
	}

	private void createAliasControl(Composite composite, boolean isReadOnly) {
		fAliasField = new StringDialogField();
		fAliasField.setLabelText(NewWizardMessages.ServiceAliasLabel);
		fAliasField.setText(getBindingEGLConfiguration().getAlias());
		createStringDialogField(composite, fAliasField, adapter);	
		fAliasField.getTextControl(composite).setEditable(!isReadOnly);
	}
	
	public void HandleAliasFieldChanged() {
		getBindingEGLConfiguration().setAlias(fAliasField.getText());		
	}

	public void HandleServiceNameFieldChanged() {
		getBindingEGLConfiguration().setEGLServiceOrInterface(fServiceNameField.getText());	
		validatePage();
	}
	
		
}
