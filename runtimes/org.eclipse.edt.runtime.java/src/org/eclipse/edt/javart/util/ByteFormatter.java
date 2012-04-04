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
package org.eclipse.edt.javart.util;

import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.ConversionAttributeSet;

import eglx.lang.AnyException;

import org.eclipse.edt.javart.resources.Trace;

/**
 * This class formats bytes into an easily readable format.
 * In addition it converts the byte data to the format specified in the ConversionAttributeSet
 * This is used as a debugging tool when passing bytes to and from a host
 * 
 * @author jvincens
 */
public class ByteFormatter
{
	private ConversionAttributeSet attrs;
	private static final int MAX_DISPLAY_LEN = 1024;
	private static final int BYTE_LINE_LEN = 37;
	private static final int CHAR_LINE_LEN = 17;
	private static ByteStorage bs;
	
	private ByteFormatter( ConversionAttributeSet attrs )
	{
		this.attrs = attrs;
		bs = new ByteStorage( 1 );
	}
	
	private void print( byte[] bytes, StringBuilder line, int max )
	{
		int len = bytes == null ? 0 : bytes.length;
		if( len > max )
		{
			len = max;
		}
		evalBytes( bytes, line, len );
	}
	
	private void evalBytes( byte[] bytes, StringBuilder line, int limit )
	{
		StringBuilder ebcdicBuf = new StringBuilder();
		StringBuilder asciiBuf = new StringBuilder();
		StringBuilder byteBuf = new StringBuilder();
		int i;
		for( i = 0; i < bytes.length && i < limit; i++ )
		{
			//16 is the start of a new line
			if( i % 16 == 0 )
			{
				line.append( getInt( i ) + " " );
				ebcdicBuf = new StringBuilder( "'" );
				asciiBuf = new StringBuilder( "'" );
				byteBuf = new StringBuilder();
			}
			byteBuf.append( getByte( bytes[ i ] ) );
			appendChar( bytes[ i ], asciiBuf, attrs );
			//is the next value on a new line append the asii and ebcdic diplay values
			if( (i + 1) % 16 == 0 )
			{
				endLine( line, ebcdicBuf, asciiBuf, byteBuf );
			}
			else if( (i + 1) % 4 == 0 )
			{
				byteBuf.append( " " );
			}
		}
		if( byteBuf.length() < BYTE_LINE_LEN )
		{
			endLine( line, ebcdicBuf, asciiBuf, byteBuf );
		}
	}


	private void endLine( StringBuilder line, StringBuilder ebcdicBuf, StringBuilder asciiBuf, StringBuilder byteBuf )
	{
		if( byteBuf.length() < BYTE_LINE_LEN )
		{
			while( byteBuf.length() < BYTE_LINE_LEN )
			{
				byteBuf.append( ' ' );
			}
			while( asciiBuf.length() < CHAR_LINE_LEN )
			{
				asciiBuf.append( ' ' );
			}
			while( ebcdicBuf.length() < CHAR_LINE_LEN )
			{
				ebcdicBuf.append( ' ' );
			}
		}
		line.append( byteBuf );
		if( attrs != null )
		{
			line.append( asciiBuf );
			line.append( "'  " );
		}
		line.append( '\n' );
	}

	private static String getInt( int i )
	{
		String retVal = "00000000" + Integer.toHexString( i ).toUpperCase();
		return retVal.substring( retVal.length() - 8  );
	}
	
	private static String getByte( byte b )
	{
		String retVal = "00" + Integer.toHexString( b ).toUpperCase();
		return retVal.substring( retVal.length() - 2  );
	}

	private static void appendChar( byte val, StringBuilder buf, ConversionAttributeSet attrs )
	{
		if( attrs != null )
		{
			char ch = getChar( val, attrs );
			if( Character.isISOControl( ch ) )
			{
				ch = '.';
			}
			buf.append( ch );
		}
	}

	private static char getChar( byte val, ConversionAttributeSet attrs )
	{
		char ch = ' ';
		try
		{
			attrs.apply( bs );
			byte[] bytes = new byte[1];
			bytes[0] = val;
			bs.reset( bytes );
			String strVal = bs.loadString( 1, 1 );
			if( strVal != null && strVal.length() > 0 )
			{
				ch = strVal.charAt( 0 );
			}
		}
		catch( Exception e )
		{
		}
		return ch;
	}

	public static void printBytes( byte[] bytes, String header, ConversionAttributeSet attrs ) throws AnyException
	{
		if( bytes != null )
		{
			ByteFormatter bf = new ByteFormatter( attrs );
			StringBuilder buf = new StringBuilder();
			bf.print( bytes, buf, MAX_DISPLAY_LEN );
			System.out.println(  header );
			System.out.println(  buf.toString() );
		}
	}

	public static void traceBytes( byte[] bytes, int dumpSize, String header, ConversionAttributeSet attrs, Trace tracer ) throws AnyException
	{
		if( bytes != null )
		{
			ByteFormatter bf = new ByteFormatter( attrs );
			StringBuilder buf = new StringBuilder();
			bf.print( bytes, buf, dumpSize );
			buf.insert( 0, "\n" );
			buf.insert( 0, header );
			tracer.put( buf.toString() );
		}
	}
	
	public static void traceBytes( byte[] bytes, String header, ConversionAttributeSet attrs, Trace tracer ) throws AnyException
	{
		traceBytes( bytes, MAX_DISPLAY_LEN, header, attrs, tracer );
	}
	
	public static void traceBytes( byte[] bytes, Trace tracer ) throws AnyException
	{
		if( bytes != null )
		{
			ByteFormatter bf = new ByteFormatter( null );
			StringBuilder buf = new StringBuilder();
			bf.print( bytes, buf, MAX_DISPLAY_LEN );
			buf.insert( 0, "\n" );
			tracer.put( buf.toString() );
		}
	}
	
	
	
}
