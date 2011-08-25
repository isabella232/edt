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
package eglx.java;
import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Program;
public class JavaObjectException extends egl.lang.AnyException {
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
	public void ezeCopy(egl.lang.AnyValue source) {
		this.exceptionType = ((JavaObjectException) source).exceptionType;
	}
	public JavaObjectException ezeNewValue(Object... args) {
		return new JavaObjectException();
	}
	public void ezeSetEmpty() {
		exceptionType = Constants.EMPTY_STRING;
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
		exceptionType = Constants.EMPTY_STRING;
	}
	public String getExceptionType() {
		return (exceptionType);
	}
	public void setExceptionType( String ezeValue ) {
		this.exceptionType = ezeValue;
	}
}
