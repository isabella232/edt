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
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.JavartException;

import egl.lang.AnyNumber;


/**
 * Class to be used in processing Decimal operations 
 * 
 * @author twilson
 *
 */
public class EDecimal extends AnyBoxedObject<BigDecimal> implements AnyNumber {
	private static final long serialVersionUID = 80L;
	private static final BigDecimal ZERO = BigDecimal.ZERO;
	
	/**
	 * An array used for the upper limits of digit-oriented items that have
	 * values with decimals.
	 */
	private static final BigDecimal[][] MAX_DIGIT_ORIENTED_BD_VALUES;

	/**
	 * An array used for the lower limits of digit-oriented items that have
	 * values with decimals.
	 */
	private static final BigDecimal[][] MIN_DIGIT_ORIENTED_BD_VALUES;

	/**
	 * An array used for the upper limits of byte-oriented items that have
	 * values with decimals.
	 */
	private static final BigDecimal[][] MAX_BYTE_ORIENTED_BD_VALUES;

	/**
	 * An array used for the lower limits of byte-oriented items that have
	 * values with decimals.
	 */
	private static final BigDecimal[][] MIN_BYTE_ORIENTED_BD_VALUES;

	/** */
	public static final int TRUNCATE_BD = BigDecimal.ROUND_DOWN;

	/** */
	public static final int ROUND_BD = BigDecimal.ROUND_HALF_UP;

	public static final int BIGDECIMAL_RESULT_SCALE = 32;

	// Initialize the BD_VALUES arrays.
	static
	{
		// The first dimension of the DIGIT_ORIENTED arrays is based on the
		// length of the items (1-32). The second dimension is long enough to
		// hold the number of decimal digits that the items might have.
		MAX_DIGIT_ORIENTED_BD_VALUES = new BigDecimal[ 34 ][];
		MAX_DIGIT_ORIENTED_BD_VALUES[ 0 ] = new BigDecimal[ 1 ];
		MAX_DIGIT_ORIENTED_BD_VALUES[ 0 ][ 0 ] = ZERO;

		MIN_DIGIT_ORIENTED_BD_VALUES = new BigDecimal[ 34 ][];
		MIN_DIGIT_ORIENTED_BD_VALUES[ 0 ] = new BigDecimal[ 1 ];
		MIN_DIGIT_ORIENTED_BD_VALUES[ 0 ][ 0 ] = ZERO;

		for ( int i = 1; i < 34; i++ )
		{
			MAX_DIGIT_ORIENTED_BD_VALUES[ i ] = new BigDecimal[ i + 1 ];
			MIN_DIGIT_ORIENTED_BD_VALUES[ i ] = new BigDecimal[ i + 1 ];
		}

		// The first dimension of the BYTE_ORIENTED arrays is based on
		// the length of the items (4, 9, or 18 digits). The second dimension
		// is long enough to hold the number of decimal digits that the items
		// might have.
		MAX_BYTE_ORIENTED_BD_VALUES = new BigDecimal[ 3 ][];
		MAX_BYTE_ORIENTED_BD_VALUES[ 0 ] = new BigDecimal[ 5 ];
		MAX_BYTE_ORIENTED_BD_VALUES[ 0 ][ 0 ] = ZERO;
		MAX_BYTE_ORIENTED_BD_VALUES[ 1 ] = new BigDecimal[ 10 ];
		MAX_BYTE_ORIENTED_BD_VALUES[ 1 ][ 0 ] = ZERO;
		MAX_BYTE_ORIENTED_BD_VALUES[ 2 ] = new BigDecimal[ 19 ];
		MAX_BYTE_ORIENTED_BD_VALUES[ 2 ][ 0 ] = ZERO;

		MIN_BYTE_ORIENTED_BD_VALUES = new BigDecimal[ 3 ][];
		MIN_BYTE_ORIENTED_BD_VALUES[ 0 ] = new BigDecimal[ 5 ];
		MIN_BYTE_ORIENTED_BD_VALUES[ 0 ][ 0 ] = ZERO;
		MIN_BYTE_ORIENTED_BD_VALUES[ 1 ] = new BigDecimal[ 10 ];
		MIN_BYTE_ORIENTED_BD_VALUES[ 1 ][ 0 ] = ZERO;
		MIN_BYTE_ORIENTED_BD_VALUES[ 2 ] = new BigDecimal[ 19 ];
		MIN_BYTE_ORIENTED_BD_VALUES[ 2 ][ 0 ] = ZERO;
	}

	/**
	 * An array used for the upper limits of digit-oriented items (their length
	 * specifies a number of digits not a number of bytes) without decimals.
	 */
	private static final long[] MAX_DIGIT_ORIENTED_LONG_VALUES = {
		0,
		9,
		99,
		999,
		9999,
		99999,
		999999,
		9999999,
		99999999,
		999999999,
		9999999999L,
		99999999999L,
		999999999999L,
		9999999999999L,
		99999999999999L,
		999999999999999L,
		9999999999999999L,
		99999999999999999L,
		999999999999999999L,
		Long.MAX_VALUE
	};

