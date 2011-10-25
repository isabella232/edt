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
 * A class for Dates. The value is a Calendar.
 * @author mheitz
 */
public class EDate extends AnyBoxedObject<Calendar> {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public EDate() {
		this(DateTimeUtil.getNewCalendar());
	}

	public EDate(Calendar value) {
		super(value);
	}

	public static EDate ezeBox(Calendar value) {
		Calendar clone = null;
		if (value != null) {
			clone = ETimestamp.ezeClone(value, ETimestamp.YEAR_CODE, ETimestamp.FRACTION1_CODE);
		}
		return new EDate(clone);
	}

	public static Object ezeCast(Object value, Object[] constraints) throws AnyException {
		Integer[] args = new Integer[constraints.length];
		java.lang.System.arraycopy(constraints, 0, args, 0, args.length);
		return ezeCast(value, args);
	}

	public static Calendar ezeCast(Object value) throws AnyException {
		return (Calendar) EAny.ezeCast(value, "asDate", EDate.class, null, null);
	}

	public static boolean ezeIsa(Object value) {
		return value instanceof EDate
			|| (value instanceof Calendar && !((Calendar) value).isSet(Calendar.HOUR) && !((Calendar) value).isSet(Calendar.MINUTE)
				&& !((Calendar) value).isSet(Calendar.SECOND) && !((Calendar) value).isSet(Calendar.MILLISECOND));
	}

	public String toString() {
		return EString.asString(object);
	}

	public static Calendar defaultValue() {
		long now = java.lang.System.currentTimeMillis();
		Calendar cal = DateTimeUtil.getBaseCalendar();
		cal.setTimeInMillis(now);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int date = cal.get(Calendar.DATE);
		cal.clear();
		cal.set(year, month, date);
		return cal;
	}

	/**
	 * {@Operation narrow} Converts a string to a date. The string is parsed by searching for the month, then the day, then
	 * the year. One or two digits can be specified for the month and day. The year requires a minimum of one digit and a
	 * maximum of at least four digits (in other words, some implementations can support years beyond 9999). One separator
	 * character is required between the month and day, and another between the day and year. The separator character can be
	 * anything, even a digit (though that's probably a bad idea) and the two separator characters don't have to be
	 * identical.
	 * @throws TypeCastException if the string can't be parsed into a date.
	 */
	public static Calendar asDate(EString date) throws TypeCastException {
		return asDate(date.ezeUnbox());
	}

	public static Calendar asDate(String date) throws TypeCastException {
		if (date == null)
			return null;
		// Quick check for strings that are too long or too short.
		int length = date.length();
		if (length < 5 || length > 10) {
			// Minimum is 5 characters: 1/1/1
			// Maximum is 10 characters: 11/11/1111
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "date";
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, date, tcx.actualTypeName, tcx.castToName );
		}

		int months = -1;
		int days = -1;
		int years = -1;

		PARSE: {
			char ch = date.charAt(0);
			if (ch < '0' || ch > '9') {
				break PARSE;
			}
			months = ch - '0';
			ch = date.charAt(1);
			int i;
			if (ch < '0' || ch > '9') {
				// There's one digit for the month.
				i = 2;
			} else {
				// Two digits for the month.
				months = months * 10 + ch - '0';
				i = 3;
			}

			ch = date.charAt(i);
			if (ch < '0' || ch > '9') {
				break PARSE;
			}
			days = ch - '0';
			i++;
			ch = date.charAt(i);
			if (ch < '0' || ch > '9') {
				// There's one digit for the day.
				i++;
			} else {
				// Two digits for the day.
				days = days * 10 + ch - '0';
				i += 2;
			}

			ch = date.charAt(i);
			if (ch < '0' || ch > '9') {
				break PARSE;
			}
			int actualDigits = 1; 
			years = ch - '0';
			i++;
			for (int digits = 0; i < length && digits < 5; digits++, i++) {
				ch = date.charAt(i);
				if (ch < '0' || ch > '9') {
					break PARSE;
				}
				years = years * 10 + ch - '0';
				actualDigits++;
			}

			// Make sure we didn't get too many or too few digits for the year.
			if (actualDigits != 4 || i < length) {
				years = -1;
			}
		}

		// Make sure all required fields were found.
		if (months == -1 || days == -1 || years == -1) {
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "date";
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, date, tcx.actualTypeName, tcx.castToName );
		}

		// The last thing to do is put the values into a Calendar.
		Calendar cal = DateTimeUtil.getBaseCalendar();
		cal.clear();
		cal.set(Calendar.YEAR, years);
		cal.set(Calendar.MONTH, months - 1);
		cal.set(Calendar.DATE, days);
		// Verify that the values are valid.
		try {
			cal.getTimeInMillis();
		}
		catch (Exception ex) {
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "date";
			tcx.initCause( ex );
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, date, tcx.actualTypeName, tcx.castToName );
		}
		return cal;
	}

	public static Calendar asDate(EDate date) throws AnyException {
		if (date == null)
			return null;
		return asDate(date.ezeUnbox());
	}

	public static Calendar asDate(ETimestamp date) throws AnyException {
		if (date == null)
			return null;
		return asDate(date.ezeUnbox());
	}

	public static Calendar asDate(GregorianCalendar date) throws AnyException {
		if (date == null)
			return null;
		return asDate((Calendar) date);
	}

	public static Calendar asDate(Calendar date) throws AnyException {
		if (date == null)
			return null;
		return ETimestamp.convert(EString.asString(date), ETimestamp.YEAR_CODE, ETimestamp.DAY_CODE);
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
		return op1.equals(op2);
	}

	public static boolean notEquals(Calendar op1, Calendar op2) {
		if (op1 == null && op2 == null)
			return false;
		if (op1 == null || op2 == null)
			return true;
		return !op1.equals(op2);
	}

	/**
	 * Returns the difference between 2 dates
	 */
	public static int daysDifferent(Calendar aDate, Calendar bDate) throws AnyException {
		return (int) ((aDate.getTimeInMillis() - bDate.getTimeInMillis()) / (1000 * DateTimeUtil.SECONDS_PER_DAY));
	}

	/**
	 * Returns the adds days to a date
	 */
	public static Calendar addDays(Calendar aDate, int amount) throws AnyException {
		Calendar newDate = ETimestamp.ezeClone(aDate, ETimestamp.YEAR_CODE, ETimestamp.FRACTION1_CODE);
		newDate.setLenient(true);
		newDate.add(Calendar.DATE, amount);
		newDate.setLenient(false);
		return newDate;
	}

	/**
	 * Returns the extension of a date
	 */
	public static Calendar extend(Calendar aDate, String timeSpanPattern) throws AnyException {
		// Default values in case the pattern doesn't specify things.
		int startCode = ETimestamp.YEAR_CODE;
		int endCode = ETimestamp.SECOND_CODE;
		TimestampIntervalMask mask = new TimestampIntervalMask(timeSpanPattern);
		if (mask.getStartCode() != -1 && mask.getStartCode() <= mask.getEndCode()) {
			startCode = mask.getStartCode();
			endCode = mask.getEndCode();
		}
		return new ETimestamp(ETimestamp.ezeClone(aDate, startCode, endCode), startCode, endCode).ezeUnbox();
	}
}
