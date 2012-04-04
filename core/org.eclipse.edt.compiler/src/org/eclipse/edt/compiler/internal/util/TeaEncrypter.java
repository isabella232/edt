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
package org.eclipse.edt.compiler.internal.util;

import java.math.BigInteger;
import java.util.ArrayList;


/**
 * Class for encrypting/decrypting strings using the
 * Tiny Encryption Algorithm.
 */
public class TeaEncrypter
{
	private Tea tea;
	private byte encKey[] =
		new BigInteger( "2342abc2342ddee987efefd3a4bb192", 16 ).toByteArray(); //$NON-NLS-1$
	
	public TeaEncrypter()
	{
		tea = new Tea( encKey );
	}
	
	/**
	 * Encrypts the given text
	 * 
	 * @param text  The text to encrypt
	 * @return the encrypted value
	 */
	public String encrypt( String text )
	{
		// get bytes in platform independent way
		char[] chars = text.toCharArray();
		byte[] src = new byte[chars.length << 1]; 
		for ( int i = 0; i < chars.length; i++ )
		{
			src[i] = (byte)(chars[i] & 0xff);
			src[i+1] = (byte)(chars[i] >> 8 & 0xff);
		}
		
		int[] enc = tea.encode( src, src.length );
		
		// convert int[] to hex string
		String ret = ""; //$NON-NLS-1$
		String next;
		for ( int j = 0; j < enc.length; j++ )
		{
			next = Integer.toHexString( enc[j] );
			if ( next.length() % 2 != 0 )
			{
				next = "0" + next; //$NON-NLS-1$
			}
			ret += next;
		}
		
		return ret;
	}
	
	/**
	 * Decrypts the given text
	 * 
	 * @param enc  The value to decrypt
	 * @return the decrypted value
	 */
	public String decrypt( String text )
	{
		// convert the hex string to int[] parsing 8 chars at a time
		ArrayList ints = new ArrayList();
		int len = text.length();
		
		int end;
		String next;
		for ( int i = 0; i < len; )
		{
			end = i + 8;
			if ( end >= len )
			{
				end = len;
			}
				
			next = text.substring( i, end );
			ints.add( Long.decode( "0x" + next ) ); //$NON-NLS-1$
			i = end;
		}
		
		int[] enc = new int[ints.size()];
		for ( int i = 0; i < enc.length; i++ )
		{
			enc[i] = ((Long)(ints.get( i ))).intValue();
		}
	
		return new String( tea.decode( enc ) ).replaceAll( "\0", "" ); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
