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
package org.eclipse.edt.javart;

import java.io.Serializable;
import java.util.Calendar;

import org.eclipse.edt.javart.util.DateTimeUtil;


/**
 * This is a simple structure to hold the value of a Timestamp.  The fields are
 * public for easy access.
 *  
 * @author mheitz
 */
public class TimestampData implements Cloneable, Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	/**
	 * The value, except for fractions of seconds.
	 */
	public Calendar calendar;
	
	/**
	 * The part of the value that's fractions of seconds.
	 */
	public int microseconds;
	
	/**
	 * Initializes the fields to the current time.
	 */
	public TimestampData()
	{
		calendar = DateTimeUtil.getNewCalendar();
		int milliseconds = calendar.get( Calendar.MILLISECOND );
		microseconds = milliseconds * 1000;
		calendar.set( Calendar.MILLISECOND, 0 );
	}
	
	/**
	 * Initializes the fields from existing values.
	 * <P>
	 * cal.get( Calendar.MILLISECOND ) should return zero.
	 */
	public TimestampData( Calendar cal, int micros )
	{
		calendar = cal;
		microseconds = micros;
	}
	
	/**
	 * Returns a clone of this object.
	 */
	public Object clone() throws CloneNotSupportedException
	{
		TimestampData theClone = (TimestampData)super.clone();
		theClone.calendar = (Calendar)theClone.calendar.clone(); 
		return theClone;
	}
}
