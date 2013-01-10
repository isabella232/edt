/*******************************************************************************
 * Copyright © 2009, 2013 IBM Corporation and others.
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

public class Parameter
{
	static final String TYPE_STRING = "string";
	static final String TYPE_BOOLEAN = "boolean";
	
	private String name;
	private String value;
	private String type;
	
	public Parameter( String name, String type, String value )
	{
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public Parameter( String name, String value )
	{
		this.name = name;
		this.value = value;
		this.type = null;
	}

	public String getName()
	{
		return name;
	}

	public String getValue()
	{
		return value;
	}
	
	void setValue(String value) {
		this.value = value;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Parameter)) {
			return false;
		}
		
		Parameter p = (Parameter)o;
		return Binding.equal(name, p.name) && Binding.equal(type, type) && Binding.equal(value, p.value);
	}
}
