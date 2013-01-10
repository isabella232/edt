/*******************************************************************************
 * Copyright © 2006, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.util;

import java.math.BigInteger;

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.Platform;

import eglx.lang.AnyException;
import eglx.lang.InvalidArgumentException;

/**
 * Utility methods that are used by the various "Numeric" Value classes.
 * 
 * @author mheitz
 */
public class NumericUtil
{
	/**
	 * This is BigInteger.valueOf( 100000000000000000L ).
	 */
	private final static BigInteger TEN_TO_THE_SEVENTEENTH = 
		BigInteger.valueOf( 100000000000000000L );

	/** 
	 * The sign mask for positive NUM and NUMC values in ASCII. 
	 */
	public final static byte ASCII_POSITIVE_NUM_NUMC_MASK = 0x30;

	/** 
	 * The sign mask for positive NUM values in EBCDIC. 
	 */
	public final static byte EBCDIC_POSITIVE_NUM_MASK = (byte)0xF0;

	/** 
	 * The sign mask for positive NUM values in the local character encoding. 
	 */
	public final static byte LOCAL_POSITIVE_NUM_MASK;

	/** 
	 * The sign mask for positive NUMC values in EBCDIC. 
	 */
	public final static byte EBCDIC_POSITIVE_NUMC_MASK = (byte)0xC0;

	/** 
	 * The sign mask for positive NUMC values in the local character encoding. 
	 */
	public final static byte LOCAL_POSITIVE_NUMC_MASK;

	/** 
	 * The sign mask for negative NUM and NUMC values in ASCII. 
	 */
	public final static byte ASCII_NEGATIVE_NUM_NUMC_MASK = 0x70;

	/** 
	 * The sign mask for negative NUM and NUMC values in EBCDIC. 
	 */
	public final static byte EBCDIC_NEGATIVE_NUM_NUMC_MASK = (byte)0xD0;

	/** 
	 * The sign mask for negative NUM and NUMC values in the local character
	 * encoding. 
	 */
	public final static byte LOCAL_NEGATIVE_NUM_NUMC_MASK;

	/** 
	 * The difference between the number zero and the character zero in ASCII.
	 */
	public final static byte ASCII_ZERO_DIFF = 0x30;
	
	/** 
	 * The difference between the number zero and the character zero in EBCDIC.
	 */
	public final static byte EBCDIC_ZERO_DIFF = (byte)0xF0;
	
	/** 
	 * The difference between the number zero and the character zero in the local
	 * character encoding.  
	 */
	public final static byte LOCAL_ZERO_DIFF;
	
	// Initialize the "LOCAL" constants.
	static
	{
		if ( Platform.IS_ASCII )
		{
			LOCAL_POSITIVE_NUM_MASK = ASCII_POSITIVE_NUM_NUMC_MASK;
			LOCAL_POSITIVE_NUMC_MASK = ASCII_POSITIVE_NUM_NUMC_MASK;
			LOCAL_NEGATIVE_NUM_NUMC_MASK = ASCII_NEGATIVE_NUM_NUMC_MASK;
			LOCAL_ZERO_DIFF = ASCII_ZERO_DIFF;
		}
		else
		{
			LOCAL_POSITIVE_NUM_MASK = EBCDIC_POSITIVE_NUM_MASK;
			LOCAL_POSITIVE_NUMC_MASK = EBCDIC_POSITIVE_NUMC_MASK;
			LOCAL_NEGATIVE_NUM_NUMC_MASK = EBCDIC_NEGATIVE_NUM_NUMC_MASK;
			LOCAL_ZERO_DIFF = EBCDIC_ZERO_DIFF;
		}
	}
	
	/**
	 * Returns the length in bytes of an item with the given characteristics.
	 * 
	 * @param digits  how many digits it has.
	 * @param isNum   true for NUM and NUMC, false for DECIMAL, MONEY, or PACF.
	 * @return the length in bytes of the item.
	 */
	public static int getLengthInBytes( int digits, boolean isNum )
	{
		if ( isNum )
		{
			// There's one byte per digit.
			return digits;
		}
		else
		{
			// This works for DECIMAL, MONEY, and PACF.  There are two digits
			// per byte, plus half a byte for the sign.  Round up to the nearest
			// whole byte.
			return (digits / 2) + 1;
		}
	}

	/**
	 * Converts a value to DECIMAL or PACF format, writing it into the buffer at
	 * the specified position.
	 * 
	 * @param value       the value.
	 * @param buffer      where to write the value.
	 * @param offset      index of the first byte in the buffer.
	 * @param length      the number of digits to write.
	 * @param itemLength  length of the value in bytes.
	 * @param pos         the positive sign to use: 0xC for DECIMAL, 0xF for PACF.
	 */
	public static void toDecimal( int value, byte[] buffer, int offset, int length, 
			int itemLength, byte pos )
	{
		int bufferIndex = offset + itemLength - 1;
		
		// Make sure the value is positive and set the sign.
		if ( value >= 0 )
		{
			buffer[ bufferIndex ] = pos;
		}
		else
		{
			value = -value;
			buffer[ bufferIndex ] = (byte)0x0D;
		}
		
		// Write the digits, starting from the end and working backwards.
		boolean wroteLow = true;
		int digitsToWrite = length;
		while ( value > 0 && digitsToWrite > 0 )
		{
			// Set the high nibble of the current byte.
			buffer[ bufferIndex ] |= (byte)((value % 10) << 4);
			digitsToWrite--;
			wroteLow = false;
			value = value / 10;

			// We're done with this byte.
			bufferIndex--;
			
			if ( value == 0 || digitsToWrite == 0 )
			{
				// We're done with value.
				break;
			}
			
			// Set the low nibble of the current byte.
			buffer[ bufferIndex ] = (byte)(value % 10);
			digitsToWrite--;
			wroteLow = true;
			value = value / 10;
		}
		
		// Fill with zeros if neccessary.
		if ( digitsToWrite > 0 )
		{
			if ( wroteLow )
			{
				// Since we just wrote the low nibble of a byte, the high nibble
				// is already a zero.  We can move to the next byte.
				digitsToWrite--;
				bufferIndex--;
			}
			
			while ( digitsToWrite > 0 )
			{
				// Zero out two nibbles at a time.
				buffer[ bufferIndex ] = 0;
				bufferIndex--;
				digitsToWrite -= 2;
			}
		}
	}
	
