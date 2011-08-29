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
package eglx.services;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import egl.lang.AnyException;
@javax.xml.bind.annotation.XmlRootElement(name="ServiceBindingException")
public class ServiceBindingException extends egl.lang.AnyException {
	private static final long serialVersionUID = 10L;
	public ServiceBindingException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((ServiceBindingException) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
	}
	public ServiceBindingException ezeNewValue(Object... args) {
		return new ServiceBindingException();
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
