/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

public class NumericOverflowException extends RuntimeException {

	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public NumericOverflowException() {
	}
	
	public NumericOverflowException(Exception ex) {
		super(ex);
	}

	public NumericOverflowException(String arg0) {
		super(arg0);
	}
}