	/**
	 * Converts a value to DECIMAL or PACF format, writing it into the buffer at
	 * the specified position.
	 * 
	 * @param value       the value.
	 * @param buffer      where to write the value.
	 * @param offset      index of the first byte in the buffer.
	 * @param length      the number of digits to write.
	 * @param itemLength  length of the value in bytes.
	 * @param pos         the positive sign to use: 0xC for DECIMAL, 0xF for PACF.
	 */
	public static void toDecimal( long value, byte[] buffer, int offset, int length, 
			int itemLength, byte pos )
	{
		int bufferIndex = offset + itemLength - 1;
		
		// Make sure the value is positive and set the sign.
		if ( value >= 0 )
		{
			buffer[ bufferIndex ] = pos;
		}
		else
		{
			value = -value;
			buffer[ bufferIndex ] = (byte)0x0D;
		}
		
		// Write the digits, starting from the end and working backwards.
		boolean wroteLow = true;
		int digitsToWrite = length;
		while ( value > 0 && digitsToWrite > 0 )
		{
			// Set the high nibble of the current byte.
			buffer[ bufferIndex ] |= (byte)((value % 10) << 4);
			digitsToWrite--;
			wroteLow = false;
			value = value / 10;

			// We're done with this byte.
			bufferIndex--;
			
			if ( value == 0 || digitsToWrite == 0 )
			{
				// We're done with value.
				break;
			}
			
			// Set the low nibble of the current byte.
			buffer[ bufferIndex ] = (byte)(value % 10);
			digitsToWrite--;
			wroteLow = true;
			value = value / 10;
		}
		
		// Fill with zeros if neccessary.
		if ( digitsToWrite > 0 )
		{
			if ( wroteLow )
			{
				// Since we just wrote the low nibble of a byte, the high nibble
				// is already a zero.  We can move to the next byte.
				digitsToWrite--;
				bufferIndex--;
			}
			
			while ( digitsToWrite > 0 )
			{
				// Zero out two nibbles at a time.
				buffer[ bufferIndex ] = 0;
				bufferIndex--;
				digitsToWrite -= 2;
			}
		}
	}

	/**
	 * Converts a value to DECIMAL or PACF format, writing it into the buffer at
	 * the specified position.  Do not use this method if the data has fewer than
	 * 18 digits.  In that case, do this instead: 
	 * toDecimal( value.longValue(), ... ) ).
	 * 
	 * @param value       the value.
	 * @param buffer      where to write the value.
	 * @param offset      index of the first byte in the buffer.
	 * @param length      the number of digits to write, must be at least 18.
	 * @param itemLength  length of the value in bytes.
	 * @param pos         the positive sign to use: 0xC for DECIMAL, 0xF for PACF.
	 */
	public static void toDecimal( BigInteger value, byte[] buffer, int offset, 
			int length, int itemLength, byte pos )
	{
		if ( value.bitLength() < 63 )
		{
			// The value can be stored in a long, so use the method that doesn't
			// operate on BigIntegers.  It's much faster than this one.
			toDecimal( value.longValue(), buffer, offset, length, itemLength, pos );
			return;
		}
		
		// Split the value into two BigIntegers that can be converted to longs.
		BigInteger[] topAndBottom = value.divideAndRemainder( TEN_TO_THE_SEVENTEENTH );
		
		// Write the bottom half and sign using the other toDecimal method.
		int byteOfSeventeethDigit = offset + (length / 2) - 8;
		long bottom = topAndBottom[ 1 ].longValue();
		toDecimal( bottom, buffer, byteOfSeventeethDigit, 17, 9, pos );
		
		// Convert the top half to a long, and make sure it's positive since the
		// sign has already been written.
		long top = topAndBottom[ 0 ].longValue();
		if ( top < 0 )
		{
			top = -top;
		}
		
		// Write the top half of the digits.  Start from the end (digit 18) and
		// work backwards.
		int digitsToWrite = length - 17;
		int bufferIndex = byteOfSeventeethDigit - 1;
		while ( top > 0 && digitsToWrite > 0 )
		{
			// Set the low nibble of the current byte.
			buffer[ bufferIndex ] = (byte)(top % 10);
			digitsToWrite--;
			top = top / 10;

			if ( top == 0 || digitsToWrite == 0 )
			{
				// We're done with top.  If any zeros need to be added, we can
				// skip the high nibble of this byte.  It's already a zero.
				bufferIndex--;
				digitsToWrite--;
				break;
			}
			
			// Set the high nibble of the current byte.
			buffer[ bufferIndex ] |= (byte)((top % 10) << 4 );
			digitsToWrite--;
			top = top / 10;

			// We're done with this byte.
			bufferIndex--;
		}
		
		// Fill with zeros as necessary.
		while ( digitsToWrite > 0 )
		{
			// Zero out two nibbles at a time.
			buffer[ bufferIndex ] = 0;
			bufferIndex--;
			digitsToWrite -= 2;
		}
	}
	
