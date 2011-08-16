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
package org.eclipse.edt.javart.services.servlet.proxy;

import egl.lang.AnyException;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.json.ArrayNode;
import org.eclipse.edt.javart.json.JsonParser;
import org.eclipse.edt.javart.json.JsonUtilities;
import org.eclipse.edt.javart.json.ObjectNode;
import org.eclipse.edt.javart.json.ParseException;
import org.eclipse.edt.javart.json.StringNode;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.services.ServiceReference;
import org.eclipse.edt.javart.services.bindings.Binding;
import org.eclipse.edt.javart.services.bindings.EGLBinding;
import org.eclipse.edt.javart.services.bindings.ProtocolLOCAL;
import org.eclipse.edt.javart.services.bindings.WebBinding;

import eglx.http.HttpResponse;
import eglx.services.*;





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

    ServiceInvoker( ExecutableBase program )
    {
    	super(program);
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
    	protected Binding getBinding()
    	{
    		return binding;
    	}
    	protected void setBinding( Binding binding )
    	{
    		this.binding = binding;
    	}
		protected abstract String getParameterOrder();
    }

    FunctionInfo getFunctionInfo( Encoding inEncoding, Encoding outEncoding, String body, EGLBinding binding )
    {
    	FunctionInfo info = null;
    	if( inEncoding.equals(ENCODING_JSON) )
    	{
        	info = new JsonFunctionInfo( body, outEncoding );
        	info.setBinding(binding);
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
			setFomBindingNode(info);
    	}
    	else
    	{
    		//FIXME
    	}

    	return info;
    }
   	private void setFomBindingNode( FunctionInfo info )
   	{
	   	Binding binding = null;
		ObjectNode bindingNode = (ObjectNode)JsonUtilities.getValueNode((ObjectNode)info.parsedObject(), JsonUtilities.BINDING_ID, new ObjectNode() );
	   	// String bindingType = JsonUtilities.getValueNode(bindingNode, JsonUtilities.BINDING_TYPE_ID, new StringNode("webbinding",false) ).toJava();
	   	String name = JsonUtilities.getValueNode(bindingNode, JsonUtilities.BINDING_NAME_ID, new StringNode("",false) ).toJava().trim();
		if( bindingNode != null )
		{
		   	String bindingType = JsonUtilities.getValueNode(bindingNode, JsonUtilities.BINDING_TYPE_ID, new StringNode("webbinding",false) ).toJava();
		   	if( bindingType.equalsIgnoreCase(JsonUtilities.BINDING_WEBTYPE_ID))
		   	{
				String interfaceName = JsonUtilities.getValueNode(bindingNode, JsonUtilities.WEB_BINDING_INTERFACE_ID, new StringNode("",false) ).toJava();
				if( name == null || name.length() == 0 )
				{
					int partNameIdx = interfaceName.lastIndexOf(".") + 1;
					//set the default name to the interface class name
					if( partNameIdx == 0 )
					{
						name = interfaceName;
					}
					else
					{
						name = interfaceName.substring( partNameIdx );
					}
				}
				binding = new WebBinding( name,
						interfaceName,
						JsonUtilities.getValueNode(bindingNode, JsonUtilities.WEB_BINDING_WSDL_LOCATION_ID, new StringNode("",false) ).toJava(),
						JsonUtilities.getValueNode(bindingNode, JsonUtilities.WEB_BINDING_WSDL_PORT_ID, new StringNode("",false) ).toJava(),
						JsonUtilities.getValueNode(bindingNode, JsonUtilities.WEB_BINDING_WSDL_SERVICE_ID, new StringNode("",false) ).toJava(),
						JsonUtilities.getValueNode(bindingNode, JsonUtilities.WEB_BINDING_URI_ID, new StringNode("",false) ).toJava() );
			}
		   	else if( bindingType.equalsIgnoreCase(JsonUtilities.BINDING_EGLTYPE_ID))
		   	{
		   		/*
		   		name: this.f_bindingName,
                type: "EGLBinding",
                serviceName: this.f_serviceName,
                alias: this.f_alias,
                protocol: "local"

		   		 */
		   		binding = new EGLBinding( 
		   							JsonUtilities.getValueNode(bindingNode, JsonUtilities.BINDING_NAME_ID, new StringNode("",false) ).toJava(), 
		   							JsonUtilities.getValueNode(bindingNode, JsonUtilities.EGL_BINDING_SERVICE_NAME_ID, new StringNode("",false) ).toJava(), 
		   							JsonUtilities.getValueNode(bindingNode, JsonUtilities.EGL_BINDING_ALIAS_ID, new StringNode("",false) ).toJava() );
				String protocol = JsonUtilities.getValueNode(bindingNode, JsonUtilities.PROTOCOL_ID, new StringNode("",false) ).toJava();
		   		if( protocol.equalsIgnoreCase(JsonUtilities.PROTOCOL_LOCAL_ID) )
		   		{
		   			((EGLBinding)binding).setProtocol( new ProtocolLOCAL( "" ) );
		   		}
		   		
		   	}
		}
		info.setBinding(binding);
   	}

