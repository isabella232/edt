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

import java.util.ArrayList;

import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLElementDelta;
import org.eclipse.edt.ide.core.model.IEGLProject;


/**
 * @see IEGLElementDelta
 */
public class EGLElementDelta implements IEGLElementDelta {
	/**
	 * The element that this delta describes the change to.
	 * @see #getElement()
	 */
	protected IEGLElement fChangedElement;
	/**
	 * @see #getKind()
	 */
	private int fKind = 0;
	/**
	 * @see #getFlags()
	 */
	private int fChangeFlags = 0;
	/**
	 * @see #getAffectedChildren()
	 */
	protected IEGLElementDelta[] fAffectedChildren = fgEmptyDelta;

	/**
	 * Collection of resource deltas that correspond to non java resources deltas.
	 */
	protected IResourceDelta[] resourceDeltas = null;

	/**
	 * Counter of resource deltas
	 */
	protected int resourceDeltasCounter;
	/**
	 * @see #getMovedFromHandle()
	 */
	protected IEGLElement fMovedFromHandle = null;
	/**
	 * @see #getMovedToHandle()
	 */
	protected IEGLElement fMovedToHandle = null;
	/**
	 * Empty array of IEGLElementDelta
	 */
	protected static  IEGLElementDelta[] fgEmptyDelta= new IEGLElementDelta[] {};
/**
 * Creates the root delta. To create the nested delta
 * hierarchies use the following convenience methods. The root
 * delta can be created at any level (for example: project, package root,
 * package fragment...).
 * <ul>
 * <li><code>added(IEGLElement)</code>
 * <li><code>changed(IEGLElement)</code>
 * <li><code>moved(IEGLElement, IEGLElement)</code>
 * <li><code>removed(IEGLElement)</code>
 * <li><code>renamed(IEGLElement, IEGLElement)</code>
 * </ul>
 */
public EGLElementDelta(IEGLElement element) {
	super();
	fChangedElement = element;
}
/**
 * Adds the child delta to the collection of affected children.  If the
 * child is already in the collection, walk down the hierarchy.
 */
protected void addAffectedChild(EGLElementDelta child) {
	switch (fKind) {
		case ADDED:
		case REMOVED:
			// no need to add a child if this parent is added or removed
			return;
		case CHANGED:
			fChangeFlags |= F_CHILDREN;
			break;
		default:
			fKind = CHANGED;
			fChangeFlags |= F_CHILDREN;
	}

	// if a child delta is added to a compilation unit delta or below, 
	// it's a fine grained delta
	if (fChangedElement.getElementType() >= IEGLElement.EGL_FILE) {
		this.fineGrained();
	}
	
	if (fAffectedChildren.length == 0) {
		fAffectedChildren = new IEGLElementDelta[] {child};
		return;
	}
	IEGLElementDelta existingChild = null;
	int existingChildIndex = -1;
	if (fAffectedChildren != null) {
		for (int i = 0; i < fAffectedChildren.length; i++) {
			if (this.equalsAndSameParent(fAffectedChildren[i].getElement(), child.getElement())) { // handle case of two jars that can be equals but not in the same project
				existingChild = fAffectedChildren[i];
				existingChildIndex = i;
				break;
			}
		}
	}
	if (existingChild == null) { //new affected child
		fAffectedChildren= growAndAddToArray(fAffectedChildren, child);
	} else {
		switch (existingChild.getKind()) {
			case ADDED:
				switch (child.getKind()) {
					case ADDED: // child was added then added -> it is added
					case CHANGED: // child was added then changed -> it is added
						return;
					case REMOVED: // child was added then removed -> noop
						fAffectedChildren = this.removeAndShrinkArray(fAffectedChildren, existingChildIndex);
						return;
				}
				break;
			case REMOVED:
				switch (child.getKind()) {
					case ADDED: // child was removed then added -> it is changed
						child.fKind = CHANGED;
						fAffectedChildren[existingChildIndex] = child;
						return;
					case CHANGED: // child was removed then changed -> it is removed
					case REMOVED: // child was removed then removed -> it is removed
						return;
				}
				break;
			case CHANGED:
				switch (child.getKind()) {
					case ADDED: // child was changed then added -> it is added
					case REMOVED: // child was changed then removed -> it is removed
						fAffectedChildren[existingChildIndex] = child;
						return;
					case CHANGED: // child was changed then changed -> it is changed
						IEGLElementDelta[] children = child.getAffectedChildren();
						for (int i = 0; i < children.length; i++) {
							EGLElementDelta childsChild = (EGLElementDelta) children[i];
							((EGLElementDelta) existingChild).addAffectedChild(childsChild);
						}
						
						// update flags if needed
						switch (((EGLElementDelta) existingChild).fChangeFlags) {
							case F_ADDED_TO_EGLPATH:
							case F_REMOVED_FROM_EGLPATH:
								((EGLElementDelta) existingChild).fChangeFlags |= ((EGLElementDelta) child).fChangeFlags;
								break;
						}
						
						// add the non-java resource deltas if needed
						// note that the child delta always takes precedence over this existing child delta
						// as non-java resource deltas are always created last (by the DeltaProcessor)
						IResourceDelta[] resDeltas = child.getResourceDeltas();
						if (resDeltas != null) {
							((EGLElementDelta)existingChild).resourceDeltas = resDeltas;
							((EGLElementDelta)existingChild).resourceDeltasCounter = child.resourceDeltasCounter;
						}
						return;
				}
				break;
			default: 
				// unknown -> existing child becomes the child with the existing child's flags
				int flags = existingChild.getFlags();
				fAffectedChildren[existingChildIndex] = child;
				child.fChangeFlags |= flags;
		}
	}
}
/**
 * Creates the nested deltas resulting from an add operation.
 * Convenience method for creating add deltas.
 * The constructor should be used to create the root delta 
 * and then an add operation should call this method.
 */
public void added(IEGLElement element) {
	EGLElementDelta addedDelta = new EGLElementDelta(element);
	addedDelta.fKind = ADDED;
	insertDeltaTree(element, addedDelta);
}
/**
 * Adds the child delta to the collection of affected children.  If the
 * child is already in the collection, walk down the hierarchy.
 */
protected void addResourceDelta(IResourceDelta child) {
	switch (fKind) {
		case ADDED:
		case REMOVED:
			// no need to add a child if this parent is added or removed
			return;
		case CHANGED:
			fChangeFlags |= F_CONTENT;
			break;
		default:
			fKind = CHANGED;
			fChangeFlags |= F_CONTENT;
	}
	if (resourceDeltas == null) {
		resourceDeltas = new IResourceDelta[5];
		resourceDeltas[resourceDeltasCounter++] = child;
		return;
	}
	if (resourceDeltas.length == resourceDeltasCounter) {
		// need a resize
		System.arraycopy(resourceDeltas, 0, (resourceDeltas = new IResourceDelta[resourceDeltasCounter * 2]), 0, resourceDeltasCounter);
	}
	resourceDeltas[resourceDeltasCounter++] = child;
}
/**
 * Creates the nested deltas resulting from a change operation.
 * Convenience method for creating change deltas.
 * The constructor should be used to create the root delta 
 * and then a change operation should call this method.
 */
public void changed(IEGLElement element, int changeFlag) {
	EGLElementDelta changedDelta = new EGLElementDelta(element);
	changedDelta.fKind = CHANGED;
	changedDelta.fChangeFlags |= changeFlag;
	insertDeltaTree(element, changedDelta);
}
/**
 * Mark this delta as a content changed delta.
 */
public void contentChanged() {
	fChangeFlags |= F_CONTENT;
}
/**
 * Clone this delta so that its elements are rooted at the given project.
 */
public IEGLElementDelta clone(IEGLProject project) {
	EGLElementDelta clone = 
		new EGLElementDelta(((EGLElement)fChangedElement).rootedAt(project));
	if (fAffectedChildren != fgEmptyDelta) {
		int length = fAffectedChildren.length;
		IEGLElementDelta[] cloneChildren = new IEGLElementDelta[length];
		for (int i= 0; i < length; i++) {
			cloneChildren[i] = ((EGLElementDelta)fAffectedChildren[i]).clone(project);
		}
		clone.fAffectedChildren = cloneChildren;
	}	
	clone.fChangeFlags = fChangeFlags;
	clone.fKind = fKind;
	if (fMovedFromHandle != null) {
		clone.fMovedFromHandle = ((EGLElement)fMovedFromHandle).rootedAt(project);
	}
	if (fMovedToHandle != null) {
		clone.fMovedToHandle = ((EGLElement)fMovedToHandle).rootedAt(project);
	}
	clone.resourceDeltas = this.resourceDeltas;
	clone.resourceDeltasCounter = this.resourceDeltasCounter;
	return clone;
}

/**
 * Creates the nested deltas for a closed element.
 */
public void closed(IEGLElement element) {
	EGLElementDelta delta = new EGLElementDelta(element);
	delta.fKind = CHANGED;
	delta.fChangeFlags |= F_CLOSED;
	insertDeltaTree(element, delta);
}
/**
 * Creates the nested delta deltas based on the affected element
 * its delta, and the root of this delta tree. Returns the root
 * of the created delta tree.
 */
protected EGLElementDelta createDeltaTree(IEGLElement element, EGLElementDelta delta) {
	EGLElementDelta childDelta = delta;
	ArrayList ancestors= getAncestors(element);
	if (ancestors == null) {
		if (this.equalsAndSameParent(delta.getElement(), getElement())) { // handle case of two jars that can be equals but not in the same project
			// the element being changed is the root element
			fKind= delta.fKind;
			fChangeFlags = delta.fChangeFlags;
			fMovedToHandle = delta.fMovedToHandle;
			fMovedFromHandle = delta.fMovedFromHandle;
		}
	} else {
		for (int i = 0, size = ancestors.size(); i < size; i++) {
			IEGLElement ancestor = (IEGLElement) ancestors.get(i);
			EGLElementDelta ancestorDelta = new EGLElementDelta(ancestor);
			ancestorDelta.addAffectedChild(childDelta);
			childDelta = ancestorDelta;
		}
	}
	return childDelta;
}
/**
 * Returns whether the two java elements are equals and have the same parent.
 */
protected boolean equalsAndSameParent(IEGLElement e1, IEGLElement e2) {
	IEGLElement parent1;
	return e1.equals(e2) && ((parent1 = e1.getParent()) != null) && parent1.equals(e2.getParent());
}
/**
 * Returns the <code>EGLElementDelta</code> for the given element
 * in the delta tree, or null, if no delta for the given element is found.
 */
protected EGLElementDelta find(IEGLElement e) {
	if (this.equalsAndSameParent(fChangedElement, e)) { // handle case of two jars that can be equals but not in the same project
		return this;
	} else {
		for (int i = 0; i < fAffectedChildren.length; i++) {
			EGLElementDelta delta = ((EGLElementDelta)fAffectedChildren[i]).find(e);
			if (delta != null) {
				return delta;
			}
		}
	}
	return null;
}
/**
 * Mark this delta as a fine-grained delta.
 */
public void fineGrained() {
	if (fKind == 0) { // if not set yet
		fKind = CHANGED;
	}
	fChangeFlags |= F_FINE_GRAINED;
}
/**
 * @see IEGLElementDelta
 */
public IEGLElementDelta[] getAddedChildren() {
	return getChildrenOfType(ADDED);
}
/**
 * @see IEGLElementDelta
 */
public IEGLElementDelta[] getAffectedChildren() {
	return fAffectedChildren;
}
/**
 * Returns a collection of all the parents of this element up to (but
 * not including) the root of this tree in bottom-up order. If the given
 * element is not a descendant of the root of this tree, <code>null</code>
 * is returned.
 */
private ArrayList getAncestors(IEGLElement element) {
	IEGLElement parent = element.getParent();
	if (parent == null) {
		return null;
	}
	ArrayList parents = new ArrayList();
	while (!parent.equals(fChangedElement)) {
		parents.add(parent);
		parent = parent.getParent();
		if (parent == null) {
			return null;
		}
	}
	parents.trimToSize();
	return parents;
}
/**
 * @see IEGLElementDelta
 */
public IEGLElementDelta[] getChangedChildren() {
	return getChildrenOfType(CHANGED);
}
/**
 * @see IEGLElementDelta
 */
protected IEGLElementDelta[] getChildrenOfType(int type) {
	int length = fAffectedChildren.length;
	if (length == 0) {
		return new IEGLElementDelta[] {};
	}
	ArrayList children= new ArrayList(length);
	for (int i = 0; i < length; i++) {
		if (fAffectedChildren[i].getKind() == type) {
			children.add(fAffectedChildren[i]);
		}
	}

	IEGLElementDelta[] childrenOfType = new IEGLElementDelta[children.size()];
	children.toArray(childrenOfType);
	
	return childrenOfType;
}
/**
 * Returns the delta for a given element.  Only looks below this
 * delta.
 */
public EGLElementDelta getDeltaFor(IEGLElement element) {
	if (this.equalsAndSameParent(getElement(), element)) // handle case of two jars that can be equals but not in the same project
		return this;
	if (fAffectedChildren.length == 0)
		return null;
	int childrenCount = fAffectedChildren.length;
	for (int i = 0; i < childrenCount; i++) {
		EGLElementDelta delta = (EGLElementDelta)fAffectedChildren[i];
		if (this.equalsAndSameParent(delta.getElement(), element)) { // handle case of two jars that can be equals but not in the same project
			return delta;
		} else {
			delta = ((EGLElementDelta)delta).getDeltaFor(element);
			if (delta != null)
				return delta;
		}
	}
	return null;
}
/**
 * @see IEGLElementDelta
 */
public IEGLElement getElement() {
	return fChangedElement;
}
/**
 * @see IEGLElementDelta
 */
public int getFlags() {
	return fChangeFlags;
}
/**
 * @see IEGLElementDelta
 */
public int getKind() {
	return fKind;
}
/**
 * @see IEGLElementDelta
 */
public IEGLElement getMovedFromElement() {
	return fMovedFromHandle;
}
/**
 * @see IEGLElementDelta
 */
public IEGLElement getMovedToElement() {
	return fMovedToHandle;
}
/**
 * @see IEGLElementDelta
 */
public IEGLElementDelta[] getRemovedChildren() {
	return getChildrenOfType(REMOVED);
}
/**
 * Return the collection of resource deltas. Return null if none.
 */
public IResourceDelta[] getResourceDeltas() {
	if (resourceDeltas == null) return null;
	if (resourceDeltas.length != resourceDeltasCounter) {
		System.arraycopy(resourceDeltas, 0, resourceDeltas = new IResourceDelta[resourceDeltasCounter], 0, resourceDeltasCounter);
	}
	return resourceDeltas;
}
/**
 * Adds the new element to a new array that contains all of the elements of the old array.
 * Returns the new array.
 */
protected IEGLElementDelta[] growAndAddToArray(IEGLElementDelta[] array, IEGLElementDelta addition) {
	IEGLElementDelta[] old = array;
	array = new IEGLElementDelta[old.length + 1];
	System.arraycopy(old, 0, array, 0, old.length);
	array[old.length] = addition;
	return array;
}
/**
 * Creates the delta tree for the given element and delta, and then
 * inserts the tree as an affected child of this node.
 */
protected void insertDeltaTree(IEGLElement element, EGLElementDelta delta) {
	EGLElementDelta childDelta= createDeltaTree(element, delta);
	if (!this.equalsAndSameParent(element, getElement())) { // handle case of two jars that can be equals but not in the same project
		addAffectedChild(childDelta);
	}
}
/**
 * Creates the nested deltas resulting from an move operation.
 * Convenience method for creating the "move from" delta.
 * The constructor should be used to create the root delta 
 * and then the move operation should call this method.
 */
public void movedFrom(IEGLElement movedFromElement, IEGLElement movedToElement) {
	EGLElementDelta removedDelta = new EGLElementDelta(movedFromElement);
	removedDelta.fKind = REMOVED;
	removedDelta.fChangeFlags |= F_MOVED_TO;
	removedDelta.fMovedToHandle = movedToElement;
	insertDeltaTree(movedFromElement, removedDelta);
}
/**
 * Creates the nested deltas resulting from an move operation.
 * Convenience method for creating the "move to" delta.
 * The constructor should be used to create the root delta 
 * and then the move operation should call this method.
 */
public void movedTo(IEGLElement movedToElement, IEGLElement movedFromElement) {
	EGLElementDelta addedDelta = new EGLElementDelta(movedToElement);
	addedDelta.fKind = ADDED;
	addedDelta.fChangeFlags |= F_MOVED_FROM;
	addedDelta.fMovedFromHandle = movedFromElement;
	insertDeltaTree(movedToElement, addedDelta);
}
/**
 * Creates the nested deltas for an opened element.
 */
public void opened(IEGLElement element) {
	EGLElementDelta delta = new EGLElementDelta(element);
	delta.fKind = CHANGED;
	delta.fChangeFlags |= F_OPENED;
	insertDeltaTree(element, delta);
}
/**
 * Removes the child delta from the collection of affected children.
 */
protected void removeAffectedChild(EGLElementDelta child) {
	int index = -1;
	if (fAffectedChildren != null) {
		for (int i = 0; i < fAffectedChildren.length; i++) {
			if (this.equalsAndSameParent(fAffectedChildren[i].getElement(), child.getElement())) { // handle case of two jars that can be equals but not in the same project
				index = i;
				break;
			}
		}
	}
	if (index >= 0) {
		fAffectedChildren= removeAndShrinkArray(fAffectedChildren, index);
	}
}
/**
 * Removes the element from the array.
 * Returns the a new array which has shrunk.
 */
protected IEGLElementDelta[] removeAndShrinkArray(IEGLElementDelta[] old, int index) {
	IEGLElementDelta[] array = new IEGLElementDelta[old.length - 1];
	if (index > 0)
		System.arraycopy(old, 0, array, 0, index);
	int rest = old.length - index - 1;
	if (rest > 0)
		System.arraycopy(old, index + 1, array, index, rest);
	return array;
}
/**
 * Creates the nested deltas resulting from an delete operation.
 * Convenience method for creating removed deltas.
 * The constructor should be used to create the root delta 
 * and then the delete operation should call this method.
 */
public void removed(IEGLElement element) {
	EGLElementDelta removedDelta= new EGLElementDelta(element);
	insertDeltaTree(element, removedDelta);
	EGLElementDelta actualDelta = getDeltaFor(element);
	if (actualDelta != null) {
		actualDelta.fKind = REMOVED;
		actualDelta.fChangeFlags = 0;
		actualDelta.fAffectedChildren = fgEmptyDelta;
	}
}
/**
 * Creates the nested deltas resulting from a change operation.
 * Convenience method for creating change deltas.
 * The constructor should be used to create the root delta 
 * and then a change operation should call this method.
 */
public void sourceAttached(IEGLElement element) {
	EGLElementDelta attachedDelta = new EGLElementDelta(element);
	attachedDelta.fKind = CHANGED;
	attachedDelta.fChangeFlags |= F_SOURCEATTACHED;
	insertDeltaTree(element, attachedDelta);
}
/**
 * Creates the nested deltas resulting from a change operation.
 * Convenience method for creating change deltas.
 * The constructor should be used to create the root delta 
 * and then a change operation should call this method.
 */
public void sourceDetached(IEGLElement element) {
	EGLElementDelta detachedDelta = new EGLElementDelta(element);
	detachedDelta.fKind = CHANGED;
	detachedDelta.fChangeFlags |= F_SOURCEDETACHED;
	insertDeltaTree(element, detachedDelta);
}
/** 
 * Returns a string representation of this delta's
 * structure suitable for debug purposes.
 *
 * @see #toString()
 */
public String toDebugString(int depth) {
	StringBuffer buffer = new StringBuffer();
	for (int i= 0; i < depth; i++) {
		buffer.append('\t');
	}
	buffer.append(((EGLElement)getElement()).toDebugString());
	buffer.append("["); //$NON-NLS-1$
	switch (getKind()) {
		case IEGLElementDelta.ADDED :
			buffer.append('+');
			break;
		case IEGLElementDelta.REMOVED :
			buffer.append('-');
			break;
		case IEGLElementDelta.CHANGED :
			buffer.append('*');
			break;
		default :
			buffer.append('?');
			break;
	}
	buffer.append("]: {"); //$NON-NLS-1$
	int changeFlags = getFlags();
	boolean prev = false;
	if ((changeFlags & IEGLElementDelta.F_CHILDREN) != 0) {
		if (prev)
			buffer.append(" | "); //$NON-NLS-1$
		buffer.append("CHILDREN"); //$NON-NLS-1$
		prev = true;
	}
	if ((changeFlags & IEGLElementDelta.F_CONTENT) != 0) {
		if (prev)
			buffer.append(" | "); //$NON-NLS-1$
		buffer.append("CONTENT"); //$NON-NLS-1$
		prev = true;
	}
	if ((changeFlags & IEGLElementDelta.F_MOVED_FROM) != 0) {
		if (prev)
			buffer.append(" | "); //$NON-NLS-1$
		buffer.append("MOVED_FROM(" + ((EGLElement)getMovedFromElement()).toStringWithAncestors() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		prev = true;
	}
	if ((changeFlags & IEGLElementDelta.F_MOVED_TO) != 0) {
		if (prev)
			buffer.append(" | "); //$NON-NLS-1$
		buffer.append("MOVED_TO(" + ((EGLElement)getMovedToElement()).toStringWithAncestors() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		prev = true;
	}
	if ((changeFlags & IEGLElementDelta.F_ADDED_TO_EGLPATH) != 0) {
		if (prev)
			buffer.append(" | "); //$NON-NLS-1$
		buffer.append("ADDED TO CLASSPATH"); //$NON-NLS-1$
		prev = true;
	}
	if ((changeFlags & IEGLElementDelta.F_REMOVED_FROM_EGLPATH) != 0) {
		if (prev)
			buffer.append(" | "); //$NON-NLS-1$
		buffer.append("REMOVED FROM CLASSPATH"); //$NON-NLS-1$
		prev = true;
	}
	if ((changeFlags & IEGLElementDelta.F_REORDER) != 0) {
		if (prev)
			buffer.append(" | "); //$NON-NLS-1$
		buffer.append("REORDERED"); //$NON-NLS-1$
		prev = true;
	}
	if ((changeFlags & IEGLElementDelta.F_ARCHIVE_CONTENT_CHANGED) != 0) {
		if (prev)
			buffer.append(" | "); //$NON-NLS-1$
		buffer.append("ARCHIVE CONTENT CHANGED"); //$NON-NLS-1$
		prev = true;
	}
	if ((changeFlags & IEGLElementDelta.F_MODIFIERS) != 0) {
		if (prev)
			buffer.append(" | "); //$NON-NLS-1$
		buffer.append("MODIFIERS CHANGED"); //$NON-NLS-1$
		prev = true;
	}
	if ((changeFlags & IEGLElementDelta.F_FINE_GRAINED) != 0) {
		if (prev)
			buffer.append(" | "); //$NON-NLS-1$
		buffer.append("FINE GRAINED"); //$NON-NLS-1$
		prev = true;
	}
	buffer.append("}"); //$NON-NLS-1$
	IEGLElementDelta[] children = getAffectedChildren();
	if (children != null) {
		for (int i = 0; i < children.length; ++i) {
			buffer.append("\n"); //$NON-NLS-1$
			buffer.append(((EGLElementDelta) children[i]).toDebugString(depth + 1));
		}
	}
	for (int i = 0; i < resourceDeltasCounter; i++) {
		buffer.append("\n");//$NON-NLS-1$
		for (int j = 0; j < depth+1; j++) {
			buffer.append('\t');
		}
		IResourceDelta resourceDelta = resourceDeltas[i];
		buffer.append(resourceDelta.toString());
		buffer.append("["); //$NON-NLS-1$
		switch (resourceDelta.getKind()) {
			case IResourceDelta.ADDED :
				buffer.append('+');
				break;
			case IResourceDelta.REMOVED :
				buffer.append('-');
				break;
			case IResourceDelta.CHANGED :
				buffer.append('*');
				break;
			default :
				buffer.append('?');
				break;
		}
		buffer.append("]"); //$NON-NLS-1$
	}
	return buffer.toString();
}
/** 
 * Returns a string representation of this delta's
 * structure suitable for debug purposes.
 */
public String toString() {
	return toDebugString(0);
}
}
