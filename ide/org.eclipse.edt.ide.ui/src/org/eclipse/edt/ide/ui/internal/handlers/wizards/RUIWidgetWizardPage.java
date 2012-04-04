/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.handlers.wizards;

import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IListAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.ListDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class RUIWidgetWizardPage extends WizardPage {

	private int nColumns = 5;
	private StringDialogField fWidgetDialogField;
	private StatusInfo fProgramStatus;	
	protected boolean inputNeedsProcessing = true;
	private String widgetName;
	
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
	
	private void handleWidgetDialogFieldChanged() {
		//Update Configuration
		widgetName = fWidgetDialogField.getText();
		//Validate Page
		validatePage();		
	}
	
	private HandlerConfiguration getConfiguration() {
		return ((RUIWidgetWizard)getWizard()).getConfiguration();
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_RUI_WIDGET_DEFINITION);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		// Create Widget name field
		fWidgetDialogField = new StringDialogField();
		fWidgetDialogField.setDialogFieldListener(new HandlerDialogFieldAdapter());
		fWidgetDialogField.setLabelText(NewWizardMessages.NewEGLRUIWidgetWizardPagePartlabel);

		fWidgetDialogField.setEnabled(false);

		fWidgetDialogField.doFillIntoGrid(composite, nColumns - 1);
		DialogField.createEmptySpace(composite);

		LayoutUtil.setWidthHint(fWidgetDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fWidgetDialogField.getTextControl(null));	
		
		// Initialize values for both fields
		widgetName = getConfiguration().getHandlerName();
		fWidgetDialogField.setText(getConfiguration().getHandlerName());
		
		setControl(composite);
		validatePage();	
		Dialog.applyDialogFont(parent);
	}
	
	protected boolean validatePage() {	
		
//		//Validate the part
//		fProgramStatus.setOK();
//		String partName= getConfiguration().getHandlerName();
//		
//		// must not be empty
//		if (partName.length() == 0) {
//			fProgramStatus.setError(NewWizardMessages.NewTypeWizardPageErrorEnterPartName);
//		}
//		else {
//			if (partName.indexOf('.') != -1) {
//				fProgramStatus.setError(NewWizardMessages.NewTypeWizardPageErrorQualifiedName);
//			}
//			else {
//				validateEGLName(partName, EGLNameValidator.PROGRAM, fProgramStatus);			
//			}
//		}
//		
//		updateStatus(new IStatus[] { fProgramStatus });
//		
//		if(fProgramStatus.getSeverity()==IStatus.ERROR)
//			return false;
//		else
//			return true;
		return true;

	}
	
	protected int getMaxFieldWidth() {
		return convertWidthInCharsToPixels(40);
	}
	
	public boolean isInputNeedsProcessing() {
		return inputNeedsProcessing;
	}
	
	public void setInputNeedsProcessing(boolean b) {
		this.inputNeedsProcessing = b;
	}
	
	public String getWidgetName() {
		return this.widgetName;
	}
	
	public void updateWidgetName(){
		fWidgetDialogField.setText(getConfiguration().getHandlerName());
	}

}
