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
package egl.lang;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import egl.lang.AnyException;
import org.eclipse.edt.runtime.java.egl.lang.EString;
import java.lang.String;
import org.eclipse.edt.runtime.java.egl.lang.EInt;
import java.lang.Integer;
@javax.xml.bind.annotation.XmlRootElement(name="InvocationException")
public class InvocationException extends egl.lang.AnyException {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String name;
	@javax.xml.bind.annotation.XmlTransient
	public int returnValue;
	public InvocationException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((InvocationException) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.name = ((InvocationException) source).name;
		this.returnValue = ((InvocationException) source).returnValue;
	}
	public InvocationException ezeNewValue(Object... args) {
		return new InvocationException();
	}
	public void ezeSetEmpty() {
		name = Constants.EMPTY_STRING;
		returnValue = 0;
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
		name = Constants.EMPTY_STRING;
		returnValue = 0;
	}
	@org.eclipse.edt.javart.json.Json(name="name", clazz=EString.class, asOptions={})
	public String getName() {
		return (name);
	}
	public void setName( String ezeValue ) {
		this.name = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="returnValue", clazz=EInt.class, asOptions={})
	public int getReturnValue() {
		return (returnValue);
	}
	public void setReturnValue( int ezeValue ) {
		this.returnValue = ezeValue;
	}
}
