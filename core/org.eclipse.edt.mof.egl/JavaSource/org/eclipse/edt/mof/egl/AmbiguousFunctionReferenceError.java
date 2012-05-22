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
package org.eclipse.edt.mof.egl;

public class AmbiguousFunctionReferenceError extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public AmbiguousFunctionReferenceError() {
		super();
	}

	/**
	 * @param message
	 */
	public AmbiguousFunctionReferenceError(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AmbiguousFunctionReferenceError(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public AmbiguousFunctionReferenceError(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
