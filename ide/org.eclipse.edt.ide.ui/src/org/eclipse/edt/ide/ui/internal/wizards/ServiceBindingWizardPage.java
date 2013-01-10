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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.internal.deployment.ui.FileBrowseDialog;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;
import org.eclipse.edt.ide.ui.wizards.BindingBaseConfiguration;
import org.eclipse.edt.ide.ui.wizards.BindingEGLConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLWizardUtilities;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

public class ServiceBindingWizardPage extends EGLBindingWizardPage {
	public static final String WIZPAGENAME_EGLServiceBindingWizardPage = "WIZPAGENAME_EGLServiceBindingWizardPage"; //$NON-NLS-1$
    //	private Button bOverWriteBindingCheckBox;
	private StatusInfo fEGLDDFileStatus;	
	
	public ServiceBindingWizardPage(String pageName) {
		super(pageName);
		setTitle(NewWizardMessages.EGLServiceBindingWizPageTitle);
		setDescription(NewWizardMessages.EGLServiceBindingWizPageDescription);
		nColumns = 5;
		fEGLDDFileStatus = new StatusInfo();
	}

	@Override
	public void createControl(Composite parent) {

		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.MODULE_EXTERNALSERVICE_WS1);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);

		createEGLDDFileControl(composite);
		createBindingControls(composite, getConfiguration(), null, true);
		createCheckBoxOverwriteFileControl(composite, NewWizardMessages.UpdateEGLBindingLabel);
		
		setControl(composite);
		validatePage();
		Dialog.applyDialogFont(parent);
	}
	
	/**
	 * when the page 1st become visible, set the initial egldd file name to be the project name
	 * so user does not HAVE to fill in the egldd file name
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible) ;
		if(visible){
			String eglddfileName = fEGLFileDialogField.getText();
			if(eglddfileName == null || eglddfileName.trim().length()==0)	
				fEGLFileDialogField.setText(CoreUtility.getValidProjectName(getBindingEGLConfiguration().getProjectName()));
		}
	}

	private BindingEGLConfiguration getConfiguration(){
		return (BindingEGLConfiguration)((ServiceBindingWizard)getWizard()).getConfiguration(getName());
	}
	
	protected BindingEGLConfiguration getBindingEGLConfiguration() {
		return (BindingEGLConfiguration)((ServiceBindingWizard)getWizard()).getConfiguration(ServiceBindingWizardPage.WIZPAGENAME_EGLServiceBindingWizardPage);		
	}
	
	protected void createComponentNameControl(Composite parent, String labelName, BindingBaseConfiguration esConfig) {
		createComponentNameNoBrowseControl(parent, labelName, esConfig);
	}
		
	private void createEGLDDFileControl(Composite composite) {
		createContainerControls(composite, nColumns);
		createEGLFileWithBrowseControls(composite, NewWizardMessages.EGLDDLabel);
		createSeparator(composite, nColumns);
	}
	
	protected void handleFileBrowseButtonSelected() {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getBindingEGLConfiguration().getProjectName());
		ElementTreeSelectionDialog dialog = FileBrowseDialog.openBrowseFileDialog(getShell(), project, 
				null, false, true, null, EGLDDRootHelper.EXTENSION_EGLDD, 
				NewWizardMessages.ChooseEGLDDDialogTitle, 
				NewWizardMessages.ChooseEGLDDDialogDescription, NewWizardMessages.ChooseEGLDDDialogMsg);
		if(dialog.open() == IDialogConstants.OK_ID){
			Object obj = dialog.getFirstResult();
			if(obj instanceof IFile){
				IFile eglddFile = (IFile)obj;
				String fullFileName = eglddFile.getName();
				int dot = fullFileName.indexOf('.');				
				fEGLFileDialogField.setText(fullFileName.substring(0, dot));
			}
		}
	}
	

	protected void handleEGLFileDialogFieldChanged() {
		super.handleEGLFileDialogFieldChanged();
		
		IFile eglddFile = CoreUtility.getExistingEGLDDFileHandle(getBindingEGLConfiguration());//getEGLDDFileHandle();		
		if(eglddFile != null){
			EGLDeploymentRoot deploymentRoot = null;			
			try{
				if(eglddFile.exists()){
					deploymentRoot = EGLDDRootHelper.getEGLDDFileSharedWorkingModel(eglddFile, false);
				}
				getBindingEGLConfiguration().setEGLDeploymentDescriptor(eglddFile, deploymentRoot);				
				validatePage();					
			}
			finally{
				if(deploymentRoot != null)
					EGLDDRootHelper.releaseSharedWorkingModel(eglddFile, false);
			}
		}
	}

	protected void validateBindingName(StatusInfo statusinfo) {
		if(!getBindingEGLConfiguration().isOverwrite()){
			//if not override the existing binding, check to see 
			//if this binding name already existed in the user chosen deployment descriptor 
			EGLDeploymentRoot deploymentRoot = getBindingEGLConfiguration().getEGLDeploymentRoot(); 
			if(deploymentRoot != null){
				String currBindingName = getBindingEGLConfiguration().getBindingName();
				Binding eglBinding = EGLDDRootHelper.getBindingByName(deploymentRoot, currBindingName);
				if(eglBinding != null){
					statusinfo.setError(NewWizardMessages.bind(NewWizardMessages.WSDLBindingWizpageValidationQueryOverride, currBindingName, 
							 CoreUtility.getExistingEGLDDFileHandle(getBindingEGLConfiguration()).getName()));
				}
			}
		}
	}
	
	protected boolean validatePage() {
		boolean ret = false;
		fEGLDDFileStatus.setOK();
		if(super.validatePage()){
			String eglddFileName = getBindingEGLConfiguration().getFileName();
			if(eglddFileName == null || eglddFileName.trim().length()==0){
				fEGLDDFileStatus.setError(NewWizardMessages.WSDLBindingWizPageValidationSetEGLDD);
			}else {
				String projectName = getBindingEGLConfiguration().getProjectName();
				String containerName = getBindingEGLConfiguration().getContainerName();
				String fileName= getBindingEGLConfiguration().getFileName();
				String fileExtensionName = getBindingEGLConfiguration().getFileExtension();
			
				//no package for egldd file
				ret = EGLWizardUtilities.validateFile(projectName, containerName, "", fileName, fileExtensionName, fEGLDDFileStatus, this, false); //$NON-NLS-1$
			}
			updateStatus(new IStatus[]{fEGLDDFileStatus});
		}
		
		return ret;
	}
}
