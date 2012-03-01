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
	private StringDialogField fTextEncodingField;
	private StringDialogField fTimezoneField;
	private StringDialogField fDateFormatField;
	private StringDialogField fDateSeparatorField;
	private StringDialogField fTimeFormatField;
	private StringDialogField fTimeSeparatorField;
	
	private static String storeSystemNameId = ".STORE_SYSTEM_NAME_ID";
	private static String storeUserIDNameId = ".STORE_USERID_NAME_ID";
	private static String storePasswordNameId = ".STORE_PASSWORD_NAME_ID";
	private static String storeLibraryNameId = ".STORE_LIBRARY_NAME_ID";
	private static String storeTextEncodingId = ".STORE_TEXT_ENCODING_ID";
	private static String storeTimezoneId = ".STORE_TIMEZONE_ID";
	private static String storeDateFormatId = ".STORE_DATE_FORMAT_ID";
	private static String storeDateSeparatorId = ".STORE_DATE_SEPARATOR_ID";
	private static String storeTimeFormatId = ".STORE_TIME_FORMAT_ID";
	private static String storeTimeSeparatorId = ".STORE_TIME_SEPARATOR_ID";
	
	private final String fStoreSystemNameId;
	private final String fStoreUserIDNameId;
	private final String fStorePasswordNameId;
	private final String fStoreLibraryNameId;
	private final String fStoreTextEncodingId;
	private final String fStoreTimezoneId;
	private final String fStoreDateFormatId;
	private final String fStoreDateSeparatorId;
	private final String fStoreTimeFormatId;
	private final String fStoreTimeSeparatorId;
	
	public IBMiBindingWizardPage(String pageName){
		super(pageName);
		fStoreSystemNameId = pageName + storeSystemNameId;
		fStoreUserIDNameId = pageName + storeUserIDNameId;
		fStorePasswordNameId = pageName + storePasswordNameId;
		fStoreLibraryNameId = pageName + storeLibraryNameId;
		fStoreTextEncodingId = pageName + storeTextEncodingId;
		fStoreTimezoneId = pageName + storeTimezoneId;
		fStoreDateFormatId = pageName + storeDateFormatId;
		fStoreDateSeparatorId = pageName + storeDateSeparatorId;
		fStoreTimeFormatId = pageName + storeTimeFormatId;
		fStoreTimeSeparatorId = pageName + storeTimeSeparatorId;
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
		createTextEncodingControl(composite);
		createTimezoneControl(composite);
		createDateFormatControl(composite);
		createDateSeparatorControl(composite);
		createTimeFormatControl(composite);
		createTimeSeparatorControl(composite);
		setControl(composite);
		Dialog.applyDialogFont(parent);
		
		determinePageCompletion();
	}
	
	private IBMiBindingConnectionConfiguration getConfiguration(){
		return (IBMiBindingConnectionConfiguration)((EGLPartWizard)getWizard()).getConfiguration(getName());
	}
	
	private void createSystemControl(Composite composite){
		GridData gd;
		
		Label spacer = new Label(composite, SWT.WRAP);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		spacer.setLayoutData(gd);
		
		fSystemField = new StringDialogField();
		fSystemField.setLabelText(Messages.LabelSystem);
		fSystemField.setText(getStoredValue(fStoreSystemNameId));
		getConfiguration().setSystem(fSystemField.getText());
		createStringDialogField(composite, fSystemField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fSystemField) {
							HandleSystemFieldChanged();
						}
					}		
		});
	}
	
	protected void HandleSystemFieldChanged(){
		getConfiguration().setSystem(fSystemField.getText());
		determinePageCompletion();
	}
	
	private void createUserIdControl(Composite composite){
		fUserIdField = new StringDialogField();
		fUserIdField.setLabelText(Messages.LabelUserId);
		fUserIdField.setText(getStoredValue(fStoreUserIDNameId));
		getConfiguration().setUserId(fUserIdField.getText());
		createStringDialogField(composite, fUserIdField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fUserIdField) {
							HandleUserIdChanged();
						}
					}
		});
	}

	protected void HandleUserIdChanged() {
		getConfiguration().setUserId(fUserIdField.getText());
		determinePageCompletion();
	}
	
	private void createPasswordControl(Composite composite){
		fPasswordField = new StringDialogField();
		fPasswordField.setLabelText(Messages.LabelPassword);
		fPasswordField.setText(getStoredValue(fStorePasswordNameId));
		getConfiguration().setPassword(fPasswordField.getText());
		createStringDialogField(composite, fPasswordField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fPasswordField) {
							HandlePasswordChanged();
						}
					}
		});
	}

	protected void HandlePasswordChanged() {
		getConfiguration().setPassword(fPasswordField.getText());
		determinePageCompletion();
	}
	
	private void createLibraryControl(Composite composite){
		fLibraryField = new StringDialogField();
		fLibraryField.setLabelText(Messages.LabelLibrary);
		fLibraryField.setText(getStoredValue(fStoreLibraryNameId));
		getConfiguration().setLibrary(fLibraryField.getText());
		createStringDialogField(composite, fLibraryField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fLibraryField) {
							HandleLibraryChanged();
						}
					}
		});
	}
	
	protected void HandleLibraryChanged() {
		getConfiguration().setLibrary(fLibraryField.getText());
		determinePageCompletion();
	}
	
	private void createTextEncodingControl(Composite composite){
		fTextEncodingField = new StringDialogField();
		fTextEncodingField.setLabelText(Messages.LabelTextEncoding);
		fTextEncodingField.setText(getStoredValue(fStoreTextEncodingId));
		getConfiguration().setTextEncoding(fTextEncodingField.getText());
		createStringDialogField(composite, fTextEncodingField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fTextEncodingField) {
							HandleTextEncodingChanged();
						}
					}
		});
	}

	protected void HandleTextEncodingChanged() {
		getConfiguration().setTextEncoding(fTextEncodingField.getText());
		determinePageCompletion();
	}
	
	private void createTimezoneControl(Composite composite){
		fTimezoneField = new StringDialogField();
		fTimezoneField.setLabelText(Messages.LabelTimezone);
		fTimezoneField.setText(getStoredValue(fStoreTimezoneId));
		getConfiguration().setTimezone(fTimezoneField.getText());
		createStringDialogField(composite, fTimezoneField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fTimezoneField) {
							HandleTimezoneChanged();
						}
					}
		});
	}

	protected void HandleTimezoneChanged() {
		getConfiguration().setTimezone(fTimezoneField.getText());
		determinePageCompletion();
	}
	
	private void createDateFormatControl(Composite composite){
		fDateFormatField = new StringDialogField();
		fDateFormatField.setLabelText(Messages.LabelDateFormat);
		fDateFormatField.setText(getStoredValue(fStoreDateFormatId));
		getConfiguration().setDateFormat(fDateFormatField.getText());
		createStringDialogField(composite, fDateFormatField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fDateFormatField) {
							HandleDateFormatChanged();
						}
					}
		});
	}

	protected void HandleDateFormatChanged() {
		getConfiguration().setDateFormat(fDateFormatField.getText());
		determinePageCompletion();
	}
	
	private void createDateSeparatorControl(Composite composite){
		fDateSeparatorField = new StringDialogField();
		fDateSeparatorField.setLabelText(Messages.LabelDateSeparator);
		fDateSeparatorField.setText(getStoredValue(fStoreDateSeparatorId));
		getConfiguration().setDateSeparator(fDateSeparatorField.getText());
		createStringDialogField(composite, fDateSeparatorField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fDateSeparatorField) {
							HandleDateSeparatorChanged();
						}
					}
		});
		fDateSeparatorField.getTextControl().setTextLimit(1);
	}

	protected void HandleDateSeparatorChanged() {
		getConfiguration().setDateSeparator(fDateSeparatorField.getText());
		determinePageCompletion();
	}
	
	private void createTimeFormatControl(Composite composite){
		fTimeFormatField = new StringDialogField();
		fTimeFormatField.setLabelText(Messages.LabelTimeFormat);
		fTimeFormatField.setText(getStoredValue(fStoreTimeFormatId));
		getConfiguration().setTimeFormat(fTimeFormatField.getText());
		createStringDialogField(composite, fTimeFormatField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fTimeFormatField) {
							HandleTimeFormatChanged();
						}
					}
		});
	}

	protected void HandleTimeFormatChanged() {
		getConfiguration().setTimeFormat(fTimeFormatField.getText());
		determinePageCompletion();
	}
	
	private void createTimeSeparatorControl(Composite composite){
		fTimeSeparatorField = new StringDialogField();
		fTimeSeparatorField.setLabelText(Messages.LabelTimeSeparator);
		fTimeSeparatorField.setText(getStoredValue(fStoreTimeSeparatorId));
		getConfiguration().setTimeSeparator(fTimeSeparatorField.getText());
		createStringDialogField(composite, fTimeSeparatorField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fTimeSeparatorField) {
							HandleTimeSeparatorChanged();
						}
					}
		});
		fTimeSeparatorField.getTextControl().setTextLimit(1);
	}

	protected void HandleTimeSeparatorChanged() {
		getConfiguration().setTimeSeparator(fTimeSeparatorField.getText());
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
		String dateFormat = fDateFormatField.getText();
		String timeFormat = fTimeFormatField.getText();
		if (name == null || name.trim().length() == 0) {
			setErrorMessage(Messages.IBMiBindingBlankError);
			result = false;
		}else if(null == systemName || 0 == systemName.trim().length()){
			setErrorMessage(Messages.IBMiBindingSystemBlankError);
			result = false;
		}
		else if (dateFormat != null && dateFormat.trim().length() > 0 && !isValidInteger(dateFormat)) {
			setErrorMessage(Messages.IBMiBindingDateFormatError);
			result = false;
		}
		else if (timeFormat != null && timeFormat.trim().length() > 0 && !isValidInteger(timeFormat)) {
			setErrorMessage(Messages.IBMiBindingTimeFormatError);
			result = false;
		}
		
		setPageComplete(result);
		return result;
	}
	
	private boolean isValidInteger(String value) {
		try {
			Integer.decode(value);
			return true;
		}
		catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	@Override
	public HashMap<String, String> getStoredKeyValues() {
		HashMap<String, String> ret = super.getStoredKeyValues();
		ret.put(fStorePasswordNameId, fPasswordField.getText());
		ret.put(fStoreSystemNameId, fSystemField.getText());
		ret.put(fStoreUserIDNameId, fUserIdField.getText());
		ret.put(fStoreLibraryNameId, fLibraryField.getText());
		ret.put(fStoreTextEncodingId, fTextEncodingField.getText());
		ret.put(fStoreTimezoneId, fTimezoneField.getText());
		ret.put(fStoreDateFormatId, fDateFormatField.getText());
		ret.put(fStoreDateSeparatorId, fDateSeparatorField.getText());
		ret.put(fStoreTimeFormatId, fTimeFormatField.getText());
		ret.put(fStoreTimeSeparatorId, fTimeSeparatorField.getText());
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
