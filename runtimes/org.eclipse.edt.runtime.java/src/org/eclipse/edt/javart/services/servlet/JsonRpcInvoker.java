/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.edt.javart.services.servlet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.javart.Runtime;
import org.eclipse.edt.javart.json.ArrayNode;
import org.eclipse.edt.javart.json.JsonParser;
import org.eclipse.edt.javart.json.JsonUtilities;
import org.eclipse.edt.javart.json.ObjectNode;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.services.FunctionParameterKind;
import org.eclipse.edt.javart.services.FunctionSignature;
import org.eclipse.edt.javart.services.ServiceBase;
import org.eclipse.edt.javart.services.ServiceUtilities;
import org.eclipse.edt.runtime.java.eglx.lang.EDictionary;

import eglx.http.HttpUtilities;
import eglx.http.Request;
import eglx.http.Response;
import eglx.json.JsonLib;
import eglx.lang.AnyException;
import eglx.services.ServiceInvocationException;
import eglx.services.ServiceKind;

public class JsonRpcInvoker extends LocalServiceInvoker {
	private static String JSON_RPC_METHOD_ID = "method";
	private static String JSON_RPC_PARAMETER_ID = "params";
	private static String JSON_RPC_RESULT_ID = "result";

	public JsonRpcInvoker(String serviceClassName, ServiceKind serviceKind) {
		super(serviceClassName, serviceKind);
	}

	public Response invoke(Request request)throws AnyException
	{
		if (trace()){
			tracer().put("invoking " + getServiceClassName() + " using " + ServiceUtilities.convert(getServiceKind()));
		}
		Response response = new Response();
		traceElapsedTime( true );

		try{
			ObjectNode jsonRequest = request.body == null ? new ObjectNode() : JsonParser.parse(request.body);
			if (trace()){
				tracer().put( "  invoking function " + JsonUtilities.getValueNode(jsonRequest, JSON_RPC_METHOD_ID).toJava() );
			}
			String result = wrapperProxyReturn( invokeEglService(jsonRequest) );
			traceElapsedTime( false );
			if (trace()){
				tracer().put( "return data from dedicated service:" + result == null ? "null" : result );
			}
			response.body = result;
			boolean failed = resultContainsError( result );
			response.status =  failed ? HttpUtilities.HTTP_STATUS_FAILED: HttpUtilities.HTTP_STATUS_OK;
			response.statusMessage = failed ? HttpUtilities.HTTP_STATUS_MSG_FAILED : HttpUtilities.HTTP_STATUS_MSG_OK;
		}
		catch(ServiceInvocationException sie){
			response.body = eglx.json.JsonUtilities.createJsonAnyException(sie);
			response.status = HttpUtilities.HTTP_STATUS_FAILED;
			response.statusMessage = HttpUtilities.HTTP_STATUS_MSG_FAILED;
		} catch (Throwable t) {
			response.body = eglx.json.JsonUtilities.createJsonAnyException( 
					ServiceUtilities.buildServiceInvocationException(Message.SOA_E_EGL_SERVICE_INVOCATION, new String[]{request.body == null ? "null" : request.body, getServiceClassName()}, t, getServiceKind()));
			response.status = HttpUtilities.HTTP_STATUS_FAILED;
			response.statusMessage = HttpUtilities.HTTP_STATUS_MSG_FAILED;
		}
		return response;
	}
	public String invokeEglService( final ObjectNode jsonRequest ) throws ServiceInvocationException
	{
		String returnVal = null;
		String methodName = JsonUtilities.getValueNode(jsonRequest, JSON_RPC_METHOD_ID).toJava();
		try
		{
			Method method = getMethod(methodName);
			Object[] parameters = eglx.json.JsonUtilities.getParameters(method, (ArrayNode)JsonUtilities.getValueNode(jsonRequest, JSON_RPC_PARAMETER_ID));
			ServiceBase service = getService();
			Runtime.getRunUnit().setActiveExecutable(service);
			Object ret = method.invoke(service, parameters);
			returnVal = convertToJson(method, parameters, ret);
			Runtime.getRunUnit().endRunUnit(service);
		}
		catch (ServiceInvocationException sie)
		{
			throw sie;
		}
		catch (Throwable t)
		{
			throw ServiceUtilities.buildServiceInvocationException(Message.SOA_E_WS_SERVICE, new String[] {getServiceClassName(), methodName}, t, getServiceKind() );
		}
		return returnVal;
	}
	
	private String convertToJson(Method method, Object[] parameters, Object ret){
		FunctionSignature signature = method.getAnnotation(FunctionSignature.class);
		final List<Object> responseParameters = new ArrayList<Object>();
		for(int idx = 0; idx < method.getParameterTypes().length; idx++){
			if(!FunctionParameterKind.IN.equals(signature.parameters()[idx].kind())){
				responseParameters.add(eglx.json.JsonUtilities.wrapCalendar(parameters[idx], signature.parameters()[idx].parameterType(), signature.parameters()[idx].asOptions()));
			}
		}
		if(ret != null){
			responseParameters.add(eglx.json.JsonUtilities.wrapCalendar(ret, signature.parameters()[signature.parameters().length - 1].parameterType(), signature.parameters()[signature.parameters().length - 1].asOptions()));
		}
		Object response;
		if(responseParameters.size() == 0){
			response = null;
		}
		else if(responseParameters.size() == 1){
			response = responseParameters.get(0);
		}
		else{
			response = responseParameters;
		}
		EDictionary responseObject = new EDictionary();
		responseObject.put(JSON_RPC_RESULT_ID, response);
		return JsonLib.convertToJSON(responseObject);
	}

	private String wrapperProxyReturn( Object returnVal )
	{
		return returnVal == null ? "{}" : returnVal.toString();
	}

	protected boolean resultContainsError( String result )
	{
		return result.indexOf( "{\"error\" : {" ) != -1;
	}
	@Override
	public ServiceKind getServiceKind(Request innerRequest) {
		return getServiceKind();
	}
}
