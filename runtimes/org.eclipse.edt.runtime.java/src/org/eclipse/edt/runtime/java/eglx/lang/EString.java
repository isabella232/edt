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

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.javart.util.JavartDateFormat;

import eglx.lang.*;

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
		return (String) EAny.ezeCast(value, "asString", EString.class, new Class[] { Integer[].class }, args);
	}

	public static boolean ezeIsa(Object value, Integer... length) {
		boolean isa = (value instanceof EString && ((EString) value).ezeUnbox() != null);
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

	public static String asString(EBytes value, Integer... length) {
		if (value == null)
			return null;
		return asString(value.ezeUnbox(), length);
	}

	public static String asString(byte[] value, Integer... length) {
		if (value == null)
			return null;
		StringBuilder str = new StringBuilder();
		str.append( "0x" );
		for ( int i = 0; i < value.length; i++ )
		{
			int bits = value[ i ] & 0xFF;
			if ( bits < 16 )
			{
				str.append( '0' );
				str.append( Integer.toHexString( bits ) );
			}
			else
			{
				str.append( Integer.toHexString( bits ) );
			}
		}
		return str.toString();
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

	public static String asString(Number value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value), length);
	}

	public static String asString(eglx.lang.ENumber value, Integer... length) {
		if (value == null)
			return null;
		return asString(String.valueOf(value.ezeUnbox()), length);
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
		if (length.length != 0 && value.ezeUnbox().length() > length[0])
			return value.ezeUnbox().substring(0, length[0]);
		else
			return value.ezeUnbox();
	}

	public static String asString(EDate value, Integer... length) {
		if (value == null)
			return null;
		return asString(asStringDate(value.ezeUnbox()), length);
	}

	protected static String asStringDate(Calendar original) {
		if (original == null)
			return null;
		Calendar cal = (Calendar) original.clone();
		// Get the format pattern to use.
		String format = "MM/dd/yyyy";
		// Get a formatter for the value, set it up, and run it.
		synchronized (DateTimeUtil.LOCK) {
			JavartDateFormat formatter = DateTimeUtil.getDateFormat(format);
			try {
				cal.setLenient(true);
				return formatter.format(cal.getTime());
			}
			catch (IllegalArgumentException iax) {
				TypeCastException tcx = new TypeCastException();
				tcx.castToName = "string";
				tcx.actualTypeName = "date";
				tcx.initCause( iax );
				throw tcx.fillInMessage( Message.CONVERSION_ERROR, cal.getTime(), tcx.actualTypeName, tcx.castToName );
			}
		}
	}

	public static String asString(ETime value, Integer... length) {
		if (value == null)
			return null;
		return asString(asStringTime(value.ezeUnbox()), length);
	}

	protected static String asStringTime(Calendar original) {
		if (original == null)
			return null;
		Calendar cal = (Calendar) original.clone();
		// Get the format pattern to use.
		String format = "HH:mm:ss";
		// Get a formatter for the value, set it up, and run it.
		synchronized (DateTimeUtil.LOCK) {
			JavartDateFormat formatter = DateTimeUtil.getDateFormat(format);
			try {
				cal.setLenient(true);
				return formatter.format(cal.getTime());
			}
			catch (IllegalArgumentException iax) {
				TypeCastException tcx = new TypeCastException();
				tcx.castToName = "string";
				tcx.actualTypeName = "time";
				tcx.initCause( iax );
				throw tcx.fillInMessage( Message.CONVERSION_ERROR, cal.getTime(), tcx.actualTypeName, tcx.castToName );
			}
		}
	}

	public static String asString(ETimestamp value, Integer... length) {
		if (value == null)
			return null;
		return asString(asStringTimestamp(value.ezeUnbox(), value.getStartCode(), value.getEndCode()), length);
	}

	protected static String asStringTimestamp(Calendar original, int startCode, int endCode) {
		if (original == null)
			return null;
		Calendar cal = (Calendar) original.clone();
		// Get the format pattern to use.
		StringBuilder format = new StringBuilder(26);
		String separator = null;
		if (startCode <= ETimestamp.YEAR_CODE && endCode >= ETimestamp.YEAR_CODE) {
			format.append( "yyyy" );
			separator = "-";
		}
		if (startCode <= ETimestamp.MONTH_CODE && endCode >= ETimestamp.MONTH_CODE) {
			if (separator != null) {
				format.append( separator );
			}
			format.append( "MM" );
			separator = "-";
		}
		if (startCode <= ETimestamp.DAY_CODE && endCode >= ETimestamp.DAY_CODE) {
			if (separator != null) {
				format.append( separator );
			}
			format.append( "dd" );
			separator = " ";
		}
		if (startCode <= ETimestamp.HOUR_CODE && endCode >= ETimestamp.HOUR_CODE) {
			if (separator != null) {
				format.append( separator );
			}
			format.append( "HH" );
			separator = ":";
		}
		if (startCode <= ETimestamp.MINUTE_CODE && endCode >= ETimestamp.MINUTE_CODE) {
			if (separator != null) {
				format.append( separator );
			}
			format.append( "mm" );
			separator = ":";
		}
		if (startCode <= ETimestamp.SECOND_CODE && endCode >= ETimestamp.SECOND_CODE) {
			if (separator != null) {
				format.append( separator );
			}
			format.append( "ss" );
			separator = ".";
		}
		if (startCode <= ETimestamp.FRACTION6_CODE && endCode >= ETimestamp.FRACTION1_CODE) {
			if (separator != null) {
				format.append( separator );
			}
			format.append( "SSSSSS".substring( 0, endCode - ETimestamp.FRACTION1_CODE + 1 ) );
		}
		// Get a formatter for the value, set it up, and run it.
		synchronized (DateTimeUtil.LOCK) {
			JavartDateFormat formatter = DateTimeUtil.getDateFormat(format.toString());
			if (startCode <= ETimestamp.SECOND_CODE && endCode >= ETimestamp.FRACTION1_CODE) {
				int micros = cal.get(Calendar.MILLISECOND) * 1000;
				if (micros < 0) {
					cal.add(Calendar.SECOND, -1);
					micros += DateTimeUtil.MICROSECONDS_PER_SECOND;
				}
				formatter.setMicrosecond(micros);
			}
			try {
				cal.setLenient(true);
				return formatter.format(cal.getTime());
			}
			catch (IllegalArgumentException iax) {
				TypeCastException tcx = new TypeCastException();
				tcx.castToName = "string";
				tcx.actualTypeName = "timestamp(\"" + ETimestamp.createMask( startCode, endCode ) + "\")";
				tcx.initCause( iax );
				throw tcx.fillInMessage( Message.CONVERSION_ERROR, cal.getTime(), tcx.actualTypeName, tcx.castToName );
			}
		}
	}

	public static String getDefaultEncoding() {
		return System.getProperty("file.encoding");
	}

	public static String fromBytes(EBytes value) {
		return fromBytes(value.ezeUnbox());
	}

	public static String fromBytes(byte[] value) {
		return new String( value );
	}

	public static String fromBytes(EBytes value, String encoding) {
		return fromBytes(value.ezeUnbox(), encoding);
	}

	public static String fromBytes(byte[] value, String encoding) {
		try {
			return new String(value, encoding);
		}
		catch (UnsupportedEncodingException e) {
			InvalidArgumentException ex = new InvalidArgumentException();
			ex.initCause( e );
			throw ex.fillInMessage( Message.CONVERSION_ERROR, encoding );
		}
	}

	public static byte[] toBytes(String value) {
		if (value == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		return value.getBytes();
	}

	public static byte[] toBytes(String value, String encoding) {
		if (value == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		try {
			return value.getBytes(encoding);
		}
		catch (UnsupportedEncodingException e) {
			InvalidArgumentException ex = new InvalidArgumentException();
			ex.initCause( e );
			throw ex.fillInMessage( Message.CONVERSION_ERROR, encoding );
		}
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static EString asNumber(String value, Integer... length) throws AnyException {
		if (value == null)
			return null;
		return EString.ezeBox(asString(value, length));
	}

	public static EString asNumber(EString value, Integer... length) throws AnyException {
		if (value == null)
			return null;
		return value;
	}

	public static String plus(String op1, String op2) throws AnyException {
		return concat(op1, op2);
	}

	public static String concat(String op1, String op2) throws AnyException {
		if (op1 == null)
			op1 = "";
		if (op2 == null)
			op2 = "";
		return op1 + op2;
	}

	public static String concatNull(String op1, String op2) {
		if (op1 == null || op2 == null)
			return null;
		return op1 + op2;
	}

	public static int compareTo(String op1, String op2) throws AnyException {
		return op1.compareTo(op2);
	}

	public static boolean equals(String op1, String op2) throws AnyException {
		if (op1 == op2)
			return true;
		if (op1 == null || op2 == null)
			return false;
		return op1.equals(op2);
	}

	public static boolean notEquals(String op1, String op2) throws AnyException {
		return !equals(op1, op2);
	}

	public static String substring(String str, int start, int end) throws AnyException {
		if (str == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		int max = str.length();
		if (start < 1 || start > max) {
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = start;
			throw ex.fillInMessage( Message.INVALID_SUBSTRING_INDEX, start, end );
		} else if (end < start || end < 1 || end > max) {
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = end;
			throw ex.fillInMessage( Message.INVALID_SUBSTRING_INDEX, start, end );
		}
		return str.substring(start - 1, end);
	}

	/**
	 * Returns the length of the string or limited string
	 */
	public static int length(String source) {
		if (source == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		return source.length();
	}

	/**
	 * Deletes trailing blank spaces and nulls from the start and end of strings.
	 */
	public static String trim(String source) {
		return clip(clipLeading(source));
	}

	/**
	 * Deletes trailing blank spaces and nulls from the start of strings.
	 */
	public static String clipLeading(String value) {
		if (value == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
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
		if (source == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
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
		if (source == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		return source.toLowerCase();
	}

	/**
	 * returns the uppercase value of the string or limited string
	 */
	public static String toUpperCase(String source) {
		if (source == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		return source.toUpperCase();
	}

	/**
	 * returns whether the string starts with a specific string
	 */
	public static boolean startsWith(String source, String value) {
		if (source == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		return source.startsWith(value);
	}

	/**
	 * returns whether the string ends with a specific string
	 */
	public static boolean endsWith(String source, String value) {
		if (source == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		return source.endsWith(value);
	}

	/**
	 * returns the position of a specific string within a string
	 */
	public static int indexOf(String source, String value) {
		if (source == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		return source.indexOf(value) + 1;
	}

	/**
	 * returns the position of a specific string within a string
	 */
	public static int indexOf(String source, String value, int start) {
		if (source == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		if (start < 1 || start > source.length()) {
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = start;
			throw ex.fillInMessage( Message.INDEX_OUT_OF_BOUNDS, start );
		}
		return source.indexOf(value, start - 1) + 1;
	}

	/**
	 * returns the last position of a specific string within a string
	 */
	public static int lastIndexOf(String source, String value) {
		if (source == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		return source.lastIndexOf(value) + 1;
	}

	/**
	 * replaces the first occurrence of a string in another string
	 */
	public static String replaceStr(String source, String search, String replacement) {
		if (source == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		return source.replace(search, replacement);
	}

	/**
	 * Returns the integer value of a character code within a string
	 */
	public static int charCodeAt(String source, int index) {
		if (source == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		if (index < 1 || index > source.length()) {
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = index;
			throw ex.fillInMessage( Message.INDEX_OUT_OF_BOUNDS, index );
		}
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
		if (source == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
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
				if (i + 1 >= pattern.length()) {
					InvalidPatternException ex = new InvalidPatternException();
					ex.pattern = pattern;
					throw ex.fillInMessage( Message.INVALID_MATCH_PATTERN, pattern );
				}
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
			InvalidPatternException ex = new InvalidPatternException();
			ex.pattern = pattern;
			ex.initCause( e );
			throw ex.fillInMessage( Message.INVALID_MATCH_PATTERN, pattern );
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
		if (source == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		
		String regex = patternToRegex(pattern, escape);
		try {
			return source.matches(regex);
		}
		catch (PatternSyntaxException e) {
			InvalidPatternException ex = new InvalidPatternException();
			ex.pattern = pattern;
			ex.initCause( e );
			throw ex.fillInMessage( Message.INVALID_MATCH_PATTERN, pattern );
		}
	}
	
	/**
	 * Makes a Java regular expression from an EGL pattern and escape char.
	 */
	private static String patternToRegex( String pattern, String escape )
	{
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
				if (i + 1 >= pattern.length()) {
					InvalidPatternException ex = new InvalidPatternException();
					ex.pattern = pattern;
					throw ex.fillInMessage( Message.INVALID_MATCH_PATTERN, pattern );
				}
				char nch = pattern.charAt(i + 1);
				switch ( nch )
				{
					case '*':
					case '?':
					case '[':
					case ']':
					case '^':
						regex.append( "\\" + nch );
						break;
						
					default:
						if ( nch == escapeChar )
						{
							if ( escapeChar == '\\' || escapeThese.indexOf( escapeChar ) != -1 )
							{
								regex.append( '\\' );
							}
							regex.append( escapeChar );
						}
						else if ( escapeChar == '\\' )
						{
							// Must be a special java character.
							regex.append( "\\" + nch );
						}
						else 
						{
							// There was no need for use of the escape character, omit it.
							regex.append( nch );
						}
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
		
		return regex.toString();
	}

	public String toString() {
		if (maxLength > 0)
			return asString(object, maxLength);
		else
			return object;
	}

	/**
	 * Creates a string from a list of strings, with a separator between them.
	 */
	public static String join( String separator, List<String> strings )
	{
		int size = strings.size();
		if ( size == 0 )
		{
			return "";
		}
		else if ( size == 1 )
		{
			return strings.get( 0 );
		}
		
		StringBuilder joined = new StringBuilder( strings.get( 0 ) );
		for ( int i = 1; i < size; i++ )
		{
			joined.append( separator );
			joined.append( strings.get( i ) );
		}
		return joined.toString();
	}

	/**
	 * Returns an array containing substrings of this string.  The 'separator' 
	 * parameter indicates where each substring ends.  If the separator is not 
	 * contained in this string, then the result contains only this string.
	 */
	public static List<String> split( String str, String separator )
	{
		if ( str == null )
		{
			throw new NullValueException().fillInMessage( Message.NULL_NOT_ALLOWED );
		}

		List<String> result = new ArrayList<String>();
		int i = str.indexOf( separator );
		if ( i == -1 )
		{
			result.add( str );
		}
		else
		{
			int start = 0;
			do
			{
				result.add( str.substring( start, i ) );
				start = i + separator.length();
				i = str.indexOf( separator, start );
			}
			while ( i != -1 );
			if ( start <= str.length() )
			{
				result.add( str.substring( start, str.length() ) );
			}
		}
		
		return result;
	}
	
	/**
	 * Same as the other version of split, except the result will not be larger 
	 * than the specified limit.  Once the separator has been found limit - 1 
	 * times, the remainder of the string is returned in the last element of the 
	 * array, even if it contains the separator.  Returns an empty array if limit 
	 * is less than one.
	 */
	public static List<String> split( String str, String separator, int limit )
	{
		if ( str == null )
		{
			throw new NullValueException().fillInMessage( Message.NULL_NOT_ALLOWED );
		}

		List<String> result = new ArrayList<String>();
		if ( limit >= 1  )
		{
			int i = str.indexOf( separator );
			if ( i == -1 )
			{
				result.add( str );
			}
			else
			{
				int count = 0;
				int start = 0;
				do
				{
					count++;
					result.add( str.substring( start, i ) );
					start = i + separator.length();
					i = str.indexOf( separator, start );
				}
				while ( i != -1 && count < limit );
				if ( count < limit )
				{
					result.add( str.substring( start, str.length() ) );
				}
			}
		}
		
		return result;
	}

	/**
	 * This is like split(string), except the returned substrings are separated by 
	 * characters that match the specified pattern (see the matchesPattern 
	 * function).
	 */
	public static List<String> splitOnPattern( String str, String pattern )
	{
		return splitOnPattern( str, pattern, "\\" );
	}
	
	/**
	 * This is the same as splitOnPattern(EString), but the additional parameter
	 * specifies the escape character used in the pattern.
	 */
	public static List<String> splitOnPattern( String str, String pattern, String escape )
	{
		if ( str == null )
		{
			throw new NullValueException().fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		
		String regex = patternToRegex( pattern, escape );
		try 
		{
			return Arrays.asList( str.split( regex, -1 ) );
		}
		catch ( PatternSyntaxException psx )
		{
			InvalidPatternException ex = new InvalidPatternException();
			ex.pattern = pattern;
			ex.initCause( psx );
			throw ex.fillInMessage( Message.INVALID_MATCH_PATTERN, pattern );
		}
	}
	
	/**
	 * Same as the other version of splitOnPattern, except the result will not be 
	 * larger than the specified limit.  Once the pattern has been matched limit - 
	 * 1 times, the remainder of the string is returned in the last element of the 
	 * array, even if part of it matches the pattern.  Returns an empty array if
	 * limit is less than one.
	 */
	public static List<String> splitOnPattern( String str, String pattern, int limit )
	{
		return splitOnPattern( str, pattern, "\\", limit );
	}
	
	/**
	 * This is the same as splitOnPattern(EString,EInt), but the additional parameter
	 * specifies the escape character used in the pattern.
	 */
	public static List<String> splitOnPattern( String str, String pattern, String escape, int limit )
	{
		if ( str == null )
		{
			throw new NullValueException().fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		
		if ( limit < 1  )
		{
			return new ArrayList<String>();
		}
		
		String regex = patternToRegex( pattern, escape );
		String[] parts = null;
		try 
		{
			// Use limit + 1 because Java's split returns the remainder of the
			// source string as the last element of the result array.
			parts = str.split( regex, limit + 1 );
		}
		catch ( PatternSyntaxException psx )
		{
			InvalidPatternException ex = new InvalidPatternException();
			ex.pattern = pattern;
			ex.initCause( psx );
			throw ex.fillInMessage( Message.INVALID_MATCH_PATTERN, pattern );
		}

		// We can't use Arrays.asList() here because you can't change the size
		// of its result.  Also, if we got limit + 1 elements from split(), ignore
		// the last one.
		List<String> result = new ArrayList<String>( limit );
		for ( int i = 0; i < parts.length && i < limit; i++ )
		{
			result.add( parts[ i ] );
		}
		return result;
	}
	
	/**
	 * Returns a reversed copy of this string.
	 */
	public static String reverse( String str )
	{
		if ( str == null )
		{
			throw new NullValueException().fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		return new StringBuilder( str ).reverse().toString();
	}
	
	/**
	 * Does a case-insensitive comparison of two strings.  Returns a negative 
	 * number, zero, or a positive number to indicate that this string is less 
	 * than, equal to, or greater than the other string.
	 */
	public static int compareIgnoreCase( String str1, String str2 )
	{
		if ( str1 == null )
		{
			throw new NullValueException().fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		return str1.compareToIgnoreCase( str2 );
	}
	
	/**
	 * Makes a new string containing the characters of this string, with a second 
	 * string inserted somewhere in the middle.
	 *   str1.insert(offset, str2) is equivalent to 
	 *   str1[1:offset] :: str2 :: str1[offset:str1.length()]
	 *
	 * @throws InvalidIndexException if offset is less than 1 or greater 
	 *    than the length of this string.
	 */
	public static String insertStr( String str1, int offset, String str2 )
	{
		if ( str1 == null )
		{
			throw new NullValueException().fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		if ( offset < 1 || offset > str1.length() )
		{
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = offset;
			throw ex.fillInMessage( Message.INDEX_OUT_OF_BOUNDS, offset );
		}

		return str1.substring( 0, offset - 1 ) + str2 + str1.substring( offset - 1, str1.length() );
	}
	
	/**
	 * Makes a new string containing the characters of this string, with a section 
	 * replaced by a second string.
	 *   str1.replaceStrAt(start, end, str2) is equivalent to 
	 *   str1[1:start] :: str2 :: str1[end:str1.length()]
	 *
	 * @throws InvalidIndexException if startIndex is less than 1, greater 
	 *    than the length of this string, or greater than endIndex.
	 * @throws InvalidIndexException if endIndex is greater than the length
	 *    of this string.
	 */
	public static String replaceStrAt( String str1, int startIndex, int endIndex, String str2 )
	{
		if ( str1 == null )
		{
			throw new NullValueException().fillInMessage( Message.NULL_NOT_ALLOWED );
		}

		if ( startIndex < 1 || startIndex > str1.length() || startIndex > endIndex )
		{
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = startIndex;
			throw ex.fillInMessage( Message.INDEX_OUT_OF_BOUNDS, startIndex );
		}
		if ( endIndex > str1.length() )
		{
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = endIndex;
			throw ex.fillInMessage( Message.INDEX_OUT_OF_BOUNDS, endIndex );
		}
		
		return str1.substring( 0, startIndex - 1 ) + str2 + str1.substring( endIndex - 1, str1.length() );
	}
	
	/**
	 * This is like the indexOf function, except we look for a pattern (as defined 
	 * for the matchesPattern function) instead of a literal substring.
	 */
	public static int indexOfPattern( String str, String pattern )
	{
		return indexOfPattern( str, pattern, "\\", 1 );
	}

	/**
	 * This is the same as indexOfPattern(EString), but the additional parameter
	 * specifies the escape character used in the pattern.
	 */
	public static int indexOfPattern( String str, String pattern, String escape )
	{
		return indexOfPattern( str, pattern, escape, 1 );
	}
	
	/**
	 * This is like the indexOf function, except we look for a pattern (as defined 
	 * for the matchesPattern function) instead of a literal substring.
	 *
	 * @throws InvalidIndexException if startIndex is less than 1 or greater 
	 *    than the length of this string.
	 */
	public static int indexOfPattern( String str, String pattern, int startIndex )
	{
		return indexOfPattern( str, pattern, "\\", startIndex );
	}
	
	/**
	 * This is the same as indexOfPattern(EString,EInt), but the additional parameter
	 * specifies the escape character used in the pattern.
	 */
	public static int indexOfPattern( String str, String pattern, String escape, int startIndex )
	{
		if ( str == null )
		{
			throw new NullValueException().fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		if ( startIndex < 1 || startIndex > str.length() )
		{
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = startIndex;
			throw ex.fillInMessage( Message.INDEX_OUT_OF_BOUNDS, startIndex );
		}
		
		String regex = patternToRegex( pattern, escape );
		try 
		{
			Matcher matcher = Pattern.compile( regex ).matcher( str );
			if ( matcher.find( startIndex - 1 ) )
			{
				return matcher.start() + 1;
			}
			else
			{
				return 0;
			}
		}
		catch ( PatternSyntaxException e )
		{
			InvalidPatternException ex = new InvalidPatternException();
			ex.pattern = pattern;
			ex.initCause( e );
			throw ex.fillInMessage( Message.INVALID_MATCH_PATTERN, pattern );
		}
	}

	/**
	 * Returns a string made by searching this string for the pattern (as defined 
	 * for the matchesPattern function), and substituting the replacement string 
	 * where a match is found.  All matches will be replaced if the 'global' 
	 * parameter is true, otherwise only the first match is replaced.  If the 
	 * pattern is not matched, then this string is returned.
	 */
	public static String replacePattern( String str, String pattern, String replacement, boolean global )
	{
		return replacePattern( str, pattern, "\\", replacement, global );
	}
	
	/**
	 * This is the same as replacePattern(EString,EString,EBoolean), but the additional parameter
	 * specifies the escape character used in the pattern.
	 */
	public static String replacePattern( String str, String pattern, String escape, String replacement, boolean global )
	{
		if ( str == null )
		{
			throw new NullValueException().fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		
		String regex = patternToRegex( pattern, escape );
		try 
		{
			Matcher matcher = Pattern.compile( regex ).matcher( str );
			if ( global )
			{
				return matcher.replaceAll( Matcher.quoteReplacement( replacement ) );
			}
			else
			{
				return matcher.replaceFirst( Matcher.quoteReplacement( replacement ) );
			}
		}
		catch ( PatternSyntaxException e )
		{
			InvalidPatternException ex = new InvalidPatternException();
			ex.pattern = pattern;
			ex.initCause( e );
			throw ex.fillInMessage( Message.INVALID_MATCH_PATTERN, pattern );
		}
	}

	/**
	 * Returns true if this string's length is zero.
	 */
	public static boolean isEmpty( String str ) 
	{
		if ( str == null )
		{
			throw new NullValueException().fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		return str.isEmpty();
	}
}
