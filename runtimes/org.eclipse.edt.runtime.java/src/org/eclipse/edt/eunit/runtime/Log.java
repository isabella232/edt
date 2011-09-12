package org.eclipse.edt.eunit.runtime;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.egl.lang.EString;
import java.lang.String;
import org.eclipse.edt.runtime.java.egl.lang.AnyValue;
@javax.xml.bind.annotation.XmlRootElement()
public class Log extends org.eclipse.edt.runtime.java.egl.lang.AnyValue {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String msg;
	public Log() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((Log) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.msg = ((Log) source).msg;
	}
	public Log ezeNewValue(Object... args) {
		return new Log();
	}
	public void ezeSetEmpty() {
		msg = Constants.EMPTY_STRING;
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
		msg = Constants.EMPTY_STRING;
	}
	@org.eclipse.edt.javart.json.Json(name="msg", clazz=EString.class, asOptions={})
	public String getMsg() {
		return (msg);
	}
	public void setMsg( String ezeValue ) {
		this.msg = ezeValue;
	}
}
