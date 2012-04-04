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
import eglx.http.Request;
import eglx.http.Response;

import org.eclipse.edt.runtime.java.eglx.lang.EAny;
@javax.xml.bind.annotation.XmlRootElement(name="HttpSoap")
public class HttpSoap extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	@org.eclipse.edt.javart.json.Json(name="request", clazz=Request.class, asOptions={})
	public Request request;
	@org.eclipse.edt.javart.json.Json(name="response", clazz=Response.class, asOptions={})
	public Response response;
	@org.eclipse.edt.javart.json.Json(name="responseHeader", clazz=EAny.class, asOptions={})
	public eglx.lang.EAny responseHeader;
	
	public HttpSoap() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
		request = new Request();
		response = new Response();
		responseHeader = null;
	}
	public Request getRequest() {
		return request;
	}
	public Response getResponse() {
		return response;
	}
	public void setSoapRequestHeader(eglx.lang.EAny rec) {
	}
	public eglx.lang.EAny getSoapResponseHeader() {
		return responseHeader;
	}
}
