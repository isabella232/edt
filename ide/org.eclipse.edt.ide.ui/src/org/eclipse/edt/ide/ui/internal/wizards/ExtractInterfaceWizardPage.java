/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.edt.ide.ui.wizards.ExtractInterfaceConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class ExtractInterfaceWizardPage extends WSDLInterfaceWizardPage {
	public static final String WIZPAGENAME_ExtractInterfaceWizardPage = "WIZPAGENAME_ExtractInterfaceWizardPage"; //$NON-NLS-1$
    private int nColumns = 5;
    
    protected class ExtractFunctionsListLabelProvider extends FunctionsListLabelProvider {
    	ExtractInterfaceConfiguration config;
    	public ExtractFunctionsListLabelProvider(ExtractInterfaceConfiguration config) {
    		this.config = config;
    	}
    	
	    public String getText(Object element) {
			if (element instanceof NestedFunction) {
			    return ExtractInterfaceConfiguration.getFunctionSimpleSignature((NestedFunction)element, false, config.getFPackage());
			}
		    return element == null ? "" : element.toString();//$NON-NLS-1$
	    }
    }
    
    /**
     * @param pageName
     */
    public ExtractInterfaceWizardPage(String pageName) {
        super(pageName);
		setTitle(NewWizardMessages.NewEGLExtractInterfaceWizardPageTitle);
		setDescription(NewWizardMessages.NewEGLExtractInterfaceWizardPageDescription);        
    }
    
	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_EXTRACT_INTERFACE);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		createContainerControls(composite, nColumns);
		createPackageControls(composite);		
		createSeparator(composite, nColumns);	
		createEGLFileControls(composite);
		
		modifyFileListeners();		
		createInterfaceEGLNameControls(composite, getConfiguration().getInterfaceName(), 0);
		fInterfaceDialogField.getTextControl(composite).setEditable(false);
        createFunctionListControls(composite, new ExtractFunctionsListLabelProvider(getConfiguration()), 0);		
        createCheckBoxOverwriteFileControl(composite);
        createUpdateEGLPathControls(composite);
		setControl(composite);
		
		validatePage();	
		Dialog.applyDialogFont(parent);
	}	
        
	public void updateControlValues() {
		super.updateControlValues();
		populateFuncList(0, fFunctionListField);
	}
	
	private ExtractInterfaceConfiguration getConfiguration() {
		return (ExtractInterfaceConfiguration)((EGLFileWizard) getWizard()).getConfiguration(getName());
	}
	
	protected EGLFileConfiguration getFileConfiguration() {
		return getConfiguration();
	}
	
	private void modifyFileListeners() {
		fEGLFileDialogField.getTextControl(null).addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				//Update Fields
			    fInterfaceDialogField.setText(fEGLFileDialogField.getText());				
			}
			
		});
	}	    
	
	@Override
	protected void populateFuncList(int indexInWSDLConfig, CheckedListDialogField funcListField) {	
		//clear the list 1st
		funcListField.removeAllElements();
		
	    List funcs = getConfiguration().getFFunctions();
	    for(int i=0; i<funcs.size(); i++) {
	    	boolean isChecked = getConfiguration().getFunctionSelectionState(i);
	    	funcListField.addElement(funcs.get(i));
	    	funcListField.setChecked(funcs.get(i), isChecked);
	    }
	}	
	
	@Override
	protected void handleInterfaceNameDialogFieldChanged(int indexInWSDLConfig, StringDialogField field) {
		//Update Configuration
		getConfiguration().setInterfaceName(field.getText());
		
		//Validate Page
		validatePage();		
	}
	
	@Override
    protected void handleFunctionListFieldChanged(int indexInWSDLConfig, CheckedListDialogField funcListField) {        
        //need to update the config               
        int size = funcListField.getSize();
        for(int i=0; i<size; i++) {
            boolean isChecked = funcListField.isChecked(funcListField.getElement(i));
            getConfiguration().setFunctionsSelectionState(i, isChecked);
        }        
    }	   
	
    @Override
	protected void updateOtherConfiguration() {
	}    
}
