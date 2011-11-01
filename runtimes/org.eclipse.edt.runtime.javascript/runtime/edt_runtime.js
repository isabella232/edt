/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

/* TODO sbg We're using two different conventions for static functions:
 * the old RBD style in which they're simply functions on a class with
 * a singleton constructor, and a JavaScript-style in which functions are
 * simply declared in the correct namespace.   Need to pick one or the other,
 * or somehow reconcile the two....
 */

egl.createNullValueException = function( /*string*/ messageID, /*string or array*/ inserts )
{
	if (typeof(inserts) != "string") {
		inserts = egl.getRuntimeMessage( messageID, inserts );
	}
	egl.exceptionThrown = true;
	var args = new Array();
	args.push( [ "messageID", messageID || "" ] );
	args.push( [ "message", inserts || "" ] );
	return new egl.eglx.lang.NullValueException( args );
};

/****************************************************************************
 * AnyValue
 ****************************************************************************/
egl.defineClass( "eglx.lang", "AnyValue"
,
{
	// TODO sbg In java RT, this is an interface;  do we need any methods on it here?
}
);


/****************************************************************************
 * EAny
 ****************************************************************************/
egl.defineClass( "eglx.lang", "EAny",
{
}
);
egl.eglx.lang.EAny.ezeWrap = function(obj){
	return new egl.eglx.lang.AnyBoxedObject(obj);
};
egl.eglx.lang.EAny.ezeCast = function(obj, cons){
	return egl.convertAnyToRefType(obj, obj.eze$$signature, cons);
};
egl.eglx.lang.EAny.unbox = function(obj){
	return egl.unboxAny(obj);
};
egl.eglx.lang.EAny.fromEAny = function(obj, sig){
	return egl.boxAny(obj,sig);
};

/****************************************************************************
 * AnyBoxedObject
 ****************************************************************************/
