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
package org.eclipse.edt.compiler.internal.core.validation.name;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author dollar
 *
 * This class is a lexer (AKA lexical analizer, or scanner) for the EGL Name 
 * validator. It turns the characters of a Reader into s.  It
 * also keeps lists of any comments and problems that were found.
 *
 * <P> The methods whose names begin with "scan" are used to get characters from
 * the input.  The methods whose names begin with "read" recognize the characters
 * and turn them into s.
 */
public class EGLNameLexer 
{
	/**
	 * The object we're reading characters from.
	 */
	protected PushbackReader reader;

	/**
	 * The current offset.
	 */
	protected int offset;

	/**
	 * The current line within the input string (not the file).
	 */
	protected int line;
	
	/**
	 * The current column.
	 */
	protected int column;	

	/**
	 * True if the last character read was a carriage return, \r.
	 */
	protected boolean sawCR;


	protected IProblemRequestor problemRequestor;

	/**
	 * All the comments we've found:  objects.
	 */
	protected ArrayList comments;
	
	/*
	 * Lookahead tokens
	 */
	public EGLNameToken lookAhead1;
	public EGLNameToken lookAhead2; 
	public EGLNameToken lookAhead3;

    private ICompilerOptions compilerOptions;	
	
	/**
	 * Creates an EGLLexer capable of lexing the Reader.
	 *
	 * @param reader  a Reader on the input.
	 */
	public EGLNameLexer( Reader reader, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) 
	{
		this( reader, 0 , false, problemRequestor, compilerOptions);
	}	

	/**
	 * Creates an EGLSQLLexer capable of lexing the Reader.
	 *
	 * @param reader         a Reader on the input.
	 * @param currentOffset  the starting offset.
	 */
	public EGLNameLexer( Reader reader, int currentOffset, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) 
	{
		this( reader, currentOffset, false, problemRequestor, compilerOptions);
	}

	/**
	 * Creates an EGLSQLLexer capable of lexing the Reader.
	 *
	 * @param reader         a Reader on the input.
	 * @param currentOffset  the starting offset.
	 * @param filename		 boolean to say if we are tokenizing a filename
	 */
	public EGLNameLexer( Reader reader, int currentOffset, boolean filename, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) 
	{
		this.problemRequestor = problemRequestor;	
		this.compilerOptions = compilerOptions;
		
		init( reader );
	
		offset = currentOffset - 1;
		line = 1;
		column = 0;
		lookAhead1 = null;
		lookAhead2 = null;
		lookAhead3 = null;
		
	}
	
	
	/**
	 * Reverts the lexer to its initial state but with new input.  All fields
	 * will be reset.  The lists of problems and comments will be cleared.
	 * 
	 * @param reader  a Reader on the new input.
	 */
	public void init( Reader reader )
	{
		// Create a reader with a 2-character pushback buffer.
		this.reader = new PushbackReader( reader, 2 );

		line = 1;
		column = 0;	
		offset = -1;
		
		lookAhead1 = null;
		lookAhead2 = null;
		lookAhead3 = null;
		
		sawCR = false;
	
		if ( comments == null )
		{
			comments = new ArrayList();
		}
		else
		{
			comments.clear();
		}
	}
	
	/**
	 * Returns the comments that were read.  They will be EGLPrimeTokens.
	 */
	public List getComments()
	{
		return comments;
	}
	
	/**
	 * Returns the current offset.
	 *
	 * @return the current offset.
	 */
	public int getOffset()
	{
		return offset;
	}

	/**
	 * Returns the current line number within the statement.
	 *
	 * @return the current line number.
	 */
	public int getLine()
	{
		return line;
	}

	/**
	 * Returns the current column number.
	 *
	 * @return the current column number.
	 */
	public int getColumn()
	{
		return column;
	}

