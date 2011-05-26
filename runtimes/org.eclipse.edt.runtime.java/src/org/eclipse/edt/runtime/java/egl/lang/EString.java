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

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.JavartException;

public class EString extends AnyBoxedObject<String> {
	private static final long serialVersionUID = 80L;
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
	
	/**
	 * AnyString AS Number conversion operation
	 * @param program
	 * @param value
	 * @return
	 * @throws JavartException
	 */
	public static BigDecimal asNumber(Executable program, String value) throws JavartException {
		if (value == null) return null;
		return EDecimal.asDecimal(program, value);
	}
	
	public static String ezeCast(Object value, Integer...args) throws JavartException {
		return (String)AnyObject.ezeCast(value, "asString", EString.class, new Class[]{Integer[].class}, args);
	}
	
	public static boolean ezeIsa(Object value, Integer...length) {
		boolean isa = value instanceof EString;
		if (isa && length.length != 0) {
			isa = ((EString)value).getLength() == length[0];
		}
		return isa;
	}
	
	public static String asString(Executable program, Short value, Integer...length) {
		if (value == null) return null;
		return asString(program, String.valueOf(value), length);
			
	}

	public static String asString(Executable program, Integer value, Integer...length) {
		if (value == null) return null;
		return asString(program, String.valueOf(value), length);
	}

	public static String asString(Executable program, Long value, Integer...length) {
		if (value == null) return null;
		return asString(program, String.valueOf(value), length);
	}
	
	public static String asString(Executable program, Float value, Integer...length) {
		if (value == null) return null;
		return asString(program, String.valueOf(value), length);
	}
	
	public static String asString(Executable program, Double value, Integer...length) {
		if (value == null) return null;
		return asString(program, String.valueOf(value), length);
	}
	
	public static String asString(Executable program, BigDecimal value, Integer...length) {
		if (value == null) return null;
		return asString(program, String.valueOf(value), length);
	}

	public static String asString(Executable program, String value, Integer...length) {
		if (length.length != 0 && value.length() > length[0]) {
			value = value.substring(0, length[0]);
		}
		return value;
	}
		
	public static String plus(Executable program, String op1, String op2) throws JavartException {
		if (op1 == null || op2 == null) {
			throw new NullValueException();
		}
		return op1 + op2;
	}
	
	public static String concat(Executable program, String op1, String op2) throws JavartException {
		if (op1 == null || op2 == null) {
			throw new NullValueException();
		}
		return op1 + op2;
	}
	
	public static String concatNull(Executable program, String op1, String op2) {
		if (op1 == null || op2 == null) return null;
		return op1 + op2;
	}
	
	public static boolean equals(Executable program, String op1, String op2) throws JavartException {
		if (op1 == null || op2 == null) {
			throw new NullValueException();
		}
		return op1.equals(op2);
	}
	
	public static boolean notEquals(Executable program, String op1, String op2) throws JavartException {
		return !equals(program, op1, op2);
	}
	
	public static String substring(String str, Integer startIndex, Integer endIndex) throws JavartException {
		if (str == null || startIndex == null || endIndex == null) {
			throw new NullValueException();
		}

		int start = startIndex;
		int end = endIndex;
		int max = str.length();

		if ( start < 1 || start > max )
		{
			IndexOutOfBoundsException ex = new IndexOutOfBoundsException();
			ex.indexValue = start;
			throw ex;
		}
		else if ( end < start || end < 1 || end > max )
		{
			IndexOutOfBoundsException ex = new IndexOutOfBoundsException();
			ex.indexValue = end;
			throw ex;
		}
		
		return str.substring( start - 1, end );
	}
	
	public static String substringAssign(String str, String newValue, Integer startIndex, Integer endIndex) throws JavartException {
		if (str == null || newValue == null || startIndex == null || endIndex == null) {
			throw new NullValueException();
		}

		int start = startIndex;
		int end = endIndex;
		int max = str.length();

		if ( start < 1 || start > max )
		{
			IndexOutOfBoundsException ex = new IndexOutOfBoundsException();
			ex.indexValue = start;
			throw ex;
		}
		else if ( end < start || end < 1 || end > max )
		{
			IndexOutOfBoundsException ex = new IndexOutOfBoundsException();
			ex.indexValue = end;
			throw ex;
		}
		
		int valLength = newValue.length();
		int subLength = end - start + 1;
		
		String substringValue;
		if ( valLength > subLength )
		{
			substringValue = newValue.substring( 0, subLength );
		}
		else if ( valLength < subLength )
		{
			StringBuilder paddedBuf = new StringBuilder( newValue );
			for ( int i = valLength; i < subLength; i++ )
			{
				paddedBuf.append( ' ' );
			}
			substringValue = paddedBuf.toString();
		}
		else
		{
			substringValue = newValue;
		}
		
		StringBuilder buf = new StringBuilder( str );
		buf.replace( startIndex - 1, endIndex, substringValue );
		
		return buf.toString();
	}
}
