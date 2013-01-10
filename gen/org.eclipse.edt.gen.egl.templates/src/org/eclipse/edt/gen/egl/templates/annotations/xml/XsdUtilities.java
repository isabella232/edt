/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.egl.templates.annotations.xml;

import java.util.HashMap;
import java.util.Map;

public class XsdUtilities {
	
	static final String __DEFAULT = "##default";
	
	static XsdUtilities instance = new XsdUtilities();
	private Map<String, String> xsdTypes;
	private Map<String, String> xsdTypes(){
		if(xsdTypes == null){
			xsdTypes = new HashMap<String, String>();
			xsdTypes.put("boolean","boolean");
			xsdTypes.put("byte","byte");
			xsdTypes.put("short","smallint");
			xsdTypes.put("int","int");
			xsdTypes.put("long","bigint");
			xsdTypes.put("double","float");
			xsdTypes.put("float","smallFloat");
	
			xsdTypes.put("string","string");
			xsdTypes.put("date","date");
			xsdTypes.put("time","time");
			xsdTypes.put("datetime","timestamp");
			xsdTypes.put("anysimpletype", "java.lang.Object");
		}
		return xsdTypes;
	}
	
	String convertToEglType(String xsdType, String xsdNamespace, boolean isNillable){
		String eglType = xsdTypes().get(xsdType.toLowerCase());
		if(eglType == null){
			eglType = xsdType + "---???---";
		}
		if(isNillable){
			eglType += '?';
		}
		return eglType;
	}
}
