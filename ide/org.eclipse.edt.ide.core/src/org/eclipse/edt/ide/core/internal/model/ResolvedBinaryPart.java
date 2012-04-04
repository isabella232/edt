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

import org.eclipse.edt.ide.core.model.IEGLElement;

public class ResolvedBinaryPart extends BinaryPart {
	private String uniqueKey;

	protected ResolvedBinaryPart(IEGLElement parent, String name) {
		super(parent, name);
		this.uniqueKey = uniqueKey;
	}

	// public String getFullyQualifiedParameterizedName() throws
	// EGLModelException {
	// return getFullyQualifiedParameterizedName(getFullyQualifiedName('.'),
	// this.uniqueKey);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.core.BinaryType#getKey()
	 */
	public String getKey() {
		return this.uniqueKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.core.BinaryType#isResolved()
	 */
	public boolean isResolved() {
		return true;
	}

	public EGLElement unresolved() {
		SourceRefElement handle = new BinaryPart(this.fParent, this.fName);
		// TODO handle.occurrenceCount = this.occurrenceCount;
		return handle;
	}
}
