/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import java.util.Map;

public class ParameterUtil {

	public static int getIntValue(Parameter parameter, int defaultValue){
		try {
			return parameter != null && parameter.getValue() != null ? Integer.parseInt(parameter.getValue()) : defaultValue;
		}
		catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	public static boolean getBooleanValue(Parameter parameter, boolean defaultValue){
		return parameter != null && parameter.getValue() != null ? "true".equalsIgnoreCase(parameter.getValue()) : defaultValue;
	}
	public static String getStringValue(Parameter parameter, String defaultValue){
		return parameter != null && parameter.getValue() != null ? parameter.getValue() : defaultValue;
	}
	static void setValue(Map<String, Parameter> parameters, String name, boolean value){
		Parameter parameter = parameters.get(name);
		if (parameter == null){
			parameter = new Parameter(name, Parameter.TYPE_BOOLEAN, String.valueOf(value));
			parameters.put(name, parameter);
		}
		else{
			parameter.setValue(String.valueOf(value));
		}
	}
	static void setValue(Map<String, Parameter> parameters, String name, String value){
		Parameter parameter = parameters.get(name);
		if (parameter == null){
			parameter = new Parameter(name, Parameter.TYPE_BOOLEAN, value);
		}
		else{
			parameter.setValue(value);
		}
	}
}
