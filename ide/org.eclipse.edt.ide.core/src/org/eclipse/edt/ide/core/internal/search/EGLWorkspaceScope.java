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
package org.eclipse.edt.ide.core.internal.search;

import java.util.HashSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.model.EGLModelManager;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLElementDelta;
import org.eclipse.edt.ide.core.model.IEGLProject;

/**
 * A EGL-specific scope for searching the entire workspace.
 * The scope can be configured to not search binaries. By default, binaries
 * are included.
 */
public class EGLWorkspaceScope extends EGLSearchScope {
	protected boolean needsInitialize;
	
public EGLWorkspaceScope() {
	EGLModelManager.getEGLModelManager().rememberScope(this);
}
public boolean encloses(IEGLElement element) {
	/*
	if (this.needsInitialize) {
		this.initialize();
	}
	return super.encloses(element);
	*/
	/*A workspace scope encloses all java elements (this assumes that the index selector
	 * and thus enclosingProjectAndJars() returns indexes on the classpath only and that these
	 * indexes are consistent.)
	 * NOTE: Returning true gains 20% of a hierarchy build on Object
	 */
	return true;
}
public boolean encloses(String resourcePathString) {
	/*
	if (this.needsInitialize) {
		this.initialize();
	}
	return super.encloses(resourcePathString);
	*/
	/*A workspace scope encloses all resources (this assumes that the index selector
	 * and thus enclosingProject() returns indexes on the eglpath only and that these
	 * indexes are consistent.)
	 * NOTE: Returning true gains 20% of a hierarchy build on Object
	 */
	return true;
}
public IPath[] enclosingProjects() {
	if (this.needsInitialize) {
		this.initialize();
	}
	return super.enclosingProjects();
}
public boolean equals(Object o) {
  return o instanceof EGLWorkspaceScope;
}
public int hashCode() {
	return EGLWorkspaceScope.class.hashCode();
}
public void initialize() {
	super.initialize();
	try {
		IEGLProject[] projects = EGLModelManager.getEGLModelManager().getEGLModel().getEGLProjects();
		for (int i = 0, length = projects.length; i < length; i++)
			this.add(projects[i], false, new HashSet(2));
	} catch (EGLModelException ignored) {
	}
	this.needsInitialize = false;
}
public void processDelta(IEGLElementDelta delta) {
	if (this.needsInitialize) return;
	IEGLElement element = delta.getElement();
	switch (element.getElementType()) {
		case IEGLElement.EGL_MODEL:
			IEGLElementDelta[] children = delta.getAffectedChildren();
			for (int i = 0, length = children.length; i < length; i++) {
				IEGLElementDelta child = children[i];
				this.processDelta(child);
			}
			break;
		case IEGLElement.EGL_PROJECT:
			int kind = delta.getKind();
			switch (kind) {
				case IEGLElementDelta.ADDED:
				case IEGLElementDelta.REMOVED:
					this.needsInitialize = true;
					break;
				case IEGLElementDelta.CHANGED:
					children = delta.getAffectedChildren();
					for (int i = 0, length = children.length; i < length; i++) {
						IEGLElementDelta child = children[i];
						this.processDelta(child);
					}
					break;
			}
			break;
		case IEGLElement.PACKAGE_FRAGMENT_ROOT:
			kind = delta.getKind();
			switch (kind) {
				case IEGLElementDelta.ADDED:
				case IEGLElementDelta.REMOVED:
					this.needsInitialize = true;
					break;
				case IEGLElementDelta.CHANGED:
					int flags = delta.getFlags();
					if ((flags & IEGLElementDelta.F_ADDED_TO_EGLPATH) > 0
						|| (flags & IEGLElementDelta.F_REMOVED_FROM_EGLPATH) > 0) {
						this.needsInitialize = true;
					}
					break;
			}
			break;
	}
}
public String toString() {
	return "EGLWorkspaceScope"; //$NON-NLS-1$
}
}
