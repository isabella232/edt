/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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
/*
 * param: args is an array. The argument for the convertFunc
 */
egl.eglx.lang.convert = function(convertFunc, args){
	if(args[0] == null){
		return null;
	}
    return convertFunc.apply(null, args);
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
	if( obj1 == null && obj2 == null)
		return true;
	if( obj1 == null || obj2 == null)
		return false;
	
	if (obj1.eze$$value !== undefined)
		obj1 = obj1.eze$$value;
	if (obj2.eze$$value !== undefined)
		obj2 = obj2.eze$$value;
	
	if ( obj1 instanceof egl.javascript.BigDecimal ) {
		if ( obj2 instanceof egl.javascript.BigDecimal ) {
			return obj1.compareTo( obj2 ) === 0;
		}
		else if ( typeof obj2 === 'string' || typeof obj2 === 'number' ) {
			return obj1.compareTo( new egl.javascript.BigDecimal( obj2 ) ) === 0;
		}
	}
	else if ( obj2 instanceof egl.javascript.BigDecimal ) {
		if ( typeof obj1 === 'string' || typeof obj1 === 'number' ) {
			return new egl.javascript.BigDecimal( obj1 ).compareTo( obj2 ) === 0;
		}
	}
	if (obj1.equals !== undefined) {
		return obj1.equals( obj2 );
	}
	return obj1 == obj2;
};
egl.eglx.lang.EAny.notEquals = function(obj1, obj2){
	return !egl.eglx.lang.EAny.equals(obj1, obj2);
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
egl.eglx.lang.EInt16.fromEInt64 = function (x) {
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
egl.eglx.lang.EInt16.fromEFloat32 = function (x) {
    return egl.convertFloatToSmallint(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt16.fromEBytes = function (x) { 
	return egl.convertBytesToSmallint(x);
}; 
egl.eglx.lang.EInt16.fromEString = function (x) {
	return egl.convertStringToSmallint(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt16.ezeCast = function (x, nullable) {
	return egl.convertAnyToSmallint(egl.boxAny(x), nullable, egl.createNumericOverflowException);    
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
egl.eglx.lang.EInt16.bitnot = function (x) {
    return egl.bitnot(x); 
};
egl.eglx.lang.EInt16.asNumber= function (x) {
	return egl.boxAny(x, "i;");
};
egl.eglx.lang.EInt16.leftShift = function (x1, x2) {
    return x1 << x2; 
};
egl.eglx.lang.EInt16.rightShiftArithmetic = function (x1, x2) {
    return x1 >> x2; 
};
egl.eglx.lang.EInt16.rightShiftLogical = function (x1, x2) {
    return x1 >>> x2; 
};
egl.eglx.lang.EInt16.negate = function (x) {
    return -x; 
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
	return egl.convertDecimalToInt(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt32.fromEFloat32 = function (x) {
	return egl.convertFloatToInt(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt32.fromEFloat64 = function (x) {
	return egl.convertFloatToInt(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt32.fromEBytes = function (x) { 
	return egl.convertBytesToInt(x);
}; 
egl.eglx.lang.EInt32.fromEString = function (x) {
	return egl.convertStringToInt(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt32.ezeCast = function (x, nullable) {
	return egl.convertAnyToInt(egl.boxAny(x), nullable, egl.createNumericOverflowException);   
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
egl.eglx.lang.EInt32.bitnot = function (x) {
    return egl.bitnot(x); 
};
egl.eglx.lang.EInt32.asNumber= function (x) {
	return egl.boxAny(x, "I;");
};
egl.eglx.lang.EInt32.leftShift = function (x1, x2) {
    return x1 << x2; 
};
egl.eglx.lang.EInt32.rightShiftArithmetic = function (x1, x2) {
    return x1 >> x2; 
};
egl.eglx.lang.EInt32.rightShiftLogical = function (x1, x2) {
    return x1 >>> x2; 
};
egl.eglx.lang.EInt32.negate = function (x) {
    return -x; 
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
egl.eglx.lang.EFloat32.fromEBytes = function (x) {
	return egl.convertBytesToSmallfloat(x);
};
egl.eglx.lang.EFloat32.fromEString = function (x){
	return egl.convertStringToFloat(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EFloat32.ezeCast = function (x, nullable) {
	return egl.convertAnyToSmallfloat(egl.boxAny(x), nullable, egl.createNumericOverflowException);    
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
egl.eglx.lang.EFloat32.negate = function (x) {
    return -x; 
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
egl.eglx.lang.EDecimal.fromEBytes = function (x, decimals, limit) {
	return egl.convertBytesToDecimal(x, decimals, limit);
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
		return egl.convertAnyToDecimal(x, -1, undefined, egl.createNumericOverflowException); 
};
egl.eglx.lang.EDecimal.fromEFloat32 = function (x, decimals, limit) {
	if (limit)
		return egl.convertFloatToDecimal(x, decimals, limit, egl.createNumericOverflowException);
	else
		return egl.convertFloatToDecimal(x, -1, undefined, egl.createNumericOverflowException);
};
egl.eglx.lang.EDecimal.fromEFloat64 = function (x, decimals, limit) {
	if (limit)
		return egl.convertFloatToDecimal(x, decimals, limit, egl.createNumericOverflowException);
	else
		return egl.convertFloatToDecimal(x, -1, undefined, egl.createNumericOverflowException);
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
		return egl.convertAnyToDecimal(egl.boxAny(x), decimals, limit, nullable, egl.createNumericOverflowException);
	else
		return egl.convertAnyToDecimal(egl.boxAny(x), -1, undefined, nullable, egl.createNumericOverflowException); 
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
egl.eglx.lang.EInt64.fromEFloat32 = function (x) {  
	return egl.convertFloatToBigint(x, egl.createNumericOverflowException);
}; 
egl.eglx.lang.EInt64.fromEFloat64 = function (x) {  
	return egl.convertFloatToBigint(x, egl.createNumericOverflowException);
};
egl.eglx.lang.EInt64.fromENumber = function (x, nullable) {  
	nullable = nullable || false;  // TODO nullability should be generated
	return egl.convertAnyToBigint(x, nullable);
};
egl.eglx.lang.EInt64.fromEBytes = function (x) { 
	return egl.convertBytesToBigint(x);
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
	return egl.convertAnyToBigint(egl.boxAny(x), nullable, egl.createNumericOverflowException);    
};
egl.eglx.lang.EInt64.pow = function (x, exp) {
	return x.pow(exp); 
};
egl.eglx.lang.EInt64.fromEInt64 = function (x) {
	return x;
}; 
egl.eglx.lang.EInt64.asNumber= function (x) {
	return egl.boxAny(x, "B;");
};
egl.eglx.lang.EInt64.negate = function (x) {
	return -x;
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
egl.eglx.lang.EFloat64.Infinity = Infinity; 
egl.eglx.lang.EFloat64.NegativeInfinity = -Infinity; 
egl.eglx.lang.EFloat64.NaN = NaN; 
egl.eglx.lang.EFloat64.NegativeZero = -0; 
egl.eglx.lang.EFloat64.fromEInt64 = function (x) { 
	return Number(x.toString()); 
};
egl.eglx.lang.EFloat64.fromEBytes = function (x) {
	return egl.convertBytesToFloat(x);
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
	return egl.convertAnyToFloat(egl.boxAny(x), nullable, egl.createNumericOverflowException);    
};
egl.eglx.lang.EFloat64.pow = function (x, exp) {
	return Math.pow(x, exp); 
};
egl.eglx.lang.EFloat64.asNumber= function (x) {
	return egl.boxAny(x, "F;");
};
egl.eglx.lang.EFloat64.negate = function (x) {
    return -x; 
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
	return egl.convertAnyToBoolean(egl.boxAny(x), nullable);   
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
egl.eglx.lang.EString.ezeCast = function (x, nullable, length) {
	result = egl.convertAnyToString(egl.boxAny(x), nullable);
	return egl.convertTextToStringN( result, length);
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
	if (str == null || startIndex == null || endIndex == null) {
		throw egl.createNullValueException( "CRRUI2005E", [] );
	}
	start = startIndex;
	end = endIndex;
	max = str.length;
	if (start < 1 || start > max) {
		throw egl.createInvalidIndexException( 'CRRUI2014E', [ start, max ], start );
	} else if (end < start || end < 1 || end > max) {
		throw egl.createInvalidIndexException( 'CRRUI2014E', [ end, max ], end );
	}
	return str.substring(start-1, end);
};
egl.eglx.lang.EString.substringAssign = function(tgt, src, start, end){
	return tgt.splice(start, end, src);
};
egl.eglx.lang.EString.fromEInt16 = function (x, len) {
	return egl.eglx.lang.EString.asString(''+x, len );
};
egl.eglx.lang.EString.fromEInt32 = function (x, len) {
	return egl.eglx.lang.EString.asString(''+x, len );
};
egl.eglx.lang.EString.fromEInt64 = function (x, len) {
	return egl.eglx.lang.EString.asString((x).toString(), len );
};
egl.eglx.lang.EString.fromEFloat32 = function (x, len) {
	return egl.eglx.lang.EString.asString(''+x, len );
};
egl.eglx.lang.EString.fromEFloat64 = function (x, len) {
	return egl.eglx.lang.EString.asString((x).toString(), len );
};
egl.eglx.lang.EString.fromEDecimal = function (x, len) {
	return egl.eglx.lang.EString.asString((x).toString(), len );
};
egl.eglx.lang.EString.fromEBoolean = function (x, len) {
	return egl.eglx.lang.EString.asString((x).toString(), len );
};
egl.eglx.lang.EString.fromENumber = function (x, len) {
	return egl.eglx.lang.EString.asString(egl.unboxAny(x).toString(), len );
};
egl.eglx.lang.EString.fromETimestamp = function (timestamp, pattern, len) {
	if(pattern == null) pattern = "yyyyMMddHHmmss";
	var format = egl.eglx.lang.ETimestamp.getFormatFromPattern(pattern);	
	var result =  egl.timeStampToString(timestamp, format);
	return egl.eglx.lang.EString.asString(result, len );
};
egl.eglx.lang.EString.fromEDate = function (d, len) {
	var result = ( d == null ? d : egl.dateToString(d, "MM/dd/yyyy") ); 
	return egl.eglx.lang.EString.asString(result, len );
};
egl.eglx.lang.EString.fromETime = function (d, len) {
	var result = ( d == null ? d : egl.timeToString(d, "HH:mm:ss") ); 
	return egl.eglx.lang.EString.asString(result, len );
};
egl.eglx.lang.EString.fromAnyObject = function (x, len) {
	var result = egl.convertAnyToString(x, false);  //TODO sbg avoid hardcoding the boolean flag
	return egl.eglx.lang.EString.asString(result, len );
};
egl.eglx.lang.EString.matchesPattern = function (str, pattern, escape) {
	if (str == null || pattern == null) {
		throw egl.createNullValueException( "CRRUI2005E", [] );
	}
	if(!pattern) return false;
	escape = escape || "\\";
	var regExpStr = egl.eglx.lang.EString.patternToRegExpStr(pattern, escape);
	try{
		return new RegExp("^" + regExpStr).test(str);
	}catch (oops){
		throw egl.createInvalidPatternException('CRRUI2013E', [pattern]);
	}
};
egl.eglx.lang.EString.isLike = function (str, pattern, escape) {
	if (str == null || pattern == null) {
		throw egl.createNullValueException( "CRRUI2005E", [] );
	}
	escape = escape || "\\";
	
	//EGL 'like' is similar to SQL regular expressions.
	//Change % to .* and _ to . so that we can use RegExp
	var trimP = pattern.trimRight();
	if(!pattern) return false;
	var newPattern = "";
	
	for ( var i = 0; i < trimP.length; i++ ) {
		switch ( trimP.charAt(i) ) {
		
		case "%":
			newPattern += ".*";
			break;
			
		case "_":
			newPattern += ".";
			break;
			
		//escape character found
		//\% -> %, \_ -> _, but for anything else, the escape character is ignored
		case escape:
			var tempChar = trimP.charAt(++i);
			if ( tempChar.length === 0 ) {
				throw egl.createInvalidPatternException('CRRUI2013E', [pattern]);
			}
			//extra handling if the escape char is backslash
			//can filter universal escape chars through to the regexp (\t, \n, etc)
			if ( escape == "\\" && tempChar != "%" && tempChar != "_" ) {
				newPattern += "\\";
			}
			newPattern += egl.eglx.lang.EString.regExpAlias( tempChar );
			break;
			
		//regular character, but make it safe for use in a regexp
		default:
			newPattern += egl.eglx.lang.EString.regExpAlias( trimP.charAt(i) );
			break;	
		}
	}
	newPattern = "\^" + newPattern + "\$";	
	try{
		return (new RegExp( newPattern )).test( str.trimRight() );
	}catch ( oops ){
		throw egl.createInvalidPatternException('CRRUI2013E', [pattern]);
	}
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
	return str.replace(new RegExp(target, "g"), replacement);
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
egl.eglx.lang.EString.getDefaultEncoding = function() {
	return "UTF-16BE";
};
egl.eglx.lang.EString.toBytes = function( str, encoding ) {
	if ( arguments.length === 2 )
		egl.eglx.lang.EString.validateEncoding( encoding );
	var ret, i, c;
	switch ( encoding )
	{
	case 'US-ASCII':
		ret = new Array( str.length );
		for ( i = 0; i < str.length; i++ )
		{
			c = str.charCodeAt( i );
			ret[ i ] = c < 128 ? c : 0x3f; // Like a JVM, insert a question mark (0x3f) if c is invalid.
		}
		break;
	case 'UTF-16LE':
		ret = new Array( str.length * 2 );
		for ( i = 0; i < str.length; i++ )
		{
			c = str.charCodeAt( i );
			ret[ i * 2 ] = c & 0xFF;
			ret[ i * 2 + 1 ] = (c & 0xFF00) >>> 8;
		}
		break;
	case 'UTF-16':
		ret = new Array( str.length * 2 + 2 );
		ret[ 0 ] = 0xFE; // Start with the big-endian byte order marker.
		ret[ 1 ] = 0xFF;
		for ( i = 0; i < str.length; i++ )
		{
			c = str.charCodeAt( i );
			ret[ i * 2 + 2 ] = (c & 0xFF00) >>> 8;
			ret[ i * 2 + 3 ] = c & 0xFF;
		}
		break;
	case 'UTF-8':
		ret = [];
		for ( i = 0; i < str.length; i++ )
		{
			// charCodeAt never returns 65536 or higher, so we don't handle suggorate pairs correctly.
			c = str.charCodeAt( i );
			if ( c <= 0x007F )
			{
				// It's in the ASCII range, so store it unchanged.
				ret.push( c );
			}
			else if ( c <= 0x07FF )
			{
				// Store two bytes: 110xxxxx 10xxxxxx
				ret.push( 0xC0 | (c >>> 6) );
				ret.push( 0x80 | (c & 0x3F) );
			}
			else
			{
				// Store three bytes: 1110xxxx 10xxxxxx 10xxxxxx
				ret.push( 0xE0 | (c >>> 12) );
				ret.push( 0x80 | ((c >>> 6) & 0x3F) );
				ret.push( 0x80 | (c & 0x3F) );
			}
		}
		break;
	default: // UTF-16BE
		ret = new Array( str.length * 2 );
		for ( i = 0; i < str.length; i++ )
		{
			c = str.charCodeAt( i );
			ret[ i * 2 ] = (c & 0xFF00) >>> 8;
			ret[ i * 2 + 1 ] = c & 0xFF;
		}
		break;
	}
	return egl.eglx.lang.EBytes.ezeNew( ret );
};
egl.eglx.lang.EString.fromBytes = function( bytes, encoding ) {
	if ( arguments.length === 1 )
		encoding = egl.eglx.lang.EString.getDefaultEncoding();
	else
		egl.eglx.lang.EString.validateEncoding( encoding );
	var ret, i, c, c2, c3, c4;
	switch ( encoding )
	{
	case 'US-ASCII':
		ret = new Array( bytes.length );
		for ( i = 0; i < bytes.length; i++ )
		{
			c = bytes[ i ];
			ret[ i ] = c < 128 ? String.fromCharCode( c ) : '?'; // Like a JVM, insert a question mark if c is invalid.
		}
		break;
	case 'UTF-16LE':
		ret = new Array( bytes.length / 2 );
		for ( i = 0; i < ret.length; i++ )
		{
			c = bytes[ i * 2 ] | (bytes[ i * 2 + 1 ] << 8);
			ret[ i ] = String.fromCharCode( c );
		}
		break;
	case 'UTF-16':
		// Check for the optional byte order marker.
		if ( bytes[ 0 ] === 0xFE && bytes[ 1 ] === 0xFF )
			return egl.eglx.lang.EString.fromBytes( bytes.slice( 2 ), 'UTF-16BE' );
		else if ( bytes[ 0 ] === 0xFF && bytes[ 1 ] === 0xFE )
			return egl.eglx.lang.EString.fromBytes( bytes.slice( 2 ), 'UTF-16LE' );
		else
			return egl.eglx.lang.EString.fromBytes( bytes, 'UTF-16BE' );
		break;
	case 'UTF-8':
		ret = [];
		var newChar;
		for ( i = 0; i < bytes.length; i++ )
		{
			newChar = null;
			c = bytes[ i ];
			if ( c <= 0x7F )
			{
				// The character is in the ASCII range, so store it unchanged.
				newChar = String.fromCharCode( c );
			}
			else if ( ++i < bytes.length )
			{
				c2 = bytes[ i ];
				if ( (c & 0xE0) == 0xC0 && (c2 & 0xC0) == 0x80 )
				{
					// The character is encoded in two bytes: 110xxxxx 10xxxxxx
					newChar = String.fromCharCode( ((c & 0x1F) << 6) | (c2 & 0x3F) );
				}
				else if ( ++i < bytes.length )
				{
					c3 = bytes[ i ];
					if ( (c & 0xF0) == 0xE0 && (c2 & 0xC0) == 0x80 && (c3 & 0xC0) == 0x80 )
					{
						// The character is encoded in three bytes: 1110xxxx 10xxxxxx 10xxxxxx
						newChar = String.fromCharCode( ((c & 0x0F) << 12) | ((c2 & 0x3F) << 6) | (c3 & 0x3F) );
					}
					else if ( ++i < bytes.length )
					{
						c4 = bytes[ i ];
						if ( (c & 0xF8) == 0xF0 && (c2 & 0xC0) == 0x80 && (c3 & 0xC0) == 0x80 && (c4 & 0xC0) == 0x80 )
						{
							// The character is encoded in four bytes: 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
							newChar = String.fromCharCode( ((c & 0x07) << 18)| ((c2 & 0x3F) << 12) | ((c3 & 0x3F) << 6) | (c4 & 0x3F) );
						}
					}
				}
			}
			
			if ( newChar != null )
			{
				ret.push( newChar );
			}
			else
			{
				// The data is invalid.
				ret.push( '?' );
			}
		}
		break;
	default: // UTF-16BE
		ret = new Array( bytes.length / 2 );
		for ( i = 0; i < ret.length; i++ )
		{
			c = (bytes[ i * 2 ] << 8) | bytes[ i * 2 + 1 ];
			ret[ i ] = String.fromCharCode( c );
		}
		break;
	}
	return ret.join("");
};
egl.eglx.lang.EString.validateEncoding = function( encoding ) {
	switch ( encoding )
	{
	case 'US-ASCII':
	case 'UTF-8':
	case 'UTF-16BE':
	case 'UTF-16LE':
	case 'UTF-16': 
		break;
	default:
		throw egl.createInvalidArgumentException( "CRRUI2110E", [ encoding ] );
	}
};
egl.eglx.lang.EString.nullconcat = function(op1, op2) {
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
	return egl.convertAnyToNumber(egl.boxAny(x, "S;"), false);
};
egl.eglx.lang.EString.compareTo = function (x1, x2) {
    if ((x1 === null) || (x2 === null))
        throw egl.createNullValueException( "CRRUI2005E", [] );
    else {
        return x1 == x2 ? 0 : ( x1 > x2 ? 1 : -1 );
    }
};
egl.eglx.lang.EString.isEmpty = function (x) {
	if ( x === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	return x.length === 0;
};
egl.eglx.lang.EString.reverse = function (x) {
	if ( x === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	return x.split("").reverse().join("");
};
egl.eglx.lang.EString.join = function (separator, strs) {
	if ( separator === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	return strs.join(separator);
};
egl.eglx.lang.EString.compareIgnoreCase = function (x,y) {
	if ( x === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	return x.toLocaleUpperCase().localeCompare(y.toLocaleUpperCase());
};
egl.eglx.lang.EString.split = function (str, sep, limit) {
	if ( str === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	var result;
	if ( arguments.length === 3 ) {
		if ( limit < 1 ) result = [];
		else result = str.split(sep, limit);
	}
	else result = str.split(sep);
	result.setType("[S;");
	return result;
};
egl.eglx.lang.EString.insertStr = function (str1, offset, str2) {
	if ( str1 === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	if ( offset < 1 || offset > str1.length )
		throw egl.createInvalidIndexException( 'CRRUI2014E', [ offset, str1.length ], offset );
	return str1.substring(0,offset-1) + str2 + str1.substring(offset-1);
};
egl.eglx.lang.EString.replaceStrAt = function(str1, start, end, str2) {
	if ( str1 === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	if ( start < 1 || start > str1.length || start > end )
		throw egl.createInvalidIndexException( 'CRRUI2014E', [ start, str1.length ], start );
	if ( end > str1.length )
		throw egl.createInvalidIndexException( 'CRRUI2014E', [ end, str1.length ], end );
	return str1.substring(0,start-1) + str2 + str1.substring(end-1);
};
egl.eglx.lang.EString.indexOfPattern = function(str, pattern)
{
	if (str == null) {
		throw egl.createNullValueException( "CRRUI2005E", [] );
	}
	var escape, startIndex;
	if ( arguments.length === 2 ) {
		escape = "\\";
		startIndex = 1;
	}
	else if ( arguments.length === 4 ) {
		escape = arguments[2];
		startIndex = arguments[3];
		if ( startIndex < 1 || startIndex > str.length )
			throw egl.createInvalidIndexException( 'CRRUI2014E', [ startIndex, str.length ], startIndex );
		str = str.substring( startIndex - 1 );
	}
	else if ( typeof(arguments[2]) === "string" ) {
		escape = arguments[2];
		startIndex = 1;
	}
	else {
		escape = "\\";
		startIndex = arguments[2];
		if ( startIndex < 1 || startIndex > str.length )
			throw egl.createInvalidIndexException( 'CRRUI2014E', [ startIndex, str.length ], startIndex );
		str = str.substring( startIndex - 1 );
	}
	
	var regExpStr = egl.eglx.lang.EString.patternToRegExpStr(pattern, escape);
	try{
		var match = str.search(new RegExp(regExpStr));
		return match === -1 ? 0 : match + startIndex;
	}catch (oops){
		throw egl.createInvalidPatternException('CRRUI2013E', [pattern]);
	}
};
egl.eglx.lang.EString.replacePattern = function(str, pattern)
{
	if (str == null) {
		throw egl.createNullValueException( "CRRUI2005E", [] );
	}
	var escape, replacement, global;
	if (arguments.length === 5)
	{
		escape = arguments[2];
		replacement = arguments[3];
		global = arguments[4];
	}
	else {
		escape = "\\";
		replacement = arguments[2];
		global = arguments[3];
	}
	
	var regExpStr = egl.eglx.lang.EString.patternToRegExpStr(pattern, escape);
	try{
		var rx = global ? new RegExp(regExpStr,'g') : new RegExp(regExpStr);
		return str.replace(rx, function(){return replacement;});
	}catch (oops){
		throw egl.createInvalidPatternException('CRRUI2013E', [pattern]);
	}
};
egl.eglx.lang.EString.splitOnPattern = function (str, pattern) {
	if ( str === null ) throw egl.createNullValueException( "CRRUI2005E", [] );
	
	var escape, limit, result;
	if ( arguments.length === 2 ) {
		escape = "\\";
		limit = 0;
	}
	else if ( arguments.length === 4 ) {
		escape = arguments[2];
		limit = arguments[3];
		if ( limit < 1 ) {
			result = [];
			result.setType("[S;");
			return result;
		}
	}
	else if ( typeof(arguments[2]) === "string" ) {
		escape = arguments[2];
		limit = 0;
	}
	else {
		escape = "\\";
		limit = arguments[2];
		if ( limit < 1 ) {
			result = [];
			result.setType("[S;");
			return result;
		}
	}
	
	var regExpStr = egl.eglx.lang.EString.patternToRegExpStr(pattern, escape);
	try{
		var rx = new RegExp(regExpStr);
		if ( egl.IE )
			result = egl.IE_Split_On_RegExp( str, rx, limit );
		else
			result = limit > 0 ? str.split(rx,limit) : str.split(rx);
	}catch (oops){
		throw egl.createInvalidPatternException('CRRUI2013E', [pattern]);
	}
	result.setType("[S;");
	return result;
};

// This is a replacement for IE's broken implementation of String.split when its
// argument is a RegExp.  Assholes!
egl.IE_Split_On_RegExp = function( str, rx, limit ) {
	limit = limit || Infinity;
	var result = [];
	var info = rx.exec( str );
	while ( info !== null && limit > 0 )
	{
		var separator = info.index;
		result.push( str.substring( 0, separator ) );
		str = str.substring( separator + info[0].length );
		info = rx.exec( str );
		limit--;
	}
	if ( limit > 0 )
	{
		result.push( str );
	}
	return result;
};

//Below are private methods, just for internal use in EString
egl.eglx.lang.EString.asString = function (x, len) {
	if ( typeof(len) == "undefined" ) {
        return x;		
	} else {
		return egl.convertTextToStringN( x, len );
	} 
};
egl.eglx.lang.EString.regExpAlias = function(ch) {
	//takes in a single character and returns an appropriate form to use in regular expressions
	//certain symbols hold special meaning, but calling this function will add a \ to treat them
	//like any character
	switch (ch) {
	case "^":
	case "$":
	case "*":
	case "+":
	case "?":
	case ".":
	case "(":
	case ")":
	case "[":
	case "]":
	case "{":
	case "}":
	case "\\":
		return "\\" + ch;
	
	//nuthin' fancy
	default:
		return ch;
	}
};
egl.eglx.lang.EString.patternToRegExpStr = function(pattern, escape) {
	//converts EGL * ? [^-] symbols to javascript equivalents for matches
	var newPattern = "";
	
	for ( var i = 0; i < pattern.length; i++ ) {
		switch ( pattern.charAt(i) ) {
		
		case "*":
			newPattern += ".*";
			break;
			
		case "?":
			newPattern += ".";
			break;
			
		//escape character found
		case escape:
			var tempChar = pattern.charAt(++i);
			//extra handling if the escape char is backslash
			//can filter universal escape chars through to the regexp (\t, \n, etc)
			if ( escape == "\\" && tempChar != "*" && tempChar != "?" ) {
				newPattern += "\\";
			}
			newPattern += egl.eglx.lang.EString.regExpAlias( tempChar );
			break;
			
		case "[":
			//find a matching ]
			var tempi = i + 1;
			while ( tempi < pattern.length && pattern.charAt(tempi) != "]" ) {
				tempi++;
			}
			
			if ( tempi == pattern.length || pattern.charAt(tempi - 1) == escape ) {
				//no matching ] found
				//needed to also make sure \] doesn't count as ]
				newPattern += "\\[";
			}
			else {
				//matching ] found
				//add everything between the brackets (at the risk of the user)
				newPattern += pattern.substring(i, tempi + 1 );
				i = tempi;
			}
			break;
			
		//regular character, but make it safe for regexp
		default:
			newPattern += egl.eglx.lang.EString.regExpAlias( pattern.charAt(i) );
			break;	
		}
	}
	return newPattern;	
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

String.prototype.endsWith = function(substr) { 
	return (this.indexOf(substr, this.length - substr.length) !== -1);
};
String.prototype.startsWith = function(substr) { 
	return (this.indexOf(substr) == 0);
};
String.prototype.replaceStr = function(target, replacement) {
	return this.replace(target, replacement);
};

/****************************************************************************
 * EBytes
 ****************************************************************************/
egl.defineClass( "eglx.lang", "EBytes", 
		"eglx.lang", "AnyBoxedObject",
{
	"constructor" : function(bytes) {
		egl.eglx.lang.AnyBoxedObject.call(this, bytes);
	}
});
egl.eglx.lang.EBytes.ezeNew = function ( x ) 
{
	if ( typeof x === 'number' )
	{
		var arr = new Array( x );
		for ( var i = 0; i < x; i++ )
		{
			arr[ i ] = 0;
		}
		x = arr;
	}
	x.isEBytes = true;
	return x;
};
egl.eglx.lang.EBytes.length = function (b) {
	return b.length;
};
egl.eglx.lang.EBytes.equals = function(op1, op2) {
	if (op1 === op2)
		return true;
	if (op1 === null || op2 === null || op1.length !== op2.length)
		return false;
	for ( var i = 0; i < op1.length; i++ )
	{
		if ( op1[i] !== op2[i] )
		{
			return false;
		}
	}
	return true;
};
egl.eglx.lang.EBytes.notEquals = function(b1, b2) {
	return !egl.eglx.lang.EBytes.equals( b1, b2 );
};
egl.eglx.lang.EBytes.compareTo = function(op1, op2) 
{
	var i = 0;
	if ( op1.length === op2.length )
	{
		while ( i < op1.length )
		{
			if ( op1[i] != op2[i] )
				return (op1[i] & 0xFF) < (op2[i] & 0xFF) ? -1 : 1;
			i++;
		}
	}
	else if ( op1.length > op2.length )
	{
		while ( i < op2.length )
		{
			if ( op1[i] != op2[i] )
				return (op1[i] & 0xFF) < (op2[i] & 0xFF) ? -1 : 1;
			i++;
		}
		while ( i < op1.length )
		{
			if ( op1[i] != 0 )
				return 1;
			i++;
		}
	}
	else
	{
		while ( i < op1.length )
		{
			if ( op1[i] != op2[i] )
				return (op1[i] & 0xFF) < (op2[i] & 0xFF) ? -1 : 1;
			i++;
		}
		while ( i < op2.length )
		{
			if ( op2[i] != 0 )
				return -1;
			i++;
		}
	}

	return 0;
};
egl.eglx.lang.EBytes.plus = function (op1, op2) {
	if (op1 === null) op1 = [];
	if (op2 === null) op2 = [];
	return egl.eglx.lang.EBytes.ezeNew(op1.concat(op2));
};
egl.eglx.lang.EBytes.concat = function (op1, op2) {
	return egl.eglx.lang.EBytes.plus(op1, op2);
};
egl.eglx.lang.EBytes.nullconcat = function (op1, op2) {
	if (op1 === null || op2 === null)
		return null;
	return egl.eglx.lang.EBytes.plus(op1, op2);
};
egl.eglx.lang.EBytes.substring = function(bytes, startIndex, endIndex) {
	if (bytes == null || startIndex == null || endIndex == null) {
		throw egl.createNullValueException( "CRRUI2005E", [] );
	}
	start = startIndex;
	end = endIndex;
	max = bytes.length;
	if (start < 1 || start > max) {
		throw egl.createInvalidIndexException( 'CRRUI2014E', [ start, max ], start );
	} else if (end < start || end < 1 || end > max) {
		throw egl.createInvalidIndexException( 'CRRUI2014E', [ end, max ], end );
	}
	return egl.eglx.lang.EBytes.ezeNew(bytes.slice(start-1, end));
};
egl.eglx.lang.EBytes.ezeCast = function (x, nullable, length) {
	return egl.convertAnyToBytes(egl.boxAny(x), nullable, length);
};
egl.eglx.lang.EBytes.fromEInt16 = function (x, len) {
	return egl.convertSmallintToBytes(x, len);
};
egl.eglx.lang.EBytes.fromEInt32 = function (x, len) {
	return egl.convertIntToBytes(x, len);
};
egl.eglx.lang.EBytes.fromEInt64 = function (x, len) {
	return egl.convertBigintToBytes(x, len);
};
egl.eglx.lang.EBytes.fromEFloat32 = function (x, len) {
	return egl.convertSmallfloatToBytes(x, len);
};
egl.eglx.lang.EBytes.fromEFloat64 = function (x, len) {
	return egl.convertFloatToBytes(x, len);
};
egl.eglx.lang.EBytes.fromEDecimal = function (x, decSig, len) {
	return egl.convertDecimalToBytes(x, decSig, len);
};
egl.eglx.lang.EBytes.fromENumber = function (x, len) {
	return egl.convertAnyToBytes(x, true, len);
};
egl.eglx.lang.EBytes.toString = function (x) {
	if ( x === null ) return 'null';
	var str = '0x';
	for ( var i = 0; i < x.length; i++ )
	{
		if ( x[i] < 16 )
			str += '0' + x[i].toString( 16 );
		else
			str += x[i].toString( 16 );
	}
	return str;
};
egl.eglx.lang.EBytes.ezeAssignToLonger = function( target, targetLength, source )
{
	if ( source === null )
	{
		return source;
	}
	else if ( target === null )
	{
		target = egl.eglx.lang.EBytes.ezeNew( targetLength );
		for ( var i = 0; i < source.length; i++ )
		{
			target[ i ] = source[ i ];
		}
	}
	else
	{
		for ( var i = 0; i < source.length; i++ )
		{
			target[ i ] = source[ i ];
		}
	}
	return target;
};
egl.eglx.lang.EBytes.ezeAssignFromAny = function( source, target, targetLength )
{
	if ( source === null )
	{
		return source;
	}
	
	if ( target === null )
	{
		target = egl.eglx.lang.EBytes.ezeNew( targetLength || source.length );
	}
	var min = Math.min( target.length, source.length );
	for ( var i = 0; i < min; i++ )
	{
		target[ i ] = source[ i ];
	}
	return target;
};

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
    return egl.convertStringToDate( x ); 
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
	return egl.convertAnyToDate(egl.boxAny(any), nullable);
};

egl.eglx.lang.EDate.dayOf = function(d) {   
	return d.getDate();
};
egl.eglx.lang.EDate.monthOf = function(d) {   
	return d.getMonth() + 1;
};
egl.eglx.lang.EDate.yearOf = function(d) {   
	return d.getFullYear();
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

egl.eglx.lang.ETime.fromETime = function (x) {
	return x;  // TODO sbg bug in generator -- delete this fn when fixed
};
egl.eglx.lang.ETime.fromETimestamp = function (x, pattern) {
	try{
		return egl.eglx.lang.ETimestamp.timeOf(x, pattern);
	}catch(ex){
		throw egl.createTypeCastException( "CRRUI2017E", [ x, 'timestamp', 'time' ], 'time', 'timestamp' );
	}
};
egl.eglx.lang.ETime.fromEString = function (x) {   
    return egl.stringToTimeWithDefaultSeparator( x, "HHmmss" ); 
};
egl.eglx.lang.ETime.equals = function (x, y) {   
	return egl.timeEquals(x, y, false);  //TODO sbg false should be a flag indicating nullable
};
egl.eglx.lang.ETime.notEquals = function (x, y) {   
	return !egl.timeEquals(x, y, false);  //TODO sbg false should be a flag indicating nullable
};
egl.eglx.lang.ETime.compareTo = function (x, y) {   
	return egl.eglx.lang.ETimestamp.compareTo(x, y);
};
egl.eglx.lang.ETime.hourOf = function(t) {   
	return t.getHours();
};
egl.eglx.lang.ETime.minuteOf = function(t) {   
	return t.getMinutes();
};
egl.eglx.lang.ETime.secondOf = function(t) {   
	return t.getSeconds();
};

egl.eglx.lang.ETime.extend = function(/*extension*/ time, /*optional mask*/pattern ) {
	if (time === null)
		throw egl.createNullValueException( "CRRUI2005E", [] );
	else
		return egl.dateTime.extend( "time", time, pattern );
};
egl.eglx.lang.ETime.ezeCast = function (any, nullable) {   
	return egl.convertAnyToTime(egl.boxAny(any), nullable);
};


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
	if (startCode == egl.eglx.lang.ETimestamp.CodeKind.YEAR_CODE && endCode >= egl.eglx.lang.ETimestamp.CodeKind.YEAR_CODE) {
		format = "yyyy";
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
	if (endCode >= egl.eglx.lang.ETimestamp.CodeKind.FRACTION1_CODE) {
		if (separator != null) {
			format += separator;
		}
		var match = pattern.match(/f+/);
		if ( match != null ) {
			format += match[0];
		}
	}
	return format;
};

egl.eglx.lang.ETimestamp.ezeCast = function(x, nullable, pattern){
	return egl.convertAnyToTimestamp(egl.boxAny(x), nullable, pattern || "yyyyMMddHHmmss");  
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
		format = "yyyyMMddHHmmss";
	}else{
		format = pattern;
	}
	return egl.eglx.lang.ETimestamp.extend( egl.stringToTimeStampWithDefaultSeparator(timestamp, format), pattern);
};

egl.eglx.lang.ETimestamp.fromEDate = function (date, pattern){
	return egl.eglx.lang.ETimestamp.extend( date, pattern);
};

egl.eglx.lang.ETimestamp.fromETime = function (time, pattern){
	return egl.eglx.lang.ETimestamp.extend( time, pattern);
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
		"weekdayOf" : [ egl.eglx.lang.ETimestamp.CodeKind.YEAR_CODE, egl.eglx.lang.ETimestamp.CodeKind.DAY_CODE ],
		"hourOf" : [ egl.eglx.lang.ETimestamp.CodeKind.HOUR_CODE, egl.eglx.lang.ETimestamp.CodeKind.HOUR_CODE ],
		"minuteOf" : [ egl.eglx.lang.ETimestamp.CodeKind.MINUTE_CODE, egl.eglx.lang.ETimestamp.CodeKind.MINUTE_CODE ],
		"secondOf" : [ egl.eglx.lang.ETimestamp.CodeKind.SECOND_CODE, egl.eglx.lang.ETimestamp.CodeKind.SECOND_CODE ]
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
	timeCopy.setDate( 1 ); //Bug 372937 changed from current date due to issues with February 29
	timeCopy.setMonth( 0 ); //Bug 372937 changed from current month due to issues with February 29
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

egl.eglx.lang.ETimestamp.hourOf = function (ts, pattern){
	if(	egl.eglx.lang.ETimestamp.checkArgument("hourOf", pattern) ){
		throw egl.createInvalidArgumentException("CRRUI2033E", [ts]);
	};
	return ( (ts) ? ts.getHours() : null );
};

egl.eglx.lang.ETimestamp.minuteOf = function (ts, pattern){
	if(	egl.eglx.lang.ETimestamp.checkArgument("minuteOf", pattern) ){
		throw egl.createInvalidArgumentException("CRRUI2033E", [ts]);
	};
	return ( (ts) ? ts.getMinutes() : null );
};

egl.eglx.lang.ETimestamp.secondOf = function (ts, pattern){
	if(	egl.eglx.lang.ETimestamp.checkArgument("secondOf", pattern) ){
		throw egl.createInvalidArgumentException("CRRUI2033E", [ts]);
	};
	return ( (ts) ? ts.getSeconds() : null );
};


/****************************************************************************
 * ENumber
 ****************************************************************************/
egl.defineClass( "eglx.lang", "ENumber",
{
}
);
egl.eglx.lang.ENumber.ZERO =  {eze$$value : 0, eze$$signature : "I;"};
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
	return egl.convertDecimalToDecimal(egl.boxAny(x), decimals, limit, nullable, egl.createNumericOverflowException);
};
egl.eglx.lang.ENumber.negate = function (x) {
    return {eze$$value : -x.eze$$value, eze$$signature : x.eze$$signature}; 
};
egl.eglx.lang.ENumber.precision = function(x) {
    if (x == null || x.eze$$value == null) {
        throw egl.createNullValueException( "CRRUI2005E", [] );
    }

	var kind;

	var firstChar = x.eze$$signature.charAt(0);
	var firstCharIdx = 0;
	if (firstChar !== '?') {
		kind = firstChar;
	} else {
		kind = x.eze$$signature.charAt(1);
		firstCharIdx = 1;
	}

	switch (kind) {
	case 'I':
		return 9;
	case 'i':
		return 4;
	case 'B':
		return 18;
	case 'F':
		return 15;
	case 'f':
		return 6;
	case 'b':
	case 'n':
	case 'd':
	case '9':
	case 'p':
        var colon = x.eze$$signature.indexOf(':');
        return (colon > 0 ? x.eze$$signature.substring(firstCharIdx + 1, colon) : x.eze$$value.mant.length) ;
	case 'X':
		var length = x.eze$$signature.substring(firstCharIdx + 1,
				x.eze$$signature.indexOf(';'));
		if (length === '8') {
			return 6;
		} else if (length === '16') {
			return 15;
		}
	}
	return 0;
};
egl.eglx.lang.ENumber.decimals = function(x) {
    if (x instanceof egl.javascript.BigDecimal) {
        return x.scale();
    }
    if (x == null || x.eze$$value == null) {
        throw egl.createNullValueException( "CRRUI2005E", [] );
    }

	var kind;

	var firstChar = x.eze$$signature.charAt(0);
	if (firstChar !== '?') {
		kind = firstChar;
	} else {
		kind = x.eze$$signature.charAt(1);
	}

	var result = 0;
	switch (kind) {
	case 'b':
	case 'n':
	case 'd':
	case '9':
	case 'p':
		var colon = x.eze$$signature.indexOf(':');
		result = colon > 0 ? x.eze$$signature.substring(colon + 1, x.eze$$signature.indexOf(';')) : x.eze$$value.scale();
		break;
	case 'F':
	case 'f':
		if (x.eze$$value != 0) {
			var numStr = new egl.javascript.BigDecimal(x.eze$$value).format(-1,-1);
			var pointIndex = numStr.lastIndexOf('.');
			if (pointIndex != -1) {
				// Ignore trailing zeros.
				var lastDigitIndex = numStr.length - 1;
				while (lastDigitIndex > pointIndex && numStr.charAt(lastDigitIndex) == '0') {
					lastDigitIndex--;
				}
				result = lastDigitIndex - pointIndex;
			}
		}
	}

	return result;
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
