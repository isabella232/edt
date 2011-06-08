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
import org.eclipse.edt.javart.JavartException;

import egl.lang.AnyNumber;

public class EFloat64 extends AnyBoxedObject<Double> implements AnyNumber {
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = 70L;
		
	private EFloat64(Double value) { super(value); }
	
	public static EFloat64 ezeBox(Double value) {
		return new EFloat64(value);
	}

	public static Double ezeCast(Object value) throws JavartException {
		return (Double)AnyObject.ezeCast(value, "asFloat64", EFloat64.class, null, null);
	}
	
	public static boolean ezeIsa(Object value) {
		return value instanceof EFloat64;
	}

	public static Double asFloat64(Short value) {
		if (value == null) return null;
		return Double.valueOf(value);
	}
	
	public static Double asFloat64(Integer value) {
		if (value == null) return null;
		return Double.valueOf(value);
	}
	
	public static Double asFloat64(Long value) {
		if (value == null) return null;
		return Double.valueOf(value);
	}
	
	public static Double asFloat64(Float value) {
		if (value == null) return null;
		return Double.valueOf(value);
	}

	public static Double asFloat64(BigDecimal value) {
		if (value == null) return null;
		return value.doubleValue();

	}

	public static Double asFloat64(String value) throws JavartException {
		if (value == null) return null;
		return asFloat64(EDecimal.asDecimal(value));
	}
	
	public static int compareTo(Double op1, Double op2) throws JavartException {
		if (op1 == null || op2 == null) {
			throw new NullValueException();
		}
		return op1.compareTo(op2);
	}
	
	public static boolean equals(Double op1, Double op2) {
		if (op1 == null && op2 == null) return true;
		if ((op1 != null && op2 == null) || (op1 == null && op2 != null)) return false;
		return op1.equals(op2);
	}
	
	public static boolean notEquals(Double op1, Double op2) {
		return !equals(op1, op2);
	}

}
