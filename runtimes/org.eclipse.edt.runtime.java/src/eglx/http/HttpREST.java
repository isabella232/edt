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
import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.Program;
import org.eclipse.edt.runtime.java.egl.lang.AnyValue;

import eglx.services.Encoding;
public class HttpREST extends AnyValue {
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
		org.eclipse.edt.runtime.java.egl.lang.AnyValue.ezeCopyTo(new HttpRequest(), request);
		request.method = HttpMethod.POST;
		request.encoding = Encoding.XML;
		request.charset = "UTF-8";
		org.eclipse.edt.runtime.java.egl.lang.AnyValue.ezeCopyTo(new HttpResponse(), response);
		response.encoding = Encoding.USE_CONTENTTYPE;
		isEglRpc = false;
	}
	public HttpRequest getRequest() {
		return (request);
	}
	public void setRequest( HttpRequest ezeValue ) {
		this.request = ezeValue;
	}
	public HttpResponse getResponse() {
		return (response);
	}
	public void setResponse( HttpResponse ezeValue ) {
		this.response = ezeValue;
	}
	public boolean getIsEglRpc() {
		return (isEglRpc);
	}
	public void setIsEglRpc( boolean ezeValue ) {
		this.isEglRpc = ezeValue;
	}
}
