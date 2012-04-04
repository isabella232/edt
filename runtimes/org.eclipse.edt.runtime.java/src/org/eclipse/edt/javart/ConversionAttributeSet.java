/*******************************************************************************
 * Copyright © 2006, 2012 IBM Corporation and others.
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.eclipse.edt.javart.resources.Platform;

/**
 * This is a set of values that can be used to control the data conversion done
 * by a ByteStorage.
 * 
 * @author mheitz
 */
public class ConversionAttributeSet implements Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/** 
	 * The byte order used to store the data (ByteStorage.BYTEORDER_xxx).
	 */
	public byte byteOrder;

	/** 
	 * The character encoding in effect.  Null means use the local encoding.
	 */
	public String encoding;

	/** 
	 * True if the encoding indicates BIDI conversion.
	 */
	public transient boolean isBidi;

	/**
	 * True for ASCII, false for EBCDIC.
	 */
	public boolean isAscii;

	/** 
	 * True if character data is converted to Unicode.
	 */
	public boolean isUnicode;

	/** 
	 * True if floating point numbers are in IEEE format.
	 */
	public boolean isIeeeFloat;
	
	/**
	 * True if data is, or should be, in EGL Java format.
	 */
	public boolean eglJavaFormat;
	
	/**
	 * Makes a set of default attributes.
	 */
	public ConversionAttributeSet()
	{
		byteOrder = ByteStorage.BYTEORDER_BIG_ENDIAN;
		encoding = null;
		isAscii = Platform.IS_ASCII;
		isBidi = false;
		isUnicode = false;
		isIeeeFloat = true;
		eglJavaFormat = true;
	}

	/**
	 * Call this to modify all the attributes at once.
	 */
	public void setConversion( byte byteOrder, String encoding, boolean isAscii, 
			boolean isBidi, boolean isUnicode, boolean isIeeeFloat, boolean eglJavaFormat )
	{
		this.byteOrder = byteOrder;
		this.encoding = encoding;
		this.isAscii = isAscii;
		this.isUnicode = isUnicode;
		this.isIeeeFloat = isIeeeFloat;
		this.eglJavaFormat = eglJavaFormat;
		
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
	 * Applies the conversion attributes to the ByteStorage.
	 * 
	 * @param storage  the ByteStorage.
	 */
	public void apply( ByteStorage storage )
	{
		storage.setConversion( byteOrder, encoding, isAscii, isBidi, isUnicode, isIeeeFloat );
		storage.setEglJavaFormat( eglJavaFormat );
	}
	
	/**
	 * Serializes an instance of this class.
	 * 
	 * @param out  The output stream.
	 * @throws IOException
	 */
	private void writeObject( ObjectOutputStream out )
			throws IOException
	{
		out.defaultWriteObject();
		
		// Store isBidi when encoding isn't null; otherwise we know it's false.
		if ( this.encoding != null )
		{
			out.writeBoolean( this.isBidi );
		}
	}
	
	/**
	 * Deserializes an instance of this class.
	 * 
	 * @param in  The input stream.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject( ObjectInputStream in )
			throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		
		// Read in isBidi if the encoding's not null.
		if ( this.encoding != null )
		{
			this.isBidi = in.readBoolean();
		}
	}
}
