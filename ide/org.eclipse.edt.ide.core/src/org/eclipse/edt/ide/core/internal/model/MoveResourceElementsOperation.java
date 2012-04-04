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

import org.eclipse.edt.ide.core.model.IEGLElement;

/**
 * @author mattclem
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
/**
 * This operation moves resources (package fragments and compilation units) from their current
 * container to a specified destination container, optionally renaming the
 * elements.
 * A move resource operation is equivalent to a copy resource operation, where
 * the source resources are deleted after the copy.
 * <p>This operation can be used for reorganizing resources within the same container.
 *
 * @see CopyResourceElementsOperation
 */
public class MoveResourceElementsOperation extends CopyResourceElementsOperation {
/**
 * When executed, this operation will move the given elements to the given containers.
 */
public MoveResourceElementsOperation(IEGLElement[] elementsToMove, IEGLElement[] destContainers, boolean force) {
	super(elementsToMove, destContainers, force);
}
/**
 * @see MultiOperation
 */
protected String getMainTaskName() {
	return EGLModelResources.operationMoveResourceProgress;
}
/**
 * @see CopyResourceElementsOperation#isMove()
 */
protected boolean isMove() {
	return true;
}
}
