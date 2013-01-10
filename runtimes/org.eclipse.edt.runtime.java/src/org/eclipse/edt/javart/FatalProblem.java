/*******************************************************************************
 * Copyright © 2006, 2013 IBM Corporation and others.
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

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.util.JavartUtil;

import eglx.lang.AnyException;

/**
 * Exception for fatal program errors.  These can't be handled with an EGL try
 * statement.
 */
public class FatalProblem extends AnyException
{
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/**
	 * Makes the FatalProblem.  Prepare to die!
	 */
	public FatalProblem()
	{
	}

	/**
	 * Makes a FatalProblem for an Exception that was caught but can't be
	 * handled.  Calls <code>fillInMessage</code> and does 
	 * <code>initCause( ex );</code>. 
	 * 
	 * @param program
	 *            The program that caught an exception.
	 * @param ex
	 *            The exception that was caught.
	 */
	public FatalProblem( Exception ex )
	{
		fillInMessage( idFor( ex ), messageFor( ex ) );
		initCause( ex );
	}
	
	/**
	 * Returns the message ID for the given exception.  If it's a 
	 * AnyException, its message ID is returned.  Otherwise
	 * Message.UNHANDLED_EXCEPTION is returned.
	 */
	private static String idFor( Exception ex )
	{
		if ( ex instanceof AnyException )
		{
			return ((AnyException)ex).getMessageID();
		}
		else
		{
			return Message.UNHANDLED_EXCEPTION;
		}
	}
	
	/**
	 * Returns the message text for the given exception.  If it's a 
	 * AnyException, its message text is returned.  Otherwise the text for
	 * Message.UNHANDLED_EXCEPTION is returned.
	 */
	private static String messageFor( Exception ex )
	{
		if ( ex instanceof AnyException )
		{
			return ((AnyException)ex).getMessage();
		}
		else
		{
			return JavartUtil.errorMessage( Message.UNHANDLED_EXCEPTION, ex );
		}
	}
}
