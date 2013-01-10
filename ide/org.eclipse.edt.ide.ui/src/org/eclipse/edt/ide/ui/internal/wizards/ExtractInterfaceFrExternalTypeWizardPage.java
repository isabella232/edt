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

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.edt.ide.ui.wizards.ExtractInterfaceConfiguration;
import org.eclipse.edt.ide.ui.wizards.ExtractInterfaceFrExternalTypeConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class ExtractInterfaceFrExternalTypeWizardPage extends
              ExtractInterfaceWizardPage {
	public static final String WIZPAGENAME_ExtractInterfaceFrExternalTypeWizardPage = "WIZPAGENAME_ExtractInterfaceFrExternalTypeWizardPage"; //$NON-NLS-1$
	private int nColumns = 5;
	
	public ExtractInterfaceFrExternalTypeWizardPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_EXTRACT_INTERFACE_FR_ET);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		createContainerControls(composite, nColumns);
		createPackageControls(composite);		
		createSeparator(composite, nColumns);	
		createEGLFileControls(composite);
		
		createTabControls(composite);
        
        createCheckBoxOverwriteFileControl(composite);
        createUpdateEGLPathControls(composite);
		setControl(composite);
		
		validatePage();	
		Dialog.applyDialogFont(parent);
	}	
	
	private ExtractInterfaceFrExternalTypeConfiguration getConfiguration() {
		return (ExtractInterfaceFrExternalTypeConfiguration)((EGLFileWizard) getWizard()).getConfiguration(getName());
	}
	
	protected EGLFileConfiguration getFileConfiguration() {
		return getConfiguration();
	}
	
	protected void createTabItems(CTabFolder tabfolder) {
	    List extractInterfaceConfigs = getConfiguration().getExtractInterfaceConfigurations();
	    int interfaceCnt = extractInterfaceConfigs.size();
	    
	    if(tabControls == null)	        
	        tabControls = new Composite[interfaceCnt];
	    
	    if(interfaceCnt != tabControls.length)
	    	tabControls = new Composite[interfaceCnt];
	    
	    int i=0;
	    for(Iterator it = extractInterfaceConfigs.iterator(); it.hasNext(); i++) {
	    	ExtractInterfaceConfiguration extractInterfaceConfig = (ExtractInterfaceConfiguration)it.next();
            String interfaceName = extractInterfaceConfig.getInterfaceName() ;
            
            createTabItem(tabfolder, i, interfaceName, interfaceName, new ExtractFunctionsListLabelProvider(extractInterfaceConfig));		            
	    }
	}
	
	/**
	 * override the parent implementation, ignore the input parameter
	 */
	protected void populateFuncList(int index, CheckedListDialogField funcListField) {	
		//clear the list 1st
		funcListField.removeAllElements();
		
		ExtractInterfaceConfiguration eConfig = (ExtractInterfaceConfiguration)(getConfiguration().getExtractInterfaceConfigurations().get(index));
	    List funcs = eConfig.getFFunctions(); 
	    for(int i=0; i<funcs.size(); i++) {
	    	boolean isChecked = eConfig.getFunctionSelectionState(i);
	    	funcListField.addElement(funcs.get(i));
	    	funcListField.setChecked(funcs.get(i), isChecked);
	    }
	}	
	
	/**
	 * override the parent implementation
	 */	
	protected void handleInterfaceNameDialogFieldChanged(int index, StringDialogField field) {
		
		ExtractInterfaceConfiguration eConfig = (ExtractInterfaceConfiguration)(getConfiguration().getExtractInterfaceConfigurations().get(index));
		
		//Update Configuration
		eConfig.setInterfaceName(field.getText());
		
		//Validate Page
		validatePage();		
	}

	/**
	 * override the parent implementation, ignore the input parameter
	 * when the check box gets checked/unchecked
	 *
	 */
    protected void handleFunctionListFieldChanged(int index, CheckedListDialogField funcListField) {        
    	ExtractInterfaceConfiguration eConfig = (ExtractInterfaceConfiguration)(getConfiguration().getExtractInterfaceConfigurations().get(index));
        //need to update the config               
        int size = funcListField.getSize();
        for(int i=0; i<size; i++) {
            boolean isChecked = funcListField.isChecked(funcListField.getElement(i));
            eConfig.setFunctionsSelectionState(i, isChecked);
        }        
    }	   	
}
