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
package eglx.http;
import org.eclipse.edt.javart.resources.*;
import eglx.rest.ServiceType;
import eglx.http.Request;
import eglx.http.Response;
@javax.xml.bind.annotation.XmlRootElement(name="HttpRest")
public class HttpRest extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	@org.eclipse.edt.javart.json.Json(name="request", clazz=Request.class, asOptions={})
	public Request request;
	@org.eclipse.edt.javart.json.Json(name="response", clazz=Response.class, asOptions={})
	public Response response;
	@org.eclipse.edt.javart.json.Json(name="restType", clazz=ServiceType.class, asOptions={})
	public ServiceType restType;
	
	public HttpRest() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
		request = new Request();
		response = new Response();
		restType = null;
	}
	public Request getRequest() {
		return request;
	}
	public Response getResponse() {
		return response;
	}
}
