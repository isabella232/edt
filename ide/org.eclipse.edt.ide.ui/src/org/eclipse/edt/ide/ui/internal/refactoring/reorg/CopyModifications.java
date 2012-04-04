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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.refactoring.participants.ResourceModifications;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CopyArguments;
import org.eclipse.ltk.core.refactoring.participants.IParticipantDescriptorFilter;
import org.eclipse.ltk.core.refactoring.participants.ParticipantManager;
import org.eclipse.ltk.core.refactoring.participants.RefactoringArguments;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;

public class CopyModifications extends RefactoringModifications {
	
	private List fCopies;
	private List fCopyArguments;
	private List fParticipantDescriptorFilter;
	
	public CopyModifications() {
		fCopies= new ArrayList();
		fCopyArguments= new ArrayList();
		fParticipantDescriptorFilter= new ArrayList();
	}
	
	public void copy(IResource resource, CopyArguments args) {
		add(resource, args, null);
	}
	
	public void copy(IEGLElement element, CopyArguments eglArgs, CopyArguments resourceArgs) throws CoreException {
		switch(element.getElementType()) {
			case IEGLElement.PACKAGE_FRAGMENT_ROOT:
				copy((IPackageFragmentRoot)element, eglArgs, resourceArgs);
				break;
			case IEGLElement.PACKAGE_FRAGMENT:
				copy((IPackageFragmentRoot)element, eglArgs, resourceArgs);
				break;
			case IEGLElement.EGL_FILE:
				copy((IEGLFile)element, eglArgs, resourceArgs);
				break;
			default:
				add(element, eglArgs, null);
		}
	}

	public void copy(IPackageFragmentRoot sourceFolder, CopyArguments eglArgs, CopyArguments resourceArgs) {
		add(sourceFolder, eglArgs, null);
		add(sourceFolder.getResource(), resourceArgs, null);
		
		IResource sourceResource= sourceFolder.getResource();
		if (sourceResource != null) {
			getResourceModifications().addCopyDelta(sourceResource, resourceArgs);
			IFile classpath= getEGLPathFile((IResource) resourceArgs.getDestination());
			if (classpath != null) {
				getResourceModifications().addChanged(classpath);
			}
		}
	}
	
	public void copy(IPackageFragment pack, CopyArguments eglArgs, CopyArguments resourceArgs) throws CoreException {
		add(pack, eglArgs, null);
		add(pack.getResource(), resourceArgs, null);

		IPackageFragmentRoot eglDestination= (IPackageFragmentRoot) eglArgs.getDestination();
		if (eglDestination.getResource() == null)
			return;
		IPackageFragment newPack= eglDestination.getPackageFragment(pack.getElementName());
		// Here we have a special case. When we copy a package into the same source
		// folder than the user will choose an "unused" name at the end which will
		// lead to the fact that we can copy the pack. Unfortunately we don't know
		// the new name yet, so we use the current package name.
		if (!pack.hasSubpackages() && (!newPack.exists() || pack.equals(newPack))) {
			// we can do a simple move
			IContainer resourceDestination= newPack.getResource().getParent();
			createIncludingParents(resourceDestination);
			getResourceModifications().addCopyDelta(pack.getResource(), resourceArgs);
		} else {
			IContainer resourceDestination= (IContainer) newPack.getResource();
			createIncludingParents(resourceDestination);
			CopyArguments arguments= new CopyArguments(resourceDestination, resourceArgs.getExecutionLog());
			IResource[] resourcesToCopy= collectResourcesOfInterest(pack);
			for (int i= 0; i < resourcesToCopy.length; i++) {
				IResource toCopy= resourcesToCopy[i];
				getResourceModifications().addCopyDelta(toCopy, arguments);
			}
		}
	}

	public void copy(IEGLFile unit, CopyArguments eglArgs, CopyArguments resourceArgs) throws CoreException {
		add(unit, eglArgs, null);
		add(unit.getResource(), resourceArgs, null);

		if (unit.getResource() != null) {
			getResourceModifications().addCopyDelta(unit.getResource(), resourceArgs);
		}
	}

	public void buildDelta(IResourceChangeDescriptionFactory builder) {
		for (int i= 0; i < fCopies.size(); i++) {
			Object element= fCopies.get(i);
			if (element instanceof IResource) {
				ResourceModifications.buildCopyDelta(builder, (IResource) element, (CopyArguments) fCopyArguments.get(i));
			}
		}
		getResourceModifications().buildDelta(builder);
	}
	
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status, RefactoringProcessor owner, String[] natures, SharableParticipants shared) {
		List result= new ArrayList();
		for (int i= 0; i < fCopies.size(); i++) {
			result.addAll(Arrays.asList(ParticipantManager.loadCopyParticipants(status, 
				owner, fCopies.get(i), 
				(CopyArguments) fCopyArguments.get(i), 
				(IParticipantDescriptorFilter) fParticipantDescriptorFilter.get(i), 
				natures, shared)));
		}
		result.addAll(Arrays.asList(getResourceModifications().getParticipants(status, owner, natures, shared)));
		return (RefactoringParticipant[]) result.toArray(new RefactoringParticipant[result.size()]);
	}
	
	private void add(Object element, RefactoringArguments args, IParticipantDescriptorFilter filter) {
		Assert.isNotNull(element);
		Assert.isNotNull(args);
		fCopies.add(element);
		fCopyArguments.add(args);
		fParticipantDescriptorFilter.add(filter);
	}
} 
