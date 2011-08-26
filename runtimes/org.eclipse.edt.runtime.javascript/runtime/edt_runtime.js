/*
 * Licensed Materials - Property of IBM
 *
 * Copyright IBM Corporation 2008, 2010. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA DP Schedule Contract with IBM Corp.
 */

/* TODO sbg We're using two different conventions for static functions:
 * the old RBD style in which they're simply functions on a class with
 * a singleton constructor, and a JavaScript-style in which functions are
 * simply declared in the correct namespace.   Need to pick one or the other,
 * or somehow reconcile the two....
 */


/****************************************************************************
 * AnyValue
 ****************************************************************************/
egl.defineClass( "egl.lang", "AnyValue"
,
{
	// TODO sbg In java RT, this is an interface;  do we need any methods on it here?
}
);


/****************************************************************************
 * EglAny
 ****************************************************************************/
egl.defineClass( "egl.lang", "EglAny",
{
}
);
egl.egl.lang.EglAny.ezeWrap = function(obj){
	return new egl.egl.lang.AnyBoxedObject(obj);
};
egl.egl.lang.EglAny.ezeCast = function(obj, cons){
	return egl.convertAnyToRefType(obj, obj.eze$$signature, cons);
};

egl.egl.lang.EglAny.fromEglAny = function(obj, sig){
	return {eze$$value : obj, eze$$signature : sig};
};

/****************************************************************************
 * AnyBoxedObject
 ****************************************************************************/
egl.defineClass( "egl.lang", "AnyBoxedObject",
		"egl.lang", "EglAny",
{
	"constructor" : function(obj) {
		this.eze$$value = obj;
	},
    "ezeUnbox" : function(){
    	return this.eze$$value;
    },
    "ezeCopy" : function(obj){
    	if ((this.eze$$value == null) || !(obj instanceof egl.egl.lang.AnyValue)){ 
    		this.eze$$value = obj;
    	}
    	else {
    		this.eze$$value.ezeCopy(obj);
    	}
    }
}
);

/****************************************************************************
 * EInt16 covers the egl type "smallint" and is represented as a native 
 * JavaScript Number; we only instantiate one of these when boxing parms 
 * of this type across function invocations.
 ****************************************************************************/
