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
package eglx.http;

import java.io.UnsupportedEncodingException;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

public class Base64 extends AbstractPreferences
{
	private static Base64 instance = new Base64();
	String currentValue;
	private Base64()
	{
		super( null, "" );
	}

	protected AbstractPreferences childSpi( String name )
	{
		return null;
	}

	protected String[] childrenNamesSpi() throws BackingStoreException
	{
		return null;
	}

	protected void flushSpi() throws BackingStoreException
	{
	}

	protected String getSpi( String key )
	{
		return null;
	}

	protected String[] keysSpi() throws BackingStoreException
	{
		return null;
	}

	protected void putSpi( String key, String value )
	{
	}

	protected void removeNodeSpi() throws BackingStoreException
	{
	}

	protected void removeSpi( String key )
	{
	}

	protected void syncSpi() throws BackingStoreException
	{
	}

	private String decodeBase64( String encodedValue )
	{
		try
		{
			currentValue = encodedValue;
			return new String( getByteArray( currentValue, new byte[0] ), "UTF-8" );
		}
		catch( UnsupportedEncodingException usee)
		{
			return encodedValue;
		}
	}

	private byte[] decodeBase64ToByteArray( String encodedValue )
	{
		currentValue = encodedValue;
		return getByteArray( currentValue, new byte[0] );
	}

	private String encodeBase64( String value )
	{
		try
		{
			putByteArray( value, value.getBytes( "UTF-8" ) );
			return get( "", "" );
		}
		catch( UnsupportedEncodingException usee)
		{
			return value;
		}
	}

	private String encodeBase64( byte[] value )
	{
		putByteArray("", value);
		return get( "", "" );
	}

	public String get( String key, String def )
	{
		return currentValue;
	}
	
	public void put( String key, String value )
	{
		this.currentValue = value;
	}
	
	public static byte[] decodeToByteArray( String encodedValue )
	{
		synchronized ( instance.lock )
		{
			return instance.decodeBase64ToByteArray(encodedValue);
		}
	}
	public static String decode( String encodeValue )
	{
		synchronized ( instance.lock )
		{
			return instance.decodeBase64( encodeValue );
		}
	}
	public static String encode( String value )
	{
		synchronized ( instance.lock )
		{
			return instance.encodeBase64( value );
		}
	}
	public static String encode( byte[] value )
	{
		synchronized ( instance.lock )
		{
			return instance.encodeBase64( value );
		}
	}

	public static void main(java.lang.String[] args )
	{
		String[] values = { "Joe", "Vincens", "vINCENS", "J\ny\rh", "1234567890", "abcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", ",./<>?;':\"[]{}`~-_=+|!@#$%^&*()", "\r\n\t" };
		String[] encodedValues = new String[values.length];
		String[] decodedValues = new String[values.length];
		
		for( int idx = 0; idx < values.length; idx++ )
		{
			encodedValues[idx] = Base64.encode( values[idx] );
			decodedValues[idx] = Base64.decode( encodedValues[idx] );
			System.out.println("startValue" + values[idx] + "encoded:'" + encodedValues[idx] + "' decoded:'" + decodedValues[idx] + "' " + (values[idx].equals( decodedValues[idx] ) ? "equal" : "NOT EQUAL") );
		}
		
//		String[] encodedValues = { "Sm9l", "dklOQ0VOUw==" };
	}
}