	/**
	 * Returns the next token from the input and fills the lookAhead tokens.
	 */
	public EGLNameToken getNextToken(boolean filename)
		throws IOException
	{
		EGLNameToken token = null;
		try {
			if (lookAhead1==null)
			{ // fill in lookAhead1 and lookAhead2 and lookAhead3
				token = nextToken(filename);
				if (token != null)
				{   // fill up lookAhead array 
					lookAhead1 = nextToken(filename);
					if (lookAhead1 != null)
						lookAhead2 = nextToken(filename);
						if (lookAhead2 != null)
							lookAhead3 = nextToken(filename);
				}
			}	
			else if (lookAhead2==null )
			{  // fill in lookAhead2 and lookAhead3
				token = lookAhead1;
				lookAhead1 = lookAhead2;
				if (lookAhead1 != null)
					lookAhead2 = nextToken(filename);
					if (lookAhead2 != null)
						lookAhead3 = nextToken(filename);
			}				
			else
			{ // fill in lookAhead3
				token = lookAhead1;
				lookAhead1 = lookAhead2;
				lookAhead2 = lookAhead3;
				if (lookAhead2 != null)
					lookAhead3 = nextToken(filename);
			}
			return token;
		}
		catch ( IOException iox )
		{
			throw iox ;
		}		
		
	}	
	
	/**
	 * Returns the next token from the input.
	 */
	private EGLNameToken nextToken(boolean filename)
		throws IOException
	{
		int c;
		
		try
		{
			// Get the next non-blank character. Bail if we hit the end.
			if ( ( c = scanNonBlank() ) == -1 )
			{
				return null;
			}

			// Check for comments.
			COMMENT_LOOP: 
				while ( c == '/' )
				{
					switch ( scanAheadOneChar() )
					{
						case '/':				
							break;
	
						case '*':
							break;
	
						default:
							// We didn't get a comment, so break out of the while loop.
							break COMMENT_LOOP;
					}
	
					// Reset c.  It may be the start of another comment or
					// the end of the input.
					c = scanNonBlank();
					if ( c == -1 )
					{
						return null;
					}
				}
			
			// See if it's a single quoted string literal.
			// This is only valid inside of SQL.
			if ( c == '\'' )
			{
				return readSQLString( c, offset, line, column );
			}	
			
			// See if it's a double quoted string.
			if ( c == '\"' )
			{
				return readEGLString( c, offset, line, column );
			}
			
			
			if (filename) {
				// See if it's the first character of an identifier or keyword.
				if ( EGLCharInfo.isFileIdentifier1( c, compilerOptions ) )
				{
					return readIdentifier( c, offset, line, column );
				}
			}
			else
				// See if it's the first character of an identifier or keyword.
				if ( EGLCharInfo.isIdentifier1( c, compilerOptions ) )
				{
					return readIdentifier( c, offset, line, column );
				}
				
			// See if it's a number.
			if ( EGLCharInfo.isDigit( c ) )
			{
				return readNumber( c, offset, line, column );
			}
			if ( c == '.' && EGLCharInfo.isDigit( scanAheadOneChar() ) )
			{
				return readFraction( c, offset, line, column );
			}
	
			// See if it's a special symbol.
			if ( EGLCharInfo.isSymbol1( c ) )
			{
				return readSymbol( c, offset, line, column );
			}
			
							
		}
		catch ( IOException iox )
		{
			throw iox ;
		}
	
		// don't consider anything invalid, just create a token
		// with this character as the text.  The token type will
		// be unknown.  
		return new EGLNameToken( EGLNameToken.UNKNOWN_EGL, String.valueOf( (char)c ), offset, line, column );	
	}
	

	/**
	 * Returns an EGLNameToken representing a number with no digits before the decimal point.
	 *
	 * @param c          the first character of the token.  It has been read.
	 * @param startOffset the offset at the start of the token.
	 * @param startLine   the starting line (within the SQL statement) of the token.
	 * @param startColumn the starting column of the token. 
	 * @return an EGLNameToken for a fraction.
	 */
	protected EGLNameToken readFraction( int c, int startOffset, int startLine, int startColumn ) 
		throws IOException
	{
		int kind;
		int maxSize = 0;
		
		// c is the decimal point.
		StringBuffer s = new StringBuffer().append( (char)c );
		
		// Read the digits after the decimal point.
		while ( EGLCharInfo.isDigit( c ) ) 
		{
			s.append( (char)scanChar() );
			c = scanAheadOneChar();
		}
	
		if (c == 'e' || c == 'E') { 
			//	FloatLit = ({DecimalLit}|{Integer})[eE][+-]?{Integer}	
			kind = EGLNameToken.FLOAT_NUMBER;
			// 32 digits are allowed in mantissa
			//  1 decimal allowed
			//  1 'e'
			//  1 sign
			//	1 exponent (could be larger but then digits in mantissa are limited)
			maxSize =  36 ;
			s.append( (char)scanChar() );
			// now read the exponent
			c = scanAheadOneChar();
			if (c == '+' || c == '-') {
				s.append( (char)scanChar() );
				c = scanAheadOneChar();
			}
			while ( EGLCharInfo.isDigit( c ) )
			{
				s.append( (char)scanChar() );
				c = scanAheadOneChar();
			}				
		}
		else {
			kind = EGLNameToken.REAL_NUMBER;
			// 32 digits are allowed plus the decimal point.
			maxSize = 33;
		}
		
		// Make sure the number isn't too large.  
		if ( s.length() > maxSize )
		{
			// Just emit a message and keep parsing.
			problemRequestor.acceptProblem(
				startOffset,      
				startOffset+s.length(),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.L_TOO_MANY_DIGITS);
		}
		
		return new EGLNameToken( kind, s.toString(), startOffset, startLine, startColumn );
	}

