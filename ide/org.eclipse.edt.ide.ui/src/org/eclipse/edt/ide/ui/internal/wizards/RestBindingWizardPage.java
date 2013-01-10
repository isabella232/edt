/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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

import java.util.HashMap;

import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.ui.SOAMessages;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.BindingBaseConfiguration;
import org.eclipse.edt.ide.ui.wizards.BindingRestConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class RestBindingWizardPage extends EGLDDBindingWizardPage {
	public static final String WIZPAGENAME_RestBindingWizardPage = "WIZPAGENAME_RestBindingWizardPage"; //$NON-NLS-1$
	private StringDialogField fBaseUriField;
	private StringDialogField fSessionCookieId;
	
	private static String storeBaseURINameId = ".STORE_BASE_URI_NAME_ID";
	private static String storeSessionCookieNameId = ".STORE_SESSION_COOKIE_NAME_ID";
	
	private final String fStoreBaseURINameId;
	private final String fStoreSessionCookieNameId;
	
	public RestBindingWizardPage(String pageName){
		super(pageName);
		fStoreBaseURINameId = pageName + storeBaseURINameId;
		fStoreSessionCookieNameId = pageName + storeSessionCookieNameId;
		setTitle(NewWizardMessages.TitleAddRestBinding);
		setDescription(NewWizardMessages.DescAddRestBinding);
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
		
		createComponentNameControl(composite, NewWizardMessages.LabelRestBindingName, getEGLDDBindingConfiguration().getConfiguration( "edt.binding.rest" ));
		createBaseUriControl(composite);
		createSessionCookieId(composite);
		setControl(composite);
		Dialog.applyDialogFont(parent);
		
		determinePageCompletion();
	}
	
	private BindingRestConfiguration getConfiguration(){
		return (BindingRestConfiguration)((EGLPartWizard)getWizard()).getConfiguration(getName());
	}
	
	protected BindingRestConfiguration getBindingRestConfiguration(){
		return (BindingRestConfiguration)((EGLPartWizard)getWizard()).getConfiguration(RestBindingWizardPage.WIZPAGENAME_RestBindingWizardPage);
	}
	
	private void createBaseUriControl(Composite composite){
		GridData gd;
		
		Label spacer = new Label(composite, SWT.WRAP);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		spacer.setLayoutData(gd);
		
		Label desc = new Label(composite, SWT.WRAP);
		desc.setText(SOAMessages.BaseURIDesc);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		gd.widthHint = convertWidthInCharsToPixels(80);
		desc.setLayoutData(gd);
		
		spacer = new Label(composite, SWT.WRAP);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		spacer.setLayoutData(gd);
		
		fBaseUriField = new StringDialogField();
		fBaseUriField.setLabelText(NewWizardMessages.LabelRestBaseURI);
		IDialogSettings dialogSettings = getDialogSettings();
		if(dialogSettings != null && (dialogSettings.get(fStoreBaseURINameId)) != null) {
			fBaseUriField.setText(dialogSettings.get(fStoreBaseURINameId));
			getConfiguration().setBaseUri(fBaseUriField.getText());
		}
		createStringDialogField(composite, fBaseUriField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fBaseUriField)
							HandleBaseUriFieldChanged();
					}		
		});
		
		new Label(composite, SWT.WRAP).setText(SOAMessages.ExampleDeployedURI);
		Text example = new Text(composite, SWT.READ_ONLY|SWT.WRAP);
		example.setText("http://myhostname:8080/myTargetWebProject/restservices/myService"); //$NON-NLS-1$
		example.setBackground(composite.getBackground());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns - 1;
		example.setLayoutData(gd);
		
		new Label(composite, SWT.WRAP).setText(SOAMessages.ExampleWorkspaceURI);
		example = new Text(composite, SWT.READ_ONLY|SWT.WRAP);
		example.setText("workspace://myServiceProject/myPackage.myService"); //$NON-NLS-1$
		example.setBackground(composite.getBackground());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns - 1;
		example.setLayoutData(gd);
	}
	
	protected void HandleBaseUriFieldChanged(){
		getConfiguration().setBaseUri(fBaseUriField.getText());
		determinePageCompletion();
	}
	
	private void createSessionCookieId(Composite composite){
		Label spacer = new Label(composite, SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		spacer.setLayoutData(gd);
		
		fSessionCookieId = new StringDialogField();
		fSessionCookieId.setLabelText(NewWizardMessages.LabelSessionCookieId);
		IDialogSettings dialogSettings = getDialogSettings();
		if(dialogSettings != null && (dialogSettings.get(fStoreSessionCookieNameId) != null)) {
			fSessionCookieId.setText(dialogSettings.get(fStoreSessionCookieNameId));
			getConfiguration().setSessionCookieId(fSessionCookieId.getText());
		}
		createStringDialogField(composite, fSessionCookieId, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fSessionCookieId)
							HandleSessionCookieIdChanged();
					}			
		});
	}

	protected void HandleSessionCookieIdChanged() {
		getConfiguration().setSessionCookieId(fSessionCookieId.getText());
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
		if (name == null || name.trim().length() == 0) {
			setErrorMessage(NewWizardMessages.RestBindingBlankError);
			result = false;
		}
		
		setPageComplete(result);
		return result;
	}
	
	@Override
	public HashMap<String, String> getStoredKeyValues() {
		HashMap<String, String> ret = super.getStoredKeyValues();
		ret.put(fStoreBaseURINameId, fBaseUriField.getText());
		ret.put(fStoreSessionCookieNameId, fSessionCookieId.getText());
		return ret;
	}
}
