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

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

public class SocketUtil {
	
	private SocketUtil() {
		// No instances.
	}
	
	/**
	 * All the ports currently in use by test servers. There's a timing delay between when we check the open port and when
	 * Jetty binds to the port, so another test server could come through here and try to use the same "open" port.
	 */
	private static final Set<Integer> usedPorts = new HashSet<Integer>(10);
	
	/**
	 * Finds an open port, starting at the given port and incrementing by 1 up to <code>maxPortsToTry</code> times.
	 * Each port is checked <code>maxAttempts</code> times.
	 */
	public static synchronized int findOpenPort(int port, int maxAttempts, int maxPortsToTry) throws IOException {
		IOException lastEx = null;
		for (int portToTry = port; portToTry < (port + maxPortsToTry); portToTry++) {
			if (usedPorts.contains(portToTry)) {
				// Don't let the already-used port affect how many ports we try.
				maxPortsToTry++;
			}
			else {
				for (int attemptsOnThisSocket = 0; attemptsOnThisSocket < maxAttempts; attemptsOnThisSocket++) {
					try {
						new ServerSocket(portToTry).close();
						return portToTry;
					}
					catch (IOException iox) {
						lastEx = iox;
					}
				}
			}
		}
		
		throw lastEx;
	}
	
	/**
	 * Reserves the port such that no other calls to {@link #findOpenPort(int, int, int)} will return it.
	 * 
	 * @param port  The port number to reserve.
	 */
	public static void reservePort(int port) {
		usedPorts.add(port);
	}
	
	/**
	 * Removes the port from the list of reserved ports.
	 * 
	 * @param port  The port number to free up.
	 */
	public static void freePort(int port) {
		usedPorts.remove(port);
	}
}
