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
package org.eclipse.edt.ide.core.internal.lookup.workingcopy;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.ide.core.internal.lookup.AbstractFileInfoCreator;
import org.eclipse.edt.ide.core.internal.lookup.IDuplicatePartRequestor;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IWorkingCopy;

/**
 * Create a working copy FileInfo that is based on a working copy in the EGL TModel.
 */
public class WorkingCopyFileInfoCreator extends AbstractFileInfoCreator {
	
	private IWorkingCopy workingCopy;

	public WorkingCopyFileInfoCreator(IProject project, String[] packageName, IFile file, IWorkingCopy workingCopy, File fileAST, IDuplicatePartRequestor duplicatePartRequestor) {
		super(WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project), packageName, file, fileAST, duplicatePartRequestor);
		
		this.workingCopy = workingCopy;
	}
	
	public String getContents() throws CoreException, IOException {
		return ((IEGLFile)workingCopy).getBuffer().getContents();
	}
	
	/**
	 * It is possible that the WCC Resource Change job hasn't been run yet.  This means that we can't rely completely on the parents isDuplicatePart method, which 
	 * requires that the resources changes have been processed.
	 */
	protected boolean isDuplicatePart(String caseInsensitivePartName) {
		if(projectInfo.hasPackage(packageName)){
			return super.isDuplicatePart(caseInsensitivePartName);
		}else{
			return false;
		}
	}
}
