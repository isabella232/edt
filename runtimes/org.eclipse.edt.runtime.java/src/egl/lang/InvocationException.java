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
import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Program;
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
	public String getName() {
		return (name);
	}
	public void setName( String ezeValue ) {
		this.name = ezeValue;
	}
	public int getReturnValue() {
		return (returnValue);
	}
	public void setReturnValue( int ezeValue ) {
		this.returnValue = ezeValue;
	}
}
