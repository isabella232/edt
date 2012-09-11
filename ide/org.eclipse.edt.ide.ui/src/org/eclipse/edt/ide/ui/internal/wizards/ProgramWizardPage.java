/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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
import org.eclipse.edt.ide.ui.wizards.ProgramConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

public class ProgramWizardPage extends EGLPartWizardPage {
	
	private int progType = ProgramConfiguration.BASIC_PROGRAM;
	
	private final static String PAGE_NAME= "NewProgramWizardPage"; //$NON-NLS-1$
	private final static String SETTINGS_MAIN_PROGRAM= "program_type"; //$NON-NLS-1$	
	
	private int nColumns = 5;

	private StringDialogField fProgramDialogField;
	private StatusInfo fProgramStatus;
	
	private Group fProgramTypeGroup;
	private Button fBasicProgramButton;
//	private Button fActionProgramButton;
	//private Button fCalledProgramButton;
	private Button fUIProgramButton;

	private ProgramFieldAdapter adapter = new ProgramFieldAdapter();

	/**
	 * @param pageName
	 */
	protected ProgramWizardPage(String pageName) {
		super(pageName);

		setTitle(NewWizardMessages.NewEGLProgramWizardPageTitle);
		setDescription(NewWizardMessages.NewEGLProgramWizardPageDescription);

		fProgramStatus= new StatusInfo();
	}
	
	private class ProgramFieldAdapter implements IStringButtonAdapter, IDialogFieldListener, IListAdapter {

		// -------- IStringButtonAdapter
		 public void changeControlPressed(DialogField field) { }
		
		 // -------- IListAdapter
		 public void customButtonPressed(ListDialogField field, int index) { }
		
		 public void selectionChanged(ListDialogField field) { }
		
		 // -------- IDialogFieldListener
		 public void dialogFieldChanged(DialogField field) {
		 	handleProgramDialogFieldChanged();
		 }
		
		 public void doubleClicked(ListDialogField field) { }
	}
	
	public void init() {
//		super.init();
		
		IDialogSettings section= getDialogSettings().getSection(PAGE_NAME);
		if (section != null) {
			try{
				progType = section.getInt(SETTINGS_MAIN_PROGRAM);
				getConfiguration().setProgramType(progType);
			}
			catch(NumberFormatException e){
				progType = ProgramConfiguration.BASIC_PROGRAM;
				section.put(SETTINGS_MAIN_PROGRAM, progType);
			}
		}
	}		
	
	private ProgramConfiguration getConfiguration() {
		return (ProgramConfiguration)((ProgramWizard) getWizard()).getConfiguration();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		init();
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_PROGRAM_DEFINITION);
		
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
		createProgramTypeControls(composite);

//		createTemplateSelectionControls(composite);
		
		setControl(composite);
		
