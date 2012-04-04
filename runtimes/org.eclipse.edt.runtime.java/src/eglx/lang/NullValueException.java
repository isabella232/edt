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
import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.Program;
public class NullValueException extends eglx.lang.AnyException {
	private static final long serialVersionUID = 10L;
	public NullValueException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((NullValueException) source);
	}
	public void ezeCopy(eglx.lang.AnyValue source) {
	}
	public NullValueException ezeNewValue(Object... args) {
		return new NullValueException();
	}
	public void ezeSetEmpty() {
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
	}
}
