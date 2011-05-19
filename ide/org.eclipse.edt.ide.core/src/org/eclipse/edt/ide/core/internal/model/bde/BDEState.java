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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.model.bde.IPluginModelBase;
import org.eclipse.edt.ide.core.model.bde.PluginRegistry;
import org.eclipse.osgi.service.resolver.BundleDescription;

import org.eclipse.edt.ide.core.model.EGLCore;

public class BDEState extends MinimalState {

	private BDEAuxiliaryState fAuxiliaryState;

	private ArrayList<IPluginModelBase> fTargetModels = new ArrayList<IPluginModelBase>();
	private ArrayList<IPluginModelBase> fWorkspaceModels = new ArrayList<IPluginModelBase>();
	private boolean fCombined;
	private long fTargetTimestamp;
	private boolean fNewState;

	public BDEState(URL[] urls, boolean resolve, IProgressMonitor monitor) {
		this(new URL[0], urls, resolve, false, monitor);
	}

	public BDEState(URL[] workspace, URL[] target, boolean resolve, boolean removeTargetDuplicates, IProgressMonitor monitor) {
		long start = System.currentTimeMillis();
		fAuxiliaryState = new BDEAuxiliaryState();

		if (resolve) {
			readTargetState(target, monitor);
		} else {
			createNewTargetState(resolve, target, monitor);
		}

//		if (removeTargetDuplicates) {
//			removeDuplicatesFromState(fState);
//		}
		BinaryProjectDescription[] temp = new BinaryProjectDescription[descriptions.size()];
		descriptions.toArray(temp);
		createTargetModels(temp);

		if (resolve && workspace.length > 0 && !fNewState && !"true".equals(System.getProperty("pde.nocache"))) //$NON-NLS-1$ //$NON-NLS-2$
			readWorkspaceState(workspace);

		if (DEBUG)
			System.out.println("Time to create state: " + (System.currentTimeMillis() - start) + " ms"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void readTargetState(URL[] urls, IProgressMonitor monitor) {
		fTargetTimestamp = computeTimestamp(urls);
		File dir = new File(DIR, Long.toString(fTargetTimestamp) + ".target"); //$NON-NLS-1$
		if (/**(fState = readStateCache(dir)) == null || **/ !fAuxiliaryState.readPluginInfoCache(dir)) {
			createNewTargetState(true, urls, monitor);
			if (!dir.exists())
				dir.mkdirs();
			fAuxiliaryState.savePluginInfo(dir);
		} else {
			BinaryProjectDescription[] descs = fAuxiliaryState.getPluginInfoFromCache();
			for(BinaryProjectDescription description : descs) {
				this.descriptions.add(description);
			}
		}
	}

	private void createNewTargetState(boolean resolve, URL[] urls, IProgressMonitor monitor) {
		monitor.beginTask("", urls.length); //$NON-NLS-1$
		for (int i = 0; i < urls.length; i++) {
			File file = new File(urls[i].getFile());
			try {
				if (monitor.isCanceled())
					// if canceled, stop loading bundles
					return;
				monitor.subTask(file.getName());
				addBundle(file, -1);
			} catch (CoreException e) {
			} catch (IOException e) {
				EDTCoreIDEPlugin.log(new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, IStatus.ERROR, "Invalid manifest format at " + file.getAbsolutePath(), //$NON-NLS-1$
						null));
			} finally {
				monitor.worked(1);
			}
		}
		fNewState = true;
	}

	protected void addAuxiliaryData(BinaryProjectDescription desc, Dictionary manifest, boolean hasBundleStructure) {
		fAuxiliaryState.addAuxiliaryData(desc, manifest, hasBundleStructure);
	}


	private IPluginModelBase[] createTargetModels(BinaryProjectDescription[] bundleDescriptions) {
		HashMap models = new HashMap((4 / 3) * bundleDescriptions.length + 1);
		for (int i = 0; i < bundleDescriptions.length; i++) {
			BinaryProjectDescription desc = bundleDescriptions[i];
			IPluginModelBase base = createExternalModel(desc);
			fTargetModels.add(base);
			models.put(desc.getName(), base);
		}
		if (models.isEmpty())
			return new IPluginModelBase[0];
		return (IPluginModelBase[]) models.values().toArray(new IPluginModelBase[models.size()]);
	}

	private void readWorkspaceState(URL[] urls) {
	}

	public boolean isCombined() {
		return fCombined;
	}

	private long computeTimestamp(URL[] urls) {
		return computeTimestamp(urls, 0);
	}

	private long computeTimestamp(URL[] urls, long timestamp) {
		for (int i = 0; i < urls.length; i++) {
			File file = new File(urls[i].getFile());
			if (file.exists()) {
				if (file.isFile()) {
					timestamp ^= file.lastModified();
				} 
				timestamp ^= file.getAbsolutePath().hashCode();
			}
		}
		return timestamp;
	}

	private IPluginModelBase createExternalModel(BinaryProjectDescription desc) {
		ExternalPluginModel model = null;
//		if (desc.getHost() == null)
			model = new ExternalPluginModel();
		model.load(desc, this);
		model.setBundleDescription(desc);
		return model;
	}

	public IPluginModelBase[] getTargetModels() {
		return (IPluginModelBase[]) fTargetModels.toArray(new IPluginModelBase[fTargetModels.size()]);
	}

	public IPluginModelBase[] getWorkspaceModels() {
		return (IPluginModelBase[]) fWorkspaceModels.toArray(new IPluginModelBase[fWorkspaceModels.size()]);
	}

	public void shutdown() {
		IPluginModelBase[] models = PluginRegistry.getWorkspaceModels();
		long timestamp = 0;
		if (!"true".equals(System.getProperty("pde.nocache")) && shouldSaveState(models)) { //$NON-NLS-1$ //$NON-NLS-2$
			timestamp = computeTimestamp(models);
			File dir = new File(DIR, Long.toString(timestamp) + ".workspace"); //$NON-NLS-1$
//			State state = stateObjectFactory.createState(false);
			for (int i = 0; i < models.length; i++) {
				BinaryProjectDescription desc = models[i].getBundleDescription();
//				if (desc != null)
//					state.addBundle(state.getFactory().createBundleDescription(desc));
			}
//			saveState(state, dir);
			BDEAuxiliaryState.writePluginInfo(models, dir);
		}
		clearStaleStates(".target", fTargetTimestamp); //$NON-NLS-1$
		clearStaleStates(".workspace", timestamp); //$NON-NLS-1$
		clearStaleStates(".cache", 0); //$NON-NLS-1$
	}

	private long computeTimestamp(IPluginModelBase[] models) {
		URL[] urls = new URL[models.length];
		for (int i = 0; i < models.length; i++) {
			try {
				IProject project = models[i].getUnderlyingResource().getProject();
				urls[i] = new File(project.getLocation().toString()).toURL();
			} catch (MalformedURLException e) {
			}
		}
		return computeTimestamp(urls);
	}

	private boolean shouldSaveState(IPluginModelBase[] models) {
		int nonOSGiModels = 0;
		for (int i = 0; i < models.length; i++) {
			String id = models[i].getPluginBase().getId();
			if (id == null) {
				// not an OSGi bundle
				++nonOSGiModels;
				continue;
			}
//			if (id.trim().length() == 0 || !models[i].isLoaded() || !models[i].isInSync() || models[i].getBundleDescription() == null)
//				return false;
		}
		return models.length - nonOSGiModels > 0;
	}

	private void clearStaleStates(String extension, long latest) {
		File dir = new File(EDTCoreIDEPlugin.getPlugin().getStateLocation().toOSString());
		File[] children = dir.listFiles();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				File child = children[i];
				if (child.isDirectory()) {
					String name = child.getName();
					if (name.endsWith(extension) && name.length() > extension.length() && !name.equals(Long.toString(latest) + extension)) {
//						CoreUtility.deleteContent(child);
					}
				}
			}
		}
	}


	public String getPluginName(long bundleID) {
		return fAuxiliaryState.getPluginName(bundleID);
	}


	public String getProject(long bundleID) {
		return fAuxiliaryState.getProject(bundleID);
	}

	public BinaryProjectDescription[] addAdditionalBundles(URL[] newBundleURLs) {
		// add new Bundles to the State
		ArrayList descriptions = new ArrayList(newBundleURLs.length);
		for (int i = 0; i < newBundleURLs.length; i++) {
			File file = new File(newBundleURLs[i].getFile());
			try {
				BinaryProjectDescription desc = addBundle(file, -1);
				if (desc != null)
					descriptions.add(desc);
			} catch (CoreException e) {
			} catch (IOException e) {
				EDTCoreIDEPlugin.log(new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, IStatus.ERROR, "Invalid manifest format at " + file.getAbsolutePath(), //$NON-NLS-1$
						null));
			}
		}
		// compute Timestamp and save all new information
		fTargetTimestamp = computeTimestamp(newBundleURLs, fTargetTimestamp);
		File dir = new File(DIR, Long.toString(fTargetTimestamp) + ".target"); //$NON-NLS-1$
		if (!dir.exists())
			dir.mkdirs();
		fAuxiliaryState.savePluginInfo(dir);
//		saveState(dir);

		// resolve state - same steps as when populating a new State
//		resolveState(false);

		return (BinaryProjectDescription[]) descriptions.toArray(new BinaryProjectDescription[descriptions.size()]);
	}

	public File getTargetDirectory() {
		return new File(DIR, Long.toString(fTargetTimestamp) + ".target"); //$NON-NLS-1$
	}

}
