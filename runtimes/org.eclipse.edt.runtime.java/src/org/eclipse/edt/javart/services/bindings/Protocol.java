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

import java.io.Serializable;

public abstract class Protocol implements Serializable {

	private static final long serialVersionUID = 70L;
	
	public final static int PROTOCOL_REF = 1;
	public final static int PROTOCOL_LOCAL = 2;
	public final static int PROTOCOL_CICSECI = 3;
	public final static int PROTOCOL_CICSSSL = 4;
	public final static int PROTOCOL_CICSJ2C = 5;
	public final static int PROTOCOL_JAVA400 = 6;
	public final static int PROTOCOL_TCPIP = 7;
	public final static int PROTOCOL_JAVA400J2C = 8;
	
	
	protected String name;
	
	public Protocol (String name)
	{
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public abstract int getProtocolType();
	
}
