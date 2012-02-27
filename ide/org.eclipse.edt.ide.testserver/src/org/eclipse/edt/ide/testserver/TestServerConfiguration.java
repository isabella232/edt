/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
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
import org.eclipse.edt.ide.internal.testserver.DefaultServlet;
import org.eclipse.edt.ide.internal.testserver.HotCodeReplaceListener;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.osgi.util.NLS;

/**
 * Manages a Jetty test server. This class is capable of starting and stopping the server, and handles changes to
 * the server configuration automatically. Extensions to the server can be contributed via the testServerExtension
 * extension point.
 */
public class TestServerConfiguration implements IDebugEventSetListener, IResourceChangeListener, IPropertyChangeListener {
	
	public static final int DEFAULT_PORT = 9701;
	
	public static final String TEST_SERVER_CONFIG_TYPE_ID = "org.eclipse.edt.ide.testserver.testServerLaunchType"; //$NON-NLS-1$
	
	private IProject project;
	private boolean debugMode;
	private int port;
	private boolean started;
	private ILaunch launch;
	private List<TerminationListener> terminationListeners;
	private String[] latestCheckedClasspath; // Used to determine when a classpath has changed.
	
	/**
	 * Constructor.
	 * 
	 * @param project    The root project for which this server should run.
	 * @param debugMode  True if the Java process should run in debug mode.
	 */
	public TestServerConfiguration(IProject project, boolean debugMode) {
		this(project, debugMode, -1);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param project    The root project for which this server should run.
	 * @param debugMode  True if the Java process should run in debug mode.
	 * @param port       The port that the server should run on, or -1 if any available port should be used.
	 */
	public TestServerConfiguration(IProject project, boolean debugMode, int port) {
		this.project = project;
		this.debugMode = debugMode;
		this.port = port;
		
		for (AbstractTestServerContribution contrib : TestServerPlugin.getContributions()) {
			contrib.init(this);
		}
	}
	
	/**
	 * Starts the server; if it's already started this will return immediately. If waitForServerToStart is specified,
	 * we will wait up to 60 seconds for it to start before throwing an error.
	 * 
	 * @param monitor  An optional progress monitor (may be null).
	 * @param waitForServerToStart  A flag indicating if this method should wait for the server to finish starting before returning.
	 * @throws CoreException
	 */
	public synchronized void start(IProgressMonitor monitor, boolean waitForServerToStart) throws CoreException {
		if (started) {
			return;
		}
		
		try {
			if (!project.hasNature(JavaCore.NATURE_ID)) {
				throw new CoreException(new Status(IStatus.ERROR, TestServerPlugin.PLUGIN_ID, NLS.bind(TestServerMessages.ProjectMissingJavaNature, project.getName())));
			}
			
			if (port < 0) {
				port = SocketUtil.findOpenPort(DEFAULT_PORT, 5, 100);
				SocketUtil.reservePort(port);
			}
			
			AbstractTestServerContribution[] contributions = TestServerPlugin.getContributions();
			
			// Create a temporary launch configuration, set the project and claspath entries, then run it in either RUN or DEBUG mode.
			ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(TEST_SERVER_CONFIG_TYPE_ID);
			ILaunchConfigurationWorkingCopy copy = type.newInstance(null, NLS.bind(TestServerMessages.TestServerProcessName, new Object[]{project.getName(), port}));
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project.getName());
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, TestServer.class.getCanonicalName());
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
			
			List<String> classpath = copy.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, new ArrayList<String>(10));
			ClasspathUtil.buildClasspath(this, classpath);
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath);
			
			StringBuilder args = new StringBuilder( 100 );
			args.append(copy.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "")); //$NON-NLS-1$
			args.append(" -p "); //$NON-NLS-1$
			args.append(port);
			args.append(" -i "); //$NON-NLS-1$
			args.append(TestServerIDEConnector.getInstance().getPortNumber());
			args.append(" -c \"/"); //$NON-NLS-1$
			args.append(project.getName());
			args.append("\""); //$NON-NLS-1$
			
			if (TestServerPlugin.getDefault().getPreferenceStore().getBoolean(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_ENABLE_DEBUG)) {
				args.append(" -d"); //$NON-NLS-1$
			}
			
			// Append the configurators argument.
			Set<String> configuratorClasses = new HashSet<String>(10);
			for (AbstractTestServerContribution contrib : contributions) {
				String[] classes = contrib.getConfiguratorClassNames(this);
				if (classes != null && classes.length > 0) {
					for (String clazz : classes) {
						configuratorClasses.add(clazz);
					}
				}
			}
			if (configuratorClasses.size() > 0) {
				args.append(" -contribs "); //$NON-NLS-1$
				boolean first = true;
				for (String clazz : configuratorClasses) {
					if (!first) {
						args.append(':');
					}
					args.append(clazz);
				}
			}
			
			// Append contributed args.
			for (AbstractTestServerContribution contrib : contributions) {
				String extraArgs = contrib.getArgumentAdditions(this);
				if (extraArgs != null) {
					args.append(extraArgs);
				}
			}
			
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, args.toString());
			
			// register a listener for the launch to detect when the process is terminated
			DebugPlugin.getDefault().addDebugEventListener(this);
			
			// register a listener for changes to DD files
			ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
			
			launch = copy.launch(debugMode ? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE, monitor);
			latestCheckedClasspath = ClasspathUtil.resolveClasspath(launch.getLaunchConfiguration());
			
			if (waitForServerToStart) {
				// Wait up to 60 seconds for the server to start.
				for (int i = 0; i < 240; i++) {
					try {
						if (invokeServlet(DefaultServlet.SERVLET_PATH, "") == 200) { //$NON-NLS-1$
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
				
				if (!started) {
					try {
						terminate();
					}
					catch (DebugException de) {
					}
					
					throw new CoreException(new Status(IStatus.ERROR, TestServerPlugin.PLUGIN_ID, NLS.bind(TestServerMessages.PingFailed,
							new Object[]{project.getName(), String.valueOf(port)})));
				}
			}
			
			for (IDebugTarget target : launch.getDebugTargets()) {
				IJavaDebugTarget javaTarget = (IJavaDebugTarget)target.getAdapter(IJavaDebugTarget.class);
				if (javaTarget != null) {
					javaTarget.addHotCodeReplaceListener(new HotCodeReplaceListener(this));
				}
			}
			
			TestServerPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		}
		catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, TestServerPlugin.PLUGIN_ID, e.getMessage(), e));
		}
	}
	
	/**
	 * @return true if the server is started.
	 */
	public boolean isRunning() {
		return started;
	}
	
	/**
	 * @return true if the server was started in debug mode.
	 */
	public boolean isDebugMode() {
		return debugMode;
	}
	
	/**
	 * @return the test server's root project.
	 */
	public IProject getProject() {
		return project;
	}
	
	/**
	 * @return the port on which the test server is running.
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Terminates the Java process if it's running.
	 * @throws DebugException
	 */
	public void terminate() throws DebugException {
		if (launch != null) {
			launch.terminate();
		}
	}
	
	/**
	 * Adds a listener to be notified when the server terminates.
	 * @param listener  The listener.
	 */
	public void addTerminationListener(TerminationListener listener) {
		if (terminationListeners == null) {
			terminationListeners = new ArrayList<TerminationListener>();
		}
		terminationListeners.add(listener);
	}
	
	/**
	 * Removes a listener from the list to be notified when the server terminates.
	 * @param listener  The listener.
	 */
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
		TestServerPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		SocketUtil.freePort(port);
		
		// Notify the listeners before we null anything out.
		if (terminationListeners != null) {
			for (TerminationListener listener : terminationListeners) {
				listener.terminated(this);
			}
		}
		
		for (AbstractTestServerContribution contrib : TestServerPlugin.getContributions()) {
			contrib.dispose(this);
		}
		
		this.project = null;
		this.launch = null;
		this.started = false;
		this.terminationListeners = null;
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
	public void propertyChange(PropertyChangeEvent event) {
		if (!started) {
			return;
		}
		
		if (ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_ENABLE_DEBUG.equals(event.getProperty())) {
			try {
				int status = invokeServlet(DefaultServlet.SERVLET_PATH, DefaultServlet.ARG_DEBUG + "=" + event.getNewValue()); //$NON-NLS-1$
				if (status != 200) {
					TestServerPlugin.getDefault().log(NLS.bind(TestServerMessages.DefaultServletBadStatus, new Object[]{status, project.getName()}));
				}
			}
			catch (IOException ioe) {
				
			}
		}
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (!started) {
			return;
		}
		
		for (AbstractTestServerContribution contrib : TestServerPlugin.getContributions()) {
			contrib.resourceChanged(event, this);
		}
	}
	
	/**
	 * Invokes a servlet on the server. Clients may use this method, but be careful about invoking it from the
	 * UI thread (e.g. if you have a breakpoint in the test server preventing the response from being sent, you'll
	 * have a frozen workbench).
	 * 
	 * @param servletPath  Path to the servlet, NOT prefixed with a '/'
	 * @param args         Arguments to be included in the request
	 * @return the response code from jetty
	 * @throws IOException
	 */
	public int invokeServlet(String servletPath, String args) throws IOException {
		String projectName = project.getName();
		try {
			projectName = URLEncoder.encode(projectName, "UTF-8"); //$NON-NLS-1$
		}
		catch (UnsupportedEncodingException e) {
			// Shouldn't happen.
		}
		
		URLConnection conn = new URL("http://localhost:" + port + "/" + projectName + "/" + servletPath).openConnection(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
	
	/**
	 * @return true if the given project is on the EGL path of this test server.
	 */
	public boolean isOnEGLPath(IProject project) {
		return isOnEGLPath(this.project, project, new HashSet<IProject>());
	}
	
	public boolean isOnEGLPath(IProject currProject, IProject deltaProject, Set<IProject> seen) {
		if (seen.contains(currProject)) {
			return false;
		}
		seen.add(currProject);
		
		if (currProject.equals(deltaProject)) {
			return true;
		}
		
		try {
			if (currProject.hasNature(EGLCore.NATURE_ID)) {
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
		}
		catch (EGLModelException e) {
			TestServerPlugin.getDefault().log(e);
		}
		catch (CoreException e ) {
			TestServerPlugin.getDefault().log(e);
		}
		
		return false;
	}
	
	/**
	 * Causes this config to recompute its classpath and see if it's different from what's currently being used. If the server hasn't
	 * been started yet, this will return false.
	 * 
	 * @return true if the classpath has changed since the server launch.
	 */
	public boolean hasClasspathChanged() {
		if (!started || this.launch == null || this.launch.getLaunchConfiguration() == null) {
			return false;
		}
		
		boolean result = false;
		try {
			// Create a launch configuration in the same way as when launching the server, but only set the attributes that would affect the classpath.
			ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(
					IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunchConfigurationWorkingCopy copy = type.newInstance(null, "ezeTemp"); //$NON-NLS-1$
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project.getName());
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
			
			List<String> newClasspath = copy.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, new ArrayList<String>(10));
			ClasspathUtil.buildClasspath(this, newClasspath);
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, newClasspath);
			
			String[] newResolvedClasspath = ClasspathUtil.resolveClasspath(copy);
			
			// Order DOES matter in a classpath, in the case of duplicate qualified class names. The arrays must be exactly equal to be considered unchanged.
			result = !Arrays.equals(latestCheckedClasspath, newResolvedClasspath);
			latestCheckedClasspath = newResolvedClasspath;
		}
		catch (CoreException ce) {
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return "Test server: project=" + (project == null ? "null" : project.getName())+ ", port=" + port + ", started=" + started; 
	}
	
	/**
	 * Allows clients to be notified when this server configuration terminates.
	 */
	public static interface TerminationListener {
		public void terminated(TestServerConfiguration config);
	}
}
