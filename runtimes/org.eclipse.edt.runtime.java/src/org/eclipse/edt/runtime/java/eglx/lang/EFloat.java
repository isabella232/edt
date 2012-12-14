/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
import org.eclipse.edt.javart.messages.Message;

import eglx.java.JavaObjectException;
import eglx.lang.AnyException;
import eglx.lang.NullValueException;
import eglx.lang.TypeCastException;

public class EFloat extends AnyBoxedObject<Double> implements eglx.lang.ENumber {
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public static final double Infinity = Double.POSITIVE_INFINITY;
	public static final double NegativeInfinity = Double.NEGATIVE_INFINITY;
	public static final double NaN = Double.NaN;
	public static final double NegativeZero = Double.longBitsToDouble( 0x8000000000000000L );

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
		return (value instanceof EFloat && ((EFloat) value).ezeUnbox() != null) || value instanceof Double;
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

	public static Double asFloat(eglx.lang.ENumber value) {
		if (value == null)
			return null;
		return value.ezeUnbox().doubleValue();
	}

	public static Double asFloat(String value) throws AnyException {
		if (value == null)
			return null;
		try
		{
			//TODO this is too permissive.  See the doc for the method.  We should
			// only allow a sign, digits, decimal point, digits, and exponent.  The
			// sign and everything after the first set of digits is optional.
			return Double.valueOf( value );
		}
		catch ( Exception ex )
		{
			// It's invalid.
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "float";
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, value, tcx.actualTypeName, tcx.castToName );
		}
	}

	public static Double asFloat(EString value) throws AnyException {
		if (value == null)
			return null;
		return asFloat(value.ezeUnbox());
	}

	public static Double asFloat( byte[] value ) throws AnyException
	{
		if ( value == null )
		{
			return null;
		}
		
		if ( value.length != 8 )
		{
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "bytes(" + value.length + ')';
			tcx.castToName = "float";
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, value, tcx.actualTypeName, tcx.castToName );
		}
		
		long bits = ((value[ 0 ] & 0xFFL) << 56)
				| ((value[ 1 ] & 0xFFL) << 48)
				| ((value[ 2 ] & 0xFFL) << 40)
				| ((value[ 3 ] & 0xFFL) << 32)
				| ((value[ 4 ] & 0xFFL) << 24)
				| ((value[ 5 ] & 0xFFL) << 16)
				| ((value[ 6 ] & 0xFFL) << 8)
				| (value[ 7 ] & 0xFFL);
		return Double.longBitsToDouble( bits );
	}

	public static Double asFloat( EBytes value ) throws AnyException
	{
		if ( value == null )
		{
			return null;
		}
		return asFloat( value.ezeUnbox() );
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static EFloat asNumber(Double value) throws AnyException {
		if (value == null)
			return null;
		return EFloat.ezeBox(value);
	}

	public static EFloat asNumber(EFloat value) throws AnyException {
		if (value == null)
			return null;
		return value;
	}

	public static double negate(Double op) throws AnyException {
		if (op == null) {
			NullValueException nvx = new NullValueException();
			throw nvx.fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		return -op;
	}

	public static double plus(double op1, double op2) throws AnyException {
		return op1 + op2;
	}

	public static double minus(double op1, double op2) throws AnyException {
		return op1 - op2;
	}

	public static double divide(double op1, double op2) throws AnyException {
		if (op2 == 0.0) {
			JavaObjectException jox = new JavaObjectException();
			jox.exceptionType = EFloat.class.getCanonicalName();
			throw jox;
		}
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
		return Double.valueOf(op1).compareTo(op2);
	}

	public static boolean equals(Double op1, Double op2) {
		if (op1 == op2)
			return true;
		if (op1 == null || op2 == null)
			return false;
		return op1.compareTo(op2) == 0;
	}

	public static boolean notEquals(Double op1, Double op2) {
		return !equals(op1, op2);
	}
}
