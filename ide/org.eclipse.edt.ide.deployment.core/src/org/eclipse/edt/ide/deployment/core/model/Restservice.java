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
package org.eclipse.edt.ide.deployment.core.model;

import org.eclipse.edt.javart.resources.egldd.ParameterUtil;

public class Restservice extends Service{
	
	public static final String ATTRIBUTE_SERVICE_REST_enableGeneration = "enableGeneration";
	public static final String ATTRIBUTE_SERVICE_REST_uriFragment = "uriFragment";
	public static final String ATTRIBUTE_SERVICE_REST_implType = "implType";
	
	
	public Restservice(String type, String implementation)
	{
		super(type, implementation);
	}

	public Restservice(Service service)
	{
		super(service.getType(), service.getImplementation());
		parameters = service.parameters;
	}

	public boolean isEnableGeneration() {
		return ParameterUtil.getBooleanValue(getParameter(ATTRIBUTE_SERVICE_REST_enableGeneration), true);
	}

	public String getUri() {
		return getParameter(ATTRIBUTE_SERVICE_REST_uriFragment).getValue();
	}
	
	public int getImplType() {
		return ParameterUtil.getIntValue(getParameter(ATTRIBUTE_SERVICE_REST_implType), 0);
	}

	public boolean isStateful(){
		return(false);
	}
}
