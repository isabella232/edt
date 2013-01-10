/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Simple server that listens for connections from Jetty servers, forwarding requests to the contribution classes.
 */
public class TestServerIDEConnector {
	
	private static TestServerIDEConnector instance;
	private static int START_PORT_NUMBER = 6590;
	private static int portNumber = START_PORT_NUMBER;
	private static boolean running = true;
	private static final String EOL = "\r\n"; //$NON-NLS-1$
	
	private ServerSocket serverSocket;
	
	public static TestServerIDEConnector getInstance() {
		if (instance == null) {
			instance = new TestServerIDEConnector();
		}
		return instance;
	}
	
	private TestServerIDEConnector() {
		do {
			try {
				portNumber = START_PORT_NUMBER++;
				serverSocket = new ServerSocket(portNumber);
			}
			catch (Exception e) {
			}
		}
		while (serverSocket == null);
		
		new Thread() {
			public void run() {
				startServer();
			}
		}.start();
	}
	
	public void startServer() {
		try {
			while (running) {
				final Socket client = serverSocket.accept();
				handleBrowserEvent(client);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void handleBrowserEvent(final Socket socket) {
		PrintStream ps = null;
		try {
			ps = new PrintStream(socket.getOutputStream());
			for (AbstractTestServerContribution contrib : TestServerPlugin.getContributions()) {
				// First contribution to handle a request wins.
				if (contrib.handleServerRequest(socket, ps)) {
					break;
				}
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
			try {
				fail(ps);
			}
			catch (Exception ee) {
			}
		}
		finally {
			if (ps != null) {
				// Will throw an error if a contribution already closed it.
				try {
					ps.flush();
					ps.close();
				}
				catch (Exception e) {
				}
			}
		}
	}
	
	public int getPortNumber() {
		return portNumber;
	}
	
	public void fail(PrintStream ps) throws InterruptedException {
		ps.print(getBadResponseHeader());
		ps.close();
	}

	public static String getBadResponseHeader() {
		return "HTTP/1.0 404 " + EOL + "Content-Type: text/plain; charset=UTF-8" + EOL + EOL; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static String getGoodResponseHeader(String url, String contentType, boolean cache) {
		return getResponseHeader(url, contentType, cache, 200, "OK"); //$NON-NLS-1$
	}
	
	public static String getResponseHeader(String url, String contentType, boolean cache, int status, String statusMsg) {
		return "HTTP/1.0 " //$NON-NLS-1$
				+ String.valueOf(status) + " " + statusMsg //$NON-NLS-1$
				+ EOL
				+ "Content-Type: " + contentType + EOL + EOL; //$NON-NLS-1$
	}
}