	/**
	 * Returns a long made from the DECIMAL or PACF data in the buffer.
	 * 
	 * @param buffer  where to read the value from.
	 * @param offset  index of the first byte in the buffer.
	 * @param length  the number of digits to read.
	 * @return the value as a long.
	 * @throws InvalidArgumentException if the bytes are not in the expected format.
	 */
	public static long decimalToLong( byte[] buffer, int offset, int length )
		throws AnyException
	{
		// Make a long from the first digit, which is in the high nibble
		// of the first byte.
		long result = (buffer[ offset ] & 0xF0) >> 4;  
		
		// Now loop over the rest of the digits.
		for ( int lastByte = offset + (length / 2); offset < lastByte; )
		{
			// Pick up the next digit, which is in the low nibble of the byte.
			int nextDigit = buffer[ offset ] & 0xF;
			if ( nextDigit < 10 )
			{
				result = result * 10 + nextDigit;
			}
			else
			{
				throw new InvalidArgumentException().fillInMessage( Message.INVALID_DATA, "decimal" );
			}
			
			// Move to the next byte.
			offset++;
			
			// Pick up the next digit, which is in the high nibble of the byte.
			nextDigit = (buffer[ offset ] & 0xF0) >> 4;
			if ( nextDigit < 10 )
			{
				result = result * 10 + nextDigit;
			}
			else
			{
				throw new InvalidArgumentException().fillInMessage( Message.INVALID_DATA, "decimal" );
			}
		}
		
		// Now check the sign, which is in the low nibble of the byte.  The
		// negative sign is usually 0xD, but we recognize 0xB as well.
		int sign = buffer[ offset ] & 0xF;
		if ( sign == 0xC || sign == 0xF )
		{
			return result;
		}
		else if ( sign == 0xD || sign == 0xB )
		{
			return -result;
		}
		else
		{
			throw new InvalidArgumentException().fillInMessage( Message.INVALID_DATA, "decimal" );
		}
	}

	/**
	 * Returns a BigInteger made from the DECIMAL or PACF data in the buffer.
	 * 
	 * @param buffer  where to read the value from.
	 * @param offset  index of the first byte in the buffer.
	 * @param length  the number of digits to read.
	 * @return the value as a BigInteger.
	 * @throws InvalidArgumentException if the bytes are not in the expected format.
	 */
	public static BigInteger decimalToBigInteger( byte[] buffer, int offset, int length )
		throws AnyException
	{
		// Avoid using BigIntegers as much as possible.
		if ( length <= 17 )
		{
			return BigInteger.valueOf( decimalToLong( buffer, offset, length ) );
		}
		
		// Get the bottom 17 digits and the sign as a long.
		int byteOfSeventeethDigit = offset + (length / 2) - 8;
		long bottom = decimalToLong( buffer, byteOfSeventeethDigit, 17 );
		
		// Note the sign for later and make the value positive.
		boolean positive = true;
		if ( bottom < 0 )
		{
			bottom = -bottom;
			positive = false;
		}
		
		// Make a long from the top 1-15 digits.  Start by grabbing the digits
		// from the first byte.
		long top = (buffer[ offset ] & 0xF0) >> 4;
		if ( top > 9 )
		{
			throw new InvalidArgumentException().fillInMessage( Message.INVALID_DATA, "decimal" );
		}
		int nextDigit = buffer[ offset ] & 0xF;
		if ( nextDigit < 10 )
		{
			top = top * 10 + nextDigit;
		}
		else
		{
			throw new InvalidArgumentException().fillInMessage( Message.INVALID_DATA, "decimal" );
		}
		
		// Collect the rest of the remaining digits.
		for ( offset++; offset < byteOfSeventeethDigit; offset++ )
		{
			// Add in the digit from the high nibble.
			nextDigit = (buffer[ offset ] & 0xF0) >> 4;
			if ( nextDigit < 10 )
			{
				top = top * 10 + nextDigit;
			}
			else
			{
				throw new InvalidArgumentException().fillInMessage( Message.INVALID_DATA, "decimal" );
			}

			// Add in the digit from the low nibble.
			nextDigit = buffer[ offset ] & 0xF;
			if ( nextDigit < 10 )
			{
				top = top * 10 + nextDigit;
			}
			else
			{
				throw new InvalidArgumentException().fillInMessage( Message.INVALID_DATA, "decimal" );
			}
		}
		
		// Now merge bottom and top into a BigInteger.
		BigInteger bi;
		if ( top == 0 )
		{
			// There aren't any significant digits in top.
			bi = BigInteger.valueOf( bottom );
		}
		else
		{
			// The value is (top * 10^17) + bottom.
			bi = BigInteger.valueOf( top );
			bi = bi.multiply( TEN_TO_THE_SEVENTEENTH );
			bi = bi.add( BigInteger.valueOf( bottom ) );
		}
		
		// Correct the sign if we changed it.
		if ( positive )
		{
			return bi;
		}
		else
		{
			return bi.negate();
		}
	}

	/**
	 * Writes the value zero in NUM, NUMC, DECIMAL, or PACF format into the buffer
	 * at the specified position.
	 * 
	 * @param buffer      where to write the value.
	 * @param offset      index of the first byte in the buffer.
	 * @param itemLength  length of the value in bytes.
	 * @param pos         the positive sign to use.
	 * @param zero        the byte that indicates zero.
	 */
	public static void toZero( byte[] buffer, int offset, int itemLength, byte pos, byte zero )
	{
		// Fill with zeros.
		int i = 0;
		for ( ; i < itemLength - 1; i++ )
		{
			buffer[ offset + i ] = zero;
		}
		
		// Set the sign.
		buffer[ offset + i ] = pos;
	}

	/**
	 * Converts a value to NUM or NUMC format, writing it into the buffer at
	 * the specified position.
	 * 
	 * @param value     the value.
	 * @param buffer    where to write the value.
	 * @param offset    index of the first byte in the buffer.
	 * @param length    the number of digits to write.
	 * @param ascii     true if it's ASCII format.
	 * @param num       true if it's NUM not NUMC.
	 */
	public static void toNum( int value, byte[] buffer, int offset, int length, 
			boolean ascii, boolean num )
	{
		if ( ascii )
		{
			toNum( value, buffer, offset, length, 
					ASCII_POSITIVE_NUM_NUMC_MASK, 
					ASCII_NEGATIVE_NUM_NUMC_MASK, 
					ASCII_ZERO_DIFF );
		}
		else
		{
			toNum( value, buffer, offset, length, 
					num ? EBCDIC_POSITIVE_NUM_MASK : EBCDIC_POSITIVE_NUMC_MASK, 
					EBCDIC_NEGATIVE_NUM_NUMC_MASK, EBCDIC_ZERO_DIFF );
		}
	}

