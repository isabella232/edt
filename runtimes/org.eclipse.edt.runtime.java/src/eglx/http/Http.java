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
import org.eclipse.edt.runtime.java.egl.lang.AnyValue;
import eglx.services.ServiceType;
import eglx.http.HttpResponse;
@javax.xml.bind.annotation.XmlRootElement(name="Http")
public class Http extends org.eclipse.edt.runtime.java.egl.lang.AnyValue {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public HttpRequest request;
	@javax.xml.bind.annotation.XmlTransient
	public HttpResponse response;
	@javax.xml.bind.annotation.XmlTransient
	public ServiceType invocationType;
	public Http() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((Http) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.request.ezeCopy(((Http) source).request);
		this.response.ezeCopy(((Http) source).response);
		this.invocationType = ((Http) source).invocationType;
	}
	public Http ezeNewValue(Object... args) {
		return new Http();
	}
	public void ezeSetEmpty() {
		this.request.ezeSetEmpty();
		this.response.ezeSetEmpty();
		invocationType = null;
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
		response = new HttpResponse();
		org.eclipse.edt.runtime.java.egl.lang.AnyValue.ezeCopyTo(new HttpResponse(), response);
		invocationType = null;
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
	@org.eclipse.edt.javart.json.Json(name="invocationType", clazz=ServiceType.class, asOptions={})
	public ServiceType getInvocationType() {
		return (invocationType);
	}
	public void setInvocationType( ServiceType ezeValue ) {
		this.invocationType = ezeValue;
	}
}
