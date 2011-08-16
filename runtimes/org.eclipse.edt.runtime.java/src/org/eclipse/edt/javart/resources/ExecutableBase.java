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
package org.eclipse.edt.javart.resources;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Executable;
import egl.lang.AnyException;
import org.eclipse.edt.javart.RunUnit;

/**
 * This class represents anything in EGL that contains functions: programs,
 * libraries, page handlers, etc.
 * <P>
 * In order to allow Executables to be used as JSFHandler beans, all method names 
 * must begin _ or $.
 * 
 * @author mheitz
 */
public abstract class ExecutableBase implements Executable, Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/**
	 * The RunUnit.
	 */
	public RunUnit runUnit;

	
	/**
	 * Makes a Executable. 
	 * <P>
	 * The flags parameter indicates special behaviors:
	 * <UL>
	 * <LI>If the first bit in flags is 1, SysVar will not be constructed.
	 * <LI>If the second bit in flags is 1, VGVar will not be constructed.
	 * <LI>If the third bit in flags is 1, ConverseVar will not be constructed.
	 * </UL>
	 * <P>
	 * The other 29 bits in flags are currently unused.  We hope that they
	 * can be used in the future, in place of new constructors with additional
	 * boolean arguments.
	 * 
	 * @param name
	 *            its name.
	 * @param alias
	 *            its alias (might be the same as the name).
	 * @param runUnit
	 *            its RunUnit.
	 * @param segmented
	 *            controls segmentation behavior.
	 * @param truncateDecimals
	 *            true if extra decimal digits are dropped during an assignment.
	 * @param flags
	 *            bit flags used to indicate various behaviors.
	 */
	public ExecutableBase( RunUnit runUnit )
		throws AnyException
	{
		this.runUnit = runUnit;
	}
	
	/**
	 * Constructs and initializes the system variables. System libraries don't
	 * require system variables. They'll override this method to initialize any
	 * variables they have, or do nothing at all.
	 *
	 * @throws AnyException
	 */
	protected void _constructSystemVariables( ) throws AnyException
	{				
	}


	/**
	 * @return the RunUnit.
	 */
	public RunUnit _runUnit()
	{
		return runUnit;
	}

	/**
	 * Default implementation just returns the simple name of the class
	 */
	public String _alias() {
		return getClass().getSimpleName();
	}
	
	/**
	 * Commits changes made by programs in the RunUnit.
	 */
	public void _commit() throws AnyException
	{
		runUnit.commit();
	}

	/**
	 * Discards changes made by programs in the RunUnit.
	 */
	public void _rollback() throws AnyException
	{
		runUnit.rollback();
	}

	@Override
	/**
	 * Cleanup any resources.  Sub classes will override this method
	 * to handle any 
	 */
	public void _cleanup() {
		
	}
	
	
	/**
	 * Serializes an instance of this class.
	 * 
	 * @param out  The output stream.
	 * @throws IOException
	 */
	private void writeObject( ObjectOutputStream out )
			throws IOException
	{
		out.defaultWriteObject();
	}	

}
