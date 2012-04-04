/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import eglx.http.Request;
import eglx.http.Response;
import eglx.http.HttpUtilities;
import eglx.services.ServiceKind;

public class HttpServiceInvoker extends EglHttpConnection {
	
	protected HttpServiceInvoker() {
	}
	public Response invoke(Request innerRequest)throws Throwable
	{
		//debug("proxy: url="+xmlRequest.URL);
		HttpServiceHandler rest = new HttpServiceHandler();
		HttpUtilities.validateUrl(innerRequest);
		Response innerResponse = rest.invokeRestService( innerRequest, openConnection(innerRequest) );
		if( innerResponse == null )
		{
			innerResponse = new Response();
			innerResponse.status = HttpUtilities.HTTP_STATUS_FAILED;
			innerResponse.body = eglx.json.JsonUtilities.createJsonAnyException(ServiceUtilities.buildServiceInvocationException(Message.SOA_E_WS_REST_NO_RESPONSE, new String[]{innerRequest.uri}, null, getServiceKind(innerRequest) ));
		}
		return innerResponse;
	}

	@Override
	public ServiceKind getServiceKind(Request innerRequest) {
		return ProxyUtilities.isSoapCall(innerRequest) ? ServiceKind.WEB : ServiceKind.REST;
	}
}
