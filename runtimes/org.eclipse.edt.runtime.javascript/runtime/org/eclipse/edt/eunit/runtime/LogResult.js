/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
define(["org/eclipse/edt/eunit/runtime/ConstantsLib", "org/eclipse/edt/eunit/runtime/Log", "org/eclipse/edt/eunit/runtime/AssertionFailedException", "org/eclipse/edt/eunit/runtime/Status"],function(){
	if (egl.eze$$userLibs) egl.eze$$userLibs.push('org.eclipse.edt.eunit.runtime.LogResult');
	else egl.eze$$userLibs = ['org.eclipse.edt.eunit.runtime.LogResult'];
	egl.defineRUILibrary('org.eclipse.edt.eunit.runtime', 'LogResult',
	{
		'eze$$fileName': 'org/eclipse/edt/eunit/runtime/LogResult.egl',
		'eze$$runtimePropertiesFile': 'org.eclipse.edt.eunit.runtime.LogResult',
			"constructor": function() {
				if(egl.org.eclipse.edt.eunit.runtime.LogResult['$inst']) return egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'];
				egl.org.eclipse.edt.eunit.runtime.LogResult['$inst']=this;
				new egl.org.eclipse.edt.eunit.runtime.ConstantsLib();
				this.ACTUALHEADER = "";
				egl.atLine(this.eze$$fileName,42,1057,17, this);
				this.ACTUALHEADER = "Actual value = ";
				this.EXPECTEDHEADER = "";
				egl.atLine(this.eze$$fileName,43,1119,19, this);
				this.EXPECTEDHEADER = "Expected value = ";
				this.ACTUALSIZEHEADER = "";
				egl.atLine(this.eze$$fileName,44,1185,22, this);
				this.ACTUALSIZEHEADER = "Actual array size = ";
				this.EXPECTEDSIZEHEADER = "";
				egl.atLine(this.eze$$fileName,45,1256,24, this);
				this.EXPECTEDSIZEHEADER = "Exepcted array size = ";
				this.eze$$setInitial();
			}
			,
			"eze$$setEmpty": function() {
				this.outR = new egl.org.eclipse.edt.eunit.runtime.Log();
				this.s = new egl.org.eclipse.edt.eunit.runtime.Status();
			}
			,
			"eze$$setInitial": function() {
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
					this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("LogResult", null, false);
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
			"clearResults": function() {
				try { egl.enter("clearResults",this,arguments);
					egl.atLine(this.eze$$fileName,48,1314,14, this);
					this.outR.msg = "";
					egl.atLine(this.eze$$fileName,49,1332,12, this);
					this.s.code = -1;
					egl.atLine(this.eze$$fileName,50,1348,14, this);
					this.s.reason = "";
					egl.atLine(this.eze$$fileName,47,1287,81, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"getStatus": function() {
				try { egl.enter("getStatus",this,arguments);
					egl.atLine(this.eze$$fileName,54,1415,11, this);
					if (!egl.debugg) egl.leave();
					return this.s == null ? null : this.s.eze$$clone();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"getLog": function() {
				try { egl.enter("getLog",this,arguments);
					egl.atLine(this.eze$$fileName,58,1472,14, this);
					if (!egl.debugg) egl.leave();
					return this.outR == null ? null : this.outR.eze$$clone();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"logStdOut": function(logmsg) {
				try { egl.enter("logStdOut",this,arguments);
					egl.addLocalFunctionVariable("logmsg", logmsg, "eglx.lang.EString", "logmsg");
					egl.atLine(this.eze$$fileName,65,1574,33, this);
					this.outR.msg = ((this.outR.msg) + egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE);
					egl.atLine(this.eze$$fileName,66,1611,19, this);
					this.outR.msg = ((this.outR.msg) + logmsg);
					egl.atLine(this.eze$$fileName,64,1534,104, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"passed": function(str) {
				try { egl.enter("passed",this,arguments);
					egl.addLocalFunctionVariable("str", str, "eglx.lang.EString", "str");
					egl.atLine(this.eze$$fileName,73,1724,30, this);
					this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SPASSED;
					egl.atLine(this.eze$$fileName,74,1758,51, this);
					if ((((egl.eglx.lang.NullType.equals(str, null)) || ((str) == "")))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,75,1791,11, this);
							str = "OK";
							egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,77,1813,15, this);
					this.s.reason = str;
					egl.atLine(this.eze$$fileName,72,1690,144, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"failed": function(str) {
				try { egl.enter("failed",this,arguments);
					egl.addLocalFunctionVariable("str", str, "eglx.lang.EString", "str");
					egl.atLine(this.eze$$fileName,81,1874,30, this);
					this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SFAILED;
					egl.atLine(this.eze$$fileName,82,1908,24, this);
					str = (("FAILED - ") + str);
					egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,83,1936,15, this);
					this.s.reason = str;
					egl.atLine(this.eze$$fileName,80,1840,117, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"error": function(str) {
				try { egl.enter("error",this,arguments);
					egl.addLocalFunctionVariable("str", str, "eglx.lang.EString", "str");
					egl.atLine(this.eze$$fileName,88,2037,29, this);
					this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SERROR;
					egl.atLine(this.eze$$fileName,89,2070,23, this);
					str = (("ERROR - ") + str);
					egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,90,2097,15, this);
					this.s.reason = str;
					egl.atLine(this.eze$$fileName,87,2004,114, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"skipped": function(str) {
				try { egl.enter("skipped",this,arguments);
					egl.addLocalFunctionVariable("str", str, "eglx.lang.EString", "str");
					egl.atLine(this.eze$$fileName,94,2159,31, this);
					this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SNOT_RUN;
					egl.atLine(this.eze$$fileName,95,2194,25, this);
					str = (("SKIPPED - ") + str);
					egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,96,2223,15, this);
					this.s.reason = str;
					egl.atLine(this.eze$$fileName,93,2124,120, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertTrueException": function(failedReason, testCondition, throwsFailException) {
				try { egl.enter("assertTrueException",this,arguments);
					egl.addLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString", "failedReason");
					egl.addLocalFunctionVariable("testCondition", testCondition, "eglx.lang.EBoolean", "testCondition");
					egl.addLocalFunctionVariable("throwsFailException", throwsFailException, "eglx.lang.EBoolean", "throwsFailException");
					egl.atLine(this.eze$$fileName,100,2372,173, this);
					if (testCondition) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,101,2394,13, this);
							this.passed("OK");
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,103,2420,21, this);
							this.failed(failedReason);
							egl.atLine(this.eze$$fileName,104,2446,92, this);
							if (throwsFailException) {
								try{egl.enterBlock();
									var eze$LNNTemp4 = null;
									{
										var eze$SettingTarget1;
										egl.atLine(this.eze$$fileName,105,2481,48, this);
										eze$SettingTarget1 = new egl.org.eclipse.edt.eunit.runtime.AssertionFailedException();
										egl.atLine(this.eze$$fileName,105,2510,18, this);
										egl.checkNull(eze$SettingTarget1).message = this.s.reason;
										egl.atLine(this.eze$$fileName,105,2481,48, this);
										eze$LNNTemp4 = eze$SettingTarget1;
									}
									egl.atLine(this.eze$$fileName,105,2475,55, this);
									throw eze$LNNTemp4;
								}finally{egl.exitBlock();}
							}
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,99,2250,301, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertTrue": function(failedReason, testCondition) {
				try { egl.enter("assertTrue",this,arguments);
					egl.addLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString", "failedReason");
					egl.addLocalFunctionVariable("testCondition", testCondition, "eglx.lang.EBoolean", "testCondition");
					egl.atLine(this.eze$$fileName,120,3080,55, this);
					this.assertTrueException(failedReason, testCondition, true);
					egl.atLine(this.eze$$fileName,119,3007,134, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertTrue1": function(testCondition) {
				try { egl.enter("assertTrue1",this,arguments);
					egl.addLocalFunctionVariable("testCondition", testCondition, "eglx.lang.EBoolean", "testCondition");
					egl.atLine(this.eze$$fileName,128,3403,30, this);
					this.assertTrue("", testCondition);
					egl.atLine(this.eze$$fileName,127,3353,86, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertBigIntEqual": function(message, expected, actual) {
				try { egl.enter("assertBigIntEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EBigint", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EBigint", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,136,3663,20, this);
					isEqual = (egl.eglx.lang.EInt64.equals(expected,actual));
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,137,3688,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"B;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"B;"]), isEqual);
					egl.atLine(this.eze$$fileName,135,3558,189, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertBigIntEqual1": function(expected, actual) {
				try { egl.enter("assertBigIntEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EBigint", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EBigint", "actual");
					egl.atLine(this.eze$$fileName,141,3822,40, this);
					this.assertBigIntEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,140,3753,115, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertStringEqual": function(message, expected, actual) {
				try { egl.enter("assertStringEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EString", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EString", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,145,3981,20, this);
					isEqual = ((expected) == actual);
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,146,4006,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"S;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"S;"]), isEqual);
					egl.atLine(this.eze$$fileName,144,3876,189, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertStringEqual1": function(expected, actual) {
				try { egl.enter("assertStringEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EString", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EString", "actual");
					egl.atLine(this.eze$$fileName,150,4140,40, this);
					this.assertStringEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,149,4071,115, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertStringArrayEqual": function(message, expected, actual) {
				try { egl.enter("assertStringArrayEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EList<eglx.lang.EString>", "!expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EList<eglx.lang.EString>", "!actual");
					var isArrayEqual;
					egl.addLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean", "isArrayEqual");
					egl.atLine(this.eze$$fileName,154,4313,4, this);
					isArrayEqual = true;
					egl.setLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean");
					var expectedSize;
					egl.addLocalFunctionVariable("expectedSize", expectedSize, "eglx.lang.EInt", "expectedSize");
					egl.atLine(this.eze$$fileName,155,4341,18, this);
					expectedSize = expected.getSize();
					egl.setLocalFunctionVariable("expectedSize", expectedSize, "eglx.lang.EInt");
					var actualSize;
					egl.addLocalFunctionVariable("actualSize", actualSize, "eglx.lang.EInt", "actualSize");
					egl.atLine(this.eze$$fileName,156,4383,16, this);
					actualSize = actual.getSize();
					egl.setLocalFunctionVariable("actualSize", actualSize, "eglx.lang.EInt");
					var failedReason = "";
					egl.addLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString", "failedReason");
					egl.atLine(this.eze$$fileName,159,4430,904, this);
					if (((expectedSize == actualSize))) {
						try{egl.enterBlock();
							var failedHeader;
							egl.addLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString", "failedHeader");
							egl.atLine(this.eze$$fileName,160,4487,20, this);
							failedHeader = "Array element No.[";
							egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
							var expectedValues;
							egl.addLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString", "expectedValues");
							egl.atLine(this.eze$$fileName,161,4537,20, this);
							expectedValues = ((this.EXPECTEDHEADER) + "[");
							egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
							var actualValues;
							egl.addLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString", "actualValues");
							egl.atLine(this.eze$$fileName,162,4585,18, this);
							actualValues = ((this.ACTUALHEADER) + "[");
							egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,163,4609,395, this);
							{
								try{egl.enterBlock();
									var i = 0;
									egl.addLocalFunctionVariable("i", i, "eglx.lang.EInt", "i");
									for (i = 1; ((i <= expectedSize)); i = ((i + 1))) {
										egl.setLocalFunctionVariable("i", i, "eglx.lang.EInt");
										try{egl.enterBlock();
											egl.atLine(this.eze$$fileName,164,4648,177, this);
											if (((egl.checkNull(expected, "expected")[expected.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]) != egl.checkNull(actual, "actual")[actual.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)])) {
												try{egl.enterBlock();
													egl.atLine(this.eze$$fileName,165,4683,56, this);
													if (!(isArrayEqual)) {
														try{egl.enterBlock();
															egl.atLine(this.eze$$fileName,166,4708,21, this);
															failedHeader = ((failedHeader) + ", ");
															egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
														}finally{egl.exitBlock();}
													}
													egl.atLine(this.eze$$fileName,168,4747,21, this);
													isArrayEqual = false;
													egl.setLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean");
													egl.atLine(this.eze$$fileName,169,4780,28, this);
													failedHeader = ((failedHeader) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32, [i]));
													egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
												}finally{egl.exitBlock();}
											}
											egl.atLine(this.eze$$fileName,172,4840,30, this);
											expectedValues = ((expectedValues) + egl.checkNull(expected, "expected")[expected.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]);
											egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
											egl.atLine(this.eze$$fileName,173,4876,26, this);
											actualValues = ((actualValues) + egl.checkNull(actual, "actual")[actual.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]);
											egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
											egl.atLine(this.eze$$fileName,174,4908,88, this);
											if (((i != expectedSize))) {
												try{egl.enterBlock();
													egl.atLine(this.eze$$fileName,175,4936,23, this);
													expectedValues = ((expectedValues) + ", ");
													egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
													egl.atLine(this.eze$$fileName,176,4966,21, this);
													actualValues = ((actualValues) + ", ");
													egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
												}finally{egl.exitBlock();}
											}
										}finally{egl.exitBlock();}
										egl.atLine(this.eze$$fileName,163,4609,395, this);
									}
								}
								finally{egl.exitBlock();}
							}
							egl.atLine(this.eze$$fileName,179,5009,30, this);
							failedHeader = ((failedHeader) + "] differs; ");
							egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,180,5044,24, this);
							expectedValues = ((expectedValues) + "]; ");
							egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,181,5073,21, this);
							actualValues = ((actualValues) + "] ");
							egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,182,5099,60, this);
							failedReason = ((((failedHeader) + expectedValues)) + actualValues);
							egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,184,5172,21, this);
							isArrayEqual = false;
							egl.setLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean");
							egl.atLine(this.eze$$fileName,185,5199,120, this);
							failedReason = (((((((((((((((("Failed: ") + this.EXPECTEDSIZEHEADER)) + "'")) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32, [expectedSize]))) + "' ")) + this.ACTUALSIZEHEADER)) + "'")) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32, [actualSize]))) + "' ");
							egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,187,5338,95, this);
					if ((((egl.eglx.lang.NullType.notEquals(message, null)) && ((message) != "")))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,187,5374,1, this);
							;
							egl.atLine(this.eze$$fileName,188,5380,46, this);
							failedReason = ((((message) + " - ")) + failedReason);
							egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,190,5437,39, this);
					this.assertTrue(failedReason, isArrayEqual);
					egl.atLine(this.eze$$fileName,153,4194,1288, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertStringArrayEqual1": function(expected, actual) {
				try { egl.enter("assertStringArrayEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EList<eglx.lang.EString>", "!expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EList<eglx.lang.EString>", "!actual");
					egl.atLine(this.eze$$fileName,194,5567,45, this);
					this.assertStringArrayEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,193,5489,129, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertDateEqual": function(message, expected, actual) {
				try { egl.enter("assertDateEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EDate", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EDate", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,198,5724,20, this);
					isEqual = (egl.eglx.lang.EDate.equals(expected, actual));
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,199,5749,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"K;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"K;"]), isEqual);
					egl.atLine(this.eze$$fileName,197,5625,183, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertDateEqual1": function(expected, actual) {
				try { egl.enter("assertDateEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EDate", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EDate", "actual");
					egl.atLine(this.eze$$fileName,203,5877,38, this);
					this.assertDateEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,202,5814,107, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertTimeEqual": function(message, expected, actual) {
				try { egl.enter("assertTimeEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.ETime", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.ETime", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,207,6026,20, this);
					isEqual = (egl.eglx.lang.ETime.equals(expected, actual));
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,208,6051,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"L;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"L;"]), isEqual);
					egl.atLine(this.eze$$fileName,206,5927,183, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertTimeEqual1": function(expected, actual) {
				try { egl.enter("assertTimeEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.ETime", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.ETime", "actual");
					egl.atLine(this.eze$$fileName,212,6181,38, this);
					this.assertTimeEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,211,6118,107, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertTimestampEqual": function(message, expected, actual) {
				try { egl.enter("assertTimestampEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.ETimestamp", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.ETimestamp", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,216,6346,20, this);
					isEqual = (egl.eglx.lang.ETimestamp.equals(expected, actual));
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,217,6371,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"yyyyMMddhhmmss","J;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"yyyyMMddhhmmss","J;"]), isEqual);
					egl.atLine(this.eze$$fileName,215,6232,200, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertTimestampEqual1": function(expected, actual) {
				try { egl.enter("assertTimestampEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.ETimestamp", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.ETimestamp", "actual");
					egl.atLine(this.eze$$fileName,221,6516,43, this);
					this.assertTimestampEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,220,6438,127, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertDecimalEqual": function(message, expected, actual) {
				try { egl.enter("assertDecimalEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EDecimal", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EDecimal", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,225,6681,20, this);
					isEqual = (egl.eglx.lang.EDecimal.equals(expected,actual));
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,226,6706,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"d;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"d;"]), isEqual);
					egl.atLine(this.eze$$fileName,224,6573,196, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertDecimalEqual1": function(expected, actual) {
				try { egl.enter("assertDecimalEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EDecimal", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EDecimal", "actual");
					egl.atLine(this.eze$$fileName,230,6847,41, this);
					this.assertDecimalEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,229,6775,119, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertFloatEqual": function(message, expected, actual) {
				try { egl.enter("assertFloatEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EFloat", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EFloat", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,234,7003,30, this);
					isEqual = this.isFloatEqual(expected, actual);
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,235,7038,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"F;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"F;"]), isEqual);
					egl.atLine(this.eze$$fileName,233,6901,196, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertFloatEqual1": function(expected, actual) {
				try { egl.enter("assertFloatEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EFloat", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EFloat", "actual");
					egl.atLine(this.eze$$fileName,239,7169,39, this);
					this.assertFloatEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,238,7103,111, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"isFloatEqual": function(expected, actual) {
				try { egl.enter("isFloatEqual",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EFloat", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EFloat", "actual");
					var normalExpected = 0;
					egl.addLocalFunctionVariable("normalExpected", normalExpected, "eglx.lang.EFloat", "normalExpected");
					var normalActual = 0;
					egl.addLocalFunctionVariable("normalActual", normalActual, "eglx.lang.EFloat", "normalActual");
					var delta = 0;
					egl.addLocalFunctionVariable("delta", delta, "eglx.lang.EFloat", "delta");
					var mantissaExpected = 0;
					egl.addLocalFunctionVariable("mantissaExpected", mantissaExpected, "eglx.lang.EInt", "mantissaExpected");
					var mantissaActual = 0;
					egl.addLocalFunctionVariable("mantissaActual", mantissaActual, "eglx.lang.EInt", "mantissaActual");
					var signExpected = "";
					egl.addLocalFunctionVariable("signExpected", signExpected, "eglx.lang.EString", "signExpected");
					var signActual = "";
					egl.addLocalFunctionVariable("signActual", signActual, "eglx.lang.EString", "signActual");
					var deltaLimit;
					egl.addLocalFunctionVariable("deltaLimit", deltaLimit, "eglx.lang.EFloat", "deltaLimit");
					egl.atLine(this.eze$$fileName,247,7476,5, this);
					deltaLimit = 1E-14;
					egl.setLocalFunctionVariable("deltaLimit", deltaLimit, "eglx.lang.EFloat");
					var eze$Temp11 = 0;
					var eze$Temp12;
					egl.atLine(this.eze$$fileName,248,7503,53, this);
					eze$Temp12 = egl.eglx.lang.EAny.ezeWrap(eze$Temp11);
					var eze$Temp13 = "";
					var eze$Temp14;
					egl.atLine(this.eze$$fileName,248,7503,53, this);
					eze$Temp14 = egl.eglx.lang.EAny.ezeWrap(eze$Temp13);
					egl.atLine(this.eze$$fileName,248,7486,71, this);
					normalExpected = this.normalFloat(expected, eze$Temp12, eze$Temp14);
					egl.setLocalFunctionVariable("normalExpected", normalExpected, "eglx.lang.EFloat");
					egl.atLine(this.eze$$fileName,248,7503,53, this);
					mantissaExpected = eze$Temp12.ezeUnbox();
					egl.setLocalFunctionVariable("mantissaExpected", mantissaExpected, "eglx.lang.EInt");
					egl.atLine(this.eze$$fileName,248,7503,53, this);
					signExpected = eze$Temp14.ezeUnbox();
					egl.setLocalFunctionVariable("signExpected", signExpected, "eglx.lang.EString");
					var eze$Temp15 = 0;
					var eze$Temp16;
					egl.atLine(this.eze$$fileName,249,7576,47, this);
					eze$Temp16 = egl.eglx.lang.EAny.ezeWrap(eze$Temp15);
					var eze$Temp17 = "";
					var eze$Temp18;
					egl.atLine(this.eze$$fileName,249,7576,47, this);
					eze$Temp18 = egl.eglx.lang.EAny.ezeWrap(eze$Temp17);
					egl.atLine(this.eze$$fileName,249,7561,63, this);
					normalActual = this.normalFloat(actual, eze$Temp16, eze$Temp18);
					egl.setLocalFunctionVariable("normalActual", normalActual, "eglx.lang.EFloat");
					egl.atLine(this.eze$$fileName,249,7576,47, this);
					mantissaActual = eze$Temp16.ezeUnbox();
					egl.setLocalFunctionVariable("mantissaActual", mantissaActual, "eglx.lang.EInt");
					egl.atLine(this.eze$$fileName,249,7576,47, this);
					signActual = eze$Temp18.ezeUnbox();
					egl.setLocalFunctionVariable("signActual", signActual, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,250,7628,38, this);
					delta = ((normalExpected - normalActual));
					egl.setLocalFunctionVariable("delta", delta, "eglx.lang.EFloat");
					egl.atLine(this.eze$$fileName,251,7670,27, this);
					delta = egl.eglx.lang.MathLib.abs(delta);
					egl.setLocalFunctionVariable("delta", delta, "eglx.lang.EFloat");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,252,7719,105, this);
					isEqual = ((((((signExpected) == signActual) && ((mantissaExpected == mantissaActual)))) && ((delta < deltaLimit))));
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,256,7836,16, this);
					if (!egl.debugg) egl.leave();
					return isEqual;
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertSmallFloatEqual": function(message, expected, actual) {
				try { egl.enter("assertSmallFloatEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.ESmallfloat", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.ESmallfloat", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,260,7985,35, this);
					isEqual = this.isSmallFloatEqual(expected, actual);
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,261,8025,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"f;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"f;"]), isEqual);
					egl.atLine(this.eze$$fileName,259,7868,216, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertSmallFloatEqual1": function(expected, actual) {
				try { egl.enter("assertSmallFloatEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.ESmallfloat", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.ESmallfloat", "actual");
					egl.atLine(this.eze$$fileName,265,8171,44, this);
					this.assertSmallFloatEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,264,8090,131, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"isSmallFloatEqual": function(expected, actual) {
				try { egl.enter("isSmallFloatEqual",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.ESmallfloat", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.ESmallfloat", "actual");
					var normalExpected = 0;
					egl.addLocalFunctionVariable("normalExpected", normalExpected, "eglx.lang.ESmallfloat", "normalExpected");
					var normalActual = 0;
					egl.addLocalFunctionVariable("normalActual", normalActual, "eglx.lang.ESmallfloat", "normalActual");
					var delta = 0;
					egl.addLocalFunctionVariable("delta", delta, "eglx.lang.ESmallfloat", "delta");
					var mantissaExpected = 0;
					egl.addLocalFunctionVariable("mantissaExpected", mantissaExpected, "eglx.lang.EInt", "mantissaExpected");
					var mantissaActual = 0;
					egl.addLocalFunctionVariable("mantissaActual", mantissaActual, "eglx.lang.EInt", "mantissaActual");
					var signExpected = "";
					egl.addLocalFunctionVariable("signExpected", signExpected, "eglx.lang.EString", "signExpected");
					var signActual = "";
					egl.addLocalFunctionVariable("signActual", signActual, "eglx.lang.EString", "signActual");
					var deltaLimit;
					egl.addLocalFunctionVariable("deltaLimit", deltaLimit, "eglx.lang.EFloat", "deltaLimit");
					egl.atLine(this.eze$$fileName,273,8503,4, this);
					deltaLimit = 1E-4;
					egl.setLocalFunctionVariable("deltaLimit", deltaLimit, "eglx.lang.EFloat");
					var eze$Temp19 = 0;
					var eze$Temp20;
					egl.atLine(this.eze$$fileName,274,8529,58, this);
					eze$Temp20 = egl.eglx.lang.EAny.ezeWrap(eze$Temp19);
					var eze$Temp21 = "";
					var eze$Temp22;
					egl.atLine(this.eze$$fileName,274,8529,58, this);
					eze$Temp22 = egl.eglx.lang.EAny.ezeWrap(eze$Temp21);
					egl.atLine(this.eze$$fileName,274,8512,76, this);
					normalExpected = this.normalSmallFloat(expected, eze$Temp20, eze$Temp22);
					egl.setLocalFunctionVariable("normalExpected", normalExpected, "eglx.lang.ESmallfloat");
					egl.atLine(this.eze$$fileName,274,8529,58, this);
					mantissaExpected = eze$Temp20.ezeUnbox();
					egl.setLocalFunctionVariable("mantissaExpected", mantissaExpected, "eglx.lang.EInt");
					egl.atLine(this.eze$$fileName,274,8529,58, this);
					signExpected = eze$Temp22.ezeUnbox();
					egl.setLocalFunctionVariable("signExpected", signExpected, "eglx.lang.EString");
					var eze$Temp23 = 0;
					var eze$Temp24;
					egl.atLine(this.eze$$fileName,275,8607,52, this);
					eze$Temp24 = egl.eglx.lang.EAny.ezeWrap(eze$Temp23);
					var eze$Temp25 = "";
					var eze$Temp26;
					egl.atLine(this.eze$$fileName,275,8607,52, this);
					eze$Temp26 = egl.eglx.lang.EAny.ezeWrap(eze$Temp25);
					egl.atLine(this.eze$$fileName,275,8592,68, this);
					normalActual = this.normalSmallFloat(actual, eze$Temp24, eze$Temp26);
					egl.setLocalFunctionVariable("normalActual", normalActual, "eglx.lang.ESmallfloat");
					egl.atLine(this.eze$$fileName,275,8607,52, this);
					mantissaActual = eze$Temp24.ezeUnbox();
					egl.setLocalFunctionVariable("mantissaActual", mantissaActual, "eglx.lang.EInt");
					egl.atLine(this.eze$$fileName,275,8607,52, this);
					signActual = eze$Temp26.ezeUnbox();
					egl.setLocalFunctionVariable("signActual", signActual, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,276,8664,38, this);
					delta = ((normalExpected - normalActual));
					egl.setLocalFunctionVariable("delta", delta, "eglx.lang.ESmallfloat");
					egl.atLine(this.eze$$fileName,277,8706,27, this);
					delta = egl.eglx.lang.MathLib.abs(delta);
					egl.setLocalFunctionVariable("delta", delta, "eglx.lang.ESmallfloat");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,278,8755,105, this);
					isEqual = ((((((signExpected) == signActual) && ((mantissaExpected == mantissaActual)))) && ((delta < deltaLimit))));
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,282,8872,16, this);
					if (!egl.debugg) egl.leave();
					return isEqual;
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"expectAssertTrue": function(message, expected, actual, isEqual) {
				try { egl.enter("expectAssertTrue",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EAny", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EAny", "actual");
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					var failedReason;
					egl.addLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString", "failedReason");
					egl.atLine(this.eze$$fileName,286,9034,44, this);
					failedReason = this.buildFailedReason(message, expected, actual);
					egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,287,9083,34, this);
					this.assertTrue(failedReason, isEqual);
					egl.atLine(this.eze$$fileName,285,8904,221, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"buildFailedReason": function(message, expected, actual) {
				try { egl.enter("buildFailedReason",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EAny", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EAny", "actual");
					var failedReason;
					egl.addLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString", "failedReason");
					egl.atLine(this.eze$$fileName,291,9259,24, this);
					failedReason = this.expect(expected, actual);
					egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,292,9288,95, this);
					if ((((egl.eglx.lang.NullType.notEquals(message, null)) && ((message) != "")))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,293,9330,46, this);
							failedReason = ((((message) + " - ")) + failedReason);
							egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,295,9387,22, this);
					if (!egl.debugg) egl.leave();
					return failedReason;
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"expect": function(expected, actual) {
				try { egl.enter("expect",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EAny", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EAny", "actual");
					var standardMsg;
					egl.addLocalFunctionVariable("standardMsg", standardMsg, "eglx.lang.EString", "standardMsg");
					egl.atLine(this.eze$$fileName,299,9526,88, this);
					standardMsg = (((((((((((((((("Failed: ") + this.EXPECTEDHEADER)) + "'")) + egl.eglx.lang.EString.ezeCast(expected, false))) + "' ")) + this.ACTUALHEADER)) + "'")) + egl.eglx.lang.EString.ezeCast(actual, false))) + "' ");
					egl.setLocalFunctionVariable("standardMsg", standardMsg, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,300,9625,21, this);
					if (!egl.debugg) egl.leave();
					return standardMsg;
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"normalFloat": function(afloat, mantissa, sign) {
				try { egl.enter("normalFloat",this,arguments);
					egl.addLocalFunctionVariable("afloat", afloat, "eglx.lang.EFloat", "afloat");
					egl.addLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt", "mantissa");
					egl.addLocalFunctionVariable("sign", sign, "eglx.lang.EString", "sign");
					egl.atLine(this.eze$$fileName,305,9778,13, this);
					mantissa.ezeCopy(0);
					egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
					egl.atLine(this.eze$$fileName,306,9801,127, this);
					if (((afloat >= 0))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,307,9830,11, this);
							sign.ezeCopy("+");
							egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,309,9869,11, this);
							sign.ezeCopy("-");
							egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,310,9894,21, this);
							afloat = ((afloat * -1));
							egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.EFloat");
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,312,9938,306, this);
					if ((((egl.eglx.lang.NullType.notEquals(afloat, null)) && ((afloat != 0))))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,313,9985,115, this);
							while (((afloat < 1))) {
								try{egl.enterBlock();
									egl.atLine(this.eze$$fileName,314,10020,21, this);
									afloat = ((afloat * 10));
									egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.EFloat");
									egl.atLine(this.eze$$fileName,315,10059,24, this);
									mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) - 1)));
									egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
								}finally{egl.exitBlock();}
								egl.atLine(this.eze$$fileName,313,9985,115, this);
							}
							egl.atLine(this.eze$$fileName,317,10114,117, this);
							while (((afloat >= 10))) {
								try{egl.enterBlock();
									egl.atLine(this.eze$$fileName,318,10151,21, this);
									afloat = (egl.divide(afloat ,10));
									egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.EFloat");
									egl.atLine(this.eze$$fileName,319,10190,24, this);
									mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) + 1)));
									egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
								}finally{egl.exitBlock();}
								egl.atLine(this.eze$$fileName,317,10114,117, this);
							}
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,322,10254,15, this);
					if (!egl.debugg) egl.leave();
					return afloat;
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"normalSmallFloat": function(afloat, mantissa, sign) {
				try { egl.enter("normalSmallFloat",this,arguments);
					egl.addLocalFunctionVariable("afloat", afloat, "eglx.lang.ESmallfloat", "afloat");
					egl.addLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt", "mantissa");
					egl.addLocalFunctionVariable("sign", sign, "eglx.lang.EString", "sign");
					egl.atLine(this.eze$$fileName,326,10409,13, this);
					mantissa.ezeCopy(0);
					egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
					egl.atLine(this.eze$$fileName,327,10432,127, this);
					if (((egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEFloat32, [afloat]) >= 0))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,328,10461,11, this);
							sign.ezeCopy("+");
							egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,330,10500,11, this);
							sign.ezeCopy("-");
							egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,331,10525,21, this);
							afloat = egl.eglx.lang.convert(egl.eglx.lang.EFloat32.fromEInt32, [((egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEFloat32, [afloat]) * -1))]);
							egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.ESmallfloat");
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,333,10569,306, this);
					if ((((egl.eglx.lang.NullType.notEquals(afloat, null)) && ((egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEFloat32, [afloat]) != 0))))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,334,10616,115, this);
							while (((egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEFloat32, [afloat]) < 1))) {
								try{egl.enterBlock();
									egl.atLine(this.eze$$fileName,335,10651,21, this);
									afloat = egl.eglx.lang.convert(egl.eglx.lang.EFloat32.fromEInt32, [((egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEFloat32, [afloat]) * 10))]);
									egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.ESmallfloat");
									egl.atLine(this.eze$$fileName,336,10690,24, this);
									mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) - 1)));
									egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
								}finally{egl.exitBlock();}
								egl.atLine(this.eze$$fileName,334,10616,115, this);
							}
							egl.atLine(this.eze$$fileName,338,10745,117, this);
							while (((egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEFloat32, [afloat]) >= 10))) {
								try{egl.enterBlock();
									egl.atLine(this.eze$$fileName,339,10782,21, this);
									afloat = egl.eglx.lang.convert(egl.eglx.lang.EFloat32.fromEDecimal, [(new egl.javascript.BigDecimal(egl.divide(egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEFloat32, [afloat]),10))),"d;"]);
									egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.ESmallfloat");
									egl.atLine(this.eze$$fileName,340,10821,24, this);
									mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) + 1)));
									egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
								}finally{egl.exitBlock();}
								egl.atLine(this.eze$$fileName,338,10745,117, this);
							}
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,343,10885,15, this);
					if (!egl.debugg) egl.leave();
					return afloat;
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"getOutR": function() {
				return outR;
			}
			,
			"setOutR": function(ezeValue) {
				this.outR = ezeValue;
			}
			,
			"getS": function() {
				return s;
			}
			,
			"setS": function(ezeValue) {
				this.s = ezeValue;
			}
			,
			"getACTUALHEADER": function() {
				return ACTUALHEADER;
			}
			,
			"getEXPECTEDHEADER": function() {
				return EXPECTEDHEADER;
			}
			,
			"getACTUALSIZEHEADER": function() {
				return ACTUALSIZEHEADER;
			}
			,
			"getEXPECTEDSIZEHEADER": function() {
				return EXPECTEDSIZEHEADER;
			}
			,
			"toString": function() {
				return "[LogResult]";
			}
			,
			"eze$$getName": function() {
				return "LogResult";
			}
			,
			"eze$$getChildVariables": function() {
				var eze$$parent = this;
				return [
				{name: "ConstantsLib", value : egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'], type : "org.eclipse.edt.eunit.runtime.ConstantsLib", jsName : "egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']"},
				{name: "outR", value : eze$$parent.outR, type : "org.eclipse.edt.eunit.runtime.Log", jsName : "outR"},
				{name: "s", value : eze$$parent.s, type : "org.eclipse.edt.eunit.runtime.Status", jsName : "s"},
				{name: "ACTUALHEADER", value : eze$$parent.ACTUALHEADER, type : "eglx.lang.EString", jsName : "ACTUALHEADER"},
				{name: "EXPECTEDHEADER", value : eze$$parent.EXPECTEDHEADER, type : "eglx.lang.EString", jsName : "EXPECTEDHEADER"},
				{name: "ACTUALSIZEHEADER", value : eze$$parent.ACTUALSIZEHEADER, type : "eglx.lang.EString", jsName : "ACTUALSIZEHEADER"},
				{name: "EXPECTEDSIZEHEADER", value : eze$$parent.EXPECTEDSIZEHEADER, type : "eglx.lang.EString", jsName : "EXPECTEDSIZEHEADER"}
				];
			}
		}
	);
});
