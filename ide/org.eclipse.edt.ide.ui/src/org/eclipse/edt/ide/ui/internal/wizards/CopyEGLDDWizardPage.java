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
package org.eclipse.edt.ide.ui.internal.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Services;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.internal.deployment.ui.FileBrowseDialog;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.edt.ide.ui.wizards.CopyEGLDDConfiguration;
import org.eclipse.edt.ide.ui.wizards.CopyEGLDDConfiguration.EGLDeploymentTreeContentProvider;
import org.eclipse.edt.ide.ui.wizards.CopyEGLDDConfiguration.EGLDeploymentTreeLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

public class CopyEGLDDWizardPage extends EGLPackageWizardPage {
	
	public static final String WIZPAGENAME_CopyEGLDDWizardPage = "WIZPAGENAME_CopyEGLDDWizardPage"; //$NON-NLS-1$
	private int nColumns=4;
	private StringButtonDialogField fEGLDDFileNameField;
	private ContainerCheckedTreeViewer fTreeViewer;
	private EGLDeploymentTreeContentProvider fTreeContentProvider;
	private EGLDeploymentTreeLabelProvider fTreeLabelProvider;
	private StatusInfo fFileStatus;

	public CopyEGLDDWizardPage(String pageName) {
		super(pageName);
		setTitle(NewWizardMessages.CopyEGLDDWizPageTitle);
		setDescription(NewWizardMessages.CopyEGLDDWizPageDescription);
		fFileStatus = new StatusInfo();
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGLDDWIZ_COPYEGLDD);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		createFileNameControl(composite);
		createCopyElementSelectionControl(composite);
		
		setControl(composite);
		validatePage();
		Dialog.applyDialogFont(parent);
	}
	
	private CopyEGLDDConfiguration getConfiguration(){
		return (CopyEGLDDConfiguration)((CopyEGLDDWizard)getWizard()).getConfiguration(getName());
	}
	
	private void createCopyElementSelectionControl(Composite composite) {
		Tree tree = new Tree(composite, SWT.SINGLE|SWT.CHECK|SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = nColumns;
		tree.setLayoutData(gd);
		fTreeViewer = new ContainerCheckedTreeViewer(tree);
		
		fTreeContentProvider = new EGLDeploymentTreeContentProvider();
		fTreeLabelProvider = new EGLDeploymentTreeLabelProvider(); 
		fTreeViewer.setContentProvider(fTreeContentProvider);
		fTreeViewer.setLabelProvider(fTreeLabelProvider);
		fTreeViewer.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				if(event.getChecked()){
					Object element = event.getElement();
					if(element instanceof Bindings || element instanceof Services){
						Object[] children = fTreeContentProvider.getChildren(element);
						for (int i=0; i<children.length; i++)
							queryOverride(children[i], fTreeLabelProvider.getText(children[i]));						
					}
					else
						queryOverride(element, fTreeLabelProvider.getText(element));
				}
				
				getConfiguration().updateSelectedElements(fTreeViewer.getCheckedElements());
			}

			private void queryOverride(Object element, String currLabel) {
				if(getConfiguration().getSameNameNodeInCurrentEGLDD(element, currLabel) != null){
					 //ask for user if they want to override
					String msg = NewWizardMessages.bind(NewWizardMessages.QueryOverrideNodeMsg, currLabel);
					if(!MessageDialog.openQuestion(getShell(), NewWizardMessages.QueryOverrideNodeTitle, msg))
						fTreeViewer.setChecked(element, false); 	//uncheck the widget
				}
			}
			
		});
	}

	protected void createFileNameControl(Composite parent) {		
		IStringBrowseButtonFieldAdapter adapter = new IStringBrowseButtonFieldAdapter(){

			public void dialogFieldChanged(DialogField field) {
				if(field == fEGLDDFileNameField)
					HandleEGLDDFileNameChanged();
				
			}

			public void changeControlPressed(DialogField field) {
				if(field == fEGLDDFileNameField)
					HandleEGLDDFileNameBrowsePressed();
			}
			
		};
		fEGLDDFileNameField = createStringBrowseButtonDialogField(parent, adapter, NewWizardMessages.BrowseEGLDDFileLabel, "", nColumns-1); //$NON-NLS-1$
	}

	protected void HandleEGLDDFileNameBrowsePressed() {
		ElementTreeSelectionDialog dialog = FileBrowseDialog.openBrowseFileDialog(getShell(), 
				getConfiguration().getCurrentProject(), null, true, true, null, 
				EGLDDRootHelper.EXTENSION_EGLDD,
				NewWizardMessages.CopyEGLDDDialogTitle, 
				NewWizardMessages.CopyEGLDDDialogDescription, 
				NewWizardMessages.CopyEGLDDDialogMsg); 
		if(dialog.open() == IDialogConstants.OK_ID){
			Object obj = dialog.getFirstResult();
			if(obj instanceof IFile){
				IFile file = (IFile)obj;
				fEGLDDFileNameField.setText(file.getFullPath().toString());
			}
		}
	}

	protected void HandleEGLDDFileNameChanged() {		
		if(validatePage())
		{			
			IFile copyfrFile = getConfiguration().getCopyFromEGLDDFile(fEGLDDFileNameField.getText());
			
			if(copyfrFile != null){
				EGLDeploymentRoot deploymentRoot = null;
				try{
					deploymentRoot = EGLDDRootHelper.getEGLDDFileSharedWorkingModel(copyfrFile, false);	
					if(deploymentRoot != null){
						fTreeViewer.setInput(deploymentRoot);
						fTreeViewer.expandAll();
					}
				}
				finally{
					if(deploymentRoot != null)
						EGLDDRootHelper.releaseSharedWorkingModel(copyfrFile, false);
				}
			}
		}
	}	
	
	protected boolean validatePage(){
		fFileStatus.setOK();
		
		String copyFrFilePath = fEGLDDFileNameField.getText();
		if(copyFrFilePath == null || copyFrFilePath.trim().length()==0){
			fFileStatus.setError(NewWizardMessages.CopyEGLDDWizPageValidationSetEGLDD);
		}
		else{
			IFile copyfrFile = getConfiguration().getCopyFromEGLDDFile(copyFrFilePath);
			
			if(!copyfrFile.exists()){
				String errMsg = NewWizardMessages.bind(NewWizardMessages.CopyEGLDDWizPageValidationFileNotExist, copyFrFilePath);
				fFileStatus.setError(errMsg);
			}
			else if(copyfrFile.equals(getConfiguration().getCurrentEGLDDFile()))
				fFileStatus.setError(NewWizardMessages.CopyEGLDDWizPageValidationCopySelf);
		}
		
		updateStatus(new IStatus[]{fFileStatus});
		return !fFileStatus.isError();
	}

}
