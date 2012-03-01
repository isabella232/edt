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
package org.eclipse.edt.ide.internal.testserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.edt.ide.testserver.TestServer;

/**
 * Servlet that's part of every test server. It can be used to check if the server is running.
 */
public class DefaultServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The path of the servlet running inside the context root.
	 */
	public static final String SERVLET_PATH = "default"; //$NON-NLS-1$
	
	/**
	 * The argument for changing the debug setting.
	 */
	public static final String ARG_DEBUG = "debug"; //$NON-NLS-1$
	
	/**
	 * The test server.
	 */
	private final TestServer server;
	
	public DefaultServlet(TestServer server) {
		this.server = server;
	}
	
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String debug = req.getParameter(ARG_DEBUG);
		if (debug != null && debug.length() > 0) {
			server.setDebug(Boolean.valueOf(debug), false);
		}
		else {
			server.logInfo("Ping!"); //$NON-NLS-1$
		}
	}
}
