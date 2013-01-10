/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.edt.ide.ui.internal.refactoring.tagging.INameUpdating;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;

public abstract class RenameProcessor extends org.eclipse.ltk.core.refactoring.participants.RenameProcessor implements INameUpdating {
	
	private String fNewElementName;
	private String fComment;
	
	public final RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context) throws CoreException, OperationCanceledException {
		RefactoringStatus result= doCheckFinalConditions(pm, context);
		return result;
	}
	
	protected abstract RefactoringStatus doCheckFinalConditions(IProgressMonitor pm, CheckConditionsContext context) throws CoreException, OperationCanceledException;
	
	protected abstract IFile[] getChangedFiles() throws CoreException;

	protected abstract String[] getAffectedProjectNatures() throws CoreException;

	public void setNewElementName(String newName) {
		fNewElementName= newName;
	}

	public String getNewElementName() {
		return fNewElementName;
	}
	
	/**
	 * <code>true</code> by default, subclasses may override.
	 * 
	 * @return <code>true</code> iff this refactoring needs all editors to be saved,
	 *  <code>false</code> otherwise
	 */
	public boolean needsSavedEditors() {
		return true;
	}

	public final boolean canEnableComment() {
		return true;
	}

	public final String getComment() {
		return fComment;
	}

	public final void setComment(String comment) {
		fComment= comment;
	}
}
