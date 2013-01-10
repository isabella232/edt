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
package org.eclipse.edt.ide.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class EGLSourceFolderConfiguration extends EGLContainerConfiguration {
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			setDefaultAttributes();
			return;
		}	
	
		Object selectedElement= selection.getFirstElement();
	
		String projPath= null;
	
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
			if(eproject != null && eproject.exists()) {
				projPath = eproject.getProject().getFullPath().makeRelative().toOSString();
			}
		}
	
		if (projPath != null) {
			setProjectName(projPath);
			setSourceFolderName(""); //$NON-NLS-1$
		} else {
			setDefaultAttributes();
		}
	}
	
	private void setDefaultAttributes() {
		String projPath= ""; //$NON-NLS-1$
	
		try {
			// find the first java project
			IProject[] projects= ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (int i= 0; i < projects.length; i++) {
				IProject proj= projects[i];
				if (proj.hasNature(EGLCore.NATURE_ID)) {
					projPath= proj.getFullPath().makeRelative().toString();
					break;
				}
			}					
		} catch (CoreException e) {
			// ignore here
		}
		setProjectName(projPath);
		setSourceFolderName(""); //$NON-NLS-1$
	}
}