	/**
	 * An array used for the lower limits of digit-oriented items (their length
	 * specifies a number of digits not a number of bytes) without decimals.
	 */
	private static final long[] MIN_DIGIT_ORIENTED_LONG_VALUES = {
		0,
		-9,
		-99,
		-999,
		-9999,
		-99999,
		-999999,
		-9999999,
		-99999999,
		-999999999,
		-9999999999L,
		-99999999999L,
		-999999999999L,
		-9999999999999L,
		-99999999999999L,
		-999999999999999L,
		-9999999999999999L,
		-99999999999999999L,
		-999999999999999999L,
		Long.MIN_VALUE
	};

	/**
	 * An array used for the upper limits of digit-oriented items (their length
	 * specifies a number of digits not a number of bytes) without decimals.
	 */
	public static final BigInteger[] MAX_DIGIT_ORIENTED_BI_VALUES = { BigInteger.ZERO,
			BigInteger.valueOf( 9 ), BigInteger.valueOf( 99 ), BigInteger.valueOf( 999 ),
			BigInteger.valueOf( 9999 ), BigInteger.valueOf( 99999 ),
			BigInteger.valueOf( 999999 ), BigInteger.valueOf( 9999999 ),
			BigInteger.valueOf( 99999999 ), BigInteger.valueOf( 999999999 ),
			BigInteger.valueOf( 9999999999L ), BigInteger.valueOf( 99999999999L ),
			BigInteger.valueOf( 999999999999L ), BigInteger.valueOf( 9999999999999L ),
			BigInteger.valueOf( 99999999999999L ),
			BigInteger.valueOf( 999999999999999L ),
			BigInteger.valueOf( 9999999999999999L ),
			BigInteger.valueOf( 99999999999999999L ),
			BigInteger.valueOf( 999999999999999999L ),
			new BigInteger( "9999999999999999999" ),
			new BigInteger( "99999999999999999999" ),
			new BigInteger( "999999999999999999999" ),
			new BigInteger( "9999999999999999999999" ),
			new BigInteger( "99999999999999999999999" ),
			new BigInteger( "999999999999999999999999" ),
			new BigInteger( "9999999999999999999999999" ),
			new BigInteger( "99999999999999999999999999" ),
			new BigInteger( "999999999999999999999999999" ),
			new BigInteger( "9999999999999999999999999999" ),
			new BigInteger( "99999999999999999999999999999" ),
			new BigInteger( "999999999999999999999999999999" ),
			new BigInteger( "9999999999999999999999999999999" ),
			new BigInteger( "99999999999999999999999999999999" ),
			new BigInteger( "999999999999999999999999999999999" ) };

	/**
	 * An array used for the lower limits of digit-oriented items (their length
	 * specifies a number of digits not a number of bytes) without decimals.
	 */
	public static final BigInteger[] MIN_DIGIT_ORIENTED_BI_VALUES = { BigInteger.ZERO,
			MAX_DIGIT_ORIENTED_BI_VALUES[ 1 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 2 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 3 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 4 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 5 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 6 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 7 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 8 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 9 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 10 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 11 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 12 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 13 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 14 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 15 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 16 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 17 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 18 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 19 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 20 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 21 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 22 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 23 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 24 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 25 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 26 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 27 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 28 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 29 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 30 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 31 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 32 ].negate(),
			MAX_DIGIT_ORIENTED_BI_VALUES[ 33 ].negate() };

	private int maxPrecision;
	
	public int getPrecision() {
		return maxPrecision;
	}
	
	public int getDecimals() {
		return object.scale();
	}
	
	private EDecimal(BigDecimal value) { 
		super(value); 
		maxPrecision = value.precision();
	}
	
	private EDecimal(BigDecimal value, int precision) { 
		super(value); 
		maxPrecision = precision;
	}

	public static EDecimal ezeBox(BigDecimal value) {
		return new EDecimal(value);
	}
	
	public static EDecimal ezeBox(BigDecimal value, int precision, int decimals) {
		return new EDecimal(value, precision);
	}

	/**
	 * Returns the upper limit for a digit-oriented item (its length specifies a
	 * number of digits not a number of bytes). This method will cache values in
	 * MAX_DIGIT_ORIENTED_BD_VALUES.
	 * 
	 * @param length
	 *            the number of decimal digits stored by the item.
	 * @param decimals
	 *            the number of decimal digits stored by the item.
	 * @return the largest number that the item can store.
	 */
	public static BigDecimal getMaxValue( int length, int decimals )
	{
		// Get the cached limit.
		BigDecimal max = MAX_DIGIT_ORIENTED_BD_VALUES[ length ][ decimals ];
		if ( max == null )
		{
			// Need to make it, and save it for later.
			max = new BigDecimal( MAX_DIGIT_ORIENTED_BI_VALUES[ length ], decimals );
			MAX_DIGIT_ORIENTED_BD_VALUES[ length ][ decimals ] = max;
		}

		return max;
	}

