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
package org.eclipse.edt.ide.core.internal.model.bde.target;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.model.bde.IPluginModelBase;
import org.eclipse.edt.ide.core.internal.model.bde.BDEPreferencesManager;
import org.eclipse.edt.ide.core.internal.model.bde.BDEState;
import org.eclipse.edt.ide.core.internal.model.bde.ICoreConstants;
import org.eclipse.edt.ide.core.internal.model.bde.TargetPlatform;
import org.eclipse.edt.ide.core.internal.model.bde.TargetPlatformResetJob;

import org.eclipse.edt.ide.core.model.EGLCore;

/**
 * Sets the current target platform based on a target definition.
 * 
 * @since 3.5
 */
public class LoadTargetDefinitionJob extends WorkspaceJob {

	private static final String JOB_FAMILY_ID = "LoadTargetDefinitionJob"; //$NON-NLS-1$

	/**
	 * Target definition being loaded
	 */
	private ITargetDefinition fTarget;

	/**
	 * Whether a target definition was specified
	 */
	private boolean fNone = false;

	/**
	 * Constructs a new operation to load the specified target definition
	 * as the current target platform. When <code>null</code> is specified
	 * the target platform is empty and all other settings are default.  This
	 * method will cancel all existing LoadTargetDefinitionJob instances then
	 * schedules the operation as a user job.
	 * 
	 * @param target target definition or <code>null</code> if none
	 */
	public static void load(ITargetDefinition target) {
		load(target, null);
	}

	/**
	 * Constructs a new operation to load the specified target definition
	 * as the current target platform. When <code>null</code> is specified
	 * the target platform is empty and all other settings are default.  This
	 * method will cancel all existing LoadTargetDefinitionJob instances then
	 * schedules the operation as a user job.  Adds the given listener to the
	 * job that is started.
	 * 
	 * @param target target definition or <code>null</code> if none
	 * @param listener job change listener that will be added to the created job
	 */
	public static void load(ITargetDefinition target, IJobChangeListener listener) {
		Job.getJobManager().cancel(JOB_FAMILY_ID);
		Job job = new LoadTargetDefinitionJob(target);
		job.setUser(true);
		if (listener != null) {
			job.addJobChangeListener(listener);
		}
		job.schedule();
	}

