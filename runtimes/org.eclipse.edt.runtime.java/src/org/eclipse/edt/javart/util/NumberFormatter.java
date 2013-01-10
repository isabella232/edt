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
package org.eclipse.edt.javart.util;

import java.math.BigDecimal;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.resources.LocalizedText;

/**
 * This class makes a formatted string from a BigDecimal and a formatting pattern.
 * 
 * It began as a line by line translation, from C to Java, of code from I4GL.  Parts
 * have been rewritten to improve its performance.  Currently it's twice as fast as before.
 * 
 * TODO I think the performance could get even better.  At the end of fmtNum we call
 * the rpfs method, which copies characters off of the prstack variable.  The
 * characters are in reverse order.  If we can write them to prstack in the correct
 * order, then most of rpfs can be removed.
 */
public class NumberFormatter
{
	/*
	 * rfmtnum format the given value to a character string according to the
	 * given fmtstring
	 */
	public static String fmtNum( BigDecimal value, String fmtstring, LocalizedText locText )
	{
		if ( fmtstring == null || fmtstring.trim().length() == 0 )
		{
			return "";
		}
		
		return fmtNum( value.toPlainString(), value.signum(), fmtstring, locText );
	}
	
	public static String fmtNum( Float value, String fmtstring, LocalizedText locText )
	{
		if ( fmtstring == null || fmtstring.trim().length() == 0 )
		{
			return "";
		}
		
		if (value > 0.0)
			return fmtNum( value.toString(), 1, fmtstring, locText );
		else if (value < 0.0)
			return fmtNum( value.toString(), -1, fmtstring, locText );
		return fmtNum( value.toString(), 0, fmtstring, locText );
	}
	
	public static String fmtNum( Double value, String fmtstring, LocalizedText locText )
	{
		if ( fmtstring == null || fmtstring.trim().length() == 0 )
		{
			return "";
		}
		
		if (value > 0.0)
			return fmtNum( value.toString(), 1, fmtstring, locText );
		else if (value < 0.0)
			return fmtNum( value.toString(), -1, fmtstring, locText );
		return fmtNum( value.toString(), 0, fmtstring, locText );
	}
	
