package org.eclipse.edt.jtopen.data.common;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.AnyValue;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="SystemDefinition")
public class SystemDefinition extends org.eclipse.edt.runtime.java.eglx.lang.AnyValue {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String systemName;
	@javax.xml.bind.annotation.XmlTransient
	public String userId;
	@javax.xml.bind.annotation.XmlTransient
	public String password;
	public SystemDefinition() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((SystemDefinition) source);
	}
	public void ezeCopy(eglx.lang.AnyValue source) {
		this.systemName = ((SystemDefinition) source).systemName;
		this.userId = ((SystemDefinition) source).userId;
		this.password = ((SystemDefinition) source).password;
	}
	public SystemDefinition ezeNewValue(Object... args) {
		return new SystemDefinition();
	}
	public void ezeSetEmpty() {
		systemName = "";
		userId = "";
		password = "";
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
		systemName = "";
		userId = "";
		password = "";
	}
	@org.eclipse.edt.javart.json.Json(name="systemName", clazz=EString.class, asOptions={})
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String ezeValue) {
		systemName = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="userId", clazz=EString.class, asOptions={})
	public String getUserId() {
		return userId;
	}
	public void setUserId(String ezeValue) {
		userId = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="password", clazz=EString.class, asOptions={})
	public String getPassword() {
		return password;
	}
	public void setPassword(String ezeValue) {
		password = ezeValue;
	}
}
