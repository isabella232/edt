/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
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
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IListAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.ListDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.RUIHandlerConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class RUIHandlerWizardPage extends EGLPartWizardPage {
	
	private final static String PAGE_NAME= "NewRUIHandlerWizardPage"; //$NON-NLS-1$
	
	private int nColumns = 5;

	private StringDialogField fHandlerDialogField;
	private StringDialogField fHandlerTitleField;
	private boolean hasModifiedTitle = false;
	private StatusInfo fProgramStatus;

	/**
	 * @param pageName
	 */
	protected RUIHandlerWizardPage(String pageName) {
		super(pageName);

		setTitle(NewWizardMessages.NewEGLRUIHandlerWizardPageTitle);
		setDescription(NewWizardMessages.NewEGLRUIHandlerWizardPageDescription);

		fProgramStatus= new StatusInfo();
	}
	
	private class HandlerDialogFieldAdapter implements IStringButtonAdapter, IDialogFieldListener, IListAdapter {

		// -------- IStringButtonAdapter
		 public void changeControlPressed(DialogField field) { }
		
		 // -------- IListAdapter
		 public void customButtonPressed(ListDialogField field, int index) { }
		
		 public void selectionChanged(ListDialogField field) { }
		
		 // -------- IDialogFieldListener
		 public void dialogFieldChanged(DialogField field) {
		 	handleHandlerDialogFieldChanged();
		 }
		
		 public void doubleClicked(ListDialogField field) { }
	}
	
	private class HandlerTitleFieldAdapter implements IStringButtonAdapter, IDialogFieldListener, IListAdapter {

		// -------- IStringButtonAdapter
		 public void changeControlPressed(DialogField field) { }
		
		 // -------- IListAdapter
		 public void customButtonPressed(ListDialogField field, int index) { }
		
		 public void selectionChanged(ListDialogField field) { }
		
		 // -------- IDialogFieldListener
		 public void dialogFieldChanged(DialogField field) {
			 handleHandlerTitleFieldChanged();
		 }
		
		 public void doubleClicked(ListDialogField field) { }
	}
	
	public void init() {
	}		
	
	private RUIHandlerConfiguration getConfiguration() {
		return (RUIHandlerConfiguration)((RUIHandlerWizard) getWizard()).getConfiguration();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		init();
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_RUI_HANDLER_DEFINITION);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		createContainerControls(composite, nColumns);
		createPackageControls(composite);
		
		createSeparator(composite, nColumns);
		
		createEGLFileControls(composite);
		createProgramPartControls(composite);
		
		modifyFileListeners();
		
		setControl(composite);
		
		validatePage();	
		Dialog.applyDialogFont(parent);
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}
	
	private void createProgramPartControls(Composite parent) {
		
		// Create Handler name field
		fHandlerDialogField = new StringDialogField();
		fHandlerDialogField.setDialogFieldListener(new HandlerDialogFieldAdapter());
		fHandlerDialogField.setLabelText(NewWizardMessages.NewEGLRUIHandlerWizardPagePartlabel);

		fHandlerDialogField.setEnabled(false);

		fHandlerDialogField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);

		LayoutUtil.setWidthHint(fHandlerDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fHandlerDialogField.getTextControl(null));	
		
		// Create Handler title field
		fHandlerTitleField = new StringDialogField();
		fHandlerTitleField.setDialogFieldListener(new HandlerTitleFieldAdapter());
		fHandlerTitleField.setLabelText(NewWizardMessages.NewEGLRUIHandlerWizardPageTitlelabel);

		fHandlerTitleField.setEnabled(true);

		fHandlerTitleField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);

		LayoutUtil.setWidthHint(fHandlerTitleField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fHandlerTitleField.getTextControl(null));	
		
		// Initialize values for both fields
		fHandlerDialogField.setText(getConfiguration().gethandlerName());
		fHandlerTitleField.setText(getConfiguration().getHandlerTitle());		
	}
	
	private void modifyFileListeners() {
		fEGLFileDialogField.getTextControl(null).addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				//Update Fields
				fHandlerDialogField.setText(fEGLFileDialogField.getText());		
				
				if(!hasModifiedTitle){
					fHandlerTitleField.setText(fEGLFileDialogField.getText());
				}
			}
			
		});
	}
	
	private void handleHandlerTitleFieldChanged() {
		if(!fHandlerTitleField.getText().equals(fHandlerDialogField.getText())){
			hasModifiedTitle = true; // Don't keep the handler name and title in synch any more.
		}
		getConfiguration().setHandlerTitle(fHandlerTitleField.getText());
	}

	
	private void handleHandlerDialogFieldChanged() {
		//Update Configuration
		getConfiguration().setHandlerName(fHandlerDialogField.getText());
		
		//Validate Page
		validatePage();		
	}
	
	protected boolean validatePage() {
		
		//Validate the file
		if(super.validatePage()){
		
			//Validate the part
			fProgramStatus.setOK();
			String partName= getConfiguration().gethandlerName();
			
			// must not be empty
			if (partName.length() == 0) {
				fProgramStatus.setError(NewWizardMessages.NewTypeWizardPageErrorEnterPartName);
			}
			else {
				if (partName.indexOf('.') != -1) {
					fProgramStatus.setError(NewWizardMessages.NewTypeWizardPageErrorQualifiedName);
				}
				else {
					validateEGLName(partName, EGLNameValidator.PROGRAM, fProgramStatus);			
				}
			}
			
			updateStatus(new IStatus[] { fProgramStatus });
			
			if(fProgramStatus.getSeverity()==IStatus.ERROR)
				return false;
			else
				return true;
		}
		else
			return false;
	}
	
	public void finishPage() {
		super.finishPage();
		
		//Update the dialog settings
		IDialogSettings section= getDialogSettings().getSection(PAGE_NAME);
		if (section == null) {
			section= getDialogSettings().addNewSection(PAGE_NAME);
		}	
	}

}
