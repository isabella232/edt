/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.edt.ide.deployment.services.internal.testserver.DeploymentDescriptorFinder.DDFile;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLUtility;
import org.eclipse.edt.ide.testserver.AbstractTestServerContribution;
import org.eclipse.edt.ide.testserver.ClasspathUtil;
import org.eclipse.edt.ide.testserver.TestServerConfiguration;
import org.eclipse.edt.ide.testserver.TestServerIDEConnector;
import org.eclipse.edt.ide.testserver.TestServerPlugin;
import org.eclipse.edt.javart.services.servlet.proxy.RuiBrowserHttpRequest;
import org.eclipse.jst.server.core.internal.JavaServerPlugin;
import org.eclipse.jst.server.core.internal.RuntimeClasspathContainer;
import org.eclipse.jst.server.core.internal.RuntimeClasspathProviderWrapper;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * Provides support on the test server for desployment descriptors, services, and SQL including JNDI.
 */
@SuppressWarnings("restriction")
public class ServicesContribution extends AbstractTestServerContribution {
	
	private final Object syncObj;
	
	private ArrayList<TestServerConfiguration> runningConfigurations;
	private Map<TestServerConfiguration, String> currentDefaultDDName;
	private Map<TestServerConfiguration, List<DDFile>> currentDDFiles;
	private Map<TestServerConfiguration, String> currentDDNameOrder;
	private DDResourceChangeListener ddListener;
	
	public ServicesContribution() {
		this.syncObj = new Object();
		this.runningConfigurations = new ArrayList<TestServerConfiguration>();
		this.currentDDFiles = new HashMap<TestServerConfiguration, List<DDFile>>();
		this.currentDefaultDDName = new HashMap<TestServerConfiguration, String>();
		this.currentDDNameOrder = new HashMap<TestServerConfiguration, String>();
		this.ddListener = new DDResourceChangeListener(this);
	}
	
	@Override
	public void dispose() {
		this.ddListener.dispose();
		
		this.runningConfigurations = null;
		this.currentDDFiles = null;
		this.currentDefaultDDName = null;
		this.currentDDNameOrder = null;
		this.ddListener = null;
	}
	
	@Override
	public void init(TestServerConfiguration config) {
		synchronized (syncObj) {
			runningConfigurations.add(config);
		}
	}
	
	@Override
	public void dispose(TestServerConfiguration config) {
		synchronized (syncObj) {
			runningConfigurations.remove(config);
			currentDDFiles.remove(config);
			currentDefaultDDName.remove(config);
			currentDDNameOrder.remove(config);
		}
	}
	
	@Override
	public String[] getConfiguratorClassNames(TestServerConfiguration config) {
		return new String[]{ServicesConfigurator.class.getCanonicalName()};
	}
	
	@Override
	public String getArgumentAdditions(TestServerConfiguration config) {
		StringBuilder buf = new StringBuilder(100);
		
		buf.append(" -dd \""); //$NON-NLS-1$
		List<DDFile> ddFiles = DeploymentDescriptorFinder.findDeploymentDescriptors(config.getProject());
		buf.append(DeploymentDescriptorFinder.toArgumentString(ddFiles));
		
		buf.append("\" -ddd \""); //$NON-NLS-1$
		String defaultDDName = DeploymentDescriptorFinder.getDefaultDDName(config.getProject());
		buf.append(defaultDDName);
		
		String orderedDDNames = DeploymentDescriptorFinder.toOrderedArgumentString(ddFiles);
		buf.append("\" -odd \""); //$NON-NLS-1$
		buf.append(orderedDDNames);
		buf.append("\""); //$NON-NLS-1$
		
		synchronized (syncObj) {
			currentDDFiles.put(config, ddFiles);
			currentDefaultDDName.put(config, defaultDDName);
			currentDDNameOrder.put(config, orderedDDNames);
		}
		
		return buf.toString();
	}
	
