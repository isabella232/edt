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

/**
 * @see ISourceRange
 */
/* package */ class SourceRange implements ISourceRange {

protected int offset, length;

protected SourceRange(int offset, int length) {
	this.offset = offset;
	this.length = length;
}
/**
 * @see ISourceRange
 */
public int getLength() {
	return this.length;
}
/**
 * @see ISourceRange
 */
public int getOffset() {
	return this.offset;
}
public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("[offset="); //$NON-NLS-1$
	buffer.append(this.offset);
	buffer.append(", length="); //$NON-NLS-1$
	buffer.append(this.length);
	buffer.append("]"); //$NON-NLS-1$
	return buffer.toString();
}
}
