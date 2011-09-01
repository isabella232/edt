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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.DebugException;
import org.eclipse.edt.ide.rui.internal.testserver.TestServerConfiguration.TerminationListener;

public class TestServerManager implements TerminationListener, IResourceChangeListener {
	
	private Object syncObj;
	private Map<IProject, TestServerConfiguration> serverConfigMap;
	
	public TestServerManager() {
		syncObj = new Object();
		serverConfigMap = new HashMap<IProject, TestServerConfiguration>();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE);
	}
	
	/**
	 * Disposes this manager. Once disposed the manager should not be reused. All of its running servers will be terminated.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		
		synchronized(syncObj) {
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
			case IResourceChangeEvent.PRE_DELETE:
			case IResourceChangeEvent.PRE_CLOSE:
				IResource resource = event.getResource();
				if (resource != null && resource.getType() == IResource.PROJECT) {
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
}
