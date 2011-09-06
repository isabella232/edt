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
package org.eclipse.edt.ide.rui.internal.testserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.ide.rui.internal.testserver.TestServerConfiguration.TerminationListener;
import org.osgi.service.prefs.Preferences;

public class TestServerManager implements TerminationListener, IResourceChangeListener {
	
	private Object syncObj;
	private HashMap<IProject, TestServerConfiguration> serverConfigMap;
	private Map<IProject, DDPreferenceListener> listenerMap;
	
	public TestServerManager() {
		syncObj = new Object();
		serverConfigMap = new HashMap<IProject, TestServerConfiguration>();
		listenerMap = new HashMap<IProject, TestServerManager.DDPreferenceListener>();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE);
		
		// Populate the initial listeners.
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects) {
			if (project.isAccessible()) {
				addDDListener(project);
			}
		}
	}
	
	/**
	 * Disposes this manager. Once disposed the manager should not be reused. All of its running servers will be terminated.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		
		synchronized(syncObj) {
			// Remove the listeners before terminating.
			if (listenerMap != null) {
				for (Entry<IProject, DDPreferenceListener> entry : listenerMap.entrySet()) {
					Preferences prefs = new ProjectScope(entry.getKey()).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(ProjectSettingsUtility.PROJECT_DEFAULT_DEPLOYMENT_DESCRIPTOR);
					if (prefs instanceof IEclipsePreferences) {
						((IEclipsePreferences)prefs).removePreferenceChangeListener(entry.getValue());
					}
				}
				listenerMap = null;
			}
			
			if (serverConfigMap != null) {
				for (TestServerConfiguration config : serverConfigMap.values()) {
					try {
						config.terminate();
					}
					catch (DebugException e) {
					}
				}
				serverConfigMap = null;
			}
		}
	}
	
	private void addDDListener(IProject project) {
		synchronized (syncObj) {
			if (listenerMap == null || listenerMap.containsKey(project)) {
				return;
			}
			Preferences prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(ProjectSettingsUtility.PROJECT_DEFAULT_DEPLOYMENT_DESCRIPTOR);
			if (prefs instanceof IEclipsePreferences) {
				DDPreferenceListener listener = new DDPreferenceListener(project);
				((IEclipsePreferences)prefs).addPreferenceChangeListener(listener);
				listenerMap.put(project, listener);
			}
		}
	}
	
	private void removeDDListener(IProject project) {
		DDPreferenceListener listener;
		synchronized (syncObj) {
			if (listenerMap == null) {
				return;
			}
			listener = listenerMap.remove(project);
		}
		
		if (listener != null) {
			Preferences prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(ProjectSettingsUtility.PROJECT_DEFAULT_DEPLOYMENT_DESCRIPTOR);
			if (prefs instanceof IEclipsePreferences) {
				((IEclipsePreferences)prefs).removePreferenceChangeListener(listener);
			}
		}
	}
	
	/**
	 * @return a test server configuration for the given project. If the configuration didn't yet exist, it will be created.
	 */
	public TestServerConfiguration getServerConfiguration(IProject project, boolean debugMode) {
		TestServerConfiguration config;
		synchronized(syncObj) {
			if (serverConfigMap == null) {
				return null;
			}
			config = serverConfigMap.get(project);
		}
		if (config == null) {
			config = new TestServerConfiguration(project, debugMode);
			config.addTerminationListener(this);
			
			addDDListener(project);
			synchronized(syncObj) {
				serverConfigMap.put(project, config);
			}
		}
		return config;
	}
	
	/**
	 * Removes the configuration for the given project from this manager, if it exists.
	 */
	public void removeServerConfiguration(IProject project) {
		removeDDListener(project);
		synchronized(syncObj) {
			if (serverConfigMap != null) {
				serverConfigMap.remove(project);
			}
		}
	}
	
	@Override
	public void terminated(TestServerConfiguration config) {
		removeServerConfiguration(config.getProject());
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		switch (event.getType()) {
			case IResourceChangeEvent.POST_CHANGE:
				 for (IResourceDelta child : event.getDelta().getAffectedChildren()) {
				    	if ((child.getFlags() & IResourceDelta.OPEN) != 0) {
					    	IResource resource = child.getResource();
					    	// No need to check if the project is open, if it was being closed we wouldn't have gotten here.
							if (resource.getType() == IResource.PROJECT) {
								addDDListener(resource.getProject());
							}
						}
				    	else if (child.getKind() == IResourceDelta.CHANGED) {
				    		IResource resource = child.getResource();
							if (resource.getType() == IResource.PROJECT) {
								addDDListener(resource.getProject());
							}
				    	}
				 }
				break;
				
			case IResourceChangeEvent.PRE_DELETE:
			case IResourceChangeEvent.PRE_CLOSE:
				IResource resource = event.getResource();
				if (resource != null && resource.getType() == IResource.PROJECT) {
					removeDDListener(resource.getProject());
					
					TestServerConfiguration config = null;
					synchronized(syncObj) {
						if (serverConfigMap != null) {
							config = serverConfigMap.remove(resource);
						}
					}
					if (config != null) {
						try {
							config.terminate();
						}
						catch (DebugException e) {
						}
						config.dispose();
					}
				}
				break;
		}
	}
	
	private class DDPreferenceListener implements IPreferenceChangeListener {
		final IProject project;
		
		DDPreferenceListener(IProject project) {
			this.project = project;
		}
		
		@Override
		public void preferenceChange(PreferenceChangeEvent event) {
			// For each config, have it update its mappings if the project is on its EGL path.
			HashMap<IProject, TestServerConfiguration> mapClone;
			synchronized(syncObj) {
				if (serverConfigMap == null) {
					return;
				}
				mapClone = (HashMap)serverConfigMap.clone();
			}
			
			for (TestServerConfiguration config : mapClone.values()) {
				if (config.isOnEGLPath(this.project)) {
					config.updateServiceMappingsOnServer();
				}
			}
		}
	}
}
