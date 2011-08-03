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
import java.util.Calendar;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.javart.util.JavartDateFormat;

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

	public static String ezeCast(Object value, Integer... args) throws JavartException {
		return (String) EglAny.ezeCast(value, "asString", EString.class, new Class[] { Integer[].class }, args);
	}

	public static boolean ezeIsa(Object value, Integer... length) {
		boolean isa = value instanceof EString;
		if (isa && length.length != 0)
			isa = ((EString) value).getLength() == length[0];
		return isa;
	}

	public static String asString(Boolean value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(Short value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(Integer value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(Long value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(Float value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(Double value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(BigDecimal value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(String value, Integer... length) {
		if (length.length != 0 && value.length() > length[0])
			value = value.substring(0, length[0]);
		return value;
	}

	public static String asString(Calendar value, Integer... length) {
		// Get the format pattern to use.
		// String format = program._runUnit().getDefaultTimestampFormat();
		// if ( format.length() == 0 )
		// {
		// // Use the one made specially for this item.
		// format = DefaultFormatPattern;
		// }
		String format = ETimestamp.DefaultFormatPattern;
		// Get a formatter for the value, set it up, and run it.
		boolean reset = false;
		synchronized (DateTimeUtil.LOCK) {
			JavartDateFormat formatter = DateTimeUtil.getDateFormat(format);
			int micros = value.get(Calendar.MILLISECOND) * 1000;
			if (micros < 0) {
				reset = true;
				value.add(Calendar.SECOND, -1);
				micros += DateTimeUtil.MICROSECONDS_PER_SECOND;
			}
			formatter.setCentury(value.get(Calendar.YEAR) / 100 + 1);
			formatter.setMicrosecond(micros);
			try {
				return asString(formatter.format(value.getTime()), length);
			}
			catch (IllegalArgumentException iax) {
				throw new TimestampFormatException();
			}
			finally {
				if (reset)
					value.add(Calendar.SECOND, 1);
			}
		}
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static BigDecimal asNumber(String value, Integer... length) throws JavartException {
		if (value == null)
			return null;
		return EDecimal.asDecimal(asString(value, length));
	}

	public static String plus(String op1, String op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return op1 + op2;
	}

	public static String concat(String op1, String op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return op1 + op2;
	}

	public static String concatNull(String op1, String op2) {
		if (op1 == null || op2 == null)
			return null;
		return op1 + op2;
	}

	public static boolean equals(String op1, String op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return op1.equals(op2);
	}

	public static boolean notEquals(String op1, String op2) throws JavartException {
		return !equals(op1, op2);
	}

	public static String substring(String str, Integer startIndex, Integer endIndex) throws JavartException {
		if (str == null || startIndex == null || endIndex == null)
			throw new NullValueException();
		int start = startIndex;
		int end = endIndex;
		int max = str.length();
		if (start < 1 || start > max) {
			IndexOutOfBoundsException ex = new IndexOutOfBoundsException();
			ex.indexValue = start;
			throw ex;
		} else if (end < start || end < 1 || end > max) {
			IndexOutOfBoundsException ex = new IndexOutOfBoundsException();
			ex.indexValue = end;
			throw ex;
		}
		return str.substring(start - 1, end);
	}

	/**
	 * Returns the length of the string or limited string
	 */
	public static Integer length(String source, Integer... length) {
		return asString(source, length).length();
	}

	/**
	 * Deletes trailing blank spaces and nulls from the start and end of strings.
	 */
	public static String trim(String source, Integer... length) {
		return clip(clipLeading(asString(source, length)));
	}

	/**
	 * Deletes trailing blank spaces and nulls from the start of strings.
	 */
	public static String clipLeading(String source, Integer... length) {
		String value = asString(source, length);
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
	public static String clip(String source, Integer... length) {
		String value = asString(source, length);
		int lth = value.length();
		if (lth > 0) {
			int startingIdx = lth - 1;
			boolean charsDeleted = false;
			while (startingIdx >= 0) {
				char c = value.charAt(startingIdx);
				if (c <= ' ' || c == '\u3000') {
					charsDeleted = true;
					startingIdx--;
				} else
					break;
			}
			if (charsDeleted)
				return value.substring(0, startingIdx + 1);
		}
		return value;
	}

	/**
	 * returns the lowercase value of the string or limited string
	 */
	public static String toLowerCase(String source, Integer... length) {
		return asString(source, length).toLowerCase();
	}

	/**
	 * returns the uppercase value of the string or limited string
	 */
	public static String toUpperCase(String source, Integer... length) {
		return asString(source, length).toUpperCase();
	}

	/**
	 * returns whether the string starts with a specific string
	 */
	public static Boolean startsWith(String source, String value) {
		return source.startsWith(value);
	}

	public static Boolean startsWith(String source, Integer length, String value) {
		return asString(source, length).startsWith(value);
	}

	/**
	 * returns whether the string ends with a specific string
	 */
	public static Boolean endsWith(String source, String value) {
		return source.endsWith(value);
	}

	public static Boolean endsWith(String source, Integer length, String value) {
		return asString(source, length).endsWith(value);
	}

	/**
	 * returns the position of a specific string within a string
	 */
	public static Integer indexOf(String source, String value) {
		return source.indexOf(value);
	}

	public static Integer indexOf(String source, Integer length, String value) {
		return asString(source, length).indexOf(value);
	}

	/**
	 * returns the position of a specific string within a string
	 */
	public static Integer indexOf(String source, String value, Integer start) {
		return source.indexOf(value, start);
	}

	public static Integer indexOf(String source, Integer length, String value, Integer start) {
		return asString(source, length).indexOf(value, start);
	}

	/**
	 * returns the last position of a specific string within a string
	 */
	public static Integer lastIndexOf(String source, String value) {
		return source.lastIndexOf(value);
	}

	public static Integer lastIndexOf(String source, Integer length, String value) {
		return asString(source, length).lastIndexOf(value);
	}

	/**
	 * replaces the first occurrence of a string in another string
	 */
	public static String replaceStr(String source, String search, String replacement) {
		return source.replace(search, replacement);
	}

	public static String replaceStr(String source, Integer length, String search, String replacement) {
		return asString(source, length).replace(search, replacement);
	}

	/**
	 * Returns the integer value of a character code within a string
	 */
	public static Integer charCodeAt(String source, Integer index) {
		return (int) source.charAt(index);
	}

	public static Integer charCodeAt(String source, Integer length, Integer index) {
		return (int) asString(source, length).charAt(index);
	}

	/**
	 * Returns whether the string is like another
	 */
	public static Boolean isLike(String source, String value) {
		// return isLike(source, value);
		return false;
	}

	public static Boolean isLike(String source, Integer length, String value) {
		return isLike(asString(source, length), value);
	}

	/**
	 * Returns whether the string is like another
	 */
	public static Boolean matchesPattern(String source, String value) {
		// return matchesPattern(source, value);
		return false;
	}

	public static Boolean matchesPattern(String source, Integer length, String value) {
		return matchesPattern(asString(source, length), value);
	}
}
