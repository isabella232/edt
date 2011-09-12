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
import org.eclipse.edt.javart.Constants;

import egl.lang.*;

public class ESmallint extends AnyBoxedObject<Short> implements AnyNumber {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	private static final int precision = 4;

	private ESmallint(Short value) {
		super(value);
	}

	public static ESmallint ezeBox(Short value) {
		return new ESmallint(value);
	}

	public static Short ezeCast(Object value) throws AnyException {
		return (Short) EglAny.ezeCast(value, "asSmallint", ESmallint.class, null, null);
	}

	public static boolean ezeIsa(Object value) {
		return value instanceof ESmallint || value instanceof Short;
	}

	public String toString() {
		return EString.asString(object);
	}

	public static short asSmallint(short value) {
		return value;
	}

	public static short asSmallint(int value) throws AnyException {
		boolean throwOverflowExceptions = true; // TODO need program flag on whether to throw exceptions or not.
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

	public static short asSmallint(long value) throws AnyException {
		boolean throwOverflowExceptions = true; // TODO need program flag on whether to throw exceptions or not.
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

	public static short asSmallint(float value) throws AnyException {
		boolean throwOverflowExceptions = true; // TODO need program flag on whether to throw exceptions or not.
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

	public static short asSmallint(double value) throws AnyException {
		boolean throwOverflowExceptions = true; // TODO need program flag on whether to throw exceptions or not.
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

	public static short asSmallint(BigDecimal value) throws AnyException {
		if (value == null)
			throw new NullValueException();
		boolean throwOverflowExceptions = true; // TODO need program flag on whether to throw exceptions or not.
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

	public static short asSmallint(String value) throws AnyException {
		if (value == null)
			throw new NullValueException();
		return asSmallint(EDecimal.asDecimal(value));
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static BigDecimal asNumber(short value) throws AnyException {
		return EDecimal.asDecimal(value);
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

	public static int compareTo(short op1, short op2) throws AnyException {
		return (int) (op1 - op2);
	}

	public static boolean equals(short op1, short op2) {
		return op1 == op2;
	}

	public static boolean notEquals(short op1, short op2) {
		return op1 != op2;
	}

	public int precision() {
		return precision;
	}
}
