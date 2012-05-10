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
			egl.atLine(this.eze$$fileName,42,1061,17, this);
			this.ACTUALHEADER = "Actual value = ";
			this.EXPECTEDHEADER = "";
			egl.atLine(this.eze$$fileName,43,1123,19, this);
			this.EXPECTEDHEADER = "Expected value = ";
			this.ACTUALSIZEHEADER = "";
			egl.atLine(this.eze$$fileName,44,1189,22, this);
			this.ACTUALSIZEHEADER = "Actual array size = ";
			this.EXPECTEDSIZEHEADER = "";
			egl.atLine(this.eze$$fileName,45,1260,24, this);
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
				egl.atLine(this.eze$$fileName,48,1318,14, this);
				this.outR.msg = "";
				egl.atLine(this.eze$$fileName,49,1336,12, this);
				this.s.code = -1;
				egl.atLine(this.eze$$fileName,50,1352,14, this);
				this.s.reason = "";
				egl.atLine(this.eze$$fileName,47,1291,81, this);
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
		}
		,
		"getStatus": function() {
			try { egl.enter("getStatus",this,arguments);
				egl.atLine(this.eze$$fileName,54,1419,11, this);
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
				egl.atLine(this.eze$$fileName,58,1476,14, this);
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
				egl.atLine(this.eze$$fileName,65,1578,33, this);
				this.outR.msg = ((this.outR.msg) + egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE);
				egl.atLine(this.eze$$fileName,66,1615,19, this);
				this.outR.msg = ((this.outR.msg) + logmsg);
				egl.atLine(this.eze$$fileName,64,1538,104, this);
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
				egl.atLine(this.eze$$fileName,73,1728,30, this);
				this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SPASSED;
				egl.atLine(this.eze$$fileName,74,1762,51, this);
				if ((((egl.eglx.lang.NullType.equals(str, null)) || ((str) == "")))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,75,1795,11, this);
						str = "OK";
						egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,77,1817,15, this);
				this.s.reason = str;
				egl.atLine(this.eze$$fileName,72,1694,144, this);
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
				egl.atLine(this.eze$$fileName,81,1878,30, this);
				this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SFAILED;
				egl.atLine(this.eze$$fileName,82,1912,24, this);
				str = (("FAILED - ") + str);
				egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,83,1940,15, this);
				this.s.reason = str;
				egl.atLine(this.eze$$fileName,80,1844,117, this);
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
				egl.atLine(this.eze$$fileName,88,2041,29, this);
				this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SERROR;
				egl.atLine(this.eze$$fileName,89,2074,23, this);
				str = (("ERROR - ") + str);
				egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,90,2101,15, this);
				this.s.reason = str;
				egl.atLine(this.eze$$fileName,87,2008,114, this);
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
				egl.atLine(this.eze$$fileName,94,2163,31, this);
				this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SNOT_RUN;
				egl.atLine(this.eze$$fileName,95,2198,25, this);
				str = (("SKIPPED - ") + str);
				egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,96,2227,15, this);
				this.s.reason = str;
				egl.atLine(this.eze$$fileName,93,2128,120, this);
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
				egl.atLine(this.eze$$fileName,100,2376,173, this);
				if (testCondition) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,101,2398,13, this);
						this.passed("OK");
					}finally{egl.exitBlock();}
				}
				else {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,103,2424,21, this);
						this.failed(failedReason);
						egl.atLine(this.eze$$fileName,104,2450,92, this);
						if (throwsFailException) {
							try{egl.enterBlock();
								var eze$LNNTemp4 = null;
								{
									var eze$SettingTarget1;
									egl.atLine(this.eze$$fileName,105,2485,48, this);
									eze$SettingTarget1 = new egl.org.eclipse.edt.eunit.runtime.AssertionFailedException();
									egl.atLine(this.eze$$fileName,105,2514,18, this);
									egl.checkNull(eze$SettingTarget1).message = this.s.reason;
									egl.atLine(this.eze$$fileName,105,2485,48, this);
									eze$LNNTemp4 = eze$SettingTarget1;
								}
								egl.atLine(this.eze$$fileName,105,2479,55, this);
								throw eze$LNNTemp4;
							}finally{egl.exitBlock();}
						}
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,99,2254,301, this);
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
				egl.atLine(this.eze$$fileName,120,3084,55, this);
				this.assertTrueException(failedReason, testCondition, true);
				egl.atLine(this.eze$$fileName,119,3011,134, this);
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
				egl.atLine(this.eze$$fileName,128,3407,30, this);
				this.assertTrue("", testCondition);
				egl.atLine(this.eze$$fileName,127,3357,86, this);
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
				egl.atLine(this.eze$$fileName,136,3667,20, this);
				isEqual = (egl.eglx.lang.EInt64.equals(expected,actual));
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,137,3692,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"B;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"B;"]), isEqual);
				egl.atLine(this.eze$$fileName,135,3562,189, this);
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
				egl.atLine(this.eze$$fileName,141,3826,40, this);
				this.assertBigIntEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,140,3757,115, this);
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
				egl.atLine(this.eze$$fileName,145,3985,20, this);
				isEqual = ((expected) == actual);
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,146,4010,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"S;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"S;"]), isEqual);
				egl.atLine(this.eze$$fileName,144,3880,189, this);
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
				egl.atLine(this.eze$$fileName,150,4144,40, this);
				this.assertStringEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,149,4075,115, this);
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
				egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EList<eglx.lang.EString>", "expected");
				egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EList<eglx.lang.EString>", "actual");
				var isArrayEqual;
				egl.addLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean", "isArrayEqual");
				egl.atLine(this.eze$$fileName,154,4317,4, this);
				isArrayEqual = true;
				egl.setLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean");
				var expectedSize;
				egl.addLocalFunctionVariable("expectedSize", expectedSize, "eglx.lang.EInt", "expectedSize");
				egl.atLine(this.eze$$fileName,155,4345,18, this);
				expectedSize = expected.getSize();
				egl.setLocalFunctionVariable("expectedSize", expectedSize, "eglx.lang.EInt");
				var actualSize;
				egl.addLocalFunctionVariable("actualSize", actualSize, "eglx.lang.EInt", "actualSize");
				egl.atLine(this.eze$$fileName,156,4387,16, this);
				actualSize = actual.getSize();
				egl.setLocalFunctionVariable("actualSize", actualSize, "eglx.lang.EInt");
				var failedReason = "";
				egl.addLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString", "failedReason");
				egl.atLine(this.eze$$fileName,159,4434,904, this);
				if (((expectedSize == actualSize))) {
					try{egl.enterBlock();
						var failedHeader;
						egl.addLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString", "failedHeader");
						egl.atLine(this.eze$$fileName,160,4491,20, this);
						failedHeader = "Array element No.[";
						egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
						var expectedValues;
						egl.addLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString", "expectedValues");
						egl.atLine(this.eze$$fileName,161,4541,20, this);
						expectedValues = ((this.EXPECTEDHEADER) + "[");
						egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
						var actualValues;
						egl.addLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString", "actualValues");
						egl.atLine(this.eze$$fileName,162,4589,18, this);
						actualValues = ((this.ACTUALHEADER) + "[");
						egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,163,4613,395, this);
						{
							try{egl.enterBlock();
								var i = 0;
								egl.addLocalFunctionVariable("i", i, "eglx.lang.EInt", "i");
								for (i = 1; ((i <= expectedSize)); i = ((i + 1))) {
									egl.setLocalFunctionVariable("i", i, "eglx.lang.EInt");
									try{egl.enterBlock();
										egl.atLine(this.eze$$fileName,164,4652,177, this);
										if (((egl.checkNull(expected, "expected")[expected.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]) != egl.checkNull(actual, "actual")[actual.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)])) {
											try{egl.enterBlock();
												egl.atLine(this.eze$$fileName,165,4687,56, this);
												if (!(isArrayEqual)) {
													try{egl.enterBlock();
														egl.atLine(this.eze$$fileName,166,4712,21, this);
														failedHeader = ((failedHeader) + ", ");
														egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
													}finally{egl.exitBlock();}
												}
												egl.atLine(this.eze$$fileName,168,4751,21, this);
												isArrayEqual = false;
												egl.setLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean");
												egl.atLine(this.eze$$fileName,169,4784,28, this);
												failedHeader = ((failedHeader) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32, [i]));
												egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
											}finally{egl.exitBlock();}
										}
										egl.atLine(this.eze$$fileName,172,4844,30, this);
										expectedValues = ((expectedValues) + egl.checkNull(expected, "expected")[expected.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]);
										egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
										egl.atLine(this.eze$$fileName,173,4880,26, this);
										actualValues = ((actualValues) + egl.checkNull(actual, "actual")[actual.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]);
										egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
										egl.atLine(this.eze$$fileName,174,4912,88, this);
										if (((i != expectedSize))) {
											try{egl.enterBlock();
												egl.atLine(this.eze$$fileName,175,4940,23, this);
												expectedValues = ((expectedValues) + ", ");
												egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
												egl.atLine(this.eze$$fileName,176,4970,21, this);
												actualValues = ((actualValues) + ", ");
												egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
											}finally{egl.exitBlock();}
										}
									}finally{egl.exitBlock();}
									egl.atLine(this.eze$$fileName,163,4613,395, this);
								}
							}
							finally{egl.exitBlock();}
						}
						egl.atLine(this.eze$$fileName,179,5013,30, this);
						failedHeader = ((failedHeader) + "] differs; ");
						egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,180,5048,24, this);
						expectedValues = ((expectedValues) + "]; ");
						egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,181,5077,21, this);
						actualValues = ((actualValues) + "] ");
						egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,182,5103,60, this);
						failedReason = ((((failedHeader) + expectedValues)) + actualValues);
						egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				else {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,184,5176,21, this);
						isArrayEqual = false;
						egl.setLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean");
						egl.atLine(this.eze$$fileName,185,5203,120, this);
						failedReason = (((((((((((((((("Failed: ") + this.EXPECTEDSIZEHEADER)) + "'")) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32, [expectedSize]))) + "' ")) + this.ACTUALSIZEHEADER)) + "'")) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32, [actualSize]))) + "' ");
						egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,187,5342,95, this);
				if ((((egl.eglx.lang.NullType.notEquals(message, null)) && ((message) != "")))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,187,5378,1, this);
						;
						egl.atLine(this.eze$$fileName,188,5384,46, this);
						failedReason = ((((message) + " - ")) + failedReason);
						egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,190,5441,39, this);
				this.assertTrue(failedReason, isArrayEqual);
				egl.atLine(this.eze$$fileName,153,4198,1288, this);
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
		}
		,
		"assertStringArrayEqual1": function(expected, actual) {
			try { egl.enter("assertStringArrayEqual1",this,arguments);
				egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EList<eglx.lang.EString>", "expected");
				egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EList<eglx.lang.EString>", "actual");
				egl.atLine(this.eze$$fileName,194,5571,45, this);
				this.assertStringArrayEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,193,5493,129, this);
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
				egl.atLine(this.eze$$fileName,198,5728,20, this);
				isEqual = (egl.eglx.lang.EDate.equals(expected, actual));
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,199,5753,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"K;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"K;"]), isEqual);
				egl.atLine(this.eze$$fileName,197,5629,183, this);
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
				egl.atLine(this.eze$$fileName,203,5881,38, this);
				this.assertDateEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,202,5818,107, this);
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
				egl.atLine(this.eze$$fileName,207,6030,20, this);
				isEqual = (egl.eglx.lang.ETime.equals(expected, actual));
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,208,6055,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"L;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"L;"]), isEqual);
				egl.atLine(this.eze$$fileName,206,5931,183, this);
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
				egl.atLine(this.eze$$fileName,212,6185,38, this);
				this.assertTimeEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,211,6122,107, this);
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
				egl.atLine(this.eze$$fileName,216,6350,20, this);
				isEqual = (egl.eglx.lang.ETimestamp.equals(expected, actual));
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,217,6375,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"yyyyMMddhhmmss","J;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"yyyyMMddhhmmss","J;"]), isEqual);
				egl.atLine(this.eze$$fileName,215,6236,200, this);
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
				egl.atLine(this.eze$$fileName,221,6520,43, this);
				this.assertTimestampEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,220,6442,127, this);
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
				egl.atLine(this.eze$$fileName,225,6685,20, this);
				isEqual = (egl.eglx.lang.EDecimal.equals(expected,actual));
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,226,6710,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"d;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"d;"]), isEqual);
				egl.atLine(this.eze$$fileName,224,6577,196, this);
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
				egl.atLine(this.eze$$fileName,230,6851,41, this);
				this.assertDecimalEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,229,6779,119, this);
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
				egl.atLine(this.eze$$fileName,234,7007,30, this);
				isEqual = this.isFloatEqual(expected, actual);
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,235,7042,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"F;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"F;"]), isEqual);
				egl.atLine(this.eze$$fileName,233,6905,196, this);
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
				egl.atLine(this.eze$$fileName,239,7173,39, this);
				this.assertFloatEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,238,7107,111, this);
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
				egl.atLine(this.eze$$fileName,247,7480,5, this);
				deltaLimit = 1E-14;
				egl.setLocalFunctionVariable("deltaLimit", deltaLimit, "eglx.lang.EFloat");
				var eze$Temp11 = 0;
				var eze$Temp12;
				egl.atLine(this.eze$$fileName,248,7507,53, this);
				eze$Temp12 = egl.eglx.lang.EAny.ezeWrap(eze$Temp11);
				var eze$Temp13 = "";
				var eze$Temp14;
				egl.atLine(this.eze$$fileName,248,7507,53, this);
				eze$Temp14 = egl.eglx.lang.EAny.ezeWrap(eze$Temp13);
				egl.atLine(this.eze$$fileName,248,7490,71, this);
				normalExpected = this.normalFloat(expected, eze$Temp12, eze$Temp14);
				egl.setLocalFunctionVariable("normalExpected", normalExpected, "eglx.lang.EFloat");
				egl.atLine(this.eze$$fileName,248,7507,53, this);
				mantissaExpected = eze$Temp12.ezeUnbox();
				egl.setLocalFunctionVariable("mantissaExpected", mantissaExpected, "eglx.lang.EInt");
				egl.atLine(this.eze$$fileName,248,7507,53, this);
				signExpected = eze$Temp14.ezeUnbox();
				egl.setLocalFunctionVariable("signExpected", signExpected, "eglx.lang.EString");
				var eze$Temp15 = 0;
				var eze$Temp16;
				egl.atLine(this.eze$$fileName,249,7580,47, this);
				eze$Temp16 = egl.eglx.lang.EAny.ezeWrap(eze$Temp15);
				var eze$Temp17 = "";
				var eze$Temp18;
				egl.atLine(this.eze$$fileName,249,7580,47, this);
				eze$Temp18 = egl.eglx.lang.EAny.ezeWrap(eze$Temp17);
				egl.atLine(this.eze$$fileName,249,7565,63, this);
				normalActual = this.normalFloat(actual, eze$Temp16, eze$Temp18);
				egl.setLocalFunctionVariable("normalActual", normalActual, "eglx.lang.EFloat");
				egl.atLine(this.eze$$fileName,249,7580,47, this);
				mantissaActual = eze$Temp16.ezeUnbox();
				egl.setLocalFunctionVariable("mantissaActual", mantissaActual, "eglx.lang.EInt");
				egl.atLine(this.eze$$fileName,249,7580,47, this);
				signActual = eze$Temp18.ezeUnbox();
				egl.setLocalFunctionVariable("signActual", signActual, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,250,7632,38, this);
				delta = ((normalExpected - normalActual));
				egl.setLocalFunctionVariable("delta", delta, "eglx.lang.EFloat");
				egl.atLine(this.eze$$fileName,251,7674,27, this);
				delta = egl.eglx.lang.MathLib.abs(delta);
				egl.setLocalFunctionVariable("delta", delta, "eglx.lang.EFloat");
				var isEqual;
				egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
				egl.atLine(this.eze$$fileName,252,7723,105, this);
				isEqual = ((((((signExpected) == signActual) && ((mantissaExpected == mantissaActual)))) && ((delta < deltaLimit))));
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,256,7840,16, this);
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
				egl.atLine(this.eze$$fileName,260,7989,35, this);
				isEqual = this.isSmallFloatEqual(expected, actual);
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,261,8029,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"f;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"f;"]), isEqual);
				egl.atLine(this.eze$$fileName,259,7872,216, this);
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
				egl.atLine(this.eze$$fileName,265,8175,44, this);
				this.assertSmallFloatEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,264,8094,131, this);
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
				egl.atLine(this.eze$$fileName,273,8507,4, this);
				deltaLimit = 1E-4;
				egl.setLocalFunctionVariable("deltaLimit", deltaLimit, "eglx.lang.EFloat");
				var eze$Temp19 = 0;
				var eze$Temp20;
				egl.atLine(this.eze$$fileName,274,8533,58, this);
				eze$Temp20 = egl.eglx.lang.EAny.ezeWrap(eze$Temp19);
				var eze$Temp21 = "";
				var eze$Temp22;
				egl.atLine(this.eze$$fileName,274,8533,58, this);
				eze$Temp22 = egl.eglx.lang.EAny.ezeWrap(eze$Temp21);
				egl.atLine(this.eze$$fileName,274,8516,76, this);
				normalExpected = this.normalSmallFloat(expected, eze$Temp20, eze$Temp22);
				egl.setLocalFunctionVariable("normalExpected", normalExpected, "eglx.lang.ESmallfloat");
				egl.atLine(this.eze$$fileName,274,8533,58, this);
				mantissaExpected = eze$Temp20.ezeUnbox();
				egl.setLocalFunctionVariable("mantissaExpected", mantissaExpected, "eglx.lang.EInt");
				egl.atLine(this.eze$$fileName,274,8533,58, this);
				signExpected = eze$Temp22.ezeUnbox();
				egl.setLocalFunctionVariable("signExpected", signExpected, "eglx.lang.EString");
				var eze$Temp23 = 0;
				var eze$Temp24;
				egl.atLine(this.eze$$fileName,275,8611,52, this);
				eze$Temp24 = egl.eglx.lang.EAny.ezeWrap(eze$Temp23);
				var eze$Temp25 = "";
				var eze$Temp26;
				egl.atLine(this.eze$$fileName,275,8611,52, this);
				eze$Temp26 = egl.eglx.lang.EAny.ezeWrap(eze$Temp25);
				egl.atLine(this.eze$$fileName,275,8596,68, this);
				normalActual = this.normalSmallFloat(actual, eze$Temp24, eze$Temp26);
				egl.setLocalFunctionVariable("normalActual", normalActual, "eglx.lang.ESmallfloat");
				egl.atLine(this.eze$$fileName,275,8611,52, this);
				mantissaActual = eze$Temp24.ezeUnbox();
				egl.setLocalFunctionVariable("mantissaActual", mantissaActual, "eglx.lang.EInt");
				egl.atLine(this.eze$$fileName,275,8611,52, this);
				signActual = eze$Temp26.ezeUnbox();
				egl.setLocalFunctionVariable("signActual", signActual, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,276,8668,38, this);
				delta = ((normalExpected - normalActual));
				egl.setLocalFunctionVariable("delta", delta, "eglx.lang.ESmallfloat");
				var eze$Temp27;
				egl.atLine(this.eze$$fileName,277,8718,18, this);
				eze$Temp27 = delta;
				egl.atLine(this.eze$$fileName,277,8710,27, this);
				delta = egl.eglx.lang.convert(egl.eglx.lang.EFloat32.fromEFloat64, [egl.eglx.lang.MathLib.abs(eze$Temp27)]);
				egl.setLocalFunctionVariable("delta", delta, "eglx.lang.ESmallfloat");
				var isEqual;
				egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
				egl.atLine(this.eze$$fileName,278,8759,105, this);
				isEqual = ((((((signExpected) == signActual) && ((mantissaExpected == mantissaActual)))) && ((delta < deltaLimit))));
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,282,8876,16, this);
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
				egl.atLine(this.eze$$fileName,286,9038,44, this);
				failedReason = this.buildFailedReason(message, expected, actual);
				egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,287,9087,34, this);
				this.assertTrue(failedReason, isEqual);
				egl.atLine(this.eze$$fileName,285,8908,221, this);
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
				egl.atLine(this.eze$$fileName,291,9263,24, this);
				failedReason = this.expect(expected, actual);
				egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,292,9292,95, this);
				if ((((egl.eglx.lang.NullType.notEquals(message, null)) && ((message) != "")))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,293,9334,46, this);
						failedReason = ((((message) + " - ")) + failedReason);
						egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,295,9391,22, this);
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
				egl.atLine(this.eze$$fileName,299,9530,88, this);
				standardMsg = (((((((((((((((("Failed: ") + this.EXPECTEDHEADER)) + "'")) + egl.eglx.lang.EString.ezeCast({eze$$value : expected, eze$$signature : egl.inferSignature(expected)}, false))) + "' ")) + this.ACTUALHEADER)) + "'")) + egl.eglx.lang.EString.ezeCast({eze$$value : actual, eze$$signature : egl.inferSignature(actual)}, false))) + "' ");
				egl.setLocalFunctionVariable("standardMsg", standardMsg, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,300,9629,21, this);
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
				egl.atLine(this.eze$$fileName,305,9782,13, this);
				mantissa.ezeCopy(0);
				egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
				egl.atLine(this.eze$$fileName,306,9805,127, this);
				if (((afloat >= 0))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,307,9834,11, this);
						sign.ezeCopy("+");
						egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				else {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,309,9873,11, this);
						sign.ezeCopy("-");
						egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,310,9898,21, this);
						afloat = ((afloat * -1));
						egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.EFloat");
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,312,9942,306, this);
				if ((((egl.eglx.lang.NullType.notEquals(afloat, null)) && ((afloat != 0))))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,313,9989,115, this);
						while (((afloat < 1))) {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,314,10024,21, this);
								afloat = ((afloat * 10));
								egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.EFloat");
								egl.atLine(this.eze$$fileName,315,10063,24, this);
								mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) - 1)));
								egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
							}finally{egl.exitBlock();}
							egl.atLine(this.eze$$fileName,313,9989,115, this);
						}
						egl.atLine(this.eze$$fileName,317,10118,117, this);
						while (((afloat >= 10))) {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,318,10155,21, this);
								afloat = (egl.divide(afloat ,10));
								egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.EFloat");
								egl.atLine(this.eze$$fileName,319,10194,24, this);
								mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) + 1)));
								egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
							}finally{egl.exitBlock();}
							egl.atLine(this.eze$$fileName,317,10118,117, this);
						}
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,322,10258,15, this);
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
				egl.atLine(this.eze$$fileName,326,10413,13, this);
				mantissa.ezeCopy(0);
				egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
				egl.atLine(this.eze$$fileName,327,10436,127, this);
				if (((egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEFloat32, [afloat]) >= 0))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,328,10465,11, this);
						sign.ezeCopy("+");
						egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				else {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,330,10504,11, this);
						sign.ezeCopy("-");
						egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,331,10529,21, this);
						afloat = egl.eglx.lang.convert(egl.eglx.lang.EFloat32.fromEInt32, [((egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEFloat32, [afloat]) * -1))]);
						egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.ESmallfloat");
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,333,10573,306, this);
				if ((((egl.eglx.lang.NullType.notEquals(afloat, null)) && ((egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEFloat32, [afloat]) != 0))))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,334,10620,115, this);
						while (((egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEFloat32, [afloat]) < 1))) {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,335,10655,21, this);
								afloat = egl.eglx.lang.convert(egl.eglx.lang.EFloat32.fromEInt32, [((egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEFloat32, [afloat]) * 10))]);
								egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.ESmallfloat");
								egl.atLine(this.eze$$fileName,336,10694,24, this);
								mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) - 1)));
								egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
							}finally{egl.exitBlock();}
							egl.atLine(this.eze$$fileName,334,10620,115, this);
						}
						egl.atLine(this.eze$$fileName,338,10749,117, this);
						while (((egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEFloat32, [afloat]) >= 10))) {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,339,10786,21, this);
								afloat = (new egl.javascript.BigDecimal(egl.divide(egl.eglx.lang.convert(egl.eglx.lang.EInt32.fromEFloat32, [afloat]),10)));
								egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.ESmallfloat");
								egl.atLine(this.eze$$fileName,340,10825,24, this);
								mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) + 1)));
								egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
							}finally{egl.exitBlock();}
							egl.atLine(this.eze$$fileName,338,10749,117, this);
						}
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,343,10889,15, this);
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
