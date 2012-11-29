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
import org.eclipse.edt.javart.util.NumericUtil;

import eglx.lang.*;

/**
 * Class to be used in processing Decimal operations
 * @author twilson
 */
public class EDecimal extends AnyBoxedObject<BigDecimal> implements eglx.lang.ENumber {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/**
	 * An array used for the upper limits of digit-oriented items that have values with decimals.
	 */
	private static final BigDecimal[][] MAX_DIGIT_ORIENTED_BD_VALUES;

	/**
	 * An array used for the lower limits of digit-oriented items that have values with decimals.
	 */
	private static final BigDecimal[][] MIN_DIGIT_ORIENTED_BD_VALUES;

	/**
	 * An array used for the upper limits of byte-oriented items that have values with decimals.
	 */
	private static final BigDecimal[][] MAX_BYTE_ORIENTED_BD_VALUES;

	/**
	 * An array used for the lower limits of byte-oriented items that have values with decimals.
	 */
	private static final BigDecimal[][] MIN_BYTE_ORIENTED_BD_VALUES;

	/** */
	public static final int TRUNCATE_BD = BigDecimal.ROUND_DOWN;

	/** */
	public static final int ROUND_BD = BigDecimal.ROUND_HALF_UP;

	public static final int BIGDECIMAL_RESULT_SCALE = 32;

	// Initialize the BD_VALUES arrays.
	static {
		// The first dimension of the DIGIT_ORIENTED arrays is based on the
		// length of the items (1-32). The second dimension is long enough to
		// hold the number of decimal digits that the items might have.
		MAX_DIGIT_ORIENTED_BD_VALUES = new BigDecimal[34][];
		MAX_DIGIT_ORIENTED_BD_VALUES[0] = new BigDecimal[1];
		MAX_DIGIT_ORIENTED_BD_VALUES[0][0] = BigDecimal.ZERO;

		MIN_DIGIT_ORIENTED_BD_VALUES = new BigDecimal[34][];
		MIN_DIGIT_ORIENTED_BD_VALUES[0] = new BigDecimal[1];
		MIN_DIGIT_ORIENTED_BD_VALUES[0][0] = BigDecimal.ZERO;

		for (int i = 1; i < 34; i++) {
			MAX_DIGIT_ORIENTED_BD_VALUES[i] = new BigDecimal[i + 1];
			MIN_DIGIT_ORIENTED_BD_VALUES[i] = new BigDecimal[i + 1];
		}

		// The first dimension of the BYTE_ORIENTED arrays is based on
		// the length of the items (4, 9, or 18 digits). The second dimension
		// is long enough to hold the number of decimal digits that the items
		// might have.
		MAX_BYTE_ORIENTED_BD_VALUES = new BigDecimal[3][];
		MAX_BYTE_ORIENTED_BD_VALUES[0] = new BigDecimal[5];
		MAX_BYTE_ORIENTED_BD_VALUES[0][0] = BigDecimal.ZERO;
		MAX_BYTE_ORIENTED_BD_VALUES[1] = new BigDecimal[10];
		MAX_BYTE_ORIENTED_BD_VALUES[1][0] = BigDecimal.ZERO;
		MAX_BYTE_ORIENTED_BD_VALUES[2] = new BigDecimal[19];
		MAX_BYTE_ORIENTED_BD_VALUES[2][0] = BigDecimal.ZERO;

		MIN_BYTE_ORIENTED_BD_VALUES = new BigDecimal[3][];
		MIN_BYTE_ORIENTED_BD_VALUES[0] = new BigDecimal[5];
		MIN_BYTE_ORIENTED_BD_VALUES[0][0] = BigDecimal.ZERO;
		MIN_BYTE_ORIENTED_BD_VALUES[1] = new BigDecimal[10];
		MIN_BYTE_ORIENTED_BD_VALUES[1][0] = BigDecimal.ZERO;
		MIN_BYTE_ORIENTED_BD_VALUES[2] = new BigDecimal[19];
		MIN_BYTE_ORIENTED_BD_VALUES[2][0] = BigDecimal.ZERO;
	}

