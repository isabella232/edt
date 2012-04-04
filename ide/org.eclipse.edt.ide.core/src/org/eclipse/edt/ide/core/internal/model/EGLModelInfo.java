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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.edt.ide.core.model.IEGLModel;


/**
 * Implementation of IEGLModel. A EGL Model is specific to a
 * workspace.
 *
 * @see IEGLModel
 */
public class EGLModelInfo extends OpenableElementInfo {

	/**
	 * A array with all the non-java projects contained by this model
	 */
	Object[] nonEGLResources;

/**
 * Constructs a new EGL Model Info 
 */
protected EGLModelInfo() {
}
/**
 * Compute the non-java resources contained in this java project.
 */
private Object[] computeNonEGLResources() {
	IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
	int length = projects.length;
	Object[] nonEGLResources = null;
	int index = 0;
	for (int i = 0; i < length; i++) {
		IProject project = projects[i];
		if (!EGLProject.hasEGLNature(project)) {
			if (nonEGLResources == null) {
				nonEGLResources = new Object[length];
			}
			nonEGLResources[index++] = project;
		}
	}
	if (index == 0) return NO_NON_EGL_RESOURCES;
	if (index < length) {
		System.arraycopy(nonEGLResources, 0, nonEGLResources = new Object[index], 0, index);
	}
	return nonEGLResources;
}

/**
 * Returns an array of non-java resources contained in the receiver.
 */
Object[] getNonEGLResources() {

	Object[] nonEGLResources = this.nonEGLResources;
	if (nonEGLResources == null) {
		nonEGLResources = computeNonEGLResources();
		this.nonEGLResources = nonEGLResources;
	}
	return nonEGLResources;
}
}
