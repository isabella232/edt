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
import org.eclipse.edt.runtime.java.egl.lang.EString;
import java.lang.String;
import org.eclipse.edt.runtime.java.egl.lang.AnyValue;
import org.eclipse.edt.runtime.java.egl.lang.EDictionary;
import org.eclipse.edt.runtime.java.egl.lang.EInt32;
import java.lang.Integer;
import eglx.http.HttpResponse;
import eglx._service.Encoding;
public class HttpResponse extends AnyValue {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public Integer status;
	@javax.xml.bind.annotation.XmlTransient
	public String statusMessage;
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
	public HttpResponse() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((HttpResponse) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.status = ((HttpResponse) source).status;
		this.statusMessage = ((HttpResponse) source).statusMessage;
		this.encoding = ((HttpResponse) source).encoding;
		this.charset = ((HttpResponse) source).charset;
		this.contentType = ((HttpResponse) source).contentType;
		this.headers = ((HttpResponse) source).headers;
		this.body = ((HttpResponse) source).body;
	}
	public HttpResponse ezeNewValue(Object... args) {
		return new HttpResponse();
	}
	public void ezeSetEmpty() {
		status = null;
		statusMessage = null;
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
		status = null;
		statusMessage = null;
		encoding = null;
		charset = null;
		contentType = null;
		headers = null;
		body = null;
	}
	public Integer getStatus() {
		return (status);
	}
	public void setStatus( Integer ezeValue ) {
		this.status = ezeValue;
	}
	public String getStatusMessage() {
		return (statusMessage);
	}
	public void setStatusMessage( String ezeValue ) {
		this.statusMessage = ezeValue;
	}
	public Encoding getEncoding() {
		return (encoding);
	}
	public void setEncoding( Encoding ezeValue ) {
		this.encoding = ezeValue;
	}
	public String getCharset() {
		return (charset);
	}
	public void setCharset( String ezeValue ) {
		this.charset = ezeValue;
	}
	public String getContentType() {
		return (contentType);
	}
	public void setContentType( String ezeValue ) {
		this.contentType = ezeValue;
	}
	public egl.lang.EDictionary getHeaders() {
		return (headers);
	}
	public void setHeaders( egl.lang.EDictionary ezeValue ) {
		this.headers = ezeValue;
	}
	public String getBody() {
		return (body);
	}
	public void setBody( String ezeValue ) {
		this.body = ezeValue;
	}
}
