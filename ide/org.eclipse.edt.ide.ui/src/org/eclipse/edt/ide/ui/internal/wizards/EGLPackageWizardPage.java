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
package org.eclipse.edt.ide.ui.internal.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.packageexplorer.EGLElementContentProvider;
import org.eclipse.edt.ide.ui.internal.packageexplorer.EGLElementLabelProvider;
import org.eclipse.edt.ide.ui.internal.packageexplorer.EGLElementSorter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLWizardUtilities;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

public class EGLPackageWizardPage extends EGLContainerWizardPage {
	
	private int nColumns = 4;

	private IPackageFragmentRoot fCurrRoot;	
	protected StringButtonDialogField fContainerDialogField;
	protected Button fCheckBoxUpdateEGLPath;
	protected StatusInfo fContainerStatus;
	
	private StringDialogField fPackageDialogField;
	protected StatusInfo fPackageStatus;

	private PackageFieldAdapter adapter = new PackageFieldAdapter();

	/**
	 * @param pageName
	 */
	public EGLPackageWizardPage(String pageName) {
		super(pageName);
		
		setTitle(NewWizardMessages.NewPackageWizardPageTitle);
		setDescription(NewWizardMessages.NewPackageWizardPageDescription);
		
		fContainerStatus= new StatusInfo();
		fPackageStatus= new StatusInfo();
		fCurrRoot = null;
	}

