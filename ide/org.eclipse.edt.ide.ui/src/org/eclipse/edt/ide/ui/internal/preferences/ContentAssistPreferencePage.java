/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.preferences;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ContentAssistPreferencePage extends AbstractPreferencePage implements
		IWorkbenchPreferencePage {

	protected Button autoActivationButton = null;
	protected Text  delayTimeTextBox;
	protected ModifyListener modifyListener;
	
	public ContentAssistPreferencePage(){
		super();
		setPreferenceStore(EDTUIPlugin.getDefault().getPreferenceStore());	
		modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controlModified(e.widget);
			}
		};
	}
	public void init(IWorkbench workbench) {

	}

	protected Control createContents(Composite parent) {
		
		Composite composite = (Composite) super.createContents(parent);
		Group contentAssistGroup = createGroup(composite, 1);
		contentAssistGroup.setText(UINlsStrings.ContentAssistLabel);
		Composite contentAssistComposite = createComposite(contentAssistGroup, 1);
		autoActivationButton= createCheckBox(contentAssistComposite, UINlsStrings.AutoActivationLabel);
		autoActivationButton.setSelection(EDTUIPreferenceConstants.getPreferenceStore().getBoolean(EDTUIPreferenceConstants.CODEASSIST_AUTOACTIVATION));
		
		Composite internalComposite = createComposite(contentAssistGroup, 2);
		Label delayTimeLabel = new Label( internalComposite, SWT.NONE);
		delayTimeLabel.setText(PreferencesMessages.EGLEditorPreferencePage_autoActivationDelay);
		
		delayTimeTextBox = new Text(internalComposite, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData();
		data.verticalAlignment = GridData.CENTER;
		data.horizontalAlignment = GridData.FILL;
		data.widthHint = 20;
		delayTimeTextBox.setLayoutData(data);
		delayTimeTextBox.setText(String.valueOf(EDTUIPreferenceConstants.getPreferenceStore().getInt(EDTUIPreferenceConstants.CODEASSIST_AUTOACTIVATION_DELAY)));
		delayTimeTextBox.setTextLimit(3);
		delayTimeTextBox.addModifyListener( modifyListener );
		setSize(composite);
		return parent;
	}
	
	protected void performDefaults() {
		autoActivationButton.setSelection(getPreferenceStore().getDefaultBoolean(EDTUIPreferenceConstants.CODEASSIST_AUTOACTIVATION));
		delayTimeTextBox.setText(String.valueOf(getPreferenceStore().getDefaultInt(EDTUIPreferenceConstants.CODEASSIST_AUTOACTIVATION_DELAY)));
		super.performDefaults();
	}

	protected void initializeValues() {
		autoActivationButton.setSelection(getPreferenceStore().getBoolean(EDTUIPreferenceConstants.CODEASSIST_AUTOACTIVATION));
		delayTimeTextBox.setText(String.valueOf(getPreferenceStore().getInt(EDTUIPreferenceConstants.CODEASSIST_AUTOACTIVATION_DELAY)));
	}

	protected void storeValues() {
		getPreferenceStore().setValue(EDTUIPreferenceConstants.CODEASSIST_AUTOACTIVATION, autoActivationButton.getSelection());
		getPreferenceStore().setValue(EDTUIPreferenceConstants.CODEASSIST_AUTOACTIVATION_DELAY, delayTimeTextBox.getText());
	}

	public boolean performOk() {
		boolean result = super.performOk();
		EDTUIPlugin.getDefault().saveUIPluginPreferences();		
		return result;
	}
	
	private void controlModified( Widget widget ) {
		if( widget == delayTimeTextBox ) {
			validateInputDelay();
		}
	}
	
	private void validateInputDelay() {
		String text = delayTimeTextBox.getText();
		boolean status = validatePositiveNumber(text);
		if (status) {
			setValid(true);
			setErrorMessage(null);
		} else {
			setValid(false);
		}
	}
	
	private  boolean validatePositiveNumber(final String number) {
		
		if (number.length() == 0) {
			setErrorMessage(PreferencesMessages.SpellingPreferencePage_empty_threshold);
			return (false);
		} else {
			try {
				final int value = Integer.parseInt(number);
				if (value <= 0) {
					setErrorMessage(Messages
							.format(PreferencesMessages.SpellingPreferencePage_invalid_threshold,
									number));
					return (false);
				}
			} catch (NumberFormatException exception) {
				setErrorMessage(Messages
						.format(PreferencesMessages.SpellingPreferencePage_invalid_threshold,
								number));
				return (false);
			}
		}

		return (true);
	}
	
}
