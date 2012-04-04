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
package org.eclipse.edt.ide.core.model;

import org.eclipse.core.resources.IResourceDelta;

public interface IEGLElementDelta {

	/**
	 * Status constant indicating that the element has been added.
	 * Note that an added EGL element delta has no children, as they are all implicitely added.
	 */
	public int ADDED = 1;

	/**
	 * Status constant indicating that the element has been removed.
	 * Note that a removed EGL element delta has no children, as they are all implicitely removed.
	 */
	public int REMOVED = 2;

	/**
	 * Status constant indicating that the element has been changed,
	 * as described by the change flags.
	 * 
	 * @see #getFlags
	 */
	public int CHANGED = 4;

	/**
	 * Change flag indicating that the content of the element has changed.
	 * This flag is only valid for elements which correspond to files.
	 */
	public int F_CONTENT = 0x0001;

	/**
	 * Change flag indicating that the modifiers of the element have changed.
	 * This flag is only valid if the element is an <code>IMember</code>. 
	 */
	public int F_MODIFIERS = 0x0002;
	
	/**
	 * Change flag indicating properties for given element have changed.
	 * This flag is only valid if the element is an <code>IEGLMember</code>.
	 */
	public int F_PROPERTIES = 0x0004;

	/**
	 * Change flag indicating that there are changes to the children of the element.
	 * This flag is only valid if the element is an <code>IParent</code>. 
	 */
	public int F_CHILDREN = 0x0008;

	/**
	 * Change flag indicating that the element was moved from another location.
	 * The location of the old element can be retrieved using <code>getMovedFromElement</code>.
	 */
	public int F_MOVED_FROM = 0x0010;

	/**
	 * Change flag indicating that the element was moved to another location.
	 * The location of the new element can be retrieved using <code>getMovedToElement</code>.
	 */
	public int F_MOVED_TO = 0x0020;

	/**
	 * Change flag indicating that a classpath entry corresponding to the element has been added to the project's classpath. 
	 * This flag is only valid if the element is an <code>IPackageFragmentRoot</code>.
	 */
	public int F_ADDED_TO_EGLPATH = 0x0040;

	/**
	 * Change flag indicating that a classpath entry corresponding to the element has been removed from the project's 
	 * classpath. This flag is only valid if the element is an <code>IPackageFragmentRoot</code>.
	 */
	public int F_REMOVED_FROM_EGLPATH = 0x0080;

	/**
	 * Change flag indicating that a classpath entry corresponding to the element has changed position in the project's 
	 * classpath. This flag is only valid if the element is an <code>IPackageFragmentRoot</code>.
	 * @deprecated Use F_REORDER instead.
	 */
	public int F_EGLPATH_REORDER = 0x0100;
	/**
	 * Change flag indicating that the element has changed position relatively to its siblings. 
	 * If the element is an <code>IPackageFragmentRoot</code>,  a classpath entry corresponding 
	 * to the element has changed position in the project's classpath.
	 * 
	 * @since 2.1
	 */
	public int F_REORDER = 0x0100;

	/**
	 * Change flag indicating that the underlying <code>IProject</code> has been
	 * opened. This flag is only valid if the element is an <code>IJavaProject</code>. 
	 */
	public int F_OPENED = 0x0200;

	/**
	 * Change flag indicating that the underlying <code>IProject</code> has been
	 * closed. This flag is only valid if the element is an <code>IJavaProject</code>. 
	 */
	public int F_CLOSED = 0x0400;

	
	/**
	 * Change flag indicating that one of the supertypes of an <code>IPart</code>
	 * has changed.
	 */
	public int F_SUPER_TYPES = 0x0800;
	/**
	 * Change flag indicating that the source attachment path or the source attachment root path of a classpath entry 
	 * corresponding to the element was added. This flag is only valid if the element is an 
	 * <code>IPackageFragmentRoot</code>.
	 */
	public int F_SOURCEATTACHED = 0x1000;	

