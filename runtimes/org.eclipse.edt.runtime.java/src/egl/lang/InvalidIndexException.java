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
import org.eclipse.edt.javart.Program;
public class InvalidIndexException extends egl.lang.AnyException {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public int index;
	public InvalidIndexException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((InvalidIndexException) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.index = ((InvalidIndexException) source).index;
	}
	public InvalidIndexException ezeNewValue(Object... args) {
		return new InvalidIndexException();
	}
	public void ezeSetEmpty() {
		index = 0;
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
		index = 0;
	}
	public int getIndex() {
		return (index);
	}
	public void setIndex( int ezeValue ) {
		this.index = ezeValue;
	}
}
