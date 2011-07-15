/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.runtime.java.egl.lang;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.EglException;
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.resources.Trace;
import org.eclipse.edt.javart.util.JavartUtil;



public class SysLib extends ExecutableBase {

	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/**
	 * The stream for startLog and errorlog system functions
	 */
	private transient PrintWriter outputStream = null;
	
	/**
	 * Current Exception
	 */
	public EglException currentException = null;
	
	/**
	 * Constructor
	 * 
	 * @param ru
	 *            The rununit
	 * @throws JavartException
	 */
	public SysLib( RunUnit ru ) throws JavartException
	{
		super( ru );
	}


	/**
	 * Returns the value of the named property, or a null/empty string if
	 * there's no such property.
	 * 
	 * @param program
	 *            The program
	 * @param propertyName
	 *            the name of the property.
	 * @return the value of the property, or a null/empty StringValue.
	 * @throws JavartException
	 */
	public String getProperty( String propertyName )
	{
		String value = _runUnit().getProperties().get( propertyName.trim() );
		if ( value == null )
		{
			value = java.lang.System.getProperty( propertyName.trim() );
		}
		
		return value;
	}

	/**
	 * Suspend current thread for a specified amount of time. The time to wait
	 * is specified in seconds in EGL, with fractions honored down to two
	 * decimal places.
	 * 
	 * @param program
	 *            The program
	 * @param time
	 *            waiting period (units of 1/100 seconds).
	 */
	public void wait( BigDecimal time )
	{
		// Truncate any extra digits by shifting the decimal point
		// over two places and converting the value to a long.
		time = time.movePointRight( 2 );
		time = new BigDecimal( BigInteger.valueOf( time.longValue() ) );

		try
		{
			Thread.sleep( time.longValue() * 10 );
		}
		catch ( IllegalArgumentException e )
		{
			// no-op
		}
		catch ( InterruptedException e )
		{
			// no-op
		}
	}

	/**
	 * The startLog function opens an error log file.
	 * 
	 * @param program
	 *            The program
	 * @param filename
	 *            The name of the log file
	 */
	public void startLog( String filename ) throws JavartException
	{
		try
		{
			this.outputStream = new PrintWriter( new BufferedWriter( new FileWriter(
					filename, true ) ), true );
		}
		catch ( IOException e )
		{
			JavartUtil.throwRuntimeException( 
					Message.SYSTEM_FUNCTION_ERROR, 
					JavartUtil.errorMessage( 
							(Executable)null, 
							Message.SYSTEM_FUNCTION_ERROR, 
							new Object[] { "SysLib.startLog", e } ), 
					null );
		}
	}

	/**
	 * The errorLog() function adds a message to the current log file.
	 * 
	 * @param program
	 *            The program
	 * @param errorMsg
	 *            The message to add
	 */
	public void errorLog( String errorMsg )
	{
		if ( this.outputStream == null )
		{
			return;
		}

		// DateFormatter is a com.ibm.icu class...fix this
		this.outputStream.println( 
				_runUnit().getLocalizedText().getDateFormatter().format( new Date() ) );
		this.outputStream.println( errorMsg );
	}
	
	/**
	 * Returns true if startLog has been called successfully.
	 */
	public boolean _errorLogIsOn()
	{
		return outputStream != null;
	}

	/**
	 * Run an external command in the foreground, in LINE mode. This does not
	 * return until the command has completed.
	 * 
	 * @param commandString
	 *            The command to run
	 * @throws JavartException
	 */
	public void callCmd( String commandString ) throws JavartException
	{
		callCmd( commandString, "line" );
	}

	/**
	 * Run an external program in the foreground. This does not return until the
	 * command has completed.
	 * 
	 * @param commandString
	 *            The command string
	 * @param modeString
	 *            The mode value ("form" or "line")
	 * @throws JavartException
	 */
	public void callCmd( String commandString, String modeString )
			throws JavartException
	{
		System.callCmd( commandString, modeString.equalsIgnoreCase( "line" ) );
	}

