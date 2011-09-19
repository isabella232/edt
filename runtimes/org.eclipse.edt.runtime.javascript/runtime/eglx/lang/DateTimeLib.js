/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
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
	if (julianIntDate!=null) {
		//taking old code for what it's worth
				var year = Math.floor(julianIntDate / 1000);
				var cal = new Date( year, 0, 1 );
				cal.setTime( cal.getTime() + ( (julianIntDate % 1000) - 1 ) * egl.MS_PER_DAY );
				return cal;
			}
			else {
				return null;
			}
		};

egl.eglx.lang.DateTimeLib["dateFromInt"] = function (/*Integer*/ day ) {
	//day is an integer, where 0 represents 12/31/1899
	//a null day means return the current date
	var date = null;
	if ( day != null) {
		date = this.currentDate();
		date.setHours( 0 );
		date.setMinutes( 0 );
		date.setSeconds( 0 );
		date.setMilliseconds( 0 );
		date.setYear( 1899 );
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