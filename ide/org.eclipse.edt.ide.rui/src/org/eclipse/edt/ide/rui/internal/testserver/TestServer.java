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

import org.eclipse.edt.javart.services.servlet.proxy.AjaxProxyServlet;
import org.eclipse.edt.javart.services.servlet.rest.rpc.PreviewServiceServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.log.Log;
import org.mortbay.log.StdErrLog;

/**
 * Jetty server that's used for testing out services before they're deployed. The services
 * are run directly in their Java project.
 */
public class TestServer {
	
	private static boolean debug;
	
	/**
	 * Argument format:
	 * <ul>
	 * <li>-p &lt;port number&gt; (the server's port number)</li>
	 * <li>-c &lt;context root&gt; (the server's context root)</li>
	 * <li>-d (enables debug output)</li>
	 * <li>-s &lt;service mappings&gt; (a string in the format: <i>mapping</i> [|<i>mapping</i>...]) where <i>mapping</i> is the format
	 *     <i>uri</i>;<i>className</i>;<i>stateful</i>, where <i>uri</i> is the URI of the service, <i>className</i> is the qualified class name of
	 *     the service implementation, and <i>stateful</i> is a boolean indicator "true" or "false" whether the service is stateful or not stateful</li>
	 * </ul>
	 */
	public static void main(String[] args) throws Exception {
		Integer port = null;
		String contextRoot = null;
		String serviceMappings = null;
		for (int i = 0; i < args.length; i++ ) {
			if ("-p".equals(args[i])) {
				if (i + 1 < args.length) {
					try {
						port = Integer.parseInt(args[i+1]);
						i++;
					}
					catch (NumberFormatException e) {
						log("Unable to parse port value \"" + args[i+1] + "\"");
					}
				}
				else {
					log("Missing port value for argument \"" + args[i] + "\"");
				}
			}
			else if ("-c".equals(args[i])) {
				if (i + 1 < args.length) {
					contextRoot = args[i+1].trim();
					i++;
				}
				else {
					log("Missing context root value for argument \"" + args[i] + "\"");
				}
			}
			else if ("-s".equals(args[i])) {
				if (i + 1 < args.length) {
					serviceMappings = args[i+1].trim();
					i++;
				}
				else {
					log("Missing service mapping value for argument \"" + args[i] + "\"");
				}
			}
			else if ("-d".equals(args[i])) {
				debug = true;
			}
			else {
				if (args[i].startsWith("-")) {
					log("Unrecognized option \"" + args[i] + "\"");
				}
				else {
					log("Skipping value \"" + args[i] + "\"");
				}
			}
		}
		
		if (contextRoot == null || contextRoot.length() == 0) {
			System.err.println("Context root argument not specified, cannot start the server");
			return;
		}
		
		if (port == null) {
			System.err.println("Port argument not specified, cannot start the server");
			return;
		}
		
		if (port < 0) {
			System.err.println("Port argument \"" + port + "\" is invalid, cannot start the server");
			return;
		}
		
		if (contextRoot.charAt(0) != '/') {
			contextRoot = '/' + contextRoot;
		}
		
		Context context = new Context(Context.SESSIONS);
		context.setContextPath(contextRoot);
		
		// Servlet to handle REST services
		PreviewServiceServlet previewServlet = new PreviewServiceServlet();
		ServletHolder holder = new ServletHolder(previewServlet);
		context.addServlet(holder, "/restservices/*");
		
		// Servlet to handle dedicated services
		context.addServlet(new ServletHolder(new AjaxProxyServlet()), "/___proxy");
		
		// Servlet to handle changes to service mappings
		ConfigServlet configServlet = new ConfigServlet(previewServlet);
		context.addServlet(new ServletHolder(configServlet), ConfigServlet.SERVLET_PATH);
		if (serviceMappings != null && serviceMappings.length() > 0) {
			configServlet.parse(serviceMappings, true);
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
	
	private static void log(String msg) {
		if (debug) {
			System.err.println(msg);
		}
	}
}
