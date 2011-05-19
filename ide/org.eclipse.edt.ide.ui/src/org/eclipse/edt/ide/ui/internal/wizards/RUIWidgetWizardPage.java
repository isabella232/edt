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
import org.eclipse.edt.ide.ui.wizards.RUIWidgetConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class RUIWidgetWizardPage extends EGLPartWizardPage {
	
	private final static String PAGE_NAME= "NewRUIWidgetWizardPage"; //$NON-NLS-1$
	
	private int nColumns = 5;

	private StringDialogField fWidgetDialogField;
	private StatusInfo fProgramStatus;	


	/**
	 * @param pageName
	 */
	protected RUIWidgetWizardPage(String pageName) {
		super(pageName);

		setTitle(NewWizardMessages.NewEGLRUIWidgetWizardPageTitle);
		setDescription(NewWizardMessages.NewEGLRUIWidgetWizardPageDescription);

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
			 handleWidgetDialogFieldChanged();
		 }
		
		 public void doubleClicked(ListDialogField field) { }
	}
	
	public void init() {
	}		
	
	private RUIWidgetConfiguration getConfiguration() {
		return (RUIWidgetConfiguration)((RUIWidgetWizard) getWizard()).getConfiguration();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		init();
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_RUI_WIDGET_DEFINITION);
		
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
		
		// Create Widget name field
		fWidgetDialogField = new StringDialogField();
		fWidgetDialogField.setDialogFieldListener(new HandlerDialogFieldAdapter());
		fWidgetDialogField.setLabelText(NewWizardMessages.NewEGLRUIWidgetWizardPagePartlabel);

		fWidgetDialogField.setEnabled(false);

		fWidgetDialogField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);

		LayoutUtil.setWidthHint(fWidgetDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fWidgetDialogField.getTextControl(null));	
		
		// Initialize values for both fields
		fWidgetDialogField.setText(getConfiguration().getWidgetName());
	}
	
	private void modifyFileListeners() {
		fEGLFileDialogField.getTextControl(null).addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				//Update Fields
				fWidgetDialogField.setText(fEGLFileDialogField.getText());		
			}
			
		});
	}

	
	private void handleWidgetDialogFieldChanged() {
		//Update Configuration
		getConfiguration().setWidgetName(fWidgetDialogField.getText());
		
		//Validate Page
		validatePage();		
	}
	
	protected boolean validatePage() {
		
		//Validate the file
		if(super.validatePage()){
		
			//Validate the part
			fProgramStatus.setOK();
			String partName= getConfiguration().getWidgetName();
			
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
