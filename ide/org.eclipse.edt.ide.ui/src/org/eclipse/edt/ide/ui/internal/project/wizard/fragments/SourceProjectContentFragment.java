/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.project.wizard.fragments;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.project.wizard.pages.ProjectWizardMainPage;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;

public class SourceProjectContentFragment extends ProjectContentFragment {
	private final ProjectConfiguration config;
	private StatusInfo locationInfo;
	private String customDirectory = "";  //$NON-NLS-1$
	
	public SourceProjectContentFragment(Composite renderOn, ProjectWizardMainPage parentPage) {
		super(renderOn, parentPage);
		config = parentPage.getModel();
		locationInfo = new StatusInfo();
	}
	
	public void dispose() {
		// TODO Auto-generated method stub
	}
	
	protected void handleLocationBrowseButtonPressed() {
		DirectoryDialog dialog = openLocationDialog();
		
		String selectedDirectory = dialog.open();
		if(selectedDirectory != null) {
			directory.setText(selectedDirectory);
		}
		
		//Validate
		validateProjectLocation();
	}

	protected void hookListeners() {
		this.specifyProjectDirectory.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				config.setUseDefaults(specifyProjectDirectory.getSelection());
				boolean useDefaults = config.isUseDefaults();
				directoryLabel.setEnabled(!useDefaults);
				directory.setEnabled(!useDefaults);
				browseDirectory.setEnabled(!useDefaults);
				if (useDefaults) {
					customDirectory = directory.getText();
					directory.setText(Platform.getLocation().append(config.getProjectName()).toOSString());
				} else {
//					if (customDirectory == "")
//						getParentPage().setPageComplete(false);
					directory.setText(customDirectory);					
				}
			}
		});
		
		this.directory.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				config.setCustomProjectLocation(directory.getText());
				validateProjectLocation();
			}
		});
		
		this.browseDirectory.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleLocationBrowseButtonPressed();
			}
		});
	}
	
	protected void setInitialValues() {
		specifyProjectDirectory.setSelection(true);
		specifyProjectDirectory.setEnabled(false);
		directoryLabel.setEnabled(false);
		directory.setEnabled(false); 
		directory.setText(Platform.getLocation().append(config.getProjectName()).toOSString());
		browseDirectory.setEnabled(false);
	}

	public boolean isValidateProjectLocation() {
		locationInfo.setOK();
		String projectLocation = config.getCustomProjectLocation();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		if (config.getCustomProjectLocation().equals("")) { //$NON-NLS-1$
			locationInfo.setError(NewWizardMessages.EGLSourceProjectWizardPage_projectLocationEmpty);
		} else {
			IPath path = new Path(""); //$NON-NLS-1$
			if (!path.isValidPath(projectLocation)) {
				locationInfo.setError(NewWizardMessages.EGLSourceProjectWizardPage_locationError);
			} else {
				IPath projectPath = new Path(projectLocation);
				if (!config.isUseDefaults()) {
					if (Platform.getLocation().isPrefixOf(projectPath)) {
						locationInfo.setError(NewWizardMessages.EGLSourceProjectWizardPage_defaultLocationError);
					} else {
						IProject handle = ResourcesPlugin.getWorkspace()
							.getRoot().getProject(config.getProjectName());
						IStatus currLocationStatus = workspace
								.validateProjectLocation(handle, projectPath);
						if (!currLocationStatus.isOK()) {
							locationInfo.setError(currLocationStatus.getMessage());
						}
					}
				}
			}
		}
		return locationInfo.isOK();
	}
	

	public void validateProjectLocation() {
		isValidateProjectLocation();
		getParentPage().setPageComplete(locationInfo.isOK());
		getParentPage().setErrorMessage(locationInfo.getMessage());
	}
	
	private DirectoryDialog openLocationDialog() {
		DirectoryDialog dialog = new DirectoryDialog(getParentPage().getShell());
		dialog.setMessage(NewWizardMessages.EGLSourceProjectWizardPage_DirectoryDialogTitle);

		String dirName = config.getCustomProjectLocation();
		if (!dirName.equals("")) { //$NON-NLS-1$
			File path = new File(dirName);
			if (path.exists())
				dialog.setFilterPath(new Path(dirName).toOSString());
		}
		
		return dialog;
	}
}
