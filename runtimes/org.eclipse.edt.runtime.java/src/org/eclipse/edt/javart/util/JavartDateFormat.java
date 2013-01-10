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

import java.text.ParseException;
import java.util.Date;

import com.ibm.icu.text.DateFormatSymbols;

/**
 * A formatter for date/time values.  It can turn them into formatted Strings,
 * and it can create them by parsing Strings.
 */
public interface JavartDateFormat
{
    /**
     * Apply the given pattern string to this date format.
     */
    public void applyPattern( String pattern );

    /**
     * Sets the formatting symbols.
     * 
     * @param symbols  the formatting symbols.
     */
	public void setDateFormatSymbols( DateFormatSymbols symbols );

    /**
     * Formats a Date into a date/time string.
     * 
     * @param date  the value to be formatted into a string.
     * @return the formatted time string.
     * @throws IllegalArgumentException if the pattern is invalid.
     */
    public String format( Date date );
    
    /**
     * Parse a date/time string.
     *
     * @param text  the date/time string to be parsed.
     * @return a Date.
     * @exception ParseException if the given string cannot be parsed as a Date.
     */
	public Date parse( String text ) throws ParseException;

    /**
     * @return Returns the century.
     */
    public int getCentury();

    /**
     * @param century
     *            The century to set.
     */
    public void setCentury( int century );

    /**
     * @return Returns the microsecond.
     */
    public int getMicrosecond();

    /**
     * @param microsecond
     *            The microsecond to set.
     */
    public void setMicrosecond( int microsecond );

    /**
     * Specify whether or not date/time parsing is to be lenient.  With
     * lenient parsing, the parser may use heuristics to interpret inputs that
     * do not precisely match this object's format.  With strict parsing,
     * inputs must match this object's format.
     * @param lenient when true, parsing is lenient
     */
    public void setLenient( boolean lenient );
}