	/**
	 * Returns the lower limit for a digit-oriented item (its length specifies a
	 * number of digits not a number of bytes). This method will cache values in
	 * MIN_DIGIT_ORIENTED_BD_VALUES.
	 * 
	 * @param length
	 *            the number of decimal digits stored by the item.
	 * @param decimals
	 *            the number of decimal digits stored by the item.
	 * @return the smallest number that the item can store.
	 */
	public static BigDecimal getMinValue( int length, int decimals )
	{
		// Get the cached limit.
		BigDecimal min = MIN_DIGIT_ORIENTED_BD_VALUES[ length ][ decimals ];
		if ( min == null )
		{
			// Need to make it, and save it for later.
			BigDecimal max = getMaxValue( length, decimals );
			min = max.negate();
			MIN_DIGIT_ORIENTED_BD_VALUES[ length ][ decimals ] = min;
		}

		return min;
	}

	/**
	 * Call this when the assignment overflows and the target is a numeric type.
	 * 
	 * @param program
	 * @param target
	 * @param source
	 * @throws JavartException
	 */
	private static BigDecimal handleNumericOverflow( Executable program, BigDecimal value, int precision, int scale, boolean ignoreOverflow )
		throws JavartException
	{
		BigDecimal result = value;
		if ( ignoreOverflow )
		{
			// Don't throw an exception.  Store as much of the source as possible.
			// This algorithm comes from v6, and from VAGen before that.
			// TODO is this still the right algorithm?
			BigDecimal divisor = new BigDecimal( BigInteger.ONE, -(precision - scale));			
			
			// This is result = source % divisor.
			result = 
				value.subtract( value.divide( divisor, 0, BigDecimal.ROUND_DOWN )
						.multiply( divisor ) );
			
		}
		else
		{
			// The program wants an exception to be thrown.
			throw new NumericOverflowException();
			
//			String message = org.eclipse.edt.javart.util.JavartUtil.errorMessage(
//					program,
//					Message.EXPRESSION_OVERFLOW,
//					new Object[]{value.toString() + " as decimal(" + precision + ", " + scale + ")"});
//			throw new RuntimeException(Message.EXPRESSION_OVERFLOW, message);
		}
		return result;

	}
	
	public static Object ezeCast(Object value, Object[] constraints) throws JavartException {
		Integer[] args = new Integer[constraints.length];
		java.lang.System.arraycopy(constraints, 0, args, 0, args.length);
		return ezeCast(value, args);
	}
				
	public static BigDecimal ezeCast(Object value, Integer...args) throws JavartException {
		return (BigDecimal)AnyObject.ezeCast(value, "asDecimal", EDecimal.class, new Class[]{Integer[].class}, args);
	}
	
	public static boolean ezeIsa(Object value, Integer...args) {
		boolean isa = value instanceof EDecimal;
		if (isa && args.length != 0) {
			isa = ((EDecimal)value).getPrecision() == args[0];
			if (isa && args.length == 1) {
				isa = ((EDecimal)value).getDecimals() == 0;
			}
			else if (isa && args.length == 2) {
				isa = ((EDecimal)value).getDecimals() == args[1];
			}

		}
		return isa;
	}

	public static BigDecimal asDecimal(Executable program, Short value, Integer...args) throws JavartException {
		if (value == null) return null;
		
		if (args.length == 2) {
			return asDecimal(program, BigDecimal.valueOf(value), args[0], args[1]);
		}
		else {
			return BigDecimal.valueOf(value);
		}
	}

	public static BigDecimal asDecimal(Executable program, Integer value, Integer...args) throws JavartException {
		if (value == null) return null;
		
		if (args.length == 2) {
			return asDecimal(program, BigDecimal.valueOf(value), args[0], args[1]);
		}
		else {
			return BigDecimal.valueOf(value);
		}
	}
	
//	public static BigDecimal asDecimal(Executable program, Integer value, Integer...args) {
//		if (value == null) return null;
//		return asDecimal(program, value, args);
//	}

	public static BigDecimal asDecimal(Executable program, Long value, Integer...args) throws JavartException {
		if (value == null) return null;
		
		if (args.length == 2) {
			return asDecimal(program, BigDecimal.valueOf(value), args[0], args[1]);
		}
		else {
			return BigDecimal.valueOf(value);
		}
	}
	
