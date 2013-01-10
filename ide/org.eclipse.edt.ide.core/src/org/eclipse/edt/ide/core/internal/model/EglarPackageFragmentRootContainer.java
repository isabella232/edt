/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.IEGLProject;

public class EglarPackageFragmentRootContainer extends EglarPackageFragmentRoot {
	private List<EglarPackageFragmentRoot> roots = new ArrayList<EglarPackageFragmentRoot>();

	public EglarPackageFragmentRootContainer(IEGLProject project) {
		this(null, project);
	}
	protected EglarPackageFragmentRootContainer(IPath externalJarPath, IEGLProject project) {
		super(externalJarPath, project);
	}
	
	/**
	 * 
	 * @param root
	 */
	public void addJarPackageFragmentRoot(EglarPackageFragmentRoot root) {
		roots.add(root);
	}
	
	/**
	 * 
	 * @param root
	 * @return
	 */
	public EglarPackageFragmentRoot removeJarPackageFragmentRoot(EglarPackageFragmentRoot root) {
		int index = roots.indexOf(root);
		if(index > -1) {
			return roots.remove(index);
		}
		return null;
	}
	
	public EglarPackageFragmentRoot[] getAllJarPackageFragmentRoot() {
		EglarPackageFragmentRoot[] ret = new EglarPackageFragmentRoot[roots.size()];
		roots.toArray(ret);
		return ret;
	}
	
	public boolean hasJarPackageFragmentRoot() {
		return roots.size() != 0;
	}

	public int hashCode() {
		return this.getParent().getResource().getFullPath().toString().hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof EglarPackageFragmentRootContainer) {
			return getParent().equals(((EglarPackageFragmentRootContainer)o).getParent());
		}
		return false;
	}
	
	public boolean exists() {
		return getParent() != null;
	}
	
	public IPath getPath() {
		return Path.EMPTY;
	}

	public String getLabel(){
		return EGLModelResources.eglarSystemLibraries;
	}
}
