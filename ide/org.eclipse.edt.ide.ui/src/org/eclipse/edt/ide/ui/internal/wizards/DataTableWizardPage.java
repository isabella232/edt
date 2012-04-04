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
import org.eclipse.edt.ide.ui.wizards.DataTableConfiguration;
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
import org.eclipse.ui.PlatformUI;

public class DataTableWizardPage extends EGLPartWizardPage {

	private int dataTableType = 0;
	private final static String PAGE_NAME= "NewDataTableWizardPage"; //$NON-NLS-1$
	private final static String SETTINGS_DATA_TABLE_TYPE = "data_table_type"; //$NON-NLS-1$

	private int nColumns = 5;

	private StringDialogField fDataTableDialogField;
	private StatusInfo fDataTableStatus;
	
	private Group fDataTableTypeGroup;
	private Button fNoneDataTableButton;
	private Button fMessageDataTableButton;
	private Button fMatchValidDataTableButton;
	private Button fMatchInvalidDataTableButton;
	private Button fRangeCheckDataTableButton;

	private DataTableFieldAdapter adapter = new DataTableFieldAdapter();

	/**
	 * @param pageName
	 */
	protected DataTableWizardPage(String pageName) {
		super(pageName);

		setTitle(NewWizardMessages.NewEGLTableWizardPageTitle);
		setDescription(NewWizardMessages.NewEGLTableWizardPageDescription);

		fDataTableStatus= new StatusInfo();
	}
	
	private class DataTableFieldAdapter implements IStringButtonAdapter, IDialogFieldListener, IListAdapter {

		// -------- IStringButtonAdapter
		 public void changeControlPressed(DialogField field) { }
		
		 // -------- IListAdapter
		 public void customButtonPressed(ListDialogField field, int index) { }
		
		 public void selectionChanged(ListDialogField field) { }
		
		 // -------- IDialogFieldListener
		 public void dialogFieldChanged(DialogField field) {
			handleDataTableDialogFieldChanged();
		 }
		
		 public void doubleClicked(ListDialogField field) { }
	}
	
	public void init() {
//		super.init();
		
		IDialogSettings section= getDialogSettings().getSection(PAGE_NAME);
		if (section != null) {
			try{
				dataTableType = section.getInt(SETTINGS_DATA_TABLE_TYPE);
				getConfiguration().setDataTableType(dataTableType);
			}
			catch(NumberFormatException e){
				dataTableType = DataTableConfiguration.NONE;
				section.put(SETTINGS_DATA_TABLE_TYPE, dataTableType);
			}
		}
	}		
	
	private DataTableConfiguration getConfiguration() {
		return (DataTableConfiguration)((DataTableWizard) getWizard()).getConfiguration();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		init();
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_DATA_TABLE_DEFINITION);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		createContainerControls(composite, nColumns);
		createPackageControls(composite);
		
		createSeparator(composite, nColumns);
		
		createEGLFileControls(composite);
		createDataTablePartControls(composite);
		
		modifyFileListeners();
		createDataTableTypeControls(composite);
		
//		createTemplateSelectionControls(composite);
		
		setControl(composite);
		
