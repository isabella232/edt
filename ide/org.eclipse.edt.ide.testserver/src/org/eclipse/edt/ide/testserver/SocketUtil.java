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
import java.net.ServerSocket;

public class SocketUtil {
	
	private SocketUtil() {
		// No instances.
	}
	
	/**
	 * Finds an open port, starting at the given port and incrementing by 1 up to <code>maxPortsToTry</code> times.
	 * Each port is checked <code>maxAttempts</code> times.
	 */
	public static int findOpenPort(int port, int maxAttempts, int maxPortsToTry) throws IOException {
		IOException lastEx = null;
		for (int portToTry = port; portToTry < (port + maxPortsToTry); portToTry++) {
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
		
		throw lastEx;
	}
}