	/**
	 * Converts a value to NUM or NUMC format, writing it into the buffer at
	 * the specified position.
	 * 
	 * @param value     the value.
	 * @param buffer    where to write the value.
	 * @param offset    index of the first byte in the buffer.
	 * @param length    the number of digits to write.
	 * @param pos       the positive sign mask to use.
	 * @param neg       the negative sign mask to use.
	 * @param zero      the difference between the number zero and the character
	 *                    zero in the character encoding of the buffer.
	 */
	public static void toNum( int value, byte[] buffer, int offset, int length, 
			byte pos, byte neg, byte zero )
	{
		int bufferIndex = offset + length - 1;
		
		// Make sure the value is positive, then set the sign and last digit.
		if ( value >= 0 )
		{
			buffer[ bufferIndex ] = (byte)(pos | (value % 10));
		}
		else
		{
			value = -value;
			buffer[ bufferIndex ] = (byte)(neg | (value % 10));
		}		
		
		// Write the rest of the digits, starting from the end and working backwards.
		int digitsToWrite = length - 1;
		for ( value = value / 10; value > 0 && digitsToWrite > 0; digitsToWrite-- )
		{
			bufferIndex--;
			buffer[ bufferIndex ] = (byte)((value % 10) + zero);
			value = value / 10;
		}
		
		// Fill with zeros if neccessary.
		while ( digitsToWrite > 0 )
		{
			digitsToWrite--;
			bufferIndex--;
			buffer[ bufferIndex ] = zero;
		}
	}
	
	/**
	 * Converts a value to NUM or NUMC format, writing it into the buffer at
	 * the specified position.
	 * 
	 * @param value     the value.
	 * @param buffer    where to write the value.
	 * @param offset    index of the first byte in the buffer.
	 * @param length    the number of digits to write.
	 * @param ascii     true if it's ASCII format.
	 * @param num       true if it's NUM not NUMC.
	 */
	public static void toNum( long value, byte[] buffer, int offset, int length, 
			boolean ascii, boolean num )
	{
		if ( ascii )
		{
			toNum( value, buffer, offset, length, 
					ASCII_POSITIVE_NUM_NUMC_MASK, 
					ASCII_NEGATIVE_NUM_NUMC_MASK, 
					ASCII_ZERO_DIFF );
		}
		else
		{
			toNum( value, buffer, offset, length, 
					num ? EBCDIC_POSITIVE_NUM_MASK : EBCDIC_POSITIVE_NUMC_MASK, 
					EBCDIC_NEGATIVE_NUM_NUMC_MASK, EBCDIC_ZERO_DIFF );
		}
	}

	/**
	 * Converts a value to NUM or NUMC format, writing it into the buffer at
	 * the specified position.
	 * 
	 * @param value     the value.
	 * @param buffer    where to write the value.
	 * @param offset    index of the first byte in the buffer.
	 * @param length    the number of digits to write.
	 * @param pos       the positive sign mask to use.
	 * @param neg       the negative sign mask to use.
	 * @param zero      the difference between the number zero and the character
	 *                    zero in the character encoding of the buffer.
	 */
	public static void toNum( long value, byte[] buffer, int offset, int length, 
			byte pos, byte neg, byte zero )
	{
		int bufferIndex = offset + length - 1;
		
		// Make sure the value is positive, then set the sign and last digit.
		if ( value >= 0 )
		{
			buffer[ bufferIndex ] = (byte)(pos | (value % 10));
		}
		else
		{
			value = -value;
			buffer[ bufferIndex ] = (byte)(neg | (value % 10));
		}		
		
		// Write the rest of the digits, starting from the end and working backwards.
		int digitsToWrite = length - 1;
		for ( value = value / 10; value > 0 && digitsToWrite > 0; digitsToWrite-- )
		{
			bufferIndex--;
			buffer[ bufferIndex ] = (byte)((value % 10) + zero);
			value = value / 10;
		}
		
		// Fill with zeros if neccessary.
		while ( digitsToWrite > 0 )
		{
			digitsToWrite--;
			bufferIndex--;
			buffer[ bufferIndex ] = zero;
		}
	}

	/**
	 * Converts a value to NUM or NUMC format, writing it into the buffer at
	 * the specified position.  Do not use this method if the data has fewer than
	 * 18 digits.  In that case, do this instead: toNum( value.longValue(), ... ) ).
	 * 
	 * @param value     the value.
	 * @param buffer    where to write the value.
	 * @param offset    index of the first byte in the buffer.
	 * @param length    the number of digits to write.
	 * @param ascii     true if it's ASCII format.
	 * @param num       true if it's NUM not NUMC.
	 */
	public static void toNum( BigInteger value, byte[] buffer, int offset, 
			int length, boolean ascii, boolean num )
	{
		if ( ascii )
		{
			toNum( value, buffer, offset, length, 
					ASCII_POSITIVE_NUM_NUMC_MASK, 
					ASCII_NEGATIVE_NUM_NUMC_MASK, 
					ASCII_ZERO_DIFF );
		}
		else
		{
			toNum( value, buffer, offset, length, 
					num ? EBCDIC_POSITIVE_NUM_MASK : EBCDIC_POSITIVE_NUMC_MASK, 
					EBCDIC_NEGATIVE_NUM_NUMC_MASK, EBCDIC_ZERO_DIFF );
		}
	}
	
