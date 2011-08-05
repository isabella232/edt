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
package org.eclipse.edt.runtime.java.eglx.xml;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.runtime.java.egl.lang.AnyException;

public class XMLProcessingException extends AnyException {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public String detail;

	public XMLProcessingException() throws JavartException {
		detail = "";
	}

	public XMLProcessingException(Exception ex) {
		super(ex);
	}

	public XMLProcessingException(String detail) throws JavartException {
		this.detail = detail;
	}

	public Object clone() throws java.lang.CloneNotSupportedException {
		XMLProcessingException ezeClone = (XMLProcessingException) super.clone();
		ezeClone.detail = detail;
		return ezeClone;
	}
}