	private static String fmtNum( String valueString, int signum, String fmtstring,
			LocalizedText locText )
	{
		// We don't want the first digit to be a negative sign or a zero.
		if ( valueString.charAt( 0 ) == '-' )
		{
			valueString = valueString.substring( 1 ); 
		}
		if ( valueString.charAt( 0 ) == '0' )
		{
			valueString = valueString.substring( 1 ); 
		}

		// The default FMTSIZE is assumed to be 80 		
		int FMTSIZE = 80;
		if(valueString.length() > FMTSIZE)
		{
			// The reason for this fix is in some cases a number
			// could be represented with an exponent 3e89
			// The to string representation in EGL returns a string
			// that could be more than the default size, and the format
			// specifier could include decimal point #.##. 
			// To accomodate for everything the fmtsize needs to be
			// size of string representation + extra digits after decimal.
			FMTSIZE = valueString.length();
			int decpos = fmtstring.indexOf('.');
			int stlen = fmtstring.length();
			int ndecchars = stlen - decpos;
			FMTSIZE += ndecchars;
		}
		
		
		int AREASIZE = FMTSIZE + 2;

		/*
		 * set the first and last space in formatter to 0, copy the fmtstring
		 * starting at the second space
		 */
		char[] formatter = new char[ AREASIZE ];
		fmtstring.getChars( 0, Math.min( FMTSIZE, fmtstring.length() ), 
				formatter, 1 );

		/*
		 * Set p_frmt to point to one char beyond dec point. This decimal point
		 * is from the format string, thus is always represented by a period
		 * rather than locale dependent.
		 */
		char[] p_frmt = formatter;
		int p_frmtIndex = 1;
		int firstDecCharLocation = -1;
		for (; p_frmt[p_frmtIndex] != 0; p_frmtIndex++) {
			if (p_frmt[p_frmtIndex] == '.') 
			{
				p_frmtIndex++;
				firstDecCharLocation = p_frmtIndex;
				break;
			}
		}

		/*
		 * p_frmt should now point to one character beyond the decimal point in
		 * the format string Loop forward from p_frmt and set deccnt accordingly
		 */

		/*
		 * Loop forward from p_frmt and set deccnt accordingly
		 */
		int deccnt = 0;
		for (; p_frmt[p_frmtIndex] != 0; p_frmtIndex++) {
			switch (p_frmt[p_frmtIndex]) {
			/*
			 * Since p_frmt starts right after the decimal point and there can
			 * only be one decimal point in the format string, we should not
			 * encounter any more '.'.
			 */
			case '$':
			case '#':
			case '&':
			case '*':
			case '(':
			case '+':
			case '-':
			case '<':
				deccnt++;
			}
		}

		/*
		 * get string of text digits (without decimal point) from value
		 */ 
		char[] dgtstr = new char[ AREASIZE ];
		int decpt_pos = deccvt(valueString, signum, deccnt, dgtstr);

		CCharArray eedecpoint = new CCharArray(AREASIZE);
		eedecpoint.ccharArrayIndex = 1 + decpt_pos;
		for ( int i = 0; dgtstr[ i ] != 0; i++ )
		{
			eedecpoint.ccharArray[ i + 1 ] = dgtstr[ i ];
		}

		CBoolean useMonetary = new CBoolean( false );
		int rightmost = fmtanalysis( formatter, useMonetary );

		/*
		 * leaderprinted is FALSE iff a leading currency symbol needs to be
		 * printed, but hasn't yet been printed
		 */
		CBoolean leaderprinted = new CBoolean(true);

		/*
		 * trailerprinted is FALSE iff a trailing currency symbol needs to be
		 * printed, but hasn't yet been printed
		 */
		CBoolean trailerprinted = new CBoolean(true);
		CBoolean parprinted = new CBoolean(true), minprinted = new CBoolean(
				true), plusprinted = new CBoolean(true);

		CInteger sym_index = new CInteger(0);

		char[] signa = new char[ 8 ];

		chksigns(formatter, sym_index, parprinted, minprinted, leaderprinted,
				trailerprinted, plusprinted, signa);

		/* load leading signs into signa array */
		CCharArray p_source = new CCharArray();
		p_frmtIndex = align(firstDecCharLocation - 1, p_source, eedecpoint, p_frmt, p_frmtIndex, rightmost);

		/* go right to left in format */
		char[] prstack = new char[AREASIZE];

		char lastchar = 0;

		CInteger stack_index = new CInteger(0);
		char currentChar = p_frmt[p_frmtIndex];
		boolean isNegative = signum == -1;
		for (; currentChar != 0; currentChar = p_frmt[--p_frmtIndex]) 
		{			
			if ( currentChar == ',' && p_source.ccharArray[p_source.ccharArrayIndex] == 0 )
			{
				p_frmt[p_frmtIndex] = lastchar; /* just fill leading commas */
				currentChar = lastchar;
			}

			switch ( currentChar ) 
			{
			case ',':
				prstack[stack_index.value++] = locText.getSeparatorSymbol();
				break;
			case '.':
				char decimalSeparator = 
					useMonetary.value ? locText.getMonetaryDecimalSeparator() : locText.getDecimalSymbol();
				prstack[stack_index.value++] = decimalSeparator;
				break;
			case '<':
				leadingLT(p_source, prstack, stack_index);
				break;
			case '*':
				leadingNotLT(p_source, prstack, stack_index, '*');
				break;
			case '&':
				leadingNotLT(p_source, prstack, stack_index, '0');
				break;
			case '#':
				leadingNotLT(p_source, prstack, stack_index, ' ');
				break;
			case '-':
				prsigns(minprinted.value, p_source, prstack, sym_index,
						stack_index, signa, minprinted, plusprinted,
						parprinted, leaderprinted, trailerprinted, isNegative,
						locText);
				break;
			case '+':
				prsigns(plusprinted.value, p_source, prstack, sym_index,
						stack_index, signa, minprinted, plusprinted,
						parprinted, leaderprinted, trailerprinted, isNegative,
						locText);
				break;
			case '(':
				prsigns(parprinted.value, p_source, prstack, sym_index,
						stack_index, signa, minprinted, plusprinted,
						parprinted, leaderprinted, trailerprinted, isNegative,
						locText);
				break;
			case '$':
				prsigns(leaderprinted.value, p_source, prstack, sym_index,
						stack_index, signa, minprinted, plusprinted,
						parprinted, leaderprinted, trailerprinted, isNegative,
						locText);
				break;
			case '@':
				prsigns(trailerprinted.value, p_source, prstack,
						sym_index, stack_index, signa, minprinted, plusprinted,
						parprinted, leaderprinted, trailerprinted, isNegative,
						locText);
				break;
			case ')':
				prstack[stack_index.value++] = isNegative ? ')' : ' ';
				break;
			default:
				prstack[stack_index.value++] = currentChar;
				break;
			} /* end of switch */
			lastchar = currentChar;
		}

		char[] result = new char[ AREASIZE ];
		int len = 
			rpfs(result, prstack, stack_index.value - 1, trailerprinted, leaderprinted,
				minprinted, plusprinted, parprinted, p_source);

		return new String( result, 0, len );
	}
	