	/**
	 * Converts a value to NUM or NUMC format, writing it into the buffer at
	 * the specified position.  Do not use this method if the data has fewer than
	 * 18 digits.  In that case, do this instead: toNum( value.longValue(), ... ) ).
	 * 
	 * @param value     the value.
	 * @param buffer    where to write the value.
	 * @param offset    index of the first byte in the buffer.
	 * @param length    the number of digits to write.
	 * @param pos       the positive sign mask to use.
	 * @param neg       the negative sign mask to use.
	 * @param zero      the difference between the number zero and the character
	 *                    zero in the character encoding of the buffer.
	 */
	public static void toNum( BigInteger value, byte[] buffer, int offset, 
			int length, byte pos, byte neg, byte zero )
	{
		if ( value.bitLength() < 63 )
		{
			// The value can be stored in a long, so use the method that doesn't
			// operate on BigIntegers.  It's much faster than this one.
			toNum( value.longValue(), buffer, offset, length, pos, neg, zero );
			return;
		}

		// Split the value into two BigIntegers that can be converted to longs.
		BigInteger[] topAndBottom = value.divideAndRemainder( TEN_TO_THE_SEVENTEENTH );
		
		// Write the bottom half and sign using the other toNum method.
		int byteOfSeventeethDigit = offset + length - 17;
		long bottom = topAndBottom[ 1 ].longValue();
		toNum( bottom, buffer, byteOfSeventeethDigit, 17, pos, neg, zero );

		// Convert the top half to a long, and make sure it's positive since the
		// sign has already been written.
		long top = topAndBottom[ 0 ].longValue();
		if ( top < 0 )
		{
			top = -top;
		}

		// Write the top half of the digits.  Start from the end (digit 18) and
		// work backwards.
		int digitsToWrite = length - 17;
		int bufferIndex = byteOfSeventeethDigit;

		for ( ; top > 0 && digitsToWrite > 0; digitsToWrite-- )
		{
			bufferIndex--;
			buffer[ bufferIndex ] = (byte)((top % 10) + zero);
			top = top / 10;
		}
		
		// Fill with zeros if neccessary.
		while ( digitsToWrite > 0 )
		{
			digitsToWrite--;
			bufferIndex--;
			buffer[ bufferIndex ] = zero;
		}
	}
	
	/**
	 * Converts binary NUM/NUMC data from local format to remote format.
	 * 
	 * @param source        the source of the data.
	 * @param sourceOffset  where the data begins within source.
	 * @param length        how many digits we have.
	 * @param isNum         true if it's NUM data, false if it's NUMC data.
	 * @param target        where to put the result.
	 * @param targetOffset  where the result should begin within target.
	 */
	public static void numToRemote( byte[] source, int sourceOffset, int length, 
			boolean isNum, byte[] target, int targetOffset )
	{
		// Convert the digits.
		byte zero = Platform.IS_ASCII ? EBCDIC_ZERO_DIFF : ASCII_ZERO_DIFF;
		for ( int i = 0; i < length - 1; i++ )
		{
			target[ targetOffset + i ] = (byte)(zero + (byte)(source[ sourceOffset + i ] & 0xF));
		}
		target[ targetOffset + length - 1 ] = (byte)(source[ sourceOffset + length - 1 ] & 0xF);

		// Convert the sign (it's in the last byte).
		byte sign = (byte)(source[ sourceOffset + length - 1 ] & 0xF0);
		if ( Platform.IS_ASCII )
		{
			// The data is in ASCII.
			if ( sign == ASCII_POSITIVE_NUM_NUMC_MASK )
			{
				byte posSign = isNum ? EBCDIC_POSITIVE_NUM_MASK : EBCDIC_POSITIVE_NUMC_MASK;
				target[ targetOffset + length - 1 ] |= posSign;
			}
			else
			{
				target[ targetOffset + length - 1 ] |= EBCDIC_NEGATIVE_NUM_NUMC_MASK;
			}
		}
		else
		{
			// The data is in EBCDIC.
			if ( sign == EBCDIC_POSITIVE_NUM_MASK || sign == EBCDIC_POSITIVE_NUMC_MASK )
			{
				target[ targetOffset + length - 1 ] |= ASCII_POSITIVE_NUM_NUMC_MASK;
			}
			else
			{
				target[ targetOffset + length - 1 ] |= ASCII_NEGATIVE_NUM_NUMC_MASK;
			}
		}
	}

	// IEEE 754 Floating point format represents numbers as follows:
	// seee eeee | efff ffff | ffff ffff | ffff ffff
	// where s = sign bit
	// e = eponent bit
	// f = fraction bit
	// The decimal point is assumed at the left of the second byte.
	// When the exponent is non-zero, the MSB of the second byte is assumed to be
	// value 1 (2^-1 = 0.5).
	private final static int FLOAT_SIGN_MASK = 0x80000000;
	private final static int FLOAT_EXPONENT_MASK = 0x7f800000;
	private final static int FLOAT_MANTISSA_MASK = 0x007fffff;
	private final static int FLOAT_MANTISSA_MSB_MASK = 0x00800000;

	// The exponent is treated as an unsigned number, giving a range of 0..255.
	// A bias of 126 is subtracted from it to give the power used.
	private final static int FLOAT_BIAS = 126;

	// The value of the IEEE Float is then given by mantissa * (2^(exp-126)), the
	// sign bit giving the sign

	// S390 floats have the following format:
	// seee eee | ffff ffff | ffff ffff | ffff ffff
	// The exponent a power of 16, not 2.  Hence the best we can guarantee is that one
	// of the top four bits of the mantissa are non-zero - therefore there is no implied
	// bit as with IEEE
	// The exponent has a bias of 64, giving a range of -63..64
	private final static int S390_FLOAT_BIAS = 64;
	private final static int S390_FLOAT_EXPONENT_MASK = 0x7f000000;
	private final static int S390_FLOAT_MANTISSA_MASK = 0x00ffffff;

