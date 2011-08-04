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

public class ESmallint extends AnyBoxedObject<Short> implements AnyNumber {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	private static final int precision = 4;

	private ESmallint(Short value) {
		super(value);
	}

	public static ESmallint ezeBox(Short value) {
		return new ESmallint(value);
	}

	public static Short ezeCast(Object value) throws JavartException {
		return (Short) EglAny.ezeCast(value, "asSmallint", ESmallint.class, null, null);
	}

	public static boolean ezeIsa(Object value) {
		return value instanceof ESmallint;
	}

	public static Short asSmallint(Short value) {
		if (value == null)
			return null;
		return value;
	}

	public static Short asSmallint(Integer value) throws JavartException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = true; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException(ex.getLocalizedMessage());
			}
		} else
			result = Long.valueOf(value).shortValue();
		return result;
	}

	public static Short asSmallint(Long value) throws JavartException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = true; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException(ex.getLocalizedMessage());
			}
		} else
			result = Long.valueOf(value).shortValue();
		return result;
	}

	public static Short asSmallint(Float value) throws JavartException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = true; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException(ex.getLocalizedMessage());
			}
		} else
			result = value.shortValue();
		return result;
	}

	public static Short asSmallint(Double value) throws JavartException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = true; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;;
		if (throwOverflowExceptions) {
			try {
				result = BigDecimal.valueOf(value).shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException(ex.getLocalizedMessage());
			}
		} else
			result = value.shortValue();
		return result;
	}

	public static Short asSmallint(BigDecimal value) throws JavartException {
		if (value == null)
			return null;
		boolean throwOverflowExceptions = true; // TODO need program flag on whether to throw exceptions or not.
		short result = 0;;
		if (throwOverflowExceptions) {
			try {
				result = value.shortValueExact();
			}
			catch (ArithmeticException ex) {
				throw new NumericOverflowException(ex.getLocalizedMessage());
			}
		} else
			result = value.shortValue();
		return result;
	}

	public static Short asSmallint(String value, Integer... length) throws JavartException {
		if (value == null)
			return null;
		return asSmallint(EDecimal.asDecimal(EString.asString(value, length)));
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static BigDecimal asNumber(Short value) throws JavartException {
		if (value == null)
			return null;
		return EDecimal.asDecimal(value);
	}

	public static int plus(Short op1, Short op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return op1 + op2;
	}

	public static int minus(Short op1, Short op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return op1 - op2;
	}

	public static BigDecimal divide(Short op1, Short op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return BigDecimal.valueOf(op1).divide(BigDecimal.valueOf(op2), EDecimal.BIGDECIMAL_RESULT_SCALE, EDecimal.ROUND_BD);
	}

	public static int multiply(Short op1, Short op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		try {
			return BigDecimal.valueOf(op1 * op2).intValueExact();
		}
		catch (ArithmeticException ex) {
			throw new NumericOverflowException(ex.getLocalizedMessage());
		}
	}

	public static int remainder(Short op1, Short op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return op1 % op2;
	}

	public static double power(Short op1, Short op2) throws JavartException {
		if (op1 == null || op2 == null)
			throw new NullValueException();
		return StrictMath.pow( op1, op2 );
	}

	public static int compareTo(Short op1, Short op2) throws JavartException {
		if (op1 == null && op2 == null)
			return 0;
		if ((op1 != null && op2 == null) || (op1 == null && op2 != null))
			throw new NullValueException();
		return op1.compareTo(op2);
	}

	public static boolean equals(Short op1, Short op2) {
		if (op1 == null && op2 == null)
			return true;
		if ((op1 != null && op2 == null) || (op1 == null && op2 != null))
			return false;
		return op1.equals(op2);
	}

	public static boolean notEquals(Short op1, Short op2) {
		return !equals(op1, op2);
	}

	public int precision() {
		return precision;
	}
}
