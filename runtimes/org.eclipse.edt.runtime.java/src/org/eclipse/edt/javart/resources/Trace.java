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
package org.eclipse.edt.javart.resources;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.util.JavartUtil;

/**
 * Trace is used to trace the behavior of a generated program. The output can go
 * to a file, System.err or System.out. The <code>put</code> method causes a
 * message to be traced.
 * <P>
 * Classes can use the <code>traceIsOn</code> methods to determine when they
 * should add something to the trace. Both versions of <code>traceIsOn</code>
 * examine the trace level, which is a bit field. Each bit represents a category
 * of operations, and if a bit is set then any operation in that category should
 * be traced. A trace level of zero means no tracing will be done.
 */
public class Trace implements Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	/**
	 * The underlying object that tracing is written to.
	 */
	private transient PrintWriter out;
   
	/**
	 * The operations that will be traced; possible values are:
	 *   NO_TRACE             no trace is enabled
	 *   GENERAL_TRACE        program setup attributes, function, CALL trace
	 *   MATH_TRACE           trace the Math functions
	 *   STRING_TRACE         trace the String functions
	 *   TABLE_TRACE          trace operations on tables
	 *   CALL_PARM_TRACE      trace parameters on CALL
	 *   CALL_OPTIONS_TRACE   trace settings on CALL
	 *   UI_RECORD_TRACE      trace operations on UI records
	 *   JDBC_TRACE           trace JDBC operations
	 *   FILE_TRACE           trace file I/O and driver calls
	 *   PROPERTIES_TRACE     trace the properties used by the program
	 */
	private int traceLevel;
	
	/**
	 * True if we're writing the trace to a file.
	 */
	private boolean usingFile;
	
	/**
	 * The name of the trace device.
	 */
	private String name;

	public static final int NO_TRACE               = 0;
	public static final int GENERAL_TRACE          = 1;
	public static final int MATH_TRACE             = 2;
	public static final int STRING_TRACE           = 4;
	public static final int TABLE_TRACE            = 8;
	public static final int CALL_PARM_TRACE        = 16;
	public static final int CALL_OPTIONS_TRACE     = 32;
	public static final int UI_RECORD_TRACE        = 64;
	public static final int JDBC_TRACE             = 128;
	public static final int FILE_TRACE             = 256;
	public static final int PROPERTIES_TRACE       = 512;

	/**
	 * The default name of the trace file.
	 */
	public static final String DEFAULT_FILE_NAME = "egltrace.out";
	
	/**
	 * Constructs the trace object. <code>devType</code> should be
	 * "0" to use System.out, "1" to use System.err, or "2" to use
	 * a file. 
	 * 
	 * <P> <code>file</code> names the file to use when <code>devType</code>
	 * is "2".  It defaults to <code>DEFAULT_FILE_NAME</code>.
	 * 
	 * @param trcLevel
	 *            the initial level of tracing (0, 1, 2 ...).
	 * @param devType
	 *            the device type (0, 1, or 2).
	 * @param file
	 *            the file to trace to.
	 */
	public Trace( String trcLevel, String devType, String file )
	{
		traceLevel = convertStringToIntValue( trcLevel, NO_TRACE );
		if ( traceLevel == NO_TRACE )
		{
			name = "not tracing";
		}
		else
		{
			if ( devType.equals( "0" ) )
			{
				out = new PrintWriter( System.out );
				name = "System.out";
			}
			else if ( devType.equals( "2" ) )
			{
				if ( file == null || file.length() == 0 )
				{
					file = DEFAULT_FILE_NAME;
				}
				
				try
				{
					out = 
						new PrintWriter( 
							new BufferedOutputStream(
								new FileOutputStream( file, true ) ),
								true );
					name = file;
					usingFile = true;
				}
				catch ( Exception ex )
				{
					out = new PrintWriter( System.err );
					name = "System.err";
					put( " Exception on trace file: <" + file + ">" );
					ex.printStackTrace( out );
				}
			}
			else
			{
				out = new PrintWriter( System.err );
				name = "System.err";
			}
		}
	}

	/**
	 * Returns false if the trace level is set to NO_TRACE, true otherwise.
	 * 
	 * @return false if the trace level is set to NO_TRACE, true otherwise.
	 */
	public boolean traceIsOn()
	{
		return (traceLevel != NO_TRACE);
	}

	/**
	 * Returns true if the current trace level includes <code>level</code>,
	 * false otherwise.
	 * 
	 * @param level
	 *            the trace level to inquire about.
	 * @return true if the current trace level includes <code>level</code>,
	 *         false otherwise.
	 */
	public boolean traceIsOn( int level )
	{
		return ((traceLevel & level) != 0);
	}

	
	/**
	 * Allows for programmatically changing the current trace level value.
	 * CUI is using this to turn off tracing so that when we assign
	 * invalid values to EGL variables bad error messages aren't displayed
	 * in the trace log.  CUI turns tracing back on after the assignment.
	 * @param level
	 * @return int the old/previous trace level
	 */
	public int setTraceLevel(int level)
	{
		int oldlevel = this.traceLevel;
		this.traceLevel = level;
		return oldlevel;
	}
	
	
	/**
	 * Closes the trace object.
	 */
	public void close()
	{
		if ( usingFile && out != null )
		{
			out.close();
			out = null;
		}
	}

	/**
	 * If we're tracing to a file, and the trace object is closed, it will be
	 * reopened.
	 */
	private void checkReopen()
	{
		if ( usingFile && out == null )
		{
			try
			{
				out = 
					new PrintWriter( 
						new BufferedOutputStream(
							new FileOutputStream( name, true ) ),
							true );
			}
			catch ( Exception ex )
			{
				usingFile = false;
				out = new PrintWriter( System.err );
				put( " Exception on trace file: <" + name + ">" );
				name = "System.err";
				ex.printStackTrace( out );
			}
		}
	}

	/**
	 * Returns the integer representation of <code>s</code>, or
	 * <code>defVal</code> if any conversion error is detected.
	 * 
	 * @param s
	 *            the String to be converted.
	 * @param defVal
	 *            the default integer value to return if there's an error.
	 * @return the integer representation of <code>s</code>.
	 */
	private int convertStringToIntValue( String s, int defVal )
	{
		if ( s == null )
		{
			return defVal;
		}
		
		try
		{
			return Integer.parseInt( s );
		}
		catch ( NumberFormatException e )
		{
			return defVal;
		}
	}

	/**
	 * Returns the trace name and traceLevel.
	 * 
	 * @return information about the state of this object.
	 */
	public String getInfo()
	{
		return "Trace to: " + name + ", level: " + traceLevel;
	}

	/**
	 * Writes text to the trace object.
	 * 
	 * @param text
	 *            the output string.
	 */
	public void put( String text )
	{
		checkReopen();
		
		out.println( '(' + JavartUtil.getCurrentTime() + "> [" + Thread.currentThread().getName() + "]" + text );
		out.flush();
	}

	/**
	 * Writes the data of a byte array to the trace output device. The output is
	 * written using <code>put<code>.
	 * <P>
	 * Every 16 bytes, write a new line of output to the trace device.
	 * Lines look like this:
	 * <PRE>
	 *  a0 | 00000020 48454c4c 4f202020 20202020 |     HELLO
	 * </PRE>
	 * The first part is the address of the first byte.  The middle part shows
	 * each byte, and the last part shows the character representation of the
	 * bytes.
	 *
	 * @param bytes   the byte array to be written.
	 */
	public void putBytes( byte[] bytes )
	{
		putBytes( bytes, bytes.length );
	}

	/**
	 * Writes the data of a byte array to the trace output
	 * device.  The output is written using <code>put<code>.
	 * <P>
	 * Every 16 bytes, write a new line of output to the trace device.
	 * Lines look like this:
	 * <PRE>
	 *  a0 | 00000020 48454c4c 4f202020 20202020 |     HELLO
	 * </PRE>
	 * The first part is the address of the first byte.  The middle part shows
	 * each byte, and the last part shows the character representation of the
	 * bytes.
	 *
	 * @param bytes   the byte array to be written.
	 * @param length  the number of bytes to write.
	 */
	public void putBytes( byte[] bytes, int length )
	{
		// **** CAUTION: This code is ugly! You might vomit! **** //

		// Write the hex value of each byte into a StringBuilder.
		StringBuilder byteStrBuf = new StringBuilder( " " );
		String charStr;

		String spaces;
		int spacer = 0;

		int label = 0;
		int chars = 0;

		int i = 0;
		for ( ; i < length; i++ )
		{			
			if ( chars == 16 )
			{
				// Time to output this line.
				charStr = new String( bytes, i - 16, 16 );
				if ( label < 0x10 )
				{
					spacer = 0;
				}
				else if ( label < 0x100 )
				{
					spacer = 1;
				}
				else if ( label < 0x1000 )
				{
					spacer = 2;
				}
				else
				{
					spacer = 3;
				}
				spaces = "   ".substring( spacer );
				put( spaces + Integer.toHexString( label ) + " |" + byteStrBuf + " | " + charStr );
				byteStrBuf = new StringBuilder();
				chars = 0;
				label += 16;
			}
			if ( i % 4 == 0 && i > 0 )
			{
				byteStrBuf.append( ' ' );
			}
			if ( (bytes[ i ] & 0xff) < 0x10 )
			{
				byteStrBuf.append( '0' );
			}
			chars++;
			byteStrBuf.append( Integer.toHexString( bytes[ i ] & 0xff ) );
		}
		if ( chars > 0 )
		{
			// Write out the last line.
			if ( chars < 16 )
			{
				// It had less than 16 bytes in it.  Add blanks.
				String blanks = "                                   ";
				int bytesMissing; 
				int blocksMissing;
				if ( chars % 4 == 0 )
				{
					bytesMissing = 0;
				}
				else
				{
					bytesMissing = 4 - (chars % 4);
				}
				blocksMissing = 3 - ((chars - 1) / 4);
				int blanksNeeded = bytesMissing * 2 + blocksMissing * 9;
				byteStrBuf.append( blanks.substring( 0, blanksNeeded ) );
			}
			charStr = new String( bytes, i - chars, chars );
			if ( label < 0x10 )
			{
				spacer = 0;
			}
			else if ( label < 0x100 )
			{
				spacer = 1;
			}
			else if ( label < 0x1000 )
			{
				spacer = 2;
			}
			else
			{
				spacer = 3;
			}
			spaces = "   ".substring( spacer );
			put( spaces + Integer.toHexString( label ) + " |" + byteStrBuf + " | " + charStr );
		}
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
		close();
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
		
		if ( traceIsOn() )
		{
			if ( usingFile )
			{
				checkReopen();
			}
			else if ( "System.out".equals( name ) )
			{
				out = new PrintWriter( System.out );
			}
			else
			{
				out = new PrintWriter( System.err );
			}
		}
	}
	public boolean isValidDeviceType()
	{
		return out != null;
	}
}
