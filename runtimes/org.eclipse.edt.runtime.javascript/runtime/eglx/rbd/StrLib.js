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
egl.defineRUILibrary('eglx.rbd', 'StrLib',
{
	'eze$$fileName': 'eglx/rbd/StrLib.egl',
	'eze$$runtimePropertiesFile': 'eglx.rbd.StrLib',
		"constructor": function() {
			if(egl.eglx.rbd.StrLib['$inst']) return egl.eglx.rbd.StrLib['$inst'];
			egl.eglx.rbd.StrLib['$inst']=this;
			this.eze$$setInitial();
		}
		,
		"eze$$setEmpty": function() {
			this.defaultDateFormat = "";
			this.defaultTimeFormat = "";
			this.defaultTimeStampFormat = "";
			this.defaultMoneyFormat = "";
			this.defaultNumericFormat = "";
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("StrLib", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("defaultDateFormat", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("defaultDateFormat");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("defaultDateFormat", "defaultDateFormat", "S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("defaultTimeFormat", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("defaultTimeFormat");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("defaultTimeFormat", "defaultTimeFormat", "S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("defaultTimeStampFormat", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("defaultTimeStampFormat");
				this.fieldInfos[2] =new egl.eglx.services.FieldInfo("defaultTimeStampFormat", "defaultTimeStampFormat", "S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("defaultMoneyFormat", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("defaultMoneyFormat");
				this.fieldInfos[3] =new egl.eglx.services.FieldInfo("defaultMoneyFormat", "defaultMoneyFormat", "S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("defaultNumericFormat", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("defaultNumericFormat");
				this.fieldInfos[4] =new egl.eglx.services.FieldInfo("defaultNumericFormat", "defaultNumericFormat", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"booleanAsString": function(value) {
			if (value) {
				return "true";
			}
			else {
				return "false";
			}
		}
		,
		"characterLen": function(text) {
			var eze$Temp2 = 0;
			eze$Temp2 = egl.eglx.lang.EString.textLen(egl.eglx.lang.EString.trim(text));
			return eze$Temp2;
		}
		,
		"clip": function(source) {
			if ((egl.eglx.lang.NullType.equals({eze$$value : source, eze$$signature : "S;"}, null))) {
				return null;
			}
			else {
				var eze$Temp4 = "";
				eze$Temp4 = egl.eglx.lang.EString.clip(source);
				return eze$Temp4;
			}
		}
		,
		"clip": function(source, clipType) {
			if ((((egl.eglx.lang.NullType.equals({eze$$value : source, eze$$signature : "S;"}, null)) || (egl.eglx.lang.NullType.equals({eze$$value : clipType, eze$$signature : "I;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp6 = "";
				eze$Temp6 = egl.eglx.lang.EString.trim(source);
				if (((clipType == 0))) {
					return eze$Temp6;
				}
				else {
					var eze$Temp8 = "";
					eze$Temp8 = egl.eglx.lang.EString.clipLeading(source);
					if (((clipType == 1))) {
						return eze$Temp8;
					}
					else {
						var eze$Temp10 = "";
						eze$Temp10 = egl.eglx.lang.EString.clip(source);
						if (((clipType == 2))) {
							return eze$Temp10;
						}
						else {
							return source;
						}
					}
				}
			}
		}
		,
		"formatDate": function(dateValue) {
			if ((egl.eglx.lang.NullType.equals({eze$$value : dateValue, eze$$signature : "K;"}, null))) {
				return null;
			}
			else {
				var eze$Temp14 = egl.eglx.lang.DateTimeLib['$inst'].currentDate();
				eze$Temp14 = (function(x){ return x != null ? (x) : egl.eglx.lang.DateTimeLib['$inst'].currentDate(); })(dateValue);
				var eze$Temp13 = "";
				eze$Temp13 = egl.eglx.lang.StringLib.format(eze$Temp14, this.defaultDateFormat);
				return eze$Temp13;
			}
		}
		,
		"formatDate": function(dateValue, format) {
			if ((((egl.eglx.lang.NullType.equals({eze$$value : dateValue, eze$$signature : "K;"}, null)) || (egl.eglx.lang.NullType.equals({eze$$value : format, eze$$signature : "S;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp17 = egl.eglx.lang.DateTimeLib['$inst'].currentDate();
				eze$Temp17 = (function(x){ return x != null ? (x) : egl.eglx.lang.DateTimeLib['$inst'].currentDate(); })(dateValue);
				var eze$Temp18 = "";
				eze$Temp18 = (function(x){ return x != null ? (x) : ""; })(format);
				var eze$Temp16 = "";
				eze$Temp16 = egl.eglx.lang.StringLib.format(eze$Temp17, eze$Temp18);
				return eze$Temp16;
			}
		}
		,
		"formatNumber": function(intValue) {
			if ((egl.eglx.lang.NullType.equals({eze$$value : intValue, eze$$signature : "i;"}, null))) {
				return null;
			}
			else {
				var eze$Temp21 = 0;
				eze$Temp21 = (function(x){ return x != null ? (x) : 0; })(intValue);
				var eze$Temp20 = "";
				eze$Temp20 = egl.eglx.lang.StringLib.format(eze$Temp21, this.defaultNumericFormat);
				return eze$Temp20;
			}
		}
		,
		"formatNumber": function(intValue, format) {
			if ((((egl.eglx.lang.NullType.equals({eze$$value : intValue, eze$$signature : "i;"}, null)) || (egl.eglx.lang.NullType.equals({eze$$value : format, eze$$signature : "S;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp24 = 0;
				eze$Temp24 = (function(x){ return x != null ? (x) : 0; })(intValue);
				var eze$Temp25 = "";
				eze$Temp25 = (function(x){ return x != null ? (x) : ""; })(format);
				var eze$Temp23 = "";
				eze$Temp23 = egl.eglx.lang.StringLib.format(eze$Temp24, eze$Temp25);
				return eze$Temp23;
			}
		}
		,
		"formatNumber": function(intValue) {
			if ((egl.eglx.lang.NullType.equals({eze$$value : intValue, eze$$signature : "I;"}, null))) {
				return null;
			}
			else {
				var eze$Temp28 = 0;
				eze$Temp28 = (function(x){ return x != null ? (x) : 0; })(intValue);
				var eze$Temp27 = "";
				eze$Temp27 = egl.eglx.lang.StringLib.format(eze$Temp28, this.defaultNumericFormat);
				return eze$Temp27;
			}
		}
		,
		"formatNumber": function(intValue, format) {
			if ((((egl.eglx.lang.NullType.equals({eze$$value : intValue, eze$$signature : "I;"}, null)) || (egl.eglx.lang.NullType.equals({eze$$value : format, eze$$signature : "S;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp31 = 0;
				eze$Temp31 = (function(x){ return x != null ? (x) : 0; })(intValue);
				var eze$Temp32 = "";
				eze$Temp32 = (function(x){ return x != null ? (x) : ""; })(format);
				var eze$Temp30 = "";
				eze$Temp30 = egl.eglx.lang.StringLib.format(eze$Temp31, eze$Temp32);
				return eze$Temp30;
			}
		}
		,
		"formatNumber": function(intValue) {
			if ((egl.eglx.lang.NullType.equals({eze$$value : intValue, eze$$signature : "B;"}, null))) {
				return null;
			}
			else {
				var eze$Temp35 = egl.eglx.lang.EInt64.ZERO;
				eze$Temp35 = (function(x){ return x != null ? (x) : egl.eglx.lang.EInt64.ZERO; })(intValue);
				var eze$Temp34 = "";
				eze$Temp34 = egl.eglx.lang.StringLib.format(eze$Temp35, this.defaultNumericFormat);
				return eze$Temp34;
			}
		}
		,
		"formatNumber": function(intValue, format) {
			if ((((egl.eglx.lang.NullType.equals({eze$$value : intValue, eze$$signature : "B;"}, null)) || (egl.eglx.lang.NullType.equals({eze$$value : format, eze$$signature : "S;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp38 = egl.eglx.lang.EInt64.ZERO;
				eze$Temp38 = (function(x){ return x != null ? (x) : egl.eglx.lang.EInt64.ZERO; })(intValue);
				var eze$Temp39 = "";
				eze$Temp39 = (function(x){ return x != null ? (x) : ""; })(format);
				var eze$Temp37 = "";
				eze$Temp37 = egl.eglx.lang.StringLib.format(eze$Temp38, eze$Temp39);
				return eze$Temp37;
			}
		}
		,
		"formatNumber": function(decValue) {
			if ((egl.eglx.lang.NullType.equals({eze$$value : decValue, eze$$signature : "d;"}, null))) {
				return null;
			}
			else {
				var eze$Temp41 = "";
				eze$Temp41 = egl.eglx.lang.StringLib.format(decValue, this.defaultNumericFormat);
				return eze$Temp41;
			}
		}
		,
		"formatNumber": function(decValue, format) {
			if ((((egl.eglx.lang.NullType.equals({eze$$value : decValue, eze$$signature : "d;"}, null)) || (egl.eglx.lang.NullType.equals({eze$$value : format, eze$$signature : "S;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp44 = "";
				eze$Temp44 = (function(x){ return x != null ? (x) : ""; })(format);
				var eze$Temp43 = "";
				eze$Temp43 = egl.eglx.lang.StringLib.format(decValue, eze$Temp44);
				return eze$Temp43;
			}
		}
		,
		"formatNumber": function(floatValue) {
			if ((egl.eglx.lang.NullType.equals({eze$$value : floatValue, eze$$signature : "f;"}, null))) {
				return null;
			}
			else {
				var eze$Temp47 = 0;
				eze$Temp47 = (function(x){ return x != null ? (x) : 0; })(floatValue);
				var eze$Temp46 = "";
				eze$Temp46 = egl.eglx.lang.StringLib.format(eze$Temp47, this.defaultNumericFormat);
				return eze$Temp46;
			}
		}
		,
		"formatNumber": function(floatValue, format) {
			if ((((egl.eglx.lang.NullType.equals({eze$$value : floatValue, eze$$signature : "f;"}, null)) || (egl.eglx.lang.NullType.equals({eze$$value : format, eze$$signature : "S;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp50 = 0;
				eze$Temp50 = (function(x){ return x != null ? (x) : 0; })(floatValue);
				var eze$Temp51 = "";
				eze$Temp51 = (function(x){ return x != null ? (x) : ""; })(format);
				var eze$Temp49 = "";
				eze$Temp49 = egl.eglx.lang.StringLib.format(eze$Temp50, eze$Temp51);
				return eze$Temp49;
			}
		}
		,
		"formatNumber": function(floatValue) {
			if ((egl.eglx.lang.NullType.equals({eze$$value : floatValue, eze$$signature : "F;"}, null))) {
				return null;
			}
			else {
				var eze$Temp54 = 0;
				eze$Temp54 = (function(x){ return x != null ? (x) : 0; })(floatValue);
				var eze$Temp53 = "";
				eze$Temp53 = egl.eglx.lang.StringLib.format(eze$Temp54, this.defaultNumericFormat);
				return eze$Temp53;
			}
		}
		,
		"formatNumber": function(floatValue, format) {
			if ((((egl.eglx.lang.NullType.equals({eze$$value : floatValue, eze$$signature : "F;"}, null)) || (egl.eglx.lang.NullType.equals({eze$$value : format, eze$$signature : "S;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp57 = 0;
				eze$Temp57 = (function(x){ return x != null ? (x) : 0; })(floatValue);
				var eze$Temp58 = "";
				eze$Temp58 = (function(x){ return x != null ? (x) : ""; })(format);
				var eze$Temp56 = "";
				eze$Temp56 = egl.eglx.lang.StringLib.format(eze$Temp57, eze$Temp58);
				return eze$Temp56;
			}
		}
		,
		"formatTimeStamp": function(timestampvalue) {
			if ((egl.eglx.lang.NullType.equals({eze$$value : timestampvalue, eze$$signature : "J'yyyyMMddHHmmssffffff';"}, null))) {
				return null;
			}
			else {
				var eze$Temp61 = egl.eglx.lang.ETimestamp.currentTimeStamp("yyyyMMddhhmmss");
				eze$Temp61 = (function(x){ return x != null ? (x) : egl.eglx.lang.ETimestamp.currentTimeStamp(); })(egl.eglx.lang.ETimestamp.ezeCast({eze$$value : timestampvalue, eze$$signature : "J'yyyyMMddHHmmssffffff';"},"yyyyMMddhhmmss"));
				var eze$Temp60 = "";
				eze$Temp60 = egl.eglx.lang.StringLib.format(eze$Temp61, this.defaultTimeStampFormat);
				return eze$Temp60;
			}
		}
		,
		"formatTimeStamp": function(timestampvalue, format) {
			if ((((egl.eglx.lang.NullType.equals({eze$$value : timestampvalue, eze$$signature : "J'yyyyMMddHHmmssffffff';"}, null)) || (egl.eglx.lang.NullType.equals({eze$$value : format, eze$$signature : "S;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp64 = egl.eglx.lang.ETimestamp.currentTimeStamp("yyyyMMddhhmmss");
				eze$Temp64 = (function(x){ return x != null ? (x) : egl.eglx.lang.ETimestamp.currentTimeStamp(); })(egl.eglx.lang.ETimestamp.ezeCast({eze$$value : timestampvalue, eze$$signature : "J'yyyyMMddHHmmssffffff';"},"yyyyMMddhhmmss"));
				var eze$Temp65 = "";
				eze$Temp65 = (function(x){ return x != null ? (x) : ""; })(format);
				var eze$Temp63 = "";
				eze$Temp63 = egl.eglx.lang.StringLib.format(eze$Temp64, eze$Temp65);
				return eze$Temp63;
			}
		}
		,
		"getNextToken": function(source, index, delimiters) {
			var charIndex = 0;
			charIndex = egl.eglx.lang.EInt32.fromEDecimal((((new egl.javascript.BigDecimal(egl.divide(egl.eglx.lang.EAny.unbox(index),2))).add(egl.eglx.lang.EDecimal.fromEInt16({eze$$value : 1, eze$$signature : "i;"}, egl.javascript.BigDecimal.prototype.NINES[8])))));
			var token = null;
			var eze$Temp66 = null;
			eze$Temp66 = egl.eglx.lang.EAny.ezeWrap(charIndex);
			token = egl.eglx.lang.StringLib.getNextToken(egl.eglx.lang.EAny.unbox(source), eze$Temp66, delimiters);
			charIndex = eze$Temp66.ezeUnbox();
			if ((egl.eglx.lang.NullType.notEquals({eze$$value : token, eze$$signature : "S;"}, null))) {
				index.ezeCopy(((((((charIndex - 1)) * 2)) + 1)));
			}
			return token;
		}
		,
		"getTokenCount": function(source, delimiters) {
			var eze$Temp68 = 0;
			eze$Temp68 = egl.eglx.lang.StringLib.getTokenCount(source, delimiters);
			return eze$Temp68;
		}
		,
		"indexOf": function(source, pattern) {
			var eze$Temp69 = 0;
			eze$Temp69 = egl.eglx.lang.EString.indexOf(egl.eglx.lang.EAny.unbox(source), pattern);
			return eze$Temp69;
		}
		,
		"indexOf": function(source, pattern, startIndex) {
			var eze$Temp70 = 0;
			eze$Temp70 = egl.eglx.lang.EString.indexOf(egl.eglx.lang.EAny.unbox(source), pattern, startIndex);
			return eze$Temp70;
		}
		,
		"lowerCase": function(characterItem) {
			if ((egl.eglx.lang.NullType.equals({eze$$value : characterItem, eze$$signature : "S;"}, null))) {
				return null;
			}
			else {
				var eze$Temp72 = "";
				eze$Temp72 = egl.eglx.lang.EString.toLowerCase(characterItem);
				return eze$Temp72;
			}
		}
		,
		"spaces": function(characterCount) {
			if ((egl.eglx.lang.NullType.equals({eze$$value : characterCount, eze$$signature : "I;"}, null))) {
				return null;
			}
			else {
				var result = "";
				var fiftyBlanks = "";
				fiftyBlanks = "                                                  ";
				while (((characterCount >= 50))) {
					result += fiftyBlanks;
					characterCount = ((characterCount - 50));
				}
				if (((characterCount > 0))) {
					result += egl.eglx.lang.EString.substring(fiftyBlanks, 1, characterCount);
				}
				return result;
			}
		}
		,
		"upperCase": function(characterItem) {
			if ((egl.eglx.lang.NullType.equals({eze$$value : characterItem, eze$$signature : "S;"}, null))) {
				return null;
			}
			else {
				var eze$Temp77 = "";
				eze$Temp77 = egl.eglx.lang.EString.toUpperCase(characterItem);
				return eze$Temp77;
			}
		}
		,
		"getDefaultDateFormat": function() {
			return defaultDateFormat;
		}
		,
		"setDefaultDateFormat": function(ezeValue) {
			this.defaultDateFormat = ezeValue;
		}
		,
		"getDefaultTimeFormat": function() {
			return defaultTimeFormat;
		}
		,
		"setDefaultTimeFormat": function(ezeValue) {
			this.defaultTimeFormat = ezeValue;
		}
		,
		"getDefaultTimeStampFormat": function() {
			return defaultTimeStampFormat;
		}
		,
		"setDefaultTimeStampFormat": function(ezeValue) {
			this.defaultTimeStampFormat = ezeValue;
		}
		,
		"getDefaultMoneyFormat": function() {
			return defaultMoneyFormat;
		}
		,
		"setDefaultMoneyFormat": function(ezeValue) {
			this.defaultMoneyFormat = ezeValue;
		}
		,
		"getDefaultNumericFormat": function() {
			return defaultNumericFormat;
		}
		,
		"setDefaultNumericFormat": function(ezeValue) {
			this.defaultNumericFormat = ezeValue;
		}
		,
		"toString": function() {
			return "[StrLib]";
		}
	}
);
