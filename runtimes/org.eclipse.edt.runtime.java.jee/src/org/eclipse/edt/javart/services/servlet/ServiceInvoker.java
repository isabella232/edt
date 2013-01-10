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
package org.eclipse.edt.javart.services.servlet;

import org.eclipse.edt.javart.json.ArrayNode;
import org.eclipse.edt.javart.json.JsonParser;
import org.eclipse.edt.javart.json.JsonUtilities;
import org.eclipse.edt.javart.json.ObjectNode;
import org.eclipse.edt.javart.json.ParseException;
import org.eclipse.edt.javart.json.StringNode;
import org.eclipse.edt.javart.resources.egldd.Binding;

import eglx.services.Encoding;





public abstract class ServiceInvoker extends Invoker
{
	public static final String REST_SERVICE_SESSION_ID = "com.ibm.javart.services.Rest.restfulService.session";
	private static String EGL_PARAMETER_ORDER = "EGL_PARAMETER_ORDER";
	private static final int ENCODING_JSON = 0;
	private static final int ENCODING_XML = 1;
	public static String JSON_RPC_BINDINGNAME_ID = "bindingName";
	public static String JSON_RPC_METHOD_ID = "method";
	public static String JSON_RPC_PARAMETER_ID = "params";
	public static String JSON_RPC_RESULT_ID = "result";
    private String userId;
    private String password;
    private int timeout = -1;
	private static String BINDING_ID = "binding";
	private static String WEB_BINDING_INTERFACE_ID = "interfacename";
	private static String BINDING_WEBTYPE_ID = "WebBinding";
	private static String BINDING_EGLTYPE_ID = "EGLBinding";
	private static String BINDING_NAME_ID = "name";
	private static String BINDING_TYPE_ID = "type";
	private static String PROTOCOL_LOCAL_ID = "local";
	private static String EGL_BINDING_SERVICE_NAME_ID = "serviceName";
	private static String EGL_BINDING_ALIAS_ID = "alias";
	private static String PROTOCOL_ID = "protocol";
	private static String WEB_BINDING_WSDL_LOCATION_ID = "wsdlLocation";
	private static String WEB_BINDING_WSDL_PORT_ID = "wsdlPort";
	private static String WEB_BINDING_WSDL_SERVICE_ID = "wsdlService";
	private static String WEB_BINDING_URI_ID = "uri";

    ServiceInvoker()
    {
    }

    private class JsonFunctionInfo extends FunctionInfo
    {

    	JsonFunctionInfo( String body, Encoding outEncoding )
		{
			super( body, outEncoding  );
		}
    	protected Object parsedObject()
    	{
    		if( parsedObject == null )
    		{
    			parsedObject = getJsonParameters(body);
    		}
    		return parsedObject;
    	}
		protected String getParameterOrder()
		{
			if( parameterOrder == null )
			{
				parameterOrder = JsonUtilities.getValueNode((ObjectNode)parsedObject(), EGL_PARAMETER_ORDER, new StringNode("",false) ).toJava();
			}
			return parameterOrder;
		}
       	protected Object getJsParameters()
       	{
       		if( jsParameters == null )
       		{
       			jsParameters = (ArrayNode)JsonUtilities.getValueNode((ObjectNode)parsedObject(), JSON_RPC_PARAMETER_ID, new ArrayNode() );
       		}
       		return jsParameters;
       	}
    }
    protected abstract class FunctionInfo
    {

    	protected String body;
    	protected String serviceName;
    	protected String serviceAlias;
    	protected Object parsedObject;
     	protected String parameterOrder;
    	protected String functionName;
    	protected Object jsParameters;
    	protected Binding binding;
   		protected Encoding outEncoding;


		FunctionInfo( String body, Encoding outEncoding )
		{
			this.body = body;
			this.outEncoding = outEncoding;
		}
		FunctionInfo( String body, String serviceName, String serviceAlias )
		{
			this.body = body;
			this.serviceName = serviceName;
			this.serviceAlias = serviceAlias;
		}
    	protected abstract Object parsedObject();
    	protected String getFunctionName()
    	{
    		functionName = JsonUtilities.getValueNode((ObjectNode)parsedObject(), JSON_RPC_METHOD_ID, new StringNode("",false) ).toJava();
    		return functionName;
    	}
    	protected abstract Object getJsParameters();
		protected abstract String getParameterOrder();
    }

    FunctionInfo getFunctionInfo( Encoding inEncoding, Encoding outEncoding, String body, boolean b )
    {
    	FunctionInfo info = null;
    	if( inEncoding.equals(ENCODING_JSON) )
    	{
        	info = new JsonFunctionInfo( body, outEncoding );
    	}
    	else
    	{
    		//FIXME
    	}
    	return info;
    }
    FunctionInfo getFunctionInfo( Encoding inEncoding, Encoding outEncoding, String body )
    {
    	FunctionInfo info = null;
    	if( inEncoding.equals(ENCODING_JSON) )
    	{
        	info = new JsonFunctionInfo( body, outEncoding );
    	}
    	else
    	{
    		//FIXME
    	}

    	return info;
    }
	private ObjectNode getJsonParameters( String body )
	{
		ObjectNode payload;
		try
		{
			payload = JsonParser.parse( body );
		}
		catch( ParseException e )
		{
			payload = new ObjectNode();
		}
		return payload;
	}

	public void setUserId( String userId )
	{
		this.userId = userId;
	}

	public void setPassword( String password )
	{
		this.password = password;
	}

	public int getTimeout()
	{
		return timeout;
	}

	public void setTimeout( int timeout )
	{
		this.timeout = timeout;
	}
}
