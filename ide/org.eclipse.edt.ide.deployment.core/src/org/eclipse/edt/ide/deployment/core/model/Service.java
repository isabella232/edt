/*******************************************************************************
 * Copyright 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.core.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.javart.resources.egldd.Parameter;

public class Service {
	
	
	public static String SERVICE_REST = "edt.service.rest";
	private String type;
	
	private String implementation;
	
	protected Map<String, Parameter> parameters = new HashMap<String, Parameter>();
	
	public Service(String type, String implementation)
	{
		this.type = type;
		this.implementation = implementation;
	}
	
	public Parameter getParameter(String name) {
		return parameters.get(name.toLowerCase());
	}

	public void addParameter(Parameter parameter) {
		this.parameters.put(parameter.getName().toLowerCase(), parameter);
	}

	public String getType() {
		return type;
	}
	
	public String getImplementation() {
		return implementation;
	}
}
