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
package org.eclipse.edt.ide.testserver;

import java.io.PrintStream;
import java.net.Socket;

import org.eclipse.core.resources.IResourceChangeEvent;

/**
 * Extension to the test server, running from within the IDE. Subclasses must have a default constructor,
 * and may override any of the methods in this class. Register this class with the org.eclipse.edt.ide.testserver.testServerExtension
 * extension point.
 */
public abstract class AbstractTestServerContribution {
	
	/**
	 * Called when the configuration is instantiated, so that contributions can do any initializations.
	 * 
	 * @param config  The new test server configuration.
	 */
	public void init(TestServerConfiguration config) {
	}
	
	/**
	 * Called when the configuration is disposed, so that contributions can do any cleanup.
	 * 
	 * @param config  The test server configuration.
	 */
	public void dispose(TestServerConfiguration config) {
	}
	
	/**
	 * Called when this contribution is being disposed.
	 */
	public void dispose() {
	}
	
	/**
	 * @param config  The test server configuration being started
	 * @return the names of classes extending {@link AbstractConfigurator} that should be registered with the test server,
	 *         or null if no configurators are provided.
	 */
	public String[] getConfiguratorClassNames(TestServerConfiguration config) {
		return null;
	}
	
	/**
	 * @param config  The test server configuration being started
	 * @return arguments to be passed to the test server, which can be processed by an {@link AbstractConfigurator},
	 *         or null if no arguments are provided.
	 * @see #getConfiguratorClassNames()
	 */
	public String getArgumentAdditions(TestServerConfiguration config) {
		return null;
	}
	
	/**
	 * @param config  The test server configuration being started
	 * @return extra classpath entries in the format expected by JDT, or null if no entries are provided.
	 * @see ClasspathUtil
	 */
	public String[] getClasspathAdditions(TestServerConfiguration config) {
		return null;
	}
	
	/**
	 * Handles a request sent from jetty to the IDE. Extensions to the server can ask the IDE for information, such as
	 * the connection information for a workspace:// SQL URI, and the request will come through here. Clients do not
	 * need to close the print stream after handling a response. Once a request has been handled, no other contributions
	 * will be called.
	 * 
	 * @param socket  The request's socket.
	 * @param ps      The response stream.
	 * @return true if the request was handled, false if it was not handled.
	 * @throws Exception
	 */
	public boolean handleServerRequest(Socket socket, PrintStream ps) throws Exception {
		return false;
	}
	
	/**
	 * Called when a resource has changed, once for each configuration individually. By default the event is ignored.
	 * 
	 * @param event   The resource change event.
	 * @param config  The active configuration.
	 */
	public void resourceChanged(IResourceChangeEvent event, TestServerConfiguration config) {
	}
}
