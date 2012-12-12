/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

egl.convertTextToStringN = function( str, n )
{
	if ( str != null && str.length > n )
	{
		return str.substring( 0, n );
	}
	return str;
};

egl.convertTextToFixedText = function( str, len )
{
	if ( str == null || str.length == len )
	{
		return str;
	}
	else if ( str.length > len )
	{
		return str.substring( 0, len );
	}
	else
	{
		return str + egl.eglx.lang.StringLib.spaces( len - str.length );
	}
};

Number.prototype.compareTo 	= function(other) { return (this<other) ? -1 : 1; };
Date.prototype.compareTo 	= function(other) { return (this<other) ? -1 : 1; };
Boolean.prototype.compareTo = function(other) { return (this<other) ? -1 : 1; };
String.prototype.compareTo 	= function(other) { return (this<other) ? -1 : 1; };

egl.textLen = function (s) {
	//Returns the number of bytes in a text expression, excluding any trailing spaces or null values.
	if ( s === null )
		return 0;
	return s.replace( /(\s)*$/, "" ).length;
};

egl.padWithLeadingZeros = function(s, totChars)
{
    for ( var zerosToAdd = totChars - s.length; zerosToAdd > 0; zerosToAdd-- )
    {
        s = "0" + s;
    }
	return s;
};
egl.padWithTrailingZeros = function(s, totChars)
{
    for ( var zerosToAdd = totChars - s.length; zerosToAdd > 0; zerosToAdd-- )
    {
        s = s + "0";
    }
    return s;
};

egl.getDateInfo = function( d )
{
	var day = egl.padWithLeadingZeros("" + d.getDate(),2);
	var dayWithoutLeadingZeros = d.getDate();
	var month = egl.padWithLeadingZeros("" + (d.getMonth()+1),2);
	var monthWithoutLeadingZeros = d.getMonth()+1;
	var year = egl.padWithLeadingZeros("" + Math.min( 9999,d.getFullYear() ), 4 );
	var twoDigitYear = year.substr(2,2);
	var monthShort = egl.dateConstants.shortMonths[ d.getMonth() ];
	var dayShort = egl.dateConstants.shortDays[ d.getDay() ];
	var yearLong = egl.padWithLeadingZeros("" + Math.min( 9999,d.getFullYear() ), 5 );
	var monthLong = egl.dateConstants.longMonths[ d.getMonth() ];
	var era = d.getFullYear() > 0 ? "AD" : "BC";
	
	var result = new Array();
	result[0] = year;
	result[1] = twoDigitYear;
	result[2] = month;
	result[3] = day;
	result[4] = monthWithoutLeadingZeros;
	result[5] = dayWithoutLeadingZeros;
	result[6] = monthShort;
	result[7] = dayShort;
	result[8] = yearLong;
	result[9] = monthLong;
	result[10] = era;
	return result;
};

egl.dateConstants = {
	shortMonths: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
    longMonths: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
    shortDays: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
    longDays: ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']
};

egl.dateToString = function( d, format )
{
	var dateInfo = egl.getDateInfo(d);
	var result = "";
	
	var validCheck = format.replace(/GGG|G|yyyyy|yyyy|yy|MMMMM|MMM|MM|M|dd|d|EEE|''|'([^']|'')*'|[^A-Za-z]/g, "");
	if(validCheck.length > 0)
	{
		throw egl.createTypeCastException( "CRRUI2717E", [format] );
	}
	
	var tokens = format.match(/GGG|G|yyyyy|yyyy|yy|MMMMM|MMM|MM|M|dd|d|EEE|''|'([^']|'')*'|[^A-Za-z]/g);
	var numTokens = (tokens == null) ? 0 : tokens.length;
	for(var i = 0; i < numTokens; i++ )
	{
		if(tokens[i] == "GGG")
		{
			result += dateInfo[10];
		}
		else if(tokens[i] == "G")
		{
			result += dateInfo[10];
		}
		else if(tokens[i] == "yyyyy")
		{
			result += dateInfo[8];
		}
		else if(tokens[i] == "yyyy")
		{
			result += dateInfo[0];
		}
		else if(tokens[i] == "yy")
		{
			result += dateInfo[1];
		}
		else if(tokens[i] == "MMMMM")
		{
			result += dateInfo[9];
		}
		else if(tokens[i] == "MMM")
		{
			result += dateInfo[6];
		}
		else if(tokens[i] == "MM")
		{
			result += dateInfo[2];
		}
		else if(tokens[i] == "M")
		{
			result += dateInfo[4];
		}
		else if(tokens[i] == "dd")
		{
			result += dateInfo[3];
		}
		else if(tokens[i] == "d")
		{
			result += dateInfo[5];
		}
		else if(tokens[i] == "EEE")
		{
			result += dateInfo[7];
		}
		else if(tokens[i] == "''")
		{
			result += "'";
		}
		else if(tokens[i].charAt(0) == "'")
		{
			var lit = tokens[i].substr(1,tokens[i].length-2);
			result += lit.replace(/''/, "'");
		}
		else
		{
			result += tokens[i];
		}
	}
	
	return result;
};

egl.getTimeInfo = function( t )
{
	var hours24 = t.getHours();
	var hours12;
	var minutes = egl.padWithLeadingZeros("" + t.getMinutes(),2);
	var seconds = egl.padWithLeadingZeros("" + t.getSeconds(),2);
	var amPm;

	if( hours24 <= 11 )
	{
		amPm = "AM";
		if( hours24 == 0 )
		{
			hours12 = 12;
		}
		else
		{
			hours12 = hours24;
		}
	}
	else
	{
		amPm = "PM";
		if( hours24 != 12 )
		{
			hours12 = (hours24 - 12);
		}
		else
		{
			hours12 = 12;
		}
	}
	
	hours24 = egl.padWithLeadingZeros("" + hours24,2);
	hours12 = egl.padWithLeadingZeros("" + hours12,2);
	
	timezone = (-t.getTimezoneOffset() < 0 ? '-' : '+') + (Math.abs(t.getTimezoneOffset() / 60) < 10 ? '0' : '') + (Math.abs(t.getTimezoneOffset() / 60)) + '00';
	
	var result = new Array();
	result[0] = hours24;
	result[1] = hours12;
	result[2] = minutes;
	result[3] = seconds;
	result[4] = amPm;
	result[5] = timezone;
	return result;
};

egl.timeToString = function( t, format )
{
	var timeParts = egl.getTimeInfo(t);
	var result = "";
	
	var validCheck = format.replace(/HH|hh|mm|ss|a|Z|''|'([^']|'')*'|[^A-Za-z]/g, "");
	if(validCheck.length > 0)
	{
		throw egl.createTypeCastException( "CRRUI2717E", [format] );
	}
	
	var tokens = format.match(/HH|hh|mm|ss|a|Z|''|'([^']|'')*'|[^A-Za-z]/g);
	var numTokens = (tokens == null) ? 0 : tokens.length;
	for(var i = 0; i < numTokens; i++ )
	{
		if(tokens[i] == "HH")
		{
			result += timeParts[0];
		}
		else if(tokens[i] == "hh")
		{
			result += timeParts[1];
		}
		else if(tokens[i] == "mm")
		{
			result += timeParts[2];
		}
		else if(tokens[i] == "ss")
		{
			result += timeParts[3];
		}
		else if(tokens[i] == "a")
		{
			result += timeParts[4];
		}
		else if(tokens[i] == "Z")
		{
			result += timeParts[5];
		}
		else if(tokens[i] == "''")
		{
			result += "'";
		}
		else if(tokens[i].charAt(0) == "'")
		{
			var lit = tokens[i].substr(1,tokens[i].length-2);
			result += lit.replace(/''/, "'");
		}
		else
		{
			result += tokens[i];
		}
	}
	
	return result;
};

egl.stringToTimeWithDefaultSeparator = function(s, format)
{
	var result = egl.stringToTimeInternal(s, format, true, true);
	if( result != null )
	{
		return result;
	}
	else
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ s, "string", "time" ], "time", "string" );
	}
};

egl.stringToTime = function(s, format)
{
	var result = egl.stringToTimeInternal(s, format, true);
	if( result == null )
	{
		result = egl.stringToTimeInternal(s, format, false);
	}
	if( result == null && s.length < format.length )
	{
		result = egl.stringToTimeInternal("0" + s, format, false);
	}
	
	if( result != null )
	{
		return result;
	}
	else
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ s, "string", "time" ], "time", "string" );
	}
};

egl.stringToTimeInternal = function(s, format, strict, defaultSeparator)
{
	var result = new Date(2000, 0, 1);
	var tokens = format.match(/HH|hh|mm|ss|a|''|'([^']|'')*'|[^A-Za-z]/g);
	var numTokens = (tokens == null) ? 0 : tokens.length;
	result.setSeconds(0);
	
	if(!strict)
	{
		s = s.replace(/[^0-9]/g, "");
	}
	
	for(var i = 0; i < numTokens; i++ )
	{
		s = egl.processToken(tokens[i], s, result, strict);
		if(s == null)
		{
			return null;
		}
		//Separator is needed for each token and must be ':'
		if(defaultSeparator){
			if(i < numTokens - 1){
				if( s.charAt(0) == ':')
					s = s.substr(1);
				else
					return null;
			}
		}
	}
	
	if("PM" == result["eze$$AMPM"] && result.getHours() < 12)
	{
		result.setHours(result.getHours() + 12);
	}
	else if("AM" == result["eze$$AMPM"] && result.getHours() == 12)
	{
		result.setHours(result.getHours() - 12);
	}
	
	return result;
};

egl.stringToDateWithDefaultSeparator = function(s, format)
{
	var result = egl.stringToDateInternal(s, format, true, true);
	if( result != null )
	{
		result.setHours(0);
		result.setMinutes(0);
		result.setSeconds(0);
		result.setMilliseconds(0);
		return result;
	}
	else
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ s, "string", "date" ], "date", "string" );
	}
};

egl.stringToDate = function(s, format)
{
	var result = egl.stringToDateInternal(s, format, true);
	if( result == null )
	{
		result = egl.stringToDateInternal(s, format, false);
	}
	if( result == null && s.length < format.length )
	{
		result = egl.stringToDateInternal("0" + s, format, false);
	}
	
	if( result != null )
	{
		result.setHours(0);
		result.setMinutes(0);
		result.setSeconds(0);
		result.setMilliseconds(0);
		return result;
	}
	else
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ s, "string", "date" ], "date", "string" );
	}
};

egl.stringToDateInternal = function(s, format, strict, defaultSeparator)
{
	var result = new Date(1970, 0, 1);
	var tokens = format.match(/yyyy|yy|MM|dd|''|'([^']|'')*'|[^A-Za-z]/g);
	var numTokens = (tokens == null) ? 0 : tokens.length;
	
	if(!strict)
	{
		s = s.replace(/[^0-9]/g, "");
	}

	//The Year must be set first, otherwize the leap year is wrong ,such as 2000/2/29 will be 2000/3/1
	var yearResult = new Date(1970, 0, 1);
	var tempS = s;
	for(var i = 0; i < numTokens; i++ )
	{
		tempS = egl.processToken(tokens[i], tempS, yearResult, strict);
		if(tempS == null)
		{
			return null;
		}
		if( yearResult.getFullYear() != result.getFullYear() )
		{
			result.setFullYear( yearResult.getFullYear() );
			break;
		}
		//Separator is needed for each token
		if(defaultSeparator){
			if(i < numTokens - 1){
				if(tempS.length >= 1)
					tempS = tempS.substr(1);
				else
					return null;
			}
		}
	}

	for(var i = 0; i < numTokens; i++ )
	{
		s = egl.processToken(tokens[i], s, result, strict);
		if(s == null)
		{
			return null;
		}
		//Separator is needed for each token
		if(defaultSeparator){
			if(i < numTokens - 1){
				if(s.length >= 1)
					s = s.substr(1);
				else
					return null;
			}
		}
	}
	
	if (strict)
	{
		if (s != null && s.length > 0)
		{
			return null;
		}
	}
	
	if(result["eze$$adjustdate"])
	{
		result = egl.adjustYear(result);
	}
	
	return result;
};

egl.adjustYear = function(origDate)
{
	var d = (new Date);
	var curFullYear = d.getFullYear();
	var curTwoDigitPrefix = ("" + curFullYear).substr(0,2);

	var range = new Array();
	range[0] = new Date(d);
	range[0].setFullYear(range[0].getFullYear()-80);
	range[1] = new Date(d);
	range[1].setFullYear(range[1].getFullYear()+20);
	
	var newDate = new Date();
	newDate.setTime(origDate.getTime());
	var origTwoDigitYear = ("" + origDate.getFullYear());
	origTwoDigitYear = egl.padWithLeadingZeros(origTwoDigitYear.substr(origTwoDigitYear.length - 2),2);
	newDate.setFullYear("" + (curTwoDigitPrefix-1) + origTwoDigitYear);
	
	if( newDate.getTime() >= range[0].getTime() && newDate.getTime() <= range[1].getTime() )
	{
		if ( origDate["eze$$CheckLeap"] )
		{
			newDate["eze$$CheckLeap"] = origDate["eze$$CheckLeap"];
		}
		if ( !egl.leapYearCheck(newDate,3,4) )
		{
			return null;
		}
		return newDate;
	}
	
	newDate.setFullYear("" + curTwoDigitPrefix + origTwoDigitYear);
	if( newDate.getTime() >= range[0].getTime() && newDate.getTime() <= range[1].getTime() )
	{
		if ( origDate["eze$$CheckLeap"] )
		{
			newDate["eze$$CheckLeap"] = origDate["eze$$CheckLeap"];
		}
		if ( !egl.leapYearCheck(newDate,3,4) )
		{
			return null;
		}
		return newDate;
	}
	
	newDate.setFullYear("" + (curTwoDigitPrefix+1) + origTwoDigitYear);
	if ( origDate["eze$$CheckLeap"] )
	{
		newDate["eze$$CheckLeap"] = origDate["eze$$CheckLeap"];
	}
	if ( !egl.leapYearCheck(newDate,3,4) )
	{
		return null;
	}
	return newDate;
};

