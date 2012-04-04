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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class FileRemoveOperation extends WorkspaceModifyOperation {

	private EGLFileConfiguration configuration;
	
	/**
	 * 
	 */
	public FileRemoveOperation(EGLFileConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void execute(IProgressMonitor monitor)
		throws CoreException, InvocationTargetException, InterruptedException {
			
		IFile eglFile = configuration.getFile();
		
		if(eglFile!=null)
			eglFile.delete(true, false,monitor);
		else
			throw new CoreException(new StatusInfo(IStatus.ERROR, NewWizardMessages.NewEGLFileWizardPageRemoveError));
	}

}
