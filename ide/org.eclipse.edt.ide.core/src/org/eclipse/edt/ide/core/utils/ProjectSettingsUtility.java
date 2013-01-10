/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.ide.core.AbstractGenerator;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.ide.core.IIDECompiler;
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
	private static final String[] EMPTY_STRING_ARRAY = {};
	
	/**
	 * Constant for the project-level property indicating the generator to use for the project.
	 */
	public static final String PROPERTY_COMPILER_ID = "compilerId"; //$NON-NLS-1$
	
	/**
	 * Constant for the project-level property indicating the generator to use for the project.
	 */
	public static final String PROPERTY_GENERATOR_IDS = "generatorIds"; //$NON-NLS-1$
	
	/**
	 * Constant for project-level property indicating the projects which contain the source for the generated code.
	 */
	public static final String PROPERTY_SOURCE_PROJECTS = "sourceProjects"; //$NON-NLS-1$
	
	/**
	 * Constant for the key used on project-level settings.
	 */
	public static final String PROJECT_KEY = "<project>"; //$NON-NLS-1$
	
	/**
	 * Constant for the project-level property indicating the default deployment descriptor.
	 */
	public static final String PROJECT_DEFAULT_DEPLOYMENT_DESCRIPTOR = "DefaultDeploymentDescriptorPath"; //$NON-NLS-1$
	
	/**
	 * Constant for the Java language
	 */
	public static final String LANGUAGE_JAVA = "Java"; //$NON-NLS-1$
	
	/**
	 * Constant for the JavaScript language
	 */
	public static final String LANGUAGE_JAVASCRIPT = "JavaScript"; //$NON-NLS-1$

	/**
	 * Constant for the JavaScript Development Mode generator ID
	 */
	public static final String GENERATOR_ID_JAVASCRIPT_DEV = "org.eclipse.edt.ide.gen.JavaScriptDevGenProvider"; //$NON-NLS-1$
	
	private static final Map<ICompiler, String[]> preferenceNodes = new HashMap<ICompiler, String[]>();
	
	/**
	 * Object to synchronize access to certain functions that could cause deadlock.
	 */
	private static final Object lock = new Object();
	
	/**
	 * Returns the ICompiler registered for the given project. This returns null if there is no compiler.
	 * 
	 * @param project  The project.
	 * @return the project's compiler, possibly null.
	 */
	public static IIDECompiler getCompiler(IProject project) {
		String id = getCompilerId(project);
		if (id == null) {
			// When the project doesn't have its own settings, use the workspace defaults
			id = EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( EDTCorePreferenceConstants.COMPILER_ID );
		}
		if (id != null) {
			IIDECompiler[] compilers = EDTCoreIDEPlugin.getPlugin().getCompilers();
			for (int i = 0; i < compilers.length; i++) {
				if (compilers[i].getId().equals(id)) {
					return compilers[i];
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the ID of the ICompiler registered for the given project. This returns null if there is no compiler,
	 * indicating the workspace default compiler should be used.
	 * 
	 * @param project  The project.
	 * @return the project's compiler ID, possibly null.
	 */
	public static String getCompilerId(IProject project) {
		if (project != null) {
			Preferences prefs;
			synchronized (lock) {
				prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(PROPERTY_COMPILER_ID);
			}
			String setting = findSetting(project.getFullPath(), prefs, false);
			if (setting != null) {
				setting = setting.trim();
				if (setting.length() != 0) {
					return setting;
				}
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
		Preferences prefs;
		synchronized (lock) {
			prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(PROPERTY_COMPILER_ID);
		}
		
		if (id == null || id.length() == 0) {
			// Remove the setting
			prefs.remove(keyFor(project.getFullPath()));
		}
		else {
			prefs.put(keyFor(project.getFullPath()), id);
		}
		synchronized (lock) {
			prefs.flush();
		}
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
			ids = getWorkspaceGeneratorIds();
		}
		if (ids != null && ids.length != 0) {
			IIDECompiler compiler = getCompiler(resource.getProject());
			IGenerator[] gens = EDTCoreIDEPlugin.getPlugin().getGenerators();
			if (gens.length > 0) {
				List<IGenerator> generators = new ArrayList<IGenerator>(ids.length);
				for (int i = 0; i < gens.length; i++) {
					if (gens[i].getCompiler() == compiler) { // only return generators that belong to the resource's compiler
						String nextId = gens[i].getId();
						for (int j = 0; j < ids.length; j++) {
							if (nextId.equals(ids[j].trim())) {
								generators.add(gens[i]);
								break;
							}
						}
					}
				}
				return generators.toArray(new IGenerator[generators.size()]);
			}
		}
		
		return EMPTY_GENERATORS;
	}
	
	/**
	 * Returns the IGenerators registered for the project and any resources under this project. 
	 * If there are none, this returns an empty array.
	 * 
	 * @param resource  The resource (a file, folder, or project)
	 * @return a non-null array of the IGenerators for the resource
	 */
	public static IGenerator[] getProjectGenerators(IProject resource) {
		String[] ids = getChildrenGeneratorIds(resource);
		if (ids.length != 0) {
			IIDECompiler compiler = getCompiler(resource.getProject());
			IGenerator[] gens = EDTCoreIDEPlugin.getPlugin().getGenerators();
			if (gens.length > 0) {
				List<IGenerator> generators = new ArrayList<IGenerator>(ids.length);
				for (int i = 0; i < gens.length; i++) {
					if (gens[i].getCompiler() == compiler) { // only return generators that belong to the resource's compiler
						String nextId = gens[i].getId();
						for (int j = 0; j < ids.length; j++) {
							if (nextId.equals(ids[j].trim())) {
								generators.add(gens[i]);
								break;
							}
						}
					}
				}
				return generators.toArray(new IGenerator[generators.size()]);
			}
		}
		
		return EMPTY_GENERATORS;
	}
	
	/**
	 * @return the default generator IDs from workspace preferences, never null.
	 */
	public static String[] getWorkspaceGeneratorIds() {
		String genIDs = EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString(EDTCorePreferenceConstants.GENERATOR_IDS);
		if (genIDs != null && (genIDs = genIDs.trim()).length() != 0) {
			return genIDs.split(","); //$NON-NLS-1$
		}
		else {
			return EMPTY_STRING_ARRAY;
		}
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
		Preferences prefs;
		synchronized (lock) {
			prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(PROPERTY_GENERATOR_IDS);
		}
		
		// First check for the resource. If it doesn't exist, check its parent, then its grandparent, and so on, until at the project level.
		String setting = findSetting(resource.getFullPath(), prefs, true);
		if (setting != null) {
			setting = setting.trim();
			if (setting.length() != 0) {
				String[] ids = setting.split(","); //$NON-NLS-1$
				
				// trim each value
				for (int i = 0; i < ids.length; i++) {
					ids[i] = ids[i].trim();
				}
				
				return ids;
			}
			return EMPTY_STRING_ARRAY;
		}
		
		return null;
	}
	
	/**
	 * Returns the IDs of the generators associated with the resource and its children resources. 
	 * 
	 * @param resource  The resource (a file, folder, or project)
	 * @return an array of the generator IDs, never null.
	 */
	public static String[] getChildrenGeneratorIds(IResource resource) {
		List<String> generators = new ArrayList<String>(0);
		IProject project = resource.getProject();
		Preferences prefs;
		synchronized (lock) {
			prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(PROPERTY_GENERATOR_IDS);
		}
		
		try {
		    String[] keys = prefs.keys();
		    String resourcePath = keyFor(resource.getFullPath());
		    for (String key : keys) {
		    	String setting = prefs.get(key, null);
		    	String path = EclipseUtilities.convertToInternalPath(key);
		        if (PROJECT_KEY.equals(resourcePath) || pathStartWith(path, resourcePath)) {
					setting = setting.trim();
					if (setting.length() != 0) {
						String[] ids = setting.split(","); //$NON-NLS-1$
						// trim each value
						for (int i = 0; i < ids.length; i++) {
							generators.add(ids[i].trim());
						}
	
					}
		        }
		    }
		}
		catch (BackingStoreException bse) {
			// Nothing we can do except still check if any resources are using workspace generators below.
			EDTCoreIDEPlugin.log(bse);
		}
		
		// If this resource specifies its own generation settings, do not add the workspace generator IDs. If this has its own
		// settings that means none of its children resources could be inheriting from workspace settings either.
		if (findSetting(resource.getFullPath(), prefs, false) == null) {
			for (String id : getWorkspaceGeneratorIds()) {
				generators.add(id);
			}
		}
		
		return (String[])generators.toArray(new String[generators.size()]);
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
		Preferences prefs;
		synchronized (lock) {
			prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(PROPERTY_GENERATOR_IDS);
		}
		
		if (ids == null) {
			// Remove the setting
			prefs.remove(keyFor(resource.getFullPath()));
		}
		else if (ids.length == 0) {
			prefs.put(keyFor(resource.getFullPath()), ""); //$NON-NLS-1$
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
		
		synchronized (lock) {
			prefs.flush();
		}
	}
	
	/**
	 * Removes any generator ID settings for everything in the project.
	 * 
	 * @param project  The project.
	 * @throws BackingStoreException
	 */
	public static void clearAllGeneratorIds(IProject project) throws BackingStoreException {
		Preferences prefs;
		synchronized (lock) {
			prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(PROPERTY_GENERATOR_IDS);
		}
		
		prefs.clear();
		
		synchronized (lock) {
			prefs.flush();
		}
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
		Preferences propertyPrefs;
		synchronized (lock) {
			propertyPrefs = prefs.node(propertyID);
		}
		
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
	 * Returns the argument for the generator. 
	 * 
	 * @param resource      The resource
	 * @param store         The workspace-level preference store
	 * @param prefs         The project-level preference store
	 * @param propertyID    The project-level property ID
	 * @return the directory in which to generate the resource
	 */
	public static String getGenerationArgument(IResource resource, IPreferenceStore store, IEclipsePreferences prefs,
			String propertyID) {
		Preferences propertyPrefs;
		synchronized (lock) {
			propertyPrefs = prefs.node(propertyID);
		}
		
		String setting = ProjectSettingsUtility.findSetting(resource.getFullPath(), propertyPrefs, true);
		if (setting != null && setting.length() > 0) {
			return setting;
		}
		else {
			return ""; //$NON-NLS-1$
		}
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
		Preferences propertyPrefs;
		synchronized (lock) {
			propertyPrefs = prefs.node(propertyID);
		}
		
		if (value == null || value.length() == 0) {
			// Remove setting
			propertyPrefs.remove(keyFor(resource.getFullPath()));
		}
		else {
			propertyPrefs.put(keyFor(resource.getFullPath()), value);
		}
		
		synchronized (lock) {
			propertyPrefs.flush();
		}
	}
	
	/**
	 * Updates the setting in the target project indicating which projects contain the corresponding source. Nothing is
	 * changed if the source and target projects are the same.
	 * 
	 * @param targetProject The project containing the generated artifacts.
	 * @param sourceProject The project containing the source code.
	 */
	public static void addSourceProject(IProject targetProject, IProject sourceProject) throws BackingStoreException {
		if (!targetProject.equals(sourceProject)) {
			Preferences prefs;
			synchronized (lock) {
				prefs = new ProjectScope(targetProject).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(PROPERTY_SOURCE_PROJECTS);
			}
			
			String[] projects = getSourceProjects(targetProject);
			if (projects.length == 0) {
				// First entry - just add it.
				prefs.put(keyFor(targetProject.getFullPath()), sourceProject.getName());
			}
			else {
				// Only add if not already present.
				boolean add = true;
				for (String project : projects) {
					if (project.equals(sourceProject.getName())) {
						add = false;
						break;
					}
				}
				
				if (add) {
					StringBuilder buf = new StringBuilder(100);
					for (int i = 0; i < projects.length; i++) {
						if (i != 0) {
							buf.append(',');
						}
						buf.append(projects[i]);
					}
					buf.append(',');
					buf.append(sourceProject.getName());
					prefs.put(keyFor(targetProject.getFullPath()), buf.toString());
				}
			}
			
			synchronized (lock) {
				prefs.flush();
			}
		}
	}
	
	/**
	 * @return the names of the projects containing the source code for the given project, never null.
	 */
	public static String[] getSourceProjects(IProject targetProject) {
		Preferences prefs;
		synchronized (lock) {
			prefs = new ProjectScope(targetProject).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(PROPERTY_SOURCE_PROJECTS);
		}
		
		String setting = ProjectSettingsUtility.findSetting(targetProject.getFullPath(), prefs, false);
		if (setting != null) {
			setting = setting.trim();
			if (setting.length() != 0) {
				String[] projects = setting.split(","); //$NON-NLS-1$
				
				// trim each value
				for (int i = 0; i < projects.length; i++) {
					projects[i] = projects[i].trim();
				}
				
				return projects;
			}
		}
		
		return EMPTY_STRING_ARRAY;
	}
	
	/**
	 * Sets the default deployment descriptor for a project. 
	 * If the default deployment descriptor is null or blank, the default 
	 * deployment descriptor setting is removed from the project.
	 * 
	 * @param project  The project.
	 * @param pathValue  The path of the default deployment descriptor.
	 * @throws BackingStoreException
	 */
	public static void setDefaultDeploymentDescriptor(IProject project, String pathValue) throws BackingStoreException {
		Preferences prefs;
		synchronized (lock) {
			prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(PROJECT_DEFAULT_DEPLOYMENT_DESCRIPTOR);
		}
		
		if (pathValue == null || pathValue.length() == 0) {
			// Remove the setting
			prefs.remove(keyFor(project.getFullPath()));
		}
		else {
			prefs.put(keyFor(project.getFullPath()), pathValue);
		}
		
		synchronized (lock) {
			prefs.flush();
		}
	}
	
	public static String getDefaultDeploymentDescriptor(IResource resource) {
		IProject project = resource.getProject();
		Preferences prefs;
		synchronized (lock) {
			prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(PROJECT_DEFAULT_DEPLOYMENT_DESCRIPTOR);
		}
		
		// First check for the resource. If it doesn't exist, check its parent, then its grandparent, and so on, until at the project level.
		String setting = findSetting(resource.getFullPath(), prefs, true);
		if (setting != null) {
			setting = setting.trim();
			return setting;
		}
		
		return null;
	}	
	/**
	 * Sets the generator argument used to generate the resource. 
	 * 
	 * @param resource    The resource
	 * @param value       The directory string; may be null or blank
	 * @param prefs       The preference store
	 * @param propertyID  The property ID
	 * @throws BackingStoreException
	 */
	public static void setGenerationArgument(IResource resource, String value, IEclipsePreferences prefs, String propertyID) throws BackingStoreException {
		Preferences propertyPrefs;
		synchronized (lock) {
			propertyPrefs = prefs.node(propertyID);
		}
		
		if (value == null || value.length() == 0) {
			// Remove setting
			propertyPrefs.remove(keyFor(resource.getFullPath()));
		}
		else {
			propertyPrefs.put(keyFor(resource.getFullPath()), value);
		}
		
		synchronized (lock) {
			propertyPrefs.flush();
		}
	}	
	
	
	/**
	 * Sets a flag to indicate the project needs to be rebuild
	 * 
	 * @param resource  The resource (a file, folder, or project)
	 * @throws BackingStoreException
	 */
	public static void setBuildFlag(IResource resource) throws BackingStoreException {
		
		if (resource != null) {
			//set flag to rebuild the project containing the resource, the preference change event will be caught by ProjectSettingsListenerManager
			IProject project = resource.getProject();
			Preferences prefs;
			synchronized (lock) {
				prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(EDTCorePreferenceConstants.BUILD_FLAG);
			}
			
			String key = keyFor(resource.getFullPath());
			int buildFlag = prefs.getInt(key, 0); 
			buildFlag++;
			prefs.putInt(key, buildFlag);
			
			synchronized (lock) {
				prefs.flush();
			}
		}
		else {
			//set flag to force a rebuild for projects which inherit workspace compiler & generator setting
			//the preference change event will be caught by EDTCoreIDEPlugin.PreferenceListener
			IPreferenceStore store = EDTCoreIDEPlugin.getPlugin().getPreferenceStore();
			int buildFlag = store.getInt(EDTCorePreferenceConstants.BUILD_FLAG);
			buildFlag++;
			store.setValue(EDTCorePreferenceConstants.BUILD_FLAG, buildFlag);
		}
	}
	
	
	public static String[] getJavaGenerationDirectory(IProject project) {
		return getGenerationDirectoryForLanguage(project, LANGUAGE_JAVA);
	}

	public static String[] getJavaScriptGenerationDirectory(IProject project) {
		return getGenerationDirectoryForLanguage(project, LANGUAGE_JAVASCRIPT);
	}

	public static String getJavaScriptDevGenerationDirectory(IProject project) {
		IGenerator[] generators = getGenerators(project);
		for (int i = 0; i < generators.length; i++) {
			IGenerator generator = generators[i];
			if(generator.getId().equalsIgnoreCase(GENERATOR_ID_JAVASCRIPT_DEV)){
				if(generator instanceof AbstractGenerator){
					AbstractGenerator ideGenerator = (AbstractGenerator)generator;
					return ideGenerator.getOutputDirectory(project);
				}
			}
		}
		return null;
	}
	
	protected static String[] getGenerationDirectoryForLanguage(IProject project, String language) {
		if (language==null) {
			return new String[0];
		}

		HashSet<String> retValue = new HashSet<String>();
		IGenerator[] generators = getProjectGenerators(project);
		
		for (int i = 0; i < generators.length; i++) {
			IGenerator generator = generators[i];
			if(language.equalsIgnoreCase(generator.getLanguage()) &&
					!generator.getId().equalsIgnoreCase(GENERATOR_ID_JAVASCRIPT_DEV)){
				if(generator instanceof AbstractGenerator){
					AbstractGenerator ideGenerator = (AbstractGenerator)generator;
					String[] values = ideGenerator.getPrjojectOutputDirectors(project);
					for ( int j = 0; j < values.length; j ++ ) {
						retValue.add( values[j] );
					}
				}
			}
		}
		return retValue.toArray(new String[0]);
	}

	public static void replaceWorkspaceSettings(IPath oldPath, IPath newPath) throws BackingStoreException {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects) {
			if (project.isAccessible()) {
				replaceWorkspaceSettings(project, oldPath, newPath);
			}
		}
	}
    public static void replaceWorkspaceSettings(IProject project, IPath oldPath, IPath newPath) throws BackingStoreException {
    	Preferences projectPrefs;
    	synchronized (lock) {
    		projectPrefs = Platform.getPreferencesService().getRootNode().node(ProjectScope.SCOPE).node(project.getName());
    	}
    	
    	String[] prefsFiles = getPreferenceNodes( project );
		for (String file : prefsFiles) {
		    Preferences prefs;
		    synchronized (lock) {
		    	prefs = projectPrefs.node(file); 
		    }
		    
	    	String[] names = prefs.childrenNames();
	    	for (String name : names) {
	    	    Preferences nextNode;
	    	    synchronized (lock) {
	    	    	nextNode = prefs.node(name);
	    	    }
	    	    
	    	    String[] keys = nextNode.keys();
	    	    for (String key : keys) {
	    	    	String oldKey = keyFor(oldPath);
	    	    	String oldValue = EclipseUtilities.convertToInternalPath(oldPath.toString());
	    	    	String oldValue1 = EclipseUtilities.convertToInternalPath(oldKey);
	    	    	String value = nextNode.get(key, null);
	    	        if ( pathStartWith( value, oldValue ) ) {
	    	        	String pathStr = EclipseUtilities.convertToInternalPath( newPath.toString() );
	    	        	value = value.replace( oldValue, pathStr );
	    	        	nextNode.put(key, value);
	    	        } else if ( pathStartWith( value, oldPath.toString() ) ) {
	    	        	String pathStr = newPath.toString();
	    	        	value = value.replace( oldPath.toString(), pathStr );
	    	        	nextNode.put(key, value);
	    	        } else if ( project.getFullPath().matchingFirstSegments( oldPath) == 1 && pathStartWith( value, oldValue1 ) ) {
	    	        	String newValue = EclipseUtilities.convertToInternalPath(keyFor(newPath));
	    	        	value = value.replace( oldValue1, newValue );
	    	        	nextNode.put(key, value);
	    	        } else if ( !PROJECT_KEY.equals(key) && pathStartWith( key, oldKey ) ) {
	    	            nextNode.remove(key);
	    	            key = key.replace( oldKey, keyFor(newPath));
	    	            nextNode.put(key, value);
	    	        }
	    	    }
	    	}
	    	
	    	synchronized (lock) {
	    		prefs.flush();
	    	}
    	}
    }
    
	public static void removeWorkspaceSettings(IPath path) throws BackingStoreException {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects) {
			if (project.isAccessible()) {
				removeWorkspaceSettings(project, path);
			}
		}
	}
	
    public static void removeWorkspaceSettings(IProject project, IPath path) throws BackingStoreException {
    	Preferences projectPrefs;
    	synchronized (lock) {
    		projectPrefs = Platform.getPreferencesService().getRootNode().node(ProjectScope.SCOPE).node(project.getName());
    	}
    	
		String[] prefsFiles = getPreferenceNodes( project );
		for (String file : prefsFiles) {
		    Preferences prefs;
		    synchronized (lock) {
		    	prefs = projectPrefs.node(file);
		    }
		    
	    	String[] names = prefs.childrenNames();
	    	for (String name : names) {
	    	    Preferences nextNode;
	    	    synchronized (lock) {
	    	    	nextNode = prefs.node(name);
	    	    }
	    	    String[] keys = nextNode.keys();
	    	    for (String key : keys) {
	    	    	String oldKey = keyFor(path);
	    	    	String oldValue = EclipseUtilities.convertToInternalPath(path.toString());
	    	    	String oldValue1 = EclipseUtilities.convertToInternalPath(oldKey);
	    	    	String value = nextNode.get(key, null);
	    	        if ( (!PROJECT_KEY.equals(oldKey) && key.equals( oldKey )) || pathStartWith( value, oldValue ) || pathStartWith( value, path.toString() ) || (project.getFullPath().matchingFirstSegments( path) == 1 && pathStartWith( value, oldValue1 ) )  ) {
	    	            nextNode.remove( key );
	    	        }
	    	    }
	    	}
	    	
	    	synchronized (lock) {
	    		prefs.flush();
	    	}
    	}
    }
    
    private static boolean pathStartWith( String path1, String path2 ) {
    	return path1.equals( path2 ) || (path1.startsWith( path2 + "/" ) ); //$NON-NLS-1$
    }
	
    private static String[] getPreferenceNodes(IProject project) {
    	ICompiler compiler = getCompiler(project);
    	String[] nodes =  new String[0];
    	if (compiler != null) {
    		nodes = preferenceNodes.get(compiler);
    		if (nodes == null) {
	    		List<org.eclipse.edt.compiler.IGenerator> gens = compiler.getGenerators();
	    		List<String> ids = new ArrayList<String>(gens.size());
	    		if (gens.size() > 0) {
	    			for (org.eclipse.edt.compiler.IGenerator gen : gens) {
	    				if (gen instanceof IGenerator) {
	    					String id = ((IGenerator)gen).getProjectSettingsPluginId();
	    					if (id != null && id.length() > 0 && !ids.contains(id)) {
	    						ids.add(id);
	    					}
	    				}
	    			}
	    		}
	    		nodes = ids.toArray(new String[ids.size()]);
	    		preferenceNodes.put(compiler, nodes);
    		}
    	}
    	String[] newNodes = new String[nodes.length + 1];
    	System.arraycopy( nodes, 0, newNodes, 0, nodes.length );
    	newNodes[nodes.length] = EDTCoreIDEPlugin.PLUGIN_ID;
    	return newNodes;
    }
}