// This is called repeatedly while we're parsing a string for a date, time, or timestamp.
// The initial 'result' Date object must be set to January 1st.
//
// Properties will be added to 'result' when some processing and/or validation must be
// completed later.
//   * eze$$AMPM is set to either "AM" or "PM" when the token is "a".
//   * When the token is "yy", eze$$adjustdate is set to true, telling us to call adjustYear.
//   * eze$$CheckLeap is used to validate that February 29th is only accepted in a leap year.
//     It's a number, used as a bit field to mark which parts of a date have previously
//     been seen.  Days are bit 1, months are bit 2, and years are bit 3.  We only validate 
//     that Feb 29 is in a leap year if all three parts of a date have been seen.  We only
//     set the day bit when the value is 29, and we only set the month bit when the value is
//     Feb.
egl.processToken = function(token, s, result, strict)
{
	if(token == "yyyy")
	{
		if(s.length < 4)
		{
			return null;
		}
		
		var sub = s.substr(0,4);
		if( egl.isdoubledigit(sub) )
		{
			result.setFullYear(sub);
			if ( !egl.leapYearCheck(result,3,4) )
			{
				return null;
			}
			s = s.substr(4);
		}
		else
		{
			return null;
		}
	}
	else if(token == "yy")
	{
		if(s.length < 2)
		{
			return null;
		}

		result["eze$$adjustdate"] = true;
		var sub = s.substr(0,2);
		if( egl.isdoubledigit(sub) )
		{
			result.setFullYear(sub);
			s = s.substr(2);
		}
		else
		{
			return null;
		}
	}
	else if(token == "MM")
	{
		if(s.length < 2)
		{
			return null;
		}

		var sub = s.substr(0,2);
		if( egl.isdoubledigit(sub) && sub >= 1 && sub <= 12 )
		{
			var d = result.getDate();
			if ( (d > 29 && sub === "02")
				|| (d === 31 && (sub === "04" || sub === "06" || sub === "09" || sub === "11")) )
			{
				return null;
			}
			
			result.setMonth(sub-1);			
			if ( sub === "02" )
			{
				if ( !egl.leapYearCheck(result,5,2) )
				{
					return null;
				}
			}
			s = s.substr(2);
		}
		else
		{
			return null;
		}
	}
	else if(token == "dd")
	{
		if(s.length < 2)
		{
			return null;
		}

		var sub = s.substr(0,2);
		if( egl.isdoubledigit(sub) && sub >= 1 && sub <= 31 )
		{
			switch(result.getMonth())
			{
				case 1:
					if ( sub === "30" || sub === "31" ) return null;
					break;
				
				case 3:
				case 5:
				case 8:
				case 10:
					if ( sub === "31" ) return null;
			}
			result.setDate(sub);
			if ( sub === "29" )
			{
				if ( !egl.leapYearCheck(result,6,1) )
				{
					return null;
				}
			}
			s = s.substr(2);
		}
		else
		{
			return null;
		}
	}
	else if(token == "HH")
	{
		if(s.length < 2)
		{
			return null;
		}
		
		var sub = s.substr(0,2);
		if( egl.isdoubledigit(sub) && sub >= 0 && sub <= 23 )
		{
			result.setHours(sub);
			s = s.substr(2);
		}
		else
		{
			return null;
		}
	}
	else if(token == "hh")
	{
		if(s.length < 2)
		{
			return null;
		}
		
		var sub = s.substr(0,2);
		if( egl.isdoubledigit(sub) && sub >= 1 && sub <= 12 )
		{
			result.setHours(sub);
			s = s.substr(2);
		}
		else
		{
			return null;
		}
	}
	else if(token == "mm")
	{
		if(s.length < 2)
		{
			return null;
		}
		
		var sub = s.substr(0,2);
		if( egl.isdoubledigit(sub) && sub >= 0 && sub <= 59 )
		{
			result.setMinutes(sub);
			s = s.substr(2);
		}
		else
		{
			return null;
		}
	}
	else if(token == "ss")
	{
		if(s.length < 2)
		{
			return null;
		}
		
		var sub = s.substr(0,2);
		if( egl.isdoubledigit(sub) && sub >= 0 && sub <= 59 )
		{
			result.setSeconds(sub);
			s = s.substr(2);
		}
		else
		{
			return null;
		}
	}
	else if(token.charAt(0) == "f")
	{
	    var len = token.length;
		if(s.length < len)
		{
			return null;
		}
		result.setMilliseconds(s.substr(0,3));
		s = s.substr(len);
	}
	else if(token == "a")
	{
		var amPm = s.substr(0,2).toUpperCase();
		if(amPm == "PM")
		{
			result["eze$$AMPM"] = "PM";
		}
		else if(amPm == "AM")
		{
			result["eze$$AMPM"] = "AM";
		}
		else
		{
			return null;
		}
		s = s.substr(2);
	}
	else if( strict )
	{
		if(token == "''")
		{
			if(s.charAt(0) != "'")
			{
				return null;				
			}
			s = s.substr(1);
		}
		else if(token.charAt(0) == "'")
		{
			var lit = token.substr(1,token.length-2);
			lit = lit.replace(/''/, "'");
			if(s.substr(0, lit.length) != lit)
			{
				return null;
			}
			s = s.substr(lit.length);
		}
		else
		{
			if(s.substr(0, token.length) != token)
			{
				return null;
			}
			s = s.substr(token.length);
		}
	}
	
	return s;
};

egl.leapYearCheck = function( result, doCheck, bitFlag )
{
	if ( result["eze$$CheckLeap"] )
	{
		if ( result["eze$$CheckLeap"] === doCheck )
		{
			// Verify that it's a leap year, either fail or set the day & month to
			// Feb 29.  The date will have "rolled forward" to March 1 if the year
			// wasn't a leap year when we set the day & month in egl.processToken.
			var year = result.getFullYear();
			if ( (year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)) )
			{
				result.setMonth(1);
				result.setDate(29);
			}
			else
			{
				// Error!  There's no Feb 29 in this year!
				return false;
			}
		}
		else
		{
			// All parts of the date haven't been seen yet.  Record the fact that
			// we've seen another part.
			result["eze$$CheckLeap"] += bitFlag;
		}
	}
	else
	{
		// Record the fact that we've seen the first part of the date.
		result["eze$$CheckLeap"] = bitFlag;
	}
	
	return true;
};

egl.timeStampToString = function( ts, format )
{
	var dateParts = egl.getDateInfo(ts);
	var timeParts = egl.getTimeInfo(ts);
	var result = "";
	var regex = /GGG|G|yyyyy|yyyy|yy|MMMMM|MMM|MM|M|dd|d|EEE|HH|hh|mm|ss|S+|f+|a|Z|''|'([^']|'')*'|[^A-Za-z]/g;
	
	var validCheck = format.replace(regex, "");
	if(validCheck.length > 0)
	{
		throw egl.createTypeCastException( "CRRUI2717E", [format] );
	}
	
	var tokens = format.match(regex);
	var numTokens = (tokens == null) ? 0 : tokens.length;
	for(var i = 0; i < numTokens; i++ )
	{
		if(tokens[i] == "GGG")
		{
			result += dateParts[10];
		}
		else if(tokens[i] == "G")
		{
			result += dateParts[10];
		}
		else if(tokens[i] == "yyyyy")
		{
			result += dateParts[8];
		}
		else if(tokens[i] == "yyyy")
		{
			result += dateParts[0];
		}
		else if(tokens[i] == "yy")
		{
			result += dateParts[1];
		}
		else if(tokens[i] == "MMMMM")
		{
			result += dateParts[9];
		}
		else if(tokens[i] == "MMM")
		{
			result += dateParts[6];
		}
		else if(tokens[i] == "MM")
		{
			result += dateParts[2];
		}
		else if(tokens[i] == "M")
		{
			result += dateParts[4];
		}
		else if(tokens[i] == "dd")
		{
			result += dateParts[3];
		}
		else if(tokens[i] == "d")
		{
			result += dateParts[5];
		}
		else if(tokens[i] == "EEE")
		{
			result += dateParts[7];
		}
		else if(tokens[i] == "HH")
		{
			result += timeParts[0];
		}
		else if(tokens[i] == "hh")
		{
			result += timeParts[1];
		}
		else if(tokens[i] == "mm")
		{
			result += timeParts[2];
		}
		else if(tokens[i] == "ss")
		{
			result += timeParts[3];
		}
		else if(tokens[i] == "a")
		{
			result += timeParts[4];
		}
		else if(tokens[i].charAt(0) == "f" || tokens[i].charAt(0) == "S")
		{
            result += egl.padWithTrailingZeros("" + ts.getMilliseconds(),tokens[i].length);
		}
		else if(tokens[i] == "Z")
		{
			result += timeParts[5];
		}
		else if(tokens[i] == "''")
		{
			result += "'";
		}
		else if(tokens[i].charAt(0) == "'")
		{
			var lit = tokens[i].substr(1,tokens[i].length-2);
			result += lit.replace(/''/, "'");
		}
		else
		{
			result += tokens[i];
		}
	}
	
	return result;
};

egl.stringToTimeStampWithDefaultSeparator = function(s, format){
	var result = egl.stringToTimeStampInternal(s, format, true, true);
	if( result != null )
	{		
		return result;
	}
	else
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ s, "string", "timestamp" ], "time", "timestamp" );
	}
};

egl.stringToTimeStamp = function(s, format)
{
	var result = egl.stringToTimeStampInternal(s, format, true);
	if( result == null )
	{
		result = egl.stringToTimeStampInternal(s, format, false);
	}
	if( result == null && s.length < format.length )
	{
		result = egl.stringToTimeStampInternal("0" + s, format, false);
	}
	
	if( result != null )
	{		
		return result;
	}
	else
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ s, "string", "timestamp" ], "time", "timestamp" );
	}
};

egl.stringToTimeStampInternal = function( s, format, strict, defaultSeparator )
{
	var result = new Date(1970, 0, 1);

	var tokens = format.match(/yyyy|yy|MM|dd|HH|hh|mm|ss|f+|a|''|'([^']|'')*'|[^A-Za-z]/g);
	var numTokens = (tokens == null) ? 0 : tokens.length;
	
	if(!strict)
	{
		s = s.replace(/[^0-9]/g, "");
	}
	
	//The Year must be set first, otherwize the leap year is wrong ,such as 2000/2/29 will be 2000/3/1
	var yearResult = new Date;
	yearResult.setFullYear(0);
	yearResult.setMonth(0);
	yearResult.setDate(1);
	var tempS = s;
	for(var i = 0; i < numTokens; i++ )
	{
		tempS = egl.processToken(tokens[i], tempS, yearResult, strict);
		if(tempS == null)
		{
			return null;
		}
		if( yearResult.getFullYear() != result.getFullYear() )
		{
			result.setFullYear( yearResult.getFullYear() );
			break;
		}
		//Separator is needed for each token
		if(defaultSeparator){
			if(i < numTokens - 1){
				if(tempS.length >= 1)
					tempS = tempS.substr(1);
				else
					return null;
			}	
		}
		
	}
	if( yearResult.getFullYear() == result.getFullYear() ){
		//If year is not set, set it to a leap year to accept dates "0229"
		result.setFullYear(2000);
	}
	
	for(var i = 0; i < numTokens; i++ )
	{
		s = egl.processToken(tokens[i], s, result, strict);
		if(s == null)
		{
			return null;
		}
		//Separator is needed for each token
		if(defaultSeparator){
			if(i < numTokens - 1){
				if(s.length >= 1)
					s = s.substr(1);
				else
					return null;
			}
		}
	}
	if(s.length != 0){
	   return null;
    }
	
	if(result["eze$$adjustdate"])
	{
		result = egl.adjustYear(result);
	}
	if("PM" == result["eze$$AMPM"] && result.getHours() < 12)
	{
		result.setHours(result.getHours() + 12);
	}
	else if("AM" == result["eze$$AMPM"] && result.getHours() == 12)
	{
		result.setHours(result.getHours() - 12);
	}

	return result;
};

egl.convertNumberToInteger = function( x, upper, lower, creatx )
{
	if ( !isFinite( x ) )
	{
		var target = upper == 2147483647 ? "int" : "smallint";
		if (!creatx)
		{
			creatx = egl.createTypeCastException;
		}
		throw creatx( "CRRUI2018E", [ String( x ), target ] );
	}

	if ( x >= 0 )
	{
		x = Math.floor( x );
		if ( x > upper )
		{
			var target = upper == 2147483647 ? "int" : "smallint";
			if (!creatx)
			{
				creatx = egl.createTypeCastException;
			}
			throw creatx( "CRRUI2018E", [ String( x ), target ] );
		}
	}
	else
	{
		x = Math.ceil( x );
		if ( x < lower )
		{
			var target = upper == 2147483647 ? "int" : "smallint";
			if (!creatx)
			{
				creatx = egl.createTypeCastException;
			}
			throw creatx( "CRRUI2018E", [ String( x ), target ] );
		}
	}

	return x;
};

egl.convertFloatToInteger = function( x, upper, lower, creatx )
{
	if ( !isFinite( x ) )
	{
		var target = upper == 2147483647 ? "int" : "smallint";
		if (!creatx)
		{
			creatx = egl.createTypeCastException;
		}
		throw creatx( "CRRUI2018E", [ String( x ), target ] );
	}

	if ( x > 0 )
	{
		x = Math.floor( x );
		if ( x > upper )
		{
			var target = upper == 2147483647 ? "int" : "smallint";
			if (!creatx)
			{
				creatx = egl.createTypeCastException;
			}
			throw creatx( "CRRUI2018E", [ String( x ), target ] );
		}
	}
	else if ( x < 0 )
	{
		x = -Math.floor( -x );
		if ( x < lower )
		{
			var target = upper == 2147483647 ? "int" : "smallint";
			if (!creatx)
			{
				creatx = egl.createTypeCastException;
			}
			throw creatx( "CRRUI2018E", [ String( x ), target ] );
		}
	}

	return x;
};

egl.convertNumberToInt = function( x, creatx )
{
	return egl.convertNumberToInteger( x, 2147483647, -2147483648, creatx );
};

egl.convertFloatToInt = function( x, creatx )
{
	return egl.convertFloatToInteger( x, 2147483647, -2147483648, creatx );
};

egl.convertNumberToSmallint = function( x, creatx )
{
	return egl.convertNumberToInteger( x, 32767, -32768, creatx );
};

egl.convertFloatToSmallint = function( x, creatx )
{
	return egl.convertFloatToInteger( x, 32767, -32768, creatx );
};

egl.convertIntegerToDecimal = function( x, limit, creatx )
{
	return egl.convertDecimalToDecimal( new egl.javascript.BigDecimal( String( x ) ), 0, limit, creatx );
};

egl.convertFloatToDecimal = function( x, decimals, limit, creatx )
{
	if ( !isFinite( x ) )
	{
		if (!creatx)
		{
			creatx = egl.createTypeCastException;
		}
		throw creatx( "CRRUI2018E", [ String( x ), "decimal" ] );
	}
	if ( decimals < 0 ) {
		return new egl.javascript.BigDecimal( x );
	} else {
		x = new egl.javascript.BigDecimal( (x*1.0).toFixed( decimals + 1 ) );// toFixed rounds half-up so give an extra decimal, let the next call round down
		return egl.convertDecimalToDecimal( x, decimals, limit, creatx );
	}
};
egl.convertNumberToDecimal = function( x, decimals, limit, creatx )
{
    if ( !isFinite( x ) )
    {
        if (!creatx)
        {
            creatx = egl.createTypeCastException;
        }
        throw creatx( "CRRUI2018E", [ String( x ), "decimal" ] );
    }
    if ( limit === undefined ) {
        return new egl.javascript.BigDecimal( x );
    } else {
        x = new egl.javascript.BigDecimal( (x*1.0).toFixed( decimals + 1 ) );// toFixed rounds half-up so give an extra decimal, let the next call round down
        return egl.convertDecimalToDecimal( x, decimals, limit, creatx );
    }
};

egl.convertDecimalToDecimal = function( x, decimals, limit, creatx )
{
	if ( limit && x.scale() > decimals )
	{
		x = x.setScale( decimals, egl.javascript.BigDecimal.prototype.ROUND_DOWN );
	}

	if ( limit && (x.compareTo( limit ) > 0 || x.compareTo( limit.negate() ) < 0) )
	{
		if ( !creatx )
		{
			creatx = egl.createTypeCastException;
		}
		throw creatx( "CRRUI2018E", [ String( x ), "decimal" ] );
	}

	return x;
};

egl.convertBytesToSmallint = function(x)
{
	if ( x.length != 2 )
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ String( x ), "bytes(" + x.length + ")", "smallint" ] );
	}

	if ( x[0] < 0x80 )
	{
		return x[0] << 8 | x[1];
	}
	else
	{
		return x[0] << 8 | x[1] | 0xFFFF0000;
	}
};

egl.convertSmallintToBytes = function( x, length )
{
	if ( length && length !== 2 )
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ String( x ), "smallint", "bytes(" + length + ")" ] );
	}

	var bytes = new Array(2);
	bytes[0] = (x & 0xFF00) >>> 8;
	bytes[1] = x & 0xFF;
	return egl.eglx.lang.EBytes.ezeNew(bytes);
};

egl.convertBytesToInt = function(x)
{
	if ( x.length != 4 )
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ String( x ), "bytes(" + x.length + ")", "int" ] );
	}
	return x[0] << 24 | x[1] << 16 | x[2] << 8 | x[3];
};

egl.convertIntToBytes = function( x, length )
{
	if ( length && length !== 4 )
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ String( x ), "int", "bytes(" + length + ")" ] );
	}

	var bytes = new Array(4);
	bytes[0] = (x & 0xFF000000) >>> 24;
	bytes[1] = (x & 0xFF0000) >>> 16;
	bytes[2] = (x & 0xFF00) >>> 8;
	bytes[3] = x & 0xFF;
	return egl.eglx.lang.EBytes.ezeNew(bytes);
};

egl.convertBytesToBigint = function(x)
{
	if ( x.length != 8 )
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ String( x ), "bytes(" + x.length + ")", "bigint" ] );
	}

	var neg = false;
	var byte, bit;
	if ( x[0] > 0x80 )
	{
		// It'll be a negative number. Have to convert to a positive value so it
		// can be parsed.  Re-negate later.
		neg = true;
		var compliment = new Array(8);
		for (byte = 0; byte < 8; byte++)
		{
			compliment[byte] = ~x[byte] & 0xFF;
		}
		x = compliment;
	}
	
	var bigint = egl.javascript.BigDecimal.prototype.ZERO;
	for ( byte = 0; byte < 8; byte++ )
	{
		var mask = 0x80;
		for ( bit = 0; bit < 8; mask >>>= 1, bit++ )
		{
			if ( (x[byte] & mask) !== 0 )
			{
				bigint = bigint.add( egl.BD_POWERS_OF_2[ byte * 8 + bit - 1 ] ); 
			}
		}
	}
	if ( neg )
	{
		bigint = bigint.negate().subtract( egl.javascript.BigDecimal.prototype.ONE );
	}
	return bigint;
};

