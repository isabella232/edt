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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLElementDelta;
import org.eclipse.edt.ide.core.model.IParent;

import org.eclipse.edt.compiler.internal.core.utils.CharOperation;

/**
 * A java element delta biulder creates a java element delta on
 * a java element between the version of the java element
 * at the time the comparator was created and the current version 
 * of the java element.
 *
 * It performs this operation by locally caching the contents of 
 * the java element when it is created. When the method
 * createDeltas() is called, it creates a delta over the cached 
 * contents and the new contents.
 */
public class EGLElementDeltaBuilder {
	/**
	 * The java element handle
	 */
	IEGLElement eglElement;

	/**
	 * The maximum depth in the java element children we should look into
	 */
	int maxDepth = Integer.MAX_VALUE;

	/**
	 * The old handle to info relationships
	 */
	Map infos;

	/**
	 * The old position info
	 */
	Map oldPositions;

	/**
	 * The new position info
	 */
	Map newPositions;

	/**
	 * Change delta
	 */
	EGLElementDelta delta;

	/**
	 * List of added elements
	 */
	ArrayList added;

	/**
	 * List of removed elements
	 */
	ArrayList removed;
	
	/**
	 * Doubly linked list item
	 */
	class ListItem {
		public IEGLElement previous;
		public IEGLElement next;

