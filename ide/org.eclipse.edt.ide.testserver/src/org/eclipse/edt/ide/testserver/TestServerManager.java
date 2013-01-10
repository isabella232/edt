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
package org.eclipse.edt.ide.testserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.edt.ide.internal.testserver.UpdateErrorDialog;
import org.eclipse.edt.ide.testserver.TestServerConfiguration.TerminationListener;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

/**
 * Keeps track of the test server instances, created new ones when needed. Also handles notifying the user when
 * the classpath for one or more servers has changed. This class should be used instead of directly instantiating
 * {@link TestServerConfiguration}.
 */
public class TestServerManager implements TerminationListener, IResourceChangeListener {
	
	private static TestServerManager INSTANCE;
	
	private final Object syncObj;
	private final HashMap<IProject, TestServerConfiguration> serverConfigMap;
	
	public static synchronized TestServerManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TestServerManager();
		}
		return INSTANCE;
	}
	
	private TestServerManager() {
		syncObj = new Object();
		serverConfigMap = new HashMap<IProject, TestServerConfiguration>();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE);
	}
	
	/**
	 * Resets this manager. All running servers will be terminated.
	 */
	public void reset() {
		synchronized(syncObj) {
			for (TestServerConfiguration config : serverConfigMap.values()) {
				try {
					config.terminate();
				}
				catch (DebugException e) {
				}
			}
			serverConfigMap.clear();
		}
	}
	
	/**
	 * @return a test server configuration for the given project. If the configuration didn't yet exist, it will be created.
	 */
	public TestServerConfiguration getServerConfiguration(IProject project, boolean debugMode) {
		synchronized(syncObj) {
			TestServerConfiguration config = serverConfigMap.get(project);
			if (config == null) {
				config = new TestServerConfiguration(project, debugMode);
				config.addTerminationListener(this);
				serverConfigMap.put(project, config);
			}
			return config;
		}
	}
	
	/**
	 * @return the corresponding test server configuration for the given project, or null if one does not exist.
	 */
	public TestServerConfiguration lookupConfiguration(IProject project) {
		synchronized(syncObj) {
			return serverConfigMap.get(project);
		}
	}
	
	/**
	 * Removes the configuration for the given project from this manager, if it exists.
	 */
	public void removeServerConfiguration(IProject project) {
		synchronized(syncObj) {
			serverConfigMap.remove(project);
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
				if (event.getDelta() != null) {
					int mapSize;
					synchronized (syncObj) {
						mapSize = serverConfigMap.size();
					}
					
					if (mapSize > 0) {
						class CheckClasspath extends RuntimeException {private static final long serialVersionUID = 1L;}; // For fast exit of delta visitor
						try {
							event.getDelta().accept(new IResourceDeltaVisitor() {
								@Override
								public boolean visit(IResourceDelta delta) throws CoreException {
									if (delta == null) {
										return false;
									}
									switch (delta.getKind()) {
										case IResourceDelta.CHANGED:
											if ((delta.getFlags() & IResourceDelta.CONTENT) == 0 &&
													(delta.getFlags() & IResourceDelta.ENCODING) == 0) {
												// No actual change, skip it.
												return true;
											}
											// Fall through.
										case IResourceDelta.ADDED:
										case IResourceDelta.REMOVED:
											if (ClasspathUtil.canAffectClasspath(delta.getFullPath().lastSegment())) {
												throw new CheckClasspath();
											}
											break;
									}
									return true;
								}
							});
							
						}
						catch (CoreException ce) {
							TestServerPlugin.getDefault().log(ce.getMessage(), ce);
						}
						catch (CheckClasspath cc) {
							handleClasspathChanged();
						}
					}
				}
				break;
				
			case IResourceChangeEvent.PRE_DELETE:
			case IResourceChangeEvent.PRE_CLOSE:
				IResource resource = event.getResource();
				if (resource != null && resource.getType() == IResource.PROJECT) {
					TestServerConfiguration config = null;
					synchronized(syncObj) {
						config = serverConfigMap.remove(resource);
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
	
	/**
	 * We check for classpath changes on all test servers at once so that you don't get several dialogs
	 * when a classpath change affects multiple servers. Instead, you get one that lists all the affected
	 * test servers.
	 */
	private void handleClasspathChanged() {
		final List<TestServerConfiguration> configsWithChanges = new ArrayList<TestServerConfiguration>();
		HashMap<IProject, TestServerConfiguration> mapClone;
		synchronized(syncObj) {
			mapClone = (HashMap)serverConfigMap.clone();
		}
		
		if (mapClone != null) {
			for (TestServerConfiguration config : mapClone.values()) {
				if (config.hasClasspathChanged()) {
					configsWithChanges.add(config);
				}
			}
		}
		
		if (configsWithChanges.size() > 0) {
			switch (TestServerPlugin.getDefault().getPreferenceStore().getInt(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_CLASSPATH_CHANGED)) {
				case ITestServerPreferenceConstants.TESTSERVER_IGNORE:
					break;
				case ITestServerPreferenceConstants.TESTSERVER_TERMINATE:
					for (final TestServerConfiguration config : configsWithChanges) {
						try {
							config.terminate();
						}
						catch (final DebugException e) {
							final Display display = TestServerPlugin.getDisplay();
							display.asyncExec(new Runnable() {
								@Override
								public void run() {
									ErrorDialog.openError(TestServerPlugin.getShell(), TestServerMessages.TerminateFailedTitle, 
											NLS.bind(TestServerMessages.TerminateFailedMsg, config.getProject().getName()),
											e.getStatus());
								}
							});
						}
					}
					break;
				case ITestServerPreferenceConstants.TESTSERVER_PROMPT:
				default:
					final Display display = TestServerPlugin.getDisplay();
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							if (display.isDisposed()) {
								return;
							}
							
							StringBuilder projectsInsert = new StringBuilder(20 * configsWithChanges.size());
							for (TestServerConfiguration config : configsWithChanges) {
								if (projectsInsert.length() != 0) {
									projectsInsert.append('\n');
								}
								projectsInsert.append(config.getProject().getName());
							}
							
							UpdateErrorDialog dialog = new UpdateErrorDialog(
									TestServerPlugin.getShell(), TestServerMessages.ClasspathChangedTitle, null, 
									new Status(IStatus.WARNING, TestServerPlugin.PLUGIN_ID, NLS.bind(TestServerMessages.ClasspathChangedMsg, projectsInsert) + "\n\n" //$NON-NLS-1$
											+ (configsWithChanges.size() > 1 ? TestServerMessages.ErrorDialogTerminatePluralMsg : TestServerMessages.ErrorDialogTerminateMsg)),
											TestServerPlugin.getDefault().getPreferenceStore(),
									ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_CLASSPATH_CHANGED,
									configsWithChanges.toArray(new TestServerConfiguration[configsWithChanges.size()]));
							dialog.open();
						}
					});
					break;
			}
		}
	}
}
