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
package org.eclipse.edt.runtime.java.eglx.services;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.runtime.java.egl.lang.AnyException;

public class ServiceInvocationException extends AnyException {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public String detail1;
	public String detail2;
	public String detail3;
	
	public ServiceInvocationException() throws JavartException {
		this.detail1 = "";
		this.detail2 = "";
		this.detail3 = "";
	}

	public ServiceInvocationException(Exception ex) {
		super(ex);
	}

	public ServiceInvocationException(String detail1, String detail2, String detail3) {
		this.detail1 = detail1;
		this.detail2 = detail2;
		this.detail3 = detail3;
	}

	public Object clone() throws java.lang.CloneNotSupportedException {
		ServiceInvocationException ezeClone = (ServiceInvocationException) super.clone();
		ezeClone.detail1 = detail1;
		ezeClone.detail2 = detail2;
		ezeClone.detail3 = detail3;
		return ezeClone;
	}
}
