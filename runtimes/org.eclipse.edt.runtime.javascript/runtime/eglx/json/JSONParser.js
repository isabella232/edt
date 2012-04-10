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

egl.defineClass( 'eglx.json', 'JSONParser', {
	"constructor" : function()
	{
		try { egl.enter("<init>",this,arguments);
		if(egl.eglx.json.$JSONParser) return egl.eglx.json.$JSONParser;
		egl.eglx.json.$JSONParser=this;
		} finally { egl.leave(); }
	}
	,
    /**
     * First step in the validation.  Regex used to replace all escape
     * sequences (i.e. "\\", etc) with '@' characters (a non-JSON character).
     * @property _ESCAPES
     * @type {RegExp}
     * @static
     * @private
     */
    _ESCAPES : /\\["\\\/bfnrtu]/g,
    /**
     * Second step in the validation.  Regex used to replace all simple
     * values with ']' characters.
     * @property _VALUES
     * @type {RegExp}
     * @static
     * @private
     */
    _VALUES  : /"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,
    /**
     * Third step in the validation.  Regex used to remove all open square
     * brackets following a colon, comma, or at the beginning of the string.
     * @property _BRACKETS
     * @type {RegExp}
     * @static
     * @private
     */
    _BRACKETS : /(?:^|:|,)(?:\s*\[)+/g,
    /**
     * Final step in the validation.  Regex used to test the string left after
     * all previous replacements for invalid characters.
     * @property _INVALID
     * @type {RegExp}
     * @static
     * @private
     */
    _INVALID  : /^[\],:{}\s]*$/,

    /**
     * Regex used to replace special characters in strings for JSON
     * stringification.
     * @property _SPECIAL_CHARS
     * @type {RegExp}
     * @static
     * @private
     */
    _SPECIAL_CHARS : /["\\\x00-\x1f\x7f-\x9f]/g,

    /**
     * Regex used to reconstitute serialized Dates.
     * @property _PARSE_DATE
     * @type {RegExp}
     * @static
     * @private
     */
    _PARSE_DATE : /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})Z$/,

    /**
     * Character substitution map for common escapes and special characters.
     * @property _CHARS
     * @type {Object}
     * @static
     * @private
     */
    _CHARS : {
        '\b': '\\b',
        '\t': '\\t',
        '\n': '\\n',
        '\f': '\\f',
        '\r': '\\r',
        '"' : '\\"',
        '\\': '\\\\'
    },

    /**
     * Traverses nested objects, applying a filter or mutation function to
     * each value.  The value returned from the function will replace the
     * original value in the key:value pair.  If the value returned is
     * undefined, the key will be omitted from the returned object.
     * @method _applyFilter
     * @param data {MIXED} Any JavaScript data
     * @param filter {Function} filter or mutation function
     * @return {MIXED} The results of the filtered data
     * @static
     * @private
     */
    _applyFilter : function (data, filter) {
        var walk = function (k,v) {
            var i, n;
            if (v && typeof v === 'object') {
                for (i in v) {
                    if (v.hasOwnProperty(i)) {
                        n = walk(i, v[i]);
                        if (n === undefined) {
                            delete v[i];
                        } else {
                            v[i] = n;
                        }
                    }
                }
            }
            return filter(k, v);
        };

        if (typeof(filter) == "function") {
            walk('',data);
        }

        return data;
    },

    /**
     * Four step determination whether a string is valid JSON.  In three steps,
     * escape sequences, safe values, and properly placed open square brackets
     * are replaced with placeholders or removed.  Then in the final step, the
     * result of all these replacements is checked for invalid characters.
     * @method isValid
     * @param str {String} JSON string to be tested
     * @return {boolean} is the string safe for eval?
     * @static
     */
    isValid : function (str) {
        if (typeof(str) !== "string") {
            return false;
        }

        return this._INVALID.test(str.
                replace(this._ESCAPES,'@').
                replace(this._VALUES,']').
                replace(this._BRACKETS,''));
    },

    /**
     * Serializes a Date instance as a UTC date string.  Used internally by
     * stringify.  Override this method if you need Dates serialized in a
     * different format.
     * @method dateToString
     * @param d {Date} The Date to serialize
     * @return {String} stringified Date in UTC format YYYY-MM-DDTHH:mm:SSZ
     * @static
     */
    dateToString : function (d) {
        function _zeroPad(v) {
            return v < 10 ? '0' + v : v;
        }

        return '"' + d.getUTCFullYear()   + '-' +
            _zeroPad(d.getUTCMonth() + 1) + '-' +
            _zeroPad(d.getUTCDate())      + 'T' +
            _zeroPad(d.getUTCHours())     + ':' +
            _zeroPad(d.getUTCMinutes())   + ':' +
            _zeroPad(d.getUTCSeconds())   + 'Z"';
    },

    /**
     * Reconstitute Date instances from the default JSON UTC serialization.
     * Reference this from a parse filter function to rebuild Dates during the
     * parse operation.
     * @method stringToDate
     * @param str {String} String serialization of a Date
     * @return {Date}
     */
    stringToDate : function (str) {
        if (this._PARSE_DATE.test(str)) {
            var d = new Date();
            d.setUTCFullYear(RegExp.$1, (RegExp.$2|0)-1, RegExp.$3);
            d.setUTCHours(RegExp.$4, RegExp.$5, RegExp.$6);
            return d;
        }
    },

    /**
     * Parse a JSON string, returning the native JavaScript representation.
     * Only minor modifications from http://www.json.org/json.js.
     * @param s {string} JSON string data
     * @param filter {function} (optional) function(k,v) passed each key value pair of object literals, allowing pruning or altering values
     * @return {MIXED} the native JavaScript representation of the JSON string
     * @throws SyntaxError
     * @method parse
     * @static
     * @public
     */
    parse : function (s,filter) {
        try {
        	egl.enter("parse", this, arguments);
        	// Ensure valid JSON
		    if (this.isValid(s)) {
		        // Eval the text into a JavaScript data structure, apply any
		        // filter function, and return
		        return this._applyFilter( eval('(' + s + ')'), filter );
		    }
        }
        finally {
        	egl.leave();
        }
        // The text is not JSON parsable
        throw egl.createRuntimeException( "CRRUI2104E", [s] );
    },


    /**
     * Converts an arbitrary value to a JSON string representation.
     * Cyclical object or array references are replaced with null.
     * If a whitelist is provided, only matching object keys will be included.
     * If a depth limit is provided, objects and arrays at that depth will
     * be stringified as empty.
     * @method stringify
     * @param o {MIXED} any arbitrary object to convert to JSON string
     * @param w {Array} (optional) whitelist of acceptable object keys to include
     * @param d {number} (optional) depth limit to recurse objects/arrays (practical minimum 1)
     * @return {string} JSON string representation of the input
     * @static
     * @public
     */
    stringify : function (o,w,d) {

        var m      = this._CHARS,
            str_re = this._SPECIAL_CHARS,
            pstack = []; // Processing stack used for cyclical ref protection

        // escape encode special characters
        var _char = function (c) {
            if (!m[c]) {
                var a = c.charCodeAt();
                m[c] = '\\u00' + Math.floor(a / 16).toString(16) +
                                           (a % 16).toString(16);
            }
            return m[c];
        };

        // Enclose the escaped string in double quotes
        var _string = function (s) {
            return '"' + s.replace(str_re, _char) + '"';
        };

        // Use the configured date conversion
        var _date = J.dateToString;
    
        // Worker function.  Fork behavior on data type and recurse objects and
        // arrays per the configured depth.
        var _stringify = function (o,w,d) {
            var t = typeof o,
                i,len,j, // array iteration
                k,v,     // object iteration
                vt,      // typeof v during iteration
                a;       // composition array for performance over string concat

            // String
            if (t === 'string') {
                return _string(o);
            }

            // native boolean and Boolean instance
            if (t === 'boolean' || o instanceof Boolean) {
                return String(o);
            }

            // native number and Number instance
            if (t === 'number' || o instanceof Number) {
                return isFinite(o) ? String(o) : 'null';
            }

            // Date
            if (o instanceof Date) {
                return _date(o);
            }

            // Array
            if (l.isArray(o)) {
                // Check for cyclical references
                for (i = pstack.length - 1; i >= 0; --i) {
                    if (pstack[i] === o) {
                        return 'null';
                    }
                }

                // Add the array to the processing stack
                pstack[pstack.length] = o;

                a = [];
                // Only recurse if we're above depth config
                if (d > 0) {
                    for (i = o.length - 1; i >= 0; --i) {
                        a[i] = _stringify(o[i],w,d-1) || 'null';
                    }
                }

                // remove the array from the stack
                pstack.pop();

                return '[' + a.join(',') + ']';
            }

            // Object
            if (t === 'object') {
                // Test for null reporting as typeof 'object'
                if (!o) {
                    return 'null';
                }

                // Check for cyclical references
                for (i = pstack.length - 1; i >= 0; --i) {
                    if (pstack[i] === o) {
                        return 'null';
                    }
                }

                // Add the object to the  processing stack
                pstack[pstack.length] = o;

                a = [];
                // Only recurse if we're above depth config
                if (d > 0) {

                    // If whitelist provided, take only those keys
                    if (w) {
                        for (i = 0, j = 0, len = w.length; i < len; ++i) {
                            if (typeof w[i] === 'string') {
                                v = _stringify(o[w[i]],w,d-1);
                                if (v) {
                                    a[j++] = _string(w[i]) + ':' + v;
                                }
                            }
                        }

                    // Otherwise, take all valid object properties
                    // omitting the prototype chain properties
                    } else {
                        j = 0;
                        for (k in o) {
                            if (typeof k === 'string' && l.hasOwnProperty(o,k)) {
                                v = _stringify(o[k],w,d-1);
                                if (v) {
                                    a[j++] = _string(k) + ':' + v;
                                }
                            }
                        }
                    }
                }

                // Remove the object from processing stack
                pstack.pop();

                return '{' + a.join(',') + '}';
            }

            return undefined; // invalid input
        };

        // Default depth to POSITIVE_INFINITY
        d = d >= 0 ? d : 1/0;

        // process the input
        return _stringify(o,w,d);
    }
});
new egl.eglx.json.JSONParser();
/*
http://www.JSON.org/json_parse_state.js
2009-05-31

Public Domain.

NO WARRANTY EXPRESSED OR IMPLIED. USE AT YOUR OWN RISK.

This file creates a json_parse function.

    json_parse(text, reviver)
        This method parses a JSON text to produce an object or array.
        It can throw a SyntaxError exception.

        The optional reviver parameter is a function that can filter and
        transform the results. It receives each of the keys and values,
        and its return value is used instead of the original value.
        If it returns what it received, then the structure is not modified.
        If it returns undefined then the member is deleted.

        Example:

        // Parse the text. Values that look like ISO date strings will
        // be converted to Date objects.

        myData = json_parse(text, function (key, value) {
            var a;
            if (typeof value === 'string') {
                a =
/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*)?)Z$/.exec(value);
                if (a) {
                    return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3], +a[4],
                        +a[5], +a[6]));
                }
            }
            return value;
        });

This is a reference implementation. You are free to copy, modify, or
redistribute.

This code should be minified before deployment.
See http://javascript.crockford.com/jsmin.html

USE YOUR OWN COPY. IT IS EXTREMELY UNWISE TO LOAD CODE FROM SERVERS YOU DO
NOT CONTROL.
*/

