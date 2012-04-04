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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class DeleteChange extends Change {

	Object[] elements;

	public DeleteChange(Object[] elements) {
		super();
		this.elements = elements;
	}

	public Object getModifiedElement() {
		return elements;
	}

	public String getName() {
		return null;
	}

	public void initializeValidationData(IProgressMonitor pm) {
	}

	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
		IEGLElement specificSelection;

		for (int i = 0; i < elements.length; i++) {

			specificSelection = (IEGLElement) elements[i];

			switch (((IEGLElement) specificSelection).getElementType()) {
			case IEGLElement.EGL_FILE:
				IEGLFile deleteFile = ((IEGLFile) specificSelection);
				try {
					DeleteChange.delete(deleteFile, new NullProgressMonitor());
				} catch (EGLModelException e) {
				}
				break;
			case IEGLElement.PACKAGE_FRAGMENT:
				IPackageFragment deletePackage = ((IPackageFragment) specificSelection);
				try {
					DeleteChange.delete(deletePackage, new NullProgressMonitor());
				} catch (EGLModelException e) {

				}
				break;
			case IEGLElement.PACKAGE_FRAGMENT_ROOT:
				IPackageFragmentRoot deleteSourceFolder = ((IPackageFragmentRoot) specificSelection);
				try {
					DeleteChange.delete(deleteSourceFolder, new NullProgressMonitor());
				} catch (EGLModelException e) {
				}
				break;
			}
		}
		return null;
	}

	public static void delete(IResource res, IProgressMonitor pm) throws CoreException{
		try {
			if (res.exists()) {
				res.delete(false, pm);
			}
		} catch (CoreException e1) {
			if (res.exists())
				try {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
					res.delete(false, pm);
				} catch (CoreException e2) {
					if (res.exists())
						try {
							try {
								Thread.sleep(6000);
							} catch (InterruptedException e) {
							}
							res.delete(false, pm);
						} catch (CoreException e3) {
							throw e3;
						}
				}
		}
	}
	
	public static void delete(IEGLFile res, IProgressMonitor pm) throws CoreException{
		try {
			if (res.exists()) {
				res.delete(false, pm);
			}
		} catch (CoreException e1) {
			if (res.exists())
				try {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
					res.delete(false, pm);
				} catch (CoreException e2) {
					if (res.exists())
						try {
							try {
								Thread.sleep(6000);
							} catch (InterruptedException e) {
							}
							res.delete(false, pm);
						} catch (CoreException e3) {
							throw e3;
						}
				}
		}
	}

	public static void delete(IPackageFragment res, IProgressMonitor pm) throws CoreException{
		try {
			if (res.exists()) {
				res.delete(false, pm);
			}
		} catch (CoreException e1) {
			if (res.exists())
				try {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
					res.delete(false, pm);
				} catch (CoreException e2) {
					if (res.exists())
						try {
							try {
								Thread.sleep(6000);
							} catch (InterruptedException e) {
							}
							res.delete(false, pm);
						} catch (CoreException e3) {
							throw e3;
						}
				}
		}
	}
	
	public static void delete(IPackageFragmentRoot res, IProgressMonitor pm) throws CoreException{
		try {
			if (res.exists()) {
				res.delete(0, 0, pm);
			}
		} catch (CoreException e1) {
			if (res.exists())
				try {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
					res.delete(0, 0, pm);
				} catch (CoreException e2) {
					if (res.exists())
						try {
							try {
								Thread.sleep(6000);
							} catch (InterruptedException e) {
							}
							res.delete(0, 0, pm);
						} catch (CoreException e3) {
							throw e3;
						}
				}
		}
	}
	

}
