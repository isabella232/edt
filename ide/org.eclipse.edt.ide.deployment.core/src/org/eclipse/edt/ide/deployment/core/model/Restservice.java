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
package org.eclipse.edt.ide.deployment.core.model;

import java.util.ArrayList;
import java.util.List;

public class Restservice {
	
	private String implementation;
	private String uri;
	private boolean enableGeneration;
	private boolean stateful;
	private String protocol;
	List parameters;
	
	public Restservice(String implementation, String uri, String protocol, String stateful,String enableGenerationString )
	{
		this.implementation = implementation;
		this.uri = uri;
		this.protocol = protocol;
		this.stateful = string2Boolean( stateful );
		this.parameters = new ArrayList();
		enableGeneration = string2Boolean( enableGenerationString );
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
	public String getImplementation() {
		return implementation;
	}

	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}

	public boolean isEnableGeneration() {
		return enableGeneration;
	}

	public void setEnableGeneration(boolean enableGeneration) {
		this.enableGeneration = enableGeneration;
	}

	public String getUri() {
		return uri;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public boolean isStateful()
	{
		return stateful;
	}
	
	public void addParameter(Parameter param)
	{
		parameters.add(param);
	}
	
	public List getParameters()
	{
		return parameters;
	}
}
