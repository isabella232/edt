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

/**
 * This class exists only to define constants.
 * 
 * @author mheitz
 */
public class Constants
{
	/**
	 * Don't instantiate Constants.
	 */
	private Constants()
	{
	}
	
	/**
	 * The serialization (version) number
	 */
	public static final long SERIAL_VERSION_UID = 10L;
	
	/**
	 * The blank character in the local codepage.
	 */
	public static final byte BLANK_BYTE = " ".getBytes()[ 0 ];
	
	/**
	 * The first byte of the DBCS blank character in the local codepage.
	 */
	public static final char DBCS_BLANK_CHAR = '\u3000';
	
	/**
	 * 100 blank characters in the local codepage.
	 */
	public static final byte[] HUNDRED_BLANK_BYTES = 
		{
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE,
			BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE, BLANK_BYTE
		};
	
	/**
	 * 50 Unicode blank characters.
	 */
	public static final byte[] FIFTY_UNICODE_BLANK_BYTES =
		{
			0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20,
			0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20,
			0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20,
			0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20,
			0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20,
			0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20, 0, 0x20,
			0, 0x20, 0, 0x20
		};
	
	/**
	 * A String containing 8 blanks.
	 */
	public static final String STRING_8_BLANKS = "        ";

	/**
	 * A String containing 50 blanks.
	 */
	public static final String STRING_50_BLANKS = "                                                  ";

	/**
	 * A String containing 50 DBCS blanks.
	 */
	public static final String STRING_50_DBCS_BLANKS =
		"\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000"
		+ "\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000"
		+ "\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000"
		+ "\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000"
		+ "\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000";

	/**
	 * A String containing 8 zeros.
	 */
	public static final String STRING_8_ZEROS = "00000000";

	/**
	 * A String containing 50 zeros.
	 */
	public static final String STRING_50_ZEROS = "00000000000000000000000000000000000000000000000000";

	/*
	 * Used to set the default type of XML created from XMLLib. 
	 */
	public static final boolean XML_DOCUMENT = true;
	public static final boolean XML_FRAGMENT = false;
	public static final boolean XML_DEFAULT_TYPE = XML_FRAGMENT;
	
	/**
	 * The following are constants for signatures of built-in types this implementation of the EGL runtime for java supports
	 */
	public static final String SIGNATURE_ANY                 	   = "egl.lang.anyObject";
	public static final String SIGNATURE_ARRAYDICTIONARY           = "egl.lang.dictionary";
	public static final String SIGNATURE_BIGINT                    = "egl.lang.int64";;
	public static final String SIGNATURE_BLOB                      = "egl.lang.blob";;
	public static final String SIGNATURE_BOOLEAN                   = "egl.lang.boolean";
	public static final String SIGNATURE_BYTE                       = "egl.lang.byte";
	public static final String SIGNATURE_CHAR                      = "egl.lang.char";;
	public static final String SIGNATURE_CLOB                      = "egl.lang.clob";;
	public static final String SIGNATURE_DECIMAL                   = "egl.lang.decimal";
	public static final String SIGNATURE_DATE                      = "egl.lang.date";;
	public static final String SIGNATURE_DICTIONARY                = "egl.lang.dictionary";
	public static final String SIGNATURE_FLOAT                     = "egl.lang.float";
	public static final String SIGNATURE_HEX                       = "egl.lang.hex";
	public static final String SIGNATURE_INT                       = "egl.lang.int32";
	public static final String SIGNATURE_MONTHSPANINTERVAL         = "egl.lang.monthspaninterval";
	public static final String SIGNATURE_SECONDSPANINTERVAL        = "egl.lang.secondspaninterval";
	public static final String SIGNATURE_SMALLFLOAT                = "egl.lang.smallfloat";
	public static final String SIGNATURE_SMALLINT                  = "egl.lang.int16";
	public static final String SIGNATURE_STRING                    = "egl.lang.string";
	public static final String SIGNATURE_TIME                      = "egl.lang.time";
	public static final String SIGNATURE_TIMESTAMP                 = "egl.lang.timestamp";
	
	/**
	 * The following are property constants
	 */
	public static final String APPLICATION_PROPERTY_FILE_NAME_KEY  = "egl.application.property.file";
}
