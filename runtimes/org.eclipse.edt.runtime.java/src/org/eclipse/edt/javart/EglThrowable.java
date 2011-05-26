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
 * This is the superclass of things we throw that don't indicate an error.
 */
public class EglThrowable extends Exception
{
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = 70L;
	
	/**
	 * To improve performance, we override the usual implementation of this method,
	 * which is VERY expensive.  But unfortunately this means a stack trace for this
	 * exception will not be available.  That's OK since EGL users don't see them
	 * anyway.
	 * <P>
	 * For debugging, you can get stack traces in your EglThrowables (and loose
	 * the performance improvement) by setting the system property 
	 * org.eclipse.edt.javart.StackTraces to true.  
	 *
	 * @return this object.
	 */
	public Throwable fillInStackTrace()
	{
		if ( JavartException.NO_STACK_TRACES )
		{
			return this;
		}
		return super.fillInStackTrace();
	}
}
