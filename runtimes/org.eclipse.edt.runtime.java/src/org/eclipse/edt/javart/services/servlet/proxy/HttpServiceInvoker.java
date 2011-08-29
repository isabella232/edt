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
package org.eclipse.edt.javart.services.servlet.proxy;

import org.eclipse.edt.javart.messages.Message;

import eglx.http.HttpRequest;
import eglx.http.HttpResponse;
import eglx.http.HttpUtilities;
import eglx.services.ServiceKind;
import eglx.services.ServiceUtilities;

public class HttpServiceInvoker extends EglHttpConnection {
	
	protected HttpServiceInvoker() {
	}
	public HttpResponse invoke(HttpRequest innerRequest)throws Exception
	{
		//debug("proxy: url="+xmlRequest.URL);
		HttpServiceHandler rest = new HttpServiceHandler();
		HttpUtilities.validateUrl(innerRequest);
		HttpResponse innerResponse = rest.invokeRestService( innerRequest, openConnection(innerRequest) );
		if( innerResponse == null )
		{
			innerResponse = new HttpResponse();
			innerResponse.setStatus(HttpUtilities.HTTP_STATUS_FAILED);
			innerResponse.setBody(eglx.json.JsonUtilities.createJsonAnyException(ServiceUtilities.buildServiceInvocationException(Message.SOA_E_WS_REST_NO_RESPONSE, new String[]{innerRequest.getUri()}, null, getServiceKind(innerRequest) )));
		}
		return innerResponse;
	}

	@Override
	public ServiceKind getServiceKind(HttpRequest innerRequest) {
		return ProxyUtilities.isSoapCall(innerRequest) ? ServiceKind.WEB : ServiceKind.REST;
	}
}
