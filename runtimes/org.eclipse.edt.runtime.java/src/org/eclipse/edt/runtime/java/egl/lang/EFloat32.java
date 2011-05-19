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

public class EFloat32 extends AnyBoxedObject<Float> implements AnyNumber {
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = 70L;
	
	private EFloat32(Float value) { super(value); }
	
	public static EFloat32 ezeBox(Float value) {
		return new EFloat32(value);
	}
	
	public static Float ezeCast(Object value) throws JavartException {
		return (Float)AnyObject.ezeCast(value, "asFloat32", EFloat32.class, null, null);
	}
	
	public static boolean ezeIsa(Object value) {
		return value instanceof EFloat32;
	}
	
	public static Float asFloat32(Executable program, Short value) {
		if (value == null) return null;
		return value.floatValue();
	}
	
	public static Float asFloat32(Executable program, Integer value) {
		if (value == null) return null;
		return value.floatValue();
	}
	
	public static Float asFloat32(Executable program, Long value) {
		if (value == null) return null;
		return value.floatValue();
	}
		
	public static Float asFloat32(Executable program, BigDecimal value) {
		if (value == null) return null;
		return value.floatValue();

	}

	public static Float asFloat32(Executable program, String value) throws JavartException {
		if (value == null) return null;
		return asFloat32(program, EDecimal.asDecimal(program, value));
	}
	
	public static Float asFloat32(Executable program, Double value) throws JavartException {
		if (value == null) return null;
		return value.floatValue();
	}

	public static int compareTo(Executable program, Float op1, Float op2) throws JavartException {
		if (op1 == null || op2 == null) {
			throw new NullValueException();
		}
		return op1.compareTo(op2);
	}

	public static boolean equals(Executable program, Float op1, Float op2) {
		if (op1 == null && op2 == null) return true;
		if ((op1 != null && op2 == null) || (op1 == null && op2 != null)) return false;
		return op1.equals(op2);
	}
	
	public static boolean notEquals(Executable program, Float op1, Float op2) {
		return !equals(program, op1, op2);
	}

}
