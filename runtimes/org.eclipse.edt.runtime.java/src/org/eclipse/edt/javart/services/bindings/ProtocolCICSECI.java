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

public class ProtocolCICSECI extends Protocol {
	
	private static final long serialVersionUID = 70L;

	private String conversionTable;

	private String ctgLocation;

	private String ctgPort;

	private String location;

	private String serverID;

	public ProtocolCICSECI(String name, String conversionTable,
			String ctgLocation, String ctgPort, String location, String serverID) {
		super(name);
		this.conversionTable = conversionTable;
		this.ctgLocation = ctgLocation;
		this.ctgPort = ctgPort;
		this.location = location;
		this.serverID = serverID;
	}

	public int getProtocolType() {
		return PROTOCOL_CICSECI;
	}

	public String getConversionTable() {
		return conversionTable;
	}

	public void setConversionTable(String conversionTable) {
		this.conversionTable = conversionTable;
	}

	public String getCtgLocation() {
		return ctgLocation;
	}

	public void setCtgLocation(String ctgLocation) {
		this.ctgLocation = ctgLocation;
	}

	public String getCtgPort() {
		return ctgPort;
	}

	public void setCtgPort(String ctgPort) {
		this.ctgPort = ctgPort;
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
	
}
