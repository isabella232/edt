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

import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.json.JsonParser;
import org.eclipse.edt.javart.json.ParseException;
import org.eclipse.edt.javart.resources.ExecutableBase;

import eglx._service.ServiceInvocationException;
import eglx.http.HttpRequest;
import eglx.http.HttpResponse;
import eglx.json.JSONToEGLConverter;



public class ServletUtilities 
{

	private ServletUtilities() 
	{
	}

    public static HttpRequest createHttpRequest(ExecutableBase program, String object)throws ServiceInvocationException{
    	HttpRequest request = null;
    	try {
    		request = new HttpRequest();
			JSONToEGLConverter.convertToEgl(program, request, JsonParser.parse(object));
		} catch (JavartException e) {
//FIXME
		} catch (ParseException e) {
//FIXME
		}
		return request;
    }
    
    public static void setBody(HttpResponse response, JavartException jrte){
    	
    }
    
    public static void setBody(HttpResponse outerResponse, HttpResponse innerResponse){
    	if(innerResponse == null){
    		innerResponse = new HttpResponse();
    	}
    }
    
}
