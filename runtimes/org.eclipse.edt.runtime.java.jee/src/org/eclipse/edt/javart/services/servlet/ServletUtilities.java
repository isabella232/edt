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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.runtime.java.eglx.lang.EDictionary;

import eglx.http.HttpUtilities;
import eglx.http.Request;
import eglx.http.Response;
import eglx.json.JsonLib;
import eglx.lang.AnyException;
import eglx.services.ServiceInvocationException;



public class ServletUtilities 
{

	private ServletUtilities() 
	{
	}

    public static Request createHttpRequest(String object)throws ServiceInvocationException{
    	Request request = null;
    	if(object == null){
			request = new Request();
    	}
    	else{
	    	try {
	    		EDictionary localRequest = new EDictionary();
				JsonLib.convertFromJSON(object, localRequest);
				request = new Request();
				Object obj = unBox(localRequest.get("body"));
				request.body = obj == null ? null : obj.toString();
				request.headers = (EDictionary)unBox(localRequest.get("headers"));
				obj = unBox(localRequest.get("method"));
				request.method = HttpUtilities.convert(obj instanceof Number ? ((Number)obj).intValue(): -1);
				obj = unBox(localRequest.get("uri"));
				request.uri = obj == null ? null : obj.toString();
			} catch (AnyException e) {
				//FIXME throw exception
			}
    	}
		return request;
    }
    
    private static Object unBox(Object obj){
    	while(obj instanceof AnyBoxedObject<?>){
    		obj = ((AnyBoxedObject<?>)obj).ezeUnbox();
    	}
    	return obj;
    }
    public static void setBody(Response outerResponse, Response innerResponse){
    	if(innerResponse == null){
    		innerResponse = new Response();
    	}
    	outerResponse.body = JsonLib.convertToJSON(innerResponse);
    }

	public static Request createNewRequest(javax.servlet.http.HttpServletRequest httpServletReq ) throws IOException {

		Request newRequest = new Request();
		newRequest.method = HttpUtilities.convert(httpServletReq.getMethod());
		newRequest.uri = httpServletReq.getRequestURI();
		newRequest.headers = processHeaders(httpServletReq);
		/*FIXME add parameter map
		if(JSON_RPC_GET_METHOD_ID.equalsIgnoreCase(newRequest.method))
		{
			newRequest.arguments = httpServletReq.getParameterMap();//processArgs(request);
		}*/
		BufferedReader reader = httpServletReq.getReader();
		newRequest.body = processContent(httpServletReq.getContentLength(), reader);

		return newRequest;
	}
	private static EDictionary processHeaders(javax.servlet.http.HttpServletRequest httpServletReq) throws IOException{
		EDictionary result = new EDictionary();
		Enumeration headerEnum = httpServletReq.getHeaderNames();
		while(headerEnum.hasMoreElements()){
			String headername = (String)headerEnum.nextElement();
			result.put(headername, httpServletReq.getHeader(headername));
		}

		return result;
	}
	private static String processContent(int contentLength, BufferedReader reader) throws IOException
	{
		StringBuilder contentBuffer = new StringBuilder();
		if( contentLength > 0 ){	
			//int nRead = reader.read();
			for (int i = 0; i < contentLength; )
			{				
				char nRead = (char)reader.read();
				contentBuffer.append(nRead);
				
				//need to calculate the byte count of a character
				String str = Character.toString(nRead);
				byte[] bytes = str.getBytes("utf-8");
				i += bytes.length;
			}
		}
		return contentBuffer.toString();
	}


}
