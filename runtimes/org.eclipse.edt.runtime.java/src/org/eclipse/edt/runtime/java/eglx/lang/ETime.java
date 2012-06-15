/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.runtime.java.eglx.lang;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.javart.util.TimestampIntervalMask;

import eglx.lang.*;

/**
 * A class for Times. The value is a Calendar.
 * @author mheitz
 */
public class ETime extends AnyBoxedObject<Calendar> {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public ETime() {
		this(DateTimeUtil.getNewCalendar());
	}

	public ETime(Calendar value) {
		super(value);
	}

	public static ETime ezeBox(Calendar value) {
		Calendar clone = null;
		if (value != null) {
			clone = (Calendar) value.clone();
		}
		return new ETime(clone);
	}

	public static Object ezeCast(Object value, Object[] constraints) throws AnyException {
		Integer[] args = new Integer[constraints.length];
		java.lang.System.arraycopy(constraints, 0, args, 0, args.length);
		return ezeCast(value, args);
	}

	public static Calendar ezeCast(Object value, Integer... args) throws AnyException {
		return (Calendar) EAny.ezeCast(value, "asTime", ETime.class, null, null);
	}

	public static boolean ezeIsa(Object value, Integer... args) {
		return value instanceof ETime || value instanceof Calendar;
	}

	public String toString() {
		return EString.asString(EString.asStringTime(object));
	}

	public static Calendar defaultValue() {
		long now = java.lang.System.currentTimeMillis();
		Calendar cal = DateTimeUtil.getBaseCalendar();
		cal.setTimeInMillis(now);
		return cal;
	}

	public static Calendar asTime(EString time) throws TypeCastException {
		if (time == null)
			return null;
		return asTime(time.ezeUnbox());
	}

	/**
	 * {@Operation narrow} Converts a string to a time. The string is parsed
	 * by searching for the hour, then the minute, then the second.  Two digits
	 * must be specified for each segment.  One separator character is required between
	 * the hour and minute, and another between the minute and second.  The separator 
	 * character must be a ":".
	 *
	 * @throws TypeCastException if the string can't be parsed into a time.
	 */
	public static Calendar asTime( String time ) throws TypeCastException
	{
		if ( time == null )
			return null;

		// Check the length and separators.
		if ( time.length() != 8 || time.charAt( 2 ) != ':' || time.charAt( 5 ) != ':' )
		{
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "time";
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, time, tcx.actualTypeName, tcx.castToName );
		}
		
