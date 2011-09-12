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
		return value instanceof EFloat;
	}

	public static double asFloat(short value) {
		return Double.valueOf(value);
	}

	public static double asFloat(int value) {
		return Double.valueOf(value);
	}

	public static double asFloat(long value) {
		return Double.valueOf(value);
	}

	public static double asFloat(float value) {
		return Double.valueOf(value);
	}

	public static double asFloat(double value) {
		return value;
	}

	public static double asFloat(BigDecimal value) {
		if (value == null)
			throw new NullValueException();
		return value.doubleValue();
	}

	public static double asFloat(String value) throws AnyException {
		if (value == null)
			throw new NullValueException();
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

	public static int compareTo(double op1, double op2) throws AnyException {
		return (int) (op1 - op2);
	}

	public static boolean equals(double op1, double op2) {
		return op1 == op2;
	}

	public static boolean notEquals(double op1, double op2) {
		return op1 != op2;
	}
}
