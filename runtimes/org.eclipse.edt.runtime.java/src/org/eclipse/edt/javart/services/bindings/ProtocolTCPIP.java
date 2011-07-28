/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.services.bindings;

public class ProtocolTCPIP extends Protocol {
	
	private static final long serialVersionUID = 70L;

	private String location;
	private String serverID;
	
	public ProtocolTCPIP(String name, String location, String serverID) {
		super(name);
		this.location = location;
		this.serverID = serverID;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getServerID() {
		return serverID;
	}

	public void setServerID(String serverID) {
		this.serverID = serverID;
	}
	public int getProtocolType() {
		return PROTOCOL_TCPIP;
	}
	

}
