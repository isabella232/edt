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

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.javart.util.JavartDateFormat;
import org.eclipse.edt.javart.util.TimestampIntervalMask;

/**
 * A class for Timestamps. The value is a Calendar and a number of microseconds (an int) which is encapsulated in a
 * TimestampData object. Timestamps store a varying set of fields, from years down to microseconds. The first and last fields
 * are indicated by the startCode and endCode fields, which are set from the "CODE" constants.
 * @author mheitz
 */
public class ETimestamp extends AnyBoxedObject<Calendar> {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	public static final String DefaultPattern = "yyyyMMddHHmmss";
	public static final String DefaultFormatPattern = "yyyy-MM-dd HH:mm:ss.SSSSSS";

	/**
	 * Indicates that years are stored by this timestamp.
	 */
	public static final int YEAR_CODE = 0;

	/**
	 * Indicates that months are stored by this timestamp.
	 */
	public static final int MONTH_CODE = 1;

	/**
	 * Indicates that days are stored by this timestamp.
	 */
	public static final int DAY_CODE = 2;

	/**
	 * Indicates that hours are stored by this timestamp.
	 */
	public static final int HOUR_CODE = 3;

	/**
	 * Indicates that minutes are stored by this timestamp.
	 */
	public static final int MINUTE_CODE = 4;

	/**
	 * Indicates that seconds are stored by this timestamp.
	 */
	public static final int SECOND_CODE = 5;

	/**
	 * Indicates that tenths of seconds are stored by this timestamp.
	 */
	public static final int FRACTION1_CODE = 6;

	/**
	 * Indicates that hundredths of seconds are stored by this timestamp.
	 */
	public static final int FRACTION2_CODE = 7;

	/**
	 * Indicates that milliseconds are stored by this timestamp.
	 */
	public static final int FRACTION3_CODE = 8;

	/**
	 * Indicates that ten-thousandths of seconds are stored by this timestamp.
	 */
	public static final int FRACTION4_CODE = 9;

	/**
	 * Indicates that hundred-thousandths of seconds are stored by this timestamp.
	 */
	public static final int FRACTION5_CODE = 10;

	/**
	 * Indicates that microseconds are stored by this timestamp.
	 */
	public static final int FRACTION6_CODE = 11;

	private int startCode;
	private int endCode;

	public ETimestamp() {
		this(DateTimeUtil.getNewCalendar(), YEAR_CODE, SECOND_CODE);
	}

	public ETimestamp(Calendar value, int startCode, int endCode) {
		super(value);
		this.startCode = startCode;
		this.endCode = endCode;
	}

	public static ETimestamp ezeBox(Calendar value, int startCode, int endCode) {
		return new ETimestamp((Calendar) value.clone(), startCode, endCode);
	}

	public static Object ezeCast(Object value, Object[] constraints) throws JavartException {
		Integer[] args = new Integer[constraints.length];
		java.lang.System.arraycopy(constraints, 0, args, 0, args.length);
		return ezeCast(value, args);
	}

	public static BigDecimal ezeCast(Object value, Integer... args) throws JavartException {
		return (BigDecimal) EglAny.ezeCast(value, "asTimestamp", ETimestamp.class, new Class[] { Integer[].class }, args);
	}

	public static boolean ezeIsa(Object value, Integer... args) {
		boolean isa = value instanceof ETimestamp;
		if (isa && args.length == 2)
			isa = ((ETimestamp) value).startCode == args[0] && ((ETimestamp) value).endCode == args[1];
		return isa;
	}

	public static Calendar asTimestamp(Calendar timestamp) throws JavartException {
		return asTimestamp(timestamp, DefaultPattern);
	}

	public static Calendar asTimestamp(Calendar timestamp, String timespanMask) throws JavartException {
		if (timestamp == null)
			return null;
		if (timespanMask == null || timespanMask.length() == 0)
			timespanMask = DefaultPattern;
		// Default values in case the pattern doesn't specify things.
		int startCode = ETimestamp.YEAR_CODE;
		int endCode = ETimestamp.SECOND_CODE;
		TimestampIntervalMask mask = new TimestampIntervalMask(timespanMask);
		if (mask.getStartCode() != -1 && mask.getStartCode() <= mask.getEndCode()) {
			startCode = mask.getStartCode();
			endCode = mask.getEndCode();
		}
		return asTimestamp(timestamp, startCode, endCode);
	}