	/*
	 * Function: deccvt
	 * 
	 * Parameters:
	 * np - pointer to a decimal structure containing the DECIMAL value
	 * to be converted.
	 * ndigit - number of digits to the right of decimal point
	 * decpt - pointer to an integer that is the position of the decimal point
	 * relative to the beginning of the string.
	 * decstr - returned decimal string buffer.
	 * decstrlen - length of decimal string buffer.
	 */
	private static int deccvt(String number, int signum, int ndigit, char[] decstr)
	{
		int decpt = 0;
		CharSequence seq;

		int index = number.indexOf('.');
		if (index == -1) {
			StringBuilder buf = new StringBuilder( number.length() + 1 + ndigit );
			buf.append( number );
			buf.append( '.' );
			
			while ( ndigit >= 50 )
			{
				buf.append( Constants.STRING_50_ZEROS );
				ndigit -= 50;
			}

			if ( ndigit > 0 )
			{
				buf.append( Constants.STRING_50_ZEROS.substring( 0, ndigit ) );
			}
			seq = buf;
		} else {
			if (ndigit == 0)				
				seq = number.substring( 0, index );
			else {
				// See if we have too few or too many decimal digits in the string.
				int decDigitsInNumber = number.length() - index - 1;
				if ( decDigitsInNumber == ndigit )
				{
					seq = number;
				}
				else if ( decDigitsInNumber < ndigit )
				{
					int zerosNeeded = ndigit - decDigitsInNumber;
					StringBuilder buf = new StringBuilder( number.length() + zerosNeeded );
					buf.append( number );

					while ( zerosNeeded >= 50 )
					{
						buf.append( Constants.STRING_50_ZEROS );
						zerosNeeded -= 50;
					}

					if ( zerosNeeded > 0 )
					{
						buf.append( Constants.STRING_50_ZEROS.substring( 0, zerosNeeded ) );
					}
					seq = buf;
				}
				else
				{
					seq = number.substring( 0, number.length() - (decDigitsInNumber - ndigit) );
				}
			}
		}
		
		/*
		 * calculate the correct position of the decimal point. handle the value
		 * 0 differently (since the routine returns empty buffer for this
		 * value).
		 */
		if (signum != 0)
		{
			int length = seq.length();
			decpt = length;
			for (int j = 0, i = 0; i < length; i++) {
				char c = seq.charAt(i);
				if (c == '.')
					decpt = i;
				else
					decstr[j++] = c;
			}
		}
		return decpt;
	}
	
	/*
	 *
	 */
	private static int fmtanalysis( char[] formatter, CBoolean useMonetary ) 
	{
		int i = 1;
		while ( true )
		{
			switch ( formatter[ i ] )
			{
				case 0:
					return i;
					
				case '$':
				case '@':
					useMonetary.value = true;
					break;
			}
			i++;
		}
	}
	
	/*
	 * chksigns function: Set value of the following global variables according
	 * to the format parprinted minprinted leaderprinted trailerprinted
	 * plusprinted The idea here is that if the format tells us we are to print
	 * a symbol then we try to make sure that we have done so by setting the
	 * appropriate flag to FALSE. During calls to prsi, the appropriate flags
	 * are set to TRUE. Finally, during the call to rpfs, we check each of these
	 * four flags are checked and if any are FALSE, then the outgoing string is
	 * filled with *'s (indicating error.)
	 */
	private static void chksigns(char[] formatter, CInteger sym_index,
			CBoolean parprinted, CBoolean minprinted, CBoolean leaderprinted,
			CBoolean trailerprinted, CBoolean plusprinted, char[] signa) {
		
		char c = formatter[ 1 ];
		for ( int i = 2; c != 0; i++ )
		{
			switch ( c ) {
			case '(':
				ldsigna('(', sym_index, signa);
				parprinted.value = false;
				break;
			case '-':
				ldsigna('-', sym_index, signa);
				minprinted.value = false;
				break;
			case '$':
				ldsigna('$', sym_index, signa);
				leaderprinted.value = false;
				break;
			case '@':
				ldsigna('@', sym_index, signa);
				trailerprinted.value = false;
				break;
			case '+':
				ldsigna('+', sym_index, signa);
				plusprinted.value = false;
				break;
			}
			c = formatter[ i ];
		}
	}
	
