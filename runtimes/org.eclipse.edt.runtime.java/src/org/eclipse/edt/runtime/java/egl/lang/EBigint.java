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
import java.math.BigInteger;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;

import egl.lang.AnyException;
import egl.lang.AnyNumber;
import egl.lang.NumericOverflowException;

public class EBigint extends AnyBoxedObject<Long> implements AnyNumber {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	private static final long DefaultValue = 0L;
	private static final int Precision = 18;

	private EBigint(Long value) {
		super(value);
	}

	public static EBigint ezeBox(Long value) {
		return new EBigint(value);
	}

	/**
	 * Dynamic implementation of data type conversion. Used when casting values of unknown type to the receiver type. This
	 * method looks up the appropriate conversion operation first in the receiver class and if not found then lookup will
	 * continue into the class of the <code>value</code> parameter.
	 * @param value
	 * @param args
	 * @return
	 * @throws AnyException
	 */
	public static Long ezeCast(Object value) throws AnyException {
		return (Long) EglAny.ezeCast(value, "asBigint", EBigint.class, null, null);
	}

	public static boolean ezeIsa(Object value) {
		return value instanceof EBigint || value instanceof Long;
	}

	public String toString() {
		return EString.asString(object);
	}

	public static Long asBigint(Short value) {
		if (value == null)
			return null;
		return value.longValue();
	}

	public static Long asBigint(ESmallint value) {
		if (value == null)
			return null;
		return value.ezeUnbox().longValue();
	}

	public static Long asBigint(Integer value) {
		if (value == null)
			return null;
		return value.longValue();
	}

	public static Long asBigint(EInt value) {
		if (value == null)
			return null;
		return value.ezeUnbox().longValue();
	}

	public static Long asBigint(Long value) {
		return value;
	}

	public static Long asBigint(EBigint value) {
		if (value == null)
			return null;
		return value.ezeUnbox();
	}

	public static Long asBigint(Float value) {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		long result = 0;
		if (throwOverflowExceptions)
			try {
				result = BigDecimal.valueOf(value).longValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = Float.valueOf(value).longValue();
		return result;
	}

	public static Long asBigint(ESmallfloat value) {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		long result = 0;
		if (throwOverflowExceptions)
			try {
				result = BigDecimal.valueOf(value.ezeUnbox()).longValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = Float.valueOf(value.ezeUnbox()).longValue();
		return result;
	}

	public static Long asBigint(Double value) {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		long result = 0;
		if (throwOverflowExceptions)
			try {
				result = BigDecimal.valueOf(value).longValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = Double.valueOf(value).longValue();
		return result;
	}

	public static Long asBigint(EFloat value) {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		long result = 0;
		if (throwOverflowExceptions)
			try {
				result = BigDecimal.valueOf(value.ezeUnbox()).longValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = Double.valueOf(value.ezeUnbox()).longValue();
		return result;
	}

	public static Long asBigint(BigDecimal value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		long result = 0;
		if (throwOverflowExceptions)
			try {
				result = value.longValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = value.longValue();
		return result;
	}

	public static Long asBigint(EDecimal value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		long result = 0;
		if (throwOverflowExceptions)
			try {
				result = value.ezeUnbox().longValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = value.ezeUnbox().longValue();
		return result;
	}

	public static Long asBigint(BigInteger value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		long result = 0;
		if (throwOverflowExceptions)
			try {
				result = new BigDecimal(value).longValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException();
			}
		else
			result = value.longValue();
		return result;
	}

	public static Long asBigint(String value) throws AnyException {
		return asBigint(EDecimal.asDecimal(value));
	}

	public static Long asBigint(EString value) throws AnyException {
		if (value == null)
			return null;
		return asBigint(EDecimal.asDecimal(value.ezeUnbox()));
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static BigDecimal asNumber(Long value) throws AnyException {
		if (value == null)
			return null;
		return EDecimal.asDecimal(value);
	}

	public static BigDecimal asNumber(EBigint value) throws AnyException {
		if (value == null)
			return null;
		return EDecimal.asDecimal(value.ezeUnbox());
	}

	public static long plus(long op1, long op2) throws AnyException {
		return op1 + op2;
	}

	public static long minus(long op1, long op2) throws AnyException {
		return op1 - op2;
	}

	public static BigDecimal divide(long op1, long op2) throws AnyException {
		return BigDecimal.valueOf(op1).divide(BigDecimal.valueOf(op2), EDecimal.BIGDECIMAL_RESULT_SCALE, EDecimal.ROUND_BD);
	}

	public static long multiply(long op1, long op2) throws AnyException {
		return op1 * op2;
	}

	public static long remainder(long op1, long op2) throws AnyException {
		return op1 % op2;
	}

	public static Double power(long op1, long op2) throws AnyException {
		return StrictMath.pow(op1, op2);
	}

	public static int compareTo(Long op1, Long op2) throws AnyException {
		if (op1 == null && op2 == null)
			return 0;
		return op1.compareTo(op2);
	}

	public static boolean equals(Long op1, Long op2) {
		if (op1 == null || op2 == null)
			return false;
		return op1.equals(op2);
	}

	public static boolean notEquals(Long op1, Long op2) {
		if (op1 == null || op2 == null)
			return false;
		return !op1.equals(op2);
	}

	public static long defaultValue() {
		return DefaultValue;
	}

	public static int precision() {
		return Precision;
	}
}
