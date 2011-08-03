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
import java.math.BigInteger;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.resources.ExecutableBase;

public class MathLib extends ExecutableBase {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/**
	 * A constant used by log10. The value is StrictMath.log(10).
	 */
	private static final double LOG_OF_10 = 2.302585092994046;

	/**
	 * A mask that can extract the exponent bits from the bits of a double.
	 */
	private static final long DOUBLE_EXPONENT_MASK = 0x7ff0000000000000L;

	/**
	 * The number of bits in the mantissa of a double.
	 */
	private static final int DOUBLE_MANTISSA_LENGTH = 52;

	/**
	 * A bit pattern which can be anded to a double to set its exponent to zero.
	 */
	private static final long DOUBLE_NO_EXPONENT_MASK = 0x800fffffffffffffL;

	/**
	 * The largest biased exponent a double may have if it's not infinite or NaN.
	 */
	private static final int DOUBLE_MAX_EXPONENT = 0x7fe;

	/**
	 * The amount that the exponent of a double is biased.
	 */
	private static final int DOUBLE_EXPONENT_BIAS = 1022;

	/**
	 * A mask that can extract the sign bit from the bits of a double.
	 */
	private static final long DOUBLE_SIGN_MASK = 0x8000000000000000L;

	/**
	 * A bit pattern which when considered as a double will have a mantissa of zero with a biased zero exponent.
	 */
	private static final long DOUBLE_ZERO_EXPONENT = ((long) DOUBLE_EXPONENT_BIAS << DOUBLE_MANTISSA_LENGTH);

	/**
	 * A mask that can extract the mantissa bits from the bits of a double.
	 */
	private static final long DOUBLE_MANTISSA_MASK = 0x000fffffffffffffL;

	/**
	 * Digit length of an int.
	 */
	private static final int INT_PRECISION = 9;

	/**
	 * Digit length of a short.
	 */
	private static final int SHORT_PRECISION = 4;

	/**
	 * Digit length of a long.
	 */
	private static final int LONG_PRECISION = 18;

	/**
	 * Digit length of a float.
	 */
	private static final int FLOAT_PRECISION = 6;

	/**
	 * Digit length of a double.
	 */
	private static final int DOUBLE_PRECISION = 15;

	/**
	 * The constructor.
	 */
	public MathLib(RunUnit ru) throws JavartException {
		super(ru);
	}

	/**
	 * Computes the absolute value of <code>numericField</code>.
	 */
	public Short abs(Short numericField) {
		if (numericField < 0)
			return new Short((short) (0 - numericField));
		return numericField;
	}

	public Integer abs(Integer numericField) {
		if (numericField < 0)
			return new Integer((int) (0 - numericField));
		return numericField;
	}

	public Long abs(Long numericField) {
		if (numericField < 0)
			return new Long((long) (0 - numericField));
		return numericField;
	}

	public Double abs(Double numericField) {
		return StrictMath.abs(numericField);
	}

	public Double abs(Float numericField) {
		return StrictMath.abs(numericField.doubleValue());
	}

	public BigDecimal abs(BigDecimal numericField) {
		return numericField.abs();
	}

	/**
	 * Computes the arc cos of <code>numericField</code>.
	 */
	public Double acos(Double numericField) throws JavartException {
		if (numericField < -1 || numericField > 1)
			return Double.NaN;
		return StrictMath.acos(numericField);
	}

	/**
	 * Computes the arc sin of <code>numericField</code>.
	 */
	public Double asin(Double numericField) throws JavartException {
		if (numericField < -1 || numericField > 1)
			return Double.NaN;
		return StrictMath.asin(numericField);
	}

	/**
	 * Computes the arc tan of <code>numericField</code>.
	 */
	public Double atan(Double numericField) throws JavartException {
		return StrictMath.atan(numericField);
	}

	/**
	 * Computes the arc tan of <code>numericField1 / numericField2</code> in the range -PI to PI.
	 */
	public Double atan2(Double numericField1, Double numericField2) {
		return StrictMath.atan2(numericField1, numericField2);
	}

