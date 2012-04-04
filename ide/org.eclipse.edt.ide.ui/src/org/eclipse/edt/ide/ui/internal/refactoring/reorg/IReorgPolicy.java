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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;

public interface IReorgPolicy extends IReorgEnablementPolicy {
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context, IReorgQueries reorgQueries) throws CoreException;
	public RefactoringStatus setDestination(IResource resource) throws EGLModelException;
	public RefactoringStatus setDestination(IEGLElement eglElement) throws EGLModelException;
	
	public boolean canChildrenBeDestinations(IResource resource);
	public boolean canChildrenBeDestinations(IEGLElement eglElement);
	public boolean canElementBeDestination(IResource resource);
	public boolean canElementBeDestination(IEGLElement eglElement);
	
	public IResource[] getResources();
	public IEGLElement[] getEGLElements();
	
	public IResource getResourceDestination();
	public IEGLElement getEGLElementDestination();
	
	public boolean hasAllInputSet();

	public boolean canUpdateReferences();
	public void setUpdateReferences(boolean update);
	public boolean getUpdateReferences();
	public boolean canUpdateQualifiedNames();
	
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status, RefactoringProcessor processor, String[] natures, SharableParticipants shared) throws CoreException;
	
	public static interface IEGLCopyPolicy extends IReorgPolicy{
		public Change createChange(IProgressMonitor pm, INewNameQueries copyQueries) throws EGLModelException;
	}
	public static interface IEGLMovePolicy extends IReorgPolicy{
		public Change createChange(IProgressMonitor pm) throws EGLModelException;
		public Change postCreateChange(Change[] participantChanges, IProgressMonitor pm) throws CoreException;
		public ICreateTargetQuery getCreateTargetQuery(ICreateTargetQueries createQueries);
		public boolean isTextualMove();
	}
}