	public static BigDecimal asDecimal(Executable program, BigDecimal value, Integer...args) throws JavartException {
		if (args.length == 2) {
			return asDecimal(program, value, args[0], args[1]);
		}
		else {
			return value;
		}
	}
	
	public static BigDecimal asDecimal(Executable program, BigDecimal value, int precision, int scale) throws JavartException {
		return asDecimal(program, value,getMaxValue(precision, scale), getMinValue(precision, scale), precision, scale, false);
	}

	public static BigDecimal asDecimal(Executable program, BigDecimal value, BigDecimal max, BigDecimal min, int precision, int scale) throws JavartException {
		return asDecimal(program, value, max, min, precision, scale, false);
	}

	public static BigDecimal asDecimal(Executable program, BigDecimal value, BigDecimal max, BigDecimal min, int precision, int scale, boolean ignoreOverflow) throws JavartException {
		if (value == null) return null;
		BigDecimal result = value;
		if ( scale < value.scale() )
		{
			// truncate or round the value based on the program setting
			// TODO set this variable up in Program
			// if ( program._truncateDecimals )
			if (false)
			{
				result = value.setScale( scale, TRUNCATE_BD );
			}
			else
			{
				result = value.setScale( scale, ROUND_BD );
			}
		}
		if (ignoreOverflow) {
			return result;
		}
		else {
			// Now make sure the value isn't too big for the target.
			if (( result.compareTo( max ) <= 0
					&& result.compareTo( min ) >= 0 ) )
			{
				return result;
			}
			else
			{
				return handleNumericOverflow( program, value, precision, scale, ignoreOverflow );
			}
		}
	}
	
	public static BigDecimal asDecimal(Executable program, String value, Integer...args) throws JavartException {
		return asDecimal(program, asDecimal(program, value, false), args);
	}
	
	public static BigDecimal asDecimal(Executable program, String value, boolean blanksAsZero) {
		// Check for zero length string and remove extra blanks.
		value = value.trim();
		if ( value.length() == 0 )
		{
			if ( blanksAsZero )
			{
				return BigDecimal.ZERO;
			}
			else
			{
				throw new NumberFormatException();
			}
		}
	
		// Remove a leading +.
		if ( value.charAt( 0 ) == '+' )
		{
			value = value.substring( 1 );
		}

		// If the string has an exponent, parse it with Double.  If not, but it does
		// have a decimal point, parse it with BigDecimal.  Recognize the decimal
		// point specified by the user as well as a period.
		// TODO EGL numeric values should not be caring about localized decimal points.
		// char decSym = prog._runUnit().getLocalizedText().getDecimalSymbol();
		char decSym = '.';
		if ( value.indexOf( 'e' ) != -1 || value.indexOf( 'E' ) != -1 )
		{
			return new BigDecimal( Double.parseDouble( value ) );
		}
		else if ( value.indexOf( decSym ) != -1 )
		{
			return new BigDecimal( value );
		}

		// if the length is bigger than 18 it won't fit in a long
		// TODO handle large numbers
		if ( value.length() > 18 )
		{
			return new BigDecimal(new BigInteger( value ));
		}

		return BigDecimal.valueOf(Long.valueOf( value ));

	}
	
	public static String asString(Executable program, BigDecimal value) throws JavartException {
		return value.toString();
	}
	
	public static BigDecimal plus(Executable program, BigDecimal op1, BigDecimal op2) {
		if (op1 == null || op2 == null) return null;
		return op1.add( op2 );
	}

	public static BigDecimal minus(Executable program, BigDecimal op1, BigDecimal op2) {
		if (op1 == null || op2 == null) return null;
		return op1.subtract( op2 );
	}

	public static BigDecimal divide(Executable program, BigDecimal op1, BigDecimal op2) {
		if (op1 == null || op2 == null) return null;
		return op1.divide( op2, BIGDECIMAL_RESULT_SCALE, ROUND_BD);
	}

	public static BigDecimal multiply(Executable program, BigDecimal op1, BigDecimal op2) {
		if (op1 == null || op2 == null) return null;
		return op1.multiply( op2 );
	}

	public static BigDecimal remainder(Executable program, BigDecimal op1, BigDecimal op2) {
		if (op1 == null || op2 == null) return null;
		return op1.remainder( op2 );
	}
	
	public static int compareTo(Executable program, BigDecimal op1, BigDecimal op2) throws JavartException {
		if (op1 == null || op2 == null) {
			throw new NullValueException();
		}
		return op1.compareTo(op2);
	}
	
	public static boolean equals(Executable program, BigDecimal op1, BigDecimal op2) {
		if (op1 == null && op2 == null) return true;
		if ((op1 != null && op2 == null) || (op1 == null && op2 != null)) return false;
		return op1.equals(op2);
	}
	
	public static boolean notEquals(Executable program, BigDecimal op1, BigDecimal op2) {
		return !equals(program, op1, op2);
	}
	
}
