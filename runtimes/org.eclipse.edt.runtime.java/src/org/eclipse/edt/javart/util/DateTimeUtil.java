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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.DateFormatSymbols;
import com.ibm.icu.util.*;

/**
 * This class has constants and utility methods related to date/time values.
 * 
 * @author mheitz
 */
public class DateTimeUtil
{
	/**
	 * The value of TimeZone.getDefault().
	 */
	public static final java.util.TimeZone DEFAULT_TIME_ZONE = java.util.TimeZone.getDefault();
	
	/**
	 * The ICU4J version of DEFAULT_TIME_ZONE.
	 */
	private static final TimeZone DEFAULT_ICU_TIME_ZONE = TimeZone.getTimeZone( DEFAULT_TIME_ZONE.getID(), TimeZone.TIMEZONE_JDK );
	
	/**
	 * The number of microseconds in a second (one million).
	 */
	public static final int MICROSECONDS_PER_SECOND = 1000000;

	/**
	 * SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY = 86400
	 */
	public static final int SECONDS_PER_DAY = 86400;

	/**
	 * This is used by getNewCalendar.
	 */
	private static final Calendar baseCalendar;
	static
	{
		// Initialize baseCalendar.
		baseCalendar = Calendar.getInstance( DEFAULT_TIME_ZONE );
		baseCalendar.clear();
		baseCalendar.setLenient( false );
	}

	/**
	 * Returns a new instance of a Calendar, set to the current time.  It will
	 * not be lenient.
	 * 
	 * @return a new instance of a Calendar.
	 */
	public static Calendar getNewCalendar()
	{
		// Cloning an existing Calendar is faster than calling 
		// Calendar.getInstance() and then setLenient( false ).
		Calendar cal = (Calendar)baseCalendar.clone();
		cal.setTimeInMillis( System.currentTimeMillis() );
		return cal;
	}
	
	public static Calendar getNewCalendar(Date date) {
		Calendar cal = getNewCalendar();
		cal.setTime(date);
		return cal;
	}
	
	public static Calendar getNewCalendar(java.sql.Date date) {
		Calendar cal = null;
		if(date != null){
			cal = getNewCalendar();
			cal.setTime(date);
		}
		return cal;
	}

	public static Calendar getNewCalendar(Time time) {
		Calendar cal = null;
		if(time != null){
			cal = getNewCalendar();
			cal.setTime(time);
		}
		return cal;
	}
	
	public static Calendar getNewCalendar(Timestamp timestamp) {
		Calendar cal = null;
		if(timestamp != null){
			cal = getNewCalendar();
			cal.setTime(timestamp);
		}
		return cal;
	}

	
	/**
	 * This is like getNewCalendar but the calendar isn't set to the current time.
	 * 
	 * @return a new calendar.
	 */
	public static Calendar getBaseCalendar()
	{
		return (Calendar)baseCalendar.clone();
	}
	
	/**
	 * Indicates a type of calendar.  Used to index the DATE_FORMAT arrays.
	 */
	private static final int GREGORIAN = 0;
	
	/**
	 * Indicates a type of calendar.  Used to index the DATE_FORMAT arrays.
	 */
	private static final int BUDDHIST = 1;
	
	/**
	 * Indicates a type of calendar.  Used to index the DATE_FORMAT arrays.
	 */
	private static final int CHINESE = 2;
	
	/**
	 * Indicates a type of calendar.  Used to index the DATE_FORMAT arrays.
	 */
	private static final int HEBREW = 3;
	
	/**
	 * Indicates a type of calendar.  Used to index the DATE_FORMAT arrays.
	 */
	private static final int ISLAMIC = 4;
	
	/**
	 * Indicates a type of calendar.  Used to index the DATE_FORMAT arrays.
	 */
	private static final int JAPANESE = 5;