	public static Calendar asTimestamp(String timestamp, Integer... length) throws JavartException {
		return asTimestamp(EString.asString(timestamp, length), DefaultPattern);
	}

	public static Calendar asTimestamp(String timestamp, String timespanMask) throws JavartException {
		// Default values in case the pattern doesn't specify things.
		int startCode = ETimestamp.YEAR_CODE;
		int endCode = ETimestamp.SECOND_CODE;
		TimestampIntervalMask mask = new TimestampIntervalMask(timespanMask);
		if (mask.getStartCode() != -1 && mask.getStartCode() <= mask.getEndCode()) {
			startCode = mask.getStartCode();
			endCode = mask.getEndCode();
		}
		return convert(timestamp, startCode, endCode);
	}

	public static Calendar asTimestamp(String timestamp, Integer length, String timespanMask) throws JavartException {
		return asTimestamp(EString.asString(timestamp, length), timespanMask);
	}

	public static Calendar asTimestamp(Calendar date, int startCode, int endCode) {
		if (date == null)
			return null;
		Calendar result = DateTimeUtil.getBaseCalendar();
		// Get values for the full set of fields. Fields that we need will be
		// set from the calendar. The others will be set to reasonable defaults.
		Calendar forDefaults = null;
		int year;
		if (startCode == YEAR_CODE)
			// Get the year.
			year = date.get(Calendar.YEAR);
		else {
			// Use the current year.
			forDefaults = DateTimeUtil.getNewCalendar();
			year = forDefaults.get(Calendar.YEAR);
		}
		int month;
		if (startCode <= MONTH_CODE && endCode >= MONTH_CODE)
			// Get the month.
			month = date.get(Calendar.MONTH);
		else if (startCode < MONTH_CODE)
			// We don't care what month it is.
			month = 0;
		else {
			// Use the current month.
			if (forDefaults == null)
				forDefaults = DateTimeUtil.getNewCalendar();
			month = forDefaults.get(Calendar.MONTH);
		}
		int day;
		if (startCode <= DAY_CODE && endCode >= DAY_CODE)
			// Get the day.
			day = date.get(Calendar.DATE);
		else if (startCode < DAY_CODE)
			// We don't care what day it is.
			day = 1;
		else {
			// Use the current day.
			if (forDefaults == null)
				forDefaults = DateTimeUtil.getNewCalendar();
			day = forDefaults.get(Calendar.DATE);
		}
		int hour;
		if (startCode <= HOUR_CODE && endCode >= HOUR_CODE)
			// Get the hour.
			hour = date.get(Calendar.HOUR_OF_DAY);
		else if (startCode < HOUR_CODE)
			// We don't care what hour it is.
			hour = 0;
		else {
			// Use the current hour.
			if (forDefaults == null)
				forDefaults = DateTimeUtil.getNewCalendar();
			hour = forDefaults.get(Calendar.HOUR_OF_DAY);
		}
		int minute;
		if (startCode <= MINUTE_CODE && endCode >= MINUTE_CODE)
			// Get the minute.
			minute = date.get(Calendar.MINUTE);
		else if (startCode < MINUTE_CODE)
			// We don't care what minute it is.
			minute = 0;
		else {
			// Use the current minute.
			if (forDefaults == null)
				forDefaults = DateTimeUtil.getNewCalendar();
			minute = forDefaults.get(Calendar.MINUTE);
		}
		int second;
		if (startCode <= SECOND_CODE && endCode >= SECOND_CODE)
			// Get the second.
			second = date.get(Calendar.SECOND);
		else if (startCode < SECOND_CODE)
			// We don't care what second it is.
			second = 0;
		else {
			// Use the current second.
			if (forDefaults == null)
				forDefaults = DateTimeUtil.getNewCalendar();
			second = forDefaults.get(Calendar.SECOND);
		}
		int milliseconds;
		if (startCode <= FRACTION1_CODE && endCode >= FRACTION1_CODE)
			// Get the second.
			milliseconds = date.get(Calendar.MILLISECOND);
		else if (startCode < FRACTION1_CODE)
			// We don't care what second it is.
			milliseconds = 0;
		else {
			// Use the current second.
			if (forDefaults == null)
				forDefaults = DateTimeUtil.getNewCalendar();
			milliseconds = forDefaults.get(Calendar.MILLISECOND);
		}
		// if ( endCode >= FRACTION1_CODE )
		// {
		// // Make sure we don't keep too many digits of the micros value.
		// int fractionDigits = endCode - FRACTION1_CODE + 1;
		// for ( int i = fractionDigits; i < 6; i++ )
		// {
		// microseconds /= 10;
		// }
		// for ( int i = fractionDigits; i < 6; i++ )
		// {
		// microseconds *= 10;
		// }
		// }
		// else
		// {
		// microseconds = 0;
		// }
		// Update the values.
		result.set(year, month, day, hour, minute, second);
		result.set(Calendar.MILLISECOND, milliseconds);
		return result;
	}

