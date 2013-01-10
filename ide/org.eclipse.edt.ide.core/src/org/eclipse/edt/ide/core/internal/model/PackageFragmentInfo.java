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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.edt.ide.core.model.EGLModelException;


/**
 * Element info for PackageFragments.
 */
class PackageFragmentInfo extends OpenableElementInfo {

	/**
	 * A array with all the non-egl resources contained by this PackageFragment
	 */
	protected Object[] fNonEGLResources;

/**
 * Create and initialize a new instance of the receiver
 */
public PackageFragmentInfo() {
	fNonEGLResources = null;
}
/**
 */
boolean containsEGLResources() {
	return fChildren.length != 0;
}
/**
 * Returns an array of non-egl resources contained in the receiver.
 */
Object[] getNonEGLResources(IResource underlyingResource, PackageFragmentRoot rootHandle) {
	Object[] nonEGLResources = fNonEGLResources;
	if (nonEGLResources == null) {
		try {
			nonEGLResources = 
				PackageFragmentRootInfo.computeFolderNonEGLResources(
					(EGLProject)rootHandle.getEGLProject(), 
					(IContainer)underlyingResource, 
					rootHandle.fullExclusionPatternChars());
		} catch (EGLModelException e) {
		}
		fNonEGLResources = nonEGLResources;
	}
	return nonEGLResources;
}
/**
 * Set the fNonEGLResources to res value
 */
synchronized void setNonEGLResources(Object[] resources) {
	fNonEGLResources = resources;
}
}
