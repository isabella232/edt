/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.deployment.util;

public class RuntimePropertiesFileUtil {

	private static final String PROPERTIES_SUFFIX = ".properties";
	public static final String JS_SUFFIX = "-eze$$rt.js";
	
	/**
	 * @return the name of the generated javascript file.
	 */
	public static String getJavascriptFileName(String propertiesFile){
		return propertiesFile + JS_SUFFIX;
	}
	
	/**
	 * @return the name of the .properties file.
	 */
	public static String getPropertiesFileName(String propertiesFile){
		return propertiesFile + PROPERTIES_SUFFIX;
	}
	
	public static String getBundleName(String uri) {
		// contextroot/my/pkg/Properties-eze$$rt.js => my/pkg/Properties
		return uri.substring(uri.indexOf("/") + 1, uri.indexOf(JS_SUFFIX));
	}
	
	public static String convertToPropertiesFile(String uri) {
		// Strip off "-eze$$rt.js" and append ".properties"
		String fileName = uri;
		if (fileName.endsWith(JS_SUFFIX)) {
			fileName = fileName.substring(0, uri.length() - JS_SUFFIX.length());
		}
		return fileName + PROPERTIES_SUFFIX;
	}
}
