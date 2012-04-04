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
package org.eclipse.edt.ide.ui.internal.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.ibm.icu.text.MessageFormat;

public class DeleteBeanChange extends Change {

	IFile beanFile;
	IFile beanInfoFile;

	public DeleteBeanChange(IFile beanFile, IFile beanInfoFile) {
		super();
		this.beanFile = beanFile;
		this.beanInfoFile = beanInfoFile;
	}

	public Object getModifiedElement() {
		return beanFile;
	}

	public String getName() {
		return MessageFormat.format(UINlsStrings.DeleteJsfBeanRefactoring_name, new String[] {beanFile.getFullPath().toString(), beanInfoFile.getFullPath().toString()});
	}

	public void initializeValidationData(IProgressMonitor pm) {

	}

	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
		
		if (beanFile != null) {
			if (beanFile.exists())
				DeleteChange.delete(beanFile, pm);
		}

		if (beanInfoFile != null) {
			if (beanInfoFile.exists())
				DeleteChange.delete(beanInfoFile, pm);
		}

		return null;
	}

}