	/**
	 * An array used for the upper limits of digit-oriented items (their length specifies a number of digits not a number of
	 * bytes) without decimals.
	 */
	public static final BigInteger[] MAX_DIGIT_ORIENTED_BI_VALUES = { BigInteger.ZERO, BigInteger.valueOf(9), BigInteger.valueOf(99), BigInteger.valueOf(999),
		BigInteger.valueOf(9999), BigInteger.valueOf(99999), BigInteger.valueOf(999999), BigInteger.valueOf(9999999), BigInteger.valueOf(99999999),
		BigInteger.valueOf(999999999), BigInteger.valueOf(9999999999L), BigInteger.valueOf(99999999999L), BigInteger.valueOf(999999999999L),
		BigInteger.valueOf(9999999999999L), BigInteger.valueOf(99999999999999L), BigInteger.valueOf(999999999999999L), BigInteger.valueOf(9999999999999999L),
		BigInteger.valueOf(99999999999999999L), BigInteger.valueOf(999999999999999999L), new BigInteger("9999999999999999999"),
		new BigInteger("99999999999999999999"), new BigInteger("999999999999999999999"), new BigInteger("9999999999999999999999"),
		new BigInteger("99999999999999999999999"), new BigInteger("999999999999999999999999"), new BigInteger("9999999999999999999999999"),
		new BigInteger("99999999999999999999999999"), new BigInteger("999999999999999999999999999"), new BigInteger("9999999999999999999999999999"),
		new BigInteger("99999999999999999999999999999"), new BigInteger("999999999999999999999999999999"), new BigInteger("9999999999999999999999999999999"),
		new BigInteger("99999999999999999999999999999999"), new BigInteger("999999999999999999999999999999999") };

	/**
	 * An array used for the lower limits of digit-oriented items (their length specifies a number of digits not a number of
	 * bytes) without decimals.
	 */
	public static final BigInteger[] MIN_DIGIT_ORIENTED_BI_VALUES = { BigInteger.ZERO, MAX_DIGIT_ORIENTED_BI_VALUES[1].negate(),
		MAX_DIGIT_ORIENTED_BI_VALUES[2].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[3].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[4].negate(),
		MAX_DIGIT_ORIENTED_BI_VALUES[5].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[6].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[7].negate(),
		MAX_DIGIT_ORIENTED_BI_VALUES[8].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[9].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[10].negate(),
		MAX_DIGIT_ORIENTED_BI_VALUES[11].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[12].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[13].negate(),
		MAX_DIGIT_ORIENTED_BI_VALUES[14].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[15].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[16].negate(),
		MAX_DIGIT_ORIENTED_BI_VALUES[17].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[18].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[19].negate(),
		MAX_DIGIT_ORIENTED_BI_VALUES[20].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[21].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[22].negate(),
		MAX_DIGIT_ORIENTED_BI_VALUES[23].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[24].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[25].negate(),
		MAX_DIGIT_ORIENTED_BI_VALUES[26].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[27].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[28].negate(),
		MAX_DIGIT_ORIENTED_BI_VALUES[29].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[30].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[31].negate(),
		MAX_DIGIT_ORIENTED_BI_VALUES[32].negate(), MAX_DIGIT_ORIENTED_BI_VALUES[33].negate() };

	private int maxPrecision;
	private int maxDecimals;

	public int getPrecision() {
		return maxPrecision;
	}

	public int getDecimals() {
		return maxDecimals;
	}

	private EDecimal(BigDecimal value) {
		super(value);
		if (value != null) {
			maxPrecision = value.precision();
			maxDecimals = value.scale();
		}
	}

	private EDecimal(BigDecimal value, int precision) {
		super(value);
		if (value != null) {
			maxPrecision = precision;
		}
	}

	private EDecimal(BigDecimal value, int precision, int decimals) {
		super(value);
		if (value != null) {
			maxPrecision = precision;
			maxDecimals = decimals;
		}
	}

	public String toString() {
		return EString.asString(object);
	}

	public static EDecimal ezeBox(BigDecimal value) {
		return new EDecimal(value);
	}

	public static EDecimal ezeBox(BigDecimal value, int precision, int decimals) {
		return new EDecimal(value, precision, decimals);
	}

