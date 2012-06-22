/*******************************************************************************
 * Copyright © 2006, 2012 IBM Corporation and others.
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
import java.util.Map.Entry;
import java.util.Set;

public class Binding {
	
	public static int EGLBINDING = 1;
	public static int WEBBINDING = 2;
	public static int NATIVEBINDING = 3;
	public static int RESTBINDING = 4;
	public static int SQLDATABASEBINDING = 5;
	public static int DEDICATEDBINDING = 6;
	
	public static String BINDING_SERVICE_LOCAL = " edt.binding.local";
	public static String BINDING_SERVICE_REST = " edt.binding.rest";
	public static String BINDING_SERVICE_DEDICATED = "edt.binding.dedicated"; //TODO add support for this
	public static String BINDING_DB_SQL = "edt.binding.sql";
	
	public static String SOAP11VERSION = "SOAP-1.1";
	public static String SOAP12VERSION = "SOAP-1.2";
	
	private String name;
	
	private String type;
	
	private String uri;
	
	private boolean useURI;
	
	protected Map<String, Parameter> parameters = new HashMap<String, Parameter>();
	
	public Binding(Binding binding)
    {
    	this(binding.getName(), binding.getType(), binding.getUri(), binding.isUseURI());
    	parameters = binding.parameters;
    }
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
	
	public void setParameters(Map<String, Parameter> newParams) {
		parameters = newParams;
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
	
	public void setUri(String uri) {
		this.uri = uri;
	}

	public boolean isUseURI() {
		return useURI;
	}
	
	public void setUseURI( boolean useURI) {
		this.useURI = useURI;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Binding)) {
			return false;
		}
		
		Binding b = (Binding)o;
		if (useURI == b.useURI && getClass().equals(b.getClass()) && equal(name, b.name) && equal(type, b.type) && equal(uri, b.uri)
				&& parameters.size() == b.parameters.size()) {
			Set<Entry<String, Parameter>> entrySet = b.parameters.entrySet();
			for (Entry<String, Parameter> entry : parameters.entrySet()) {
				if (!entrySet.contains(entry)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public static boolean equal(String s1, String s2) {
		if (s1 == null) {
			return s2 == null;
		}
		return s1.equals(s2);
	}
}
