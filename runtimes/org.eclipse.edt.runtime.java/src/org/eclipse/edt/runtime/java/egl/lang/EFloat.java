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
import org.eclipse.edt.javart.JavartException;

import egl.lang.AnyNumber;

public class EFloat extends AnyBoxedObject<Double> implements AnyNumber {
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	private EFloat(Double value) {
		super(value);
	}

	public static EFloat ezeBox(Double value) {
		return new EFloat(value);
	}

	public static Double ezeCast(Object value) throws JavartException {
		return (Double) EglAny.ezeCast(value, "asFloat", EFloat.class, null, null);
	}

	public static boolean ezeIsa(Object value) {
		return value instanceof EFloat;
	}

	public static Double asFloat(Short value) {
		if (value == null)
			return null;
		return Double.valueOf(value);
	}

	public static Double asFloat(Integer value) {
		if (value == null)
			return null;
		return Double.valueOf(value);
	}

	public static Double asFloat(Long value) {
		if (value == null)
			return null;
		return Double.valueOf(value);
	}

	public static Double asFloat(Float value) {
		if (value == null)
			return null;
		return Double.valueOf(value);
	}

	public static Double asFloat(Double value) {
		if (value == null)
			return null;
		return Double.valueOf(value);
	}

	public static Double asFloat(BigDecimal value) {
		if (value == null)
			return null;
		return value.doubleValue();
	}

	public static Double asFloat(String value, Integer... length) throws JavartException {
		if (value == null)
			return null;
		return asFloat(EDecimal.asDecimal(EString.asString(value, length)));
	}

	public static double plus(Double op1, Double op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return op1 + op2;
	}

	public static double minus(Double op1, Double op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return op1 - op2;
	}

	public static double divide(Double op1, Double op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return op1 / op2;
	}

	public static double multiply(Double op1, Double op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return op1 * op2;
	}

	public static double remainder(Double op1, Double op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return op1 % op2;
	}
	
	public static double power(Double op1, Double op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return StrictMath.pow( op1, op2 );
	}

	public static int compareTo(Double op1, Double op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return op1.compareTo(op2);
	}

	public static boolean equals(Double op1, Double op2) {
		if (op1 == null && op2 == null)
			return true;
		if ((op1 != null && op2 == null) || (op1 == null && op2 != null))
			return false;
		return op1.equals(op2);
	}

	public static boolean notEquals(Double op1, Double op2) {
		return !equals(op1, op2);
	}
}
