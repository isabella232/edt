/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.eunit.runtime;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import org.eclipse.edt.runtime.java.eglx.lang.AnyValue;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="Status")
public class Status extends org.eclipse.edt.runtime.java.eglx.lang.AnyValue {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public int code;
	@javax.xml.bind.annotation.XmlTransient
	public String reason;
	public Status() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((Status) source);
	}
	public void ezeCopy(eglx.lang.AnyValue source) {
		this.code = ((Status) source).code;
		this.reason = ((Status) source).reason;
	}
	public Status ezeNewValue(Object... args) {
		return new Status();
	}
	public void ezeSetEmpty() {
		code = 0;
		reason = "";
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
		code = 0;
		reason = "";
	}
	@org.eclipse.edt.javart.json.Json(name="code", clazz=EInt.class, asOptions={})
	public int getCode() {
		return code;
	}
	public void setCode(int ezeValue) {
		code = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="reason", clazz=EString.class, asOptions={})
	public String getReason() {
		return reason;
	}
	public void setReason(String ezeValue) {
		reason = ezeValue;
	}
}
