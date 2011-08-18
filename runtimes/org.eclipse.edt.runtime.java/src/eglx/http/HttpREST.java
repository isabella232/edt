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
package eglx.http;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import eglx.http.HttpRequest;
import eglx.services.Encoding;
import eglx.http.HttpMethod;
import eglx.http.HttpResponse;
import org.eclipse.edt.runtime.java.egl.lang.EBoolean;
import java.lang.Boolean;
import org.eclipse.edt.runtime.java.egl.lang.AnyValue;
public class HttpREST extends org.eclipse.edt.runtime.java.egl.lang.AnyValue {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public HttpRequest request;
	@javax.xml.bind.annotation.XmlTransient
	public HttpResponse response;
	@javax.xml.bind.annotation.XmlTransient
	public boolean isEglRpc;
	public HttpREST() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((HttpREST) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.request.ezeCopy(((HttpREST) source).request);
		this.response.ezeCopy(((HttpREST) source).response);
		this.isEglRpc = ((HttpREST) source).isEglRpc;
	}
	public HttpREST ezeNewValue(Object... args) {
		return new HttpREST();
	}
	public void ezeSetEmpty() {
		this.request.ezeSetEmpty();
		this.response.ezeSetEmpty();
		isEglRpc = false;
	}
	public boolean isVariableDataLength() {
		return false;
	}
	public void loadFromBuffer(ByteStorage buffer, Program program) {
	}
	public int sizeInBytes() {
		return 0;
	}
	public void storeInBuffer(ByteStorage buffer) {
	}
	public void ezeInitialize() {
		request = new HttpRequest();
		org.eclipse.edt.runtime.java.egl.lang.AnyValue.ezeCopyTo(new HttpRequest(), request);
		request.method = HttpMethod.POST;
		request.encoding = Encoding.XML;
		request.charset = "UTF-8";
		response = new HttpResponse();
		org.eclipse.edt.runtime.java.egl.lang.AnyValue.ezeCopyTo(new HttpResponse(), response);
		response.encoding = Encoding.USE_CONTENTTYPE;
		isEglRpc = false;
	}
	@org.eclipse.edt.javart.json.Json(name="request", clazz=HttpRequest.class, asOptions={})
	public HttpRequest getRequest() {
		return (request);
	}
	public void setRequest( HttpRequest ezeValue ) {
		this.request = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="response", clazz=HttpResponse.class, asOptions={})
	public HttpResponse getResponse() {
		return (response);
	}
	public void setResponse( HttpResponse ezeValue ) {
		this.response = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="isEglRpc", clazz=EBoolean.class, asOptions={})
	public boolean getIsEglRpc() {
		return (isEglRpc);
	}
	public void setIsEglRpc( boolean ezeValue ) {
		this.isEglRpc = ezeValue;
	}
}
