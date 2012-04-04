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
package org.eclipse.edt.ide.deployment.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.javart.resources.egldd.Parameter;

public class RUIApplication
{
	private boolean deployAllHandlers;
	private String name;
	private List<RUIHandler> handlers;
	private List<Parameter> parameters;
	
	public RUIApplication(String name, String deployAllHandlers )
	{
		this.name = name;
		this.deployAllHandlers = string2Boolean( deployAllHandlers );
		this.handlers = new ArrayList<RUIHandler>();
		this.parameters = new ArrayList<Parameter>();
	}

	private static boolean string2Boolean( String value )
	{
		if (value != null && value.equalsIgnoreCase("true"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean deployAllHandlers()
	{
		return deployAllHandlers;
	}

	public void setDeployAllHandlers( boolean deployAllHandlers )
	{
		this.deployAllHandlers = deployAllHandlers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void addRUIHandler(RUIHandler handler)
	{
		this.handlers.add(handler);
	}
	
	public void removeRUIHandler(RUIHandler handler)
	{
		this.handlers.remove(handler);
	}

	public List<RUIHandler> getRUIHandlers()
	{
		return handlers;
	}
	
	public void addParameter(Parameter param)
	{
		parameters.add(param);
	}
	
	public List<Parameter> getParameters()
	{
		return parameters;
	}
}
