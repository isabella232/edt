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
package org.eclipse.edt.ide.jtopen.wizards;

import java.util.HashMap;

import org.eclipse.edt.ide.jtopen.Messages;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.ui.SOAMessages;
import org.eclipse.edt.ide.ui.internal.wizards.EGLDDBindingWizardPage;
import org.eclipse.edt.ide.ui.internal.wizards.EGLPartWizard;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.BindingBaseConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

public class IBMiBindingWizardPage extends EGLDDBindingWizardPage {
	private StringDialogField fSystemField;
	private StringDialogField fUserIdField;
	private StringDialogField fPasswordField;
	private StringDialogField fLibraryField;
	
	private static String storeSystemNameId = ".STORE_SYSTEM_NAME_ID";
	private static String storeUserIDNameId = ".STORE_USERID_NAME_ID";
	private static String storePasswordNameId = ".STORE_PASSWORD_NAME_ID";
	private static String storeLibraryNameId = ".STORE_LIBRARY_NAME_ID";
	
	public IBMiBindingWizardPage(String pageName){
		super(pageName);
		storeSystemNameId = pageName + storeSystemNameId;
		storeUserIDNameId = pageName + storeUserIDNameId;
		storePasswordNameId = pageName + storePasswordNameId;
		storeLibraryNameId = pageName + storeLibraryNameId;
		setTitle(Messages.TitleAddIBMiBinding);
		setDescription(Messages.DescAddIBMiBinding);
		nColumns = 4;
	}
	
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite = new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.MODULE_RESTBINDING);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createComponentNameControl(composite, Messages.LabelIBMiBindingName, getEGLDDBindingConfiguration().getConfiguration( "edt.binding.ibmiconnection" ));
		createSystemControl(composite);
		createUserIdControl(composite);
		createPasswordControl(composite);
		createLibraryControl(composite);
		setControl(composite);
		Dialog.applyDialogFont(parent);
		
		determinePageCompletion();
	}
	
	private IBMiBindingConnectionConfiguration getConfiguration(){
		return (IBMiBindingConnectionConfiguration)((EGLPartWizard)getWizard()).getConfiguration(getName());
	}
//	
//	protected IBMiBindingConnectionConfiguration getBindingRestConfiguration(){
//		return (IBMiBindingConnectionConfiguration)((EGLPartWizard)getWizard()).getConfiguration("org.eclipse.edt.ide.ui.internal.wizards.IBMiBindingWizardPage");
//	}
	
	private void createSystemControl(Composite composite){
		GridData gd;
		
		Label spacer = new Label(composite, SWT.WRAP);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		spacer.setLayoutData(gd);
		
		Label desc = new Label(composite, SWT.WRAP);
		desc.setText(SOAMessages.BaseURIDesc);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		gd.widthHint = 550;
		desc.setLayoutData(gd);
		
		spacer = new Label(composite, SWT.WRAP);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		spacer.setLayoutData(gd);
		
		fSystemField = new StringDialogField();
		fSystemField.setLabelText(Messages.LabelSystem);
		fSystemField.setText(getStoredValue(storeSystemNameId));
		createStringDialogField(composite, fSystemField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fSystemField)
							HandleSystemFieldChanged();
					}		
		});
		
