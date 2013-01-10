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
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.IReorgPolicy.IEGLCopyPolicy;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;

import com.ibm.icu.text.MessageFormat;

public final class EGLCopyProcessor extends org.eclipse.ltk.core.refactoring.participants.CopyProcessor implements IReorgDestinationValidator {
	private INewNameQueries fNewNameQueries;
	private IReorgQueries fReorgQueries;
	private IEGLCopyPolicy fCopyPolicy;
	
	public static EGLCopyProcessor create(IResource[] resources, IEGLElement[] eglElements) throws EGLModelException{
		IEGLCopyPolicy copyPolicy= ReorgPolicyFactory.createCopyPolicy(resources, eglElements);
		if (! copyPolicy.canEnable())
			return null;
		return new EGLCopyProcessor(copyPolicy);
	}

	private EGLCopyProcessor(IEGLCopyPolicy copyPolicy) {
		fCopyPolicy= copyPolicy;
	}
	
	public String getProcessorName() {
		return UINlsStrings.CopyRefactoring_0; 
	}
	
	public String getIdentifier() {
		return this.getClass().getName();
	}
	
	public boolean isApplicable() throws CoreException {
		return fCopyPolicy.canEnable();
	}
	
	public void setNewNameQueries(INewNameQueries newNameQueries){
		Assert.isNotNull(newNameQueries);
		fNewNameQueries= newNameQueries;
	}

	public void setReorgQueries(IReorgQueries queries){
		Assert.isNotNull(queries);
		fReorgQueries= queries;
	}

	public IEGLElement[] getEGLElements() {
		return fCopyPolicy.getEGLElements();
	}

	public IResource[] getResources() {
		return fCopyPolicy.getResources();
	}
	
	public Object[] getElements() {
		IEGLElement[] jElements= fCopyPolicy.getEGLElements();
		IResource[] resources= fCopyPolicy.getResources();
		List result= new ArrayList(jElements.length + resources.length);
		result.addAll(Arrays.asList(jElements));
		result.addAll(Arrays.asList(resources));
		return result.toArray();
	}

	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException {
		RefactoringStatus result= new RefactoringStatus();
		result.merge(RefactoringStatus.create(checkInSync(ReorgUtils.getNotNulls(fCopyPolicy.getResources()))));
		IResource[] eglResources= ReorgUtils.getResources(fCopyPolicy.getEGLElements());
		result.merge(RefactoringStatus.create(checkInSync(ReorgUtils.getNotNulls(eglResources))));
		return result;
	}
	
	public Object getCommonParentForInputElements(){
		return new ParentChecker(fCopyPolicy.getResources(), fCopyPolicy.getEGLElements()).getCommonParent();
	}
	
	public RefactoringStatus setDestination(IEGLElement destination) throws EGLModelException{
		return fCopyPolicy.setDestination(destination);
	}

	public RefactoringStatus setDestination(IResource destination) throws EGLModelException{
		return fCopyPolicy.setDestination(destination);
	}
	
	public boolean canChildrenBeDestinations(IEGLElement eglElement) {
		return fCopyPolicy.canChildrenBeDestinations(eglElement);
	}
	public boolean canChildrenBeDestinations(IResource resource) {
		return fCopyPolicy.canChildrenBeDestinations(resource);
	}
	public boolean canElementBeDestination(IEGLElement eglElement) {
		return fCopyPolicy.canElementBeDestination(eglElement);
	}
	public boolean canElementBeDestination(IResource resource) {
		return fCopyPolicy.canElementBeDestination(resource);
	}
	
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context) throws CoreException {
		Assert.isNotNull(fNewNameQueries, "Missing new name queries"); //$NON-NLS-1$
		Assert.isNotNull(fReorgQueries, "Missing reorg queries"); //$NON-NLS-1$
		pm.beginTask("", 2); //$NON-NLS-1$
		RefactoringStatus result= fCopyPolicy.checkFinalConditions(new SubProgressMonitor(pm, 1), context, fReorgQueries);
		result.merge(context.check(new SubProgressMonitor(pm, 1)));
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.base.IRefactoring#createChange(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Change createChange(IProgressMonitor pm) throws CoreException {
		Assert.isNotNull(fNewNameQueries);
		Assert.isTrue(fCopyPolicy.getEGLElementDestination() == null || fCopyPolicy.getResourceDestination() == null);
		Assert.isTrue(fCopyPolicy.getEGLElementDestination() != null || fCopyPolicy.getResourceDestination() != null);		
		try {
			final CompositeChange result= new CompositeChange(getChangeName()) {
				public Change perform(IProgressMonitor pm2) throws CoreException {
					return super.perform(pm2);					
				}				
			};
			Change change= fCopyPolicy.createChange(pm, fNewNameQueries);
			if (change instanceof CompositeChange){
				CompositeChange subComposite= (CompositeChange)change;
				result.merge(subComposite);
			} else{
				result.add(change);
			}
			return result;		
		} finally {
			pm.done();
		}
	}

	private String getChangeName() {
		return UINlsStrings.EGLCopyProcessor_change_name; 
	}
	
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants sharedParticipants) throws CoreException {
		RefactoringParticipant[] result= fCopyPolicy.loadParticipants(status, this, getAffectedProjectNatures(), sharedParticipants);
		return result;
	}
	
	private String[] getAffectedProjectNatures() throws CoreException {
		return new String[]{"org.eclipse.edt.ide.core.eglnature"};
	}
	
	/**
	 * Checks if the given resource is in sync with the underlying file system.
	 * 
	 * @param resource the resource to be checked
	 * @return IStatus status describing the check's result. If <code>status.
	 * isOK()</code> returns <code>true</code> then the resource is in sync
	 */
	public static IStatus checkInSync(IResource resource) {
		return checkInSync(new IResource[] {resource});
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
