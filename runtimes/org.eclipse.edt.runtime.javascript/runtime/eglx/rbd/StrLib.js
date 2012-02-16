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
			var eze$Temp2;
			eze$Temp2 = egl.eglx.lang.EString.textLen(egl.eglx.lang.EString.clip(text));
			return eze$Temp2;
		}
		,
		"clip_1_S": function(source) {
			if ((egl.eglx.lang.NullType.equals(source, null))) {
				return null;
			}
			else {
				var eze$Temp4;
				eze$Temp4 = egl.eglx.lang.EString.clip(source);
				return eze$Temp4;
			}
		}
		,
		"clip_2_S_I": function(source, clipType) {
			if ((((egl.eglx.lang.NullType.equals(source, null)) || (egl.eglx.lang.NullType.equals(clipType, null))))) {
				return null;
			}
			else {
				{
					EzeLabel_eze_CaseLabel_0: if (((clipType == 0))) {
						var eze$Temp7;
						eze$Temp7 = egl.eglx.lang.EString.trim(source);
						return eze$Temp7;
					}
					else {
						if (((clipType == 1))) {
							var eze$Temp9;
							eze$Temp9 = egl.eglx.lang.EString.clipLeading(source);
							return eze$Temp9;
						}
						else {
							if (((clipType == 2))) {
								var eze$Temp11;
								eze$Temp11 = egl.eglx.lang.EString.clip(source);
								return eze$Temp11;
							}
							else {
								return source;
							}
						}
					}
				}
			}
		}
		,
		"formatDate": function(dateValue, format) {
			if ((((egl.eglx.lang.NullType.equals(dateValue, null)) || (egl.eglx.lang.NullType.equals(format, null))))) {
				return null;
			}
			else {
				var eze$Temp14;
				eze$Temp14 = egl.checkNull({eze$$value : dateValue, eze$$signature : egl.inferSignature(dateValue)});
				var eze$Temp15;
				eze$Temp15 = egl.checkNull(format);
				var eze$Temp13;
				eze$Temp13 = egl.eglx.lang.StringLib.format(eze$Temp14, eze$Temp15);
				return eze$Temp13;
			}
		}
		,
		"formatNumber_2_i_S": function(intValue, format) {
			if ((((egl.eglx.lang.NullType.equals(intValue, null)) || (egl.eglx.lang.NullType.equals(format, null))))) {
				return null;
			}
			else {
				var eze$Temp18;
				eze$Temp18 = egl.checkNull({eze$$value : intValue, eze$$signature : egl.inferSignature(intValue)});
				var eze$Temp19;
				eze$Temp19 = egl.checkNull(format);
				var eze$Temp17;
				eze$Temp17 = egl.eglx.lang.StringLib.format(eze$Temp18, eze$Temp19);
				return eze$Temp17;
			}
		}
		,
		"formatNumber_2_I_S": function(intValue, format) {
			if ((((egl.eglx.lang.NullType.equals(intValue, null)) || (egl.eglx.lang.NullType.equals(format, null))))) {
				return null;
			}
			else {
				var eze$Temp22;
				eze$Temp22 = egl.checkNull({eze$$value : intValue, eze$$signature : egl.inferSignature(intValue)});
				var eze$Temp23;
				eze$Temp23 = egl.checkNull(format);
				var eze$Temp21;
				eze$Temp21 = egl.eglx.lang.StringLib.format(eze$Temp22, eze$Temp23);
				return eze$Temp21;
			}
		}
		,
		"formatNumber_2_B_S": function(intValue, format) {
			if ((((egl.eglx.lang.NullType.equals(intValue, null)) || (egl.eglx.lang.NullType.equals(format, null))))) {
				return null;
			}
			else {
				var eze$Temp26;
				eze$Temp26 = egl.checkNull({eze$$value : intValue, eze$$signature : egl.inferSignature(intValue)});
				var eze$Temp27;
				eze$Temp27 = egl.checkNull(format);
				var eze$Temp25;
				eze$Temp25 = egl.eglx.lang.StringLib.format(eze$Temp26, eze$Temp27);
				return eze$Temp25;
			}
		}
		,
		"formatNumber_2_d_S": function(decValue, format) {
			if ((((egl.eglx.lang.NullType.equals(decValue, null)) || (egl.eglx.lang.NullType.equals(format, null))))) {
				return null;
			}
			else {
				var eze$Temp30;
				eze$Temp30 = egl.checkNull(format);
				var eze$Temp29;
				eze$Temp29 = egl.eglx.lang.StringLib.format({eze$$value : decValue, eze$$signature : egl.inferSignature(decValue)}, eze$Temp30);
				return eze$Temp29;
			}
		}
		,
		"formatNumber_2_f_S": function(floatValue, format) {
			if ((((egl.eglx.lang.NullType.equals(floatValue, null)) || (egl.eglx.lang.NullType.equals(format, null))))) {
				return null;
			}
			else {
				var eze$Temp33;
				eze$Temp33 = egl.checkNull({eze$$value : floatValue, eze$$signature : egl.inferSignature(floatValue)});
				var eze$Temp34;
				eze$Temp34 = egl.checkNull(format);
				var eze$Temp32;
				eze$Temp32 = egl.eglx.lang.StringLib.format(eze$Temp33, eze$Temp34);
				return eze$Temp32;
			}
		}
		,
		"formatNumber_2_F_S": function(floatValue, format) {
			if ((((egl.eglx.lang.NullType.equals(floatValue, null)) || (egl.eglx.lang.NullType.equals(format, null))))) {
				return null;
			}
			else {
				var eze$Temp37;
				eze$Temp37 = egl.checkNull({eze$$value : floatValue, eze$$signature : egl.inferSignature(floatValue)});
				var eze$Temp38;
				eze$Temp38 = egl.checkNull(format);
				var eze$Temp36;
				eze$Temp36 = egl.eglx.lang.StringLib.format(eze$Temp37, eze$Temp38);
				return eze$Temp36;
			}
		}
		,
		"formatTimeStamp": function(timestampvalue, format) {
			if ((((egl.eglx.lang.NullType.equals(timestampvalue, null)) || (egl.eglx.lang.NullType.equals(format, null))))) {
				return null;
			}
			else {
				var eze$Temp41;
				eze$Temp41 = egl.checkNull(format);
				var eze$Temp40;
				eze$Temp40 = egl.eglx.lang.StringLib.format({eze$$value : timestampvalue, eze$$signature : egl.inferSignature(timestampvalue)}, eze$Temp41);
				return eze$Temp40;
			}
		}
		,
		"getNextToken": function(source, index, delimiters) {
			var charIndex;
			charIndex = (function(x){ return x == null ? x : egl.eglx.lang.EInt32.fromEDecimal.apply( this, arguments );})((((new egl.javascript.BigDecimal(egl.divide(egl.eglx.lang.EAny.unbox(index),2))).add((function(x){ return x == null ? x : egl.eglx.lang.EDecimal.fromEInt16.apply( this, arguments );})(1, egl.javascript.BigDecimal.prototype.NINES[3])))),"d;");
			var eze$Temp42;
			eze$Temp42 = egl.eglx.lang.EAny.ezeWrap(charIndex);
			var token;
			token = egl.eglx.lang.StringLib.getNextToken(source, eze$Temp42, delimiters);
			charIndex = eze$Temp42.ezeUnbox();
			index.ezeCopy(((((((charIndex - 1)) * 2)) + 1)));
			return token;
		}
		,
		"getTokenCount": function(source, delimiters) {
			var eze$Temp43;
			eze$Temp43 = egl.eglx.lang.StringLib.getTokenCount(source, delimiters);
			return eze$Temp43;
		}
		,
		"indexOf_2_S_S": function(source, pattern) {
			var eze$Temp44;
			eze$Temp44 = egl.eglx.lang.EString.indexOf(source, pattern);
			return eze$Temp44;
		}
		,
		"indexOf_3_S_S_I": function(source, pattern, startIndex) {
			var eze$Temp45;
			eze$Temp45 = egl.eglx.lang.EString.indexOf(source, pattern, startIndex);
			return eze$Temp45;
		}
		,
		"lowerCase": function(characterItem) {
			if ((egl.eglx.lang.NullType.equals(characterItem, null))) {
				return null;
			}
			else {
				var eze$Temp47;
				eze$Temp47 = egl.eglx.lang.EString.toLowerCase(characterItem);
				return eze$Temp47;
			}
		}
		,
		"spaces": function(characterCount) {
			if ((egl.eglx.lang.NullType.equals(characterCount, null))) {
				return null;
			}
			else {
				var result = "";
				var fiftyBlanks;
				fiftyBlanks = "                                                  ";
				while (((characterCount >= 50))) {
					result = ((result) + fiftyBlanks);
					characterCount = ((characterCount - 50));
				}
				if (((characterCount > 0))) {
					result = ((result) + egl.eglx.lang.EString.substring(fiftyBlanks, 1, characterCount));
				}
				return result;
			}
		}
		,
		"upperCase": function(characterItem) {
			if ((egl.eglx.lang.NullType.equals(characterItem, null))) {
				return null;
			}
			else {
				var eze$Temp52;
				eze$Temp52 = egl.eglx.lang.EString.toUpperCase(characterItem);
				return eze$Temp52;
			}
		}
		,
		"toString": function() {
			return "[StrLib]";
		}
	}
);