egl.defineClass( "egl.lang", "EInt16"
				,"egl.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.egl.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.egl.lang.EInt16.fromEInt32 = function (x) {   
	return egl.convertNumberToSmallint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.egl.lang.EInt16.fromEDecimal = function (x) {   
	return egl.convertDecimalToSmallint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.egl.lang.EInt16.fromAnyNum = function (x) {   
	return egl.convertDecimalToSmallint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.egl.lang.EInt16.fromEFloat64 = function (x) {
	return egl.convertFloatToSmallint(x);
};
egl.egl.lang.EInt16.fromEString = function (x) {
	return egl.convertStringToSmallint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};

/****************************************************************************
 * EInt32 covers the egl type "int" and is represented as a native JavaScript 
 * Number; we only instantiate one of these when boxing parms of this type 
 * across function invocations.
 ****************************************************************************/
egl.defineClass( "egl.lang", "EInt32"
				,"egl.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.egl.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.egl.lang.EInt32.ZERO = 0;
egl.egl.lang.EInt32.fromEString = function (x) {
	return egl.convertNumberToInt(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.egl.lang.EInt32.fromEDecimal = function (x) {
	return egl.convertDecimalToInt(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.egl.lang.EInt32.fromAnyNum = function (x) {
	return egl.convertDecimalToInt(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.egl.lang.EInt32.fromAnyObject = function (x) {
	return egl.convertAnyToInt(x,false);  //TODO sbg avoid hardcoding the boolean flag
};
egl.egl.lang.EInt32.fromEInt64 = function (x) {
	return egl.convertDecimalToInt(x);
};
egl.egl.lang.EInt32.fromEFloat64 = function (x) {
	return egl.convertFloatToInt(x);
};
egl.egl.lang.EInt32.fromEString = function (x) {
	return egl.convertStringToInt(x);
};
egl.egl.lang.EInt32.ezeCast = function (x) {
	return egl.convertAnyToInt(x, false);    // TODO need nullable support
};

/****************************************************************************
 * EFloat32 covers the egl type "smallfloat" and is represented as a native 
 * JavaScript Number; we only instantiate one of these when boxing parms of 
 * this type across function invocations.
 ****************************************************************************/
egl.defineClass( "egl.lang", "EFloat32"
				,"egl.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.egl.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.egl.lang.EFloat32.ZERO = egl.javascript.BigDecimal.prototype.ZERO;
egl.egl.lang.EFloat32.fromEFloat64 = function (x) {
	return egl.convertFloatToSmallfloat(x,"TODO: make an exception for this"/*egl.createRuntimeException*/);
};



/****************************************************************************
 * EDecimal
 ****************************************************************************/
egl.defineClass( "egl.lang", "EDecimal"
				,"egl.lang", "AnyBoxedObject",
{
	"constructor" : function(s){
		egl.egl.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.egl.lang.EDecimal.ZERO = egl.javascript.BigDecimal.prototype.ZERO;
egl.egl.lang.EDecimal.fromEInt16 = function (x, decimals, limit) {   
	if (limit)
		return egl.convertIntegerToDecimal(x, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
	else
		return new egl.javascript.BigDecimal(x);
};
egl.egl.lang.EDecimal.fromEInt32 = function (x, decimals, limit) {   
	if (limit)
		return egl.convertIntegerToDecimal(x, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
	else
		return new egl.javascript.BigDecimal(x);
};
egl.egl.lang.EDecimal.fromEInt64 = function (x, decimals, limit) {   
	if (limit)
		return egl.convertIntegerToDecimal(x, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
	else
		return new egl.javascript.BigDecimal(x);
};
egl.egl.lang.EDecimal.fromEDecimal = function (x, decimals, limit) { 
	return egl.convertDecimalToDecimal(x, decimals, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.egl.lang.EDecimal.fromAnyNum = function (x, decimals, limit) {
	if (limit)
		return egl.convertDecimalToDecimal(x, decimals, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
	else
		return egl.convertDecimalToDecimal(x, 0, decimals, "TODO: make an exception for this"/*egl.createRuntimeException*/); 
};
egl.egl.lang.EDecimal.fromEFloat64 = function (x, decimals, limit) {
	if (limit)
		return egl.convertFloatToDecimal(x, decimals, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
	else
		return egl.convertFloatToDecimal(x, 0, decimals, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};



/****************************************************************************
 * EInt64 covers the egl type "bigint" 
 ****************************************************************************/
egl.defineClass( "egl.lang", "EInt64"
				,"egl.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.egl.lang.AnyBoxedObject.call(this, i);	//TODO no ctor chaining?! 
	}
}
);
egl.egl.lang.EInt64.ZERO = egl.javascript.BigDecimal.prototype.ZERO;
egl.egl.lang.EInt64.fromEInt16 = function (x) {   
	return new egl.javascript.BigDecimal(x);
};
egl.egl.lang.EInt64.fromEInt32 = function (x) {   
	return new egl.javascript.BigDecimal(x);
};
egl.egl.lang.EInt64.fromEString = function (x) {  
	return egl.convertStringToBigint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.egl.lang.EInt64.fromEDecimal = function (x) {  
	return egl.convertDecimalToBigint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.egl.lang.EInt64.fromEFloat64 = function (x) {  
	return egl.convertFloatToBigint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.egl.lang.EInt64.fromAnyNum = function (x) {  
	return egl.convertDecimalToBigint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};



/****************************************************************************
 * EFloat64 covers the egl type "float" and is represented as a JavaScript Number).
 ****************************************************************************/
egl.defineClass( "egl.lang", "EFloat64"
				,"egl.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.egl.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.egl.lang.EFloat64.ZERO = egl.javascript.BigDecimal.prototype.ZERO;
egl.egl.lang.EFloat64.fromEInt64 = function (x) { 
	return Number(x.toString()); 
};
egl.egl.lang.EFloat64.fromEDecimal = function (x) { 
	return Number(x.toString()); 
};


egl.defineClass( "egl.lang", "EBoolean"
		,"egl.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.egl.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.egl.lang.EBoolean.fromEString = function (x) {   
	return x.toLowerCase() === "true"; 
};
egl.egl.lang.EBoolean.ezeCast = function (x) {   
	return x; 
};
egl.egl.lang.EBoolean.fromEBoolean = function (x) {  //TODO sbg likely a gen bug   
	return x; 
};


/****************************************************************************
 * EString
 ****************************************************************************/
egl.defineClass( "egl.lang", "EString"
				,"egl.lang", "AnyBoxedObject",
{
	"constructor" : function(str) {
		egl.egl.lang.AnyBoxedObject.call(this, str); //TODO no ctor chaining?!
		
		this.maxLength = -1;
	},
 	"constructor" : function(str, length) {
		egl.egl.lang.AnyBoxedObject.call(this, str); //TODO no ctor chaining?!
	
		this.maxLength = length;
	},
	"equals" : function(pgm, str1, str2) {
		if ((str1 == null) || (str2 == null)) {
			throw new egl.egl.lang.NullValueException();
		}
		return str1 == str2;
	}
}
);
egl.egl.lang.EString.ezeCast = function (x) {
	return egl.convertAnyToString(x, false);    // TODO need nullable support
};

egl.egl.lang.EString.ezeBox = function(str){
	return new egl.egl.lang.EString(str);
};
egl.egl.lang.EString.ezeBox = function(str, len){
	return new egl.egl.lang.EString(str, len);
};
egl.egl.lang.EString.equals = function(s, start, end) {
	return s.substring(start-1, end);
};
egl.egl.lang.EString.substring = function(s, start, end) {
	return s.substring(start-1, end);
};
egl.egl.lang.EString.substringAssign = function(tgt, src, start, end){
	return tgt.splice(start, end, src);
};
egl.egl.lang.EString.fromEInt16 = function (x) {
	return (x).toString();
};
egl.egl.lang.EString.fromEInt32 = function (x) {
	return (x).toString();
};
egl.egl.lang.EString.fromEInt64 = function (x) {
	return (x).toString();
};
egl.egl.lang.EString.fromEFloat64 = function (x) {
	return (x).toString();
};
egl.egl.lang.EString.fromAnyObject = function (x) {
	return egl.convertAnyToString(x, false);  //TODO sbg avoid hardcoding the boolean flag
};
egl.egl.lang.EString.matches = function (str1, str2, esc) {
	esc = esc || "\\";
	return egl.matches(str1, str2, esc);
};
egl.egl.lang.EString.like = function (str1, str2, esc) {
	esc = esc || "\\";
	return egl.like(str1, str2, esc);
};


//Returns the number of bytes in a text expression, excluding any trailing spaces or null values.
String.prototype.length = function() {  //TODO Don't override JavaScript field of the same name
	return ( s === null ) ? 0
						  : this.clip().length;
};

String.prototype.clip = function() {
	return ( str === null || option === null ) ?  null
											   :  this.clipLeading().clipTrailing();
};

String.prototype.clipLeading = function() {
	return this.replace( /^(\s)*/, "" );
};

String.prototype.clipTrailing = function() {
	return this.replace( /(\s)*$/, "" );
};

/* TODO JavaScript's impls of these are acceptable as-is
String.prototype.toUpperCase = function() { //TODO Don't override JavaScript function of the same name
};
String.prototype.toLowerCase = function() { //TODO Don't override JavaScript function of the same name
};
String.prototype.indexOf = function(substr) { //TODO Don't override JavaScript function of the same name
};
String.prototype.indexOf = function(substr, startIndex) { //TODO Don't override JavaScript function of the same name
};
String.prototype.lastIndexOf = function() { //TODO Don't override JavaScript function of the same name
};
 */
String.prototype.endsWith = function(substr) { 
	return (this.indexOf(substr, this.length - substr.length) !== -1);
};
String.prototype.startsWith = function(substr) { 
	return (this.indexOf(substr) == 0);
};
String.prototype.replaceStr = function(target, replacement) {
	return this.replace(target, replacement);   // TODO should we simply alias replaceStr as replace?
};

/* TODO JavaScript's impls of these are acceptable as-is
String.prototype.charCodeAt = function(index) { 
};
*/
/****************************************************************************
 * EDate
 ****************************************************************************/
egl.defineClass( "egl.lang", "EDate"
		,"egl.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.egl.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.egl.lang.EDate.fromEDate = function (x) {
	return x;  // TODO sbg bug in generator -- delete this fn when fixed
};
egl.egl.lang.EDate.fromEString = function (x) {   
return egl.stringToDate( x, egl.eglx.rbd.StrLib[$inst].defaultDateFormat ); // TODO need dateformat as arg?
};

/****************************************************************************
 * ETime
 ****************************************************************************/
egl.defineClass( "egl.lang", "ETime"
		,"egl.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.egl.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
//unsupported 0.7
//egl.egl.lang.ETime.fromEString = function (x) {   
//	return egl.stringToTime( x, egl.eglx.rbd.StrLib[$inst].defaultTimeFormat ); // TODO need timeformat as arg?
//};


/****************************************************************************
 * ETimestamp
 ****************************************************************************/
egl.defineClass( "egl.lang", "ETimestamp"
		,"egl.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.egl.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.egl.lang.ETimestamp["currentTimeStamp"] = function (pattern) {
	// returns a new Date object
	// timestamp keeps every field
	return egl.egl.lang.ETimestamp.extend("timestamp", new Date(), pattern);
};

egl.egl.lang.ETimestamp["extend"] = function (/*type of date*/ type, /*extension*/ date, /*optional mask*/pattern ) {
	//Converts a timestamp, time, or date into a longer or shorter timestamp value.
	if ( !date ) {
		return null;
	}
	
	//copy is returned if the pattern is invalid
	var dateCopy = new Date( date );
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
		date.setFullYear( now.getFullYear() );
		date.setMonth( now.getMonth() );
		date.setDate( now.getDate() );
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
		[
		  [ "y", function(d){ d.setFullYear( now.getFullYear() ); }, function(d){ d.setFullYear( 0 ); } ],
	      [ "M", function(d){ d.setMonth( now.getMonth() ); }, function(d){ d.setMonth( 0 ); } ], 
	      [ "d", function(d){ d.setDate( now.getDate() ); }, function(d){ d.setDate( 1 ); } ],
	      [ "H", function(d){ d.setHours( now.getHours() ); }, function(d){ d.setHours( 0 ); } ],
	      [ "m", function(d){ d.setMinutes( now.getMinutes() ); }, function(d){ d.setMinutes( 0 ); } ],
	      [ "s", function(d){ d.setSeconds( now.getSeconds() ); }, function(d){ d.setSeconds( 0 ); } ], 
	      [ "f", function(d){ d.setMilliseconds( now.getMilliseconds() ); }, function(d){ d.setMilliseconds( 0 ); } ]
	    ];
	
	//set the new date values to the current time until the first
	//char in the formatting string appears
	var leadChar = pattern.charAt( 0 );
	var i = 0;
	while ( i < chars.length && leadChar != chars[ i ][ 0 ] ) {
		(chars[ i ][ 1 ])( dateCopy );
		i++;
	}
	//if the pattern has bad characters, just return the original date
	if ( i >= chars.length ) {
		return date;
	}
	
	//find the last character and set everything after it to zeros
	var lastChar = pattern.charAt( pattern.length - 1 );
	i = chars.length - 1;
	while ( i >= 0 && lastChar != chars[ i ][ 0 ] ) {
		(chars[ i ][ 2 ])( dateCopy );
		i--;
	}
	//if the pattern has bad characters, just return the date
	if ( i < 0 ) {
		return date;
	}
	
	return dateCopy;
};

/****************************************************************************
 * AnyNum
 ****************************************************************************/
egl.defineClass( "egl.lang", "AnyNum",
{
}
);
egl.egl.lang.AnyNum.fromEInt16 = function (x, decimals, limit) {   
		return egl.convertIntegerToDecimal(x, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.egl.lang.AnyNum.fromEInt32 = function (x, decimals, limit) {   
		return egl.convertIntegerToDecimal(x, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.egl.lang.AnyNum.fromEDecimal = function (x, decimals, limit) { 
	return egl.convertDecimalToDecimal(x, decimals, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.egl.lang.AnyNum.fromEFloat64 = function (x, decimals, limit) { 
	return egl.convertFloatToDecimal(x, decimals, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};

egl.egl.lang.AnyNum.ezeCast = function(x, decimals, limit){
	return egl.convertDecimalToDecimal(x, decimals, limit, false, "TODO: make an exception for this"/*egl.createRuntimeException*/);  //TODO sbg false should be a flag indicating nullable
};




/****************************************************************************
 * NullType
 ****************************************************************************/
egl.defineClass( "egl.lang", "NullType",
{
}
);
egl.egl.lang.NullType.equals = function(x, y) {
	return x == y;  //TODO sbg Should this be generated as a simple comparison, rather than all this overhead?
};
egl.egl.lang.NullType.fromEList = function(list) {
	return list; //TODO sbg Should this be generated as a simple comparison, rather than all this overhead?
};
egl.egl.lang.NullType.fromAnyObject = function(obj, sig) {
	return {eze$$value : obj, eze$$signature : sig};
};

