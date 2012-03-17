package org.eclipse.edt.jtopen.data.area;
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
@javax.xml.bind.annotation.XmlRootElement(name="DataAreaDefinition")
public class DataAreaDefinition extends org.eclipse.edt.runtime.java.eglx.lang.AnyValue {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String areaname;
	@javax.xml.bind.annotation.XmlTransient
	public String libname;
	@javax.xml.bind.annotation.XmlTransient
	public int noBytes;
	@javax.xml.bind.annotation.XmlTransient
	public int noDecimals;
	@javax.xml.bind.annotation.XmlTransient
	public int areaType;
	@javax.xml.bind.annotation.XmlTransient
	public boolean initializedByEGL;
	@javax.xml.bind.annotation.XmlTransient
	public SystemDefinition systemDef;
	@javax.xml.bind.annotation.XmlTransient
	public String path;
	public DataAreaDefinition() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((DataAreaDefinition) source);
	}
	public void ezeCopy(eglx.lang.AnyValue source) {
		this.areaname = ((DataAreaDefinition) source).areaname;
		this.libname = ((DataAreaDefinition) source).libname;
		this.noBytes = ((DataAreaDefinition) source).noBytes;
		this.noDecimals = ((DataAreaDefinition) source).noDecimals;
		this.areaType = ((DataAreaDefinition) source).areaType;
		this.initializedByEGL = ((DataAreaDefinition) source).initializedByEGL;
		this.systemDef.ezeCopy(((DataAreaDefinition) source).systemDef);
		this.path = ((DataAreaDefinition) source).path;
	}
	public DataAreaDefinition ezeNewValue(Object... args) {
		return new DataAreaDefinition();
	}
	public void ezeSetEmpty() {
		areaname = "";
		libname = "";
		noBytes = 0;
		noDecimals = 0;
		areaType = 0;
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
		areaname = "";
		libname = "";
		noBytes = 0;
		noDecimals = 0;
		areaType = 0;
		systemDef = new SystemDefinition();
		path = null;
		initializedByEGL = false;
	}
	@org.eclipse.edt.javart.json.Json(name="areaname", clazz=EString.class, asOptions={})
	public String getAreaname() {
		return areaname;
	}
	public void setAreaname(String ezeValue) {
		areaname = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="libname", clazz=EString.class, asOptions={})
	public String getLibname() {
		return libname;
	}
	public void setLibname(String ezeValue) {
		libname = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="noBytes", clazz=EInt.class, asOptions={})
	public int getNoBytes() {
		return noBytes;
	}
	public void setNoBytes(Integer ezeValue) {
		noBytes = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="noDecimals", clazz=EInt.class, asOptions={})
	public int getNoDecimals() {
		return noDecimals;
	}
	public void setNoDecimals(Integer ezeValue) {
		noDecimals = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="areaType", clazz=EInt.class, asOptions={})
	public int getAreaType() {
		return areaType;
	}
	public void setAreaType(Integer ezeValue) {
		areaType = ezeValue;
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
