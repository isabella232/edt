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
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.ListDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.LibraryConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;

public class LibraryWizardPage extends EGLPartWizardPage {
	private int libraryType = LibraryConfiguration.BASIC_LIBRARY;

	private int nColumns = 5;

	private StringDialogField fLibraryDialogField;
	private StatusInfo fLibraryStatus;
	private Group fLibraryTypeGroup;

	private LibraryFieldAdapter adapter = new LibraryFieldAdapter();

	/**
	 * @param pageName
	 */
	protected LibraryWizardPage(String pageName) {
		super(pageName);

		setTitle(NewWizardMessages.NewEGLLibraryWizardPageTitle);
		setDescription(NewWizardMessages.NewEGLLibraryWizardPageDescription);

		fLibraryStatus= new StatusInfo();
	}
	
	private class LibraryFieldAdapter implements IStringButtonAdapter, IDialogFieldListener {

		// -------- IStringButtonAdapter
		 public void changeControlPressed(DialogField field) { }
	
		 // -------- IDialogFieldListener
		 public void dialogFieldChanged(DialogField field) {
			handleLibraryDialogFieldChanged();
		 }
		
		 public void doubleClicked(ListDialogField field) { }
	}
	
	private LibraryConfiguration getConfiguration() {
		return (LibraryConfiguration)((LibraryWizard) getWizard()).getConfiguration();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
//		init();
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_LIBRARY_DEFINITION);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		createContainerControls(composite, nColumns);
		createPackageControls(composite);
		
		createSeparator(composite, nColumns);
		
		createEGLFileControls(composite);
		createLibraryControls(composite);
		
		modifyFileListeners();
		createLibraryTypeControls(composite);
		
		setControl(composite);
		
		validatePage();	
		Dialog.applyDialogFont(parent);
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}
	
	protected void createLibraryControls(Composite parent) {
		fLibraryDialogField = new StringDialogField();
		fLibraryDialogField.setDialogFieldListener(adapter);
		fLibraryDialogField.setLabelText(NewWizardMessages.NewEGLLibraryWizardPagePartlabel);

		fLibraryDialogField.setText(getConfiguration().getLibraryName());
		fLibraryDialogField.setEnabled(false);

		fLibraryDialogField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);

		LayoutUtil.setWidthHint(fLibraryDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fLibraryDialogField.getTextControl(null));	
	}
	
	private void createLibraryTypeControls(Composite parent) {		
		Composite libraryTypeComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		libraryTypeComposite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		libraryTypeComposite.setLayoutData(gd);
		
		
		GridLayout groupLayout = new GridLayout();
		layout.numColumns = nColumns;
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		
		fLibraryTypeGroup = new Group(libraryTypeComposite, SWT.NONE);
		fLibraryTypeGroup.setText(NewWizardMessages.NewEGLLibraryWizardPageLibraryTypeLabel);
		fLibraryTypeGroup.setLayout(groupLayout);
		fLibraryTypeGroup.setLayoutData(gd);
		
		//createLibraryChoiceButton(fLibraryTypeGroup, NewWizardMessages.NewEGLLibraryWizardPageLibraryTypeNative, LibraryConfiguration.NATIVE_LIBRARY);
		boolean isRuiProject = getPackageFragmentRoot().getEGLProject().isRuiProject();
		//if(EGLBasePlugin.isRUI()){
		if(isRuiProject){
			createLibraryChoiceButton(fLibraryTypeGroup, NewWizardMessages.NewEGLLibraryWizardPageLibraryTypeBasic, LibraryConfiguration.BASIC_LIBRARY);
			createLibraryChoiceButton(fLibraryTypeGroup, NewWizardMessages.NewEGLLibraryWizardPageLibraryTypeRUIProp, LibraryConfiguration.RUIPROP_LIBRARY);
		} else {
			libraryTypeComposite.setVisible(false);
		}
	}
	
	private Button createLibraryChoiceButton(Composite grp, String btnLabel, final int libType){
		Button btn = new Button(grp, SWT.RADIO);
		btn.setText(btnLabel);
		btn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				getConfiguration().setLibraryType(libType);
			}
		});
		
		btn.setSelection(libraryType==libType);
		return btn;
	}
	
	protected void modifyFileListeners() {
		fEGLFileDialogField.getTextControl(null).addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				//Update Fields
				fLibraryDialogField.setText(fEGLFileDialogField.getText());				
			}
		
		});
	}
	
	private void handleLibraryDialogFieldChanged() {
		//Update Configuration
		getConfiguration().setLibraryName(fLibraryDialogField.getText());
		
		//Validate Page
		validatePage();		
	}
	
	protected boolean validatePage() {
		
		//Validate the file
		if(super.validatePage()){
		
			//Validate the part
			fLibraryStatus.setOK();
			String partName= getConfiguration().getLibraryName();
			
			// must not be empty
			if (partName.length() == 0) {
				fLibraryStatus.setError(NewWizardMessages.NewTypeWizardPageErrorEnterPartName);
			}
			else {
				if (partName.indexOf('.') != -1) {
					fLibraryStatus.setError(NewWizardMessages.NewTypeWizardPageErrorQualifiedName);
				}
				else {
	//				IStatus val= EGLConventions.validateEGLTypeName(partName);
	
					validateEGLName(partName, EGLNameValidator.LIBRARY, fLibraryStatus);
	
					//Old validation method:
	//				if (val.getSeverity() == IStatus.ERROR) {
	//					fLibraryStatus.setError(NewWizardMessages.getFormattedString("NewTypeWizardPage.error.InvalidPartName", val.getMessage())); //$NON-NLS-1$
	//				} else if (val.getSeverity() == IStatus.WARNING) {
	//					fLibraryStatus.setWarning(NewWizardMessages.getFormattedString("NewTypeWizardPage.warning.PartNameDiscouraged", val.getMessage())); //$NON-NLS-1$
	//				}
				}
			}
			
			updateStatus(new IStatus[] { fContainerStatus, fPackageStatus, fEGLFileStatus, fLibraryStatus });
			
			if(fLibraryStatus.getSeverity()==IStatus.ERROR)
				return false;
			else
				return true;
		}
		else
			return false;
	}

}
