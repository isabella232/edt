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

public class EInt16 extends AnyBoxedObject<Short> implements AnyNumber {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	private static final int precision = 4;

	private EInt16(Short value) { super(value); }
	
	public static EInt16 ezeBox(Short value) {
		return new EInt16(value);
	}

	public static Short ezeCast(Object value) throws JavartException {
		return (Short)AnyObject.ezeCast(value, "asInt16", EInt16.class, null, null);
	}
	
	public static boolean ezeIsa(Object value) {
		return value instanceof EInt16;
	}

	public static Short asInt16(Short value) {
		if (value == null) return null;
		return value;
	}
	
	public static Short asInt16(Integer value) throws JavartException {
		if (value == null) return null;

		boolean throwOverflowExceptions = true;  // TODO need program flag on whether to throw exceptions or not.
		short result = 0;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException(ex.getLocalizedMessage());
			}
		}
		else {
			result = Long.valueOf(value).shortValue();
		}
		return result;
	}
	
	
	public static Short asInt16(Long value) throws JavartException {
		if (value == null) return null;

		boolean throwOverflowExceptions = true;  // TODO need program flag on whether to throw exceptions or not.
		short result = 0;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException(ex.getLocalizedMessage());
			}
		}
		else {
			result = Long.valueOf(value).shortValue();
		}
		return result;
	}
	
	
	public static Short asInt16(Float value) throws JavartException {
		if (value == null) return null;
		
		boolean throwOverflowExceptions = true;  // TODO need program flag on whether to throw exceptions or not.
		short result = 0;;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException(ex.getLocalizedMessage());
			}
		}
		else {
			result = value.shortValue();
		}
		return result;
	}

	public static Short asInt16(Double value) throws JavartException {
		if (value == null) return null;
		
		boolean throwOverflowExceptions = true;  // TODO need program flag on whether to throw exceptions or not.
		short result = 0;;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException(ex.getLocalizedMessage());
			}
		}
		else {
			result = value.shortValue();
		}
		return result;
	}
	
	public static Short asInt16(BigDecimal value) throws JavartException {
		if (value == null) return null;
		
		boolean throwOverflowExceptions = true;  // TODO need program flag on whether to throw exceptions or not.
		short result = 0;;
		if (throwOverflowExceptions) {
			try {
				result = value.shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException(ex.getLocalizedMessage());
			}
		}
		else {
			result = value.shortValue();
		}
		return result;
	}

	public static Short asInt16(String value) throws JavartException {
		if (value == null) return null;

		return asInt16(EDecimal.asDecimal(value));
	}
	
	public static int plus(Short op1, Short op2) throws JavartException  {
		if (op1 == null || op2 == null) throw new NullValueException();
		return op1 + op2;
	}

	public static int minus(Short op1, Short op2) throws JavartException {
		if (op1 == null || op2 == null) throw new NullValueException();
		return op1 - op2;
	}

	public static BigDecimal divide(Short op1, Short op2) throws JavartException  {
		if (op1 == null || op2 == null) throw new NullValueException();
		return BigDecimal.valueOf(op1).divide( BigDecimal.valueOf(op2), EDecimal.BIGDECIMAL_RESULT_SCALE, EDecimal.ROUND_BD);
	}

	public static int multiply(Short op1, Short op2) throws JavartException {
		if (op1 == null || op2 == null) throw new NullValueException();
		try {
			return BigDecimal.valueOf(op1 * op2).intValueExact();
		}
		catch (ArithmeticException ex) {
			throw new NumericOverflowException(ex.getLocalizedMessage());
		}
	}

	public static int remainder(Short op1, Short op2) throws JavartException  {
		if (op1 == null || op2 == null) throw new NullValueException();
		return op1 % op2;
	}

	public static int compareTo(Short op1, Short op2) throws JavartException {
		if (op1 == null && op2 == null) return 0;
		if ((op1 != null && op2 == null) || (op1 == null && op2 != null)) throw new NullValueException();
		return op1.compareTo(op2);
	}
	
	public static boolean equals(Short op1, Short op2) {
		if (op1 == null && op2 == null) return true;
		if ((op1 != null && op2 == null) || (op1 == null && op2 != null)) return false;
		return op1.equals(op2);
	}
	
	public static boolean notEquals(Short op1, Short op2) {
		return !equals(op1, op2);
	}
		
	public int precision() {
		return precision;
	}
	
}
