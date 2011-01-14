/*******************************************************************************
 * Copyright © 2000, 2010 IBM Corporation and others.
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

/**
 * @author twilson
 *
 */
public class NoSuchMemberError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public NoSuchMemberError() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public NoSuchMemberError(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NoSuchMemberError(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public NoSuchMemberError(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
