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
package org.eclipse.edt.ide.ui.internal.refactoring.changes;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.INewNameQuery;
import org.eclipse.ltk.core.refactoring.Change;

abstract class EGLFileReorgChange extends Change {
	
	protected static final int NONE= 0;
	protected static final int READ_ONLY= 1 << 0;
	protected static final int DIRTY= 1 << 1;
	private static final int SAVE= 1 << 2;
	protected static final int SAVE_IF_DIRTY= SAVE | DIRTY;

	private String fCuHandle;
	private String fOldPackageHandle;
	private String fNewPackageHandle;

	private INewNameQuery fNewNameQuery;

	EGLFileReorgChange(IEGLFile cu, IPackageFragment dest, INewNameQuery newNameQuery) {
		fCuHandle= cu.getHandleIdentifier();
		fNewPackageHandle= dest.getHandleIdentifier();
		fNewNameQuery= newNameQuery;
		fOldPackageHandle= cu.getParent().getHandleIdentifier();
	}

	EGLFileReorgChange(IEGLFile cu, IPackageFragment dest) {
		this(cu, dest, null);
	}

	EGLFileReorgChange(String oldPackageHandle, String newPackageHandle, String cuHandle) {
		fOldPackageHandle= oldPackageHandle;
		fNewPackageHandle= newPackageHandle;
		fCuHandle= cuHandle;
	}

	public final Change perform(IProgressMonitor pm) throws CoreException {
		pm.beginTask(getName(), 1);
		try {
			IEGLFile unit= getCu();
//			ResourceMapping mapping= JavaElementResourceMapping.create(unit);
			Change result= doPerformReorg(new SubProgressMonitor(pm, 1));
//			markAsExecuted(unit, mapping);
			return result;
		} finally {
			pm.done();
		}
	}

	abstract Change doPerformReorg(IProgressMonitor pm) throws CoreException;

	public Object getModifiedElement() {
		return getCu();
	}

	IEGLFile getCu() {
		return (IEGLFile)EGLCore.create(fCuHandle);
	}

	IPackageFragment getOldPackage() {
		return (IPackageFragment)EGLCore.create(fOldPackageHandle);
	}

	IPackageFragment getDestinationPackage() {
		return (IPackageFragment)EGLCore.create(fNewPackageHandle);
	}

	String getNewName() {
		if (fNewNameQuery == null)
			return null;
		return fNewNameQuery.getNewName();
	}

	static String getPackageName(IPackageFragment pack) {
		if (pack.isDefaultPackage())
			return UINlsStrings.OpenPartDialog_DefaultPackage; 
		else
			return pack.getElementName();
	}

}