	/**
	 * Run an external command in the background, in LINE mode. This returns
	 * immediately, not waiting for the command to complete.
	 * 
	 * @param commandString
	 *            The command to run
	 * @throws JavartException
	 */
	public void startCmd( String commandString ) throws JavartException
	{
		startCmd( commandString, "line" );
	}

	/**
	 * Start an external command in the background. This returns immediately,
	 * not waiting for the command to complete.
	 * 
	 * @param commandString
	 *            The command
	 * @param modeString
	 *            The mode value ("form" or "line")
	 * @throws JavartException
	 */
	public void startCmd( String commandString, String modeString )
			throws JavartException
	{
		System.startCmd( commandString, modeString.equalsIgnoreCase( "line" ) );
	}

	/**
	 * Returns a formatted message from the RunUnit's message bundle, or null if
	 * no message with the key is found.
	 * 
	 * @param program	the program
	 * @param key		the message key
	 * @return			the formatted message
	 */
	public String getMessage( Executable program, String key )
	{
		return getMessage( program, key, null );
	}

	/**
	 * Returns a formatted message from the RunUnit's message bundle, or null if
	 * no message with the key is found.
	 * 
	 * @param key      the message key.
	 * @param inserts  an array of JavartStrings, holding the inserts.
	 * @return the formatted message.
	 */
	public String getMessage( Executable program, String key, egl.lang.EList<String> inserts )
	{
		// Get the inserts as Strings.
		String[] insertStrings = null;
		if ( inserts != null )
		{
			insertStrings = new String[ inserts.size() ];
			for ( int i = 0; i < insertStrings.length; i++ )
			{
				insertStrings[ i ] = (String)inserts.get( i );
			}
		}
	
		// Look up the message.
		key = key.trim();
		String message = 
			program._runUnit().getLocalizedText().getMessage( key, insertStrings );
		return message;
	}

	/**
	 * Calls the Power Server and resource manager to rollback changes.
	 */
	public void rollback( Executable program )
		throws JavartException
	{
		RuntimeException errorException = null;
		Trace trace = program._runUnit().getTrace();
		boolean tracing = trace.traceIsOn( Trace.GENERAL_TRACE );
		try
		{			      
			if ( tracing )
			{
				trace.put( "rollBack()" );
				trace.put( "    resetting Recoverable Resources ..." );
			}
			
			/* Roll back recoverable resources */
			program._runUnit().rollback();
		}
		catch ( JavartException jx )
		{
			String message = JavartUtil.errorMessage(
					program,
					Message.SYSTEM_FUNCTION_ERROR,
					new Object[] { "SysLib.rollBack", jx.getMessage() } );
			errorException = 
					new RuntimeException(Message.SYSTEM_FUNCTION_ERROR, message);
	
		}
		finally
		{
			if ( errorException == null )
			{
				// Rollback went OK.
				if ( tracing )
				{
					trace.put( "<-- rollBack()   rc = 0" );
				}
			}
			else
			{
				// Rollback failed.
				if ( tracing )
				{
					trace.put( "<-- rollBack()   rc <> 0" );
				}
				throw errorException;
			}
		}
	}

	/**
	 * Change the locale of the running program dynamically.
	 * 
	 * @param languageCode
	 * @param countryCode
	 * @param variant			null if not specified
	 */
	public void setLocale( Executable program, String languageCode, String countryCode, String variant )
	{
//		if ( program instanceof JSFHandler )
//		{
//			((JSFHandler)program)._localize( languageCode, countryCode, variant );
//		}
//		else if ( program instanceof VGUIRecordBean )
//		{
//			((VGUIRecordBean)program)._setLocale( languageCode, countryCode, variant );
//		}
//		else
//		{
		// TODO: Make all Executables implement switchLocale(Locale)
		Locale locale;
		if ( variant == null )
		{
			locale = new Locale( languageCode, countryCode );
		}
		else
		{
			locale = new Locale( languageCode, countryCode, variant );
		}
		program._runUnit().switchLocale( locale );
	}

}