		public ListItem(IEGLElement previous, IEGLElement next) {
			this.previous = previous;
			this.next = next;
		}
	}
/**
 * Creates a java element comparator on a java element
 * looking as deep as necessary.
 */
public EGLElementDeltaBuilder(IEGLElement eglElement) {
	this.eglElement = eglElement;
	this.initialize();
	this.recordElementInfo(
		eglElement, 
		(EGLModel)this.eglElement.getEGLModel(),
		0);
}
/**
 * Creates a java element comparator on a java element
 * looking only 'maxDepth' levels deep.
 */
public EGLElementDeltaBuilder(IEGLElement eglElement, int maxDepth) {
	this.eglElement = eglElement;
	this.maxDepth = maxDepth;
	this.initialize();
	this.recordElementInfo(
		eglElement, 
		(EGLModel)this.eglElement.getEGLModel(),
		0);
}
/**
 * Repairs the positioning information
 * after an element has been added
 */
private void added(IEGLElement element) {
	this.added.add(element);
	ListItem current = this.getNewPosition(element);
	ListItem previous = null, next = null;
	if (current.previous != null)
		previous = this.getNewPosition(current.previous);
	if (current.next != null)
		next = this.getNewPosition(current.next);
	if (previous != null)
		previous.next = current.next;
	if (next != null)
		next.previous = current.previous;
}
/**
 * Builds the java element deltas between the old content of the compilation
 * unit and its new content.
 */
public void buildDeltas() {
	this.recordNewPositions(this.eglElement, 0);
	this.findAdditions(this.eglElement, 0);
	this.findDeletions();
	this.findChangesInPositioning(this.eglElement, 0);
	this.trimDelta(this.delta);
	if (this.delta.getAffectedChildren().length == 0) {
		// this is a fine grained but not children affected -> mark as content changed
		this.delta.contentChanged();
	}
}
/**
 * Finds elements which have been added or changed.
 */
private void findAdditions(IEGLElement newElement, int depth) {
	EGLElementInfo oldInfo = this.getElementInfo(newElement);
	if (oldInfo == null && depth < this.maxDepth) {
		this.delta.added(newElement);
		added(newElement);
	} else {
		this.removeElementInfo(newElement);
	}
	
	if (depth >= this.maxDepth) {
		// mark element as changed
		this.delta.changed(newElement, IEGLElementDelta.F_CONTENT);
		return;
	}

	EGLElementInfo newInfo = null;
	try { 
		newInfo = (EGLElementInfo)((EGLElement)newElement).getElementInfo();
	} catch (EGLModelException npe) {
		return;
	}
	
	this.findContentChange(oldInfo, newInfo, newElement);
		
	if (oldInfo != null && newElement instanceof IParent) {

		IEGLElement[] children = newInfo.getChildren();
		if (children != null) {
			int length = children.length;
			for(int i = 0; i < length; i++) {
				this.findAdditions(children[i], depth + 1);
			}
		}		
	}
}
/**
 * Looks for changed positioning of elements.
 */
private void findChangesInPositioning(IEGLElement element, int depth) {
	if (depth >= this.maxDepth || this.added.contains(element) || this.removed.contains(element))
		return;
		
	if (!isPositionedCorrectly(element)) {
		this.delta.changed(element, IEGLElementDelta.F_REORDER);
	} 
	
	if (element instanceof IParent) {
		EGLElementInfo info = null;
		try { 
			info = (EGLElementInfo)((EGLElement)element).getElementInfo();
		} catch (EGLModelException npe) {
			return;
		}

		IEGLElement[] children = info.getChildren();
		if (children != null) {
			int length = children.length;
			for(int i = 0; i < length; i++) {
				this.findChangesInPositioning(children[i], depth + 1);
			}
		}		
	}
}
/**
 * The elements are equivalent, but might have content changes.
 */
private void findContentChange(EGLElementInfo oldInfo, EGLElementInfo newInfo, IEGLElement newElement) {
	if (oldInfo instanceof MemberElementInfo && newInfo instanceof MemberElementInfo) {
		// Check if changed between public/private
		if (((MemberElementInfo)oldInfo).getModifiers() != ((MemberElementInfo)newInfo).getModifiers()) {
			this.delta.changed(newElement, IEGLElementDelta.F_MODIFIERS);
		} else if (oldInfo instanceof SourceFunctionElementInfo && newInfo instanceof SourceFunctionElementInfo) {
			if (!CharOperation.equals(
					((SourceFunctionElementInfo)oldInfo).getReturnTypeName(), 
					((SourceFunctionElementInfo)newInfo).getReturnTypeName())) {
				this.delta.changed(newElement, IEGLElementDelta.F_CONTENT);
			}
		} else if (oldInfo instanceof SourceFieldElementInfo && newInfo instanceof SourceFieldElementInfo) {
			// TODO Handle changes in properties as content change
			if (!CharOperation.equals(
					((SourceFieldElementInfo)oldInfo).getTypeName(), 
					((SourceFieldElementInfo)newInfo).getTypeName())) {
				this.delta.changed(newElement, IEGLElementDelta.F_CONTENT);
			}
		}
	}
	if (oldInfo instanceof SourcePartElementInfo && newInfo instanceof SourcePartElementInfo) {
		SourcePartElementInfo oldSourcePartInfo = (SourcePartElementInfo)oldInfo;
		SourcePartElementInfo newSourcePartInfo = (SourcePartElementInfo)newInfo;
		if (oldSourcePartInfo.getContentHashCode() != newSourcePartInfo.getContentHashCode()) { 
			this.delta.changed(newElement, IEGLElementDelta.F_CONTENT);
		}
	}
}
/**
 * Adds removed deltas for any handles left in the table
 */
private void findDeletions() {
	Iterator iter = this.infos.keySet().iterator();
	while(iter.hasNext()) {
		IEGLElement element = (IEGLElement)iter.next();
		this.delta.removed(element);
		this.removed(element);
	}
}
private EGLElementInfo getElementInfo(IEGLElement element) {
	return (EGLElementInfo)this.infos.get(element);
}
private ListItem getNewPosition(IEGLElement element) {
	return (ListItem)this.newPositions.get(element);
}
private ListItem getOldPosition(IEGLElement element) {
	return (ListItem)this.oldPositions.get(element);
}
private void initialize() {
	this.infos = new HashMap(20);
	this.oldPositions = new HashMap(20);
	this.newPositions = new HashMap(20);
	this.putOldPosition(this.eglElement, new ListItem(null, null));
	this.putNewPosition(this.eglElement, new ListItem(null, null));
	this.delta = new EGLElementDelta(eglElement);
	
	// if building a delta on a compilation unit or below, 
	// it's a fine grained delta
	if (eglElement.getElementType() >= IEGLElement.EGL_FILE) {
		this.delta.fineGrained();
	}
	
	this.added = new ArrayList(5);
	this.removed = new ArrayList(5);
}
/**
 * Inserts position information for the elements into the new or old positions table
 */
private void insertPositions(IEGLElement[] elements, boolean isNew) {
	int length = elements.length;
	IEGLElement previous = null, current = null, next = (length > 0) ? elements[0] : null;
	for(int i = 0; i < length; i++) {
		previous = current;
		current = next;
		next = (i + 1 < length) ? elements[i + 1] : null;
		if (isNew) {
			this.putNewPosition(current, new ListItem(previous, next));
		} else {
			this.putOldPosition(current, new ListItem(previous, next));
		}
	}
}
/**
 * Returns whether the elements position has not changed.
 */
private boolean isPositionedCorrectly(IEGLElement element) {
	ListItem oldListItem = this.getOldPosition(element);
	if (oldListItem == null) return false;
	
	ListItem newListItem = this.getNewPosition(element);
	if (newListItem == null) return false;
	
	IEGLElement oldPrevious = oldListItem.previous;
	IEGLElement newPrevious = newListItem.previous;
	if (oldPrevious == null) {
		return newPrevious == null;
	} else {
		return oldPrevious.equals(newPrevious);
	}
}
private void putElementInfo(IEGLElement element, EGLElementInfo info) {
	this.infos.put(element, info);
}
private void putNewPosition(IEGLElement element, ListItem position) {
	this.newPositions.put(element, position);
}
private void putOldPosition(IEGLElement element, ListItem position) {
	this.oldPositions.put(element, position);
}
/**
 * Records this elements info, and attempts
 * to record the info for the children.
 */
private void recordElementInfo(IEGLElement element, EGLModel model, int depth) {
	if (depth >= this.maxDepth) {
		return;
	}
	EGLElementInfo info = (EGLElementInfo)EGLModelManager.getEGLModelManager().getInfo(element);
	if (info == null) // no longer in the java model.
		return;
	this.putElementInfo(element, info);
		
	if (element instanceof IParent) {
		IEGLElement[] children = info.getChildren();
		if (children != null) {
			insertPositions(children, false);
			for(int i = 0, length = children.length; i < length; i++)
				recordElementInfo(children[i], model, depth + 1);
		}
	}
}
/**
 * Fills the newPositions hashtable with the new position information
 */
private void recordNewPositions(IEGLElement newElement, int depth) {
	if (depth < this.maxDepth && newElement instanceof IParent) {
		EGLElementInfo info = null;
		try { 
			info = (EGLElementInfo)((EGLElement)newElement).getElementInfo();
		} catch (EGLModelException npe) {
			return;
		}

		IEGLElement[] children = info.getChildren();
		if (children != null) {
			insertPositions(children, true);
			for(int i = 0, length = children.length; i < length; i++) {
				recordNewPositions(children[i], depth + 1);
			}
		}
	}
}
/**
 * Repairs the positioning information
 * after an element has been removed
 */
private void removed(IEGLElement element) {
	this.removed.add(element);
	ListItem current = this.getOldPosition(element);
	ListItem previous = null, next = null;
	if (current.previous != null)
		previous = this.getOldPosition(current.previous);
	if (current.next != null)
		next = this.getOldPosition(current.next);
	if (previous != null)
		previous.next = current.next;
	if (next != null)
		next.previous = current.previous;
	
}
private void removeElementInfo(IEGLElement element) {
	this.infos.remove(element);
}
public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("Built delta:\n"); //$NON-NLS-1$
	buffer.append(this.delta.toString());
	return buffer.toString();
}
/**
 * Trims deletion deltas to only report the highest level of deletion
 */
private void trimDelta(EGLElementDelta delta) {
	if (delta.getKind() == IEGLElementDelta.REMOVED) {
		IEGLElementDelta[] children = delta.getAffectedChildren();
		for(int i = 0, length = children.length; i < length; i++) {
			delta.removeAffectedChild((EGLElementDelta)children[i]);
		}
	} else {
		IEGLElementDelta[] children = delta.getAffectedChildren();
		for(int i = 0, length = children.length; i < length; i++) {
			trimDelta((EGLElementDelta)children[i]);
		}
	}
}
}