	/**
	 * Converts an IEEE float to an S390 float (as an int). 
	 * 
	 * @param ieeeFloat  the IEEE float.
	 * @return the number as an S390 float.
	 */
	public static int floatToS390IntBits( float ieeeFloat )
	{
		// To convert from IEEE to S390 we use the following formula:
		// let r = exponent % 4;  q = exponent / 4;
		// if q == 0 then m.2^x = m.16^q
		// if q != 0 then m.2^x = (m.2^(r-4)).16^(q+1)  for positive q,
		//                      = (m.2^-r).16^q) for negative q

		// Get the bit pattern
		int ieeeIntBits = Float.floatToIntBits( ieeeFloat );

		// Test the sign bit (0 = positive, 1 = negative)
		boolean positive = ((ieeeIntBits & FLOAT_SIGN_MASK) == 0);

		// Deal with zero straight away...
		if ( (ieeeIntBits & 0x7fffffff) == 0 )
		{
			// + or - 0.0
			return ieeeIntBits;
		}

		// Extract the exponent
		int exponent = ieeeIntBits & FLOAT_EXPONENT_MASK;
		// shift right 23 bits to get exponent in least significant byte
		exponent = exponent >>> 23;
		// subtract the bias to get the true value
		exponent = exponent - FLOAT_BIAS;

		// Extract the mantissa
		int mantissa = ieeeIntBits & FLOAT_MANTISSA_MASK;
		// for an exponent greater than -FLOAT_BIAS, add in the implicit bit
		if ( exponent > (-FLOAT_BIAS) )
		{
			mantissa = mantissa | FLOAT_MANTISSA_MSB_MASK;
		}

		// Now begin the conversion to S390
		int remainder = Math.abs( exponent ) % 4;
		int quotient = Math.abs( exponent ) / 4;
		int s390Exponent = quotient;
		if ( (exponent > 0) && (remainder != 0) )
		{
			s390Exponent = s390Exponent + 1;
		}

		// put the sign back in
		if ( exponent < 0 )
		{
			s390Exponent = -s390Exponent;
		}

		// Add the bias
		s390Exponent += S390_FLOAT_BIAS;

		// Now adjust the mantissa part
		int s390Mantissa = mantissa;
		if ( remainder > 0 )
		{
			if ( exponent > 0 )
			{
				// the next two lines perform the (m.2^(r-4)) part of the
				// conversion
				int shift_places = 4 - remainder;
				s390Mantissa = s390Mantissa >>> shift_places;
			}
			else
			{
				// to avoid loss of precision when the exponent is at a minimum,
				// we may need to shift the mantissa four places left, and
				// decrease the
				// s390Exponent by one before shifting right
				if ( (exponent == -(FLOAT_BIAS)) && ((s390Mantissa & 0x00f00000) == 0) )
				{
					s390Mantissa = s390Mantissa << 4;
					s390Exponent = s390Exponent - 1;
				}
				// the next two line perform the (m.2^-r) part of the conversion
				int shift_places = remainder;
				s390Mantissa = s390Mantissa >>> shift_places;
			}
		}

		// An exponent of -FLOAT_BIAS is the smallest that IEEE can do. S390 has
		// a wider range, and hence may be able to normalise the mantissa more
		// than
		// is possible for IEEE
		// Also, since an exponent of -FLOAT_BIAS has no implicit bit set, the
		// mantissa
		// starts with a value of 2^-1 at the second bit of the second byte. We
		// thus need
		// to shift one place left to move the mantissa to the S390 position
		// Follwoing that, we notmalise as follows:
		// Each shift left of four bits is equivalent to multiplying by 16,
		// so the exponent must be reduced by 1
		if ( exponent == -(FLOAT_BIAS) )
		{
			s390Mantissa = s390Mantissa << 1;
			while ( (s390Mantissa != 0) && ((s390Mantissa & 0x00f00000) == 0) )
			{
				s390Mantissa = s390Mantissa << 4;
				s390Exponent = s390Exponent - 1;
			}
		}

		// Assemble the s390BitPattern
		int s390Float = 0;
		int s390ExponentBits = s390Exponent & 0x0000007F;
		// make sure we only deal with 7 bits
		// add the exponent
		s390Float = s390ExponentBits << 24; // shift to MSB
		// add the sign
		if ( !positive )
		{
			s390Float = s390Float | FLOAT_SIGN_MASK;
		}
		// add the mantissa
		s390Float = s390Float | s390Mantissa;

		return s390Float;
	}

	/**
	 * Converts an S390 float (as an int) to an IEEE float. 
	 * 
	 * @param floatBits  the S390 float.
	 * @return the number as an IEEE float.
	 */
	public static float intS390BitsToFloat( int floatBits )
	{
		// To convert from S390 to IEEE we use the fomula:
		// m.16^x = m.2^4x, and then normalise by shifting the mantissa up to
		// three places left

		// Test the sign bit (0 = positive, 1 = negative)
		boolean positive = ((floatBits & FLOAT_SIGN_MASK) == 0);

		// Deal with zero straight away...
		if ( (floatBits & 0x7fffffff) == 0 )
		{
			// + or - 0.0
			if ( positive )
			{
				return 0.0F;
			}
			else
			{
				return -(0.0F);
			}
		}
		int mantissa = floatBits & S390_FLOAT_MANTISSA_MASK;
		int exponent = floatBits & S390_FLOAT_EXPONENT_MASK;

		// move the exponent into the LSB
		exponent = exponent >> 24;
		// subtract the bias
		exponent = exponent - S390_FLOAT_BIAS;

		// caculate the IEEE exponent
		int ieeeExponent = exponent * 4;

		// Normalise the mantissa
		int ieeeMantissa = mantissa;
		// Deal with exponents <= -FLOAT_BIAS
		if ( ieeeExponent <= -(FLOAT_BIAS) )
		{
			// ieeeMantissa is one place to the right since there is no implicit
			// bit set
			ieeeMantissa = ieeeMantissa >> 1;
			// now increase the exponent until it reaches -FLOAT_BIAS, shifting
			// right one
			// place at each stage to compensate
			while ( ieeeExponent < -(FLOAT_BIAS) )
			{
				ieeeExponent = ieeeExponent + 1;
				ieeeMantissa = ieeeMantissa >> 1;
			}
		}

		// Deal with exponents greater than -FLOAT_BIAS
		while ( (ieeeMantissa != 0) && ((ieeeMantissa & FLOAT_MANTISSA_MSB_MASK) == 0)
				&& (ieeeExponent > -(FLOAT_BIAS)) )
		{
			ieeeMantissa = ieeeMantissa << 1; // *2
			ieeeExponent = ieeeExponent - 1; // /2
		}

		// s390 has a wider range than IEEE, so deal with over and underflows
		if ( ieeeExponent < -149 )
		{
			return 0.0F; // underflow
		}
		else
		{
			if ( ieeeExponent > 128 )
			{
				if ( positive )
				{
					return (Float.MAX_VALUE * 2); // + infinity
				}
				else
				{
					return -(Float.MAX_VALUE * 2); // -infinity
				}
			}
		}

		// Build the IEEE float
		int ieeeBits = 0;
		if ( !positive )
		{
			ieeeBits = ieeeBits | FLOAT_SIGN_MASK;
		}

		// add the bias to the exponent
		ieeeExponent = ieeeExponent + FLOAT_BIAS;
		// move it to the IEEE exponent position
		ieeeExponent = ieeeExponent << 23;
		// add to the result
		ieeeBits = ieeeBits | ieeeExponent;

		// mask the top bit of the mantissa (implicit in IEEE)
		ieeeMantissa = ieeeMantissa & FLOAT_MANTISSA_MASK;
		// add to the result
		ieeeBits = ieeeBits | ieeeMantissa;

		return Float.intBitsToFloat( ieeeBits );
	}

