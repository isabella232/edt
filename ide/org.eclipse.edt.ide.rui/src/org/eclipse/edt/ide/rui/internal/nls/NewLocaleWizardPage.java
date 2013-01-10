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
package org.eclipse.edt.ide.rui.internal.nls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.ide.rui.internal.HelpContextIDs;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class NewLocaleWizardPage extends WizardPage {
	
	List currentDescriptions = new ArrayList();
	List currentCodes = new ArrayList();
	HashMap currentUserLocaleRuntimeLocaleCombinations;
	Locale locale;
	
	Text code;
	Text description;
	
	protected Combo runtimeLocaleChoicesCombo;

	/**
	 * @param pageName
	 */
	public NewLocaleWizardPage(String pageName, String title, Locale locale, List currentCodes, List currentDescriptions, HashMap combinations) {
		super(pageName, title, (ImageDescriptor) null);
		this.currentCodes = currentCodes;
		this.currentDescriptions = currentDescriptions;
		this.locale = locale;
		this.currentUserLocaleRuntimeLocaleCombinations = combinations;
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
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 15;
		layout.marginWidth = 15;
		layout.marginHeight = 7;
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.widthHint = 300;
		
		twoCols.setLayout(layout);
		twoCols.setLayoutData(gridData);
		
		Label descriptionlabel = new Label(twoCols, SWT.NONE);
		descriptionlabel.setText(RUINlsStrings.RUIDeployPreferencePage_4);
		gridData = new GridData();
		descriptionlabel.setLayoutData(gridData);
		
		description = new Text(twoCols, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		description.setLayoutData(gridData);
		description.setFocus();
		description.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
			
		});
		
		Label codelabel = new Label(twoCols, SWT.NONE);
		codelabel.setText(RUINlsStrings.RUIDeployPreferencePage_5);
		gridData = new GridData();
		codelabel.setLayoutData(gridData);
		
		code = new Text(twoCols, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		code.setLayoutData(gridData);
		code.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
			
		});
		
		Label runtimeMessageslabel = new Label(twoCols, SWT.NONE);
		runtimeMessageslabel.setText(RUINlsStrings.RUIDeployPreferencePage_6);
		gridData = new GridData();
		runtimeMessageslabel.setLayoutData(gridData);
		
		runtimeLocaleChoicesCombo = new Combo(twoCols, SWT.READ_ONLY);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		runtimeLocaleChoicesCombo.setLayoutData(gridData);
		runtimeLocaleChoicesCombo.setItems(LocaleUtility.getRuntimeDescriptionsArray());
		
		// Default the locale to the first selection only if it is null
		if (locale.getRuntimeLocaleCode() == null) {
			runtimeLocaleChoicesCombo.setText(LocaleUtility.getRuntimeDescriptionsArray()[0]);
			locale.setRuntimeLocaleCode(LocaleUtility.getRuntimeCodeForDescription(runtimeLocaleChoicesCombo.getText()));
		} else {
			runtimeLocaleChoicesCombo.setText(LocaleUtility.getRuntimeDescriptionForCode(locale.getRuntimeLocaleCode()));
		}
		runtimeLocaleChoicesCombo.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				setPageComplete(validatePage());
			}
			
		});
		
	}
	
	protected boolean validatePage() {
		String error = "";
		String code = this.code.getText();
		String runtimeCode = LocaleUtility.getRuntimeCodeForDescription(this.runtimeLocaleChoicesCombo.getText());
		
		if (this.description.getText().equals("")) { //$NON-NLS-1$
			error = RUINlsStrings.NewLocaleDialog_Please_enter_a_unique_locale_descri_;
		} else {
			if (code.equals("")) { //$NON-NLS-1$
				error = RUINlsStrings.NewLocaleDialog_Please_enter_a_unique_locale_cod_;
			} else {
			
				/**
				 * if the user/runtime locale combination is unique then allow duplicate user locale code
				 */
				if (this.currentUserLocaleRuntimeLocaleCombinations.containsKey(code)) {
					List runtimes = (List)this.currentUserLocaleRuntimeLocaleCombinations.get(code);
					if (runtimes.contains(runtimeCode)) {
						error = RUINlsStrings.NewLocaleDialog_locale_combo_already_exists;
					} else {
						if (currentDescriptions.contains(this.description.getText())) {
							error = RUINlsStrings.NewLocaleDialog_The_description_is_not_uniqu_;
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
			locale.setDescription(description.getText());
			locale.setRuntimeLocaleCode(runtimeCode);
			return true;
		}
	}

}