	/**
	 * Returns an EGLNameToken for an identifier.
	 *
	 * @param c          the first character of the token.  It has been read.
	 * @param startOffset the offset at the start of the token.
	 * @param startLine   the starting line of the token.
	 * @param startColumn the starting column of the token. 
	 * @return an EGLNameToken for an identifier.
	 */
	protected EGLNameToken readIdentifier( int c, int startOffset, int startLine, int startColumn ) 
		throws IOException
	{
		StringBuffer s = new StringBuffer().append( (char)c );
	
		// Read the rest of the identifier.  Make sure we leave the character
		// immediately after the end of the identifier in the stream.
		while ( EGLCharInfo.isIdentifier( scanAheadOneChar(), compilerOptions ) )
		{
			s.append( (char)scanChar() );
		}
		String ident = s.toString();
	 
		// Return an EGLNameToken
		return new EGLNameToken( EGLNameToken.IDENTIFIER, ident, startOffset, startLine, startColumn );
	}

	/**
	 * Returns an EGLNameToken for an identifier.
	 *
	 * @param c          the first character of the token.  It has been read.
	 * @param startOffset the offset at the start of the token.
	 * @param startLine   the starting line of the token.
	 * @param startColumn the starting column of the token. 
	 * @return an EGLNameToken for an identifier.
	 */
	protected EGLNameToken readSQLString( int c, int startOffset, int startLine, int startColumn ) 
		throws IOException
	{
		StringBuffer s = new StringBuffer().append( (char)c );
		int nextChar;
		
		// Read the rest of the string.  Make sure we leave the character
		// immediately after the end of the string in the stream.	
		while ( true )
		{
			nextChar = scanAheadOneChar();
			
			switch ( nextChar )
			{
				case '\'':
					// Found a complete string.  Make sure there's something inside
					// the quotes.
					s.append( (char)scanChar() );
					// Return an EGLNameToken
					return new EGLNameToken( EGLNameToken.QUOTED_STRING, s.toString(), startOffset, startLine, startColumn );
				case '\n':
				case '\r':
				case -1:
					// End of line or end of input.
					problemRequestor.acceptProblem(
						startOffset,      
						startOffset+s.length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.L_STRING_NOT_CLOSED);  
					// Return an EGLNameToken
					return new EGLNameToken( EGLNameToken.QUOTED_STRING, s.toString(), startOffset, startLine, startColumn );		 
				default:
					s.append( (char)scanChar() );
					break;
			}
		}
	}

