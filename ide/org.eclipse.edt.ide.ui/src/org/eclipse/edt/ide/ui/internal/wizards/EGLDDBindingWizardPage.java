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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.internal.dialogs.EGLPartSelectionDialog;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.BindingBaseConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLDDBindingConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class EGLDDBindingWizardPage extends EGLFileWizardPage {
	
	public static final String WIZPAGENAME_EGLDDBindingWizardPage = "WIZPAGENAME_EGLDDBindingWizardPage"; //$NON-NLS-1$
	protected int nColumns = 3;
	private List<Button> bindingButtons = new ArrayList<Button>();
	
	protected StringDialogField fNameField;	
	protected StatusInfo fNameStatus;
	
	public EGLDDBindingWizardPage(String pageName) {
		super(pageName);
		setTitle(NewWizardMessages.EGLDDBindingWizPageTitle);
		setDescription(NewWizardMessages.EGLDDBindingWizPageDescription);		
		fNameStatus = new StatusInfo();
	}
	
	private EGLDDBindingConfiguration getConfiguration(){
		return (EGLDDBindingConfiguration)((EGLPartWizard)getWizard()).getConfiguration(getName());
	}

	protected EGLDDBindingConfiguration getEGLDDBindingConfiguration() {
		return (EGLDDBindingConfiguration)((EGLPartWizard)getWizard()).getConfiguration(EGLDDBindingWizardPage.WIZPAGENAME_EGLDDBindingWizardPage); 
	}
	
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGLDDWIZ_ADDBINDING);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
			
		createChooseBindingTypeControl(composite);
		
		setControl(composite);
		Dialog.applyDialogFont(parent);
	}
	
	protected void createChooseBindingTypeControl(Composite parent) {
		Group grp = new Group(parent, SWT.NONE);
		grp.setText(NewWizardMessages.ModuleChooseBindingTypes);
		GridLayout groupLayout = new GridLayout();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = nColumns;		
		grp.setLayout(groupLayout);
		grp.setLayoutData(gd);
		EGLDDBindingWizardProvider[] providers = EGLDDBindingProviderRegistry.singleton.getEGLDDBindingWizardProviders();
		for ( int i = 0; i < providers.length; i ++ ) {
			Button button = createBindingRadioButton(grp, providers[i].description, providers[i].bindingId);
			bindingButtons.add( button );
			
			// For GTK we need to manually select the first radio button
			if (i == 0) {
				button.setSelection(true);
			}
		}
		
	}

	private Button createBindingRadioButton(Group grp, String btnText, final int bindingType){
		Button btn = new Button(grp, SWT.RADIO);
		btn.setText(btnText);
		btn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if(((Button)(e.getSource())).getSelection())
					HandleBindingTypeChanged(bindingType);
			}
		});
		return btn;
	}
	
	protected void HandleBindingTypeChanged(int bindingType) {
		getConfiguration().setBindingType(bindingType);	
		updateWizardDialogButtons(this);
	}

	protected void updateWizardDialogButtons(IWizardPage currWizPage) {
		IWizard wiz = getWizard();
		IWizardContainer wizDlg = getWizard().getContainer();
		if(wiz instanceof EGLDDBindingWizard){
			((EGLDDBindingWizard)wiz).updatePagePathAndNextPage(currWizPage);
		}
		wizDlg.updateButtons();
	}

	protected void createComponentNameControl(Composite parent, String labelName, final BindingBaseConfiguration esConfig) {
		createComponentNameNoBrowseControl(parent, labelName, esConfig);	
	}

	protected void createComponentNameNoBrowseControl(Composite parent, String labelName, final BindingBaseConfiguration esConfig) {
		fNameField = new StringDialogField();
		fNameField.setLabelText(labelName);	
		fNameField.setText(esConfig.getBindingName());		
		fNameField.setDialogFieldListener(new IDialogFieldListener(){
			public void dialogFieldChanged(DialogField field) {
				HandleBindingNameChanged(esConfig);
			}			
		});		
	
		fNameField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);
	
		LayoutUtil.setWidthHint(fNameField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));
	}
		
	protected void validateBindingName(StatusInfo statusinfo){
		if(fNameField != null){
			String currname = fNameField.getText();
			if(currname == null || currname.trim().length()==0){
				statusinfo.setError(NewWizardMessages.EGLDDBindingWizPageValidationSetBindingName);
			}
			else{
				Binding binding = EGLDDRootHelper.getBindingByName(getEGLDDBindingConfiguration().getDeploymentRoot(), currname);
				if(binding != null){
					statusinfo.setError(NewWizardMessages.bind(NewWizardMessages.EGLDDBindingWizPageValidationBindingExisted, currname));
				}
			}				
		}
	}

	protected void createStringDialogField(Composite parent, StringDialogField field, IDialogFieldListener fieldAdapter) {
		field.setDialogFieldListener(fieldAdapter);
		field.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);
		LayoutUtil.setWidthHint(field.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(field.getTextControl(null));							
	}

	protected void HandleBindingNameChanged(final BindingBaseConfiguration esConfig) {
		esConfig.setBindingName(fNameField.getText());
	}
	
	/**
	 * it will list all the EGL interface in the project and its referenced project
	 * it will return the fully qualified egl interface name string if user choose one
	 * null if no EGL interfaces are available or user did not click ok on the dialog
	 * @param project
	 * @return null, if user did not choose ok
	 */
	protected IPart browsedEGLPartFQValue(IProject project, int eglPartType, boolean isWorkspaceScope) {
		return browsedEGLPartFQValue(project, eglPartType, isWorkspaceScope, null);
	}

	/**
	 * it will list all the EGL interface in the project and its referenced project
	 * it will return the fully qualified egl interface name string if user choose one
	 * null if no EGL interfaces are available or user did not click ok on the dialog
	 * @param project
	 * @param helpId 
	 * @return null, if user did not choose ok
	 */
	protected IPart browsedEGLPartFQValue(IProject project, int eglPartType, boolean isWorkspaceScope, String helpId) {
		IPart newPart = null;
		EGLPartSelectionDialog dialog = getEGLPartSelectionDialog(eglPartType,
																NewWizardMessages.NewTypeWizardPageInterfaceDialogTitle, 
																NewWizardMessages.NewTypeWizardPageInterfaceDialogLabel, project,isWorkspaceScope, helpId);
		if(dialog.open() == IDialogConstants.OK_ID)
		{
			Object[] results = dialog.getResult();
			if(results.length>0)
			{
				newPart = (IPart)(results[0]);
			}
		}
		return newPart;
	}

	/**
	 * helper methods
	 * @param elemKind type of egl Part, i.e. IEGLSearchConstants.SERVICE
	 * @return
	 */
	protected EGLPartSelectionDialog getEGLPartSelectionDialog(int elemKind, String title, String message, IProject project, boolean isWorkspaceScope) {
		return getEGLPartSelectionDialog(elemKind, title, message, project, isWorkspaceScope, null);
	}

	protected EGLPartSelectionDialog getEGLPartSelectionDialog(int elemKind, String title, String message, IProject project, boolean isWorkspaceScope, final String helpId) {
		Shell shell = getShell();
		EGLFileConfiguration fileConfig = new EGLFileConfiguration();
		
	
		IRunnableContext context = getWizard().getContainer();
		
		IEGLSearchScope searchScope = null;
		if(isWorkspaceScope)
			searchScope = SearchEngine.createWorkspaceScope();
		else{
			IEGLProject eglProj = EGLCore.create(project);
			searchScope = SearchEngine.createEGLSearchScope(new IEGLElement[]{eglProj}, true);
		}
		
	    EGLPartSelectionDialog dialog = new EGLPartSelectionDialog(shell, context, elemKind, "", null, searchScope, fileConfig) { //$NON-NLS-1$
	    	public Control createDialogArea(Composite parent) {
				Control control = super.createDialogArea(parent);
				if(helpId != null)
					PlatformUI.getWorkbench().getHelpSystem().setHelp(control, helpId);
				return control;
			}
	    }; //$NON-NLS-1$
		dialog.setTitle(title);	    
		dialog.setMessage(message);
		return dialog;
	}
	
	@Override
	public boolean canFlipToNextPage() {
		if(getWizard().getStartingPage() == this) {
		   return true;
		} else {
			return super.canFlipToNextPage();
		}
	}
	
	public IWizardPage getNextPage() {
		return getWizard().getNextPage(this);
	}
	
	/**
	 * Subclasses should override to set any error messages and the page complete flag.
	 */
	protected boolean determinePageCompletion() {
		setPageComplete(true);
		return true;
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		determinePageCompletion();
	}
}