/*FIXME   	private static MethodParameter[] getEmptyParameters( ExecutableBase program, ServiceReference ref, FunctionInfo info, IntValue serviceKind ) throws AnyException
   	{
   		if( ref instanceof LocalProxy && ((LocalProxy)ref).getServiceReference() instanceof ServiceCore2 )
   		{
   	   		if( serviceKind == ServiceKind.REST )
   	   		{
   	   			return ((ServiceCore2)((LocalProxy)ref).getServiceReference()).parametersByOperationName( info.getOperationName() );
   	   		}
   	   		else
   	   		{
   	   			return ((ServiceCore)((LocalProxy)ref).getServiceReference()).parameters( info.getFunctionName() );
   	   		}
   		}
/*TODO SOAP   		else if( ref instanceof WebProxy )
   		{
			return wsdl2EglConversion( program, info );
   		}
   		else if( ref instanceof JAXWSReference )
		{
			return ((JAXWSReference)ref).parameters( info.getFunctionName() );
		}
   		else if( ref instanceof WASWebProxy )
		{
			return ((WASWebProxy)ref).parameters( info.getFunctionName() );
		}*/
/*TODO host program service   		else if( ref instanceof HostProgramServiceReference )
   		{
   			return ((HostProgramServiceReference)ref).parameters( info.getFunctionName() );
   		}
   		return new MethodParameter[0];
   	}*/
	public String invokeEglService( final FunctionInfo info ) throws ServiceInvocationException
	{
		String returnVal = null;
		ServiceReference ref = null;
		ExecutableBase program = program();
		try
		{
			/*
			 * First parse the body into JSON objects
			 * the json object include all the information needed to create a WebBindiing
			 * Use the ServiceBinder to instantiate a service reference
			 * populate the parameters
			 * invoke the service
			 * convert the parameters back to JSON
			 */

			ref = getServiceReference( info );

			if( "eze_endStatefulServiceSession".equalsIgnoreCase( info.getFunctionName() ) )
			{
				endSession( program()._runUnit() );
				returnVal = "{}";
			}
			else
			{
/*				setSession( ((ExecutableBase)((LocalProxy)ref).getServiceReference())._runUnit() );
	
				if( ref instanceof ExecutableBase )
				{
					program = (ExecutableBase)ref;
				}
	
				MethodParameter[] parameters = getEglParameters( program, ref, info, ServiceKind.REST );
		   		if( ref instanceof LocalProxy && ((LocalProxy)ref).getServiceReference() instanceof ServiceCore2 )
		   		{
					returnVal = convert2JSON( program,
							((ServiceCore2)((LocalProxy)ref).getServiceReference()).ezeInvokeByOperationName(info.getOperationName(), info.getFunctionName(), parameters),
							parameters, info.outEncoding, ServiceKind.REST );
		   		}
		   		else
		   		{
					returnVal = convert2JSON( program,
							ref.ezeInvoke(info.getOperationName(), info.getFunctionName(), parameters),
							parameters, info.outEncoding, ServiceKind.REST );
		   		}*/
			}
			
//			((ExecutableBase)((LocalProxy)ref).getServiceReference())._runUnit().endRunUnit((ExecutableBase)((LocalProxy)ref).getServiceReference());
		}
		catch (ServiceInvocationException sie)
		{
			throw sie;
		}
		catch (Throwable t)
		{
			AnyException sie;
			if( info == null )
			{
				sie = ServiceUtilities.buildServiceInvocationException( program, Message.SOA_E_WS_PROXY_REST, new String[] {"unknown", "unknown"}, t, ServiceKind.REST );
			}
			else
			{
				sie = ServiceUtilities.buildServiceInvocationException( program, Message.SOA_E_WS_PROXY_REST, new String[] {info.getBinding().getName(), info.getFunctionName()}, t, ServiceKind.REST );
			}
			if( sie instanceof ServiceInvocationException )
			{
				throw (ServiceInvocationException)sie;
			}
			else
			{
				returnVal = eglx.json.JsonUtilities.createJsonAnyException(program,sie);
			}
		}
		finally
		{
			try
			{
//				removeSession( ((ExecutableBase)((LocalProxy)ref).getServiceReference())._runUnit() );
			}
			catch( AnyException j ){}
		}
		return returnVal;
	}


	protected ServiceReference getServiceReference( FunctionInfo info ) throws Exception
	{
		return null;
//FIXME		return program()._runUnit().getServiceBinder().bindService(program(), info.getBinding() );
	}
	protected void setSession( RunUnit runUnit )
	{
	}

	protected void endSession( RunUnit runUnit )
	{
	}

	protected void removeSession( RunUnit runUnit )
	{
	}