	@Override
	public String[] getClasspathAdditions(TestServerConfiguration config) {
		List<String> entries = new ArrayList<String>(2);
		String entry = ClasspathUtil.getClasspathEntry("org.eclipse.edt.ide.deployment.core"); //$NON-NLS-1$
		if (entry != null) {
			entries.add(entry);
		}
		
		entry = ClasspathUtil.getClasspathEntry("org.eclipse.edt.ide.deployment.services"); //$NON-NLS-1$
		if (entry != null) {
			entries.add(entry);
		}
		
		entry = ClasspathUtil.getClasspathEntry("org.eclipse.edt.runtime.java"); //$NON-NLS-1$
		if (entry != null) {
			entries.add(entry);
		}
		
		entry = ClasspathUtil.getClasspathEntry("com.ibm.icu"); //$NON-NLS-1$
		if (entry != null) {
			entries.add(entry);
		}
		
		DDUtil.addJDBCJars(config.getProject(), new HashSet<IProject>(), new HashSet<IResource>(), entries);
		
		// Add a Tomcat runtime if one's available, so that JNDI can use connection pooling.
		IRuntime bestTomcat = null;
		for (IRuntime rt : ServerUtil.getRuntimes(null, null)) {
			if (rt.getRuntimeType().getName().toLowerCase().contains("tomcat")) { //$NON-NLS-1$
				if (bestTomcat == null) {
					bestTomcat = rt;
				}
				else if (bestTomcat.getRuntimeType().getVersion().compareTo(rt.getRuntimeType().getVersion()) < 0) {
					bestTomcat = rt;
				}
			}
		}
		
		if (bestTomcat != null) {
			RuntimeClasspathProviderWrapper rcpw = JavaServerPlugin.findRuntimeClasspathProvider(bestTomcat.getRuntimeType());
			if (rcpw != null) {
				entries.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeClasspathEntry containerPath=\"" //$NON-NLS-1$
						+ RuntimeClasspathContainer.SERVER_CONTAINER + "/" + rcpw.getId() + "/" + bestTomcat.getId() //$NON-NLS-1$ //$NON-NLS-2$
						+ "\" path=\"3\" type=\"4\"/>"); //$NON-NLS-1$
			}
		}
		
