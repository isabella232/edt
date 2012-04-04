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

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IProblemRequestor;

/**
 * Reconcile a working copy and signal the changes through a delta.
 */
public class ReconcileWorkingCopyOperation extends EGLModelOperation {
		
	boolean forceProblemDetection;
	
	public ReconcileWorkingCopyOperation(IEGLElement workingCopy, boolean forceProblemDetection) {
		super(new IEGLElement[] {workingCopy});
		this.forceProblemDetection = forceProblemDetection;
	}
	/**
	 * @exception EGLModelException if setting the source
	 * 	of the original compilation unit fails
	 */
	protected void executeOperation() throws EGLModelException {
		if (fMonitor != null){
			if (fMonitor.isCanceled()) return;
			fMonitor.beginTask(EGLModelResources.elementReconciling, 10);
		}
	
		WorkingCopy workingCopy = getWorkingCopy();
		boolean wasConsistent = workingCopy.isConsistent();
		EGLElementDeltaBuilder deltaBuilder = null;
	
		try {
			// create the delta builder (this remembers the current content of the cu)
			if (!wasConsistent){
				deltaBuilder = new EGLElementDeltaBuilder(workingCopy);
				
				// update the element infos with the content of the working copy
				workingCopy.makeConsistent(fMonitor);
				deltaBuilder.buildDeltas();
		
			}
	
			if (fMonitor != null) fMonitor.worked(2);
			
			// force problem detection? - if structure was consistent
			if (forceProblemDetection && wasConsistent){
				if (fMonitor != null && fMonitor.isCanceled()) return;
		
				IProblemRequestor problemRequestor = workingCopy.problemRequestor;
				if (problemRequestor != null && problemRequestor.isActive()){
					problemRequestor.beginReporting();
					EGLFileProblemFinder.process(workingCopy, problemRequestor, fMonitor);
					problemRequestor.endReporting();
				}
			}
			
			// register the deltas
			if (deltaBuilder != null){
				if ((deltaBuilder.delta != null) && (deltaBuilder.delta.getAffectedChildren().length > 0)) {
					addReconcileDelta(workingCopy, deltaBuilder.delta);
				}
			}
		} finally {
			if (fMonitor != null) fMonitor.done();
		}
	}
	/**
	 * Returns the working copy this operation is working on.
	 */
	protected WorkingCopy getWorkingCopy() {
		return (WorkingCopy)getElementToProcess();
	}
	/**
	 * @see EGLModelOperation#isReadOnly
	 */
	public boolean isReadOnly() {
		return true;
	}
	protected IEGLModelStatus verify() {
		IEGLModelStatus status = super.verify();
		if (!status.isOK()) {
			return status;
		}
		WorkingCopy workingCopy = getWorkingCopy();
		if (workingCopy.useCount == 0) {
			return new EGLModelStatus(IEGLModelStatusConstants.ELEMENT_DOES_NOT_EXIST, workingCopy); //was destroyed
		}
		return status;
	}


}
