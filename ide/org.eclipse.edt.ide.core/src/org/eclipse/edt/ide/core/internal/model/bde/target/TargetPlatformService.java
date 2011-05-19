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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
//import org.eclipse.core.runtime.URIUtil;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.model.bde.IPluginModelBase;
import org.eclipse.edt.ide.core.model.bde.PluginRegistry;
import org.eclipse.edt.ide.core.internal.model.bde.BDEPreferencesManager;
import org.eclipse.edt.ide.core.internal.model.bde.ICoreConstants;
import org.eclipse.edt.ide.core.internal.model.bde.PluginModelManager;
import org.eclipse.osgi.service.datalocation.Location;

import org.eclipse.edt.ide.core.model.EGLCore;
/**
 * Target platform service implementation.
 * 
 * @since 3.5
 */
public class TargetPlatformService implements ITargetPlatformService {
	/**
	 * File extension for target definitions
	 */
	public static final String TARGET_FILE_EXTENSION = "target"; //$NON-NLS-1$
	/**
	 * Service instance
	 */
	private static ITargetPlatformService fgDefault;

	private TargetPlatformService() {
	}

	public synchronized static ITargetPlatformService getDefault() {
		if (fgDefault == null) {
			fgDefault = new TargetPlatformService();
		}
		return fgDefault;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetPlatformService#deleteTarget(org.eclipse.pde.internal.core.target.provisional.ITargetHandle)
	 */
	public void deleteTarget(ITargetHandle handle) throws CoreException {
		((AbstractTargetHandle) handle).delete();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetPlatformService#getTarget(java.lang.String)
	 */
	public ITargetHandle getTarget(String memento) throws CoreException {
		try {
			URI uri = new URI(memento);
			String scheme = uri.getScheme();
			if (LocalTargetHandle.SCHEME.equals(scheme)) {
				return LocalTargetHandle.restoreHandle(uri);
			} 
		} catch (URISyntaxException e) {
			throw new CoreException(new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, Messages.TargetPlatformService_0, e));
		}
		throw new CoreException(new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, Messages.TargetPlatformService_1, null));
	}


	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetPlatformService#getTargets(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public ITargetHandle[] getTargets(IProgressMonitor monitor) {
		List<ITargetHandle> local = findLocalTargetDefinitions();
		return (ITargetHandle[]) local.toArray(new ITargetHandle[local.size()]);
	}

	/**
	 * Finds and returns all local target definition handles
	 *
	 * @return all local target definition handles
	 */
	private List<ITargetHandle> findLocalTargetDefinitions() {
		IPath containerPath = LocalTargetHandle.LOCAL_TARGET_CONTAINER_PATH;
		List<ITargetHandle> handles = new ArrayList<ITargetHandle>(10);
		final File directory = containerPath.toFile();
		if (directory.isDirectory()) {
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return dir.equals(directory) && name.endsWith(ICoreConstants.TARGET_FILE_EXTENSION);
				}
			};
			File[] files = directory.listFiles(filter);
			for (int i = 0; i < files.length; i++) {
				try {
					handles.add(LocalTargetHandle.restoreHandle(files[i].toURI()));
				} catch (CoreException e) {
					EDTCoreIDEPlugin.log(e);
				}
			}
		}
		return handles;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetPlatformService#newDirectoryContainer(java.lang.String)
	 */
	public IBundleContainer newDirectoryContainer(String path) {
		return new DirectoryBundleContainer(path);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetPlatformService#newTarget()
	 */
	public ITargetDefinition newTarget() {
		return new TargetDefinition(new LocalTargetHandle());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetPlatformService#saveTargetDefinition(org.eclipse.pde.internal.core.target.provisional.ITargetDefinition)
	 */
	public void saveTargetDefinition(ITargetDefinition definition) throws CoreException {
		((AbstractTargetHandle) definition.getHandle()).save(definition);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetPlatformService#getWorkspaceTargetDefinition()
	 */
	public ITargetHandle getWorkspaceTargetHandle() throws CoreException {
		// If the plug-in registry has not been initialized we may not have a target set, getting the start forces the init
//		PluginModelManager manager = Activator.getPlugin().getModelManager();
//		if (!manager.isInitialized()) {
//			manager.getExternalModelManager();
//		}

		BDEPreferencesManager preferences = EDTCoreIDEPlugin.getPlugin().getPreferencesManager();
		String memento = preferences.getString(ICoreConstants.WORKSPACE_TARGET_HANDLE);
		if (memento != null && memento.length() != 0 && !memento.equals(ICoreConstants.NO_TARGET)) {
			return getTarget(memento);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetPlatformService#copyTargetDefinition(org.eclipse.pde.internal.core.target.provisional.ITargetDefinition, org.eclipse.pde.internal.core.target.provisional.ITargetDefinition)
	 */
	public void copyTargetDefinition(ITargetDefinition from, ITargetDefinition to) throws CoreException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		((TargetDefinition) from).write(outputStream);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		((TargetDefinition) to).setContents(inputStream);
	}


	/**
	 * This is a utility method to initialize a target definition based on current workspace
	 * preference settings (target platform settings). It is not part of the service API since
	 * the preference settings should eventually be removed.
	 * 
	 * @param definition target definition
	 * @throws CoreException
	 */
	public void loadTargetDefinitionFromPreferences(ITargetDefinition target) throws CoreException {
		BDEPreferencesManager preferences = EDTCoreIDEPlugin.getPlugin().getPreferencesManager();
		initializeImplicitInfo(preferences, target);
		initializeLocationInfo(preferences, target);
		initializeAdditionalLocsInfo(preferences, target);
		initializePluginContent(preferences, target);
	}

	private void initializeImplicitInfo(BDEPreferencesManager preferences, ITargetDefinition target) {
		String value = preferences.getString(ICoreConstants.IMPLICIT_DEPENDENCIES);
		if (value.length() > 0) {
			StringTokenizer tokenizer = new StringTokenizer(value, ","); //$NON-NLS-1$
			NameVersionDescriptor[] plugins = new NameVersionDescriptor[tokenizer.countTokens()];
			int i = 0;
			while (tokenizer.hasMoreTokens()) {
				String id = tokenizer.nextToken();
				plugins[i++] = new NameVersionDescriptor(id, null);
			}
			target.setImplicitDependencies(plugins);
		}
	}

	private void initializeLocationInfo(BDEPreferencesManager preferences, ITargetDefinition target) {
		boolean useThis = preferences.getString(ICoreConstants.TARGET_MODE).equals(ICoreConstants.VALUE_USE_THIS);
		boolean profile = preferences.getBoolean(ICoreConstants.TARGET_PLATFORM_REALIZATION);
		String home = null;
		// Target weaving
		Location configArea = Platform.getConfigurationLocation();
		String configLocation = null;
		if (configArea != null) {
			configLocation = configArea.getURL().getFile();
		}
		if (configLocation != null) {
			Location location = Platform.getInstallLocation();
			if (location != null) {
				URL url = location.getURL();
				if (url != null) {
					IPath installPath = new Path(url.getFile());
					IPath configPath = new Path(configLocation);
					if (installPath.isPrefixOf(configPath)) {
						// if it is the default configuration area, do not specify explicitly
						configPath = configPath.removeFirstSegments(installPath.segmentCount());
						configPath = configPath.setDevice(null);
						if (configPath.segmentCount() == 1 && configPath.lastSegment().equals("configuration")) { //$NON-NLS-1$
							configLocation = null;
						}
					}
				}
			}
		}
		if (useThis) {
			home = "${eclipse_home}"; //$NON-NLS-1$
		} else {
			home = preferences.getString(ICoreConstants.PLATFORM_PATH);
		}
		IBundleContainer primary = null;
		if (profile) {
//			primary = newProfileContainer(home, configLocation);
		} else {
			primary = newDirectoryContainer(home);
		}
		target.setName(Messages.TargetPlatformService_5);
		target.setBundleContainers(new IBundleContainer[] {primary});
	}

	private void initializeAdditionalLocsInfo(BDEPreferencesManager preferences, ITargetDefinition target) {
		String additional = preferences.getString(ICoreConstants.ADDITIONAL_LOCATIONS);
		StringTokenizer tokenizer = new StringTokenizer(additional, ","); //$NON-NLS-1$
		int size = tokenizer.countTokens();
		if (size > 0) {
			IBundleContainer[] locations = new IBundleContainer[size + 1];
			locations[0] = target.getBundleContainers()[0];
			int i = 1;
			while (tokenizer.hasMoreTokens()) {
				locations[i++] = newDirectoryContainer(tokenizer.nextToken().trim());
			}
			target.setBundleContainers(locations);
		}
	}

	private void initializePluginContent(BDEPreferencesManager preferences, ITargetDefinition target) {
		String value = preferences.getString(ICoreConstants.CHECKED_PLUGINS);
		if (value.length() == 0 || value.equals(ICoreConstants.VALUE_SAVED_NONE)) {
			// no bundles
			target.setBundleContainers(null);
			return;
		}
		if (!value.equals(ICoreConstants.VALUE_SAVED_ALL)) {
			// restrictions on container
			IPluginModelBase[] models = PluginRegistry.getExternalModels();
			ArrayList<NameVersionDescriptor> list = new ArrayList<NameVersionDescriptor>(models.length);
			Set<String> disabledIDs = new HashSet<String>();
			for (int i = 0; i < models.length; i++) {
				if (!models[i].isEnabled()) {
					disabledIDs.add(models[i].getPluginBase().getId());
				}
			}
			for (int i = 0; i < models.length; i++) {
				if (models[i].isEnabled()) {
					String id = models[i].getPluginBase().getId();
					if (id != null) {
						if (disabledIDs.contains(id)) {
							// include version info since some versions are disabled
							list.add(new NameVersionDescriptor(id, models[i].getPluginBase().getVersion()));
						} else {
							list.add(new NameVersionDescriptor(id, null));
						}
					}
				}
			}
			if (list.size() > 0) {
				target.setIncluded((NameVersionDescriptor[]) list.toArray(new NameVersionDescriptor[list.size()]));
			}
		}

	}

	/**
	 * Creates a target definition with default settings - i.e. the running host.
	 * Uses an explicit configuration area if not equal to the default location.
	 * 
	 * @return target definition
	 */
	public ITargetDefinition newDefaultTargetDefinition() {
		ITargetDefinition target = newTarget();
		Location configArea = Platform.getConfigurationLocation();
		String configLocation = null;
		if (configArea != null) {
			configLocation = configArea.getURL().getFile();
		}
		if (configLocation != null) {
			Location location = Platform.getInstallLocation();
			if (location != null) {
				URL url = location.getURL();
				if (url != null) {
					IPath installPath = new Path(url.getFile());
					IPath configPath = new Path(configLocation);
					if (installPath.isPrefixOf(configPath)) {
						// if it is the default configuration area, do not specify explicitly
						configPath = configPath.removeFirstSegments(installPath.segmentCount());
						configPath = configPath.setDevice(null);
						if (configPath.segmentCount() == 1 && configPath.lastSegment().equals("configuration")) { //$NON-NLS-1$
							configLocation = null;
						}
					}
				}
			}
		}
		target.setName(Messages.TargetPlatformService_7);
		return target;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetPlatformService#compareWithTargetPlatform(org.eclipse.pde.internal.core.target.provisional.ITargetDefinition)
	 */
	public IStatus compareWithTargetPlatform(ITargetDefinition target) throws CoreException {
		if (!target.isResolved()) {
			return null;
		}

		// Get the current models from the target platform
//		IPluginModelBase[] models = Activator.getPlugin().getModelManager().getExternalModels();
//		Set<String> allLocations = new HashSet<String>(models.length);
//		Map<String, IPluginModelBase> stateLocations = new HashMap<String, IPluginModelBase>(models.length);
//		for (int i = 0; i < models.length; i++) {
//			IPluginModelBase base = models[i];
//			allLocations.add(base.getInstallLocation());
//			stateLocations.put(base.getInstallLocation(), base);
//		}

		// Compare the platform bundles against the definition ones and collect any missing bundles
		MultiStatus multi = new MultiStatus(EGLCore.PLUGIN_ID, 0, "", null); //$NON-NLS-1$ 
		IResolvedBundle[] bundles = target.getAllBundles();
		Set<NameVersionDescriptor> alreadyConsidered = new HashSet<NameVersionDescriptor>(bundles.length);
		for (int i = 0; i < bundles.length; i++) {
			IResolvedBundle bundle = bundles[i];
			BinaryProjectInfo info = bundle.getBundleInfo();
//			File file = URIUtil.toFile(info.getLocation());
//			String location = file.getAbsolutePath();
//			stateLocations.remove(location);
//			NameVersionDescriptor desc = new NameVersionDescriptor(info.getSymbolicName(), info.getVersion());
//			if (!alreadyConsidered.contains(desc)) {
//				alreadyConsidered.add(desc);
//				// ignore duplicates (symbolic name & version)
//				if (!allLocations.contains(location)) {
//					// it's not in the state... if it's not really in the target either (missing) this
//					// is not an error
//					IStatus status = bundle.getStatus();
//					if (status.isOK() || (status.getCode() != IResolvedBundle.STATUS_DOES_NOT_EXIST && status.getCode() != IResolvedBundle.STATUS_VERSION_DOES_NOT_EXIST)) {
//						// its in the target, missing in the state
//						IStatus s = new Status(IStatus.WARNING, EGLCore.PLUGIN_ID, ITargetPlatformService.STATUS_MISSING_FROM_TARGET_PLATFORM, bundle.getBundleInfo().getSymbolicName(), null);
//						multi.add(s);
//					}
//				}
//			}
		}

		// Anything left over is in the state and not the target (have been removed from the target)
//		Iterator<IPluginModelBase> iterator = stateLocations.values().iterator();
//		while (iterator.hasNext()) {
//			IPluginModelBase model = iterator.next();
//			IStatus status = new Status(IStatus.WARNING, EGLCore.PLUGIN_ID, ITargetPlatformService.STATUS_MISSING_FROM_TARGET_DEFINITION, model.getPluginBase().getId(), null);
//			multi.add(status);
//		}

		if (multi.isOK()) {
			return Status.OK_STATUS;
		}
		return multi;
	}
}