	/**
	 * Converts a formatted string into a Calendar that's suitable for assigning or comparing with this item. Strings are
	 * parsed using defaultTimeStampFormat if it's not blank, otherwise we use the pattern field of this object. If the
	 * string can't be parsed using a pattern then we do it by hand, pulling out digits and ignoring non-digit characters.
	 * <P> When we parse using defaultTimeStampFormat, only the fields from the format string are set. The rest default to
	 * zeros so you get Jan 1 1970, etc. When we parse by hand, unspecified fields are set to the current time. This behavior
	 * is OK for assignments, but it must be accounted for when comparing two timestamps.
	 */
	public static Calendar asTimestamp(String timestamp, String format, int startCode, int endCode) throws JavartException {
		if (timestamp == null)
			return null;
		Calendar result;
		timestamp = timestamp.trim();
		if (format == null || format.length() == 0) {
			// format = program._runUnit().getDefaultTimestampFormat();
			format = DefaultFormatPattern;
		}
		try {
			result = convert(timestamp, format);
		}
		catch (ParseException pe) {
			// OK. Do it ourselves
			result = convert(timestamp, startCode, endCode);
		}
		return result;
	}

	public static Calendar asTimestamp(String timestamp, Integer length, String format, int startCode, int endCode) throws JavartException {
		return asTimestamp(EString.asString(timestamp, length), format, startCode, endCode);
	}

