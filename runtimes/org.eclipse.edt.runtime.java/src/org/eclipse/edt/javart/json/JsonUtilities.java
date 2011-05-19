/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.json;

import java.util.List;

public class JsonUtilities 
{

	public static String BINDING_ID = "binding";
	public static String WEB_BINDING_INTERFACE_ID = "interfacename";
	public static String BINDING_WEBTYPE_ID = "WebBinding";
	public static String BINDING_EGLTYPE_ID = "EGLBinding";
	public static String BINDING_NAME_ID = "name";
	public static String BINDING_TYPE_ID = "type";
	public static String PROTOCOL_LOCAL_ID = "local";
	public static String EGL_BINDING_SERVICE_NAME_ID = "serviceName";
	public static String EGL_BINDING_ALIAS_ID = "alias";
	public static String PROTOCOL_ID = "protocol";
	public static String WEB_BINDING_WSDL_LOCATION_ID = "wsdlLocation";
	public static String WEB_BINDING_WSDL_PORT_ID = "wsdlPort";
	public static String WEB_BINDING_WSDL_SERVICE_ID = "wsdlService";
	public static String WEB_BINDING_URI_ID = "uri";

	public static ValueNode getValueNode( ObjectNode object, String key, ValueNode defaultValue )
	{
		List pairs = object.pairs;
		NameValuePairNode pair;
		for( int idx = 0; idx < pairs.size(); idx++ )
		{
			pair = (NameValuePairNode)pairs.get(idx);
			if( pair.getName().getJavaValue().equalsIgnoreCase(key) )
			{
				return pair.getValue();
			}
		}
		return defaultValue;
	}
	
}
