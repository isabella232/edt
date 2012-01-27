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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.edt.javart.ide.IDEResourceLocator;
import org.eclipse.edt.javart.services.servlet.proxy.AjaxProxyServlet;
import org.eclipse.edt.javart.services.servlet.rest.rpc.PreviewServiceServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.log.Log;
import org.mortbay.log.StdErrLog;

import resources.edt.binding.IDEBindingResourceProcessor;

/**
 * Jetty server that's used for testing out services before they're deployed. The services are run directly in their Java project.
 */
public class TestServer {
	
	private static boolean debug;
	
	/**
	 * Argument format:
	 * <ul>
	 * <li>-p &lt;port number&gt; (the server's port number)</li>
	 * <li>-i &lt;IDE port number&gt; (the port number over which the IDE can be reached)</li>
	 * <li>-c &lt;context root&gt; (the server's context root)</li>
	 * <li>-d (enables debug output)</li>
	 * <li>-s &lt;service mappings&gt; (a string in the format: <i>mapping</i> [|<i>mapping</i>...]) where <i>mapping</i> is the format
	 *     <i>uri</i>;<i>className</i>;<i>stateful</i>, where <i>uri</i> is the URI of the service, <i>className</i> is the qualified class name of
	 *     the service implementation, and <i>stateful</i> is a boolean indicator "true" or "false" whether the service is stateful or not stateful</li>
	 * <li>-dd &lt;&gt; (a string where the delimiter is File.pathSeparatorChar and each token alternates between a dd name and its absolute path, with
	 *     the name listed first, e.g. ddName1;/path/to/ddName1.egldd;ddName2;/path/to/ddName2.egldd</li>
	 * <li>-ddd &lt;default DD name&gt; (the name of the default deployment descriptor)</li>
	 * </ul>
	 */
	public static void main(String[] args) throws Exception {
		Integer port = null;
		Integer idePort = null;
		String contextRoot = null;
		String serviceMappings = null;
		String ddFiles = null;
		String defaultDD = ""; //$NON-NLS-1$
		for (int i = 0; i < args.length; i++ ) {
			if ("-p".equals(args[i])) { //$NON-NLS-1$
				if (i + 1 < args.length) {
					try {
						port = Integer.parseInt(args[i+1]);
						i++;
					}
					catch (NumberFormatException e) {
						log("Unable to parse port value \"" + args[i+1] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				else {
					log("Missing port value for argument \"" + args[i] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			if ("-i".equals(args[i])) { //$NON-NLS-1$
				if (i + 1 < args.length) {
					try {
						idePort = Integer.parseInt(args[i+1]);
						i++;
					}
					catch (NumberFormatException e) {
						log("Unable to parse IDE port value \"" + args[i+1] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				else {
					log("Missing IDE port value for argument \"" + args[i] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else if ("-c".equals(args[i])) { //$NON-NLS-1$
				if (i + 1 < args.length) {
					contextRoot = args[i+1].trim();
					i++;
				}
				else {
					log("Missing context root value for argument \"" + args[i] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else if ("-s".equals(args[i])) { //$NON-NLS-1$
				if (i + 1 < args.length) {
					serviceMappings = args[i+1].trim();
					i++;
				}
				else {
					log("Missing service mapping value for argument \"" + args[i] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else if ("-dd".equals(args[i])) { //$NON-NLS-1$
				if (i + 1 < args.length) {
					ddFiles = args[i+1].trim();
					i++;
				}
				else {
					log("Missing deployment descriptor file list value for argument \"" + args[i] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else if ("-ddd".equals(args[i])) { //$NON-NLS-1$
				if (i + 1 < args.length) {
					defaultDD = args[i+1].trim();
					i++;
				}
				else {
					log("Missing default deployment descriptor value for argument \"" + args[i] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else if ("-d".equals(args[i])) { //$NON-NLS-1$
				debug = true;
			}
			else {
				if (args[i].startsWith("-")) { //$NON-NLS-1$
					log("Unrecognized option \"" + args[i] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					log("Skipping value \"" + args[i] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		
		if (contextRoot == null || contextRoot.length() == 0) {
			System.err.println("Context root argument not specified, cannot start the server"); //$NON-NLS-1$
			return;
		}
		
		if (port == null) {
			System.err.println("Port argument not specified, cannot start the server"); //$NON-NLS-1$
			return;
		}
		
		if (idePort == null) {
			System.err.println("IDE port argument not specified, cannot start the server"); //$NON-NLS-1$
			return;
		}
		
		if (port < 0) {
			System.err.println("Port argument \"" + port + "\" is invalid, cannot start the server"); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		
		if (idePort < 0) {
			System.err.println("IDE port argument \"" + idePort + "\" is invalid, cannot start the server"); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		
		try {
			// Encode the context but not a leading '/'
			contextRoot = '/' + URLEncoder.encode(contextRoot.charAt(0) == '/' ? contextRoot.substring(1) : contextRoot, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// Shouldn't happen.
			if (contextRoot.charAt(0) != '/') {
				contextRoot = "/" + contextRoot;
			}
		}
		
		Context context = new Context(Context.SESSIONS);
		context.setContextPath(contextRoot);
		
		// Servlet to handle REST services
		PreviewServiceServlet previewServlet = new PreviewServiceServlet();
		ServletHolder holder = new ServletHolder(previewServlet);
		context.addServlet(holder, "/restservices/*"); //$NON-NLS-1$
		
		// Servlet to handle dedicated services
		context.addServlet(new ServletHolder(new AjaxProxyServlet()), "/___proxy"); //$NON-NLS-1$
		
		// Register the resource locator for dynamically finding resource bindings.
		IDEResourceLocator locator = new IDEResourceLocator(idePort);
		IDEBindingResourceProcessor bindingProcessor = new IDEBindingResourceProcessor(locator);
		bindingProcessor.setDefaultDD(defaultDD);
		
		// Servlet to handle changes to service mappings
		ConfigServlet configServlet = new ConfigServlet(previewServlet, bindingProcessor);
		context.addServlet(new ServletHolder(configServlet), ConfigServlet.SERVLET_PATH);
		if (serviceMappings != null && serviceMappings.length() > 0) {
			configServlet.parseMappings(serviceMappings, true);
		}
		if (ddFiles != null && ddFiles.length() > 0) {
			configServlet.parseDDFiles(ddFiles, true);
		}
		
		Server server = new Server(port);
		server.setHandler( context );
		
		if (debug) {
			// Turn on logging for the server
			StdErrLog log = new StdErrLog();
			log.setDebugEnabled(true);
			Log.setLog(log);
		}
		
		server.start();
		server.join();
	}
	
	public static void log(String msg) {
		if (debug) {
			System.err.println(msg);
		}
	}
	
	public static void log(Exception e) {
		if (debug) {
			e.printStackTrace();
		}
	}
}