	/**
	 * Constructs a new operation to load the specified target definition
	 * as the current target platform. When <code>null</code> is specified
	 * the target platform is empty and all other settings are default.
	 *<p>
	 * Clients should use {@link #getLoadJob(ITargetDefinition)} instead to ensure
	 * any existing jobs are cancelled.
	 * </p>
	 * @param target target definition or <code>null</code> if none
	 */
	public LoadTargetDefinitionJob(ITargetDefinition target) {
		super(Messages.LoadTargetDefinitionJob_0);
		fTarget = target;
		if (target == null) {
			fNone = true;
			ITargetPlatformService service = (ITargetPlatformService) EDTCoreIDEPlugin.getPlugin().acquireService(ITargetPlatformService.class.getName());
			fTarget = service.newTarget();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
	 */
	public boolean belongsTo(Object family) {
		return JOB_FAMILY_ID.equals(family);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.WorkspaceJob#runInWorkspace(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		try {
			BDEPreferencesManager preferences = EDTCoreIDEPlugin.getPlugin().getPreferencesManager();
			monitor.beginTask(Messages.LoadTargetOperation_mainTaskName, 100);

//			loadImplicitPlugins(preferences, new SubProgressMonitor(monitor, 15));
//			if (monitor.isCanceled()) {
//				return Status.CANCEL_STATUS;
//			}

			loadPlugins(preferences, new SubProgressMonitor(monitor, 60));
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			loadAdditionalPreferences(preferences);
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			EDTCoreIDEPlugin.getPlugin().getPreferencesManager().savePluginPreferences();
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}

	/**
	 * Resolves the bundles in the target platform and sets them in the corresponding
	 * CHECKED_PLUGINS preference. Sets home and addition location preferences as well.
	 * 
	 * @param pref
	 * @param monitor
	 * @throws CoreException
	 */
	private void loadPlugins(BDEPreferencesManager pref, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(Messages.LoadTargetOperation_loadPluginsTaskName, 100);
		String currentPath = pref.getString(ICoreConstants.PLATFORM_PATH);
		IBundleContainer[] containers = fTarget.getBundleContainers();
		// the first container is assumed to be the primary/home location
		String path = null;
		if (containers != null && containers.length > 0) {
			path = ((AbstractBundleContainer) containers[0]).getLocation(true);
		}
		if (path == null) {
			path = TargetPlatform.getDefaultLocation();
		} else {
			try {
				IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
				path = manager.performStringSubstitution(path);
			} catch (CoreException e) {
				return;
			}
		}
		monitor.worked(10);
		List additional = getAdditionalLocs();
		// update preferences (Note: some preferences updated in handleReload())
		pref.setValue(ICoreConstants.PLATFORM_PATH, path);
		handleReload(path, additional, pref, new SubProgressMonitor(monitor, 85));
		String mode = new Path(path).equals(new Path(TargetPlatform.getDefaultLocation())) ? ICoreConstants.VALUE_USE_THIS : ICoreConstants.VALUE_USE_OTHER;
		pref.setValue(ICoreConstants.TARGET_MODE, mode);

		ListIterator li = additional.listIterator();
		StringBuffer buffer = new StringBuffer();
		while (li.hasNext())
			buffer.append(li.next()).append(","); //$NON-NLS-1$
		if (buffer.length() > 0)
			buffer.setLength(buffer.length() - 1);
		pref.setValue(ICoreConstants.ADDITIONAL_LOCATIONS, buffer.toString());

		String newValue = currentPath;
		for (int i = 0; i < 4; i++) {
			String value = pref.getString(ICoreConstants.SAVED_PLATFORM + i);
			pref.setValue(ICoreConstants.SAVED_PLATFORM + i, newValue);
			if (!value.equals(currentPath))
				newValue = value;
			else
				break;
		}
		monitor.done();
	}

	/**
	 * Sets the TARGET_PROFILE preference which stores the ID of the target profile used 
	 * (if based on an target extension) or the workspace location of the file that
	 * was used. For now we just clear it.
	 * <p>
	 * Sets the WORKSPACE_TARGET_HANDLE.
	 * </p>
	 * @param pref
	 */
	private void loadAdditionalPreferences(BDEPreferencesManager pref) throws CoreException {
		pref.setValue(ICoreConstants.TARGET_PROFILE, ""); //$NON-NLS-1$
		String memento = fTarget.getHandle().getMemento();
		if (fNone) {
			memento = ICoreConstants.NO_TARGET;
		}
		pref.setValue(ICoreConstants.WORKSPACE_TARGET_HANDLE, memento);
		IBundleContainer[] containers = fTarget.getBundleContainers();
		boolean profile = false;
		if (containers != null && containers.length > 0) {
			//TODO profile = containers[0] instanceof ProfileBundleContainer;
		}
		pref.setValue(ICoreConstants.TARGET_PLATFORM_REALIZATION, profile);
	}

	/**
	 * Returns a list of additional locations of bundles.
	 * 
	 * @return additional bundle locations
	 */
	private List getAdditionalLocs() throws CoreException {
		ArrayList additional = new ArrayList();
		// secondary containers are considered additional
		IBundleContainer[] containers = fTarget.getBundleContainers();
		if (containers != null && containers.length > 1) {
			for (int i = 1; i < containers.length; i++) {
				additional.add(((AbstractBundleContainer) containers[i]).getLocation(true));
			}
		}
		return additional;
	}

	private void handleReload(String targetLocation, List additionalLocations, BDEPreferencesManager pref, IProgressMonitor monitor) throws CoreException {
		SubMonitor subMon = SubMonitor.convert(monitor, Messages.LoadTargetOperation_reloadTaskName, 100);
		try {
			Set included = new HashSet();
			Set duplicates = new HashSet();
			List infos = new ArrayList();
			Set includedIds = new HashSet();

			if (!fTarget.isResolved()) {
				fTarget.resolve(subMon.newChild(20));
			} else {
				subMon.worked(20);
			}

			// collect all bundles, ignoring duplicates (symbolic name & version)
			IResolvedBundle[] resolved = fTarget.getBundles();
			List pooled = new ArrayList();
			boolean considerPool = false;
			for (int i = 0; i < resolved.length; i++) {
				if (resolved[i].getStatus().isOK()) {
					BinaryProjectInfo bundleInfo = resolved[i].getBundleInfo();
					NameVersionDescriptor desc = new NameVersionDescriptor(bundleInfo.getSymbolicName(), bundleInfo.getVersion());
					File file = new File(bundleInfo.getLocation());
					boolean inPool = AbstractTargetHandle.BUNDLE_POOL.isPrefixOf(new Path(file.getAbsolutePath()));
					considerPool = considerPool || inPool;
					if (!duplicates.contains(desc)) {
						if (inPool) {
							pooled.add(desc);
						}
						infos.add(bundleInfo);
						included.add(bundleInfo);
						includedIds.add(bundleInfo.getSymbolicName());
						duplicates.add(desc);
					}
				}
			}

			// Compute missing (not included) bundles (preference need to know disabled/missing bundles)
			List missing = new ArrayList();
			NameVersionDescriptor[] restrictions = fTarget.getIncluded();
			if (restrictions != null) {
				IResolvedBundle[] all = fTarget.getAllBundles();
				for (int j = 0; j < all.length; j++) {
					IResolvedBundle bi = all[j];
					if (!included.contains(bi.getBundleInfo())) {
						missing.add(bi.getBundleInfo());
					}
				}
			}

			List paths = new ArrayList(infos.size() + missing.size());
			Iterator iterator = infos.iterator();
			while (iterator.hasNext()) {
				BinaryProjectInfo info = (BinaryProjectInfo) iterator.next();
				try {
					paths.add(new File(info.getLocation()).toURL());
				} catch (MalformedURLException e) {
					throw new CoreException(new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, Messages.LoadTargetDefinitionJob_1, e));
				}
			}

			// generate URLs and save CHECKED_PLUGINS (which are missing), and add to master list of paths
			StringBuffer checked = new StringBuffer();
			StringBuffer versions = new StringBuffer();
			int count = 0;
			iterator = missing.iterator();
			Set missingDescriptions = new HashSet(missing.size());
			while (iterator.hasNext()) {
				BinaryProjectInfo bi = (BinaryProjectInfo) iterator.next();
				NameVersionDescriptor desc = new NameVersionDescriptor(bi.getSymbolicName(), bi.getVersion());
				missingDescriptions.add(desc);
				try {
					paths.add(new File(bi.getLocation()).toURL());
				} catch (MalformedURLException e) {
					throw new CoreException(new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, Messages.LoadTargetDefinitionJob_1, e));
				}
				if (count > 0) {
					checked.append(" "); //$NON-NLS-1$
				}
				checked.append(bi.getSymbolicName());
				count++;
				if (includedIds.contains(bi.getSymbolicName())) {
					// multiple versions of the bundle are available and some are included - store version info of excluded bundles
					if (versions.length() > 0) {
						versions.append(" "); //$NON-NLS-1$
					}
					versions.append(desc.toPortableString());
				}
			}

			URL[] urls = (URL[]) paths.toArray(new URL[paths.size()]);
			BDEState state = new BDEState(urls, true, new SubProgressMonitor(monitor, 45));
			IPluginModelBase[] models = state.getTargetModels();
			for (int i = 0; i < models.length; i++) {
				NameVersionDescriptor nv = new NameVersionDescriptor(models[i].getPluginBase().getName(), models[i].getPluginBase().getVersion());
				models[i].setEnabled(!missingDescriptions.contains(nv));
			}
			// save CHECKED_PLUGINS
			if (urls.length == 0) {
				pref.setValue(ICoreConstants.CHECKED_PLUGINS, ICoreConstants.VALUE_SAVED_NONE);
			} else if (missing.size() == 0) {
				pref.setValue(ICoreConstants.CHECKED_PLUGINS, ICoreConstants.VALUE_SAVED_ALL);
			} else {
				pref.setValue(ICoreConstants.CHECKED_PLUGINS, checked.toString());
			}
			// save CHECKED_VERSION_PLUGINS
			if (versions.length() > 0) {
				pref.setValue(ICoreConstants.CHECKED_VERSION_PLUGINS, versions.toString());
			} else {
				// no version information required
				pref.setValue(ICoreConstants.CHECKED_VERSION_PLUGINS, ICoreConstants.VALUE_SAVED_NONE);
			}

			// saved POOLED_BUNDLES
			if (pooled.isEmpty()) {
				if (considerPool) {
					// all pooled bundles are excluded
					pref.setValue(ICoreConstants.POOLED_BUNDLES, ICoreConstants.VALUE_SAVED_NONE);
				} else {
					// nothing in the pool
					pref.setValue(ICoreConstants.POOLED_BUNDLES, ""); //$NON-NLS-1$
				}
			} else {
				StringBuffer buf = new StringBuffer();
				Iterator iterator2 = pooled.iterator();
				while (iterator2.hasNext()) {
					NameVersionDescriptor desc = (NameVersionDescriptor) iterator2.next();
					buf.append(desc.getId());
					buf.append(',');
					String version = desc.getVersion();
					if (version == null) {
						buf.append(ICoreConstants.VALUE_SAVED_NONE); // indicates null version
					} else {
						buf.append(version);
					}
					if (iterator2.hasNext()) {
						buf.append(',');
					}
				}
				pref.setValue(ICoreConstants.POOLED_BUNDLES, buf.toString());
			}

			Job job = new TargetPlatformResetJob(state);
			job.schedule();
			try {
				job.join();
			} catch (InterruptedException e) {
			}
		} finally {
			if (monitor != null) {
				monitor.done();
			}
			subMon.done();
		}
	}
}
