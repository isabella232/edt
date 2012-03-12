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
egl.eglx.lang.convert = function(convertFunc, value){
	if(value == null){
		return value;
	}
    return convertFunc.apply(null, Array.prototype.slice.call(arguments, 1));
};
egl.eglx.lang.AnyValue.ezeCopyTo = function(source, target){
	if (source == null) {
		if (target != null) {
			target.eze$$setEmpty();
		}
		return null;
	}
	else {
		if (target == null) {
			target = source.eze$$clone();
		}
		target.ezeCopy(source);
	}
	return target;
};

/****************************************************************************
 * EAny
 ****************************************************************************/
egl.defineClass( "eglx.lang", "EAny",
{
}
);
egl.eglx.lang.converNullValue = function(value, convertFunc, args){
	if(value == null){
		return value;
	}
	return convertFunc(value, args);
};
egl.eglx.lang.EAny.ezeWrap = function(obj){
	return new egl.eglx.lang.AnyBoxedObject(obj);
};
egl.eglx.lang.EAny.ezeCast = function(obj, cons){
	obj = egl.boxAny(obj);
	return egl.convertAnyToRefType(obj, obj.eze$$signature, cons);
};
egl.eglx.lang.EAny.unbox = function(obj){
	return egl.unboxAny(obj);
};
egl.eglx.lang.EAny.fromEAny = function(obj){
	var sig = arguments[arguments.length - 1];
	return egl.boxAny(obj instanceof egl.eglx.lang.AnyBoxedObject?obj.eze$$value:obj,sig);
};
egl.eglx.lang.EAny.equals = function(obj1, obj2){
	if ( obj1.eze$$value != null && obj1.eze$$value.equals ) {
		return obj1.eze$$value.equals( obj2.eze$$value );
	}
	return (obj1.eze$$value == obj2.eze$$value);
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
	return egl.convertNumberToSmallint(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt16.fromEDecimal = function (x) {   
	return egl.convertDecimalToSmallint(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt16.fromENumber = function (x, nullable) {  
	nullable = nullable || false; 
	return egl.convertAnyToSmallint(x, nullable, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt16.fromEFloat64 = function (x) {
	return egl.convertFloatToSmallint(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt16.fromEString = function (x) {
	return egl.convertStringToSmallint(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt16.ezeCast = function (x, nullable) {
	return egl.convertAnyToSmallint(x, nullable, egl.createNumericOverflowException);    
};
egl.eglx.lang.EInt16.pow = function (x, exp) {
	return egl.eglx.lang.EDecimal.fromEInt16(x).pow(egl.eglx.lang.EDecimal.fromEInt16(exp), egl.javascript.BigDecimal.prototype.eglMC); 
};
egl.eglx.lang.EInt16.bitand = function (x1, x2) {
	return egl.bitand(x1, x2); 
};
egl.eglx.lang.EInt16.bitor = function (x1, x2) {
	return egl.bitor(x1, x2); 
};
egl.eglx.lang.EInt16.bitxor = function (x1, x2) {
	return egl.bitxor(x1, x2); 
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
	return egl.convertDecimalToInt(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt32.fromENumber = function (x, nullable) {  
	nullable = nullable || false;  // TODO nullability should be generated
	return egl.convertAnyToInt(x, nullable, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt32.fromAnyObject = function (x) {
	return egl.convertAnyToInt(x,false, egl.createNumericOverflowException);  //TODO sbg avoid hardcoding the boolean flag
};
egl.eglx.lang.EInt32.fromEInt64 = function (x) {
	return egl.convertDecimalToInt(egl.unboxAny(x), egl.createNumericOverflowException);
};
egl.eglx.lang.EInt32.fromEFloat32 = function (x) {
	return egl.convertFloatToInt(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt32.fromEFloat64 = function (x) {
	return egl.convertFloatToInt(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt32.fromEString = function (x) {
	return egl.convertStringToInt(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt32.ezeCast = function (x, nullable) {
	return egl.convertAnyToInt(x, nullable, egl.createNumericOverflowException);   
};
egl.eglx.lang.EInt32.pow = function (x, exp) {
	return egl.eglx.lang.EDecimal.fromEInt16(x).pow(egl.eglx.lang.EDecimal.fromEInt16(exp), egl.javascript.BigDecimal.prototype.eglMC); 
};
egl.eglx.lang.EInt32.bitand = function (x1, x2) {
	return egl.bitand(x1, x2); 
};
egl.eglx.lang.EInt32.bitor = function (x1, x2) {
	return egl.bitor(x1, x2); 
};
egl.eglx.lang.EInt32.bitxor = function (x1, x2) {
	return egl.bitxor(x1, x2); 
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
	return egl.convertFloatToSmallfloat(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EFloat32.fromEInt32  = function (x) {
	return egl.convertFloatToSmallfloat(Number( (new egl.javascript.BigDecimal(x)).toString()), egl.createNumericOverflowException);
};
egl.eglx.lang.EFloat32.fromEInt64  = function (x) {
	return egl.convertFloatToSmallfloat(Number( (new egl.javascript.BigDecimal(x)).toString()), egl.createNumericOverflowException);
};
egl.eglx.lang.EFloat32.fromEDecimal  = function (x) {
	return egl.convertFloatToSmallfloat(Number( x.toString()), egl.createNumericOverflowException);

};
egl.eglx.lang.EFloat32.fromEString = function (x){
	return egl.convertStringToFloat(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EFloat32.ezeCast = function (x, nullable) {
	return egl.convertAnyToSmallfloat(x, nullable, egl.createNumericOverflowException);    
};
egl.eglx.lang.EFloat32.pow = function (x, exp) {
	return Math.pow(x, exp); 
};
egl.eglx.lang.EFloat32.fromENumber = function (x, nullable) {
	nullable = nullable || false;  // TODO nullability should be generated
	return egl.convertFloatToSmallfloat(egl.convertAnyToFloat(x, nullable, egl.createNumericOverflowException), egl.createNumericOverflowException);
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
		return egl.convertIntegerToDecimal(x, limit, egl.createNumericOverflowException);
	else
		return new egl.javascript.BigDecimal(x);
};
egl.eglx.lang.EDecimal.fromEInt32 = function (x, decimals, limit) {   
	if (limit)
		return egl.convertIntegerToDecimal(x, limit, egl.createNumericOverflowException);
	else
		return new egl.javascript.BigDecimal(x);
};
egl.eglx.lang.EDecimal.fromEInt64 = function (x, decimals, limit) {   
	if (limit)
		return egl.convertIntegerToDecimal(x, limit, egl.createNumericOverflowException);
	else
		return new egl.javascript.BigDecimal(x);
};
egl.eglx.lang.EDecimal.fromEDecimal = function (x, decimals, limit) { 
	return egl.convertDecimalToDecimal(x, decimals, limit, egl.createNumericOverflowException);
};
egl.eglx.lang.EDecimal.fromEString = function (x, decimals, limit) { 
	if (limit)
		return egl.convertStringToDecimal(x, decimals, limit, egl.createNumericOverflowException);
	else
		return egl.convertStringToDecimal(x, egl.eglx.lang.EString.textLen(x), decimals);
};
egl.eglx.lang.EDecimal.fromENumber = function (x, decimals, limit) {
	if (limit)
		return egl.convertAnyToDecimal(x, decimals, limit, egl.createNumericOverflowException);
	else
		return egl.convertAnyToDecimal(x, -1, decimals, egl.createNumericOverflowException); 
};
egl.eglx.lang.EDecimal.fromEFloat32 = function (x, decimals, limit) {
	if (limit)
		return egl.convertFloatToDecimal(x, decimals, limit, egl.createNumericOverflowException);
	else
		return egl.convertFloatToDecimal(x, -1, decimals, egl.createNumericOverflowException);
};
egl.eglx.lang.EDecimal.fromEFloat64 = function (x, decimals, limit) {
	if (limit)
		return egl.convertFloatToDecimal(x, decimals, limit, egl.createNumericOverflowException);
	else
		return egl.convertFloatToDecimal(x, -1, decimals, egl.createNumericOverflowException);
};
egl.eglx.lang.EDecimal.equals = function (x, y) {
	if(!x && !y){
		return true;
	}else if(!x || !y){
		return false;
	}
	return (x.compareTo(y) == 0);
};
egl.eglx.lang.EDecimal.notEquals = function(x, y) {
	return !egl.eglx.lang.EDecimal.equals(x, y);
};
egl.eglx.lang.EDecimal.ezeCast = function (x, nullable, decimals, limit) {
	if (limit)
		return egl.convertAnyToDecimal(x, decimals, limit, nullable, egl.createNumericOverflowException);
	else
		return egl.convertAnyToDecimal(x, -1, decimals, nullable, egl.createNumericOverflowException); 
};
egl.eglx.lang.EDecimal.asNumber= function (x, sig) {
	return egl.boxAny(x,sig);
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
	return egl.convertStringToBigint(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt64.fromEDecimal = function (x) {  
	return egl.convertDecimalToBigint(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt64.fromEFloat64 = function (x) {  
	return egl.convertFloatToBigint(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt64.fromENumber = function (x, nullable) {  
	nullable = nullable || false;  // TODO nullability should be generated
	return egl.convertAnyToBigint(x, nullable);
};
egl.eglx.lang.EInt64.fromEFloat32 = function (x) {  
	return egl.convertFloatToBigint(x, egl.createNumericOverflowException);
}; 
egl.eglx.lang.EInt64.equals = function (x, y) {
	if(!x && !y){
		return true;
	}else if(!x || !y){
		return false;
	}
	return (x.compareTo(y) == 0);
};
egl.eglx.lang.EInt64.notEquals = function(x, y) {
	return !egl.eglx.lang.EInt64.equals(x, y);
};
egl.eglx.lang.EInt64.ezeCast = function (x, nullable) {
	return egl.convertAnyToBigint(x, nullable, egl.createNumericOverflowException);    
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
	return egl.convertStringToFloat(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EFloat64.fromENumber = function (x, nullable) {
	nullable = nullable || false;  // TODO nullability should be generated
	return egl.convertAnyToFloat(x, nullable, egl.createNumericOverflowException);
};
egl.eglx.lang.EFloat64.ezeCast = function (x, nullable) {
	return egl.convertAnyToFloat(x, nullable, egl.createNumericOverflowException);    
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
			throw egl.createNullValueException( "CRRUI2005E", [] );
		}
		return str1 == str2;
	}
}
);
egl.eglx.lang.EString.textLen = function (s) {
	if ( s === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	return s.length;
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
		throw egl.createInvalidIndexException( 'CRRUI2019E', [ str, max ], start );
	} else if (end < start || end < 1 || end > max) {
		throw egl.createInvalidIndexException( 'CRRUI2019E', [ str, max ], end );
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
egl.eglx.lang.EString.fromETimestamp = function (timestamp, pattern) {
	if(pattern == null) pattern = "yyyyMMddhhmmss";
	var format = egl.eglx.lang.ETimestamp.getFormatFromPattern(pattern);	
	return egl.timeStampToString(timestamp, format); // TODO sbg Need a constant, but can't depend on eglx.lang.Constants
};
egl.eglx.lang.EString.fromEDate = function (d) {
	return d == null ? d : egl.dateToString(d, "MM/dd/yyyy"); 
};
egl.eglx.lang.EString.fromAnyObject = function (x) {
	return egl.convertAnyToString(x, false);  //TODO sbg avoid hardcoding the boolean flag
};
egl.eglx.lang.EString.matchesPattern = function (str1, str2, esc) {
	if ((str1 == null) || (str2 == null)) {
		throw egl.createNullValueException( "CRRUI2005E", [] );
	}
	esc = esc || "\\";
	return egl.matches(str1, str2, esc);
};
egl.eglx.lang.EString.isLike = function (str1, str2, esc) {
	if ((str1 == null) || (str2 == null)) {
		throw egl.createNullValueException( "CRRUI2005E", [] );
	}
	esc = esc || "\\";
	return egl.like(str1, str2, esc);
};
egl.eglx.lang.EString.indexOf = function (str, pattern, start) {
	if ((str == null) || (pattern == null)) {
		throw egl.createNullValueException( "CRRUI2005E", [] );
	}
	if (!start) {
		start = 1;
	}
	return str.indexOf(pattern, start - 1) + 1;
};
egl.eglx.lang.EString.lastIndexOf = function (str, pattern) {
	if ((str == null) || (pattern == null)) {
		throw egl.createNullValueException( "CRRUI2005E", [] );
	}
	return str.lastIndexOf(pattern) + 1;
};
egl.eglx.lang.EString.replaceStr = function(str, target, replacement) {
	if ((str == null) || (target == null) || (replacement == null)) {
		throw egl.createNullValueException( "CRRUI2005E", [] );
	}
	return str.replace(new RegExp(target, "g"), replacement);   // TODO should we simply alias replaceStr as replace?
};
egl.eglx.lang.EString.charCodeAt = function (str, index) {
	if ((str == null) || (index == null)) {
		throw egl.createNullValueException( "CRRUI2005E", [] );
	}
	return egl.eglx.lang.EString.substring(str, index, index).charCodeAt();
};
egl.eglx.lang.EString.trim = function (str) {
	if (str == null) {
		throw egl.createNullValueException( "CRRUI2005E", [] );
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
egl.eglx.lang.EDate.fromETimestamp = function (x, pattern) {
	try{
		return egl.eglx.lang.ETimestamp.dateOf(x, pattern);
	}catch(ex){
		throw egl.createTypeCastException( "CRRUI2017E", [ x, 'timestamp', 'date' ], 'date', 'timestamp' );
	}
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
		throw egl.createNullValueException( "CRRUI2005E", [] );
	else
		return egl.convertIntToDate(egl.convertDateToInt(x) + y);
};
egl.eglx.lang.EDate.daysDifferent = function (x, y) {
	if ((x === null) || (y === null))
		throw egl.createNullValueException( "CRRUI2005E", [] );
	else
		return egl.convertDateToInt(x) - egl.convertDateToInt(y);
};
egl.eglx.lang.EDate.compareTo = function (x, y) {
	if ((x === null) || (y === null))
		throw egl.createNullValueException( "CRRUI2005E", [] );
	else {
		var diff = egl.convertDateToInt(x) - egl.convertDateToInt(y);
		return diff == 0 ? 0 : diff > 0 ? 1 : -1;
	}
};
egl.eglx.lang.EDate.extend = function(/*extension*/ date, /*optional mask*/pattern ) {
	if (date === null)
		throw egl.createNullValueException( "CRRUI2005E", [] );
	else
		return egl.dateTime.extend( "date", date, pattern );
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
egl.eglx.lang.ETimestamp.CodeKind = {
	YEAR_CODE:		0,
	MONTH_CODE:		1,
	DAY_CODE:		2,
	HOUR_CODE:		3,
	MINUTE_CODE:	4,
	SECOND_CODE:	5,
	FRACTION1_CODE:	6,
	FRACTION2_CODE:	7,
	FRACTION3_CODE:	8,
	FRACTION4_CODE:	9,
	FRACTION5_CODE:	10,
	FRACTION6_CODE:	11
};
egl.eglx.lang.ETimestamp["currentTimeStamp"] = function (pattern) {
	// returns a new Date object
	// timestamp keeps every field
	return egl.eglx.lang.ETimestamp.extend( new Date(), pattern);
};

egl.eglx.lang.ETimestamp["extend"] = function ( /*extension*/ date, /*optional mask*/pattern ) {
	return egl.dateTime.extend("timestamp", date, pattern);
};

egl.eglx.lang.ETimestamp.getStartCode = function(pattern){
	if(pattern == null) return "";
	var startsWith = function(str, substr){
		return str.indexOf(substr) == 0;
	};	
	if (startsWith(pattern, "yyyy"))
		return egl.eglx.lang.ETimestamp.CodeKind.YEAR_CODE;
	else if (startsWith(pattern, "MM"))
		return egl.eglx.lang.ETimestamp.CodeKind.MONTH_CODE;
	else if (startsWith(pattern, "dd"))
		return egl.eglx.lang.ETimestamp.CodeKind.DAY_CODE;
	else if (startsWith(pattern, "HH"))
		return egl.eglx.lang.ETimestamp.CodeKind.HOUR_CODE;
	else if (startsWith(pattern, "mm"))
		return egl.eglx.lang.ETimestamp.CodeKind.MINUTE_CODE;
	else if (startsWith(pattern, "ss"))
		return egl.eglx.lang.ETimestamp.CodeKind.SECOND_CODE;
	else if (startsWith(pattern, "f"))
		return egl.eglx.lang.ETimestamp.CodeKind.FRACTION1_CODE;
	return "";
};

egl.eglx.lang.ETimestamp.getEndCode = function(pattern){
	if(pattern == null) return "";
	var endsWith = function(str, substr){
		return str.slice(0-substr.length) == substr;
	};
	if (endsWith(pattern, "yyyy"))
		return egl.eglx.lang.ETimestamp.CodeKind.YEAR_CODE;
	else if (endsWith(pattern, "MM"))
		return egl.eglx.lang.ETimestamp.CodeKind.MONTH_CODE;
	else if (endsWith(pattern, "dd"))
		return egl.eglx.lang.ETimestamp.CodeKind.DAY_CODE;
	else if (endsWith(pattern, "HH"))
		return egl.eglx.lang.ETimestamp.CodeKind.HOUR_CODE;
	else if (endsWith(pattern, "mm"))
		return egl.eglx.lang.ETimestamp.CodeKind.MINUTE_CODE;
	else if (endsWith(pattern, "ss"))
		return egl.eglx.lang.ETimestamp.CodeKind.SECOND_CODE;
	else if (endsWith(pattern, "ffffff"))
		return egl.eglx.lang.ETimestamp.CodeKind.FRACTION6_CODE;
	else if (endsWith(pattern, "fffff"))
		return egl.eglx.lang.ETimestamp.CodeKind.FRACTION5_CODE;
	else if (endsWith(pattern, "ffff"))
		return egl.eglx.lang.ETimestamp.CodeKind.FRACTION4_CODE;
	else if (endsWith(pattern, "fff"))
		return egl.eglx.lang.ETimestamp.CodeKind.FRACTION3_CODE;
	else if (endsWith(pattern, "ff"))
		return egl.eglx.lang.ETimestamp.CodeKind.FRACTION2_CODE;
	else if (endsWith(pattern, "f"))
		return egl.eglx.lang.ETimestamp.CodeKind.FRACTION1_CODE;
	return "";
};

egl.eglx.lang.ETimestamp.getFormatFromPattern = function(pattern){
	if(pattern == null){
		return "yyyy-MM-dd HH:mm:ss";
	}
	var startCode = egl.eglx.lang.ETimestamp.getStartCode(pattern);
	var endCode = egl.eglx.lang.ETimestamp.getEndCode(pattern);
	var format = "";
	var separator = null;
	if (startCode <= egl.eglx.lang.ETimestamp.CodeKind.YEAR_CODE && endCode >= egl.eglx.lang.ETimestamp.CodeKind.YEAR_CODE) {
		format += "yyyy";
		separator = "-";
	}
	if (startCode <= egl.eglx.lang.ETimestamp.CodeKind.MONTH_CODE && endCode >= egl.eglx.lang.ETimestamp.CodeKind.MONTH_CODE) {
		if (separator != null) {
			format += separator;
		}
		format += "MM";
		separator = "-";
	}
	if (startCode <= egl.eglx.lang.ETimestamp.CodeKind.DAY_CODE && endCode >= egl.eglx.lang.ETimestamp.CodeKind.DAY_CODE) {
		if (separator != null) {
			format += separator;
		}
		format += "dd";
		separator = " ";
	}
	if (startCode <= egl.eglx.lang.ETimestamp.CodeKind.HOUR_CODE && endCode >= egl.eglx.lang.ETimestamp.CodeKind.HOUR_CODE) {
		if (separator != null) {
			format += separator;
		}
		format += "HH";
		separator = ":";
	}
	if (startCode <= egl.eglx.lang.ETimestamp.CodeKind.MINUTE_CODE && endCode >= egl.eglx.lang.ETimestamp.CodeKind.MINUTE_CODE) {
		if (separator != null) {
			format += separator;
		}
		format += "mm";
		separator = ":";
	}
	if (startCode <= egl.eglx.lang.ETimestamp.CodeKind.SECOND_CODE && endCode >= egl.eglx.lang.ETimestamp.CodeKind.SECOND_CODE) {
		if (separator != null) {
			format += separator;
		}
		format += "ss";
		separator = ".";
	}
	if (startCode <= egl.eglx.lang.ETimestamp.CodeKind.FRACTION6_CODE && endCode >= egl.eglx.lang.ETimestamp.CodeKind.FRACTION1_CODE) {
		if (separator != null) {
			format += separator;
		}
		format += "ffffff";
	}
	return format;
};

egl.eglx.lang.ETimestamp.ezeCast = function(x, nullable, pattern){
	return egl.convertAnyToTimestamp(x, nullable, pattern);  
};
egl.eglx.lang.ETimestamp.equals = function (x, y) {   
	return egl.timeStampEquals(x, y, false);  //TODO sbg false should be a flag indicating nullable
};
egl.eglx.lang.ETimestamp.notEquals = function (x, y) {   
	return !egl.timeStampEquals(x, y, false);
};
egl.eglx.lang.ETimestamp.compareTo = function (x, y) {   
	if ((x === null) || (y === null))
		throw egl.createNullValueException( "CRRUI2005E", [] );
	else {
		return x > y ? 1 : x < y ? -1 : 0;
	}
};
egl.eglx.lang.ETimestamp.fromEString = function (timestamp, pattern) {  
	var format;
	if(pattern == null){
		format = "yyyy-MM-dd HH:mm:ss";
	}else{
		format = pattern;
	}
	return egl.eglx.lang.ETimestamp.extend( egl.stringToTimeStamp(timestamp, format), pattern);
};

egl.eglx.lang.ETimestamp.fromEDate = function (date, pattern){
	return egl.eglx.lang.ETimestamp.extend( date, pattern);
};

egl.eglx.lang.ETimestamp.checkArgument = function(functionName, pattern){
	var startCode = egl.eglx.lang.ETimestamp.getStartCode(pattern);
	var endCode = egl.eglx.lang.ETimestamp.getEndCode(pattern);
	function checkArgs( minCode, maxCode ){
		return (startCode > minCode || endCode < maxCode);
	};
	var checkFuncs = {
		"dateOf" : [ egl.eglx.lang.ETimestamp.CodeKind.YEAR_CODE, egl.eglx.lang.ETimestamp.CodeKind.DAY_CODE ],
		"timeOf" : [ egl.eglx.lang.ETimestamp.CodeKind.HOUR_CODE, egl.eglx.lang.ETimestamp.CodeKind.SECOND_CODE ],
		"dayOf" : [ egl.eglx.lang.ETimestamp.CodeKind.DAY_CODE, egl.eglx.lang.ETimestamp.CodeKind.DAY_CODE ],
		"monthOf" : [ egl.eglx.lang.ETimestamp.CodeKind.MONTH_CODE, egl.eglx.lang.ETimestamp.CodeKind.MONTH_CODE ],
		"yearOf" : [ egl.eglx.lang.ETimestamp.CodeKind.YEAR_CODE, egl.eglx.lang.ETimestamp.CodeKind.YEAR_CODE ],
		"weekdayOf" : [ egl.eglx.lang.ETimestamp.CodeKind.YEAR_CODE, egl.eglx.lang.ETimestamp.CodeKind.DAY_CODE ]
	};
	var args = checkFuncs[functionName];
	return 	checkArgs(args[0], args[1], args[2]);
};

egl.eglx.lang.ETimestamp.dateOf = function (ts, pattern){
	if( egl.eglx.lang.ETimestamp.checkArgument("dateOf", pattern) ){
		throw egl.createInvalidArgumentException("CRRUI2033E", [ts]);
//		throw egl.createTypeCastException( "CRRUI2017E", [ x, 'timestamp', 'date' ], 'date', 'timestamp' );
	}
	return ts;
};

egl.eglx.lang.ETimestamp.timeOf = function (ts, pattern){
	if( egl.eglx.lang.ETimestamp.checkArgument("timeOf", pattern) ){
		throw egl.createInvalidArgumentException("CRRUI2033E", [ts]);
//		throw egl.createTypeCastException( "CRRUI2017E", [ ts, 'timestamp', 'time' ], 'time', 'timestamp' );
	}
	var timeCopy = new Date( ts );
	var now = new Date ();
	timeCopy.setDate( now.getDate() );
	timeCopy.setMonth( now.getMonth() );
	timeCopy.setFullYear( 2000 );	
	timeCopy.setMilliseconds( 0 );
	return timeCopy;	
};

egl.eglx.lang.ETimestamp.dayOf = function (ts, pattern){
	if(	egl.eglx.lang.ETimestamp.checkArgument("dayOf", pattern) ){
		throw egl.createInvalidArgumentException("CRRUI2033E", [ts]);
	};
	return ( (ts) ? ts.getDate() : null );
};

egl.eglx.lang.ETimestamp.monthOf = function (ts, pattern){
	if(	egl.eglx.lang.ETimestamp.checkArgument("monthOf", pattern) ){
		throw egl.createInvalidArgumentException("CRRUI2033E", [ts]);
	};
	return ( (ts) ? ts.getMonth() + 1 : null );
};

egl.eglx.lang.ETimestamp.yearOf = function (ts, pattern){
	if(	egl.eglx.lang.ETimestamp.checkArgument("yearOf", pattern) ){
		throw egl.createInvalidArgumentException("CRRUI2033E", [ts]);
	};
	return ( (ts) ? ts.getFullYear() : null );
};

egl.eglx.lang.ETimestamp.weekdayOf = function (ts, pattern){
	if(	egl.eglx.lang.ETimestamp.checkArgument("weekdayOf", pattern) ){
		throw egl.createInvalidArgumentException("CRRUI2033E", [ts]);
	};
	return ( (ts) ? ts.getDay() : null );
};


/****************************************************************************
 * ENumber
 ****************************************************************************/
egl.defineClass( "eglx.lang", "ENumber",
{
}
);
egl.eglx.lang.ENumber.ZERO =  {eze$$value : 0, eze$$signature : "N;"};
egl.eglx.lang.ENumber.fromEInt16 = function (x, decimals, limit) {   
		return egl.convertIntegerToDecimal(x, limit, egl.createNumericOverflowException);
};
egl.eglx.lang.ENumber.fromEInt32 = function (x, decimals, limit) {   
		return egl.convertIntegerToDecimal(x, limit, egl.createNumericOverflowException);
};
egl.eglx.lang.ENumber.fromEDecimal = function (x, decimals, limit) { 
	return egl.convertDecimalToDecimal(x, decimals, limit, egl.createNumericOverflowException);
};
egl.eglx.lang.ENumber.fromEFloat64 = function (x, decimals, limit) { 
	return egl.convertFloatToDecimal(x, decimals, limit, egl.createNumericOverflowException);
};

egl.eglx.lang.ENumber.ezeCast = function(x, nullable, decimals, limit){
	return egl.convertDecimalToDecimal(x, decimals, limit, nullable, egl.createNumericOverflowException);
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

/****************************************************************************
 * AnyDelegate
 ****************************************************************************/
egl.defineClass( "eglx.lang", "AnyDelegate",
{
}
);
egl.eglx.lang.AnyDelegate.equals = function(object1, object2){
	if (object1 == null && object2 == null)
		return true;
	if (object1 == null || object2 == null)
		return false;
	return object1.eq( object2 );
};

egl.eglx.lang.AnyDelegate.notEquals = function(object1, object2){
	if (object1 == null && object2 == null)
		return false;
	if (object1 == null || object2 == null)
		return true;
	return !(object1.eq( object2 ));
};
