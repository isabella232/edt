package org.eclipse.edt.jtopen.data.queue;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.AnyValue;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import org.eclipse.edt.jtopen.data.common.SystemDefinition;
import org.eclipse.edt.runtime.java.eglx.lang.EBoolean;
import java.lang.Boolean;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="DataQueueDefinition")
public class DataQueueDefinition extends org.eclipse.edt.runtime.java.eglx.lang.AnyValue {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String qname;
	@javax.xml.bind.annotation.XmlTransient
	public String libname;
	@javax.xml.bind.annotation.XmlTransient
	public boolean keyed;
	@javax.xml.bind.annotation.XmlTransient
	public int keyLen;
	@javax.xml.bind.annotation.XmlTransient
	public int msgLen;
	@javax.xml.bind.annotation.XmlTransient
	public boolean initializedByEGL;
	@javax.xml.bind.annotation.XmlTransient
	public SystemDefinition systemDef;
	@javax.xml.bind.annotation.XmlTransient
	public String path;
	public DataQueueDefinition() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((DataQueueDefinition) source);
	}
	public void ezeCopy(eglx.lang.AnyValue source) {
		this.qname = ((DataQueueDefinition) source).qname;
		this.libname = ((DataQueueDefinition) source).libname;
		this.keyed = ((DataQueueDefinition) source).keyed;
		this.keyLen = ((DataQueueDefinition) source).keyLen;
		this.msgLen = ((DataQueueDefinition) source).msgLen;
		this.initializedByEGL = ((DataQueueDefinition) source).initializedByEGL;
		this.systemDef.ezeCopy(((DataQueueDefinition) source).systemDef);
		this.path = ((DataQueueDefinition) source).path;
	}
	public DataQueueDefinition ezeNewValue(Object... args) {
		return new DataQueueDefinition();
	}
	public void ezeSetEmpty() {
		qname = "";
		libname = "";
		keyed = false;
		keyLen = 0;
		msgLen = 0;
		initializedByEGL = false;
		this.systemDef.ezeSetEmpty();
		path = null;
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
		qname = "";
		libname = "";
		keyed = false;
		keyLen = 0;
		msgLen = 0;
		systemDef = new SystemDefinition();
		path = null;
		initializedByEGL = false;
	}
	@org.eclipse.edt.javart.json.Json(name="qname", clazz=EString.class, asOptions={})
	public String getQname() {
		return qname;
	}
	public void setQname(String ezeValue) {
		qname = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="libname", clazz=EString.class, asOptions={})
	public String getLibname() {
		return libname;
	}
	public void setLibname(String ezeValue) {
		libname = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="keyed", clazz=EBoolean.class, asOptions={})
	public boolean getKeyed() {
		return keyed;
	}
	public void setKeyed(Boolean ezeValue) {
		keyed = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="keyLen", clazz=EInt.class, asOptions={})
	public int getKeyLen() {
		return keyLen;
	}
	public void setKeyLen(Integer ezeValue) {
		keyLen = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="msgLen", clazz=EInt.class, asOptions={})
	public int getMsgLen() {
		return msgLen;
	}
	public void setMsgLen(Integer ezeValue) {
		msgLen = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="initializedByEGL", clazz=EBoolean.class, asOptions={})
	public boolean getInitializedByEGL() {
		return initializedByEGL;
	}
	public void setInitializedByEGL(Boolean ezeValue) {
		initializedByEGL = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="systemDef", clazz=SystemDefinition.class, asOptions={})
	public SystemDefinition getSystemDef() {
		SystemDefinition eze$Temp1 = null;
		return (org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(systemDef, eze$Temp1));
	}
	public void setSystemDef(SystemDefinition ezeValue) {
		systemDef = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(ezeValue, systemDef);
	}
	@org.eclipse.edt.javart.json.Json(name="path", clazz=EString.class, asOptions={})
	public String getPath() {
		return path;
	}
	public void setPath(String ezeValue) {
		path = ezeValue;
	}
}
