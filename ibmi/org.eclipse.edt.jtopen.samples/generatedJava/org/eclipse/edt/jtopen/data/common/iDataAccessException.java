package org.eclipse.edt.jtopen.data.common;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import eglx.lang.AnyException;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="iDataAccessException")
public class iDataAccessException extends eglx.lang.AnyException {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String functionName;
	@javax.xml.bind.annotation.XmlTransient
	public String path;
	@javax.xml.bind.annotation.XmlTransient
	public AnyException exception;
	public iDataAccessException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((iDataAccessException) source);
	}
	public void ezeCopy(eglx.lang.AnyValue source) {
		this.functionName = ((iDataAccessException) source).functionName;
		this.path = ((iDataAccessException) source).path;
		this.exception = ((iDataAccessException) source).exception;
	}
	public iDataAccessException ezeNewValue(Object... args) {
		return new iDataAccessException();
	}
	public void ezeSetEmpty() {
		functionName = "";
		path = "";
		exception = new AnyException();
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
		functionName = "";
		path = "";
		exception = new AnyException();
	}
	@org.eclipse.edt.javart.json.Json(name="functionName", clazz=EString.class, asOptions={})
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String ezeValue) {
		functionName = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="path", clazz=EString.class, asOptions={})
	public String getPath() {
		return path;
	}
	public void setPath(String ezeValue) {
		path = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="exception", clazz=AnyException.class, asOptions={})
	public AnyException getException() {
		return exception;
	}
	public void setException(AnyException ezeValue) {
		exception = ezeValue;
	}
}
