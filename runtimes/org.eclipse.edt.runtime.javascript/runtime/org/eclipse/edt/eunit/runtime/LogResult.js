/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
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
			egl.atLine(this.eze$$fileName,31,514,17, this);
			this.ACTUALHEADER = "Actual value = ";
			this.EXPECTEDHEADER = "";
			egl.atLine(this.eze$$fileName,32,576,19, this);
			this.EXPECTEDHEADER = "Expected value = ";
			this.ACTUALSIZEHEADER = "";
			egl.atLine(this.eze$$fileName,33,642,22, this);
			this.ACTUALSIZEHEADER = "Actual array size = ";
			this.EXPECTEDSIZEHEADER = "";
			egl.atLine(this.eze$$fileName,34,713,24, this);
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
				egl.atLine(this.eze$$fileName,37,771,14, this);
				this.outR.msg = "";
				egl.atLine(this.eze$$fileName,38,789,12, this);
				this.s.code = -1;
				egl.atLine(this.eze$$fileName,39,805,14, this);
				this.s.reason = "";
				egl.atLine(this.eze$$fileName,36,744,81, this);
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
		}
		,
		"getStatus": function() {
			try { egl.enter("getStatus",this,arguments);
				egl.atLine(this.eze$$fileName,43,872,11, this);
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
				egl.atLine(this.eze$$fileName,47,929,14, this);
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
				egl.atLine(this.eze$$fileName,54,1031,33, this);
				this.outR.msg = ((this.outR.msg) + egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE);
				egl.atLine(this.eze$$fileName,55,1068,19, this);
				this.outR.msg = ((this.outR.msg) + logmsg);
				egl.atLine(this.eze$$fileName,53,991,104, this);
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
				egl.atLine(this.eze$$fileName,62,1181,30, this);
				this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SPASSED;
				egl.atLine(this.eze$$fileName,63,1215,51, this);
				if ((((egl.eglx.lang.NullType.equals(str, null)) || ((str) == "")))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,64,1248,11, this);
						str = "OK";
						egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,66,1270,15, this);
				this.s.reason = str;
				egl.atLine(this.eze$$fileName,61,1147,144, this);
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
				egl.atLine(this.eze$$fileName,70,1331,30, this);
				this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SFAILED;
				egl.atLine(this.eze$$fileName,71,1365,24, this);
				str = (("FAILED - ") + str);
				egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,72,1393,15, this);
				this.s.reason = str;
				egl.atLine(this.eze$$fileName,69,1297,117, this);
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
				egl.atLine(this.eze$$fileName,77,1494,29, this);
				this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SERROR;
				egl.atLine(this.eze$$fileName,78,1527,23, this);
				str = (("ERROR - ") + str);
				egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,79,1554,15, this);
				this.s.reason = str;
				egl.atLine(this.eze$$fileName,76,1461,114, this);
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
				egl.atLine(this.eze$$fileName,83,1616,31, this);
				this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SNOT_RUN;
				egl.atLine(this.eze$$fileName,84,1651,25, this);
				str = (("SKIPPED - ") + str);
				egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,85,1680,15, this);
				this.s.reason = str;
				egl.atLine(this.eze$$fileName,82,1581,120, this);
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
				egl.atLine(this.eze$$fileName,89,1829,173, this);
				if (testCondition) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,90,1851,13, this);
						this.passed("OK");
					}finally{egl.exitBlock();}
				}
				else {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,92,1877,21, this);
						this.failed(failedReason);
						egl.atLine(this.eze$$fileName,93,1903,92, this);
						if (throwsFailException) {
							try{egl.enterBlock();
								var eze$LNNTemp4 = null;
								{
									var eze$SettingTarget1;
									egl.atLine(this.eze$$fileName,94,1938,48, this);
									eze$SettingTarget1 = new egl.org.eclipse.edt.eunit.runtime.AssertionFailedException();
									egl.atLine(this.eze$$fileName,94,1967,18, this);
									egl.checkNull(eze$SettingTarget1).message = this.s.reason;
									egl.atLine(this.eze$$fileName,94,1938,48, this);
									eze$LNNTemp4 = eze$SettingTarget1;
								}
								egl.atLine(this.eze$$fileName,94,1932,55, this);
								throw eze$LNNTemp4;
							}finally{egl.exitBlock();}
						}
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,88,1707,301, this);
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
				egl.atLine(this.eze$$fileName,109,2537,55, this);
				this.assertTrueException(failedReason, testCondition, true);
				egl.atLine(this.eze$$fileName,108,2464,134, this);
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
				egl.atLine(this.eze$$fileName,117,2860,30, this);
				this.assertTrue("", testCondition);
				egl.atLine(this.eze$$fileName,116,2810,86, this);
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
				egl.atLine(this.eze$$fileName,125,3120,20, this);
				isEqual = (egl.eglx.lang.EInt64.equals(expected,actual));
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,126,3145,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"B;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"B;"]), isEqual);
				egl.atLine(this.eze$$fileName,124,3015,189, this);
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
				egl.atLine(this.eze$$fileName,130,3279,40, this);
				this.assertBigIntEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,129,3210,115, this);
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
				egl.atLine(this.eze$$fileName,134,3438,20, this);
				isEqual = ((expected) == actual);
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,135,3463,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"S;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"S;"]), isEqual);
				egl.atLine(this.eze$$fileName,133,3333,189, this);
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
				egl.atLine(this.eze$$fileName,139,3597,40, this);
				this.assertStringEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,138,3528,115, this);
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
				egl.atLine(this.eze$$fileName,143,3770,4, this);
				isArrayEqual = true;
				egl.setLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean");
				var expectedSize;
				egl.addLocalFunctionVariable("expectedSize", expectedSize, "eglx.lang.EInt", "expectedSize");
				egl.atLine(this.eze$$fileName,144,3798,18, this);
				expectedSize = expected.getSize();
				egl.setLocalFunctionVariable("expectedSize", expectedSize, "eglx.lang.EInt");
				var actualSize;
				egl.addLocalFunctionVariable("actualSize", actualSize, "eglx.lang.EInt", "actualSize");
				egl.atLine(this.eze$$fileName,145,3840,16, this);
				actualSize = actual.getSize();
				egl.setLocalFunctionVariable("actualSize", actualSize, "eglx.lang.EInt");
				var failedReason = "";
				egl.addLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString", "failedReason");
				egl.atLine(this.eze$$fileName,148,3887,904, this);
				if (((expectedSize == actualSize))) {
					try{egl.enterBlock();
						var failedHeader;
						egl.addLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString", "failedHeader");
						egl.atLine(this.eze$$fileName,149,3944,20, this);
						failedHeader = "Array element No.[";
						egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
						var expectedValues;
						egl.addLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString", "expectedValues");
						egl.atLine(this.eze$$fileName,150,3994,20, this);
						expectedValues = ((this.EXPECTEDHEADER) + "[");
						egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
						var actualValues;
						egl.addLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString", "actualValues");
						egl.atLine(this.eze$$fileName,151,4042,18, this);
						actualValues = ((this.ACTUALHEADER) + "[");
						egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,152,4066,395, this);
						{
							try{egl.enterBlock();
								var i = 0;
								egl.addLocalFunctionVariable("i", i, "eglx.lang.EInt", "i");
								for (i = 1; ((i <= expectedSize)); i = ((i + 1))) {
									egl.setLocalFunctionVariable("i", i, "eglx.lang.EInt");
									try{egl.enterBlock();
										egl.atLine(this.eze$$fileName,153,4105,177, this);
										if (((egl.checkNull(expected, "expected")[expected.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]) != egl.checkNull(actual, "actual")[actual.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)])) {
											try{egl.enterBlock();
												egl.atLine(this.eze$$fileName,154,4140,56, this);
												if (!(isArrayEqual)) {
													try{egl.enterBlock();
														egl.atLine(this.eze$$fileName,155,4165,21, this);
														failedHeader = ((failedHeader) + ", ");
														egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
													}finally{egl.exitBlock();}
												}
												egl.atLine(this.eze$$fileName,157,4204,21, this);
												isArrayEqual = false;
												egl.setLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean");
												egl.atLine(this.eze$$fileName,158,4237,28, this);
												failedHeader = ((failedHeader) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32, [i]));
												egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
											}finally{egl.exitBlock();}
										}
										egl.atLine(this.eze$$fileName,161,4297,30, this);
										expectedValues = ((expectedValues) + egl.checkNull(expected, "expected")[expected.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]);
										egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
										egl.atLine(this.eze$$fileName,162,4333,26, this);
										actualValues = ((actualValues) + egl.checkNull(actual, "actual")[actual.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]);
										egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
										egl.atLine(this.eze$$fileName,163,4365,88, this);
										if (((i != expectedSize))) {
											try{egl.enterBlock();
												egl.atLine(this.eze$$fileName,164,4393,23, this);
												expectedValues = ((expectedValues) + ", ");
												egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
												egl.atLine(this.eze$$fileName,165,4423,21, this);
												actualValues = ((actualValues) + ", ");
												egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
											}finally{egl.exitBlock();}
										}
									}finally{egl.exitBlock();}
									egl.atLine(this.eze$$fileName,152,4066,395, this);
								}
							}
							finally{egl.exitBlock();}
						}
						egl.atLine(this.eze$$fileName,168,4466,30, this);
						failedHeader = ((failedHeader) + "] differs; ");
						egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,169,4501,24, this);
						expectedValues = ((expectedValues) + "]; ");
						egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,170,4530,21, this);
						actualValues = ((actualValues) + "] ");
						egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,171,4556,60, this);
						failedReason = ((((failedHeader) + expectedValues)) + actualValues);
						egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				else {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,173,4629,21, this);
						isArrayEqual = false;
						egl.setLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean");
						egl.atLine(this.eze$$fileName,174,4656,120, this);
						failedReason = (((((((((((((((("Failed: ") + this.EXPECTEDSIZEHEADER)) + "'")) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32, [expectedSize]))) + "' ")) + this.ACTUALSIZEHEADER)) + "'")) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32, [actualSize]))) + "' ");
						egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,176,4795,95, this);
				if ((((egl.eglx.lang.NullType.notEquals(message, null)) && ((message) != "")))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,176,4831,1, this);
						;
						egl.atLine(this.eze$$fileName,177,4837,46, this);
						failedReason = ((((message) + " - ")) + failedReason);
						egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,179,4894,39, this);
				this.assertTrue(failedReason, isArrayEqual);
				egl.atLine(this.eze$$fileName,142,3651,1288, this);
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
				egl.atLine(this.eze$$fileName,183,5024,45, this);
				this.assertStringArrayEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,182,4946,129, this);
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
				egl.atLine(this.eze$$fileName,187,5181,20, this);
				isEqual = (egl.eglx.lang.EDate.equals(expected, actual));
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,188,5206,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"K;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"K;"]), isEqual);
				egl.atLine(this.eze$$fileName,186,5082,183, this);
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
				egl.atLine(this.eze$$fileName,192,5334,38, this);
				this.assertDateEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,191,5271,107, this);
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
				egl.atLine(this.eze$$fileName,196,5499,20, this);
				isEqual = (egl.eglx.lang.ETimestamp.equals(expected, actual));
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,197,5524,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"yyyyMMddhhmmss","J;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"yyyyMMddhhmmss","J;"]), isEqual);
				egl.atLine(this.eze$$fileName,195,5385,200, this);
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
				egl.atLine(this.eze$$fileName,201,5669,43, this);
				this.assertTimestampEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,200,5591,127, this);
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
				egl.atLine(this.eze$$fileName,205,5834,20, this);
				isEqual = (egl.eglx.lang.EDecimal.equals(expected,actual));
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,206,5859,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"d;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"d;"]), isEqual);
				egl.atLine(this.eze$$fileName,204,5726,196, this);
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
				egl.atLine(this.eze$$fileName,210,6000,41, this);
				this.assertDecimalEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,209,5928,119, this);
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
				egl.atLine(this.eze$$fileName,214,6156,30, this);
				isEqual = this.isFloatEqual(expected, actual);
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,215,6191,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"F;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"F;"]), isEqual);
				egl.atLine(this.eze$$fileName,213,6054,196, this);
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
				egl.atLine(this.eze$$fileName,219,6322,39, this);
				this.assertFloatEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,218,6256,111, this);
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
				egl.atLine(this.eze$$fileName,227,6629,5, this);
				deltaLimit = 1E-14;
				egl.setLocalFunctionVariable("deltaLimit", deltaLimit, "eglx.lang.EFloat");
				var eze$Temp11 = 0;
				var eze$Temp12;
				egl.atLine(this.eze$$fileName,228,6656,53, this);
				eze$Temp12 = egl.eglx.lang.EAny.ezeWrap(eze$Temp11);
				var eze$Temp13 = "";
				var eze$Temp14;
				egl.atLine(this.eze$$fileName,228,6656,53, this);
				eze$Temp14 = egl.eglx.lang.EAny.ezeWrap(eze$Temp13);
				egl.atLine(this.eze$$fileName,228,6639,71, this);
				normalExpected = this.normalFloat(expected, eze$Temp12, eze$Temp14);
				egl.setLocalFunctionVariable("normalExpected", normalExpected, "eglx.lang.EFloat");
				egl.atLine(this.eze$$fileName,228,6656,53, this);
				mantissaExpected = eze$Temp12.ezeUnbox();
				egl.setLocalFunctionVariable("mantissaExpected", mantissaExpected, "eglx.lang.EInt");
				egl.atLine(this.eze$$fileName,228,6656,53, this);
				signExpected = eze$Temp14.ezeUnbox();
				egl.setLocalFunctionVariable("signExpected", signExpected, "eglx.lang.EString");
				var eze$Temp15 = 0;
				var eze$Temp16;
				egl.atLine(this.eze$$fileName,229,6729,47, this);
				eze$Temp16 = egl.eglx.lang.EAny.ezeWrap(eze$Temp15);
				var eze$Temp17 = "";
				var eze$Temp18;
				egl.atLine(this.eze$$fileName,229,6729,47, this);
				eze$Temp18 = egl.eglx.lang.EAny.ezeWrap(eze$Temp17);
				egl.atLine(this.eze$$fileName,229,6714,63, this);
				normalActual = this.normalFloat(actual, eze$Temp16, eze$Temp18);
				egl.setLocalFunctionVariable("normalActual", normalActual, "eglx.lang.EFloat");
				egl.atLine(this.eze$$fileName,229,6729,47, this);
				mantissaActual = eze$Temp16.ezeUnbox();
				egl.setLocalFunctionVariable("mantissaActual", mantissaActual, "eglx.lang.EInt");
				egl.atLine(this.eze$$fileName,229,6729,47, this);
				signActual = eze$Temp18.ezeUnbox();
				egl.setLocalFunctionVariable("signActual", signActual, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,230,6781,38, this);
				delta = ((normalExpected - normalActual));
				egl.setLocalFunctionVariable("delta", delta, "eglx.lang.EFloat");
				egl.atLine(this.eze$$fileName,231,6823,27, this);
				delta = egl.eglx.lang.MathLib.abs(delta);
				egl.setLocalFunctionVariable("delta", delta, "eglx.lang.EFloat");
				var isEqual;
				egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
				egl.atLine(this.eze$$fileName,232,6872,105, this);
				isEqual = ((((((signExpected) == signActual) && ((mantissaExpected == mantissaActual)))) && ((delta < deltaLimit))));
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,236,6989,16, this);
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
				egl.atLine(this.eze$$fileName,240,7138,35, this);
				isEqual = this.isSmallFloatEqual(expected, actual);
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,241,7178,53, this);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [expected,"f;"]), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny, [actual,"f;"]), isEqual);
				egl.atLine(this.eze$$fileName,239,7021,216, this);
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
				egl.atLine(this.eze$$fileName,245,7324,44, this);
				this.assertSmallFloatEqual("", expected, actual);
				egl.atLine(this.eze$$fileName,244,7243,131, this);
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
				egl.atLine(this.eze$$fileName,253,7656,4, this);
				deltaLimit = 1E-4;
				egl.setLocalFunctionVariable("deltaLimit", deltaLimit, "eglx.lang.EFloat");
				var eze$Temp19 = 0;
				var eze$Temp20;
				egl.atLine(this.eze$$fileName,254,7682,58, this);
				eze$Temp20 = egl.eglx.lang.EAny.ezeWrap(eze$Temp19);
				var eze$Temp21 = "";
				var eze$Temp22;
				egl.atLine(this.eze$$fileName,254,7682,58, this);
				eze$Temp22 = egl.eglx.lang.EAny.ezeWrap(eze$Temp21);
				egl.atLine(this.eze$$fileName,254,7665,76, this);
				normalExpected = this.normalSmallFloat(expected, eze$Temp20, eze$Temp22);
				egl.setLocalFunctionVariable("normalExpected", normalExpected, "eglx.lang.ESmallfloat");
				egl.atLine(this.eze$$fileName,254,7682,58, this);
				mantissaExpected = eze$Temp20.ezeUnbox();
				egl.setLocalFunctionVariable("mantissaExpected", mantissaExpected, "eglx.lang.EInt");
				egl.atLine(this.eze$$fileName,254,7682,58, this);
				signExpected = eze$Temp22.ezeUnbox();
				egl.setLocalFunctionVariable("signExpected", signExpected, "eglx.lang.EString");
				var eze$Temp23 = 0;
				var eze$Temp24;
				egl.atLine(this.eze$$fileName,255,7760,52, this);
				eze$Temp24 = egl.eglx.lang.EAny.ezeWrap(eze$Temp23);
				var eze$Temp25 = "";
				var eze$Temp26;
				egl.atLine(this.eze$$fileName,255,7760,52, this);
				eze$Temp26 = egl.eglx.lang.EAny.ezeWrap(eze$Temp25);
				egl.atLine(this.eze$$fileName,255,7745,68, this);
				normalActual = this.normalSmallFloat(actual, eze$Temp24, eze$Temp26);
				egl.setLocalFunctionVariable("normalActual", normalActual, "eglx.lang.ESmallfloat");
				egl.atLine(this.eze$$fileName,255,7760,52, this);
				mantissaActual = eze$Temp24.ezeUnbox();
				egl.setLocalFunctionVariable("mantissaActual", mantissaActual, "eglx.lang.EInt");
				egl.atLine(this.eze$$fileName,255,7760,52, this);
				signActual = eze$Temp26.ezeUnbox();
				egl.setLocalFunctionVariable("signActual", signActual, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,256,7817,38, this);
				delta = ((normalExpected - normalActual));
				egl.setLocalFunctionVariable("delta", delta, "eglx.lang.ESmallfloat");
				var eze$Temp27;
				egl.atLine(this.eze$$fileName,257,7867,18, this);
				eze$Temp27 = delta;
				egl.atLine(this.eze$$fileName,257,7859,27, this);
				delta = egl.eglx.lang.convert(egl.eglx.lang.EFloat32.fromEFloat64, [egl.eglx.lang.MathLib.abs(eze$Temp27)]);
				egl.setLocalFunctionVariable("delta", delta, "eglx.lang.ESmallfloat");
				var isEqual;
				egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
				egl.atLine(this.eze$$fileName,258,7908,105, this);
				isEqual = ((((((signExpected) == signActual) && ((mantissaExpected == mantissaActual)))) && ((delta < deltaLimit))));
				egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
				egl.atLine(this.eze$$fileName,262,8025,16, this);
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
				egl.atLine(this.eze$$fileName,266,8187,44, this);
				failedReason = this.buildFailedReason(message, expected, actual);
				egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,267,8236,34, this);
				this.assertTrue(failedReason, isEqual);
				egl.atLine(this.eze$$fileName,265,8057,221, this);
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
				egl.atLine(this.eze$$fileName,271,8412,24, this);
				failedReason = this.expect(expected, actual);
				egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,272,8441,95, this);
				if ((((egl.eglx.lang.NullType.notEquals(message, null)) && ((message) != "")))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,273,8483,46, this);
						failedReason = ((((message) + " - ")) + failedReason);
						egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,275,8540,22, this);
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
				egl.atLine(this.eze$$fileName,279,8679,88, this);
				standardMsg = (((((((((((((((("Failed: ") + this.EXPECTEDHEADER)) + "'")) + egl.eglx.lang.EString.ezeCast({eze$$value : expected, eze$$signature : egl.inferSignature(expected)}, false))) + "' ")) + this.ACTUALHEADER)) + "'")) + egl.eglx.lang.EString.ezeCast({eze$$value : actual, eze$$signature : egl.inferSignature(actual)}, false))) + "' ");
				egl.setLocalFunctionVariable("standardMsg", standardMsg, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,280,8778,21, this);
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
				egl.atLine(this.eze$$fileName,285,8931,13, this);
				mantissa.ezeCopy(0);
				egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
				egl.atLine(this.eze$$fileName,286,8954,127, this);
				if (((afloat >= 0))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,287,8983,11, this);
						sign.ezeCopy("+");
						egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				else {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,289,9022,11, this);
						sign.ezeCopy("-");
						egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,290,9047,21, this);
						afloat = ((afloat * -1));
						egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.EFloat");
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,292,9091,306, this);
				if ((((egl.eglx.lang.NullType.notEquals(afloat, null)) && ((afloat != 0))))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,293,9138,115, this);
						while (((afloat < 1))) {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,294,9173,21, this);
								afloat = ((afloat * 10));
								egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.EFloat");
								egl.atLine(this.eze$$fileName,295,9212,24, this);
								mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) - 1)));
								egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
							}finally{egl.exitBlock();}
							egl.atLine(this.eze$$fileName,293,9138,115, this);
						}
						egl.atLine(this.eze$$fileName,297,9267,117, this);
						while (((afloat >= 10))) {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,298,9304,21, this);
								afloat = (egl.divide(afloat ,10));
								egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.EFloat");
								egl.atLine(this.eze$$fileName,299,9343,24, this);
								mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) + 1)));
								egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
							}finally{egl.exitBlock();}
							egl.atLine(this.eze$$fileName,297,9267,117, this);
						}
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,302,9407,15, this);
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
				egl.atLine(this.eze$$fileName,306,9562,13, this);
				mantissa.ezeCopy(0);
				egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
				egl.atLine(this.eze$$fileName,307,9585,127, this);
				if (((afloat >= 0))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,308,9614,11, this);
						sign.ezeCopy("+");
						egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				else {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,310,9653,11, this);
						sign.ezeCopy("-");
						egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,311,9678,21, this);
						afloat = ((afloat * -1));
						egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.ESmallfloat");
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,313,9722,306, this);
				if ((((egl.eglx.lang.NullType.notEquals(afloat, null)) && ((afloat != 0))))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,314,9769,115, this);
						while (((afloat < 1))) {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,315,9804,21, this);
								afloat = ((afloat * 10));
								egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.ESmallfloat");
								egl.atLine(this.eze$$fileName,316,9843,24, this);
								mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) - 1)));
								egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
							}finally{egl.exitBlock();}
							egl.atLine(this.eze$$fileName,314,9769,115, this);
						}
						egl.atLine(this.eze$$fileName,318,9898,117, this);
						while (((afloat >= 10))) {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,319,9935,21, this);
								afloat = (egl.divide(afloat ,10));
								egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.ESmallfloat");
								egl.atLine(this.eze$$fileName,320,9974,24, this);
								mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) + 1)));
								egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
							}finally{egl.exitBlock();}
							egl.atLine(this.eze$$fileName,318,9898,117, this);
						}
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,323,10038,15, this);
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