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
package org.eclipse.edt.compiler.internal.sqltokenizer;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLCharInfo;


/**
 * @author dollar
 *
 * This class is a lexer (AKA lexical analizer, or scanner) for the EGL SQL 
 * statements. It turns the characters of a Reader into s.  It
 * also keeps lists of any comments and problems that were found.
 *
 * <P> The methods whose names begin with "scan" are used to get characters from
 * the input.  The methods whose names begin with "read" recognize the characters
 * and turn them into s.
 */
public class EGLSQLLexer 
{
	/**
	 * The object we're reading characters from.
	 */
	protected PushbackReader reader;
	
	/**
	 * The current number of left squares encounterd that haven't been matched with a right square.
	 */	
	protected int leftSquare;

	/**
	 * The current offset.
	 */
	protected int offset;

	/**
	 * The current line within the statement (not the file).
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

	/**
	 * All the problems we've found: EGLMessage objects.
	 */
	protected ArrayList problems;

	/**
	 * All the comments we've found:  objects.
	 */
	protected ArrayList comments;
	
	/*
	 * Lookahead tokens
	 */
	public EGLPrimeToken lookAhead1;
	public EGLPrimeToken lookAhead2; 
	public EGLPrimeToken lookAhead3;	
	
    public static final HashMap clauseKeywords = new HashMap( 18 );

	private ICompilerOptions compilerOptions;

