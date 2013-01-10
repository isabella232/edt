/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.services.internal.testserver;

import java.util.HashMap;
import java.util.Map;

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
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.ide.testserver.TestServerConfiguration;
import org.osgi.service.prefs.Preferences;

public class DDResourceChangeListener implements IResourceChangeListener {
	
	private final ServicesContribution contrib;
	private final Object syncObj;
	
	private Map<IProject, DefaultDDPreferenceListener> listenerMap;
	
	public DDResourceChangeListener(ServicesContribution contrib) {
		this.contrib = contrib;
		this.syncObj = new Object();
		this.listenerMap = new HashMap<IProject, DDResourceChangeListener.DefaultDDPreferenceListener>();
		
		// Populate the initial listeners.
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects) {
			if (project.isAccessible()) {
				addDDListener(project);
			}
		}
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE);
	}
	
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		
		synchronized (syncObj) {
			for (DefaultDDPreferenceListener listener : listenerMap.values()) {
				removeDDListener(listener);
			}
			this.listenerMap = null;
		}
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		switch (event.getType()) {
			case IResourceChangeEvent.POST_CHANGE:
				if (event.getDelta() != null) {
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
				}
				break;
				
			case IResourceChangeEvent.PRE_DELETE:
			case IResourceChangeEvent.PRE_CLOSE:
				IResource resource = event.getResource();
				if (resource != null && resource.getType() == IResource.PROJECT) {
					removeDDListener(resource.getProject());
				}
				break;
		}
	}
	
	private void addDDListener(IProject project) {
		synchronized (syncObj) {
			if (listenerMap == null || listenerMap.containsKey(project)) {
				return;
			}
			Preferences prefs = new ProjectScope(project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(ProjectSettingsUtility.PROJECT_DEFAULT_DEPLOYMENT_DESCRIPTOR);
			if (prefs instanceof IEclipsePreferences) {
				DefaultDDPreferenceListener listener = new DefaultDDPreferenceListener(project);
				((IEclipsePreferences)prefs).addPreferenceChangeListener(listener);
				listenerMap.put(project, listener);
			}
		}
	}
	
	private void removeDDListener(IProject project) {
		DefaultDDPreferenceListener listener;
		synchronized (syncObj) {
			if (listenerMap == null) {
				return;
			}
			listener = listenerMap.remove(project);
		}
		
		if (listener != null) {
			removeDDListener(listener);
		}
	}
	
	private void removeDDListener(DefaultDDPreferenceListener listener) {
		Preferences prefs = new ProjectScope(listener.project).getNode(EDTCoreIDEPlugin.PLUGIN_ID).node(ProjectSettingsUtility.PROJECT_DEFAULT_DEPLOYMENT_DESCRIPTOR);
		if (prefs instanceof IEclipsePreferences) {
			((IEclipsePreferences)prefs).removePreferenceChangeListener(listener);
		}
	}
	
	/**
	 * When the default DD for a project changes, then for any project with this project on the EGL path we need
	 * to check if the settings should be updated on the server.
	 */
	private class DefaultDDPreferenceListener implements IPreferenceChangeListener {
		private final IProject project;
		
		DefaultDDPreferenceListener(IProject project) {
			this.project = project;
		}
		
		@Override
		public void preferenceChange(PreferenceChangeEvent event) {
			// For each config, have it update its settings if the project is on its EGL path.
			for (TestServerConfiguration config : contrib.getRunningConfigurationsCopy()) {
				if (config.isOnEGLPath(this.project)) {
					contrib.updateDDSettingsOnServer(config);
				}
			}
		}
	}
}
