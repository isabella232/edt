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

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.EglException;
import org.eclipse.edt.javart.EglThrowable;
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.FatalException;
import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.runtime.java.egl.lang.EglAny;
import org.eclipse.edt.runtime.java.egl.lang.IndexOutOfBoundsException;
import org.eclipse.edt.runtime.java.egl.lang.JavaObjectException;
import org.eclipse.edt.runtime.java.egl.lang.NullValueException;
import org.eclipse.edt.runtime.java.egl.lang.TypeCastException;


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
	 * Returns a string containing the Object's type in EGL.  Uses 
	 * getEglTypeFromSignature if the Object is a Storage.
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
	 * Truth
	 * 
	 * @return true
	 */
	public static final boolean alwaysTrue()
	{
		return true;
	}
	
	/**
	 * Falsehood
	 * 
	 * @return false
	 */
	public static final boolean alwaysFalse()
	{
		return false;
	}
	
	/**
	 * Does nothing.  We generate a call to this method at the beginning of a
	 * try statement so the compiler will let us catch any kind of JavartException.
	 */
	public static final void beginTry() throws JavartException
	{
	}
	
	/**
	 * Returns true if the argument is a JavaObjectException, or an Exception
	 * that we'd wrap in a JavaObjectException.
	 * 
	 * @param ex  the exception.
	 * @return true if the exception should be treated as a JavaObjectException.
	 */
	public static boolean isJavaObjectException( Exception ex )
	{
		return ex instanceof JavaObjectException
			|| (!(ex instanceof JavartException) && !(ex instanceof EglThrowable));
	}
	
	/**
	 * Returns a JavaObjectException for the given exception.  If the
	 * exception is a JavaObjectException, its record is reused.  If the
	 * exception isn't a JavaObjectException, a new record is created.
	 */
	public static JavaObjectException makeJavaObjectException( Executable p, 
			String name, Exception ex )
		throws JavartException
	{
		JavaObjectException jox;
		if ( ex instanceof JavaObjectException )
		{
			return (JavaObjectException)ex;
		}
		else
		{

			String msg = ex.getMessage();
			String className = ex.getClass().getName();
			if ( msg == null || msg.trim().length() == 0 )
			{
				msg = className;
			}
			
			jox = new JavaObjectException(Message.CAUGHT_JAVA_EXCEPTION, msg);
			jox.setExceptionType(className);
			
			return jox;
		}
	}
	
	/**
	 * Checks if the value to be assigned to a substring is the proper length.
	 * If it is too long, it is truncated. If too short, it is padded. The
	 * indices are assumed to be valid.
	 * 
	 * @param value        The value being assigned.
	 * @param startIndex   The starting index.
	 * @param endIndex     The ending index.
	 * @param padding      The char to pad with.
	 * @return the value to assign, possibly truncated or padded.
	 */
	public static String checkSubstringValue( String value, int startIndex,
			int endIndex, char padding )
	{
		int valLength = value.length();
		int subLength = endIndex - startIndex + 1;
		
		if ( valLength > subLength )
		{
			return value.substring( 0, subLength );
		}
		else if ( valLength < subLength )
		{
			StringBuilder buf = new StringBuilder( value );
			for ( int i = valLength; i < subLength; i++ )
			{
				buf.append( padding );
			}
			return buf.toString();
		}
		else
		{
			return value;
		}
	}
	
	/**
	 * Checks if the value to be assigned to a substring is the proper length.
	 * If it is too long, it is truncated. If too short, it is padded with
	 * the specified byte(s). The indices are assumed to be valid.
	 * 
	 * @param value          The value being assigned.
	 * @param padding        The byte(s) to pad with.
	 * @param lengthInBytes  The length of the substring in bytes.
	 * @return the value to assign, possibly truncated or padded.
	 */
	public static byte[] checkSubstringValue( byte[] value, byte[] padding,
			int lengthInBytes )
	{
		if ( value.length > lengthInBytes )
		{
			byte[] newValue = new byte[ lengthInBytes ];
			System.arraycopy( value, 0, newValue, 0, newValue.length );
			return newValue;
		}
		else if ( value.length < lengthInBytes )
		{
			byte[] newValue = new byte[ lengthInBytes ];
			byte pad;
			System.arraycopy( value, 0, newValue, 0, value.length );
			for ( int i = value.length; i < lengthInBytes; i++ )
			{
				pad = padding.length == 1
						? padding[ 0 ]
						: padding[ i % 2 ];
				newValue[ i ] = pad;
			}
			return newValue;
		}
		else
		{
			return value;
		}
	}
	
	/**
	 * Checks the start and end indices for a substring access, and throws an
	 * exception with ID = Message.INVALID_SUBSTRING_INDEX if they are invalid.
	 * 
	 * @param program     The program.
	 * @param startIndex  The start index.
	 * @param endIndex    The end index.
	 * @param maxlen      The maximum length of the item being substringed.
	 * @throws JavartException
	 */
	public static void checkSubstringIndices( Executable program, int startIndex,
			int endIndex, int maxlen )
		throws JavartException
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
			
			IndexOutOfBoundsException ex = 
				new IndexOutOfBoundsException();
			ex.message = message;
			ex.indexValue = startIndex;
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
			
			IndexOutOfBoundsException ex = 
				new IndexOutOfBoundsException();
			ex.message = message;
			ex.indexValue = endIndex;
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
	 * Throws an exception with ID = Message.DATA_FORMAT_ERROR.
	 * 
	 * @throws JavartException
	 */
	public static void throwDataFormatException( String name, Executable program )
		throws JavartException
	{
		String message = errorMessage(
				program,
				Message.DATA_FORMAT_ERROR,
				new Object[] { name } );
		throwRuntimeException( Message.DATA_FORMAT_ERROR, message, program );
	}
	
	/**
	 * Throws an exception with ID = Message.NULL_REFERENCE_VARIABLE.
	 * 
	 * @throws NullValueException
	 */
	public static void throwNullReferenceVariableException( Executable program, String nullRef )
		throws JavartException
	{
		String message = errorMessage( program, Message.NULL_REFERENCE_VARIABLE, new Object[] { nullRef } );
		NullValueException nvx = 
			new NullValueException();
		
		nvx.message = message;
		nvx.setMessageID(Message.NULL_REFERENCE_VARIABLE);
		throw nvx;
	}
	
	/**
	 * Use throwNullReferenceVariableException, not this method, if possible.
	 * Throws an exception with ID = Message.NULL_REFERENCE.
	 * 
	 * @throws NullValueException
	 */
	public static void throwNullValueException( Executable program )
		throws JavartException
	{
		String message = errorMessage( program, Message.NULL_REFERENCE );
		NullValueException nvx = 
			new NullValueException();
		
		nvx.message = message;
		nvx.setMessageID(Message.NULL_REFERENCE);
		throw nvx;
	}

	/**
	 * Throws a RuntimeException.
	 * 
	 * @throws org.eclipse.edt.runtime.java.egl.lang.RuntimeException
	 */
	public static void throwRuntimeException( String id, String message, Executable program )
		throws JavartException
	{
		org.eclipse.edt.runtime.java.egl.lang.RuntimeException rex = 
			new org.eclipse.edt.runtime.java.egl.lang.RuntimeException();
		
		rex.setMessage(message);
		rex.setMessageID(id);
		throw rex;
	}

	/**
	 * Throws an exception with ID = Message.CONVERSION_ERROR.
	 * 
	 * @throws TypeCastException
	 */
	public static void throwTypeCastException( String actualTypeName, 
			String castToName, Executable program )
		throws JavartException
	{
		String message = 
			errorMessage( program, Message.CONVERSION_ERROR, 
				new String[] { actualTypeName, castToName } );
		TypeCastException tcx = 
			new TypeCastException();
		
		tcx.message = message;
		tcx.setMessageID(Message.CONVERSION_ERROR);
		tcx.castToName = castToName;
		tcx.actualTypeName = actualTypeName;
		throw tcx;
	}

	/**
	 * Throws an exception with ID = Message.UNSUPPORTED_OPERANDS.
	 * 
	 * @throws JavartException
	 */
	public static void throwUnsupportedOperandsException( String operator, 
			String op1, String op2, Executable program )
		throws JavartException
	{
		String message = errorMessage(
				program,
				Message.UNSUPPORTED_OPERANDS,
				new Object[] { operator, op1, op2 } );
		throwRuntimeException( Message.UNSUPPORTED_OPERANDS, message, program );
	}

	/**
	 * Throws an exception with ID = Message.INDEX_OUT_OF_BOUNDS.
	 * 
	 * @throws JavartException
	 */
	public static void throwIndexOutOfBoundsException( int index, Executable program )
		throws JavartException
	{
		String message = errorMessage(
				program,
				Message.INDEX_OUT_OF_BOUNDS,
				new Object[] { String.valueOf( index ) } );
		
		IndexOutOfBoundsException ex = 
			new IndexOutOfBoundsException();
		ex.message = message;
		ex.indexValue = index;
		ex.setMessageID(Message.INDEX_OUT_OF_BOUNDS);
		
		throw ex;
	}

	/**
	 * Throws an exception with ID = Message.ARRAY_INDEX_OUT_OF_BOUNDS.
	 * 
	 * @throws JavartException
	 */
	public static void throwArrayIndexOutOfBoundsException( int index, String name, 
			int size, Executable program )
		throws JavartException
	{
		String message = errorMessage(
				program,
				Message.ARRAY_INDEX_OUT_OF_BOUNDS,
				new Object[] { String.valueOf( index ), name, String.valueOf( size ) } );
		
		IndexOutOfBoundsException ex = 
			new IndexOutOfBoundsException();
		ex.message = message;
		ex.indexValue = index;
		ex.setMessageID(Message.ARRAY_INDEX_OUT_OF_BOUNDS);
		
		throw ex;
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
	 * The specified length of a bin is the number of bytes, not the number of
	 * integer digits. This returns the number of integer digits based on the
	 * defined bin length and decimals.
	 * 
	 * @param length    The length.
	 * @param decimals  The decimals.
	 * @return
	 */
	public static int getTrueBinLength( int length, int decimals )
	{
		switch ( length )
		{
			case 4:
				return Integer.toString( Short.MAX_VALUE - decimals ).length();
				
			case 9:
				return Integer.toString( Integer.MAX_VALUE - decimals ).length();
				
			case 18:
			default:
				return Long.toString( Long.MAX_VALUE - decimals ).length();
		}
	}
	
	/**
	 * This is called by generated code when a try statement has no onException
	 * blocks.  If the Exception that was caught can't be ignored, it is
	 * re-thrown.  Otherwise nothing happens.
	 * 
	 * @param caught  the Exception that was caught.
	 * @throws the Exception, if it can't be ignored.
	 */
	public static void noExceptionHandlers( Exception caught ) throws Exception
	{
		if ( caught instanceof FatalException || caught instanceof EglThrowable )
		{
			throw caught;
		}
		else if ( caught instanceof EglException )
		{
		}
		else if ( caught instanceof JavartException )
		{
			throw caught;
		}
	}
	
	/**
	 * Compares the two objects to see if they have been modified by the user.
	 * 
	 * @param prevValue  The previous value displayed on the page.
	 * @param newValue   The new value from the page.
	 * @return true if the two values are not identical.
	 */
	public static boolean javaObjectChanged( Object prevValue, Object newValue )
	{
		if ( newValue == null || prevValue == null )
		{
			return !(newValue == null && prevValue == null);
		}
		else if ( newValue.getClass().isArray() && prevValue.getClass().isArray() )
		{
			// check for changed size
			int length = Array.getLength( newValue );
			
			if ( length != Array.getLength( prevValue ) )
			{
				return true;
			}
			
			// check for any individual elements to have changed.
			for ( int i = 0; i < length; i++ )
			{
				// support multidim arrays
				if ( javaObjectChanged(
						Array.get( prevValue, i ), Array.get( newValue, i ) ) )
				{
					return true;
				}
			}
			
			return false;
		}
		else
		{
			if ( prevValue instanceof String )
			{
				prevValue = ((String)prevValue).trim();
			}
			if ( newValue instanceof String )
			{
				newValue = ((String)newValue).trim();
			}
			
			if ( newValue.equals( prevValue ) )
			{
				return false;
			}
		}
		
		return true;
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
