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


public class RUIHandler
{
	private String implementation;
	private boolean enableGeneration;
	private List<Parameter> parameters;
	
	public RUIHandler( String implementation, String enableGenerationString )
	{
		this.implementation = implementation;
		this.enableGeneration = string2Boolean( enableGenerationString );
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
	
	public String getImplementation()
	{
		return implementation;
	}

	public void setImplementation( String implementation )
	{
		this.implementation = implementation;
	}
	
	public boolean isEnableGeneration()
	{
		return enableGeneration;
	}

	public void setEnableGeneration( boolean enableGeneration )
	{
		this.enableGeneration = enableGeneration;
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
