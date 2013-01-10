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
package org.eclipse.edt.ide.core.internal.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IWorkingCopy;


/**
 * Commits the contents of a working copy compilation
 * unit to its original element and resource, bringing
 * the EGL Model up-to-date with the current contents of the working
 * copy.
 *
 * <p>It is possible that the contents of the
 * original resource have changed since the working copy was created,
 * in which case there is an update conflict. This operation allows
 * for two settings to resolve conflict set by the <code>fForce</code> flag:<ul>
 * <li>force flag is <code>false</code> - in this case an <code>EGLModelException</code>
 * 	is thrown</li>
 * <li>force flag is <code>true</code> - in this case the contents of
 * 	the working copy are applied to the underlying resource even though
 * 	the working copy was created before a subsequent change in the
 * 	resource</li>
 * </ul>
 *
 * <p>The default conflict resolution setting is the force flag is <code>false</code>
 *
 * A EGLModelOperation exception is thrown either if the commit could not
 * be performed or if the new content of the compilation unit violates some EGL Model
 * constraint (e.g. if the new package declaration doesn't match the name of the folder
 * containing the compilation unit).
 */
public class CommitWorkingCopyOperation extends EGLModelOperation {
	/**
	 * Constructs an operation to commit the contents of a working copy
	 * to its original compilation unit.
	 */
	public CommitWorkingCopyOperation(IEGLFile element, boolean force) {
		super(new IEGLElement[] {element}, force);
	}
	/**
	 * @exception EGLModelException if setting the source
	 * 	of the original compilation unit fails
	 */
	protected void executeOperation() throws EGLModelException {
		try {
			beginTask(EGLModelResources.workingCopyCommit, 2);
			WorkingCopy copy = (WorkingCopy)getEGLFile();
			IEGLFile original = (IEGLFile) copy.getOriginalElement();
		
			
			// creates the delta builder (this remembers the content of the cu)	
			if (!original.isOpen()) {
				// force opening so that the delta builder can get the old info
				original.open(null);
			}
			EGLElementDeltaBuilder deltaBuilder;
			if (Util.isExcluded(original)) {
				deltaBuilder = null;
			} else {
				deltaBuilder = new EGLElementDeltaBuilder(original);
			}
		
			// save the cu
			IBuffer originalBuffer = original.getBuffer();
			if (originalBuffer == null) return;
			char[] originalContents = originalBuffer.getCharacters();
			boolean hasSaved = false;
			try {
				IBuffer copyBuffer = copy.getBuffer();
				if (copyBuffer == null) return;
				originalBuffer.setContents(copyBuffer.getCharacters());
				original.save(fMonitor, fForce);
				this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
				hasSaved = true;
			} finally {
				if (!hasSaved){
					// restore original buffer contents since something went wrong
					originalBuffer.setContents(originalContents);
				}
			}
			// make sure working copy is in sync
			copy.updateTimeStamp((EGLFile)original);
			copy.makeConsistent(this);
			worked(1);
		
			if (deltaBuilder != null) {
				// build the deltas
				deltaBuilder.buildDeltas();
			
				// add the deltas to the list of deltas created during this operation
				if (deltaBuilder.delta != null) {
					addDelta(deltaBuilder.delta);
				}
			}
			worked(1);
		} finally {	
			done();
		}
	}
	/**
	 * Returns the compilation unit this operation is working on.
	 */
	protected IEGLFile getEGLFile() {
		return (IEGLFile)getElementToProcess();
	}
	/**
	 * Possible failures: <ul>
	 *	<li>INVALID_ELEMENT_TYPES - the compilation unit supplied to this
	 *		operation is not a working copy
	 *  <li>ELEMENT_NOT_PRESENT - the compilation unit the working copy is
	 *		based on no longer exists.
	 *  <li>UPDATE_CONFLICT - the original compilation unit has changed since
	 *		the working copy was created and the operation specifies no force
	 *  <li>READ_ONLY - the original compilation unit is in read-only mode
	 *  </ul>
	 */
	public IEGLModelStatus verify() {
		IEGLFile cu = getEGLFile();
		if (!cu.isWorkingCopy()) {
			return new EGLModelStatus(IEGLModelStatusConstants.INVALID_ELEMENT_TYPES, cu);
		}
		IEGLFile original= (IEGLFile)cu.getOriginalElement();
		IResource resource = original.getResource();
		if (!cu.isBasedOn(resource) && !fForce) {
			return new EGLModelStatus(IEGLModelStatusConstants.UPDATE_CONFLICT);
		}
		// no read-only check, since some repository adapters can change the flag on save
		// operation.	
		return EGLModelStatus.VERIFIED_OK;
	}
	protected ISchedulingRule getSchedulingRule() {
		//IResource resource = getElementToProcess().getResource();
		IResource resource = ((IWorkingCopy)getElementToProcess()).getOriginalElement().getResource();
		IWorkspace workspace = resource.getWorkspace();
		if (resource.exists()) {
			return workspace.getRuleFactory().modifyRule(resource);
		} else {
			return workspace.getRuleFactory().createRule(resource);
		}
	}
}
