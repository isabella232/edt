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
package org.eclipse.edt.ide.core.internal.model;

import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;


/**
 * An operation created as a result of a call to EGLCore.run(IWorkspaceRunnable, IProgressMonitor)
 * that encapsulates a user defined IWorkspaceRunnable.
 */
public class BatchOperation extends EGLModelOperation {
	protected IWorkspaceRunnable runnable;
	public BatchOperation(IWorkspaceRunnable runnable) {
		this.runnable = runnable;
	}

	/* (non-EGLdoc)
	 * @see org.eclipse.edt.ide.core.internal.model.EGLModelOperation#executeOperation()
	 */
	protected void executeOperation() throws EGLModelException {
		try {
			this.runnable.run(fMonitor);
		} catch (CoreException ce) {
			if (ce instanceof EGLModelException) {
				throw (EGLModelException)ce;
			} else {
				if (ce.getStatus().getCode() == IResourceStatus.OPERATION_FAILED) {
					Throwable e= ce.getStatus().getException();
					if (e instanceof EGLModelException) {
						throw (EGLModelException) e;
					}
				}
				throw new EGLModelException(ce);
			}
		}
	}
	
	/* (non-EGLdoc)
	 * @see org.eclipse.edt.ide.core.internal.model.EGLModelOperation#verify()
	 */
	protected IEGLModelStatus verify() {
		// cannot verify user defined operation
		return EGLModelStatus.VERIFIED_OK;
	}

	
}
