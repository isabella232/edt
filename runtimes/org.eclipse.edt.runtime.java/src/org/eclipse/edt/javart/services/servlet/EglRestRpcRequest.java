package org.eclipse.edt.javart.services.servlet;

import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Program;
import org.eclipse.edt.runtime.java.egl.lang.EString;

import egl.lang.EglAny;

class EglRestRpcRequest extends org.eclipse.edt.runtime.java.egl.lang.AnyValue {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String method;
	@javax.xml.bind.annotation.XmlTransient
	public egl.lang.EglList<egl.lang.EglAny> params;
	public EglRestRpcRequest() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((EglRestRpcRequest) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.method = ((EglRestRpcRequest) source).method;
		this.params = ((EglRestRpcRequest) source).params;
	}
	public EglRestRpcRequest ezeNewValue(Object... args) {
		return new EglRestRpcRequest();
	}
	public void ezeSetEmpty() {
		method = Constants.EMPTY_STRING;
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
		method = Constants.EMPTY_STRING;
		params = null;
	}
	@org.eclipse.edt.javart.json.Json(name="method", clazz=EString.class, asOptions={})
	public String getMethod() {
		return (method);
	}
	public void setMethod( String ezeValue ) {
		this.method = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="params", clazz=EglAny.class, asOptions={})
	public egl.lang.EglList<egl.lang.EglAny> getParams() {
		return (params);
	}
	public void setParams( egl.lang.EglList<egl.lang.EglAny> ezeValue ) {
		this.params = ezeValue;
	}
}