	/**
	 * Returns the upper limit for a digit-oriented item (its length specifies a number of digits not a number of bytes).
	 * This method will cache values in MAX_DIGIT_ORIENTED_BD_VALUES.
	 * @param length the number of decimal digits stored by the item.
	 * @param decimals the number of decimal digits stored by the item.
	 * @return the largest number that the item can store.
	 */
	public static BigDecimal getMaxValue(int length, int decimals) {
		// Get the cached limit.
		BigDecimal max = MAX_DIGIT_ORIENTED_BD_VALUES[length][decimals];
		if (max == null) {
			// Need to make it, and save it for later.
			max = new BigDecimal(MAX_DIGIT_ORIENTED_BI_VALUES[length], decimals);
			MAX_DIGIT_ORIENTED_BD_VALUES[length][decimals] = max;
		}

		return max;
	}

	/**
	 * Returns the lower limit for a digit-oriented item (its length specifies a number of digits not a number of bytes).
	 * This method will cache values in MIN_DIGIT_ORIENTED_BD_VALUES.
	 * @param length the number of decimal digits stored by the item.
	 * @param decimals the number of decimal digits stored by the item.
	 * @return the smallest number that the item can store.
	 */
	public static BigDecimal getMinValue(int length, int decimals) {
		// Get the cached limit.
		BigDecimal min = MIN_DIGIT_ORIENTED_BD_VALUES[length][decimals];
		if (min == null) {
			// Need to make it, and save it for later.
			BigDecimal max = getMaxValue(length, decimals);
			min = max.negate();
			MIN_DIGIT_ORIENTED_BD_VALUES[length][decimals] = min;
		}

		return min;
	}

	/**
	 * Call this when the assignment overflows and the target is a numeric type.
	 * @param program
	 * @param target
	 * @param source
	 * @throws AnyException
	 */
	private static BigDecimal handleNumericOverflow(BigDecimal value, int precision, int scale, boolean ignoreOverflow) throws AnyException {
		BigDecimal result = value;
		if (ignoreOverflow) {
			// Don't throw an exception. Store as much of the source as possible.
			// This algorithm comes from v6, and from VAGen before that.
			// TODO is this still the right algorithm?
			BigDecimal divisor = new BigDecimal(BigInteger.ONE, -(precision - scale));

			// This is result = source % divisor.
			result = value.subtract(value.divide(divisor, 0, BigDecimal.ROUND_DOWN).multiply(divisor));

		} else {
			// The program wants an exception to be thrown.
			throw new NumericOverflowException();

			// String message = org.eclipse.edt.javart.util.JavartUtil.errorMessage(
			// program,
			// Message.EXPRESSION_OVERFLOW,
			// new Object[]{value.toString() + " as decimal(" + precision + ", " + scale + ")"});
			// throw new RuntimeException(Message.EXPRESSION_OVERFLOW, message);
		}
		return result;

	}

	public static Object ezeCast(Object value, Object[] constraints) throws AnyException {
		Integer[] args = new Integer[constraints.length];
		java.lang.System.arraycopy(constraints, 0, args, 0, args.length);
		return ezeCast(value, args);
	}

	public static BigDecimal ezeCast(Object value, Integer... args) throws AnyException {
		return (BigDecimal) EAny.ezeCast(value, "asDecimal", EDecimal.class, new Class[] { Integer[].class }, args);
	}

	public static boolean ezeIsa(Object value, Integer... args) {
		boolean isa = (value instanceof EDecimal && ((EDecimal) value).ezeUnbox() != null);
		if (isa) {
			if (args.length != 0) {
				isa = ((EDecimal) value).getPrecision() == args[0];
				if (isa && args.length == 1)
					isa = ((EDecimal) value).getDecimals() == 0;
				else if (isa && args.length == 2)
					isa = ((EDecimal) value).getDecimals() == args[1];
			}
		} else {
			isa = value instanceof BigDecimal;
			if (isa && args.length != 0) {
				isa = ((BigDecimal) value).precision() == args[0];
				if (isa && args.length == 1)
					isa = ((BigDecimal) value).scale() == 0;
				else if (isa && args.length == 2)
					isa = ((BigDecimal) value).scale() == args[1];
			}
		}
		return isa;
	}

