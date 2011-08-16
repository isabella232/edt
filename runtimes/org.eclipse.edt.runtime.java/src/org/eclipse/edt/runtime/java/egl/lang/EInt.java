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
		return value instanceof EInt;
	}

	public String toString() {
		return EString.asString(object);
	}

	public static Integer asInt(Short value) throws AnyException {
		if (value == null)
			return null;
		return value.intValue();
	}

	public static Integer asInt(Integer value) throws AnyException {
		if (value == null)
			return null;
		return value.intValue();
	}

	public static Integer asInt(Long value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions)
			result = BigDecimal.valueOf(value).intValueExact();
		else
			result = Long.valueOf(value).intValue();
		return result;
	}

	public static Integer asInt(BigInteger value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions)
			result = new BigDecimal(value).intValueExact();
		else
			result = value.intValue();
		return result;
	}

	public static Integer asInt(Float value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;;
		if (throwOverflowExceptions)
			result = BigDecimal.valueOf(value).intValueExact();
		else
			result = value.intValue();
		return result;
	}

	public static Integer asInt(Double value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;;
		if (throwOverflowExceptions)
			result = BigDecimal.valueOf(value).intValueExact();
		else
			result = value.intValue();
		return result;
	}

	public static Integer asInt(BigDecimal value) throws AnyException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = false; // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions)
			result = value.intValueExact();
		else
			result = value.intValue();
		return result;
	}

	public static Integer asInt(String value) throws AnyException {
		if (value == null)
			return null;
		return asInt(EDecimal.asDecimal(value));
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static BigDecimal asNumber(Integer value) throws AnyException {
		if (value == null)
			return null;
		return EDecimal.asDecimal(value);
	}

	public static int defaultValue() {
		return DefaultValue;
	}

	public static int precision() {
		return Precision;
	}

	public static Integer plus(Integer op1, Integer op2) throws AnyException {
		if (op1 == null || op2 == null)
			return null;
		return BigDecimal.valueOf((long) op1 + op2).intValueExact();
	}

	public static Integer minus(Integer op1, Integer op2) throws AnyException {
		if (op1 == null || op2 == null)
			return null;
		return BigDecimal.valueOf((long) op1 - op2).intValueExact();
	}

	public static BigDecimal divide(Integer op1, Integer op2) throws AnyException {
		if (op1 == null || op2 == null)
			return null;
		return BigDecimal.valueOf(op1).divide(BigDecimal.valueOf(op2), EDecimal.BIGDECIMAL_RESULT_SCALE, EDecimal.ROUND_BD);
	}

	public static Integer multiply(Integer op1, Integer op2) throws AnyException {
		if (op1 == null || op2 == null)
			return null;
		return BigDecimal.valueOf((long) op1 * op2).intValueExact();
	}

	public static Integer remainder(Integer op1, Integer op2) throws AnyException {
		if (op1 == null || op2 == null)
			return null;
		return op1 % op2;
	}
	
	public static Double power(Integer op1, Integer op2) throws AnyException {
		if (op1 == null || op2 == null)
			return null;
		return StrictMath.pow( op1, op2 );
	}

	public static int compareTo(Integer op1, Integer op2) throws AnyException {
		if (op1 == null && op2 == null)
			return 0;
		if ((op1 != null && op2 == null) || (op1 == null && op2 != null))
			throw new NullValueException();
		return op1.compareTo(op2);
	}

	public static boolean equals(Integer op1, Integer op2) {
		if (op1 == null || op2 == null)
			return false;
		return op1.equals(op2);
	}

	public static boolean notEquals(Integer op1, Integer op2) {
		if (op1 == null || op2 == null)
			return false;
		return !op1.equals(op2);
	}
}
