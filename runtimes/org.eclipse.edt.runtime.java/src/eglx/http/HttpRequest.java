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
import org.eclipse.edt.runtime.java.egl.lang.AnyValue;
import org.eclipse.edt.runtime.java.egl.lang.EString;
import java.lang.String;
import org.eclipse.edt.runtime.java.egl.lang.EDictionary;
import eglx.http.HttpMethod;
import eglx.services.Encoding;
@javax.xml.bind.annotation.XmlRootElement(name="HttpRequest")
public class HttpRequest extends org.eclipse.edt.runtime.java.egl.lang.AnyValue {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String uri;
	@javax.xml.bind.annotation.XmlTransient
	public HttpMethod method;
	@javax.xml.bind.annotation.XmlTransient
	public Encoding encoding;
	@javax.xml.bind.annotation.XmlTransient
	public String charset;
	@javax.xml.bind.annotation.XmlTransient
	public String contentType;
	@javax.xml.bind.annotation.XmlTransient
	public egl.lang.EDictionary headers;
	@javax.xml.bind.annotation.XmlTransient
	public String body;
	public HttpRequest() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((HttpRequest) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.uri = ((HttpRequest) source).uri;
		this.method = ((HttpRequest) source).method;
		this.encoding = ((HttpRequest) source).encoding;
		this.charset = ((HttpRequest) source).charset;
		this.contentType = ((HttpRequest) source).contentType;
		this.headers = ((HttpRequest) source).headers;
		this.body = ((HttpRequest) source).body;
	}
	public HttpRequest ezeNewValue(Object... args) {
		return new HttpRequest();
	}
	public void ezeSetEmpty() {
		uri = null;
		method = null;
		encoding = null;
		charset = null;
		contentType = null;
		headers = null;
		body = null;
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
		uri = null;
		method = null;
		encoding = null;
		charset = null;
		contentType = null;
		headers = null;
		body = null;
	}
	@org.eclipse.edt.javart.json.Json(name="uri", clazz=EString.class, asOptions={})
	public String getUri() {
		return (uri);
	}
	public void setUri( String ezeValue ) {
		this.uri = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="method", clazz=HttpMethod.class, asOptions={})
	public HttpMethod getMethod() {
		return (method);
	}
	public void setMethod( HttpMethod ezeValue ) {
		this.method = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="encoding", clazz=Encoding.class, asOptions={})
	public Encoding getEncoding() {
		return (encoding);
	}
	public void setEncoding( Encoding ezeValue ) {
		this.encoding = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="charset", clazz=EString.class, asOptions={})
	public String getCharset() {
		return (charset);
	}
	public void setCharset( String ezeValue ) {
		this.charset = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="contentType", clazz=EString.class, asOptions={})
	public String getContentType() {
		return (contentType);
	}
	public void setContentType( String ezeValue ) {
		this.contentType = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="headers", clazz=EDictionary.class, asOptions={})
	public egl.lang.EDictionary getHeaders() {
		return (headers);
	}
	public void setHeaders( egl.lang.EDictionary ezeValue ) {
		this.headers = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="body", clazz=EString.class, asOptions={})
	public String getBody() {
		return (body);
	}
	public void setBody( String ezeValue ) {
		this.body = ezeValue;
	}
}
