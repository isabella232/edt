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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.IReorgPolicy.IEGLMovePolicy;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringArguments;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;

import com.ibm.icu.text.MessageFormat;

public final class MoveProcessor extends org.eclipse.ltk.core.refactoring.participants.MoveProcessor implements IReorgDestinationValidator {
	private IReorgQueries fReorgQueries;
	private IEGLMovePolicy fMovePolicy;
	private ICreateTargetQueries fCreateTargetQueries;
	private boolean fWasCanceled;
	private String fComment;
	
	public MoveProcessor(IEGLMovePolicy policy) {
		fMovePolicy= policy;
	}
	
	protected Object getDestination() {
		IEGLElement je= fMovePolicy.getEGLElementDestination();
		if (je != null)
			return je;
		return fMovePolicy.getResourceDestination();
	}
	
	public Object[] getElements() {
		List result= new ArrayList();
		result.addAll(Arrays.asList(fMovePolicy.getEGLElements()));
		result.addAll(Arrays.asList(fMovePolicy.getResources()));
		return result.toArray();
	}

	public String getIdentifier() {
		return this.getClass().getName();
	}

	public boolean isApplicable() throws CoreException {
		return fMovePolicy.canEnable();
	}
	
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants shared) throws CoreException {
		return fMovePolicy.loadParticipants(status, this, getAffectedProjectNatures(), shared);
	}

	protected String[] getAffectedProjectNatures() throws CoreException {
		return new String[]{"com.ibm.etools.egl.model.eglnature"};
	}

	public boolean wasCanceled() {
		return fWasCanceled;
	}

	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException {
		pm.beginTask("", 1); //$NON-NLS-1$
		try {
			RefactoringStatus result= new RefactoringStatus();
			result.merge(RefactoringStatus.create(checkInSync(ReorgUtils.getNotNulls(fMovePolicy.getResources()))));
			IResource[] eglResources= ReorgUtils.getResources(fMovePolicy.getEGLElements());
			result.merge(RefactoringStatus.create(checkInSync(ReorgUtils.getNotNulls(eglResources))));
			return result;
		} finally {
			pm.done();
		}
	}

	public Object getCommonParentForInputElements(){
		return new ParentChecker(fMovePolicy.getResources(), fMovePolicy.getEGLElements()).getCommonParent();
	}
	
	public IEGLElement[] getEGLElements() {
		return fMovePolicy.getEGLElements();
	}
	
	public IResource[] getResources() {
		return fMovePolicy.getResources();
	}

	public RefactoringStatus setDestination(IEGLElement destination) throws EGLModelException{
		return fMovePolicy.setDestination(destination);
	}

	public RefactoringStatus setDestination(IResource destination) throws EGLModelException{
		return fMovePolicy.setDestination(destination);
	}
	
	public boolean canChildrenBeDestinations(IEGLElement eglElement) {
		return fMovePolicy.canChildrenBeDestinations(eglElement);
	}
	public boolean canChildrenBeDestinations(IResource resource) {
		return fMovePolicy.canChildrenBeDestinations(resource);
	}
	public boolean canElementBeDestination(IEGLElement eglElement) {
		return fMovePolicy.canElementBeDestination(eglElement);
	}
	public boolean canElementBeDestination(IResource resource) {
		return fMovePolicy.canElementBeDestination(resource);
	}
	
	public void setReorgQueries(IReorgQueries queries){
		Assert.isNotNull(queries);
		fReorgQueries= queries;
	}

	public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context) throws CoreException {
		try{
			Assert.isNotNull(fReorgQueries);
			fWasCanceled= false;
			return fMovePolicy.checkFinalConditions(pm, context, fReorgQueries);
		} catch (OperationCanceledException e) {
			fWasCanceled= true;
			throw e;
		}
	}

	public Change createChange(IProgressMonitor pm) throws CoreException {
		Assert.isTrue(fMovePolicy.getEGLElementDestination() == null || fMovePolicy.getResourceDestination() == null);
		Assert.isTrue(fMovePolicy.getEGLElementDestination() != null || fMovePolicy.getResourceDestination() != null);		
		try {
			final CompositeChange result = new CompositeChange(UINlsStrings.EGLMoveProcessor_change_name);
			Change change= fMovePolicy.createChange(pm);
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
	
	public Change postCreateChange(Change[] participantChanges, IProgressMonitor pm) throws CoreException {
		return fMovePolicy.postCreateChange(participantChanges, pm);
	}

	public String getProcessorName() {
		return UINlsStrings.MoveRefactoring_0; 
	}
	
	public boolean canUpdateReferences(){
		return fMovePolicy.canUpdateReferences();
	}
	
	public void setUpdateReferences(boolean update){
		fMovePolicy.setUpdateReferences(update);
	}
	
	public boolean getUpdateReferences() {
		if (!canUpdateReferences())
			return false;
		return fMovePolicy.getUpdateReferences();
	}
	
	public boolean canUpdateQualifiedNames() {
		return fMovePolicy.canUpdateQualifiedNames();
	}
	
	public boolean hasAllInputSet() {
		return fMovePolicy.hasAllInputSet();
	}
	public boolean hasDestinationSet() {
		return fMovePolicy.getEGLElementDestination() != null || fMovePolicy.getResourceDestination() != null;
	}
	
	public void setCreateTargetQueries(ICreateTargetQueries queries){
		Assert.isNotNull(queries);
		fCreateTargetQueries= queries;
	}
	/**
	 * @return the create target queries, or <code>null</code> if creating new targets is not supported
	 */
	public ICreateTargetQuery getCreateTargetQuery() {
		return fMovePolicy.getCreateTargetQuery(fCreateTargetQueries);
	}
	public boolean isTextualMove() {
		return fMovePolicy.isTextualMove();
	}

	public RefactoringStatus initialize(RefactoringArguments arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canEnableComment() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getComment() {
		return fComment;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setComment(String comment) {
		fComment= comment;
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
