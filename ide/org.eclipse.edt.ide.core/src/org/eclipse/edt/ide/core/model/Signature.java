/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.model;

import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
;
// TODO Get all types done
/**
 * Provides methods for encoding and decoding type and method signature strings.
 * <p>
 * The syntax for a type signature is:
 * <pre>
 * typeSignature ::=
 *     "A"  // bigint
 *   | "B"  // bin
 *   | "C"  // byte
 *   | "D"  // char
 *   | "E"  // date
 *   | "F"  // dbchar
 *   | "G"  // decimal
 *   | "H"  // decimalfloat
 *   | "I"  // float
 *   | "J"  // int
 *   | "K"  // interval
 *   | "L"  // integerdate
 *   | "M"  // mbchar
 *   | "N"  // num
 *   | "O"  // smallint
 *   | "P"  // time
 *   | "Q"  // timestamp
 *   | "R"  // unicode
 *   | "S"  // varchar
 *   | "T"  // vardbchar
 *   | "U"  // varmbchar
 *   | "V"  // varunicode
 *   | "W"  // void
 *   | "X"  // boolean
 *   | "Y" + sourceTypeName + ";"  // unresolved named type (in source code)
 *   | "[" + typeSignature  // array of type denoted by typeSignature
 * </pre>
 * </p>
 * <p>
 * Examples:
 * <ul>
 *   <li><code>"[[I"</code> denotes <code>int[][]</code></li>
 *   <li><code>"QRecordX"</code> denotes <code>RecordX</code> in source code</li>
 *   <li><code>"Qmy.pkg.RecordX"</code> denotes <code>my.pkg.RecordX</code> in source code</li>
 *   <li><code>"[QString"</code> denotes <code>String[]</code> in source code</li>
 * </ul>
 * </p>
 * <p>
 * The syntax for a method signature is:
 * <pre>
 * methodSignature ::= "(" + paramTypeSignature* + ")" + returnTypeSignature
 * paramTypeSignature ::= typeSignature
 * returnTypeSignature ::= typeSignature
 * </pre>
 * <p>
 * Examples:
 * <ul>
 *   <li><code>"()I"</code> denotes <code>int foo()</code></li>
 *   <li><code>"([Ljava.lang.String;)V"</code> denotes <code>void foo(java.lang.String[])</code> in compiled code</li>
 *   <li><code>"(QString;)QObject;"</code> denotes <code>Object foo(String)</code> in source code</li>
 * </ul>
 * </p>
 * <p>
 * This class provides static methods and constants only; it is not intended to be
 * instantiated or subclassed by clients.
 * </p>
 */
public final class Signature {

	public static final char C_ARRAY = '[';
	public static final char C_BIGINT = 'A';
	public static final char C_BIN = 'B';
	public static final char C_BYTE = 'C';
	public static final char C_CHAR = 'D';
	public static final char C_DATE = 'E';
	public static final char C_DBCHAR = 'F';
	public static final char C_DECIMAL = 'G';
	public static final char C_DECIMALFLOAT = 'H';
	public static final char C_FLOAT = 'I';
	public static final char C_INT = 'J';
	public static final char C_INTERVAL = 'K';
	public static final char C_INTEGERDATE = 'L';
	public static final char C_MBCHAR = 'M';
	public static final char C_NUM = 'N';
	public static final char C_NUMBER = 'O';
	public static final char C_SMALLINT = 'P';
	public static final char C_TIME = 'Q';
	public static final char C_TIMESTAMP = 'R';
	public static final char C_UNICODE = 'S';
	public static final char C_VARCHAR = 'T';
	public static final char C_VARDBCHAR = 'U';
	public static final char C_VARMBCHAR = 'V';
	public static final char C_VARUNICODE = 'W';
	public static final char C_VOID = 'X';
	public static final char C_RESOLVED = 'Y';
	public static final char C_UNRESOLVED = 'Z';
	public static final char C_DOT = '.';
	public static final char C_SEMICOLON = ';';
	public static final char C_DOLLAR = '$';
	public static final char C_NAME_END = ';';
	public static final char C_PARAM_START = '(';
	public static final char C_PARAM_END = ')';

	public static final String SIG_ARRAY = "["; //$NON-NLS-1$
	public static final String SIG_BIGINT = "A"; //$NON-NLS-1$
	public static final String SIG_BIN = "B"; //$NON-NLS-1$
	public static final String SIG_BYTE = "C"; //$NON-NLS-1$
	public static final String SIG_CHAR = "D"; //$NON-NLS-1$
	public static final String SIG_DATE = "E"; //$NON-NLS-1$
	public static final String SIG_DBCHAR = "F"; //$NON-NLS-1$
	public static final String SIG_DECIMAL = "G"; //$NON-NLS-1$
	public static final String SIG_DECIMALFLOAT = "H"; //$NON-NLS-1$
	public static final String SIG_FLOAT = "I"; //$NON-NLS-1$
	public static final String SIG_INT = "J"; //$NON-NLS-1$
	public static final String SIG_INTERVAL = "K"; //$NON-NLS-1$
	public static final String SIG_INTEGERDATE = "L"; //$NON-NLS-1$
	public static final String SIG_MBCHAR = "M"; //$NON-NLS-1$
	public static final String SIG_NUM = "N"; //$NON-NLS-1$
	public static final String SIG_NUMBER = "O"; //$NON-NLS-1$
	public static final String SIG_SMALLINT = "P"; //$NON-NLS-1$
	public static final String SIG_TIME = "Q"; //$NON-NLS-1$
	public static final String SIG_TIMESTAMP = "R"; //$NON-NLS-1$
	public static final String SIG_UNICODE = "S"; //$NON-NLS-1$
	public static final String SIG_VARCHAR = "T"; //$NON-NLS-1$
	public static final String SIG_VARDBCHAR = "U"; //$NON-NLS-1$
	public static final String SIG_VARMBCHAR = "V"; //$NON-NLS-1$
	public static final String SIG_VARUNICODE = "W"; //$NON-NLS-1$
	public static final String SIG_VOID = "X"; //$NON-NLS-1$
	public static final String SIG_RESOLVED = "Y"; //$NON-NLS-1$
	public static final String SIG_UNRESOLVED = "Z"; //$NON-NLS-1$
	public static final String SIG_DOT = "."; //$NON-NLS-1$
	public static final String SIG_SEMICOLON = ";"; //$NON-NLS-1$
	public static final String SIG_DOLLAR = "$"; //$NON-NLS-1$
	public static final String SIG_NAME_END = ";"; //$NON-NLS-1$
	public static final String SIG_PARAM_START = "("; //$NON-NLS-1$
	public static final String SIG_PARAM_END = ")"; //$NON-NLS-1$

	private static final String EMPTY = new String(CharOperation.NO_CHAR);

