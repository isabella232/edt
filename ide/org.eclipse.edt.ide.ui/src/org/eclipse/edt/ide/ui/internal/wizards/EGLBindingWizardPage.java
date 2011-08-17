/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.internal.model.SourcePartElementInfo;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.ReferenceProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.ui.CommTypes;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLBindingProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDProtocolFormPage;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.BindingBaseConfiguration;
import org.eclipse.edt.ide.ui.wizards.BindingEGLConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLDDBindingConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;

public class EGLBindingWizardPage extends EGLDDBindingWizardPage {
	public static final String WIZPAGENAME_EGLBindingWizardPage = "WIZPAGENAME_EGLBindingWizardPage"; //$NON-NLS-1$
	private StringDialogField fServiceNameField;
	private StringDialogField fAliasField;

	private EGLBindingFieldAdapter adapter = new EGLBindingFieldAdapter();
	private Button[] fCommTypeBtns;
	private TableViewer fTableViewer;
	private Combo fComboProtocol;
	private StatusInfo fProtocolRefStatus;
	private StatusInfo fServiceNameStatus;
	
	private class commTypeBtnSelectionAdapter extends SelectionAdapter{
		private int btnIndex;
		public commTypeBtnSelectionAdapter(int btnIndex){
			this.btnIndex = btnIndex;
		}
		
   		public void widgetSelected(SelectionEvent e) {
   			HandleProtocolRadioButtonSelected(btnIndex);
		}   				
	}
	
	private class EGLBindingFieldAdapter implements IStringBrowseButtonFieldAdapter{
		public void dialogFieldChanged(DialogField field) {
			if(field == fNameField)
				HandleBindingNameChanged(getBindingEGLConfiguration());
			else if(field == fAliasField)
				HandleAliasFieldChanged();
			else if(field == fServiceNameField)
				HandleServiceNameFieldChanged();
		}

		public void changeControlPressed(DialogField field) {
			if(field == fNameField)
				HandleEGLBindingNameBrowsePressed();			
		}
	}
	
	public EGLBindingWizardPage(String pageName) {
		super(pageName);
		setTitle(NewWizardMessages.EGLBindingWizPageTitle);
		setDescription(NewWizardMessages.EGLBindingWizPageDescription);
		nColumns = 4;
		fServiceNameStatus = new StatusInfo();
		fProtocolRefStatus = new StatusInfo();
	}
	
	protected void HandleProtocolRadioButtonSelected(int selBtnIndex){
		boolean sel = fCommTypeBtns[selBtnIndex].getSelection();
		updateControlState();
		if(sel){
			getBindingEGLConfiguration().setSelectedCommTypeBtnIndex(selBtnIndex);
			Protocol protocol = getBindingEGLConfiguration().getProtocol();
			fTableViewer.setInput(protocol);
			validatePage();
		}		
	}
	