	/**
	 * Returns an EGLNameToken for an identifier.
	 *
	 * @param c          the first character of the token.  It has been read.
	 * @param startOffset the offset at the start of the token.
	 * @param startLine   the starting line of the token.
	 * @param startColumn the starting column of the token. 
	 * @return an EGLNameToken for an identifier.
	 */
	protected EGLNameToken readEGLString( int c, int startOffset, int startLine, int startColumn ) 
		throws IOException
	{
		StringBuffer s = new StringBuffer().append( (char)c );
		int nextChar;
		
		// Read the rest of the string.  Make sure we leave the character
		// immediately after the end of the string in the stream.	
		while ( true )
		{
			nextChar = scanAheadOneChar();
			
			switch ( nextChar )
			{
				case '\"':
					// Found a complete string.  Make sure there's something inside
					// the quotes.
					s.append( (char)scanChar() );
					// Return an EGLNameToken
					return new EGLNameToken( EGLNameToken.QUOTED_STRING, s.toString(), startOffset, startLine, startColumn );
				case '\n':
				case '\r':
				case -1:
					// End of line or end of input.
					problemRequestor.acceptProblem(
						startOffset,      
						startOffset+s.length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.L_STRING_NOT_CLOSED);
					// Return an EGLNameToken
					return new EGLNameToken( EGLNameToken.QUOTED_STRING, s.toString(), startOffset, startLine, startColumn );		 
				default:
					s.append( (char)scanChar() );
					break;
			}
		}
	}
	
	
	/**
	 * Returns an EGLPrimeToken for a number.
	 *
	 * @param c          the first character of the token.  It has been read.
	 * @param startOffset the offset at the start of the token.
	 * @param startLine   the starting line of the token.
	 * @param startColumn the starting column of the token. 
	 * @return an EGLNameToken for a number (integer, real, or floating).
	 */
	protected EGLNameToken readNumber( int c, int startOffset, int startLine, int startColumn ) 
		throws IOException
	{
		int kind;
		int maxSize = 0;
		
		StringBuffer s = new StringBuffer().append( (char)c );	
		
		// Read the digits before the decimal point.
		c = scanAheadOneChar();
		while ( EGLCharInfo.isDigit( c ) )
		{
			s.append( (char)scanChar() );
			c = scanAheadOneChar();
		}
	
		// If there's no decimal point or no e for floating point notation, return
		// an integer token.  Otherwise decide if we have a real number or
		// a floating point number.
		if ( c != '.' &&  c != 'e'  &&  c != 'E' )
		{
			kind = EGLNameToken.INTEGER;
	
			// 18 digits are allowed.
			maxSize = 18;
		}
		else
		{
	
			if (c == '.') {
				s.append( (char)scanChar() );
			
				// Read the digits after the decimal point.
				c = scanAheadOneChar();
				while ( EGLCharInfo.isDigit( c ) )
				{
					s.append( (char)scanChar() );
					c = scanAheadOneChar();
				}
			}
			if (c == 'e' || c == 'E') { 
				//	FloatLit = ({DecimalLit}|{Integer})[eE][+-]?{Integer}	
				kind = EGLNameToken.FLOAT_NUMBER;
				// 32 digits are allowed in mantissa
				//  1 decimal allowed
				//  1 'e'
				//  1 sign
				//	1 exponent (could be larger but then digits in mantissa are limited)
				maxSize =  36 ;
				s.append( (char)scanChar() );
				// now read the exponent
				c = scanAheadOneChar();
				if (c == '+' || c == '-') {
					s.append( (char)scanChar() );
					c = scanAheadOneChar();
				}
				while ( EGLCharInfo.isDigit( c ) )
				{
					s.append( (char)scanChar() );
					c = scanAheadOneChar();
				}				
			}
			else {
				kind = EGLNameToken.REAL_NUMBER;
				// 32 digits are allowed plus the decimal point.
				maxSize = 33;
			}
		}
		
		// Make sure the number isn't too large.
		if ( s.length() > maxSize )
		{
			problemRequestor.acceptProblem(
				startOffset,      
				startOffset+s.length(),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.L_TOO_MANY_DIGITS);						
		}
		
		return new EGLNameToken( kind, s.toString(), startOffset, startLine, startColumn );
	}

	/**
	 * Returns an EGLNameToken for a symbol.  
	 *
	 * @param c          the first character of the token.  It has been read.
	 * @param startOffset the offset at the start of the token.
	 * @param startLine   the starting line of the token.
	 * @param startColumn the starting column of the token. 
	 * @return an EGLNameToken for a symbol.
	 */
	protected EGLNameToken readSymbol( int c, int startOffset, int startLine, int startColumn ) 
		throws IOException
	{
		switch ( c )
		{
			case '[':
				return new EGLNameToken( EGLNameToken.L_SQUARE, "[",  startOffset, startLine, startColumn ); //$NON-NLS-1$
				
			case ']':
				return new EGLNameToken( EGLNameToken.R_SQUARE, "]", startOffset, startLine, startColumn ); //$NON-NLS-1$
	
			case ',':
				return new EGLNameToken( EGLNameToken.COMMA, ",", startOffset, startLine, startColumn ); //$NON-NLS-1$
				
			case '.':
				return new EGLNameToken( EGLNameToken.DOT, ".", startOffset, startLine, startColumn ); //$NON-NLS-1$
				
			case ':': 
			{
				return new EGLNameToken( EGLNameToken.COLON, ":", startOffset, startLine, startColumn ); //$NON-NLS-1$
			}
			default : {
				StringBuffer s = new StringBuffer().append( (char)c );
				return new EGLNameToken( EGLNameToken.UNKNOWN_EGL, s.toString(), startOffset, startLine, startColumn );
			}
		}
	}

