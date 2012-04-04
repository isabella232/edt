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

import org.eclipse.edt.ide.core.model.ISourceRange;

/* package */ class EGLFileElementInfo extends OpenableElementInfo {

	/**
	 * The length of this compilation unit's source code <code>String</code>
	 */
	protected int fSourceLength;

	/** 
	 * Timestamp of original resource at the time this element
	 * was opened or last updated.
	 */
	protected long fTimestamp;
/**
 * Returns the length of the source string.
 */
public int getSourceLength() {
	return fSourceLength;
}
protected ISourceRange getSourceRange() {
	return new SourceRange(0, fSourceLength);
}
/**
 * Sets the length of the source string.
 */
public void setSourceLength(int newSourceLength) {
	fSourceLength = newSourceLength;
}
}
