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
package org.eclipse.edt.runtime.java.egl.lang;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.javart.util.TimestampIntervalMask;

/**
 * A class for Dates. The value is a Calendar.
 * @author mheitz
 */
public class EDate extends AnyBoxedObject<Calendar> {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public EDate() {
		this(DateTimeUtil.getNewCalendar());
	}

	public EDate(Calendar value) {
		super(value);
	}

	public static EDate ezeBox(Calendar value) {
		return new EDate((Calendar) value.clone());
	}

	public static Object ezeCast(Object value, Object[] constraints) throws JavartException {
		Integer[] args = new Integer[constraints.length];
		java.lang.System.arraycopy(constraints, 0, args, 0, args.length);
		return ezeCast(value, args);
	}

	public static Calendar ezeCast(Object value) throws JavartException {
		return (Calendar) EglAny.ezeCast(value, "asDate", EDate.class, null, null);
	}

	public static boolean ezeIsa(Object value) {
		return value instanceof EDate;
	}

	public String toString() {
		return EString.asString(object);
	}

	public static Calendar defaultValue() {
		long now = java.lang.System.currentTimeMillis();
		Calendar cal = DateTimeUtil.getBaseCalendar();
		cal.setTimeInMillis(now);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
		return cal;
	}

	public static Calendar asDate(String date) throws JavartException {
		return ETimestamp.convert(date, ETimestamp.YEAR_CODE, ETimestamp.DAY_CODE);
	}

	public static Calendar asDate(GregorianCalendar date) throws JavartException {
		return asDate((Calendar) date);
	}

	public static Calendar asDate(Calendar date) throws JavartException {
		return ETimestamp.convert(EString.asString(date), ETimestamp.YEAR_CODE, ETimestamp.DAY_CODE);
	}

	public static boolean equals(Calendar op1, Calendar op2) {
		if (op1 == null || op2 == null)
			return false;
		return op1.equals(op2);
	}

	public static boolean notEquals(Calendar op1, Calendar op2) {
		if (op1 == null || op2 == null)
			return false;
		return !op1.equals(op2);
	}

	/**
	 * Returns the difference between 2 dates
	 */
	public static Integer daysDifferent(Calendar aDate, Calendar bDate) throws JavartException {
		if (aDate == null || bDate == null)
			throw new NullValueException();
		return (int) ((aDate.getTimeInMillis() - bDate.getTimeInMillis()) / (1000 * DateTimeUtil.SECONDS_PER_DAY));
	}

	/**
	 * Returns the adds days to a date
	 */
	public static Calendar addDays(Calendar aDate, Integer amount) throws JavartException {
		if (aDate == null || amount == null)
			throw new NullValueException();
		if (!aDate.isSet(Calendar.DATE))
			throw new TypeCastException();
		Calendar newDate = (Calendar) aDate.clone();
		newDate.roll(Calendar.DAY_OF_YEAR, amount);
		return newDate;
	}

	/**
	 * Returns the extension of a date
	 */
	public static Calendar extend(Calendar aDate, String timeSpanPattern) throws JavartException {
		if (aDate == null)
			throw new NullValueException();
		// Default values in case the pattern doesn't specify things.
		int startCode = ETimestamp.YEAR_CODE;
		int endCode = ETimestamp.SECOND_CODE;
		TimestampIntervalMask mask = new TimestampIntervalMask(timeSpanPattern);
		if (mask.getStartCode() != -1 && mask.getStartCode() <= mask.getEndCode()) {
			startCode = mask.getStartCode();
			endCode = mask.getEndCode();
		}
		return new ETimestamp((Calendar) aDate.clone(), startCode, endCode).ezeUnbox();
	}
}
