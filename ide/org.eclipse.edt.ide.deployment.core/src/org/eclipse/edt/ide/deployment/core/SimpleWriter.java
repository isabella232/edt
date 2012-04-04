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
package org.eclipse.edt.ide.deployment.core;
import java.io.*;


/**
* This class writes indented text.  It has two modes of operation.  The default
* mode is called auto-indent mode.  In auto-indent mode, writing a { character
* causes the next line to be indented one more tab than the current line.
* Writing a } character causes the current line to be indented one less
* tab than the previous line.  Whitespace at the front of each line is
* removed in auto-indent mode.
*
* <P> If auto-indent mode is turned off, you can control tabbing manually.
* Calling pushIndent and popIndent change the amount that a line is indented.
* They work even in auto-indent mode.
*
* <P> In either mode, use the print and println methods to write output.
* 
* <P> This class is based on Dave Charboneau's TemplateWriter class from the
* DeclarativeTemplateCompiler.
*/

public class SimpleWriter 
{
	/**
	 * The underlying output stream.
	 */
	private Writer      writer;

	/**
	 * The current line number.
	 */
	private int         lineNumber;
	
	/**
	 * An array of one newline character.
	 */
	private static final char[] NEWLINE = new char[] { '\n' };
	
	/**
	 * Creates a TabbedWriter which writes its output to a FileWriter
	 * that is wrapped by a BufferedWriter.
	 *
	 * <P> Auto-indenting is on by default, so { and } trigger changes
	 * to the indent level, and whitespace at the front of each line 
	 * is removed.
	 *
	 * @param fileName  the name of the file to write to.
	 * @see #setAutoIndent(boolean)
	 */
	public SimpleWriter( String fileName ) throws Exception
	{
		try {
			this.writer = new BufferedWriter( new FileWriter( fileName ) );
		} 
		catch (IOException e) {
			throw new Exception("Exception thrown in SimpleWriter: " + e.getMessage());
		}

		lineNumber = 1;
	}
	
	public SimpleWriter( Writer writer ) 
	{
		this.writer = writer;
	}
	
	/**
	 * Closes the stream.
	 */
	public void close() throws Exception
	{
		writer.close();
	}

	
	/**
	 * Flushes the stream.  If the last character written was not a newline,
	 * the last partial line of text will not be flushed.  This is because the 
	 * text is indented accoring to what's in the current line.  When you flush
	 * in the middle of a line the TabbedWriter doesn't know how much to indent it.
	 */
	public void flush() throws Exception
	{
		writer.flush();
	}
	
	
	/**
	 * Returns the current line number.  The first line of the file
	 * is line number 1.
	 *
	 * @return the current line number.
	 */
	public int getLineNumber() 
	{
		return lineNumber;
	}
	
	/**
	 * Returns the underlying Writer.
	 *
	 * @return the Writer.
	 */
	public Writer getWriter()
	{
		return writer;
	}
	
	
	/**
	 * Prints the given String.
	 *
	 * @param str  the String to be printed.
	 */
	public void print( String str ) throws Exception
	{
		print( str.toCharArray() );
	}
	
	/**
	 * Prints each character in an array.
	 *
	 * @param chars  the characters to be printed.
	 * @exception IOException if there's a problem.
	 */
	public void print( char[] chars ) throws Exception
	{
		printManuallyIndented( chars );
	}
	
	/**
	 * Prints the given char.
	 *
	 * @param c  the char to be printed.
	 */
	public void print( char c ) throws Exception
	{
		print( new char[] { c } );
	}
	
	/**
	 * Prints the given int.
	 *
	 * @param i  the int to be printed.
	 */
	public void print( int i ) throws Exception
	{
		print( Integer.toString( i ).toCharArray() );
	}
	
	/**
	 * Prints the given boolean.
	 *
	 * @param b  the boolean to be printed.
	 */
	public void print( boolean b )throws Exception
	{
		if ( b )
		{
			print( "true" );
		}
		else
		{
			print( "false" );
		}
	}
	
	/**
	 * Prints a newline character.
	 */
	public void println() throws Exception
	{
		print( NEWLINE );
	}
	
	/**
	 * Prints the given String followed by a newline character.
	 *
	 * @param str  the String to be printed.
	 */
	public void println( String str ) throws Exception
	{
		print( str );
		print( NEWLINE );
	}
	
	/**
	 * Prints each character in an array followed by a newline character.
	 *
	 * @param chars  the characters to be printed.
	 */
	public void println( char[] chars ) throws Exception
	{
		print( chars );
		print( NEWLINE );
	}
	
	/**
	 * Prints the given char followed by a newline character.
	 *
	 * @param c  the char to be printed.
	 * @exception IOException if there's a problem.
	 */
	public void println( char c ) throws Exception
	{
		print( new char[] { c, '\n' } );
	}
	
	/**
	 * Prints the given int followed by a newline character.
	 *
	 * @param i  the int to be printed.
	 */
	public void println( int i ) throws Exception
	{
		print( Integer.toString( i ).toCharArray() );
		print( NEWLINE );
	}

	/**
	 * Prints the given boolean followed by a newline character.
	 *
	 * @param b  the boolean to be printed.
	 */
	public void println( boolean b ) throws Exception
	{
		print( b );
		print( NEWLINE );
	}

	/**
	 * Prints the characters to the stream.
	 *
	 * @param ch  the characters to write.
	 */
	private void printManuallyIndented( char[] ch ) throws Exception
	{
		int currentStart = 0;
		int end = ch.length;
		
		try {
			for ( int i = 0; i < end; i++ )
			{	
				if ( ch[ i ] == '\n' ) 
				{

					writer.write( ch, currentStart, i + 1 - currentStart );
				
					currentStart = i + 1;

					// Count the line.
					lineNumber++;
				}
			}

			// If there are characters after the last newline, we haven't
			// written them yet.
			if ( end > currentStart && ch[ end - 1 ] != '\n' )
			{
				writer.write( ch, currentStart, end - currentStart );

			}
		}
		catch (IOException e) {
			throw new Exception(e.getMessage());
		}
	}
	
}