	private static final char[] BIGINT = { 'b', 'i', 'g', 'i', 'n', 't' };
	private static final char[] BIN = { 'b', 'i', 'n' };
	private static final char[] BYTE = { 'b', 'y', 't', 'e' };
	private static final char[] CHAR = { 'c', 'h', 'a', 'r' };
	private static final char[] DATE = { 'd', 'a', 't', 'e' };
	private static final char[] DBCHAR = { 'd', 'b', 'c', 'h', 'a', 'r' };
	private static final char[] DECIMAL = { 'd', 'e', 'c', 'i', 'm', 'a', 'l' };
	private static final char[] DECIMALFLOAT =
		{ 'd', 'e', 'c', 'i', 'm', 'a', 'l', 'f', 'l', 'o', 'a', 't' };
	private static final char[] FLOAT = { 'f', 'l', 'o', 'a', 't' };
	private static final char[] INT = { 'i', 'n', 't' };
	private static final char[] INTERVAL =
		{ 'i', 'n', 't', 'e', 'r', 'v', 'a', 'l' };
	private static final char[] INTEGERDATE =
		{ 'i', 'n', 't', 'e', 'g', 'e', 'r', 'd', 'a', 't', 'e' };
	private static final char[] MBCHAR = { 'm', 'b', 'c', 'h', 'a', 'r' };
	private static final char[] NUM = { 'n', 'u', 'm' };
	private static final char[] NUMBER = { 'n', 'u', 'm','b','e','r' };
	private static final char[] SMALLINT =
		{ 's', 'm', 'a', 'l', 'l', 'i', 'n', 't' };
	private static final char[] TIME = { 't', 'i', 'm', 'e' };
	private static final char[] TIMESTAMP =
		{ 't', 'i', 'm', 'e', 's', 't', 'a', 'm', 'p' };
	private static final char[] UNICODE = { 'u', 'n', 'i', 'c', 'o', 'd', 'e' };
	private static final char[] VARCHAR = { 'v', 'a', 'r', 'c', 'h', 'a', 'r' };
	private static final char[] VARDBCHAR =
		{ 'v', 'a', 'r', 'd', 'b', 'c', 'h', 'a', 'r' };
	private static final char[] VARMBCHAR =
		{ 'v', 'a', 'r', 'm', 'b', 'c', 'h', 'a', 'r' };
	private static final char[] VARUNICODE =
		{ 'u', 'n', 'i', 'c', 'o', 'd', 'e' };

	/**
	 * Not instantiable.
	 */
	private Signature() {
	}

