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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.model.EGLConventions;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.edt.ide.ui.wizards.EGLSourceFolderConfiguration;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class EGLSourceFolderWizardPage extends EGLContainerWizardPage {
	
	private int nColumns = 4;
	
	private StringButtonDialogField fProjectDialogField;
	private StatusInfo fProjectStatus;
	
	private StringButtonDialogField fSourceFolderDialogField;
	private StatusInfo fRootStatus;
	
	private SourceFolderFieldAdapter adapter = new SourceFolderFieldAdapter();

	/**
	 * @param pageName
	 */
	public EGLSourceFolderWizardPage(String pageName) {
		super(pageName);
		setTitle(NewWizardMessages.NewSourceFolderWizardPageTitle);
		setDescription(NewWizardMessages.NewSourceFolderWizardPageDescription);
		
		fRootStatus= new StatusInfo();
		fProjectStatus= new StatusInfo();
	}
	
	private class SourceFolderFieldAdapter implements IStringButtonAdapter, IDialogFieldListener {

		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			if(field==fProjectDialogField){
				handleProjectBrowseButtonSelected();
			}
			else if(field==fSourceFolderDialogField){
				handleSourceFolderBrowseButtonSelected();
			}
		}
	
		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			if(field==fProjectDialogField){
//				handleProjectDialogFieldChanged();  can only select through browse button
			}
			else if(field==fSourceFolderDialogField){
				handleSourceFolderDialogFieldChanged();
			}
		}
	}
	
	private EGLSourceFolderConfiguration getConfiguration() {
		return ((EGLSourceFolderWizard) getWizard()).getConfiguration();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_SOURCE_FOLDER_DEFINITION);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		createProjectControl(composite);
		createSourceFolderControl(composite);
		
		setControl(composite);
		
		validatePage();		
		Dialog.applyDialogFont(parent);
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			fSourceFolderDialogField.setFocus();
		}
	}	
	
	private void createProjectControl(Composite parent) {
		fProjectDialogField = new StringButtonDialogField(adapter);
		fProjectDialogField.setDialogFieldListener(adapter);
		fProjectDialogField.setLabelText(NewWizardMessages.NewSourceFolderWizardPageProjectLabel);
		fProjectDialogField.setButtonLabel(NewWizardMessages.NewSourceFolderWizardPageProjectButton);
		
		fProjectDialogField.setText(getConfiguration().getProjectName());
		
		fProjectDialogField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);
		
		LayoutUtil.setWidthHint(fProjectDialogField.getTextControl(null), getMaxFieldWidth());		
	}
	
	private void createSourceFolderControl(Composite parent) {
		fSourceFolderDialogField = new StringButtonDialogField(adapter);
		fSourceFolderDialogField.setDialogFieldListener(adapter);
		fSourceFolderDialogField.setLabelText(NewWizardMessages.NewSourceFolderWizardPageRootLabel);
		fSourceFolderDialogField.setButtonLabel(NewWizardMessages.NewSourceFolderWizardPageRootButton);
		
		fSourceFolderDialogField.setText(getConfiguration().getSourceFolderName());
		
		fSourceFolderDialogField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);
		
		LayoutUtil.setWidthHint(fSourceFolderDialogField.getTextControl(null), getMaxFieldWidth());		
	}
	
	private void handleProjectBrowseButtonSelected(){
		//Choose project
		ElementListSelectionDialog dialog = openProjectDialog();
		
		if(dialog.open()==ElementListSelectionDialog.OK) {
			IEGLProject project = ((IEGLProject)dialog.getFirstResult());
			
			//Update configuration
			getConfiguration().setProjectName(project.getElementName());
			
			//Update Project Name
			fProjectDialogField.setText(getConfiguration().getProjectName());
			
			//Validate
			validatePage();
			
			// update error information
			updateStatus(new IStatus[] { fProjectStatus, fRootStatus });
			
		}		
	}
	
	private void handleSourceFolderBrowseButtonSelected(){
		//Choose source folder
		ElementTreeSelectionDialog dialog = openSourceFolderDialog();
		
		if(dialog.open()==ElementTreeSelectionDialog.OK) {
			
			IFolder folder = ((IFolder)dialog.getFirstResult());
			IPath path = new Path(""); //$NON-NLS-1$
			
			if (folder != null) {
				path= folder.getFullPath().removeFirstSegments(1);
			}
			
			//Update Configuration
			getConfiguration().setSourceFolderName(path.toOSString());
			
			//Update Source Folder name
			fSourceFolderDialogField.setText(getConfiguration().getSourceFolderName());
			
			//Validate
			validatePage();
			
			//update error information
			updateStatus(new IStatus[] { fProjectStatus, fRootStatus });
		}
	}
	
	private void handleSourceFolderDialogFieldChanged(){
		//Update Configuration
		getConfiguration().setSourceFolderName(fSourceFolderDialogField.getText());
		
		//Validate
		validatePage();		
		
		//update those error information
		updateStatus(new IStatus[] { fProjectStatus, fRootStatus });
		
	}
	
	private ElementTreeSelectionDialog openSourceFolderDialog() {
		Class[] acceptedClasses= new Class[] { IFolder.class };
		ISelectionStatusValidator validator= new TypedElementSelectionValidator(acceptedClasses, false);
		ViewerFilter filter= new TypedViewerFilter(acceptedClasses, null);	
		
		ILabelProvider lp= new WorkbenchLabelProvider();
		ITreeContentProvider cp=  new WorkbenchContentProvider();
		
		IProject project = fWorkspaceRoot.getProject(getConfiguration().getProjectName());

		ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(getShell(), lp, cp);
		dialog.setValidator(validator);
		dialog.setTitle(NewWizardMessages.NewSourceFolderWizardPageChooseExistingRootDialogTitle);
		dialog.setMessage(NewWizardMessages.NewSourceFolderWizardPageChooseExistingRootDialogDescription);
		dialog.addFilter(filter);
		dialog.setInput(project);
		
		IResource res= project.findMember(new Path(getConfiguration().getSourceFolderName()));
		if (res != null) {
			dialog.setInitialSelection(res);
		}
		
		return dialog;		
	}
	
	private ElementListSelectionDialog openProjectDialog() {
		ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), labelProvider);
		
		IEGLProject[] projects;
		try{
			projects = EGLCore.create(fWorkspaceRoot).getEGLProjects();
		}
		catch (EGLModelException e) {
			EGLLogger.log(this, e);
			projects = new IEGLProject[0];
		}
		
		dialog.setTitle(NewWizardMessages.NewSourceFolderWizardPageChooseProjectDialogTitle);
		dialog.setMessage(NewWizardMessages.NewSourceFolderWizardPageChooseProjectDialogDescription);
		dialog.setElements(projects);
		
		IProject project = null;
		IResource currProject = fWorkspaceRoot.findMember(getConfiguration().getProjectName());
		
		if(currProject instanceof IProject)
			project = ((IProject)currProject);
		
		if(project!=null)
			dialog.setInitialSelections(new Object[] { project });
		
		return dialog;
	}
	
	private void validatePage() {
		
		IEGLProject fCurrEProject;
		
		IEGLPathEntry[] fEntries = null;
		IPath fOutputLocation = null;
		
		IEGLPathEntry[] fNewEntries;
		IPath fNewOutputLocation;
		
		boolean fIsProjectAsSourceFolder;
		
		//Validate the project field
		String str = fProjectDialogField.getText();
		if (str.length() == 0) {
			fProjectStatus.setError(NewWizardMessages.NewSourceFolderWizardPageErrorEnterProjectName);
			return;
		}
		IPath path= new Path(str);
		if (path.segmentCount() != 1) {
			fProjectStatus.setError(NewWizardMessages.NewSourceFolderWizardPageErrorInvalidProjectPath);
			return;
		}
		IProject project= fWorkspaceRoot.getProject(path.toString());
		if (!project.exists()) {
			fProjectStatus.setError(NewWizardMessages.NewSourceFolderWizardPageErrorProjectNotExists);
			return;
		}
		
		fCurrEProject = null;
		try {
			if (project.hasNature(EGLCore.NATURE_ID)) {
				fCurrEProject= EGLCore.create(project);
				fEntries= fCurrEProject.getRawEGLPath();
				fOutputLocation= fCurrEProject.getOutputLocation();
				fProjectStatus.setOK();
			}
			else {
				fProjectStatus.setError(NewWizardMessages.NewSourceFolderWizardPageErrorNotAnEGLProject);
				return;
			}
		} catch (CoreException e) {
			EGLLogger.log(this, e);
			fCurrEProject= null;
		}	
		
		//Validate the source folder field
		fSourceFolderDialogField.enableButton(fCurrEProject != null);
		fIsProjectAsSourceFolder= false;
		if (fCurrEProject == null) {
			return;
		}
		fRootStatus.setOK();
		
		IPath projPath= fCurrEProject.getProject().getFullPath();
		str= fSourceFolderDialogField.getText();
		if (str.length() == 0) {
			fRootStatus.setError(NewWizardMessages.bind(NewWizardMessages.NewSourceFolderWizardPageErrorEnterRootName, fCurrEProject.getProject().getFullPath().toString())); //$NON-NLS-1$
		} else {
			IStatus validate;
			
			try{
				path= projPath.append(str);
			}
			catch(ArrayIndexOutOfBoundsException e){
				fRootStatus.setError(NewWizardMessages.NewContainerWizardPageErrorBackSlash + " " + str + "."); //$NON-NLS-1$ //$NON-NLS-2$
				updateStatus(new IStatus[] { fProjectStatus, fRootStatus });
				return;
			}
			
			validate= fWorkspaceRoot.getWorkspace().validatePath(path.toString(), IResource.FOLDER);
			if (validate.matches(IStatus.ERROR)) {
				fRootStatus.setError(NewWizardMessages.bind(NewWizardMessages.NewSourceFolderWizardPageErrorInvalidRootName, validate.getMessage()));
			} else {
				IResource res= fWorkspaceRoot.findMember(path);
				if (res != null) {
					if (res.getType() != IResource.FOLDER) {
						fRootStatus.setError(NewWizardMessages.NewSourceFolderWizardPageErrorNotAFolder);
						return;
					}
				}
				ArrayList newEntries= new ArrayList(fEntries.length + 1);
				int projectEntryIndex= -1;
				
				for (int i= 0; i < fEntries.length; i++) {
					IEGLPathEntry curr= fEntries[i];
					if (curr.getEntryKind() == IEGLPathEntry.CPE_SOURCE) {
						if (path.equals(curr.getPath())) {
							fRootStatus.setError(NewWizardMessages.NewSourceFolderWizardPageErrorAlreadyExisting);
							return;
						}
						if (projPath.equals(curr.getPath())) {
							projectEntryIndex= i;
						}	
					}
					newEntries.add(curr);
				}
				
				IEGLPathEntry newEntry= EGLCore.newSourceEntry(path);
				
				Set modified= new HashSet();				
				if (projectEntryIndex != -1) {
					fIsProjectAsSourceFolder= true;
					newEntries.set(projectEntryIndex, newEntry);
				} else {
					newEntries.add(EGLCore.newSourceEntry(path));
				}
					
				fNewEntries= (IEGLPathEntry[]) newEntries.toArray(new IEGLPathEntry[newEntries.size()]);
				fNewOutputLocation= fOutputLocation;

				IEGLModelStatus status= EGLConventions.validateEGLPath(fCurrEProject, fNewEntries, fNewOutputLocation);
				if (!status.isOK()) {
					if (fOutputLocation.equals(projPath)) {
						fNewOutputLocation= projPath.append(EDTCorePreferenceConstants.getPreferenceStore().getString(EDTCorePreferenceConstants.EGL_OUTPUT_FOLDER));
						IStatus status2= EGLConventions.validateEGLPath(fCurrEProject, fNewEntries, fNewOutputLocation);
						if (status2.isOK()) {
							if (fIsProjectAsSourceFolder) {
								fRootStatus.setInfo(NewWizardMessages.bind(NewWizardMessages.NewSourceFolderWizardPageWarningReplaceSFandOL, fNewOutputLocation.makeRelative().toString())); //$NON-NLS-1$
							} else {
								fRootStatus.setInfo(NewWizardMessages.bind(NewWizardMessages.NewSourceFolderWizardPageWarningReplaceOL, fNewOutputLocation.makeRelative().toString())); //$NON-NLS-1$
							}
							return;
						}
					}
					fRootStatus.setError(status.getMessage());
					return;
				} else if (fIsProjectAsSourceFolder) {
					fRootStatus.setInfo(NewWizardMessages.NewSourceFolderWizardPageWarningReplaceSF);
					return;
				}
				if (!modified.isEmpty()) {
					fRootStatus.setInfo(NewWizardMessages.bind(NewWizardMessages.NewSourceFolderWizardPageWarningAddedExclusions, String.valueOf(modified.size()))); //$NON-NLS-1$
					return;
				}
			}
		}
	}	
}
