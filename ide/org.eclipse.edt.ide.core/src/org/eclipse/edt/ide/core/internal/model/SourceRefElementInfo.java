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
 * Element info for ISourceReference elements. 
 */
/* package */ class SourceRefElementInfo extends EGLElementInfo {
	protected int fSourceRangeStart, fSourceRangeEnd;
protected SourceRefElementInfo() {
	setIsStructureKnown(true);
}
public int getDeclarationSourceEnd() {
	return fSourceRangeEnd;
}
/**
 * @see com.ibm.etools.egl.internal.model.internal.compiler.env.ISourceType#getDeclarationSourceStart()
 * @see com.ibm.etools.egl.internal.model.internal.compiler.env.ISourceMethod#getDeclarationSourceStart()
 * @see com.ibm.etools.egl.internal.model.internal.compiler.env.ISourceField#getDeclarationSourceStart()
 */
public int getDeclarationSourceStart() {
	return fSourceRangeStart;
}
protected ISourceRange getSourceRange() {
	return new SourceRange(fSourceRangeStart, fSourceRangeEnd - fSourceRangeStart);
}
protected void setSourceRangeEnd(int end) {
	fSourceRangeEnd = end;
}
protected void setSourceRangeStart(int start) {
	fSourceRangeStart = start;
}
}
