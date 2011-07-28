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

public class ProtocolJAVA400J2C extends ProtocolJAVA400 {
	
	private static final long serialVersionUID = 70L;

	private String currentLibrary;

	public ProtocolJAVA400J2C(String name, String libraries, String currentLibrary, String location, String conversionTable, String password, String userID) {
		super( name, libraries, location, conversionTable, password, userID );
		this.currentLibrary = currentLibrary;
	}

	public int getProtocolType() {
		return PROTOCOL_JAVA400J2C;
	}
	
	public String getCurrentLibrary() {
		return currentLibrary;
	}

}
