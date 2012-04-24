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
public class TypeCastException extends eglx.lang.AnyException {
	private static final long serialVersionUID = 10L;
	public String castToName;
	public String actualTypeName;
	public TypeCastException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((TypeCastException) source);
	}
	public void ezeCopy(eglx.lang.AnyValue source) {
		this.castToName = ((TypeCastException) source).castToName;
		this.actualTypeName = ((TypeCastException) source).actualTypeName;
	}
	public TypeCastException ezeNewValue(Object... args) {
		return new TypeCastException();
	}
	public void ezeSetEmpty() {
		castToName = "";
		actualTypeName = "";
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
		castToName = "";
		actualTypeName = "";
	}
	public String getCastToName() {
		return (castToName);
	}
	public void setCastToName( String ezeValue ) {
		this.castToName = ezeValue;
	}
	public String getActualTypeName() {
		return (actualTypeName);
	}
	public void setActualTypeName( String ezeValue ) {
		this.actualTypeName = ezeValue;
	}
}