		validatePage();	
		Dialog.applyDialogFont(parent);
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}
	
	private void createDataTablePartControls(Composite parent) {
		fDataTableDialogField = new StringDialogField();
		fDataTableDialogField.setDialogFieldListener(adapter);
		fDataTableDialogField.setLabelText(NewWizardMessages.NewEGLTableWizardPagePartlabel);

		fDataTableDialogField.setText(getConfiguration().getDataTableName());
		fDataTableDialogField.setEnabled(false);

		fDataTableDialogField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);

		LayoutUtil.setWidthHint(fDataTableDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fDataTableDialogField.getTextControl(null));	
	}
	
	private void modifyFileListeners() {
		fEGLFileDialogField.getTextControl(null).addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				//Update Fields
				fDataTableDialogField.setText(fEGLFileDialogField.getText());				
			}
			
		});
	}
	
	private void createDataTableTypeControls(Composite parent) {		
		Composite dataTableTypeComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		dataTableTypeComposite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		dataTableTypeComposite.setLayoutData(gd);
		
		
		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 3;
		groupLayout.verticalSpacing = 2;
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		
		fDataTableTypeGroup = new Group(dataTableTypeComposite, SWT.NONE);
		fDataTableTypeGroup.setText(NewWizardMessages.NewEGLTableWizardPageDataTableSubTypeLabel);
		fDataTableTypeGroup.setLayout(groupLayout);
		fDataTableTypeGroup.setLayoutData(gd);
		
		fNoneDataTableButton = new Button(fDataTableTypeGroup, SWT.RADIO);
		fNoneDataTableButton.setText(NewWizardMessages.NewEGLTableWizardPageTableTypeNone);
		fNoneDataTableButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getConfiguration().setDataTableType(DataTableConfiguration.NONE);		
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		fMessageDataTableButton = new Button(fDataTableTypeGroup, SWT.RADIO);
		fMessageDataTableButton.setText(NewWizardMessages.NewEGLTableWizardPageTableTypeMessage);
		fMessageDataTableButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getConfiguration().setDataTableType(DataTableConfiguration.MESSAGE);
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		fMatchValidDataTableButton = new Button(fDataTableTypeGroup, SWT.RADIO);
		fMatchValidDataTableButton.setText(NewWizardMessages.NewEGLTableWizardPageTableTypeMatchvalid);
		fMatchValidDataTableButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getConfiguration().setDataTableType(DataTableConfiguration.MATCH_VALID);
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});	
		
		fMatchInvalidDataTableButton = new Button(fDataTableTypeGroup, SWT.RADIO);
		fMatchInvalidDataTableButton.setText(NewWizardMessages.NewEGLTableWizardPageTableTypeMatchinvalid);
		fMatchInvalidDataTableButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getConfiguration().setDataTableType(DataTableConfiguration.MATCH_INVALID);
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		fRangeCheckDataTableButton = new Button(fDataTableTypeGroup, SWT.RADIO);
		fRangeCheckDataTableButton.setText(NewWizardMessages.NewEGLTableWizardPageTableTypeRangecheck);
		fRangeCheckDataTableButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getConfiguration().setDataTableType(DataTableConfiguration.RANGE_CHECK);
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});	
		
		switch(dataTableType) {
			case DataTableConfiguration.MESSAGE:
				fMessageDataTableButton.setSelection(true);
				break;
			case DataTableConfiguration.MATCH_VALID:
				fMatchValidDataTableButton.setSelection(true);
				break;
			case DataTableConfiguration.MATCH_INVALID:
				fMatchInvalidDataTableButton.setSelection(true);
				break;
			case DataTableConfiguration.RANGE_CHECK:
				fRangeCheckDataTableButton.setSelection(true);
				break;
			default:
				fNoneDataTableButton.setSelection(true);
				break;	
		}
	}
	
	private void handleDataTableDialogFieldChanged() {
		//Update Configuration
		getConfiguration().setDataTableName(fDataTableDialogField.getText());
		
		//Validate Page
		validatePage();		
	}
	
	protected boolean validatePage() {
		
		//Validate the file
		if(super.validatePage()){
		
			//Validate the part
			fDataTableStatus.setOK();
			String partName= getConfiguration().getDataTableName();
			
			// must not be empty
			if (partName.length() == 0) {
				fDataTableStatus.setError(NewWizardMessages.NewTypeWizardPageErrorEnterPartName);
			}
			else {
				if (partName.indexOf('.') != -1) {
					fDataTableStatus.setError(NewWizardMessages.NewTypeWizardPageErrorQualifiedName);
				}
				else {
	//				IStatus val= EGLConventions.validateEGLTypeName(partName);
					
					validateEGLName(partName, EGLNameValidator.DATATABLE, fDataTableStatus);

					// Old Validation method:
	//				if (val.getSeverity() == IStatus.ERROR) {
	//					fDataTableStatus.setError(NewWizardMessages.getFormattedString("NewTypeWizardPage.error.InvalidPartName", val.getMessage())); //$NON-NLS-1$
	//				} else if (val.getSeverity() == IStatus.WARNING) {
	//					fDataTableStatus.setWarning(NewWizardMessages.getFormattedString("NewTypeWizardPage.warning.PartNameDiscouraged", val.getMessage())); //$NON-NLS-1$
	//				}
				}
			}
			
			updateStatus(new IStatus[] { fDataTableStatus });
			
			if(fDataTableStatus.getSeverity()==IStatus.ERROR)
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
		section.put(SETTINGS_DATA_TABLE_TYPE, getConfiguration().getDataTableType());		
	}

}
