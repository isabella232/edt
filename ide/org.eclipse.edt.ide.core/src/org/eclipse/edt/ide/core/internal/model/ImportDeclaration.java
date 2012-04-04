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

import org.eclipse.edt.ide.core.model.IImportContainer;
import org.eclipse.edt.ide.core.model.IImportDeclaration;

/**
 * @see IImportDeclaration
 */

/* package */ class ImportDeclaration extends SourceRefElement implements IImportDeclaration {


/**
 * Constructs an ImportDeclartaion in the given import container
 * with the given name.
 */
protected ImportDeclaration(IImportContainer parent, String name) {
	super(IMPORT_DECLARATION, parent, name);
}
/**
 * @see EGLElement#getHandleMemento()
 */
protected char getHandleMementoDelimiter() {
	return EGLElement.EGLM_IMPORTDECLARATION;
}
/**
 * Returns true if the import is on-demand (ends with ".*")
 */
public boolean isOnDemand() {
	return fName.endsWith(".*"); //$NON-NLS-1$
}
/**
 */
public String readableName() {

	return null;
}
/**
 * @private Debugging purposes
 */
protected void toStringInfo(int tab, StringBuffer buffer, Object info) {
	buffer.append(this.tabString(tab));
	buffer.append("import "); //$NON-NLS-1$
	buffer.append(getElementName());
	if (info == null) {
		buffer.append(" (not open)"); //$NON-NLS-1$
	}
}
}
