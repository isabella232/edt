/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.refactoring.participants.ResourceModifications;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.ltk.core.refactoring.participants.ValidateEditChecker;

public abstract class RefactoringModifications {

	private ResourceModifications fResourceModifications;

	public RefactoringModifications() {
		fResourceModifications= new ResourceModifications();
	}
	
	public ResourceModifications getResourceModifications() {
		return fResourceModifications;
	}
	
	public abstract RefactoringParticipant[] loadParticipants(RefactoringStatus status, RefactoringProcessor owner, String[] natures, SharableParticipants shared);

	public abstract void buildDelta(IResourceChangeDescriptionFactory builder);
	
	public void buildValidateEdits(ValidateEditChecker checker) {
		// Default implementation does nothing.
	}

	protected void createIncludingParents(IContainer container) {
		while (container != null && !(container.exists() || getResourceModifications().willExist(container))) {
			getResourceModifications().addCreate(container);
			container= container.getParent();
		}
	}

	protected IResource[] collectResourcesOfInterest(IPackageFragment source) throws CoreException {
		IEGLElement[] children = source.getChildren();
		int childOfInterest = IEGLElement.EGL_FILE;
		if (source.getKind() == IPackageFragmentRoot.K_BINARY) {
			childOfInterest = IEGLElement.CLASS_FILE;
		}
		ArrayList result = new ArrayList(children.length);
		for (int i = 0; i < children.length; i++) {
			IEGLElement child = children[i];
			if (child.getElementType() == childOfInterest && child.getResource() != null) {
				result.add(child.getResource());
			}
		}
		return (IResource[]) result.toArray(new IResource[result.size()]);
	}
	
	protected IFile getEGLFile(IResource resource) {
		IProject project= resource.getProject();
		if (project == null)
			return null;
		IResource result= project.findMember(".eglpath"); //$NON-NLS-1$
		if (result instanceof IFile)
			return (IFile)result;
		return null;
	}
	
	protected IFile getEGLPathFile(IResource resource) {
		IProject project= resource.getProject();
		if (project == null)
			return null;
		IResource result= project.findMember(".eglpath"); //$NON-NLS-1$
		if (result instanceof IFile)
			return (IFile)result;
		return null;
	}
}
