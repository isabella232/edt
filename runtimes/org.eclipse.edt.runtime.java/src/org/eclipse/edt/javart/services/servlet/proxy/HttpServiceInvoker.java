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
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.services.servlet.ServletUtilities;

import eglx.http.HttpRequest;
import eglx.http.HttpResponse;
import eglx.http.HttpUtilities;
import eglx.json.JsonUtilities;
import eglx.services.ServiceKind;
import eglx.services.ServiceUtilities;

public class HttpServiceInvoker extends EglHttpConnection {
	
	protected HttpServiceInvoker(ExecutableBase program) {
		super(program);
	}
	public HttpResponse invoke(RuiBrowserHttpRequest ruiRequest, HttpRequest innerRequest )throws Exception
	{
		//debug("proxy: url="+xmlRequest.URL);
		HttpResponse innerResponse = invoke( innerRequest );
		if( innerResponse == null )
		{
			innerResponse = new HttpResponse();
			innerResponse.setStatus(HttpUtilities.HTTP_STATUS_FAILED);
			ServletUtilities.setBody(innerResponse, ServiceUtilities.buildServiceInvocationException( program(), Message.SOA_E_WS_REST_NO_RESPONSE, new String[]{innerRequest.getUri()}, null, getServiceKind(innerRequest) ));
		}
		return innerResponse;
	}

	private HttpResponse invoke(HttpRequest restRequest ) throws Exception
	{
		HttpResponse response = null;

		HttpServiceHandler rest = new HttpServiceHandler(program());
		HttpUtilities.validateUrl(program(), restRequest);
		response = rest.invokeRestService( restRequest, openConnection(restRequest) );
		response.setBody( JsonUtilities.trimJSON( response.getBody() ) );
		return response;
	}
	@Override
	ServiceKind getServiceKind(HttpRequest innerRequest) {
		return ProxyUtilities.isSoapCall(innerRequest) ? ServiceKind.WEB : ServiceKind.REST;
	}
}