	/**
	 * Computes the ceiling of <code>numericField</code>. Ceiling is the smallest (closest to negative infinity)
	 * floating-point value that is not less than the argument and is equal to a mathematical integer.
	 */
	public Double ceiling(Double numericField) {
		return StrictMath.ceil(numericField);
	}

	/**
	 * Computes the ceiling of <code>numericField</code>. Ceiling is the smallest (closest to negative infinity)
	 * floating-point value that is not less than the argument and is equal to a mathematical integer.
	 */
	public BigDecimal ceiling(BigDecimal numericField) {
		return numericField.setScale(0, BigDecimal.ROUND_CEILING);
	}

	/**
	 * Computes the cos of <code>numericField</code>.
	 */
	public Double cos(Double numericField) {
		return StrictMath.cos(numericField);
	}

	/**
	 * Computes the hyperbolic cosine of <code>numericField</code>.
	 */
	public Double cosh(Double numericField) throws JavartException {
		return StrictMath.cosh(numericField);
	}

	/**
	 * Returns the number of digits to the right of the decimal point that the value can store.
	 */
	public int decimals(BigDecimal numericField) {
		return numericField.scale();
	}

	/**
	 * Computes the exp of <code>numericField</code>. e to the power(<code>numericField</code>)
	 */
	public Double exp(Double numericField) {
		return StrictMath.exp(numericField);
	}

	/**
	 * Computes the floor of <code>numericField</code>: the largest (closest to positive infinity) floating-point value that
	 * is not greater than the argument and is equal to a mathematical integer.
	 */
	public Double floor(Double numericField) {
		return StrictMath.floor(numericField);
	}

	/**
	 * Computes the floor of <code>numericField</code>: the largest (closest to positive infinity) floating-point value that
	 * is not greater than the argument and is equal to a mathematical integer.
	 */
	public BigDecimal floor(BigDecimal numericField) {
		return numericField.setScale(0, BigDecimal.ROUND_FLOOR);
	}

	/**
	 * Breaks <code>numericField</code> into its mantissa, which is stored in <code>result</code>, and its exponent, which is
	 * stored in <code>exponent</code>.
	 */
	public Double frexp(Double numericField, AnyBoxedObject<Integer> exponent) throws JavartException {
		// Get the number to be split.
		double value = numericField;
		// Extract the exponent.
		long bits = Double.doubleToLongBits(numericField);
		int exp = (int) ((bits & DOUBLE_EXPONENT_MASK) >> DOUBLE_MANTISSA_LENGTH);
		// See if it's a "normal" number.
		if (0 < exp && exp <= DOUBLE_MAX_EXPONENT) {
			// Unbias the exponent.
			exp -= DOUBLE_EXPONENT_BIAS;
			// Store the exponent.
			exponent.ezeCopy(exp);
			// Give the mantissa a zero exponent.
			long mantissa = bits & (DOUBLE_SIGN_MASK | DOUBLE_MANTISSA_MASK);
			mantissa |= DOUBLE_ZERO_EXPONENT;
			// Turn the mantissa into a double.
			value = Double.longBitsToDouble(mantissa);
		} else {
			// value is either NaN, positive or negative zero, positive
			// or negative Infinity, or a denormalized number. All of
			// these cases are handled the same.
			exponent.ezeCopy(0);
		}
		return value;
	}

	/**
	 * Multiplies <code>numericField</code> by 2<sup><code>power</code></sup> and stores the answer in <code>result</code>.
	 */
	public Double ldexp(Double numericField, Integer pow) throws JavartException {
		// Convert the value of numericField to its bit pattern, get the current
		// exponent, and remove it from the bit string.
		double value = numericField;
		long bits = Double.doubleToLongBits(value);
		long exp = (bits & DOUBLE_EXPONENT_MASK) >> DOUBLE_MANTISSA_LENGTH;
		bits &= DOUBLE_NO_EXPONENT_MASK;
		// Increase the exponent by adding pow, and quit if the new exponent
		// is too big or too small.
		exp += pow;
		if (exp < 0 || exp > DOUBLE_MAX_EXPONENT)
			return Double.NaN;
		// Construct the new double and assign it to result.
		bits |= (exp << DOUBLE_MANTISSA_LENGTH);
		return Double.longBitsToDouble(bits);
	}

