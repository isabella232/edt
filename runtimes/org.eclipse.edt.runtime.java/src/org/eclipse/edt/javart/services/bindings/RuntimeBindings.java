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
import java.util.HashMap;


public class RuntimeBindings implements Serializable
{
	private static final long serialVersionUID = 70L;
	
	public static final int WSDL_INTERFACE = 1;
	public static final int EGL_INTERFACE = 2;
	public static final int NATIVE_INTERFACE = 3;
	public static final String WEBSERVICE_RUNTIME_JAXWS = "JAXWS";

	private String name;

	private HashMap<String, Binding> bindings;

	private HashMap<String, Protocol> protocols;
	
	private boolean debugMode;
	
	public RuntimeBindings(String name)
	{
		this.name = name;
		bindings = new HashMap<String, Binding>();
		protocols = new HashMap<String, Protocol>();
		debugMode = false;
	}

	public void addBinding(Binding binding)
	{
		bindings.put(binding.getName().toLowerCase(), binding);
	}

	public Binding getBinding(String name)
	{
		return bindings.get(name.toLowerCase());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public void addProtocol(Protocol protocol)
	{
		protocols.put(protocol.getName(), protocol);
	}

	public Protocol getProtocol(String name)
	{
		return protocols.get(name);
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}




}