egl.defineClass( "eglx.lang", "AnyBoxedObject",
		"eglx.lang", "EAny",
{
	"constructor" : function(obj) {
		this.eze$$value = obj;
	},
    "ezeUnbox" : function(){
    	return this.eze$$value;
    },
    "ezeCopy" : function(obj){
    	if ((this.eze$$value == null) || !(obj instanceof egl.eglx.lang.AnyValue)){ 
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
egl.defineClass( "eglx.lang", "EInt16"
				,"eglx.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.eglx.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.eglx.lang.EInt16.fromEInt32 = function (x) {   
	return egl.convertNumberToSmallint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.EInt16.fromEDecimal = function (x) {   
	return egl.convertDecimalToSmallint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.EInt16.fromENumber = function (x, nullable) {  
	nullable = nullable || false;  // TODO nullability should be generated
	return egl.convertAnyToSmallint(x, nullable);
};
egl.eglx.lang.EInt16.fromEFloat64 = function (x) {
	return egl.convertFloatToSmallint(x);
};
egl.eglx.lang.EInt16.fromEString = function (x) {
	return egl.convertStringToSmallint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.EInt16.ezeCast = function (x, nullable) {
	return egl.convertAnyToSmallint(x, nullable);    
};
egl.eglx.lang.EInt16.pow = function (x, exp) {
	return egl.eglx.lang.EDecimal.fromEInt16(x).pow(egl.eglx.lang.EDecimal.fromEInt16(exp), egl.javascript.BigDecimal.prototype.eglMC); 
};

egl.eglx.lang.EInt16.asNumber= function (x) {
	return egl.boxAny(x, "I;");
};

/****************************************************************************
 * EInt32 covers the egl type "int" and is represented as a native JavaScript 
 * Number; we only instantiate one of these when boxing parms of this type 
 * across function invocations.
 ****************************************************************************/
egl.defineClass( "eglx.lang", "EInt32"
				,"eglx.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.eglx.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.eglx.lang.EInt32.ZERO = 0;
egl.eglx.lang.EInt32.fromEDecimal = function (x) {
	return egl.convertDecimalToInt(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.EInt32.fromENumber = function (x, nullable) {  
	nullable = nullable || false;  // TODO nullability should be generated
	return egl.convertAnyToInt(x, nullable);
};
egl.eglx.lang.EInt32.fromAnyObject = function (x) {
	return egl.convertAnyToInt(x,false);  //TODO sbg avoid hardcoding the boolean flag
};
egl.eglx.lang.EInt32.fromEInt64 = function (x) {
	return egl.convertDecimalToInt(x);
};
egl.eglx.lang.EInt32.fromEFloat32 = function (x) {
	return egl.convertFloatToInt(x);
};
egl.eglx.lang.EInt32.fromEFloat64 = function (x) {
	return egl.convertFloatToInt(x);
};
egl.eglx.lang.EInt32.fromEString = function (x) {
	return egl.convertStringToInt(x);
};
egl.eglx.lang.EInt32.ezeCast = function (x, nullable) {
	return egl.convertAnyToInt(x, nullable);   
};
egl.eglx.lang.EInt32.pow = function (x, exp) {
	return egl.eglx.lang.EDecimal.fromEInt16(x).pow(egl.eglx.lang.EDecimal.fromEInt16(exp), egl.javascript.BigDecimal.prototype.eglMC); 
};
egl.eglx.lang.EInt32.asNumber= function (x) {
	return egl.boxAny(x, "I;");
};

/****************************************************************************
 * EFloat32 covers the egl type "smallfloat" and is represented as a native 
 * JavaScript Number; we only instantiate one of these when boxing parms of 
 * this type across function invocations.
 ****************************************************************************/
egl.defineClass( "eglx.lang", "EFloat32"
				,"eglx.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.eglx.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.eglx.lang.EFloat32.ZERO = egl.javascript.BigDecimal.prototype.ZERO;
egl.eglx.lang.EFloat32.fromEFloat64 = function (x) {
	return egl.convertFloatToSmallfloat(x,"TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.EFloat32.fromEInt32  = function (x) {
	return egl.convertFloatToSmallfloat(Number( (new egl.javascript.BigDecimal(x)).toString()));
};
egl.eglx.lang.EFloat32.fromEInt64  = function (x) {
	return egl.convertFloatToSmallfloat(Number( (new egl.javascript.BigDecimal(x)).toString()));
};
egl.eglx.lang.EFloat32.fromEDecimal  = function (x) {
	return egl.convertFloatToSmallfloat(Number( x.toString()));

};
egl.eglx.lang.EFloat32.fromEString = function (x){
	return egl.convertStringToFloat(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.EFloat32.ezeCast = function (x, nullable) {
	return egl.convertAnyToSmallfloat(x, nullable);    
};
egl.eglx.lang.EFloat32.pow = function (x, exp) {
	return Math.pow(x, exp); 
};
egl.eglx.lang.EFloat32.fromENumber = function (x, nullable) {
	nullable = nullable || false;  // TODO nullability should be generated
	return egl.convertFloatToSmallfloat(egl.convertAnyToFloat(x, nullable));
};
egl.eglx.lang.EFloat32.asNumber= function (x) {
	return egl.boxAny(x, "f;");
};


/****************************************************************************
 * EDecimal
 ****************************************************************************/
egl.defineClass( "eglx.lang", "EDecimal"
				,"eglx.lang", "AnyBoxedObject",
{
	"constructor" : function(s){
		egl.eglx.lang.AnyBoxedObject.call(this, s); //TODO no ctor chaining?!
	}
}
);
egl.eglx.lang.EDecimal.ZERO = egl.javascript.BigDecimal.prototype.ZERO;
egl.eglx.lang.EDecimal.fromEInt16 = function (x, decimals, limit) {   
	if (limit)
		return egl.convertIntegerToDecimal(x, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
	else
		return new egl.javascript.BigDecimal(x);
};
egl.eglx.lang.EDecimal.fromEInt32 = function (x, decimals, limit) {   
	if (limit)
		return egl.convertIntegerToDecimal(x, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
	else
		return new egl.javascript.BigDecimal(x);
};
egl.eglx.lang.EDecimal.fromEInt64 = function (x, decimals, limit) {   
	if (limit)
		return egl.convertIntegerToDecimal(x, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
	else
		return new egl.javascript.BigDecimal(x);
};
egl.eglx.lang.EDecimal.fromEDecimal = function (x, decimals, limit) { 
	return egl.convertDecimalToDecimal(x, decimals, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.EDecimal.fromEString = function (x, decimals, limit) { 
	if (limit)
		return egl.convertStringToDecimal(x, decimals, limit);
	else
		return egl.convertStringToDecimal(x, egl.eglx.lang.EString.textLen(x), decimals);
};
egl.eglx.lang.EDecimal.fromENumber = function (x, decimals, limit) {
	if (limit)
		return egl.convertAnyToDecimal(x, decimals, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
	else
		return egl.convertAnyToDecimal(x, 0, decimals, "TODO: make an exception for this"/*egl.createRuntimeException*/); 
};
egl.eglx.lang.EDecimal.fromEFloat32 = function (x, decimals, limit) {
	if (limit)
		return egl.convertFloatToDecimal(x, decimals, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
	else
		return egl.convertFloatToDecimal(x, 0, decimals, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.EDecimal.fromEFloat64 = function (x, decimals, limit) {
	if (limit)
		return egl.convertFloatToDecimal(x, decimals, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
	else
		return egl.convertFloatToDecimal(x, 0, decimals, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.EDecimal.ezeCast = function (x, nullable, decimals, limit) {
	if (limit)
		return egl.convertAnyToDecimal(x, decimals, limit, nullable, "TODO: make an exception for this"/*egl.createRuntimeException*/);
	else
		return egl.convertAnyToDecimal(x, 0, decimals, nullable, "TODO: make an exception for this"/*egl.createRuntimeException*/); 
};
egl.eglx.lang.EDecimal.asNumber= function (x) {
	return egl.boxAny(x);
};



/****************************************************************************
 * EInt64 covers the egl type "bigint" 
 ****************************************************************************/
egl.defineClass( "eglx.lang", "EInt64"
				,"eglx.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.eglx.lang.AnyBoxedObject.call(this, i);	//TODO no ctor chaining?! 
	}
}
);
egl.eglx.lang.EInt64.ZERO = egl.javascript.BigDecimal.prototype.ZERO;
egl.eglx.lang.EInt64.fromEInt16 = function (x) {   
	return new egl.javascript.BigDecimal(x);
};
egl.eglx.lang.EInt64.fromEInt32 = function (x) {   
	return new egl.javascript.BigDecimal(x);
};
egl.eglx.lang.EInt64.fromEString = function (x) {  
	return egl.convertStringToBigint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.EInt64.fromEDecimal = function (x) {  
	return egl.convertDecimalToBigint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.EInt64.fromEFloat64 = function (x) {  
	return egl.convertFloatToBigint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.EInt64.fromENumber = function (x, nullable) {  
	nullable = nullable || false;  // TODO nullability should be generated
	return egl.convertAnyToBigint(x, nullable);
};
egl.eglx.lang.EInt64.fromEFloat32 = function (x) {  
	return egl.convertFloatToBigint(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
}; 
egl.eglx.lang.EInt64.ezeCast = function (x, nullable) {
	return egl.convertAnyToBigint(x, nullable);    
};
egl.eglx.lang.EInt64.pow = function (x, exp) {
	return x.pow(exp); 
};
egl.eglx.lang.EInt64.fromEInt64 = function (x) {
	return x;  // TODO sbg This seems likely to be a generation error.....
}; 
egl.eglx.lang.EInt64.asNumber= function (x) {
	return egl.boxAny(x, "B;");
};


/****************************************************************************
 * EFloat64 covers the egl type "float" and is represented as a JavaScript Number).
 ****************************************************************************/
egl.defineClass( "eglx.lang", "EFloat64"
				,"eglx.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.eglx.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.eglx.lang.EFloat64.ZERO = egl.javascript.BigDecimal.prototype.ZERO;
egl.eglx.lang.EFloat64.fromEInt64 = function (x) { 
	return Number(x.toString()); 
};
egl.eglx.lang.EFloat64.fromEDecimal = function (x) { 
	return Number(x.toString()); 
};
egl.eglx.lang.EFloat64.fromEString = function (x){
	return egl.convertStringToFloat(x, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.EFloat64.fromENumber = function (x, nullable) {
	nullable = nullable || false;  // TODO nullability should be generated
	return egl.convertAnyToFloat(x, nullable);
};
egl.eglx.lang.EFloat64.ezeCast = function (x, nullable) {
	return egl.convertAnyToFloat(x, nullable);    
};
egl.eglx.lang.EFloat64.pow = function (x, exp) {
	return Math.pow(x, exp); 
};
egl.eglx.lang.EFloat64.asNumber= function (x) {
	return egl.boxAny(x, "F;");
};


/****************************************************************************
 * EBoolean 
 ****************************************************************************/
egl.defineClass( "eglx.lang", "EBoolean"
		,"eglx.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.eglx.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.eglx.lang.EBoolean.fromEString = function (x) {   
	return x.toLowerCase() === "true"; 
};
egl.eglx.lang.EBoolean.ezeCast = function (x, nullable) {   
	return egl.convertAnyToBoolean(x, nullable);   
};
egl.eglx.lang.EBoolean.fromEBoolean = function (x) {  //TODO sbg likely a gen bug   
	return x; 
};


/****************************************************************************
 * EString
 ****************************************************************************/
egl.defineClass( "eglx.lang", "EString"
				,"eglx.lang", "AnyBoxedObject",
{
	"constructor" : function(str) {
		egl.eglx.lang.AnyBoxedObject.call(this, str); //TODO no ctor chaining?!
		
		this.maxLength = -1;
	},
 	"constructor" : function(str, length) {
		egl.eglx.lang.AnyBoxedObject.call(this, str); //TODO no ctor chaining?!
	
		this.maxLength = length;
	},
	"equals" : function(pgm, str1, str2) {
		if ((str1 == null) || (str2 == null)) {
			throw new egl.eglx.lang.NullValueException();
		}
		return str1 == str2;
	}
}
);
egl.eglx.lang.EString.textLen = function (s) {
   return ( s === null ) ? 0 : s.length;
};
egl.eglx.lang.EString.ezeCast = function (x, nullable) {
	return egl.convertAnyToString(x, nullable);  
};

egl.eglx.lang.EString.ezeBox = function(str){
	return new egl.eglx.lang.EString(str);
};
egl.eglx.lang.EString.ezeBox = function(str, len){
	return new egl.eglx.lang.EString(str, len);
};
egl.eglx.lang.EString.equals = function(str1, str2) {
	return (str1 == str2);
};
egl.eglx.lang.EString.notEquals = function(str1, str2) {
	return (str1 != str2);
};
egl.eglx.lang.EString.substring = function(str, startIndex, endIndex) {
	if (str == null || startIndex == null || endIndex == null)
		return null;
	start = startIndex;
	end = endIndex;
	max = str.length;
	if (start < 1 || start > max) {
		ex = new egl.eglx.lang.InvalidIndexException();
		ex.index = start;
		throw ex;
	} else if (end < start || end < 1 || end > max) {
		ex = new egl.eglx.lang.InvalidIndexException();
		ex.index = end;
		throw ex;
	}
	return str.substring(start-1, end);
};
egl.eglx.lang.EString.substringAssign = function(tgt, src, start, end){
	return tgt.splice(start, end, src);
};
egl.eglx.lang.EString.fromEInt16 = function (x) {
	return ''+x;
};
egl.eglx.lang.EString.fromEInt32 = function (x) {
	return ''+x;
};
egl.eglx.lang.EString.fromEInt64 = function (x) {
	return (x).toString();
};
egl.eglx.lang.EString.fromEFloat32 = function (x) {
	return ''+x;
};
egl.eglx.lang.EString.fromEFloat64 = function (x) {
	return (x).toString();
};
egl.eglx.lang.EString.fromEDecimal = function (x) {
	return (x).toString();
};
egl.eglx.lang.EString.fromEBoolean = function (x) {
	return (x).toString();
};
egl.eglx.lang.EString.fromENumber = function (x) {
	return egl.unboxAny(x).toString();
};
egl.eglx.lang.EString.fromETimestamp = function (timestamp) {
	return egl.timeStampToString(timestamp, "yyyy-MM-dd HH.mm.ss.SSSSSS"); // TODO sbg Need a constant, but can't depend on eglx.lang.Constants
};
egl.eglx.lang.EString.fromEDate = function (d) {
	return egl.dateToString(d, "MM/dd/yyyy"); 
};
egl.eglx.lang.EString.fromAnyObject = function (x) {
	return egl.convertAnyToString(x, false);  //TODO sbg avoid hardcoding the boolean flag
};
egl.eglx.lang.EString.matchesPattern = function (str1, str2, esc) {
	if ((str1 == null) || (str2 == null)) {
		throw new egl.eglx.lang.NullValueException();
	}
	esc = esc || "\\";
	return egl.matches(str1, str2, esc);
};
egl.eglx.lang.EString.isLike = function (str1, str2, esc) {
	if ((str1 == null) || (str2 == null)) {
		throw new egl.eglx.lang.NullValueException();
	}
	esc = esc || "\\";
	return egl.like(str1, str2, esc);
};
egl.eglx.lang.EString.indexOf = function (str, pattern, start) {
	if ((str == null) || (pattern == null)) {
		throw new egl.eglx.lang.NullValueException();
	}
	if (!start) {
		start = 1;
	}
	return str.indexOf(pattern, start - 1) + 1;
};
egl.eglx.lang.EString.lastIndexOf = function (str, pattern) {
	if ((str == null) || (pattern == null)) {
		throw new egl.eglx.lang.NullValueException();
	}
	return str.lastIndexOf(pattern) + 1;
};
egl.eglx.lang.EString.replaceStr = function(str, target, replacement) {
	if ((str == null) || (target == null) || (replacement == null)) {
		throw new egl.eglx.lang.NullValueException();
	}
	return str.replace(new RegExp(target, "g"), replacement);   // TODO should we simply alias replaceStr as replace?
};
egl.eglx.lang.EString.charCodeAt = function (str, index) {
	if ((str == null) || (index == null)) {
		throw new egl.eglx.lang.NullValueException();
	}
	return egl.eglx.lang.EString.substring(str, index, index).charCodeAt();
};
egl.eglx.lang.EString.trim = function (str) {
	if (str == null) {
		throw new egl.eglx.lang.NullValueException();
	}
	return str.clipLeading().clipTrailing();
};
egl.eglx.lang.EString.plus = function (op1, op2) {
	a = (op1) ? op1 : "";
	b = (op2) ? op2 : "";
	return a + b;
};
egl.eglx.lang.EString.concat = function (op1, op2) {
	a = (op1) ? op1 : "";
	b = (op2) ? op2 : "";
	return a + b;
};

egl.eglx.lang.EString.nullconcat = function (op1, op2) {
	return egl.nullableconcat(op1, op2);
};
egl.eglx.lang.EString.clip = function(s){
	if ( s === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	return s.clip();
};
egl.eglx.lang.EString.clipLeading = function(s){
	if ( s === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	return s.clipLeading();
};
egl.eglx.lang.EString.clipTrailing = function(s){
	if ( s === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	return s.clipTrailing();
};
egl.eglx.lang.EString.toLowerCase = function(s){
	if ( s === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	return s.toLowerCase();
};
egl.eglx.lang.EString.toUpperCase = function(s){
	if ( s === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	return s.toUpperCase();
};
egl.eglx.lang.EString.endsWith = function(s, substr) { 
	if ( s === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	return s.endsWith(substr);
};
egl.eglx.lang.EString.startsWith = function(s, substr) { 
	if ( s === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	return s.startsWith(substr);
};
egl.eglx.lang.EString.asNumber = function (x) {
	return egl.convertAnyToNumber(egl.boxAny(x, "S;"), false);  //TODO sbg avoid hardcoding the boolean flag
};


//Returns the number of bytes in a text expression, excluding any trailing spaces or null values.
String.prototype.length = function() {  //TODO Don't override JavaScript field of the same name
	return ( s === null ) ? 0
						  : this.clip().length;
};

String.prototype.clip = function() {
	return this.clipTrailing();
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
egl.defineClass( "eglx.lang", "EDate"
		,"eglx.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.eglx.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.eglx.lang.EDate.fromEDate = function (x) {
	return x;  // TODO sbg bug in generator -- delete this fn when fixed
};
egl.eglx.lang.EDate.fromEString = function (x) {   
    return egl.stringToDate( x, "MM/dd/yyyy" ); 
};
egl.eglx.lang.EDate.equals = function (x, y) {   
	return egl.dateEquals(x, y, false);  //TODO sbg false should be a flag indicating nullable
};
egl.eglx.lang.EDate.notEquals = function (x, y) {   
	return !egl.dateEquals(x, y, false);  //TODO sbg false should be a flag indicating nullable
};

egl.eglx.lang.EDate.addDays = function (x, y) {
	if ((x === null) || (y === null))
		throw new egl.eglx.lang.NullValueException();
	else
		return egl.eglx.lang.DateTimeLib.dateFromInt(egl.convertDateToInt(x) + y);
};
egl.eglx.lang.EDate.daysDifferent = function (x, y) {
	if ((x === null) || (y === null))
		throw new egl.eglx.lang.NullValueException();
	else
		return egl.convertDateToInt(x) - egl.convertDateToInt(y);
};
egl.eglx.lang.EDate.ezeCast = function (any, nullable) {   
	return egl.convertAnyToDate(any, nullable);
};


/****************************************************************************
 * ETime
 ****************************************************************************/
egl.defineClass( "eglx.lang", "ETime"
		,"eglx.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.eglx.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
//unsupported 0.7
//egl.eglx.lang.ETime.fromEString = function (x) {   
//	return egl.stringToTime( x, egl.eglx.rbd.StrLib[$inst].defaultTimeFormat ); // TODO need timeformat as arg?
//};


/****************************************************************************
 * ETimestamp
 ****************************************************************************/
egl.defineClass( "eglx.lang", "ETimestamp"
		,"eglx.lang", "AnyBoxedObject",
{
	"constructor" : function(i){
		egl.eglx.lang.AnyBoxedObject.call(this, i); //TODO no ctor chaining?!
	}
}
);
egl.eglx.lang.ETimestamp["currentTimeStamp"] = function (pattern) {
	// returns a new Date object
	// timestamp keeps every field
	return egl.eglx.lang.ETimestamp.extend("timestamp", new Date(), pattern);
};

egl.eglx.lang.ETimestamp["extend"] = function (/*type of date*/ type, /*extension*/ date, /*optional mask*/pattern ) {
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


egl.eglx.lang.ETimestamp.ezeCast = function(x, nullable, pattern){
	if(pattern == null){
		pattern = "yyyy-MM-dd HH.mm.ss";
	}
	return egl.convertAnyToTimestamp(x, nullable, pattern);  
};
egl.eglx.lang.ETimestamp.equals = function (x, y) {   
	return egl.timeStampEquals(x, y, false);  //TODO sbg false should be a flag indicating nullable
};


/****************************************************************************
 * ENumber
 ****************************************************************************/
egl.defineClass( "eglx.lang", "ENumber",
{
}
);
egl.eglx.lang.ENumber.fromEInt16 = function (x, decimals, limit) {   
		return egl.convertIntegerToDecimal(x, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.ENumber.fromEInt32 = function (x, decimals, limit) {   
		return egl.convertIntegerToDecimal(x, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.ENumber.fromEDecimal = function (x, decimals, limit) { 
	return egl.convertDecimalToDecimal(x, decimals, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};
egl.eglx.lang.ENumber.fromEFloat64 = function (x, decimals, limit) { 
	return egl.convertFloatToDecimal(x, decimals, limit, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};

egl.eglx.lang.ENumber.ezeCast = function(x, nullable, decimals, limit){
	return egl.convertDecimalToDecimal(x, decimals, limit, nullable, "TODO: make an exception for this"/*egl.createRuntimeException*/);
};




/****************************************************************************
 * NullType
 ****************************************************************************/
egl.defineClass( "eglx.lang", "NullType",
{
}
);
egl.eglx.lang.NullType.equals = function(x, y) {
	return egl.unboxAny(x) == egl.unboxAny(y);  //TODO sbg Should this be generated as a simple comparison, rather than all this overhead?
};
egl.eglx.lang.NullType.notEquals = function(x, y) {
	return egl.unboxAny(x) != egl.unboxAny(y);  //TODO sbg Should this be generated as a simple comparison, rather than all this overhead?
};
egl.eglx.lang.NullType.fromEList = function(list) {
	return list; //TODO sbg Should this be generated as a simple comparison, rather than all this overhead?
};
egl.eglx.lang.NullType.fromAnyObject = function(obj, sig) {
	return {eze$$value : obj, eze$$signature : sig};
};

/****************************************************************************
 * AnyEnumeration
 ****************************************************************************/
egl.defineClass( "eglx.lang", "AnyEnumeration",
{
}
);
egl.eglx.lang.AnyEnumeration.equals = function(object1, object2){
	var unboxedOp1 = egl.unboxAny(object1);
	var unboxedOp2 = egl.unboxAny(object2);
	if (unboxedOp1 == null && unboxedOp2 == null)
		return true;
	if (unboxedOp1 == null || unboxedOp2 == null)
		return false;
	return unboxedOp1 == unboxedOp2;
};

egl.eglx.lang.AnyEnumeration.notEquals = function(object1, object2){
	var unboxedOp1 = egl.unboxAny(object1);
	var unboxedOp2 = egl.unboxAny(object2);
	if (unboxedOp1 == null && unboxedOp2 == null)
		return false;
	if (unboxedOp1 == null || unboxedOp2 == null)
		return true;
	return !(unboxedOp1==unboxedOp2);
};
