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
package org.eclipse.edt.ide.ui.internal.wizards;

import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.ui.SOAMessages;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.BindingBaseConfiguration;
import org.eclipse.edt.ide.ui.wizards.BindingRestConfiguration;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class RestBindingWizardPage extends EGLDDBindingWizardPage {
	public static final String WIZPAGENAME_RestBindingWizardPage = "WIZPAGENAME_RestBindingWizardPage"; //$NON-NLS-1$
	private StringDialogField fBaseUriField;
	private StringDialogField fSessionCookieId;
	private Button fBtnPreserveRequestHeaders;
	private RestBindingNameFieldAdapter adapter = new RestBindingNameFieldAdapter();
	
	private class RestBindingNameFieldAdapter implements IStringBrowseButtonFieldAdapter{

		public void dialogFieldChanged(DialogField field) {
			if(field == fNameField)
				HandleBindingNameChanged(getBindingRestConfiguration());
			
		}

		public void changeControlPressed(DialogField field) {
			HandleRestBindingNameBrowsePressed();			
		}
		
	}
	
	public RestBindingWizardPage(String pageName){
		super(pageName);
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

		createComponentNameControl(composite, NewWizardMessages.LabelRestBindingName, getEGLDDBindingConfiguration().getBindingRestConfiguration());
		createBaseUriControl(composite);
		createBaseUriExample(composite);
		createSessionCookieId(composite);
		//createPreserveRequestHeaderControl(composite);		
		setControl(composite);
		Dialog.applyDialogFont(parent);
	}
	
	private void createPreserveRequestHeaderControl(Composite composite) {
		fBtnPreserveRequestHeaders = new Button(composite, SWT.CHECK);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		fBtnPreserveRequestHeaders.setLayoutData(gd);
		fBtnPreserveRequestHeaders.setText(NewWizardMessages.LabelPreserveReqHeaders);
		fBtnPreserveRequestHeaders.setSelection(getConfiguration().isPreserveRequestHeader());
		
		fBtnPreserveRequestHeaders.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandlePreserveRequestHeaderChanged();
			}
		});
	}

	protected void HandlePreserveRequestHeaderChanged() {
		getConfiguration().setPreserveRequestHeader(fBtnPreserveRequestHeaders.getSelection());		
	}

	private BindingRestConfiguration getConfiguration(){
		return (BindingRestConfiguration)((EGLPartWizard)getWizard()).getConfiguration(getName());
	}
	
	protected BindingRestConfiguration getBindingRestConfiguration(){
		return (BindingRestConfiguration)((EGLPartWizard)getWizard()).getConfiguration(RestBindingWizardPage.WIZPAGENAME_RestBindingWizardPage);
	}
	
	private void createBaseUriControl(Composite composite){
		fBaseUriField = new StringDialogField();
		fBaseUriField.setLabelText(NewWizardMessages.LabelRestBaseURI);
		fBaseUriField.setText(getConfiguration().getBaseUri());
		createStringDialogField(composite, fBaseUriField, 
				new IDialogFieldListener(){
					public void dialogFieldChanged(DialogField field) {
						if(field == fBaseUriField)
							HandleBaseUriFieldChanged();
					}		
		});
	}
	
	private void createBaseUriExample(Composite composite){
		new Label(composite, SWT.NULL);
		Text example = new Text(composite, SWT.READ_ONLY);
		example.setText(SOAMessages.BaseURITooltip);
		Label spacer = new Label(composite, SWT.NULL);
		LayoutUtil.setHorizontalSpan( spacer, 2 );
	}
	
	protected void HandleBaseUriFieldChanged(){
		getConfiguration().setBaseUri(fBaseUriField.getText());
	}
	
	private void createSessionCookieId(Composite composite){
		fSessionCookieId = new StringDialogField();
		fSessionCookieId.setLabelText(NewWizardMessages.LabelSessionCookieId);
		fSessionCookieId.setText(getConfiguration().getSessionCookieId());
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
	}
	
	protected void createComponentNameControl(Composite parent, String labelName, final BindingBaseConfiguration esConfig) {		
		fNameField = createStringBrowseButtonDialogField(parent, adapter, labelName, esConfig.getBindingName(), nColumns-1);
	}	
	
	public void HandleRestBindingNameBrowsePressed(){
		IPart eglPart = browsedEGLPartFQValue(getBindingRestConfiguration().getProject(), IEGLSearchConstants.SERVICE|IEGLSearchConstants.INTERFACE, true);
		if(eglPart != null){			
			//set to be the simple name
			fNameField.setText(eglPart.getElementName());			
		}
	}
}
