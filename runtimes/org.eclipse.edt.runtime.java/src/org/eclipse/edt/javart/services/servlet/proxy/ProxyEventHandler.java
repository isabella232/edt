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

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.services.servlet.JsonRpcInvoker;
import org.eclipse.edt.javart.services.servlet.ServletUtilities;
import org.eclipse.edt.javart.services.servlet.TracerBase;

import eglx.http.HttpRequest;
import eglx.http.HttpResponse;
import eglx.http.HttpUtilities;
import eglx.services.ServiceBindingException;
import eglx.services.ServiceInvocationException;
import eglx.services.ServiceKind;
import eglx.services.ServiceUtilities;


public class ProxyEventHandler extends TracerBase
{
	
	public ProxyEventHandler()
	{
	}

	public HttpResponse runProxy(String urlString, HttpRequest ruiRequest, HttpRequest serviceRequest )
	{
		if(trace()){
			tracer().put( new StringBuilder(" Request URL:")
    				.append(ruiRequest.getUri())
    				.append(" method:")
    				.append(HttpUtilities.httpMethodToString(ruiRequest.getMethod()))
    				.append(" header:")
    				.append(ProxyUtilities.convert( ruiRequest.getHeaders() ))
    				.append(" content:")
    				.append(ruiRequest.getBody()).toString());
		}

		HttpResponse outerResponse = new HttpResponse();
		HttpResponse innerResponse = null;
		ServiceKind serviceKind = ServiceKind.REST;
		try
		{
			if( urlString.indexOf("___proxy") != -1 &&
					isEGLDedicatedCall(serviceRequest))
			{
				serviceKind = ServiceKind.EGL;
				//FIXME parse the body to get the service name
				innerResponse = new JsonRpcInvoker(serviceRequest.getUri(), serviceKind).invoke(serviceRequest);
			}
			else if( urlString.indexOf("___proxy") != -1 )
			{
				HttpServiceInvoker invoker = new HttpServiceInvoker();
				serviceKind = invoker.getServiceKind(serviceRequest);
				innerResponse = invoker.invoke(serviceRequest);
			}
		}
		catch(ServiceBindingException sbe )
		{
			if(innerResponse == null){
				innerResponse = new HttpResponse();
			}
			innerResponse.setStatus(HttpUtilities.HTTP_STATUS_FAILED);
			innerResponse.setStatusMessage(HttpUtilities.HTTP_STATUS_MSG_FAILED);
			innerResponse.setBody(eglx.json.JsonUtilities.createJsonAnyException(sbe));
		}
		catch(ServiceInvocationException sie)
		{
			if(innerResponse == null){
				innerResponse = new HttpResponse();
			}
			innerResponse.setStatus(HttpUtilities.HTTP_STATUS_FAILED);
			innerResponse.setStatusMessage(HttpUtilities.HTTP_STATUS_MSG_FAILED);
			innerResponse.setBody(eglx.json.JsonUtilities.createJsonAnyException(sie));
		}
		catch(Throwable t)
		{
			if(innerResponse == null){
				innerResponse = new HttpResponse();
			}
			innerResponse.setBody(eglx.json.JsonUtilities.createJsonAnyException(ServiceUtilities.buildServiceInvocationException(Message.SOA_E_WS_PROXY_UNIDENTIFIED, new Object[0], t, serviceKind )));
			innerResponse.setStatus(HttpUtilities.HTTP_STATUS_FAILED);
			innerResponse.setStatusMessage(HttpUtilities.HTTP_STATUS_MSG_FAILED);
		}
		finally
		{
			outerResponse.setStatus(HttpUtilities.HTTP_STATUS_OK);
			outerResponse.setStatusMessage(HttpUtilities.HTTP_STATUS_MSG_OK);
			setBody(outerResponse, innerResponse);
		}
		if(trace()){
			tracer().put( new StringBuilder(" Response Status:")
						.append(String.valueOf( outerResponse.getStatus() ))
						.append(" status msg:")
						.append(outerResponse.getStatusMessage())
						.append(" header:")
						.append(ProxyUtilities.convert(outerResponse.getHeaders()))
						.append(" body:")
						.append(outerResponse.getBody()).toString() );
		}
		return outerResponse;
	}
	
	protected boolean isEGLDedicatedCall(HttpRequest request) {
		return ProxyUtilities.isEGLDedicatedCall(request);
	}
	
	protected void setBody(HttpResponse outerResponse, HttpResponse innerResponse) {
		ServletUtilities.setBody(outerResponse, innerResponse);
	}
}
