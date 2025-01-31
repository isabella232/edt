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

import java.util.ArrayList;

import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IParent;
import org.eclipse.edt.ide.core.model.IRegion;



/**
 * @see IRegion
 */
 
public class Region implements IRegion {

	/**
	 * A collection of the top level elements
	 * that have been added to the region
	 */
	protected ArrayList fRootElements;
/**
 * Creates an empty region.
 *
 * @see IRegion
 */
public Region() {
	fRootElements = new ArrayList(1);
}
/**
 * @see IRegion#add(IEGLElement)
 */
public void add(IEGLElement element) {
	if (!contains(element)) {
		//"new" element added to region
		removeAllChildren(element);
		fRootElements.add(element);
		fRootElements.trimToSize();
	}
}
/**
 * @see IRegion
 */
public boolean contains(IEGLElement element) {
	
	int size = fRootElements.size();
	ArrayList parents = getAncestors(element);
	
	for (int i = 0; i < size; i++) {
		IEGLElement aTop = (IEGLElement) fRootElements.get(i);
		if (aTop.equals(element)) {
			return true;
		}
		for (int j = 0, pSize = parents.size(); j < pSize; j++) {
			if (aTop.equals(parents.get(j))) {
				//an ancestor is already included
				return true;
			}
		}
	}
	return false;
}
/**
 * Returns a collection of all the parents of this element
 * in bottom-up order.
 *
 */
private ArrayList getAncestors(IEGLElement element) {
	ArrayList parents = new ArrayList();
	IEGLElement parent = element.getParent();
	while (parent != null) {
		parents.add(parent);
		parent = parent.getParent();
	}
	parents.trimToSize();
	return parents;
}
/**
 * @see IRegion
 */
public IEGLElement[] getElements() {
	int size= fRootElements.size();
	IEGLElement[] roots= new IEGLElement[size];
	for (int i = 0; i < size; i++) {
		roots[i]= (IEGLElement) fRootElements.get(i);
	}

	return roots;
}
/**
 * @see IRegion#remove(IEGLElement)
 */
public boolean remove(IEGLElement element) {

	removeAllChildren(element);
	return fRootElements.remove(element);
}
/**
 * Removes any children of this element that are contained within this
 * region as this parent is about to be added to the region.
 *
 * <p>Children are all children, not just direct children.
 */
private void removeAllChildren(IEGLElement element) {
	if (element instanceof IParent) {
		ArrayList newRootElements = new ArrayList();
		for (int i = 0, size = fRootElements.size(); i < size; i++) {
			IEGLElement currentRoot = (IEGLElement)fRootElements.get(i);
			//walk the current root hierarchy
			IEGLElement parent = currentRoot.getParent();
			boolean isChild= false;
			while (parent != null) {
				if (parent.equals(element)) {
					isChild= true;
					break;
				}
				parent = parent.getParent();
			}
			if (!isChild) {
				newRootElements.add(currentRoot);
			}
		}
		fRootElements= newRootElements;
	}
}
/**
 * Returns a printable representation of this region.
 */
public String toString() {
	StringBuffer buffer= new StringBuffer();
	IEGLElement[] roots= getElements();
	buffer.append('[');
	for (int i= 0; i < roots.length; i++) {
		buffer.append(roots[i].getElementName());
		if (i < (roots.length - 1)) {
			buffer.append(", "); //$NON-NLS-1$
		}
	}
	buffer.append(']');
	return buffer.toString();
}
}
