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

import eglx.lang.AnyException;
import eglx.lang.NumericOverflowException;
import eglx.lang.TypeCastException;

public class ESmallint extends AnyBoxedObject<Short> implements eglx.lang.ENumber {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	private static final int precision = 4;

	private ESmallint(Short value) {
		super(value);
	}

	public static ESmallint ezeBox(Short value) {
		return new ESmallint(value);
	}

	public static Short ezeCast(Object value) throws AnyException {
		return (Short) EAny.ezeCast(value, "asSmallint", ESmallint.class, null, null);
	}

	public static boolean ezeIsa(Object value) {
		return value instanceof ESmallint || value instanceof Short;
	}

	public String toString() {
		return EString.asString(object);
	}

	public static Short asSmallint(Short value) {
		if (value == null)
			return null;
		return value;
	}

	public static Short asSmallint(ESmallint value) {
		if (value == null)
			return null;
		return value.ezeUnbox();
	}

	public static Short asSmallint(Integer value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		} else
			result = Integer.valueOf(value).shortValue();
		return result;
	}

	public static Short asSmallint(EInt value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value.ezeUnbox()).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		} else
			result = Integer.valueOf(value.ezeUnbox()).shortValue();
		return result;
	}

	public static Short asSmallint(Long value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		} else
			result = Long.valueOf(value).shortValue();
		return result;
	}

	public static Short asSmallint(EBigint value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value.ezeUnbox()).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		} else
			result = Long.valueOf(value.ezeUnbox()).shortValue();
		return result;
	}

	public static Short asSmallint(Float value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		} else
			result = Float.valueOf(value).shortValue();
		return result;
	}

	public static Short asSmallint(ESmallfloat value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value.ezeUnbox()).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		} else
			result = Float.valueOf(value.ezeUnbox()).shortValue();
		return result;
	}

	public static Short asSmallint(Double value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		} else
			result = Double.valueOf(value).shortValue();
		return result;
	}

	public static Short asSmallint(EFloat value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value.ezeUnbox()).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		} else
			result = Double.valueOf(value.ezeUnbox()).shortValue();
		return result;
	}

	public static Short asSmallint(BigDecimal value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;;
		if (throwOverflowExceptions) {
			try {
				result = value.shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		} else
			result = value.shortValue();
		return result;
	}

	public static Short asSmallint(EDecimal value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;;
		if (throwOverflowExceptions) {
			try {
				result = value.ezeUnbox().shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		} else
			result = value.ezeUnbox().shortValue();
		return result;
	}

	public static Short asSmallint(BigInteger value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;
		if (throwOverflowExceptions)
			try {
				result = new BigDecimal(value).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = value.shortValue();
		return result;
	}

	public static Short asSmallint(Number value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;;
		if (throwOverflowExceptions) {
			try {
				result = value.shortValue();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		} else
			result = value.shortValue();
		return result;
	}

	public static Short asSmallint(eglx.lang.ENumber value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;;
		if (throwOverflowExceptions) {
			try {
				result = ((Number) value.ezeUnbox()).shortValue();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		} else
			result = ((Number) value.ezeUnbox()).shortValue();
		return result;
	}

	public static Short asSmallint(String value) throws AnyException {
		if (value == null)
			return null;
		try
		{
			// Remove a leading plus.
			String input = value.charAt( 0 ) == '+' ? value.substring( 1 ) : value;
			return Short.valueOf( input );
		}
		catch ( Exception ex )
		{
			// Length is zero (charAt failed) or invalid number (valueOf failed).
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "smallint";
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, value, tcx.actualTypeName, tcx.castToName );
		}
	}

	public static Short asSmallint(EString value) throws AnyException {
		if (value == null)
			return null;
		return asSmallint(value.ezeUnbox());
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static ESmallint asNumber(Short value) throws AnyException {
		if (value == null)
			return null;
		return ESmallint.ezeBox(value);
	}

	public static ESmallint asNumber(ESmallint value) throws AnyException {
		if (value == null)
			return null;
		return value;
	}

	public static int plus(short op1, short op2) throws AnyException {
		return op1 + op2;
	}

	public static int minus(short op1, short op2) throws AnyException {
		return op1 - op2;
	}

	public static BigDecimal divide(short op1, short op2) throws AnyException {
		return BigDecimal.valueOf(op1).divide(BigDecimal.valueOf(op2), EDecimal.BIGDECIMAL_RESULT_SCALE, EDecimal.ROUND_BD);
	}

	public static int multiply(short op1, short op2) throws AnyException {
		return op1 * op2;
	}

	public static int remainder(short op1, short op2) throws AnyException {
		return op1 % op2;
	}

	public static double power(short op1, short op2) throws AnyException {
		return StrictMath.pow(op1, op2);
	}

	public static int compareTo(Short op1, Short op2) throws AnyException {
		if (op1 == null && op2 == null)
			return 0;
		return op1.compareTo(op2);
	}

	public static boolean equals(Short op1, Short op2) {
		if (op1 == null && op2 == null)
			return true;
		if (op1 == null || op2 == null)
			return false;
		return op1.compareTo(op2) == 0;
	}

	public static boolean notEquals(Short op1, Short op2) {
		return !equals(op1, op2);
	}

	public int precision() {
		return precision;
	}
	
	public static int bitand(short op1, short op2) throws AnyException {
		return op1 & op2;
	}

	public static int bitor(short op1, short op2) throws AnyException {
		return op1 | op2;
	}

	public static int xor(short op1, short op2) throws AnyException {
		return op1 ^ op2;
	}

	public static Short leftShift(Short op1, Short op2) {
		return (short)(op1 << op2);
	}

	public static Short rightShiftArithmetic(Short op1, Short op2) {
		return (short)(op1 >> op2);
	}

	public static Short rightShiftLogical(Short op1, Short op2) {
		return (short)(op1 >>> op2);
	}
}