	public static BigDecimal asDecimal(Short value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return asDecimal(BigDecimal.valueOf(value), args[0], args[1]);
		else
			return BigDecimal.valueOf(value);
	}

	public static BigDecimal asDecimal(ESmallint value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return asDecimal(BigDecimal.valueOf(value.ezeUnbox()), args[0], args[1]);
		else
			return BigDecimal.valueOf(value.ezeUnbox());
	}

	public static BigDecimal asDecimal(Integer value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return asDecimal(BigDecimal.valueOf(value), args[0], args[1]);
		else
			return BigDecimal.valueOf(value);
	}

	public static BigDecimal asDecimal(EInt value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return asDecimal(BigDecimal.valueOf(value.ezeUnbox()), args[0], args[1]);
		else
			return BigDecimal.valueOf(value.ezeUnbox());
	}

	public static BigDecimal asDecimal(Long value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return asDecimal(BigDecimal.valueOf(value), args[0], args[1]);
		else
			return BigDecimal.valueOf(value);
	}

	public static BigDecimal asDecimal(EBigint value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return asDecimal(BigDecimal.valueOf(value.ezeUnbox()), args[0], args[1]);
		else
			return BigDecimal.valueOf(value.ezeUnbox());
	}

	public static BigDecimal asDecimal(Float value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return asDecimal(BigDecimal.valueOf(value), args[0], args[1]);
		else
			return BigDecimal.valueOf(value);
	}

	public static BigDecimal asDecimal(ESmallfloat value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return asDecimal(BigDecimal.valueOf(value.ezeUnbox()), args[0], args[1]);
		else
			return BigDecimal.valueOf(value.ezeUnbox());
	}

	public static BigDecimal asDecimal(Double value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return asDecimal(BigDecimal.valueOf(value), args[0], args[1]);
		else
			return BigDecimal.valueOf(value);
	}

	public static BigDecimal asDecimal(EFloat value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return asDecimal(BigDecimal.valueOf(value.ezeUnbox()), args[0], args[1]);
		else
			return BigDecimal.valueOf(value.ezeUnbox());
	}

	public static BigDecimal asDecimal(BigDecimal value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return asDecimal(value, args[0], args[1]);
		else
			return value;
	}

	public static BigDecimal asDecimal(EDecimal value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return asDecimal(value.ezeUnbox(), args[0], args[1]);
		else
			return value.ezeUnbox();
	}

	public static BigDecimal asDecimal(BigDecimal value, int precision, int scale) throws AnyException {
		if (value == null)
			return null;
		return asDecimal(value, getMaxValue(precision, scale), getMinValue(precision, scale), precision, scale, false);
	}

	public static BigDecimal asDecimal(EDecimal value, int precision, int scale) throws AnyException {
		if (value == null)
			return null;
		return asDecimal(value.ezeUnbox(), getMaxValue(precision, scale), getMinValue(precision, scale), precision, scale, false);
	}

	public static BigDecimal asDecimal(BigDecimal value, BigDecimal max, BigDecimal min, int precision, int scale) throws AnyException {
		if (value == null || max == null || min == null) //TODO why check max and min?  can they ever be null?
			return null;
		return asDecimal(value, max, min, precision, scale, false);
	}

	public static BigDecimal asDecimal(BigDecimal value, BigDecimal max, BigDecimal min, int precision, int scale, boolean ignoreOverflow) throws AnyException {
		if (value == null || max == null || min == null) //TODO why check max and min?  can they ever be null?
			return null;
		BigDecimal result = value;
		if (scale < value.scale()) {
			// truncate or round the value based on the program setting
			// TODO set this variable up in Program
			// if ( program._truncateDecimals )
			// {
			// result = value.setScale( scale, TRUNCATE_BD );
			// }
			// else
			// {
			result = value.setScale(scale, TRUNCATE_BD);
			// }
		}
		if (ignoreOverflow)
			return result;
		else {
			// Now make sure the value isn't too big for the target.
			if ((result.compareTo(max) <= 0 && result.compareTo(min) >= 0))
				return result;
			else
				return handleNumericOverflow(value, precision, scale, ignoreOverflow);
		}
	}

