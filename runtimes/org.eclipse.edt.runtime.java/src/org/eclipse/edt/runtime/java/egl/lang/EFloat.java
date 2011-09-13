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

	public static Double ezeCast(Object value) throws AnyException {
		return (Double) EglAny.ezeCast(value, "asFloat", EFloat.class, null, null);
	}

	public String toString() {
		return EString.asString(object);
	}

	public static boolean ezeIsa(Object value) {
		return value instanceof EFloat || value instanceof Double;
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

	public static Double asFloat(String value) throws AnyException {
		if (value == null)
			return null;
		return asFloat(EDecimal.asDecimal(value));
	}

	public static double plus(double op1, double op2) throws AnyException {
		return op1 + op2;
	}

	public static double minus(double op1, double op2) throws AnyException {
		return op1 - op2;
	}

	public static double divide(double op1, double op2) throws AnyException {
		return op1 / op2;
	}

	public static double multiply(double op1, double op2) throws AnyException {
		return op1 * op2;
	}

	public static double remainder(double op1, double op2) throws AnyException {
		return op1 % op2;
	}

	public static double power(double op1, double op2) throws AnyException {
		return StrictMath.pow(op1, op2);
	}

	public static int compareTo(Double op1, Double op2) throws AnyException {
		if (op1 == null && op2 == null)
			return 0;
		if ((op1 != null && op2 == null) || (op1 == null && op2 != null))
			throw new NullValueException();
		return op1.compareTo(op2);
	}

	public static boolean equals(Double op1, Double op2) {
		if (op1 == null || op2 == null)
			return false;
		return op1.equals(op2);
	}

	public static boolean notEquals(Double op1, Double op2) {
		if (op1 == null || op2 == null)
			return false;
		return !op1.equals(op2);
	}
}
