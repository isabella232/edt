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
package org.eclipse.edt.ide.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.ICompiler;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Utility for reading and writing EGL project-level settings.
 */
public class ProjectSettingsUtility {
	
	private ProjectSettingsUtility() {
		// No instances.
	}
	
	/**
	 * Constant for empty generator array.
	 */
	private static final IGenerator[] EMPTY_GENERATORS = {};
	
	/**
	 * Constant for empty generator ID array.
	 */
	private static final String[] EMPTY_GENERATOR_IDS = {};
	
	/**
	 * Constant for the project-level property indicating the generator to use for the project.
	 */
	public static final String PROPERTY_COMPILER_ID = "compilerId"; //$NON-NLS-1$
	
	/**
	 * Constant for the project-level property indicating the generator to use for the project.
	 */
	public static final String PROPERTY_GENERATOR_IDS = "generatorIds"; //$NON-NLS-1$
	
	/**
	 * Constant for the key used on project-level settings.
	 */
	public static final String PROJECT_KEY = "<project>"; //$NON-NLS-1$
	
	/**
	 * Returns the ICompiler registered for the given project. This returns null if there is no compiler.
	 * 
	 * @param project  The project.
	 * @return the project's compiler, possibly null.
	 */
	public static ICompiler getCompiler(IProject project) {
		String id = getCompilerId(project);
		if (id == null) {
			// When the project doesn't have its own settings, use the workspace defaults
			id = EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( EDTCorePreferenceConstants.COMPILER_ID );
		}
		if (id != null) {
			ICompiler[] compilers = EDTCoreIDEPlugin.getPlugin().getCompilers();
			for (int i = 0; i < compilers.length; i++) {
				if (compilers[i].getId().equals(id)) {
					return compilers[i];
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the ID of the ICompiler registered for the given project. This returns null if there is no compiler.
	 * 
	 * @param project  The project.
	 * @return the project's compiler ID, possibly null.
	 */
	public static String getCompilerId(IProject project) {
		Preferences prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(PROPERTY_COMPILER_ID);
		String setting = findSetting(project.getFullPath(), prefs, false);
		if (setting != null) {
			setting = setting.trim();
			if (setting.length() != 0) {
				return setting;
			}
		}
		return null;
	}
	
	/**
	 * Sets the compiler ID for a project. If the ID is null or blank, the compiler setting is removed from the project.
	 * 
	 * @param project  The project.
	 * @param id       The ICompiler's ID.
	 * @throws BackingStoreException
	 */
	public static void setCompiler(IProject project, String id) throws BackingStoreException {
		Preferences prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(PROPERTY_COMPILER_ID);
		
		if (id == null || id.length() == 0) {
			// Remove the setting
			prefs.remove(keyFor(project.getFullPath()));
		}
		else {
			prefs.put(keyFor(project.getFullPath()), id);
		}
		prefs.flush();
	}
	
	/**
	 * Returns the IGenerators registered for the given resource. If there are none, this
	 * returns an empty array.
	 * 
	 * @param resource  The resource (a file, folder, or project)
	 * @return a non-null array of the IGenerators for the resource
	 */
	public static IGenerator[] getGenerators(IResource resource) {
		String[] ids = getGeneratorIds(resource);
		if (ids == null) {
			// When the project doesn't have its own settings, use the workspace defaults
			String defaultIDs = EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString(EDTCorePreferenceConstants.GENERATOR_IDS);
			if (defaultIDs != null && (defaultIDs = defaultIDs.trim()).length() != 0) {
				ids = defaultIDs.split(",");
			}
		}
		if (ids != null && ids.length != 0) {
			IGenerator[] gens = EDTCoreIDEPlugin.getPlugin().getGenerators();
			if (gens.length > 0) {
				List<IGenerator> generators = new ArrayList<IGenerator>(ids.length);
				for (int i = 0; i < gens.length; i++) {
					String nextId = gens[i].getId();
					for (int j = 0; j < ids.length; j++) {
						if (nextId.equals(ids[j].trim())) {
							generators.add(gens[i]);
							break;
						}
					}
				}
				return generators.toArray(new IGenerator[generators.size()]);
			}
		}
		
		return EMPTY_GENERATORS;
	}
	
	/**
	 * Returns the IDs of the generators associated with the resource. This will first check the given resource for a
	 * generator setting. If it doesn't have one, its parent is checked, and so on all the way to the project setting.
	 * If there are no generators for the resource, this returns null.
	 * 
	 * @param resource  The resource (a file, folder, or project)
	 * @return an array of the generator IDs, possibly null
	 */
	public static String[] getGeneratorIds(IResource resource) {
		IProject project = resource.getProject();
		Preferences prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(PROPERTY_GENERATOR_IDS);
		
		// First check for the resource. If it doesn't exist, check its parent, then its grandparent, and so on, until at the project level.
		String setting = findSetting(resource.getFullPath(), prefs, true);
		if (setting != null) {
			setting = setting.trim();
			if (setting.length() != 0) {
				String[] ids = setting.split(",");
				
				// trim each value
				for (int i = 0; i < ids.length; i++) {
					ids[i] = ids[i].trim();
				}
				
				return ids;
			}
			return EMPTY_GENERATOR_IDS;
		}
		
		return null;
	}
	
	/**
	 * Sets the generator IDs for the given resource. If null this deletes the setting (meaning the resource will inherit
	 * settings from its parent). If an empty array, this sets a blank value indicating that the resource has no generators.
	 * 
	 * @param resource  The resource (a file, folder, or project)
	 * @param ids       The IDs to set, possibly empty or null
	 * @throws BackingStoreException
	 */
	public static void setGeneratorIds(IResource resource, String[] ids) throws BackingStoreException {
		IProject project = resource.getProject();
		Preferences prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(PROPERTY_GENERATOR_IDS);
		
		if (ids == null) {
			// Remove the setting
			prefs.remove(keyFor(resource.getFullPath()));
		}
		else if (ids.length == 0) {
			prefs.put(keyFor(resource.getFullPath()), "");
		}
		else {
			StringBuilder buf = new StringBuilder(100);
			for (int i = 0; i < ids.length; i++) {
				if (i != 0) {
					buf.append(',');
				}
				buf.append(ids[i]);
			}
			prefs.put(keyFor(resource.getFullPath()), buf.toString());
		}
		prefs.flush();
	}
	
	/**
	 * Searches for a setting on the path. If there was no setting, checks the parent path, and so on, until
	 * it has reached the root.
	 * 
	 * @param path         The path of the resource containing the setting.
	 * @param prefs        The preference store
	 * @param checkParent  Flag indicating if we should check the parent for the setting if it wasn't found
	 * @return the setting for the path, or null if not found.
	 */
	public static String findSetting(IPath path, Preferences prefs, boolean checkParent) {
		String setting = prefs.get(keyFor(path), null);
		if (checkParent && setting == null && path.segmentCount() > 1) {
			setting = findSetting(path.removeLastSegments(1), prefs, checkParent);
		}
		return setting;
	}
	
	/**
	 * @return the key to find the given setting for the resource path.
	 */
	public static String keyFor(IPath fullPath) {
		return fullPath.segmentCount() > 1 ? fullPath.removeFirstSegments(1).toString() : PROJECT_KEY;
	}
	
	/**
	 * Finds the path that contains the setting for the given keyName. If you inquire about "proj/src/bar.egl" but the setting was actually
	 * specified at the project level (meaning bar.egl inherits this setting and didn't override it) then this method will return an IPath
	 * for "proj".
	 * 
	 * @param path     The path of the resource containing the setting.
	 * @param prefs    The preference store
	 * @return the path that actually defines the setting being used for the path
	 */
	public static IPath findPathForSetting(IPath path, Preferences prefs) {
		String setting = prefs.get(keyFor(path), null);
		if (setting != null) {
			return path;
		}
		
		if (path.segmentCount() > 1) {
			return findPathForSetting(path.removeLastSegments(1), prefs);
		}
		return null;
	}
	
	/**
	 * Returns the directory in which to generate the resource. First we check for a project-level setting, and fall back on the
	 * default generation directory specified in preferences, if there is a preference store.
	 * 
	 * @param resource      The resource
	 * @param store         The workspace-level preference store
	 * @param prefs         The project-level preference store
	 * @param propertyID    The project-level property ID
	 * @param preferenceID  The workspace-level preference ID
	 * @return the directory in which to generate the resource
	 */
	public static String getGenerationDirectory(IResource resource, IPreferenceStore store, IEclipsePreferences prefs,
			String propertyID, String preferenceID) {
		Preferences propertyPrefs = prefs.node(propertyID);
		String setting = ProjectSettingsUtility.findSetting(resource.getFullPath(), propertyPrefs, true);
		if (setting != null && setting.length() > 0) {
			return setting;
		}
		
		if (store != null && preferenceID != null && preferenceID.length() > 0) {
			// Use the current or default value from preferences, making sure to convert to the internal format.
			if (setting == null || setting.length() == 0) {
				setting = store.getString(preferenceID);
			}
			if (setting == null || setting.length() == 0) {
				// Shouldn't get here - preference page should display an error if you don't specify a valid default directory.
				setting = store.getDefaultString(preferenceID);
			}
		}
		return EclipseUtilities.convertToInternalPath(setting);
	}
	
	/**
	 * Sets the directory in which to generate the resource. If a null or blank value is passed in, we remove the project-level
	 * setting, causing the resource to inherit parent settings.
	 * 
	 * @param resource    The resource
	 * @param value       The directory string; may be null or blank
	 * @param prefs       The preference store
	 * @param propertyID  The property ID
	 * @throws BackingStoreException
	 */
	public static void setGenerationDirectory(IResource resource, String value, IEclipsePreferences prefs, String propertyID) throws BackingStoreException {
		Preferences propertyPrefs = prefs.node(propertyID);
		if (value == null || value.length() == 0) {
			// Remove setting
			propertyPrefs.remove(keyFor(resource.getFullPath()));
		}
		else {
			propertyPrefs.put(keyFor(resource.getFullPath()), value);
		}
		
		propertyPrefs.flush();
	}
}