egl.convertBigintToBytes = function( x, length )
{
	if ( length && length !== 8 )
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ String( x ), "bigint", "bytes(" + length + ")" ] );
	}

	var bytes = [ 0, 0, 0, 0, 0, 0, 0, 0 ];
	if ( x.signum() !== 0 )
	{
		var binary = egl.convertBigDecimalTo64Bits( x ).join( "" );
		for ( var i = 0; i < 8; i++ )
		{
			bytes[ i ] = parseInt( binary.substr( i * 8, 8 ), 2 );
		}
	}
	
	return egl.eglx.lang.EBytes.ezeNew( bytes );
};

egl.convertBigDecimalTo64Bits = function( x )
{
	// Prereq: the unscaled value of x is between -9223372036854775808
	// and 9223372036854775807, inclusive.
	var positive = true;
	var result = new Array( 64 );
	var i;

	if ( x.scale() != 0 ) x = x.movePointRight( x.scale() );
	if ( x.signum() < 0 )
	{
		// Work with positive numbers <= 9223372036854775807.
		positive = false;
		x = x.negate().subtract( egl.javascript.BigDecimal.prototype.ONE );
	}

	for ( i = 0; i < 63; i++ )
	{
		if ( x.compareTo( egl.BD_POWERS_OF_2[ i ] ) >= 0 )
		{
			result[ i + 1 ] = 1;
			x = x.subtract( egl.BD_POWERS_OF_2[ i ] );
			if ( x.signum() === 0 )
			{
				// All remaining bits are zeros.
				i++;
				while ( ++i < 64 )
				{
					result[ i ] = 0;
				}
			}
		}
		else
		{
			result[ i + 1 ] = 0;
		}
	}
	
	if ( positive )
	{
		result[ 0 ] = 0; // + sign
	}
	else
	{
		result[ 0 ] = 1; // - sign
		
		// This code turns the result into a two's compliment negative number.
		// It has the effect of flipping all the bits, then adding 1.
		result[ 63 ] = result[ 63 ] === 0 ? 1 : 0; 
		var carry = false;
		for ( i = 62; i > 0; i-- )
		{
			if ( result[ i ] === 0 )
			{
				if ( !carry )
				{
					result[ i ] = 1;
				}
			}
			else
			{
				if ( carry )
				{
					carry = false;
				}
				else
				{
					result[ i ] = 0;
				}
			}
		}
	}
	return result;
};

egl.BD_POWERS_OF_2 = [ // This is 2**62 down to 2**0.
	new egl.javascript.BigDecimal("4611686018427387904"), new egl.javascript.BigDecimal("2305843009213693952"),
	new egl.javascript.BigDecimal("1152921504606846976"), new egl.javascript.BigDecimal("576460752303423488"),
	new egl.javascript.BigDecimal("288230376151711744"), new egl.javascript.BigDecimal("144115188075855872"),
	new egl.javascript.BigDecimal("72057594037927936"), new egl.javascript.BigDecimal("36028797018963968"),
	new egl.javascript.BigDecimal("18014398509481984"), new egl.javascript.BigDecimal("9007199254740992"),
	new egl.javascript.BigDecimal("4503599627370496"), new egl.javascript.BigDecimal("2251799813685248"),
	new egl.javascript.BigDecimal("1125899906842624"), new egl.javascript.BigDecimal("562949953421312"),
	new egl.javascript.BigDecimal("281474976710656"), new egl.javascript.BigDecimal("140737488355328"),
	new egl.javascript.BigDecimal("70368744177664"), new egl.javascript.BigDecimal("35184372088832"),
	new egl.javascript.BigDecimal("17592186044416"), new egl.javascript.BigDecimal("8796093022208"),
	new egl.javascript.BigDecimal("4398046511104"), new egl.javascript.BigDecimal("2199023255552"),
	new egl.javascript.BigDecimal("1099511627776"), new egl.javascript.BigDecimal("549755813888"),
	new egl.javascript.BigDecimal("274877906944"), new egl.javascript.BigDecimal("137438953472"),
	new egl.javascript.BigDecimal("68719476736"), new egl.javascript.BigDecimal("34359738368"),
	new egl.javascript.BigDecimal("17179869184"), new egl.javascript.BigDecimal("8589934592"),
	new egl.javascript.BigDecimal("4294967296"), new egl.javascript.BigDecimal("2147483648"),
	new egl.javascript.BigDecimal("1073741824"), new egl.javascript.BigDecimal("536870912"),
	new egl.javascript.BigDecimal("268435456"), new egl.javascript.BigDecimal("134217728"),
	new egl.javascript.BigDecimal("67108864"), new egl.javascript.BigDecimal("33554432"),
	new egl.javascript.BigDecimal("16777216"), new egl.javascript.BigDecimal("8388608"),
	new egl.javascript.BigDecimal("4194304"), new egl.javascript.BigDecimal("2097152"),
	new egl.javascript.BigDecimal("1048576"), new egl.javascript.BigDecimal("524288"),
	new egl.javascript.BigDecimal("262144"), new egl.javascript.BigDecimal("131072"),
	new egl.javascript.BigDecimal("65536"), new egl.javascript.BigDecimal("32768"),
	new egl.javascript.BigDecimal("16384"), new egl.javascript.BigDecimal("8192"),
	new egl.javascript.BigDecimal("4096"), new egl.javascript.BigDecimal("2048"),
	new egl.javascript.BigDecimal("1024"), new egl.javascript.BigDecimal("512"),
	new egl.javascript.BigDecimal("256"), new egl.javascript.BigDecimal("128"),
	new egl.javascript.BigDecimal("64"), new egl.javascript.BigDecimal("32"),
	new egl.javascript.BigDecimal("16"), new egl.javascript.BigDecimal("8"),
	new egl.javascript.BigDecimal("4"), new egl.javascript.BigDecimal("2"),
	egl.javascript.BigDecimal.prototype.ONE ];

egl.convertBytesToDecimal = function(x, decimals, limit)
{
	var digit1, digit2;
	var value = egl.javascript.BigDecimal.prototype.ZERO;
	for ( var i = 0; i < x.length - 1; i++ )
	{
		digit1 = (x[i] & 0xF0) >>> 4;
		digit2 = x[i] & 0x0F;
		if ( digit1 > 9 || digit2 > 9 )
			throw egl.createTypeCastException( "CRRUI2017E", [ String( x ), "bytes", "decimal" ] );
		value = value.movePointRight(2).add(new egl.javascript.BigDecimal(digit1 * 10)).add(new egl.javascript.BigDecimal(digit2));
	}
	digit1 = (x[ x.length - 1 ] & 0xF0) >>> 4;
	if ( digit1 > 9 )
		throw egl.createTypeCastException( "CRRUI2017E", [ String( x ), "bytes", "decimal" ] );
	value = value.movePointRight(1).add(new egl.javascript.BigDecimal(digit1));
	if ( limit && value.compareTo( limit ) > 0 )
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ String( x ), "bytes", "decimal" ] );
	}

	digit2 = x[ x.length - 1 ] & 0x0F; // sign is 0x0C for +, 0x0D for -
	if ( digit2 === 0x0D )
	{
		return value.negate();
	}
	else if ( digit2 !== 0x0C )
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ String( x ), "bytes", "decimal" ] );
	}
	else
	{
		return value;
	}
};

egl.convertDecimalToBytes = function( dec, decSig, length ) 
{
	var decLen, decByteLen;
	var colonIndex = decSig.indexOf( ':' );
	var padding = '';
	if ( colonIndex >= 0 )
	{
		decLen = decSig.substring( 1, colonIndex );
		decByteLen = Math.floor( decLen / 2 + 1 );
		
		if ( length && length !== decByteLen )
		{
			throw egl.createTypeCastException( "CRRUI2017E", [ String( dec ), 'decimal', 'bytes(' + length + ')' ] );
		}
	}
	else if ( length )
	{
		decLen = dec.movePointRight( dec.scale() ).abs().toString().length;
		var actualDecByteLen = Math.floor( decLen / 2 + 1 );
		if ( actualDecByteLen > length )
		{
			throw egl.createTypeCastException( "CRRUI2017E", [ String( dec ), 'decimal', 'bytes(' + length + ')' ] );
		}
		else if ( actualDecByteLen < length )
		{
			padding = '0000000000000000000000000000000'.substr( 0, length - actualDecByteLen );
		}
		decByteLen = length;
	}
	else
	{
		decLen = dec.movePointRight( dec.scale() ).abs().toString().length;
		decByteLen = Math.floor( decLen / 2 + 1 );
	}
	
	var result = new Array( decByteLen );
	var decStr, positive;
	if ( dec.signum() > -1 )
	{
		positive = true;
		decStr = padding + dec.movePointRight( dec.scale() ).toString();
	}
	else
	{
		positive = false;
		decStr = padding + dec.negate().movePointRight( dec.scale() ).toString();
	}

	var bytesIndex = 0, decIndex = 0;
	if ( decLen % 2 === 1 )
	{
		// odd length, so set first nibble of first byte from the string
		result[ 0 ] = (decStr.charCodeAt( 0 ) - 0x30) << 4;
		decIndex = 1;
	}
	while ( decIndex < decStr.length )
	{
		result[ bytesIndex ] |= (decStr.charCodeAt( decIndex ) - 0x30);
		decIndex++;
		bytesIndex++;
		result[ bytesIndex ] = (decStr.charCodeAt( decIndex ) - 0x30) << 4;
		decIndex++;
	}
	
	if ( positive )
	{
		result[ bytesIndex ] |= 0x0C;
	}
	else
	{
		result[ bytesIndex ] |= 0x0D;
	}
	
	return egl.eglx.lang.EBytes.ezeNew( result );
};

egl.convertBytesToSmallfloat = function(x)
{
	if ( x.length != 4 )
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ String( x ), "bytes", "smallfloat" ] );
	}
	var bits = x[0] << 24 | x[1] << 16 | x[2] << 8 | x[3];
	var pos = (bits & 0x80000000) == 0;
	var exp = (bits & 0x7F800000) >>> 23;
	var frac = bits & 0x007FFFFF;
	var value;
	if ( exp == 255 )
	{
		return frac === 0 ? (pos ? Infinity : Number.NEGATIVE_INFINITY) : NaN;
	}
	else if ( exp === 0 )
	{
		if ( frac === 0 )
		{
			return 0;
		}
		// denormalized
		value = frac / 8388608 * 1.1754943508222875e-38; // frac / 2**23 * 2**-126
	}
	else
	{
		frac |= 0x00800000; // insert the implicit leading 1
		value = frac / 8388608 * Math.pow( 2, exp - 127 ); // frac / 2**23 * 2**(exp-127)
	}
	return pos ? value : -value;
};

egl.convertSmallfloatToBytes = function( x, length )
{
	if ( length && length !== 4 )
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ String( x ), "smallfloat", "bytes(" + length + ")" ] );
	}

	//TODO write this
	return egl.eglx.lang.EBytes.ezeNew( 4 ); 
};

egl.convertBytesToFloat = function(x)
{
	if ( x.length != 8 )
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ String( x ), "bytes", "float" ] );
	}
	var pos = (x[0] & 0x80) === 0;
	var exp = ((x[0] & 0x7F) << 4) | ((x[1] & 0xF0) >>> 4);
	var fracBits1_20 = (x[1] & 0x0F) << 16 | x[2] << 8 | x[3];
	var fracBits21_52 = x[4] << 24 | x[5] << 16 | x[6] << 8 | x[7];
	
	if ( exp === 0 && fracBits1_20 === 0 && fracBits21_52 === 0 )
	{
		return 0;
	}
	else if ( exp === 2047 )
	{
		return fracBits1_20 === 0 && fracBits21_52 === 0 ? (pos ? Infinity : Number.NEGATIVE_INFINITY) : NaN;
	}
	
	// Compute the fraction by testing each of its 52 bits.
	var frac = egl.javascript.BigDecimal.prototype.ZERO;
	var mask = 0x080000;
	var i;
	for ( i = 11; i < 31; i++ )
	{
		if ( (fracBits1_20 & mask) !== 0 )
		{
			frac = frac.add( egl.BD_POWERS_OF_2[ i ] );
		}
		mask >>>= 1;
	}
	mask = 0x80000000;
	for ( ; i < 63; i++ )
	{
		if ( (fracBits21_52 & mask) !== 0 )
		{
			frac = frac.add( egl.BD_POWERS_OF_2[ i ] );
		}
		mask >>>= 1;
	}
	
	if ( exp === 0 )
	{
		// denormalized
		exp = -1022;
	}
	else
	{
		frac = frac.add( egl.BD_POWERS_OF_2[ 10 ] ); // insert the implicit leading 1 (so add 2**52)
		exp -= 1023;
	}
	frac = frac.divide( egl.BD_POWERS_OF_2[ 10 ], egl.javascript.BigDecimal.prototype.eglMC ); // divide by 2**52
	frac = Number( frac.toString() ) * Math.pow( 2, exp );
	return pos ? frac : -frac;
};

egl.convertFloatToBytes = function( x, length )
{
	if ( length && length !== 8 )
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ String( x ), "float", "bytes(" + length + ")" ] );
	}

	//TODO write this
//	var bd = new egl.javascript.BigDecimal( x );
//	var str = bd.format( 1, -1, -1, 0, egl.javascript.MathContext.prototype.SCIENTIFIC, -1 );
	return egl.eglx.lang.EBytes.ezeNew( 8 ); 
};

egl.convertAnyToBytes = function( any, nullable, length )
{
	if ( any == null )
	{
		return nullable ? null : egl.eglx.lang.EBytes.ezeNew( length );
	}
	else
	{
		var kind = any.eze$$signature.charAt(0) === '?' ? any.eze$$signature.charAt(1) : any.eze$$signature.charAt(0);
		switch ( kind )
		{
			case 'G':
				if ( length )
				{
					if ( any.eze$$value.length > length )
					{
						return egl.eglx.lang.EBytes.ezeNew( any.eze$$value.slice( 0, any.eze$$value.length - length ) );
					}
					else if ( any.eze$$value.length < length )
					{
						var newBytes = egl.eglx.lang.EBytes.ezeNew( length );
						for ( var i = 0; i < any.eze$$value.length; i++ )
						{
							newBytes[ i ] = any.eze$$value[ i ];
						}
						return newBytes;
					}
				}
				return any.eze$$value;
				
			case 'g':
				if ( length )
				{
					var sig = any.eze$$signature;
					var sigLength = parseInt( sig.substring( sig.indexOf( 'g' ) + 1, sig.indexOf( ';' ) ), 10 );
					if ( length > sigLength )
					{
						throw egl.createTypeCastException( "CRRUI2017E", [ any.eze$$value, egl.typeName( sig ), 'bytes(' + length + ')' ],
								'bytes(' + length + ')', egl.typeName( sig ) );
					}
					else if ( length < sigLength )
					{
						return egl.eglx.lang.EBytes.ezeNew( any.eze$$value.slice( 0, any.eze$$value.length - length ) );
					}
				}
				return any.eze$$value;
				
			case 'I':
				return egl.convertIntToBytes(any.eze$$value, length);

			case 'i':
				return egl.convertSmallintToBytes(any.eze$$value, length);

			case 'B':
				return egl.convertBigintToBytes(any.eze$$value, length);

			case 'F':
				return egl.convertFloatToBytes(any.eze$$value, length);
				
			case 'f':
				return egl.convertSmallfloatToBytes(any.eze$$value, length);
			
			case 'd':
				return egl.convertDecimalToBytes(any.eze$$value, any.eze$$signature, length);
		}
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), 'bytes' ], 'bytes', egl.typeName( any.eze$$signature ) );
};

egl.MAX_INT_BD = new egl.javascript.BigDecimal("2147483647");
egl.MIN_INT_BD = new egl.javascript.BigDecimal("-2147483648");
egl.MAX_SMALLINT_BD = new egl.javascript.BigDecimal("32767");
egl.MIN_SMALLINT_BD = new egl.javascript.BigDecimal("-32768");
egl.MAX_BIGINT_BD = new egl.javascript.BigDecimal("9223372036854775807");
egl.MIN_BIGINT_BD = new egl.javascript.BigDecimal("-9223372036854775808");
egl.MAX_SMALLFLOAT = 3.4028234663852886e38;
egl.MIN_SMALLFLOAT = 1.401298464324817e-45;

