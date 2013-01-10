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
package org.eclipse.edt.ide.ui.internal.refactoring.changes;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.ibm.icu.text.MessageFormat;

public class MoveEGLFileChange extends EGLFileReorgChange {

	private boolean fUndoable;
	private long fStampToRestore;
	
	public MoveEGLFileChange(IEGLFile cu, IPackageFragment newPackage){
		super(cu, newPackage);
		fStampToRestore= IResource.NULL_STAMP;
	}
	
	private MoveEGLFileChange(IPackageFragment oldPackage, String cuName, IPackageFragment newPackage, long stampToRestore) {
		super(oldPackage.getHandleIdentifier(), newPackage.getHandleIdentifier(), oldPackage.getEGLFile(cuName).getHandleIdentifier());
		fStampToRestore= stampToRestore;
	}
	
	public String getName() {
		return MessageFormat.format(UINlsStrings.MoveEGLFileChange_name, 
		new String[]{getCu().getElementName(), getPackageName(getDestinationPackage())}); 
	}

	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
		return new RefactoringStatus();
	}
	
	Change doPerformReorg(IProgressMonitor pm) throws CoreException {
		String name;
		String newName= getNewName();
		if (newName == null)
			name= getCu().getElementName();
		else
			name= newName;
		
		// get current modification stamp
		long currentStamp= IResource.NULL_STAMP;
		IResource resource= getCu().getResource();
		if (resource != null) {
			currentStamp= resource.getModificationStamp();
			if (!resource.isSynchronized(IResource.DEPTH_INFINITE)) {
				resource.refreshLocal(IResource.DEPTH_INFINITE, pm);
			}
		}
		
		fUndoable= ! getDestinationPackage().getEGLFile(name).exists();
		
		// perform the move and restore modification stamp
				
//		getCu().move(getDestinationPackage(), null, newName, true, pm);
		IResource file = getCu().getResource();
		if (!fUndoable) {
			 getDestinationPackage().getEGLFile(name).getResource().delete(true, null);
		}
		file.move( getDestinationPackage().getEGLFile(name).getResource().getFullPath(), true, null);
		
		
		if (fStampToRestore != IResource.NULL_STAMP) {
			IEGLFile moved= getDestinationPackage().getEGLFile(name);
			IResource movedResource= moved.getResource();
			if (movedResource != null) {
				movedResource.revertModificationStamp(fStampToRestore);
			}
		}
		
		if (fUndoable) {
			return new MoveEGLFileChange(getDestinationPackage(), getCu().getElementName(), getOldPackage(), currentStamp);
		} else {
			return null;
		}
	}
	
	public void initializeValidationData(IProgressMonitor pm) {
	}
}
