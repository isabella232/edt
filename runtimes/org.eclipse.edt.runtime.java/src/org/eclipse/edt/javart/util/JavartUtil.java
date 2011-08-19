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
package org.eclipse.edt.javart.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.ControlFlow;
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.FatalProblem;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.runtime.java.egl.lang.EglAny;

import egl.lang.AnyException;
import egl.lang.InvalidIndexException;
import egl.lang.NullValueException;
import eglx.java.JavaObjectException;


/**
 * JavartUtil contains some helpful utility functions. It's not meant to be
 * instantiated; all the methods are static.
 */
public class JavartUtil
{
	/**
	 * The system line separator (CRLF on Windows).
	 */
	public static final String LINE_SEPARATOR = System.getProperty( "line.separator" );
	
	/**
	 * A GregorianCalendar.
	 */
	private static GregorianCalendar gregorianCalendar;
	
	/**
	 * The constructor. It's private to prevent instantiation.
	 */
	private JavartUtil()
	{
		// Nothing to do
	}
	
	/**
	 * Returns a GregorianCalendar that can be used for time and date functions.
	 * 
	 * @return a GregorianCalendar.
	 */
	public static GregorianCalendar getCalendar()
	{
		if ( gregorianCalendar == null )
		{
			gregorianCalendar = new GregorianCalendar();
		}

		return gregorianCalendar;
	}

	/**
	 * Returns the current time in "HH:MM:SS" format. A 24-hour clock is used,
	 * so 1pm is "13:00:00".
	 * 
	 * @return The time
	 */
	public static String getCurrentTime()
	{
		// Get the current time.
		GregorianCalendar calendar = getCalendar();
		calendar.setTime( new Date() );
		int hour = calendar.get( Calendar.HOUR_OF_DAY );
		int minute = calendar.get( Calendar.MINUTE );
		int second = calendar.get( Calendar.SECOND );

		// Make sure each part has two digits.
		StringBuilder time = new StringBuilder( 8 );
		if ( hour < 10 )
		{
			time.append( '0' );
		}
		time.append( hour );
		time.append( ':' );
		if ( minute < 10 )
		{
			time.append( '0' );
		}
		time.append( minute );
		time.append( ':' );
		if ( second < 10 )
		{
			time.append( '0' );
		}
		time.append( second );

		// Concatenate the parts to get the result.
		return time.toString();
	}
	
	/**
	 * Returns a string containing the Object's type in EGL.
	 * 
	 * @param object  the Object.
	 */
	public static String getEglType( Object object )
	{
		if ( object instanceof EglAny )
		{
			return ((EglAny)object).ezeTypeSignature();
		}
		else 
		{
			return object.getClass().getName();
		}
	}

	/**
	 * Returns the name of a class minus its package.
	 *
	 * @param fullName  the class name to use.
	 * @return <code>fullName</code> minus its package.
	 */
	public static String removePackageName( String fullName )
	{
		int index = fullName.lastIndexOf( '.' );
		
		if ( index == -1 )
		{
			return fullName;
		}

		return fullName.substring( index + 1 );
	}

	/**
	 * Returns the name of a class's package.  If there is no package,
	 * defaultName is returned.
	 *
	 * @param fullName     the class name to use.
	 * @param defaultName  the default package name.
	 * @return <code>fullName</code>'s package, or defaultName.
	 */
	public static String packageName( String fullName, String defaultName )
	{
		int index = fullName.lastIndexOf( '.' );
		
		if ( index == -1 )
		{
			return defaultName;
		}

		return fullName.substring( 0, index );
	}
	
	/**
	 * Checks the start and end indices for a substring access, and throws an
	 * exception with ID = Message.INVALID_SUBSTRING_INDEX if they are invalid.
	 * 
	 * @param program     The program.
	 * @param startIndex  The start index.
	 * @param endIndex    The end index.
	 * @param maxlen      The maximum length of the item being substringed.
	 * @throws AnyException
	 */
	public static void checkSubstringIndices( Executable program, int startIndex,
			int endIndex, int maxlen )
		throws AnyException
	{
		if ( startIndex < 1 || startIndex > maxlen )
		{
			// startIndex is bad.
			String message = errorMessage(
					program,
					Message.INVALID_SUBSTRING_INDEX,
					new Object[]{
						Integer.valueOf( startIndex ),
						Integer.valueOf( endIndex )
					} );
			
			InvalidIndexException ex = 
				new InvalidIndexException();
			ex.setMessage( message );
			ex.index = startIndex;
			ex.setMessageID(Message.INVALID_SUBSTRING_INDEX);
			
			throw ex;
		}
		else if ( endIndex < startIndex || endIndex < 1 || endIndex > maxlen )
		{
			// endIndex is bad.
			String message = errorMessage(
					program,
					Message.INVALID_SUBSTRING_INDEX,
					new Object[]{
						Integer.valueOf( startIndex ),
						Integer.valueOf( endIndex )
					} );
			
			InvalidIndexException ex = 
				new InvalidIndexException();
			ex.setMessage( message );
			ex.index = endIndex;
			ex.setMessageID(Message.INVALID_SUBSTRING_INDEX);
			
			throw ex;
		}
	}
	
