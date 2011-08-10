/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.runtime.java.egl.lang;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.JavartException;

public class InvocationException extends AnyException {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public String name;
	public int returnValue;

	public InvocationException() throws JavartException {
		returnValue = 0;
		ezeInitialize();
	}

	public InvocationException(Exception e) {
		super(e);
	}

	/**
	 * Returns a clone of this object.
	 */
	public Object clone() throws CloneNotSupportedException {
		InvocationException theClone = (InvocationException) super.clone();
		theClone.name = name;
		theClone.returnValue = returnValue;
		return theClone;
	}
}