egl.convertDecimalToInteger = function( x, upper, lower, creatx )
{
	if ( x.signum() >= 0 )
	{
		x = x.setScale( 0, egl.javascript.BigDecimal.prototype.ROUND_FLOOR );
		if ( x.compareTo( upper ) > 0 )
		{
			var target = 
				upper === egl.MAX_INT_BD ? "int" 
					: upper === egl.MAX_BIGINT_BD ? "bigint" 
						: "smallint";
			if (!creatx)
			{
				creatx = egl.createTypeCastException;
			}
			throw creatx( "CRRUI2018E", [ String( x ), target ] );
		}
	}
	else
	{
		x = x.setScale( 0, egl.javascript.BigDecimal.prototype.ROUND_CEILING );
		if ( x.compareTo( lower ) < 0 )
		{
			var target = 
				upper === egl.MAX_INT_BD ? "int" 
					: upper === egl.MAX_BIGINT_BD ? "bigint" 
						: "smallint";
			if (!creatx)
			{
				creatx = egl.createTypeCastException;
			}
			throw creatx( "CRRUI2018E", [ String( x ), target ] );
		}
	}

	return x;
};

egl.convertDecimalToInt = function( x, creatx )
{
	return Number( egl.convertDecimalToInteger( x, egl.MAX_INT_BD, egl.MIN_INT_BD, creatx ) );
};

egl.convertDecimalToSmallint = function( x, creatx )
{
	return Number( egl.convertDecimalToInteger( x, egl.MAX_SMALLINT_BD, egl.MIN_SMALLINT_BD, creatx ) );
};

egl.convertDecimalToBigint = function( x, creatx )
{
	return egl.convertDecimalToInteger( x, egl.MAX_BIGINT_BD, egl.MIN_BIGINT_BD, creatx );
};

egl.convertFloatToBigint = function( x, creatx )
{
	if ( !isFinite( x ) )
	{
		if (!creatx)
		{
			creatx = egl.createTypeCastException;
		}
		throw creatx( "CRRUI2018E", [ String( x ), "bigint" ] );
	}

	x = new egl.javascript.BigDecimal( (x*1.0).toFixed( 0 ) );
	return egl.convertDecimalToInteger( x, egl.MAX_BIGINT_BD, egl.MIN_BIGINT_BD, creatx );
};

egl.convertFloatToSmallfloat = function( x, creatx )
{
	if ( !isFinite( x ) || Math.abs( x ) > egl.MAX_SMALLFLOAT || (x != 0 && Math.abs( x ) < egl.MIN_SMALLFLOAT) )
	{
		if (!creatx)
		{
			creatx = egl.createTypeCastException;
		}
		throw creatx( "CRRUI2018E", [ String( x ), "smallfloat" ] );
	}
	
	return x;
};

egl.convertStringToInt = function( /*string*/ str ) {
	var decimal = egl.getDecimalSymbol();
	if ( decimal && decimal !== '.' ) 
	{
		str = str.replace(decimal,'.');
	}
	try
	{
		var num = Number( str );
		if ( isFinite( num ) && !(num === 0 && (str === null || /^\s*$/.test(str))) )
		{
			return egl.convertNumberToInt( num );
		}
	}
	catch ( bad ) {}
	throw egl.createTypeCastException( "CRRUI2017E", [ str, "string", "int" ] );
};

egl.convertStringToSmallint = function( /*string*/ str ) {
	var decimal = egl.getDecimalSymbol();
	if ( decimal && decimal !== '.' ) 
	{
		str = str.replace(decimal,'.');
	}
	try
	{
		var num = Number( str );
		if ( isFinite( num ) && !(num === 0 && (str === null || /^\s*$/.test(str))) )
		{
			return egl.convertNumberToSmallint( num );
		}
	}
	catch ( bad ) {}
	throw egl.createTypeCastException( "CRRUI2017E", [ str, "string", "smallint" ] );
};

egl.convertStringToFloat = function( /*string*/ str ) {
	var decimal = egl.getDecimalSymbol();
	if ( decimal && decimal !== '.' ) 
	{
		str = str.replace(decimal,'.');
	}
	var num = Number( str );
	if ( isFinite( num ) && !(num === 0 && (str === null || /^\s*$/.test(str))) ) {
		return num;
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ str, "string", "float" ] );
};

egl.convertStringToDecimal = function( /*string*/ str, decimals, limit ) {
	var decimal = egl.getDecimalSymbol();
	if ( decimal && decimal !== '.' ) 
	{
		str = str.replace(decimal,'.');
	}
	try
	{
		var num = new egl.javascript.BigDecimal( str );
	}
	catch ( oops )
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ str, "string", "decimal" ] );
	}
	return egl.convertDecimalToDecimal( num, decimals, limit );
};

egl.convertStringToBigint = function( str ) {
	var decimal = egl.getDecimalSymbol();
	if ( decimal && decimal !== '.' ) 
	{
		str = str.replace(decimal,'.');
	}
	try
	{
		var num = new egl.javascript.BigDecimal( str );
	}
	catch ( oops )
	{
		throw egl.createTypeCastException( "CRRUI2017E", [ str, "string", "bigint" ] );
	}
	return egl.convertDecimalToInteger( num, egl.MAX_BIGINT_BD, egl.MIN_BIGINT_BD );
};

egl.convertStringToEGLNumber = function( str ) {
	var decimal = egl.getDecimalSymbol();
	if ( decimal && decimal !== '.' ) 
	{
		str = str.replace( decimal,'.' );
	}
	try
	{
		return egl.boxAny( new egl.javascript.BigDecimal( str ) );
	}
	catch ( bad )
	{
		var num = Number( str );
		if ( isFinite( num ) && !(num === 0 && (str === null || /^\s*$/.test(str))) ) 
		{
			return { eze$$value : num, eze$$signature : "?F;" };
		}
		throw egl.createTypeCastException( "CRRUI2017E", [ str, "string", "number" ] );
	}
};

egl.convertDateToInt = function( /*date*/ date ) {
	var refDate = new Date( 1899, 11, 31 );
	var days = ( date.getTime() - refDate.getTime() ) / egl.MS_PER_DAY;
	return egl.convertFloatToInt( days );
};

egl.nullableconcat = function(var1, var2) {
	if (var1 == null || var2 == null)
		return null;
	return var1 + var2;
};

egl.nullableadd = function(var1, var2) {
	if (var1 == null || var2 == null)
		return null;
	if (var1 instanceof egl.javascript.BigDecimal ||
		var2 instanceof egl.javascript.BigDecimal)
	{
		return new egl.javascript.BigDecimal(var1.toString()).add(new egl.javascript.BigDecimal(var2.toString()));
	}
	return Number(var1) + Number(var2);
};

egl.nullablesubtract = function(var1, var2) {
	if (var1 == null || var2 == null)
		return null;
	if (var1 instanceof egl.javascript.BigDecimal ||
		var2 instanceof egl.javascript.BigDecimal)
	{
		return new egl.javascript.BigDecimal(var1.toString()).subtract(new egl.javascript.BigDecimal(var2.toString()));
	}
	return Number(var1) - Number(var2);
};

egl.nullablemultiply = function(var1, var2) {
	if (var1 == null || var2 == null)
		return null;
	if (var1 instanceof egl.javascript.BigDecimal ||
		var2 instanceof egl.javascript.BigDecimal)
	{
		return new egl.javascript.BigDecimal(var1.toString()).multiply(new egl.javascript.BigDecimal(var2.toString()));
	}
	return Number(var1) * Number(var2);
};

egl.divide = function(var1, var2) {
	if (var2 == 0)
		throw egl.createRuntimeException("CRRUI2037E", null);
	return var1 / var2;
};

egl.bitand = function(var1, var2) {
	if (var1 == null || var2 == null)
		throw egl.createNullValueException( "CRRUI2005E", [] );
	return var1 & var2;
};

egl.bitor = function(var1, var2) {
	if (var1 == null || var2 == null)
		throw egl.createNullValueException( "CRRUI2005E", [] );
	return var1 | var2;
};

egl.bitxor = function(var1, var2) {
	if (var1 == null || var2 == null)
		throw egl.createNullValueException( "CRRUI2005E", [] );
	return var1 ^ var2;
};
egl.bitnot = function(var1) {
    if (var1 == null)
        throw egl.createNullValueException( "CRRUI2005E", [] );
    return ~var1;
};

egl.nullabledivide = function(var1, var2) {
	if (var1 == null || var2 == null)
		return null;
	var isfloat1 = (typeof var1 === 'number' && Math.round(var1)!==var1);
	var isfloat2 = (typeof var2 === 'number' && Math.round(var2)!==var2);
	if (isfloat1 || isfloat2)
	{
		var rhs = Number(var2.toString());
		if (rhs == 0)
			throw egl.createRuntimeException("CRRUI2037E", null);
		return Number(var1.toString()) / rhs;
	}
	
	var dec1 = new egl.javascript.BigDecimal(var1.toString());
	var dec2 = new egl.javascript.BigDecimal(var2.toString());
	return dec1.divide(dec2,egl.javascript.BigDecimal.prototype.eglMC);
};

egl.remainder = function(var1, var2) {
	if (var2 == 0)
		throw egl.createRuntimeException("CRRUI2037E", null);
	return var1 % var2;
};

egl.nullableremainder = function(var1, var2) {
	if (var1 == null || var2 == null)
		return null;
	if (var1 instanceof egl.javascript.BigDecimal ||
		var2 instanceof egl.javascript.BigDecimal)
	{
		return new egl.javascript.BigDecimal(var1.toString()).remainder(new egl.javascript.BigDecimal(var2.toString()));
	}
	var rhs = Number(var2);
	if (rhs == 0)
		throw egl.createRuntimeException("CRRUI2037E", null);
	return Number(var1) % rhs;
};

egl.nullablepow = function(var1, var2, forceDecimal) {
	if (var1 == null || var2 == null)
		return null;
	if (forceDecimal || var1 instanceof egl.javascript.BigDecimal || var2 instanceof egl.javascript.BigDecimal)
	{
		return new egl.javascript.BigDecimal(var1.toString()).pow(new egl.javascript.BigDecimal(var2.toString()),egl.javascript.BigDecimal.prototype.eglMC);
	}
	return Math.pow(Number(var1),Number(var2));
};

egl.numberRTTI = function(n) {
	if ( typeof n === "number" )
		return 0;
	if ( n instanceof egl.javascript.BigDecimal )
		return 1;
	if ( n.eze$$value === null )
		return 3;
	var sig = n.eze$$signature; 
	if ( sig === "F;" || sig === "?F;" || sig === "f;" || sig === "?f;" )
		return 2;
	else
		return egl.numberRTTI(n.eze$$value);
};

egl.compareEGLNumbers = function(var1, var1type, var2, var2type) {
	if ( var1type === -1 )
	{
		var1type = egl.numberRTTI(var1);
		var1 = var1.eze$$value;
	}
	if ( var2type === -1 )
	{
		var2type = egl.numberRTTI(var2);
		var2 = var2.eze$$value;
	}
	
	if ( var1type === 2 )
	{
		if ( var2type === 1 )
		{
			var2 = Number(var2.toString());
		}
		return var1 == var2 ? 0 : var1 < var2 ? -1 : 1;
	}
	else if ( var1type === 1 )
	{
		if ( var2type === 2 )
		{
			var1 = Number(var1.toString());
			return var1 == var2 ? 0 : var1 < var2 ? -1 : 1;
		}
		else if ( var2type === 0 )
		{
			var2 = new egl.javascript.BigDecimal(String(var2));
		}
		return var1.compareTo(var2);
	}
	else
	{
		if ( var2type === 1 )
		{
			var1 = new egl.javascript.BigDecimal(String(var1));
			return var1.compareTo(var2);
		}
		return var1 == var2 ? 0 : var1 < var2 ? -1 : 1;
	}
};

egl.divideEGLNumber = function(var1, var1type, var2, var2type) {
	if ( var1type === -1 )
	{
		var1type = egl.numberRTTI(var1);
		if ( var1type === 3 )
		{
			return var1;
		}
		var1 = var1.eze$$value;
	}
	if ( var2type === -1 )
	{
		var2type = egl.numberRTTI(var2);
		if ( var2type === 3 )
		{
			return var2;
		}
		var2 = var2.eze$$value;
	}
		
	if ( var1type === 2 )
	{
		if ( var2type === 1 )
		{
			var2 = Number(var2.toString());
		}
		return egl.boxAny(egl.divide(var1,var2));
	}
	else if ( var1type === 1 )
	{
		if ( var2type === 2 )
		{
			var1 = Number(var1.toString());
			return egl.boxAny(egl.divide(var1,var2));
		}
		else if ( var2type === 0 )
		{
			var2 = new egl.javascript.BigDecimal(String(var2));
		}
		return egl.boxAny(var1.divide(var2,egl.javascript.BigDecimal.prototype.eglMC));
	}
	else
	{
		if ( var2type === 1 )
		{
			var1 = new egl.javascript.BigDecimal(String(var1));
			return egl.boxAny(var1.divide(var2,egl.javascript.BigDecimal.prototype.eglMC));
		}
		return egl.boxAny(egl.divide(var1,var2));
	}
};

egl.powEGLNumber = function(var1, var1type, var2, var2type) {
	if ( var1type === -1 )
	{
		var1type = egl.numberRTTI(var1);
		if ( var1type === 3 )
		{
			return var1;
		}
		var1 = var1.eze$$value;
	}
	if ( var2type === -1 )
	{
		var2type = egl.numberRTTI(var2);
		if ( var2type === 3 )
		{
			return var2;
		}
		var2 = var2.eze$$value;
	}
	
	if ( var1type === 2 )
	{
		if ( var2type === 1 )
		{
			var2 = Number(var2.toString());
		}
		return egl.boxAny(Math.pow(var1,var2));
	}
	else if ( var1type === 1 )
	{
		if ( var2type === 2 )
		{
			var1 = Number(var1.toString());
			return egl.boxAny(Math.pow(var1,var2));
		}
		else if ( var2type === 0 )
		{
			var2 = new egl.javascript.BigDecimal(String(var2));
		}
		return egl.boxAny(var1.pow(var2));
	}
	else
	{
		if ( var2type === 1 )
		{
			var1 = new egl.javascript.BigDecimal(String(var1));
			return egl.boxAny(var1.pow(var2));
		}
		return egl.boxAny(Math.pow(var1,var2));
	}
};

egl.remainderEGLNumber = function(var1, var1type, var2, var2type) {
	if ( var1type === -1 )
	{
		var1type = egl.numberRTTI(var1);
		if ( var1type === 3 )
		{
			return var1;
		}
		var1 = var1.eze$$value;
	}
	if ( var2type === -1 )
	{
		var2type = egl.numberRTTI(var2);
		if ( var2type === 3 )
		{
			return var2;
		}
		var2 = var2.eze$$value;
	}
	
	if ( var1type === 2 )
	{
		if ( var2type === 1 )
		{
			var2 = Number(var2.toString());
		}
		return egl.boxAny(egl.remainder(var1,var2));
	}
	else if ( var1type === 1 )
	{
		if ( var2type === 2 )
		{
			var1 = Number(var1.toString());
			return egl.boxAny(egl.remainder(var1,var2));
		}
		else if ( var2type === 0 )
		{
			var2 = new egl.javascript.BigDecimal(String(var2));
		}
		return egl.boxAny(var1.remainder(var2));
	}
	else
	{
		if ( var2type === 1 )
		{
			var1 = new egl.javascript.BigDecimal(String(var1));
			return egl.boxAny(var1.remainder(var2));
		}
		return egl.boxAny(egl.remainder(var1,var2));
	}
};

egl.addEGLNumber = function(var1, var1type, var2, var2type) {
	if ( var1type === -1 )
	{
		var1type = egl.numberRTTI(var1);
		if ( var1type === 3 )
		{
			return var1;
		}
		var1 = var1.eze$$value;
	}
	if ( var2type === -1 )
	{
		var2type = egl.numberRTTI(var2);
		if ( var2type === 3 )
		{
			return var2;
		}
		var2 = var2.eze$$value;
	}
	
	if ( var1type === 2 )
	{
		if ( var2type === 1 )
		{
			var2 = Number(var2.toString());
		}
		return egl.boxAny(var1 + var2);
	}
	else if ( var1type === 1 )
	{
		if ( var2type === 2 )
		{
			var1 = Number(var1.toString());
			return egl.boxAny(var1 + var2);
		}
		else if ( var2type === 0 )
		{
			var2 = new egl.javascript.BigDecimal(String(var2));
		}
		return egl.boxAny(var1.add(var2));
	}
	else
	{
		if ( var2type === 1 )
		{
			var1 = new egl.javascript.BigDecimal(String(var1));
			return egl.boxAny(var1.add(var2));
		}
		return egl.boxAny(var1 + var2);
	}
};