	/**
	 * Computes the log of <code>numericField</code>. the natural logarithm (base e) of <code>numericField</code>
	 */
	public Double log(Double numericField) throws JavartException {
		if (numericField <= 0)
			return Double.NaN;
		return StrictMath.log(numericField);
	}

	/**
	 * Computes the log of <code>numericField</code>. the logarithm (base 10) of <code>numericField</code>
	 */
	public Double log10(Double numericField) throws JavartException {
		if (numericField <= 0)
			return Double.NaN;
		// Compute the log and return the result. Since Java has no
		// log10 method, we use the formula log_b(x) = nl(x) / nl(b).
		return StrictMath.log(numericField) / LOG_OF_10;
	}

	/**
	 * Computes the maximum of the fields
	 */
	public Short max(Short numericField1, Short numericField2) {
		if (numericField1 < numericField2)
			return numericField2;
		return numericField1;
	}

	public Integer max(Integer numericField1, Integer numericField2) {
		if (numericField1 < numericField2)
			return numericField2;
		return numericField1;
	}

	public Long max(Long numericField1, Long numericField2) {
		if (numericField1 < numericField2)
			return numericField2;
		return numericField1;
	}

	public Double max(Double numericField1, Double numericField2) {
		return StrictMath.max(numericField1, numericField2);
	}

	public Float max(Float numericField1, Float numericField2) {
		return StrictMath.max(numericField1, numericField2);
	}

	public BigDecimal max(BigDecimal numericField1, BigDecimal numericField2) {
		return numericField1.max(numericField2);
	}

	/**
	 * Computes the minimum of the fields
	 */
	public Short min(Short numericField1, Short numericField2) {
		if (numericField1 < numericField2)
			return numericField1;
		return numericField2;
	}

	public Integer min(Integer numericField1, Integer numericField2) {
		if (numericField1 < numericField2)
			return numericField1;
		return numericField2;
	}

	public Long min(Long numericField1, Long numericField2) {
		if (numericField1 < numericField2)
			return numericField1;
		return numericField2;
	}

	public Double min(Double numericField1, Double numericField2) {
		return StrictMath.min(numericField1, numericField2);
	}

	public Float min(Float numericField1, Float numericField2) {
		return StrictMath.min(numericField1, numericField2);
	}

	public BigDecimal min(BigDecimal numericField1, BigDecimal numericField2) {
		return numericField1.min(numericField2);
	}

	/**
	 * Stores the integral part of <code>op1</code> in <code>op2</code> and the fractional part in <code>result</code>
	 */
	public Double modf(Double numericField1, AnyBoxedObject<Long> numericField2) throws JavartException {
		// Get the number and split it.
		BigDecimal bd = new BigDecimal(numericField1);
		BigInteger intPart = bd.toBigInteger();
		BigDecimal fractPart = new BigDecimal(intPart).subtract(bd).negate();
		// Store the integral part in numericField2. If numericField2 is a hex
		numericField2.ezeCopy(intPart.longValue());
		return fractPart.doubleValue();
	}

	/**
	 * Computes <code>op1</code> to the power of <code>op2</code>.
	 */
	public Double pow(Double op1, Double op2) throws JavartException {
		// Check the args. If op1 is 0, op2 must be > 0. If op1 is
		// negative, op2 must be a whole number.
		if (op1 == 0 && op2 <= 0)
			return Double.POSITIVE_INFINITY;
		else if (op1 < 0 && op2 > StrictMath.floor(op2))
			return Double.NaN;
		return StrictMath.pow(op1, op2);
	}

	/**
	 * returns the maximum precision (in decimal digits) for a number. For floating-point numbers (8-digit HEX for
	 * standard-precision floating-point number or 16-digit HEX for double-precision floating-point number), the precision is
	 * the maximum number of decimal digits that can be represented in the number for the system on which the program is
	 * running.
	 */
	public int precision(Short numericField) {
		return SHORT_PRECISION;
	}

