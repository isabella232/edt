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
package eglx.persistence.sql;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import egl.lang.AnyException;
import org.eclipse.edt.runtime.java.egl.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.runtime.java.egl.lang.EString;
import java.lang.String;
@javax.xml.bind.annotation.XmlRootElement(name="SqlException")
public class SQLException extends egl.lang.AnyException {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String SQLState;
	@javax.xml.bind.annotation.XmlTransient
	public Integer ErrorCode;
	public SQLException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((SQLException) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.SQLState = ((SQLException) source).SQLState;
		this.ErrorCode = ((SQLException) source).ErrorCode;
	}
	public SQLException ezeNewValue(Object... args) {
		return new SQLException();
	}
	public void ezeSetEmpty() {
		SQLState = null;
		ErrorCode = null;
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
		SQLState = null;
		ErrorCode = null;
	}
	@org.eclipse.edt.javart.json.Json(name="SQLState", clazz=EString.class, asOptions={})
	public String getSQLState() {
		return (SQLState);
	}
	public void setSQLState( String ezeValue ) {
		this.SQLState = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="ErrorCode", clazz=EInt.class, asOptions={})
	public Integer getErrorCode() {
		return (ErrorCode);
	}
	public void setErrorCode( Integer ezeValue ) {
		this.ErrorCode = ezeValue;
	}
}
