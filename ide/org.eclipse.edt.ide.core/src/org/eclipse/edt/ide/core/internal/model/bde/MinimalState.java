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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.model.util.Util;
import org.eclipse.edt.ide.core.model.bde.IPluginModelBase;
import org.osgi.framework.Constants;

import org.eclipse.edt.ide.core.model.EGLCore;

public class MinimalState {

	protected long fId;

	private boolean fEEListChanged = false; // indicates that the EE has changed
	// this could be due to the system bundle changing location
	// or initially when the ee list is first created.

	private String[] fExecutionEnvironments; // an ordered list of known/supported execution environments

	private boolean fNoProfile;

	protected static boolean DEBUG = false;


	protected static String DIR;

	protected String fSystemBundle = "org.eclipse.osgi";
	
	protected List<BinaryProjectDescription> descriptions = new ArrayList<BinaryProjectDescription>();
	
	static {
		DEBUG = EDTCoreIDEPlugin.getPlugin().isDebugging() && "true".equals(Platform.getDebugOption("org.eclipse.edt.ide.core/cache")); //$NON-NLS-1$ //$NON-NLS-2$
		DIR = EDTCoreIDEPlugin.getPlugin().getStateLocation().toOSString();
	}

	protected MinimalState(MinimalState state) {
		this.fId = state.fId;
		this.fEEListChanged = state.fEEListChanged;
		this.fExecutionEnvironments = state.fExecutionEnvironments;
		this.fNoProfile = state.fNoProfile;
		this.fSystemBundle = state.fSystemBundle;
	}

	protected MinimalState() {
	}

	public void addBundle(IPluginModelBase model, boolean update) {
		if (model == null)
			return;

		BinaryProjectDescription desc = model.getBundleDescription();
		long bundleId = desc == null || !update ? -1 : desc.getId();
		try {
			BinaryProjectDescription newDesc = addBundle(new File(model.getInstallLocation()), bundleId);
			model.setBundleDescription(newDesc);
			if (newDesc == null && update)
				this.descriptions.remove(newDesc);
				//fState.removeBundle(desc);
		} catch (IOException e) {
		} catch (CoreException e) {
			EDTCoreIDEPlugin.log(e);
		}
	}

	public BinaryProjectDescription addBundle(File bundleLocation, long bundleId) throws CoreException, IOException {
		Dictionary<String, String> binaryProjInfo = new Hashtable<String, String>();//(bundleLocation);
		if(!Util.isBinaryProject(bundleLocation)) {
			return null;
		}
		binaryProjInfo.put(Constants.BUNDLE_SYMBOLICNAME, getBinaryProjectName(bundleLocation));
		BinaryProjectDescription description = new BinaryProjectDescription();
		description.setId(bundleId == -1 ? getNextId() : bundleId);
		description.setName(getBinaryProjectName(bundleLocation));
		description.setLocation(bundleLocation.getPath());
		
		this.descriptions.add(description);
		
		//BundleDescription desc = addBundle(binaryProjInfo, bundleLocation, bundleId);
		
		if (description != null) {
			addAuxiliaryData(description, binaryProjInfo, true);
		}
		return description;
	}

	
	private String getBinaryProjectName(File bundleLocation) {
		if(bundleLocation == null) {
			return "";
		}
		return bundleLocation.getName();
	}
	
	protected void addAuxiliaryData(BinaryProjectDescription desc, Dictionary manifest, boolean hasBundleStructure) {
	}

	public void removeBundleDescription(BinaryProjectDescription description) {
		this.descriptions.remove(description);
	}


	public void addBundleDescription(BinaryProjectDescription toAdd) {
		if(toAdd != null)
			this.descriptions.add(toAdd);
	}


	public long getNextId() {
		return ++fId;
	}


	public String getSystemBundle() {
		return fSystemBundle;
	}

}

