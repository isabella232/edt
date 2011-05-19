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
package org.eclipse.edt.javart;

import egl.lang.AnyException;

/**
 * EglException is the superclass of all exceptions that can be declared, thrown,
 * or handled in EGL statements. 
 */
public class EglException extends JavartException
{
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = 70L;
	
	/**
	 * The exception's record
	 */
	private AnyException exceptionRecord;
	
	/**
	 * Makes the EglException.
	 * 
	 * @param id
	 *            The message ID.
	 * @param message
	 *            The error message.
	 */
	public EglException( String id, String message, AnyException record )
	{
		super( id, message );
		this.exceptionRecord = record;
	}
	
	/**
	 * Returns the exceptionRecord
	 * @return the exceptionRecord.
	 */
	public AnyException getRecord()
	{
		return exceptionRecord;
	}
}
