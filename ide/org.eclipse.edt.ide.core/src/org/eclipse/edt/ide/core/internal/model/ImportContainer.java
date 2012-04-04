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
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IImportContainer;
import org.eclipse.edt.ide.core.model.IImportDeclaration;
import org.eclipse.edt.ide.core.model.IParent;
import org.eclipse.edt.ide.core.model.ISourceRange;
import org.eclipse.edt.ide.core.model.ISourceReference;

/**
 * @see IImportContainer
 */
public class ImportContainer extends SourceRefElement implements IImportContainer {
protected ImportContainer(IEGLFile parent) {
	super(IMPORT_CONTAINER, parent, ""); //$NON-NLS-1$
}
/**
 * @see EGLElement#getHandleMemento()
 */
public String getHandleMemento(){
	return ((EGLElement)getParent()).getHandleMemento();
}
/**
 * @see EGLElement#getHandleMemento()
 */
protected char getHandleMementoDelimiter() {
	Assert.isTrue(false, "Should not be called"); //$NON-NLS-1$
	return 0;
}
/**
 * @see IImportContainer
 */
public IImportDeclaration getImport(String name) {
	return new ImportDeclaration(this, name);
}
/**
 * @see ISourceReference
 */
public ISourceRange getSourceRange() throws EGLModelException {
	IEGLElement[] imports= getChildren();
	ISourceRange firstRange= ((ISourceReference)imports[0]).getSourceRange();
	ISourceRange lastRange= ((ISourceReference)imports[imports.length - 1]).getSourceRange();
	SourceRange range= new SourceRange(firstRange.getOffset(), lastRange.getOffset() + lastRange.getLength() - firstRange.getOffset());
	return range;
}
/**
 * Import containers only exist if they have children.
 * @see IParent
 */
public boolean hasChildren() throws EGLModelException {
	return true;
}
/**
 */
public String readableName() {

	return null;
}
/**
 * @private Debugging purposes
 */
protected void toString(int tab, StringBuffer buffer) {
	Object info = EGLModelManager.getEGLModelManager().peekAtInfo(this);
	if (info == null || !(info instanceof EGLElementInfo)) return;
	IEGLElement[] children = ((EGLElementInfo)info).getChildren();
	for (int i = 0; i < children.length; i++) {
		if (i > 0) buffer.append("\n"); //$NON-NLS-1$
		((EGLElement)children[i]).toString(tab, buffer);
	}
}
/**
 *  Debugging purposes
 */
protected void toStringInfo(int tab, StringBuffer buffer, Object info) {
	buffer.append(this.tabString(tab));
	buffer.append("[import container]"); //$NON-NLS-1$
	if (info == null) {
		buffer.append(" (not open)"); //$NON-NLS-1$
	}
}
}
