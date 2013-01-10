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
package org.eclipse.edt.javart;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.eclipse.edt.javart.resources.Platform;
import org.eclipse.edt.javart.util.NumericUtil;

/**
 * Utility class for persisting and reconstituting EGL runtime data to byte
 * arrays.  It can be configured to automatically do data conversion.  Conversion
 * is enabled by calling setConversion.  When enabled, the load methods (except
 * loadByte and loadBytes) convert to the local format and the store methods 
 * (except storeByte and storeBytes) convert from the local format.  If conversion
 * is enabled and you are reading or writing directly to the buffer, then you
 * must do the conversion yourself.
 * <P>
 * Character conversion is done in loadString and storeString.  If the encoding
 * is a Bidi encoding and the Unicode flag is true, the conversion is a two-stage
 * process.  Otherwise, conversion uses either the encoding or the Unicode flag,
 * but not both. 
 */
public class ByteStorage implements Cloneable, Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/** 
	 * Value for the byteOrder property. 
	 */
	public static final byte BYTEORDER_BIG_ENDIAN = 1;

	/** 
	 * Value for the byteOrder property. 
	 */
	public static final byte BYTEORDER_LITTLE_ENDIAN = 2;

	/** 
	 * Value for the byteOrder property. 
	 */
	public static final byte BYTEORDER_UNIX = 3;

	/** 
	 * Value for the reference = null. 
	 */
	public static final byte REF_NULL = 0x0;

	/** 
	 * Value for the reference not = null. 
	 */
	public static final byte REF_NOT_NULL = 0x01;

	/**
	 * On EBCDIC systems, this value signifies the start of double-byte characters.
	 * It must eventually be followed by SHIFT_IN.
	 */
	public final static byte SHIFT_OUT = 0x0E;
	
	/**
	 * On EBCDIC systems, this value signifies the end of double-byte characters
	 * until the next SHIFT_OUT.
	 */
	public final static byte SHIFT_IN  = 0x0F;

	/** 
	 * The buffer containing the data.
	 */
	private byte[] buffer;

	/** 
	 * The position of the read/write pointer in the buffer.
	 */
	public int position;

	/** 
	 * The byte order used to store the data (BYTEORDER_xxx).
	 */
	private byte byteOrder;

	/** 
	 * The character encoding in effect.  Null means use the local encoding.
	 */
	private String encoding;

	/** 
	 * True if the encoding indicates BIDI conversion.
	 */
	private boolean isBidi;

	/**
	 * True for ASCII, false for EBCDIC.
	 */
	private boolean isAscii;

	/** 
	 * True if character data is converted to Unicode.
	 */
	private boolean isUnicode;
	
	/**
	 * True if floating point numbers are in IEEE format.
	 */
	private boolean isIeeeFloat;
	
	/**
	 * Provides guidance for certain conversions.  When we're converting data to
	 * bytes, it's true if they'll be passed to EGL Java code.  When we're converting
	 * data from bytes, it's true if they were produced by EGL Java code.  The
	 * default value is true.
	 */
	private boolean eglJavaFormat;

	/**
	 * Constructor for storing data.
	 * <p>
	 * The internal buffer will be allocated with the desired initial capacity,
	 * and can grow dynamically if output fills it up.
	 * <P>
	 * By default, data is not converted from the local format.
	 * 
	 * @param capacity
	 *            The initial capacity
	 */
	public ByteStorage( int capacity )
	{
		this( new byte[ capacity ] );
	}

	/**
	 * Constructor for loading and/or storing data.
	 * <p>
	 * The data buffer can grow if written to, so changes will not necessarily
	 * appear in the array the caller passes to the constructor.
	 * <P>
	 * By default, data is not converted from the local format.
	 * 
	 * @param buffer
	 *            The data buffer to read from
	 */
	public ByteStorage( byte[] buffer )
	{
		this.buffer = buffer;
		this.position = 0;
		
		// Don't do conversion.
		this.byteOrder = BYTEORDER_BIG_ENDIAN;
		this.encoding = null;
		this.isAscii = Platform.IS_ASCII;
		this.isBidi = false;
		this.isUnicode = false;
		this.isIeeeFloat = true;
		this.eglJavaFormat = true;
	}
	
	/**
	 * Return the data buffer that has been written.
	 * 
	 * This returns the actual internal buffer, so if the caller changes the
	 * contents, it will affect the state of this ByteStorage.
	 * 
	 * However, if more data is added to this ByteStorage, the internal buffer
	 * can be reallocated to contain the additional data, and in that case the
	 * buffer previously returned by this method will no longer reflect the
	 * current state of this object.
	 * 
	 * @return The buffer
	 */
	public byte[] getBytes()
	{
		return this.buffer;
	}

	/**
	 * Return a copy of the current contents of the data buffer.
	 * 
	 * This returns a copy of the actual internal buffer, containing bytes from
	 * the start to the current position.
	 * 
	 * @return Copy of the buffer
	 */
	public byte[] getBytesCopy()
	{
		byte[] retbytes = new byte[ this.position ];

		System.arraycopy( this.buffer, 0, retbytes, 0, this.position );

		return retbytes;
	}

	/**
	 * Return the current in/out position
	 * 
	 * @return The position
	 */
	public int getPosition()
	{
		return this.position;
	}

	/**
	 * Set the current in/out position. If it is beyond the end of the buffer,
	 * the buffer is expanded (if allowed).
	 * 
	 * @param position
	 *            The position
	 */
	public void setPosition( int position )
	{
		if ( position > this.buffer.length )
		{
			ensureCapacity( position );
		}

		this.position = position;
	}

	/**
	 * Resize the internal buffer to a specific length. This can truncate data,
	 * and repositions the in/out pointer if necessary.
	 * 
	 * @param size
	 *            The desired length
	 */
	private void reallocate( int size )
	{
		byte[] newbuf = new byte[ size ];

		int copylen = Math.min( this.buffer.length, size );

		System.arraycopy( this.buffer, 0, newbuf, 0, copylen );

		this.buffer = newbuf;

		if ( this.position > size )
		{
			this.position = size;
		}
	}

	/**
	 * If necessary, increase the capacity of the storage to at least a desired
	 * size.
	 * 
	 * @param need
	 *            Minimum size necessary
	 */
	public void ensureCapacity( int need )
	{
		if ( this.buffer.length < need )
		{
			reallocate( Math.max( need, this.buffer.length * 2 ) );
		}
	}

	/**
	 * Call this to alter what conversion is done.
	 */
	public void setConversion( byte byteOrder, String encoding, boolean isAscii, 
			boolean isBidi, boolean isUnicode, boolean isIeeeFloat )
	{
		this.byteOrder = byteOrder;
		this.encoding = encoding;
		this.isAscii = isAscii;
		this.isUnicode = isUnicode;
		this.isIeeeFloat = isIeeeFloat;
		
		// isBidi must be false when the encoding is null.
		if ( encoding == null )
		{
			this.isBidi = false;
		}
		else
		{
			this.isBidi = isBidi;
		}
	}

	/**
	 * Call this to turn off conversion.  It is equivalent to:
	 * <PRE>
	 * setConversion( BYTEORDER_BIG_ENDIAN, null, Platform.IS_ASCII, false, false, true );
	 * setEglJavaFormat( true );
	 * </PRE>
	 */
	public void setNoConversion()
	{
		setConversion( BYTEORDER_BIG_ENDIAN, null, Platform.IS_ASCII, false, false, true );
		setEglJavaFormat( true );
	}

	/**
	 * Changes the underlying buffer and sets the position to zero.  Does not
	 * change the conversion settings.
	 * 
	 * @param newBuffer  the new buffer.
	 */
	public void reset( byte[] newBuffer )
	{
		buffer = newBuffer;
		position = 0;
	}
	
	/**
	 * Add a number of blanks to the output stream.
	 * 
	 * @param count  how many blanks to add.
	 * @return the new position.
	 */
	public int storeBlanks( int count )
	{
		if ( isUnicode )
		{
			// Store blanks from Constants.FIFTY_UNICODE_BLANK_BYTES.
			byte[] blanks = Constants.FIFTY_UNICODE_BLANK_BYTES;
			while ( count >= 50 )
			{
				storeBytes( blanks, 0, 100 );
				count -= 50;
			}
			if ( count > 0 )
			{
				storeBytes( blanks, 0, count * 2 );
			}
		}
		else if ( encoding == null || isAscii == Platform.IS_ASCII )
		{
			// Store blanks from Constants.HUNDRED_BLANK_BYTES.
			byte[] blanks = Constants.HUNDRED_BLANK_BYTES;
			while ( count >= 100 )
			{
				storeBytes( blanks, 0, 100 );
				count -= 100;
			}
			if ( count > 0 )
			{
				storeBytes( blanks, 0, count );
			}
		}
		else
		{
			int newPosition = position + count;
			ensureCapacity( newPosition );
			Arrays.fill( buffer, position, newPosition, isAscii ? (byte)0x20 : (byte)0x40 );
			position = newPosition;
		}
		
		return position;
	}

	/**
	 * Add a byte to the output stream.  Does no conversion.
	 * 
	 * @param value
	 *            The value
	 * @return The new position
	 */
	public int storeByte( int value )
	{
		int pos = this.position;

		try
		{
			this.buffer[ pos++ ] = (byte)value;

			this.position = pos;
		}
		catch ( ArrayIndexOutOfBoundsException e )
		{
			ensureCapacity( this.position + 1 );

			pos = this.position;
			this.buffer[ pos++ ] = (byte)value;
			this.position = pos;
		}

		return this.position;
	}

	/**
	 * Add bytes to the output stream.  Does no conversion.
	 * 
	 * @param value
	 *            The value
	 * @param start
	 *            The starting offset in the input buffer (value)
	 * @param length
	 *            How many bytes to write
	 * @return The new position
	 */
	public int storeBytes( byte[] value, int start, int length )
	{
		try
		{
			System.arraycopy( value, start, this.buffer, this.position, length );

			this.position += length;
		}
		catch ( IndexOutOfBoundsException e )
		{
			ensureCapacity( this.position + length );

			System.arraycopy( value, start, this.buffer, this.position, length );
			this.position += length;
		}

		return this.position;
	}

	/**
	 * Add bytes to the output stream.  Does no conversion.
	 * 
	 * @param value
	 *            The value
	 * @return The new position
	 */
	public int storeBytes( byte[] value )
	{
		return storeBytes( value, 0, value.length );
	}

	/**
	 * Add a short to the output stream.  Does conversion according to the 
	 * byte order.
	 * 
	 * @param value
	 *            The value
	 * @return The new position
	 */
	public int storeShort( int value )
	{
		int pos = this.position;

		try
		{
			if ( byteOrder != BYTEORDER_LITTLE_ENDIAN )
			{			
				this.buffer[ pos ] = (byte)(value >> 8);
				this.buffer[ pos + 1 ] = (byte)value;
			}
			else
			{
				this.buffer[ pos + 1 ] = (byte)(value >> 8);
				this.buffer[ pos ] = (byte)value;
			}
			this.position = pos + 2;
		}
		catch ( ArrayIndexOutOfBoundsException e )
		{
			ensureCapacity( this.position + 2 );

			if ( byteOrder != BYTEORDER_LITTLE_ENDIAN )
			{			
				this.buffer[ pos ] = (byte)(value >> 8);
				this.buffer[ pos + 1 ] = (byte)value;
			}
			else
			{
				this.buffer[ pos + 1 ] = (byte)(value >> 8);
				this.buffer[ pos ] = (byte)value;
			}
			this.position = pos + 2;
		}

		return this.position;
	}

	/**
	 * Add a short to the output stream.  Does conversion according to the 
	 * byte order.  The value must be in big-endian format.
	 * 
	 * @param value
	 *            The 2-byte value
	 * @param start
	 *            The first byte of the value
	 * @return The new position
	 */
	public int storeShort( byte[] value, int start )
	{
		if ( byteOrder != BYTEORDER_LITTLE_ENDIAN )
		{
			return storeBytes( value, start, 2 );
		}
		else
		{
			int shortValue = 
				((value[ start ] & 0xFF) << 8)
				| (value[ start + 1 ] & 0xFF);
			return storeShort( shortValue );
		}
	}
	
	/**
	 * Add an int to the output stream.  Does conversion according to the 
	 * byte order.
	 * 
	 * @param value
	 *            The value
	 * @return The new position
	 */
	public int storeInt( int value )
	{
		int pos = this.position;

		try
		{
			if ( byteOrder != BYTEORDER_LITTLE_ENDIAN )
			{			
				this.buffer[ pos ] = (byte)(value >> 24);
				this.buffer[ pos + 1 ] = (byte)(value >> 16);
				this.buffer[ pos + 2 ] = (byte)(value >> 8);
				this.buffer[ pos + 3 ] = (byte)value;
			}
			else
			{
				this.buffer[ pos + 3 ] = (byte)(value >> 24);
				this.buffer[ pos + 2 ] = (byte)(value >> 16);
				this.buffer[ pos + 1 ] = (byte)(value >> 8);
				this.buffer[ pos ] = (byte)value;
			}

			this.position = pos + 4;
		}
		catch ( ArrayIndexOutOfBoundsException e )
		{
			ensureCapacity( this.position + 4 );

			if ( byteOrder != BYTEORDER_LITTLE_ENDIAN )
			{			
				this.buffer[ pos ] = (byte)(value >> 24);
				this.buffer[ pos + 1 ] = (byte)(value >> 16);
				this.buffer[ pos + 2 ] = (byte)(value >> 8);
				this.buffer[ pos + 3 ] = (byte)value;
			}
			else
			{
				this.buffer[ pos + 3 ] = (byte)(value >> 24);
				this.buffer[ pos + 2 ] = (byte)(value >> 16);
				this.buffer[ pos + 1 ] = (byte)(value >> 8);
				this.buffer[ pos ] = (byte)value;
			}

			this.position = pos + 4;
		}

		return this.position;
	}

	/**
	 * Add an int to the output stream.  Does conversion according to the 
	 * byte order.  The value must be in big-endian format.
	 * 
	 * @param value
	 *            The 4-byte value
	 * @param start
	 *            The first byte of the value
	 * @return The new position
	 */
	public int storeInt( byte[] value, int start )
	{
		if ( byteOrder != BYTEORDER_LITTLE_ENDIAN )
		{
			return storeBytes( value, start, 4 );
		}
		else
		{
			int intValue = 
				((value[ start ] & 0xFF) << 24)
				| ((value[ start + 1 ] & 0xFF) << 16)
				| ((value[ start + 2 ] & 0xFF) << 8)
				| (value[ start + 3 ] & 0xFF);
			return storeInt( intValue );
		}
	}
	
	/**
	 * Add a long to the output stream.  Does conversion according to the 
	 * byte order.
	 * 
	 * @param value
	 *            The value
	 * @return The new position
	 */
	public int storeLong( long value )
	{
		int pos = this.position;

		try
		{
			switch ( byteOrder )
			{
				case BYTEORDER_BIG_ENDIAN:
					this.buffer[ pos ] = (byte)(value >> 56);
					this.buffer[ pos + 1 ] = (byte)(value >> 48);
					this.buffer[ pos + 2 ] = (byte)(value >> 40);
					this.buffer[ pos + 3 ] = (byte)(value >> 32);
					this.buffer[ pos + 4 ] = (byte)(value >> 24);
					this.buffer[ pos + 5 ] = (byte)(value >> 16);
					this.buffer[ pos + 6 ] = (byte)(value >> 8);
					this.buffer[ pos + 7 ] = (byte)value;
					break;

				case BYTEORDER_LITTLE_ENDIAN:
					this.buffer[ pos + 7 ] = (byte)(value >> 56);
					this.buffer[ pos + 6 ] = (byte)(value >> 48);
					this.buffer[ pos + 5 ] = (byte)(value >> 40);
					this.buffer[ pos + 4 ] = (byte)(value >> 32);
					this.buffer[ pos + 3 ] = (byte)(value >> 24);
					this.buffer[ pos + 2 ] = (byte)(value >> 16);
					this.buffer[ pos + 1 ] = (byte)(value >> 8);
					this.buffer[ pos ] = (byte)value;
					break;
					
				case BYTEORDER_UNIX:
					// Same as BIG_ENDIAN but swap the first/last four bytes.
					this.buffer[ pos + 4 ] = (byte)(value >> 56);
					this.buffer[ pos + 5 ] = (byte)(value >> 48);
					this.buffer[ pos + 6 ] = (byte)(value >> 40);
					this.buffer[ pos + 7 ] = (byte)(value >> 32);
					this.buffer[ pos ] = (byte)(value >> 24);
					this.buffer[ pos + 1 ] = (byte)(value >> 16);
					this.buffer[ pos + 2 ] = (byte)(value >> 8);
					this.buffer[ pos + 3 ] = (byte)value;
					break;
			}

			this.position = pos + 8;
		}
		catch ( ArrayIndexOutOfBoundsException e )
		{
			ensureCapacity( this.position + 8 );

			switch ( byteOrder )
			{
				case BYTEORDER_BIG_ENDIAN:
					this.buffer[ pos ] = (byte)(value >> 56);
					this.buffer[ pos + 1 ] = (byte)(value >> 48);
					this.buffer[ pos + 2 ] = (byte)(value >> 40);
					this.buffer[ pos + 3 ] = (byte)(value >> 32);
					this.buffer[ pos + 4 ] = (byte)(value >> 24);
					this.buffer[ pos + 5 ] = (byte)(value >> 16);
					this.buffer[ pos + 6 ] = (byte)(value >> 8);
					this.buffer[ pos + 7 ] = (byte)value;
					break;

				case BYTEORDER_LITTLE_ENDIAN:
					this.buffer[ pos + 7 ] = (byte)(value >> 56);
					this.buffer[ pos + 6 ] = (byte)(value >> 48);
					this.buffer[ pos + 5 ] = (byte)(value >> 40);
					this.buffer[ pos + 4 ] = (byte)(value >> 32);
					this.buffer[ pos + 3 ] = (byte)(value >> 24);
					this.buffer[ pos + 2 ] = (byte)(value >> 16);
					this.buffer[ pos + 1 ] = (byte)(value >> 8);
					this.buffer[ pos ] = (byte)value;
					break;
					
				case BYTEORDER_UNIX:
					// Same as BIG_ENDIAN but swap the first/last four bytes.
					this.buffer[ pos + 4 ] = (byte)(value >> 56);
					this.buffer[ pos + 5 ] = (byte)(value >> 48);
					this.buffer[ pos + 6 ] = (byte)(value >> 40);
					this.buffer[ pos + 7 ] = (byte)(value >> 32);
					this.buffer[ pos ] = (byte)(value >> 24);
					this.buffer[ pos + 1 ] = (byte)(value >> 16);
					this.buffer[ pos + 2 ] = (byte)(value >> 8);
					this.buffer[ pos + 3 ] = (byte)value;
					break;
			}

			this.position = pos + 8;
		}

		return this.position;
	}
	
	/**
	 * Add a long to the output stream.  Does conversion according to the 
	 * byte order.  The value must be in big-endian format.
	 * 
	 * @param value
	 *            The 8-byte value
	 * @param start
	 *            The first byte of the value
	 * @return The new position
	 */
	public int storeLong( byte[] value, int start )
	{
		if ( byteOrder == BYTEORDER_BIG_ENDIAN )
		{
			return storeBytes( value, start, 8 );
		}
		else
		{
			long longValue = 
				((value[ start ] & 0xFFL) << 56)
				| ((value[ start + 1 ] & 0xFFL) << 48)
				| ((value[ start + 2 ] & 0xFFL) << 40)
				| ((value[ start + 3 ] & 0xFFL) << 32)
				| ((value[ start + 4 ] & 0xFFL) << 24)
				| ((value[ start + 5 ] & 0xFFL) << 16)
				| ((value[ start + 6 ] & 0xFFL) << 8)
				| (value[ start + 7 ] & 0xFFL);
			
			return storeLong( longValue );
		}
	}
	
	/**
	 * Add the bytes of a float to the input stream.  Does conversion according
	 * to the IEEE flag.
	 * 
	 * @param value
	 *            The value
	 * @return The new position
	 */
	public int storeFloat( float value )
	{
		int pos = this.position;

		int bits;
		if ( isIeeeFloat )
		{
			bits = Float.floatToRawIntBits( value );
		}
		else
		{
			bits = NumericUtil.floatToS390IntBits( value );
		}
		
		try
		{
			this.buffer[ pos ] = (byte)(bits >> 24);
			this.buffer[ pos + 1 ] = (byte)(bits >> 16);
			this.buffer[ pos + 2 ] = (byte)(bits >> 8);
			this.buffer[ pos + 3 ] = (byte)bits;

			this.position = pos + 4;
		}
		catch ( ArrayIndexOutOfBoundsException e )
		{
			ensureCapacity( this.position + 4 );

			this.buffer[ pos ] = (byte)(bits >> 24);
			this.buffer[ pos + 1 ] = (byte)(bits >> 16);
			this.buffer[ pos + 2 ] = (byte)(bits >> 8);
			this.buffer[ pos + 3 ] = (byte)bits;

			this.position = pos + 4;
		}

		return this.position;
	}
	
	/**
	 * Add the bytes of a double to the input stream.  Does conversion according
	 * to the IEEE flag.
	 * 
	 * @param value
	 *            The value
	 * @return The new position
	 */
	public int storeDouble( double value )
	{
		int pos = this.position;

		long bits;
		if ( isIeeeFloat )
		{
			bits = Double.doubleToRawLongBits( value );
		}
		else
		{
			bits = NumericUtil.doubleToS390LongBits( value );
		}
		
		try
		{
			this.buffer[ pos ] = (byte)(bits >> 56);
			this.buffer[ pos + 1 ] = (byte)(bits >> 48);
			this.buffer[ pos + 2 ] = (byte)(bits >> 40);
			this.buffer[ pos + 3 ] = (byte)(bits >> 32);
			this.buffer[ pos + 4 ] = (byte)(bits >> 24);
			this.buffer[ pos + 5 ] = (byte)(bits >> 16);
			this.buffer[ pos + 6 ] = (byte)(bits >> 8);
			this.buffer[ pos + 7 ] = (byte)bits;
			
			this.position = pos + 8;
		}
		catch ( ArrayIndexOutOfBoundsException e )
		{
			ensureCapacity( this.position + 8 );

			this.buffer[ pos ] = (byte)(bits >> 56);
			this.buffer[ pos + 1 ] = (byte)(bits >> 48);
			this.buffer[ pos + 2 ] = (byte)(bits >> 40);
			this.buffer[ pos + 3 ] = (byte)(bits >> 32);
			this.buffer[ pos + 4 ] = (byte)(bits >> 24);
			this.buffer[ pos + 5 ] = (byte)(bits >> 16);
			this.buffer[ pos + 6 ] = (byte)(bits >> 8);
			this.buffer[ pos + 7 ] = (byte)bits;
			
			this.position = pos + 8;
		}

		return this.position;
	}

	/**
	 * Add the bytes of a String to the input stream.  Does conversion according
	 * to the encoding and isUnicode flag.
	 * 
	 * @param value
	 *            The value
	 * @return The new position
	 */
	public int storeString( String value )
	{
		return storeString( value, false );
	}
	
	/**
	 * Add a dbchar's bytes to the input stream.  Does conversion according to
	 * the encoding and isUnicode flag.
	 * 
	 * @param value
	 *            The value
	 * @return The new position
	 */
	public int storeDbchar( String value )
	{
		return storeString( value, true );
	}
	
	/**
	 * Add the bytes of a String to the input stream.  Does conversion according
	 * to the encoding and isUnicode flag.
	 * 
	 * @param value
	 *            The value
	 * @param stripSOSI
	 *            True if we should remove shift-in and shift-out bytes before
	 *            storing the value in the buffer.
	 * @return The new position
	 */
	private int storeString( String value, boolean stripSOSI )
	{
		byte[] bytes;
		if ( encoding == null && !isUnicode )
		{
			// Use the local format.
			bytes = value.getBytes();
			if ( stripSOSI )
			{
				bytes = stripSOSI( bytes );
			}
		}
		else if ( encoding != null )
		{
			// Use the encoding.
			try
			{
				bytes = value.getBytes( encoding );
				if ( stripSOSI )
				{
					bytes = stripSOSI( bytes );
				}
			}
			catch ( UnsupportedEncodingException uex )
			{
				bytes = value.getBytes();
				if ( stripSOSI )
				{
					bytes = stripSOSI( bytes );
				}
			}
		}
		else
		{
			// Convert to Unicode.
			bytes = new byte[ value.length() * 2 ];
			for ( int i = 0, j = 0; i < bytes.length; i += 2, j++ )
			{
				char c = value.charAt( j );
				bytes[ i ] = (byte)(c >> 8 );
				bytes[ i + 1 ] = (byte)c;
			}
		}
		
		return storeBytes( bytes, 0, bytes.length );
	}

	/**
	 * If the input starts with shift-out and ends with shift-in, returns a new
	 * array that doesn't include them.  Otherwise returns the input.
	 * 
	 * @param bytes  the bytes to check.
	 * @return bytes without SOSI.
	 */
	private byte[] stripSOSI( byte[] bytes )
	{
		if ( bytes[ 0 ] != SHIFT_OUT || bytes[ bytes.length - 1 ] != SHIFT_IN )
		{
			return bytes;
		}
		else
		{
			byte[] temp = new byte[ bytes.length - 2 ];
			System.arraycopy( bytes, 1, temp, 0, temp.length );
			return temp;
		}
	}
	
	/**
	 * Load a byte from the input stream.  Does no conversion.
	 * 
	 * @return The value
	 */
	public byte loadByte()
	{
		int pos = this.position;

		byte value = this.buffer[ pos++ ];

		this.position = pos;

		return value;
	}

	/**
	 * Load a short from the input stream.  Does conversion according to the 
	 * byte order.
	 * 
	 * @return The value
	 */
	public short loadShort()
	{
		int pos = this.position;

		int value;
		if ( byteOrder != BYTEORDER_LITTLE_ENDIAN )
		{
			value = ((this.buffer[ pos ] & 0xFF) << 8) //
					| (this.buffer[ pos + 1 ] & 0xFF);
		}
		else
		{
			value = ((this.buffer[ pos + 1 ] & 0xFF) << 8) //
					| (this.buffer[ pos ] & 0xFF);
		}

		this.position = pos + 2;

		return (short)value;
	}

	/**
	 * Load a short from the input stream.  Does conversion according to the 
	 * byte order.  The result will be in big-endian format.
	 * 
	 * @param value
	 *            Where to put the 2-byte value
	 * @param start
	 *            The index for the first byte of the value
	 */
	public void loadShort( byte[] value, int start )
	{
		if ( byteOrder != BYTEORDER_LITTLE_ENDIAN )
		{
			loadBytes( value, start, 2 );
		}
		else
		{
			short shortValue = loadShort();
			value[ start ] = (byte)(shortValue >> 8);
			value[ start + 1 ] = (byte)shortValue;
		}
	}

	/**
	 * Load an int from the input stream.  Does conversion according to the 
	 * byte order.
	 * 
	 * @return The value
	 */
	public int loadInt()
	{
		int pos = this.position;

		int value;
		if ( byteOrder != BYTEORDER_LITTLE_ENDIAN )
		{			
			value = ((this.buffer[ pos ] & 0xFF) << 24) //
				| ((this.buffer[ pos + 1 ] & 0xFF) << 16) //
				| ((this.buffer[ pos + 2 ] & 0xFF) << 8) //
				| (this.buffer[ pos + 3 ] & 0xFF);
		}
		else
		{
			value = ((this.buffer[ pos + 3 ] & 0xFF) << 24) //
				| ((this.buffer[ pos + 2 ] & 0xFF) << 16) //
				| ((this.buffer[ pos + 1 ] & 0xFF) << 8) //
				| (this.buffer[ pos ] & 0xFF);
		}

		this.position = pos + 4;

		return value;
	}

	/**
	 * Load an int from the input stream.  Does conversion according to the 
	 * byte order.  The result will be in big-endian format.
	 * 
	 * @param value
	 *            Where to put the 4-byte value
	 * @param start
	 *            The index for the first byte of the value
	 */
	public void loadInt( byte[] value, int start )
	{
		if ( byteOrder != BYTEORDER_LITTLE_ENDIAN )
		{
			loadBytes( value, start, 4 );
		}
		else
		{
			int intValue = loadInt();
			value[ start ] = (byte)(intValue >> 24);
			value[ start + 1 ] = (byte)(intValue >> 16);
			value[ start + 2 ] = (byte)(intValue >> 8);
			value[ start + 3 ] = (byte)intValue;
		}
	}

	/**
	 * Load a long from the input stream.  Does conversion according to the 
	 * byte order.
	 * 
	 * @return The value
	 */
	public long loadLong()
	{
		int pos = this.position;

		long value;
		switch ( byteOrder )
		{
			case BYTEORDER_BIG_ENDIAN:
				value = ((this.buffer[ pos ] & 0xFFL) << 56) //
					| ((this.buffer[ pos + 1 ] & 0xFFL) << 48) //
					| ((this.buffer[ pos + 2 ] & 0xFFL) << 40) //
					| ((this.buffer[ pos + 3 ] & 0xFFL) << 32) //
					| ((this.buffer[ pos + 4 ] & 0xFFL) << 24) //
					| ((this.buffer[ pos + 5 ] & 0xFFL) << 16) //
					| ((this.buffer[ pos + 6 ] & 0xFFL) << 8) //
					| (this.buffer[ pos + 7 ] & 0xFFL);
				break;
				
			case BYTEORDER_LITTLE_ENDIAN:
				value = ((this.buffer[ pos + 7 ] & 0xFFL) << 56) //
					| ((this.buffer[ pos + 6 ] & 0xFFL) << 48) //
					| ((this.buffer[ pos + 5 ] & 0xFFL) << 40) //
					| ((this.buffer[ pos + 4 ] & 0xFFL) << 32) //
					| ((this.buffer[ pos + 3 ] & 0xFFL) << 24) //
					| ((this.buffer[ pos + 2 ] & 0xFFL) << 16) //
					| ((this.buffer[ pos + 1 ] & 0xFFL) << 8) //
					| (this.buffer[ pos ] & 0xFFL);
				break;
				
			default: // BYTEORDER_UNIX
				// Same as BIG_ENDIAN but swap the first/last four bytes.
				value = ((this.buffer[ pos ] & 0xFFL) << 24) //
					| ((this.buffer[ pos + 1 ] & 0xFFL) << 16) //
					| ((this.buffer[ pos + 2 ] & 0xFFL) << 8) //
					| (this.buffer[ pos + 3 ] & 0xFFL) //
					| ((this.buffer[ pos + 4 ] & 0xFFL) << 56) //
					| ((this.buffer[ pos + 5 ] & 0xFFL) << 48) //
					| ((this.buffer[ pos + 6 ] & 0xFFL) << 40) //
					| ((this.buffer[ pos + 7 ] & 0xFFL) << 32);
				break;
		}

		this.position = pos + 8;

		return value;
	}

	/**
	 * Load a long from the input stream.  Does conversion according to the 
	 * byte order.  The result will be in big-endian format.
	 * 
	 * @param value
	 *            Where to put the 8-byte value
	 * @param start
	 *            The index for the first byte of the value
	 */
	public void loadLong( byte[] value, int start )
	{
		if ( byteOrder == BYTEORDER_BIG_ENDIAN )
		{
			loadBytes( value, start, 8 );
		}
		else
		{
			long longValue = loadLong();
			
			value[ start ] = (byte)(longValue >> 56);
			value[ start + 1 ] = (byte)(longValue >> 48);
			value[ start + 2 ] = (byte)(longValue >> 40);
			value[ start + 3 ] = (byte)(longValue >> 32);
			value[ start + 4 ] = (byte)(longValue >> 24);
			value[ start + 5 ] = (byte)(longValue >> 16);
			value[ start + 6 ] = (byte)(longValue >> 8);
			value[ start + 7 ] = (byte)longValue;
		}
	}

	/**
	 * Load bytes from the input stream.  Does no conversion.
	 * 
	 * @param value
	 *            The buffer to receive the data
	 * @param valstart
	 *            The starting offset in the output buffer
	 * @param length
	 *            How many bytes to read
	 * @return The new position
	 */
	public int loadBytes( byte[] value, int valstart, int length )
	{
		System.arraycopy( this.buffer, this.position, value, valstart, length );

		this.position += length;

		return this.position;
	}

	/**
	 * Load bytes from the output stream.  Does no conversion.
	 * 
	 * @param value
	 *            The value
	 * @return The new position
	 */
	public int loadBytes( byte[] value )
	{
		return loadBytes( value, 0, value.length );
	}

	/**
	 * Load bytes from the output stream as a String.  Does conversion according
	 * to the encoding and isUnicode flag.
	 * 
	 * @param byteLength
	 *            How many bytes to turn into a String, if the value is not stored
	 *            in Unicode.  When the value is in Unicode, the number of bytes 
	 *            read equals byteLength * unicodeExpansion.
	 * @param unicodeExpansion
	 *            The ratio of the size of a unicode character to the size of a
	 *            non-unicode character.  For char items the value should be 2
	 *            because a unicode character is two bytes and a non-unicode
	 *            character in a char item is one byte.
	 * @return The String.
	 */
	public String loadString( int byteLength, int unicodeExpansion )
	{
		return loadString( byteLength, unicodeExpansion, false );
	}

	/**
	 * Load a dbchar's bytes from the output stream as a String.  Does conversion
	 * according to the encoding and isUnicode flag.
	 * 
	 * @param byteLength
	 *            How many bytes to turn into a String, if the value is not stored
	 *            in Unicode.  When the value is in Unicode, the number of bytes 
	 *            read equals byteLength * unicodeExpansion.
	 * @return The String.
	 */
	public String loadDbchar( int byteLength )
	{
		return loadString( byteLength, 1, true );
	}
	
	/**
	 * Load bytes from the output stream as a String.  Does conversion according
	 * to the encoding and isUnicode flag.
	 * 
	 * @param byteLength
	 *            How many bytes to turn into a String, if the value is not stored
	 *            in Unicode.  When the value is in Unicode, the number of bytes 
	 *            read equals byteLength * unicodeExpansion.
	 * @param unicodeExpansion
	 *            The ratio of the size of a unicode character to the size of a
	 *            non-unicode character.  For char items the value should be 2
	 *            because a unicode character is two bytes and a non-unicode
	 *            character in a char item is one byte.
	 * @param addSOSI
	 *            True if the bytes don't include shift-in and shift-out chars
	 *            necessary on EBCDIC systems.
	 * @return The String.
	 */
	private String loadString( int byteLength, int unicodeExpansion, boolean addSOSI )
	{
		// If the data is in Unicode, adjust byteLength.
		if ( isUnicode && (encoding == null || isBidi) )
		{
			byteLength *= unicodeExpansion;
		}
		
		// Read the data.
		byte[] bytes = new byte[ byteLength ];
		loadBytes( bytes, 0, byteLength );

		// Convert the data.
		if ( encoding != null || !isUnicode )
		{
			return bytesToString( bytes, encoding, addSOSI );
		}
		else
		{
			// Convert from Unicode.
			char[] chars = new char[ byteLength / 2 ];
			for ( int i = 0, j = 0; i < chars.length; i++, j += 2 )
			{
				chars[ i ] = (char)(bytes[ j ] << 8);
				chars[ i ] |= (bytes[ j + 1 ] & 0xFF);
			}
			
			return new String( chars );
		}
	}
	
	/**
	 * Converts a byte array to a string, using the specified codepage.
	 * 
	 * @param bytes     the bytes.
	 * @param encoding  the encoding, null means use the local codepage.
	 * @param addSOSI   true if SO/SI bytes need to be added before & after the bytes,
	 *                  on EBCDIC systems.
	 * @return the bytes converted to a String.
	 */
	private String bytesToString( byte[] bytes, String encoding, boolean addSOSI )
	{
		try
		{
			if ( !isAscii && addSOSI && bytes[ 0 ] != SHIFT_OUT 
					&& bytes[ bytes.length - 1 ] != SHIFT_IN )
			{
				// Add SO/SI bytes.
				byte[] temp = new byte[ bytes.length + 2 ];
				System.arraycopy( bytes, 0, temp, 1, bytes.length );
				temp[ 0 ] = SHIFT_OUT;
				temp[ temp.length - 1 ] = SHIFT_IN;
				bytes = temp;
			}
		
			String str = encoding != null ? new String( bytes, encoding ) : new String( bytes );

			// Remove the SO/SI bytes from the result since they're not part of the value.
			if ( !isAscii && addSOSI && str.charAt( 0 ) == SHIFT_OUT 
					&& str.charAt( str.length() - 1 ) == SHIFT_IN )
			{
				str = str.substring( 1, str.length() + 1 );
			}

			return str;
		}
		catch ( UnsupportedEncodingException uex )
		{
			// Won't happen.
			return new String( bytes );
		}
	}
	
	/**
	 * Load a float from the input stream.  Does conversion according to the 
	 * IEEE flag.
	 * 
	 * @return The value
	 */
	public float loadFloat()
	{
		int pos = this.position;

		int bits = 
			((buffer[ pos ] & 0xFF) << 24) //
			| ((buffer[ pos + 1 ] & 0xFF) << 16) //
			| ((buffer[ pos + 2 ] & 0xFF) << 8) //
			| (buffer[ pos + 3 ] & 0xFF);

		this.position = pos + 4;

		float value;
		if ( isIeeeFloat )
		{
			value = Float.intBitsToFloat( bits );
		}
		else
		{
			value = NumericUtil.intS390BitsToFloat( bits );
		}
		
		return value;
	}
	
	/**
	 * Load a double from the input stream.  Does conversion according to the 
	 * IEEE flag.
	 * 
	 * @return The value
	 */
	public double loadDouble()
	{
		int pos = this.position;

		long bits = 
			((buffer[ pos ] & 0xFFL) << 56) //
			| ((buffer[ pos + 1 ] & 0xFFL) << 48) //
			| ((buffer[ pos + 2 ] & 0xFFL) << 40) //
			| ((buffer[ pos + 3 ] & 0xFFL) << 32) //
			| ((buffer[ pos + 4 ] & 0xFFL) << 24) //
			| ((buffer[ pos + 5 ] & 0xFFL) << 16) //
			| ((buffer[ pos + 6 ] & 0xFFL) << 8) //
			| (buffer[ pos + 7 ] & 0xFFL);
		
		this.position = pos + 8;

		double value;
		if ( isIeeeFloat )
		{
			value = Double.longBitsToDouble( bits );
		}
		else
		{
			value = NumericUtil.longS390BitsToDouble( bits );
		}
		
		return value;
	}
	
	/**
	 * Get the number of bytes remaining in the buffer from the current position
	 * 
	 * @return The number of bytes
	 */
	public int getNumBytesRemaining()
	{
		return this.buffer.length - this.position;
	}

	/**
	 * @return the byte order used to store the data (BYTEORDER_xxx).
	 */
	public byte getByteOrder()
	{
		return byteOrder;
	}

	/**
	 * @return the character encoding in effect.  Null means we're using the local
	 * encoding.
	 */
	public String getEncoding()
	{
		return encoding;
	}

	/**
	 * @return true for ASCII, false for EBCDIC.
	 */
	public boolean isAscii()
	{
		return isAscii;
	}

	/**
	 * @return true if the encoding indicates BIDI conversion.  Will return false
	 *    if the encoding is null.
	 */
	public boolean isBidi()
	{
		return isBidi;
	}

	/**
	 * @return true if character data is converted to Unicode.
	 */
	public boolean isUnicode()
	{
		return isUnicode;
	}

	/**
	 * @return true if floating point numbers are in IEEE format.
	 */
	public boolean isIeeeFloat()
	{
		return isIeeeFloat;
	}

	/**
	 * Provides guidance for certain conversions.  When we're converting data to
	 * bytes, returns true if they'll be passed to EGL Java code.  When we're converting
	 * data from bytes, returns true if they were produced by EGL Java code.  
	 */
	public boolean isEglJavaFormat()
	{
		return eglJavaFormat;
	}

	/**
	 * Sets the eglJavaFormat flag.
	 */
	public void setEglJavaFormat( boolean eglJava )
	{
		eglJavaFormat = eglJava;
	}
	
	/**
	 * Returns a String showing the class name, current position, and length.
	 */
	public String toString()
	{
		return "ByteStorage[pos=" + position + " of " + buffer.length + ']';
	}
	
	/**
	 * Returns a clone of this object.
	 */
	public Object clone() throws CloneNotSupportedException
	{
		ByteStorage theClone = (ByteStorage)super.clone();
		buffer = (byte[])buffer.clone();
		return theClone;
	}
}
