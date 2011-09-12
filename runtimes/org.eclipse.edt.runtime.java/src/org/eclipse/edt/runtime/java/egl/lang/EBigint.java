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

import egl.lang.AnyException;
import egl.lang.AnyNumber;
import egl.lang.NullValueException;
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

	public static long asBigint(short value) {
		return value;
	}

	public static long asBigint(int value) {
		return value;
	}

	public static long asBigint(long value) {
		return value;
	}

	public static long asBigint(float value) {
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

	public static long asBigint(double value) {
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

	public static long asBigint(BigDecimal value) throws AnyException {
		if (value == null)
			throw new NullValueException();
		boolean throwOverflowExceptions = true; // TODO need program flag on whether to throw exceptions or not.
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

	public static long asBigint(String value) throws AnyException {
		if (value == null)
			throw new NullValueException();
		return asBigint(EDecimal.asDecimal(value));
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static BigDecimal asNumber(long value) throws AnyException {
		return EDecimal.asDecimal(value);
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

	public static int compareTo(long op1, long op2) throws AnyException {
		return (int) (op1 - op2);
	}

	public static boolean equals(long op1, long op2) {
		return op1 == op2;
	}

	public static boolean notEquals(long op1, long op2) {
		return op1 != op2;
	}

	public static long defaultValue() {
		return DefaultValue;
	}

	public static int precision() {
		return Precision;
	}
}
