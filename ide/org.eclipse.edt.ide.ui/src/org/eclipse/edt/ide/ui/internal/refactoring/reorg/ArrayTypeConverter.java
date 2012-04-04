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

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;

class ArrayTypeConverter {

	private ArrayTypeConverter() {
	}

	static IFile[] toFileArray(Object[] objects){
		List l= Arrays.asList(objects);
		return (IFile[]) l.toArray(new IFile[l.size()]);
	}
		
	static IFolder[] toFolderArray(Object[] objects){
		List l= Arrays.asList(objects);
		return (IFolder[]) l.toArray(new IFolder[l.size()]);
	}

	static IEGLFile[] toEGLFileArray(Object[] objects){
		List l= Arrays.asList(objects);
		return (IEGLFile[]) l.toArray(new IEGLFile[l.size()]);
	}
	
	static IPackageFragmentRoot[] toPackageFragmentRootArray(Object[] objects){
		List l= Arrays.asList(objects);
		return (IPackageFragmentRoot[]) l.toArray(new IPackageFragmentRoot[l.size()]);
	}
	
	static IPackageFragment[] toPackageArray(Object[] objects){
		List l= Arrays.asList(objects);
		return (IPackageFragment[]) l.toArray(new IPackageFragment[l.size()]);
	}
}
