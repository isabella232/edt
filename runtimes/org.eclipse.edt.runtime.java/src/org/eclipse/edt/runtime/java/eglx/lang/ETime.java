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

import eglx.lang.AnyException;
import eglx.lang.TypeCastException;

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

	/**
	 * {@Operation narrow} Converts a string to a time. The string is parsed by searching for the month, then the day, then
	 * the year. One or two digits can be specified for the month and day. The year requires a minimum of one digit and a
	 * maximum of at least four digits (in other words, some implementations can support years beyond 9999). One separator
	 * character is required between the month and day, and another between the day and year. The separator character can be
	 * anything, even a digit (though that's probably a bad idea) and the two separator characters don't have to be
	 * identical.
	 * @throws TypeCastException if the string can't be parsed into a time.
	 */
	public static Calendar asTime(EString time) throws TypeCastException {
		return asTime(time.ezeUnbox());
	}

	public static Calendar asTime(String time) throws TypeCastException {
		return convert(time);
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
			tcx.actualTypeName = "timestamp";
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
			tcx.actualTypeName = "timestamp";
			tcx.castToName = "time";
			tcx.initCause( ex );
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, original, tcx.actualTypeName, tcx.castToName );
		}
		return result;
	}

	public static Calendar convert(String time) {
		if (time == null)
			return null;

		int hours = -1;
		int minutes = -1;
		int seconds = -1;
		int length = time.length();
		PARSE: if (length > 0) {
			// ch is the character we're currently looking at. i is the index of
			// the next character after ch.
			char ch;
			int i = 0;
			// Locate the first digit.
			do {
				ch = time.charAt(i);
				i++;
			}
			while (i < length && !('0' <= ch && ch <= '9'));
			// Read in the number of hours.
			if (i <= length) {
				hours = ch - '0';
				if (i < length) {
					ch = time.charAt(i);
					i++;
					if ('0' <= ch && ch <= '9') {
						hours = hours * 10 + ch - '0';
						if (i < length) {
							ch = time.charAt(i);
							i++;
						} else {
							break PARSE;
						}
					}
				} else {
					break PARSE;
				}
			}
			// Skip ahead to the next digit.
			while (i < length && !('0' <= ch && ch <= '9')) {
				ch = time.charAt(i);
				i++;
			}
			// Read in the number of minutes.
			if (i <= length) {
				minutes = ch - '0';
				if (i < length) {
					ch = time.charAt(i);
					i++;
					if ('0' <= ch && ch <= '9') {
						minutes = minutes * 10 + ch - '0';
						if (i < length) {
							ch = time.charAt(i);
							i++;
						} else {
							break PARSE;
						}
					}
				} else {
					break PARSE;
				}
			}
			// Skip ahead to the next digit.
			while (i < length && !('0' <= ch && ch <= '9')) {
				ch = time.charAt(i);
				i++;
			}
			// Read in the number of seconds.
			if (i <= length) {
				seconds = ch - '0';
				if (i < length) {
					ch = time.charAt(i);
					i++;
					if ('0' <= ch && ch <= '9') {
						seconds = seconds * 10 + ch - '0';
						if (i < length) {
							ch = time.charAt(i);
							i++;
						} else {
							break PARSE;
						}
					}
				} else {
					break PARSE;
				}
			}
		}

		// Make sure all required fields were found.
		if (hours == -1 || minutes == -1 || seconds == -1) {
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "time";
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, time, tcx.actualTypeName, tcx.castToName );
		}

		// The last thing to do is put the values into a Calendar.
		Calendar cal = DateTimeUtil.getBaseCalendar();
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		cal.set(Calendar.DATE, cal.get(Calendar.DATE));
		cal.set(Calendar.HOUR_OF_DAY, hours);
		cal.set(Calendar.MINUTE, minutes);
		cal.set(Calendar.SECOND, seconds);
		// Verify that the values are valid.
		try {
			cal.setTimeInMillis(cal.getTimeInMillis());
		}
		catch (Exception ex) {
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "time";
			tcx.initCause( ex );
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, time, tcx.actualTypeName, tcx.castToName );
		}
		return cal;
	}

	public static int compareTo(Calendar op1, Calendar op2) throws AnyException {
		if (op1 == null && op2 == null)
			return 0;
		return op1.compareTo(op2);
	}

	public static boolean equals(Calendar op1, Calendar op2) {
		if (op1 == null && op2 == null)
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
