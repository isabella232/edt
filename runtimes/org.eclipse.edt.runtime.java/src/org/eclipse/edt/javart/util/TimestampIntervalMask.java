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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.runtime.java.eglx.lang.ETimestamp;

import eglx.lang.InvalidArgumentException;

/**
 * Helper class used to calculate arguments to be passed when instantiating
 * timestamp and interval types.
 */
public class TimestampIntervalMask implements Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	/**
	 * The pattern associated with a timestamp or interval.
	 */
	private String pattern = "";
	private transient String lowerCasePattern = pattern;
	
	public TimestampIntervalMask(String pattern) 
	{
		if ( pattern != null )
		{
			pattern = pattern.trim();
			if (pattern.length() > 0) {
				// make this big enough so that repeated characters don't cause an overflow in the array
				int[] flags = new int[100];
				int index = 0;
				for ( int i = 0; i < pattern.length(); i++ )
				{
					switch ( pattern.charAt( i ) )
					{
						case 'y':
						case 'Y':
							flags[index++] = ETimestamp.YEAR_CODE;
							break;
						case 'm':
							flags[index++] = ETimestamp.MINUTE_CODE;
							break;
						case 'M':
							flags[index++] = ETimestamp.MONTH_CODE;
							break;
						case 'd':
						case 'D':
							flags[index++] = ETimestamp.DAY_CODE;
							break;
						case 'h':
						case 'H':
							flags[index++] = ETimestamp.HOUR_CODE;
							break;
						case 's':
						case 'S':
							flags[index++] = ETimestamp.SECOND_CODE;
							break;
						case 'f':
						case 'F':
							flags[index++] = ETimestamp.FRACTION1_CODE;
							break;
						default:
							InvalidArgumentException ex = new InvalidArgumentException();
							throw ex.fillInMessage(Message.INVALID_DATA, "yyyyMMddhhmmssffffff");
					}
				}
				// make sure that from the start of the flag array to endIndex, the values are >= the current one
				int endIndex = index - 1;
				for (index = 0; index < endIndex; index++) {
					// if the next value is the same or exactly 1 larger, then pattern is still ok
					if (!(flags[index] == flags[index + 1] || (flags[index] + 1) == flags[index + 1])) {
						InvalidArgumentException ex = new InvalidArgumentException();
						throw ex.fillInMessage(Message.INVALID_DATA, "yyyyMMddhhmmssffffff");
					}
				}
				// copy the valid pattern
				this.pattern = pattern;
				// Before we make the lowerCasePattern, replace 'S' with 'f' so we can
				// tell the difference between 'S' and 's'.  They'll both be 's' after
				// conversion to lower case.
				this.lowerCasePattern = pattern.replace( 'S', 'f' ).toLowerCase();
			}
		}
	}
	
	/**
	 * Get the start code.
	 */
	public int getStartCode()
	{
		if ( lowerCasePattern.length() > 0 )
		{
			switch ( lowerCasePattern.charAt( 0 ) )
			{
				case 'y':
					return ETimestamp.YEAR_CODE;
				case 'm':
					return isMonth( true ) ? ETimestamp.MONTH_CODE : ETimestamp.MINUTE_CODE;
				case 'd':
					return ETimestamp.DAY_CODE;
				case 'h':
					return ETimestamp.HOUR_CODE;
				case 's':
					return ETimestamp.SECOND_CODE; 
				case 'f':
					switch( numStartChars() )
					{
			            case 1:
			            	return ETimestamp.FRACTION1_CODE;
			            case 2:
			            	return ETimestamp.FRACTION2_CODE;
			            case 3:
			            	return ETimestamp.FRACTION3_CODE;
			            case 4:
			            	return ETimestamp.FRACTION4_CODE;
			            case 5:
			            	return ETimestamp.FRACTION5_CODE;
			            case 6:
			            	return ETimestamp.FRACTION6_CODE;
		            }
			}
		}
		
		return -1;
	}
	
	/**
	 * get the end code.
	 */
	public int getEndCode()
	{
		if ( lowerCasePattern.length() > 0 )
		{
			switch ( lowerCasePattern.charAt( lowerCasePattern.length() - 1 ) )
			{
				case 'y':
					return ETimestamp.YEAR_CODE;
				case 'm':
					return isMonth( false ) ? ETimestamp.MONTH_CODE : ETimestamp.MINUTE_CODE;
				case 'd':
					return ETimestamp.DAY_CODE;
				case 'h':
					return ETimestamp.HOUR_CODE;
				case 's':
					return ETimestamp.SECOND_CODE; 
				case 'f':
					switch( numEndChars() )
					{
			            case 1:
			            	return ETimestamp.FRACTION1_CODE;
			            case 2:
			            	return ETimestamp.FRACTION2_CODE;
			            case 3:
			            	return ETimestamp.FRACTION3_CODE;
			            case 4:
			            	return ETimestamp.FRACTION4_CODE;
			            case 5:
			            	return ETimestamp.FRACTION5_CODE;
			            case 6:
			            	return ETimestamp.FRACTION6_CODE;
		            }
			}
		}
		
		return -1;
	}
	
	/**
	 * Check if 'm' represents a month or not.
	 */
	private boolean isMonth( boolean isStartCode )
	{
		int count;
        if( isStartCode )
        {
            count = numStartChars();
            if( lowerCasePattern.length() == count )
            {
            	// The pattern contains only 'm's, decide based on the case of first 'm'
            	if( pattern.charAt(0) == 'M' )
            		return true;
            	else
            		return false;
            }
            else
            {
                return ( lowerCasePattern.charAt(count) == 'd' );
            }
        }
        else
        {
            count = numEndChars();
            if( lowerCasePattern.length() == count )
            {
            	// The pattern contains only 'm's, decide based on the case of first 'm'.
            	if( pattern.charAt(0) == 'M' )
            		return true;
            	else
            		return false;
            }
            else
            {
                return( lowerCasePattern.charAt(lowerCasePattern.length() - count - 1) == 'y' );
            }
        }
	}
	
	/**
	 * Number of start characters
	 */
	private int numStartChars()
	{
        int count = 1;
        char prevCh = lowerCasePattern.charAt(0);
        for( int i = 1; i < lowerCasePattern.length(); i++ )
        {
            if( lowerCasePattern.charAt(i) == prevCh )
            {
                prevCh = lowerCasePattern.charAt(i);
                count++;
            }
            else
            {
                break;
            }
        }
        return count;
    }
	
	/**
	 * Number of end characters
	 */
	private int numEndChars()
	{
        int count = 1;
        char prevCh = lowerCasePattern.charAt( lowerCasePattern.length() - 1 );
        for( int i = lowerCasePattern.length() - 2; i >= 0; i-- )
        {
            if (lowerCasePattern.charAt(i) == prevCh)
            {
                prevCh = lowerCasePattern.charAt(i);
                count++;
            }
            else
            {
                break;
            }
        }
        return count;
    }
	
	/**
	 * Get the formatting string. 
	 */
	public String getFormattingPattern( int startCode, int endCode )
	{
		StringBuilder buf = new StringBuilder();
		
		int idx = 0;
		int length = lowerCasePattern.length();

		switch( startCode )
		{
			case ETimestamp.YEAR_CODE:
				for( ; idx < length && lowerCasePattern.charAt( idx ) == 'y'; idx++ )
				{
					buf.append('y');
				}
				if( endCode == ETimestamp.YEAR_CODE )
					break;

			case ETimestamp.MONTH_CODE:
				if( startCode != ETimestamp.MONTH_CODE )
				{
					buf.append('-');
				}
				for( ; idx < length && lowerCasePattern.charAt( idx ) == 'm'; idx++ )
				{
					buf.append('M');
				}
				if( endCode == ETimestamp.MONTH_CODE )
					break;

			case ETimestamp.DAY_CODE:
				if( startCode != ETimestamp.DAY_CODE )
				{
					buf.append('-');
				}
				for( ; idx < length && lowerCasePattern.charAt( idx ) == 'd'; idx++ )
				{
					buf.append('d');
				}
				if( endCode == ETimestamp.DAY_CODE )
					break;

			case ETimestamp.HOUR_CODE:
				if( startCode != ETimestamp.HOUR_CODE )
				{
					buf.append(' ');
				}
				for( ; idx < length && lowerCasePattern.charAt( idx ) == 'h'; idx++ )
				{
					buf.append('H');
				}
				if( endCode == ETimestamp.HOUR_CODE )
					break;

			case ETimestamp.MINUTE_CODE:
				if( startCode != ETimestamp.MINUTE_CODE )
				{
					buf.append(':');
				}
				for( ; idx < length && lowerCasePattern.charAt( idx ) == 'm'; idx++ )
				{
					buf.append('m');
				}
				if( endCode == ETimestamp.MINUTE_CODE )
					break;

			case ETimestamp.SECOND_CODE:
				if( startCode != ETimestamp.SECOND_CODE )
				{
					buf.append(':');
				}
				for( ; idx < length && lowerCasePattern.charAt( idx ) == 's'; idx++ )
				{
					buf.append('s');
				}
				if( endCode == ETimestamp.SECOND_CODE )
					break;

			case ETimestamp.FRACTION1_CODE:
			case ETimestamp.FRACTION2_CODE:
			case ETimestamp.FRACTION3_CODE:
			case ETimestamp.FRACTION4_CODE:
			case ETimestamp.FRACTION5_CODE:
			case ETimestamp.FRACTION6_CODE:
				if( startCode <= ETimestamp.SECOND_CODE )
				{
					buf.append('.');
				}
				for( int i = 0; i < endCode - ETimestamp.SECOND_CODE; i++ )
				{
					buf.append('S');
				}
		}
		return buf.toString();
	}
	
	/**
	 * Return length of the mask.
	 */
	public int getMaskLength()
	{
		return pattern.length();
	}
	
	/**
	 * Generate the number of y's in the mask.
	 */
	public int getYearDigits()
	{
		return getDigits('y');
	}
	
	/**
	 * Generate the number of M's in the mask.
	 */
	public int getMonthDigits()
	{
		return getDigits('m');
	}
	
	/**
	 * Generate the number of d's in the mask.
	 */
	public int getDayDigits()
	{
		return getDigits('d');
	}
	
	/**
	 * Generate the number of h's in the mask.
	 */
	public int getHourDigits()
	{
		return getDigits('h');
	}
	
	/**
	 * Generate the number of m's in the mask.
	 */
	public int getMinuteDigits()
	{
		return getDigits('m');
	}
	
	/**
	 * Generate the number of s's in the mask.
	 */
	public int getSecondDigits()
	{
		return getDigits('s');
	}
	
	/**
	 * Generate the number of f's in the mask.
	 */
	public int getFractionDigits()
	{
		return getDigits('f');
	}
	
	private int getDigits(char type)
	{
		int first = lowerCasePattern.indexOf(type);
		if (first >= 0)
		{
			int last = lowerCasePattern.lastIndexOf(type);
			return (last - first + 1);
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * The maxValue is the maximum number of months that an interval(month) can store.
	 */
	public long getMaxMonthValue()
	{
		long maxValue = 0;
		int firstY = lowerCasePattern.indexOf('y');
		int firstM = lowerCasePattern.indexOf('m');
		if (firstM >= 0)
		{
			// Pattern contains y's and M's
			if (firstY >= 0)
			{
				maxValue = 11;
			}
			else
			// Pattern only contains M's
			{
				int lastM = lowerCasePattern.lastIndexOf('m');
				maxValue = maxValue(lastM - firstM + 1);
			}
		}
		
		if (firstY >= 0)
		{	
			int lastY = lowerCasePattern.lastIndexOf('y');
			maxValue += maxValue(lastY - firstY + 1) * 12;
		}
		
		return maxValue;
	}
	
	/**
	 * The maxValue is the maximum number of seconds that an interval(second)
	 * can store before overflowing.
	 */
	public long getMaxSecondValue()
	{
		long maxValue = 0;
		int multiplier;
		char firstChar = lowerCasePattern.charAt(0);
		
		switch (firstChar)
		{
			case 'd': multiplier = DateTimeUtil.SECONDS_PER_DAY;
				break;
			case 'h': multiplier = 3600;
				break;
			case 'm': multiplier = 60;
				break;
			case 's': multiplier = 1;
				break;
			default: multiplier = 0;
				break;
		}
		
		maxValue = (maxValue(lowerCasePattern.lastIndexOf(firstChar) + 1) + 1)
				* multiplier - 1;
		
		return maxValue;
	}
	
	/**
	 * Calculate the maximum value that can be stored in a certain
	 * number of digits.  Each digit has a value of 9.
	 * @param digits	Number of digits
	 * @return			Maximum value 
	 */
	private long maxValue(int digits)
	{
		switch( digits )
		{
			case 1: return 9;
			case 2: return 99;
			case 3: return 999;
			case 4: return 9999;
			case 5: return 99999;
			case 6: return 999999;
			case 7: return 9999999;
			case 8: return 99999999;
			case 9: return 999999999;
			default: return 0;
		}
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
		lowerCasePattern = (pattern == null ? "" : pattern.toLowerCase());
	}
}
