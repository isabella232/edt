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
package org.eclipse.edt.ide.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class EGLContainerConfiguration extends EGLElementConfiguration{
	
	/** The Project. */
	private String projectName;
	
	private String initialProjectName;
	
	/** The Source Folder. */
	private String sourceFolderName;
	
	private boolean bNeed2UpdateEGLPath = false;
	private boolean bUpdateEGLPath = true;
	
	public EGLContainerConfiguration(){
		super();
		setDefaultAttributes();
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			setDefaultAttributes();
			return;
		}	
		
		Object selectedElement= selection.getFirstElement();
		
		String projPath = null;
		String sourcePath = null;
		
		if (selectedElement instanceof IResource) {
			IProject proj= ((IResource)selectedElement).getProject();
			if (proj != null) {
				projPath= proj.getFullPath().makeRelative().toString();
			}	
		} else if (selectedElement instanceof IEGLElement) {
			IEGLProject eproject= ((IEGLElement)selectedElement).getEGLProject();
			if (eproject != null) {
				projPath= eproject.getProject().getFullPath().makeRelative().toOSString();
			}
		} else if (selectedElement instanceof IJavaProject) {
			IEGLProject eproject = EGLCore.create(((IJavaProject)selectedElement).getProject());
			if (eproject != null) {
				projPath= eproject.getProject().getFullPath().makeRelative().toOSString();
			}
		}
		
		if (selectedElement instanceof IPackageFragmentRoot) {
			sourcePath = ((IPackageFragmentRoot)selectedElement).getElementName();
		}
		
		if (projPath != null) {
			projectName = projPath;
			initialProjectName = projectName;
			if(sourcePath != null) {
				sourceFolderName = sourcePath;
			}
			else{
				sourceFolderName = ""; //$NON-NLS-1$
			}
		} else {
			setDefaultAttributes();
		}
	}

	/**
	 * @return
	 */
	public String getSourceFolderName() {
		return sourceFolderName;
	}

	/**
	 * @param root
	 */
	public void setSourceFolderName(String root) {
		sourceFolderName = root;
	}

	/**
	 * @return
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param project
	 */
	public void setProjectName(String project) {
		this.projectName = project;
	}
	
	/**
	 * Returns the concatination of hte project and source folder names
	 *
	 */
	public String getContainerName() {
		String str = getProjectName();
		if(str.trim().length() == 0){
			return str;
		}
		return str.concat(System.getProperty("file.separator") + getSourceFolderName()); //$NON-NLS-1$
	}
	
	/**
	 * Parses input.  Expects a container in the format project(separator)sourcefolder
	 *
	 */
	public void setContainerName(String container) {
		String projectToken = ""; //$NON-NLS-1$
		String sourceToken = ""; //$NON-NLS-1$
		
		int separatorIndex = container.indexOf(System.getProperty("file.separator")); //$NON-NLS-1$
		
		if(separatorIndex != -1) {
			projectToken = container.substring(0, separatorIndex);
			sourceToken = container.substring(separatorIndex+1);
		}else{
			projectToken = container;
			sourceToken = "";
		}
		
		setProjectName(projectToken);
		setSourceFolderName(sourceToken);
	}
	
	private void setDefaultAttributes() {
		String projPath= ""; //$NON-NLS-1$
		
		try {
			// find the first java project
			IProject[] projects= ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (int i= 0; i < projects.length; i++) {
				IProject proj= projects[i];
				// check open projects only since closed projects get an exception on hasNature()
				if (proj.isOpen() && proj.hasNature(EGLCore.NATURE_ID)) {
					projPath= proj.getFullPath().makeRelative().toString();
					break;
				}
			}					
		} catch (CoreException e) {
			// ignore here
		}
		projectName = projPath;
		initialProjectName = projectName;
		sourceFolderName = "";		 //$NON-NLS-1$
	}

    /**
     * @return Returns the bUpdateEGLPath.
     */
    public boolean isUpdateEGLPath() {
        return bUpdateEGLPath;
    }
    /**
     * @param updateEGLPath The bUpdateEGLPath to set.
     */
    public void setUpdateEGLPath(boolean updateEGLPath) {
        bUpdateEGLPath = updateEGLPath;
    }
    /**
     * @return Returns the initialProjectName.
     */
    public String getInitialProjectName() {
        return initialProjectName;
    }
    /**
     * @return Returns the bNeed2UpdateEGLPath.
     */
    public boolean isNeed2UpdateEGLPath() {
        return bNeed2UpdateEGLPath;
    }
    /**
     * @param need2UpdateEGLPath The bNeed2UpdateEGLPath to set.
     */
    public void setNeed2UpdateEGLPath(boolean need2UpdateEGLPath) {
        bNeed2UpdateEGLPath = need2UpdateEGLPath;
    }
    /**
     * @param initialProjectName The initialProjectName to set.
     */
    public void setInitialProjectName(String initialProjectName) {
        this.initialProjectName = initialProjectName;
    }
}
