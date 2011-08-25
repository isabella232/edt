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
package org.eclipse.edt.javart.services.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.runtime.java.egl.lang.EDictionary;

import egl.lang.AnyException;
import eglx.http.HttpRequest;
import eglx.http.HttpResponse;
import eglx.http.HttpUtilities;
import eglx.json.JsonLib;
import eglx.services.ServiceInvocationException;



public class ServletUtilities 
{

	private ServletUtilities() 
	{
	}

    public static HttpRequest createHttpRequest(String object)throws ServiceInvocationException{
    	HttpRequest request = null;
    	try {
    		EDictionary localRequest = new EDictionary();
			JsonLib.convertFromJSON(object, localRequest);
			request = new HttpRequest();
			request.setBody(unBox(localRequest.get("body")).toString());
			request.setHeaders((EDictionary)unBox(localRequest.get("headers")));
			request.setMethod(HttpUtilities.convert(((Long)unBox(localRequest.get("method"))).intValue()));
			request.setUri(unBox(localRequest.get("uri")).toString());
		} catch (AnyException e) {
//FIXME
		}
		return request;
    }
    
    private static Object unBox(Object obj){
    	while(obj instanceof AnyBoxedObject<?>){
    		obj = ((AnyBoxedObject<?>)obj).ezeUnbox();
    	}
    	return obj;
    }
    public static void setBody(HttpResponse outerResponse, HttpResponse innerResponse){
    	if(innerResponse == null){
    		innerResponse = new HttpResponse();
    	}
    	outerResponse.setBody(JsonLib.convertToJSON(innerResponse));
    }

	public static HttpRequest createNewRequest(javax.servlet.http.HttpServletRequest httpServletReq ) throws IOException {

		HttpRequest newRequest = new HttpRequest();
		newRequest.setMethod(HttpUtilities.convert(httpServletReq.getMethod()));
		newRequest.setUri(httpServletReq.getRequestURI()); //processRequestURL(request);
		newRequest.setHeaders(processHeaders(httpServletReq));
		/*FIXME add parameter map
		if(JSON_RPC_GET_METHOD_ID.equalsIgnoreCase(newRequest.method))
		{
			newRequest.arguments = httpServletReq.getParameterMap();//processArgs(request);
		}*/
		BufferedReader reader = httpServletReq.getReader();
		newRequest.setBody(processContent(httpServletReq.getContentLength(), reader));

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
