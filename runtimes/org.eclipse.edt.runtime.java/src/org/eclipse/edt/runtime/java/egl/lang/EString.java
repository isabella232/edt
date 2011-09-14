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
import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.PatternSyntaxException;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.javart.util.JavartDateFormat;

import egl.lang.AnyException;
import egl.lang.InvalidIndexException;
import egl.lang.InvalidPatternException;
import egl.lang.NullValueException;
import egl.lang.TypeCastException;

public class EString extends AnyBoxedObject<String> {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	protected static final java.lang.String DefaultValue = "";

	private int maxLength;

	public int getLength() {
		return maxLength;
	}

	private EString(String value) {
		super(value);
		maxLength = -1;
	}

	private EString(String value, int length) {
		super(value);
		maxLength = length;
	}

	public static EString ezeBox(String value) {
		return new EString(value);
	}

	public static EString ezeBox(String value, int length) {
		return new EString(value, length);
	}

	public static String ezeCast(Object value, Integer... args) throws AnyException {
		if (value == null) {
			return "";
		}
		return (String) EglAny.ezeCast(value, "asString", EString.class, new Class[] { Integer[].class }, args);
	}

	public static boolean ezeIsa(Object value, Integer... length) {
		boolean isa = value instanceof EString;
		if (isa) {
			if (length.length != 0)
				isa = ((EString) value).getLength() == length[0];
		} else {
			isa = value instanceof String;
		}
		return isa;
	}