	public void HandleEGLBindingNameBrowsePressed() {
		IPart servicePart = browsedEGLPartFQValue(getBindingEGLConfiguration().getProject(), IEGLSearchConstants.SERVICE|IEGLSearchConstants.INTERFACE, true);
		if(servicePart != null){
			SourcePart sourcePart = (SourcePart)servicePart;
			
			//set to be the simple name
			fNameField.setText(servicePart.getElementName());
			
			try {
				SourcePartElementInfo elemInfo = (SourcePartElementInfo)sourcePart.getElementInfo();
				
				//if it's interface, do not set these field
				if(elemInfo.isService()){
					fServiceNameField.setText(servicePart.getFullyQualifiedName());
					fAliasField.setText(getBindingEGLConfiguration().getAliasFrServicePart(servicePart));					
				}					
			} catch (EGLModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected void HandleBindingNameChanged(BindingBaseConfiguration esConfig) {
		super.HandleBindingNameChanged(esConfig);
		validatePage();
	}
	
	protected boolean validatePage(){
		fNameStatus.setOK();
		fServiceNameStatus.setOK();
		fProtocolRefStatus.setOK();
		
		validateBindingName(fNameStatus);
		
		if(fServiceNameField != null){
			String serviceName = fServiceNameField.getText();
			if(serviceName == null || serviceName.trim().length() ==0){
				fServiceNameStatus.setError(NewWizardMessages.EGLBindingValidationMsgFQServiceName);
			}
		}
		
		if(fCommTypeBtns != null && fCommTypeBtns[0] != null){
			if(fCommTypeBtns[0].getSelection()){
				String refProtocol = fComboProtocol.getText();
				if(refProtocol == null || refProtocol.trim().length() ==0){
					fProtocolRefStatus.setError(NewWizardMessages.EGLBindingValidationMsgRefProtocol);
				}
			}
		}
		
		updateStatus(new IStatus[]{fNameStatus, fServiceNameStatus, fProtocolRefStatus});
		return (!fNameStatus.isError() && !fServiceNameStatus.isError() && !fProtocolRefStatus.isError());
	}

	private BindingEGLConfiguration getConfiguration(){
		return (BindingEGLConfiguration)((EGLPartWizard)getWizard()).getConfiguration(getName());
	}
	
	protected BindingEGLConfiguration getBindingEGLConfiguration(){
		return (BindingEGLConfiguration)((EGLPartWizard)getWizard()).getConfiguration(EGLBindingWizardPage.WIZPAGENAME_EGLBindingWizardPage);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGLDDWIZ_ADDBINDING_EGL);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		createBindingControls(composite);
		setControl(composite);
		validatePage();
		Dialog.applyDialogFont(parent);
	}
	
	protected void createBindingControls(Composite composite){
		EGLDeploymentRoot deploymentRoot = getEGLDDBindingConfiguration().getDeploymentRoot();
		createBindingControls(composite, getEGLDDBindingConfiguration().getBindingEGLConfiguration(), deploymentRoot, false);		
	}

	protected void createBindingControls(Composite composite, BindingEGLConfiguration bindingEGLConfig, EGLDeploymentRoot deploymentRoot, boolean isReadOnly) {
		createComponentNameControl(composite, NewWizardMessages.EGLBindingNameLabel, bindingEGLConfig);
		createServiceNameControl(composite, isReadOnly);
		createAliasControl(composite, isReadOnly);
		createProtocolControl(composite, deploymentRoot, EGLDDBindingConfiguration.BINDINGTYPE_EGL);
	}
	
	protected void createComponentNameControl(Composite parent, String labelName, final BindingBaseConfiguration esConfig) {		
		fNameField = createStringBrowseButtonDialogField(parent, adapter, labelName, esConfig.getBindingName(), nColumns-1);
	}	

	protected void createProtocolControl(Composite parent, EGLDeploymentRoot deploymentRoot, int bindingType) {
		Group grp = new Group(parent, SWT.NONE);
		grp.setText(NewWizardMessages.ChooseProtocolGrpLabel);
		
		GridLayout g1 = new GridLayout(2, true);
		grp.setLayout(g1);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = nColumns;
		grp.setLayoutData(gd);
		
		List commtypeList = CommTypes.getSupportedProtocol(bindingType);
		fCommTypeBtns = new Button[commtypeList.size()+1];
		
		fCommTypeBtns[0] = new Button(grp, SWT.RADIO);
		fCommTypeBtns[0].setText(NewWizardMessages.ChooseProtocolLabel);
		fComboProtocol = new Combo(grp, SWT.DROP_DOWN | SWT.READ_ONLY);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fComboProtocol.setLayoutData(gd);
		updateProtocolComboItems(deploymentRoot, bindingType);
        
        fComboProtocol.addSelectionListener(new SelectionAdapter(){
        	public void widgetSelected(SelectionEvent e) {
        		HandleProtocolComboSelectionChanged();
        	}
        });	
        
        fCommTypeBtns[0].addSelectionListener(new SelectionAdapter(){
        	public void widgetSelected(SelectionEvent e) {
        		boolean sel = fCommTypeBtns[0].getSelection();
        		updateControlState();
        		if(sel){           			
        			HandleProtocolComboSelectionChanged();
        		}
        	}
        });
		
        int i=1;
        for(Iterator it=commtypeList.iterator(); it.hasNext(); i++){
        	CommTypes commtype = (CommTypes)it.next();
        	fCommTypeBtns[i] = new Button(grp, SWT.RADIO);
        	fCommTypeBtns[i].setText(commtype.getLiteral());
        	fCommTypeBtns[i].addSelectionListener(new commTypeBtnSelectionAdapter(i));
        }		
 
        int selCommTypeBtnIndex = getBindingEGLConfiguration().getSelectedCommTypeBtnIndex();
        
        Table t = new Table(parent, SWT.SINGLE|SWT.FULL_SELECTION|SWT.H_SCROLL | SWT.V_SCROLL|SWT.BORDER);
        EGLDDProtocolFormPage.createProtocolAttribTable(NewWizardMessages.TableColAttrib, NewWizardMessages.TableColValue, nColumns, t);
        fTableViewer = EGLBindingProtocol.createProtocolAttributeTableViewer(t);
        
        fCommTypeBtns[selCommTypeBtnIndex].setSelection(true);
        HandleProtocolRadioButtonSelected(selCommTypeBtnIndex);        
	}

	protected void updateProtocolComboItems(EGLDeploymentRoot deploymentRoot, int bindingtype) {
		fComboProtocol.setItems(EGLDDRootHelper.getProtocolComboItems(deploymentRoot, fComboProtocol, bindingtype));
	}

	private void updateControlState(){
		boolean selProtocol = fCommTypeBtns[0].getSelection();
		fComboProtocol.setEnabled(selProtocol);
		fTableViewer.getTable().setEnabled(!selProtocol);			
	}
	
	protected void HandleProtocolComboSelectionChanged() {
		getBindingEGLConfiguration().setSelectedCommTypeBtnIndex(0);
		String protocolRef = fComboProtocol.getText();     				
		if(protocolRef != null && protocolRef.length()>0){
			Object data = fComboProtocol.getData();
			if(data instanceof Protocol[]){
				Protocol[] protocols = (Protocol[])data;
				int index = fComboProtocol.getSelectionIndex();
				ReferenceProtocol refProtocol = (ReferenceProtocol)getBindingEGLConfiguration().getProtocol();    					
				refProtocol.setRef(protocols[index].getName());
				fTableViewer.setInput(protocols[index]);
			}
		}	
		validatePage();
	}

	private void createServiceNameControl(Composite composite, boolean isReadOnly) {
		fServiceNameField = new StringDialogField();
		fServiceNameField.setLabelText(NewWizardMessages.ServiceNameLabel);
		fServiceNameField.setText(getBindingEGLConfiguration().getEGLServiceOrInterface());	
		createStringDialogField(composite, fServiceNameField, adapter);
		fServiceNameField.getTextControl(composite).setEditable(!isReadOnly);
	}

	private void createAliasControl(Composite composite, boolean isReadOnly) {
		fAliasField = new StringDialogField();
		fAliasField.setLabelText(NewWizardMessages.ServiceAliasLabel);
		fAliasField.setText(getBindingEGLConfiguration().getAlias());
		createStringDialogField(composite, fAliasField, adapter);	
		fAliasField.getTextControl(composite).setEditable(!isReadOnly);
	}
	
	public void HandleAliasFieldChanged() {
		getBindingEGLConfiguration().setAlias(fAliasField.getText());		
	}

	public void HandleServiceNameFieldChanged() {
		getBindingEGLConfiguration().setEGLServiceOrInterface(fServiceNameField.getText());	
		validatePage();
	}
	
		
}