		// Get the digits and put the values into a Calendar.
		try
		{
			int hours = -1;
			char digit1 = time.charAt( 0 );
			char digit2 = time.charAt( 1 );
			if ( '0' <= digit1 && digit1 <= '9' && '0' <= digit2 && digit2 <= '9' )
			{
				hours = (digit1 - '0') * 10 + (digit2 - '0');
			}
			int minutes = -1;
			digit1 = time.charAt( 3 );
			digit2 = time.charAt( 4 );
			if ( '0' <= digit1 && digit1 <= '9' && '0' <= digit2 && digit2 <= '9' )
			{
				minutes = (digit1 - '0') * 10 + (digit2 - '0');
			}
			int seconds = -1;
			digit1 = time.charAt( 6 );
			digit2 = time.charAt( 7 );
			if ( '0' <= digit1 && digit1 <= '9' && '0' <= digit2 && digit2 <= '9' )
			{
				seconds = (digit1 - '0') * 10 + (digit2 - '0');
			}

			Calendar cal = DateTimeUtil.getBaseCalendar();
			cal.set( Calendar.YEAR, cal.get( Calendar.YEAR ) );
			cal.set( Calendar.MONTH, cal.get( Calendar.MONTH ) );
			cal.set( Calendar.DATE, cal.get( Calendar.DATE ) );
			cal.set( Calendar.HOUR_OF_DAY, hours );
			cal.set( Calendar.MINUTE, minutes );
			cal.set( Calendar.SECOND, seconds );
			
			// Verify that the values are valid.
			cal.setTimeInMillis( cal.getTimeInMillis() );
			
			return cal;
		}
		catch ( Exception ex )
		{
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "time";
			tcx.initCause( ex );
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, time, tcx.actualTypeName, tcx.castToName );
		}
	}

	public static Calendar asTime(ETime time) throws AnyException {
		if (time == null)
			return null;
		return asTime(time.ezeUnbox());
	}

	public static Calendar asTime(ETimestamp time) throws AnyException {
		if (time == null)
			return null;
		return asTime(time.ezeUnbox(), time.getStartCode(), time.getEndCode());
	}

	public static Calendar asTime(GregorianCalendar time) throws AnyException {
		if (time == null)
			return null;
		return asTime((Calendar) time);
	}

	public static Calendar asTime(Calendar time) throws AnyException {
		if (time == null)
			return null;
		return asTime(time, ETimestamp.HOUR_CODE, ETimestamp.SECOND_CODE);
	}

	public static Calendar asTime(Calendar original, int startCode, int endCode) {
		if (original == null)
			return null;
		// Make sure all required fields were found.
		if (startCode > ETimestamp.HOUR_CODE || endCode < ETimestamp.SECOND_CODE) {
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "timestamp(\"" + ETimestamp.createMask( startCode, endCode ) + "\")";
			tcx.castToName = "time";
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, original, tcx.actualTypeName, tcx.castToName );
		}
		Calendar cal = (Calendar) original.clone();
		Calendar result = DateTimeUtil.getBaseCalendar();
		// Get values for the full set of fields. Fields that we need will be
		// set from the calendar. The others will be set to reasonable defaults.
		result.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
		result.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
		result.set(Calendar.SECOND, cal.get(Calendar.SECOND));
		try {
			result.setTimeInMillis(result.getTimeInMillis());
		}
		catch (Exception ex) {
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "timestamp(\"" + ETimestamp.createMask( startCode, endCode ) + "\")";
			tcx.castToName = "time";
			tcx.initCause( ex );
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, original, tcx.actualTypeName, tcx.castToName );
		}
		return result;
	}

	public static int compareTo(Calendar op1, Calendar op2) throws AnyException {
		return op1.compareTo(op2);
	}

	public static boolean equals(Calendar op1, Calendar op2) {
		if (op1 == op2)
			return true;
		if (op1 == null || op2 == null)
			return false;
		return op1.get(Calendar.HOUR_OF_DAY) == op2.get(Calendar.HOUR_OF_DAY) && op1.get(Calendar.MINUTE) == op2.get(Calendar.MINUTE)
			&& op1.get(Calendar.SECOND) == op2.get(Calendar.SECOND);
	}

	public static boolean notEquals(Calendar op1, Calendar op2) {
		return !equals(op1, op2);
	}
	
	/**
	 * Returns the hour of a time
	 */
	public static int hourOf(ETime aTime) {
		return aTime.ezeUnbox().get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * Returns the minute of a time
	 */
	public static int minuteOf(ETime aTime) {
		return aTime.ezeUnbox().get(Calendar.MINUTE);
	}

	/**
	 * Returns the second of a time
	 */
	public static int secondOf(ETime aTime) {
		return aTime.ezeUnbox().get(Calendar.SECOND);
	}

	/**
	 * Returns the extension of a time
	 */
	public static Calendar extend(ETime aTime, String timeSpanPattern) throws AnyException {
		// Default values in case the pattern doesn't specify things.
		int startCode = ETimestamp.YEAR_CODE;
		int endCode = ETimestamp.SECOND_CODE;
		TimestampIntervalMask mask = new TimestampIntervalMask(timeSpanPattern);
		if (mask.getStartCode() != -1 && mask.getStartCode() <= mask.getEndCode()) {
			startCode = mask.getStartCode();
			endCode = mask.getEndCode();
		}
		return ETimestamp.asTimestamp(aTime, startCode, endCode);
	}
}
