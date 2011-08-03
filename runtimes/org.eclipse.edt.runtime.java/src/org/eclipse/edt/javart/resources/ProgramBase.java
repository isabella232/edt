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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.Program;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.Transfer;

import egl.lang.EglAny;
import egl.lang.AnyValue;

/**
 * This class represents anything in EGL that contains functions: programs,
 * libraries, page handlers, etc.
 * <P>
 * In order to allow Programs to be used as JSFHandler beans, all method names 
 * must begin _ or $.
 * 
 * @author mheitz
 */
public abstract class ProgramBase extends ExecutableBase implements Program, Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

				
	/**
	 * The line of the EGL statement whose generated code is currently executing.
	 * Not used and will remain -1 unless includeLineNumbers is YES. 
	 */
	public int ezeCurrentLine;
					
	public ProgramBase( RunUnit runUnit )
		throws JavartException
	{
		super(runUnit);
	}

	/**
	 * Gives initial values to the system variables which are not saved across
	 * segments.
	 */
	protected void _initUnsavedSysVars()
		throws JavartException
	{
	}

	/**
	 * This is used to reinitialize fields of a library at the start of a new segment.
	 * The generator will override it if necessary.
	 */
	protected void _initUnsavedFields() throws Exception
	{
		_initUnsavedSysVars();
	}
	
	/**
	 * Starts running the program. This version of the method does nothing
	 * (suitable for libraries).
	 */
	public void _start(String...args) throws Exception
	{
		List<String> list = new ArrayList<String>();
		for (String arg : args){ list.add(arg); }
		main(list);
	}

	/**
	 * Completes the process of transferring to this Program from some other Program.
	 * This method should be called before <code>start</code>.
	 *
	 * <P> This method does nothing.  Programs that need to do some work before
	 * being started will override it.
	 *
	 * @exception Exception if an error occurs.
	 */
	public void _finishTransfer()
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
	 * Return null for the program's parameters. Called programs can override
	 * this.
	 * 
	 * @return list of the program's parameters
	 */
	public EglAny[] _parameters() throws JavartException
	{
		return null;
	}
	
	/**
	 * Returns true if _passParamLocal is overridden, to enable faster passing
	 * of parameters to called program on local calls.
	 * 
	 * @return false by default.
	 * @see #_passParamLocal
	 */
	public boolean _canPassParamsLocal()
	{
		return false;
	}
	
	/**
	 * Override this method and _canPassParamsLocal to support faster passing 
	 * of parameters to called programs on local calls.  When a LocalJavaCaller
	 * determines that the argument and parameters are the same type, it will
	 * use this method to replace the parameter with the argument.  That avoids
	 * passing data back and forth via a ByteStorage, which is slow. 
	 * 
	 * @param index  which param to update (0-based).
	 * @param o      the Object to use for the param.
	 */
	public void _passParamLocal( int index, Object o )
	{
	}

	/**
	 * _retainOnLocalExit returns true when this program has been called locally,
	 * and should be cached after it returns.  This version of the method always
	 * returns false.  It is overridden in called programs as follows: When the
	 * action is 0, the program has been called locally, and a field generated 
	 * into the program is initialized to indicate the value of the unloadOnExit 
	 * annotation (1 means yes, 2 means no).  The SysVar, ConverseVar, and VGVar
	 * variables are also reset.  When the action is 1, the program has done an 
	 * exit program with unloadOnExit = yes, so the field will be set to 1 if it's 
	 * currently 2.  When the action is 2, the program has done an exit program 
	 * with unloadOnExit = no, so the field will be set to 2 if it's currently 1.
	 * When the action is any other value, the field is unchanged.  The returned 
	 * value is true when the field is equal to 2.
	 * 
	 * @return true if this Program has been called locally and should be cached.
	 * @throws JavartException if SysVar, ConverseVar, and VGVar initialization 
	 *   fails (only possible when the action is zero).
	 */
	public boolean _retainOnExit( int action ) throws JavartException
	{
		return false;
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
