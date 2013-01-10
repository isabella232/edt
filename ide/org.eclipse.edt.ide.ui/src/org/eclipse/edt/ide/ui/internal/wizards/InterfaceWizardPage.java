/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.dialogs.InterfaceSelectionDialog;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.InterfaceConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class InterfaceWizardPage extends InterfaceSelectionListWizardPage {
	
	private int nColumns = 5;
	private StringDialogField fInterfaceDialogField;
	private InterfaceFieldAdapter adapter = new InterfaceFieldAdapter();
	private StatusInfo fInterfaceStatus;

	private class InterfaceFieldAdapter extends InterfaceListFieldAdapter
			implements IStringButtonAdapter, IDialogFieldListener {
		public void changeControlPressed(DialogField field) {
		}

		public void dialogFieldChanged(DialogField field) {
			handleInterfaceDialogFieldChanged();
		}
	}
	
	
	protected InterfaceWizardPage(String pageName) {
		super(pageName);

		setTitle(NewWizardMessages.NewEGLInterfaceWizardPageTitle);
		setDescription(NewWizardMessages.NewEGLInterfaceWizardPageDescription);
		fInterfaceStatus = new StatusInfo();
		initSuperInterfacesControl(adapter);
	}
	
	private InterfaceConfiguration getConfiguration() {
		return (InterfaceConfiguration)((InterfaceWizard) getWizard()).getConfiguration();
	}
	
	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_INTERFACE_DEFINITION);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		createContainerControls(composite, nColumns);
		createPackageControls(composite);
		createSeparator(composite, nColumns);
		
		createEGLFileControls(composite);
		createInterfaceControls(composite);
		modifyFileListeners();		
		setControl(composite);
		
		validatePage();	
		Dialog.applyDialogFont(parent);
	}
	
	private void createInterfaceControls(Composite parent) {
	    fInterfaceDialogField = new StringDialogField();
	    fInterfaceDialogField.setDialogFieldListener(adapter);
	    fInterfaceDialogField.setLabelText(NewWizardMessages.NewEGLInterfaceWizardPagePartlabel);

	    fInterfaceDialogField.setText(getConfiguration().getInterfaceName());
	    fInterfaceDialogField.setEnabled(false);
	    fInterfaceDialogField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);

		LayoutUtil.setWidthHint(fInterfaceDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fInterfaceDialogField.getTextControl(null));	
	}
	
	@Override
	protected InterfaceSelectionDialog getInterfaceSelectionDialog(IEGLProject project) {
	    String interfaceSubType = null;
	    if(getConfiguration().getInterfaceType()==InterfaceConfiguration.BASIC_INTERFACE)
	        interfaceSubType = IEGLConstants.INTERFACE_SUBTYPE_BASIC;
	    else if(getConfiguration().getInterfaceType()==InterfaceConfiguration.JAVAOBJ_INTERFACE)	    	
	        interfaceSubType = IEGLConstants.INTERFACE_SUBTYPE_JAVAOBJECT;
	        
	    InterfaceSelectionDialog dialog = new InterfaceSelectionDialog(getShell(), getWizard().getContainer(), fSuperInterfacesDialogField, IEGLSearchConstants.INTERFACE, interfaceSubType, getConfiguration(), project);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPageInterfacesDialogInterfaceTitle);	    
	    return dialog;
	}
	
	private void modifyFileListeners() {
		fEGLFileDialogField.getTextControl(null).addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				//Update Fields
			    fInterfaceDialogField.setText(fEGLFileDialogField.getText());				
			}
			
		});
	}
	
	private void handleInterfaceDialogFieldChanged() {
		//Update Configuration
		getConfiguration().setInterfaceName(fInterfaceDialogField.getText());
		validatePage();		
	}
	
   protected boolean validatePage() {
		if(super.validatePage()) {
		
			//Validate the part
		    fInterfaceStatus.setOK();
			String partName= getConfiguration().getInterfaceName();
			
			// must not be empty
			if (partName.length() == 0) {
			    fInterfaceStatus.setError(NewWizardMessages.NewTypeWizardPageErrorEnterPartName);
			} else {
				if (partName.indexOf('.') != -1) {
				    fInterfaceStatus.setError(NewWizardMessages.NewTypeWizardPageErrorQualifiedName);
				} else {
					validateEGLName(partName, EGLNameValidator.LIBRARY, fInterfaceStatus);			
				}
			}
			
			updateStatus(new IStatus[] { fInterfaceStatus });
			
			if(fInterfaceStatus.getSeverity()==IStatus.ERROR)
				return false;
			else
				return true;
		} else
			return false;
	}
}
