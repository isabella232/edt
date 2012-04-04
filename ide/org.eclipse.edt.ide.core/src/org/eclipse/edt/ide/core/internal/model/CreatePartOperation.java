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


/**
 * <p>This operation creates a class or interface.
 *
 * <p>Required Attributes:<ul>
 *  <li>Parent element - must be a compilation unit, or type.
 *  <li>The source code for the type. No verification of the source is
 *      performed.
 * </ul>
 */
public class CreatePartOperation /* extends CreatePartMemberOperation */{
/**
 * When executed, this operation will create a type unit
 * in the given parent element (a compilation unit, type)
 */
//public CreatePartOperation(IEGLElement parentElement, String source, boolean force) {
//	super(parentElement, source, force);
//}
///**
// * @see CreateElementInCUOperation#generateResultHandle()
// */
//protected IEGLElement generateResultHandle() {
//	IEGLElement parent= getParentElement();
//	int type= parent.getElementType();
//	if (type == IEGLElement.PART) {
//		return ((IPart)parent).getType(fDOMNode.getName());
//	} else if (type == IEGLElement.EGL_FILE) {
//		return ((IEGLFile)parent).getType(fDOMNode.getName());
//	} 
//	return null;
//}
///**
// * @see CreateElementInCUOperation#getMainTaskName()
// */
//public String getMainTaskName(){
//	return Util.bind("operation.createTypeProgress"); //$NON-NLS-1$
//}
///**
// * Returns the <code>IPart</code> the member is to be created in.
// */
//protected IPart getType() {
//	IEGLElement parent = getParentElement();
//	if (parent.getElementType() == IEGLElement.PART) {
//		return (IPart) parent;
//	}
//	return null;
//}
///**
// * @see CreateTypeMemberOperation#verifyNameCollision
// */
//protected IEGLModelStatus verifyNameCollision() {
//	IEGLElement parent = getParentElement();
//	int type = parent.getElementType();
//	if (type == IEGLElement.PART) {
//		if (((IPart) parent).getType(fDOMNode.getName()).exists()) {
//			return new EGLModelStatus(
//				IEGLModelStatusConstants.NAME_COLLISION, 
//				Util.bind("status.nameCollision", fDOMNode.getName())); //$NON-NLS-1$
//		}
//	} else
//		if (type == IEGLElement.EGL_FILE) {
//			if (((IEGLFile) parent).getType(fDOMNode.getName()).exists()) {
//				return new EGLModelStatus(
//					IEGLModelStatusConstants.NAME_COLLISION, 
//					Util.bind("status.nameCollision", fDOMNode.getName())); //$NON-NLS-1$
//			}
//		}
//	return EGLModelStatus.VERIFIED_OK;
//}
}
