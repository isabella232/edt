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
package eglx.lang;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import egl.lang.AnyException;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.TimestampData;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.javart.util.JavartDateFormat;
import org.eclipse.edt.javart.util.NumberFormatter;
import egl.lang.InvalidIndexException;

public class StrLib extends ExecutableBase {

	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	private static RunUnit staticRu;

	/**
	 * Constructor
	 */
	public StrLib(RunUnit ru) throws AnyException {
		super(ru);
		this.staticRu = ru;
	}

	/**
	 * default timestamp format
	 */
	public static String defaultTimeStampFormat;

	/**
	 * Returns a number as a formatted string using the given formatting pattern.
	 */
	public static String format(BigDecimal number, String format) {
		return NumberFormatter.fmtNum(number, format, staticRu.getLocalizedText());
	}

	/**
	 * Returns a number as a formatted string using the given formatting pattern.
	 */
	public static String format(short number, String format) {
		return NumberFormatter.fmtNum(new BigDecimal(number), format, staticRu.getLocalizedText());
	}

	/**
	 * Returns a number as a formatted string using the given formatting pattern.
	 */
	public static String format(int number, String format) {
		return NumberFormatter.fmtNum(new BigDecimal(number), format, staticRu.getLocalizedText());
	}

	/**
	 * Returns a number as a formatted string using the given formatting pattern.
	 */
	public static String format(long number, String format) {
		return NumberFormatter.fmtNum(new BigDecimal(number), format, staticRu.getLocalizedText());
	}

	/**
	 * Returns a number as a formatted string using the given formatting pattern.
	 */
	public static String format(float number, String format) {
		return NumberFormatter.fmtNum(new BigDecimal(number), format, staticRu.getLocalizedText());
	}

	/**
	 * Returns a number as a formatted string using the given formatting pattern.
	 */
	public static String format(double number, String format) {
		return NumberFormatter.fmtNum(new BigDecimal(number), format, staticRu.getLocalizedText());
	}

	/**
	 * formats a parameter into a timestamp value and returns a value of type STRING. The DB2 format is the default format.
	 */
	public static String format(Calendar timestampValue, String timestampFormat) {
		String format = timestampFormat;
		if (format == null || format.length() == 0)
			format = defaultTimeStampFormat;
		// if ( format.length() == 0 )
		// format = timestampValue.ezeUnbox().get???();
		TimestampData data = new TimestampData(timestampValue, 0);
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
				JavartDateFormat formatter = DateTimeUtil.getDateFormat(format);
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
	 * Returns the next token from the source string, or NULL if there is none. If a token is found, the index argument is
	 * updated with the token's ending position. The exception egl.core.IndexOutOfBounds is thrown if the index is less than
	 * 1 or greater than the length of the source String.
	 */
	public static String getNextToken(String source, AnyBoxedObject<Integer> index, String delimiters) throws AnyException {
		int start = index.ezeUnbox();
		byte[] sourceBytes = source.getBytes();
		// Validate the substring index.
		if (start < 1 || start > sourceBytes.length) {
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = start;
			throw ex;
		}
		// Search the substring for tokens. We don't use a
		// java.util.StringTokenizer because we need to know the index of
		// the match in addition to the token.
		int tokenStart = start - 1;
		int searchEnd = sourceBytes.length;
		byte[] delimiterBytes = delimiters.getBytes();
		// Skip delimiters at the beginning of the token.
		// Check each byte to see if it's the start of a one-byte delimiter.
		while (tokenStart != searchEnd && indexOf(sourceBytes[tokenStart], delimiterBytes) != -1) {
			tokenStart++;
		}
		// If we're at the end of the substring, the search failed.
		if (tokenStart >= searchEnd)
			return null;
		// Now we know we've found the beginning of a token. Find its end.
		int tokenEnd = tokenStart + 1;
		// Check each byte to see if it's the start of a one-byte delimiter.
		while (tokenEnd != searchEnd && indexOf(sourceBytes[tokenEnd], delimiterBytes) == -1) {
			tokenEnd++;
		}
		// Store the end of the token in index.
		index.ezeCopy(tokenEnd + 1);
		// Return the token's bytes.
		int tokenLength = tokenEnd - tokenStart;
		byte[] tokenData = new byte[tokenLength];
		System.arraycopy(sourceBytes, tokenStart, tokenData, 0, tokenLength);
		return tokenData.toString();
	}

	private static int indexOf(byte b, byte[] data) {
		for (int i = 0; i < data.length; i++) {
			if (data[i] == b)
				return i;
		}
		return -1;
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
