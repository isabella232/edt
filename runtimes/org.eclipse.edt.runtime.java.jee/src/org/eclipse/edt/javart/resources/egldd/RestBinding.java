/*******************************************************************************
 * Copyright © 2006, 2013 IBM Corporation and others.
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

public class RestBinding extends Binding {

	public static final String ATTRIBUTE_BINDING_REST_sessionCookieId = "sessionCookieId";
	public static final String ATTRIBUTE_BINDING_REST_enableGeneration = "enableGeneration";
    public RestBinding (Binding binding)
    {
    	super(binding);
    }

	public int getBindingType()
	{
		return RESTBINDING;
	}
		
	public boolean isEnableGeneration() {
		return ParameterUtil.getBooleanValue(getParameter(ATTRIBUTE_BINDING_REST_enableGeneration), true);
	}

	public String getSessionCookieId() {
		return ParameterUtil.getStringValue(getParameter(ATTRIBUTE_BINDING_REST_sessionCookieId), "");
	}

}