		validatePage();	
		Dialog.applyDialogFont(parent);
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}
	
	private void createProgramPartControls(Composite parent) {
		fProgramDialogField = new StringDialogField();
		fProgramDialogField.setDialogFieldListener(adapter);
		fProgramDialogField.setLabelText(NewWizardMessages.NewEGLProgramWizardPagePartlabel);

		fProgramDialogField.setText(getConfiguration().getProgramName());
		fProgramDialogField.setEnabled(false);

		fProgramDialogField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);

		LayoutUtil.setWidthHint(fProgramDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fProgramDialogField.getTextControl(null));	
	}
	
	private void modifyFileListeners() {
		fEGLFileDialogField.getTextControl(null).addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				//Update Fields
				fProgramDialogField.setText(fEGLFileDialogField.getText());				
			}
			
		});
	}
	
	private void createProgramTypeControls(Composite parent) {		
		Composite programTypeComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		programTypeComposite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		programTypeComposite.setLayoutData(gd);
		
		
		GridLayout groupLayout = new GridLayout();
		layout.numColumns = nColumns;
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		
		fProgramTypeGroup = new Group(programTypeComposite, SWT.NONE);
		fProgramTypeGroup.setText(NewWizardMessages.NewEGLProgramWizardPageProgramTypeLabel);
		fProgramTypeGroup.setLayout(groupLayout);
		fProgramTypeGroup.setLayoutData(gd);		
		
		fBasicProgramButton = new Button(fProgramTypeGroup, SWT.RADIO);
		fBasicProgramButton.setText(NewWizardMessages.NewEGLProgramWizardPageProgramTypeBasic);
		fBasicProgramButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				/*if(!fCalledProgramButton.isEnabled())
					fCalledProgramButton.setEnabled(true);
				
				if(fCalledProgramButton.getSelection())
					getConfiguration().setProgramType(ProgramConfiguration.BASIC_CALLED_PROGRAM);
				else
					getConfiguration().setProgramType(ProgramConfiguration.BASIC_PROGRAM);	*/	
				getConfiguration().setProgramType(ProgramConfiguration.BASIC_PROGRAM);
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		if(progType==ProgramConfiguration.BASIC_PROGRAM || progType==ProgramConfiguration.BASIC_CALLED_PROGRAM)
			fBasicProgramButton.setSelection(true);
		else
			fBasicProgramButton.setSelection(false);
		
		//since this is the only choice, no need to show it to the user
		fBasicProgramButton.setVisible(false);
		fProgramTypeGroup.setVisible(false);

		fUIProgramButton = new Button(fProgramTypeGroup, SWT.RADIO);
		fUIProgramButton.setText(NewWizardMessages.NewEGLProgramWizardPageProgramTypeUI);
		fUIProgramButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				/*if(fCalledProgramButton.getSelection())
					fCalledProgramButton.setSelection(false);
				if(fCalledProgramButton.isEnabled())
					fCalledProgramButton.setEnabled(false);*/
									
				getConfiguration().setProgramType(ProgramConfiguration.UI_PROGRAM);
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		if(progType==ProgramConfiguration.UI_PROGRAM)
			fUIProgramButton.setSelection(true);
		else
			fUIProgramButton.setSelection(false);
/*		
		fActionProgramButton = new Button(fProgramTypeGroup, SWT.RADIO);
		fActionProgramButton.setText(NewWizardMessages.NewEGLProgramWizardPageProgramTypeAction);
		fActionProgramButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(fCalledProgramButton.getSelection())
					fCalledProgramButton.setSelection(false);
				if(fCalledProgramButton.isEnabled())
					fCalledProgramButton.setEnabled(false);
									
				getConfiguration().setProgramType(ProgramConfiguration.ACTION_PROGRAM);
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		if(progType==ProgramConfiguration.ACTION_PROGRAM)
			fActionProgramButton.setSelection(true);
		else{
			fActionProgramButton.setSelection(false);
			if(progType==ProgramConfiguration.BASIC_CALLED_PROGRAM || progType==ProgramConfiguration.TEXT_UI_CALLED_PROGRAM)
				fActionProgramButton.setEnabled(false);
		}
		
		//WebTrans - DO NOT REMOVE
		fActionProgramButton.setVisible(false);
*/				
		
		new Label(fProgramTypeGroup, SWT.NONE);
			
		/*fCalledProgramButton = new Button(programTypeComposite, SWT.CHECK);
		fCalledProgramButton.setText(NewWizardMessages.NewEGLProgramWizardPageProgramTypeCalled);
		fCalledProgramButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				boolean calledSelection = fCalledProgramButton.getSelection();
				if(fActionProgramButton.getSelection() && calledSelection)
					fActionProgramButton.setSelection(!calledSelection);
				fActionProgramButton.setEnabled(!calledSelection);
				
				if(fBasicProgramButton.getSelection() && calledSelection)
					getConfiguration().setProgramType(ProgramConfiguration.BASIC_CALLED_PROGRAM);
				else if(fBasicProgramButton.getSelection() && !calledSelection)
					getConfiguration().setProgramType(ProgramConfiguration.BASIC_PROGRAM);
					
				if(fTextProgramButton.getSelection() && calledSelection)
					getConfiguration().setProgramType(ProgramConfiguration.TEXT_UI_CALLED_PROGRAM);
				else if(fTextProgramButton.getSelection() && !calledSelection)
					getConfiguration().setProgramType(ProgramConfiguration.TEXT_UI_PROGRAM);
					
				progType = getConfiguration().getProgramType();
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});*/
		
		/*if(progType==ProgramConfiguration.ACTION_PROGRAM || progType==ProgramConfiguration.VGWEBTRANS_PROGRAM)
			fCalledProgramButton.setEnabled(false);
		else
			fCalledProgramButton.setEnabled(true);
		
		if(progType==ProgramConfiguration.TEXT_UI_CALLED_PROGRAM || progType==ProgramConfiguration.BASIC_CALLED_PROGRAM)
			fCalledProgramButton.setSelection(true);
		else
			fCalledProgramButton.setSelection(false);*/
	}
	
	private void handleProgramDialogFieldChanged() {
		//Update Configuration
		getConfiguration().setProgramName(fProgramDialogField.getText());
		
		//Validate Page
		validatePage();		
	}
	
	protected boolean validatePage() {
		
		//Validate the file
		if(super.validatePage()){
		
			//Validate the part
			fProgramStatus.setOK();
			String partName= getConfiguration().getProgramName();
			
			// must not be empty
			if (partName.length() == 0) {
				fProgramStatus.setError(NewWizardMessages.NewTypeWizardPageErrorEnterPartName);
			}
			else {
				if (partName.indexOf('.') != -1) {
					fProgramStatus.setError(NewWizardMessages.NewTypeWizardPageErrorQualifiedName);
				}
				else {
	//				IStatus val= EGLConventions.validateEGLTypeName(partName);			
						
					validateEGLName(partName, EGLNameValidator.PROGRAM, fProgramStatus);			
					
					//Old validation method:
	//				if (val.getSeverity() == IStatus.ERROR) {
	//					fProgramStatus.setError(NewWizardMessages.getFormattedString("NewTypeWizardPage.error.InvalidPartName", val.getMessage())); //$NON-NLS-1$
	//				} else if (val.getSeverity() == IStatus.WARNING) {
	//					fProgramStatus.setWarning(NewWizardMessages.getFormattedString("NewTypeWizardPage.warning.PartNameDiscouraged", val.getMessage())); //$NON-NLS-1$
	//				}
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
		section.put(SETTINGS_MAIN_PROGRAM, getConfiguration().getProgramType());		
	}

}
