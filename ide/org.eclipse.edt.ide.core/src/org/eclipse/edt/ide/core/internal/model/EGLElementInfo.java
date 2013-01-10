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

import org.eclipse.edt.ide.core.model.IEGLElement;

/**
 * Holds cached structure and properties for a EGL element.
 * Subclassed to carry properties for specific kinds of elements.
 */
/* package */ class EGLElementInfo {

	/**
	 * Collection of handles of immediate children of this
	 * object. This is an empty array if this element has
	 * no children.
	 */
	protected IEGLElement[] fChildren;

	/**
	 * Shared empty collection used for efficiency.
	 */
	protected static IEGLElement[] fgEmptyChildren = new IEGLElement[]{};
	/**
	 * Is the structure of this element known
	 * @see IEGLElement#isStructureKnown()
	 */
	protected boolean fIsStructureKnown = false;

	/**
	 * Shared empty collection used for efficiency.
	 */
	static Object[] NO_NON_EGL_RESOURCES = new Object[] {};	
	protected EGLElementInfo() {
		fChildren = fgEmptyChildren;
	}
	public void addChild(IEGLElement child) {
		if (fChildren == fgEmptyChildren) {
			setChildren(new IEGLElement[] {child});
		} else {
			if (!includesChild(child)) {
				setChildren(growAndAddToArray(fChildren, child));
			}
		}
	}
	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new Error();
		}
	}
	public IEGLElement[] getChildren() {
		return fChildren;
	}
	/**
	 * Adds the new element to a new array that contains all of the elements of the old array.
	 * Returns the new array.
	 */
	protected IEGLElement[] growAndAddToArray(IEGLElement[] array, IEGLElement addition) {
		IEGLElement[] old = array;
		array = new IEGLElement[old.length + 1];
		System.arraycopy(old, 0, array, 0, old.length);
		array[old.length] = addition;
		return array;
	}
	/**
	 * Returns <code>true</code> if this child is in my children collection
	 */
	protected boolean includesChild(IEGLElement child) {
		
		for (int i= 0; i < fChildren.length; i++) {
			if (fChildren[i].equals(child)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @see IEGLElement#isStructureKnown()
	 */
	public boolean isStructureKnown() {
		return fIsStructureKnown;
	}
	/**
	 * Returns an array with all the same elements as the specified array except for
	 * the element to remove. Assumes that the deletion is contained in the array.
	 */
	protected IEGLElement[] removeAndShrinkArray(IEGLElement[] array, IEGLElement deletion) {
		IEGLElement[] old = array;
		array = new IEGLElement[old.length - 1];
		int j = 0;
		for (int i = 0; i < old.length; i++) {
			if (!old[i].equals(deletion)) {
				array[j] = old[i];
			} else {
				System.arraycopy(old, i + 1, array, j, old.length - (i + 1));
				return array;
			}
			j++;
		}
		return array;
	}
	public void removeChild(IEGLElement child) {
		if (includesChild(child)) {
			setChildren(removeAndShrinkArray(fChildren, child));
		}
	}
	public void setChildren(IEGLElement[] children) {
		fChildren = children;
	}
	/**
	 * Sets whether the structure of this element known
	 * @see IEGLElement#isStructureKnown()
	 */
	public void setIsStructureKnown(boolean newIsStructureKnown) {
		fIsStructureKnown = newIsStructureKnown;
	}
}
