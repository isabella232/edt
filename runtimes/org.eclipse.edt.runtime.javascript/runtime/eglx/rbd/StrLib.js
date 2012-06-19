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
	egl.defineRUILibrary('eglx.rbd', 'StrLib',
	{
		'eze$$fileName': 'eglx/rbd/StrLib.egl',
		'eze$$runtimePropertiesFile': 'eglx.rbd.StrLib',
			"constructor": function() {
				if(egl.eglx.rbd.StrLib['$inst']) return egl.eglx.rbd.StrLib['$inst'];
				egl.eglx.rbd.StrLib['$inst']=this;
			}
			,
			"eze$$setEmpty": function() {
			}
			,
			"eze$$setInitial": function() {
				if(egl.eglx.rbd.StrLib['$inst']) return egl.eglx.rbd.StrLib['$inst'];
				egl.eglx.rbd.StrLib['$inst']=this;
				try { egl.enter("<init>",this,arguments);
					this.eze$$setEmpty();
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
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
				try { egl.enter("booleanAsString",this,arguments);
					egl.addLocalFunctionVariable("value", value, "eglx.lang.EBoolean", "value");
					egl.atLine(this.eze$$fileName,16,628,68, this);
					if (value) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,17,644,17, this);
							if (!egl.debugg) egl.leave();
							return "true";
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,19,672,18, this);
							if (!egl.debugg) egl.leave();
							return "false";
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,15,567,134, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"characterLen": function(text) {
				try { egl.enter("characterLen",this,arguments);
					egl.addLocalFunctionVariable("text", text, "eglx.lang.EString", "text");
					egl.atLine(this.eze$$fileName,24,758,31, this);
					if (!egl.debugg) egl.leave();
					return egl.eglx.lang.EString.textLen(egl.eglx.lang.EString.clip(text));
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"clip_1_S": function(source) {
				try { egl.enter("clip_1_S",this,arguments);
					egl.addLocalFunctionVariable("source", source, "eglx.lang.EString", "source");
					egl.atLine(this.eze$$fileName,28,850,81, this);
					if ((egl.eglx.lang.EAny.equals(source, null))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,29,875,15, this);
							if (!egl.debugg) egl.leave();
							return null;
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,31,901,24, this);
							if (!egl.debugg) egl.leave();
							return egl.eglx.lang.EString.clip(source);
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,27,798,138, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"clip_2_S_I": function(source, clipType) {
				try { egl.enter("clip_2_S_I",this,arguments);
					egl.addLocalFunctionVariable("source", source, "eglx.lang.EString", "source");
					egl.addLocalFunctionVariable("clipType", clipType, "eglx.lang.EInt", "clipType");
					egl.atLine(this.eze$$fileName,36,1010,274, this);
					if ((((egl.eglx.lang.EAny.equals(source, null)) || (egl.eglx.lang.EAny.equals(clipType, null))))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,37,1055,15, this);
							if (!egl.debugg) egl.leave();
							return null;
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							{
								egl.atLine(this.eze$$fileName,39,1081,197, this);
								egl.atLine(this.eze$$fileName,39,1081,197, this);
								EzeLabel_eze_CaseLabel_0: if (((clipType == 0))) {
									try{egl.enterBlock();
										egl.atLine(this.eze$$fileName,41,1117,24, this);
										if (!egl.debugg) egl.leave();
										return egl.eglx.lang.EString.trim(source);
									}finally{egl.exitBlock();}
								}
								else {
									try{egl.enterBlock();
										egl.atLine(this.eze$$fileName,42,1146,45, this);
										if (((clipType == 1))) {
											try{egl.enterBlock();
												egl.atLine(this.eze$$fileName,43,1160,31, this);
												if (!egl.debugg) egl.leave();
												return egl.eglx.lang.EString.clipLeading(source);
											}finally{egl.exitBlock();}
										}
										else {
											try{egl.enterBlock();
												egl.atLine(this.eze$$fileName,44,1196,38, this);
												if (((clipType == 2))) {
													try{egl.enterBlock();
														egl.atLine(this.eze$$fileName,45,1210,24, this);
														if (!egl.debugg) egl.leave();
														return egl.eglx.lang.EString.clip(source);
													}finally{egl.exitBlock();}
												}
												else {
													try{egl.enterBlock();
														egl.atLine(this.eze$$fileName,47,1254,17, this);
														if (!egl.debugg) egl.leave();
														return source;
													}finally{egl.exitBlock();}
												}
											}finally{egl.exitBlock();}
										}
									}finally{egl.exitBlock();}
								}
							}
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,35,940,349, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"formatDate": function(dateValue, format) {
				try { egl.enter("formatDate",this,arguments);
					egl.addLocalFunctionVariable("dateValue", dateValue, "eglx.lang.EDate", "dateValue");
					egl.addLocalFunctionVariable("format", format, "eglx.lang.EString", "format");
					egl.atLine(this.eze$$fileName,53,1372,126, this);
					if ((((egl.eglx.lang.EAny.equals(dateValue, null)) || (egl.eglx.lang.EAny.equals(format, null))))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,54,1418,15, this);
							if (!egl.debugg) egl.leave();
							return null;
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							var eze$Temp9;
							egl.atLine(this.eze$$fileName,56,1452,37, this);
							eze$Temp9 = egl.checkNull({eze$$value : dateValue, eze$$signature : egl.inferSignature(dateValue)});
							var eze$Temp8;
							egl.atLine(this.eze$$fileName,56,1444,48, this);
							eze$Temp8 = egl.eglx.lang.StringLib.format(eze$Temp9, egl.checkNull(format));
							egl.atLine(this.eze$$fileName,56,1444,48, this);
							if (!egl.debugg) egl.leave();
							return eze$Temp8;
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,52,1294,209, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"formatNumber_2_i_S": function(intValue, format) {
				try { egl.enter("formatNumber_2_i_S",this,arguments);
					egl.addLocalFunctionVariable("intValue", intValue, "eglx.lang.ESmallint", "intValue");
					egl.addLocalFunctionVariable("format", format, "eglx.lang.EString", "format");
					egl.atLine(this.eze$$fileName,61,1592,124, this);
					if ((((egl.eglx.lang.EAny.equals(intValue, null)) || (egl.eglx.lang.EAny.equals(format, null))))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,62,1637,15, this);
							if (!egl.debugg) egl.leave();
							return null;
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							var eze$Temp12;
							egl.atLine(this.eze$$fileName,64,1671,36, this);
							eze$Temp12 = egl.checkNull({eze$$value : intValue, eze$$signature : egl.inferSignature(intValue)});
							var eze$Temp11;
							egl.atLine(this.eze$$fileName,64,1663,47, this);
							eze$Temp11 = egl.eglx.lang.StringLib.format(eze$Temp12, egl.checkNull(format));
							egl.atLine(this.eze$$fileName,64,1663,47, this);
							if (!egl.debugg) egl.leave();
							return eze$Temp11;
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,60,1508,213, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"formatNumber_2_I_S": function(intValue, format) {
				try { egl.enter("formatNumber_2_I_S",this,arguments);
					egl.addLocalFunctionVariable("intValue", intValue, "eglx.lang.EInt", "intValue");
					egl.addLocalFunctionVariable("format", format, "eglx.lang.EString", "format");
					egl.atLine(this.eze$$fileName,69,1805,124, this);
					if ((((egl.eglx.lang.EAny.equals(intValue, null)) || (egl.eglx.lang.EAny.equals(format, null))))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,70,1850,15, this);
							if (!egl.debugg) egl.leave();
							return null;
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							var eze$Temp15;
							egl.atLine(this.eze$$fileName,72,1884,36, this);
							eze$Temp15 = egl.checkNull({eze$$value : intValue, eze$$signature : egl.inferSignature(intValue)});
							var eze$Temp14;
							egl.atLine(this.eze$$fileName,72,1876,47, this);
							eze$Temp14 = egl.eglx.lang.StringLib.format(eze$Temp15, egl.checkNull(format));
							egl.atLine(this.eze$$fileName,72,1876,47, this);
							if (!egl.debugg) egl.leave();
							return eze$Temp14;
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,68,1726,208, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"formatNumber_2_B_S": function(intValue, format) {
				try { egl.enter("formatNumber_2_B_S",this,arguments);
					egl.addLocalFunctionVariable("intValue", intValue, "eglx.lang.EBigint", "intValue");
					egl.addLocalFunctionVariable("format", format, "eglx.lang.EString", "format");
					egl.atLine(this.eze$$fileName,77,2021,124, this);
					if ((((egl.eglx.lang.EAny.equals(intValue, null)) || (egl.eglx.lang.EAny.equals(format, null))))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,78,2066,15, this);
							if (!egl.debugg) egl.leave();
							return null;
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							var eze$Temp18;
							egl.atLine(this.eze$$fileName,80,2100,36, this);
							eze$Temp18 = egl.checkNull({eze$$value : intValue, eze$$signature : egl.inferSignature(intValue)});
							var eze$Temp17;
							egl.atLine(this.eze$$fileName,80,2092,47, this);
							eze$Temp17 = egl.eglx.lang.StringLib.format(eze$Temp18, egl.checkNull(format));
							egl.atLine(this.eze$$fileName,80,2092,47, this);
							if (!egl.debugg) egl.leave();
							return eze$Temp17;
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,76,1939,211, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"formatNumber_2_d_S": function(decValue, format) {
				try { egl.enter("formatNumber_2_d_S",this,arguments);
					egl.addLocalFunctionVariable("decValue", decValue, "eglx.lang.EDecimal", "decValue");
					egl.addLocalFunctionVariable("format", format, "eglx.lang.EString", "format");
					egl.atLine(this.eze$$fileName,85,2238,124, this);
					if ((((egl.eglx.lang.EAny.equals(decValue, null)) || (egl.eglx.lang.EAny.equals(format, null))))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,86,2283,15, this);
							if (!egl.debugg) egl.leave();
							return null;
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,88,2309,47, this);
							if (!egl.debugg) egl.leave();
							return egl.eglx.lang.StringLib.format(egl.checkNull(decValue), egl.checkNull(format));
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,84,2155,212, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"formatNumber_2_f_S": function(floatValue, format) {
				try { egl.enter("formatNumber_2_f_S",this,arguments);
					egl.addLocalFunctionVariable("floatValue", floatValue, "eglx.lang.ESmallfloat", "floatValue");
					egl.addLocalFunctionVariable("format", format, "eglx.lang.EString", "format");
					egl.atLine(this.eze$$fileName,93,2460,128, this);
					if ((((egl.eglx.lang.EAny.equals(floatValue, null)) || (egl.eglx.lang.EAny.equals(format, null))))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,94,2507,15, this);
							if (!egl.debugg) egl.leave();
							return null;
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							var eze$Temp22;
							egl.atLine(this.eze$$fileName,96,2541,38, this);
							eze$Temp22 = egl.checkNull({eze$$value : floatValue, eze$$signature : egl.inferSignature(floatValue)});
							var eze$Temp21;
							egl.atLine(this.eze$$fileName,96,2533,49, this);
							eze$Temp21 = egl.eglx.lang.StringLib.format(eze$Temp22, egl.checkNull(format));
							egl.atLine(this.eze$$fileName,96,2533,49, this);
							if (!egl.debugg) egl.leave();
							return eze$Temp21;
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,92,2372,221, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"formatNumber_2_F_S": function(floatValue, format) {
				try { egl.enter("formatNumber_2_F_S",this,arguments);
					egl.addLocalFunctionVariable("floatValue", floatValue, "eglx.lang.EFloat", "floatValue");
					egl.addLocalFunctionVariable("format", format, "eglx.lang.EString", "format");
					egl.atLine(this.eze$$fileName,101,2681,128, this);
					if ((((egl.eglx.lang.EAny.equals(floatValue, null)) || (egl.eglx.lang.EAny.equals(format, null))))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,102,2728,15, this);
							if (!egl.debugg) egl.leave();
							return null;
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							var eze$Temp25;
							egl.atLine(this.eze$$fileName,104,2762,38, this);
							eze$Temp25 = egl.checkNull({eze$$value : floatValue, eze$$signature : egl.inferSignature(floatValue)});
							var eze$Temp24;
							egl.atLine(this.eze$$fileName,104,2754,49, this);
							eze$Temp24 = egl.eglx.lang.StringLib.format(eze$Temp25, egl.checkNull(format));
							egl.atLine(this.eze$$fileName,104,2754,49, this);
							if (!egl.debugg) egl.leave();
							return eze$Temp24;
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,100,2598,216, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"formatTimeStamp": function(timestampvalue, format) {
				try { egl.enter("formatTimeStamp",this,arguments);
					egl.addLocalFunctionVariable("timestampvalue", timestampvalue, "eglx.lang.ETimestamp(yyyyMMddHHmmssffffff)", "timestampvalue");
					egl.addLocalFunctionVariable("format", format, "eglx.lang.EString", "format");
					egl.atLine(this.eze$$fileName,109,2937,136, this);
					if ((((egl.eglx.lang.EAny.equals(timestampvalue, null)) || (egl.eglx.lang.EAny.equals(format, null))))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,110,2988,15, this);
							if (!egl.debugg) egl.leave();
							return null;
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,112,3014,53, this);
							if (!egl.debugg) egl.leave();
							return egl.eglx.lang.StringLib.format(egl.checkNull(timestampvalue), egl.checkNull(format));
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,108,2819,259, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"getNextToken": function(source, index, delimiters) {
				try { egl.enter("getNextToken",this,arguments);
					egl.addLocalFunctionVariable("source", source, "eglx.lang.EString", "source");
					egl.addLocalFunctionVariable("index", index, "eglx.lang.EInt", "index");
					egl.addLocalFunctionVariable("delimiters", delimiters, "eglx.lang.EString", "delimiters");
					var charIndex;
					egl.addLocalFunctionVariable("charIndex", charIndex, "eglx.lang.EInt", "charIndex");
					egl.atLine(this.eze$$fileName,117,3197,13, this);
					charIndex = egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEDecimal, [(((new egl.javascript.BigDecimal(egl.divide(egl.eglx.lang.EAny.unbox(index),2))).add(egl.eglx.lang.convert(egl.eglx.lang.EDecimal.fromEInt32, [1, egl.javascript.BigDecimal.prototype.NINES[3]])))),"d;"]);
					egl.setLocalFunctionVariable("charIndex", charIndex, "eglx.lang.EInt");
					var eze$Temp27;
					egl.atLine(this.eze$$fileName,118,3230,55, this);
					eze$Temp27 = egl.eglx.lang.EAny.ezeWrap(charIndex);
					var token;
					egl.addLocalFunctionVariable("token", token, "eglx.lang.EString", "token");
					egl.atLine(this.eze$$fileName,118,3230,55, this);
					token = egl.eglx.lang.StringLib.getNextToken(source, eze$Temp27, delimiters);
					egl.setLocalFunctionVariable("token", token, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,118,3230,55, this);
					charIndex = eze$Temp27.ezeUnbox();
					egl.setLocalFunctionVariable("charIndex", charIndex, "eglx.lang.EInt");
					egl.atLine(this.eze$$fileName,119,3289,32, this);
					index.ezeCopy(((((((charIndex - 1)) * 2)) + 1)));
					egl.setLocalFunctionVariable("index", index, "eglx.lang.EInt");
					egl.atLine(this.eze$$fileName,120,3324,16, this);
					if (!egl.debugg) egl.leave();
					return token;
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"getTokenCount": function(source, delimiters) {
				try { egl.enter("getTokenCount",this,arguments);
					egl.addLocalFunctionVariable("source", source, "eglx.lang.EString", "source");
					egl.addLocalFunctionVariable("delimiters", delimiters, "eglx.lang.EString", "delimiters");
					egl.atLine(this.eze$$fileName,124,3428,56, this);
					if (!egl.debugg) egl.leave();
					return egl.eglx.lang.StringLib.getTokenCount(source, delimiters);
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"indexOf_2_S_S": function(source, pattern) {
				try { egl.enter("indexOf_2_S_S",this,arguments);
					egl.addLocalFunctionVariable("source", source, "eglx.lang.EString", "source");
					egl.addLocalFunctionVariable("pattern", pattern, "eglx.lang.EString", "pattern");
					egl.atLine(this.eze$$fileName,128,3563,36, this);
					if (!egl.debugg) egl.leave();
					return egl.eglx.lang.EString.indexOf(source, pattern);
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"indexOf_3_S_S_I": function(source, pattern, startIndex) {
				try { egl.enter("indexOf_3_S_S_I",this,arguments);
					egl.addLocalFunctionVariable("source", source, "eglx.lang.EString", "source");
					egl.addLocalFunctionVariable("pattern", pattern, "eglx.lang.EString", "pattern");
					egl.addLocalFunctionVariable("startIndex", startIndex, "eglx.lang.EInt", "startIndex");
					egl.atLine(this.eze$$fileName,132,3697,48, this);
					if (!egl.debugg) egl.leave();
					return egl.eglx.lang.EString.indexOf(source, pattern, startIndex);
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"lowerCase": function(characterItem) {
				try { egl.enter("lowerCase",this,arguments);
					egl.addLocalFunctionVariable("characterItem", characterItem, "eglx.lang.EString", "characterItem");
					egl.atLine(this.eze$$fileName,136,3818,102, this);
					if ((egl.eglx.lang.EAny.equals(characterItem, null))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,137,3850,15, this);
							if (!egl.debugg) egl.leave();
							return null;
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,139,3876,38, this);
							if (!egl.debugg) egl.leave();
							return egl.eglx.lang.EString.toLowerCase(characterItem);
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,135,3754,171, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"spaces": function(characterCount) {
				try { egl.enter("spaces",this,arguments);
					egl.addLocalFunctionVariable("characterCount", characterCount, "eglx.lang.EInt", "characterCount");
					egl.atLine(this.eze$$fileName,144,3987,355, this);
					if ((egl.eglx.lang.EAny.equals(characterCount, null))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,145,4020,15, this);
							if (!egl.debugg) egl.leave();
							return null;
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							var result = "";
							egl.addLocalFunctionVariable("result", result, "eglx.lang.EString", "result");
							var fiftyBlanks;
							egl.addLocalFunctionVariable("fiftyBlanks", fiftyBlanks, "eglx.lang.EString", "fiftyBlanks");
							egl.atLine(this.eze$$fileName,148,4085,52, this);
							fiftyBlanks = "                                                  ";
							egl.setLocalFunctionVariable("fiftyBlanks", fiftyBlanks, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,149,4142,91, this);
							while (((characterCount >= 50))) {
								try{egl.enterBlock();
									egl.atLine(this.eze$$fileName,150,4177,23, this);
									result = ((result) + fiftyBlanks);
									egl.setLocalFunctionVariable("result", result, "eglx.lang.EString");
									egl.atLine(this.eze$$fileName,151,4205,21, this);
									characterCount = ((characterCount - 50));
									egl.setLocalFunctionVariable("characterCount", characterCount, "eglx.lang.EInt");
								}finally{egl.exitBlock();}
								egl.atLine(this.eze$$fileName,149,4142,91, this);
							}
							egl.atLine(this.eze$$fileName,153,4237,78, this);
							if (((characterCount > 0))) {
								try{egl.enterBlock();
									egl.atLine(this.eze$$fileName,154,4267,41, this);
									result = ((result) + egl.eglx.lang.EString.substring(fiftyBlanks, 1, characterCount));
									egl.setLocalFunctionVariable("result", result, "eglx.lang.EString");
								}finally{egl.exitBlock();}
							}
							egl.atLine(this.eze$$fileName,156,4319,17, this);
							if (!egl.debugg) egl.leave();
							return result;
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,143,3928,419, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"upperCase": function(characterItem) {
				try { egl.enter("upperCase",this,arguments);
					egl.addLocalFunctionVariable("characterItem", characterItem, "eglx.lang.EString", "characterItem");
					egl.atLine(this.eze$$fileName,161,4414,102, this);
					if ((egl.eglx.lang.EAny.equals(characterItem, null))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,162,4446,15, this);
							if (!egl.debugg) egl.leave();
							return null;
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,164,4472,38, this);
							if (!egl.debugg) egl.leave();
							return egl.eglx.lang.EString.toUpperCase(characterItem);
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,160,4350,171, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"toString": function() {
				return "[StrLib]";
			}
			,
			"eze$$getName": function() {
				return "StrLib";
			}
			,
			"eze$$getChildVariables": function() {
				var eze$$parent = this;
				return [
				];
			}
		}
	);
