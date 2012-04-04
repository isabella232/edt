/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
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

import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.WorkingCopyOwner;


public class DefaultWorkingCopyOwner extends WorkingCopyOwner{
	public WorkingCopyOwner primaryBufferProvider;

	public static final DefaultWorkingCopyOwner PRIMARY =  new DefaultWorkingCopyOwner();

	private DefaultWorkingCopyOwner() {
		// only one instance can be created
	}

	public IBuffer createBuffer(IEGLFile workingCopy) {
		if (this.primaryBufferProvider != null) return this.primaryBufferProvider.createBuffer(workingCopy);
		return super.createBuffer(workingCopy);
	}
	public String toString() {
		return "Primary owner"; //$NON-NLS-1$
	}
}
