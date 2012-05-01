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
import org.eclipse.edt.runtime.java.eglx.lang.EString;

import java.lang.String;
public class DynamicAccessException extends eglx.lang.AnyException {
	private static final long serialVersionUID = 10L;
	public String key;
	public DynamicAccessException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((DynamicAccessException) source);
	}
	public void ezeCopy(eglx.lang.AnyValue source) {
		this.key = ((DynamicAccessException) source).key;
	}
	public DynamicAccessException ezeNewValue(Object... args) {
		return new DynamicAccessException();
	}
	public void ezeSetEmpty() {
		key = "";
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
		key = "";
	}
	public String getKey() {
		return (key);
	}
	public void setKey( String ezeValue ) {
		this.key = ezeValue;
	}
}
