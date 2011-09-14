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
import org.eclipse.edt.runtime.java.egl.lang.EString;
import java.lang.String;
import egl.lang.AnyException;
@javax.xml.bind.annotation.XmlRootElement(name="DynamicAccessException")
public class DynamicAccessException extends egl.lang.AnyException {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String key;
	public DynamicAccessException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((DynamicAccessException) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.key = ((DynamicAccessException) source).key;
	}
	public DynamicAccessException ezeNewValue(Object... args) {
		return new DynamicAccessException();
	}
	public void ezeSetEmpty() {
		key = Constants.EMPTY_STRING;
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
		key = Constants.EMPTY_STRING;
	}
	@org.eclipse.edt.javart.json.Json(name="key", clazz=EString.class, asOptions={})
	public String getKey() {
		return (key);
	}
	public void setKey( String ezeValue ) {
		this.key = ezeValue;
	}
}
