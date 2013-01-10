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
package org.eclipse.edt.javart.resources;

import java.nio.ByteOrder;

import org.eclipse.edt.javart.ByteStorage;

/**
 * This class has information about the machine that the JVM is running on.
 * 
 * @author mheitz
 */
public class Platform
{
	/**
	 * Tells us if this computer uses ASCII or EBCDIC characters.
	 */
	public static final boolean IS_ASCII;
	
	/**
	 * The byte order used by this computer (constants from ByteStorage).
	 */
	public static final byte BYTE_ORDER;
	
	/**
	 * The value for systemType.
	 */
	public static final String SYSTEM_TYPE;
	
	/**
	 * Values for SYSTEM_TYPE.
	 */
	public static final String AIX       = "AIX     ";
	public static final String DEBUG 	 = "DEBUG   ";
	public static final String HP        = "HP      ";
	public static final String HPUX      = "HPUX    ";
	public static final String ISERIESJ  = "ISERIESJ";
	public static final String LINUX	 = "LINUX   ";
	public static final String SOLARIS   = "SOLARIS ";
	public static final String USS       = "USS     ";
	public static final String WIN       = "WIN     ";
	public static final String UNKNOWN   = "        ";	
	public static final String ZLINUX	 = "ZLINUX  ";

	/**
	 * Initialize IS_ASCII, BYTE_ORDER, and SYSTEM_TYPE.
	 */
	static
	{
		// All ASCII codepages have the character '0' at the same place, 0x30.
		// If we make a String from 0x30 and it's equal to "0", set IS_ASCII
		// to true.
		IS_ASCII = new String( new byte[] { 0x30 } ).equals( "0" );
		
		// Get the os.name property in lower case.
		String osName = System.getProperty( "os.name", "" ).toLowerCase();
	
		// Try to find the name of the OS.  Unfortunately there are no
		// standards governing the format of os.name, so we have to make an
		// intelligent guess.  Search for the longer names first, since it
		// may prevent false matches.
		if ( osName.indexOf( "windows" ) != -1 )
		{
			SYSTEM_TYPE = WIN;
			BYTE_ORDER = ByteStorage.BYTEORDER_LITTLE_ENDIAN;
		}
		else if ( osName.indexOf( "solaris" ) != -1 
				|| osName.indexOf( "sunos" ) != -1 )
		{
			SYSTEM_TYPE = SOLARIS;
			BYTE_ORDER = ByteStorage.BYTEORDER_UNIX;
		}
		else if ( osName.indexOf( "os/390" ) != -1 )
		{
			// We also check for "z/os", below.
			SYSTEM_TYPE = USS;
			BYTE_ORDER = ByteStorage.BYTEORDER_BIG_ENDIAN;
		}
		else if ( osName.indexOf( "os/400" ) != -1 )
		{
			SYSTEM_TYPE = ISERIESJ;
			BYTE_ORDER = ByteStorage.BYTEORDER_BIG_ENDIAN;
		}
		else if ( osName.indexOf( "linux" ) != -1 )
		{
			if ( ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN )
			{
				SYSTEM_TYPE = LINUX;
				BYTE_ORDER = ByteStorage.BYTEORDER_LITTLE_ENDIAN;
			}
			else
			{
				SYSTEM_TYPE = ZLINUX;
				BYTE_ORDER = ByteStorage.BYTEORDER_BIG_ENDIAN;
			}
		}
		else if ( osName.indexOf( "hp-ux" ) != -1 )
		{
			SYSTEM_TYPE = HPUX; 
			BYTE_ORDER = ByteStorage.BYTEORDER_UNIX;
		}
		else if ( osName.indexOf( "z/os" ) != -1 )
		{
			// We also check for "os/390", above.
			SYSTEM_TYPE = USS;
			BYTE_ORDER = ByteStorage.BYTEORDER_BIG_ENDIAN;
		}
		else if ( osName.indexOf( "aix" ) != -1 )
		{
			SYSTEM_TYPE = AIX;
			BYTE_ORDER = ByteStorage.BYTEORDER_UNIX;
		}
		else
		{
			SYSTEM_TYPE = UNKNOWN;
			if ( ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN )
			{
				BYTE_ORDER = ByteStorage.BYTEORDER_LITTLE_ENDIAN;
			}
			else
			{
				BYTE_ORDER = ByteStorage.BYTEORDER_BIG_ENDIAN;
			}
		}
	}
}
