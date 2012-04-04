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
package org.eclipse.edt.javart.services.servlet;

import java.util.List;

import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.Program;
import org.eclipse.edt.runtime.java.eglx.lang.EString;

import eglx.lang.EAny;

class EglRestRpcRequest extends org.eclipse.edt.runtime.java.eglx.lang.AnyValue {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String method;
	@javax.xml.bind.annotation.XmlTransient
	public List<eglx.lang.EAny> params;
	public EglRestRpcRequest() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((EglRestRpcRequest) source);
	}
	public void ezeCopy(eglx.lang.AnyValue source) {
		this.method = ((EglRestRpcRequest) source).method;
		this.params = ((EglRestRpcRequest) source).params;
	}
	public EglRestRpcRequest ezeNewValue(Object... args) {
		return new EglRestRpcRequest();
	}
	public void ezeSetEmpty() {
		method = "";
		params = null;
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
		method = "";
		params = null;
	}
	@org.eclipse.edt.javart.json.Json(name="method", clazz=EString.class, asOptions={})
	public String getMethod() {
		return (method);
	}
	public void setMethod( String ezeValue ) {
		this.method = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="params", clazz=EAny.class, asOptions={})
	public List<eglx.lang.EAny> getParams() {
		return (params);
	}
	public void setParams( List<eglx.lang.EAny> ezeValue ) {
		this.params = ezeValue;
	}
}
