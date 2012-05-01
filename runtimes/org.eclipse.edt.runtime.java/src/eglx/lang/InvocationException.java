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
package eglx.lang;
import org.eclipse.edt.javart.*;
import java.lang.String;

import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
public class InvocationException extends eglx.lang.AnyException {
	private static final long serialVersionUID = 10L;
	public String name;
	public int returnValue;
	public InvocationException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((InvocationException) source);
	}
	public void ezeCopy(eglx.lang.AnyValue source) {
		this.name = ((InvocationException) source).name;
		this.returnValue = ((InvocationException) source).returnValue;
	}
	public InvocationException ezeNewValue(Object... args) {
		return new InvocationException();
	}
	public void ezeSetEmpty() {
		name = "";
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
		name = "";
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
