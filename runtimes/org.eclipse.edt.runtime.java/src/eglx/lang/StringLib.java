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
package eglx.lang;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import org.eclipse.edt.javart.*;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Runtime;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.javart.util.JavartDateFormat;
import org.eclipse.edt.javart.util.NumberFormatter;


public class StringLib extends ExecutableBase {

	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/**
	 * Constructor
	 */
	public StringLib() throws AnyException {
	}

	/**
	 * Returns a number as a formatted string using the given formatting pattern.
	 */
	public static String format(BigDecimal number, String format) {
		return NumberFormatter.fmtNum(number, format, Runtime.getRunUnit().getLocalizedText());
	}

	/**
	 * Returns a number as a formatted string using the given formatting pattern.
	 */
	public static String format(short number, String format) {
		return NumberFormatter.fmtNum(new BigDecimal(number), format, Runtime.getRunUnit().getLocalizedText());
	}

	/**
	 * Returns a number as a formatted string using the given formatting pattern.
	 */
	public static String format(int number, String format) {
		return NumberFormatter.fmtNum(new BigDecimal(number), format, Runtime.getRunUnit().getLocalizedText());
	}

	/**
	 * Returns a number as a formatted string using the given formatting pattern.
	 */
	public static String format(long number, String format) {
		return NumberFormatter.fmtNum(new BigDecimal(number), format, Runtime.getRunUnit().getLocalizedText());
	}

	/**
	 * Returns a number as a formatted string using the given formatting pattern.
	 */
	public static String format(float number, String format) {
		return NumberFormatter.fmtNum(number, format, Runtime.getRunUnit().getLocalizedText());
	}

	/**
	 * Returns a number as a formatted string using the given formatting pattern.
	 */
	public static String format(double number, String format) {
		return NumberFormatter.fmtNum(number, format, Runtime.getRunUnit().getLocalizedText());
	}

	/**
	 * formats a parameter into a timestamp value and returns a value of type STRING.
	 */
	public static String format(Calendar timestampValue, String timestampFormat) {
		TimestampData data = new TimestampData(timestampValue, timestampValue.get( Calendar.MILLISECOND ) * 1000);
		boolean reset = false;
		int micros = data.microseconds;
		if (micros < 0) {
			reset = true;
			data.calendar.add(Calendar.SECOND, -1);
			micros += DateTimeUtil.MICROSECONDS_PER_SECOND;
		}
		try {
			int century = data.calendar.get(Calendar.YEAR) / 100 + 1;
			Date date = data.calendar.getTime();
			synchronized (DateTimeUtil.LOCK) {
				JavartDateFormat formatter = DateTimeUtil.getDateFormat(timestampFormat);
				formatter.setCentury(century);
				formatter.setMicrosecond(micros);
				return formatter.format(date);
			}
		}
		finally {
			if (reset)
				data.calendar.add(Calendar.SECOND, 1);
		}
	}

	/**
	 * Returns the next token from the source string, or null if there is none. If a token is found, the index argument is
	 * updated with the token's ending position. The exception eglx.lang.InvalidIndexException is thrown if the index is less than
	 * 1 or greater than the length of the source String.
	 */
	public static String getNextToken(String source, AnyBoxedObject<Integer> index, String delimiters) throws AnyException {
		int start = index.ezeUnbox();
		int searchEnd = source.length();
		// Validate the substring index.
		if (start < 1 || start > searchEnd) {
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = start;
			throw ex.fillInMessage( Message.INDEX_OUT_OF_BOUNDS, start );
		}
		// Search the substring for tokens. We don't use a
		// java.util.StringTokenizer because we need to know the index of
		// the match in addition to the token.
		int tokenStart = start - 1;
		// Skip delimiters at the beginning of the token.
		// Check each char to see if it's a delimiter.
		while (tokenStart != searchEnd && delimiters.indexOf(source.charAt(tokenStart)) != -1) {
			tokenStart++;
		}
		// If we're at the end of the substring, the search failed.
		if (tokenStart >= searchEnd) {
			// Store the end of the token in index.
			index.ezeCopy(searchEnd + 1);
			return null;
		}
		// Now we know we've found the beginning of a token. Find its end.
		int tokenEnd = tokenStart + 1;
		// Check each char to see if it's the start of a delimiter.
		while (tokenEnd != searchEnd && delimiters.indexOf(source.charAt(tokenEnd)) == -1) {
			tokenEnd++;
		}
		// Store the end of the token in index.
		index.ezeCopy(tokenEnd + 1);
		// Return the token.
		return source.substring(tokenStart, tokenEnd);
	}

	/**
	 * Returns the number of tokens in the source string that are delimited by the characters of the input delimiters String.
	 */
	public static int getTokenCount(String source, String delimiters) {
		StringTokenizer tokenizer = new StringTokenizer(source, delimiters);
		return tokenizer.countTokens();
	}

	/**
	 * Returns the string value of a character code
	 */
	public static String fromCharCode(int character) {
		if ( character < 0 || character > 65535 )
		{
			InvalidArgumentException ex = new InvalidArgumentException();
			throw ex.fillInMessage( Message.VALUE_OUT_OF_RANGE, character, 0, 65535 );
		}
		return String.valueOf((char)character);
	}

	/**
	 * returns a string of a specified length.
	 */
	public static String spaces(int characterCount) {
		String retVal;
		if (characterCount <= 0)
			retVal = "";
		else if (characterCount < 50)
			retVal = Constants.STRING_50_BLANKS.substring(0, characterCount);
		else {
			StringBuilder buf = new StringBuilder(characterCount);
			buf.append(Constants.STRING_50_BLANKS);
			while (characterCount >= 50) {
				buf.append(Constants.STRING_50_BLANKS);
				characterCount -= 50;
			}
			if (characterCount > 0)
				buf.append(Constants.STRING_50_BLANKS.substring(0, characterCount));
			retVal = buf.toString();
		}
		return retVal;
	}
}
