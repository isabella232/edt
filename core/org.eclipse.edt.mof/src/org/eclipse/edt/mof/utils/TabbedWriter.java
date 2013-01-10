/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.edt.mof.serialization.SerializationException;


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
 * @author Matthew Heitz/Raleigh/IBM
 */

public class TabbedWriter 
{
	/**
	 * The underlying output stream.
	 */
	private Writer      writer;

	/**
	 * The current level of indentation.
	 */
	private int         indent;

	/**
	 * True if printIndented should add tabs before the next bit of output.
	 */
	private boolean     doIndent;

	/**
	 * True if { and } trigger changes to the indent level.
	 */
	private boolean     doAutoIndent;

	/**
	 * Buffer to hold a line of text in auto-indent mode.
	 */
	private String      currentLine;
	
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
	public TabbedWriter() 
	{
		this.writer = new BufferedWriter( new StringWriter() );

		// Assume we should indent the first line.
		doIndent = true;
		doAutoIndent = true;
		lineNumber = 1;
	}
	
	/**
	 * Creates a TabbedWriter which writes its output to the Writer.
	 * It's a good idea to use a BufferedWriter.
	 *
	 * <P> Auto-indenting is on by default, so { and } trigger changes
	 * to the indent level, and whitespace at the front of each line 
	 * is removed.
	 *
	 * @param write  the output stream.
	 * @see #setAutoIndent(boolean)
	 */
	public TabbedWriter( Writer writer ) 
	{
		this.writer = writer;
		
		// Assume we should indent the first line.
		doIndent = true;

		doAutoIndent = true;

		lineNumber = 1;
	}
	
	/**
	 * Closes the stream.
	 */
	public void close()
	{
		try {
			if ( doAutoIndent && currentLine != null )
			{
				// This forces currentLine to be written.
				println();
			}
		
			writer.close();
		}
		catch (IOException e) {
			throw new SerializationException(e.getMessage());
		}
	}
	
	/**
	 * For each occurrance of } in currentLine, subtracts one from the 
	 * indent level.  Returns the indent level of the beginning of the
	 * next line, which is determined by the number of {'s.
	 */
	private int computeIndent() 
	{
		int nextIndent = 0;
		char[] chars = currentLine.toCharArray();
		for ( int i = 0; i < chars.length; i++ )
		{
			if ( chars[ i ] == '}' )
			{
				if ( nextIndent > 0 )
				{
					// This } matches a { from the same line, so don't let them 
					// affect the indentation.
					nextIndent--;
				}
				else
				{	
					indent--;
				}
			}
			else if ( chars[ i ] == '{' )
			{
				nextIndent++;
			}
		}
		return indent + nextIndent;
	}
	
	/**
	 * Flushes the stream.  If the last character written was not a newline,
	 * the last partial line of text will not be flushed.  This is because the 
	 * text is indented accoring to what's in the current line.  When you flush
	 * in the middle of a line the TabbedWriter doesn't know how much to indent it.
	 */
	public void flush() throws SerializationException
	{
		try {
			writer.flush();
		}
		catch (IOException e) {
			throw new SerializationException(e.getMessage());
		}
	}
	
