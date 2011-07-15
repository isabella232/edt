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


public class EInt64 extends AnyBoxedObject<Long> implements AnyNumber {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	private static final long DefaultValue = 0L;
	private static final int Precision = 18;

		
	private EInt64(Long value) { super(value); }
	
	public static EInt64 ezeBox(Long value) {
		return new EInt64(value);
	}
	/**
	 * Dynamic implementation of data type conversion.  Used when casting values
	 * of unknown type to the receiver type.  This method looks up the appropriate
	 * conversion operation first in the receiver class and if not found then
	 * lookup will continue into the class of the <code>value</code> parameter.
	 * 
	 * @param value
	 * @param args
	 * @return
	 * @throws JavartException
	 */
	public static Long ezeCast(Object value) throws JavartException {
		return (Long)AnyObject.ezeCast(value, "asInt64", EInt64.class, null, null);
	}
	
	public static boolean ezeIsa(Object value) {
		return value instanceof EInt64;
	}

	public static Long asInt64(Short value) {
		if (value == null) return null;
		return value.longValue();
	}
	
	public static Long asInt64(Integer value) {
		if (value == null) return null;
		return value.longValue();
	}
	
	public static Long asInt64(Long value) {
		return value;
	}
	
	public static Integer asInt64(BigDecimal value) throws JavartException {
		if (value == null) return null;
		boolean throwOverflowExceptions = true;  // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions) {
			result = value.intValueExact();
		}
		else {
			result = value.intValue();
		}
		return result;
	}

	public static long asInt64(String value) throws JavartException {
		return asInt64(EDecimal.asDecimal(value));
	}
	
	public static long plus(Long op1, Long op2) throws JavartException {
		if (op1 == null || op2 == null) throw new NullValueException();
		return BigDecimal.valueOf(op1).add(BigDecimal.valueOf(op2)).longValueExact();
	}

	public static long minus(Long op1, Long op2) throws JavartException {
		if (op1 == null || op2 == null) throw new NullValueException();
		return BigDecimal.valueOf(op1).subtract(BigDecimal.valueOf(op2)).longValueExact();
	}

	public static BigDecimal divide(Long op1, Long op2) throws JavartException {
		if (op1 == null || op2 == null) throw new NullValueException();
		return BigDecimal.valueOf(op1).divide( BigDecimal.valueOf(op2), EDecimal.BIGDECIMAL_RESULT_SCALE, EDecimal.ROUND_BD);
	}

	public static long multiply(Long op1, Long op2) throws JavartException {
		if (op1 == null || op2 == null) throw new NullValueException();
		return BigDecimal.valueOf(op1).multiply( BigDecimal.valueOf(op2) ).longValueExact();
	}

	public static long remainder(Long op1, Long op2) throws JavartException {
		if (op1 == null || op2 == null) throw new NullValueException();
		return op1 % op2;
	}

	public static int compareTo(Long op1, Long op2) throws JavartException {
		if (op1 == null || op2 == null) throw new NullValueException();
		return op1.compareTo(op2);
	}
	
	public static boolean equals(Long op1, Long op2) {
		if (op1 == null && op2 == null) return true;
		if ((op1 != null && op2 == null) || (op1 == null && op2 != null)) return false;
		return op1.equals(op2);
	}
	
	public static boolean notEquals(Long op1, Long op2) {
		return !equals(op1, op2);
	}

	public static long defaultValue() { return DefaultValue; }
	
	public static int precision() { return Precision; }
	
}
