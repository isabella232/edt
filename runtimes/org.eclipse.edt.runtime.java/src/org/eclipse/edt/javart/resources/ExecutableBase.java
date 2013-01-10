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
package org.eclipse.edt.javart.resources;

import java.io.Serializable;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Runtime;
import org.eclipse.edt.javart.Executable;

import eglx.lang.AnyException;

/**
 * This class represents anything in EGL that contains functions: programs,
 * libraries, handlers, etc.
 * <P>
 * In order to allow Executables to be used as Java Beans, all method names 
 * must begin _ or $.
 * 
 * @author mheitz
 */
public abstract class ExecutableBase implements Executable, Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	/**
	 * Makes an Executable. 
	 */
	public ExecutableBase()
		throws AnyException
	{
	}
	
	/**
	 * Default implementation just returns the simple name of the class
	 */
	public String _name() 
	{
		return getClass().getSimpleName();
	}
	
	/**
	 * Commits changes made by programs in the RunUnit.
	 */
	public void _commit() throws AnyException
	{
		Runtime.getRunUnit().commit();
	}

	/**
	 * Discards changes made by programs in the RunUnit.
	 */
	public void _rollback() throws AnyException
	{
		Runtime.getRunUnit().rollback();
	}

	@Override
	/**
	 * Cleanup any resources.  Sub classes will override this method
	 * to handle any 
	 */
	public void _cleanup() {
		
	}
}