egl.subtractEGLNumber = function(var1, var1type, var2, var2type) {
	if ( var1type === -1 )
	{
		var1type = egl.numberRTTI(var1);
		if ( var1type === 3 )
		{
			return var1;
		}
		var1 = var1.eze$$value;
	}
	if ( var2type === -1 )
	{
		var2type = egl.numberRTTI(var2);
		if ( var2type === 3 )
		{
			return var2;
		}
		var2 = var2.eze$$value;
	}
	
	if ( var1type === 2 )
	{
		if ( var2type === 1 )
		{
			var2 = Number(var2.toString());
		}
		return egl.boxAny(var1 - var2);
	}
	else if ( var1type === 1 )
	{
		if ( var2type === 2 )
		{
			var1 = Number(var1.toString());
			return egl.boxAny(var1 - var2);
		}
		else if ( var2type === 0 )
		{
			var2 = new egl.javascript.BigDecimal(String(var2));
		}
		return egl.boxAny(var1.subtract(var2));
	}
	else
	{
		if ( var2type === 1 )
		{
			var1 = new egl.javascript.BigDecimal(String(var1));
			return egl.boxAny(var1.subtract(var2));
		}
		return egl.boxAny(var1 - var2);
	}
};

egl.multiplyEGLNumber = function(var1, var1type, var2, var2type) {
	if ( var1type === -1 )
	{
		var1type = egl.numberRTTI(var1);
		if ( var1type === 3 )
		{
			return var1;
		}
		var1 = var1.eze$$value;
	}
	if ( var2type === -1 )
	{
		var2type = egl.numberRTTI(var2);
		if ( var2type === 3 )
		{
			return var2;
		}
		var2 = var2.eze$$value;
	}
	
	if ( var1type === 2 )
	{
		if ( var2type === 1 )
		{
			var2 = Number(var2.toString());
		}
		return egl.boxAny(var1 * var2);
	}
	else if ( var1type === 1 )
	{
		if ( var2type === 2 )
		{
			var1 = Number(var1.toString());
			return egl.boxAny(var1 * var2);
		}
		else if ( var2type === 0 )
		{
			var2 = new egl.javascript.BigDecimal(String(var2));
		}
		return egl.boxAny(var1.multiply(var2));
	}
	else
	{
		if ( var2type === 1 )
		{
			var1 = new egl.javascript.BigDecimal(String(var1));
			return egl.boxAny(var1.multiply(var2));
		}
		return egl.boxAny(var1 * var2);
	}
};

egl.inferSignature = function( x )
{
	if ( x == null )
	{
		return " ;";
	}
	else
	{
		switch ( typeof x )
		{
			case "string": return "S;";
			case "number": 
				return ((""+x).indexOf('.')<0) ? "I;":"F;";
			case "boolean": return "0;";
			case "object":
				if ( "eze$$signature" in x && typeof x.eze$$signature == "string" )
				{
					return x.eze$$signature;
				}
				else if ( "signature" in x && typeof x.signature == "function" )
				{
					return x.signature();
				}
				else if ( x instanceof egl.javascript.BigDecimal )
				{
					var len = x.toString().length;
					var scale = x.scale();
					if ( x.signum() < 0 ) len--;
					if ( scale > 0 ) len--;
					return "d" + len + ":" + scale + ";";
				}
				else if ( x instanceof Date )
				{
					return "J'yyyyMMddHHmmssffffff';";
				}
				else if ( x instanceof Array )
				{
					if ( x.getType && typeof x.getType() == 'string' )
					{
						return "1" + x.getType();
					}
					else if ( x.isEBytes )
					{
						return 'g' + x.length + ';';
					}
					
					var eltSig;
					for ( var n = 0; n < x.length; n++ )
					{
						var sig = egl.inferSignature( x[n] );
						var newEltSig = sig.slice( 0, sig.length - 1 );
						if ( !eltSig )
						{
							eltSig = newEltSig;
						}
						else if ( eltSig !== newEltSig )
						{
							eltSig = "A";
							break;
						}
					}
					if ( !eltSig )
					{
						eltSig = "A";
					}
					return "1" + eltSig + ";";
				}
				else
				{
					var classStr = Object.prototype.toString.apply( x );
					classStr = classStr.substring( 8, classStr.length - 1 );
					classStr = classStr.replace( /\./g, "/" );
					return "T" + classStr + ";";
				}
				break;
		}
		return "T<unknown type>;";
	}
};

egl.typeName = function( signature )
{
	var nullable;
	var kind;

	var firstCharIdx = 0;
	var firstChar = signature.charAt(0);
	if ( firstChar !== '?' )
	{
		nullable = '';
		kind = firstChar;
	}
	else
	{
		nullable = '?';
		kind = signature.charAt(1);
		firstCharIdx = 1;
	}

	switch ( kind )
	{
		case 'T':
			return signature.substring(1,signature.length-1).replace( /\//g, '.' );

		case '1':
			return egl.typeName(signature.substring(1,signature.length)) + '[]';

		case ' ':
			return 'null';

		case 'A':
			return 'any';

		case 'S':
			return 'string' + nullable;

		case 's':
			return 'string(' + signature.substring(firstCharIdx+1, signature.indexOf(';')) + ')' + nullable;

		case 'C':
			return 'char(' + signature.substring(firstCharIdx+1, signature.indexOf(';')) + ')' + nullable;

		case 'D':
			return 'dbchar(' + signature.substring(firstCharIdx+1, signature.indexOf(';')) + ')' + nullable;

		case 'M':
			return 'mbchar(' + signature.substring(firstCharIdx+1, signature.indexOf(';')) + ')' + nullable;

		case 'U':
			return 'unicode(' + signature.substring(firstCharIdx+1, signature.indexOf(';')) + ')' + nullable;

		case 'K':
			return 'date' + nullable;

		case 'L':
			return 'time' + nullable;

		case 'J':
			var start = signature.indexOf('\'') + 1;
			var end = signature.indexOf('\'', start);
			return 'timestamp(' + signature.substring(start, end) + ')' + nullable;

		case 'I':
			return 'int' + nullable;

		case 'i':
			return 'smallint' + nullable;

		case '0':
			return 'boolean' + nullable;

		case 'F':
			return 'float' + nullable;

		case 'f':
			return 'smallfloat' + nullable;

		case 'O':
			return 'number' + nullable;

		case 'B':
			return 'bigint' + nullable;

		case 'b':
			var colon = signature.indexOf(':');
			var decimals = signature.substring(colon+1, signature.indexOf(';'));
			var length = signature.substring(firstCharIdx+1, colon);
			if (decimals != "0")
			{
				return 'bin(' + length + ', ' + decimals + ')' + nullable;
			}
			else
			{
				if (length == "4")
				{
					return 'smallint' + nullable;
				}
				else if (length == "9")
				{
					return 'int' + nullable;
				}
				return 'bigint' + nullable;
			}

		case 'N':
			var colon = signature.indexOf(':');
			return 'num(' + signature.substring(firstCharIdx+1, colon) + ', ' + signature.substring(colon+1, signature.indexOf(';')) + ')' + nullable;

		case 'n':
			var colon = signature.indexOf(':');
			return 'numc(' + signature.substring(firstCharIdx+1, colon) + ', ' + signature.substring(colon+1, signature.indexOf(';')) + ')' + nullable;

		case 'd':
			var colon = signature.indexOf(':');
			return 'decimal(' + signature.substring(firstCharIdx+1, colon) + ', ' + signature.substring(colon+1, signature.indexOf(';')) + ')' + nullable;

		case '9':
			var colon = signature.indexOf(':');
			return 'money(' + signature.substring(firstCharIdx+1, colon) + ', ' + signature.substring(colon+1, signature.indexOf(';')) + ')' + nullable;

		case 'p':
			var colon = signature.indexOf(':');
			return 'pacf(' + signature.substring(firstCharIdx+1, colon) + ', ' + signature.substring(colon+1, signature.indexOf(';')) + ')' + nullable;

		case 'X':
			return 'hex(' + signature.substring(firstCharIdx+1, signature.indexOf(';')) + ')' + nullable;

		case 'Q':
		case 'q':
			var start = signature.indexOf('\'') + 1;
			var end = signature.indexOf('\'', start);
			return 'interval(' + signature.substring(start, end) + ')' + nullable;

		case 'l':
			return 'interval' + nullable;

		case 'W':
			return 'blob';

		case 'Y':
			return 'clob';

		case 'y':
			return 'dictionary';

		case 'a':
			return 'arraydictionary';

		case 'G':
			return 'bytes' + nullable;

		case 'g':
			return 'bytes(' + signature.substring(firstCharIdx+1, signature.indexOf(';')) + ')' + nullable;
	}
		
	return 'unknown';
};

egl.boxAny = function( val, sig )
{
	if ( val == null || (typeof val === "object" && "eze$$value" in val && "eze$$signature" in val) )
	{
		return val;
	}

	if ( !sig )
	{
		sig = egl.inferSignature( val );
	}

	if ( sig.search( /1+A;/ ) !== -1 )
	{
		val = egl.boxElements( val, sig );
	}

	return { eze$$value : val, eze$$signature : sig };
};

egl.boxElements = function( array, sig )
{
	if ( array != null )
	{
		if ( !sig )
		{
			sig = egl.inferSignature( array );
		}

		if ( sig === "1A;" )
		{
			for ( var i = 0; i < array.length; i++ )
			{
				var boxed = egl.boxAny( array[ i ] );
				array[ i ] = boxed;
				if ( boxed != null && boxed.eze$$signature.search( /1+A;/ ) !== -1 )
				{
					egl.boxElements( boxed.eze$$value, boxed.eze$$signature );
				}
			}
		}
		else if ( sig.search( /1+A;/ ) !== -1 )
		{
			for ( var i = 0; i < array.length; i++ )
			{
				array[ i ] = egl.boxElements( array[ i ], sig.substring( 1 ) );
			}
		}
	}
	return array;
};

egl.unboxAny = function( any )
{
	if ( any == null || typeof any !== "object" || !("eze$$value" in any) || !("eze$$signature" in any) )
	{
		return any;
	}
	else
	{
		var val = any.eze$$value;
		if ( any.eze$$signature.search( /1+A;/ ) !== -1 )
		{
			val = egl.unboxElements( val, any.eze$$signature );
		} 
		return val;
	}
};

egl.unboxElements = function( array, sig )
{
	if ( array == null )
	{
		return array;
	}
	
	if ( sig === "1A;" )
	{
		var unboxed = new Array( array.length );
		for ( var i = 0; i < array.length; i++ )
		{
			var boxed = array[ i ];
			if ( boxed != null )
			{
				if ( boxed.eze$$signature.search( /1+A;/ ) !== -1 )
				{
					unboxed[ i ] = egl.unboxElements( boxed.eze$$value || boxed, boxed.eze$$signature );
				}
				else
				{
					unboxed[ i ] = boxed.eze$$value || boxed;
				}
			}
			else
			{
				unboxed[ i ] = null;
			}
		}
		return unboxed;
	}
	else if ( sig.search( /1+A;/ ) !== -1 )
	{
		var unboxed = new Array( array.length );
		sig = sig.substring( 1 );
		for ( var i = 0; i < array.length; i++ )
		{
			unboxed[ i ] = egl.unboxElements( array[ i ], sig );
		}
		return unboxed;
	}
	else
	{
		return array;
	}
};

egl.isa = function( any, signature, anySignature )
{
	if ( any != null )
	{
		if ( anySignature != undefined ) {
			return signature == anySignature;
		}
		var sig = egl.inferSignature( any );
		return ( sig === signature || egl.isSubType( sig, signature ) );
	}
	else
	{
		return false;
	}
};

egl.isSubType = function(subTypeSignature, signature)
{
    var kind = subTypeSignature.charAt(0);
    switch ( kind )
    {
        case 's':
        case 'd':
        case 'J':
            if ( signature.length == 2 ) {
                return (kind.toLowerCase() == signature.charAt(0).toLowerCase());
            } else {
                return false;
            }
    }
    return false;
};

egl.valueString = function( any )
{
	if ( any === null )
	{
		return "null";
	}
	else if ( any.eze$$signature.charAt( 0 ) === 'T' )
	{
		return egl.typeName( any.eze$$signature );
	}
	else
	{
		return String( any.eze$$value );
	}
};

egl.convertAnyToType = function( any, sig )
{
	var isNullable = sig.charAt(0) === '?';
	var kind = sig.charAt(0) === '?' ? sig.charAt(1) : sig.charAt(0);
	switch ( kind )
	{
		case 'A':
			return any;
			
		case 'I':
			return egl.convertAnyToInt(any, isNullable);

		case 'i':
			return egl.convertAnyToSmallint(any, isNullable);

		case '0':
			return egl.convertAnyToBoolean(any, isNullable);
			
		case 'F':
			return egl.convertAnyToFloat(any, isNullable);

		case 'f':
			return egl.convertAnyToSmallfloat(any, isNullable);

		case 'B':
			return egl.convertAnyToBigint(any, isNullable);
		
		case 'N':
		case 'n':
		case 'd':
		case '9':
		case 'p':
		case 'b':
			sig = sig.charAt(0) === '?' ? sig.substring(1) : sig;
			sig = sig.substring(1);
			sig = sig.substring(0,sig.length-1);

			var parts = sig.split(/:/);
			var length=parseInt(parts[0]);
			var decimals = parseInt(parts[1]);

			var limit = "";
			for ( var len=length; len > 0; len-- ) {
			   limit += "9";
			}
			if ( decimals > 0 )	{
			   limit = limit.substring( 0, length - decimals ) + '.' + limit.substring( length - decimals );
			}
			return egl.convertAnyToDecimal(any, decimals, new egl.javascript.BigDecimal(limit), isNullable);
		
		case 'S':
		case 's':
		case 'C':
		case 'D':
		case 'M':
		case 'U':
			return egl.convertAnyToString(any, isNullable);
			
		case 'K':
			return egl.convertAnyToDate(any, isNullable);
			
		case 'L':
			return egl.convertAnyToTime(any, isNullable);
			
		case 'J':
			sig = sig.charAt(0) === '?' ? sig.substring(1) : sig;
			sig = sig.substring(1);
			sig = sig.substring(0,sig.length-1);

			var parts = sig.split(/'/);
			var pattern="";
			if (egl.IE) {
				pattern=parts[0];
			}
			else {
				pattern=parts[1];
			}
			
			return egl.convertAnyToTimestamp(any, isNullable, pattern);
			
		case 'T':
			return egl.convertAnyToNameType(any, sig);
			
		case '1':
			return egl.convertAnyToArrayType(any, sig);
			
		case 'g':
			return egl.convertAnyToBytes(any, isNullable, parseInt(sig.substring(sig.indexOf('g')+1, sig.indexOf(';'))));

		case 'G':
			return egl.convertAnyToBytes(any, isNullable);
	}

	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), egl.typeName( sig ) ], egl.typeName( sig ), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyToRefType = function( any, sig, cons )
{
	if ( any == null )
	{
		return null;
	}
	else if ( (cons && any.eze$$value && any.eze$$value instanceof cons) || any.eze$$signature === sig )
	{
		return any.eze$$value;
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), egl.typeName( sig ) ], egl.typeName( sig ), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyToArrayType = function( any, sig )
{
	if ( any == null )
	{
		return null;
	}
	else if ( any.eze$$signature === sig )
	{
		return any.eze$$value;
	}
	else
	{
		var array;
		var anyeltsig;
		if ( any instanceof Array )
		{
			array = any;
		}
		else if ( any.eze$$value instanceof Array )
		{
			array = any.eze$$value;
			if ( any.eze$$signature.charAt( 0 ) == '1' && any.eze$$signature != '1A;' )
			{
				anyeltsig = any.eze$$signature.substring(1);
			}
		}

		if ( array )
		{
			var eltsig = sig.substring(1);
			var ret = [].setType(eltsig);
			for ( var i = 0; i < array.length; i++ )
			{
				var elt = egl.boxAny( array[i], anyeltsig );
				ret[i] = egl.convertAnyToType(elt,eltsig);
			}
			return ret;
		}
	}

	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), egl.typeName( sig ) ], egl.typeName( sig ), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyToNameType = function( any, sig, cons )
{
	if ( any == null )
	{
		return new cons;
	}
	else if ( any.eze$$signature === sig )
	{
		return any.eze$$value;
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), egl.typeName( sig ) ], egl.typeName( sig ), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyExToNameType = function( anyEx, sig )
{
	if ( anyEx == null )
	{
		return null;
	}
	else if ( egl.inferSignature( anyEx ).toLowerCase() === sig.toLowerCase() )
	{
		return anyEx;
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( anyEx ), egl.typeName( egl.inferSignature( anyEx ) ), egl.typeName( sig ) ], egl.typeName( sig ), egl.typeName( egl.inferSignature( anyEx ) ) );
};

