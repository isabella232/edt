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
import egl.lang.NullValueException;
import egl.lang.NumericOverflowException;

public class EInt extends AnyBoxedObject<Integer> implements AnyNumber {
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
		return (Integer) EglAny.ezeCast(value, "asInt", EInt.class, null, null);
	}

	public static boolean ezeIsa(Object value) {
		return value instanceof EInt || value instanceof Integer;
	}

	public String toString() {
		return EString.asString(object);
	}

	public static int asInt(short value) throws AnyException {
		return value;
	}

	public static int asInt(int value) throws AnyException {
		return value;
	}

	public static int asInt(long value) throws AnyException {
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

	public static int asInt(BigInteger value) throws AnyException {
		if (value == null)
			throw new NullValueException();
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

	public static int asInt(float value) throws AnyException {
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

	public static int asInt(double value) throws AnyException {
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

	public static int asInt(BigDecimal value) throws AnyException {
		if (value == null)
			throw new NullValueException();
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

	public static int asInt(String value) throws AnyException {
		if (value == null)
			throw new NullValueException();
		return asInt(EDecimal.asDecimal(value));
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static BigDecimal asNumber(int value) throws AnyException {
		return EDecimal.asDecimal(value);
	}

	public static int defaultValue() {
		return DefaultValue;
	}

	public static int precision() {
		return Precision;
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
		return op1 - op2;
	}

	public static boolean equals(int op1, int op2) {
		return op1 == op2;
	}

	public static boolean notEquals(int op1, int op2) {
		return op1 != op2;
	}
}
