/*******************************************************************************
 * Copyright © 2006, 2012 IBM Corporation and others.
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
 * Exception to be thrown about when an EGL program terminates.
 */
public class ExitProgram extends EglExit
{
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/** Only one instance of this exception may be created - this is it */
	private static ExitProgram theinstance = null;
	
	/**
	 * Private constructor - not just anybody can create one of these.
	 */
	private ExitProgram()
	{
		//
	}
	
	/**
	 * Return the single instance of this exception. Create it if necessary.
	 * 
	 * @return The exception object
	 */
	public static ExitProgram getSingleton()
	{
		if (theinstance == null)
		{
			theinstance = new ExitProgram();
		}
		
		return theinstance;
	}
}
