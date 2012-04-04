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
package org.eclipse.edt.ide.rui.internal.nls;

import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.ide.rui.internal.HelpContextIDs;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class EditLocaleWizardPage extends NewLocaleWizardPage {
	
	private String originalCode;
	private String originalDescription;
	private String originalRuntimeCode;
	
	/**
	 * @param pageName
	 */
	public EditLocaleWizardPage(String pageName, String title, Locale locale, List currentCodes, List currentDescriptions, HashMap combinations) {
		super(pageName, title, locale, currentCodes, currentDescriptions, combinations);
		originalCode = locale.getCode();
		originalDescription = locale.getDescription();
		originalRuntimeCode = locale.getRuntimeLocaleCode();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite twoCols = new Composite(parent, SWT.NONE);
		createDialogControls(twoCols);
		
		setControl(twoCols);
		setPageComplete(validatePage());
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(twoCols, HelpContextIDs.RUI_New_Locale_Wizard);
		Dialog.applyDialogFont(twoCols);
	}
	
	protected void createDialogControls(Composite twoCols) {
		super.createDialogControls(twoCols);
		// set defaults
		code.setText(locale.getCode());
		description.setText(locale.getDescription());	
		runtimeLocaleChoicesCombo.setText(LocaleUtility.getRuntimeDescriptionForCode(locale.getRuntimeLocaleCode()));
	}
	
	protected boolean validatePage() {
		String error = "";
		String code = this.code.getText();
		String description = this.description.getText();
		String runtimeCode = LocaleUtility.getRuntimeCodeForDescription(this.runtimeLocaleChoicesCombo.getText());
		
		if (this.description.getText().equals("")) { //$NON-NLS-1$
			error = RUINlsStrings.NewLocaleDialog_Please_enter_a_unique_locale_descri_;
			this.description.setFocus();
		} else {
			if (code.equals("")) { //$NON-NLS-1$
				error = RUINlsStrings.NewLocaleDialog_Please_enter_a_unique_locale_cod_;
				this.code.setFocus();
			} else {		
				if (error.length() == 0) {
					if (!description.equalsIgnoreCase(originalDescription)) {
						if (currentDescriptions.contains(this.description.getText())) {
							error = RUINlsStrings.NewLocaleDialog_The_description_is_not_uniqu_;
							this.description.setFocus();
						}
					}
				}
				/**
				 * if the user/runtime locale combination is unique then allow duplicate user locale code
				 */
				if (!code.equalsIgnoreCase(originalCode) || 
						!runtimeCode.equalsIgnoreCase(originalRuntimeCode)) {
					if (this.currentUserLocaleRuntimeLocaleCombinations.containsKey(code)) {
						List runtimes = (List)this.currentUserLocaleRuntimeLocaleCombinations.get(code);
						if (runtimes.contains(runtimeCode)) {
							error = RUINlsStrings.NewLocaleDialog_locale_combo_already_exists;
							this.code.setFocus();
						} 
					}
				}				
			}
		}
		
		if (error.length() > 0) {
			setMessage(error, IMessageProvider.ERROR);
			return false;
		} else {
			setMessage(null);
			locale.setCode(code);
			locale.setDescription(description);
			locale.setRuntimeLocaleCode(runtimeCode);
			return true;
		}
	}
	
}
