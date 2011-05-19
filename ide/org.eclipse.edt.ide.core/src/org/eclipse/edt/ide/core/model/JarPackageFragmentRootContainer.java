/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.internal.model.JarPackageFragmentRoot;


public class JarPackageFragmentRootContainer extends JarPackageFragmentRoot {
	private List<JarPackageFragmentRoot> roots = new ArrayList<JarPackageFragmentRoot>();

	public JarPackageFragmentRootContainer(IEGLProject project) {
		this(null, project);
	}
	protected JarPackageFragmentRootContainer(IPath externalJarPath, IEGLProject project) {
		super(externalJarPath, project);
	}
	
	/**
	 * 
	 * @param root
	 */
	public void addJarPackageFragmentRoot(JarPackageFragmentRoot root) {
		roots.add(root);
	}
	
	/**
	 * 
	 * @param root
	 * @return
	 */
	public JarPackageFragmentRoot removeJarPackageFragmentRoot(JarPackageFragmentRoot root) {
		int index = roots.indexOf(root);
		if(index > -1) {
			return roots.remove(index);
		}
		return null;
	}
	
	public JarPackageFragmentRoot[] getAllJarPackageFragmentRoot() {
		JarPackageFragmentRoot[] ret = new JarPackageFragmentRoot[roots.size()];
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
		if(o instanceof JarPackageFragmentRootContainer) {
			return getParent().equals(((JarPackageFragmentRootContainer)o).getParent());
		}
		return false;
	}
	
	public boolean exists() {
		return getParent() != null;
	}
	
	public IPath getPath() {
		return Path.EMPTY;
	}
}
