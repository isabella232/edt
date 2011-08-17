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
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.ui.CommTypes;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLBindingProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDProtocolFormPage;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.EGLDDProtocolConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;

public class EGLDDProtocolWizardPage extends EGLElementWizardPage {
	protected int nColumns = 3;
	private StringDialogField fNameField;
	protected Button[] fCommTypeBtns;
	private TableViewer fTableViewer;
	protected StatusInfo fNameStatus;
	public static final String WIZPAGENAME_EGLDDProtocolWizardPage = "WIZPAGENAME_EGLDDProtocolWizardPage"; //$NON-NLS-1$
	
	private class commTypeBtnSelectionAdapter extends SelectionAdapter{
		private int btnIndex;
		public commTypeBtnSelectionAdapter(int btnIndex){
			this.btnIndex = btnIndex;
		}
		
   		public void widgetSelected(SelectionEvent e) {
			boolean sel = fCommTypeBtns[btnIndex].getSelection();
			if(sel){
				CommTypes newcommType = CommTypes.get(btnIndex);
				getConfiguration().setCommType(newcommType);				
				setTableValues( newcommType );
			}
		}

	}
	
	public EGLDDProtocolWizardPage(String pageName) {
		super(pageName);
		setTitle(NewWizardMessages.EGLDDProtocolWizPageTitle);
		setDescription(NewWizardMessages.EGLDDProtocolWizPageDescription);
		fNameStatus = new StatusInfo();
	}

	private void setTableValues( CommTypes newcommType )
	{
		Protocol protocol = getConfiguration().getProtocol(newcommType);
		fTableViewer.setInput(protocol);
	}  
	
	protected void updateProtocolValues( Protocol protocol )
	{
		if( protocol != null )
		{
			if( fTableViewer != null )
			{
				fTableViewer.setInput(protocol);
			}
			if( fNameField != null )
			{
				fNameField.setText(protocol.getName());
			}
			setSelectionProtocolType();
		}
	}  
	
	protected void setEnabledProtocolType( boolean enabled )
	{
		int protocolCnt = fCommTypeBtns == null ? 0 : fCommTypeBtns.length;
		for( int idx = 0; idx < protocolCnt; idx++ )
		{
			fCommTypeBtns[idx].setEnabled(enabled);
		}
	}
	
	private void setSelectionProtocolType()
	{
		int protocolCnt = fCommTypeBtns == null ? 0 : fCommTypeBtns.length;
		for( int idx = 0; idx < protocolCnt; idx++ )
		{
	        fCommTypeBtns[idx].setSelection(false);
		}
		if( protocolCnt > 0 )
		{
			fCommTypeBtns[CommTypes.VALUES.indexOf(getConfiguration().getCommType())].setSelection(true);
		}
	}
	
	protected void setEnabledProtocolTableValues( boolean enabled )
	{
		if(fTableViewer != null)
		{
			fTableViewer.getControl().setEnabled(enabled);
		}
	}
	
	protected void setEnabledProtocolName( boolean enabled )
	{
		fNameField.setEnabled(enabled);
	}
	
	protected boolean isEnabledProtocolName()
	{
		return fNameField == null ? false : fNameField.isEnabled();
	}
	
	protected EGLDDProtocolConfiguration getConfiguration(){
		return (EGLDDProtocolConfiguration)((EGLDDProtocolWizard)getWizard()).getConfiguration(getName());
	}
		
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = createCompositeControl(parent);
		
		createProtocolNameControl(composite);
		createProtocolControl(composite);
		
		setControl(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGLDDWIZ_ADDPROTOCOL);
		Dialog.applyDialogFont(parent);
	}

	private void createProtocolNameControl(Composite composite) {
		fNameField = new StringDialogField();
		fNameField.setDialogFieldListener(new IDialogFieldListener(){
			public void dialogFieldChanged(DialogField field) {
				handleProtocolNameChange();
			}			
		});
		
		fNameField.setLabelText(NewWizardMessages.ProtocolNameLabel);
		fNameField.setText(getConfiguration().getName());
		
		fNameField.doFillIntoGrid(composite, nColumns-1);
		DialogField.createEmptySpace(composite);

		LayoutUtil.setWidthHint(fNameField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));			
	}

	protected void handleProtocolNameChange()
	{
		getConfiguration().setName(fNameField.getText());
		validatePage();
	}
	private void createProtocolControl(Composite parent) {
		Group grp = new Group(parent, SWT.NONE);
		grp.setText(NewWizardMessages.ChooseProtocolGrpLabel);
		
		GridLayout g1 = new GridLayout(2, true);
		grp.setLayout(g1);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = nColumns;
		grp.setLayoutData(gd);
		
		List commtypeList = CommTypes.VALUES;
		fCommTypeBtns = new Button[commtypeList.size()];
		
        int i=0;
        for(Iterator it=commtypeList.iterator(); it.hasNext(); i++){
        	CommTypes commtype = (CommTypes)it.next();
        	fCommTypeBtns[i] = new Button(grp, SWT.RADIO);
        	fCommTypeBtns[i].setText(commtype.getLiteral());
        	fCommTypeBtns[i].addSelectionListener(new commTypeBtnSelectionAdapter(i));
        }		
		
        CommTypes defaultCommType = getConfiguration().getCommType();
        fCommTypeBtns[CommTypes.VALUES.indexOf(defaultCommType)].setSelection(true);
        
        Table t = new Table(parent, SWT.SINGLE|SWT.FULL_SELECTION|SWT.H_SCROLL | SWT.V_SCROLL|SWT.BORDER);
        EGLDDProtocolFormPage.createProtocolAttribTable(NewWizardMessages.TableColAttrib, NewWizardMessages.TableColValue, nColumns, t);
        fTableViewer = EGLBindingProtocol.createProtocolAttributeTableViewer(t);
        setTableValues( defaultCommType );
        
	}

	protected Composite createCompositeControl(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		return composite;
	}

	protected boolean validatePage(){
		fNameStatus.setOK();
		
		String protocolName = fNameField.getText();
		if(protocolName == null || protocolName.trim().length()==0){
			fNameStatus.setError(NewWizardMessages.EGLDDProtocolWizPageValidationSetProtocolName);
		}
		else{
			Protocol protocol = EGLDDRootHelper.getProtocolByName(getConfiguration().getDeploymentRoot(), protocolName);
			if(protocol != null){
				fNameStatus.setError(NewWizardMessages.bind(NewWizardMessages.EGLDDProtocolWizPageValidationProtocolExisted, protocolName));
			}
		}
		
		updateStatus(new IStatus[]{fNameStatus});
		return !fNameStatus.isError();
	}
}
