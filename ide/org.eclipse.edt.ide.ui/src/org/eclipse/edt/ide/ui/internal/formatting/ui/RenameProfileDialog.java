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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Profile;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class RenameProfileDialog extends StatusDialog {
	private Label fNameLabel;
	private Text fNameText;
	
	private final StatusInfo fOk;
	private final StatusInfo fEmpty;
	private final StatusInfo fDuplicate;
	private final StatusInfo fNoMessage;

	private final EObject fProfileOrDefaultProfile;
	private final ProfileManager fProfileManager;
	private Profile fRenamedProfile;
	
	public RenameProfileDialog(Shell parentShell, String dialogTitle, EObject profile, ProfileManager manager) {
		super(parentShell);
		fProfileManager= manager;
		setTitle(dialogTitle); 
		fProfileOrDefaultProfile= profile;
		fOk= new StatusInfo();
		fDuplicate= new StatusInfo(IStatus.ERROR, NewWizardMessages.Err_DuplicateFormatProfileName); 
		fEmpty= new StatusInfo(IStatus.ERROR, NewWizardMessages.Err_EmptyFormatProfileName);
		fNoMessage= new StatusInfo(IStatus.ERROR, new String());
	}
	
	public Control createDialogArea(Composite parent) {
				
		final int numColumns= 2;
		
		GridLayout layout= new GridLayout();
		layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.numColumns= numColumns;
		
		final Composite composite= new Composite(parent, SWT.NULL);
		composite.setLayout(layout);
		
		// Create "Please enter a new name:" label
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		gd.widthHint= convertWidthInCharsToPixels(60);
		fNameLabel = new Label(composite, SWT.NONE);
		fNameLabel.setText(NewWizardMessages.EnterNewName); 
		fNameLabel.setLayoutData(gd);
		
		// Create text field to enter name
		gd = new GridData( GridData.FILL_HORIZONTAL);
		gd.horizontalSpan= numColumns;
		fNameText= new Text(composite, SWT.SINGLE | SWT.BORDER);		
		String profileDisplayName = fProfileManager.getProfileDisplayName(fProfileOrDefaultProfile) ;
		fNameText.setText(profileDisplayName);
		fNameText.setSelection(0, profileDisplayName.length());
		fNameText.setLayoutData(gd);
		fNameText.addModifyListener( new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				doValidation();
			}
		});
		fNameText.setText(profileDisplayName);
		fNameText.selectAll();
		
		applyDialogFont(composite);
		
		return composite;
	}
	

	/**
	 * Validate the current settings.
	 */
	protected void doValidation() {
		final String name= fNameText.getText().trim();
		
		if (name.length() == 0) {
			updateStatus(fEmpty);
			return;
		}
		
		if (name.equals(fProfileManager.getProfileDisplayName(fProfileOrDefaultProfile))) {
			updateStatus(fNoMessage);
			return;
		}
		
		if (fProfileManager.containsProfile(name)) {
			updateStatus(fDuplicate);
			return;
		}
		
		updateStatus(fOk);
	}		
	
	protected void okPressed() {
		if (!getStatus().isOK()) 
			return;
		final String newProfileName= fNameText.getText();		
		if(fProfileManager.isProfileBuildIn(fProfileOrDefaultProfile)){
			//do not use it as selection yet, cuz by selecting it, it will 
			//update the map values based on the newly created profile which loses all the user modified information
			//we want to update the profile value with the map information, then make it a selection
			//these work is done by the caller ModifyDialog.applyPressed()
			fRenamedProfile = fProfileManager.createNewProfile(newProfileName, fProfileOrDefaultProfile, false);			
		}
		else{
			//it must be Profile
			((Profile)fProfileOrDefaultProfile).setName(newProfileName);
			fRenamedProfile = (Profile)fProfileOrDefaultProfile;
		}
		super.okPressed();
	}

	public Profile getRenamedProfile() {
		return fRenamedProfile;
	}
	
}
