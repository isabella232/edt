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
package org.eclipse.edt.mof.serialization;

/**
 * @author twilson
 *
 */
public class SerializationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SerializationException() {
		super();
	}

	/**
	 * @param message
	 */
	public SerializationException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SerializationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public SerializationException(Throwable cause) {
		super(cause);
	}

}
