/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.eunit.runtime;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import eglx.lang.AnyException;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="AssertionFailedException")
public class AssertionFailedException extends eglx.lang.AnyException {
	private static final long serialVersionUID = 10L;
	public AssertionFailedException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((AssertionFailedException) source);
	}
	public void ezeCopy(eglx.lang.AnyValue source) {
	}
	public AssertionFailedException ezeNewValue(Object... args) {
		return new AssertionFailedException();
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