	/**
	 * Builds an error message.  If tracing is on, it will be written to the
	 * trace.  If logging is on, it will be written to the log.
	 * 
	 * @param p   the program.
	 * @param id  the message ID.
	 * @return the error message.
	 */
	public static String errorMessage( Executable p, String id )
	{
		String message = p._runUnit().getLocalizedText().getMessage( id );
		return errorMessage( id, message, p );
	}
	
	/**
	 * Builds an error message.  If tracing is on, it will be written to the
	 * trace.  If logging is on, it will be written to the log.
	 * 
	 * @param p        the program.
	 * @param id       the message ID.
	 * @param inserts  the message inserts.
	 * @return the error message.
	 */
	public static String errorMessage( Executable p, String id, Object[] inserts )
	{
		String message = p._runUnit().getLocalizedText().getMessage( id, inserts );
		return errorMessage( id, message, p );
	}
	
	private static String errorMessage( String id, String message, Executable p )
	{
		String locationMessage; 
		String locationMessageId;
		RunUnit ru = p._runUnit();
		// TODO handle stack tracing from mapping of EGL to Java directly(as in JSR 045)

		locationMessageId = Message.PROGRAM_ERROR_INFO;
		locationMessage = ru.getLocalizedText().getMessage( 
				Message.PROGRAM_ERROR_INFO,
				new String[] { p._alias() } );

		message = id + ' ' + message;
		locationMessage = locationMessageId + ' ' + locationMessage;
		
		// Write the message to the trace and log.
//		if ( ru.getTrace().traceIsOn() )
//		{
//			ru.getTrace().put( message );
//			ru.getTrace().put( locationMessage );
//		}
//		if ( ru.syslib._errorLogIsOn() )
//		{
//			ru.syslib.errorLog( null, message + LINE_SEPARATOR + locationMessage );
//		}
		
		return message + '\n' + locationMessage;
	}
	
	/**
	 * Builds an error message.  If tracing is on, it will be written to the
	 * trace.  If logging is on, it will be written to the log.
	 * 
	 * @param ru  the run unit.
	 * @param id  the message ID.
	 * @return the error message.
	 */
	public static String errorMessage( RunUnit ru, String id )
	{
		String message = ru.getLocalizedText().getMessage( id );
		return errorMessage( id, message, ru );
	}
	
	/**
	 * Builds an error message.  If tracing is on, it will be written to the
	 * trace.  If logging is on, it will be written to the log.
	 * 
	 * @param ru       the run unit.
	 * @param id       the message ID.
	 * @param inserts  the message inserts.
	 * @return the error message.
	 */
	public static String errorMessage( RunUnit ru, String id, Object[] inserts )
	{
		String message = ru.getLocalizedText().getMessage( id, inserts );
		return errorMessage( id, message, ru );
	}
	
	private static String errorMessage( String id, String message, RunUnit ru )
	{
//		Executable p = ru.peekExecutable();
//		if ( p != null )
//		{
//			return errorMessage( id, message, p );
//		}

		String programName = ru.getStartupInfo().getRuName();
		String locationMessage = ru.getLocalizedText().getMessage( 
					Message.PROGRAM_ERROR_INFO,
					new String[] { programName } );

		message = id + ' ' + message;
		locationMessage = Message.PROGRAM_ERROR_INFO + ' ' + locationMessage;
		
		// Write the message to the trace and log.
		if ( ru.getTrace().traceIsOn() )
		{
			ru.getTrace().put( message );
			ru.getTrace().put( locationMessage );
		}
//		if ( ru.syslib._errorLogIsOn() )
//		{
//			ru.syslib.errorLog( null, message + LINE_SEPARATOR + locationMessage );
//		}
		
		return message + '\n' + locationMessage;
	}

	/**
	 * Removes trailing blanks from the original string and returns the result.
	 * 
	 * @param s  The original string
	 * @return The original string with trailing blanks removed.
	 */
	public static String removeTrailingBlanks( String s )
	{
		int i;
		for ( i = s.length() - 1; i >= 0 && s.charAt( i ) == ' '; i-- );
		return s.substring( 0, i + 1 );
	}
	
