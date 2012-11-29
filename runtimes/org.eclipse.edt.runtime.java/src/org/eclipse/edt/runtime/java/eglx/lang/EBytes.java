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

public class EBytes extends AnyBoxedObject<byte[]> {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	private int maxLength;

	public int getLength() {
		return maxLength;
	}

	private EBytes(byte[] value) {
		super(value);
		maxLength = -1;
	}

	private EBytes(byte[] value, int length) {
		super(value);
		maxLength = length;
	}

	public static EBytes ezeBox(byte[] value) {
		return new EBytes(value);
	}

	public static EBytes ezeBox(byte[] value, int length) {
		return new EBytes(value, length);
	}

	public static byte[] ezeCast(Object value, Integer... args) throws AnyException {
		return (byte[]) EAny.ezeCast(value, "asBytes", EBytes.class, new Class[] { Integer[].class }, args);
	}

	public static boolean ezeIsa(Object value, Integer... length) {
		boolean isa = (value instanceof EBytes && ((EBytes) value).ezeUnbox() != null);
		if (isa) {
			if (length.length != 0)
				isa = ((EBytes) value).getLength() == length[0];
		} else {
			isa = value instanceof byte[];
		}
		return isa;
	}
	
	public static byte[] ezeAssignToLonger( byte[] target, int targetLength, byte[] source )
	{
		if ( source == null )
		{
			return null;
		}
		else if ( target == null )
		{
			target = new byte[ targetLength ];
			System.arraycopy( source, 0, target, 0, source.length );
			return target;
		}
		else
		{
			System.arraycopy( source, 0, target, 0, source.length );
			return target;
		}
	}

	public static byte[] asBytes( Short value, Integer... length ) 
	{
		if ( length.length > 0 && length[ 0 ] != 2 )
		{
			throwTypeCastException( "smallint", value, length );
		}

		if ( value == null )
		{
			return null;
		}
		
		short bValue = value;
		byte[] bytes = new byte[ 2 ];
		bytes[ 0 ] = (byte)(bValue >> 8);
		bytes[ 1 ] = (byte)bValue;
		return bytes;
	}

	public static byte[] asBytes(ESmallint value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(value.ezeUnbox(), length);
	}

	public static byte[] asBytes( Integer value, Integer... length ) 
	{
		if ( length.length > 0 && length[ 0 ] != 4 )
		{
			throwTypeCastException( "int", value, length );
		}

		if ( value == null )
		{
			return null;
		}
		
		int bValue = value;
		byte[] bytes = new byte[ 4 ];		
		bytes[ 0 ] = (byte)(bValue >> 24);
		bytes[ 1 ] = (byte)(bValue >> 16);
		bytes[ 2 ] = (byte)(bValue >> 8);
		bytes[ 3 ] = (byte)bValue;
		return bytes;
	}

	public static byte[] asBytes(EInt value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(value.ezeUnbox(), length);
	}

	public static byte[] asBytes( Long value, Integer... length ) 
	{
		if ( length.length > 0 && length[ 0 ] != 8 )
		{
			throwTypeCastException( "bigint", value, length );
		}

		if ( value == null )
		{
			return null;
		}
		
		long bValue = value;
		byte[] bytes = new byte[ 8 ];
		bytes[ 0 ] = (byte)(bValue >> 56);
		bytes[ 1 ] = (byte)(bValue >> 48);
		bytes[ 2 ] = (byte)(bValue >> 40);
		bytes[ 3 ] = (byte)(bValue >> 32);
		bytes[ 4 ] = (byte)(bValue >> 24);
		bytes[ 5 ] = (byte)(bValue >> 16);
		bytes[ 6 ] = (byte)(bValue >> 8);
		bytes[ 7 ] = (byte)bValue;
		return bytes;
	}

	public static byte[] asBytes(EBigint value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(value.ezeUnbox(), length);
	}

	public static byte[] asBytes( Float value, Integer... length ) 
	{
		if ( length.length > 0 && length[ 0 ] != 4 )
		{
			throwTypeCastException( "smallfloat", value, length );
		}

		if ( value == null )
		{
			return null;
		}
		
		int bValue = Float.floatToIntBits( value );
		byte[] bytes = new byte[ 4 ];
		bytes[ 0 ] = (byte)(bValue >> 24);
		bytes[ 1 ] = (byte)(bValue >> 16);
		bytes[ 2 ] = (byte)(bValue >> 8);
		bytes[ 3 ] = (byte)bValue;
		return bytes;
	}

