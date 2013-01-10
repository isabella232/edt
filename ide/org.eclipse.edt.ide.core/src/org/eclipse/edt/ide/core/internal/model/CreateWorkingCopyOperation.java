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

import java.util.Map;

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBufferFactory;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IProblemRequestor;


/**
 * Creates a new working copy and signal its addition through a delta.
 */
public class CreateWorkingCopyOperation extends EGLModelOperation {
	
	Map perFactoryWorkingCopies;
	IBufferFactory factory;
	IProblemRequestor problemRequestor;
	
	/*
	 * Creates a working copy from the given original cu and the given buffer factory.
	 * perFactoryWorkingCopies map is not null if the working copy is a shared working copy.
	 */
	public CreateWorkingCopyOperation(IEGLFile originalElement, Map perFactoryWorkingCopies, IBufferFactory factory, IProblemRequestor problemRequestor) {
		super(new IEGLElement[] {originalElement});
		this.perFactoryWorkingCopies = perFactoryWorkingCopies;
		this.factory = factory;
		this.problemRequestor = problemRequestor;
	}
	protected void executeOperation() throws EGLModelException {
		IEGLFile cu = getEGLFile();

		WorkingCopy workingCopy = new WorkingCopy((IPackageFragment)cu.getParent(), cu.getElementName(), this.factory, this.problemRequestor);
		// open the working copy now to ensure contents are that of the current state of this element
		workingCopy.open(this.fMonitor);
		
		if (this.perFactoryWorkingCopies != null) {
			this.perFactoryWorkingCopies.put(cu, workingCopy);
			if (EGLFile.SHARED_WC_VERBOSE) {
				System.out.println("Creating shared working copy " + workingCopy.toStringWithAncestors()); //$NON-NLS-1$
			}
		}

		// report added java delta
		EGLElementDelta delta = new EGLElementDelta(this.getEGLModel());
		delta.added(workingCopy);
		addDelta(delta);

		fResultElements = new IEGLElement[] {workingCopy};
	}
	/**
	 * Returns the compilation unit this operation is working on.
	 */
	protected IEGLFile getEGLFile() {
		return (IEGLFile)getElementToProcess();
	}
	/**
	 * @see EGLModelOperation#isReadOnly
	 */
	public boolean isReadOnly() {
		return true;
	}

}