	public static Calendar convert(String timestamp, int startCode, int endCode) {
		Calendar result;
		// Try to parse the string by hand, looking for each field (years, months,
		// etc.) that this ETimestamp stores.
		int years = -1;
		int months = -1;
		int days = -1;
		int hours = -1;
		int minutes = -1;
		int seconds = -1;
		int microseconds = -1;
		int length = timestamp.length();
		PARSE: if (length > 0) {
			// ch is the character we're currently looking at. i is the index of
			// the
			// next character after ch.
			char ch;
			int i = 0;
			// Locate the first digit.
			do {
				ch = timestamp.charAt(i);
				i++;
			}
			while (i < length && !('0' <= ch && ch <= '9'));
			// Read in the number of years.
			if (i <= length && startCode == YEAR_CODE) {
				years = 0;
				for (int j = 0; '0' <= ch && ch <= '9' && j < 4; j++) {
					years = years * 10 + ch - '0';
					if (i < length) {
						ch = timestamp.charAt(i);
						i++;
					} else {
						break PARSE;
					}
				}
			}
			// Skip ahead to the next digit.
			while (i < length && !('0' <= ch && ch <= '9')) {
				ch = timestamp.charAt(i);
				i++;
			}
			// Read in the number of months.
			if (i <= length && startCode <= MONTH_CODE && endCode >= MONTH_CODE) {
				months = ch - '0';
				if (i < length) {
					ch = timestamp.charAt(i);
					i++;
					if ('0' <= ch && ch <= '9') {
						months = months * 10 + ch - '0';
						if (i < length) {
							ch = timestamp.charAt(i);
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
				ch = timestamp.charAt(i);
				i++;
			}
			// Read in the number of days.
			if (i <= length && startCode <= DAY_CODE && endCode >= DAY_CODE) {
				days = ch - '0';
				if (i < length) {
					ch = timestamp.charAt(i);
					i++;
					if ('0' <= ch && ch <= '9') {
						days = days * 10 + ch - '0';
						if (i < length) {
							ch = timestamp.charAt(i);
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
				ch = timestamp.charAt(i);
				i++;
			}
			// Read in the number of hours.
			if (i <= length && startCode <= HOUR_CODE && endCode >= HOUR_CODE) {
				hours = ch - '0';
				if (i < length) {
					ch = timestamp.charAt(i);
					i++;
					if ('0' <= ch && ch <= '9') {
						hours = hours * 10 + ch - '0';
						if (i < length) {
							ch = timestamp.charAt(i);
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
				ch = timestamp.charAt(i);
				i++;
			}
			// Read in the number of minutes.
			if (i <= length && startCode <= MINUTE_CODE && endCode >= MINUTE_CODE) {
				minutes = ch - '0';
				if (i < length) {
					ch = timestamp.charAt(i);
					i++;
					if ('0' <= ch && ch <= '9') {
						minutes = minutes * 10 + ch - '0';
						if (i < length) {
							ch = timestamp.charAt(i);
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
				ch = timestamp.charAt(i);
				i++;
			}
			// Read in the number of seconds.
			if (i <= length && startCode <= SECOND_CODE && endCode >= SECOND_CODE) {
				seconds = ch - '0';
				if (i < length) {
					ch = timestamp.charAt(i);
					i++;
					if ('0' <= ch && ch <= '9') {
						seconds = seconds * 10 + ch - '0';
						if (i < length) {
							ch = timestamp.charAt(i);
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
				ch = timestamp.charAt(i);
				i++;
			}
			// Read in the number of microseconds.
			if (i <= length && endCode >= FRACTION1_CODE) {
				microseconds = 0;
				int microsecondsFound = 0;
				int fractionDigits = endCode - FRACTION1_CODE + 1;
				while (microsecondsFound < fractionDigits && '0' <= ch && ch <= '9') {
					microseconds *= 10;
					microseconds += ch - '0';
					microsecondsFound++;
					if (i < length) {
						ch = timestamp.charAt(i);
						i++;
					} else {
						break;
					}
				}
				if (microsecondsFound < 6) {
					// If we didn't read in the full number of fraction digits,
					// then
					// adjust what we saw. If we read "650" then microseconds
					// should
					// be 65000 not 650.
					for (int j = microsecondsFound; j < 6; j++) {
						microseconds *= 10;
					}
				}
			}
		}
		// Make sure all required fields were found.
		if ((years == -1 && startCode == YEAR_CODE) || (months == -1 && startCode <= MONTH_CODE && endCode >= MONTH_CODE)
			|| (days == -1 && startCode <= DAY_CODE && endCode >= DAY_CODE) || (hours == -1 && startCode <= HOUR_CODE && endCode >= HOUR_CODE)
			|| (minutes == -1 && startCode <= MINUTE_CODE && endCode >= MINUTE_CODE) || (seconds == -1 && startCode <= SECOND_CODE && endCode >= SECOND_CODE)
			|| (microseconds == -1 && endCode >= FRACTION1_CODE))
			throw new TypeCastException();
		// The last thing to do is put the values into a calendar and
		// TimestampData.
		Calendar cal = DateTimeUtil.getBaseCalendar();
		if (years != -1)
			cal.set(Calendar.YEAR, years);
		if (months != -1)
			cal.set(Calendar.MONTH, months - 1);
		if (days != -1)
			cal.set(Calendar.DATE, days);
		else if (months != -1)
			// No day was specified, but a month was specified. Set the day to
			// one. Without this we'd default to the current day, which might
			// be invalid for the month that was specified. For example, this
			// change was put in on May 30 after setting a timestamp to
			// Feb 2007: Feb 30 is not a valid date.
			cal.set(Calendar.DATE, 1);
		if (hours != -1)
			cal.set(Calendar.HOUR_OF_DAY, hours);
		if (minutes != -1)
			cal.set(Calendar.MINUTE, minutes);
		if (seconds != -1)
			cal.set(Calendar.SECOND, seconds);
		if (microseconds == -1)
			microseconds = 0;
		// Clear the milliseconds.
		cal.set(Calendar.MILLISECOND, 0);
		// Verify that the values are valid.
		try {
			cal.getTimeInMillis();
		}
		catch (Exception ex) {
			throw new TypeCastException();
		}
		result = cal;
		return result;
	}

	public static Calendar convert(String timestamp, String format) throws ParseException {
		synchronized (DateTimeUtil.LOCK) {
			JavartDateFormat formatter = DateTimeUtil.getDateFormat(format);
			formatter.setMicrosecond(0);
			Date date = formatter.parse(timestamp);
			Calendar cal = DateTimeUtil.getBaseCalendar();
			cal.setTime(date);
			return cal;
		}
	}

	/**
	 * Create a Timestamp pattern string based on a startcode and endcode value
	 */
	public static String createPattern(int startCode, int endCode) {
		if (startCode > endCode) {
			return null;
		}
		String[] code = { "yyyy", "MM", "dd", "HH", "mm", "ss", "SSSSSS" };
		String[] delimiters = { "-", "-", " ", ":", ":", "." };
		int sindex = (startCode > 6) ? 6 : startCode;
		int eindex = (endCode > 6) ? 6 : endCode;
		StringBuilder patternString = new StringBuilder(26);
		for (int i = sindex, j = 0; i <= eindex; i++, j++) {
			if (j > 0)
				patternString.append(delimiters[i - 1]);
			patternString.append(code[i]);
		}
		return patternString.toString();
	}

	public static Calendar defaultValue() {
		Calendar cal;
		synchronized (ETimestamp.class) {
			long now = java.lang.System.currentTimeMillis();
			cal = DateTimeUtil.getBaseCalendar();
			cal.setTimeInMillis(now);
			cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE),
				cal.get(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));
		}
		return cal;
	}

	/**
	 * Returns the day of a timestamp
	 */
	public static Integer dayOf(Calendar aTimestamp) throws JavartException {
		return aTimestamp.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Returns the month of a timestamp
	 */
	public static Integer monthOf(Calendar aTimestamp) throws JavartException {
		return aTimestamp.get(Calendar.MONTH) + 1;
	}

	/**
	 * Returns the year of a timestamp
	 */
	public static Integer yearOf(Calendar aTimestamp) throws JavartException {
		return aTimestamp.get(Calendar.YEAR);
	}

	/**
	 * Returns the weekday of a timestamp
	 */
	public static Integer weekdayOf(Calendar aTimestamp) throws JavartException {
		return aTimestamp.get(Calendar.DAY_OF_WEEK) - 1;
	}

	/**
	 * Returns the date of a timestamp
	 */
	public static Calendar dateOf(Calendar aTimestamp) throws JavartException {
		return aTimestamp;
	}

	/**
	 * Returns the time of a timestamp
	 */
	public static Calendar timeOf(Calendar aTimestamp) throws JavartException {
		return aTimestamp;
	}

	/**
	 * Returns the extension of a timestamp
	 */
	public static Calendar extend(Calendar aTimestamp, String timeSpanPattern) throws JavartException {
		// Default values in case the pattern doesn't specify things.
		int startCode = ETimestamp.YEAR_CODE;
		int endCode = ETimestamp.SECOND_CODE;
		TimestampIntervalMask mask = new TimestampIntervalMask(timeSpanPattern);
		if (mask.getStartCode() != -1 && mask.getStartCode() <= mask.getEndCode()) {
			startCode = mask.getStartCode();
			endCode = mask.getEndCode();
		}
		return new ETimestamp((Calendar) aTimestamp.clone(), startCode, endCode).ezeUnbox();
	}
}