	public static byte[] asBytes(ESmallfloat value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(value.ezeUnbox(), length);
	}

	public static byte[] asBytes( Double value, Integer... length ) 
	{
		if ( length.length > 0 && length[ 0 ] != 8 )
		{
			throwTypeCastException( "float", value, length );
		}

		if ( value == null )
		{
			return null;
		}
		
		long bValue = Double.doubleToRawLongBits( value );
		byte[] bytes = new byte[ 8 ];
		bytes[ 0 ] = (byte)(bValue >> 56);
		bytes[ 1 ] = (byte)(bValue >> 48);
		bytes[ 2 ] = (byte)(bValue >> 40);
		bytes[ 3 ] = (byte)(bValue >> 32);
		bytes[ 4 ] = (byte)(bValue >> 24);
		bytes[ 5 ] = (byte)(bValue >> 16);
		bytes[ 6 ] = (byte)(bValue >> 8);
		bytes[ 7 ] = (byte)bValue;
		return bytes;
	}

	public static byte[] asBytes(EFloat value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(value.ezeUnbox(), length);
	}

	public static byte[] asBytes(BigDecimal value, Integer... attrs) {
		if (value == null)
			return null;
		
		// attrs[0] is the bytes's length
		// attrs[1] is the decimal's precision
		if ( attrs.length > 1 && attrs[ 0 ] != attrs[ 1 ] / 2 + 1 )
		{
			throwTypeCastException( "decimal(" + attrs[ 1 ] + ')', value, attrs );
		}
		return asBytes(value.unscaledValue(), attrs);
	}

	public static byte[] asBytes(EDecimal value, Integer... length) {
		if (value == null)
			return null;

		return asBytes(value.ezeUnbox(), length.length > 0 ? new Integer[] { length[ 0 ], value.getPrecision() } : length );
	}

	public static byte[] asBytes(BigInteger value, Integer... length) {
		if (value == null)
			return null;
		
		int digits = digits( value );
		byte[] bytes = new byte[ digits / 2 + 1 ];
		if ( length.length > 0 && length[ 0 ] != bytes.length )
		{
			throwTypeCastException( "decimal(" + digits + ')', value, length );
		}

		if ( digits < 18 )
		{
			NumericUtil.toDecimal( value.longValue(), bytes, 0, digits, bytes.length, (byte)0x0C );
		}
		else
		{
			NumericUtil.toDecimal( value, bytes, 0, digits, bytes.length, (byte)0x0C );
		}
		return bytes;
	}

	/**
	 * @return the number of digits in the given BigInteger.
	 */
	private static int digits( BigInteger bi )
	{
		int bitLength = bi.bitLength();
		if ( bitLength < 4 )
		{
			return 1;
		}
		else if ( bitLength < 63 )
		{
			double log10 = Math.log10( Math.abs( bi.longValue() ) );
			int digits = (int)Math.ceil( log10 );
			if ( log10 == (int)log10 )
			{
				// Add one for powers of 10.
				digits++;
			}
			return digits;
		}
		else
		{
			return new BigDecimal( bi ).precision();
		}
	}

	public static byte[] asBytes(Number value, Integer... length) {
		if (value == null)
			return null;
		
		if ( value instanceof Integer )
		{
			return asBytes( (Integer)value, length );
		}
		else if ( value instanceof Short )
		{
			return asBytes( (Short)value, length );
		}
		else if ( value instanceof Long )
		{
			return asBytes( (Long)value, length );
		}
		else if ( value instanceof BigInteger )
		{
			return asBytes( (BigInteger)value, length );
		}
		else if ( value instanceof BigDecimal )
		{
			return asBytes( (BigDecimal)value, length );
		}
		else if ( value instanceof Double )
		{
			return asBytes( (Double)value, length );
		}
		else if ( value instanceof Float )
		{
			return asBytes( (Float)value, length );
		}
				
		throwTypeCastException( "number", value, length );
		return null; // This is dead code but necessary for the method to compile.
	}

	public static byte[] asBytes(eglx.lang.ENumber value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(value.ezeUnbox(), length);
	}

	public static byte[] asBytes(byte[] value, Integer... length) {
		if (value == null)
			return null;
		if (length.length != 0 && value.length > length[0]) {
			byte[] bytes = new byte[length[0]];
			if (bytes.length > 0) {
				for (int i = 0; i < bytes.length; i++)
					bytes[i] = value[i];
			}
			return bytes;
		}
		return value;
	}