		return entries.toArray(new String[entries.size()]);
	}
	
	@Override
	public boolean handleServerRequest(Socket socket, PrintStream ps) throws Exception {
		RuiBrowserHttpRequest request = RuiBrowserHttpRequest.createNewRequest(socket);
		if (request.getContentArguments().containsKey("connectionProfile")) { //$NON-NLS-1$
			handleConnectionProfileRequest(request, ps);
			return true;
		}
		return false;
	}
	
	/**
	 * Given the name of a connection profile, this returns its url, username, password, schema, and driver class name.
	 */
	private void handleConnectionProfileRequest(RuiBrowserHttpRequest request, PrintStream ps) throws Exception {
		String url = null;
		String user = null;
		String pass = null;
		String schema = null;
		String className = null;
		String profileName = request.getContentArguments().get("connectionProfile"); //$NON-NLS-1$
		IConnectionProfile profile = EGLSQLUtility.getConnectionProfile(profileName);
		if (profile != null) {
			url = EGLSQLUtility.getSQLConnectionURLPreference(profile);
			user = EGLSQLUtility.getSQLUserId(profile);
			pass = EGLSQLUtility.getSQLPassword(profile);
			schema = EGLSQLUtility.getDefaultSchema(profile);
			className = EGLSQLUtility.getSQLJDBCDriverClassPreference(profile);
		}
		
		if (url == null) {
			url = ""; //$NON-NLS-1$
		}
		if (user == null) {
			user = ""; //$NON-NLS-1$
		}
		if (pass == null) {
			pass = ""; //$NON-NLS-1$
		}
		if (schema == null) {
			schema = ""; //$NON-NLS-1$
		}
		if (className == null) {
			className = ""; //$NON-NLS-1$
		}
		
		String info;
		try {
			info = URLEncoder.encode(url, "UTF-8") + ';' + URLEncoder.encode(user, "UTF-8") + ';' //$NON-NLS-1$ //$NON-NLS-2$
					 + URLEncoder.encode(pass, "UTF-8") + ';' + URLEncoder.encode(schema, "UTF-8") + ';' //$NON-NLS-1$ //$NON-NLS-2$
					 + URLEncoder.encode(className, "UTF-8"); //$NON-NLS-1$
		}
		catch (UnsupportedEncodingException e) {
			// Shouldn't happen.
			info = url + ';' + user + ';' + pass + ';' + schema + ';' + className;
		}
		
		ps.print(TestServerIDEConnector.getGoodResponseHeader(request.getURL(), "application/x-www-form-urlencoded;charset=UTF-8", false)); //$NON-NLS-1$
		try {
			ps.write(info.getBytes("utf-8")); //$NON-NLS-1$
		}
		catch (UnsupportedEncodingException e) {
			// Shouldn't happen.
			ps.write(info.getBytes());
		}
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event, final TestServerConfiguration config) {
		// We need to recalculate all the DD settings if even 1 DD file in the EGL path changed. It could be that there
		// were duplicate settings among the files, and a setting that was included before has now been changed/removed, so
		// the duplicate that was previously skipped needs to be used (replacing the setting on the server).
		try {
			class RecomputeSettings extends RuntimeException {private static final long serialVersionUID = 1L;}; // For fast exit of delta visitor
			try {
				if (event.getDelta() != null) {
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
									if ("egldd".equalsIgnoreCase(delta.getFullPath().getFileExtension())) { //$NON-NLS-1$
										for (DDFile dd : currentDDFiles.get(config)) {
											if (dd.path.equals(delta.getResource().getLocation().toOSString())) {
												throw new RecomputeSettings();
											}
										}
									}
									break;
							}
							return true;
						}
					});
				}
			}
			catch (RecomputeSettings e) {
				new Thread() {
					public void run() {
						updateDDSettingsOnServer(config);
					};
				}.start();
			}
		}
		catch (CoreException e) {
			TestServerPlugin.getDefault().log(e);
		}
	}
	
	public synchronized void updateDDSettingsOnServer(TestServerConfiguration config) {
		String oldDDNameOrder;
		String oldDefaultDD;
		List<DDFile> oldDDs;
		
		synchronized (syncObj) {
			oldDefaultDD = currentDefaultDDName.get(config);
			oldDDNameOrder = currentDDNameOrder.get(config);
			oldDDs = currentDDFiles.get(config);
		}
		
		String defaultDD = DeploymentDescriptorFinder.getDefaultDDName(config.getProject());
		boolean defaultDDChanged = !oldDefaultDD.equals(defaultDD);
		
		// Iterate through, keep track of those that are no longer in the list, those that were already in the list but have
		// changed, and those that are new to the list. Updates are treated as additions, and the server will handle it appropriately.
		List<DDFile> newDDFiles = DeploymentDescriptorFinder.findDeploymentDescriptors(config.getProject());
		List<DDFile> addedOrChangedDDFiles = new ArrayList<DDFile>();
		List<DDFile> copyOfCurrentDDFiles = new ArrayList<DDFile>(oldDDs);
		for (DDFile next : newDDFiles) {
			int size = copyOfCurrentDDFiles.size();
			DDFile old = null;
			for (int i = 0; i < size; i++) {
				if (next.name.equals(copyOfCurrentDDFiles.get(i).name)) {
					old = copyOfCurrentDDFiles.remove(i);
					break;
				}
			}
			if (old == null || !old.equals(next)) {
				addedOrChangedDDFiles.add(next);
			}
		}
		
		String newDDNameOrder = DeploymentDescriptorFinder.toOrderedArgumentString(newDDFiles);
		boolean ddOrderChanged = !oldDDNameOrder.equals(newDDNameOrder);
		
		
		if (defaultDDChanged || ddOrderChanged || addedOrChangedDDFiles.size() > 0 || copyOfCurrentDDFiles.size() > 0) {
			String addedDDArg = DeploymentDescriptorFinder.toArgumentString(addedOrChangedDDFiles);
			String removedDDArg = DeploymentDescriptorFinder.toArgumentString(copyOfCurrentDDFiles); // leftovers get removed
			
			StringBuilder args = new StringBuilder(addedDDArg.length() + newDDNameOrder.length() + removedDDArg.length() + defaultDD.length() + 50);
			if (addedDDArg.length() > 0) {
				if (args.length() > 0) {
					args.append('&');
				}
				args.append(ConfigServlet.ARG_DD_ADDED);
				args.append('=');
				args.append(addedDDArg);
			}
			if (removedDDArg.length() > 0) {
				if (args.length() > 0) {
					args.append('&');
				}
				args.append(ConfigServlet.ARG_DD_REMOVED);
				args.append('=');
				args.append(removedDDArg);
			}
			if (defaultDDChanged) {
				if (args.length() > 0) {
					args.append('&');
				}
				args.append(ConfigServlet.ARG_DEFAULT_DD);
				args.append('=');
				try {
					args.append(URLEncoder.encode(defaultDD, "UTF-8")); //$NON-NLS-1$
				}
				catch(UnsupportedEncodingException e) {
					// Shouldn't happen.
					args.append(defaultDD);
				}
			}
			if (ddOrderChanged) {
				if (args.length() > 0) {
					args.append('&');
				}
				args.append(ConfigServlet.ARG_ORDERED_DDS);
				args.append('=');
				try {
					args.append(URLEncoder.encode(newDDNameOrder, "UTF-8")); //$NON-NLS-1$
				}
				catch(UnsupportedEncodingException e) {
					// Shouldn't happen.
					args.append(newDDNameOrder);
				}
			}
			
			try {
				int status = config.invokeServlet(ConfigServlet.SERVLET_PATH, args.toString());
				if (status != 200) {
					TestServerPlugin.getDefault().log(NLS.bind(Messages.ConfigServletBadStatus, status));
				}
			}
			catch (IOException e) {
				TestServerPlugin.getDefault().log(e);
			}
		}
		
		synchronized (syncObj) {
			currentDDNameOrder.put(config, newDDNameOrder);
			currentDDFiles.put(config, newDDFiles);
			currentDefaultDDName.put(config, defaultDD);
		}
	}
	
	public List<TestServerConfiguration> getRunningConfigurationsCopy() {
		synchronized (syncObj) {
			return (List<TestServerConfiguration>)runningConfigurations.clone();
		}
	}
}
