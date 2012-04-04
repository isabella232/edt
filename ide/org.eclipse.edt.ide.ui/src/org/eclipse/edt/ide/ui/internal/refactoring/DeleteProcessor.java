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
package org.eclipse.edt.ide.ui.internal.refactoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteArguments;
import org.eclipse.ltk.core.refactoring.participants.ParticipantManager;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;

import com.ibm.icu.text.MessageFormat;


public final class DeleteProcessor extends org.eclipse.ltk.core.refactoring.participants.DeleteProcessor {
		
	Object[] elements;
	Change deleteChange;
	
	public DeleteProcessor(Object[] elements) {
		this.elements = elements;
	}
	
	//---- IRefactoringProcessor ---------------------------------------------------
		
	
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants shared) throws CoreException {
		List result= new ArrayList();
		for (int i = 0; i < elements.length; i++) {
			result.addAll(Arrays.asList(ParticipantManager.loadDeleteParticipants(status, 
				this, elements[i], 
				new DeleteArguments(), getNatures(), shared)));
		}
		return (RefactoringParticipant[]) result.toArray(new RefactoringParticipant[result.size()]);
	}
	
	private String[] getNatures() {
		return new String[]{"com.ibm.etools.egl.model.eglnature"};
	}
	
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException {
		pm.beginTask("", 1); //$NON-NLS-1$
		try {
			RefactoringStatus result= new RefactoringStatus();
			result.merge(RefactoringStatus.create(checkInSync(getResources())));
			return result;
		} finally {
			pm.done();
		}
	}

	private IResource[] getResources() {
		List list = new ArrayList();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof IResource) {
				list.add(elements[i]);
				continue;
			}
			if (elements[i] instanceof IEGLElement) {
				list.add(((IEGLElement)elements[i]).getResource());
			}
		}
		return (IResource[])list.toArray(new IResource[list.size()]);
		
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.base.Refactoring#checkInput(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context) throws CoreException {
		RefactoringStatus result= new RefactoringStatus();
		deleteChange = new DeleteChange(elements);

		pm.done();
		return result;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		pm.beginTask("", 1); //$NON-NLS-1$
		pm.done();
		return deleteChange;
	}

	public Object[] getElements() {
		return elements;
	}

	public String getIdentifier() {
		return "EGLDeleteProcessor";
	}

	public String getProcessorName() {
		return UINlsStrings.DeleteRefactoring_name;
	}

	public boolean isApplicable() throws CoreException {
		return elements != null && elements.length > 0;
	}
	
	/**
	 * Checks if the given resources are in sync with the underlying file
	 * system.
	 * 
	 * @param resources the resources to be checked
	 * @return IStatus status describing the check's result. If <code>status.
	 *  isOK() </code> returns <code>true</code> then the resources are in sync
	 */
	public static IStatus checkInSync(IResource[] resources) {
		IStatus result= null;
		for (int i= 0; i < resources.length; i++) {
			IResource resource= resources[i];
			if (!resource.isSynchronized(IResource.DEPTH_INFINITE)) {
				result= addOutOfSync(result, resource);
			}			
		}
		if (result != null)
			return result;
		return new Status(IStatus.OK, EDTUIPlugin.getPluginId(), IStatus.OK, "", null); //$NON-NLS-1$		
	}
	
	private static IStatus addOutOfSync(IStatus status, IResource resource) {
		IStatus entry= new Status(
			IStatus.ERROR,
			ResourcesPlugin.PI_RESOURCES,
			IResourceStatus.OUT_OF_SYNC_LOCAL,
			MessageFormat.format(UINlsStrings.Resources_outOfSync, new String[] {resource.getFullPath().toString()}), 
			null);
		if (status == null) {
			return entry;
		} else if (status.isMultiStatus()) {
			((MultiStatus)status).add(entry);
			return status;
		} else {
			MultiStatus result= new MultiStatus(
				ResourcesPlugin.PI_RESOURCES,
				IResourceStatus.OUT_OF_SYNC_LOCAL,
				UINlsStrings.Resources_outOfSyncResources, null); 
			result.add(status);
			result.add(entry);
			return result;
		}
	}

	
}