/*jslint regexp: false*/

/*members "", "\"", ",", "\/", ":", "[", "\\", "]", acomma, avalue, b,
call, colon, container, exec, f, false, firstavalue, firstokey,
fromCharCode, go, hasOwnProperty, key, length, n, null, ocomma, okey,
ovalue, pop, push, r, replace, slice, state, t, test, true, value, "{",
"}"
*/

egl.eglx.json.$JSONParser["json_parse_state"] = (function () {

	//This function creates a JSON parse function that uses a state machine rather
	//than the dangerous eval function to parse a JSON text.
	
	var state,      // The state of the parser, one of
	                // 'go'         The starting state
	                // 'ok'         The final, accepting state
	                // 'firstokey'  Ready for the first key of the object or
	                //              the closing of an empty object
	                // 'okey'       Ready for the next key of the object
	                // 'colon'      Ready for the colon
	                // 'ovalue'     Ready for the value half of a key/value pair
	                // 'ocomma'     Ready for a comma or closing }
	                // 'firstavalue' Ready for the first value of an array or
	                //              an empty array
	                // 'avalue'     Ready for the next value of an array
	                // 'acomma'     Ready for a comma or closing ]
	    stack,      // The stack, for controlling nesting.
	    container,  // The current container object or array
	    key,        // The current key
	    value,      // The current value
	    escapes = { // Escapement translation table
	        '\\': '\\',
	        '"': '"',
	        '/': '/',
	        't': '\t',
	        'n': '\n',
	        'r': '\r',
	        'f': '\f',
	        'b': '\b'
	    },
	    string = {   // The actions for string tokens
	        go: function () {
	            state = 'ok';
	        },
	        firstokey: function () {
	            key = value;
	            state = 'colon';
	        },
	        okey: function () {
	            key = value;
	            state = 'colon';
	        },
	        ovalue: function () {
	            state = 'ocomma';
	        },
	        firstavalue: function () {
	            state = 'acomma';
	        },
	        avalue: function () {
	            state = 'acomma';
	        }
	    },
	    number = {   // The actions for number tokens
	        go: function () {
	            state = 'ok';
	        },
	        ovalue: function () {
	            state = 'ocomma';
	        },
	        firstavalue: function () {
	            state = 'acomma';
	        },
	        avalue: function () {
	            state = 'acomma';
	        }
	    },
	    action = {
	
	//The action table describes the behavior of the machine. It contains an
	//object for each token. Each object contains a method that is called when
	//a token is matched in a state. An object will lack a method for illegal
	//states.
	
	        '{': {
	            go: function () {
	                stack.push({state: 'ok'});
	                container = {};
	                state = 'firstokey';
	            },
	            ovalue: function () {
	                stack.push({container: container, state: 'ocomma', key: key});
	                container = {};
	                state = 'firstokey';
	            },
	            firstavalue: function () {
	                stack.push({container: container, state: 'acomma'});
	                container = {};
	                state = 'firstokey';
	            },
	            avalue: function () {
	                stack.push({container: container, state: 'acomma'});
	                container = {};
	                state = 'firstokey';
	            }
	        },
	        '}': {
	            firstokey: function () {
	                var pop = stack.pop();
	                value = container;
	                container = pop.container;
	                key = pop.key;
	                state = pop.state;
	            },
	            ocomma: function () {
	                var pop = stack.pop();
	                container[key] = value;
	                value = container;
	                container = pop.container;
	                key = pop.key;
	                state = pop.state;
	            }
	        },
	        '[': {
	            go: function () {
	                stack.push({state: 'ok'});
	                container = [];
	                state = 'firstavalue';
	            },
	            ovalue: function () {
	                stack.push({container: container, state: 'ocomma', key: key});
	                container = [];
	                state = 'firstavalue';
	            },
	            firstavalue: function () {
	                stack.push({container: container, state: 'acomma'});
	                container = [];
	                state = 'firstavalue';
	            },
	            avalue: function () {
	                stack.push({container: container, state: 'acomma'});
	                container = [];
	                state = 'firstavalue';
	            }
	        },
	        ']': {
	            firstavalue: function () {
	                var pop = stack.pop();
	                value = container;
	                container = pop.container;
	                key = pop.key;
	                state = pop.state;
	            },
	            acomma: function () {
	                var pop = stack.pop();
	                container.push(value);
	                value = container;
	                container = pop.container;
	                key = pop.key;
	                state = pop.state;
	            }
	        },
	        ':': {
	            colon: function () {
	                if (Object.hasOwnProperty.call(container, key)) {
	                    throw new SyntaxError('Duplicate key "' + key + '"');
	                }
	                state = 'ovalue';
	            }
	        },
	        ',': {
	            ocomma: function () {
	                container[key] = value;
	                state = 'okey';
	            },
	            acomma: function () {
	                container.push(value);
	                state = 'avalue';
	            }
	        },
	        'true': {
	            go: function () {
	                value = true;
	                state = 'ok';
	            },
	            ovalue: function () {
	                value = true;
	                state = 'ocomma';
	            },
	            firstavalue: function () {
	                value = true;
	                state = 'acomma';
	            },
	            avalue: function () {
	                value = true;
	                state = 'acomma';
	            }
	        },
	        'false': {
	            go: function () {
	                value = false;
	                state = 'ok';
	            },
	            ovalue: function () {
	                value = false;
	                state = 'ocomma';
	            },
	            firstavalue: function () {
	                value = false;
	                state = 'acomma';
	            },
	            avalue: function () {
	                value = false;
	                state = 'acomma';
	            }
	        },
	        'null': {
	            go: function () {
	                value = null;
	                state = 'ok';
	            },
	            ovalue: function () {
	                value = null;
	                state = 'ocomma';
	            },
	            firstavalue: function () {
	                value = null;
	                state = 'acomma';
	            },
	            avalue: function () {
	                value = null;
	                state = 'acomma';
	            }
	        }
	    };
	
	function debackslashify(text) {
	
	//Remove and replace any backslash escapement.
	
	    return text.replace(/\\(?:u(.{4})|([^u]))/g, function (a, b, c) {
	        return b ? String.fromCharCode(parseInt(b, 16)) : escapes[c];
	    });
	}
	
	return function (source, reviver) {
	
	//A regular expression is used to extract tokens from the JSON text.
	//The extraction process is cautious.
	
	    var r,          // The result of the exec method.
	        tx = /^[\x20\t\n\r]*(?:([,:\[\]{}]|true|false|null)|(-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)|"((?:[^\r\n\t\\\"]|\\(?:["\\\/trnfb]|u[0-9a-fA-F]{4}))*)")/;
	
	//Set the starting state.
	
	    state = 'go';
	
	//The stack records the container, key, and state for each object or array
	//that contains another object or array while processing nested structures.
	
	    stack = [];
	
	//If any error occurs, we will catch it and ultimately throw a syntax error.
	
	    try {
	
	//For each token...
	
	        for (;;) {
	            r = tx.exec(source);
	            if (!r) {
	                break;
	            }
	
	//r is the result array from matching the tokenizing regular expression.
	//r[0] contains everything that matched, including any initial whitespace.
	//r[1] contains any punctuation that was matched, or true, false, or null.
	//r[2] contains a matched number, still in string form.
	//r[3] contains a matched string, without quotes but with ecapement.
	
	            if (r[1]) {
	
	//Token: Execute the action for this state and token.
	
	                action[r[1]][state]();
	
	            } else if (r[2]) {
	
	//Number token: Convert the number string into a number value and execute
	//the action for this state and number.
	
	            	//A number has 15.955 valid digits 
	            	//r[2] could have a sign and a decimal point
	            	//  So 15 digits can be easily represented by number
	            	//  Since the string could have a sign and a decimal point the string may have only 13 digits
	            	if(r[2].length < 16){
	            		value = +r[2];
	            	}
	            	else{
	            		value = new egl.javascript.BigDecimal( r[2] );
	            	}
	                number[state]();
	            } else {
	
	//String token: Replace the escapement sequences and execute the action for
	//this state and string.
	
	                value = debackslashify(r[3]);
	                string[state]();
	            }
	
	//Remove the token from the string. The loop will continue as long as there
	//are tokens. This is a slow process, but it allows the use of ^ matching,
	//which assures that no illegal tokens slip through.
	
	            source = source.slice(r[0].length);
	        }
	
	//If we find a state/token combination that is illegal, then the action will
	//cause an error. We handle the error by simply changing the state.
	
	    } catch (e) {
	        state = e;
	    }
	
	//The parsing is finished. If we are not in the final 'ok' state, or if the
	//remaining source contains anything except whitespace, then we did not have
	//a well-formed JSON text.
	
	    if (state !== 'ok' || /[^\x20\t\n\r]/.test(source)) {
	        throw state instanceof SyntaxError ? state : new SyntaxError('JSON');
	    }
	
	//If there is a reviver function, we recursively walk the new structure,
	//passing each name/value pair to the reviver function for possible
	//transformation, starting with a temporary root object that holds the current
	//value in an empty key. If there is not a reviver function, we simply return
	//that value.
	
	    return typeof reviver === 'function' ? (function walk(holder, key) {
	        var k, v, value = holder[key];
	        if (value && typeof value === 'object') {
	            for (k in value) {
	                if (Object.hasOwnProperty.call(value, k)) {
	                    v = walk(value, k);
	                    if (v !== undefined) {
	                        value[k] = v;
	                    } else {
	                        delete value[k];
	                    }
	                }
	            }
	        }
	        return reviver.call(holder, key, value);
	    }({'': value}, '')) : value;
	};
}());
