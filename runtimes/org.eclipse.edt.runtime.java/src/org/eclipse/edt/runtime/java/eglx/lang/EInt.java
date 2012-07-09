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
package org.eclipse.edt.runtime.java.eglx.lang;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.messages.Message;

import eglx.lang.*;

public class EInt extends AnyBoxedObject<Integer> implements eglx.lang.ENumber {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	private static final int DefaultValue = 0;
	private static final int Precision = 9;

	private EInt(Integer value) {
		super(value);
	}

	public static EInt ezeBox(Integer value) {
		return new EInt(value);
	}

	public static Integer ezeCast(Object value) throws AnyException {
		return (Integer) EAny.ezeCast(value, "asInt", EInt.class, null, null);
	}

	public static boolean ezeIsa(Object value) {
		return (value instanceof EInt && ((EInt) value).ezeUnbox() != null) || value instanceof Integer;
	}

	public String toString() {
		return EString.asString(object);
	}

	public static Integer asInt(Short value) throws AnyException {
		if (value == null)
			return null;
		return value.intValue();
	}

	public static Integer asInt(ESmallint value) throws AnyException {
		if (value == null)
			return null;
		return value.ezeUnbox().intValue();
	}

	public static Integer asInt(Integer value) throws AnyException {
		if (value == null)
			return null;
		return value.intValue();
	}

	public static Integer asInt(EInt value) throws AnyException {
		if (value == null)
			return null;
		return value.ezeUnbox().intValue();
	}

	public static Integer asInt(Long value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions)
			try {
				result = BigDecimal.valueOf(value).intValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = Long.valueOf(value).intValue();
		return result;
	}

	public static Integer asInt(EBigint value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions)
			try {
				result = BigDecimal.valueOf(value.ezeUnbox()).intValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = Long.valueOf(value.ezeUnbox()).intValue();
		return result;
	}

	public static Integer asInt(Float value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions)
			try {
				result = BigDecimal.valueOf(value).intValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = Float.valueOf(value).intValue();
		return result;
	}

	public static Integer asInt(ESmallfloat value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions)
			try {
				result = BigDecimal.valueOf(value.ezeUnbox()).intValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = Float.valueOf(value.ezeUnbox()).intValue();
		return result;
	}

	public static Integer asInt(Double value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions)
			try {
				result = BigDecimal.valueOf(value).intValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = Double.valueOf(value).intValue();
		return result;
	}

	public static Integer asInt(EFloat value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions)
			try {
				result = BigDecimal.valueOf(value.ezeUnbox()).intValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = Double.valueOf(value.ezeUnbox()).intValue();
		return result;
	}

	public static Integer asInt(BigDecimal value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions)
			try {
				result = value.intValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = value.intValue();
		return result;
	}

	public static Integer asInt(EDecimal value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions)
			try {
				result = value.ezeUnbox().intValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = value.ezeUnbox().intValue();
		return result;
	}

	public static Integer asInt(BigInteger value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions)
			try {
				result = new BigDecimal(value).intValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = value.intValue();
		return result;
	}

	public static Integer asInt(Number value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions)
			try {
				result = value.intValue();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = value.intValue();
		return result;
	}

	public static Integer asInt(eglx.lang.ENumber value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions)
			try {
				result = value.ezeUnbox().intValue();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = value.ezeUnbox().intValue();
		return result;
	}

