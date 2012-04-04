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

import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageDeclaration;

/**
 * @see IPackageDeclaration
 */

public class PackageDeclaration extends SourceRefElement implements IPackageDeclaration {
protected PackageDeclaration(IEGLFile parent, String name) {
	super(PACKAGE_DECLARATION, parent, name);
}
public PackageDeclaration(ClassFile parent, String name) {
	super(PACKAGE_DECLARATION, parent, name);
}
/**
 * @see EGLElement#equalsDOMNode
 */
// protected boolean equalsDOMNode(IDOMNode node) throws EGLModelException {
//	return (node.getNodeType() == IDOMNode.PACKAGE) && getElementName().equals(node.getName());
// }
/**
 * @see EGLElement#getHandleMemento()
 */
protected char getHandleMementoDelimiter() {
	return EGLElement.EGLM_PACKAGEDECLARATION;
}
/**
 * @private Debugging purposes
 */
protected void toStringInfo(int tab, StringBuffer buffer, Object info) {
	buffer.append(this.tabString(tab));
	buffer.append("package "); //$NON-NLS-1$
	buffer.append(getElementName());
	if (info == null) {
		buffer.append(" (not open)"); //$NON-NLS-1$
	}
}
}