	/**
	 * Removes trailing DBCS blanks from the original string and returns the
	 * result.
	 * 
	 * @param s  The original string
	 * @return The original string with trailing DBCS blanks removed.
	 */
	public static String removeTrailingDbcsBlanks( String s )
	{
		int i;
		for ( i = s.length() - 1; i >= 0
				&& s.charAt( i ) == Constants.DBCS_BLANK_CHAR; i-- );
		return s.substring( 0, i + 1 );
	}
	
	/**
	 * Removes trailing DBCS and SBCS blanks from the original string and
	 * returns the result.
	 * 
	 * @param s  The original string
	 * @return The original string with trailing DBCS and SBCS blanks removed.
	 */
	public static String removeTrailingDbcsAndSbcsBlanks( String s )
	{
		int i;
		for ( i = s.length() - 1; i >= 0
				&& (s.charAt( i ) == ' '
				|| s.charAt( i ) == Constants.DBCS_BLANK_CHAR); i-- );
		return s.substring( 0, i + 1 );
	}
	
	/**
	 * Removes trailing Unicode blanks from the original string and returns the
	 * result.
	 * 
	 * @param s  The original string
	 * @return The original string with trailing Unicode blanks removed.
	 */
	public static String removeTrailingUnicodeBlanks( String s )
	{
		int i;
		for ( i = s.length() - 1; i >= 0 && s.charAt( i ) == '\u0020'; i-- );
		return s.substring( 0, i + 1 );
	}
	
	/**
	 * This is called by generated code to ensure a try statement doesn't hide
	 * an exception that can't be handled in EGL.  If the Exception that was 
	 * caught can't be ignored, it is re-thrown.  Otherwise nothing happens.
	 * 
	 * @param caught  the Exception that was caught.
	 * @throws the Exception, if it can't be ignored.
	 */
	public static void checkHandleable( Exception caught ) throws RuntimeException
	{
		if ( caught instanceof FatalProblem || caught instanceof ControlFlow )
		{
			throw (RuntimeException)caught;
		}
	}
	
	/**
	 * Returns a new AnyException for the given Throwable.  If the Throwable is
	 * already an AnyException, it is returned.  If it's a NullPointerException,
	 * a NullValueException will be returned.  In all other cases we'll return a
	 * JavaObjectException. 
	 */
	public static AnyException makeEglException( Throwable ex )
	{
		// This needs to be modified when we do https://bugs.eclipse.org/bugs/show_bug.cgi?id=355170
		// because NullPointerExceptions that come from a JavaObject ET should be treated as
		// JavaObjectExceptions, not NullValueExceptions.  Right now we assume
		// every NullPointerException should become a NullValueException.
		if ( ex instanceof AnyException )
		{
			return (AnyException)ex;
		}
		
		String msg = ex.getMessage();
		String className = ex.getClass().getName();
		if ( msg == null || msg.trim().length() == 0 )
		{
			msg = className;
		}

		if ( ex instanceof NullPointerException )
		{
			NullValueException nvx = new NullValueException();
			nvx.setMessageID( Message.NULL_REFERENCE );
			nvx.setMessage( msg );
			
			return nvx;
		}
		else
		{
			JavaObjectException jox = new JavaObjectException();
			jox.setMessageID( Message.CAUGHT_JAVA_EXCEPTION );
			jox.setMessage( msg );
			jox.exceptionType = className;
			
			return jox;
		}
	}
	
	/**
	 * Returns true if the Exception is a JavaObjectException, or something that
	 * should be represented as a JavaObjectException. 
	 */
	public static boolean isJavaObjectException( Exception ex )
	{
		// This needs to be modified when we do https://bugs.eclipse.org/bugs/show_bug.cgi?id=355170
		// because NullPointerExceptions that come from a JavaObject ET should be treated as
		// JavaObjectExceptions, not NullValueExceptions.  Right now we assume
		// every NullPointerException should become a NullValueException.
		return ex instanceof JavaObjectException 
				|| (!(ex instanceof egl.lang.AnyException) && !(ex instanceof NullPointerException));
	}

	/**
	 * Our ExecutorService (thread pool).
	 */
	private static ExecutorService threadPool;
	
	/**
	 * A class that ensures the threads in our pool are daemons.
	 */
	private static class EGLThreadFactory implements ThreadFactory
	{
		public Thread newThread( Runnable r )
		{
			Thread t = new Thread( r );
			t.setDaemon( true );
			return t;
		}
	}
	
	/**
	 * @return the ExecutorService.
	 */
	public synchronized static ExecutorService getThreadPool()
	{
		if ( threadPool == null )
		{
			threadPool = Executors.newCachedThreadPool( new EGLThreadFactory() );
		}
		
		return threadPool;
	}
}
