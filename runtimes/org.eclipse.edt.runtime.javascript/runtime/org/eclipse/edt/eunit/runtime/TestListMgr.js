/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
	if (egl.eze$$userLibs) egl.eze$$userLibs.push('org.eclipse.edt.eunit.runtime.TestListMgr');
	else egl.eze$$userLibs = ['org.eclipse.edt.eunit.runtime.TestListMgr'];
	egl.defineRUILibrary('org.eclipse.edt.eunit.runtime', 'TestListMgr',
	{
		'eze$$fileName': 'org/eclipse/edt/eunit/runtime/TestListMgr.egl',
		'eze$$runtimePropertiesFile': 'org.eclipse.edt.eunit.runtime.TestListMgr',
			"constructor": function() {
				if(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']) return egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst'];
				egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']=this;
			}
			,
			"eze$$setEmpty": function() {
				new egl.org.eclipse.edt.eunit.runtime.ConstantsLib();
				new egl.org.eclipse.edt.eunit.runtime.LogResult();
				this.bindingType = null;
				this.ms = new egl.org.eclipse.edt.eunit.runtime.MultiStatus();
				this.testIndex = 0;
				this.testMethodNames =  [];
				this.runTestMtds =  [];
				this.testLibName = "";
				this.LibraryStartTests =  [];
				this.libIndex = 0;
			}
			,
			"eze$$setInitial": function() {
				if(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']) return egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst'];
				egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']=this;
				try { egl.enter("<init>",this,arguments);
					this.eze$$setEmpty();
					egl.atLine(this.eze$$fileName,35,1058,28, this);
					this.bindingType = egl.org.eclipse.edt.eunit.runtime.ServiceBindingType.DEDICATED;
					egl.atLine(this.eze$$fileName,38,1172,1, this);
					this.testIndex = 1;
					egl.atLine(this.eze$$fileName,44,1611,1, this);
					this.libIndex = 1;
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
					this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("TestListMgr", null, false);
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
			"nextTest": function() {
				try { egl.enter("nextTest",this,arguments);
					var testId;
					egl.addLocalFunctionVariable("testId", testId, "eglx.lang.EString", "testId");
					egl.atLine(this.eze$$fileName,47,1695,17, this);
					testId = this.getTestIdString();
					egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,48,1717,46, this);
					egl.eglx.lang.SysLib.writeStdout((("Running test: ") + testId));
					egl.atLine(this.eze$$fileName,50,1858,21, this);
					this.ms.addStatus(testId);
					egl.atLine(this.eze$$fileName,51,1886,94, this);
					if (((this.testIndex < this.runTestMtds.getSize()))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,52,1928,15, this);
							this.testIndex = ((this.testIndex + 1));
							egl.atLine(this.eze$$fileName,53,1948,25, this);
							egl.getElement(this.runTestMtds, egl.eglx.lang.EInt32.ezeCast({eze$$value : this.testIndex, eze$$signature : "I;"}, false) - 1)();
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,46,1652,334, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"getTestIdString": function() {
				try { egl.enter("getTestIdString",this,arguments);
					var testMethodNamesSize;
					egl.addLocalFunctionVariable("testMethodNamesSize", testMethodNamesSize, "eglx.lang.EInt", "testMethodNamesSize");
					egl.atLine(this.eze$$fileName,58,2074,25, this);
					testMethodNamesSize = this.testMethodNames.getSize();
					egl.setLocalFunctionVariable("testMethodNamesSize", testMethodNamesSize, "eglx.lang.EInt");
					var testId;
					egl.addLocalFunctionVariable("testId", testId, "eglx.lang.EString", "testId");
					egl.atLine(this.eze$$fileName,60,2126,18, this);
					testId = ((this.testLibName) + "::");
					egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,61,2151,219, this);
					if (((this.testIndex <= testMethodNamesSize))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,62,2192,37, this);
							testId = ((testId) + egl.getElement(this.testMethodNames, egl.eglx.lang.EInt32.ezeCast({eze$$value : this.testIndex, eze$$signature : "I;"}, false) - 1));
							egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,64,2242,121, this);
							if (((this.testIndex == ((testMethodNamesSize + 1))))) {
								try{egl.enterBlock();
									egl.atLine(this.eze$$fileName,65,2288,20, this);
									testId = ((testId) + "endTest");
									egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
								}finally{egl.exitBlock();}
							}
							else {
								try{egl.enterBlock();
									egl.atLine(this.eze$$fileName,67,2323,32, this);
									testId = ((testId) + "INVALIDINDEXFOUND!!!");
									egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
								}finally{egl.exitBlock();}
							}
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,70,2374,16, this);
					if (!egl.debugg) egl.leave();
					return testId;
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"nextTestLibrary": function() {
				try { egl.enter("nextTestLibrary",this,arguments);
					egl.atLine(this.eze$$fileName,74,2434,104, this);
					if (((this.libIndex < this.LibraryStartTests.getSize()))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,75,2481,14, this);
							this.libIndex = ((this.libIndex + 1));
							egl.atLine(this.eze$$fileName,76,2500,30, this);
							egl.getElement(this.LibraryStartTests, egl.eglx.lang.EInt32.ezeCast({eze$$value : this.libIndex, eze$$signature : "I;"}, false) - 1)();
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,73,2404,142, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"handleCallBackException": function(exp, http) {
				try { egl.enter("handleCallBackException",this,arguments);
					egl.addLocalFunctionVariable("exp", exp, "eglx.lang.AnyException", "exp");
					egl.addLocalFunctionVariable("http", http, "eglx.http.IHttp", "http");
					var str;
					egl.addLocalFunctionVariable("str", str, "eglx.lang.EString", "str");
					egl.atLine(this.eze$$fileName,82,2701,65, this);
					str = (((((("Caught service exception: ") + exp.messageID)) + ": ")) + exp.message);
					egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,83,2771,602, this);
					if ((exp instanceof egl.eglx.services.ServiceInvocationException)) {
						try{egl.enterBlock();
							var sexp;
							egl.addLocalFunctionVariable("sexp", sexp, "eglx.services.ServiceInvocationException", "sexp");
							egl.atLine(this.eze$$fileName,84,2848,33, this);
							sexp = egl.eglx.lang.EAny.ezeCast({eze$$value : exp, eze$$signature : egl.inferSignature(exp)}, egl.eglx.services.ServiceInvocationException);
							egl.setLocalFunctionVariable("sexp", sexp, "eglx.services.ServiceInvocationException");
							var s1;
							egl.addLocalFunctionVariable("s1", s1, "eglx.lang.EString", "s1");
							egl.atLine(this.eze$$fileName,85,2902,25, this);
							s1 = (("detail1:") + sexp.detail1);
							egl.setLocalFunctionVariable("s1", s1, "eglx.lang.EString");
							var s2;
							egl.addLocalFunctionVariable("s2", s2, "eglx.lang.EString", "s2");
							egl.atLine(this.eze$$fileName,86,2948,25, this);
							s2 = (("detail2:") + sexp.detail2);
							egl.setLocalFunctionVariable("s2", s2, "eglx.lang.EString");
							var s3;
							egl.addLocalFunctionVariable("s3", s3, "eglx.lang.EString", "s3");
							egl.atLine(this.eze$$fileName,87,2994,25, this);
							s3 = (("detail3:") + sexp.detail3);
							egl.setLocalFunctionVariable("s3", s3, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,88,3025,33, this);
							str = ((str) + egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'].NEWLINE);
							egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,89,3063,38, this);
							str = ((((str) + s1)) + egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'].NEWLINE);
							egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,90,3106,38, this);
							str = ((((str) + s2)) + egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'].NEWLINE);
							egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,91,3149,38, this);
							str = ((((str) + s3)) + egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'].NEWLINE);
							egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,92,3192,86, this);
							str = (((egl.eglx.lang.EString.plus(((str) + "Original request body: "), http.getRequest().body))) + egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'].NEWLINE);
							egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,93,3283,83, this);
							str = (((egl.eglx.lang.EString.plus(((str) + "Raw response body: "), http.getResponse().body))) + egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'].NEWLINE);
							egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,95,3377,21, this);
					egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].error(str);
					var testId;
					egl.addLocalFunctionVariable("testId", testId, "eglx.lang.EString", "testId");
					egl.atLine(this.eze$$fileName,96,3418,50, this);
					testId = egl.getElement(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst'].testMethodNames, egl.eglx.lang.EInt32.ezeCast({eze$$value : egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst'].testIndex, eze$$signature : "I;"}, false) - 1);
					egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,97,3473,23, this);
					this.nextTest();
					egl.atLine(this.eze$$fileName,81,2614,888, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"caughtFailedAssertion": function(exp) {
				try { egl.enter("caughtFailedAssertion",this,arguments);
					egl.addLocalFunctionVariable("exp", exp, "org.eclipse.edt.eunit.runtime.AssertionFailedException", "exp");
					egl.atLine(this.eze$$fileName,102,3634,53, this);
					egl.eglx.lang.SysLib.writeStdout((("AssertionFail - ") + exp.message));
					egl.atLine(this.eze$$fileName,100,3508,187, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"caughtAnyException": function(exp) {
				try { egl.enter("caughtAnyException",this,arguments);
					egl.addLocalFunctionVariable("exp", exp, "eglx.lang.AnyException", "exp");
					var expMsg;
					egl.addLocalFunctionVariable("expMsg", expMsg, "eglx.lang.EString", "expMsg");
					egl.atLine(this.eze$$fileName,107,3810,46, this);
					expMsg = (("uncaught exception for: ") + this.getTestIdString());
					egl.setLocalFunctionVariable("expMsg", expMsg, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,108,3862,80, this);
					expMsg = ((expMsg) + ((((((((egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'].NEWLINE) + "    => ")) + exp.messageID)) + ": ")) + exp.message));
					egl.setLocalFunctionVariable("expMsg", expMsg, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,111,3987,24, this);
					egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].error(expMsg);
					egl.atLine(this.eze$$fileName,105,3702,317, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"getBindingTypeString": function(bType) {
				try { egl.enter("getBindingTypeString",this,arguments);
					egl.addLocalFunctionVariable("bType", bType, "org.eclipse.edt.eunit.runtime.ServiceBindingType", "bType");
					{
						egl.atLine(this.eze$$fileName,115,4103,304, this);
						egl.atLine(this.eze$$fileName,115,4103,304, this);
						EzeLabel_eze_CaseLabel_0: if ((egl.eglx.lang.EAny.ezeCast({eze$$value : bType, eze$$signature : egl.inferSignature(bType)}, egl.eglx.lang.AnyEnumeration) == egl.eglx.lang.EAny.ezeCast({eze$$value : egl.org.eclipse.edt.eunit.runtime.ServiceBindingType.DEDICATED, eze$$signature : "org.eclipse.edt.eunit.runtime.ServiceBindingType"}, egl.eglx.lang.AnyEnumeration))) {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,117,4159,29, this);
								if (!egl.debugg) egl.leave();
								return "DEDICATED_BINDING";
							}finally{egl.exitBlock();}
						}
						else {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,118,4193,65, this);
								if ((egl.eglx.lang.EAny.ezeCast({eze$$value : bType, eze$$signature : egl.inferSignature(bType)}, egl.eglx.lang.AnyEnumeration) == egl.eglx.lang.EAny.ezeCast({eze$$value : egl.org.eclipse.edt.eunit.runtime.ServiceBindingType.DEVELOP, eze$$signature : "org.eclipse.edt.eunit.runtime.ServiceBindingType"}, egl.eglx.lang.AnyEnumeration))) {
									try{egl.enterBlock();
										egl.atLine(this.eze$$fileName,119,4231,27, this);
										if (!egl.debugg) egl.leave();
										return "DEVELOP_BINDING";
									}finally{egl.exitBlock();}
								}
								else {
									try{egl.enterBlock();
										egl.atLine(this.eze$$fileName,120,4263,67, this);
										if ((egl.eglx.lang.EAny.ezeCast({eze$$value : bType, eze$$signature : egl.inferSignature(bType)}, egl.eglx.lang.AnyEnumeration) == egl.eglx.lang.EAny.ezeCast({eze$$value : egl.org.eclipse.edt.eunit.runtime.ServiceBindingType.DEPLOYED, eze$$signature : "org.eclipse.edt.eunit.runtime.ServiceBindingType"}, egl.eglx.lang.AnyEnumeration))) {
											try{egl.enterBlock();
												egl.atLine(this.eze$$fileName,121,4302,28, this);
												if (!egl.debugg) egl.leave();
												return "DEPLOYED_BINDING";
											}finally{egl.exitBlock();}
										}
										else {
											try{egl.enterBlock();
												egl.atLine(this.eze$$fileName,123,4350,48, this);
												if (!egl.debugg) egl.leave();
												return "UNKNOWN Binding Type - NOT supported";
											}finally{egl.exitBlock();}
										}
									}finally{egl.exitBlock();}
								}
							}finally{egl.exitBlock();}
						}
					}
					egl.atLine(this.eze$$fileName,114,4025,390, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"getBindingType": function() {
				return bindingType;
			}
			,
			"setBindingType": function(ezeValue) {
				this.bindingType = ezeValue;
			}
			,
			"getMs": function() {
				return ms;
			}
			,
			"setMs": function(ezeValue) {
				this.ms = ezeValue;
			}
			,
			"getTestIndex": function() {
				return testIndex;
			}
			,
			"setTestIndex": function(ezeValue) {
				this.testIndex = ezeValue;
			}
			,
			"getTestMethodNames": function() {
				return testMethodNames;
			}
			,
			"setTestMethodNames": function(ezeValue) {
				this.testMethodNames = ezeValue;
			}
			,
			"getRunTestMtds": function() {
				return runTestMtds;
			}
			,
			"setRunTestMtds": function(ezeValue) {
				this.runTestMtds = ezeValue;
			}
			,
			"getTestLibName": function() {
				return testLibName;
			}
			,
			"setTestLibName": function(ezeValue) {
				this.testLibName = ezeValue;
			}
			,
			"getLibraryStartTests": function() {
				return LibraryStartTests;
			}
			,
			"setLibraryStartTests": function(ezeValue) {
				this.LibraryStartTests = ezeValue;
			}
			,
			"getLibIndex": function() {
				return libIndex;
			}
			,
			"setLibIndex": function(ezeValue) {
				this.libIndex = ezeValue;
			}
			,
			"toString": function() {
				return "[TestListMgr]";
			}
			,
			"eze$$getName": function() {
				return "TestListMgr";
			}
			,
			"eze$$getChildVariables": function() {
				var eze$$parent = this;
				return [
				{name: "ConstantsLib", value : egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'], type : "org.eclipse.edt.eunit.runtime.ConstantsLib", jsName : "egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']"},
				{name: "LogResult", value : egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'], type : "org.eclipse.edt.eunit.runtime.LogResult", jsName : "egl.org.eclipse.edt.eunit.runtime.LogResult['$inst']"},
				{name: "bindingType", value : eze$$parent.bindingType, type : "org.eclipse.edt.eunit.runtime.ServiceBindingType", jsName : "bindingType"},
				{name: "ms", value : eze$$parent.ms, type : "org.eclipse.edt.eunit.runtime.MultiStatus", jsName : "ms"},
				{name: "testIndex", value : eze$$parent.testIndex, type : "eglx.lang.EInt", jsName : "testIndex"},
				{name: "testMethodNames", value : eze$$parent.testMethodNames, type : "eglx.lang.EList<eglx.lang.EString>", jsName : "!testMethodNames"},
				{name: "runTestMtds", value : eze$$parent.runTestMtds, type : "eglx.lang.EList<org.eclipse.edt.eunit.runtime.runTestMethod>", jsName : "!runTestMtds"},
				{name: "testLibName", value : eze$$parent.testLibName, type : "eglx.lang.EString", jsName : "testLibName"},
				{name: "LibraryStartTests", value : eze$$parent.LibraryStartTests, type : "eglx.lang.EList<org.eclipse.edt.eunit.runtime.runTestMethod>", jsName : "!LibraryStartTests"},
				{name: "libIndex", value : eze$$parent.libIndex, type : "eglx.lang.EInt", jsName : "libIndex"}
				];
			}
		}
	);