	private static int align(int decpoint_location, CCharArray p_source,
			CCharArray eedecpoint, char[] p_frmt, int p_frmtIndex, int rightmost) {
		/*
		 * If we found a decimal point in the format...
		 */
		if (decpoint_location >= 0) {
			/* align using decimal points */
			p_source.ccharArray = eedecpoint.ccharArray;
			p_source.ccharArrayIndex = eedecpoint.ccharArrayIndex;
			for (p_frmtIndex = decpoint_location + 1; 
					p_frmt[p_frmtIndex] != 0; p_frmtIndex++) 
			{
				if (p_source.ccharArray[p_source.ccharArrayIndex] == 0) {
					p_source.ccharArray[p_source.ccharArrayIndex] = '0';
				}
				switch (p_frmt[p_frmtIndex]) {
				case '*':
				case '$':
				case '#':
				case '&':
				case '(':
				case '+':
				case '-':
				case '<':
					p_source.ccharArrayIndex++;
				}
			}
		} else {
			/*
			 * align using first character to the left of the decimal
			 */
			p_frmtIndex = rightmost;
			p_source.ccharArray = eedecpoint.ccharArray;
			p_source.ccharArrayIndex = eedecpoint.ccharArrayIndex;
		}
		p_source.ccharArrayIndex--;
		return p_frmtIndex - 1;
	}
	
	/*
	 * leading function: Call pr to fill in a symbol. If p_source points to the
	 * end of a string, then we call pr on the fillchar symbol (unless the
	 * symbol is a ' <', which means "left justify"). If p_source does not point
	 * to the end of a string, we simply yank another character off of p_source
	 * and call pr on that.
	 */

	private static void leadingNotLT(CCharArray p_source, 
			char[] prstack, CInteger stack_index, char fchar) {
		if (p_source.ccharArray[p_source.ccharArrayIndex] == 0) {
			prstack[stack_index.value++] = fchar;
		} else {
			prstack[stack_index.value++] = p_source.ccharArray[p_source.ccharArrayIndex--];
		}
	}
	
	private static void leadingLT(CCharArray p_source, 
			char[] prstack, CInteger stack_index) {
		if (p_source.ccharArray[p_source.ccharArrayIndex] != 0) 
		{
			prstack[stack_index.value++] = p_source.ccharArray[p_source.ccharArrayIndex--];
		}
	}

	/*
	 * prsigns function: Make calls to prsi or pr, depending upon the value
	 * pointed to by p_source. If p_source does not point to the end of string,
	 * then we simply call pr(*p_source--). Otherwise, we (conditionally) call
	 * prsi on a character from the signa array.
	 */

	private static void prsigns(boolean printed, CCharArray p_source,
			char[] prstack, CInteger sym_index, CInteger stack_index,
			char[] signa, CBoolean minprinted, CBoolean plusprinted,
			CBoolean parprinted, CBoolean leaderprinted,
			CBoolean trailerprinted, boolean isNegative, LocalizedText locText) {
		if (p_source.ccharArray[p_source.ccharArrayIndex] == 0) {
			if (!printed) /* character has not yet been used */
			{
				/*
				 * print according to the character on top of signa
				 */
				sym_index.value--;
				switch (signa[sym_index.value]) {
				case '-':
					prsi(prstack, stack_index, minprinted, '-', ' ', isNegative,
							' ', locText );
					break;
				case '+':
					prsi(prstack, stack_index, plusprinted, '-', '+', isNegative,
							' ', locText);
					break;
				case '(':
					prsi(prstack, stack_index, parprinted, '(', ' ', isNegative,
							' ', locText);
					break;
				case '$':
					prsi(prstack, stack_index, leaderprinted, '$', '$',
							isNegative, ' ', locText);
					break;
				case '@':
					prsi(prstack, stack_index, trailerprinted, '@', '@',
							isNegative, ' ', locText);
					break;
				}
			} else {
				// character has already been used
				prstack[stack_index.value++] = ' ';
			}
		} else /* ...there are symbols yet to be pushed onto the stack */{
			/*
			 * If the symbol in question has yet to be printed
			 */
			if (!printed)
				if (signa[sym_index.value - 1] == '@') /*
																	    * ..and
																	    * if a
																	    * trailing
																	    * cur.
																	    * sym.
																	    */
				{
					/*
					 * Call prsi on it
					 */
					sym_index.value--;
					prsi(prstack, stack_index, trailerprinted, '@', '@',
							isNegative, ' ', locText);
					/* use it */
				} else
					/* Otherwise, just push the symbol */
					prstack[stack_index.value++] = p_source.ccharArray[p_source.ccharArrayIndex--];
		}
	}
	
