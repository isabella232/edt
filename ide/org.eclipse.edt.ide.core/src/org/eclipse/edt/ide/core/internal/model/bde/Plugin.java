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

import java.io.Serializable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.edt.ide.core.model.bde.IPluginBase;
import org.eclipse.edt.ide.core.model.bde.IPluginModelBase;


public class Plugin extends PlatformObject implements Serializable, IPluginBase{
	private String fProviderName;
	private String fId;
	private String fVersion;
	protected String fName;
	private transient IPluginModelBase fModel;
	
	
	void load(BinaryProjectDescription bundleDescription, BDEState state) {
		fId = String.valueOf(bundleDescription.getId());
		fVersion = bundleDescription.getVersion();
		fName = bundleDescription.getName();
	}
	
	public void setModel(IPluginModelBase model) {
		this.fModel = model;
	}
	public IPluginModelBase getModel() {
		return fModel;
	}

	public String getName() {
		return fName;
	}

	public void setName(String name) throws CoreException {
		this.fName = name;
	}

	public String getProviderName() {
		return fProviderName;
	}

	public String getVersion() {
		return fVersion;
	}

	public void setVersion(String version) throws CoreException {
		this.fVersion = version;
	}

	public String getId() {
		return fId;
	}

	public void setId(String id) throws CoreException {
		this.fId = id;
	}

}