	public static Integer asInt(String value) throws TypeCastException {
		if (value == null)
			return null;
		try
		{
			// Remove a leading plus.
			String input = value.charAt( 0 ) == '+' ? value.substring( 1 ) : value;
			return Integer.valueOf( input );
		}
		catch ( Exception ex )
		{
			// Length is zero (charAt failed) or invalid number (valueOf failed).
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "int";
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, value, tcx.actualTypeName, tcx.castToName );
		}
	}

	public static Integer asInt(EString value) throws AnyException {
		if (value == null)
			return null;
		return asInt(value.ezeUnbox());
	}
	
	public static Integer asInt( byte[] value ) throws AnyException 
	{
		if ( value == null )
		{
			return null;
		}
		
		if ( value.length != 4 )
		{
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "bytes(" + value.length + ')';
			tcx.castToName = "int";
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, value, tcx.actualTypeName, tcx.castToName );
		}
		
		return ((value[ 0 ] & 0xFF) << 24)
				| ((value[ 1 ] & 0xFF) << 16)
				| ((value[ 2 ] & 0xFF) << 8)
				| (value[ 3 ] & 0xFF);
	}

	public static Integer asInt( EBytes value ) throws AnyException 
	{
		if ( value == null )
		{
			return null;
		}
		return asInt( value.ezeUnbox() );
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static EInt asNumber(Integer value) throws AnyException {
		if (value == null)
			return null;
		return EInt.ezeBox(value);
	}

	public static EInt asNumber(EInt value) throws AnyException {
		if (value == null)
			return null;
		return value;
	}

	public static int defaultValue() {
		return DefaultValue;
	}

	public static int precision() {
		return Precision;
	}
	
	public static int bitnot(Integer op) throws AnyException {
		if (op == null) {
			NullValueException nvx = new NullValueException();
			throw nvx.fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		return ~op;
	}

	public static int negate(Integer op) throws AnyException {
		if (op == null) {
			NullValueException nvx = new NullValueException();
			throw nvx.fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		return -op;
	}

	public static int plus(int op1, int op2) throws AnyException {
		return op1 + op2;
	}

	public static int minus(int op1, int op2) throws AnyException {
		return op1 - op2;
	}

	public static BigDecimal divide(int op1, int op2) throws AnyException {
		return BigDecimal.valueOf(op1).divide(BigDecimal.valueOf(op2), EDecimal.BIGDECIMAL_RESULT_SCALE, EDecimal.ROUND_BD);
	}

	public static int multiply(int op1, int op2) throws AnyException {
		return op1 * op2;
	}

	public static int remainder(int op1, int op2) throws AnyException {
		return op1 % op2;
	}

	public static double power(int op1, int op2) throws AnyException {
		return StrictMath.pow(op1, op2);
	}

	public static int bitand(int op1, int op2) throws AnyException {
		return op1 & op2;
	}

	public static int bitor(int op1, int op2) throws AnyException {
		return op1 | op2;
	}

	public static int xor(int op1, int op2) throws AnyException {
		return op1 ^ op2;
	}

	public static int compareTo(int op1, int op2) throws AnyException {
		return Integer.valueOf(op1).compareTo(op2);
	}

	public static boolean equals(Integer op1, Integer op2) {
		if (op1 == op2)
			return true;
		if (op1 == null || op2 == null)
			return false;
		return op1.compareTo(op2) == 0;
	}

	public static boolean equals(Short op1, Integer op2) {
		if (op1 == null && op2 == null)
			return true;
		if (op1 == null || op2 == null)
			return false;
		return ((Integer) op1.intValue()).compareTo(op2) == 0;
	}

	public static boolean equals(Integer op1, Short op2) {
		if (op1 == null && op2 == null)
			return true;
		if (op1 == null || op2 == null)
			return false;
		return op1.compareTo(op2.intValue()) == 0;
	}

	public static boolean notEquals(Integer op1, Integer op2) {
		return !equals(op1, op2);
	}

	public static boolean notEquals(Short op1, Integer op2) {
		return !equals(op1, op2);
	}

	public static boolean notEquals(Integer op1, Short op2) {
		return !equals(op1, op2);
	}
	public static int leftShift(int op1, int op2) {
		return op1 << op2;
	}

	public static int rightShiftArithmetic(int op1, int op2) {
		return op1 >> op2;
	}

	public static int rightShiftLogical(int op1, int op2) {
		return op1 >>> op2;
	}
}
