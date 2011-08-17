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

import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.resources.ExecutableBase;

import egl.lang.AnyException;
import eglx.http.HttpRequest;
import eglx.http.HttpResponse;
import eglx.json.JsonLib;
import eglx.services.ServiceInvocationException;



public class ServletUtilities 
{

	private ServletUtilities() 
	{
	}

    public static HttpRequest createHttpRequest(ExecutableBase program, String object)throws ServiceInvocationException{
    	HttpRequest request = null;
    	try {
    		request = new HttpRequest();
			JsonLib.convertFromJSON(object, request);
		} catch (AnyException e) {
//FIXME
		}
		return request;
    }
    
    public static void setBody(RunUnit ru, HttpResponse response, AnyException jrte){
    	response.setBody(JsonLib.convertToJSON(jrte));
    }
    
    public static void setBody(ExecutableBase program, HttpResponse outerResponse, HttpResponse innerResponse){
    	if(innerResponse == null){
    		innerResponse = new HttpResponse();
    	}
    	outerResponse.setBody(JsonLib.convertToJSON(innerResponse));
    }
    
}
