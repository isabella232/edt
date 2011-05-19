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

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.util.JavartUtil;

/**
 * Exception for fatal program errors.  These can't be handled with an EGL try
 * statement.
 */
public class FatalException extends JavartException
{
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = 70L;

	/**
	 * Makes the FatalException.  Prepare to die!
	 * 
	 * @param id
	 *            The message ID.
	 * @param message
	 *            The error message.
	 */
	public FatalException( String id, String message )
	{
		super( id, message );
	}

	/**
	 * Makes a FatalException for an Exception that was caught but can't be
	 * handled.
	 * 
	 * @param program
	 *            The program that caught an exception.
	 * @param ex
	 *            The exception that was caught.
	 */
	public FatalException( Executable program, Exception ex )
	{
		super( idFor( ex ), messageFor( program, ex ) );
	}
	
	/**
	 * Returns the message ID for the given exception.  If it's a 
	 * JavartException, its message ID is returned.  Otherwise
	 * Message.UNHANDLED_EXCEPTION is returned.
	 */
	private static String idFor( Exception ex )
	{
		if ( ex instanceof JavartException )
		{
			return ((JavartException)ex).getMessageID();
		}
		else
		{
			return Message.UNHANDLED_EXCEPTION;
		}
	}
	
	/**
	 * Returns the message text for the given exception.  If it's a 
	 * JavartException, its message text is returned.  Otherwise the text for
	 * Message.UNHANDLED_EXCEPTION is returned.
	 */
	private static String messageFor( Executable program, Exception ex )
	{
		if ( ex instanceof JavartException )
		{
			return ((JavartException)ex).getMessage();
		}
		else
		{
			return JavartUtil.errorMessage( program, Message.UNHANDLED_EXCEPTION, 
					new String[] { ex.toString() } );
		}
	}
}
