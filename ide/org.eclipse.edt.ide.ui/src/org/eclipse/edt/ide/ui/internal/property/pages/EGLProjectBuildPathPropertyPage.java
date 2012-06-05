/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.property.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.model.EGLConventions;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.EGLSystemPathContaierInitializer;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.PPListElement;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
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
import org.eclipse.edt.ide.ui.wizards.EGLProjectUtility;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;
import org.eclipse.edt.ide.ui.wizards.ProjectConfigurationOperation;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.ide.IDE;

public class EGLProjectBuildPathPropertyPage extends PropertyPage {

	private IProject		project;
	private IEGLPathEntry[]	entries = null;
	private IPath			outputLocation = null;
	private IResource 		thisResource = null;
	
	private StatusInfo fEGLPathStatus;
//	private StatusInfo fOutputFolderStatus;	
	private StatusInfo fBuildPathStatus;	
	
	private ProjectConfiguration 		 configuration;
	private ProjectConfigurationOperation operation;
	
	private IPath		fOutputLocationPath;
	protected IProject  fCurrProject;
	
	private CheckedListDialogField 	fClassPathList;
	private StringButtonDialogField fBuildPathDialogField;
	private ProjectsWorkbookPage 	fProjectsPage;	
	//private EGLLibrariesWorkbookPage fLibrariesPage;
	private EGLPathOrderingWorkbookPage fOrderPage;

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		
		fCurrProject = null;
		
		if(getSelectedResource() instanceof IProject)
			project = (IProject)getSelectedResource();
					
		initialize();
		createDescriptionLabel(parent);
		
		setLayout(parent);
		createCapabilityControls(parent);			
			
