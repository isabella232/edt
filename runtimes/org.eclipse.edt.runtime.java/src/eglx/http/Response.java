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
import java.lang.String;
import eglx.services.Encoding;

import org.eclipse.edt.runtime.java.eglx.lang.EDictionary;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import org.eclipse.edt.runtime.java.eglx.lang.EString;

import java.lang.Integer;
@javax.xml.bind.annotation.XmlRootElement(name="Response")
public class Response extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	@org.eclipse.edt.javart.json.Json(name="status", clazz=EInt.class, asOptions={})
	public Integer status;
	@org.eclipse.edt.javart.json.Json(name="statusMessage", clazz=EString.class, asOptions={})
	public String statusMessage;
	@org.eclipse.edt.javart.json.Json(name="encoding", clazz=Encoding.class, asOptions={})
	public Encoding encoding;
	@org.eclipse.edt.javart.json.Json(name="charset", clazz=EString.class, asOptions={})
	public String charset;
	@org.eclipse.edt.javart.json.Json(name="contentType", clazz=EString.class, asOptions={})
	public String contentType;
	@org.eclipse.edt.javart.json.Json(name="headers", clazz=EDictionary.class, asOptions={})
	public eglx.lang.EDictionary headers;
	@org.eclipse.edt.javart.json.Json(name="body", clazz=EString.class, asOptions={})
	public String body;
	
	public Response() {
		super();
		ezeInitialize();
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
}