	/**
	 * The DateFormat objects.  There's one for each type of calendar.
	 */
	private static JavartDateFormat[] DATE_FORMATTERS = new JavartDateFormat[ 6 ];

	/**
	 * A place to keep track of the Locale used to create each JavartDateFormat in
	 * DATE_FORMATTERS.
	 */
	private static Locale[] DATE_FORMATTER_LOCALES = new Locale[ 6 ];

	/**
	 * A field provided for synchronization of calls to getDateFormat.
	 */
	public static final byte[] LOCK = new byte[ 0 ];
	
	/**
	 * Returns a formatter for the date-time format pattern.  Its applyPattern
	 * method will be called.
	 * <P>
	 * <B>*** IMPORTANT ***</B> Call this method and use the formatter within a
	 * synchronized block that synchronizes on DateTimeUtil.LOCK.  Or, if the
	 * formatter will be used extensively, clone it before leaving the
	 * synchronized block.
	 * 
	 * @param pattern  the pattern.
	 * @return a formatter for the pattern.
	 */
	public static JavartDateFormat getDateFormat( String pattern )
	{
		// Determine the calendar type.
		int type = GREGORIAN;
		boolean calendarSpecified = false;
		if ( pattern.length() > 1 )
		{
			switch ( pattern.charAt( 0 ) )
			{
				case 'B':
					if ( pattern.charAt( 1 ) == 'u' )
					{
						type = BUDDHIST;
						calendarSpecified = true;
					}
					break;
					
				case 'C':
					if ( pattern.charAt( 1 ) == 'h' )
					{
						type = CHINESE;
						calendarSpecified = true;
					}
					break;
					
				case 'G':
					if ( pattern.charAt( 1 ) == 'r' )
					{
						// GREGORIAN
						calendarSpecified = true;
					}
					break;
					
				case 'H':
					if ( pattern.charAt( 1 ) == 'e' )
					{
						type = HEBREW;
						calendarSpecified = true;
					}
					break;
					
				case 'I':
					if ( pattern.charAt( 1 ) == 's' )
					{
						type = ISLAMIC;
						calendarSpecified = true;
					}
					break;
					
				case 'J':
					if ( pattern.charAt( 1 ) == 'a' )
					{
						type = JAPANESE;
						calendarSpecified = true;
					}
					break;
			}
		}

		// If there's a calendar specifier in the pattern, remove it.
		if ( calendarSpecified )
		{
			pattern = pattern.substring( 2 );
		}
		
		// Get the formatter.  Create one if we haven't made one yet, or we made
		// one with a different Locale.
		JavartDateFormat formatter = DATE_FORMATTERS[ type ];
		Locale locale = Locale.getDefault();
		if ( formatter == null || !locale.equals( DATE_FORMATTER_LOCALES[ type ] ) )
		{
			// Need to create the formatter.
			com.ibm.icu.util.Calendar cal = null;
			switch ( type )
			{
				case GREGORIAN:
					cal = new GregorianCalendar( locale )
	                {
	                    protected DateFormat handleGetDateFormat( String pattern, String override, ULocale locale )
	                    {
	                        DateFormatSymbols symbols = new DateFormatSymbols( this, locale );
	                        return new JavartSimpleDateFormat( pattern, symbols, locale );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, ULocale locale )
	                    {
	                        return handleGetDateFormat( pattern, null, locale );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, String override, Locale locale )
	                    {
	                        return handleGetDateFormat( pattern, override, ULocale.forLocale( locale ) );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, Locale locale )
	                    {
	                        return handleGetDateFormat( pattern, null, ULocale.forLocale( locale ) );
	                    }
	                };		                
					break;
					
				case BUDDHIST:
					cal = new BuddhistCalendar( locale )
	                {
	                    protected DateFormat handleGetDateFormat( String pattern, String override, ULocale locale )
	                    {
	                        DateFormatSymbols symbols = new DateFormatSymbols( this, locale );
	                        return new JavartSimpleDateFormat( pattern, symbols, locale );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, ULocale locale )
	                    {
	                        return handleGetDateFormat( pattern, null, locale );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, String override, Locale locale )
	                    {
	                        return handleGetDateFormat( pattern, override, ULocale.forLocale( locale ) );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, Locale locale )
	                    {
	                        return handleGetDateFormat( pattern, null, ULocale.forLocale( locale ) );
	                    }
	                };		                
					break;

				case CHINESE:
					cal = new ChineseCalendar()
	                {
	                    protected DateFormat handleGetDateFormat( String pattern, String override, ULocale locale )
	                    {
	                        return new JavartChineseDateFormat( pattern, locale );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, ULocale locale )
	                    {
	                        return handleGetDateFormat( pattern, null, locale );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, String override, Locale locale )
	                    {
	                        return handleGetDateFormat( pattern, override, ULocale.forLocale( locale ) );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, Locale locale )
	                    {
	                        return handleGetDateFormat( pattern, null, ULocale.forLocale( locale ) );
	                    }
	                };		                
					break;

				case HEBREW:
					cal = new HebrewCalendar( locale )
	                {
	                    protected DateFormat handleGetDateFormat( String pattern, String override, ULocale locale )
	                    {
	                        DateFormatSymbols symbols = new DateFormatSymbols( this, locale );
	                        return new JavartSimpleDateFormat( pattern, symbols, locale );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, ULocale locale )
	                    {
	                        return handleGetDateFormat( pattern, null, locale );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, String override, Locale locale )
	                    {
	                        return handleGetDateFormat( pattern, override, ULocale.forLocale( locale ) );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, Locale locale )
	                    {
	                        return handleGetDateFormat( pattern, null, ULocale.forLocale( locale ) );
	                    }
	                };		                
					break;

				case ISLAMIC:
					cal = new IslamicCalendar( locale )
	                {
	                    protected DateFormat handleGetDateFormat( String pattern, String override, ULocale locale )
	                    {
	                        DateFormatSymbols symbols = new DateFormatSymbols( this, locale );
	                        return new JavartSimpleDateFormat( pattern, symbols, locale );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, ULocale locale )
	                    {
	                        return handleGetDateFormat( pattern, null, locale );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, String override, Locale locale )
	                    {
	                        return handleGetDateFormat( pattern, override, ULocale.forLocale( locale ) );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, Locale locale )
	                    {
	                        return handleGetDateFormat( pattern, null, ULocale.forLocale( locale ) );
	                    }
	                };		                
					break;
					
				case JAPANESE:
					cal = new JapaneseCalendar( locale )
		                {
	                    protected DateFormat handleGetDateFormat( String pattern, String override, ULocale locale )
	                    {
	                        DateFormatSymbols symbols = new DateFormatSymbols( this, locale );
	                        return new JavartSimpleDateFormat( pattern, symbols, locale );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, ULocale locale )
	                    {
	                        return handleGetDateFormat( pattern, null, locale );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, String override, Locale locale )
	                    {
	                        return handleGetDateFormat( pattern, override, ULocale.forLocale( locale ) );
	                    }
	                    
	                    protected DateFormat handleGetDateFormat( String pattern, Locale locale )
	                    {
	                        return handleGetDateFormat( pattern, null, ULocale.forLocale( locale ) );
	                    }
		                };		                
		            break;
			}

			cal.setTimeZone( DEFAULT_ICU_TIME_ZONE );
			formatter = 
            	(JavartDateFormat)cal.getDateTimeFormat(
                        DateFormat.DEFAULT, DateFormat.DEFAULT, ULocale.forLocale( locale ) );
			formatter.setLenient( false );
			
			DATE_FORMATTERS[ type ] = formatter;
			DATE_FORMATTER_LOCALES[ type ] = locale;
		}

		// Apply the pattern and return.
		formatter.applyPattern( pattern );
		return formatter;
	}
}
