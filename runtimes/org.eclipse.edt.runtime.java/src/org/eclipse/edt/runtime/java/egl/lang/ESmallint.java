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
		return value instanceof ESmallint;
	}

	public String toString() {
		return EString.asString(object);
	}

	public static Short asSmallint(Short value) {
		if (value == null)
			return null;
		return value;
	}

	public static Short asSmallint(Integer value) throws AnyException {
		if (value == null)
			return null;
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

	public static Short asSmallint(Long value) throws AnyException {
		if (value == null)
			return null;
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

	public static Short asSmallint(Float value) throws AnyException {
		if (value == null)
			return null;
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
			result = value.shortValue();
		return result;
	}

	public static Short asSmallint(Double value) throws AnyException {
		if (value == null)
			return null;
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
			result = value.shortValue();
		return result;
	}

	public static Short asSmallint(BigDecimal value) throws AnyException {
		if (value == null)
			return null;
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

	public static Short asSmallint(String value) throws AnyException {
		if (value == null)
			return null;
		return asSmallint(EDecimal.asDecimal(value));
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static BigDecimal asNumber(Short value) throws AnyException {
		if (value == null)
			return null;
		return EDecimal.asDecimal(value);
	}

	public static Integer plus(Short op1, Short op2) throws AnyException {
		if (op1 == null || op2 == null)
			return null;
		return op1 + op2;
	}

	public static Integer minus(Short op1, Short op2) throws AnyException {
		if (op1 == null || op2 == null)
			return null;
		return op1 - op2;
	}

	public static BigDecimal divide(Short op1, Short op2) throws AnyException {
		if (op1 == null || op2 == null)
			return null;
		return BigDecimal.valueOf(op1).divide(BigDecimal.valueOf(op2), EDecimal.BIGDECIMAL_RESULT_SCALE, EDecimal.ROUND_BD);
	}

	public static Integer multiply(Short op1, Short op2) throws AnyException {
		if (op1 == null || op2 == null)
			return null;
		return op1 * op2;
	}

	public static Integer remainder(Short op1, Short op2) throws AnyException {
		if (op1 == null || op2 == null)
			return null;
		return op1 % op2;
	}

	public static Double power(Short op1, Short op2) throws AnyException {
		if (op1 == null || op2 == null)
			return null;
		return StrictMath.pow(op1, op2);
	}

	public static int compareTo(Short op1, Short op2) throws AnyException {
		if (op1 == null && op2 == null)
			return 0;
		if ((op1 != null && op2 == null) || (op1 == null && op2 != null))
			throw new NullValueException();
		return op1.compareTo(op2);
	}

	public static boolean equals(Short op1, Short op2) {
		if (op1 == null || op2 == null)
			return false;
		return op1.equals(op2);
	}

	public static boolean notEquals(Short op1, Short op2) {
		if (op1 == null || op2 == null)
			return false;
		return !op1.equals(op2);
	}

	public int precision() {
		return precision;
	}
}
