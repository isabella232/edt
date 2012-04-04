/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.refactoring.reorg;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;

class ReadOnlyResourceFinder{
	private ReadOnlyResourceFinder(){
	}

	static boolean confirmDeleteOfReadOnlyElements(IEGLElement[] eglElements, IResource[] resources, IReorgQueries queries) throws CoreException {
		String queryTitle= UINlsStrings.ReadOnlyResourceFinder_0; 
		String question= UINlsStrings.ReadOnlyResourceFinder_1; 
		return ReadOnlyResourceFinder.confirmOperationOnReadOnlyElements(queryTitle, question, eglElements, resources, queries);
	}

	static boolean confirmMoveOfReadOnlyElements(IEGLElement[] eglElements, IResource[] resources, IReorgQueries queries) throws CoreException {
		String queryTitle= UINlsStrings.ReadOnlyResourceFinder_2; 
		String question= UINlsStrings.ReadOnlyResourceFinder_3; 
		return ReadOnlyResourceFinder.confirmOperationOnReadOnlyElements(queryTitle, question, eglElements, resources, queries);
	}

	private static boolean confirmOperationOnReadOnlyElements(String queryTitle, String question, IEGLElement[] eglElements, IResource[] resources, IReorgQueries queries) throws CoreException {
		boolean hasReadOnlyResources= ReadOnlyResourceFinder.hasReadOnlyResourcesAndSubResources(eglElements, resources);
		if (hasReadOnlyResources) {
			IConfirmQuery query= queries.createYesNoQuery(queryTitle, false, IReorgQueries.CONFIRM_READ_ONLY_ELEMENTS);
			return query.confirm(question);
		}
		return true;
	}

	private static boolean hasReadOnlyResourcesAndSubResources(IEGLElement[] eglElements, IResource[] resources) throws CoreException {
		return (hasReadOnlyResourcesAndSubResources(resources)||
				  hasReadOnlyResourcesAndSubResources(eglElements));
	}

	private static boolean hasReadOnlyResourcesAndSubResources(IEGLElement[] eglElements) throws CoreException {
		for (int i= 0; i < eglElements.length; i++) {
			if (hasReadOnlyResourcesAndSubResources(eglElements[i]))
				return true;
		}
		return false;
	}

	private static boolean hasReadOnlyResourcesAndSubResources(IEGLElement eglElement) throws CoreException {
		switch(eglElement.getElementType()){
			case IEGLElement.EGL_FILE:
				IResource resource= ReorgUtils.getResource(eglElement);
				return (resource != null && ReorgUtils.isReadOnly(resource));
			case IEGLElement.PACKAGE_FRAGMENT:
				IResource packResource= ReorgUtils.getResource(eglElement);
				if (packResource == null)
					return false;
				IPackageFragment pack= (IPackageFragment)eglElement;
				if (ReorgUtils.isReadOnly(packResource))
					return true;
				return hasReadOnlyResourcesAndSubResources(pack.getChildren());
			case IEGLElement.PACKAGE_FRAGMENT_ROOT:
				IPackageFragmentRoot root= (IPackageFragmentRoot) eglElement;
				if (root.isArchive())
					return false;
				IResource pfrResource= ReorgUtils.getResource(eglElement);
				if (pfrResource == null)
					return false;
				if (ReorgUtils.isReadOnly(pfrResource))
					return true;
				return hasReadOnlyResourcesAndSubResources(root.getChildren());

			case IEGLElement.FIELD:
			case IEGLElement.IMPORT_CONTAINER:
			case IEGLElement.IMPORT_DECLARATION:
			case IEGLElement.INITIALIZER:
			case IEGLElement.FUNCTION:
			case IEGLElement.PACKAGE_DECLARATION:
			case IEGLElement.PART:
				return false;
			default: 
				Assert.isTrue(false);//not handled here
				return false;
		}
	}

	private static boolean hasReadOnlyResourcesAndSubResources(IResource[] resources) throws CoreException {
		for (int i= 0; i < resources.length; i++) {
			if (hasReadOnlyResourcesAndSubResources(resources[i]))
				return true;
		}
		return false;
	}

	private static boolean hasReadOnlyResourcesAndSubResources(IResource resource) throws CoreException {
		if (resource.isLinked()) //we don't want to count these because we never actually delete linked resources
			return false;
		if (ReorgUtils.isReadOnly(resource))
			return true;
		if (resource instanceof IContainer)
			return hasReadOnlyResourcesAndSubResources(((IContainer)resource).members());
		return false;
	}
}
