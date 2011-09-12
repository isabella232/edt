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

public class ESmallfloat extends AnyBoxedObject<Float> implements AnyNumber {
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	private ESmallfloat(Float value) {
		super(value);
	}

	public static ESmallfloat ezeBox(Float value) {
		return new ESmallfloat(value);
	}

	public static Float ezeCast(Object value) throws AnyException {
		return (Float) EglAny.ezeCast(value, "asSmallfloat", ESmallfloat.class, null, null);
	}

	public static boolean ezeIsa(Object value) {
		return value instanceof ESmallfloat || value instanceof Float;
	}

	public String toString() {
		return EString.asString(object);
	}

	public static float asSmallfloat(short value) {
		return Float.valueOf(value);
	}

	public static float asSmallfloat(int value) {
		return Float.valueOf(value);
	}

	public static float asSmallfloat(long value) {
		return Float.valueOf(value);
	}

	public static float asSmallfloat(float value) throws AnyException {
		return value;
	}

	public static float asSmallfloat(double value) throws AnyException {
		return (float) value;
	}

	public static float asSmallfloat(BigDecimal value) {
		if (value == null)
			throw new NullValueException();
		return value.floatValue();
	}

	public static float asSmallfloat(String value) throws AnyException {
		if (value == null)
			throw new NullValueException();
		return asSmallfloat(EDecimal.asDecimal(value));
	}

	public static float plus(float op1, float op2) throws AnyException {
		return op1 + op2;
	}

	public static float minus(float op1, float op2) throws AnyException {
		return op1 - op2;
	}

	public static float divide(float op1, float op2) throws AnyException {
		return op1 / op2;
	}

	public static float multiply(float op1, float op2) throws AnyException {
		return op1 * op2;
	}

	public static float remainder(float op1, float op2) throws AnyException {
		return op1 % op2;
	}

	public static float power(float op1, float op2) throws AnyException {
		return (float) StrictMath.pow(op1, op2);
	}

	public static int compareTo(float op1, float op2) throws AnyException {
		return (int) (op1 - op2);
	}

	public static boolean equals(float op1, float op2) {
		return op1 == op2;
	}

	public static boolean notEquals(float op1, float op2) {
		return op1 != op2;
	}
}