	private class PackageFieldAdapter implements IStringButtonAdapter, IDialogFieldListener {

		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			//Use current project as initial selection
			String projectName = getPackageConfiguration().getProjectName();
			if (projectName != null && projectName.length() > 0)
				handleContainerBrowseButtonSelected(EGLCore.create(fWorkspaceRoot.getProject(projectName)));
			else
				handleContainerBrowseButtonSelected(null);
		}

		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			if(field==fPackageDialogField){
				handlePackageDialogFieldChanged(getPackageConfiguration());
//				fPackageStatus = packageChanged();
//				handleFieldChanged(PACKAGE);
			}
			else if(field==fContainerDialogField){
				handleContainerDialogFieldChanged();
			}
		}
	}
	
	protected interface IStringBrowseButtonFieldAdapter extends IDialogFieldListener, IStringButtonAdapter{		
	}

	private EGLPackageConfiguration getConfiguration() {
		return ((EGLPackageWizard) getWizard()).getConfiguration();
	}
	
	protected EGLPackageConfiguration getPackageConfiguration() {
		return getConfiguration();
	}
	
	//reload the value of the controls from the configuration
	public void updateControlValues()
	{
	    if(fContainerDialogField != null &&
	    		fContainerDialogField.getText() != null &&
	    		!fContainerDialogField.getText().equals( getPackageConfiguration().getContainerName() ))
	        fContainerDialogField.setText(getPackageConfiguration().getContainerName());
	    if(fPackageDialogField != null &&
	    		fPackageDialogField.getText() != null &&
	    		!fPackageDialogField.getText().equals( getPackageConfiguration().getFPackage() ))
	        fPackageDialogField.setText(getPackageConfiguration().getFPackage());	    
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_PACKAGE_DEFINITION);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		Label label= new Label(composite, SWT.WRAP);
		label.setText(NewWizardMessages.NewPackageWizardPageInfo);
		GridData gd= new GridData();
		gd.widthHint= convertWidthInCharsToPixels(80);
		gd.horizontalSpan= nColumns;
		label.setLayoutData(gd);
		
		createContainerControls(composite, nColumns);
		createPackageControls(composite);
		
		setControl(composite);
		
		internalValidatePage();	
		Dialog.applyDialogFont(parent);
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && fPackageDialogField!=null) {
			fPackageDialogField.setFocus();
		}
	}
	
	protected void createContainerControls(Composite parent, int nColumns) {
		createContainerControls(parent, nColumns, NewWizardMessages.NewContainerWizardPageContainerLabel);
	}
	
	protected void createContainerControls(Composite parent, int nColumns, String textLabel) {
		fContainerDialogField = new StringButtonDialogField(adapter);
		fContainerDialogField.setLabelText(textLabel);
		fContainerDialogField.setButtonLabel(NewWizardMessages.NewContainerWizardPageContainerButton);

		String str = getPackageConfiguration().getContainerName();
		fContainerDialogField.setText(str);
		updatePFragmentRoot(str);
		
		fContainerDialogField.setDialogFieldListener(adapter);
		fContainerDialogField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);

		LayoutUtil.setWidthHint(fContainerDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fContainerDialogField.getTextControl(null));
	}
	
	protected void createUpdateEGLPathControls(Composite parent)
	{
	    GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = nColumns-1;
	    
	    fCheckBoxUpdateEGLPath = new Button(parent, SWT.CHECK);
	    fCheckBoxUpdateEGLPath.setLayoutData(gd);
	    fCheckBoxUpdateEGLPath.setText(NewWizardMessages.NewContainerWizardPageUpdateEGLPath);
	    
	    //means child class called this method to create this control
	    getPackageConfiguration().setNeed2UpdateEGLPath(true);
	    
	    //create it invisible, only become visible if the container field is a different project than the initial project
	    fCheckBoxUpdateEGLPath.setVisible(false);
	    fCheckBoxUpdateEGLPath.addSelectionListener(new SelectionListener(){
	        private void updateConfiguration(SelectionEvent e)
	        {
                Button btn = (Button)(e.getSource());
                getPackageConfiguration().setUpdateEGLPath(btn.getSelection());	            
	        }
	        
            public void widgetSelected(SelectionEvent e) {                
            }

            public void widgetDefaultSelected(SelectionEvent e) {                
            }
	        
	    });	    
	}
	
	private void createPackageControls(Composite parent) {
		fPackageDialogField = new StringDialogField();
		fPackageDialogField.setDialogFieldListener(adapter);
		fPackageDialogField.setLabelText(NewWizardMessages.NewPackageWizardPagePackageLabel);
		
		fPackageDialogField.setText(getPackageConfiguration().getFPackage());
		
		fPackageDialogField.doFillIntoGrid(parent, nColumns - 2);
		DialogField.createEmptySpace(parent);
		
		LayoutUtil.setWidthHint(fPackageDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fPackageDialogField.getTextControl(null));	
	}
	
	private void handleContainerBrowseButtonSelected(IEGLElement initElement) {
		//Choose container
		ElementTreeSelectionDialog dialog = openContainerDialog(initElement);
		
		if(dialog.open()==ElementTreeSelectionDialog.OK) {
			
			IEGLProject projectSelection = null;
			IPackageFragmentRoot sourceFolderSelection = null;
			Object element = dialog.getFirstResult();
			
			if(element instanceof IEGLProject) {
				projectSelection = (IEGLProject)element;
			} else if(element instanceof IPackageFragmentRoot) {
				sourceFolderSelection = (IPackageFragmentRoot)element;
			}
			
			//Update Configuration
			if(projectSelection != null) {
				getPackageConfiguration().setProjectName(projectSelection.getProject().getName());
				getPackageConfiguration().setSourceFolderName(""); //$NON-NLS-1$
			}
			if(sourceFolderSelection !=null) {
				getPackageConfiguration().setProjectName(sourceFolderSelection.getEGLProject().getElementName());
				getPackageConfiguration().setSourceFolderName(sourceFolderSelection.getElementName());
			}
			
			//Update Container name
			fContainerDialogField.setText(getPackageConfiguration().getContainerName());
			
			//Validate
			internalValidatePage();
		}
	}
	
	protected void handleContainerDialogFieldChanged() {
		//Update Configuration
	    String newContainerValue = fContainerDialogField.getText();
		getPackageConfiguration().setContainerName(newContainerValue);		
		
		//if the update EGL path checkbutton has been created (by the child class)
		if(fCheckBoxUpdateEGLPath != null)		
		{
		    //start with invisible
            fCheckBoxUpdateEGLPath.setVisible(false);
		    
		    //now check to see if it's a different project than the original(initial) project
		    //get the new project name
            IResource newfolder = fWorkspaceRoot.findMember(new Path(newContainerValue));
		    if(newfolder != null)
		    {
		        String newProjName = newfolder.getProject().getName();
		        String originalProjName = getPackageConfiguration().getInitialProjectName();
		        if(!originalProjName.equals(newProjName))
		        {
		            //get the current project's EGL Path
		            IProject currProject = fWorkspaceRoot.getProject(newProjName);
		            IEGLProject currEGLProj = EGLCore.create(currProject);
		            	            
		            IProject initialProj = fWorkspaceRoot.getProject(originalProjName);
		            IEGLPathEntry newEntry = EGLCore.newProjectEntry(initialProj.getFullPath());	    
		            //if it's not already there, and by adding it won't cause circular link, we'll add to the egl path
		            if(!currEGLProj.isOnEGLPath(initialProj) && !currEGLProj.hasEGLPathCycle(new IEGLPathEntry[]{newEntry})) 
		            {
			            fCheckBoxUpdateEGLPath.setVisible(true);
			    	    fCheckBoxUpdateEGLPath.setSelection(getPackageConfiguration().isUpdateEGLPath());
		            }
		        }
		    }
		}
		
		fCurrRoot = null;
		String str = getPackageConfiguration().getContainerName();
		updatePFragmentRoot(str);
	}
	
	/**
     * @param str
     */
    protected void updatePFragmentRoot(String str) {
        if(str.length()>0)
		{
		    IPath path = new Path(str);
		    IResource res = fWorkspaceRoot.findMember(path);
		    if(res!=null)
		    {
		        int resType = res.getType();
		        if(resType == IResource.PROJECT || resType == IResource.FOLDER)
		        {
		            IProject proj = res.getProject();
		            if(proj.isOpen())
		            {
		                IEGLProject eglProj = EGLCore.create(proj);
		                fCurrRoot = eglProj.getPackageFragmentRoot(res);
		            }
		        }
		         
		    }
		}
		
		//Validate
		internalValidatePage();
    }

    private void handlePackageDialogFieldChanged(EGLPackageConfiguration config) {
		//Update Configuration
        config.setFPackage(fPackageDialogField.getText());
		
		//Validate
		internalValidatePage();
	}
	
	private ElementTreeSelectionDialog openContainerDialog(IEGLElement initElement) {
		Class[] acceptedClasses= new Class[] { IPackageFragmentRoot.class, IEGLProject.class };
		
		TypedElementSelectionValidator validator= new TypedElementSelectionValidator(acceptedClasses, false) {
			public boolean isSelectedValid(Object element) {
				try {
					if (element instanceof IEGLProject) {
						IEGLProject eproject= (IEGLProject)element;
						IPath path= eproject.getProject().getFullPath();
						return (eproject.findPackageFragmentRoot(path) != null);
					} else if (element instanceof IPackageFragmentRoot) {
						return (((IPackageFragmentRoot)element).getKind() == IPackageFragmentRoot.K_SOURCE);
					}
					return true;
				} catch (EGLModelException e) {
					EGLLogger.log(this, e);
				}
				return false;
			}
		};
		
		acceptedClasses= new Class[] { IEGLModel.class, IPackageFragmentRoot.class, IEGLProject.class };
		ViewerFilter filter= new TypedViewerFilter(acceptedClasses, null) {
			public boolean select(Viewer viewer, Object parent, Object element) {
				if (element instanceof IPackageFragmentRoot) {
					try {
						return (((IPackageFragmentRoot)element).getKind() == IPackageFragmentRoot.K_SOURCE);
					} catch (EGLModelException e) {
						EGLLogger.log(this, e);
						return false;
					}
				}
				return super.select(viewer, parent, element);
			}
		};
		
		ILabelProvider lp= new EGLElementLabelProvider(EGLElementLabelProvider.SHOW_DEFAULT);
		EGLElementContentProvider cp= new EGLElementContentProvider();	
		
		ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(getShell(), lp, cp);
		dialog.setValidator(validator);
		dialog.setSorter(new EGLElementSorter());
		dialog.setTitle(NewWizardMessages.NewContainerWizardPageChooseSourceContainerDialogTitle);
		dialog.setMessage(NewWizardMessages.NewContainerWizardPageChooseSourceContainerDialogDescription);
		dialog.addFilter(filter);
		dialog.setInput(EGLCore.create(fWorkspaceRoot));
		if (initElement != null)
			dialog.setInitialSelection(initElement);
		
		return dialog;
	}
	
	private boolean internalValidatePage(){
		return validatePage();
	}
	
	public IPackageFragmentRoot getPackageFragmentRoot() {
		return fCurrRoot;
	}
	
	public void setPackageFragmentRoot(IPackageFragmentRoot root, boolean canBeModified) {
		fCurrRoot= root;
		String str= (root == null) ? "" : root.getPath().makeRelative().toString(); //$NON-NLS-1$
		fContainerDialogField.setText(str);
		fContainerDialogField.setEnabled(canBeModified);
	}	
	
	protected boolean validatePage(){

		fContainerStatus.setOK();
		fPackageStatus.setOK();
		String projectName = getPackageConfiguration().getProjectName();
		// Trim this so that a source folder with trailing blanks will be found
		String containerName= getPackageConfiguration().getContainerName().trim();
		String sourceFolderName = getPackageConfiguration().getSourceFolderName();
		String packageName= getPackageConfiguration().getFPackage();
		
		//if the update EGL path checkbutton has been created (by the child class)
		boolean ret = true;
		if(fCheckBoxUpdateEGLPath != null)		
		{		//validate for cicular dependency on EGL Path	    
	        String originalProjName = getPackageConfiguration().getInitialProjectName();
	        if(!originalProjName.equals(projectName))
	        {
	            //get the current project's EGL Path
	            IProject currProject = fWorkspaceRoot.getProject(projectName);
	            IEGLProject currEGLProj = EGLCore.create(currProject);
	            	            
	            IProject initialProj = fWorkspaceRoot.getProject(originalProjName);
	            IEGLPathEntry newEntry = EGLCore.newProjectEntry(initialProj.getFullPath());	    
	
		        if(currEGLProj.hasEGLPathCycle(new IEGLPathEntry[]{newEntry}))
		        {
		            //error, cicular dependency
					fPackageStatus.setError(NewWizardMessages.bind(NewWizardMessages.NewContainerWizardPageErrorCircularLink, new String[]{originalProjName, projectName})); //$NON-NLS-1$	
					ret = false;
		        }
	        }
		}
		
		if(ret)
		{
		    ret = EGLWizardUtilities.validatePackage(projectName, containerName, sourceFolderName, packageName,
				fContainerStatus, fPackageStatus, this);
		    if(ret) {
		    	if (!(this instanceof EGLFileWizardPage)) {
		    		setErrorForBlankName( packageName );
		    	}
		    }
		}
		
		updateStatus(new IStatus[] { fContainerStatus, fPackageStatus });
		
		return ret;

	}

	protected StringButtonDialogField createStringBrowseButtonDialogField(Composite parent, IStringBrowseButtonFieldAdapter adapter, String textLabel, String initText, int nCols) {
		return createStringBrowseButtonDialogField(parent, adapter, textLabel, initText, nCols, NewWizardMessages.NewContainerWizardPageContainerButton);			
	}
	
	protected StringButtonDialogField createStringBrowseButtonDialogField(Composite parent, IStringBrowseButtonFieldAdapter adapter, String textLabel, String initText, int nCols, String buttonLabel) {
		StringButtonDialogField stringButtonDialogField = new StringButtonDialogField(adapter);
		stringButtonDialogField.setDialogFieldListener(adapter);
		stringButtonDialogField.setLabelText(textLabel);
		stringButtonDialogField.setButtonLabel(buttonLabel);
		stringButtonDialogField.setText(initText);
		stringButtonDialogField.doFillIntoGrid(parent, nCols);
		DialogField.createEmptySpace(parent);
		
		LayoutUtil.setWidthHint(stringButtonDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(stringButtonDialogField.getTextControl(null));
		return stringButtonDialogField;		

	}
	
	/**
	 * Issue an error for a blank package name
	 * @param packageName
	 */
	protected void setErrorForBlankName( String packageName ) {
		if (packageName.trim().length() == 0) {
    		fPackageStatus.setError(NewWizardMessages.NewPackageWizardPageErrorEnterName);
    	}
	}
}