	static
	  {  
	    clauseKeywords.put( IEGLConstants.SQLKEYWORD_SELECT, new Integer( EGLPrimeToken.SELECT ) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_INTO, new Integer( EGLPrimeToken.INTO ) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_FROM, new Integer( EGLPrimeToken.FROM ) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_VALUES, new Integer( EGLPrimeToken.VALUES ) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_UPDATE, new Integer( EGLPrimeToken.UPDATE ) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_SET, new Integer( EGLPrimeToken.SET ) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_HAVING, new Integer( EGLPrimeToken.HAVING ) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_WHERE, new Integer( EGLPrimeToken.WHERE ) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_ORDER, new Integer( EGLPrimeToken.ORDER ) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_GROUP, new Integer( EGLPrimeToken.GROUP ) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_INSERT, new Integer( EGLPrimeToken.INSERT ) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_FOR, new Integer( EGLPrimeToken.FOR ) );	
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_BY, new Integer( EGLPrimeToken.BY ) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_OF, new Integer( EGLPrimeToken.OF ) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_UNION, new Integer(EGLPrimeToken.UNION ) ); 
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_CURRENT, new Integer(EGLPrimeToken.CURRENT) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_CALL, new Integer(EGLPrimeToken.CALL) );
		clauseKeywords.put( IEGLConstants.SQLKEYWORD_DELETE, new Integer(EGLPrimeToken.DELETE));
	  } 


	/**
	 * Creates an EGLLexer capable of lexing the Reader.
	 *
	 * @param reader  a Reader on the input.
	 */
	public EGLSQLLexer( Reader reader, ICompilerOptions compilerOptions) 
	{
		this( reader, 0, compilerOptions );
	}	

	/**
	 * Creates an EGLSQLLexer capable of lexing the Reader.
	 *
	 * @param reader         a Reader on the input.
	 * @param currentOffset  the starting offset.
	 */
	public EGLSQLLexer( Reader reader, int currentOffset, ICompilerOptions compilerOptions ) 
	{
		this.compilerOptions = compilerOptions;
		init( reader );
	
		leftSquare = 0;	
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
	
		if ( problems == null )
		{
			problems = new ArrayList();
		}
		else
		{
			problems.clear();
		}
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
	 * Returns the list of problems with the input.
	 * The list will contain EGLMessage.
	 */
	public ArrayList getProblems()
	{
		return problems;
	}
	
	/**
	 * Returns the next token from the input and fills the lookAhead tokens.
	 */
	public EGLPrimeToken getNextToken()
		throws IOException
	{
		EGLPrimeToken token = null;
		try {
			if (lookAhead1==null)
			{ // fill in lookAhead1 and lookAhead2 and lookAhead3
				token = nextToken();
				if (token != null)
				{   // fill up lookAhead array 
					lookAhead1 = nextToken();
					if (lookAhead1 != null)
						lookAhead2 = nextToken();
						if (lookAhead2 != null)
							lookAhead3 = nextToken();
				}
			}	
			else if (lookAhead2==null )
			{  // fill in lookAhead2 and lookAhead3
				token = lookAhead1;
				lookAhead1 = lookAhead2;
				if (lookAhead1 != null)
					lookAhead2 = nextToken();
					if (lookAhead2 != null)
					 	lookAhead3 = nextToken();
			}				
			else
			{ // fill in lookAhead3
				token = lookAhead1;
				lookAhead1 = lookAhead2;
				lookAhead2 = lookAhead3;
				if (lookAhead2 != null)
					lookAhead3 = nextToken();
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
	private EGLPrimeToken nextToken()
		throws IOException
	{
		int c;
		EGLPrimeToken commentToken;
		
		try
		{
			// Get the next non-blank character. Bail if we hit the end.
			if ( ( c = scanNonBlank() ) == -1 )
			{
				return null;
			}

			// Check for comments.
		SQL_COMMENT_LOOP: 
			while ( c == '-' )
			{
				switch ( scanAheadOneChar() )
				{
					case '-':
						commentToken = readSQLLineComment( c, offset, line, column );											
						break;
	
					default:
						// We didn't get a comment, so break out of the while loop.
						break SQL_COMMENT_LOOP;
				}
	
				// Reset c.  It may be the start of another comment or
				// the end of the input.
				c = scanNonBlank();
				if ( c == -1 )
				{
					return null;
				}
			}
			
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
			
			// See if it's a single quoted string literal.
			// This is only valid inside of SQL.
			if ( c == '\'' )
			{
				EGLPrimeToken quotedSQL = readSQLString( c, offset, line, column );
				return quotedSQL;
			}	
			
			// See if it's a delimited name (double quoted string).
			// This is only valid inside of SQL.
			if ( c == '\"' )
			{
				EGLPrimeToken delimitedName = readDelimitedName( c, offset, line, column );
				return delimitedName;
			}								
		}
		catch ( IOException iox )
		{
			throw iox ;
		}
	
			// don't consider anything invalid, just create a token
		// with this character as the text.  The token type will
		// be unknown.  This is because we don't want to have to 
		// know what all SQL considers valid.
		return new EGLPrimeToken( EGLPrimeToken.UNKNOWN_EGL, String.valueOf( (char)c ), offset, line, column );	
	}
	

	/**
	 * Returns an EGLPrimeToken representing a number with no digits before the decimal point.
	 *
	 * @param c          the first character of the token.  It has been read.
	 * @param startOffset the offset at the start of the token.
	 * @param startLine   the starting line (within the SQL statement) of the token.
	 * @param startColumn the starting column of the token. 
	 * @return an EGLPrimeToken for a fraction.
	 */
	protected EGLPrimeToken readFraction( int c, int startOffset, int startLine, int startColumn ) 
		throws IOException
	{
		int kind;
		int maxSize;
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
			kind = EGLPrimeToken.FLOAT_NUMBER;
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
			kind = EGLPrimeToken.REAL_NUMBER;
			// 32 digits are allowed plus the decimal point.
			maxSize = 33;
		}
		
		// Make sure the number isn't too large.  
		if ( s.length() > maxSize )
		{
			// Just emit a message and keep parsing.
			problems.add(new Problem(startOffset, startOffset+s.length(), IMarker.SEVERITY_ERROR, IProblemRequestor.L_TOO_MANY_DIGITS, new String[0]));	
		}
		
		return new EGLPrimeToken( kind, s.toString(), startOffset, startLine, startColumn );
	}

	/**
	 * Returns an EGLPrimeToken for an identifier or keyword.
	 *
	 * @param c          the first character of the token.  It has been read.
	 * @param startOffset the offset at the start of the token.
	 * @param startLine   the starting line (within the SQL statement) of the token.
	 * @param startColumn the starting column of the token. 
	 * @return an EGLPrimeToken for an identifier.
	 */
	protected EGLPrimeToken readIdentifier( int c, int startOffset, int startLine, int startColumn ) 
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
	 
		// See if the identifier is really a keyword.
		int kind = keywordKind( ident );
		if ( kind == -1 )
		{
			kind = EGLPrimeToken.IDENTIFIER;
		}
	
		// Return an EGLPrimeToken.
		return new EGLPrimeToken( kind, ident, startOffset, startLine, startColumn );
	}

	/**
	 * Reads a -- comment and stores it.
	 *
	 * @param c          the opening - character of the comment.  It has been read.
	 * @param startOffset the offset at the start of the comment.
	 * @param startLine   the starting line (within the SQL statement) of the token.
	 * @param startColumn the starting column of the token. 
	 */
	protected EGLPrimeToken readSQLLineComment( int c, int startOffset, int startLine, int startColumn ) 
		throws IOException
	{
		StringBuffer s = new StringBuffer().append( (char)c );
	
		// Read the second -.
		s.append( (char)scanChar() );
	
		// Read the rest of the comment.  It ends at the first newline or
		// carriage return.
		for ( c = scanChar(); c != -1 && c != '\r' && c != '\n'; c = scanChar() )
		{
			s.append( (char)c );
		}
	
		comments.add( new EGLPrimeToken( EGLPrimeToken.SQLCOMMENT, s.toString(), startOffset, startLine, startColumn ) );
		return (EGLPrimeToken)(comments.get(comments.size()-1) );
	}

	/**
	 * Returns an EGLPrimeToken for a number.
	 *
	 * @param c          the first character of the token.  It has been read.
	 * @param startOffset the offset at the start of the token.
	 * @param startLine   the starting line (within the SQL statement) of the token.
	 * @param startColumn the starting column of the token. 
	 * @return an EGLPrimeToken for a number.
	 */
	protected EGLPrimeToken readNumber( int c, int startOffset, int startLine, int startColumn ) 
		throws IOException
	{
		int kind;
		int maxSize;
		
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
			kind = EGLPrimeToken.INTEGER;
	
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
				kind = EGLPrimeToken.FLOAT_NUMBER;
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
				kind = EGLPrimeToken.REAL_NUMBER;
				// 32 digits are allowed plus the decimal point.
				maxSize = 33;
			}
		}
		
		// Make sure the number isn't too large.
		if ( s.length() > maxSize )
		{
			problems.add(new Problem(startOffset, startOffset+s.length(), IMarker.SEVERITY_ERROR, IProblemRequestor.L_TOO_MANY_DIGITS, new String[0]));						
		}
		
		return new EGLPrimeToken( kind, s.toString(), startOffset, startLine, startColumn );
	}
	
	/**
	 * Returns an EGLPrimeToken for a single quoted string.  Strings can't span lines.
	 *
	 * <P> The escape for a double quote inside a string is \".  The escape for
	 * a slash inside a string is \\.
	 *
	 * @param firstChar  the first character of the token.  It has been read.
	 * @param startOffset the offset at the start of the token.
	 * @param startLine   the starting line (within the SQL statement) of the token.
	 * @param startColumn the starting column of the token. 
	 * @return an EGLPrimeToken for a string.
	 */
	protected EGLPrimeToken readSQLString( int firstChar, int startOffset, int startLine, int startColumn ) 
		throws IOException
	{
		// Copy all characters into a StringBuffer until we see the closing quote.
		StringBuffer s = new StringBuffer().append( (char)firstChar );
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
					return new EGLPrimeToken( EGLPrimeToken.SQL_STRING, s.toString(), startOffset, startLine, startColumn );
				case '\n':
				case '\r':
				case -1:
					// End of line or end of input.
					problems.add(new Problem(startOffset, startOffset+s.length(), IMarker.SEVERITY_ERROR, IProblemRequestor.L_STRING_NOT_CLOSED, new String[0]));
							 
					return new EGLPrimeToken( EGLPrimeToken.SQL_STRING, s.toString(), startOffset, startLine, startColumn ) ;
				
//				case '\\':
//					// The next character must be one of the valid escapes.
//					int slash = scanChar();
//					int escaped = scanChar();
//					s.append( (char)escaped );
//					if ( escaped != '"' && escaped != '\\' )
//					{
//						// The escaped character was invalid.  
//						problems.add(
//							EGLMessage.createEGLValidationErrorMessage(
//								EGLBasePlugin.EGL_VALIDATION_RESOURCE_BUNDLE,
//								EGLValidationMessages.EGLMESSAGE_L_INVALID_ESCAPE_SEQUENCE,  
//								new String[] {Integer.toString(escaped)},     
//								startOffset,      
//								startOffset+s.length() ) );									 
//					}
//					break;
				
				default:
					s.append( (char)scanChar() );
					break;
			}
		}
	}

	/**
	 * Returns an EGLPrimeToken for a single EGL quoted string.  Strings can't span lines.
	 *
	 * <P> The escape for a double quote inside a string is \".  The escape for
	 * a slash inside a string is \\.
	 *
	 * @param firstChar  the first character of the token.  It has been read.
	 * @param startOffset the offset at the start of the token.
	 * @param startLine   the starting line (within the SQL statement) of the token.
	 * @param startColumn the starting column of the token. 
	 * @return an EGLPrimeToken for a string.
	 */
	protected EGLPrimeToken readEGLString( int firstChar, int startOffset, int startLine, int startColumn ) 
		throws IOException
	{
		// Copy all characters into a StringBuffer until we see the closing quote.
		StringBuffer s = new StringBuffer().append( (char)firstChar );
		int nextChar;
		
		// Read the rest of the string.  Make sure we leave the character
		// immediately after the end of the string in the stream.
		while ( true )
		{
			nextChar = scanAheadOneChar();
			
			switch ( nextChar )
			{
				case '\"':
					// Found a complete string.
					s.append( (char)scanChar() );
					return new EGLPrimeToken( EGLPrimeToken.SQL_STRING, s.toString(), startOffset, startLine, startColumn );
				case '\n':
				case '\r':
				case -1:
					return new EGLPrimeToken( EGLPrimeToken.SQL_STRING, s.toString(), startOffset, startLine, startColumn ) ;
		
				default:
					s.append( (char)scanChar() );
					break;
			}
		}
	}
	
	/**
	 * Returns an EGLPrimeToken for a delimited name (a double quoted string).  
	 * Double quoted strings can't span lines.
	 *
	 * <P> The escape for a double quote inside a string is "". 
	 *
	 * @param firstChar  the first character of the token.  It has been read.
	 * @param startOffset the offset at the start of the token.
	 * @param startLine   the starting line (within the SQL statement) of the token.
	 * @param startColumn the starting column of the token. 
	 * @return an EGLPrimeToken for a string.
	 */
	protected EGLPrimeToken readDelimitedName( int firstChar, int startOffset, int startLine, int startColumn ) 
		throws IOException
	{
		// Copy all characters into a StringBuffer until we see the closing quote.
		StringBuffer s = new StringBuffer().append( (char)firstChar );
		int nextChar;
		int lookAheadChar;
		
		// Read the rest of the string.  Make sure we leave the character
		// immediately after the end of the string in the stream.
		while ( true )
		{
			nextChar = scanAheadOneChar();
			
			switch ( nextChar )
			{
				case '\"':
					// May have found a complete string.  
					// Need to look ahead and see if the quote is doubled
					// If it is doubled, it is just a character in the string
					// not the end
					s.append( (char)scanChar() );
					lookAheadChar = scanAheadOneChar();
					if (lookAheadChar == '\"') // doubled quote
					{                         // we aren't through yet 
						s.append( (char)scanChar() );// put second of doubled quotes on string
						break;
					}	    
					else return new EGLPrimeToken( EGLPrimeToken.DELIMITED_NAME, s.toString(), startOffset, startLine, startColumn );
				case '\n':
				case '\r':
				case -1:
					// End of line or end of input.
					problems.add(new Problem(startOffset, startOffset+s.length(), IMarker.SEVERITY_ERROR, IProblemRequestor.L_STRING_NOT_CLOSED, new String[0]));
							 
					return new EGLPrimeToken( EGLPrimeToken.SQL_STRING, s.toString(), startOffset, startLine, startColumn ) ;
				
				default:
					s.append( (char)scanChar() );
					break;
			}
		}
	}

	/**
	 * Returns an EGLPrimeToken for a symbol.  
	 * 
	 * @param c          the first character of the token.  It has been read.
	 * @param startOffset the offset at the start of the token.
	 * @param startLine   the starting line (within the SQL statement) of the token.
	 * @param startColumn the starting column of the token. 
	 * @return an EGLPrimeToken for a symbol.
	 */
	protected EGLPrimeToken readSymbol( int c, int startOffset, int startLine, int startColumn ) 
		throws IOException
	{
		switch ( c )
		{
			case ';':
				return new EGLPrimeToken( EGLPrimeToken.SEMI, ";", startOffset, startLine, startColumn ); //$NON-NLS-1$
	
			case '(':
				return new EGLPrimeToken( EGLPrimeToken.L_PAREN, "(", startOffset, startLine, startColumn ); //$NON-NLS-1$
				
			case ')':
				return new EGLPrimeToken( EGLPrimeToken.R_PAREN, ")", startOffset, startLine, startColumn ); //$NON-NLS-1$
					
			case '[':
				leftSquare = leftSquare + 1;
				return new EGLPrimeToken( EGLPrimeToken.L_SQUARE, "[",  startOffset, startLine, startColumn ); //$NON-NLS-1$
				
			case ']':
				if ( leftSquare > 0)
					leftSquare = leftSquare - 1;
				return new EGLPrimeToken( EGLPrimeToken.R_SQUARE, "]", startOffset, startLine, startColumn ); //$NON-NLS-1$
	
			case ',':
				return new EGLPrimeToken( EGLPrimeToken.COMMA, ",", startOffset, startLine, startColumn ); //$NON-NLS-1$
				
			case '.':
				return new EGLPrimeToken( EGLPrimeToken.DOT, ".", startOffset, startLine, startColumn ); //$NON-NLS-1$

			case ':':
				if ( leftSquare > 0)
					return new EGLPrimeToken( EGLPrimeToken.COLON, ":", startOffset, startLine, startColumn ); //$NON-NLS-1$
				else 
					return new EGLPrimeToken( EGLPrimeToken.HOST_VAR_COLON, ":", startOffset, startLine, startColumn ); //$NON-NLS-1$
					
			case '=':
 				return new EGLPrimeToken( EGLPrimeToken.EQUAL, "=", startOffset, startLine, startColumn ); //$NON-NLS-1$
	
			case '+':
 				return new EGLPrimeToken( EGLPrimeToken.PLUS, "+", startOffset, startLine, startColumn ); //$NON-NLS-1$
	
			case '-':
 				return new EGLPrimeToken( EGLPrimeToken.MINUS, "-", startOffset, startLine, startColumn ); //$NON-NLS-1$
	
			case '*':
 				return new EGLPrimeToken( EGLPrimeToken.SPLAT, "*", startOffset, startLine, startColumn ); //$NON-NLS-1$
	
			case '/':
 				return new EGLPrimeToken( EGLPrimeToken.SLASH, "/", startOffset, startLine, startColumn ); //$NON-NLS-1$
	
			case '%':
 				return new EGLPrimeToken( EGLPrimeToken.PERCENT, "%", startOffset, startLine, startColumn ); //$NON-NLS-1$

			case '>':
				if ( scanAheadOneChar() == '=' )
				{
					scanChar();
					return new EGLPrimeToken( EGLPrimeToken.GREATER_EQUAL, ">=", startOffset, startLine, startColumn ); //$NON-NLS-1$
				}
				else
				{
					return new EGLPrimeToken( EGLPrimeToken.GREATER, ">", startOffset, startLine, startColumn ); //$NON-NLS-1$
				}
	
			case '<':
				if ( scanAheadOneChar() == '=' )
				{
					scanChar();
					return new EGLPrimeToken( EGLPrimeToken.LESS_EQUAL, "<=", startOffset, startLine, startColumn ); //$NON-NLS-1$
				}
				else
				{
					return new EGLPrimeToken( EGLPrimeToken.LESS, "<", startOffset, startLine, startColumn ); //$NON-NLS-1$
				}
				
			case '!':
				if ( scanAheadOneChar() == '=' )
				{
					scanChar();
					return new EGLPrimeToken( EGLPrimeToken.NOT_EQUAL, "!=", startOffset, startLine, startColumn ); //$NON-NLS-1$
				}
				else
				{
 					return new EGLPrimeToken( EGLPrimeToken.BANG, "!",  startOffset, startLine, startColumn ); //$NON-NLS-1$
				}
	
			case '|':
				if ( scanAheadOneChar() == '|' )
				{
					scanChar();
					return new EGLPrimeToken( EGLPrimeToken.OR, "||", startOffset, startLine, startColumn ); //$NON-NLS-1$
				}
				else
				{
 					return new EGLPrimeToken( EGLPrimeToken.SQL_OR, "|", startOffset, startLine, startColumn );	 //$NON-NLS-1$
				}
				
			case '&':
				if ( scanAheadOneChar() == '&' )
				{
					scanChar();
 					return new EGLPrimeToken( EGLPrimeToken.AND, "&&", startOffset, startLine, startColumn ); //$NON-NLS-1$
				}
				else
				{
 					return new EGLPrimeToken( EGLPrimeToken.SQL_AND, "&", startOffset, startLine, startColumn );	 //$NON-NLS-1$
				}
		}
		
		// This should not happen!
		return null;
	}

	/**
	 * Returns a string representing all of the host variable name. 
	 *
	 * @return a string
	 */
	protected String getHostVariableName( ) 
		throws IOException
	{
		StringBuffer s = new StringBuffer().append( ':' );  // start with the colon
		int nextChar;
		
		// Read the rest of the string.  Make sure we leave the character
		// immediately after the end of the string in the stream.
		while ( true )
		{
			nextChar = scanAheadOneChar();
			
			switch ( nextChar )
			{
				case '[':
					// handle subscripts
					s.append( getSubscripts() );
				case '\r':
				case '\n':
				case '\t':
				case ' ':
				case -1: // end of stream 	
					// found whitespace outside of subscript - we're done!
					return s.toString();
				default:
					s.append( (char)scanChar() );
					break;
			}
		}
		
	}
	
	/**
	 * Returns a string representing all of the subscript.  It should be surrounded by square brackets.
	 * It may contain multiple subscripts. 
	 * If there happens to be a comment within the name, it will be added to the list of comments.
	 *
	 * @return a string
	 */
	protected String getSubscripts( ) 
		throws IOException
	{		
		EGLPrimeToken commentToken;
		StringBuffer s = new StringBuffer().append( (char)scanChar() );  // start with the left bracket
		int nextChar;
		int leftBrackets = 1;
		
		// Read the rest of the string.  Make sure we leave the character
		// immediately after the end of the string in the stream.
		while ( leftBrackets > 0 )
		{
			nextChar = scanAheadOneChar();
			
			switch ( nextChar )
			{
				case '[':
					s.append( (char)scanChar() );
					leftBrackets = leftBrackets + 1; 
					break;
				case ']':
					s.append( (char)scanChar() );
					if (leftBrackets >0)
						leftBrackets = leftBrackets - 1; 
					break;
				case -1: // end of stream 	
					leftBrackets = 0;
				case '-':
					// Check for comments.
					SQL_COMMENT_LOOP2: 
						while ( nextChar == '-' )
						{
							switch ( scanAheadOneChar() )
							{
								case '-':
									commentToken = readSQLLineComment( nextChar, offset, line, column );
									int commentLength = commentToken.getText().length();
									for (int ii=0; ii <commentLength; ii++) {
										s.append(' ');
									}
									break;
				
								default:
									s.append( (char)scanChar() );
									// We didn't get a comment, so break out of the while loop.
									break SQL_COMMENT_LOOP2;
							}

						}
				case '\'':	// starting dynamic access name (EGL quoted string) 
						EGLPrimeToken quotedString = readEGLString( nextChar, offset, line, column );
						s.append(quotedString.getText().toString());
					
				default:
					s.append( (char)scanChar() );
					break;
			}
		}
		return s.toString();
		
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
	
	/**
	 * Returns the constant that represents the given
	 * kind of keyword token.  Returns -1 if s isn't a keyword.
	 *
	 * @param s  the keyword.
	 * @return the constant that represents the given kind of keyword token.
	 */
	public static final int keywordKind( String s )
	{
		Integer kind;
  
		kind = (Integer)clauseKeywords.get( s.toLowerCase(Locale.ENGLISH) );
		return (kind == null) ? -1 : kind.intValue();
	}	
	
}
