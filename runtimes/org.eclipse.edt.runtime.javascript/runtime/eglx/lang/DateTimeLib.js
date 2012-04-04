/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
/****************************************************************************
 * DateTimeLib
 ****************************************************************************/
egl.defineClass(
		'eglx.lang', 'DateTimeLib',
	{
		"constructor" : function() {
		}
	}
);		
egl.eglx.lang.DateTimeLib["dateFromGregorian"] = function(/*int*/ val){
	//returns the date based on an int in gregorian form
	if (val!=null) {
		//yyyyMMdd is the format of the int, so mod away parts
		var day = val % 100;
		val = Math.floor( val / 100 );
		var month = (val % 100) - 1;
		var year = Math.floor( val / 100 );
		return new Date( year, month, day );
	}
	else {
		return null;
	}
};
		
egl.eglx.lang.DateTimeLib["dateFromJulian"] = function(/*int*/ val){
	//Returns a DATE value that corresponds to an integer representation of a Julian date.
	//julianIntDate is of the form yyyyDDD (DDD = number of total days so far in the year)
	if (val!=null) {
		//taking old code for what it's worth
				var year = Math.floor(val / 1000);
				var cal = new Date( year, 0, 1 );
				cal.setTime( cal.getTime() + ( (val % 1000) - 1 ) * egl.MS_PER_DAY );
				return cal;
			}
			else {
				return null;
			}
		};

egl.eglx.lang.DateTimeLib["dateFromInt"] = function (/*Integer*/ day, /*Integer*/ year ) {
	//day is an integer, where 0 represents 12/31/1969
	//a null day means return the current date
	var date = null;
	if ( day != null) {
		date = this.currentDate();
		date.setHours( 0 );
		date.setMinutes( 0 );
		date.setSeconds( 0 );
		date.setMilliseconds( 0 );
		date.setYear( year ? year : 1969 );
		date.setMonth( 11 );
		date.setDate( 31 + day );		//the critical piece; days adjust schedule
			}
			return date;
		};

		
egl.eglx.lang.DateTimeLib["mdy"] = function(/*integer*/month, /*integer*/day, /*integer*/year) {
	if(year && month && day)
	{
		var result = new Date(1970,0,1);
		result.setFullYear(year);
		result.setMonth(month-1);
		result.setDate(day);
		result.setHours(0);
		result.setMinutes(0);
		result.setSeconds(0);
		result.setMilliseconds(0);
		return result;
	}
	else
	{
		return null;
	}
};


egl.eglx.lang.DateTimeLib["currentDate"] = function() {
	//returns a new Date object
	var date = new Date();
	date.setHours( 0 );
	date.setMinutes( 0 );
	date.setSeconds( 0 );
	date.setMilliseconds( 0 );
	return date;
};
egl.eglx.lang.DateTimeLib["currentTime"] = function() {
	//returns a new Date object
	//just ignore date fields
	return new Date();
};
egl.eglx.lang.DateTimeLib["currentTimeStamp"] = function( /*string*/ pattern ) {
	//returns a new Date object
	//timestamp keeps every field
	return this.extend( "timestamp", new Date(), pattern );
};



egl.getProperTimeStampPattern = function( pattern ) {
	new egl.eglx.lang.Constants(); // TODO sbg remove if/when Constants' fields are static
	return ( pattern ) 
				? egl.formatTimeStampPattern( pattern )
				: (( egl.eglx.lang.Constants['$inst'].defaultTimeStampFormat == "" ) 
						? egl.eglx.lang.Constants['$inst'].db2TimestampFormat
						: egl.eglx.lang.Constants['$inst'].defaultTimeStampFormat );
};

egl.formatTimeStampPattern = function( mask ) {
	new egl.eglx.lang.Constants(); // TODO sbg remove if/when Constants' fields are static
	var oldMask = mask; //This is used to be printed out in the exception message
	// Masks specify fractions of seconds with "f", but formatting patterns use "S".
	mask = mask.replace(/f/g, "S");

	// Masks can be any subset of "yyyyMMddHHmmssffffff" (though they can't start or stop in the middle of a character block).
	// We want to return the corresponding subset of the format "yyyy-MM-dd HH:mm:ss.ffffff" (the odbc timestamp format)
	// that matches the subset of characters in the mask (i.e. if the mask is "MMddHH", we want to return the corresponding
	// substring of the pattern: "MM-dd HH".
	var firstCharInMask = mask.charAt(0);
	var lastCharInMask = mask.charAt(mask.length-1);
	
	var startIndex = egl.eglx.lang.Constants['$inst'].odbcTimeStampFormat.indexOf(firstCharInMask);
	var endIndex = egl.eglx.lang.Constants['$inst'].odbcTimeStampFormat.lastIndexOf(lastCharInMask)+1; // (add 1 so we actually *get* the last character)
	
	if( startIndex >= 0 && endIndex > startIndex )
	{
		return egl.eglx.lang.Constants['$inst'].odbcTimeStampFormat.substring( startIndex, endIndex );
	}
	else
	{
		throw egl.createRuntimeException( "CRRUI2032E", [ oldMask ] );
	}
};