	public static BigDecimal asDecimal(BigInteger value, int precision, int scale) throws AnyException {
		if (value == null)
			return null;
		return asDecimal(new BigDecimal(value), getMaxValue(precision, scale), getMinValue(precision, scale), precision, scale, false);
	}

	public static BigDecimal asDecimal(Number value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return asDecimal(BigDecimal.valueOf(value.doubleValue()), args[0], args[1]);
		else
			return BigDecimal.valueOf(value.doubleValue());
	}

	public static BigDecimal asDecimal(eglx.lang.ENumber value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return asDecimal(BigDecimal.valueOf(value.ezeUnbox().doubleValue()), args[0], args[1]);
		else
			return BigDecimal.valueOf(value.ezeUnbox().doubleValue());
	}

	public static BigDecimal asDecimal(String value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		try
		{
			return asDecimal( new BigDecimal( value ), args );
		}
		catch ( NumberFormatException ex )
		{
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "decimal";
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, value, tcx.actualTypeName, tcx.castToName );
		}
	}

	public static BigDecimal asDecimal(EString value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		return asDecimal(value.ezeUnbox(), args);
	}
	
	public static BigDecimal asDecimal( byte[] value, int precision, int scale ) throws AnyException 
	{
		if ( value == null )
		{
			return null;
		}
		
		int byteLength = precision / 2 + 1;
		if ( value.length == byteLength )
		{
			try
			{
				return new BigDecimal( NumericUtil.decimalToBigInteger( value, 0, precision ), scale );
			}
			catch ( InvalidArgumentException iax )
			{
				// Ignore it and throw a TypeCastException below.
			}
		}

		TypeCastException tcx = new TypeCastException();
		tcx.actualTypeName = "bytes(" + value.length + ')';
		tcx.castToName = "decimal(" + precision + ',' + scale + ')';
		throw tcx.fillInMessage( Message.CONVERSION_ERROR, value, tcx.actualTypeName, tcx.castToName );
	}

	public static BigDecimal asDecimal( EBytes value, int precision, int scale ) throws AnyException 
	{
		if ( value == null )
		{
			return null;
		}
		return asDecimal( value.ezeUnbox(), precision, scale );
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static EDecimal asNumber(BigDecimal value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		if (args.length == 2)
			return EDecimal.ezeBox(value, args[0], args[1]);
		else
			return EDecimal.ezeBox(value);
	}

	public static EDecimal asNumber(EDecimal value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		return value;
	}

	public static BigDecimal negate(BigDecimal op) throws AnyException {
		if (op == null) {
			NullValueException nvx = new NullValueException();
			throw nvx.fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		return op.negate();
	}

	public static BigDecimal plus(BigDecimal op1, BigDecimal op2) {
		return op1.add(op2);
	}

	public static BigDecimal minus(BigDecimal op1, BigDecimal op2) {
		return op1.subtract(op2);
	}

	public static BigDecimal divide(BigDecimal op1, BigDecimal op2) {
		return op1.divide(op2, BIGDECIMAL_RESULT_SCALE, TRUNCATE_BD);
	}

	public static BigDecimal multiply(BigDecimal op1, BigDecimal op2) {
		return op1.multiply(op2);
	}

	public static BigDecimal remainder(BigDecimal op1, BigDecimal op2) {
		return op1.remainder(op2);
	}

	public static double power(BigDecimal op1, BigDecimal op2) throws AnyException {
		return StrictMath.pow(op1.doubleValue(), op2.doubleValue());
	}

	public static int compareTo(BigDecimal op1, BigDecimal op2) throws AnyException {
		return op1.compareTo(op2);
	}

	public static boolean equals(BigDecimal op1, BigDecimal op2) {
		if (op1 == op2)
			return true;
		if (op1 == null || op2 == null)
			return false;
		return op1.compareTo(op2) == 0;
	}

	public static boolean notEquals(BigDecimal op1, BigDecimal op2) {
		return !equals(op1, op2);
	}
}