egl.convertAnyToInt = function( any, nullable, creatx )
{
	if ( any == null )
	{
		return nullable ? null : 0;
	}
	else
	{
		var kind = any.eze$$signature.charAt(0) === '?' ? any.eze$$signature.charAt(1) : any.eze$$signature.charAt(0);
		switch ( kind )
		{
			case 'I':
			case 'i':
				return any.eze$$value;

			case 'F':
			case 'f':
				return egl.convertFloatToInt(any.eze$$value, creatx);
			
			case 'O':
				return egl.convertAnyToInt(any.eze$$value, nullable, creatx);
			
			case 'N':
			case 'n':
			case 'd':
			case '9':
			case 'p':
			case 'B':
				return egl.convertDecimalToInt(any.eze$$value, creatx);
			
			case 'S':
			case 's':
			case 'C':
			case 'D':
			case 'M':
			case 'U':
				return egl.convertStringToInt(any.eze$$value);
			
			case 'K':
				return egl.convertDateToInt(any.eze$$value);
				
			case 'G':
			case 'g':
				return egl.convertBytesToInt(any.eze$$value);
		}
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), 'int' + (nullable ? '?' : '') ], 'int' + (nullable ? '?' : ''), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyToSmallint = function( any, nullable, creatx )
{
	if ( any == null )
	{
		return nullable ? null : 0;
	}
	else
	{
		var kind = any.eze$$signature.charAt(0) === '?' ? any.eze$$signature.charAt(1) : any.eze$$signature.charAt(0);
		switch ( kind )
		{
			case 'i':
				return any.eze$$value;

			case 'F':
			case 'f':
				return egl.convertFloatToSmallint(any.eze$$value, creatx);

			case 'O':
				return egl.convertAnyToSmallint(any.eze$$value, nullable, creatx);
			
			case 'I':
				return egl.convertNumberToSmallint(any.eze$$value, creatx);
			
			case 'N':
			case 'n':
			case 'd':
			case '9':
			case 'p':
			case 'B':
				return egl.convertDecimalToSmallint(any.eze$$value, creatx);
			
			case 'S':
			case 's':
			case 'C':
			case 'D':
			case 'M':
			case 'U':
				return egl.convertStringToSmallint(any.eze$$value);
			
			case 'K':
				return egl.convertNumberToSmallint(egl.convertDateToInt(any.eze$$value), creatx);
				
			case 'G':
			case 'g':
				return egl.convertBytesToSmallint(any.eze$$value);
		}
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), 'smallint' + (nullable ? '?' : '') ], 'smallint' + (nullable ? '?' : ''), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyToBigint = function( any, nullable, creatx )
{
	if ( any == null )
	{
		return nullable ? null : 0;
	}
	else
	{
		var kind = any.eze$$signature.charAt(0) === '?' ? any.eze$$signature.charAt(1) : any.eze$$signature.charAt(0);
		switch ( kind )
		{
			case 'B':
				return any.eze$$value;

			case 'I':
			case 'i':
				return new egl.javascript.BigDecimal(any.eze$$value.toString());

			case 'F':
			case 'f':
				return egl.convertFloatToBigint(any.eze$$value,creatx);
			
			case 'O':
				return egl.convertAnyToBigint(any.eze$$value,nullable,creatx);
			
			case 'N':
			case 'n':
			case 'd':
			case '9':
			case 'p':
				return egl.convertDecimalToBigint(any.eze$$value,creatx);
			
			case 'S':
			case 's':
			case 'C':
			case 'D':
			case 'M':
			case 'U':
				return egl.convertStringToBigint(any.eze$$value);
			
			case 'K':
				return new egl.javascript.BigDecimal(egl.convertDateToInt(any.eze$$value).toString());
				
			case 'G':
			case 'g':
				return egl.convertBytesToBigint(any.eze$$value);
		}
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), 'bigint' + (nullable ? '?' : '') ], 'int' + (nullable ? '?' : ''), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyToString = function( any, nullable )
{
	if ( any == null )
	{
		return nullable ? null : '';
	}
	else
	{
		var kind = any.eze$$signature.charAt(0) === '?' ? any.eze$$signature.charAt(1) : any.eze$$signature.charAt(0);
		switch ( kind )
		{
			case 'S':
			case 's':
			case 'C':
			case 'D':
			case 'M':
			case 'U':
			case 'T':
				return any.eze$$value;
			
			case 'K':
				return egl.eglx.lang.StringLib.format(any.eze$$value,"MM/dd/yyyy");
			
			case 'L':
				return egl.timeToString(any.eze$$value, "HH:mm:ss");
			
			case 'J':
				var pattern;
				pattern = any.eze$$signature.substring( any.eze$$signature.charAt(0) !== '?' ? 2 : 3, any.eze$$signature.length - 2 );
				return egl.eglx.lang.StringLib.format(any.eze$$value,pattern);

			case 'I':
			case 'i':
			case 'F':
			case 'f':
			case 'B':
			case '0':
				return any.eze$$value.toString();
			
			case 'N':
			case 'n':
			case 'd':
			case '9':
			case 'p':
				return any.eze$$value.toString( Number(any.eze$$signature.substring(any.eze$$signature.indexOf(':')+1, any.eze$$signature.indexOf(';'))) );

			case 'O':
				return egl.convertAnyToString(any.eze$$value,nullable);
			case 'G':
			case 'g':
				return egl.eglx.lang.EBytes.toString(any.eze$$value);
		}
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), 'string' + (nullable ? '?' : '') ], 'string' + (nullable ? '?' : ''), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyToDecimal = function( any, decimals, limit, nullable, creatx )
{
	if ( any == null )
	{
		return nullable ? null : egl.javascript.BigDecimal.prototype.ZERO;
	}
	else
	{
		var kind = any.eze$$signature.charAt(0) === '?' ? any.eze$$signature.charAt(1) : any.eze$$signature.charAt(0);
		switch ( kind )
		{
			case 'I':
			case 'i':
				return egl.convertIntegerToDecimal(any.eze$$value,limit,creatx);

			case 'N':
			case 'n':
                return egl.convertNumberToDecimal(any.eze$$value,decimals,limit,creatx);
                
			case 'd':
			case '9':
			case 'p':
			case 'B':
				return egl.convertDecimalToDecimal(any.eze$$value,decimals,limit,creatx);

			case 'F':
			case 'f':
				return egl.convertFloatToDecimal(any.eze$$value,decimals,limit,creatx);

			case 'T':
			case 'O':
				return egl.convertAnyToDecimal(any.eze$$value,decimals,limit,nullable,creatx);

			case 'S':
			case 's':
			case 'C':
			case 'D':
			case 'M':
			case 'U':
				return egl.convertStringToDecimal(any.eze$$value,decimals,limit);
			
			case 'K':
				return egl.convertIntegerToDecimal(egl.convertDateToInt(any.eze$$value),limit,creatx);
				
			case 'G':
			case 'g':
				return egl.convertBytesToDecimal(any.eze$$value,decimals,limit);
		}
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), 'decimal' + (nullable ? '?' : '') ], 'decimal' + (nullable ? '?' : ''), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyToFloat = function( any, nullable )
{
	if ( any == null )
	{
		return nullable ? null : 0;
	}
	else
	{
		var kind = any.eze$$signature.charAt(0) === '?' ? any.eze$$signature.charAt(1) : any.eze$$signature.charAt(0);
		switch ( kind )
		{
			case 'I':
			case 'i':
			case 'F':
			case 'f':
				return any.eze$$value;
			
			case 'O':
				return egl.convertAnyToFloat(any.eze$$value,nullable);
			
			case 'N':
			case 'n':
			case 'd':
			case '9':
			case 'p':
			case 'B':
				return Number( any.eze$$value.toString() );
			
			case 'S':
			case 's':
			case 'C':
			case 'D':
			case 'M':
			case 'U':
				return egl.convertStringToFloat(any.eze$$value);
			
			case 'K':
				return egl.convertDateToInt(any.eze$$value);
				
			case 'G':
			case 'g':
				return egl.convertBytesToFloat(any.eze$$value);
		}
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), 'float' + (nullable ? '?' : '') ], 'float' + (nullable ? '?' : ''), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyToNumber = function( any, nullable )
{
	if ( any == null )
	{
		return nullable ? {eze$$signature : '?I;', eze$$value : null} : {eze$$signature : 'I;', eze$$value : 0};
	}
	else
	{
		var kind = any.eze$$signature.charAt(0) === '?' ? any.eze$$signature.charAt(1) : any.eze$$signature.charAt(0);
		switch ( kind )
		{
			case 'I':
			case 'i':
			case 'B':
			case 'F':
			case 'f':
			case 'N':
			case 'n':
			case 'd':
			case '9':
			case 'p':
			case 'b':
				return {eze$$signature : any.eze$$signature, eze$$value : any.eze$$value};

			case 'O':
				return egl.convertAnyToNumber(any.eze$$value,nullable);
				
			case 'S':
			case 's':
			case 'C':
			case 'D':
			case 'M':
			case 'U':
				return {eze$$signature : 'F;', eze$$value : egl.convertStringToFloat(any.eze$$value)};
		}
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), 'number' + (nullable ? '?' : '') ], 'number' + (nullable ? '?' : ''), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyToSmallfloat = function( any, nullable, creatx )
{
	if ( any == null )
	{
		return nullable ? null : 0;
	}
	else
	{
		var kind = any.eze$$signature.charAt(0) === '?' ? any.eze$$signature.charAt(1) : any.eze$$signature.charAt(0);
		switch ( kind )
		{
			case 'I':
			case 'i':
			case 'f':
				return any.eze$$value;
			
			case 'F':
				return egl.convertFloatToSmallfloat(any.eze$$value, creatx);
			
			case 'O':
				return egl.convertAnyToSmallfloat(any.eze$$value, nullable, creatx);
			
			case 'N':
			case 'n':
			case 'd':
			case '9':
			case 'p':
			case 'B':
				return egl.convertFloatToSmallfloat(Number(any.eze$$value.toString()), creatx);
			
			case 'S':
			case 's':
			case 'C':
			case 'D':
			case 'M':
			case 'U':
				return egl.convertFloatToSmallfloat(egl.convertStringToFloat(any.eze$$value), creatx);
			
			case 'K':
				return egl.convertDateToInt(any.eze$$value);
				
			case 'G':
			case 'g':
				return egl.convertBytesToSmallfloat(any.eze$$value);
		}
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), 'smallfloat' + (nullable ? '?' : '') ], 'smallfloat' + (nullable ? '?' : ''), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyToBoolean = function( any, nullable )
{
	if ( any == null )
	{
		return nullable ? null : false;
	}
	else
	{
		var kind = any.eze$$signature.charAt(0) === '?' ? any.eze$$signature.charAt(1) : any.eze$$signature.charAt(0);
		switch ( kind )
		{
			case '0':
				return any.eze$$value;

			case 'I':
			case 'i':
			case 'F':
			case 'f':
				return Boolean(any.eze$$value);

			case 'O':
				return egl.convertAnyToBoolean(any.eze$$value, nullable);

			case 'N':
			case 'n':
			case 'd':
			case '9':
			case 'p':
			case 'B':
				return Boolean(!any.eze$$value.equals(egl.javascript.BigDecimal.prototype.ZERO));
		}
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), 'boolean' + (nullable ? '?' : '') ], 'boolean' + (nullable ? '?' : ''), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyToDate = function( any, nullable )
{
	if ( any == null )
	{
		return nullable ? null : 0;
	}
	else
	{
		var kind = any.eze$$signature.charAt(0) === '?' ? any.eze$$signature.charAt(1) : any.eze$$signature.charAt(0);
		switch ( kind )
		{
			case 'K':
			case 'J':
				return any.eze$$value;

			case 'I':
			case 'i':
				return egl.convertIntToDate(any.eze$$value);

			case 'F':
			case 'f':
				return egl.eglx.lang.RUI.dateFromInt(egl.convertFloatToInt(any.eze$$value));

			case 'O':
				return egl.convertAnyToDate(any.eze$$value, nullable);

			case 'N':
			case 'n':
			case 'd':
			case '9':
			case 'p':
			case 'B':
				return egl.convertIntToDate(egl.convertDecimalToInt(any.eze$$value));
			
			case 'S':
			case 's':
			case 'C':
			case 'D':
			case 'M':
			case 'U':
				return egl.stringToDateWithDefaultSeparator( any.eze$$value, "MMddyyyy" );
		}
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), 'date' + (nullable ? '?' : '') ], 'date' + (nullable ? '?' : ''), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyToTime = function( any, nullable )
{
	if ( any == null )
	{
		return nullable ? null : 0;
	}
	else
	{
		var kind = any.eze$$signature.charAt(0) === '?' ? any.eze$$signature.charAt(1) : any.eze$$signature.charAt(0);
		switch ( kind )
		{
			case 'L':
			case 'J':
				return any.eze$$value;
			
			case 'S':
			case 's':
			case 'C':
			case 'D':
			case 'M':
			case 'U':
				return egl.stringToTimeWithDefaultSeparator( any.eze$$value, "HHmmss" ); 
		}
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), 'time' + (nullable ? '?' : '') ], 'time' + (nullable ? '?' : ''), egl.typeName( any.eze$$signature ) );
};

egl.convertAnyToTimestamp = function( any, nullable, pattern )
{
	if ( any == null )
	{
		return nullable ? null : 0;
	}
	else
	{
		var kind = any.eze$$signature.charAt(0) === '?' ? any.eze$$signature.charAt(1) : any.eze$$signature.charAt(0);
		switch ( kind )
		{
			case 'J':
				return egl.dateTime.extend("timeStamp",any.eze$$value,pattern);

			case 'K':
				return egl.dateTime.extend("date",any.eze$$value,pattern);

			case 'L':
				return egl.dateTime.extend("time",any.eze$$value,pattern);
			
			case 'S':
			case 's':
			case 'C':
			case 'D':
			case 'M':
			case 'U':
				return egl.dateTime.timeStampValueWithPattern(any.eze$$value,pattern);
		}
	}
	throw egl.createTypeCastException( "CRRUI2017E", [ egl.valueString( any ), egl.typeName( any.eze$$signature ), 'timestamp' + (nullable ? '?' : '') ], 'timestamp' + (nullable ? '?' : ''), egl.typeName( any.eze$$signature ) );
};

egl.convertIntToDate = function( day ){
	return egl.eglx.lang.DateTimeLib.dateFromInt(day, 1899);
};

Array.prototype.maxSize=2147483647;

Array.prototype.appendElement = function Array_appendElement(val) {
	if ( this.length < this.maxSize )
	{
		this.push( val );
		return this;
	}
	else
	{
		throw egl.createInvalidIndexException( 'CRRUI2019E', [ val, this.maxSize ], this.maxSize );
	}
};	

Array.prototype.setType = function (type) { 
	this.type = type; 
	return this;
};

Array.prototype.getType = function () { return this.type; };

Array.prototype.assign = function Array_assign(other) {
	this.length = 0;
	for (var n=0; n<other.length; n++) this.push(other[n]);
};

Array.prototype.appendAll = function Array_appendAll(other) {
	for (var n=0; n<other.length; n++) this.push(other[n]);
	return this;
};

Array.prototype.getSize = function Array_getSize() {
	return this.length;
};

Array.prototype.contains = function Array_contains() {
	if (arguments.length > 2)
	{
		return this[arguments[1]]["contains"](arguments[0],arguments[2]);
	}
	var searchObject = arguments[0];
	var startingIndex = 0;
	if (arguments.length==2)
		startingIndex=arguments[1]-1;
	if (startingIndex < 0)
		startingIndex=0;
	for (var n=startingIndex; n < this.length; ++n) {
		if ( egl.unboxAny(this[n]) == searchObject ) {
			return true;
		}
	}
	return false;
};

Array.prototype.insertElement = function Array_insertElement(val, index) {
	//Remember EGL is 1-based, JS 0-based
	if ( index < 1 || index > this.length + 1 ) 
		throw egl.createRuntimeException( "CRRUI2022E", [ index, this.length ] );
	if ( this.length < index ) {
		this.push( val );
		return this;
	}
	this.splice( index-1, 0, val );
	return this;
};