		initFields(EGLCore.create(project), outputLocation, entries, false);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IUIHelpConstants.BUILD_PATH_CONTEXT);
		Dialog.applyDialogFont(parent);
		return parent;
	}

	//Inner classes
	private class BuildPathAdapter implements IStringButtonAdapter, IDialogFieldListener {
		public void changeControlPressed(DialogField field) {
		}
		public void dialogFieldChanged(DialogField field) {
			handleBuildPathDialogFieldChanged(field);
		}
	}

	/**
	 * Get the resource that has been selected
	 */
	private IResource getSelectedResource() {

		IResource  resource = null;
		IAdaptable adaptable = getElement();

		if (adaptable != null) {
			resource = (IResource) adaptable.getAdapter(IResource.class);
		}

		return resource;
	}

	/**
	 * Initializes this page.
	 */
	private void initialize() {
		fBuildPathStatus= new StatusInfo();
		fEGLPathStatus= new StatusInfo();
//		fOutputFolderStatus= new StatusInfo();		
		
		thisResource = getSelectedResource();
		noDefaultAndApplyButton();
		createProjectConfiguration();
//		setDescription(EGLUIPlugin.getResourceString(UINlsStrings.ProjectBuildSettingsPropertiesPageLabelText));
	}

	/**
	 * @see PreferencePage#performOk
	 */
	public boolean performOk() {

		if (thisResource != null) {
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException {
					operation = new ProjectConfigurationOperation(configuration);
					try{
						operation.run(monitor);
					}
					catch(InterruptedException e) {
						return;
					}
				}
			};
			try {
				new ProgressMonitorDialog(getControl().getShell()).run(true, true, runnable);
			} catch (InterruptedException e) {
			} catch (InvocationTargetException e) {
				handle(e);
			}
		}
		return true;
	}

	/**
	 * @see PreferencePage#doOk
	 */
	protected void handle(InvocationTargetException e) {
		IStatus   error;
		Throwable target = e.getTargetException();
		if (target instanceof CoreException) {
			error = ((CoreException) target).getStatus();
		} else {
			String msg = target.getMessage();
			if (msg == null)
				msg = "Internal error"; //$NON-NLS-1$
			error = new Status(IStatus.ERROR, EDTUIPlugin.PLUGIN_ID, 1, msg, target);
		}
		ErrorDialog.openError(getControl().getShell(), "Problems Occurred", null, error); //$NON-NLS-1$
	}

	private void createProjectConfiguration() {
		//Create a new blank configuration
		configuration = new ProjectConfiguration();
		
		//Use the selected resource to fill the configuration
		if(getSelectedResource() instanceof IProject)
			project = (IProject)getSelectedResource();
		else
			project = getSelectedResource().getProject();
		configuration.setProjectName(project.getName());
		configuration.setConfigureEGLPathOnly( true );
		
		configuration.addJavaPlatform();
		configuration.addJavaScriptPlatform();
		
		//Construct the list from existing entries
		try{
			entries = EGLCore.create(project).getRawEGLPath();
			
			List entriesList = new ArrayList();
			for(int j=0; j<entries.length; j++){
				entriesList.add(entries[j]); 
			}
			configuration.setRequiredProjects(entriesList);
			
		} catch (EGLModelException e){
			entries = null;
			EGLLogger.log(this, e.toString());
		}
	}
	
	private void setLayout(Composite composite) {
		GridLayout layout = new GridLayout();
		GridData   gd     = new GridData(GridData.FILL_BOTH);
		
		layout.numColumns = 1;
		
		composite.setLayout(layout);
		composite.setLayoutData(gd);
	}
	
	//Control creation methods
	private void createCapabilityControls(Composite composite){
		BuildPathAdapter adapter = new BuildPathAdapter();	
		
		String[] buttonLabels = new String[] {
			/* 0 */ NewWizardMessages.BuildPathsBlockClasspathUpButton,
			/* 1 */ NewWizardMessages.BuildPathsBlockClasspathDownButton,
			/* 2 */ null,
			/* 3 */ NewWizardMessages.BuildPathsBlockClasspathCheckallButton,
			/* 4 */ NewWizardMessages.BuildPathsBlockClasspathUncheckallButton
		};

		fClassPathList = new CheckedListDialogField(null, buttonLabels, new PPListLabelProvider());
		fClassPathList.setDialogFieldListener(adapter);
		fClassPathList.setLabelText(NewWizardMessages.BuildPathsBlockClasspathLabel);
		fClassPathList.setUpButtonIndex(0);
		fClassPathList.setDownButtonIndex(1);
		fClassPathList.setCheckAllButtonIndex(3);
		fClassPathList.setUncheckAllButtonIndex(4);		
	
		fBuildPathDialogField = new StringButtonDialogField(adapter);
		fBuildPathDialogField.setButtonLabel(NewWizardMessages.BuildPathsBlockBuildpathButton);
		fBuildPathDialogField.setDialogFieldListener(adapter);
		fBuildPathDialogField.setLabelText(NewWizardMessages.BuildPathsBlockBuildpathLabel);
	
		Composite tabComposite = new Composite(composite, SWT.NONE);	
	
		GridLayout tabLayout = new GridLayout();
		tabLayout.marginWidth = 0;
		tabLayout.numColumns = 1;		
		tabComposite.setLayout(tabLayout);
		tabComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
	
		TabFolder folder = new TabFolder(tabComposite, SWT.NONE);
		folder.setLayout(new TabFolderLayout());	
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
	 
		TabItem item;
	
		fProjectsPage = new ProjectsWorkbookPage(fClassPathList);
		IWorkbench workbench = EDTUIPlugin.getDefault().getWorkbench();
		Image projectImage = workbench.getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
	
		item = new TabItem(folder, SWT.NONE);
		item.setText(NewWizardMessages.BuildPathsBlockTabProjects);
		item.setImage(projectImage);
		item.setData(fProjectsPage);
		item.setControl(fProjectsPage.getControl(folder));
		

		Image cpoImage= PluginImages.DESC_OBJS_EGLBUILDPATH_ORDER.createImage();
//		composite.addDisposeListener(new ImageDisposer(cpoImage));	
		
		fOrderPage = new EGLPathOrderingWorkbookPage(fClassPathList);		
		item= new TabItem(folder, SWT.NONE);
		item.setText(NewWizardMessages.BuildPathsBlockTabOrder);
		item.setImage(cpoImage);
		item.setData(fOrderPage);
		item.setControl(fOrderPage.getControl(folder));	
		
		/*Image libImage= PluginImages.DESC_OBJS_EGLBUILDPATH_ORDER.createImage();
		fLibrariesPage = new EGLLibrariesWorkbookPage(fClassPathList);
		item= new TabItem(folder, SWT.NONE);
		item.setText(NewWizardMessages.BuildPathsBlockTabLibraries);
		item.setImage(libImage);
		item.setData(fLibrariesPage);
		item.setControl(fLibrariesPage.getControl(folder));	*/
			
		if (fCurrProject != null) {
			//fLibrariesPage.init(EGLCore.create(fCurrProject));
			fProjectsPage.init(EGLCore.create(fCurrProject));
			fOrderPage.init(EGLCore.create(fCurrProject));
		}
	}	
  
	private void handleBuildPathDialogFieldChanged(DialogField field) {
		//Update the configuration
		configuration.setRequiredProjects(fClassPathList.getElements());
		
		if(field==fClassPathList){
			updateEGLPathStatus();
		} else if(field==fBuildPathDialogField){
			//Currently do not support changing the output location
		}
	}  
		
	// Initialize the Capability Page fields as necessary
	private void initFields(IEGLProject eproject, IPath defaultOutputLocation, IEGLPathEntry[] defaultEntries, boolean defaultsOverrideExistingEGLPath) {
		if (!defaultsOverrideExistingEGLPath && eproject.exists() && eproject.getProject().getFile(".classpath").exists()) { //$NON-NLS-1$
			defaultOutputLocation = null;
			defaultEntries = null;
		}	
			
		IEGLProject fCurrEProject = eproject;
		IEGLPathEntry[] eglPathEntries = defaultEntries;
		IPath 	  outputLocation = defaultOutputLocation;
			
		boolean projectExists= false;
			
		List newEGLPath= null;
			
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
			EGLLogger.log(this, e);
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
		fClassPathList.setElements(newEGLPath);
		fClassPathList.setCheckedElements(exportedEntries);
		for (int i= 0; i < newEGLPath.size(); i++) {
			PPListElement currEGL= (PPListElement) newEGLPath.get(i);
			if(EGLSystemPathContaierInitializer.isValidEGLSystemPathContainerPath(currEGL.getEGLPathEntry().getPath())){
				fClassPathList.setGrayedWithoutUpdate(currEGL, true);
				break;
			}
		}
		
		if (fProjectsPage != null) {
			fProjectsPage.init(fCurrEProject);
		}
			
		/*if (fLibrariesPage != null) {
			fLibrariesPage.init(fCurrEProject);
		}*/
		if (fOrderPage != null) {
			fOrderPage.init(fCurrEProject);
		}
		
		fOutputLocationPath = outputLocation;
	}	
	
	//Validates the egl path
	public void updateEGLPathStatus() {
		fEGLPathStatus.setOK();
		
		List elements= fClassPathList.getElements();
	
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
			}else if(EGLSystemPathContaierInitializer.isValidEGLSystemPathContainerPath(currElement.getEGLPathEntry().getPath())){
				if (isChecked) {
					fClassPathList.setCheckedWithoutUpdate(currElement, false);
				}
			}else {
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
				fEGLPathStatus.setWarning(NewWizardMessages.bind(NewWizardMessages.BuildPathsBlockWarningEntryMissing, entryMissing.getPath().toString())); //$NON-NLS-1$
			} else {
				fEGLPathStatus.setWarning(NewWizardMessages.bind(NewWizardMessages.BuildPathsBlockWarningEntriesMissing, String.valueOf(nEntriesMissing))); //$NON-NLS-1$
			}
		}
		
		if (nEntriesMissing > 0) {
			if (nEntriesMissing == 1) {
				fEGLPathStatus.setWarning(NewWizardMessages.bind(
						NewWizardMessages.BuildPathsBlockWarningEntryMissing,
						entryMissing.getPath().toString())); //$NON-NLS-1$
				setMessage(NewWizardMessages.bind(
						NewWizardMessages.BuildPathsBlockWarningEntryMissing,
						entryMissing.getPath().toString()), IMessageProvider.WARNING);
				
			} else {
				fEGLPathStatus.setWarning(NewWizardMessages.bind(
						NewWizardMessages.BuildPathsBlockWarningEntriesMissing,
						String.valueOf(nEntriesMissing))); //$NON-NLS-1$
				setMessage(NewWizardMessages.bind(
						NewWizardMessages.BuildPathsBlockWarningEntryMissing,
						String.valueOf(nEntriesMissing)), IMessageProvider.WARNING);
			}
			setErrorMessage(null);
		}
				
/*		if (fCurrJProject.hasClasspathCycle(entries)) {
			fClassPathStatus.setWarning(NewWizardMessages.getString("BuildPathsBlock.warning.CycleInClassPath")); //$NON-NLS-1$
		}
*/		
		updateBuildPathStatus();
		
	}
	
	private void updateBuildPathStatus() {
		List elements= fClassPathList.getElements();
		IEGLPathEntry[] entries= new IEGLPathEntry[elements.size()];
	
		for (int i= elements.size()-1 ; i >= 0 ; i--) {
			PPListElement currElement= (PPListElement)elements.get(i);
			entries[i]= currElement.getEGLPathEntry();
		}
		
		IEGLModelStatus status= EGLConventions.validateEGLPath(EGLCore.create(project), entries, fOutputLocationPath);
		if (!status.isOK()) {
			fBuildPathStatus.setError(status.getMessage());
			return;
		}
		fBuildPathStatus.setOK();		
	}
}
