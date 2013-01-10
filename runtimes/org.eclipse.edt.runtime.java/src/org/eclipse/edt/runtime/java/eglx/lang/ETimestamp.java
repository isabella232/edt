/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.javart.util.JavartDateFormat;
import org.eclipse.edt.javart.util.TimestampIntervalMask;

import eglx.lang.*;

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
	 * Its value is 0.
	 */
	public static final int YEAR_CODE = 0;

	/**
	 * Indicates that months are stored by this timestamp.
	 * Its value is 1.
	 */
	public static final int MONTH_CODE = 1;

	/**
	 * Indicates that days are stored by this timestamp.
	 * Its value is 2.
	 */
	public static final int DAY_CODE = 2;

	/**
	 * Indicates that hours are stored by this timestamp.
	 * Its value is 3.
	 */
	public static final int HOUR_CODE = 3;

	/**
	 * Indicates that minutes are stored by this timestamp.
	 * Its value is 4.
	 */
	public static final int MINUTE_CODE = 4;

	/**
	 * Indicates that seconds are stored by this timestamp.
	 * Its value is 5.
	 */
	public static final int SECOND_CODE = 5;

	/**
	 * Indicates that tenths of seconds are stored by this timestamp.
	 * Its value is 6.
	 */
	public static final int FRACTION1_CODE = 6;

	/**
	 * Indicates that hundredths of seconds are stored by this timestamp.
	 * Its value is 7.
	 */
	public static final int FRACTION2_CODE = 7;

	/**
	 * Indicates that milliseconds are stored by this timestamp.
	 * Its value is 8.
	 */
	public static final int FRACTION3_CODE = 8;

	/**
	 * Indicates that ten-thousandths of seconds are stored by this timestamp.
	 * Its value is 9.
	 */
	public static final int FRACTION4_CODE = 9;

	/**
	 * Indicates that hundred-thousandths of seconds are stored by this timestamp.
	 * Its value is 10.
	 */
	public static final int FRACTION5_CODE = 10;

	/**
	 * Indicates that microseconds are stored by this timestamp.
	 * Its value is 11.
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

	public int getStartCode() {
		return startCode;
	}

	public int getEndCode() {
		return endCode;
	}

	public static ETimestamp ezeBox(Calendar value, int startCode, int endCode) {
		Calendar clone = null;
		if (value != null) {
			clone = (Calendar) value.clone();
		}
		return new ETimestamp(clone, startCode, endCode);
	}

	public static ETimestamp ezeBox(Calendar value) {
		return ezeBox(value, YEAR_CODE, SECOND_CODE);
	}

	public static Object ezeCast(Object value, Object[] constraints) throws AnyException {
		Integer[] args = new Integer[constraints.length];
		java.lang.System.arraycopy(constraints, 0, args, 0, args.length);
		return ezeCast(value, args);
	}

	public static Calendar ezeCast(Object value, Integer... args) throws AnyException {
		return (Calendar) EAny.ezeCast(value, "asTimestamp", ETimestamp.class, new Class[] { Integer[].class }, args);
	}

	public static boolean ezeIsa(Object value, Integer... args) {
		boolean isa = (value instanceof ETimestamp && ((ETimestamp) value).ezeUnbox() != null);
		if (isa) {
			if (args.length == 2)
				isa = ((ETimestamp) value).startCode == args[0] && ((ETimestamp) value).endCode == args[1];
		} else {
			isa = value instanceof Calendar;
		}
		return isa;
	}

	public String toString() {
		return EString.asString(EString.asStringTimestamp(object, startCode, endCode));
	}

	public static Calendar asTimestamp(EDate timestamp) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox());
	}

	public static Calendar asTimestamp(ETime timestamp) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox());
	}

	public static Calendar asTimestamp(ETimestamp timestamp) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox());
	}

	public static Calendar asTimestamp(Calendar timestamp) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp, DefaultPattern);
	}

	public static Calendar asTimestamp(EDate timestamp, String timespanMask) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox(), timespanMask);
	}

	public static Calendar asTimestamp(ETime timestamp, String timespanMask) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox(), timespanMask);
	}

	public static Calendar asTimestamp(ETimestamp timestamp, String timespanMask) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox(), timespanMask);
	}

	public static Calendar asTimestamp(Calendar original, String timespanMask) throws AnyException {
		if (original == null)
			return null;
		Calendar timestamp = (Calendar) original.clone();
		if (timespanMask == null || timespanMask.length() == 0)
			timespanMask = DefaultPattern;
		// Default values in case the pattern doesn't specify things.
		int startCode = YEAR_CODE;
		int endCode = SECOND_CODE;
		TimestampIntervalMask mask = new TimestampIntervalMask(timespanMask);
		if (mask.getStartCode() != -1 && mask.getStartCode() <= mask.getEndCode()) {
			startCode = mask.getStartCode();
			endCode = mask.getEndCode();
		}
		return asTimestamp(timestamp, startCode, endCode);
	}

	public static Calendar asTimestamp(EString timestamp, Integer... args) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox(), args);
	}

	public static Calendar asTimestamp(String timestamp, Integer... args) throws AnyException {
		if (timestamp == null)
			return null;
		if (args != null && args.length == 2)
			return convert(timestamp, args[0], args[1]);
		else
			return asTimestamp(timestamp, DefaultPattern);
	}

	public static Calendar asTimestamp(EString timestamp, String timespanMask) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox(), timespanMask);
	}

	public static Calendar asTimestamp(String timestamp, String timespanMask) throws AnyException {
		if (timestamp == null || timespanMask == null)
			return null;
		// Default values in case the pattern doesn't specify things.
		int startCode = YEAR_CODE;
		int endCode = SECOND_CODE;
		TimestampIntervalMask mask = new TimestampIntervalMask(timespanMask);
		if (mask.getStartCode() != -1 && mask.getStartCode() <= mask.getEndCode()) {
			startCode = mask.getStartCode();
			endCode = mask.getEndCode();
		}
		return convert(timestamp, startCode, endCode);
	}

	public static Calendar asTimestamp(EDate timestamp, Integer... args) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox(), args);
	}

	public static Calendar asTimestamp(ETime timestamp, Integer... args) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox(), args);
	}

	public static Calendar asTimestamp(ETimestamp timestamp, Integer... args) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox(), args);
	}

	public static Calendar asTimestamp(Calendar date, Integer... args) {
		if (date == null)
			return null;
		if (args == null || args.length < 2)
			return asTimestamp((Calendar) date, YEAR_CODE, SECOND_CODE);
		else
			return asTimestamp((Calendar) date, args[0], args[1]);
	}

	public static Calendar asTimestamp(EDate timestamp, int startCode, int endCode) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox(), startCode, endCode);
	}

	public static Calendar asTimestamp(ETime timestamp, int startCode, int endCode) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox(), startCode, endCode);
	}

	public static Calendar asTimestamp(ETimestamp timestamp, int startCode, int endCode) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox(), startCode, endCode);
	}

	public static Calendar asTimestamp(Calendar original, int startCode, int endCode) {
		if (original == null)
			return null;
		Calendar cal = (Calendar) original.clone();
		cal.setLenient(true);
		Calendar result = DateTimeUtil.getBaseCalendar();
		// Get values for the full set of fields. Fields that we need will be
		// set from the calendar. The others will be set to reasonable defaults.
		if (startCode <= YEAR_CODE && endCode >= YEAR_CODE) {
			result.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		}
		if (startCode <= MONTH_CODE && endCode >= MONTH_CODE) {
			result.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		}
		if (startCode <= DAY_CODE && endCode >= DAY_CODE) {
			result.set(Calendar.DATE, cal.get(Calendar.DATE));
		}
		if (startCode <= HOUR_CODE && endCode >= HOUR_CODE) {
			result.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
		}
		if (startCode <= MINUTE_CODE && endCode >= MINUTE_CODE) {
			result.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
		}
		if (startCode <= SECOND_CODE && endCode >= SECOND_CODE) {
			result.set(Calendar.SECOND, cal.get(Calendar.SECOND));
		}
		if (startCode <= FRACTION6_CODE && endCode >= FRACTION1_CODE) {
			result.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));
		}
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
	public static Calendar asTimestamp(EString timestamp, String format, int startCode, int endCode) throws AnyException {
		if (timestamp == null)
			return null;
		return asTimestamp(timestamp.ezeUnbox(), format, startCode, endCode);
	}

	public static Calendar asTimestamp(String timestamp, String format, int startCode, int endCode) throws AnyException {
		if (timestamp == null)
			return null;
		Calendar result;
		timestamp = timestamp.trim();
		try {
			result = convert(timestamp, format);
		}
		catch (ParseException pe) {
			// OK. Do it ourselves
			result = convert(timestamp, startCode, endCode);
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
		// Use compareTo() because equals() will consider things other than the
		// time of the Calendars.
		return op1.compareTo(op2) == 0; 
	}

	public static boolean notEquals(Calendar op1, Calendar op2) {
		return !equals(op1, op2);
	}

	/**
	 * {@Operation narrow} Converts a string to a timestamp.  The string is parsed
	 * by searching for the timestamp fields specified in the mask, in order
	 * from years down to fractions of seconds.  Each field from the mask must
	 * be present in the string.  Years must be represented with four digits, and
	 * two digits must be used for months, days, hours, minutes, and seconds.  If
	 * the mask includes fractions, there must be one digit in the string for each
	 * fraction specified by the mask.  One separator character must appear in between
	 * each field.  Any character may be used as a separator, and the separators do 
	 * not have to match.
	 *
	 * @throws TypeCastException if the string can't be parsed into a timestamp or the mask is invalid.
	 */
	public static Calendar convert( String timestamp, int startCode, int endCode ) 
	{
		int years = -1;
		int months = -1;
		int days = -1;
		int hours = -1;
		int minutes = -1;
		int seconds = -1;
		int milliseconds = -1; // Calendar supports milliseconds (3 fraction digits), not microseconds (6 fraction digits).
		int length = timestamp.length();
		
		// Calculate the required length of the string.
		int requiredFields = ((endCode > FRACTION1_CODE) ? FRACTION1_CODE : endCode) - startCode + 1;
		int requiredLength = 
				requiredFields * 2    // start by counting two characters per field, adjust below
				+ requiredFields - 1; // the number of separator characters
		if ( startCode == YEAR_CODE )
		{
			requiredLength += 2;
		}
		if ( endCode == FRACTION1_CODE )
		{
			requiredLength--;
		}
		else if ( endCode >= FRACTION3_CODE )
		{
			requiredLength += endCode - FRACTION3_CODE + 1;
		}
		
		if ( length == requiredLength )
		{
			// This loop will process one field at a time.  If an invalid character
			// is found, we exit the loop.
			int tempMillis = 0;
			for ( int i = 0, code = startCode; i < length; code++ )
			{
				char digit1 = timestamp.charAt( i );
				char digit2 = code < FRACTION1_CODE ? timestamp.charAt( i + 1 ) : '0';
				if ( digit1 < '0' || digit1 > '9' || digit2 < '0' || digit2 > '9' )
				{
					break;
				}
				
				switch ( code )
				{
					case YEAR_CODE:
						char digit3 = timestamp.charAt( 2 );
						char digit4 = timestamp.charAt( 3 );
						if ( '0' <= digit3 && digit3 <= '9' && '0' <= digit4 && digit4 <= '9' )
						{
							years = (digit1 - '0') * 1000 + (digit2 - '0') * 100 + (digit3 - '0') * 10 + (digit4 - '0');
						}
						i += 5;
						break;
					case MONTH_CODE:
						months = (digit1 - '0') * 10 + (digit2 - '0');
						i += 3;
						break;
					case DAY_CODE:
						days = (digit1 - '0') * 10 + (digit2 - '0');
						i += 3;
						break;
					case HOUR_CODE:
						hours = (digit1 - '0') * 10 + (digit2 - '0');
						i += 3;
						break;
					case MINUTE_CODE:
						minutes = (digit1 - '0') * 10 + (digit2 - '0');
						i += 3;
						break;
					case SECOND_CODE:
						seconds = (digit1 - '0') * 10 + (digit2 - '0');
						i += 3;
						break;
					case FRACTION1_CODE:
						if ( code == endCode )
						{
							milliseconds = (digit1 - '0') * 100;
						}
						else
						{
							// We need to check more fraction digits.  Don't assign 
							// to milliseconds yet.
							tempMillis = (digit1 - '0') * 100;
						}
						i++;
						break;
					case FRACTION2_CODE:
						if ( code == endCode )
						{
							milliseconds = tempMillis + (digit1 - '0') * 10;
						}
						else
						{
							// We need to check more fraction digits.  Don't assign 
							// to milliseconds yet.
							tempMillis += (digit1 - '0') * 10;
						}
						i++;
						break;
					case FRACTION3_CODE:
						if ( code == endCode )
						{
							milliseconds = tempMillis + (digit1 - '0');
						}
						else
						{
							// We need to check more fraction digits.  Don't assign 
							// to milliseconds yet.
							tempMillis += (digit1 - '0');
						}
						i++;
						break;
					default:
						// Fraction digits 4 to 6.  Don't use the digit because a Calendar
						// can only store milliseconds, not microseconds.
						milliseconds = tempMillis;
						i++;
						break;
				}
			}
		}
		
		// Make sure all required fields were found.
		if ( (years == -1 && startCode == YEAR_CODE) || (months == -1 && startCode <= MONTH_CODE && endCode >= MONTH_CODE)
			|| (days == -1 && startCode <= DAY_CODE && endCode >= DAY_CODE) || (hours == -1 && startCode <= HOUR_CODE && endCode >= HOUR_CODE)
			|| (minutes == -1 && startCode <= MINUTE_CODE && endCode >= MINUTE_CODE) || (seconds == -1 && startCode <= SECOND_CODE && endCode >= SECOND_CODE)
			|| (milliseconds == -1 && endCode >= FRACTION1_CODE) )
		{
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "timestamp(\"" + createMask( startCode, endCode ) + "\")";
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, timestamp, tcx.actualTypeName, tcx.castToName );
		}
		
		// The last thing to do is put the values into a Calendar.
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
		if (milliseconds != -1)
			cal.set(Calendar.MILLISECOND, milliseconds);
		// Verify that the values are valid. We only do this if year month and date are at least there
		try {
			if (years != -1 && months != -1 && days != -1)
				cal.setTimeInMillis(cal.getTimeInMillis());
		}
		catch (Exception ex) {
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "timestamp(\"" + createMask( startCode, endCode ) + "\")";
			tcx.initCause( ex );
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, timestamp, tcx.actualTypeName, tcx.castToName );
		}
		return cal;
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
		int sindex = (startCode > FRACTION1_CODE) ? 6 : startCode;
		int eindex = (endCode > FRACTION1_CODE) ? 6 : endCode;
		StringBuilder patternString = new StringBuilder(26);
		for (int i = sindex, j = 0; i <= eindex; i++, j++) {
			if (j > 0)
				patternString.append(delimiters[i - 1]);
			patternString.append(code[i]);
		}
		return patternString.toString();
	}

	/**
	 * Create a Timestamp mask based on a startcode and endcode value
	 */
	public static String createMask(int startCode, int endCode) {
		if (startCode > endCode) {
			return null;
		}
		StringBuilder patternString = new StringBuilder(20);
		String[] code = { "yyyy", "MM", "dd", "HH", "mm", "ss" };
		int eindex = (endCode > SECOND_CODE) ? 5 : endCode;
		for (int i = startCode; i <= eindex; i++) {
			patternString.append(code[i]);
		}
		if ( endCode >= FRACTION1_CODE )
		{
			patternString.append( "ffffff".substring( 0, endCode - FRACTION1_CODE + 1 ) );
		}
		return patternString.toString();
	}

	public static Calendar defaultValue() {
		long now = java.lang.System.currentTimeMillis();
		Calendar cal = DateTimeUtil.getBaseCalendar();
		cal.setTimeInMillis(now);
		return cal;
	}

	public static Calendar defaultValue(int startCode, int endCode) {
		long now = java.lang.System.currentTimeMillis();
		Calendar cal = DateTimeUtil.getBaseCalendar();
		cal.setTimeInMillis(now);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int date = cal.get(Calendar.DATE);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int fraction = cal.get(Calendar.MILLISECOND);
		cal.clear();
		if (startCode <= YEAR_CODE && endCode >= YEAR_CODE)
			cal.set(Calendar.YEAR, year);
		if (startCode <= MONTH_CODE && endCode >= MONTH_CODE)
			cal.set(Calendar.MONTH, month);
		if (startCode <= DAY_CODE && endCode >= DAY_CODE)
			cal.set(Calendar.DATE, date);
		if (startCode <= HOUR_CODE && endCode >= HOUR_CODE)
			cal.set(Calendar.HOUR_OF_DAY, hour);
		if (startCode <= MINUTE_CODE && endCode >= MINUTE_CODE)
			cal.set(Calendar.MINUTE, minute);
		if (startCode <= SECOND_CODE && endCode >= SECOND_CODE)
			cal.set(Calendar.SECOND, second);
		if (startCode <= FRACTION6_CODE && endCode >= FRACTION1_CODE)
			cal.set(Calendar.MILLISECOND, fraction);
		// set the computed time is possible
		try {
			cal.setTimeInMillis(cal.getTimeInMillis());
		}
		catch (Exception ex) {}
		return asTimestamp(cal, startCode, endCode);
	}

	/**
	 * Returns the day of a timestamp
	 */
	public static int dayOf(ETimestamp original) throws AnyException {
		if (original.getStartCode() > DAY_CODE || original.getEndCode() < DAY_CODE) {
			InvalidArgumentException ex = new InvalidArgumentException();
			throw ex.fillInMessage(Message.NO_FIELD_IN_TIMESTAMP, "dayOf", "dd");
		}
		Calendar aTimestamp = original.ezeUnbox();
		return aTimestamp.get(Calendar.DATE);
	}

	/**
	 * Returns the month of a timestamp
	 */
	public static int monthOf(ETimestamp original) throws AnyException {
		if (original.getStartCode() > MONTH_CODE || original.getEndCode() < MONTH_CODE) {
			InvalidArgumentException ex = new InvalidArgumentException();
			throw ex.fillInMessage(Message.NO_FIELD_IN_TIMESTAMP, "monthOf", "MM");
		}
		Calendar aTimestamp = original.ezeUnbox();
		return aTimestamp.get(Calendar.MONTH) + 1;
	}

	/**
	 * Returns the year of a timestamp
	 */
	public static int yearOf(ETimestamp original) throws AnyException {
		if (original.getStartCode() > YEAR_CODE || original.getEndCode() < YEAR_CODE) {
			InvalidArgumentException ex = new InvalidArgumentException();
			throw ex.fillInMessage(Message.NO_FIELD_IN_TIMESTAMP, "yearOf", "yyyy");
		}
		Calendar aTimestamp = original.ezeUnbox();
		return aTimestamp.get(Calendar.YEAR);
	}

	/**
	 * Returns the weekday of a timestamp
	 */
	public static int weekdayOf(ETimestamp original) throws AnyException {
		if (original.getStartCode() > YEAR_CODE || original.getEndCode() < DAY_CODE) {
			InvalidArgumentException ex = new InvalidArgumentException();
			throw ex.fillInMessage(Message.NO_FIELD_IN_TIMESTAMP, "weekdayOf", "dd");
		}
		Calendar aTimestamp = original.ezeUnbox();
		return aTimestamp.get(Calendar.DAY_OF_WEEK) - 1;
	}

	/**
	 * Returns the date of a timestamp
	 */
	public static Calendar dateOf(ETimestamp aTimestamp) throws AnyException {
		if (aTimestamp.getStartCode() > YEAR_CODE || aTimestamp.getEndCode() < DAY_CODE) {
			InvalidArgumentException ex = new InvalidArgumentException();
			throw ex.fillInMessage(Message.NO_FIELD_IN_TIMESTAMP, "dateOf", "yyyyMMdd");
		}
		return asTimestamp(aTimestamp, YEAR_CODE, DAY_CODE);
	}

	/**
	 * Returns the time of a timestamp
	 */
	public static Calendar timeOf(ETimestamp aTimestamp) throws AnyException {
		if (aTimestamp.getStartCode() > HOUR_CODE || aTimestamp.getEndCode() < SECOND_CODE) {
			InvalidArgumentException ex = new InvalidArgumentException();
			throw ex.fillInMessage(Message.NO_FIELD_IN_TIMESTAMP, "timeOf", "HHmmss");
		}
		return asTimestamp(aTimestamp, HOUR_CODE, SECOND_CODE);
	}

	/**
	 * Returns the hour of a timestamp
	 */
	public static int hourOf(ETimestamp aTimestamp) throws AnyException {
		if (aTimestamp.getStartCode() > HOUR_CODE || aTimestamp.getEndCode() < HOUR_CODE) {
			InvalidArgumentException ex = new InvalidArgumentException();
			throw ex.fillInMessage(Message.NO_FIELD_IN_TIMESTAMP, "hourOf", "HH");
		}
		return aTimestamp.ezeUnbox().get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * Returns the minute of a timestamp
	 */
	public static int minuteOf(ETimestamp aTimestamp) throws AnyException {
		if (aTimestamp.getStartCode() > MINUTE_CODE || aTimestamp.getEndCode() < MINUTE_CODE) {
			InvalidArgumentException ex = new InvalidArgumentException();
			throw ex.fillInMessage(Message.NO_FIELD_IN_TIMESTAMP, "minuteOf", "mm");
		}
		return aTimestamp.ezeUnbox().get(Calendar.MINUTE);
	}

	/**
	 * Returns the second of a timestamp
	 */
	public static int secondOf(ETimestamp aTimestamp) throws AnyException {
		if (aTimestamp.getStartCode() > SECOND_CODE || aTimestamp.getEndCode() < SECOND_CODE) {
			InvalidArgumentException ex = new InvalidArgumentException();
			throw ex.fillInMessage(Message.NO_FIELD_IN_TIMESTAMP, "secondOf", "ss");
		}
		return aTimestamp.ezeUnbox().get(Calendar.SECOND);
	}

	/**
	 * Returns the extension of a timestamp
	 */
	public static Calendar extend(ETimestamp aTimestamp, String timeSpanPattern) throws AnyException {
		// Default values in case the pattern doesn't specify things.
		int startCode = YEAR_CODE;
		int endCode = SECOND_CODE;
		TimestampIntervalMask mask = new TimestampIntervalMask(timeSpanPattern);
		if (mask.getStartCode() != -1 && mask.getStartCode() <= mask.getEndCode()) {
			startCode = mask.getStartCode();
			endCode = mask.getEndCode();
		}
		return asTimestamp(aTimestamp, startCode, endCode);
	}
}