Array.prototype.setElement = function Array_setElement(val, index) {
	if ( index < 1 || index > this.length + 1 ) 
		throw egl.createRuntimeException( "CRRUI2022E", [ index, this.length ] );
	this[index-1] = val;
};

Array.prototype.removeAll = function Array_removeAll(val) {
	this.length = 0;
};

Array.prototype.removeElement = function Array_removeElement(index) {
	this.splice( index-1, 1 );
};

Array.prototype.indexOfElement = function Array_indexOfElement(val) {
	if(this.indexOf){
		return this.indexOf(val) + 1;
	}else{
		for (var i = 0; i < this.length; i++) {
			if (this[i] == val) {
				return i + 1;
			}
		}
		return 0;
	}
};

Array.prototype.ezekw$$sort = function Array_sort(func) {
	var isAny = false;
	for(var i=0; i < this.length; i++){
		var val = this[i];
		if ( val == null || (typeof val === "object" && "eze$$value" in val && "eze$$signature" in val) ){
			isAny = true;
		}else{
			this[i] = egl.boxAny(val);
		}
	}
	this.sort(func);
	for(var i=0; !isAny && i < this.length; i++){		
		this[i] = egl.unboxAny(this[i]);
	}
};

Array.prototype.forEach = function(f, thisp) {
	for (var n=0, len=this.length; n<len; n++) {
		f.call(thisp, this[n]);
	}
};