	/**
	 * Reads a // comment and stores it.
	 *
	 * @param c          the opening / character of the comment.  It has been read.
	 * @param startLine  the line at the start of the comment.
	 * @param startCol   the column at the start of the comment.
	 */
	protected EGLNameToken readLineComment( int c, int startLine, int startCol, int startOffset ) 
		throws IOException
	{
		StringBuffer s = new StringBuffer().append( (char)c );
	
		// Read the second /.
		s.append( (char)scanChar() );
	
		// Read the rest of the comment.  It ends at the first newline or
		// carriage return.
		for ( c = scanChar(); c != -1 && c != '\r' && c != '\n'; c = scanChar() )
		{
			s.append( (char)c );
		}
	
		comments.add( new EGLNameToken( EGLNameToken.COMMENT, s.toString(), startLine, startCol, startOffset ) );
		return (EGLNameToken)(comments.get(comments.size()-1) );	
	}


	/**
	 * Reads a /* comment and stores it.
	 *
	 * @param c          the opening / character of the comment.  It has been read.
	 * @param startLine  the line at the start of the comment.
	 * @param startCol   the column at the start of the comment.
	 */
	protected EGLNameToken readBlockComment( int c, int startLine, int startCol, int startOffset ) 
		throws IOException
	{
		StringBuffer s = new StringBuffer().append( (char)c );
	
		// Read the *.
		s.append( (char)scanChar() );
	
		// Read the rest of the comment.  It ends at the first * that's followed
		// by a /.
		for ( c = scanChar(); c != -1 ; c = scanChar() )
		{
			s.append( (char)c );
			if ( c == '*' && scanAheadOneChar() == '/' )
			{
				s.append( (char)scanChar() );
				break;
			}
		}
	
		if ( c == -1 )
		{
			problemRequestor.acceptProblem(
				startOffset,
				s.length(),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.UNCLOSED_BLOCK_COMMENT);					
		}
		
		comments.add( new EGLNameToken( EGLNameToken.COMMENT, s.toString(), startLine, startCol, startOffset ) );
		return (EGLNameToken)(comments.get(comments.size()-1) );	
	}

	/**
	 * Returns the next input character without reading it.
	 */
	protected int scanAheadOneChar() throws IOException
	{
		int c = reader.read();
		if ( c == -1 )
		{
			return -1;
		}
		reader.unread( c );
		return c;
	}

	/**
	 * Returns the input character after the next one, without reading it.
	 */
	protected int scanAheadTwoChars() throws IOException
	{
		int c1 = reader.read();
		if ( c1 == -1 )
		{
			return -1;
		}
		int c2 = reader.read();
		if ( c2 == -1 )
		{
			reader.unread( c1 );
			return -1;
		}
		reader.unread( c2 );
		reader.unread( c1 );
		return c2;
	}

	/**
	 * Returns the input character.
	 */
	protected int scanChar() throws IOException
	{
		int c = reader.read();
		switch ( c )
		{
			case -1:
				return -1;
				
			case '\n':
				// If sawCR is true, it's a DOS end of line, \r\n.
				// If sawCR is false, it's a Unix end of line, \n.
				if ( ! sawCR )
					{
						line++;
						column = 0;
					}
				offset++;
				sawCR = false;
				break;				
			case '\r':
				// Either a Macintosh end of line, \r, or the start of a
				// DOS end of line, \r\n.
					line++;
					column = 0;
					offset++;
					sawCR = true;
					break;				
			case '\t':
				// Tabs cause the cursor in the logic editor to move to the
				// next tab stop.
				column++; 
				offset++;
				sawCR = false;
				break;
			
			default:
				column++;
				offset++;
				sawCR = false;
				break;
		}
		
		return c;
	}

	/**
	 * Returns the next non-blank character, or -1 if the end of the stream
	 * is reached.
	 *
	 * @return the next non-blank character.
	 */
	protected int scanNonBlank() 
		throws IOException
	{
		int c = scanChar();
		while ( c <= ' ' && c != -1 )
		{
			c = scanChar();
		}
		return c;
	}
	
	
}


