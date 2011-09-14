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
import org.eclipse.edt.runtime.java.egl.lang.EDictionary;
import eglx.http.HttpMethod;
import org.eclipse.edt.runtime.java.egl.lang.EString;
import java.lang.String;
import org.eclipse.edt.runtime.java.egl.lang.EInt;
import java.lang.Integer;
import eglx.services.Encoding;
import org.eclipse.edt.runtime.java.egl.lang.EglAny;
@javax.xml.bind.annotation.XmlRootElement(name="Request")
public class Request extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	@org.eclipse.edt.javart.json.Json(name="uri", clazz=EString.class, asOptions={})
	public String uri;
	@org.eclipse.edt.javart.json.Json(name="method", clazz=HttpMethod.class, asOptions={})
	public HttpMethod method;
	@org.eclipse.edt.javart.json.Json(name="encoding", clazz=Encoding.class, asOptions={})
	public Encoding encoding;
	@org.eclipse.edt.javart.json.Json(name="charset", clazz=EString.class, asOptions={})
	public String charset;
	@org.eclipse.edt.javart.json.Json(name="contentType", clazz=EString.class, asOptions={})
	public String contentType;
	@org.eclipse.edt.javart.json.Json(name="headers", clazz=EDictionary.class, asOptions={})
	public egl.lang.EDictionary headers;
	@org.eclipse.edt.javart.json.Json(name="body", clazz=EString.class, asOptions={})
	public String body;
	@org.eclipse.edt.javart.json.Json(name="timeout", clazz=EInt.class, asOptions={})
	public Integer timeout;
	
	public Request() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
		uri = null;
		method = null;
		encoding = null;
		charset = null;
		contentType = null;
		headers = null;
		body = null;
		timeout = null;
	}
}