	// IEEE 754 Floating point format represents double precision numbers as follows:
	// seee eeee | eeee ffff | ffff ffff | ffff ffff | ......
	// where s = sign bit
	// e = eponent bit
	// f = fraction bit
	// The decimal point is assumed at the left of the second byte.
	// When the exponent is non-zero, the MSB of the second byte is assumed to be
	// value 1 (2^-1 = 0.5).
	private final static long DOUBLE_SIGN_MASK = 0x8000000000000000L;
	private final static long DOUBLE_EXPONENT_MASK = 0x7ff0000000000000L;
	private final static long DOUBLE_MANTISSA_MASK = 0x000fffffffffffffL;
	private final static long DOUBLE_MANTISSA_MSB_MASK = 0x0010000000000000L;

	// The exponent is treated as an unsigned number
	// A bias of 1022 is subtracted from it to give the power used.
	private final static long DOUBLE_BIAS = 1022;

	// The value of the IEEE Float is then given by mantissa * (2^(exp-1022)), the
	// sign bit giving the sign

	// S390 double precision floats have the following format:
	// seee eee | ffff ffff | ffff ffff | ffff ffff | ....
	// The exponent a power of 16, not 2.  Hence the best we can guarantee is that one
	// of the top four bits of the mantissa are non-zero - therefore there is no implied
	// bit as with IEEE
	// The exponent has a bias of 64, giving a range of -63..64
	private final static int S390_DOUBLE_BIAS = 64;
	private final static long S390_DOUBLE_EXPONENT_MASK = 0x7f00000000000000L;
	private final static long S390_DOUBLE_MANTISSA_MASK = 0x00ffffffffffffffL;

	/**
	 * Converts an IEEE double to an S390 double (as a long). 
	 * 
	 * @param ieeeDouble  the IEEE double.
	 * @return the number as an S390 double.
	 */
	public static long doubleToS390LongBits( double ieeeDouble )
	{
		// To convert from IEEE to S390 we use the following formula:
		// let r = exponent % 4; q = exponent / 4;
		// if q == 0 then m.2^x = m.16^q
		// if q != 0 then m.2^x = (m.2^(r-4)).16^(q+1) for positive q,
		// = (m.2^-r).16^q) for negative q

		// Get the bit pattern
		long ieeeLongBits = Double.doubleToLongBits( ieeeDouble );

		// Test the sign bit (0 = positive, 1 = negative)
		boolean positive = ((ieeeLongBits & DOUBLE_SIGN_MASK) == 0);

		// Deal with zero straight away...
		if ( (ieeeLongBits & 0x7fffffffffffffffL) == 0 )
		{
			// + or - 0.0
			return ieeeLongBits;
		}

		// Extract the exponent
		long exponent = ieeeLongBits & DOUBLE_EXPONENT_MASK;
		// shift right 52 bits to get exponent in least significant byte
		exponent = exponent >>> 52;
		// subtract the bias to get the true value
		exponent = exponent - DOUBLE_BIAS;

		// Extract the mantissa
		long mantissa = ieeeLongBits & DOUBLE_MANTISSA_MASK;

		// Now begin the conversion to S390
		long remainder = Math.abs( exponent ) % 4;
		long quotient = Math.abs( exponent ) / 4;
		long s390Exponent = quotient;
		if ( (exponent > 0) && (remainder != 0) )
		{
			s390Exponent = s390Exponent + 1;
		}

		// put the sign back in
		if ( exponent < 0 )
		{
			s390Exponent = -s390Exponent;
		}

		// Add the bias
		s390Exponent += S390_DOUBLE_BIAS;

		// Now adjust the mantissa part
		long s390Mantissa = mantissa;
		// for an exponent greater than -DOUBLE_BIAS, add in the implicit bit
		if ( exponent > (-DOUBLE_BIAS) )
		{
			s390Mantissa = s390Mantissa | DOUBLE_MANTISSA_MSB_MASK;
		}
		else
		{
			// there is no implicit bit, so the mantissa is one bit to the right
			// of what
			// we would normally expect. We need to fix this for S390
			s390Mantissa = s390Mantissa << 1;
		}

		// S390 Mantissa starts 4 bits left of ieee one. The first of these is
		// implied in
		// IEEE so only shift 3 places
		s390Mantissa = s390Mantissa << 3;
		if ( remainder > 0 )
		{
			if ( exponent > 0 )
			{
				// the next two lines perform the (m.2^(r-4)) part of the
				// conversion
				int shift_places = (int)(4 - remainder);
				s390Mantissa = s390Mantissa >>> shift_places;
			}
			else
			{
				// to avoid loss of precision when the exponent is at a minimum,
				// we may need to shift the mantissa four places left and
				// decrease the
				// s390 exponent by one before shifting right
				if ( (exponent == -(DOUBLE_BIAS))
						&& ((s390Mantissa & 0x00f0000000000000L) == 0) )
				{
					s390Mantissa = s390Mantissa << 4;
					s390Exponent = s390Exponent - 1;
				}
				// the next two lines perform the m.2-r part of the conversion
				s390Mantissa = s390Mantissa >>> remainder;
			}
		}

		// An exponent of -DOUBLE_BIAS is the smallest that IEEE can do. S390
		// has
		// a wider range, and hence may be able to normalise the mantissa more
		// than
		// is possible for IEEE
		// Each shift left of four bits is equivalent to multiplying by 16,
		// so the exponent must be reduced by 1
		if ( exponent == -(DOUBLE_BIAS) )
		{
			while ( (s390Mantissa != 0) && ((s390Mantissa & 0x00f0000000000000L) == 0) )
			{
				s390Mantissa = s390Mantissa << 4;
				s390Exponent = s390Exponent - 1;
			}
		}

		// if the exponent is now > 127, we have an overflow since IEEE can
		// handle larger numbers
		// than S390 can.
		if ( s390Exponent > 127 )
		{
			throw new RuntimeException( //TODO need to make a proper AnyException for this
					"Number outside of range for double precision OS390 Float" );
		}
		else if ( s390Exponent < 0 )
		{
			// the number is too small to represent, set it to zero
			return 0L;
		}

		// Assemble the s390BitPattern
		long s390Double = 0L;
		long s390ExponentBits = s390Exponent & 0x000000000000007FL;
		// make sure we only deal with 7 bits
		// add the exponent
		s390Double = s390ExponentBits << 56; // shift to MSB
		// add the sign
		if ( !positive )
		{
			s390Double = s390Double | DOUBLE_SIGN_MASK;
		}
		// add the mantissa
		s390Double = s390Double | s390Mantissa;

		return s390Double;
	}

