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
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.JavartException;

import egl.lang.AnyNumber;



public class EInt32 extends AnyBoxedObject<Integer> implements AnyNumber {
	private static final long serialVersionUID = 80L;
	private static final int DefaultValue = 0;
	private static final int Precision = 9;
		
	private EInt32(Integer value) { super(value); }

	public static EInt32 ezeBox(Integer value) {
		return new EInt32(value);
	}

	public static Integer ezeCast(Object value) throws JavartException {
		return (Integer)AnyObject.ezeCast(value, "asInt32", EInt32.class, null, null);

	}
	
	public static boolean ezeIsa(Object value) {
		return value instanceof EInt32;
	}

	
	public static Integer asInt32(Executable program, Short value) throws JavartException {
		if (value == null) return null;
		return value.intValue();
	}
	
	public static Integer asInt32(Executable program, Long value) throws JavartException {
		if (value == null) return null;

		boolean throwOverflowExceptions = false;  // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions) {
			result = BigDecimal.valueOf(value).intValueExact();
		}
		else {
			result = Long.valueOf(value).intValue();
		}
		return result;
	}
	

	public static Integer asInt32(Executable program, BigDecimal value) throws JavartException {
		if (value == null) return null;
		boolean throwOverflowExceptions = false;  // TODO need program flag on whether to throw exceptions or not.
		int result = 0;
		if (throwOverflowExceptions) {
			result = value.intValueExact();
		}
		else {
			result = value.intValue();
		}
		return result;
	}
	
	public static Integer asInt32(Executable program, Float value) throws JavartException {
		if (value == null) return null;
		boolean throwOverflowExceptions = false;  // TODO need program flag on whether to throw exceptions or not.
		int result = 0;;
		if (throwOverflowExceptions) {
			result = BigDecimal.valueOf(value).intValueExact();
		}
		else {
			result = value.intValue();
		}
		return result;
	}
	
	public static Integer asInt32(Executable program, Double value) throws JavartException {
		if (value == null) return null;
		boolean throwOverflowExceptions = false;  // TODO need program flag on whether to throw exceptions or not.
		int result = 0;;
		if (throwOverflowExceptions) {
			result = BigDecimal.valueOf(value).intValueExact();
		}
		else {
			result = value.intValue();
		}
		return result;
	}
	
	public static Integer asInt32(Executable program, String value) throws JavartException {
		if (value == null) return null;
		return asInt32(program, EDecimal.asDecimal(program, value));
	}
	
	public static int defaultValue() { return DefaultValue; }
	
	public static int precision() { return Precision; }
				
	public static int plus(Executable program, Integer op1, Integer op2) throws JavartException {
		if (op1 == null || op2 == null) throw new NullValueException();
		return BigDecimal.valueOf((long)op1 + op2).intValueExact();
	}

	public static int minus(Executable program, Integer op1, Integer op2) throws JavartException {
		if (op1 == null || op2 == null) throw new NullValueException();
		return BigDecimal.valueOf((long)op1 - op2).intValueExact();
	}

	public static BigDecimal divide(Executable program, Integer op1, Integer op2) throws JavartException {
		if (op1 == null || op2 == null) throw new NullValueException();
		return BigDecimal.valueOf(op1).divide( BigDecimal.valueOf(op2), EDecimal.BIGDECIMAL_RESULT_SCALE, EDecimal.ROUND_BD);
	}

	public static int multiply(Executable program, Integer op1, Integer op2) throws JavartException {
		if (op1 == null || op2 == null) throw new NullValueException();
		return BigDecimal.valueOf((long)op1 * op2).intValueExact();
	}

	public static int remainder(Executable program, Integer op1, Integer op2) throws JavartException {
		if (op1 == null || op2 == null) throw new NullValueException();
		return op1 % op2;
	}
	
	public static int compareTo(Executable program, Integer op1, Integer op2) throws JavartException {
		if (op1 == null && op2 == null) return 0;
		if ((op1 != null && op2 == null) || (op1 == null && op2 != null)) throw new NullValueException();
		return op1.compareTo(op2);
	}

	public static boolean equals(Executable program, Integer op1, Integer op2) {
		if (op1 == null && op2 == null) return true;
		if ((op1 != null && op2 == null) || (op1 == null && op2 != null)) return false;
		return op1.equals(op2);
	}
	
	public static boolean notEquals(Executable program, Integer op1, Integer op2) {
		return !equals(program, op1, op2);
	}

}
