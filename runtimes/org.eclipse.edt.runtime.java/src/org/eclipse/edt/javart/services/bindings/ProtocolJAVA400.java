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

public class ProtocolJAVA400 extends Protocol {
	
	private static final long serialVersionUID = 70L;

	private String location;
	private String conversionTable;
	private String password;
	private String userID;
	private String library;
	
	
	public ProtocolJAVA400(String name, String library, String location, String conversionTable, String password, String userID) {
		super(name);
		this.location = location;
		this.conversionTable = conversionTable;
		this.password = password;
		this.userID = userID;
		this.library = library;
	}

	public int getProtocolType() {
		return PROTOCOL_JAVA400;
	}
	

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getConversionTable()
	{
		return conversionTable;
	}

	public String getLibrary()
	{
		return library;
	}

	public String getPassword()
	{
		return password;
	}

	public String getUserID()
	{
		return userID;
	}

}
