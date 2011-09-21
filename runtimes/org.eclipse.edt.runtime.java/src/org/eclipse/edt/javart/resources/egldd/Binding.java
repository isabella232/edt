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
package org.eclipse.edt.javart.resources.egldd;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Binding {
	
	public static int EGLBINDING = 1;
	public static int WEBBINDING = 2;
	public static int NATIVEBINDING = 3;
	public static int RESTBINDING = 4;
	public static int SQLDATABASEBINDING = 5;
	
	public static String BINDING_SERVICE_LOCAL = " edt.binding.local";
	public static String BINDING_SERVICE_REST = " edt.binding.rest";
	public static String BINDING_DB_SQL = "edt.binding.sql";
	
	public static String SOAP11VERSION = "SOAP-1.1";
	public static String SOAP12VERSION = "SOAP-1.2";
	
	private String name;
	
	private String type;
	
	private String uri;
	
	private boolean useURI;
	
	protected Map<String, Parameter> parameters = new HashMap<String, Parameter>();
	
	public Binding(String name, String type, String uri, String useURI)
	{
		this.name = name;
		this.type = type;
		this.uri = uri;
		this.useURI = useURI != null && useURI.equalsIgnoreCase("true");
	}
	
	Binding(String name, String type, String uri, boolean useURI)
	{
		this.name = name;
		this.type = type;
		this.uri = uri;
		this.useURI = useURI;
	}
	
	public Parameter getParameter(String name) {
		return parameters.get(name.toLowerCase());
	}

	public Collection<Parameter> getParameters() {
		return parameters.values();
	}

	public void addParameter(Parameter parameter) {
		this.parameters.put(parameter.getName().toLowerCase(), parameter);
	}

	public String getName() {
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public String getUri() {
		return uri;
	}

	public boolean isUseURI() {
		return useURI;
	}
	
}
