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
package org.eclipse.edt.runtime.java.eglx.lang;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;

import eglx.lang.AnyException;

public class EFloat extends AnyBoxedObject<Double> implements eglx.lang.ENumber {
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
		return (Double) EAny.ezeCast(value, "asFloat", EFloat.class, null, null);
	}

	public String toString() {
		return EString.asString(object);
	}

	public static boolean ezeIsa(Object value) {
		if (value instanceof ENumber && ((ENumber) value).ezeUnbox() instanceof Double)
			return true;
		return value instanceof EFloat || value instanceof Double;
	}

	public static Double asFloat(Short value) {
		if (value == null)
			return null;
		return Double.valueOf(value);
	}

	public static Double asFloat(ESmallint value) {
		if (value == null)
			return null;
		return Double.valueOf(value.ezeUnbox());
	}

	public static Double asFloat(Integer value) {
		if (value == null)
			return null;
		return Double.valueOf(value);
	}

	public static Double asFloat(EInt value) {
		if (value == null)
			return null;
		return Double.valueOf(value.ezeUnbox());
	}

	public static Double asFloat(Long value) {
		if (value == null)
			return null;
		return Double.valueOf(value);
	}

	public static Double asFloat(EBigint value) {
		if (value == null)
			return null;
		return Double.valueOf(value.ezeUnbox());
	}

	public static Double asFloat(Float value) {
		if (value == null)
			return null;
		return Double.valueOf(value);
	}

	public static Double asFloat(ESmallfloat value) {
		if (value == null)
			return null;
		return Double.valueOf(value.ezeUnbox());
	}

	public static Double asFloat(Double value) {
		if (value == null)
			return null;
		return Double.valueOf(value);
	}

	public static Double asFloat(EFloat value) {
		if (value == null)
			return null;
		return Double.valueOf(value.ezeUnbox());
	}

	public static Double asFloat(BigDecimal value) {
		if (value == null)
			return null;
		return value.doubleValue();
	}

	public static Double asFloat(EDecimal value) {
		if (value == null)
			return null;
		return value.ezeUnbox().doubleValue();
	}

	public static Double asFloat(BigInteger value) {
		if (value == null)
			return null;
		return value.doubleValue();
	}

	public static Double asFloat(Number value) {
		if (value == null)
			return null;
		return value.doubleValue();
	}

	public static Double asFloat(ENumber value) {
		if (value == null)
			return null;
		return value.ezeUnbox().doubleValue();
	}

	public static Double asFloat(String value) throws AnyException {
		if (value == null)
			return null;
		return asFloat(EDecimal.asDecimal(value));
	}

	public static Double asFloat(EString value) throws AnyException {
		if (value == null)
			return null;
		return asFloat(EDecimal.asDecimal(value.ezeUnbox()));
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static ENumber asNumber(Double value) throws AnyException {
		if (value == null)
			return null;
		return ENumber.asNumber(value);
	}

	public static ENumber asNumber(EFloat value) throws AnyException {
		if (value == null)
			return null;
		return ENumber.asNumber(value.ezeUnbox());
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
		return op1.compareTo(op2);
	}

	public static boolean equals(Double op1, Double op2) {
		if (op1 == null || op2 == null)
			return false;
		return op1.compareTo(op2) == 0;
	}

	public static boolean notEquals(Double op1, Double op2) {
		if (op1 == null || op2 == null)
			return false;
		return op1.compareTo(op2) != 0;
	}
}
