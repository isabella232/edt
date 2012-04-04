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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.INewNameQuery;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.ibm.icu.text.MessageFormat;

public class CopyEGLFileChange extends EGLFileReorgChange {
	
	public CopyEGLFileChange(IEGLFile cu, IPackageFragment dest, INewNameQuery newNameQuery){
		super(cu, dest, newNameQuery);
	}
		
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
		return new RefactoringStatus();
	}
	
	Change doPerformReorg(IProgressMonitor pm) throws CoreException {
		getCu().copy(getDestinationPackage(), null, getNewName(), true, pm);
		return null;
	}

	public String getName() {
		return MessageFormat.format(UINlsStrings.CopyCompilationUnitChange_copy, 
			new String[]{getCu().getElementName(), getPackageName(getDestinationPackage())});
	}
	
	public void initializeValidationData(IProgressMonitor pm) {
	}
}
