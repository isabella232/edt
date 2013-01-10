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

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;

import org.eclipse.edt.javart.Constants;

import com.ibm.icu.impl.Utility;
import com.ibm.icu.text.DateFormatSymbols;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;

/**
 * A simple date formatter.
 */
public class JavartSimpleDateFormat extends SimpleDateFormat 
	implements JavartDateFormat
{
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/**
	 * The number of microseconds.
	 */
    private int microsecond;

    /**
     * The century.
     */
    private int century;

    public JavartSimpleDateFormat( String pattern, DateFormatSymbols formatData, ULocale loc )
    {
        super( pattern, loc );
        setDateFormatSymbols( formatData );
    }

    /**
     * @return Returns the century.
     */
    public int getCentury()
    {
        return century;
    }

    /**
     * @param century
     *            The century to set.
     */
    public void setCentury( int century )
    {
        this.century = century;
    }

    /**
     * @return Returns the microsecond.
     */
    public int getMicrosecond()
    {
        return microsecond;
    }

    /**
     * @param microsecond
     *            The microsecond to set.
     */
    public void setMicrosecond( int microsecond )
    {
        this.microsecond = microsecond;
    }

    /**
     * Parse a date/time string.
     *
     * @param text  the date/time string to be parsed
     *
     * @return a Date, or null if the input could not be parsed
     * @exception ParseException if the given string cannot be parsed as a date.
     */
	public Date parse( String text ) throws ParseException
	{
		ParsePosition pos = new ParsePosition( 0 );
		Date result = parse( text, pos );
		if ( pos.getIndex() == 0 )
		{
			throw new ParseException( "ParseException: \"" + text + "\"", 
					pos.getErrorIndex() );
		}
		else if ( pos.getIndex() != text.length() )
		{
			throw new ParseException( "ParseException: \"" + text + "\"", 
					pos.getIndex() );
		}
		return result;
	}

    /**
	 * Converts one field of the input string into a numeric field value in
	 * <code>cal</code>. Returns -start (for ParsePosition) if failed.
	 * 
	 * @param text
	 *            the time text to be parsed.
	 * @param start
	 *            where to start parsing.
	 * @param ch
	 *            the pattern character for the date field text to be parsed.
	 * @param count
	 *            the count of a pattern character.
	 * @param obeyCount
	 *            if true, then the next field directly abuts this one, and we
	 *            should use the count to know when to stop parsing.
	 * @param ambiguousYear
	 *            return parameter; upon return, if ambiguousYear[0] is true,
	 *            then a two-digit year was parsed and may need to be
	 *            readjusted.
	 * @return the new start position if matching succeeded; a negative number
	 *         indicating matching failure, otherwise. As a side effect, set the
	 *         appropriate field of <code>cal</code> with the parsed value.
	 */
    protected int subParse( String text, int start, char ch, int count,
			boolean obeyCount, boolean allowNegative, boolean[] ambiguousYear,
			Calendar cal )
	{
    	// We only need to treat C and S/f specially.  Everything else is handled
    	// by the superclass.
		if ( ch != 'C' && ch != 'S' && ch != 'f' )
		{
			return super.subParse( text, start, ch, count, obeyCount, allowNegative,
					ambiguousYear, cal );
		}

		// Find the starting point.
		start = Utility.skipWhitespace( text, start );
		ParsePosition pos = new ParsePosition( start );

		// Parse the number.
		Number number = null;
		if ( obeyCount )
		{
			if ( start + count > text.length() )
			{
				return -start;
			}
			number = numberFormat.parse( text.substring( 0, start + count ), pos );
		}
		else
		{
			number = numberFormat.parse( text, pos );
		}
		if ( number == null )
		{
			return -start;
		}

		// Store the number.
		int value = number.intValue();
		if ( ch == 'C' )
		{
			// It's C for Century.
			setCentury( value );
		}
		else
		{
			// It's S/f for microsecond.  We want it as a six-digit number.
			int i = pos.getIndex() - start;
			if ( i < 6 )
			{
				value *= 10;
				i++;
				while ( i < 6 )
				{
					value *= 10;
					i++;
				}
			}
			else if ( i > 6 )
			{
				int a = 10;
				i--;
				while ( i > 6 )
				{
					a *= 10;
					i--;
				}
				value = (value + (a >> 1)) / a;
			}
			setMicrosecond( value );
		}
		return pos.getIndex();
	}

    /**
     * Format a single field; useFastFormat variant.  Reuses a
     * StringBuffer for results instead of creating a String on the
     * heap for each call.
     */
    protected void subFormat( StringBuffer buf, char ch, int count, int beginOffset,
			FieldPosition pos, Calendar cal )
	{
		switch ( ch )
		{
			case 'M':
				// To get nice output and be compatible with v6, ignore any M's beyond the fourth.
				super.subFormat( buf, ch, Math.min( count, 4 ), beginOffset, pos, cal );
				break;
				
			case 'C':
				// It's C for Century.
				buf.append( zeroPaddingNumber( getCentury(), count, Integer.MAX_VALUE ) );
				break;
				
			case 'S':
			case 'f':
				// It's S/f for microsecond.  We want it as a six-digit number.
				int value = getMicrosecond();
				switch ( count )
				{
					case 1:
						value = (value + 50000) / 100000;
						break;
					case 2:
						value = (value + 5000) / 10000;
						break;
					case 3:
						value = (value + 500) / 1000;
						break;
					case 4:
						value = (value + 50) / 100;
						break;
					case 5:
						value = (value + 5) / 10;
						break;
				}
				FieldPosition p = new FieldPosition( -1 );
				numberFormat.setMinimumIntegerDigits( Math.min( 6, count ) );
				numberFormat.setMaximumIntegerDigits( Integer.MAX_VALUE );
				numberFormat.format( value, buf, p );
				if ( count > 6 )
				{
					numberFormat.setMinimumIntegerDigits( count - 6 );
					numberFormat.format( 0L, buf, p );
				}
				break;
				
			default:
				super.subFormat( buf, ch, count, beginOffset, pos, cal );
				break;
		}
	}
}
