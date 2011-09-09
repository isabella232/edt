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
			this.jsrt$SysVar = new egl.egl.core.SysVar();
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
			eze$Temp2 = egl.egl.lang.EString.textLen(egl.egl.lang.EString.trim(text));
			return eze$Temp2;
		}
		,
		"clip": function(source) {
			if ((egl.egl.lang.NullType.equals({eze$$value : source, eze$$signature : "?S;"}, null))) {
				return null;
			}
			else {
				var eze$Temp4 = "";
				eze$Temp4 = egl.egl.lang.EString.clip(source);
				return eze$Temp4;
			}
		}
		,
		"clip": function(source, clipType) {
			if ((((egl.egl.lang.NullType.equals({eze$$value : source, eze$$signature : "?S;"}, null)) || (egl.egl.lang.NullType.equals({eze$$value : clipType, eze$$signature : "?I;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp6 = "";
				eze$Temp6 = egl.egl.lang.EString.trim(source);
				if (((clipType == 0))) {
					return eze$Temp6;
				}
				else {
					var eze$Temp8 = "";
					eze$Temp8 = egl.egl.lang.EString.clipLeading(source);
					if (((clipType == 1))) {
						return eze$Temp8;
					}
					else {
						var eze$Temp10 = "";
						eze$Temp10 = egl.egl.lang.EString.clip(source);
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
			if ((egl.egl.lang.NullType.equals({eze$$value : dateValue, eze$$signature : "?K;"}, null))) {
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
			if ((((egl.egl.lang.NullType.equals({eze$$value : dateValue, eze$$signature : "?K;"}, null)) || (egl.egl.lang.NullType.equals({eze$$value : format, eze$$signature : "?S;"}, null))))) {
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
			if ((egl.egl.lang.NullType.equals({eze$$value : intValue, eze$$signature : "?i;"}, null))) {
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
			if ((((egl.egl.lang.NullType.equals({eze$$value : intValue, eze$$signature : "?i;"}, null)) || (egl.egl.lang.NullType.equals({eze$$value : format, eze$$signature : "?S;"}, null))))) {
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
			if ((egl.egl.lang.NullType.equals({eze$$value : intValue, eze$$signature : "?I;"}, null))) {
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
			if ((((egl.egl.lang.NullType.equals({eze$$value : intValue, eze$$signature : "?I;"}, null)) || (egl.egl.lang.NullType.equals({eze$$value : format, eze$$signature : "?S;"}, null))))) {
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
			if ((egl.egl.lang.NullType.equals({eze$$value : intValue, eze$$signature : "?B;"}, null))) {
				return null;
			}
			else {
				var eze$Temp35 = egl.egl.lang.EInt64.ZERO;
				eze$Temp35 = (function(x){ return x != null ? (x) : egl.egl.lang.EInt64.ZERO; })(intValue);
				var eze$Temp34 = "";
				eze$Temp34 = egl.eglx.lang.StringLib.format(eze$Temp35, this.defaultNumericFormat);
				return eze$Temp34;
			}
		}
		,
		"formatNumber": function(intValue, format) {
			if ((((egl.egl.lang.NullType.equals({eze$$value : intValue, eze$$signature : "?B;"}, null)) || (egl.egl.lang.NullType.equals({eze$$value : format, eze$$signature : "?S;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp38 = egl.egl.lang.EInt64.ZERO;
				eze$Temp38 = (function(x){ return x != null ? (x) : egl.egl.lang.EInt64.ZERO; })(intValue);
				var eze$Temp39 = "";
				eze$Temp39 = (function(x){ return x != null ? (x) : ""; })(format);
				var eze$Temp37 = "";
				eze$Temp37 = egl.eglx.lang.StringLib.format(eze$Temp38, eze$Temp39);
				return eze$Temp37;
			}
		}
		,
		"formatNumber": function(decValue) {
			if ((egl.egl.lang.NullType.equals({eze$$value : decValue, eze$$signature : "?d;"}, null))) {
				return null;
			}
			else {
				var eze$Temp42 = egl.egl.lang.EDecimal.ZERO;
				eze$Temp42 = (function(x){ return x != null ? (x) : egl.egl.lang.EDecimal.ZERO; })(decValue);
				var eze$Temp41 = "";
				eze$Temp41 = egl.eglx.lang.StringLib.format(eze$Temp42, this.defaultNumericFormat);
				return eze$Temp41;
			}
		}
		,
		"formatNumber": function(decValue, format) {
			if ((((egl.egl.lang.NullType.equals({eze$$value : decValue, eze$$signature : "?d;"}, null)) || (egl.egl.lang.NullType.equals({eze$$value : format, eze$$signature : "?S;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp45 = egl.egl.lang.EDecimal.ZERO;
				eze$Temp45 = (function(x){ return x != null ? (x) : egl.egl.lang.EDecimal.ZERO; })(decValue);
				var eze$Temp46 = "";
				eze$Temp46 = (function(x){ return x != null ? (x) : ""; })(format);
				var eze$Temp44 = "";
				eze$Temp44 = egl.eglx.lang.StringLib.format(eze$Temp45, eze$Temp46);
				return eze$Temp44;
			}
		}
		,
		"formatNumber": function(floatValue) {
			if ((egl.egl.lang.NullType.equals({eze$$value : floatValue, eze$$signature : "?f;"}, null))) {
				return null;
			}
			else {
				var eze$Temp49 = 0;
				eze$Temp49 = (function(x){ return x != null ? (x) : 0; })(floatValue);
				var eze$Temp48 = "";
				eze$Temp48 = egl.eglx.lang.StringLib.format(eze$Temp49, this.defaultNumericFormat);
				return eze$Temp48;
			}
		}
		,
		"formatNumber": function(floatValue, format) {
			if ((((egl.egl.lang.NullType.equals({eze$$value : floatValue, eze$$signature : "?f;"}, null)) || (egl.egl.lang.NullType.equals({eze$$value : format, eze$$signature : "?S;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp52 = 0;
				eze$Temp52 = (function(x){ return x != null ? (x) : 0; })(floatValue);
				var eze$Temp53 = "";
				eze$Temp53 = (function(x){ return x != null ? (x) : ""; })(format);
				var eze$Temp51 = "";
				eze$Temp51 = egl.eglx.lang.StringLib.format(eze$Temp52, eze$Temp53);
				return eze$Temp51;
			}
		}
		,
		"formatNumber": function(floatValue) {
			if ((egl.egl.lang.NullType.equals({eze$$value : floatValue, eze$$signature : "?F;"}, null))) {
				return null;
			}
			else {
				var eze$Temp56 = 0;
				eze$Temp56 = (function(x){ return x != null ? (x) : 0; })(floatValue);
				var eze$Temp55 = "";
				eze$Temp55 = egl.eglx.lang.StringLib.format(eze$Temp56, this.defaultNumericFormat);
				return eze$Temp55;
			}
		}
		,
		"formatNumber": function(floatValue, format) {
			if ((((egl.egl.lang.NullType.equals({eze$$value : floatValue, eze$$signature : "?F;"}, null)) || (egl.egl.lang.NullType.equals({eze$$value : format, eze$$signature : "?S;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp59 = 0;
				eze$Temp59 = (function(x){ return x != null ? (x) : 0; })(floatValue);
				var eze$Temp60 = "";
				eze$Temp60 = (function(x){ return x != null ? (x) : ""; })(format);
				var eze$Temp58 = "";
				eze$Temp58 = egl.eglx.lang.StringLib.format(eze$Temp59, eze$Temp60);
				return eze$Temp58;
			}
		}
		,
		"formatTimeStamp": function(timestampvalue) {
			if ((egl.egl.lang.NullType.equals({eze$$value : timestampvalue, eze$$signature : "?J'yyyyMMddHHmmssffffff';"}, null))) {
				return null;
			}
			else {
				var eze$Temp63 = egl.egl.lang.ETimestamp.currentTimeStamp("yyyyMMddhhmmss");
				eze$Temp63 = (function(x){ return x != null ? (x) : egl.egl.lang.ETimestamp.currentTimeStamp(); })(egl.egl.lang.ETimestamp.fromETimestamp(timestampvalue,"yyyyMMddhhmmss"));
				var eze$Temp62 = "";
				eze$Temp62 = egl.eglx.lang.StringLib.format(eze$Temp63, this.defaultTimeStampFormat);
				return eze$Temp62;
			}
		}
		,
		"formatTimeStamp": function(timestampvalue, format) {
			if ((((egl.egl.lang.NullType.equals({eze$$value : timestampvalue, eze$$signature : "?J'yyyyMMddHHmmssffffff';"}, null)) || (egl.egl.lang.NullType.equals({eze$$value : format, eze$$signature : "?S;"}, null))))) {
				return null;
			}
			else {
				var eze$Temp66 = egl.egl.lang.ETimestamp.currentTimeStamp("yyyyMMddhhmmss");
				eze$Temp66 = (function(x){ return x != null ? (x) : egl.egl.lang.ETimestamp.currentTimeStamp(); })(egl.egl.lang.ETimestamp.fromETimestamp(timestampvalue,"yyyyMMddhhmmss"));
				var eze$Temp67 = "";
				eze$Temp67 = (function(x){ return x != null ? (x) : ""; })(format);
				var eze$Temp65 = "";
				eze$Temp65 = egl.eglx.lang.StringLib.format(eze$Temp66, eze$Temp67);
				return eze$Temp65;
			}
		}
		,
		"getNextToken": function(source, index, delimiters) {
			var charIndex = 0;
			charIndex = egl.egl.lang.EInt32.fromEDecimal(((((index.ezeUnbox() / 2)).add(egl.egl.lang.EDecimal.fromEInt16(1, egl.javascript.BigDecimal.prototype.NINES[3])))));
			var token = null;
			var eze$Temp68 = null;
			eze$Temp68 = egl.egl.lang.EglAny.ezeWrap(charIndex);
			token = egl.eglx.lang.StringLib.getNextToken(source.ezeUnbox(), eze$Temp68, delimiters);
			charIndex = eze$Temp68.ezeUnbox();
			if ((egl.egl.lang.NullType.notEquals({eze$$value : token, eze$$signature : "?S;"}, null))) {
				index.ezeCopy(((((((charIndex - 1)) * 2)) + 1)));
			}
			return token;
		}
		,
		"getTokenCount": function(source, delimiters) {
			var eze$Temp70 = 0;
			eze$Temp70 = egl.eglx.lang.StringLib.getTokenCount(source, delimiters);
			return eze$Temp70;
		}
		,
		"indexOf": function(source, pattern) {
			var eze$Temp71 = 0;
			eze$Temp71 = egl.egl.lang.EString.indexOf(source.ezeUnbox(), pattern);
			return eze$Temp71;
		}
		,
		"indexOf": function(source, pattern, startIndex) {
			var eze$Temp72 = 0;
			eze$Temp72 = egl.egl.lang.EString.indexOf(source.ezeUnbox(), pattern, startIndex);
			return eze$Temp72;
		}
		,
		"lowerCase": function(characterItem) {
			if ((egl.egl.lang.NullType.equals({eze$$value : characterItem, eze$$signature : "?S;"}, null))) {
				return null;
			}
			else {
				var eze$Temp74 = "";
				eze$Temp74 = egl.egl.lang.EString.toLowerCase(characterItem);
				return eze$Temp74;
			}
		}
		,
		"spaces": function(characterCount) {
			if ((egl.egl.lang.NullType.equals({eze$$value : characterCount, eze$$signature : "?I;"}, null))) {
				return null;
			}
			else {
				var result = "";
				var fiftyBlanks = "";
				fiftyBlanks = "                                                  ";
				while (((characterCount >= 50))) {
					result += fiftyBlanks;
					characterCount -= 50;
				}
				if (((characterCount > 0))) {
					result += egl.egl.lang.EString.substring(fiftyBlanks, 1, characterCount);
				}
				return result;
			}
		}
		,
		"upperCase": function(characterItem) {
			if ((egl.egl.lang.NullType.equals({eze$$value : characterItem, eze$$signature : "?S;"}, null))) {
				return null;
			}
			else {
				var eze$Temp79 = "";
				eze$Temp79 = egl.egl.lang.EString.toUpperCase(characterItem);
				return eze$Temp79;
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
