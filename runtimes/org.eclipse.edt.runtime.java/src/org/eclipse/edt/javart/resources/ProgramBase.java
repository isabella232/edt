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
package org.eclipse.edt.javart.resources;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Runtime;


import org.eclipse.edt.javart.*;

import eglx.lang.AnyException;
import eglx.lang.AnyValue;

/**
 * This class represents anything in EGL that contains functions: programs,
 * libraries, page handlers, etc.
 * <P>
 * In order to allow Executables to be used as Java Beans, all method names 
 * must begin _ or $.
 * 
 * @author mheitz
 */
public abstract class ProgramBase extends ExecutableBase implements Program, Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
					
	public ProgramBase()
		throws AnyException
	{
	}

	/**
	 * The program's input record, or null if there isn't one.
	 * 
	 * @return the program's input record.
	 */
	public AnyValue _inputRecord()
	{
		return null;
	}
	
	/**
	 * Implements the transfer to program statement.  It will throw a Transfer
	 * object.
	 * 
	 * @param name   which program to transfer to.
	 * @param input  optional data to initialize the new program's input record. 
	 */
	public void _transferToProgram( String name, AnyValue input ) 
		throws Transfer
	{
		// Make sure the class name includes the package.
		name = name.trim();
		if ( name.indexOf('.') == -1 )
		{
			String fullName = getClass().getName();
			int dotIndex = fullName.lastIndexOf( '.' );
			if ( dotIndex != -1 )
			{
				name = fullName.substring( 0, dotIndex ) + '.' + name;
			}
		}
	
		// Trace the transfer.
		RunUnit runUnit = Runtime.getRunUnit();
		if ( runUnit.getTrace().traceIsOn() )
		{
			String str = "TRANSFER TO PROGRAM " + name + ", with record ";
			if ( input == null )
			{
				str += "(null)";
			}
			else
			{
				str += input.ezeTypeSignature();
			}
			runUnit.getTrace().put( str );
		}

		runUnit.transferCleanup( false );
		throw new Transfer( name, input, false, null );
	}
	
	/**
	 * Implements the transfer to transaction statement.  It will throw a Transfer
	 * object.
	 * 
	 * @param name      which program to transfer to.
	 * @param input     optional data to initialize the new program's input record.
	 * @param doCommit  true if we should commit before transferring.
	 */
	public void _transferToTransaction( String name, AnyValue input, 
			boolean doCommit ) 
		throws Transfer
	{
		// Make sure the class name includes the package.
		name = name.trim();
		if ( name != null && name.length() > 0 && name.indexOf('.') == -1 )
		{
			String fullName = getClass().getName();
			int dotIndex = fullName.lastIndexOf( '.' );
			if ( dotIndex != -1 )
			{
				name = fullName.substring( 0, dotIndex ) + '.' + name;
			}
		}
	
		// Trace the transfer.	
		RunUnit runUnit = Runtime.getRunUnit();
		if ( runUnit.getTrace().traceIsOn() )
		{
			String str = "TRANSFER TO TRANSACTION " + name + ", with record ";
			if ( input == null )
			{
				str += "(null)";
			}
			else
			{
				str += input.ezeTypeSignature();
			}
			runUnit.getTrace().put( str );
		}

		if ( doCommit )
		{
			runUnit.commit();
		}
		runUnit.transferCleanup( true );
		throw new Transfer( name, input, true, null );
	}

	/**
	 * Implements the show statement.  It will throw a Transfer object.
	 * 
	 * @param name      which program to transfer to.
	 * @param input     optional data to initialize the new program's input record.
	 * @param ui        UIRecord or TextForm to be displayed.
	 * @param doCommit  true if we should commit before transferring.
	 */
	public void _show( String name, AnyValue input, AnyValue ui, boolean doCommit )
		throws Exception
	{
		// Make sure the class name includes the package.
		name = name.trim();
		if ( name != null && name.length() > 0 && name.indexOf('.') == -1 )
		{
			String fullName = getClass().getName();
			int dotIndex = fullName.lastIndexOf( '.' );
			if ( dotIndex != -1 )
			{
				name = fullName.substring( 0, dotIndex ) + '.' + name;
			}
		}
	
		// Trace the transfer.
		RunUnit runUnit = Runtime.getRunUnit();
		if ( runUnit.getTrace().traceIsOn() )
		{
			String str = "SHOW ";
			if ( ui == null )
			{
				str += "(null)";
			}
			else
			{
				str += ui.ezeTypeSignature();
			}
			str += " to program " + name + ", with record ";
			if ( input == null )
			{
				str += "(null)";
			}
			else
			{
				str += input.ezeTypeSignature();
			}
			runUnit.getTrace().put( str );
		}

		if ( doCommit )
		{
			runUnit.commit();
		}
		runUnit.transferCleanup( true );
		throw new Transfer( name, input, ui, true );
	}
	

	/**
	 * Cleanup any resources.  Sub classes will override this method
	 * to handle any 
	 */
	public void _clenaup() {
		
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