	public static byte[] asBytes(EBytes value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(value.ezeUnbox(), length);
	}

	private static void throwTypeCastException( String actualTypeName, Object value, Integer... length )
		throws TypeCastException
	{
		TypeCastException tcx = new TypeCastException();
		if ( length.length > 0 )
		{
			tcx.castToName = "bytes(" + length[ 0 ] + ')';
		}
		else
		{
			tcx.castToName = "bytes";
		}
		tcx.actualTypeName = actualTypeName;
		throw tcx.fillInMessage( Message.CONVERSION_ERROR, value, tcx.actualTypeName, tcx.castToName );
	}

	public static byte[] plus(byte[] op1, byte[] op2) throws AnyException {
		return concat(op1, op2);
	}

	public static byte[] concat(byte[] op1, byte[] op2) throws AnyException {
		int op1Length = (op1 == null ? 0 : op1.length);
		int op2Length = (op2 == null ? 0 : op2.length);

		byte[] bytes = new byte[ op1Length + op2Length ];
		int i = 0;
		while ( i < op1Length ) 
		{
			bytes[i] = op1[i];
			i++;
		}
		for ( int j = 0; j < op2Length; j++ )
		{
			bytes[i] = op2[j];
			i++;
		}
		return bytes;
	}

	public static byte[] concatNull(byte[] op1, byte[] op2) {
		if (op1 == null || op2 == null)
			return null;
		byte[] bytes = new byte[op1.length + op2.length];
		int i = 0;
		while (i < op1.length) {
			bytes[i] = op1[i];
			i++;
		}
		for ( int j = 0; j < op2.length; j++ )
		{
			bytes[i] = op2[j];
			i++;
		}
		return bytes;
	}

	public static boolean equals(byte[] op1, byte[] op2) throws AnyException {
		if (op1 == op2)
			return true;
		if (op1 == null || op2 == null || op1.length != op2.length)
			return false;
		for ( int i = 0; i < op1.length; i++ )
		{
			if ( op1[i] != op2[i] )
			{
				return false;
			}
		}
		return true;
	}

	public static boolean notEquals(byte[] op1, byte[] op2) throws AnyException {
		return !equals(op1, op2);
	}

	public static int compareTo( byte[] op1, byte[] op2 ) throws AnyException 
	{
		if ( op1.length == op2.length )
		{
			for ( int i = 0; i < op1.length; i++ )
			{
				if ( op1[i] != op2[i] )
				{
					return (op1[i] & 0xFF) < (op2[i] & 0xFF) ? -1 : 1;
				}
			}
		}
		else if ( op1.length > op2.length )
		{
			int i = 0;
			while ( i < op2.length )
			{
				if ( op1[i] != op2[i] )
				{
					return (op1[i] & 0xFF) < (op2[i] & 0xFF) ? -1 : 1;
				}
				i++;
			}
			while ( i < op1.length )
			{
				if ( op1[i] != 0 )
				{
					return 1;
				}
				i++;
			}
		}
		else
		{
			int i = 0;
			while ( i < op1.length )
			{
				if ( op1[i] != op2[i] )
				{
					return (op1[i] & 0xFF) < (op2[i] & 0xFF) ? -1 : 1;
				}
				i++;
			}
			while ( i < op2.length )
			{
				if ( op2[i] != 0 )
				{
					return -1;
				}
				i++;
			}
		}

		return 0;
	}

	public static byte[] substring(byte[] value, int start, int end) throws AnyException {
		if (value == null) {
			throw new NullValueException().fillInMessage(Message.NULL_NOT_ALLOWED);
		}
		int max = value.length;
		if (start < 1 || start > max) {
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = start;
			throw ex.fillInMessage(Message.INVALID_SUBSTRING_INDEX, start, end);
		} else if (end < start || end < 1 || end > max) {
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = end;
			throw ex.fillInMessage(Message.INVALID_SUBSTRING_INDEX, start, end);
		}
		byte[] bytes = new byte[end - start + 1];
		if (bytes.length > 0) {
			for (int i = 0; i < bytes.length; i++)
				bytes[i] = value[i + start - 1];
		}
		return bytes;
	}

	/**
	 * Returns the length of the bytes value.
	 */
	public static int length(byte[] source) {
		return source.length;
	}

}
