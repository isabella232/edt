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
package org.eclipse.edt.ide.core.internal.model.bde;


import java.io.File;
import java.io.InputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.bde.IPluginBase;
import org.eclipse.edt.ide.core.model.bde.IPluginModelBase;
import org.eclipse.osgi.service.resolver.BundleDescription;


public class ExternalPluginModel implements IPluginModelBase {
	private String fInstallLocation;
	private static final long serialVersionUID = 1L;
//	private String fLocalization;
	private long fTimestamp;
	private boolean fLoaded;
	protected IPluginBase fPluginBase;
	private boolean enabled = false;
	
	private BinaryProjectDescription description;
	
	public void load(BinaryProjectDescription description, BDEState state) {
		IPath path = new Path(description.getLocation());
		String device = path.getDevice();
		if (device != null)
			path = path.setDevice(device.toUpperCase());
		setInstallLocation(path.toOSString());
//		fLocalization = state.getBundleLocalization(description.getBundleId());
		
		setBundleDescription(description);
		IPluginBase base = getPluginBase();
		if (base instanceof Plugin)
			((Plugin) base).load(description, state);
		//updateTimeStamp();
		setLoaded(true);
	}
	private File getLocalFile() {
		File file = new File(getInstallLocation());
		if (file.isFile())
			return file;

		file = new File(file, ICoreConstants.BUNDLE_FILENAME_DESCRIPTOR);
		if (!file.exists()) {
			String manifest = ICoreConstants.PLUGIN_FILENAME_DESCRIPTOR;
			file = new File(getInstallLocation(), manifest);
		}
		return file;
	}
	
	public void setLoaded(boolean loaded) {
		fLoaded = loaded;
	}
	
	protected void updateTimeStamp() {
		updateTimeStamp(getLocalFile());
	}
	
	protected void updateTimeStamp(File localFile) {
		if (localFile.exists())
			fTimestamp = localFile.lastModified();
	}
	
	public void setInstallLocation(String newInstallLocation) {
		fInstallLocation = newInstallLocation;
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
		return enabled;
	}

	public void setBundleDescription(BinaryProjectDescription description) {
		this.description = description;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;		
	}

	public String getInstallLocation() {
		return fInstallLocation;
	}

	public IPluginBase createPluginBase() {
		Plugin base = new Plugin();
		base.setModel(this);
		return base;
	}
	public IResource getUnderlyingResource() {
		return null;
	}
	public boolean isLoaded() {
		return false;
	}
	public void load() throws CoreException {
	}
	public void load(InputStream source, boolean outOfSync) throws CoreException {
	}
}

