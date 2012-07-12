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
package org.eclipse.edt.javart.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.ControlFlow;
import org.eclipse.edt.javart.FatalProblem;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.Runtime;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;

import eglx.java.JavaObjectException;
import eglx.lang.AnyException;
import eglx.lang.InvalidIndexException;
import eglx.lang.NullValueException;


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
		if ( object instanceof EAny )
		{
			return ((EAny)object).ezeTypeSignature();
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
	 * Checks the start and end indices for a substring access, and throws an
	 * exception with ID = Message.INVALID_SUBSTRING_INDEX if they are invalid.
	 * 
	 * @param startIndex  The start index.
	 * @param endIndex    The end index.
	 * @param maxlen      The maximum length of the item being substringed.
	 * @throws AnyException
	 */
	public static void checkSubstringIndices( int startIndex, int endIndex, int maxlen )
		throws AnyException
	{
		if ( startIndex < 1 || startIndex > maxlen )
		{
			// startIndex is bad.
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = startIndex;
			throw ex.fillInMessage( Message.INVALID_SUBSTRING_INDEX, startIndex, endIndex );
		}
		else if ( endIndex < startIndex || endIndex < 1 || endIndex > maxlen )
		{
			// endIndex is bad.
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = endIndex;
			throw ex.fillInMessage( Message.INVALID_SUBSTRING_INDEX, startIndex, endIndex );
		}
	}
	
	/**
	 * Builds an error message with no inserts.  If tracing is on, the message will
	 * be written to the trace.  If logging is on, it will be written to the log.
	 * 
	 * @param id  the message ID.
	 * @return the error message.
	 */
	public static String errorMessage( String id )
	{
		RunUnit ru = Runtime.getRunUnit();
		String message = ru.getLocalizedText().getMessage( id );
		
		// Write the message to the trace.
		if ( ru.getTrace().traceIsOn() )
		{
			ru.getTrace().put( message );
		}
		
		return message;
	}
	
	/**
	 * Builds an error message.  If tracing is on, it will be written to the
	 * trace.  If logging is on, it will be written to the log.
	 * 
	 * @param id       the message ID.
	 * @param inserts  the message inserts.
	 * @return the error message.
	 */
	public static String errorMessage( String id, Object... inserts )
	{
		RunUnit ru = Runtime.getRunUnit();
		String message = ru.getLocalizedText().getMessage( id, inserts );
		
		// Write the message to the trace.
		if ( ru.getTrace().traceIsOn() )
		{
			ru.getTrace().put( message );
		}
		
		return message;
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
	 * utility to check to see if an object is null and if it is, then throw 
	 * a NullValueException, otherwise return the object
	 */
	public static <T extends Object> T checkNullable(T o) throws NullValueException {
		if (o == null || (o instanceof AnyBoxedObject && ((AnyBoxedObject<?>) o).ezeUnbox() == null))
		{
			NullValueException nvx = new NullValueException();
			throw nvx.fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		return o;
	}
	/**
	 * utility to check to see if an index is out of bounds and if it is, then throw 
	 * a InvalidIndexException, otherwise return the index
	 */
	public static int checkIndex(int index, List<?> list) throws InvalidIndexException {
		if (list == null)
		{
			NullValueException nvx = new NullValueException();
			throw nvx.fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		if (index < 0 || index >= list.size())
		{
			InvalidIndexException iix = new InvalidIndexException();
			iix.index = index;
			throw iix.fillInMessage( Message.LIST_INDEX_OUT_OF_BOUNDS, index, list.size() );
		}
		return index;
	}
	/**
	 * Returns an AnyException for the given Throwable.  If the Throwable is
	 * already an AnyException, it is returned.  In all other cases we'll return
	 * a JavaObjectException. 
	 */
	public static AnyException makeEglException( Throwable ex )
	{
		if ( ex instanceof AnyException )
		{
			return (AnyException)ex;
		}
		
		String msg = ex.getMessage();
		String className = ex.getClass().getName();
		if ( msg == null || msg.length() == 0 )
		{
			msg = className;
		}

		JavaObjectException jox = new JavaObjectException();
		jox.exceptionType = className;
		jox.initCause( ex );
		return jox.fillInMessage( Message.CAUGHT_JAVA_EXCEPTION, msg );
	}
	
	/**
	 * Returns true if the Exception is a JavaObjectException, or something that
	 * should be represented as a JavaObjectException. 
	 */
	public static boolean isJavaObjectException( Exception ex )
	{
		return ex instanceof JavaObjectException 
				|| !(ex instanceof eglx.lang.AnyException);
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