//		new Label(composite, SWT.WRAP).setText(SOAMessages.ExampleDeployedURI);
//		Text example = new Text(composite, SWT.READ_ONLY|SWT.WRAP);
//		example.setText("http://myhostname:8080/myTargetWebProject/restservices/myService"); //$NON-NLS-1$
//		example.setBackground(composite.getBackground());
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.horizontalSpan = nColumns - 1;
//		example.setLayoutData(gd);
//		
//		new Label(composite, SWT.WRAP).setText(SOAMessages.ExampleWorkspaceURI);
//		example = new Text(composite, SWT.READ_ONLY|SWT.WRAP);
//		example.setText("workspace://myServiceProject/myPackage.myService"); //$NON-NLS-1$
//		example.setBackground(composite.getBackground());
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.horizontalSpan = nColumns - 1;
//		example.setLayoutData(gd);
	}
	
	protected void HandleSystemFieldChanged(){
		getConfiguration().setSystem(fSystemField.getText());
		determinePageCompletion();
	}
	
	private void createUserIdControl(Composite composite){
		Label spacer = new Label(composite, SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		spacer.setLayoutData(gd);
		
		fUserIdField = new StringDialogField();
		fUserIdField.setLabelText(Messages.LabelUserId);
		fUserIdField.setText(getStoredValue(storeUserIDNameId));
		createStringDialogField(composite, fUserIdField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fUserIdField)
							HandleUserIdChanged();
					}			
		});
	}

	protected void HandleUserIdChanged() {
		getConfiguration().setUserId(fUserIdField.getText());
		determinePageCompletion();
	}
	
	private void createPasswordControl(Composite composite){
		Label spacer = new Label(composite, SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		spacer.setLayoutData(gd);
		
		fPasswordField = new StringDialogField();
		fPasswordField.setLabelText(Messages.LabelPassword);
		fPasswordField.setText(getStoredValue(storePasswordNameId));
		createStringDialogField(composite, fPasswordField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fPasswordField)
							HandlePasswordChanged();
					}			
		});
	}

	protected void HandlePasswordChanged() {
		getConfiguration().setPassword(fPasswordField.getText());
		determinePageCompletion();
	}
	
	private void createLibraryControl(Composite composite){
		Label spacer = new Label(composite, SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		spacer.setLayoutData(gd);
		
		fLibraryField = new StringDialogField();
		fLibraryField.setLabelText(Messages.LabelLibrary);
		fLibraryField.setText(getStoredValue(storeLibraryNameId));
		createStringDialogField(composite, fLibraryField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fLibraryField)
							HandleLibraryChanged();
					}			
		});
	}

	protected void HandleLibraryChanged() {
		getConfiguration().setLibrary(fLibraryField.getText());
		determinePageCompletion();
	}
	
	protected void createComponentNameControl(Composite parent, String labelName, final BindingBaseConfiguration esConfig) {
		fNameField = new StringDialogField();
		fNameField.setLabelText( labelName );
		createStringDialogField( parent, fNameField, new IDialogFieldListener() {
			@Override
			public void dialogFieldChanged(DialogField field) {
				if (field == fNameField) {
					HandleBindingNameChanged(esConfig);
				}
			}
		} );
	}
	
	protected void HandleBindingNameChanged(final BindingBaseConfiguration esConfig) {
		super.HandleBindingNameChanged(esConfig);
		determinePageCompletion();
	}
	
	@Override
	protected boolean determinePageCompletion() {
		setErrorMessage(null);
		boolean result = true;
		String name = fNameField.getText();
		String systemName = fSystemField.getText();
		if (name == null || name.trim().length() == 0) {
			setErrorMessage(Messages.IBMiBindingBlankError);
			result = false;
		}else if(null == systemName || 0 == systemName.trim().length()){
			setErrorMessage(Messages.IBMiBindingSystemBlankError);
			result = false;
		}
		
		setPageComplete(result);
		return result;
	}
	
	@Override
	public HashMap<String, String> getStoredKeyValues() {
		HashMap<String, String> ret = super.getStoredKeyValues();
		ret.put(storePasswordNameId, fPasswordField.getText());
		ret.put(storeSystemNameId, fSystemField.getText());
		ret.put(storeUserIDNameId, fUserIdField.getText());
		ret.put(storeLibraryNameId, fLibraryField.getText());
		return ret;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	private String getStoredValue(String key) {
		IDialogSettings dialogSettings = getDialogSettings();
		return dialogSettings.get(key) == null ? "" : dialogSettings.get(key);
	}
}
