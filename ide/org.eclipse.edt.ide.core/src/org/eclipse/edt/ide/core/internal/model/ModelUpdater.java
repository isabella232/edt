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

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLElementDelta;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IWorkingCopy;


/**
 * This class is used by <code>EGLModelManager</code> to update the EGLModel
 * based on some <code>IEGLElementDelta</code>s.
 */
public class ModelUpdater {

	HashSet projectsToUpdate = new HashSet();

	/**
	 * Adds the given child handle to its parent's cache of children. 
	 */
	protected void addToParentInfo(Openable child) {

		Openable parent = (Openable) child.getParent();
		if (parent != null && parent.isOpen()) {
			try {
				EGLElementInfo info = (EGLElementInfo)parent.getElementInfo();
				info.addChild(child);
			} catch (EGLModelException e) {
				// do nothing - we already checked if open
			}
		}
	}

	/**
	 * Closes the given element, which removes it from the cache of open elements.
	 */
	protected static void close(Openable element) {

		try {
			element.close();
		} catch (EGLModelException e) {
			// do nothing
		}
	}

	/**
	 * Processing for an element that has been added:<ul>
	 * <li>If the element is a project, do nothing, and do not process
	 * children, as when a project is created it does not yet have any
	 * natures - specifically a java nature.
	 * <li>If the elemet is not a project, process it as added (see
	 * <code>basicElementAdded</code>.
	 * </ul>
	 */
	protected void elementAdded(Openable element) {

		int elementType = element.getElementType();
		if (elementType == IEGLElement.EGL_PROJECT) {
			// project add is handled by EGLProject.configure() because
			// when a project is created, it does not yet have a java nature
			addToParentInfo(element);
			this.projectsToUpdate.add(element);
		} else {
			addToParentInfo(element);

			// Force the element to be closed as it might have been opened 
			// before the resource modification came in and it might have a new child
			// For example, in an IWorkspaceRunnable:
			// 1. create a package fragment p using a java model operation
			// 2. open package p
			// 3. add file X.java in folder p
			// When the resource delta comes in, only the addition of p is notified, 
			// but the package p is already opened, thus its children are not recomputed
			// and it appears empty.
			close(element);
		}

		switch (elementType) {
			case IEGLElement.PACKAGE_FRAGMENT_ROOT :
				// when a root is added, and is on the eglpath, the project must be updated
				this.projectsToUpdate.add(element.getEGLProject());
				break;
			case IEGLElement.PACKAGE_FRAGMENT :
				// get rid of namelookup since it holds onto obsolete cached info 
				EGLProject project = (EGLProject) element.getEGLProject();
				try {
					project.getEGLProjectElementInfo().setNameLookup(null);
				} catch (EGLModelException e) {
				}
				break;
		}
	}

	/**
	 * Generic processing for elements with changed contents:<ul>
	 * <li>The element is closed such that any subsequent accesses will re-open
	 * the element reflecting its new structure.
	 * </ul>
	 */
	protected void elementChanged(Openable element) {

		close(element);
	}

	/**
	 * Generic processing for a removed element:<ul>
	 * <li>Close the element, removing its structure from the cache
	 * <li>Remove the element from its parent's cache of children
	 * <li>Add a REMOVED entry in the delta
	 * </ul>
	 */
	protected void elementRemoved(Openable element) {

		if (element.isOpen()) {
			close(element);
		}
		removeFromParentInfo(element);
		int elementType = element.getElementType();

		switch (elementType) {
			case IEGLElement.EGL_MODEL :
				EGLModelManager.getEGLModelManager().getIndexManager().reset();
				break;
			case IEGLElement.EGL_PROJECT :
				EGLModelManager.getEGLModelManager().removePerProjectInfo(
					(EGLProject) element);
				break;
			case IEGLElement.PACKAGE_FRAGMENT_ROOT :
				this.projectsToUpdate.add(element.getEGLProject());
				break;
			case IEGLElement.PACKAGE_FRAGMENT :
				//1G1TW2T - get rid of namelookup since it holds onto obsolete cached info 
				EGLProject project = (EGLProject) element.getEGLProject();
				try {
					project.getEGLProjectElementInfo().setNameLookup(null);
				} catch (EGLModelException e) {
				}
				break;
		}
	}

	/**
	 * Converts a <code>IResourceDelta</code> rooted in a <code>Workspace</code> into
	 * the corresponding set of <code>IEGLElementDelta</code>, rooted in the
	 * relevant <code>EGLModel</code>s.
	 */
	public void processEGLDelta(IEGLElementDelta delta) {

//		if (DeltaProcessor.VERBOSE){
//			System.out.println("UPDATING Model with Delta: ["+Thread.currentThread()+":" + delta + "]:");//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
//		}

		try {
			this.traverseDelta(delta, null, null); // traverse delta

			// update package fragment roots of projects that were affected
			Iterator iterator = this.projectsToUpdate.iterator();
			while (iterator.hasNext()) {
				EGLProject project = (EGLProject) iterator.next();
				project.updatePackageFragmentRoots();
			}
		} finally {
			this.projectsToUpdate = new HashSet();
		}
	}

	/**
	 * Removes the given element from its parents cache of children. If the
	 * element does not have a parent, or the parent is not currently open,
	 * this has no effect. 
	 */
	protected void removeFromParentInfo(Openable child) {

		Openable parent = (Openable) child.getParent();
		if (parent != null && parent.isOpen()) {
			try {
				EGLElementInfo info = (EGLElementInfo)parent.getElementInfo();
				info.removeChild(child);
			} catch (EGLModelException e) {
				// do nothing - we already checked if open
			}
		}
	}

	/**
	 * Converts an <code>IResourceDelta</code> and its children into
	 * the corresponding <code>IEGLElementDelta</code>s.
	 * Return whether the delta corresponds to a resource on the eglpath.
	 * If it is not a resource on the eglpath, it will be added as a non-egl
	 * resource by the sender of this method.
	 */
	protected void traverseDelta(
		IEGLElementDelta delta,
		IPackageFragmentRoot root,
		IEGLProject project) {

		boolean processChildren = true;

		Openable element = (Openable) delta.getElement();
		switch (element.getElementType()) {
			case IEGLElement.EGL_PROJECT :
				project = (IEGLProject) element;
				break;
			case IEGLElement.PACKAGE_FRAGMENT_ROOT :
				root = (IPackageFragmentRoot) element;
				break;
			case IEGLElement.EGL_FILE :
				// filter out working copies (we don't want to add/remove them to/from the package fragment
				if (((IWorkingCopy)element).isWorkingCopy()) {
					return;
				}
				processChildren = false;
		}

		switch (delta.getKind()) {
			case IEGLElementDelta.ADDED :
				elementAdded(element);
				break;
			case IEGLElementDelta.REMOVED :
				elementRemoved(element);
				break;
			case IEGLElementDelta.CHANGED :
				if ((delta.getFlags() & IEGLElementDelta.F_CONTENT) != 0){
					elementChanged(element);
				}
				break;
		}
		if (processChildren) {
			IEGLElementDelta[] children = delta.getAffectedChildren();
			for (int i = 0; i < children.length; i++) {
				IEGLElementDelta childDelta = children[i];
				this.traverseDelta(childDelta, root, project);
			}
		}
	}
}
