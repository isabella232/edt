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
package org.eclipse.edt.compiler.internal;

/**
 * @author dollar
 *
 */
public class EGLCharInfo {

	/**
	  * An array containing the characters that begin symbols.
	  */
	private static final char[] INITIAL_SYMBOL_CHARS = new char[ 19 ];

	// A static intializer for the INITIAL_SYMBOL_CHARS.
		 static
		 {
				 // Set up INITIAL_SYMBOL_CHARS.
				 INITIAL_SYMBOL_CHARS[ 0 ] = '.';
				 INITIAL_SYMBOL_CHARS[ 1 ] = ';';
				 INITIAL_SYMBOL_CHARS[ 2 ] = '!';
				 INITIAL_SYMBOL_CHARS[ 3 ] = '(';
				 INITIAL_SYMBOL_CHARS[ 4 ] = ')';
				 INITIAL_SYMBOL_CHARS[ 5 ] = '[';
				 INITIAL_SYMBOL_CHARS[ 6 ] = ']';
				 INITIAL_SYMBOL_CHARS[ 7 ] = '<';
				 INITIAL_SYMBOL_CHARS[ 8 ] = '>';
				 INITIAL_SYMBOL_CHARS[ 9 ] = '=';
				 INITIAL_SYMBOL_CHARS[ 10 ] = ',';
				 INITIAL_SYMBOL_CHARS[ 11 ] = '-';
				 INITIAL_SYMBOL_CHARS[ 12 ] = '/';
				 INITIAL_SYMBOL_CHARS[ 13 ] = '*';
				 INITIAL_SYMBOL_CHARS[ 14 ] = '+';
				 INITIAL_SYMBOL_CHARS[ 15 ] = '%';
				 INITIAL_SYMBOL_CHARS[ 16 ] = ':';
				 INITIAL_SYMBOL_CHARS[ 17 ] = '&';
				 INITIAL_SYMBOL_CHARS[ 18 ] = '|';
		 }	
	
	/**
	 * Returns true if c is a digit character.  Uses
	 * Character.isDigit.
	 *
	 * @param c  the character to check.
	 * @return true if c is a digit character.
	 */
	public static final boolean isDigit( int c )
	{
		return Character.isDigit( (char)c );
	}
	/**
	 * Returns true if c can be part of an identifier.  It can be any
	 * character for which Character.isJavaIdentifierPart returns true.
	 *
	 * @param c  the character to check.
	 * @return true if c can be in an identifier.
	 */
	public static final boolean isIdentifier( int c )
	{
 		return Character.isJavaIdentifierPart( (char)c );		
	}
	/**
	 * Returns true if c can be the first character of an identifier.
	 * It can be any character for which Character.isJavaIdentifierStart
	 * returns true.
	 *
	 * @param c  the character to check.
	 * @return true if c can be the first character of an identifier.
	 */
	public static final boolean isIdentifier1( int c )
	{
		return Character.isJavaIdentifierStart( (char)c );			
	}
	/**
	 * Returns true if c can be the first character of an identifier.
	 * It can be any character for which Character.isJavaIdentifierStart
	 * returns true.
	 *
	 * @param c  the character to check.
	 * @return true if c can be the first character of an identifier.
	 */
	public static final boolean isFileIdentifier1( int c )
	{
		return ( (Character.isJavaIdentifierStart( (char)c ) )
					|| (c == '#')
					|| (c == '$') );
	}	
	/**
	 * Returns true if c can be the first character of a symbol.
	 * It is compared to the INITIAL_SYMBOL_CHARS array.
	 *
	 * @param c  the character to check.
	 * @return true if c can be the first character of a symbol.
	 */
	public static final boolean isSymbol1( int c )
	{
		char[] chars = INITIAL_SYMBOL_CHARS;
		for ( int i = 0; i < chars.length; i++ )
		{
			if ( chars[ i ] == c )
			{
				return true;
			}
		}
		return false;
	}
}
