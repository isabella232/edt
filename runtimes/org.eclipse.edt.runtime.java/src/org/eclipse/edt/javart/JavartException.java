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



/**
 * Exception for program errors.
 */
public class JavartException extends RuntimeException
{
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = 70L;
	
	/**
	 * The message ID.
	 */
	private String id;

	public JavartException() { super(); }
	
	public JavartException(String id) {
		this.id = id;
	}
	
	public JavartException(Throwable ex) {
		super(ex);
	}
	/**
	 * Makes the JavartException.
	 * 
	 * @param id
	 *            The message ID.
	 * @param message
	 *            The error message.
	 */
	public JavartException( String id, String message )
	{
		super( message );
		this.id = id;
	}
	
	/**
	 * @return The ID of the error message.
	 */
	public String getMessageID()
	{
		return id;
	}
	
	public void setMessageID(String id) {
		this.id = id;
	}
	
	/**
	 * To improve performance, we override the usual implementation of this method,
	 * which is VERY expensive.  But unfortunately this means a stack trace for this
	 * exception will not be available.  That's OK since EGL users don't see them
	 * anyway.
	 * <P>
	 * For debugging, you can get stack traces in your JavartExceptions (and loose
	 * the performance improvement) by setting the system property 
	 * org.eclipse.edt.javart.StackTraces to true.  
	 *
	 * @return this object.
	 */
	public Throwable fillInStackTrace()
	{
		if ( NO_STACK_TRACES )
		{
			return this;
		}
		return super.fillInStackTrace();
	}
	
	/**
	 * Determines if fillInStackTrace is optimized or not.
	 */
	static final boolean NO_STACK_TRACES = !Boolean.getBoolean( "org.eclipse.edt.javart.StackTraces" );
	
}
