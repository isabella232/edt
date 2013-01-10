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

import eglx.lang.AnyException;

/**
 * This is the superclass of things we throw that don't indicate an error.
 */
public class ControlFlow extends RuntimeException
{
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	/**
	 * The usual implementation of this method is VERY expensive.  To improve
	 * performance, at the cost of losing stack traces from our exceptions, set 
	 * the system property org.eclipse.edt.javart.StackTraces to false.  
	 *
	 * @return this object.
	 */
	public Throwable fillInStackTrace()
	{
		if ( AnyException.STACK_TRACES )
		{
			return super.fillInStackTrace();
		}
		return this;
	}
}
