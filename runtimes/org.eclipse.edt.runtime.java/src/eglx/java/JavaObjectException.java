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
package eglx.java;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.EString;

import java.lang.String;
@javax.xml.bind.annotation.XmlRootElement(name="JavaObjectException")
public class JavaObjectException extends eglx.lang.AnyException {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String exceptionType;
	public JavaObjectException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((JavaObjectException) source);
	}
	public void ezeCopy(eglx.lang.AnyValue source) {
		this.exceptionType = ((JavaObjectException) source).exceptionType;
	}
	public JavaObjectException ezeNewValue(Object... args) {
		return new JavaObjectException();
	}
	public void ezeSetEmpty() {
		exceptionType = "";
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
		exceptionType = "";
	}
	@org.eclipse.edt.javart.json.Json(name="exceptionType", clazz=EString.class, asOptions={})
	public String getExceptionType() {
		return (exceptionType);
	}
	public void setExceptionType( String ezeValue ) {
		this.exceptionType = ezeValue;
	}
}
