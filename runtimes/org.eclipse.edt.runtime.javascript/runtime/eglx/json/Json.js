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
/**
 * JsonLib
 */
egl.defineClass(
    'eglx.json', 'JsonLib',
{
	"constructor" : function() {
	}
});
egl.eglx.json.JsonLib["convertToJSON"] = function(/* recordOrDictionary */object) {
	this.validateJSONObject(object);
	return egl.eglx.json.toJSONString(object, true);
};
egl.eglx.json.JsonLib["validateJSONObject"] = function(/* recordOrDictionary */object) {
	if (object !== null && typeof object === "object" && "eze$$value" in object && "eze$$signature" in object) {
		object = object.eze$$value;
	}
	if (object !== null && typeof object != "object" || !("eze$$getFieldInfos" in object || object instanceof egl.eglx.lang.EDictionary)) {
		var field = object;
		if (object == undefined || object == null) {
			field = "undefined";
		}
		if (typeof object === "object" && "eze$$signature" in object) {
			field = egl.typeName(object.eze$$signature);
		}
		else{
			field = typeof object;
		}
		throw egl.createRuntimeException("CRRUI2107E", [ field ]);
	}
};
egl.eglx.json.toJSONString = function(object, depth, maxDepth, /*boolean*/ validateUnspportedTypes) {
	try {
		if (object === undefined || object === null) {
			return "null";
		}
		if (depth == undefined) {
			depth = 0;
			maxDepth = 13;
		}
		if (maxDepth && depth > maxDepth) {
			return '" Max Recursion '+maxDepth+' reached"';
		}
		if (typeof(object) == "string") {
			return '"' + egl.eglx.json.convertStringToJson(object) + '"';
		}
		if (typeof(object) == "number") {
			return String(object);
		}
		if (typeof(object) == "boolean") {
			return Boolean(object);
		}
		if (typeof(object) == "object" && object.eze$$signature) {
		   var sig = object.eze$$signature;
		   if (sig != null) {
		       var kind = sig.charAt(0) === '?' ? sig.charAt(1) : sig.charAt(0);
		   }
		   if (kind == 'K') {
		     return '"' + egl.eglx.lang.StringLib.format(object, "yyyy-MM-dd") + '"';
		   }
//unsupported 0.7		   else if (kind == 'L') {
//		     return '"' + egl.egl.core.$StrLib.formatTime(object, "HH:mm:ss") + '"';//function does not exist
//		   }
		   else if (kind == 'J') {
			   return '"' + egl.eglx.lang.StringLib.format( object, "yyyy-MM-dd HH:mm:ss") + '"' ;
		   }
		   	else if("eze$$value" in object){
		   		return egl.eglx.json.toJSONString(egl.unboxAny( object ));
			}
		}
		if (object instanceof egl.javascript.BigDecimal) {
			return object.toString();
		}
		if(typeof object === "object" && object instanceof egl.eglx.lang.Enumeration){
			return egl.eglx.json.toJSONString(object.value, depth+1, maxDepth);
		}
		if (typeof object === "object" && object instanceof Array) {
			try {
				var s = [];
				s.push("[");
				var needComma = false;
				for (var n=0; n<object.length; n++) {
					if (typeof(object[n]) != 'function') {
						if (needComma) s.push(",");
						s.push(egl.eglx.json.toJSONString(object[n], depth+1, maxDepth));
						needComma = true;
					}
				}
				s.push("]");
				return s.join('');
			}
			catch (e) {
			}
		}
		if (typeof object === "object" && "eze$$getFieldInfos" in object){
			var s = [];
			s.push("{");
			var needComma = false;
			var fieldInfos = object.eze$$getFieldInfos();
			if (fieldInfos) {
				for ( var idx = 0; idx < fieldInfos.length; idx++) {
					if (needComma) s.push(",");
					s.push('"');
					var key = fieldInfos[idx].annotations["JsonName"].name;
					s.push(key);
					s.push('":');
					var fieldValue;
					if (fieldInfos[idx].getterFunction instanceof Function) {
						fieldValue = object[fieldInfos[idx].getterFunction].apply();
					} else {
						fieldValue = object[fieldInfos[idx].getterFunction];
					}
					s.push(egl.eglx.json.toJSONString(egl.unboxAny( fieldValue ), depth+1, maxDepth));
					needComma = true;
				}
			}
			s.push("}");
			return s.join('');
		}
		if(typeof object === "object" && object instanceof egl.eglx.lang.EDictionary){
			var s = [];
			s.push("{");
			var needComma = false;
			for (f in object) {
				if (!f.match(/^eze\$\$/) && (typeof object[f] != "function")) {
					if (needComma) s.push(",");
					s.push('"');
					var key = f;
					if (object instanceof egl.egl.jsrt.Record) {
						key = object.eze$$getJSONName(key);
					}
					s.push(key);
					s.push('":');
					s.push(egl.eglx.json.toJSONString(egl.unboxAny( object[f] ), depth+1, maxDepth));
					needComma = true;
				}
			}
			s.push("}");
			return s.join('');
		}
		else{
			return '"Cannot toJSONString '+object;
		}
	}
	catch (e) {
		return '"Cannot toJSONString '+object+' due to '+e+'"';
	}
};
egl.eglx.json.convertStringToJson = function(value) {
	var str = value;
	str = str.replace(/\\/g, "\\\\");     // escape all \'s (e.g. \ -> \\)
	str = str.replace(/"/g, "\\\"");  	  // escape all "'s (e.g. " -> \")
	str = str.replace(/\//g, "\\/");	  // escape all /'s (e.g. / -> \/)
	str = str.replace(/[\b]/g, "\\b");	  // escape all <backspace> 
	str = str.replace(/\r/g, "\\r");	  // escape all <return>
	str = str.replace(/\t/g, "\\t");	  // escape all <tab>
	str = str.replace(/\f/g, "\\f");	  // escape all <formfeed>
	str = str.replace(/\n/g, "\\n");	  // escape all <newline>
	return str;	
};
egl.eglx.json.JsonLib["convertFromJSON"] = function( /* String */str, /* egl object */eglObject, /* boolean */cleanDictionary) {
	this.validateJSONObject(eglObject);
	if (!/^(\[|{)/.test(str)) {
		str = str.replace( /^(\s)*/, "" );
	}
	if (/^(\[|{)/.test(str)) {
		try {
			var jsonObject = egl.eglx.json.$JSONParser.parse(str);
			if (eglObject !== null && typeof eglObject === "object" && typeof jsonObject === "object") {
				this.populateObjectFromJsonObject(jsonObject, eglObject, undefined, cleanDictionary);
			} else {
				//throw exception;
			}
			return eglObject;
		} catch (e) {
			str = str.replace(/&/g, "&amp;").replace(/</g, "&lt;");
			throw egl.createRuntimeException("CRRUI2089E", [ str, e.message ]);
		}
	}
	return null;
};
egl.eglx.json.JsonLib["populateObjectFromJsonObject"] = function( /* Object */jsonObject, /* recordOrDictionary */eglObject, /*FieldInfo*/ fieldInfo, /* boolean */cleanDictionary) {
	if (jsonObject === undefined || jsonObject === null) {
		return null;
	}
	if (fieldInfo != undefined && fieldInfo != null) {
		var firstCharIdx = 0;
		var firstChar = fieldInfo.eglSignature.charAt(0);
		if (firstChar !== '?') {
			kind = firstChar;
		} else {
			kind = fieldInfo.eglSignature.charAt(1);
			firstCharIdx = 1;
		}
		switch (kind) {
			case 'S':
			case 's':
				return jsonObject;
			
			case 'K':
				return egl.stringToDate(jsonObject, "yyyy-MM-dd");
			
			case 'L':
				return egl.stringToTime(jsonObject, "HH:mm:ss");
			
			case 'J':
				return egl.stringToTimeStamp(jsonObject, "yyyy-MM-dd HH:mm:ss");
			
			case 'I':
				return egl.eglx.lang.EInt32.fromEString(jsonObject);
			
			case 'i':
				return egl.eglx.lang.EInt16.fromEInt32(jsonObject);
			
			case '0':
				return egl.eglx.lang.EBoolean.fromEBoolean(jsonObject);
			
			case 'F':
				return egl.eglx.lang.EFloat64.fromEDecimal(jsonObject);
			
			case 'f':
				return egl.eglx.lang.EFloat32.fromEDecimal(jsonObject);
			
			case 'B':
				return egl.eglx.lang.EInt64.fromEInt32(jsonObject);
			
			case 'N':
				var colon = fieldInfo.eglSignature.indexOf(':');
				return egl.eglx.lang.EDecimal.fromEString(jsonObject.toString(), 
						egl.convertStringToSmallint(fieldInfo.eglSignature.substring(colon + 1, fieldInfo.eglSignature.indexOf(';')), 
						egl.convertStringToSmallint(fieldInfo.eglSignature.substring(firstCharIdx + 1, colon))));
			
			case 'd':
				var colon = fieldInfo.eglSignature.indexOf(':');
				return egl.eglx.lang.EDecimal.fromEString(jsonObject.toString(), 
								egl.convertStringToSmallint(fieldInfo.eglSignature.substring(colon + 1, fieldInfo.eglSignature.indexOf(';')), 
								egl.convertStringToSmallint(fieldInfo.eglSignature.substring(firstCharIdx + 1, colon))));
			case '9':
				var colon = fieldInfo.eglSignature.indexOf(':');
				return egl.eglx.lang.EDecimal.fromEString(jsonObject.toString(),
							egl.convertStringToSmallint(fieldInfo.eglSignature.substring(colon + 1, fieldInfo.eglSignature.indexOf(';')), 
							egl.convertStringToSmallint(fieldInfo.eglSignature.substring(firstCharIdx + 1, colon))));
		}
	}
	if ((eglObject == undefined || eglObject == null) && fieldInfo != null) {
		eglObject = new fieldInfo.eglType();
	}
	if(eglObject !== null && typeof eglObject === "object" && eglObject instanceof egl.eglx.lang.Enumeration){
		return egl.eglx.services.$ServiceRT.convertToEnum(jsonObject, fieldInfo.eglType);
	}
	if(eglObject !== null && typeof eglObject === "object" && eglObject instanceof egl.eglx.lang.EInt64){
		return egl.eglx.lang.EInt64.fromEInt32(jsonObject);
	}
	if(eglObject !== null && typeof eglObject === "object" && eglObject instanceof egl.eglx.lang.EInt32){
		return egl.eglx.lang.EInt32.fromEString(jsonObject);
	}
	if(eglObject !== null && typeof eglObject === "object" && eglObject instanceof egl.eglx.lang.EInt16){
		return egl.eglx.lang.EInt16.fromEInt32(jsonObject);
	}
	if(eglObject !== null && typeof eglObject === "object" && eglObject instanceof egl.eglx.lang.EFloat64){
		return egl.eglx.lang.EFloat64.fromEDecimal(jsonObject);
	}
	if(eglObject !== null && typeof eglObject === "object" && eglObject instanceof egl.eglx.lang.EFloat32){
		return egl.eglx.lang.EFloat32.fromEDecimal(jsonObject);
	}
	if(eglObject !== null && typeof eglObject === "object" && eglObject instanceof egl.eglx.lang.EDecimal){
		return new egl.javascript.BigDecimal( jsonObject.toString() );
	}
	if(eglObject !== null && typeof eglObject === "object" && eglObject instanceof egl.eglx.lang.EBoolean){
		return egl.eglx.lang.EBoolean.fromEBoolean(jsonObject);
	}
	if(eglObject !== null && typeof eglObject === "object" && eglObject instanceof egl.eglx.lang.EDate){
		return egl.stringToDate(jsonObject, "yyyy-MM-dd");
	}
	if(eglObject !== null && typeof eglObject === "object" && eglObject instanceof egl.eglx.lang.ETimestamp){
		return egl.stringToTimeStamp(jsonObject, "yyyy-MM-dd HH:mm:ss");
	}
	if(eglObject !== null && typeof eglObject === "object" && eglObject instanceof egl.eglx.lang.EString){
		return jsonObject.toString();
	}
	if (typeof(jsonObject) == "string") {
		return jsonObject;
	}
	if ( typeof(jsonObject) == "number") {
		return jsonObject;
	}
	if (typeof(jsonObject) == "boolean") {
		return jsonObject;
	}
	if (typeof jsonObject === "object" && typeof fieldInfo === "object" && fieldInfo != null && fieldInfo.eglSignature.indexOf("[") > -1) {
		//for each array element create a new element
		var ary = new Array();
		for ( var idx = 0; idx < jsonObject.length; idx++) {
			var newFieldInfo = egl.clone(fieldInfo);
			newFieldInfo.eglSignature = newFieldInfo.eglSignature.slice(1);
			ary[idx] = this.populateObjectFromJsonObject(jsonObject[idx], null, newFieldInfo, cleanDictionary);
		}
		return ary;
	}
	if(eglObject !== null && typeof jsonObject === "object" && "eze$$value" in eglObject && "eze$$signature" in eglObject){
		this.populateObjectFromJsonObject(jsonObject, egl.unboxAny( eglObject ), undefined, cleanDictionary);
		return eglObject;
	}
	if (eglObject !== null && typeof jsonObject === "object" && "eze$$getFieldInfos" in eglObject){
		var fieldInfos = eglObject.eze$$getFieldInfos();
		if (fieldInfos) {
			for ( var idx = 0; idx < fieldInfos.length; idx++) {
				var key = fieldInfos[idx].annotations["JsonName"].name;
				var value = this.populateObjectFromJsonObject(jsonObject[key], null, fieldInfos[idx], cleanDictionary);
				if (fieldInfos[idx].setterFunction instanceof Function) {
					fieldInfos[idx].setterFunction.apply(value);
				} else {
					eglObject[fieldInfos[idx].setterFunction] = value;
				}
			}
		}
		return eglObject;
	}
	if(typeof jsonObject === "object" && eglObject === undefined || eglObject === null){
		eglObject = new egl.eglx.lang.EDictionary();
	}
	else if(eglObject !== undefined && eglObject !== null && 
			eglObject instanceof egl.eglx.lang.EDictionary &&
			(cleanDictionary === undefined || cleanDictionary === true)){
		eglObject.removeAll();
	}
	if (typeof jsonObject === "object" && jsonObject instanceof Array){
		//for each array element create a new element
		var ary = new Array();
		for ( var idx = 0; idx < jsonObject.length; idx++) {
			ary[idx] = this.populateObjectFromJsonObject(jsonObject[idx], null, null, cleanDictionary);
		}
		return ary;
	}
	else if(typeof jsonObject === "object"){
		for (f in jsonObject) {
			if(typeof jsonObject[f] !== "function"){
				eglObject[f] = this.populateObjectFromJsonObject(jsonObject[f], eglObject[f] === undefined ? null : eglObject[f], null, cleanDictionary);
			}
		}
		return eglObject;
	}
	else{
		return '"Cannot toJSONString '+jsonObject;
	}
};

