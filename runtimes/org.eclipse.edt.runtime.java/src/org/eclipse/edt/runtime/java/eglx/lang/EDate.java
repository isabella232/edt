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
			clone = ezeClone(value, ETimestamp.YEAR_CODE, ETimestamp.FRACTION1_CODE);
		}
		return new EDate(clone);
	}

	public static Object ezeCast(Object value, Object[] constraints) throws AnyException {
		Integer[] args = new Integer[constraints.length];
		java.lang.System.arraycopy(constraints, 0, args, 0, args.length);
		return ezeCast(value, args);
	}

	public static Calendar ezeCast(Object value, Integer... args) throws AnyException {
		return (Calendar) EAny.ezeCast(value, "asDate", EDate.class, null, null);
	}

	public static boolean ezeIsa(Object value, Integer... args) {
		return value instanceof EDate
			|| (value instanceof Calendar && ((Calendar) value).isSet(Calendar.ZONE_OFFSET));
	}

	public String toString() {
		return EString.asString(object);
	}

	public static Calendar ezeClone(Calendar original, int startCode, int endCode) {
		if (original == null)
			return null;
		// save the original
		boolean yearSet = original.isSet(Calendar.YEAR);
		boolean monthSet = original.isSet(Calendar.MONTH);
		boolean dateSet = original.isSet(Calendar.DATE);
		boolean hourSet = original.isSet(Calendar.HOUR_OF_DAY);
		boolean minuteSet = original.isSet(Calendar.MINUTE);
		boolean secondSet = original.isSet(Calendar.SECOND);
		boolean milliSet = original.isSet(Calendar.MILLISECOND);
		boolean zoneSet = original.isSet(Calendar.ZONE_OFFSET);
		int yearValue = original.get(Calendar.YEAR);
		int monthValue = original.get(Calendar.MONTH);
		int dateValue = original.get(Calendar.DATE);
		int hourValue = original.get(Calendar.HOUR_OF_DAY);
		int minuteValue = original.get(Calendar.MINUTE);
		int secondValue = original.get(Calendar.SECOND);
		int milliValue = original.get(Calendar.MILLISECOND);
		int zoneValue = original.get(Calendar.ZONE_OFFSET);
		// create the cloned
		Calendar cloned = defaultValue();
		cloned.clear();
		if (startCode <= ETimestamp.YEAR_CODE && endCode >= ETimestamp.YEAR_CODE && yearSet)
			cloned.set(Calendar.YEAR, yearValue);
		if (startCode <= ETimestamp.MONTH_CODE && endCode >= ETimestamp.MONTH_CODE && monthSet)
			cloned.set(Calendar.MONTH, monthValue);
		if (startCode <= ETimestamp.DAY_CODE && endCode >= ETimestamp.DAY_CODE && dateSet)
			cloned.set(Calendar.DATE, dateValue);
		if (startCode <= ETimestamp.HOUR_CODE && endCode >= ETimestamp.HOUR_CODE && hourSet)
			cloned.set(Calendar.HOUR_OF_DAY, hourValue);
		if (startCode <= ETimestamp.MINUTE_CODE && endCode >= ETimestamp.MINUTE_CODE && minuteSet)
			cloned.set(Calendar.MINUTE, minuteValue);
		if (startCode <= ETimestamp.SECOND_CODE && endCode >= ETimestamp.SECOND_CODE && secondSet)
			cloned.set(Calendar.SECOND, secondValue);
		if (startCode <= ETimestamp.FRACTION1_CODE && endCode >= ETimestamp.FRACTION1_CODE && milliSet)
			cloned.set(Calendar.MILLISECOND, milliValue);
		// this flag is used by date objects only
		if (startCode <= ETimestamp.YEAR_CODE && endCode >= ETimestamp.DAY_CODE && zoneSet)
			cloned.set(Calendar.ZONE_OFFSET, zoneValue);
		// Verify that the values are valid. We only do this if year month and date are at least there
		try {
			if (yearSet && monthSet && dateSet)
				cloned.getTimeInMillis();
		}
		catch (Exception ex) {
		}
		// we need to restore the original, because the .get method clobbers the flags set in the calendar object
		original.clear();
		if (yearSet)
			original.set(Calendar.YEAR, yearValue);
		if (monthSet)
			original.set(Calendar.MONTH, monthValue);
		if (dateSet)
			original.set(Calendar.DATE, dateValue);
		if (hourSet)
			original.set(Calendar.HOUR_OF_DAY, hourValue);
		if (minuteSet)
			original.set(Calendar.MINUTE, minuteValue);
		if (secondSet)
			original.set(Calendar.SECOND, secondValue);
		if (milliSet)
			original.set(Calendar.MILLISECOND, milliValue);
		// this flag is used by date objects only
		if (zoneSet)
			original.set(Calendar.ZONE_OFFSET, zoneValue);
		// Verify that the values are valid. We only do this if year month and date are at least there
		try {
			if (yearSet && monthSet && dateSet)
				original.getTimeInMillis();
		}
		catch (Exception ex) {
		}
		return cloned;
	}
	
	public static Calendar defaultValue() {
		long now = java.lang.System.currentTimeMillis();
		Calendar cal = DateTimeUtil.getBaseCalendar();
		cal.setTimeInMillis(now);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int date = cal.get(Calendar.DATE);
		int zone = cal.get(Calendar.ZONE_OFFSET);
		cal.clear();
		cal.set(year, month, date);
		// to indicate this is a date object, we set a field as a flag
		cal.set(Calendar.ZONE_OFFSET, zone);
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
			years = ch - '0';
			i++;
			for (int digits = 0; i < length && digits < 5; digits++, i++) {
				ch = date.charAt(i);
				if (ch < '0' || ch > '9') {
					break PARSE;
				}
				years = years * 10 + ch - '0';
			}

			// Make sure we didn't get too many digits for the year.
			if (i < length) {
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
		// to indicate this is a date object, we set a field as a flag
		cal.set(Calendar.ZONE_OFFSET, DateTimeUtil.getBaseCalendar().get(Calendar.ZONE_OFFSET));
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
		Calendar cal = convert(EString.asString(date));
		// to indicate this is a date object, we set a field as a flag
		cal.set(Calendar.ZONE_OFFSET, DateTimeUtil.getBaseCalendar().get(Calendar.ZONE_OFFSET));
		return cal;
	}

	public static Calendar convert(String date) {
		// Try to parse the string by hand, looking for each field
		int years = -1;
		int months = -1;
		int days = -1;
		int length = date.length();
		PARSE: if (length > 0) {
			// ch is the character we're currently looking at. i is the index of
			// the next character after ch.
			char ch;
			int i = 0;
			// Locate the first digit.
			do {
				ch = date.charAt(i);
				i++;
			}
			while (i < length && !('0' <= ch && ch <= '9'));
			// Read in the number of months.
			if (i <= length) {
				months = ch - '0';
				if (i < length) {
					ch = date.charAt(i);
					i++;
					if ('0' <= ch && ch <= '9') {
						months = months * 10 + ch - '0';
						if (i < length) {
							ch = date.charAt(i);
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
				ch = date.charAt(i);
				i++;
			}
			// Read in the number of days.
			if (i <= length) {
				days = ch - '0';
				if (i < length) {
					ch = date.charAt(i);
					i++;
					if ('0' <= ch && ch <= '9') {
						days = days * 10 + ch - '0';
						if (i < length) {
							ch = date.charAt(i);
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
				ch = date.charAt(i);
				i++;
			}
			// Read in the number of years.
			if (i <= length) {
				years = 0;
				for (int j = 0; '0' <= ch && ch <= '9' && j < 4; j++) {
					years = years * 10 + ch - '0';
					if (i < length) {
						ch = date.charAt(i);
						i++;
					} else {
						break PARSE;
					}
				}
			}
		}
		// Make sure all required fields were found.
		if (years == -1 || months == -1 || days == -1)
		{
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "date";
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, date, tcx.actualTypeName, tcx.castToName );
		}
		// The last thing to do is put the values into a calendar and DateData.
		Calendar cal = DateTimeUtil.getBaseCalendar();
		if (years != -1)
			cal.set(Calendar.YEAR, years);
		if (months != -1)
			cal.set(Calendar.MONTH, months - 1);
		if (days != -1)
			cal.set(Calendar.DATE, days);
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
		// we need to use the ETimestamp version of ezeClone here, because we are not creating a date object
		Calendar newDate = ETimestamp.ezeClone(aDate, ETimestamp.YEAR_CODE, ETimestamp.FRACTION1_CODE);
		newDate.setLenient(true);
		newDate.add(Calendar.DATE, amount);
		// Verify that the values are valid. We only do this if year month and date are at least there
		try {
			newDate.getTimeInMillis();
		}
		catch (Exception ex) {
		}
		// as the .add method set all of the flags on, now create a date object from the original 
		Calendar retDate = ezeClone(aDate, ETimestamp.YEAR_CODE, ETimestamp.DAY_CODE);
		retDate.set(Calendar.YEAR, newDate.get(Calendar.YEAR));
		retDate.set(Calendar.MONTH, newDate.get(Calendar.MONTH));
		retDate.set(Calendar.DATE, newDate.get(Calendar.DATE));
		// Verify that the values are valid. We only do this if year month and date are at least there
		try {
			retDate.getTimeInMillis();
		}
		catch (Exception ex) {
		}
		return retDate;
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
		// we need to use the ETimestamp version of ezeClone here, because we are not creating a date object
		return new ETimestamp(ETimestamp.ezeClone(aDate, startCode, endCode), startCode, endCode).ezeUnbox();
	}
}
