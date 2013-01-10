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

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;

/**
 * @author mattclem
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RenameResourceElementsOperation extends MoveResourceElementsOperation {
/**
 * When executed, this operation will rename the specified elements with the given names in the
 * corresponding destinations.
 */
public RenameResourceElementsOperation(IEGLElement[] elements, IEGLElement[] destinations, String[] newNames, boolean force) {
	//a rename is a move to the same parent with a new name specified
	//these elements are from different parents
	super(elements, destinations, force);
	setRenamings(newNames);
}
/**
 * @see MultiOperation
 */
protected String getMainTaskName() {
	return EGLModelResources.operationRenameResourceProgress;
}
/**
 * @see CopyResourceElementsOperation#isRename()
 */
protected boolean isRename() {
	return true;
}
/**
 * @see MultiOperation
 */
protected void verify(IEGLElement element) throws EGLModelException {
	super.verify(element);

	int elementType = element.getElementType();
	
	if (!(elementType == IEGLElement.EGL_FILE || elementType == IEGLElement.PACKAGE_FRAGMENT)) {
		error(IEGLModelStatusConstants.INVALID_ELEMENT_TYPES, element);
	}
	if (elementType == IEGLElement.EGL_FILE) {
		if (((IEGLFile) element).isWorkingCopy()) {
			error(IEGLModelStatusConstants.INVALID_ELEMENT_TYPES, element);
		}
	}
	verifyRenaming(element);
}
}
