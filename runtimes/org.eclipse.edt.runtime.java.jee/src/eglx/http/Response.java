/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.runtime.java.eglx.lang.EDictionary;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import org.eclipse.edt.runtime.java.eglx.lang.EString;

import eglx.services.Encoding;
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
	private Map<Object, List<String>> headers;
	public Map<Object, List<String>> getHeaders() {
		return headers;
	}
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
	public void initHeader(){
		if(headers == null){
			headers = new HashMap<Object, List<String>>();
		}
	}
	public void addHeader(Object key, Object value){
		if(headers == null){
			initHeader();
		}
		List<String> values = new ArrayList<String>();
		if(value instanceof List<?>){
			values.addAll((List<String>)value);
		}
		else{
			values.add(value.toString());
		}
		if(key == null){
			headers.put(new NullHeaderKey(),values);
		}
		else if(headers.containsKey(key)){
			headers.get(key).addAll(values);
		}
		else{
			headers.put(key, values);
		}
	}
	private class NullHeaderKey{
		@Override
		public String toString() {
			return null;
		}
	}
}