	/**
	 * Converts an S390 double (as a long) to an IEEE double. 
	 * 
	 * @param doubleBits  the S390 double.
	 * @return the number as an IEEE double.
	 */
	public static double longS390BitsToDouble( long doubleBits )
	{
		// To convert from S390 to IEEE we use the fomula:
		// m.16^x = m.2^4x, and then normalise by shifting the mantissa up to
		// three
		// places left

		// Test the sign bit (0 = positive, 1 = negative)
		boolean positive = ((doubleBits & DOUBLE_SIGN_MASK) == 0);

		// Deal with zero straight away...
		if ( (doubleBits & 0x7fffffffffffffffL) == 0 )
		{
			// + or - 0.0
			if ( positive )
			{
				return 0.0D;
			}
			else
			{
				return -(0.0D);
			}
		}
		long mantissa = doubleBits & S390_DOUBLE_MANTISSA_MASK;
		long exponent = doubleBits & S390_DOUBLE_EXPONENT_MASK;

		// move the exponent into the LSB
		exponent = exponent >> 56;
		// subtract the bias
		exponent = exponent - S390_DOUBLE_BIAS;

		// caculate the IEEE exponent
		long ieeeExponent = exponent * 4;

		// Normalise the mantissa
		long ieeeMantissa = mantissa;
		// IEEE mantissa starts three places right of S390 (+ implicit bit)
		ieeeMantissa = ieeeMantissa >> 3;
		// if this is the samllest possible exponent, then there is no implicit
		// bit,
		// and so we need to shift an extra bit
		if ( ieeeExponent <= -(DOUBLE_BIAS) )
		{
			ieeeMantissa = ieeeMantissa >> 1;
			// now increase the exponent until it reaches -DOUBLE_BIAS, shifting
			// right one place at each stage to compensate
			while ( ieeeExponent < -(DOUBLE_BIAS) )
			{
				ieeeExponent = ieeeExponent + 1;
				ieeeMantissa = ieeeMantissa >> 1;
			}
		}

		// complete the normalisation for exponents > -DOUBLE_BIAS
		while ( (ieeeMantissa != 0) && ((ieeeMantissa & DOUBLE_MANTISSA_MSB_MASK) == 0)
				&& (ieeeExponent > -(DOUBLE_BIAS)) )
		{
			ieeeMantissa = ieeeMantissa << 1; // *2
			ieeeExponent = ieeeExponent - 1; // /2
		}

		// s390 has a wider range than IEEE, so deal with over and underflows
		if ( ieeeExponent < -1045 )
		{
			return 0.0F; // underflow
		}
		else
		{
			if ( ieeeExponent > 1024 )
			{
				if ( positive )
				{
					return (Double.MAX_VALUE * 2); // + infinity
				}
				else
				{
					return -(Double.MAX_VALUE * 2); // -infinity
				}
			}
		}

		// Built the IEEE double
		long ieeeBits = 0;
		if ( !positive )
		{
			ieeeBits = ieeeBits | DOUBLE_SIGN_MASK;
		}

		// add the bias to the exponent
		ieeeExponent = ieeeExponent + DOUBLE_BIAS;
		// move it to the IEEE exponent position
		ieeeExponent = ieeeExponent << 52;
		// add to the result
		ieeeBits = ieeeBits | ieeeExponent;

		// mask the top bit of the mantissa (implicit in IEEE)
		ieeeMantissa = ieeeMantissa & DOUBLE_MANTISSA_MASK;
		// add to the result
		ieeeBits = ieeeBits | ieeeMantissa;

		return Double.longBitsToDouble( ieeeBits );
	}
}
