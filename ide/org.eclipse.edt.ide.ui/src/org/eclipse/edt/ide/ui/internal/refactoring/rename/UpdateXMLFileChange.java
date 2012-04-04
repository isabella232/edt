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
package org.eclipse.edt.ide.ui.internal.refactoring.rename;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;

public class UpdateXMLFileChange extends Change {

	String newProjectName;
	String oldProjectName;
	String fileName;
	MultiTextEdit multiEdit;
	IFile oldFile;

	public UpdateXMLFileChange(String newProjectName, String oldProjectName, MultiTextEdit multiEdit, IFile oldFile) {
		super();
		this.multiEdit = multiEdit;
		this.oldFile = oldFile;
		this.fileName = oldFile.getName();
		this.newProjectName = newProjectName;
		this.oldProjectName = oldProjectName;
	}

	public Object getModifiedElement() {
		return oldFile;
	}

	public String getName() {
		return fileName;
		//return MessageFormat.format(EGLUINlsStrings.Rename_GenProject_In_BLD_File, new String[] {eglBldFile.getFullPath().toString(), oldProjectName, newProjectName});
	}

	public void initializeValidationData(IProgressMonitor pm) {

	}

	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
		TextFileChange change = new TextFileChange( "", getNewFile() ); //$NON-NLS-1$
		
		change.setEdit( multiEdit );
		change.perform(pm);
		
		return null;
	}
	
	
	private IFile getNewFile() {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(newProjectName);
		if (project.exists()) {
			IResource res = project.findMember(fileName);
			if (res.exists() && ((res.getType() & IResource.FILE) > 0)) {
				return (IFile) res;
			}
		}
		return null;
	}
	

}
