/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package eglx.lang;

import java.util.Calendar;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.util.DateTimeUtil;


public class DateTimeLib extends ExecutableBase {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public static final long DEC_31_1969_LOCAL_TIME_ZONE;
	static
	{
		int offset = DateTimeUtil.DEFAULT_TIME_ZONE.getOffset( 0 );
		DEC_31_1969_LOCAL_TIME_ZONE = 0 - offset;
	}

	public DateTimeLib() throws AnyException {
	}

	/**
	 * Returns a Calendar that reflects an int.
	 */
	public static Calendar dateFromInt(int dateInt) {
		Calendar cal = DateTimeUtil.getBaseCalendar();
		long dateLong = ((long) dateInt * 1000 * DateTimeUtil.SECONDS_PER_DAY) + DEC_31_1969_LOCAL_TIME_ZONE;
		cal.setTimeInMillis(dateLong);
		return cal;
	}

	/**
	 * Returns a Calendar that represents a Gregorian date.
	 */
	public static Calendar dateFromGregorian(int gregorianIntDate) throws AnyException {
		Calendar cal = DateTimeUtil.getBaseCalendar();
		cal.set(Calendar.YEAR, gregorianIntDate / 10000);
		cal.set(Calendar.MONTH, ((gregorianIntDate % 10000) / 100) - 1);
		cal.set(Calendar.DATE, gregorianIntDate % 100);
		try {
			cal.setTimeInMillis(cal.getTimeInMillis());
		}
		catch (IllegalArgumentException e) {
			TypeCastException tcx = new TypeCastException();
			tcx.castToName = "date";
			tcx.actualTypeName = "int";
			tcx.initCause( e );
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, gregorianIntDate, tcx.actualTypeName, tcx.castToName );
		}
		return cal;
	}

	/**
	 * Returns a Calendar that represents a Julian date.
	 */
	public static Calendar dateFromJulian(int julianIntDate) throws AnyException {
		Calendar cal = DateTimeUtil.getBaseCalendar();
		// This is a workaround for a problem seen in Java 1.5 but not in 1.4.
		// It forces the Calendar to recompute its internal values. If we don't do it, the Calendar won't let us set
		// DAY_OF_YEAR because the MONTH and DAY_OF_MONTH don't jive with the new DAY_OF_YEAR.
		cal.add(Calendar.YEAR, 1);
		// end of workaround
		cal.set(Calendar.YEAR, julianIntDate / 1000);
		cal.set(Calendar.DAY_OF_YEAR, julianIntDate % 1000);
		try {
			cal.setTimeInMillis(cal.getTimeInMillis());
		}
		catch (IllegalArgumentException e) {
			TypeCastException tcx = new TypeCastException();
			tcx.castToName = "date";
			tcx.actualTypeName = "int";
			tcx.initCause( e );
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, julianIntDate, tcx.actualTypeName, tcx.castToName );
		}
		return cal;
	}

	/**
	 * Returns a Calendar that reflects the month, the day of the month, and the year of a calendar date.
	 */
	public static Calendar mdy(int month, int day, int year) throws AnyException {
		Calendar cal = DateTimeUtil.getBaseCalendar();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DATE, day);
		try {
			cal.setTimeInMillis(cal.getTimeInMillis());
		}
		catch (IllegalArgumentException e) {
			InvalidArgumentException ex = new InvalidArgumentException();
			ex.initCause( e );
			throw ex.fillInMessage( Message.MDY_ERROR, month, day, year );
		}
		return cal;
	}
}