	/*
	 * rpfs function: Pop each character off of prstack, and place it in cp. If
	 * any of the flags leaderprinted, minprinted, plusprinted, or parprinted
	 * are FALSE, this indicates that they should have been printed, but were
	 * not (probably because there wasn't enough room for them in the format).
	 */
	private static int rpfs(char[] cp, char[] prstack, int prIndex,
			CBoolean trailerprinted, CBoolean leaderprinted,
			CBoolean minprinted, CBoolean plusprinted, CBoolean parprinted,
			CCharArray p_source)
	{
		int cpIndex = 0;
		if (trailerprinted.value && leaderprinted.value
				&& minprinted.value && plusprinted.value
				&& parprinted.value && p_source.ccharArray[p_source.ccharArrayIndex] == 0) 
		{
			for ( ; prIndex >= 0; cpIndex++, prIndex-- )
			{
				if ( prstack[ prIndex ] == 0 )
				{
					return cpIndex;
				}
				cp[ cpIndex ] = prstack[ prIndex ];
			}
		} 
		else 
		{
			for ( ; prIndex >= 0; prIndex-- )
			{
				cp[ cpIndex++ ] = '*';
			}
		}
		return cpIndex;
	}
	
	/*
	 * prsi function: Check to see if the symbol has been printed. If not, make
	 * a call to pr, and set the "printed" flag to TRUE. The call made to pr is
	 * of the form: pr(pr_sign ? neg: pos); ...unless pr_sign is a '$', in which
	 * case we use monfront (see documentation for chksigns)
	 */
	private static void prsi(char[] prstack, CInteger stack_index,
			CBoolean printed, char neg, char pos, boolean isNegative,
			char fillchar, LocalizedText locText ) {
		if (!printed.value) {
			if (neg == '$') /*
							 * push money prefix onto prstack
							 */
			{
				String currencySymbol = locText.getCurrencySymbol();
				int len = currencySymbol.length();
				/*
				 * If there is no leading currency symbol, print a space
				 */
				if ( len == 1 )
				{
					prstack[stack_index.value++] = currencySymbol.charAt( 0 );
				}
				else if ( len == 0 )
				{
					prstack[stack_index.value++] = ' ';
				}
				else {
					while (len > 0)
						prstack[stack_index.value++] = currencySymbol.charAt(--len);
				}
			} else if (neg == '@') {
				String currencySymbol = locText.getCurrencySymbol();
				int len = currencySymbol.length();
				/*
				 * If there is no trailing currency symbol, print a space
				 */
				if ( len == 1 )
				{
					pl(prstack, stack_index, currencySymbol.charAt( 0 ));
				}
				else if ( len == 0 )
				{
					pl(prstack, stack_index, ' ');
				}
				else 
				{
					for (int i = 0; i < len; i++)
						pl(prstack, stack_index, currencySymbol.charAt(i));
				}
			} else
				prstack[stack_index.value++] = isNegative ? neg : pos;
			printed.value = true;
		} else {
			if (fillchar != '<')
				prstack[stack_index.value++] = fillchar;
		}
	}
	
	/*
	 * pl function: Push a character onto the prstack. Eventually these will get
	 * popped off by a call to rpfs.
	 */
	private static void pl(char[] prstack, CInteger stack_index, char c) {
		int index = stack_index.value;
		while (index > 0)
			prstack[index] = prstack[--index];
		prstack[index] = c;
		stack_index.value++;
	}
	
	/*
	 * ldsigna function: Load c into the signa array (if it has not already been
	 * loaded)
	 */
	private static void ldsigna(char c, CInteger sym_index, char[] signa) {

		if ( sym_index.value == 0 || c != signa[ sym_index.value - 1 ] )
		{
			signa[ sym_index.value++ ] = c;
		}
	}
	
	static class CBoolean 
	{
		boolean value;

		public CBoolean( boolean value )
		{
			this.value = value;
		}
	}

	static class CCharArray 
	{
		char ccharArray[];

		int ccharArrayIndex;

		public CCharArray() {
		}

		public CCharArray(int ccharArraySize) {
			ccharArray = new char[ccharArraySize];
			ccharArrayIndex = 0;
		}
	}

	static class CInteger 
	{
		int value;

		public CInteger( int value ) 
		{
			this.value = value;
		}
	}
}