	public static String asString(Boolean value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(EBoolean value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value.ezeUnbox()), length);
	}

	public static String asString(Short value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(ESmallint value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value.ezeUnbox()), length);
	}

	public static String asString(Integer value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(EInt value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value.ezeUnbox()), length);
	}

	public static String asString(Long value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(EBigint value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value.ezeUnbox()), length);
	}

	public static String asString(Float value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(ESmallfloat value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value.ezeUnbox()), length);
	}

	public static String asString(Double value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(EFloat value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value.ezeUnbox()), length);
	}

	public static String asString(BigDecimal value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(EDecimal value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value.ezeUnbox()), length);
	}

	public static String asString(BigInteger value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(String value, Integer... length) {
		if (value == null)
			return null;
		if (length.length != 0 && value.length() > length[0])
			value = value.substring(0, length[0]);
		return value;
	}

	public static String asString(EString value, Integer... length) {
		if (value == null)
			return null;
		String val;
		if (length.length != 0 && value.ezeUnbox().length() > length[0])
			return value.ezeUnbox().substring(0, length[0]);
		else
			return value.ezeUnbox();
	}

	/**
	 * TODO This method gets called for two reasons: converting date to string and converting timestamp to string. But it
	 * can't do both properly, based on the specs below. The Calendar object from a date will look exactly like the Calendar
	 * object from a timestamp("yyyyMMdd"). Right now this method does the conversion from timestamp. {@Operation
	 *  widen} Converts a date to a string in the format "MM/dd/yyyy". Leading zeros are included in the string,
	 * so April 1st in the year 9 A.D. is converted to "04/01/0009". {@Operation widen} Converts a timestamp to a
	 * string. The 26-character result will include all possible fields of a timestamp, from years down to fractions of
	 * seconds, in the format "yyyy-MM-dd HH:mm:ss.SSSSSS". Leading zeros are included in each field of the string when
	 * necessary, e.g. January is represented as "01" not "1".
	 */
	public static String asString(EDate value, Integer... length) {
		if (value == null)
			return null;
		return asString(value.ezeUnbox(), length);
	}

	public static String asString(ETimestamp value, Integer... length) {
		if (value == null)
			return null;
		return asString(value.ezeUnbox(), length);
	}

	public static String asString(GregorianCalendar value, Integer... length) {
		if (value == null)
			return null;
		return asString((Calendar) value, length);
	}

	public static String asString(Calendar cal, Integer... length) {
		if (cal == null)
			return null;
		// Get the format pattern to use.
		String format = "";
		String separator = null;
		if (cal.isSet(Calendar.YEAR)) {
			format += "yyyy";
			separator = "-";
		}
		if (cal.isSet(Calendar.MONTH)) {
			if (separator != null) {
				format += separator;
			}
			format += "MM";
			separator = "-";
		}
		if (cal.isSet(Calendar.DATE)) {
			if (separator != null) {
				format += separator;
			}
			format += "dd";
			separator = " ";
		}
		if (cal.isSet(Calendar.HOUR_OF_DAY)) {
			if (separator != null) {
				format += separator;
			}
			format += "HH";
			separator = ":";
		}
		if (cal.isSet(Calendar.MINUTE)) {
			if (separator != null) {
				format += separator;
			}
			format += "mm";
			separator = ":";
		}
		if (cal.isSet(Calendar.SECOND)) {
			if (separator != null) {
				format += separator;
			}
			format += "ss";
			separator = ".";
		}
		if (cal.isSet(Calendar.MILLISECOND)) {
			if (separator != null) {
				format += separator;
			}
			format += "SSSSSS";
		}
		// Get a formatter for the value, set it up, and run it.
		boolean reset = false;
		synchronized (DateTimeUtil.LOCK) {
			JavartDateFormat formatter = DateTimeUtil.getDateFormat(format);
			if (cal.isSet(Calendar.SECOND) && cal.isSet(Calendar.MILLISECOND)) {
				int micros = cal.get(Calendar.MILLISECOND) * 1000;
				if (micros < 0) {
					reset = true;
					cal.add(Calendar.SECOND, -1);
					micros += DateTimeUtil.MICROSECONDS_PER_SECOND;
				}
				formatter.setMicrosecond(micros);
			}
			try {
				if (cal.isSet(Calendar.YEAR))
					formatter.setCentury(cal.get(Calendar.YEAR) / 100 + 1);
				else
					formatter.setCentury(1);
				return asString(formatter.format(cal.getTime()), length);
			}
			catch (IllegalArgumentException iax) {
				TypeCastException tcx = new TypeCastException();
				tcx.castToName = "string";
				tcx.actualTypeName = "date or timestamp"; // TODO see the TODO in the method comment: need to know if the
															// Calendar is a date or timestamp
				throw tcx;
			}
			finally {
				if (reset)
					cal.add(Calendar.SECOND, 1);
			}
		}
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static BigDecimal asNumber(String value, Integer... length) throws AnyException {
		if (value == null)
			return null;
		return EDecimal.asDecimal(asString(value, length));
	}

	public static BigDecimal asNumber(EString value, Integer... length) throws AnyException {
		if (value == null)
			return null;
		return EDecimal.asDecimal(asString(value.ezeUnbox(), length));
	}

	public static String plus(String op1, String op2) throws AnyException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return concat(op1, op2);
	}

	public static String concat(String op1, String op2) throws AnyException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return op1 + op2;
	}

	public static String concatNull(String op1, String op2) {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return op1 + op2;
	}

	public static boolean equals(String op1, String op2) throws AnyException {
		if (op1 == null || op2 == null)
			return false;
		return op1.equals(op2);
	}

	public static boolean notEquals(String op1, String op2) throws AnyException {
		if (op1 == null || op2 == null)
			return false;
		return !op1.equals(op2);
	}

	public static String substring(String str, int start, int end) throws AnyException {
		if (str == null)
			return null;
		int max = str.length();
		if (start < 1 || start > max) {
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = start;
			throw ex;
		} else if (end < start || end < 1 || end > max) {
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = end;
			throw ex;
		}
		return str.substring(start - 1, end);
	}

	/**
	 * Returns the length of the string or limited string
	 */
	public static int length(String source) {
		if (source == null)
			throw new NullValueException();
		return source.length();
	}

	/**
	 * Deletes trailing blank spaces and nulls from the start and end of strings.
	 */
	public static String trim(String source) {
		if (source == null)
			throw new NullValueException();
		return clip(clipLeading(source));
	}

	/**
	 * Deletes trailing blank spaces and nulls from the start of strings.
	 */
	public static String clipLeading(String value) {
		if (value == null)
			throw new NullValueException();
		int lth = value.length();
		if (lth > 0) {
			int startingIdx = 0;
			boolean charsDeleted = false;
			while (startingIdx < lth) {
				char c = value.charAt(startingIdx);
				if (c <= ' ' || c == '\u3000') {
					charsDeleted = true;
					startingIdx++;
				} else
					break;
			}
			if (charsDeleted)
				return value.substring(startingIdx, lth);
		}
		return value;
	}

	/**
	 * Deletes trailing blank spaces and nulls from the end of strings.
	 */
	public static String clip(String source) {
		if (source == null)
			throw new NullValueException();
		int lth = source.length();
		if (lth > 0) {
			int startingIdx = lth - 1;
			boolean charsDeleted = false;
			while (startingIdx >= 0) {
				char c = source.charAt(startingIdx);
				if (c <= ' ' || c == '\u3000') {
					charsDeleted = true;
					startingIdx--;
				} else
					break;
			}
			if (charsDeleted)
				return source.substring(0, startingIdx + 1);
		}
		return source;
	}

	/**
	 * returns the lowercase value of the string or limited string
	 */
	public static String toLowerCase(String source) {
		if (source == null)
			throw new NullValueException();
		return source.toLowerCase();
	}

	/**
	 * returns the uppercase value of the string or limited string
	 */
	public static String toUpperCase(String source) {
		if (source == null)
			throw new NullValueException();
		return source.toUpperCase();
	}

	/**
	 * returns whether the string starts with a specific string
	 */
	public static boolean startsWith(String source, String value) {
		if (source == null || value == null)
			throw new NullValueException();
		return source.startsWith(value);
	}

	/**
	 * returns whether the string ends with a specific string
	 */
	public static boolean endsWith(String source, String value) {
		if (source == null || value == null)
			throw new NullValueException();
		return source.endsWith(value);
	}

	/**
	 * returns the position of a specific string within a string
	 */
	public static int indexOf(String source, String value) {
		if (source == null || value == null)
			throw new NullValueException();
		return source.indexOf(value) + 1;
	}

	/**
	 * returns the position of a specific string within a string
	 */
	public static int indexOf(String source, String value, int start) {
		if (source == null || value == null)
			throw new NullValueException();
		return source.indexOf(value, start - 1) + 1;
	}

	/**
	 * returns the last position of a specific string within a string
	 */
	public static int lastIndexOf(String source, String value) {
		if (source == null || value == null)
			throw new NullValueException();
		return source.lastIndexOf(value) + 1;
	}

	/**
	 * replaces the first occurrence of a string in another string
	 */
	public static String replaceStr(String source, String search, String replacement) {
		if (source == null || search == null || replacement == null)
			throw new NullValueException();
		return source.replace(search, replacement);
	}

	/**
	 * Returns the integer value of a character code within a string
	 */
	public static int charCodeAt(String source, int index) {
		if (source == null)
			throw new NullValueException();
		return source.charAt(index - 1);
	}

	/**
	 * Returns whether the string is like another
	 */
	public static boolean isLike(String source, String pattern) {
		return isLike(source, pattern, "\\");
	}

	/**
	 * Returns whether the string is like another
	 */
	public static boolean isLike(String source, String pattern, String escape) {
		if (source == null)
			throw new NullValueException();
		if (pattern == null || escape == null)
			return false;
		// Get the escape character.
		char escapeChar = escape.length() > 0 ? escape.charAt(0) : '\\';
		// The strategy here is to convert the pattern to java regex, escaping
		// special chars we want to treat as literals; and then evaluate with
		// the regex class
		String escapeThese = "$[]^{}+|?().*";
		StringBuilder regex = new StringBuilder();
		// Ignore trailing blanks in both operands.
		source = clip(source);
		pattern = clip(pattern);
		for (int i = 0; i < pattern.length(); i++) {
			char ch = pattern.charAt(i);
			if (ch == '\\' && escapeChar != '\\') {
				// If the character is '\\' and if that is not an escape char,
				// escape it so that it is treated as a literal character
				regex.append("\\\\");
			} else if (ch == escapeChar) {
				char nch = pattern.charAt(i + 1);
				if (nch == '_' || nch == '%') {
					regex.append(nch);
				} else if (nch == escapeChar) {
					if (escapeChar == '\\' || escapeThese.indexOf(escapeChar) != -1) {
						regex.append('\\');
					}
					regex.append(escapeChar);
				} else if (escapeChar == '\\') {
					// Must be a special java character.
					regex.append("\\" + nch);
				} else {
					// There was no need for use of the escape character, omit it.
					regex.append(nch);
				}
				i++;
			} else if (ch == '%') {
				regex.append(".*");
			} else if (ch == '_') {
				regex.append('.');
			} else if (escapeThese.indexOf(ch) != -1) {
				// Escape any characters which java-regex will interpret, that
				// we want to be treated as normal.
				regex.append("\\" + ch);
			} else {
				regex.append(ch);
			}
		}
		try {
			return source.matches(regex.toString());
		}
		catch (PatternSyntaxException e) {
			throw new InvalidPatternException();
		}
	}

	/**
	 * Returns whether the string matches another
	 */
	public static boolean matchesPattern(String source, String pattern) {
		return matchesPattern(source, pattern, "\\");
	}

	/**
	 * Returns whether the string matches another
	 */
	public static boolean matchesPattern(String source, String pattern, String escape) {
		if (source == null)
			throw new NullValueException();
		if (pattern == null || escape == null)
			return false;
		// Get the escape character.
		char escapeChar = escape.length() > 0 ? escape.charAt(0) : '\\';
		// The strategy here is to convert the pattern to java regex, escaping
		// special chars we want to treat as literals; and then evaluate with
		// the regex class
		String escapeThese = "${}+|&().";
		StringBuilder regex = new StringBuilder();
		boolean withinBrackets = false;
		for (int i = 0; i < pattern.length(); i++) {
			char ch = pattern.charAt(i);
			if (ch == '\\' && escapeChar != '\\') {
				// If the character is '\\' and if that is not an escape char,
				// escape it so that it is treated as a literal character
				regex.append("\\\\");
			} else if (ch == escapeChar) {
				char nch = pattern.charAt(i + 1);
				if (nch == '*' || nch == '?' || nch == '[' || nch == ']' || nch == '^') {
					regex.append("\\" + nch);
				} else if (nch == escapeChar) {
					if (escapeChar == '\\' || escapeThese.indexOf(escapeChar) != -1) {
						regex.append('\\');
					}
					regex.append(escapeChar);
				} else if (escapeChar == '\\') {
					// Must be a special java character.
					regex.append("\\" + nch);
				} else {
					// There was no need for use of the escape character, omit
					// it.
					regex.append(nch);
				}
				i++;
			} else if (ch == '[') {
				withinBrackets = true;
				regex.append(ch);
			} else if (ch == ']') {
				withinBrackets = false;
				regex.append(ch);
			} else if (ch == '*' && !withinBrackets) {
				regex.append(".*");
			} else if (ch == '?' && !withinBrackets) {
				regex.append('.');
			} else if (ch == '^' && !withinBrackets) {
				regex.append("\\^");
			} else if (escapeThese.indexOf(ch) != -1) {
				// Escape any characters which java-regex will interpret, that
				// we want to be treated as normal.
				regex.append("\\" + ch);
			} else {
				regex.append(ch);
			}
		}
		// If within brackets, then we have an unclosed character class which
		// is illegal syntax in java regex. Escape the last '[' and a '^' if it
		// follows, to avoid a PatternSyntaxException
		if (withinBrackets) {
			int pos = regex.lastIndexOf("[");
			regex.insert(pos, '\\');

			// Avoid InvalidIndexException
			if (pos + 2 < regex.length() && regex.charAt(pos + 2) == '^') {
				regex.insert(pos + 2, '\\');
			}
		}
		try {
			return source.matches(regex.toString());
		}
		catch (PatternSyntaxException e) {
			throw new InvalidPatternException();
		}
	}

	public String toString() {
		if (maxLength > 0)
			return asString(object, maxLength);
		else
			return object;
	}
}
