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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.ide.internal.testserver.ContributionConfiguration;
import org.eclipse.edt.ide.internal.testserver.Logger;
import org.eclipse.edt.ide.internal.testserver.DefaultServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Jetty server that's used for testing out Java applications before they're deployed. The applications,
 * such as services, are run directly in their Java project.
 */
public class TestServer {
	
	private boolean debug;
	
	private final Integer port;
	
	private final String contextRoot;
	
	private final List<AbstractConfigurator> configurators;
	
	private Server jettyServer;
	
	private WebAppContext webApp;
	
	private boolean ready;
	
	/**
	 * Required arguments:
	 * <ul>
	 * <li>-p &lt;port number&gt; (the server's port number)</li>
	 * <li>-i &lt;IDE port number&gt; (the port number over which the IDE can be reached)</li>
	 * <li>-c &lt;context root&gt; (the server's context root)</li>
	 * </ul>
	 * 
	 * Optional arguments:
	 * <ul>
	 * <li>-d (enables debug output)</li>
	 * </ul>
	 * 
	 * Additional arguments may be passed for registered configurators.
	 * @see {@link AbstractConfigurator}
	 */
	public static void main(String[] args) throws Exception {
		List<String> remainingArgs = new ArrayList<String>(args.length);
		String[] contributionClassNames = null;
		Integer port = null;
		String contextRoot = null;
		boolean debug = false;
		
		// First-pass through args is to gather the contributions and any core arg values.
		for (int i = 0; i < args.length; i++) {
			if ("-p".equals(args[i])) { //$NON-NLS-1$
				if (i + 1 < args.length) {
					try {
						port = Integer.parseInt(args[i+1]);
						i++;
					}
					catch (NumberFormatException e) {
						logWarning("Unable to parse port value \"" + args[i+1] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				else {
					logWarning("Missing port value for argument \"" + args[i] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else if ("-c".equals(args[i])) { //$NON-NLS-1$
				if (i + 1 < args.length) {
					contextRoot = args[i+1].trim();
					i++;
				}
				else {
					logWarning("Missing context root value for argument \"" + args[i] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else if ("-d".equals(args[i])) { //$NON-NLS-1$
				debug = true;
			}
			else if ("-contribs".equals(args[i])) { //$NON-NLS-1$
				if (i + 1 < args.length) {
					contributionClassNames = args[i+1].split(";"); //$NON-NLS-1$
					i++;
				}
				else {
					logWarning("Missing value for argument \"" + args[i] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else {
				remainingArgs.add(args[i]);
			}
		}
		
		List<AbstractConfigurator> configurators = new ArrayList<AbstractConfigurator>(contributionClassNames ==  null ? 0 : contributionClassNames.length);
		if (contributionClassNames != null && contributionClassNames.length > 0) {
			for (String contrib : contributionClassNames) {
				try {
					Class c = Class.forName(contrib);
					Object o = c.newInstance();
					if (o instanceof AbstractConfigurator) {
						configurators.add((AbstractConfigurator)o);
					}
					else {
						logWarning("Contribution class \"" + contrib + "\" does not extend \"" + AbstractConfigurator.class.getCanonicalName() + "\". Some functionality may be missing."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
				catch (Exception e) {
					logWarning("Could not load contribution class \"" + contrib + "\". Some functionality may be missing. Error: " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		
		// Initialize the test server. This must be done before we invoke any methods in the contributions.
		TestServer server = new TestServer(port, contextRoot, configurators, debug);
		
		// Let the contributions process the remaining arguments.
		int size = remainingArgs.size();
		boolean processed;
		for (int i = 0; i < size;) {
			processed = false;
			for (AbstractConfigurator contrib : configurators) {
				int newIndex = contrib.processNextArgument(remainingArgs, i);
				if (newIndex != i) {
					// Arg was processed; move on to next arg.
					processed = true;
					i = newIndex;
					break;
				}
			}
			if (!processed) {
				if (remainingArgs.get(i).startsWith("-")) { //$NON-NLS-1$
					logWarning("Unrecognized argument \"" + remainingArgs.get(i) + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					logWarning("Skipping argument value \"" + remainingArgs.get(i) + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
				i++;
			}
		}
		
		server.start();
	}
	
	public TestServer(int port, String contextRoot, List<AbstractConfigurator> configurators, boolean debug) {
		this.port = port;
		this.configurators = configurators;
		
		// Set up logging.
		Log.setLog(new Logger(Log.getRootLogger()));
		setDebug(debug);
		
		try {
			// Encode the context but not a leading '/'
			contextRoot = '/' + URLEncoder.encode(contextRoot.charAt(0) == '/' ? contextRoot.substring(1) : contextRoot, "UTF-8"); //$NON-NLS-1$
		}
		catch (UnsupportedEncodingException e) {
			// Shouldn't happen.
			if (contextRoot.charAt(0) != '/') {
				contextRoot = "/" + contextRoot; //$NON-NLS-1$
			}
		}
		this.contextRoot = contextRoot;
		
		// Initialize the configurators.
		for (AbstractConfigurator config : configurators) {
			config.setTestServer(this);
		}
	}
	
	private void start() throws Exception {
		if (contextRoot == null || contextRoot.length() == 0) {
			throw new Exception("Context root argument not specified, cannot start the server"); //$NON-NLS-1$
		}
		
		if (port == null) {
			throw new Exception("Port argument not specified, cannot start the server"); //$NON-NLS-1$
		}
		
		if (port < 0) {
			throw new Exception("Port argument \"" + port + "\" is invalid, cannot start the server"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		webApp = new WebAppContext(null, contextRoot);
		webApp.setDefaultsDescriptor(null);
		webApp.setThrowUnavailableOnStartupException(true);
		jettyServer = new Server(port);
		jettyServer.setHandler(webApp);
		
		// We don't have a real WAR so we must set the base to the project's directory.
		if (webApp.getResourceBase() == null) {
			webApp.setResourceBase(new File(".").getAbsolutePath()); //$NON-NLS-1$
		}
		
		// Register the ping servlet.
		webApp.addServlet(new ServletHolder(new DefaultServlet(this)), "/" + DefaultServlet.SERVLET_PATH); //$NON-NLS-1$
		
		// Add our ContributionConfiguration so that the contributions can be invoked in the middle of the jetty startup too.
		ContributionConfiguration contributionConfig = new ContributionConfiguration();
		contributionConfig.setContributions(configurators);
		appendConfiguration(contributionConfig);
		
		// Invoke preStartup() on all contributions.
		for (AbstractConfigurator config : configurators) {
			config.preStartup();
		}
		
		jettyServer.start();
		
		// Invoke postStartup() on all contributions.
		for (AbstractConfigurator config : configurators) {
			config.postStartup();
		}
		
		ready = true;
		
		jettyServer.join();
	}
	
	/**
	 * Adds the configuration to the webapp, taking care of loading classes as needed.
	 */
	public void appendConfiguration(Configuration config) throws Exception {
		if (config == null) {
			log("Attempted to add a null configuration."); //$NON-NLS-1$
			return;
		}
		
		if (webApp == null) {
			log("Attempted to add configuration class " + config.getClass().getCanonicalName() + " before the webapp was created."); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		
		// If the webapp has already loaded the other configs, just append this one. Otherwise we
		// need to load them first.
		Configuration[] configs = webApp.getConfigurations();
		if (configs == null) {
			String[] configNames = webApp.getConfigurationClasses();
			if (configNames != null) {
				configs = new Configuration[configNames.length];
				for (int i = 0; i < configNames.length; i++) {
					configs[i] = (Configuration)Loader.loadClass(this.getClass(), configNames[i]).newInstance();
				}
			}
			else {
				// Shouldn't happen.
				configs = new Configuration[0];
			}
			webApp.setConfigurations(configs);
		}
		
		// Check if it's already in the list.
		Class configClass = config.getClass();
		for (Configuration next : configs) {
			if (configClass.equals(next.getClass())) {
				log("Configuration class " + configClass.getCanonicalName() + " was already added to the webapp - skipping."); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
		}
		
		Configuration[] newConfigs = new Configuration[configs.length + 1];
		System.arraycopy(configs, 0, newConfigs, 0, configs.length);
		newConfigs[configs.length] = config;
		webApp.setConfigurations(newConfigs);
		logInfo("Configuration " + configClass.getCanonicalName() + " successfully added to the webapp."); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * @return the port on which Jetty is running.
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * @return the context root of Jetty.
	 */
	public String getContextRoot() {
		return contextRoot;
	}
	
	/**
	 * @return the Jetty server.
	 */
	public Server getJettyServer() {
		return jettyServer;
	}
	
	/**
	 * @return the web app context of the Jetty server.
	 */
	public WebAppContext getWebApp() {
		return webApp;
	}
	
	/**
	 * Sets the debug mode for logging messages.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
		Log.getRootLogger().setDebugEnabled(debug);
		
		logInfo("Debug messages " + (debug ? "enabled" : "disabled")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * @return the debug mode for logging messages.
	 */
	public boolean isDebug() {
		return debug;
	}
	
	/**
	 * @return true if the server is ready for requests.
	 */
	public boolean isReady() {
		return ready;
	}
	
	/**
	 * Logs a message to standard out if debug mode is enabled.
	 */
	public void logInfo(String msg) {
		if (debug) {
			System.out.println(msg);
		}
	}
	
	/**
	 * Logs a message to standard err if debug mode is enabled.
	 */
	public void log(String msg) {
		if (debug) {
			System.err.println(msg);
		}
	}
	
	/**
	 * Logs an exception to the console; the debug mode setting is ignored and the message always displayed.
	 */
	public void log(Exception e) {
		e.printStackTrace();
	}
	
	/**
	 * Logs a warning to the console; the debug mode setting is ignored and the message always displayed.
	 */
	public static void logWarning(String msg) {
		System.err.println("WARN: " + msg); //$NON-NLS-1$
	}
}
