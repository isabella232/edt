/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.runtime.java.egl.lang;

import java.util.Calendar;

import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.javart.util.TimestampIntervalMask;

/**
 * @author jvincens
 * 
 */
public class DateTimeLib extends ExecutableBase
{
	private static final long serialVersionUID = 70L;
	
	public DateTimeLib( RunUnit ru ) throws JavartException
	{
		super( ru );
	}

	/**
	 * Returns a ETimestamp value reflects the current datetime
	 * 
	 * @param program
	 * @return
	 */
	public Calendar currentTimeStamp( )
	{
		return DateTimeUtil.getNewCalendar();
	}

	/**
	 * Returns a positive integer, corresponding to the day portion of the
	 * timestamp. Returned IntValue.nullStatus=Value.SQL_NULL if
	 * aTimestamp.nullStatus == Value.SQL_NULL
	 * 
	 * @param program
	 * @param aTimestamp
	 * @return
	 * @throws JavartException
	 */
	public Integer dayOf( Calendar aTimestamp )
			throws JavartException
	{
		if (aTimestamp == null) return 0;
		
		return aTimestamp.get( Calendar.DAY_OF_MONTH );
	}

	/**
	 * Converts a DateValue into a longer or shorter timestamp value based on
	 * the given mask. Returned TimestampValue.nullStatus=Value.SQL_NULL if
	 * extensionField.nullStatus == Value.SQL_NULL If mask is null it returns
	 * the same value as extend(DateValue)
	 * 
	 * @param program
	 * @param extensionField
	 * @param mask
	 * @return
	 * @throws JavartException
	 * @see DateTimeLib.extend
	 */
	private Calendar extend( Calendar extensionField, String timespanPattern ) throws JavartException
	{
		if ( timespanPattern == null )
		{
			return extend( ETimestamp.asTimestamp(this, extensionField, ETimestamp.YEAR_CODE, ETimestamp.SECOND_CODE) );
		}
		
		if ( extensionField == null )
		{
			return null;
		}
		else
		{
			// Default values in case the pattern doesn't specify things.
			int startCode = ETimestamp.YEAR_CODE;
			int endCode = ETimestamp.SECOND_CODE;

			TimestampIntervalMask mask = new TimestampIntervalMask( timespanPattern );
			if ( mask.getStartCode() != -1 && mask.getStartCode() <= mask.getEndCode() )
			{
				startCode = mask.getStartCode();
				endCode = mask.getEndCode();
			}
			
			return ETimestamp.asTimestamp(this, extensionField, startCode, endCode);
		}
	}

	/**
	 * Converts a TimestampValue into a longer or shorter timestamp value.
	 * Returned TimestampValue.nullStatus=Value.SQL_NULL if
	 * extensionField.nullStatus == Value.SQL_NULL
	 * 
	 * @param program
	 * @param extensionField
	 * @return
	 * @throws JavartException
	 * @see DateTimeLib.extend
	 */
	public Calendar extend( Calendar extensionField )
			throws JavartException
	{
		return extend(extensionField, ETimestamp.DefaultPattern);
	}

	/**
	 * Returns a positive whole number between 1 and 12, corresponding to the
	 * month portion of the timestamp. If aTimestamp.nullStatus ==
	 * Value.SQL_NULL it returns an intValue.nullStatus==Value.SQL_NULL
	 * 
	 * @param program
	 * @param aTimestamp
	 * @return
	 * @throws JavartException
	 */
	public Integer monthOf( Calendar aTimestamp )
			throws JavartException
	{
		return aTimestamp != null 
			? aTimestamp.get( Calendar.MONTH ) + 1 
			: null;
	}

	/**
	 * Converts a string to a Calendar value If timestampAsString == null
	 * null is returned
	 * 
	 * @param program
	 * @param timestampAsString
	 * @return
	 * @throws JavartException
	 */
	public Calendar timeStampValue( String timestampAsString )
			throws JavartException
	{
		return timeStampValueWithPattern(timestampAsString);
	}


	/**
	 * Converts a string to a timestampValue If timestampAsString.nullStatus ==
	 * Value.SQL_NULL it returns a TimeValue.nullStatus==Value.SQL_NULL
	 * 
	 * @param program
	 * @param timestampAsString
	 * @return
	 * @throws JavartException
	 */
	public Calendar timeStampValueWithPattern( String timestampAsString ) throws JavartException
	{
		return timeStampValueWithPattern(timestampAsString, ETimestamp.DefaultPattern);
	}

	/**
	 * Converts a string to a timestampValue based on the supplied pattern If
	 * timestampAsString.nullStatus == Value.SQL_NULL it returns a
	 * TimestampValue.nullStatus==Value.SQL_NULL If pattern.nullStatus ==
	 * Value.SQL_NULL the returned value is the same as calling
	 * timestampValue(StringValue)
	 * 
	 * @param program
	 * @param timestampAsString
	 * @param pattern
	 * @return
	 * @throws JavartException
	 */
	public Calendar timeStampValueWithPattern( String timestampAsString, String pattern ) throws JavartException
	{
		return ETimestamp.asTimestamp( this, timestampAsString, pattern); 
	}

	/**
	 * Returns an IntValue that reflects a positive integer in the range 0
	 * through 6, corresponding to the day of the week implied by aTimestamp. If
	 * aTimestamp.nullStatus == Value.SQL_NULL it returns an
	 * IntValue.nullStatus==Value.SQL_NULL
	 * 
	 * @param program
	 * @param aTimestamp
	 * @return
	 * @throws JavartException
	 */
	public Integer weekdayOf( Calendar aTimestamp )
			throws JavartException
	{
		if ( aTimestamp == null )
		{
			return null;
		}
		else
		{
			return aTimestamp.get( Calendar.DAY_OF_WEEK ) - 1;
		}
	}

	/**
	 * Returns an IntValue that reflects the year portion of aTimestamp. If
	 * aTimestamp.nullStatus == Value.SQL_NULL it returns an
	 * IntValue.nullStatus==Value.SQL_NULL
	 * 
	 * @param program
	 * @param aTimestamp
	 * @return
	 * @throws JavartException
	 */
	public Integer yearOf( Calendar aTimestamp )
			throws JavartException
	{
		if ( aTimestamp == null )
		{
			return null;
		}
		else
		{
			return aTimestamp.get( Calendar.YEAR );
		}
	}
}
