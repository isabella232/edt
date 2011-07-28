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

public class ProtocolCICSJ2C extends Protocol {
	
	private static final long serialVersionUID = 70L;

	private String conversionTable;
	private String location;
	
	public ProtocolCICSJ2C(String name, String converstionTable, String location) {
		super(name);
		this.conversionTable = converstionTable;
		this.location = location;
	}

	public int getProtocolType() {
		return PROTOCOL_CICSJ2C;
	}
	
	public String getConversionTable() {
		return conversionTable;
	}

	public void setConversionTable(String conversionTable) {
		this.conversionTable = conversionTable;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
}