egl.initialValueForType = function ( elementType ) 
{
	var kind = elementType.charAt(0);
	switch ( kind )
	{
		case 'A':
		case '?':
			return null;
		case 'I':
		case 'i':
		case 'F':
		case 'f':
			return 0;
		case 'O':
			return {eze$$signature : 'I;', eze$$value : 0};
		case 'S':
		case 'U':
		case 'C':
		case 's':
			return "";
		case '0':
			return false;
		case 'd':
		case 'N':
		case '9':
		case 'B':
			return egl.javascript.BigDecimal.prototype.ZERO;
		case 'K':
			var date = new Date();
			date.setHours( 0 );
			date.setMinutes( 0 );
			date.setSeconds( 0 );
			date.setMilliseconds( 0 );
			return date;
		case 'L':
		case 'J':
			var time = new Date();
			time.setMilliseconds( 0 );
			return time;
	    case 'T':
	        elementType = elementType.replace( /\//g, "\." );
	        var index = elementType.lastIndexOf( '.' );
	        index = index < 0 ? 0 : index;
	        var packageName = elementType.substr( 1, index - 1 );
	        var typeName = elementType.substring( index + 1, elementType.length - 1 );
	        var pkg = egl.makePackage( packageName );
	        return new pkg[ typeName ]();
		default:
			throw egl.createRuntimeException( "CRRUI2034E", [ elementType ] );
	}
};

Array.prototype.resize = function Array_resize(val) {
	if (val < 0) {
		throw egl.createInvalidArgumentException("CRRUI2033E", [val]);
	}
	if (val <= this.length) {
		this.length = val;
		return;
	}
	var elementType = this.getType();
	if ( typeof elementType == 'string' ) {
	     //the Array type is "[X;", X is the element type.
	     //Below line gets the element type
	     elementType = elementType.substring( 1, elementType.length );
	}
	
	for (var n=this.length; n < val; n++) {
		if ( typeof elementType == 'function')
		{
			var obj = new elementType();
			if ( !(obj instanceof egl.egl.jsrt.Record) )
				this.push( null );
			else
				this.push( obj );
		}
		else if ( typeof elementType == 'string' )
		{
			this.push( egl.initialValueForType( elementType ) );
		}
		else if ( elementType == null )
		{
			this.push( null );
		}
		else
		{
			this.push( {} );
		}
	}
};

egl.resizeAll = function egl_resizeAll(array, sizes) {
	if (array) {  
		if ( sizes.length == 1 )
		{
			array.resize( sizes[0] );
		}
		else
		{
				// resize this level of the array
			if ( array.length >= sizes[0] )
			{
				array.length = sizes[0];
			}
			else
			{
				var arrayToAdd = sizes[0] - array.length;
				for ( var i = 0; ( i < arrayToAdd ); i++ )
				{
					var newArray = [];
					newArray.setType( array.getType() );
					newArray.maxSize = array[0].maxSize;
					array.push( newArray );
				} 
			}
				// delegate to the next level of resizeAll
			for ( var i = 0; ( i < array.length ); i++ )
			{
				egl.resizeAll( array[i], sizes.slice(1) );
			}
		}
	}
	else
	{
		throw egl.createRuntimeException( "CRRUI2035E", null );
	}
};

egl.move = function egl_move(source, target, byName, byPosition, forAll, count) {
	for (f in source) {
		if (!egl.isUserField(source, f))
			continue;
		if (typeof(source[f])=="object") {
			egl.move(source[f], target[f] = {});
		}
		else {
			target[f] = source[f];
		}
	}
	return source;
};

Array.prototype.resizeAll = function Array_resizeAll(sizes) {
	egl.resizeAll(this, sizes);
};

Array.prototype.setMaxSize = function Array_setMaxSize(val) 
{
	if ( val < 0 || this.length > val )
	{
		throw egl.createRuntimeException( 'CRRUI2020E', [ val ] );
	}
	this.maxSize = val;
};

Array.prototype.getMaxSize = function Array_getMaxSize() {
	return this.maxSize;
};

Array.prototype.setMaxSizes = function Array_setMaxSizes(sizes) {
	this.setMaxSize( sizes[0]);
	if ( sizes.length > 1 ) {
		for (var n=0; n<this.length; n++) {
			this[n].setMaxSizes( sizes.slice(1) );
		}
	}
};

Array.prototype.eze$$getName = function() {
	return "";
};

//Wrapper for internal validators that need extra parameters.
egl.defineClass(
		'org.eclipse.edt.rui.mvc.internal', 'ValidatorWrapper',
{
	"constructor" : function( context, func ) {
		this.context = context;
		this.func = func;
		this.params = [];
		for (var i = 2; i < arguments.length; i++) {
			this.params.push(arguments[i]);
		}
	},
	"validate" : function( value ) {
		var newParams = this.params.slice();
		newParams.splice(0, 0, value);
		return this.func.apply(this.context, newParams);
	}
});

egl.defineClass(
		'org.eclipse.edt.rui.mvc.internal', 'FormatterWrapper',
{
	"constructor" : function( context ) {
		this.context = context;
		this.params = [];
		for (var i = 1; i < arguments.length; i++) {
			this.params.push(arguments[i]);
		}
	},
	"format" : function( value ) {
		var newParams = this.params.slice();
		newParams.splice(0, 0, value);
		return this.context.format.apply(this.context, newParams);
	},
	"unformat" : function( value ) {
		var newParams = this.params.slice();
		newParams.splice(0, 0, value);
		return this.context.unformat.apply(this.context, newParams);
	}
});

egl.defineClass( "egl.core", "SysVar", {
	"constructor" : function() {
		this.arrayIndex = (0);
		this.overflowIndicator = (0);
	},
	
	"eze$$getName" : function() {
		return "egl.core.SysVar";
	}
});

egl.eq = function(a,b) { return a==b; };
egl.le = function(a,b) { return a<=b; };
egl.lt = function(a,b) { return a<b; };
egl.ge = function(a,b) { return a>=b; };
egl.gt = function(a,b) { return a>b; };
egl.ne = function(a,b) { return a!=b; };
egl.neg = function(v) 
{
	if ( v instanceof egl.javascript.BigDecimal )
	{
		return v.negate();
	}
	else if ( typeof v === "object" && "eze$$value" in v )
	{
		if ( v.eze$$value === null )
		{
			return v;
		}
		return { eze$$value : egl.neg(v.eze$$value), eze$$signature : v.eze$$signature };
	}
	else
	{
		return -v;
	}
};
egl.nullableNeg = function(v) {	
	if (v===null)
		return null;
	return egl.neg(v); 
};

egl.nullableSubstring = function(s, i1, i2) { return (s==null || i1==null || i2==null) ? null:s.substring(i1-1,i2); };
egl.nullableSplice = function(s1, i1, i2, s2) { return (s1==null || s2==null || i1==null || i2==null) ? null:s1.splice(i1,i2,s2); };
egl.nullableCheckIndex = function(s, i1) { return (s==null || i1==null) ? null:s[s.checkIndex(i1-1)]; };
egl.nullableGetRow = function(s, i1) { return (s==null || i1==null) ? null:s.getRow(i1-1); };

egl.nullableCompare = function(v1, v2, falseAnswer) {	
	if (v1==null || v2==null)
	{
		return falseAnswer; 
	}

	if (v1 instanceof egl.javascript.BigDecimal)
		if (v2 instanceof egl.javascript.BigDecimal)
			return v1.compareTo(v2);
		else
			return v1.compareTo(new egl.javascript.BigDecimal(v2.toString()));
	else if (v2 instanceof egl.javascript.BigDecimal)
		return new egl.javascript.BigDecimal(v1.toString()).compareTo(v2);

	if (v1 == v2)
		return 0;
	return (v1 > v2) ? 1 : -1; 
};

//this is a simple version for "systemType is systemName"
//the generator spits out a string literal for the systemName (since sysVar.systemType is a string)
//e is a potential comparator.  if none is provided, == operator used
egl.is = function(l,r,e) {
    if ( l != null && l.eze$$value ) {
        l = l.eze$$value;
    }
    if ( l == null ) {
        return false;
    }
    return (e) ? e(l.toString(),r) : (l == r); 
};

egl.dateEquals = function(/*date*/ a, /*date*/ b, falseAnswer) {
	if ( !a && !b ) {
		//both null
		return true;
	}
	else if ( (a && !b) || (!a && b) ) {
		//one is null, the other one isn't
		return falseAnswer;
	}
	else {
		return ( a.getFullYear() == b.getFullYear() &&
			 a.getMonth() == b.getMonth() &&
			 a.getDate() == b.getDate() );
	}
};

egl.timeEquals = function(/*time*/ a, /*time*/ b, falseAnswer) {
	if ( !a && !b ) {
		//both null
		return true;
	}
	else if ( (a && !b) || (!a && b) ) {
		//one is null, the other one isn't
		return falseAnswer;
	}
	else {
		return ( a.getHours() == b.getHours() 
			&& a.getMinutes() == b.getMinutes()
			&& a.getSeconds() == b.getSeconds() );
	}
};

egl.timeStampEquals = function(/*timestamp*/ a, /*timestamp*/ b, falseAnswer) {
	if ( !a && !b ) {
		//both null
		return true;
	}
	else if ( (a && !b) || (!a && b) ) {
		//one is null, the other one isn't
		return falseAnswer;
	}
	else {
		return egl.dateEquals( a, b, falseAnswer ) 
				&& egl.timeEquals( a, b, falseAnswer )
				&& a.getMilliseconds() == b.getMilliseconds();
	}
};



// Constructor for a Delegate (function reference)
// This encapsulates the function and its context in a wrapper function for
// easy invocation
(function() {
	var pkg = egl.makePackage( 'egl.jsrt' );
	if (pkg['Delegate']) { return; }
	
	pkg['Delegate']=function(context,func,name) {
	
		// Invoke the wrapped function in the proper context, with the supplied
		// arguments.
		var d=function() {
			try {
				return func.apply(context, arguments);
			}
			catch (e) {
				if ( e instanceof egl.eglx.lang.AnyException || (egl.egl.debug && e instanceof egl.egl.debug.DebugTermination))
				{
					throw e;
				}
				throw egl.createRuntimeException( "CRRUI2002E", [ context.eze$$typename, egl.lastFunctionEntered, e.message || e ] );				
			}
		};
		
		// Embed some information in the wrapper for identification purposes
		d.f=func;
		if (name) d.n=name;
		
		// Delegates pointing at the same function are equal
		d.eq=function(o) {
			return this.f === o.f;
		};
		
		// add methods for debug
		d.toString=function() {
		  return this.n;
		};
		
		d.eze$$getChildVariables=function() {
			return [];	
		};
		
		return d;
	};
})();

egl.valueByKey = function egl_valueByKey( object, key, value, signature ) 
{
	if (object == null) {
		return null;
	}
	if ( "eze$$value" in object )
	{
		object = object.eze$$value;
	}
	if (object == null) {
		return null;
	}
	var objectIsDictionary = object instanceof egl.eglx.lang.EDictionary;
	var objectSupportsLookup = !objectIsDictionary && "eze$$getFieldSignatures" in object;
	if (!(objectSupportsLookup || objectIsDictionary)) {
		throw egl.createRuntimeException( "CRRUI2024E", [key,egl.inferSignature(object)] );
	}
	
	var result = egl.findByKey(object, key);
	if ( result !== undefined && result !== null && ( typeof result !== "object" || !("eze$$value" in result) ) )
	{
		var sig = egl.inferSignature(result);
		if (objectSupportsLookup) {
			var fields = object.eze$$getFieldSignatures();
			for (var i=0; i<fields.length; i++) {
				if (key === fields[i].name) {
					sig = fields[i].sig;
					break;
				}
			}
		}
		result = egl.boxAny( result, sig );
	}
	if (result === undefined) {
		if ((objectSupportsLookup && !egl.findKey(object, key))|| (objectIsDictionary && value === undefined)) {
			throw egl.createRuntimeException( "CRRUI2025E", [key, egl.inferSignature(object)] );
		}
	}
	if (value !== undefined) 
	{
		value = egl.boxAny( value, signature );
		if (objectSupportsLookup)
		{
			var sig;
			var fields = object.eze$$getFieldSignatures();
			for (var i=0; i<fields.length; i++) {
				if (key === fields[i].name) {
					sig = fields[i].sig;
					break;
				}
			}
			value = egl.convertAnyToType(value,sig);
		}
		if ( objectIsDictionary && object.eze$$caseSensitive==false )
			key = key.toLowerCase();
		object[key] = value;
		result = object[key];
	}
	if (egl.traceInternals && result === undefined && value === undefined) {
		egl.addTraceEvent(
			"<div style='border: solid orange 4px; padding:4; color:orange'>"+
			"<b>WARNING</b>: " + 
			"field '"+key+"' not found.<hr>" +
			"<font color=orange>object="+
			egl.eglx.json.toJSONString(object)+"</font><br></div>");
	}
	return result;
};

egl.containsKey = function egl_containsKey( object, key ) 
{	
	var caseSensitive = true;
	if ( (object.eze$$caseSensitive != undefined) && object.eze$$caseSensitive==false )
	{
		caseSensitive = false;
		key = key.toLowerCase();
	}
	for (f in object) {
		var field = (caseSensitive)?f:f.toLowerCase();
		if (key == field) {
			return true;
		}
	}
	return false;
};

egl.findByKey = function egl_findByKey( object, key ) {
	egl.checkWork();
	try {
		var caseSensitive = true;
		if ( (object.eze$$caseSensitive != undefined) && object.eze$$caseSensitive==false )
		{
			caseSensitive = false;
			key = key.toLowerCase();
		}
		for (f in object) {
			var field = (caseSensitive)?f:f.toLowerCase();
			if (key == field) {
				return object[f];
			}
		}
	}
	catch (e) {
		throw egl.createRuntimeException( "CRRUI2103E", [ key, typeof(object), object, e.message] );
	}
	return undefined;
};

egl.findKey = function egl_findKey( object, key ) {
	egl.checkWork();
	try {
		var caseSensitive = true;
		if ( (object.eze$$caseSensitive != undefined) && object.eze$$caseSensitive==false )
		{
			caseSensitive = false;
			key = key.toLowerCase();
		}
		for (f in object) {
			var field = (caseSensitive)?f:f.toLowerCase();
			if (key == field) {
				return true;
			}
		}
	}
	catch (e) {
		throw egl.createRuntimeException( "CRRUI2103E", [ key, typeof(object), object, e.message] );
	}
	return false;
};

egl.getKeys = function egl_getKeys(object) {
	egl.checkWork();
	var result = [];
	for (field in object) {
		if (egl.isUserField(object, field)) {
			result.push(field);
		}
	}
	if ( (object.eze$$byKeyOrdering != undefined) && 
		 (object.eze$$byKeyOrdering.value == 1) )
		return result.sort();
	else 
		return result;
};

egl.getValues = function egl_getValues(object) {
	var keys = egl.getKeys(object);
	var result = [];
	for (var n=0; n<keys.length; n++) {
		result.push(object[keys[n]]);
	}
	return result;
};

egl.clone = function egl_clone(source) {

	if ( source === null || typeof(source)!="object") {
		return source;
	}
	
	var target;
	if ( source instanceof Array ) {
		target = [];
	}
	else if (source.eze$$thisClass != undefined){
		target = new source.eze$$thisClass();
	}
	else {
		target = {};
	}
	
	for (f in source) {
		if (typeof(source[f])=="object") {
			target[f] = egl.clone(source[f]);
		}
		else {
			target[f] = source[f];
		}
	}
	return target;
};

egl.insert = function egl_insert(target, key, value) {
	if (typeof(value)=="object") {
		target[key] = egl.clone(value);
	}
	else {
		return (target[key] = value);
	}
};

egl.insertAll = function egl_insertAll(target, source) {
	for (f in source) {
		if (egl.isUserField(source,f)) {
			target[f] = source[f];
		}
	}
};

egl.removeElement = function egl_removeElement(object, key) {
	key = object.eze$$caseSensitive ? key : key.toLowerCase() ;
	for (f in object) {
		var field = (object.eze$$caseSensitive)?f:f.toLowerCase();
		if (key == field) {
			delete object[f];
			return;
		}
	}
};

egl.removeAll = function egl_removeAll(object) {
	for (field in object) {
		if ( egl.isUserField(object, field) ) {
			delete object[field];
		}
	}
};

egl.size = function egl_size(object) {
	var count = 0;
	for (field in object) {
		if (egl.isUserField(object, field))   
			count++;
	}
	return count;
};

egl.internalFieldMatcher = /eze\$\$/;

egl.isUserField = function(object, field) {
	return typeof(object[field]) != "function" && 
			!field.match(egl.internalFieldMatcher);
};
	
String.prototype.splice = function( first, last, instr ) {
	if ( last > this.length )
		last = this.length;
	if ( first > last )
		return this;
	if ( first < 1 )
		first = 1;
	var regionLength = last - first + 1;
	if (regionLength === instr.length)
		return this.substring(0, first-1) + instr + this.substring(last);
	if (regionLength < instr.length)
		return this.substring(0, first-1) + instr.substring(0,regionLength) + this.substring(last);
	return this.substring(0, first-1) + instr + egl.eglx.lang.StringLib.spaces(regionLength - instr.length) + this.substring(last);
};

egl.isspace = function(c) { 
	return c.match(/\s/); 
};
egl.isblanks = function(s) {
	if (s == null) return true;
	s = egl.trim(s);
	return s.length==0;
};
egl.isnumeric = function(s) {
	if (s == null) return true;
	return !isNaN(parseFloat(s)) && isFinite(s);
};
egl.isdoubledigit = function(s /*Used for date/time*/) {
	if(s.charAt(0) < '0' || s.charAt(0) > '9' || s.charAt(1) < '0' || s.charAt(1) > '9'){
		return false;
	}
	return true;	
};

egl.trim = function(s) {
	return s.replace(/^\s+|\s+$/g,"");
};

String.prototype.trim = function() {
	return this.replace(/^\s+|\s+$/g,"");
}
	
String.prototype.trimRight = function() {
	var n = this.length;
	while (egl.isspace(this.charAt(n-1))) 
		--n;
	return this.substring(0,n);
};
	
String.prototype.cmp = function(s2,eq) {
	s2 = (s2 && s2.toString()) || "";
	if (eq) return (this==s2) ? 0 : 1;
	return (this<s2) ? -1 : (this>s2) ? 1 : 0;
};
	
String.prototype.eq = function(s) {
	return this.cmp(s,true) == 0;
};
	
String.prototype.ne = function(s) {
	return this.cmp(s,true) != 0;
};
	
String.prototype.gt = function(s) {
	return this.cmp(s) > 0;
};
	
String.prototype.ge = function(s) {
	return this.cmp(s) >= 0;
};

String.prototype.lt = function(s) {
	return this.cmp(s) < 0;
};
	
String.prototype.le = function(s) {
	return this.cmp(s) <= 0;
};

egl.defineClass( "egl.jsrt", "Record", {
	"constructor" : function() {
		if (arguments.length > 0) 
			this.assign(arguments[0]);
		else
			this.eze$$fieldNames = [];
		this.eze$$isNull = false;
		this.eze$$isNullable = false;
		this.eze$$caseSensitive = false;
	},
	
	"eze$$setEmpty" : function() {
	},
	
	"eze$$clone" : function() {
	},
	
	"eze$$assign" : function() {
	},
	
	"setNull" : function(bNull) {
		if (this.eze$$isNullable){
			this.eze$$isNull = Boolean(bNull);
		}
		else if (Boolean(bNull)) {
			this.eze$$setEmpty();
		}
	},
	
	"isNull" : function(bNull) {
		return (this.eze$$isNullable && this.eze$$isNull);
	},
	
	"assign" : function(array) {
		for (var n=0; n<array.length; n++) {
			this[array[n][0]] = array[n][1];
		}
	},
	
	"getValues" : function() {
		var ret = [];
		for (var f in this) {
			if ( typeof this[f] == "function" ) continue;
			if ( !this.propertyIsEnumerable(f) ) continue;
			ret.appendElement( this[f] );
		}
		return ret;
	},

	/*
	 * Return the signature (Java Style) for this object
	 */
	"signature" : function() {
		var returnValue = "T";
		if ( this.eze$$package.length>0 ) returnValue += (this.eze$$package.replace( /\./g, "/" ) + "/");
		returnValue += ( this.eze$$typename + ";" );
		return returnValue;
	}
	,
	"eze$$getChildVariables" : function() {
		if (!(this.eze$$fieldNames)) {
			return null;
		}
		
		var childVars = [];
		for (var i=0; i<this.eze$$fieldNames.length; i++) {
			childVars.push({name : this.eze$$fieldNames[i], value : this[this.eze$$fieldNames[i]]});			
		}
		
		return childVars;
	}
	,
	"eze$$getJSONName": function(/*string*/ fieldName) {
		var jsonNames = this.eze$$getJSONNames();
		for (var n=0; n<jsonNames.length; n++) {
			if (jsonNames[n][0] == fieldName)
			{
				return jsonNames[n][1];
			}
		}
		return fieldName;
	}
	,
	"eze$$getFORMName": function(/*string*/ fieldName) {
		var formDataNames = this.eze$$getFORMDataNames();
		for (var n=0; n<formDataNames.length; n++) {
			if (formDataNames[n][0] == fieldName)
			{
				return formDataNames[n][1];
			}
		}
		return fieldName;
	}
	,
	"eze$$getFORMDataNames": function() {
		return [];
	}
	,
	"eze$$getJSONNames": function() {
		return [];
	}
});

egl.formatMessage = function( msg, inserts )
{
	var formattedText = msg;
	if ( inserts != null )
	{
		for ( var i = 0; i < inserts.length ; i++ )
		{
			var pattern = "{" + i + "}";
			formattedText = formattedText.replace( pattern, inserts[i] );
		}
	}
	return formattedText;
};

egl.getRuntimeMessage = function( rscKey, inserts ) 	
{
	var rscName = "RuiMessages";
	var rscString = rscKey;
	
	if ( ( rscName != null ) && ( egl.eze$$rscBundles[rscName] != undefined ) )
	{
		var rscBundle = egl.eze$$rscBundles[rscName];
		if ( (rscKey!=null) && (rscBundle[rscKey] != null) )
		{
			rscString = rscBundle[rscKey];
			rscString = egl.formatMessage( rscString, inserts );
		}
	}
	if ( rscKey != undefined && rscString == rscKey )
	{
		for ( f in egl.eze$$rscBundles )
		{
			var rscBundle = egl.eze$$rscBundles[ f ];
			if (( f != "RuiMessages") && (rscKey!=null) && (rscBundle[rscKey] != null) )
			{
				rscString = rscBundle[rscKey];
				rscString = egl.formatMessage( rscString, inserts );
				break;
			}
		}
	}
	if ( rscString != rscKey )
	{
		rscString = "[" + rscKey + "] " + rscString; 
	}
	return rscString;
};

egl.defineClass(
		'eglx.ui.rui', 'RUIPropertiesLibrary',
		{
		"constructor" : function() {
			this.normalizedKeys = {};
		},
		"eze$$initializeProperties" : function(/*String*/ propertiesFile){
			var bundle = egl.eze$$rscBundles[propertiesFile];
			if(bundle == null){
				// Missing bundle
				var path = 'properties/' + propertiesFile + '-' + egl__htmlLocale + '.properties';
				throw egl.createRuntimeException( 'CRRUI2015E', [ path, propertiesFile ] );
			}
			// Every RUIPropertiesFile has a getProperties function, which returns an array of the fields
			// defined in the library
			var keys = this.eze$$getProperties();
			for ( var i = 0; i < keys.length; i++ ) {
				var key = keys[i];
				var value = bundle[key];
				if(value == null){
					// Missing property, set the value to the key
					this[key] = key;
				}else{
					this[key] = value;
				}
				// Store a normalized link back to the original key
				this.normalizedKeys[key.toLowerCase()] = key;
			}
		},	
		"eze$$getValueWithNormalizedKey" : function(/*String*/ key){
			var value = this[this.normalizedKeys[ key.toLowerCase() ]];
			if(value == null){
				value = "";
			}
			return value;
		},
		"getMessage" : function(/*String*/ key, /*String[]*/ inserts){
			return egl.formatMessage(key, inserts);
		},
		"eze$$getChildVariables": function() {
			return [];	
		}
});

Array.prototype.checkIndex = function Array_checkIndex( idx )
{
	if ( ( idx >= this.length ) || ( idx < 0 ) )
	{
		throw egl.createInvalidIndexException( 'CRRUI2022E', [ idx + 1, this.length ], idx + 1 );
	}
	else
		return idx;
};

egl.dateTime = {};
egl.dateTime.extend = function(/*type of date*/ type, /*extension*/ date, /*optional mask*/pattern ) {
	//Converts a timestamp, time, or date into a longer or shorter timestamp value.
	if ( !date ) {
		return null;
	}
	
	//copy is returned if the pattern is invalid
	var dateCopy = new Date( date );
	// IE Bug: the milli second is not copied
	dateCopy.setMilliseconds( date.getMilliseconds() );
	var now = new Date();
	type = type.toLowerCase();
	
	if ( type == "date" ) {
		date.setHours( 0 );
		date.setMinutes( 0 );
		date.setSeconds( 0 );
		date.setMilliseconds( 0 );
		if (!pattern || pattern == "" )
			pattern = "yyyyMMddHHmmss";
	}
	else if ( type == "time" ) {
		date.setFullYear( 2000 );
		date.setMonth( 0 );//Bug 372937 changed from current month due to issues with February 29
		date.setDate( 1 ); //Bug 372937 changed from current date due to issues with February 29
		date.setMilliseconds( 0 );
		if (!pattern || pattern == "" )
			pattern = "yyyyMMddHHmmss";
	}
	else { // ( type == "timestamp" )
		if (!pattern || pattern == "" )
			pattern = "yyyyMMddHHmmssffffff";
	}
	
	//enforce the pattern mask
	//first function is for pattern missing chars on the left side (current)
	//second function is for pattern missing chars on the right side (zeros)
	var chars = 
		[ // bug 365262, change to leap year 2000 to accept date like "0229"
		//Bug 372937 changed from current month and date due to issues with February 29
		  [ "y", function(d){ d.setFullYear( 2000 ); }, function(d){ d.setFullYear( 0 ); } ],
	      [ "M", function(d){ d.setMonth( 0 ); }, function(d){ d.setMonth( 0 ); } ], 
	      [ "d", function(d){ d.setDate( 1 ); }, function(d){ d.setDate( 1 ); } ],
	      [ "h", function(d){ d.setHours( now.getHours() ); }, function(d){ d.setHours( 0 ); } ],
	      [ "m", function(d){ d.setMinutes( now.getMinutes() ); }, function(d){ d.setMinutes( 0 ); } ],
	      [ "s", function(d){ d.setSeconds( now.getSeconds() ); }, function(d){ d.setSeconds( 0 ); } ], 
	      [ "f", function(d){ d.setMilliseconds( now.getMilliseconds() ); }, function(d){ d.setMilliseconds( 0 ); } ]
	    ];
	
	//set the new date values to the current time until the first
	//char in the formatting string appears
	var leadChar = pattern.charAt( 0 );
	var i = 0;
	var k = 0;
	var indexes = [];
	//bug 379285: only 'm' is case sensitive
	while ( i < chars.length && leadChar != chars[ i ][ 0 ] && (leadChar.toLowerCase() == "m" || leadChar.toLowerCase() != chars[ i ][ 0 ]) ) {
		indexes.appendElement(i);
		i++;
	}
	if ( i >= chars.length ) {
		throw egl.createInvalidArgumentException( "CRRUI2717E", [pattern] );
	}
	for(var j = indexes.length - 1; j >= 0; j--){
		(chars[ indexes[j] ][ 1 ])( dateCopy );
	}
	
	//find the last character and set everything after it to zeros
	var lastChar = pattern.charAt( pattern.length - 1 );
	k = chars.length - 1;
	while ( k >= 0 && lastChar != chars[ k ][ 0 ] && (lastChar.toLowerCase() == "m" || lastChar.toLowerCase() != chars[ k ][ 0 ]) ) {
		(chars[ k ][ 2 ])( dateCopy );
		k--;
	}
	if ( k < 0 ) {
		throw egl.createInvalidArgumentException( "CRRUI2717E", [pattern] );
	}
    if ( i > k ) {
        throw egl.createInvalidArgumentException("CRRUI2032E", [pattern]);
    }
    var tempPattern = pattern.replace(/[^\w\s]|(.)(?=\1)/gi, "").toLowerCase();
	for ( var h = 0; h < k - i - 1; h ++ ) {
	   if ( tempPattern.charAt(h) != chars[ h + i ][ 0 ].toLowerCase() ) {
	       throw egl.createInvalidArgumentException("CRRUI2032E", [pattern]);
	   }
	}
	return dateCopy;
};


egl.dateTime.timeStampValueWithPattern = function(/*string*/source, /*optional mask*/timeSpanPattern) {
	//Returns a TIMESTAMP value that reflects a string and is built based on a timestamp mask that you specify
	return ( ( source != null ) ? egl.stringToTimeStampWithDefaultSeparator( source, timeSpanPattern ) : null );
};


egl.checkNull = function ( obj, source )
{
	if ( obj == null )
	{
		if ( source )
			throw egl.createNullValueException( "CRRUI2023E", [ source ] );
		else
			throw egl.createNullValueException( "CRRUI2005E", [] );
	}
	else
		return obj;
};


egl.makeExceptionFromCaughtObject = function( caught )
{
	var message;
	var name;	
	if ( caught instanceof Error )
	{
		name = caught.name;
		message = caught.message;
	}
	else 
	{
		name = "";
		message = String( caught );
	}

	return egl.createJavaScriptObjectException( 'CRRUI2006E', message, name );
};

egl.createTypeCastException = function( /*string*/ messageID, /*string or array*/ inserts )
{
	var message = "";
	if (typeof(inserts) != "string") {
		message = egl.getRuntimeMessage( messageID, inserts );
	}
	egl.exceptionThrown = true;
	var args = new Array();
	args.push( [ "messageID", messageID || "" ] );
	args.push( [ "message", message || "" ] );
	args.push( [ "castToName", inserts[ 2 ] || "" ] );
	args.push( [ "actualTypeName", inserts[ 1 ] || "" ] );
	return new egl.eglx.lang.TypeCastException( args );
};

egl.createInvalidArgumentException = function( /*string*/ messageID, /*string or array*/ inserts ){
	if (typeof(inserts) != "string") {
		inserts = egl.getRuntimeMessage( messageID, inserts );
	}
	egl.exceptionThrown = true;
	var args = new Array();
	args.push( [ "messageID", messageID || "" ] );
	args.push( [ "message", inserts || "" ] );
	return new egl.eglx.lang.InvalidArgumentException( args );
};

egl.createInvalidPatternException = function( /*string*/ messageID, /*string or array*/ inserts ){
	if (typeof(inserts) != "string") {
		inserts = egl.getRuntimeMessage( messageID, inserts );
	}
	egl.exceptionThrown = true;
	var args = new Array();
	args.push( [ "messageID", messageID || "" ] );
	args.push( [ "message", inserts || "" ] );
	return new egl.eglx.lang.InvalidPatternException( args );
};

egl.createNumericOverflowException = function( /*string*/ messageID, /*string or array*/ inserts ){
	if (typeof(inserts) != "string") {
		inserts = egl.getRuntimeMessage( messageID, inserts );
	}
	egl.exceptionThrown = true;
	var args = new Array();
	args.push( [ "messageID", messageID || "" ] );
	args.push( [ "message", inserts || "" ] );
	return new egl.eglx.lang.NumericOverflowException( args );
};
