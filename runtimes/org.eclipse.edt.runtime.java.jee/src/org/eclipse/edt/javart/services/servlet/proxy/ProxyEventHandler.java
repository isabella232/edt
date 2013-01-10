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
package org.eclipse.edt.javart.services.servlet.proxy;

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.services.ServiceUtilities;
import org.eclipse.edt.javart.services.servlet.JsonRpcInvoker;
import org.eclipse.edt.javart.services.servlet.ServletUtilities;
import org.eclipse.edt.javart.services.servlet.TracerBase;

import eglx.http.Request;
import eglx.http.Response;
import eglx.http.HttpUtilities;
import eglx.services.ServiceInvocationException;
import eglx.services.ServiceKind;


public class ProxyEventHandler extends TracerBase
{
	
	public ProxyEventHandler()
	{
	}

	public Response runProxy(String urlString, Request ruiRequest, Request serviceRequest )
	{
		if(trace()){
			tracer().put( new StringBuilder(" Request URL:")
    				.append(ruiRequest.uri)
    				.append(" method:")
    				.append(HttpUtilities.httpMethodToString(ruiRequest.method))
    				.append(" header:")
    				.append(ProxyUtilities.convert( ruiRequest.headers, " " ))
    				.append(" content:")
    				.append(ruiRequest.body == null ? "null" :ruiRequest.body).toString());
		}

		Response outerResponse = new Response();
		Response innerResponse = null;
		ServiceKind serviceKind = ServiceKind.REST;
		try
		{
			if( urlString.indexOf("___proxy") != -1 &&
					isEGLDedicatedCall(serviceRequest))
			{
				serviceKind = ServiceKind.EGL;
				//FIXME parse the body to get the service name
				innerResponse = new JsonRpcInvoker(serviceRequest.uri, serviceKind).invoke(serviceRequest);
			}
			else if( urlString.indexOf("___proxy") != -1 )
			{
				HttpServiceInvoker invoker = new HttpServiceInvoker();
				serviceKind = invoker.getServiceKind(serviceRequest);
				innerResponse = invoker.invoke(serviceRequest);
			}
		}
		catch(ServiceInvocationException sie)
		{
			if(innerResponse == null){
				innerResponse = new Response();
			}
			innerResponse.status = HttpUtilities.HTTP_STATUS_FAILED;
			innerResponse.statusMessage = HttpUtilities.HTTP_STATUS_MSG_FAILED;
			innerResponse.body = eglx.json.JsonUtilities.createJsonAnyException(sie);
		}
		catch(Throwable t)
		{
			if(innerResponse == null){
				innerResponse = new Response();
			}
			innerResponse.body = eglx.json.JsonUtilities.createJsonAnyException(ServiceUtilities.buildServiceInvocationException(Message.SOA_E_WS_PROXY_UNIDENTIFIED, new Object[0], t, serviceKind ));
			innerResponse.status = HttpUtilities.HTTP_STATUS_FAILED;
			innerResponse.statusMessage = HttpUtilities.HTTP_STATUS_MSG_FAILED;
		}
		finally
		{
			outerResponse.status = HttpUtilities.HTTP_STATUS_OK;
			outerResponse.statusMessage = HttpUtilities.HTTP_STATUS_MSG_OK;
			setBody(outerResponse, innerResponse);
		}
		if(trace()){
			tracer().put( new StringBuilder(" Response Status:")
						.append(String.valueOf( outerResponse.status ))
						.append(" status msg:")
						.append(outerResponse.statusMessage)
						.append(" header:")
						.append(ProxyUtilities.convert(outerResponse.getHeaders(), " "))
						.append(" body:")
						.append(outerResponse.body).toString() );
		}
		return outerResponse;
	}
	
	protected boolean isEGLDedicatedCall(Request request) {
		return ProxyUtilities.isEGLDedicatedCall(request);
	}
	
	protected void setBody(Response outerResponse, Response innerResponse) {
		ServletUtilities.setBody(outerResponse, innerResponse);
	}
}
