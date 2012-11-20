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
 * MathLib
 ****************************************************************************/
egl.defineClass(
		'eglx.lang', 'MathLib',
	{
		"constructor" : function() {
		}
	}
);	

egl.eglx.lang.MathLib["random"] = function(x) {
	return Math.random(x);
};
		
egl.eglx.lang.MathLib["abs"] = function(x) {
	if (x instanceof egl.javascript.BigDecimal)
		return (x).abs();

	return Math.abs(x);
};
egl.eglx.lang.MathLib["acos"] = function(/* float */value) {
	return Math.acos(value);
};
egl.eglx.lang.MathLib["asin"] = function(/*float*/value) {
	return Math.asin(value);
};
egl.eglx.lang.MathLib["atan"] = function(/*float*/value) {
	return Math.atan(value);
};
egl.eglx.lang.MathLib["atan2"] = function(/*float*/value1, /*float*/value2) {
	return Math.atan2(value1, value2);
};
egl.eglx.lang.MathLib["ceiling"] = function(/*float*/value) {
	return Math.ceil(value);
};
egl.eglx.lang.MathLib["cos"] = function(/*float*/value) {
	return ( Math.cos(value));
};
egl.eglx.lang.MathLib["cosh"] = function(/*float*/x) {
	return ((Math.exp(x) + Math.exp(-x)) / 2);
};
egl.eglx.lang.MathLib["exp"] = function(/*float*/value) {
	return Math.exp(value);
};
egl.eglx.lang.MathLib["floor"] = function(/*float*/value) {
	return Math.floor(value); //throw egl.createRuntimeException("NOIMPL", null); // TODO sbg Implement
};

egl.eglx.lang.MathLib["frexp"] = function( y, x, assignFunc ) {
	if ( y > 0 ) {
		x = Math.floor( Math.log( y ) / Math.log( 2 ) ) + 1;
	} else if ( y < 0 ) {
		x = Math.floor( Math.log( Math.abs( y ) ) / Math.log( 2 ) ) + 1;
	} else {
		x = 0;
	}
	
	if (assignFunc) {
		assignFunc(x);
	}
	return y * Math.pow( 2, -x );
};

egl.eglx.lang.MathLib["ldexp"] = function(/*float*/x, /*int*/ exp) {
	return x * Math.pow( 2, exp );
};

egl.eglx.lang.MathLib["log"] = function(/*float*/x) {
	if (x > 0) {
		return Math.log(x);
	} else {
		throw egl.createRuntimeException("CRRUI2038E", [ "MathLib.log" ]);
	}
};

egl.eglx.lang.MathLib["log10"] = function(/*float*/x) {
	if (x > 0) {
		return Math.LOG10E * Math.log(x);
	} else {
		throw egl.createRuntimeException("CRRUI2038E", [ "MathLib.log10" ]);
	}
};

egl.eglx.lang.MathLib["max"] = function(x, y) {
	if (x instanceof egl.javascript.BigDecimal)
		return (x).max(y);
	
	return Math.max(x, y);
};

egl.eglx.lang.MathLib["min"] = function(x, y) {
	if (x instanceof egl.javascript.BigDecimal)
		return (x).min(y);
	
	return Math.min(x, y);
};

egl.eglx.lang.MathLib["modf"] = function(/*float*/ num, /*any type wrap of EFloat64 type*/ intOut, assignFunc ) {
	var deciPart, intPart;
	var isBigDecimal = (egl.unboxAny(intOut) instanceof egl.javascript.BigDecimal);
	
	if ( parseInt( num ) == num ) {
		intPart = num;
		deciPart = 0;
	} else {
		var numStr = new String( num );
		intPart = Number( numStr.replace( /\.\d*/, "" ) );
		deciPart = Number( numStr.replace( /\d*\./, "0." ) );
	}
	
	if ( isBigDecimal ) {
		intPart = new egl.javascript.BigDecimal( intPart.toString() );
	}
	
	if (assignFunc) {
		assignFunc(intPart);
	}else{
		intOut.eze$$value = intPart;
	}	
	return deciPart;
};

egl.eglx.lang.MathLib["pow"] = function(/*float*/base, /*float*/exponent) {
	if (base == 0 && exponent <= 0) {
		throw egl.createRuntimeException("CRRUI2040E", [ "MathLib.pow" ]);
	} else if (base < 0 && parseInt(exponent) != parseFloat(exponent)) {
		throw egl.createRuntimeException("CRRUI2041E", [ "MathLib.pow" ]);
	} else {
		return Math.pow(base, exponent);
	}
};

egl.eglx.lang.MathLib["round"] = function( /* value */v, /* exp */e) {
	var rounder;
	//TODO: BigDecimal has "round" method, consider its method future.
	var isBigDecimal = ( v instanceof egl.javascript.BigDecimal );

	if (e > 0) {
		rounder = 5.0 * Math.pow(10, e - 1);
	} else {
		rounder = 5.0 / Math.pow(10, -e + 1);
	}

	if (v >= 0) {
		v = Number( v ) + rounder;
	} else {
		v = Number( v ) - rounder;
	}

	if (e > 0) {
		v = v / Math.pow(10, e);
		v = parseInt(v);
		v = v * Math.pow(10, e);
	} else {
		v = v * Math.pow(10, -e);
		v = parseInt(v);
		v = v / Math.pow(10, -e);
	}

	return isBigDecimal ? new egl.javascript.BigDecimal( String( v ) ) : v;
};

egl.eglx.lang.MathLib["sin"] = function(/*float*/x) {
	return Math.sin(x);
};

egl.eglx.lang.MathLib["sinh"] = function(/*float*/x) {
	return ((Math.exp(x) - Math.exp(-x)) / 2);
};

egl.eglx.lang.MathLib["sqrt"] = function(/*float*/x) {
	if (x >= 0) {
		return Math.sqrt(x);
	} else {
		throw egl.createRuntimeException("CRRUI2039E", [ "MathLib.sqrt" ]);
	}
};

egl.eglx.lang.MathLib["tan"] = function(/*float*/value) {
	return Math.tan(value);
};

egl.eglx.lang.MathLib["tanh"] = function(/*float*/x) {
	return ((Math.exp(2 * x) - 1) / (Math.exp(2 * x) + 1));
};
