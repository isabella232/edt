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

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.rui.internal.testserver.ServiceFinder.RestServiceMapping;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.osgi.util.NLS;

/**
 * Manages a Jetty test server. This class is capable of starting and stopping the server, and handles changes to
 * the server configuration automatically.
 */
public class TestServerConfiguration implements IDebugEventSetListener, IResourceChangeListener {
	
	public static final int DEFAULT_PORT = 9701;
	
	private IProject project;
	private boolean debugMode;
	private int port;
	private boolean started;
	private ILaunch launch;
	private List<TerminationListener> terminationListeners;
	
	private Map<String,RestServiceMapping> currentServiceMappings;
	
	public TestServerConfiguration(IProject project, boolean debugMode) {
		this(project, debugMode, -1);
	}
	
	public TestServerConfiguration(IProject project, boolean debugMode, int port) {
		this.project = project;
		this.debugMode = debugMode;
		this.port = port;
	}
	
	public synchronized void start(IProgressMonitor monitor, boolean waitForServerToStart) throws CoreException {
		if (started) {
			return;
		}
		
		try {
			if (port < 0) {
				port = SocketUtil.findOpenPort(DEFAULT_PORT, 5, 100);
			}
			
			// Create a temporary launch configuration, set the project and claspath entries, then run it in either RUN or DEBUG mode.
			ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(
					IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunchConfigurationWorkingCopy copy = type.newInstance(null, NLS.bind(TestServerMessages.TestServerProcessName, new Object[]{project.getName(), port}));
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project.getName());
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, TestServer.class.getCanonicalName());
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, ClasspathUtil.buildClasspath(project, copy));
			
			StringBuilder args = new StringBuilder( 100 );
			args.append(copy.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "")); //$NON-NLS-1$
			args.append(" -p "); //$NON-NLS-1$
			args.append(port);
			args.append(" -c \"/"); //$NON-NLS-1$
			args.append(project.getName());
			args.append("\" -s \""); //$NON-NLS-1$
			currentServiceMappings = ServiceFinder.findRestServices(project);
			args.append(ServiceFinder.toArgumentString(currentServiceMappings.values()));
			args.append("\""); //$NON-NLS-1$