	public int precision(Integer numericField) {
		return INT_PRECISION;
	}

	public int precision(Long numericField) {
		return LONG_PRECISION;
	}

	public int precision(Float numericField) {
		return FLOAT_PRECISION;
	}

	public int precision(Double numericField) {
		return DOUBLE_PRECISION;
	}

	public int precision(BigDecimal numericField) {
		return numericField.precision() - numericField.scale();
	}

	/**
	 * Generates a random number between 0 (inclusive) and 1 (exclusive). 
	 */
	public Float random() {
		return new Float(Math.random());
	}

	/**
	 * Rounds a number or expression to a nearest value (for example, to the nearest thousands) and returns the result.
	 */
	public Float round(Float value, Integer exp) {
		// Round it.
		BigDecimal rounder;
		if (exp > 0) {
			rounder = BigDecimal.valueOf(5);
			rounder = rounder.movePointRight(exp - 1);
		} else
			rounder = BigDecimal.valueOf(5, -exp + 1);
		if (value >= 0)
			// Round positive numbers up.
			value += rounder.floatValue();
		else
			// Round negative numbers down.
			value -= rounder.floatValue();
		// Zero out the digits past the ones we rounded.
		value = new Float(Math.pow(value, -exp));
		value = new Float(value.longValue());
		value = new Float(Math.pow(value, exp));
		return value;
	}

	public Double round(Double value, Integer exp) {
		// Round it.
		BigDecimal rounder;
		if (exp > 0) {
			rounder = BigDecimal.valueOf(5);
			rounder = rounder.movePointRight(exp - 1);
		} else
			rounder = BigDecimal.valueOf(5, -exp + 1);
		if (value >= 0)
			// Round positive numbers up.
			value += rounder.floatValue();
		else
			// Round negative numbers down.
			value -= rounder.floatValue();
		// Zero out the digits past the ones we rounded.
		value = new Double(Math.pow(value, -exp));
		value = new Double(value.longValue());
		value = new Double(Math.pow(value, exp));
		return value;
	}

	public BigDecimal round(BigDecimal value, Integer exp) {
		// Round it.
		BigDecimal rounder;
		if (exp > 0) {
			rounder = BigDecimal.valueOf(5);
			rounder = rounder.movePointRight(exp - 1);
		} else
			rounder = BigDecimal.valueOf(5, -exp + 1);
		if (value.signum() >= 0)
			// Round positive numbers up.
			value = value.add(rounder);
		else
			// Round negative numbers down.
			value = value.subtract(rounder);
		// Zero out the digits past the ones we rounded.
		if (exp > 0) {
			value = value.movePointLeft(exp);
			value = new BigDecimal(value.toBigInteger());
			value = value.movePointRight(exp);
		} else {
			value = value.movePointRight(-exp);
			value = new BigDecimal(value.toBigInteger());
			value = value.movePointLeft(-exp);
		}
		return value;
	}

	/**
	 * Computes the sine of <code>numericField</code>.
	 */
	public Double sin(Double numericField) {
		return StrictMath.sin(numericField);
	}

	/**
	 * Computes the hyperbolic sine of <code>numericField</code>.
	 */
	public Double sinh(Double numericField) throws JavartException {
		return StrictMath.sinh(numericField);
	}

	/**
	 * Computes the squareroot of <code>numericField</code>.
	 */
	public Double sqrt(Double numericField) throws JavartException {
		if (numericField < 0)
			return Double.NaN;
		return StrictMath.sqrt(numericField);
	}

	/**
	 * Computes the tangent of <code>numericField</code>.
	 */
	public Double tan(Double numericField) {
		return StrictMath.tan(numericField);
	}

	/**
	 * Computes the hyperbolic tangent of <code>numericField</code>.
	 */
	public Double tanh(Double numericField) throws JavartException {
		return StrictMath.tanh(numericField);
	}
}
