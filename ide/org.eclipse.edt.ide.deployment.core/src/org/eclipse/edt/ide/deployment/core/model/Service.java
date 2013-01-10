/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.edt.javart.resources.egldd.Binding;
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
	
	public boolean equals(Object o) {
		if (!(o instanceof Service)) {
			return false;
		}
		
		Service s = (Service)o;
		if (Binding.equal(type, s.type) && Binding.equal(implementation, s.implementation) && parameters.size() == s.parameters.size()
				&& getClass().equals(s.getClass())) {
			Set<Entry<String, Parameter>> entrySet = s.parameters.entrySet();
			for (Entry<String, Parameter> entry : parameters.entrySet()) {
				if (!entrySet.contains(entry)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
