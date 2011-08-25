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
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.javart.util.JavartUtil;

import egl.lang.AnyException;
import egl.lang.TypeCastException;

public class DateTimeLib extends ExecutableBase {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public DateTimeLib(RunUnit ru) throws AnyException {
		super(ru);
	}

	/**
	 * Returns a Calendar that reflects an int.
	 */
	public static Calendar dateFromInt(int dateint) {
		Calendar cal = DateTimeUtil.getBaseCalendar();
		cal.setTimeInMillis(dateint * 1000 * DateTimeUtil.SECONDS_PER_DAY);
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
			cal.get(Calendar.YEAR);
		}
		catch (IllegalArgumentException e) {
			String message = JavartUtil.errorMessage((Executable) null, Message.SYSTEM_FUNCTION_ERROR,
				new Object[] { "DateTimeLib.dateFromJulian", String.valueOf(gregorianIntDate) });
			TypeCastException tcx = new TypeCastException();
			tcx.setMessage( message );
			tcx.setMessageID( Message.DATA_FORMAT_ERROR );
			tcx.castToName = "date";
			tcx.actualTypeName = "int";
			throw tcx;
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
			cal.get(Calendar.YEAR);
		}
		catch (IllegalArgumentException e) {
			String message = JavartUtil.errorMessage((Executable) null, Message.SYSTEM_FUNCTION_ERROR,
				new Object[] { "DateTimeLib.dateFromJulian", String.valueOf(julianIntDate) });
			TypeCastException tcx = new TypeCastException();
			tcx.setMessage( message );
			tcx.setMessageID( Message.DATA_FORMAT_ERROR );
			tcx.castToName = "date";
			tcx.actualTypeName = "int";
			throw tcx;
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
			cal.get(Calendar.YEAR);
		}
		catch (IllegalArgumentException e) {
			String message = JavartUtil.errorMessage((Executable) null, Message.SYSTEM_FUNCTION_ERROR,
				new Object[] { "DateTimeLib.mdy", String.valueOf(month), String.valueOf(day), String.valueOf(year) });
			TypeCastException tcx = new TypeCastException();
			tcx.setMessage( message );
			tcx.setMessageID( Message.DATA_FORMAT_ERROR );
			tcx.castToName = "date";
			tcx.actualTypeName = "int,int,int";
			throw tcx;
		}
		return cal;
	}
}
