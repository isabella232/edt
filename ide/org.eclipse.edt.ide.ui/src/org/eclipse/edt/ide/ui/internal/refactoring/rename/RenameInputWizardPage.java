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
package org.eclipse.edt.ide.ui.internal.refactoring.rename;

import org.eclipse.edt.ide.ui.internal.RowLayouter;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.refactoring.TextInputWizardPage;
import org.eclipse.edt.ide.ui.internal.refactoring.tagging.INameUpdating;
import org.eclipse.edt.ide.ui.internal.refactoring.tagging.IReferenceUpdating;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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

public abstract class RenameInputWizardPage extends TextInputWizardPage {

	private String fHelpContextID;
	private Button fUpdateReferences;
	
	/**
	 * Creates a new text input page.
	 * @param isLastUserPage <code>true</code> if this page is the wizard's last
	 *  user input page. Otherwise <code>false</code>.
	 * @param initialValue the initial value
	 */
	public RenameInputWizardPage(String description, String contextHelpId, boolean isLastUserPage, String initialValue) {
		super(description, isLastUserPage, initialValue);
		fHelpContextID= contextHelpId;
	}

	public void createControl(Composite parent) {
		Composite superComposite= new Composite(parent, SWT.NONE);
		setControl(superComposite);
		initializeDialogUnits(superComposite);
		superComposite.setLayout(new GridLayout());
		Composite composite= new Composite(superComposite, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginHeight= 0;
		layout.marginWidth= 0;

		composite.setLayout(layout);
		RowLayouter layouter= new RowLayouter(2);
		
		Label label= new Label(composite, SWT.NONE);
		label.setText(getLabelText());
		
		Text text= createTextInputField(composite);
		text.selectAll();
		GridData gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint= convertWidthInCharsToPixels(25);
		text.setLayoutData(gd);

		layouter.perform(label, text, 1);

		Label separator= new Label(composite, SWT.NONE);
		GridData gridData= new GridData(SWT.FILL, SWT.FILL, false, false);
		gridData.heightHint= 2;
		separator.setLayoutData(gridData);
		
		
		int indent= convertWidthInCharsToPixels(2);
		
		addOptionalUpdateReferencesCheckbox(composite, layouter);
		addAdditionalOptions(composite, layouter);
		updateForcePreview();
		
		Dialog.applyDialogFont(superComposite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), fHelpContextID);
	}
	
	/**
	 * Clients can override this method to provide more UI elements. By default, does nothing
	 * 
	 * @param composite the parent composite
	 * @param layouter the row layouter to use
	 */
	protected void addAdditionalOptions(Composite composite, RowLayouter layouter) {
		// none by default
	}

	public void setVisible(boolean visible) {
		if (visible) {
			INameUpdating nameUpdating= (INameUpdating)getRefactoring().getAdapter(INameUpdating.class);
			if (nameUpdating != null) {
				String newName= getNewName(nameUpdating);
				if (newName != null && newName.length() > 0 && !newName.equals(getInitialValue())) {
					Text textField= getTextField();
					textField.setText(newName);
					textField.setSelection(0, newName.length());
				}
			}
		}
		super.setVisible(visible);
	}

	/**
	 * Returns the new name for the Java element or <code>null</code>
	 * if no new name is provided
	 * 
	 * @return the new name or <code>null</code>
	 */
	protected String getNewName(INameUpdating nameUpdating) {
		return nameUpdating.getNewElementName();
	}
	
	protected boolean saveSettings() {
		if (getContainer() instanceof Dialog)
			return ((Dialog)getContainer()).getReturnCode() == IDialogConstants.OK_ID;
		return true;
	}
	
	public void dispose() {
		if (saveSettings()) {
		}
		super.dispose();
	}
	
	private void addOptionalUpdateReferencesCheckbox(Composite result, RowLayouter layouter) {
		final IReferenceUpdating ref= (IReferenceUpdating)getRefactoring().getAdapter(IReferenceUpdating.class);
		if (ref == null || !ref.canEnableUpdateReferences())	
			return;
		String title= UINlsStrings.RenameInputWizardPage_update_references; 
		boolean defaultValue= true; //bug 77901
		fUpdateReferences= createCheckbox(result, title, defaultValue, layouter);
		ref.setUpdateReferences(fUpdateReferences.getSelection());
		fUpdateReferences.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				ref.setUpdateReferences(fUpdateReferences.getSelection());
			}
		});		
	}
		
	protected String getLabelText() {
		return UINlsStrings.RenameInputWizardPage_new_name; 
	}

	protected boolean getBooleanSetting(String key, boolean defaultValue) {
		String update= getRefactoringSettings().get(key);
		if (update != null)
			return Boolean.valueOf(update).booleanValue();
		else
			return defaultValue;
	}
	
	protected void saveBooleanSetting(String key, Button checkBox) {
		if (checkBox != null)
			getRefactoringSettings().put(key, checkBox.getSelection());
	}

	private static Button createCheckbox(Composite parent, String title, boolean value, RowLayouter layouter) {
		Button checkBox= new Button(parent, SWT.CHECK);
		checkBox.setText(title);
		checkBox.setSelection(value);
		layouter.perform(checkBox);
		return checkBox;		
	}
	
	private void updateForcePreview() {
		boolean forcePreview= false;
//		getRefactoringWizard().setForcePreviewReview(forcePreview);
	}
}
