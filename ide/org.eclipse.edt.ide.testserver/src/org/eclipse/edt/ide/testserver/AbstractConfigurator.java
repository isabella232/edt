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

import java.util.List;


/**
 * Extension to the test server, running from within the server. Subclasses must have a default constructor,
 * and may override any of the methods in this class. Contribute this class through
 * {@link AbstractTestServerContribution#getConfiguratorClassNames(TestServerConfiguration)}.
 */
public abstract class AbstractConfigurator {
	
	/**
	 * The test server to which this configurator belongs.
	 */
	protected TestServer server;
	
	/**
	 * Sets the TestServer instance. Guaranteed to be called before any other method, and never passed null.
	 */
	public void setTestServer(TestServer server) {
		this.server = server;
	}
	
	/**
	 * If the next argument can be handled by this configurator, this should perform any such configurations from it and
	 * return <code>currentIndex + 1</code>. If the argument can be handled and is complex (e.g. the argument is a flag and
	 * the next argument is its corresponding value) this should return <code>currentIndex + n</code> where n is the number of
	 * additional arguments that were part of the initial argument. If the argument cannot be handled this should return
	 * <code>currentIndex</code>.
	 * 
	 * @param arguments  The list of arguments.
	 * @param currentIndex  The index of the next argument to process.
	 * @return the index for the new next argument to process, or the value of <code>currentIndex</code> if the argument was not handled.
	 * @throws Exception if a fatal error occurs and the server cannot start.
	 */
	public int processNextArgument(List<String> arguments, int currentIndex) throws Exception {
		return currentIndex;
	}
	
	/**
	 * Called after the Jetty server and webapp is instantiated but before the startup has been started.
	 * Subclasses should override this as needed. This can be used to validate the arguments and throw an
	 * error if the test server cannot be properly started (e.g. invalid arguments).
	 * 
	 * @throws Exception if a fatal error occurs and the server cannot start.
	 */
	public void preStartup() throws Exception {
	}
	
	/**
	 * Called after the Jetty server has fully started. Subclasses should override this as needed.
	 * 
	 * @throws Exception if a fatal error occurs and the server cannot start.
	 */
	public void postStartup() throws Exception {
	}
	
	/**
	 * Called during the preConfigure phase of the Jetty startup process. Subclasses
	 * should override this as necessary.
	 * 
	 * @throws Exception if a fatal error occurs and the server cannot start.
	 */
	public void preConfigure() throws Exception {
	}
	
	/**
	 * Called during the configure phase of the Jetty startup process. Subclasses
	 * should override this as necessary (e.g. add servlets).
	 * 
	 * @throws Exception if a fatal error occurs and the server cannot start.
	 */
	public void configure() throws Exception {
	}
	
	/**
	 * Called during the postConfigure phase of the Jetty startup process. Subclasses
	 * should override this as necessary.
	 * 
	 * @throws Exception if a fatal error occurs and the server cannot start.
	 */
	public void postConfigure() throws Exception {
	}
}