	/**
	 * Change flag indicating that the source attachment path or the source attachment root path of a classpath entry 
	 * corresponding to the element was removed. This flag is only valid if the element is an 
	 * <code>IPackageFragmentRoot</code>.
	 */
	public int F_SOURCEDETACHED = 0x2000;	
	/**
	 * Change flag indicating that this is a fine-grained delta, that is, an analysis down
	 * to the members level was done to determine if there were structural changes to
	 * members.
	 * <p>
	 * Clients can use this flag to find out if a compilation unit 
     * that have a <code>F_CONTENT</code> change should assume that there are 
     * no finer grained changes (<code>F_FINE_GRAINED</code> is set) or if 
     * finer grained changes were not considered (<code>F_FINE_GRAINED</code> 
     * is not set). 
     * 
     * @since 2.0
	 */
	public int F_FINE_GRAINED = 0x4000;

	/**
	 * Change flag indicating that the element's archive content on the classpath has changed.
	 * This flag is only valid if the element is an <code>IPackageFragmentRoot</code>
	 * which is an archive.
	 * 
	 * @see IPackageFragmentRoot#isArchive
	 * @since 2.0
	 */
	public int F_ARCHIVE_CONTENT_CHANGED = 0x8000;

	/**
	 * Returns deltas for the children that have been added.
	 * @return deltas for the children that have been added
	 */
	public IEGLElementDelta[] getAddedChildren();

	/**
	 * Returns deltas for the affected (added, removed, or changed) children.
	 * @return deltas for the affected (added, removed, or changed) children
	 */
	public IEGLElementDelta[] getAffectedChildren();

	/**
	 * Returns deltas for the children which have changed.
	 * @return deltas for the children which have changed
	 */
	public IEGLElementDelta[] getChangedChildren();

	/**
	 * Returns the element that this delta describes a change to.
	 * @return the element that this delta describes a change to
	 */
	public IEGLElement getElement();

	/**
	 * Returns flags that describe how an element has changed. 
	 * Such flags should be tested using the <code>&</code> operand. For example:
	 * <pre>
	 * if ((delta.getFlags() & IJavaElementDelta.F_CONTENT) != 0) {
	 * 	// the delta indicates a content change
	 * }
	 * </pre>
	 *
	 * @return flags that describe how an element has changed
	 */
	public int getFlags();

	/**
	 * Returns the kind of this delta - one of <code>ADDED</code>, <code>REMOVED</code>,
	 * or <code>CHANGED</code>.
	 * 
	 * @return the kind of this delta
	 */
	public int getKind();

	/**
	 * Returns an element describing this element before it was moved
	 * to its current location, or <code>null</code> if the
	 * <code>F_MOVED_FROM</code> change flag is not set. 
	 * 
	 * @return an element describing this element before it was moved
	 * to its current location, or <code>null</code> if the
	 * <code>F_MOVED_FROM</code> change flag is not set
	 */
	public IEGLElement getMovedFromElement();

	/**
	 * Returns an element describing this element in its new location,
	 * or <code>null</code> if the <code>F_MOVED_TO</code> change
	 * flag is not set.
	 * 
	 * @return an element describing this element in its new location,
	 * or <code>null</code> if the <code>F_MOVED_TO</code> change
	 * flag is not set
	 */
	public IEGLElement getMovedToElement();

	/**
	 * Returns deltas for the children which have been removed.
	 * 
	 * @return deltas for the children which have been removed
	 */
	public IEGLElementDelta[] getRemovedChildren();

	/**
	 * Returns the collection of resource deltas.
	 * <p>
	 * Note that resource deltas, like Java element deltas, are generally only valid
	 * for the dynamic scope of an event notification. Clients must not hang on to
	 * these objects.
	 * </p>
	 *
	 * @return the underlying resource deltas, or <code>null</code> if none
	 */
	public IResourceDelta[] getResourceDeltas();
}
