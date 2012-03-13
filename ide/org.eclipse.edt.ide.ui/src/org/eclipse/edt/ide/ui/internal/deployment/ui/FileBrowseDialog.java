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
package org.eclipse.edt.ide.ui.internal.deployment.ui;

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.StandardEGLElementContentProvider;
import org.eclipse.edt.ide.ui.internal.deployment.Include;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.packageexplorer.EGLElementLabelProvider;
import org.eclipse.edt.ide.ui.internal.packageexplorer.EGLElementSorter;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.internal.wizards.TypedViewerFilter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class FileBrowseDialog {	
	public static ElementTreeSelectionDialog openBrowseFileDialog(Shell shell, 
			final IProject project, IFile initWSDLFile, 
			final boolean isWorkspaceScope,
			boolean showEGLProjectsOnly,
			final String helpId, 
			final String filterFileExtension, 
			String dialogTitle,
			String dialogDescription,
			final String validationMsgInsert) {
		return FileBrowseDialog.openBrowseFileDialog(shell,project,initWSDLFile,isWorkspaceScope,showEGLProjectsOnly,helpId,filterFileExtension,
				dialogTitle,dialogDescription,validationMsgInsert,null,null);
	}
	public static ElementTreeSelectionDialog openBrowseFileDialog(Shell shell, 
			final IProject project, IFile initWSDLFile, 
			final boolean isWorkspaceScope,
			boolean showEGLProjectsOnly,
			final String helpId, 
			final String filterFileExtension, 
			String dialogTitle,
			String dialogDescription,
			final String validationMsgInsert,
			final EList<Include> includes,
			final IFile currentFile) {
		
		IEGLProject eglProject = EGLCore.create(project);
		
		ILabelProvider lp= new EGLElementLabelProvider(EGLElementLabelProvider.SHOW_DEFAULT);
		ITreeContentProvider cp = new StandardEGLElementContentProvider();	
		Object input = eglProject.getEGLModel();
		if(!showEGLProjectsOnly){
			cp = new WorkbenchContentProvider();
			lp = new WorkbenchLabelProvider();
			input = project.getWorkspace().getRoot();
		}
		ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(shell, lp, cp){
			protected Control createDialogArea(Composite parent) {
				Control control = super.createDialogArea(parent);
				if(helpId != null)
					PlatformUI.getWorkbench().getHelpSystem().setHelp(control, helpId);
				return control;
			}
		};
		dialog.setSorter(new EGLElementSorter());
		dialog.setTitle(dialogTitle);
		dialog.setMessage(dialogDescription);
		
		dialog.setInput(input);
		
		//set up validator
		ISelectionStatusValidator validator = new ISelectionStatusValidator(){
			public IStatus validate(Object[] selection) {
				if(selection.length == 1 && selection[0] instanceof IResource)
				{
					IResource resource = (IResource)selection[0];
					if(resource.getType() == IResource.FILE && resource.getFileExtension().equalsIgnoreCase(filterFileExtension))
					{
						if(currentFile!=null && resource.equals(currentFile)){
							return new StatusInfo(IStatus.ERROR, NewWizardMessages.ChooseEGLDDDialog_Error_CurrentDD);				
						}
						if(includes!=null){
							for (Include include : includes) {
								if(include.getLocation().equalsIgnoreCase(resource.getFullPath().toString())){
									return new StatusInfo(IStatus.ERROR, NewWizardMessages.ChooseEGLDDDialog_Error_ImportedDD);				
								}
							}
						}
						return new StatusInfo();
					}
				}
				return new StatusInfo(IStatus.ERROR, NewWizardMessages.bind(NewWizardMessages.WSDLFileSelectionError, validationMsgInsert));				
			}
			
		};				
		dialog.setValidator(validator);
		
		//set up filter, we only want the wsdl files under the EGLSource path
		try
		{
			final String[] refProjNames = eglProject.getRequiredProjectNames();
			
			Class[] acceptedClasses= new Class[] { IEGLModel.class, IPackageFragmentRoot.class, IPackageFragment.class, IEGLProject.class, IResource.class };
			ViewerFilter filter= new TypedViewerFilter(acceptedClasses, null) {
				public boolean select(Viewer viewer, Object parent, Object element) {
					if(isWorkspaceScope){
						if(element instanceof IResource){
							IResource resource = (IResource)element;
							if(resource.getType() == IResource.FILE){
								if(resource.getFileExtension().equalsIgnoreCase(filterFileExtension))
									return true;
								else
									return false;
							}
							return true;
						}						
					}
					else{
						if (element instanceof IPackageFragmentRoot) {
							try {
								return (((IPackageFragmentRoot)element).getKind() == IPackageFragmentRoot.K_SOURCE);
							} catch (EGLModelException e) {
								EGLLogger.log(this, e);
								return false;
							}
						}
						else if(element instanceof IResource){
							// 75518 - Show all folders, not just EGLSource
							// if(parent instanceof IEGLProject)		//filter out the resource under the project
							//	return false;
							IResource resource = (IResource)element;
							if(resource.getType() == IResource.FILE){
								if(resource.getFileExtension().equalsIgnoreCase(filterFileExtension))
									return true;
								else 
									return false;
							}
							return true;
						}
						else if(element instanceof IEGLProject){						
							IEGLProject eglProj = (IEGLProject)element;
							if(eglProj.getProject().equals(project) || isReferencedProject(eglProj)){
								return true;
							}
							return false;
						}
					}
					return super.select(viewer, parent, element);
				}
				
				private boolean isReferencedProject(IEGLProject eglProj){
					String projName = eglProj.getElementName();
					for(int i=0; i<refProjNames.length; i++){
						if(projName.equals(refProjNames[i]))
							return true;
					}
					return false;
				}
			};
			dialog.addFilter(filter);			
		}catch(CoreException e){
			e.printStackTrace();
		}
		
		if (initWSDLFile != null)
			dialog.setInitialSelection(initWSDLFile);
		
		return dialog;
	}
	
	/**
	 * @return a file selection dialog where the only files displayed are on the EGL path of the project, and inside source folders.
	 */
	public static ElementTreeSelectionDialog openBrowseFileOnEGLPathDialog(Shell shell, 
			final IProject project,
			IFile initFile, 
			final String helpId, 
			final String filterFileExtension, 
			String dialogTitle,
			String dialogDescription,
			final String validationMsgInsert,
			final EList<Include> includes,
			final IFile currentFile) {
		
		IEGLProject eglProject = EGLCore.create(project);
		
		ILabelProvider lp= new EGLElementLabelProvider(EGLElementLabelProvider.SHOW_DEFAULT);
		ITreeContentProvider cp = new StandardEGLElementContentProvider();	
		Object input = eglProject.getEGLModel();
		ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(shell, lp, cp){
			protected Control createDialogArea(Composite parent) {
				Control control = super.createDialogArea(parent);
				if(helpId != null)
					PlatformUI.getWorkbench().getHelpSystem().setHelp(control, helpId);
				return control;
			}
		};
		dialog.setSorter(new EGLElementSorter());
		dialog.setTitle(dialogTitle);
		dialog.setMessage(dialogDescription);
		
		dialog.setInput(input);
		
		//set up validator
		ISelectionStatusValidator validator = new ISelectionStatusValidator(){
			public IStatus validate(Object[] selection) {
				if(selection.length == 1 && selection[0] instanceof IResource)
				{
					IResource resource = (IResource)selection[0];
					if(resource.getType() == IResource.FILE && resource.getFileExtension().equalsIgnoreCase(filterFileExtension))
					{
						if(currentFile!=null && resource.equals(currentFile)){
							return new StatusInfo(IStatus.ERROR, NewWizardMessages.ChooseEGLDDDialog_Error_CurrentDD);				
						}
						if(includes!=null){
							for (Include include : includes) {
								if(include.getLocation().equalsIgnoreCase(resource.getFullPath().toString())){
									return new StatusInfo(IStatus.ERROR, NewWizardMessages.ChooseEGLDDDialog_Error_ImportedDD);				
								}
							}
						}
						return new StatusInfo();
					}
				}
				return new StatusInfo(IStatus.ERROR, NewWizardMessages.bind(NewWizardMessages.WSDLFileSelectionError, validationMsgInsert));				
			}
			
		};				
		dialog.setValidator(validator);
		
		//set up filter, we only want the DD files under the EGLSource path
		final HashSet<String> refProjectNames = new HashSet<String>();
		getProjectPath(eglProject, refProjectNames, new HashSet<IEGLProject>());
		
		Class[] acceptedClasses= new Class[] { IEGLModel.class, IPackageFragmentRoot.class, IPackageFragment.class, IEGLProject.class, IResource.class };
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
				else if(element instanceof IResource){
					if(parent instanceof IEGLProject) {		//filter out the resource under the project
						return false;
					}
					IResource resource = (IResource)element;
					if(resource.getType() == IResource.FILE){
						if(resource.getFileExtension().equalsIgnoreCase(filterFileExtension))
							return true;
						else 
							return false;
					}
					return true;
				}
				else if(element instanceof IEGLProject){						
					IEGLProject eglProj = (IEGLProject)element;
					if(eglProj.getProject().equals(project) || refProjectNames.contains(eglProj.getElementName())){
						return true;
					}
					return false;
				}
				return super.select(viewer, parent, element);
			}
		};
		dialog.addFilter(filter);			
		
		if (initFile != null) {
			dialog.setInitialSelection(initFile);
		}
		
		return dialog;
	}
	
	private static void getProjectPath(IEGLProject project, HashSet<String> projectNames, HashSet<IEGLProject> seen) {
		if (seen.contains(project)) {
			return;
		}
		seen.add(project);
		
		try {
			String[] projects = project.getRequiredProjectNames();
			projectNames.addAll(Arrays.asList(projects));
			
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			for (String next : projects) {
				IProject p = root.getProject(next);
				if (p.isAccessible()) {
					IEGLProject eglProj = EGLCore.create(p);
					if (eglProj != null) {
						getProjectPath(eglProj, projectNames, seen);
					}
				}
			}
		}
		catch (EGLModelException e) {
			e.printStackTrace();
		}
	}
}
