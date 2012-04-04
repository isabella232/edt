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
package org.eclipse.edt.ide.ui.internal.refactoring.changes;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.INewNameQuery;
import org.eclipse.ltk.core.refactoring.Change;

public class CreateCopyOfEGLFileChange extends CreateTextFileChange {

	private IEGLFile fOldCu;
	private INewNameQuery fNameQuery;
	
	public CreateCopyOfEGLFileChange(IPath path, String source, IEGLFile oldCu, INewNameQuery nameQuery) {
		super(path, source, null, "egl"); //$NON-NLS-1$
		fOldCu= oldCu;
		fNameQuery= nameQuery;
		setEncoding(oldCu);
	}
	
	public Change perform(IProgressMonitor pm) throws CoreException {
		final Change result= super.perform(pm);
		return result;
	}
	
	private void setEncoding(IEGLFile cunit) {
		IResource resource= cunit.getResource();
		// no file so the encoding is taken from the target
		if (!(resource instanceof IFile))
			return;
		IFile file= (IFile)resource;
		try {
			String encoding= file.getCharset(false);
			if (encoding != null) {
				setEncoding(encoding, true);
			} else {
				encoding= file.getCharset(true);
				if (encoding != null) {
					setEncoding(encoding, false);
				}
			}
		} catch (CoreException e) {
			// do nothing. Take encoding from target
		}
	}

	/*
	 * @see CreateFileChange#getOldFile()
	 */
	protected IFile getOldFile(IProgressMonitor pm) {
		pm.beginTask("", 10); //$NON-NLS-1$
		String oldSource= super.getSource();
		IPath oldPath= super.getPath();
		String newTypeName= fNameQuery.getNewName();
		try {
			String newSource= getCopiedFileSource(new SubProgressMonitor(pm, 9), fOldCu, newTypeName);
			setSource(newSource);
			setPath(constructNewPath(newTypeName));
			return super.getOldFile(new SubProgressMonitor(pm, 1));
		} catch (CoreException e) {
			setSource(oldSource);
			setPath(oldPath);
			return super.getOldFile(pm);
		}
	}

	private IPath constructNewPath(String newTypeName) {
		String newCUName= getRenamedCUName(fOldCu, newTypeName);
		return fOldCu.getResource().getParent().getFullPath().append(newCUName);
	}
	
	private String getRenamedCUName(IEGLFile cu, String newMainName) {
		String oldName = cu.getElementName();
		int i = oldName.lastIndexOf('.');
		if (i != -1) {
			return newMainName + oldName.substring(i);
		} else {
			return newMainName;
		}
	}

	private static String getCopiedFileSource(IProgressMonitor pm, IEGLFile cu, String newTypeName) throws CoreException {
		String result = null;
		IEGLFile wc= (IEGLFile) cu.getWorkingCopy();
		try {
			//EGLTextChangeManager manager= createChangeManager(pm, wc, newTypeName);
			//String result= manager.get(wc).getPreviewContent(new NullProgressMonitor());
			result = wc.getBuffer().getContents();			
		} finally {
			wc.destroy();
		}
		return result;
	}
	
//	private static EGLTextChangeManager createChangeManager(IProgressMonitor pm, IEGLFile wc, String newName) throws CoreException {
//		EGLTextChangeManager manager= new EGLTextChangeManager();
//		return manager;
//	}
}
