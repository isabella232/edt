/* 
 * This class implements a JSON parser to validate JSON input before eval-ing it. 
 * 
 * This code is based on the JSON Parser from the Yahoo UI JSON Utility (http://developer.yahoo.com/yui/json/).
 * That parser in turn is based on Douglas Crockford's JSON parsing and stringifying methods.
 * In the Yahoo JSON Utility, Douglas Crockford's JSON parsing and stringifying methods are adapted from 
 * work published at JSON.org. The adapted work is in the public domain.
 * 
 * Software License Agreement (BSD License)
 * Copyright (c) 2008, 2011, Yahoo! Inc.
 * All rights reserved.
 * 
 * Redistribution and use of this software in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following 
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of Yahoo! Inc. nor the names of its contributors may be used to endorse or promote 
 * products derived from this software without specific prior written permission of Yahoo! Inc.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

String.prototype.trim = function() {
	return this.replace(/^\s+|\s+$/g,"");
}

egl.defineClass( 'eglx.json', 'JSONParser', {
	"constructor" : function()
	{
		try { egl.enter("<init>",this,arguments);
		if(egl.eglx.json.$JSONParser) return egl.eglx.json.$JSONParser;
		egl.eglx.json.$JSONParser=this;
		this.jsrt$SysVar= new egl.egl.core.SysVar();
		new egl.eglx.lang.StringLib();
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