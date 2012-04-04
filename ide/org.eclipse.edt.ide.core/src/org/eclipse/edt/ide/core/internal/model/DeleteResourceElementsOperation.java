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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IOpenable;
import org.eclipse.edt.ide.core.model.IPackageFragment;


/**
 * @author mattclem
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/**
 * This operation deletes a collection of resources and all of their children.
 * It does not delete resources which do not belong to the Java Model
 * (eg GIF files).
 */
public class DeleteResourceElementsOperation extends MultiOperation {

	/**
	 * When executed, this operation will delete the given elements. The elements
	 * to delete cannot be <code>null</code> or empty, and must have a corresponding
	 * resource.
	 */
	protected DeleteResourceElementsOperation(IEGLElement[] elementsToProcess, boolean force) {
		super(elementsToProcess, force);
	}
	/**
	 * Deletes the direct children of <code>frag</code> corresponding to its kind
	 * (K_SOURCE or K_BINARY), and deletes the corresponding folder if it is then
	 * empty.
	 */
	private void deletePackageFragment(IPackageFragment frag)
		throws EGLModelException {
		IResource res = frag.getResource();
		if (res != null && res.getType() == IResource.FOLDER) {
			// collect the children to remove
			IEGLElement[] childrenOfInterest = frag.getChildren();
			if (childrenOfInterest.length > 0) {
				IResource[] resources = new IResource[childrenOfInterest.length];
				// remove the children
				for (int i = 0; i < childrenOfInterest.length; i++) {
					resources[i] = childrenOfInterest[i].getCorrespondingResource();
				}
				deleteResources(resources, fForce);
			}

			// Discard non-egl resources
			Object[] nonEGLResources = frag.getNonEGLResources();
			int actualResourceCount = 0;
			for (int i = 0, max = nonEGLResources.length; i < max; i++){
				if (nonEGLResources[i] instanceof IResource) actualResourceCount++;
			}
			IResource[] actualNonJavaResources = new IResource[actualResourceCount];
			for (int i = 0, max = nonEGLResources.length, index = 0; i < max; i++){
				if (nonEGLResources[i] instanceof IResource) actualNonJavaResources[index++] = (IResource)nonEGLResources[i];
			}
			deleteResources(actualNonJavaResources, fForce);
		
			// delete remaining files in this package (.class file in the case where Proj=src=bin)
			IResource[] remainingFiles;
			try {
				remainingFiles = ((IFolder) res).members();
			} catch (CoreException ce) {
				throw new EGLModelException(ce);
			}
			boolean isEmpty = true;
			for (int i = 0, length = remainingFiles.length; i < length; i++) {
				IResource file = remainingFiles[i];
				if (file instanceof IFile) {
					this.deleteResource(file, IResource.FORCE | IResource.KEEP_HISTORY);
				}
				else
				{
					if(!file.equals(res))	//if not the current folder either
						isEmpty = false;
				}
			}
			if (isEmpty && !frag.isDefaultPackage()) {
				// delete recursively empty folders
				IResource fragResource =  frag.getResource();
				if (fragResource != null) {
					deleteEmptyPackageFragment(frag, false, fragResource.getParent());
				}
			}
		}
	}
	/**
	 * @see MultiOperation
	 */
	protected String getMainTaskName() {
		return EGLModelResources.operationDeleteResourceProgress;
	}
	/**
	 * @see MultiOperation. This method delegate to <code>deleteResource</code> or
	 * <code>deletePackageFragment</code> depending on the type of <code>element</code>.
	 */
	protected void processElement(IEGLElement element) throws EGLModelException {
		switch (element.getElementType()) {
			case IEGLElement.CLASS_FILE :
			case IEGLElement.EGL_FILE :
				deleteResource(element.getResource(), fForce ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY);
				break;
			case IEGLElement.PACKAGE_FRAGMENT :
				deletePackageFragment((IPackageFragment) element);
				break;
			default :
				throw new EGLModelException(new EGLModelStatus(EGLModelStatus.INVALID_ELEMENT_TYPES, element));
		}
		// ensure the element is closed
		if (element instanceof IOpenable) {
			((IOpenable)element).close();
		}
	}
	/**
	 * @see MultiOperation
	 */
	protected void verify(IEGLElement element) throws EGLModelException {
		if (element == null || !element.exists())
			error(EGLModelStatus.ELEMENT_DOES_NOT_EXIST, element);

		int type = element.getElementType();
		if (type <= IEGLElement.PACKAGE_FRAGMENT_ROOT || type > IEGLElement.EGL_FILE)
			error(EGLModelStatus.INVALID_ELEMENT_TYPES, element);
//		else if (type == IEGLElement.PACKAGE_FRAGMENT && element instanceof JarPackageFragment)
//			error(EGLModelStatus.INVALID_ELEMENT_TYPES, element);
		IResource resource = element.getResource();
		if (resource instanceof IFolder) {
			if (resource.isLinked()) {
				error(EGLModelStatus.INVALID_RESOURCE, element);
			}
		}
	}
}