	/**
	 * Returns true if auto-indent is on.
	 *
	 * @return true if auto-indent is on.
	 */
	public boolean getAutoIndent()
	{
		return doAutoIndent;
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
	 * Adds tabs to the stream once per indent level.
	 */
	private void indent()
	{
		try {
			for ( int i = 0; i < indent; i++ ) 
			{
				writer.write( '\t' );
			}
		}
		catch (IOException e) {
			throw new SerializationException(e.getMessage());
		}
	}
	
	/**
	 * Decreases the indent level.  It's safe to call this method
	 * even if the indent level is currently zero.
	 */
	public void popIndent() 
	{
		if ( indent > 0 )
		{
			indent--;
		}
	}
	
	/**
	 * Prints the given String.
	 *
	 * @param str  the String to be printed.
	 */
	public void print( String str ) 
	{
		print( str.toCharArray() );
	}
	
	/**
	 * Prints each character in an array.
	 *
	 * @param chars  the characters to be printed.
	 * @exception IOException if there's a problem.
	 */
	public void print( char[] chars ) 
	{
		if ( doAutoIndent )
		{
			printAutoIndented( chars );
		}
		else
		{
			printManuallyIndented( chars );
		}
	}
	
	/**
	 * Prints the given char.
	 *
	 * @param c  the char to be printed.
	 */
	public void print( char c ) 
	{
		print( new char[] { c } );
	}
	
	/**
	 * Prints the given int.
	 *
	 * @param i  the int to be printed.
	 */
	public void print( int i ) 
	{
		print( Integer.toString( i ).toCharArray() );
	}
	
	/**
	 * Prints the given boolean.
	 *
	 * @param b  the boolean to be printed.
	 */
	public void print( boolean b )
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
	public void println() 
	{
		print( NEWLINE );
	}
	
	/**
	 * Prints the given String followed by a newline character.
	 *
	 * @param str  the String to be printed.
	 */
	public void println( String str ) 
	{
		print( str );
		print( NEWLINE );
	}
	
	/**
	 * Prints each character in an array followed by a newline character.
	 *
	 * @param chars  the characters to be printed.
	 */
	public void println( char[] chars ) 
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
	public void println( char c ) 
	{
		print( new char[] { c, '\n' } );
	}
	
	/**
	 * Prints the given int followed by a newline character.
	 *
	 * @param i  the int to be printed.
	 */
	public void println( int i )
	{
		print( Integer.toString( i ).toCharArray() );
		print( NEWLINE );
	}

	/**
	 * Prints the given boolean followed by a newline character.
	 *
	 * @param b  the boolean to be printed.
	 */
	public void println( boolean b ) 
	{
		print( b );
		print( NEWLINE );
	}

	/**
	 * Prints the characters to the stream.  Lines are written one at a time,
	 * and whitespace at the front of a line is removed.
	 *
	 * @param ch  the characters to write.
	 */
	private void printAutoIndented( char[] ch ) 	
	{
		int currentStart = 0;
		int end = ch.length;
		
		try {
			for ( int i = 0; i < end; i++ )
			{
				if ( ch[ i ] == '\n' ) 
				{
					// Time to write this line.
					String saveText = new String( ch, currentStart, i - currentStart );
					if ( currentLine == null )
					{
						currentLine = saveText;
					}
					else
					{
						currentLine = currentLine.concat( saveText );
					}

					// Remove extra whitespace and add the tabs for this line.
					currentLine = currentLine.trim();
					int nextIndent = computeIndent();
					indent();

					// Now we can write the text.
					writer.write( currentLine );
					writer.write( '\n' );
					currentLine = null;
					currentStart = i + 1;
				
					// Set the indent level for the next line.
					indent = nextIndent;

					// Count the line.
					lineNumber++;
				}
			}
		}
		catch (IOException e) {
			throw new SerializationException(e.getMessage());
		}

		// If there are characters after the last newline, we haven't
		// written them yet.
		if ( end > currentStart && ch[ end - 1 ] != '\n' )
		{
			String saveText = new String( ch, currentStart, end - currentStart );
			if ( currentLine == null )
			{
				currentLine = saveText;
			}
			else
			{
				currentLine = currentLine.concat( saveText );
			}
		}
	}
	
	/**
	 * Prints the characters to the stream.
	 *
	 * @param ch  the characters to write.
	 */
	private void printManuallyIndented( char[] ch ) 
	{
		int currentStart = 0;
		int end = ch.length;
		
		try {
			for ( int i = 0; i < end; i++ )
			{	
				if ( ch[ i ] == '\n' ) 
				{
					// Indent and write all the characters we've seen so far.
					if ( doIndent )
					{
						indent();
					}

					writer.write( ch, currentStart, i + 1 - currentStart );
				
					currentStart = i + 1;
					doIndent = true;

					// Count the line.
					lineNumber++;
				}
			}

			// If there are characters after the last newline, we haven't
			// written them yet.
			if ( end > currentStart && ch[ end - 1 ] != '\n' )
			{
				if ( doIndent )
					indent();
					
				writer.write( ch, currentStart, end - currentStart );
				doIndent = false;
			}
		}
		catch (IOException e) {
			throw new SerializationException(e.getMessage());
		}
	}
	
	/**
	 * Increases the indent level.
	 */
	public void pushIndent() 
	{
		indent++;
	}
	
	/**
	 * Turns auto-indenting on or off.  If it's on, writing { and }
	 * trigger changes in the indent level, and whitespace at the
	 * front of each line is removed.
	 *
	 * @param flag  the new auto-indent setting.
	 * @exception JavaGenException if there's a problem.
	 */
	public void setAutoIndent( boolean flag )
	{
		if ( doAutoIndent && !flag && currentLine != null )
		{
			// Part of a line has yet to be written.  Write it
			// out now.
			doIndent = true;
			printManuallyIndented( currentLine.toCharArray() );
			currentLine = null;
		}
		
		doAutoIndent = flag;
	}

	public String getCurrentLine()
	{
		return currentLine;
	}
}