/*FIXME	private static String invokeService( ExecutableBase program, ServiceReference ref, FunctionInfo info, int outEncoding, ServiceKind serviceKind ) throws Exception
	{
		if( ref instanceof ExecutableBase )
		{
			program = (ExecutableBase)ref;
		}

		MethodParameter[] parameters = getEglParameters( program, ref, info, serviceKind );
		return convert2JSON( program,
					ref.ezeInvoke( info.getOperationName(), info.getFunctionName(), parameters),
					parameters, outEncoding, serviceKind );
	}

	private static MethodParameter[] getEglParameters( ExecutableBase program, ServiceReference ref, FunctionInfo info, IntValue serviceKind ) throws AnyException
	{
		MethodParameter[] parameters = null;
		parameters = getEmptyParameters(program, ref, info, serviceKind );
		assignByPosition(program, info.getJsParameters(), parameters, serviceKind);
		return parameters;
	}*/

/*FIXME	private static String convert2JSON( ExecutableBase program, Object eglFunctionReturn, MethodParameter[] parameters, int outEncoding, IntValue serviceKind ) throws AnyException
	{
		String returnVal = null;
		if( parameters.length > 0 && parameters[parameters.length-1].parameterKind() == MethodParameter.RETURN )
		{
			Assign.run( program, parameters[parameters.length-1].parameter(), eglFunctionReturn );
		}
		if( outEncoding == ENCODING_JSON )
		{
			returnVal = convert2JsonByPosition( program, parameters, serviceKind).toJson();
		}
		else if( outEncoding == ENCODING_XML )
		{
			//FIXME
		}
		return returnVal;
	}*/

	public String invokeRestService( final String className, final String functionName, final int inEncoding, final int outEncoding, final String body )
	{
		String returnVal = null;
		try
		{
			HttpResponse response = null;
			if( response != null )
			{
				returnVal = "{}";
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		return returnVal;
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
/*FIXME	private static ObjectNode convert2JsonByPosition( ExecutableBase program, MethodParameter[] parameters, ServiceKind serviceKind)
		throws AnyException
	{
		//1 or less return parameters will only be returned as an object
		ObjectNode result = new ObjectNode();
		ArrayNode returnParams = new ArrayNode();
		int paramKind;
		int returnCnt = 0;
		for( int idx = 0; idx < parameters.length; idx++ )
		{
			paramKind = parameters[idx].parameterKind();
			try
			{
				if( paramKind != MethodParameter.IN )
				{
					returnCnt++;
					if( paramKind == MethodParameter.OUT && ProxyUtilities.hasExtraOutParameter( parameters[idx].parameter()) )
					{
						//references have an extra parameter and the second has the value
						idx++;
					}
					returnParams.addValue( EGLToJSONConverter.convertToJson(program, parameters[idx].parameter() ) );
				}
			}
			catch( AnyException jrte )
			{
				throw ServiceUtilities.buildServiceInvocationException( program, Message.SOA_E_WS_PROXY_PARMETERS_JSON2EGL, new String[]{((Storage)parameters[idx].parameter()).name(), ConvertToString.run( program, parameters[idx].parameter() ) }, jrte, serviceKind );
			}

		}
		if( returnCnt > 1 )
		{
			result.addPair( new NameValuePairNode( new StringNode( JSON_RPC_RESULT_ID, true ), returnParams ) );
		}
		else if( returnCnt == 1 )
		{
			result.addPair( new NameValuePairNode( new StringNode( JSON_RPC_RESULT_ID, true ), (ValueNode)returnParams.getValues().get( 0 ) ) );
		}
		return result;
	}

	private static void assignByPosition( ExecutableBase program, Object jsParameters, MethodParameter[] parameters, ServiceKind serviceKind) throws AnyException
	{
		if( jsParameters instanceof ArrayNode )
		{
			List<ValueNode> jsonValues = ((ArrayNode)jsParameters).getValues();
			int jsonValuesIdx = 0;
			ValueNode jsonValue = null;
			for( int idx = 0; idx < parameters.length; idx++ )
			{
				try
				{
					if( parameters[idx].parameterKind() != MethodParameter.OUT && parameters[idx].parameterKind() != MethodParameter.RETURN )
					{
						if( jsonValuesIdx < jsonValues.size() )
						{
							jsonValue = jsonValues.get( jsonValuesIdx++ );
							JSONToEGLConverter.convertToEgl(program, parameters[idx].parameter(), jsonValue );
						}
						else
						{
							//FIXME throw invalid values
						}
					}
				}
				catch( AnyException jrte )
				{
					throw ServiceUtilities.buildServiceInvocationException( program, Message.SOA_E_WS_PROXY_PARMETERS_JSON2EGL, new String[]{((Storage)parameters[idx].parameter()).name(), jsonValue.toJava() }, jrte, serviceKind );
				}
			}
		}
	}*/


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
