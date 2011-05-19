/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.model.bde;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.model.bde.BDEProject;
import org.eclipse.edt.ide.core.internal.model.bde.BinaryProjectDescription;
import org.eclipse.edt.ide.core.internal.model.bde.Plugin;


public class WorkspacePluginModel  implements IPluginModelBase, IModel {
	private IFile fUnderlyingResource;
	private IPluginBase fPluginBase;
	private boolean fLoaded;
	private BinaryProjectDescription description;
	private IProject project;
	
	
	public WorkspacePluginModel(IProject project) {
		this.project = project;
		fUnderlyingResource = BDEProject.getEGLPath(project);
	}
	
	public IPluginBase createPluginBase() {
		Plugin plugin = new Plugin();
		try {
			plugin.setId(project.getName());
			plugin.setName(project.getName());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		plugin.setModel(this);
		return plugin;
	}

	public BinaryProjectDescription getBundleDescription() {
		return description;
	}

	public IPluginBase getPluginBase() {
		return getPluginBase(true);
	}

	public IPluginBase getPluginBase(boolean createIfMissing) {
		if (fPluginBase == null && createIfMissing) {
			fPluginBase = createPluginBase();
			setLoaded(true);
		}
		return fPluginBase;
	}

	public boolean isEnabled() {
		return true;
	}

	public void setBundleDescription(BinaryProjectDescription description) {
		this.description = description;
	}

	public void setEnabled(boolean enabled) {
	}

	public String getInstallLocation() {
		IPath path = fUnderlyingResource.getLocation();
		return path == null ? null : path.removeLastSegments(1).addTrailingSeparator().toOSString();
	}

	public IResource getUnderlyingResource() {
		return fUnderlyingResource;
	}

	public boolean isLoaded() {
		return fLoaded;
	}

	public void load() throws CoreException {
		if (fUnderlyingResource == null)
			return;
		fPluginBase = createPluginBase();
		setLoaded(true);
	}
	
	public void setLoaded(boolean loaded) {
		fLoaded = loaded;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.bde.core.IModel#load(java.io.InputStream, boolean)
	 */
	public void load(InputStream stream, boolean outOfSync) throws CoreException {
		fPluginBase = createPluginBase();
	}
}