//			args.append(" -d"); // uncomment this line to enable debug messages in the console
			
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, args.toString());
			
			// register a listener for the launch to detect when the process is terminated
			DebugPlugin.getDefault().addDebugEventListener(this);
			
			// register a listener for changes to DD files
			ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
			
			launch = copy.launch(debugMode ? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE, monitor);
			
			if (waitForServerToStart) {
				// Wait up to 10 seconds for the server to start.
				for (int i = 0; i < 40; i++) {
					try {
						if (invokeConfigServlet("") == 200) { //$NON-NLS-1$
							started = true;
							break;
						}
					}
					catch (IOException e) {
					}
					
					try {
						Thread.sleep(250);
					}
					catch (InterruptedException e) {
					}
				}
			}
			
			for (IDebugTarget target : launch.getDebugTargets()) {
				IJavaDebugTarget javaTarget = (IJavaDebugTarget)target.getAdapter(IJavaDebugTarget.class);
				if (javaTarget != null) {
					javaTarget.addHotCodeReplaceListener(new TestServerHotCodeReplaceListener(this));
				}
			}
		}
		catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
		}
	}
	
	public boolean isRunning() {
		return started;
	}
	
	public boolean isDebugMode() {
		return debugMode;
	}
	
	public IProject getProject() {
		return project;
	}
	
	public int getPort() {
		return port;
	}
	
	public void terminate() throws DebugException {
		if (launch != null) {
			launch.terminate();
		}
	}
	
	public void addTerminationListener(TerminationListener listener) {
		if (terminationListeners == null) {
			terminationListeners = new ArrayList<TerminationListener>();
		}
		terminationListeners.add(listener);
	}
	
	public void removeTerminationListener(TerminationListener listener) {
		if (terminationListeners != null) {
			terminationListeners.remove(listener);
		}
	}
	
	/**
	 * Disposes this server configuration. Once disposed this configuration should not be reused.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		DebugPlugin.getDefault().removeDebugEventListener(this);
		
		// Notify the listeners before we null anything out.
		if (terminationListeners != null) {
			for (TerminationListener listener : terminationListeners) {
				listener.terminated(this);
			}
		}
		
		this.project = null;
		this.launch = null;
		this.started = false;
		terminationListeners = null;
	}
	
	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		if (!started) {
			// Don't care about events if we're not started.
			return;
		}
		
		if (events == null || events.length == 0) {
			return;
		}
		
		for (DebugEvent event : events) {
			if (event.getKind() == DebugEvent.TERMINATE && event.getSource() instanceof IAdaptable) {
				ILaunch launch = (ILaunch)((IAdaptable)event.getSource()).getAdapter(ILaunch.class);
				if (launch == this.launch) {
					for (IProcess process : launch.getProcesses()) {
						if (process.isTerminated()) {
							dispose();
							return;
						}
					}
				}
			}
		}
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (!started) {
			return;
		}
		// We need to recalculate all the bindings if even 1 DD file in the EGL path changed. It could be that there
		// were duplicate URIs among the files, and a URI that was included before has now been changed/removed, so
		// the duplicate that was previously skipped needs to be used (replacing the mapping on the server).
		final boolean[] recompute = {false};
		
		try {
			event.getDelta().accept(new IResourceDeltaVisitor() {
				@Override
				public boolean visit(IResourceDelta delta) throws CoreException {
					if (recompute[0]) {
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
							if ("egldd".equals(delta.getResource().getFileExtension()) //$NON-NLS-1$
									&& isOnEGLPath(project, delta.getResource().getProject(), new HashSet<IProject>())) {
								recompute[0] = true;
								return false;
							}
							break;
					}
					return true;
				}
			});
		}
		catch (CoreException e) {
			Activator.getDefault().log(e.getMessage(), e);
		}
		
		if (recompute[0]) {
			// Iterate through, keep track of those that are no longer in the list, those that were already in the list but have
			// changed, and those that are new to the list. Updates are treated as additions, and the server will handle it appropriately.
			
			Map<String,RestServiceMapping> newMappings = ServiceFinder.findRestServices(project);
			List<RestServiceMapping> addedOrChanged = new ArrayList<RestServiceMapping>();
			Map<String,RestServiceMapping> copyOfCurrent = new HashMap<String,RestServiceMapping>(currentServiceMappings);
			
			for (Iterator<RestServiceMapping> it = newMappings.values().iterator(); it.hasNext();) {
				RestServiceMapping next = it.next();
				RestServiceMapping old = copyOfCurrent.remove(next.uri);
				if (old == null || !old.equals(next)) {
					addedOrChanged.add(next);
				}
			}
			
			if (addedOrChanged.size() > 0 || copyOfCurrent.size() > 0) {
				String addedArg = ServiceFinder.toArgumentString(addedOrChanged);
				String removedArg = ServiceFinder.toArgumentString(copyOfCurrent.values()); // leftovers get removed
				
				StringBuilder args = new StringBuilder(addedArg.length() + removedArg.length() + 30);
				if (addedArg.length() > 0) {
					args.append(ConfigServlet.ARG_ADDED);
					args.append('=');
					args.append(addedArg);
				}
				if (removedArg.length() > 0) {
					if (args.length() > 0) {
						args.append('&');
					}
					args.append(ConfigServlet.ARG_REMOVED);
					args.append('=');
					args.append(removedArg);
				}
				
				try {
					int status = invokeConfigServlet(args.toString());
					if (status != 200) {
						System.err.println(NLS.bind(TestServerMessages.ConfigServletBadStatus, status));
					}
				}
				catch (IOException e) {
					Activator.getDefault().log(e.getMessage(), e);
				}
			}
			
			currentServiceMappings = newMappings;
		}
	}
	
	private int invokeConfigServlet(String args) throws IOException {
		URLConnection conn = new URL("http://localhost:" + port + "/" + project.getName() + ConfigServlet.SERVLET_PATH).openConnection(); //$NON-NLS-1$ //$NON-NLS-2$
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept-Charset", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
		
		OutputStream output = null;
		try {
		     output = conn.getOutputStream();
		     output.write(args.getBytes("UTF-8")); //$NON-NLS-1$
		}
		finally {
			if (output != null) {
				try {
					output.close();
				}
				catch (IOException logOrIgnore) {
				}
			}
		}
		return ((HttpURLConnection)conn).getResponseCode();
	}
	
	private boolean isOnEGLPath(IProject currProject, IProject deltaProject, Set<IProject> seen) {
		if (seen.contains(currProject)) {
			return false;
		}
		seen.add(currProject);
		
		if (currProject.equals(deltaProject)) {
			return true;
		}
		
		try {
			IEGLProject eglProject = EGLCore.create(currProject);
			
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			for (IEGLPathEntry entry : eglProject.getResolvedEGLPath(true)) {
				if (entry.getEntryKind() == IEGLPathEntry.CPE_PROJECT) {
					IResource resource = root.findMember(entry.getPath());
					if (resource != null && resource.getType() == IResource.PROJECT && resource.isAccessible()) {
						if (isOnEGLPath((IProject)resource, deltaProject, seen)) {
							return true;
						}
					}
				}
			}
		}
		catch (EGLModelException e) {
			Activator.getDefault().log(e.getMessage(), e);
		}
		
		return false;
	}
	
	/**
	 * Allows clients to be notified when this server configuration terminates.
	 */
	public static interface TerminationListener {
		public void terminated(TestServerConfiguration config);
	}
}