	private static long copyType(
		char[] signature,
		int sigPos,
		char[] dest,
		int index,
		boolean fullyQualifyTypeNames) {
		int arrayCount = 0;
		loop : while (true) {
			switch (signature[sigPos++]) {
				case C_ARRAY :
					arrayCount++;
					break;
				case C_BIGINT :
				case C_BIN :
					int length = BIN.length;
					System.arraycopy(BIN, 0, dest, index, length);
					index += length;
					break loop;
				case C_BYTE :
					length = BYTE.length;
					System.arraycopy(BYTE, 0, dest, index, length);
					index += length;
					break loop;
				case C_CHAR :
					length = CHAR.length;
					System.arraycopy(CHAR, 0, dest, index, length);
					index += length;
					break loop;
				case C_DATE :
					length = DATE.length;
					System.arraycopy(DATE, 0, dest, index, length);
					index += length;
					break loop;
				case C_DBCHAR :
					length = DBCHAR.length;
					System.arraycopy(DBCHAR, 0, dest, index, length);
					index += length;
					break loop;
				case C_DECIMAL :
					length = DECIMAL.length;
					System.arraycopy(DECIMAL, 0, dest, index, length);
					index += length;
					break loop;
				case C_DECIMALFLOAT :
					length = DECIMALFLOAT.length;
					System.arraycopy(DECIMALFLOAT, 0, dest, index, length);
					index += length;
					break loop;
				case C_FLOAT :
					length = FLOAT.length;
					System.arraycopy(FLOAT, 0, dest, index, length);
					index += length;
					break loop;
				case C_INT :
					length = INT.length;
					System.arraycopy(INT, 0, dest, index, length);
					index += length;
					break loop;
				case C_INTERVAL :
					length = INTERVAL.length;
					System.arraycopy(INTERVAL, 0, dest, index, length);
					index += length;
					break loop;
				case C_INTEGERDATE :
					length = INT.length;
					System.arraycopy(INTEGERDATE, 0, dest, index, length);
					index += length;
					break loop;
				case C_MBCHAR :
					length = MBCHAR.length;
					System.arraycopy(MBCHAR, 0, dest, index, length);
					index += length;
					break loop;
				case C_NUM :
					length = NUM.length;
					System.arraycopy(NUM, 0, dest, index, length);
					index += length;
					break loop;
				case C_NUMBER :
					length = NUMBER.length;
					System.arraycopy(NUMBER, 0, dest, index, length);
					index += length;
					break loop;
				case C_SMALLINT :
					length = SMALLINT.length;
					System.arraycopy(SMALLINT, 0, dest, index, length);
					index += length;
					break loop;
				case C_TIME :
					length = TIME.length;
					System.arraycopy(TIME, 0, dest, index, length);
					index += length;
					break loop;
				case C_TIMESTAMP :
					length = TIMESTAMP.length;
					System.arraycopy(TIMESTAMP, 0, dest, index, length);
					index += length;
					break loop;
				case C_UNICODE :
					length = UNICODE.length;
					System.arraycopy(UNICODE, 0, dest, index, length);
					index += length;
					break loop;
				case C_VARCHAR :
					length = VARCHAR.length;
					System.arraycopy(VARCHAR, 0, dest, index, length);
					index += length;
					break loop;
				case C_VARDBCHAR :
					length = VARDBCHAR.length;
					System.arraycopy(VARDBCHAR, 0, dest, index, length);
					index += length;
					break loop;
				case C_VARMBCHAR :
					length = VARMBCHAR.length;
					System.arraycopy(VARMBCHAR, 0, dest, index, length);
					index += length;
					break loop;
				case C_VARUNICODE :
					length = VARUNICODE.length;
					System.arraycopy(VARUNICODE, 0, dest, index, length);
					index += length;
					break loop;
				case C_RESOLVED :
				case C_UNRESOLVED :
					int end =
						CharOperation.indexOf(C_SEMICOLON, signature, sigPos);
					if (end == -1)
						throw new IllegalArgumentException();
					int start;
					if (fullyQualifyTypeNames) {
						start = sigPos;
					} else {
						start =
							CharOperation.lastIndexOf(
								C_DOT,
								signature,
								sigPos,
								end)
								+ 1;
						if (start == 0)
							start = sigPos;
					}
					length = end - start;
					System.arraycopy(signature, start, dest, index, length);
					sigPos = end + 1;
					index += length;
					break loop;
			}
		}
		while (arrayCount-- > 0) {
			dest[index++] = '[';
			dest[index++] = ']';
		}
		return (((long) index) << 32) + sigPos;
	}
	/**
	 * Creates a new type signature with the given amount of array nesting added 
	 * to the given type signature.
	 *
	 * @param typeSignature the type signature
	 * @param arrayCount the desired number of levels of array nesting
	 * @return the encoded array type signature
	 * 
	 * @since 2.0
	 */
	public static char[] createArraySignature(
		char[] typeSignature,
		int arrayCount) {
		if (arrayCount == 0)
			return typeSignature;
		int sigLength = typeSignature.length;
		char[] result = new char[arrayCount + sigLength];
		for (int i = 0; i < arrayCount; i++) {
			result[i] = C_ARRAY;
		}
		System.arraycopy(typeSignature, 0, result, arrayCount, sigLength);
		return result;
	}
	/**
	 * Creates a new type signature with the given amount of array nesting added 
	 * to the given type signature.
	 *
	 * @param typeSignature the type signature
	 * @param arrayCount the desired number of levels of array nesting
	 * @return the encoded array type signature
	 */
	public static String createArraySignature(
		String typeSignature,
		int arrayCount) {
		return new String(
			createArraySignature(typeSignature.toCharArray(), arrayCount));
	}
	/**
	 * Creates a method signature from the given parameter and return type 
	 * signatures. The encoded method signature is dot-based.
	 *
	 * @param parameterTypes the list of parameter type signatures
	 * @param returnType the return type signature
	 * @return the encoded method signature
	 * 
	 * @since 2.0
	 */
	public static char[] createFunctionSignature(
		char[][] parameterTypes,
		char[] returnType) {
		int parameterTypesLength = parameterTypes.length;
		int parameterLength = 0;
		for (int i = 0; i < parameterTypesLength; i++) {
			parameterLength += parameterTypes[i].length;

		}
		int returnTypeLength = returnType.length;
		char[] result = new char[1 + parameterLength + 1 + returnTypeLength];
		result[0] = C_PARAM_START;
		int index = 1;
		for (int i = 0; i < parameterTypesLength; i++) {
			char[] parameterType = parameterTypes[i];
			int length = parameterType.length;
			System.arraycopy(parameterType, 0, result, index, length);
			index += length;
		}
		result[index] = C_PARAM_END;
		System.arraycopy(returnType, 0, result, index + 1, returnTypeLength);
		return result;
	}
	/**
	 * Creates a method signature from the given parameter and return type 
	 * signatures. The encoded method signature is dot-based.
	 *
	 * @param parameterTypes the list of parameter type signatures
	 * @param returnType the return type signature
	 * @return the encoded method signature
	 */
	public static String createFunctionSignature(
		String[] parameterTypes,
		String returnType) {
		int parameterTypesLenth = parameterTypes.length;
		char[][] parameters = new char[parameterTypesLenth][];
		for (int i = 0; i < parameterTypesLenth; i++) {
			parameters[i] = parameterTypes[i].toCharArray();
		}
		return new String(
			createFunctionSignature(parameters, returnType.toCharArray()));
	}
	/**
	 * Creates a new type signature from the given type name encoded as a character
	 * array. This method is equivalent to
	 * <code>createTypeSignature(new String(typeName),isResolved)</code>, although
	 * more efficient for callers with character arrays rather than strings. If the 
	 * type name is qualified, then it is expected to be dot-based.
	 *
	 * @param typeName the possibly qualified type name
	 * @param isResolved <code>true</code> if the type name is to be considered
	 *   resolved (for example, a type name from a binary class file), and 
	 *   <code>false</code> if the type name is to be considered unresolved
	 *   (for example, a type name found in source code)
	 * @return the encoded type signature
	 * @see #createTypeSignature(java.lang.String,boolean)
	 */
	public static String createTypeSignature(
		char[] typeName,
		boolean isResolved) {
		return typeName.length == 0 ? "" : new String(createCharArrayTypeSignature(typeName, isResolved));
	}
	/**
	 * Creates a new type signature from the given type name encoded as a character
	 * array. This method is equivalent to
	 * <code>createTypeSignature(new String(typeName),isResolved).toCharArray()</code>, although
	 * more efficient for callers with character arrays rather than strings. If the 
	 * type name is qualified, then it is expected to be dot-based.
	 *
	 * @param typeName the possibly qualified type name
	 * @param isResolved <code>true</code> if the type name is to be considered
	 *   resolved (for example, a type name from a binary class file), and 
	 *   <code>false</code> if the type name is to be considered unresolved
	 *   (for example, a type name found in source code)
	 * @return the encoded type signature
	 * @see #createTypeSignature(java.lang.String,boolean)
	 * 
	 * @since 2.0
	 */
	public static char[] createCharArrayTypeSignature(
		char[] typeName,
		boolean isResolved) {

		if (typeName == null)
			throw new IllegalArgumentException("null"); //$NON-NLS-1$
		int length = typeName.length;
		if (length == 0)
			throw new IllegalArgumentException(new String(typeName));

		int arrayCount = CharOperation.occurencesOf('[', typeName);
		char[] sig;

		switch (typeName[0]) {
			// primitive type?
			case 'b' :
				if (CharOperation.fragmentEquals(BIGINT, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_BIGINT;
					break;
				} else if (
					CharOperation.fragmentEquals(BYTE, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_BYTE;
					break;
				} else if (
					CharOperation.fragmentEquals(BIN, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_BIN;
					break;
				}
			case 'c' :
				if (CharOperation.fragmentEquals(CHAR, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_CHAR;
					break;
				}
			case 'd' :
				if (CharOperation
					.fragmentEquals(DECIMALFLOAT, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_DECIMALFLOAT;
					break;
				} else if (
					CharOperation.fragmentEquals(
						DECIMAL,
						typeName,
						0,
						false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_DECIMAL;
					break;
				} else if (
					CharOperation.fragmentEquals(DATE, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_DATE;
					break;
				} else if (
					CharOperation.fragmentEquals(DBCHAR, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_DBCHAR;
					break;
				}
			case 'f' :
				if (CharOperation.fragmentEquals(FLOAT, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_FLOAT;
					break;
				}
			case 'i' :
				if (CharOperation.fragmentEquals(
							INTEGERDATE,
							typeName,
							0,
							false)) {
						sig = new char[arrayCount + 1];
						sig[arrayCount] = C_INTEGERDATE;
						break;
				}else if (CharOperation.fragmentEquals(
								INTERVAL,
								typeName,
								0,
								false)) {
							sig = new char[arrayCount + 1];
							sig[arrayCount] = C_INTERVAL;
							break;
				} else if (CharOperation.fragmentEquals(INT, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_INT;
					break;
				}
			case 'm' :
				if (CharOperation.fragmentEquals(MBCHAR, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_MBCHAR;
					break;
				}
			case 'n' :
				if (CharOperation.fragmentEquals(NUM, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_NUM;
					break;
				} else if (CharOperation.fragmentEquals(NUMBER, typeName, 0, false)) {
						sig = new char[arrayCount + 1];
						sig[arrayCount] = C_NUMBER;
						break;
				}
			case 's' :
				if (CharOperation
					.fragmentEquals(SMALLINT, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_SMALLINT;
					break;
				}
			case 't' :
				if (CharOperation.fragmentEquals(TIME, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_TIME;
					break;
				} else if (
					CharOperation.fragmentEquals(
						TIMESTAMP,
						typeName,
						0,
						false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_TIMESTAMP;
					break;
				}
			case 'u' :
				if (CharOperation
					.fragmentEquals(UNICODE, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_UNICODE;
					break;
				}
			case 'v' :
				if (CharOperation
					.fragmentEquals(VARCHAR, typeName, 0, false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_VARCHAR;
					break;
				} else if (
					CharOperation.fragmentEquals(
						VARDBCHAR,
						typeName,
						0,
						false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_VARDBCHAR;
					break;
				} else if (
					CharOperation.fragmentEquals(
						VARUNICODE,
						typeName,
						0,
						false)) {
					sig = new char[arrayCount + 1];
					sig[arrayCount] = C_VARUNICODE;
					break;
				}
			default :
				// non primitive type
				int sigLength = arrayCount + 1 + length + 1;
				// for example '[[[Ljava.lang.String;'
				sig = new char[sigLength];
				int sigIndex = arrayCount + 1; // index in sig
				int startID = 0; // start of current ID in typeName
				int index = 0; // index in typeName
				while (index < length) {
					char currentChar = typeName[index];
					switch (currentChar) {
						case '.' :
							if (startID == -1)
								throw new IllegalArgumentException(
									new String(typeName));
							if (startID < index) {
								sig =
									CharOperation.append(
										sig,
										sigIndex,
										typeName,
										startID,
										index);
								sigIndex += index - startID;
							}
							sig[sigIndex++] = C_DOT;
							index++;
							startID = index;
							break;
						case '[' :
							if (startID != -1) {
								if (startID < index) {
									sig =
										CharOperation.append(
											sig,
											sigIndex,
											typeName,
											startID,
											index);
									sigIndex += index - startID;
								}
								startID = -1; // no more id after []
							}
							index++;
							break;
						default :
							if (startID != -1
								&& CharOperation.isWhitespace(currentChar)) {
								if (startID < index) {
									sig =
										CharOperation.append(
											sig,
											sigIndex,
											typeName,
											startID,
											index);
									sigIndex += index - startID;
								}
								startID = index + 1;
							}
							index++;
							break;
					}
				}
				// last id
				if (startID != -1 && startID < index) {
					sig =
						CharOperation.append(
							sig,
							sigIndex,
							typeName,
							startID,
							index);
					sigIndex += index - startID;
				}

				// add L (or Q) at the beigininig and ; at the end
				sig[arrayCount] = isResolved ? C_RESOLVED : C_UNRESOLVED;
				sig[sigIndex++] = C_NAME_END;

				// resize if needed
				if (sigLength > sigIndex) {
					System.arraycopy(
						sig,
						0,
						sig = new char[sigIndex],
						0,
						sigIndex);
				}
		}

		// add array info
		for (int i = 0; i < arrayCount; i++) {
			sig[i] = C_ARRAY;
		}

		return sig;
	}
	/**
	 * Creates a new type signature from the given type name. If the type name is qualified,
	 * then it is expected to be dot-based.
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * createTypeSignature("int", hucairz) -> "I"
	 * createTypeSignature("my.company.Record1", true) -> "Ymy.company.Record1;"
	 * createTypeSignature("Record1", false) -> "ZRecord1;"
	 * createTypeSignature("my.company.Record1", false) -> "Zjava.lang.String;"
	 * createTypeSignature("int []", false) -> "[I"
	 * </code>
	 * </pre>
	 * </p>
	 *
	 * @param typeName the possibly qualified type name
	 * @param isResolved <code>true</code> if the type name is to be considered
	 *   resolved (for example, a type name from a binary class file), and 
	 *   <code>false</code> if the type name is to be considered unresolved
	 *   (for example, a type name found in source code)
	 * @return the encoded type signature
	 */
	public static String createTypeSignature(
		String typeName,
		boolean isResolved) {
		return createTypeSignature(
			typeName == null ? null : typeName.toCharArray(),
			isResolved);
	}
	/**
	 * Returns the array count (array nesting depth) of the given type signature.
	 *
	 * @param typeSignature the type signature
	 * @return the array nesting depth, or 0 if not an array
	 * @exception IllegalArgumentException if the signature is not syntactically
	 *   correct
	 * 
	 * @since 2.0
	 */
	public static int getArrayCount(char[] typeSignature)
		throws IllegalArgumentException {
		try {
			int count = 0;
			while (typeSignature[count] == C_ARRAY) {
				++count;
			}
			return count;
		} catch (ArrayIndexOutOfBoundsException e) { // signature is syntactically incorrect if last character is C_ARRAY
			throw new IllegalArgumentException();
		}
	}
	/**
	 * Returns the array count (array nesting depth) of the given type signature.
	 *
	 * @param typeSignature the type signature
	 * @return the array nesting depth, or 0 if not an array
	 * @exception IllegalArgumentException if the signature is not syntactically
	 *   correct
	 */
	public static int getArrayCount(String typeSignature)
		throws IllegalArgumentException {
		return getArrayCount(typeSignature.toCharArray());
	}
	/**
	 * Returns the type signature without any array nesting.
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * getElementType({'[', '[', 'I'}) --> {'I'}.
	 * </code>
	 * </pre>
	 * </p>
	 * 
	 * @param typeSignature the type signature
	 * @return the type signature without arrays
	 * @exception IllegalArgumentException if the signature is not syntactically
	 *   correct
	 * 
	 * @since 2.0
	 */
	public static char[] getElementType(char[] typeSignature)
		throws IllegalArgumentException {
		int count = getArrayCount(typeSignature);
		if (count == 0)
			return typeSignature;
		int length = typeSignature.length;
		char[] result = new char[length - count];
		System.arraycopy(typeSignature, count, result, 0, length - count);
		return result;
	}
	/**
	 * Returns the type signature without any array nesting.
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * getElementType("[[I") --> "I".
	 * </code>
	 * </pre>
	 * </p>
	 * 
	 * @param typeSignature the type signature
	 * @return the type signature without arrays
	 * @exception IllegalArgumentException if the signature is not syntactically
	 *   correct
	 */
	public static String getElementType(String typeSignature)
		throws IllegalArgumentException {
		return new String(getElementType(typeSignature.toCharArray()));
	}
	/**
	 * Returns the number of parameter types in the given method signature.
	 *
	 * @param methodSignature the method signature
	 * @return the number of parameters
	 * @exception IllegalArgumentException if the signature is not syntactically
	 *   correct
	 * @since 2.0
	 */
	public static int getParameterCount(char[] methodSignature)
		throws IllegalArgumentException {
		try {
			int count = 0;
			int i = CharOperation.indexOf(C_PARAM_START, methodSignature) + 1;
			if (i == 0)
				throw new IllegalArgumentException();
			for (;;) {
				char c = methodSignature[i++];
				switch (c) {
					case C_ARRAY :
						break;
					case C_BIGINT :
					case C_BIN :
					case C_BYTE :
					case C_CHAR :
					case C_DATE :
					case C_DBCHAR :
					case C_DECIMAL :
					case C_DECIMALFLOAT :
					case C_FLOAT :
					case C_INT :
					case C_INTERVAL :
					case C_INTEGERDATE :
					case C_MBCHAR :
					case C_NUM :
					case C_NUMBER :
					case C_SMALLINT :
					case C_TIME :
					case C_TIMESTAMP :
					case C_UNICODE :
					case C_VARCHAR :
					case C_VARDBCHAR :
					case C_VARMBCHAR :
					case C_VARUNICODE :
						++count;
						break;
					case C_RESOLVED :
					case C_UNRESOLVED :
						i =
							CharOperation.indexOf(
								C_SEMICOLON,
								methodSignature,
								i)
								+ 1;
						if (i == 0)
							throw new IllegalArgumentException();
						++count;
						break;
					case C_PARAM_END :
						return count;
					default :
						throw new IllegalArgumentException();
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException();
		}
	}
	/**
	 * Returns the number of parameter types in the given method signature.
	 *
	 * @param methodSignature the method signature
	 * @return the number of parameters
	 * @exception IllegalArgumentException if the signature is not syntactically
	 *   correct
	 */
	public static int getParameterCount(String methodSignature)
		throws IllegalArgumentException {
		return getParameterCount(methodSignature.toCharArray());
	}
	/**
	 * Extracts the parameter type signatures from the given method signature. 
	 * The method signature is expected to be dot-based.
	 *
	 * @param methodSignature the method signature
	 * @return the list of parameter type signatures
	 * @exception IllegalArgumentException if the signature is syntactically
	 *   incorrect
	 * 
	 * @since 2.0
	 */
	public static char[][] getParameterTypes(char[] methodSignature)
		throws IllegalArgumentException {
		try {
			int count = getParameterCount(methodSignature);
			char[][] result = new char[count][];
			if (count == 0)
				return result;
			int i = CharOperation.indexOf(C_PARAM_START, methodSignature) + 1;
			count = 0;
			int start = i;
			for (;;) {
				char c = methodSignature[i++];
				switch (c) {
					case C_ARRAY :
						// array depth is i - start;
						break;
					case C_BIGINT :
					case C_BIN :
					case C_BYTE :
					case C_CHAR :
					case C_DATE :
					case C_DBCHAR :
					case C_DECIMAL :
					case C_DECIMALFLOAT :
					case C_FLOAT :
					case C_INT :
					case C_INTERVAL :
					case C_INTEGERDATE :
					case C_MBCHAR :
					case C_NUM :
					case C_NUMBER :
					case C_SMALLINT :
					case C_TIME :
					case C_TIMESTAMP :
					case C_UNICODE :
					case C_VARCHAR :
					case C_VARDBCHAR :
					case C_VARMBCHAR :
					case C_VARUNICODE :
						// common case of base types
						if (i - start == 1) {
							switch (c) {
								case C_BIGINT :
									result[count++] = new char[] { C_BIGINT };
									break;
								case C_BIN :
									result[count++] = new char[] { C_BIN };
									break;
								case C_BYTE :
									result[count++] = new char[] { C_BYTE };
									break;
								case C_CHAR :
									result[count++] = new char[] { C_CHAR };
									break;
								case C_DATE :
									result[count++] = new char[] { C_DATE };
									break;
								case C_DBCHAR :
									result[count++] = new char[] { C_DBCHAR };
									break;
								case C_DECIMAL :
									result[count++] = new char[] { C_DECIMAL };
									break;
								case C_DECIMALFLOAT :
									result[count++] =
										new char[] { C_DECIMALFLOAT };
									break;
								case C_FLOAT :
									result[count++] = new char[] { C_FLOAT };
									break;
								case C_INT :
									result[count++] = new char[] { C_INT };
									break;
								case C_INTERVAL :
									result[count++] = new char[] { C_INTERVAL };
									break;
								case C_INTEGERDATE :
									result[count++] =
										new char[] { C_INTEGERDATE };
									break;
								case C_MBCHAR :
									result[count++] = new char[] { C_MBCHAR };
									break;
								case C_NUM :
									result[count++] = new char[] { C_NUM };
									break;
								case C_NUMBER :
									result[count++] = new char[] { C_NUMBER };
									break;
								case C_SMALLINT :
									result[count++] = new char[] { C_SMALLINT };
									break;
								case C_TIME :
									result[count++] = new char[] { C_TIME };
									break;
								case C_TIMESTAMP :
									result[count++] =
										new char[] { C_TIMESTAMP };
									break;
								case C_UNICODE :
									result[count++] = new char[] { C_UNICODE };
									break;
								case C_VARCHAR :
									result[count++] = new char[] { C_VARCHAR };
									break;
								case C_VARDBCHAR :
									result[count++] =
										new char[] { C_VARDBCHAR };
									break;
								case C_VARMBCHAR :
									result[count++] =
										new char[] { C_VARMBCHAR };
									break;
								case C_VARUNICODE :
									result[count++] =
										new char[] { C_VARUNICODE };
									break;
							}
						} else {
							result[count++] =
								CharOperation.subarray(
									methodSignature,
									start,
									i);
						}
						start = i;
						break;
					case C_RESOLVED :
					case C_UNRESOLVED :
						i =
							CharOperation.indexOf(
								C_SEMICOLON,
								methodSignature,
								i)
								+ 1;
						if (i == 0)
							throw new IllegalArgumentException();
						result[count++] =
							CharOperation.subarray(methodSignature, start, i);
						start = i;
						break;
					case C_PARAM_END :
						return result;
					default :
						throw new IllegalArgumentException();
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException();
		}
	}
	/**
	 * Extracts the parameter type signatures from the given method signature. 
	 * The method signature is expected to be dot-based.
	 *
	 * @param methodSignature the method signature
	 * @return the list of parameter type signatures
	 * @exception IllegalArgumentException if the signature is syntactically
	 *   incorrect
	 */
	public static String[] getParameterTypes(String methodSignature)
		throws IllegalArgumentException {
		char[][] parameterTypes =
			getParameterTypes(methodSignature.toCharArray());
		int length = parameterTypes.length;
		String[] result = new String[length];
		for (int i = 0; i < length; i++) {
			result[i] = new String(parameterTypes[i]);
		}
		return result;
	}
	/**
	 * Returns a char array containing all but the last segment of the given 
	 * dot-separated qualified name. Returns the empty char array if it is not qualified.
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * getQualifier({'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'O', 'b', 'j', 'e', 'c', 't'}) -> {'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g'}
	 * getQualifier({'O', 'u', 't', 'e', 'r', '.', 'I', 'n', 'n', 'e', 'r'}) -> {'O', 'u', 't', 'e', 'r'}
	 * </code>
	 * </pre>
	 * </p>
	 *
	 * @param name the name
	 * @return the qualifier prefix, or the empty char array if the name contains no
	 *   dots
	 * @exception NullPointerException if name is null
	 * @since 2.0
	 */
	public static char[] getQualifier(char[] name) {
		int lastDot = CharOperation.lastIndexOf(C_DOT, name);
		if (lastDot == -1) {
			return CharOperation.NO_CHAR;
		}
		return CharOperation.subarray(name, 0, lastDot);
	}
	/**
	 * Returns a string containing all but the last segment of the given 
	 * dot-separated qualified name. Returns the empty string if it is not qualified.
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * getQualifier("java.lang.Object") -> "java.lang"
	 * getQualifier("Outer.Inner") -> "Outer"
	 * </code>
	 * </pre>
	 * </p>
	 *
	 * @param name the name
	 * @return the qualifier prefix, or the empty string if the name contains no
	 *   dots
	 * @exception NullPointerException if name is null
	 */
	public static String getQualifier(String name) {
		int lastDot = name.lastIndexOf(C_DOT);
		if (lastDot == -1) {
			return EMPTY;
		}
		return name.substring(0, lastDot);
	}
	/**
	 * Extracts the return type from the given method signature. The method signature is 
	 * expected to be dot-based.
	 *
	 * @param methodSignature the method signature
	 * @return the type signature of the return type
	 * @exception IllegalArgumentException if the signature is syntactically
	 *   incorrect
	 * 
	 * @since 2.0
	 */
	public static char[] getReturnType(char[] methodSignature)
		throws IllegalArgumentException {
		int i = CharOperation.lastIndexOf(C_PARAM_END, methodSignature);
		if (i == -1) {
			throw new IllegalArgumentException();
		}
		return CharOperation.subarray(
			methodSignature,
			i + 1,
			methodSignature.length);
	}
	/**
	 * Extracts the return type from the given method signature. The method signature is 
	 * expected to be dot-based.
	 *
	 * @param methodSignature the method signature
	 * @return the type signature of the return type
	 * @exception IllegalArgumentException if the signature is syntactically
	 *   incorrect
	 */
	public static String getReturnType(String methodSignature)
		throws IllegalArgumentException {
		return new String(getReturnType(methodSignature.toCharArray()));
	}
	/**
	 * Returns the last segment of the given dot-separated qualified name.
	 * Returns the given name if it is not qualified.
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * getSimpleName({'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'O', 'b', 'j', 'e', 'c', 't'}) -> {'O', 'b', 'j', 'e', 'c', 't'}
	 * </code>
	 * </pre>
	 * </p>
	 *
	 * @param name the name
	 * @return the last segment of the qualified name
	 * @exception NullPointerException if name is null
	 * @since 2.0
	 */
	public static char[] getSimpleName(char[] name) {
		int lastDot = CharOperation.lastIndexOf(C_DOT, name);
		if (lastDot == -1) {
			return name;
		}
		return CharOperation.subarray(name, lastDot + 1, name.length);
	}
	/**
	 * Returns the last segment of the given dot-separated qualified name.
	 * Returns the given name if it is not qualified.
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * getSimpleName("java.lang.Object") -> "Object"
	 * </code>
	 * </pre>
	 * </p>
	 *
	 * @param name the name
	 * @return the last segment of the qualified name
	 * @exception NullPointerException if name is null
	 */
	public static String getSimpleName(String name) {
		int lastDot = name.lastIndexOf(C_DOT);
		if (lastDot == -1) {
			return name;
		}
		return name.substring(lastDot + 1, name.length());
	}
	/**
	 * Returns all segments of the given dot-separated qualified name.
	 * Returns an array with only the given name if it is not qualified.
	 * Returns an empty array if the name is empty.
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * getSimpleNames({'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'O', 'b', 'j', 'e', 'c', 't'}) -> {{'j', 'a', 'v', 'a'}, {'l', 'a', 'n', 'g'}, {'O', 'b', 'j', 'e', 'c', 't'}}
	 * getSimpleNames({'O', 'b', 'j', 'e', 'c', 't'}) -> {{'O', 'b', 'j', 'e', 'c', 't'}}
	 * getSimpleNames("") -> {}
	 * </code>
	 * </pre>
	 *
	 * @param name the name
	 * @return the list of simple names, possibly empty
	 * @exception NullPointerException if name is null
	 * @since 2.0
	 */
	public static char[][] getSimpleNames(char[] name) {
		if (name.length == 0) {
			return CharOperation.NO_CHAR_CHAR;
		}
		int dot = CharOperation.indexOf(C_DOT, name);
		if (dot == -1) {
			return new char[][] { name };
		}
		int n = 1;
		while ((dot = CharOperation.indexOf(C_DOT, name, dot + 1)) != -1) {
			++n;
		}
		char[][] result = new char[n + 1][];
		int segStart = 0;
		for (int i = 0; i < n; ++i) {
			dot = CharOperation.indexOf(C_DOT, name, segStart);
			result[i] = CharOperation.subarray(name, segStart, dot);
			segStart = dot + 1;
		}
		result[n] = CharOperation.subarray(name, segStart, name.length);
		return result;
	}
	/**
	 * Returns all segments of the given dot-separated qualified name.
	 * Returns an array with only the given name if it is not qualified.
	 * Returns an empty array if the name is empty.
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * getSimpleNames("java.lang.Object") -> {"java", "lang", "Object"}
	 * getSimpleNames("Object") -> {"Object"}
	 * getSimpleNames("") -> {}
	 * </code>
	 * </pre>
	 *
	 * @param name the name
	 * @return the list of simple names, possibly empty
	 * @exception NullPointerException if name is null
	 */
	public static String[] getSimpleNames(String name) {
		char[][] simpleNames = getSimpleNames(name.toCharArray());
		int length = simpleNames.length;
		String[] result = new String[length];
		for (int i = 0; i < length; i++) {
			result[i] = new String(simpleNames[i]);
		}
		return result;
	}
	/**
	 * Converts the given method signature to a readable form. The method signature is expected to
	 * be dot-based.
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * toString("([Ljava.lang.String;)V", "main", new String[] {"args"}, false, true) -> "void main(String[] args)"
	 * </code>
	 * </pre>
	 * </p>
	 * 
	 * @param methodSignature the method signature to convert
	 * @param methodName the name of the method to insert in the result, or 
	 *   <code>null</code> if no method name is to be included
	 * @param parameterNames the parameter names to insert in the result, or 
	 *   <code>null</code> if no parameter names are to be included; if supplied,
	 *   the number of parameter names must match that of the method signature
	 * @param fullyQualifyTypeNames <code>true</code> if type names should be fully
	 *   qualified, and <code>false</code> to use only simple names
	 * @param includeReturnType <code>true</code> if the return type is to be
	 *   included
	 * @return the char array representation of the method signature
	 * 
	 * @since 2.0
	 */
	public static char[] toCharArray(
		char[] methodSignature,
		char[] methodName,
		char[][] parameterNames,
		boolean fullyQualifyTypeNames,
		boolean includeReturnType) {
		try {
			int firstParen =
				CharOperation.indexOf(C_PARAM_START, methodSignature);
			if (firstParen == -1)
				throw new IllegalArgumentException();

			int sigLength = methodSignature.length;

			// compute result length

			// method signature
			int paramCount = 0;
			int lastParen = -1;
			int resultLength = 0;
			signature : for (int i = firstParen; i < sigLength; i++) {
				switch (methodSignature[i]) {
					case C_ARRAY :
						resultLength += 2; // []
						continue signature;
					case C_BIGINT :
						resultLength += BIGINT.length;
						break;
					case C_BIN :
						resultLength += BIN.length;
						break;
					case C_BYTE :
						resultLength += BYTE.length;
						break;
					case C_CHAR :
						resultLength += CHAR.length;
						break;
					case C_DATE :
						resultLength += DATE.length;
						break;
					case C_DBCHAR :
						resultLength += DBCHAR.length;
						break;
					case C_DECIMAL :
						resultLength += DECIMAL.length;
						break;
					case C_DECIMALFLOAT :
						resultLength += DECIMALFLOAT.length;
						break;
					case C_FLOAT :
						resultLength += FLOAT.length;
						break;
					case C_INT :
						resultLength += INT.length;
						break;
					case C_INTERVAL :
						resultLength += INTERVAL.length;
						break;
					case C_INTEGERDATE :
						resultLength += INTEGERDATE.length;
						break;
					case C_MBCHAR :
						resultLength += MBCHAR.length;
						break;
					case C_NUM :
						resultLength += NUM.length;
						break;
					case C_NUMBER :
						resultLength += NUMBER.length;
						break;
					case C_SMALLINT :
						resultLength += SMALLINT.length;
						break;
					case C_TIME :
						resultLength += TIME.length;
						break;
					case C_TIMESTAMP :
						resultLength += TIMESTAMP.length;
						break;
					case C_UNICODE :
						resultLength += UNICODE.length;
						break;
					case C_VARCHAR :
						resultLength += VARCHAR.length;
						break;
					case C_VARDBCHAR :
						resultLength += VARDBCHAR.length;
						break;
					case C_VARMBCHAR :
						resultLength += VARMBCHAR.length;
						break;
					case C_VARUNICODE :
						resultLength += VARUNICODE.length;
						break;
					case C_RESOLVED :
					case C_UNRESOLVED :
						int end =
							CharOperation.indexOf(
								C_SEMICOLON,
								methodSignature,
								i);
						if (end == -1)
							throw new IllegalArgumentException();
						int start;
						if (fullyQualifyTypeNames) {
							start = i + 1;
						} else {
							start =
								CharOperation.lastIndexOf(
									C_DOT,
									methodSignature,
									i,
									end)
									+ 1;
							if (start == 0)
								start = i + 1;
						}
						resultLength += end - start;
						i = end;
						break;
					case C_PARAM_START :
						// add space for "("
						resultLength++;
						continue signature;
					case C_PARAM_END :
						lastParen = i;
						if (includeReturnType) {
							if (paramCount > 0) {
								// remove space for ", " that was added with last parameter and remove space that is going to be added for ", " after return type 
								// and add space for ") "
								resultLength -= 2;
							} //else
							// remove space that is going to be added for ", " after return type 
							// and add space for ") "
							// -> noop

							// decrement param count because it is going to be added for return type
							paramCount--;
							continue signature;
						} else {
							if (paramCount > 0) {
								// remove space for ", " that was added with last parameter and add space for ")"
								resultLength--;
							} else {
								// add space for ")"
								resultLength++;
							}
							break signature;
						}
					default :
						throw new IllegalArgumentException();
				}
				resultLength += 2; // add space for ", "
				paramCount++;
			}

			// parameter names
			int parameterNamesLength =
				parameterNames == null ? 0 : parameterNames.length;
			for (int i = 0; i < parameterNamesLength; i++) {
				resultLength += parameterNames[i].length + 1;
				// parameter name + space
			}

			// selector
			int selectorLength = methodName == null ? 0 : methodName.length;
			resultLength += selectorLength;

			// create resulting char array
			char[] result = new char[resultLength];

			// returned type
			int index = 0;
			if (includeReturnType) {
				long pos =
					copyType(
						methodSignature,
						lastParen + 1,
						result,
						index,
						fullyQualifyTypeNames);
				index = (int) (pos >>> 32);
				result[index++] = ' ';
			}

			// selector
			if (methodName != null) {
				System.arraycopy(methodName, 0, result, index, selectorLength);
				index += selectorLength;
			}

			// parameters
			result[index++] = C_PARAM_START;
			int sigPos = firstParen + 1;
			for (int i = 0; i < paramCount; i++) {
				long pos =
					copyType(
						methodSignature,
						sigPos,
						result,
						index,
						fullyQualifyTypeNames);
				index = (int) (pos >>> 32);
				sigPos = (int) pos;
				if (parameterNames != null) {
					result[index++] = ' ';
					char[] parameterName = parameterNames[i];
					int paramLength = parameterName.length;
					System.arraycopy(
						parameterName,
						0,
						result,
						index,
						paramLength);
					index += paramLength;
				}
				if (i != paramCount - 1) {
					result[index++] = ',';
					result[index++] = ' ';
				}
			}
			if (sigPos >= sigLength) {
				throw new IllegalArgumentException();
				// should be on last paren
			}
			result[index++] = C_PARAM_END;

			return result;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException();
		}
	}
	/**
	 * Converts the given type signature to a readable string. The signature is expected to
	 * be dot-based.
	 * 
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * toString({'[', 'L', 'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'S', 't', 'r', 'i', 'n', 'g', ';'}) -> {'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'S', 't', 'r', 'i', 'n', 'g', '[', ']'}
	 * toString({'I'}) -> {'i', 'n', 't'}
	 * </code>
	 * </pre>
	 * </p>
	 * <p>
	 * Note: This method assumes that a type signature containing a <code>'$'</code>
	 * is an inner type signature. While this is correct in most cases, someone could 
	 * define a non-inner type name containing a <code>'$'</code>. Handling this 
	 * correctly in all cases would have required resolving the signature, which 
	 * generally not feasible.
	 * </p>
	 *
	 * @param signature the type signature
	 * @return the string representation of the type
	 * @exception IllegalArgumentException if the signature is not syntactically
	 *   correct
	 * 
	 * @since 2.0
	 */
	public static char[] toCharArray(char[] signature)
		throws IllegalArgumentException {
		try {
			int sigLength = signature.length;

			if (sigLength == 0 || signature[0] == C_PARAM_START) {
				try {
					return toCharArray(
						signature,
						CharOperation.NO_CHAR,
						null,
						true,
						true);
				}
				catch(IllegalArgumentException e) {
					return new char[0];
				}
			}

			// compute result length
			int resultLength = 0;
			int index = -1;
			while (signature[++index] == C_ARRAY) {
				resultLength += 2; // []
			}
			switch (signature[index]) {
				case C_BIGINT :
					resultLength += BIGINT.length;
					break;
				case C_BIN :
					resultLength += BIN.length;
					break;
				case C_BYTE :
					resultLength += BYTE.length;
					break;
				case C_CHAR :
					resultLength += CHAR.length;
					break;
				case C_DATE :
					resultLength += DATE.length;
					break;
				case C_DBCHAR :
					resultLength += DBCHAR.length;
					break;
				case C_DECIMAL :
					resultLength += DECIMAL.length;
					break;
				case C_DECIMALFLOAT :
					resultLength += DECIMALFLOAT.length;
					break;
				case C_FLOAT :
					resultLength += FLOAT.length;
					break;
				case C_INT :
					resultLength += INT.length;
					break;
				case C_INTERVAL :
					resultLength += INTERVAL.length;
					break;
				case C_INTEGERDATE :
					resultLength += INTEGERDATE.length;
					break;
				case C_MBCHAR :
					resultLength += MBCHAR.length;
					break;
				case C_NUM :
					resultLength += NUM.length;
					break;
				case C_NUMBER :
					resultLength += NUMBER.length;
					break;
				case C_SMALLINT :
					resultLength += SMALLINT.length;
					break;
				case C_TIME :
					resultLength += TIME.length;
					break;
				case C_TIMESTAMP :
					resultLength += TIMESTAMP.length;
					break;
				case C_UNICODE :
					resultLength += UNICODE.length;
					break;
				case C_VARCHAR :
					resultLength += VARCHAR.length;
					break;
				case C_VARDBCHAR :
					resultLength += VARDBCHAR.length;
					break;
				case C_VARMBCHAR :
					resultLength += VARMBCHAR.length;
					break;
				case C_VARUNICODE :
					resultLength += VARUNICODE.length;
					break;
				case C_RESOLVED :
				case C_UNRESOLVED :
					int end =
						CharOperation.indexOf(C_SEMICOLON, signature, index);
					if (end == -1)
						throw new IllegalArgumentException();
					int start = index + 1;
					resultLength += end - start;
					break;
				default :
					throw new IllegalArgumentException();
			}

			char[] result = new char[resultLength];
			copyType(signature, 0, result, 0, true);

			return result;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException();
		}
	}
	/**
	 * Converts the given array of qualified name segments to a qualified name.
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * toQualifiedName({{'j', 'a', 'v', 'a'}, {'l', 'a', 'n', 'g'}, {'O', 'b', 'j', 'e', 'c', 't'}}) -> {'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'O', 'b', 'j', 'e', 'c', 't'}
	 * toQualifiedName({{'O', 'b', 'j', 'e', 'c', 't'}}) -> {'O', 'b', 'j', 'e', 'c', 't'}
	 * toQualifiedName({{}}) -> {}
	 * </code>
	 * </pre>
	 * </p>
	 *
	 * @param segments the list of name segments, possibly empty
	 * @return the dot-separated qualified name, or the empty string
	 * 
	 * @since 2.0
	 */
	public static char[] toQualifiedName(char[][] segments) {
		int length = segments.length;
		if (length == 0)
			return CharOperation.NO_CHAR;
		if (length == 1)
			return segments[0];

		int resultLength = 0;
		for (int i = 0; i < length; i++) {
			resultLength += segments[i].length + 1;
		}
		resultLength--;
		char[] result = new char[resultLength];
		int index = 0;
		for (int i = 0; i < length; i++) {
			char[] segment = segments[i];
			int segmentLength = segment.length;
			System.arraycopy(segment, 0, result, index, segmentLength);
			index += segmentLength;
			if (i != length - 1) {
				result[index++] = C_DOT;
			}
		}
		return result;
	}
	/**
	 * Converts the given array of qualified name segments to a qualified name.
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * toQualifiedName(new String[] {"java", "lang", "Object"}) -> "java.lang.Object"
	 * toQualifiedName(new String[] {"Object"}) -> "Object"
	 * toQualifiedName(new String[0]) -> ""
	 * </code>
	 * </pre>
	 * </p>
	 *
	 * @param segments the list of name segments, possibly empty
	 * @return the dot-separated qualified name, or the empty string
	 */
	public static String toQualifiedName(String[] segments) {
		int length = segments.length;
		char[][] charArrays = new char[length][];
		for (int i = 0; i < length; i++) {
			charArrays[i] = segments[i].toCharArray();
		}
		return new String(toQualifiedName(charArrays));
	}
	/**
	 * Converts the given type signature to a readable string. The signature is expected to
	 * be dot-based.
	 * 
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * toString("[Ljava.lang.String;") -> "java.lang.String[]"
	 * toString("I") -> "int"
	 * </code>
	 * </pre>
	 * </p>
	 * <p>
	 * Note: This method assumes that a type signature containing a <code>'$'</code>
	 * is an inner type signature. While this is correct in most cases, someone could 
	 * define a non-inner type name containing a <code>'$'</code>. Handling this 
	 * correctly in all cases would have required resolving the signature, which 
	 * generally not feasible.
	 * </p>
	 *
	 * @param signature the type signature
	 * @return the string representation of the type
	 * @exception IllegalArgumentException if the signature is not syntactically
	 *   correct
	 */
	public static String toString(String signature)
		throws IllegalArgumentException {
		return new String(toCharArray(signature.toCharArray()));
	}
	/**
	 * Converts the given method signature to a readable string. The method signature is expected to
	 * be dot-based.
	 * <p>
	 * For example:
	 * <pre>
	 * <code>
	 * toString("([Ljava.lang.String;)V", "main", new String[] {"args"}, false, true) -> "void main(String[] args)"
	 * </code>
	 * </pre>
	 * </p>
	 * 
	 * @param methodSignature the method signature to convert
	 * @param methodName the name of the method to insert in the result, or 
	 *   <code>null</code> if no method name is to be included
	 * @param parameterNames the parameter names to insert in the result, or 
	 *   <code>null</code> if no parameter names are to be included; if supplied,
	 *   the number of parameter names must match that of the method signature
	 * @param fullyQualifyTypeNames <code>true</code> if type names should be fully
	 *   qualified, and <code>false</code> to use only simple names
	 * @param includeReturnType <code>true</code> if the return type is to be
	 *   included
	 * @return the string representation of the method signature
	 */
	public static String toString(
		String methodSignature,
		String methodName,
		String[] parameterNames,
		boolean fullyQualifyTypeNames,
		boolean includeReturnType) {
		char[][] params;
		if (parameterNames == null) {
			params = null;
		} else {
			int paramLength = parameterNames.length;
			params = new char[paramLength][];
			for (int i = 0; i < paramLength; i++) {
				params[i] = parameterNames[i].toCharArray();
			}
		}
		return new String(
			toCharArray(
				methodSignature.toCharArray(),
				methodName == null ? null : methodName.toCharArray(),
				params,
				fullyQualifyTypeNames,
				includeReturnType));
	}
}
