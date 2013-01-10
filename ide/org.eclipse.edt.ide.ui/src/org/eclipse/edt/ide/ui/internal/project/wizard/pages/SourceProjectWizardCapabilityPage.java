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
package org.eclipse.edt.ide.ui.internal.project.wizard.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.core.model.EGLConventions;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.PPListElement;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.project.wizards.NewEGLProjectWizard;
import org.eclipse.edt.ide.ui.internal.util.TabFolderLayout;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.internal.wizards.buildpaths.EGLPathOrderingWorkbookPage;
import org.eclipse.edt.ide.ui.internal.wizards.buildpaths.PPListLabelProvider;
import org.eclipse.edt.ide.ui.internal.wizards.buildpaths.ProjectsWorkbookPage;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.edt.ide.ui.project.templates.ProjectTemplateWizard;
import org.eclipse.edt.ide.ui.wizards.EGLProjectUtility;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class SourceProjectWizardCapabilityPage extends ContainerWizardPage {

	private boolean fCanRemoveContent;
	private IPath fCurrProjectLocation;
	protected IProject fCurrProject;
	private IPath fOutputLocationPath;
	
	private CheckedListDialogField fClassPathList;
	private StringButtonDialogField fBuildPathDialogField;
	private ProjectsWorkbookPage fProjectsPage;
	
	private StatusInfo fClassPathStatus;
	private StatusInfo fBuildPathStatus;
	private List<String> fSelectedImportProjectList;

	public void  setfSelectedImportProjectList(List<String> fSelectedImportProjectList) {
		this.fSelectedImportProjectList = fSelectedImportProjectList;
		if(fProjectsPage != null){
			fProjectsPage.updateSelectedImportProjectList(fSelectedImportProjectList);
			fProjectsPage.setfSelectedImportProjectList(fSelectedImportProjectList);
		}
	}

	public SourceProjectWizardCapabilityPage(String pageName) {
		super(pageName);
		
		fCanRemoveContent = false;
		fCurrProject = null;
		fCurrProjectLocation = null;
		
		setTitle(NewWizardMessages.CapabilityConfigurationPageTitle);
		setDescription(NewWizardMessages.CapabilityConfigurationPageDescription);
		
		fClassPathStatus = new StatusInfo();
		fBuildPathStatus = new StatusInfo();
	}
	
	//Inner classes
	
	private class BuildPathAdapter implements IStringButtonAdapter, IDialogFieldListener {

		// -------- IStringButtonAdapter --------
		public void changeControlPressed(DialogField field) {
		}
	
		// ---------- IDialogFieldListener --------
		public void dialogFieldChanged(DialogField field) {
			handleBuildPathDialogFieldChanged(field);
		}
	}
	
	private ProjectConfiguration getConfiguration() {
		ProjectTemplateWizard wizard = (ProjectTemplateWizard) getWizard();
		return ((NewEGLProjectWizard)wizard.getParentWizard()).getModel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		//Create container composite
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
	
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
	
		composite.setLayout(layout);
		
		createCapabilityControls(composite);
		
		setControl(composite);
		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_PROJECT_REQUIRED_PROJECTS);
		
		validatePage();		
	}
	
	public void setVisible(boolean visible) {
		if (visible) {
			changeToNewProject();
		} else {
			removeProject();
		}
		super.setVisible(visible);
	}
	
	//Control creation methods
	private void createCapabilityControls(Composite composite){
		BuildPathAdapter adapter= new BuildPathAdapter();	
		
		String[] buttonLabels= new String[] {
			/* 0 */ NewWizardMessages.BuildPathsBlockClasspathUpButton,
			/* 1 */ NewWizardMessages.BuildPathsBlockClasspathDownButton,
			/* 2 */ null,
			/* 3 */ NewWizardMessages.BuildPathsBlockClasspathCheckallButton,
			/* 4 */ NewWizardMessages.BuildPathsBlockClasspathUncheckallButton

		};

		fClassPathList= new CheckedListDialogField(null, buttonLabels, new PPListLabelProvider());
		fClassPathList.setDialogFieldListener(adapter);
		fClassPathList.setLabelText(NewWizardMessages.BuildPathsBlockClasspathLabel);
		fClassPathList.setUpButtonIndex(0);
		fClassPathList.setDownButtonIndex(1);
		fClassPathList.setCheckAllButtonIndex(3);
		fClassPathList.setUncheckAllButtonIndex(4);		

		fBuildPathDialogField= new StringButtonDialogField(adapter);
		fBuildPathDialogField.setButtonLabel(NewWizardMessages.BuildPathsBlockBuildpathButton);
		fBuildPathDialogField.setDialogFieldListener(adapter);
		fBuildPathDialogField.setLabelText(NewWizardMessages.BuildPathsBlockBuildpathLabel);

		Composite tabComposite= new Composite(composite, SWT.NONE);	

		GridLayout tabLayout= new GridLayout();
		tabLayout.marginWidth= 0;
		tabLayout.numColumns= 1;		
		tabComposite.setLayout(tabLayout);
		tabComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabFolder folder= new TabFolder(tabComposite, SWT.NONE);
		folder.setLayout(new TabFolderLayout());	
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem item;

		fProjectsPage= new ProjectsWorkbookPage(fClassPathList);
		IWorkbench workbench= EDTUIPlugin.getDefault().getWorkbench();
		Image projectImage= workbench.getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT);

		item= new TabItem(folder, SWT.NONE);
		item.setText(NewWizardMessages.BuildPathsBlockTabProjects);
		item.setImage(projectImage);
		item.setData(fProjectsPage);
		item.setControl(fProjectsPage.getControl(folder));
		
		// a non shared image
		Image cpoImage= PluginImages.DESC_OBJS_EGLBUILDPATH_ORDER.createImage();

		EGLPathOrderingWorkbookPage ordpage= new EGLPathOrderingWorkbookPage(fClassPathList);		
		item= new TabItem(folder, SWT.NONE);
		item.setText(NewWizardMessages.BuildPathsBlockTabOrder);
		item.setImage(cpoImage);
		item.setData(ordpage);
		item.setControl(ordpage.getControl(folder));			
		
		if (fCurrProject != null) {
			fProjectsPage.init(EGLCore.create(fCurrProject));
		}		
	}
	
	private void handleBuildPathDialogFieldChanged(DialogField field) {
		//Update the configuration
		getConfiguration().setRequiredProjects(fClassPathList.getElements());
		
		//Validate page
		validatePage();		
	}
	
	//Utility methods to handle pre-creation and cancellation
	private void changeToNewProject() {
		IProject newProjectHandle= fWorkspaceRoot.getProject(getConfiguration().getProjectName());
		
		IPath newProjectLocation;
		if(getConfiguration().isUseDefaults()) {
			newProjectLocation= new Path(getConfiguration().getInitialProjectLocation());
			fCanRemoveContent= !newProjectLocation.append(getConfiguration().getProjectName()).toFile().exists();
		} else {
			newProjectLocation= new Path(getConfiguration().getCustomProjectLocation());
			fCanRemoveContent= !newProjectLocation.toFile().exists();
		}
			
		final boolean initialize= !(newProjectHandle.equals(fCurrProject) && newProjectLocation.equals(fCurrProjectLocation));
		final boolean noProgressMonitor = !initialize && fCanRemoveContent;
		final IProject preCreationProject = newProjectHandle;
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				if (monitor == null || noProgressMonitor ){
					monitor = new NullProgressMonitor();
				}
				try {
					monitor.beginTask(NewWizardMessages.NewProjectCreationWizardPageEarlyCreationOperationDesc, 2);
					
//					EGLProjectCreationOperation createOp = new EGLProjectCreationOperation(getConfiguration());
//					createOp.run(monitor);
					
					if (initialize) {
						IEGLPathEntry[] entries = null;
						IPath outputLocation = null;
						
						init(EGLCore.create(preCreationProject), outputLocation, entries, false);
					}
					monitor.worked(1);
				} finally {
					monitor.done();
				}
			}
		};

		try {
			getContainer().run(false, true, op);
			
			fCurrProject = newProjectHandle;
			fCurrProjectLocation = newProjectLocation;
		} catch (InvocationTargetException e) {
			String message= NewWizardMessages.NewProjectCreationWizardPageEarlyCreationOperationErrorDesc;
			EDTUIPlugin.logErrorMessage( message );
		} catch  (InterruptedException e) {
			// cancel pressed
		}
	}
	
	private void removeProject() {
		if (fCurrProject == null || !fCurrProject.exists()) {
			return;
		}
	
		IRunnableWithProgress op= new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				final boolean noProgressMonitor= Platform.getLocation().equals(fCurrProjectLocation);
				if (monitor == null || noProgressMonitor) {
					monitor= new NullProgressMonitor();
				}
				monitor.beginTask(NewWizardMessages.NewProjectCreationWizardPageRemoveprojectDesc, 3);

				try {
					fCurrProject.delete(fCanRemoveContent, false, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
					fCurrProject= null;
					fCanRemoveContent= false;
				}
			}
		};

		try {
			op.run(new NullProgressMonitor());
		} catch (InvocationTargetException e) {
			String message= NewWizardMessages.NewProjectCreationWizardPageOp_error_removeMessage;
			EDTUIPlugin.logErrorMessage( message );
		} catch  (InterruptedException e) {
			// cancel pressed
		}
	}
	
	// Initialize the Capability Page fields as necessary
	private void init(IEGLProject eproject, IPath defaultOutputLocation, IEGLPathEntry[] defaultEntries, boolean defaultsOverrideExistingEGLPath) {
		if (!defaultsOverrideExistingEGLPath && eproject.exists() && eproject.getProject().getFile(".classpath").exists()) { //$NON-NLS-1$
			defaultOutputLocation= null;
			defaultEntries= null;
		}	
		
		IEGLProject fCurrEProject= eproject;
		IPath outputLocation = defaultOutputLocation;
		IEGLPathEntry[] eglPathEntries = defaultEntries;
		
		boolean projectExists= false;
		
		List<PPListElement> newEGLPath= null;
		
		try {
			IProject project= fCurrEProject.getProject();
			projectExists= (project.exists() && project.getFile(".eglPath").exists()); //$NON-NLS-1$
			if  (projectExists) {
				if (outputLocation == null) {
					outputLocation=  fCurrEProject.getOutputLocation();
				}
				if (eglPathEntries == null) {
					eglPathEntries=  fCurrEProject.getRawEGLPath();
				}
			}
			if (outputLocation == null) {
				outputLocation= EGLProjectUtility.getDefaultEGLOutputPath(eproject);
			}			

			if (eglPathEntries != null) {
				newEGLPath= EGLProjectUtility.getExistingEntries(eglPathEntries, eproject);
			}
		} catch (CoreException e) {
			EDTUIPlugin.log( e );
		}
		if (newEGLPath == null) {
			newEGLPath= EGLProjectUtility.getDefaultClassPath(eproject);
		}
		
		List exportedEntries = new ArrayList();
		for (int i= 0; i < newEGLPath.size(); i++) {
			PPListElement currEGL= (PPListElement) newEGLPath.get(i);
			if (currEGL.isExported() || currEGL.getEntryKind() == IEGLPathEntry.CPE_SOURCE) {
				exportedEntries.add(currEGL);
			}
		}
		
		// inits the dialog field
		fBuildPathDialogField.setText(outputLocation.makeRelative().toString());
		fBuildPathDialogField.enableButton(projectExists);

		for (int i= 0; i < exportedEntries.size(); i++) {
			PPListElement currEGL= (PPListElement) exportedEntries.get(i);
			if (currEGL.isExported() || currEGL.getEntryKind() == IEGLPathEntry.CPE_SOURCE) {
				fClassPathList.setGrayedWithoutUpdate(currEGL, true);
			}
		}
		
		fClassPathList.setElements(newEGLPath);
		fClassPathList.setCheckedElements(exportedEntries);

		
		fClassPathList.refresh();

		if (fProjectsPage != null) {
			fProjectsPage.setfSelectedImportProjectList(fSelectedImportProjectList);
			fProjectsPage.init(fCurrEProject);
		}
		
		fOutputLocationPath = outputLocation;

		validatePage();
	}
	
	//Do validation
	private void validatePage() {
		
		//Validate the EGL Path
		fClassPathStatus.setOK();
		
		List elements= getConfiguration().getRequiredProjects();
	
		PPListElement entryMissing= null;
		int nEntriesMissing= 0;
		IEGLPathEntry[] entries= new IEGLPathEntry[elements.size()];

		for (int i= elements.size()-1 ; i >= 0 ; i--) {
			PPListElement currElement= (PPListElement)elements.get(i);
			boolean isChecked= fClassPathList.isChecked(currElement);
			if (currElement.getEntryKind() == IEGLPathEntry.CPE_SOURCE) {
				if (!isChecked) {
					fClassPathList.setCheckedWithoutUpdate(currElement, true);
				}
			} else {
				currElement.setExported(isChecked);
			}

			entries[i]= currElement.getEGLPathEntry();
			if (currElement.isMissing()) {
				nEntriesMissing++;
				if (entryMissing == null) {
					entryMissing= currElement;
				}
			}
		}
				
		if (nEntriesMissing > 0) {
			if (nEntriesMissing == 1) {
				fClassPathStatus.setWarning(NewWizardMessages.bind(NewWizardMessages.BuildPathsBlockWarningEntryMissing, entryMissing.getPath().toString()));
			} else {
				fClassPathStatus.setWarning(NewWizardMessages.bind(NewWizardMessages.BuildPathsBlockWarningEntriesMissing, String.valueOf(nEntriesMissing)));
			}
		}
		
		//Validate the Build Path
		fBuildPathStatus.setOK();
		
		elements= getConfiguration().getRequiredProjects();
		entries= new IEGLPathEntry[elements.size()];
	
		for (int i= elements.size()-1 ; i >= 0 ; i--) {
			PPListElement currElement= (PPListElement)elements.get(i);
			entries[i]= currElement.getEGLPathEntry();
		}
		
		if(fCurrProject != null && entries.length > 0 && fCurrProjectLocation != null){
			IEGLModelStatus status= EGLConventions.validateEGLPath(EGLCore.create(fCurrProject), entries, fOutputLocationPath);
			if (!status.isOK()) {
				fBuildPathStatus.setError(status.getMessage());
			}
		}
		
		updateStatus(new IStatus[] { fClassPathStatus, fBuildPathStatus });
	}

}
