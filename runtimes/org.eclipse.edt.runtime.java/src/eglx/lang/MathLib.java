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
package eglx.lang;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.runtime.java.eglx.lang.EDecimal;

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
	public MathLib() throws AnyException {
	}

	/**
	 * Computes the absolute value of <code>numericField</code>.
	 */
	public static short abs(short numericField) {
		if (numericField < 0)
			return (short)-numericField;
		return numericField;
	}

	public static int abs(int numericField) {
		if (numericField < 0)
			return -numericField;
		return numericField;
	}

	public static long abs(long numericField) {
		if (numericField < 0)
			return -numericField;
		return numericField;
	}

	public static double abs(double numericField) {
		return StrictMath.abs(numericField);
	}

	public static float abs(float numericField) {
		return StrictMath.abs(numericField);
	}

	public static BigDecimal abs(BigDecimal numericField) {
		return numericField.abs();
	}

	/**
	 * Computes the arc cos of <code>numericField</code>.
	 */
	public static double acos(double numericField) {
		if (numericField < -1 || numericField > 1)
			return Double.NaN;
		return StrictMath.acos(numericField);
	}

	/**
	 * Computes the arc sin of <code>numericField</code>.
	 */
	public static double asin(double numericField) {
		if (numericField < -1 || numericField > 1)
			return Double.NaN;
		return StrictMath.asin(numericField);
	}

	/**
	 * Computes the arc tan of <code>numericField</code>.
	 */
	public static double atan(double numericField) {
		return StrictMath.atan(numericField);
	}

	/**
	 * Computes the arc tan of <code>numericField1 / numericField2</code> in the range -PI to PI.
	 */
	public static double atan2(double numericField1, double numericField2) {
		return StrictMath.atan2(numericField1, numericField2);
	}

	/**
	 * Computes the ceiling of <code>numericField</code>. Ceiling is the smallest (closest to negative infinity)
	 * floating-point value that is not less than the argument and is equal to a mathematical integer.
	 */
	public static double ceiling(double numericField) {
		return StrictMath.ceil(numericField);
	}

	/**
	 * Computes the ceiling of <code>numericField</code>. Ceiling is the smallest (closest to negative infinity)
	 * floating-point value that is not less than the argument and is equal to a mathematical integer.
	 */
	public static BigDecimal ceiling(BigDecimal numericField) {
		return numericField.setScale(0, BigDecimal.ROUND_CEILING);
	}

	/**
	 * Computes the cos of <code>numericField</code>.
	 */
	public static double cos(double numericField) {
		return StrictMath.cos(numericField);
	}

	/**
	 * Computes the hyperbolic cosine of <code>numericField</code>.
	 */
	public static double cosh(double numericField) {
		return StrictMath.cosh(numericField);
	}

	/**
	 * Returns the number of digits to the right of the decimal point that the value can store.
	 */
	public static int decimals(BigDecimal numericField) {
		return numericField.scale();
	}

	public static int decimals(EDecimal numericField) {
		if (numericField == null || numericField.ezeUnbox() == null)
		{
			NullValueException nvx = new NullValueException();
			throw nvx.fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		return numericField.getDecimals();
	}

	/**
	 * Computes the exp of <code>numericField</code>. e to the power(<code>numericField</code>)
	 */
	public static double exp(double numericField) {
		return StrictMath.exp(numericField);
	}

	/**
	 * Computes the floor of <code>numericField</code>: the largest (closest to positive infinity) floating-point value that
	 * is not greater than the argument and is equal to a mathematical integer.
	 */
	public static double floor(double numericField) {
		return StrictMath.floor(numericField);
	}

	/**
	 * Computes the floor of <code>numericField</code>: the largest (closest to positive infinity) floating-point value that
	 * is not greater than the argument and is equal to a mathematical integer.
	 */
	public static BigDecimal floor(BigDecimal numericField) {
		return numericField.setScale(0, BigDecimal.ROUND_FLOOR);
	}

	/**
	 * Breaks <code>numericField</code> into its mantissa, which is stored in <code>result</code>, and its exponent, which is
	 * stored in <code>exponent</code>.
	 */
	public static double frexp(double numericField, AnyBoxedObject<Integer> exponent) {
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
	public static double ldexp(double numericField, int pow) {
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
	public static double log(double numericField) {
		if (numericField <= 0)
			return Double.NaN;
		return StrictMath.log(numericField);
	}

	/**
	 * Computes the log of <code>numericField</code>. the logarithm (base 10) of <code>numericField</code>
	 */
	public static double log10(double numericField) {
		if (numericField <= 0)
			return Double.NaN;
		// Compute the log and return the result. Since Java has no
		// log10 method, we use the formula log_b(x) = nl(x) / nl(b).
		return StrictMath.log(numericField) / LOG_OF_10;
	}

	/**
	 * Computes the maximum of the fields
	 */
	public static short max(short numericField1, short numericField2) {
		if (numericField1 < numericField2)
			return numericField2;
		return numericField1;
	}

	public static int max(int numericField1, int numericField2) {
		if (numericField1 < numericField2)
			return numericField2;
		return numericField1;
	}

	public static long max(long numericField1, long numericField2) {
		if (numericField1 < numericField2)
			return numericField2;
		return numericField1;
	}

	public static double max(double numericField1, double numericField2) {
		return StrictMath.max(numericField1, numericField2);
	}

	public static float max(float numericField1, float numericField2) {
		return StrictMath.max(numericField1, numericField2);
	}

	public static BigDecimal max(BigDecimal numericField1, BigDecimal numericField2) {
		return numericField1.max(numericField2);
	}

	/**
	 * Computes the minimum of the fields
	 */
	public static short min(short numericField1, short numericField2) {
		if (numericField1 < numericField2)
			return numericField1;
		return numericField2;
	}

	public static int min(int numericField1, int numericField2) {
		if (numericField1 < numericField2)
			return numericField1;
		return numericField2;
	}

	public static long min(long numericField1, long numericField2) {
		if (numericField1 < numericField2)
			return numericField1;
		return numericField2;
	}

	public static double min(double numericField1, double numericField2) {
		return StrictMath.min(numericField1, numericField2);
	}

	public static float min(float numericField1, float numericField2) {
		return StrictMath.min(numericField1, numericField2);
	}

	public static BigDecimal min(BigDecimal numericField1, BigDecimal numericField2) {
		return numericField1.min(numericField2);
	}

	/**
	 * Stores the integral part of <code>op1</code> in <code>op2</code> and the fractional part in <code>result</code>
	 */
	public static double modf(double numericField1, AnyBoxedObject<Long> numericField2) {
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
	public static double pow(double op1, double op2) {
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
	public static int precision(short numericField) {
		return SHORT_PRECISION;
	}

	public static int precision(int numericField) {
		return INT_PRECISION;
	}

	public static int precision(long numericField) {
		return LONG_PRECISION;
	}

	public static int precision(float numericField) {
		return FLOAT_PRECISION;
	}

	public static int precision(double numericField) {
		return DOUBLE_PRECISION;
	}

	public static int precision(BigDecimal numericField) {
		return numericField.precision() - numericField.scale();
	}

	public static int precision(EDecimal numericField) {
		if (numericField == null || numericField.ezeUnbox() == null)
		{
			NullValueException nvx = new NullValueException();
			throw nvx.fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		return numericField.getPrecision();
	}

	/**
	 * Generates a random number between 0 (inclusive) and 1 (exclusive). 
	 */
	public static double random() {
		return Math.random();
	}

	/**
	 * Rounds a number or expression to a nearest value (for example, to the nearest thousands) and returns the result.
	 */
	public static float round(float value, int exp) {
		return round( BigDecimal.valueOf( value ), exp ).floatValue();
	}

	public static double round(double value, int exp) {
		return round( BigDecimal.valueOf( value ), exp ).doubleValue();
	}

	public static BigDecimal round(BigDecimal value, int exp) {
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
	public static double sin(double numericField) {
		return StrictMath.sin(numericField);
	}

	/**
	 * Computes the hyperbolic sine of <code>numericField</code>.
	 */
	public static double sinh(double numericField) {
		return StrictMath.sinh(numericField);
	}

	/**
	 * Computes the squareroot of <code>numericField</code>.
	 */
	public static double sqrt(double numericField) {
		if (numericField < 0)
			return Double.NaN;
		return StrictMath.sqrt(numericField);
	}

	/**
	 * Computes the tangent of <code>numericField</code>.
	 */
	public static double tan(double numericField) {
		return StrictMath.tan(numericField);
	}

	/**
	 * Computes the hyperbolic tangent of <code>numericField</code>.
	 */
	public static double tanh(double numericField) {
		return StrictMath.tanh(numericField);
	}
}
