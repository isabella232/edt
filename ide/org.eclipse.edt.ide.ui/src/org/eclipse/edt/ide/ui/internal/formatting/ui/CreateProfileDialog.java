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
package org.eclipse.edt.ide.ui.internal.formatting.ui;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Profile;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CreateProfileDialog extends StatusDialog {
	
    
    private static final String PREF_OPEN_EDIT_DIALOG= EDTUIPlugin.PLUGIN_ID + ".codeformatter.create_profile_dialog.open_edit"; //$NON-NLS-1$
    
    
	private Text fNameText;
	private Combo fProfileCombo;
	private Button fEditCheckbox;
	
	private final static StatusInfo fOk= new StatusInfo();
	private final static StatusInfo fEmpty= new StatusInfo(IStatus.ERROR, NewWizardMessages.Err_EmptyFormatProfileName); 
	private final static StatusInfo fDuplicate= new StatusInfo(IStatus.ERROR, NewWizardMessages.Err_DuplicateFormatProfileName); 

	private final ProfileManager fProfileManager;
	private final List fSortedProfiles;
	private final String [] fSortedNames;
	
	private Profile fCreatedProfile;
	protected boolean fOpenEditDialog;
	
	public CreateProfileDialog(Shell parentShell, ProfileManager profileManager) {
		super(parentShell);
		fProfileManager= profileManager;
		fSortedProfiles= fProfileManager.getAllProfiles();
		fSortedNames= fProfileManager.getAllProfileDisplayNames();
	}
	
	
	public void create() {
		super.create();
		setTitle(NewWizardMessages.NewCodeFormatterProfile); 
	}
	
	public Control createDialogArea(Composite parent) {
				
		final int numColumns= 2;
		
		GridData gd;
		
		final GridLayout layout= new GridLayout(numColumns, false);
		layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);

		final Composite composite= new Composite(parent, SWT.NONE);
		composite.setLayout(layout);
		
		// Create "Profile name:" label
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		gd.widthHint= convertWidthInCharsToPixels(60);
		final Label nameLabel = new Label(composite, SWT.WRAP);
		nameLabel.setText(NewWizardMessages.ProfileName); 
		nameLabel.setLayoutData(gd);
		
		// Create text field to enter name
		gd = new GridData( GridData.FILL_HORIZONTAL);
		gd.horizontalSpan= numColumns;
		fNameText= new Text(composite, SWT.SINGLE | SWT.BORDER);
		fNameText.setLayoutData(gd);
		fNameText.addModifyListener( new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				doValidation();
			}
		});
		
		// Create "Initialize settings ..." label
		gd = new GridData();
		gd.horizontalSpan = numColumns;
		Label profileLabel = new Label(composite, SWT.WRAP);
		profileLabel.setText(NewWizardMessages.InitProfileSetting); 
		profileLabel.setLayoutData(gd);
		
		gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan= numColumns;
		fProfileCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		fProfileCombo.setLayoutData(gd);
		
		
		// "Open the edit dialog now" checkbox
		gd= new GridData();
		gd.horizontalSpan= numColumns;
		fEditCheckbox= new Button(composite, SWT.CHECK);
		fEditCheckbox.setText(NewWizardMessages.OpenEditDlg); 
		fEditCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				fOpenEditDialog= ((Button)e.widget).getSelection();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		final IDialogSettings dialogSettings= EDTUIPlugin.getDefault().getDialogSettings();//.get(PREF_OPEN_EDIT_DIALOG);
		if (dialogSettings.get(PREF_OPEN_EDIT_DIALOG) != null) {
		    fOpenEditDialog= dialogSettings.getBoolean(PREF_OPEN_EDIT_DIALOG);
		} else {
		    fOpenEditDialog= true;
		}
		fEditCheckbox.setSelection(fOpenEditDialog);
		
		fProfileCombo.setItems(fSortedNames);
		fProfileCombo.setText(fSortedNames[0]);
		updateStatus(fEmpty);
		
		applyDialogFont(composite);
		
		fNameText.setFocus();
		
		return composite;
	}


	/**
	 * Validate the current settings
	 */
	protected void doValidation() {
		final String name= fNameText.getText().trim();
		
		if (fProfileManager.containsProfile(name)) {
			updateStatus(fDuplicate);
			return;
		}
		if (name.length() == 0) {
			updateStatus(fEmpty);
			return;
		}
		updateStatus(fOk);
	}
	
	
	protected void okPressed() {
		if (!getStatus().isOK()) 
			return;

		EDTUIPlugin.getDefault().getDialogSettings().put(PREF_OPEN_EDIT_DIALOG, fOpenEditDialog);

		final String newProfileName= fNameText.getText();
		final Object baseProfile = fSortedProfiles.get(fProfileCombo.getSelectionIndex());
		fCreatedProfile = fProfileManager.createNewProfile(newProfileName, baseProfile, true);
		super.okPressed();
	}
	
	public final Profile getCreatedProfile() {
		return fCreatedProfile;
	}
	
	public final boolean openEditDialog() {
		return fOpenEditDialog;
	}

}
