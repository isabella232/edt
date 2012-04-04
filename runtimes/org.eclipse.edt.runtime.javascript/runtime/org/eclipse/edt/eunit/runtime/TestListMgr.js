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
if (egl.eze$$userLibs) egl.eze$$userLibs.push('org.eclipse.edt.eunit.runtime.TestListMgr');
else egl.eze$$userLibs = ['org.eclipse.edt.eunit.runtime.TestListMgr'];
egl.defineRUILibrary('org.eclipse.edt.eunit.runtime', 'TestListMgr',
{
	'eze$$fileName': 'org/eclipse/edt/eunit/runtime/TestListMgr.egl',
	'eze$$runtimePropertiesFile': 'org.eclipse.edt.eunit.runtime.TestListMgr',
		"constructor": function() {
			if(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']) return egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst'];
			egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']=this;
			new egl.org.eclipse.edt.eunit.runtime.ConstantsLib();
			new egl.org.eclipse.edt.eunit.runtime.LogResult();
			this.eze$$setInitial();
		}
		,
		"eze$$setEmpty": function() {
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
			try { egl.enter("<init>",this,arguments);
				this.eze$$setEmpty();
				egl.atLine(this.eze$$fileName,24,492,28, this);
				this.bindingType = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEDICATED;
				egl.atLine(this.eze$$fileName,27,603,1, this);
				this.testIndex = 1;
				egl.atLine(this.eze$$fileName,33,1036,1, this);
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
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("bindingType", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("bindingType");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("bindingType", "bindingType", "org.eclipse.edt.eunit.runtime.ServiceBindingType", egl.org.eclipse.edt.eunit.runtime.ServiceBindingType, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("ms", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("ms");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("ms", "ms", "org.eclipse.edt.eunit.runtime.MultiStatus", egl.org.eclipse.edt.eunit.runtime.MultiStatus, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("testIndex", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("testIndex");
				this.fieldInfos[2] =new egl.eglx.services.FieldInfo("testIndex", "testIndex", "I;", Number, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("testMethodNames", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("testMethodNames");
				this.fieldInfos[3] =new egl.eglx.services.FieldInfo("testMethodNames", "testMethodNames", "[S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("runTestMtds", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("runTestMtds");
				this.fieldInfos[4] =new egl.eglx.services.FieldInfo("runTestMtds", "runTestMtds", "[org.eclipse.edt.eunit.runtime.runTestMethod", egl.eglx.lang.AnyDelegate, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("testLibName", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("testLibName");
				this.fieldInfos[5] =new egl.eglx.services.FieldInfo("testLibName", "testLibName", "S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("LibraryStartTests", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("LibraryStartTests");
				this.fieldInfos[6] =new egl.eglx.services.FieldInfo("LibraryStartTests", "LibraryStartTests", "[org.eclipse.edt.eunit.runtime.runTestMethod", egl.eglx.lang.AnyDelegate, annotations);
			}
			return this.fieldInfos;
		}
		,
		"nextTest": function() {
			try { egl.enter("nextTest",this,arguments);
				var testId;
				egl.addLocalFunctionVariable("testId", testId, "eglx.lang.EString", "testId");
				egl.atLine(this.eze$$fileName,36,1117,17, this);
				testId = this.getTestIdString();
				egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,37,1138,46, this);
				egl.eglx.lang.SysLib.writeStdout((("Running test: ") + testId));
				egl.atLine(this.eze$$fileName,39,1276,21, this);
				this.ms.addStatus(testId);
				var eze$Temp1;
				egl.atLine(this.eze$$fileName,40,1303,91, this);
				eze$Temp1 = ((this.testIndex < this.runTestMtds.getSize()));
				egl.atLine(this.eze$$fileName,40,1303,91, this);
				if (eze$Temp1) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,41,1344,15, this);
						this.testIndex = ((this.testIndex + 1));
						egl.atLine(this.eze$$fileName,42,1363,25, this);
						egl.checkNull(this.runTestMtds, "runTestMtds")[this.runTestMtds.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : this.testIndex, eze$$signature : "I;"}, false) - 1)]();
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,35,1075,324, this);
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
				egl.atLine(this.eze$$fileName,47,1484,25, this);
				testMethodNamesSize = this.testMethodNames.getSize();
				egl.setLocalFunctionVariable("testMethodNamesSize", testMethodNamesSize, "eglx.lang.EInt");
				var testId;
				egl.addLocalFunctionVariable("testId", testId, "eglx.lang.EString", "testId");
				egl.atLine(this.eze$$fileName,49,1534,18, this);
				testId = ((this.testLibName) + "::");
				egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,50,1558,211, this);
				if (((this.testIndex <= testMethodNamesSize))) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,51,1598,37, this);
						testId = ((testId) + egl.checkNull(this.testMethodNames, "testMethodNames")[this.testMethodNames.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : this.testIndex, eze$$signature : "I;"}, false) - 1)]);
						egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				else {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,53,1646,117, this);
						if (((this.testIndex == ((testMethodNamesSize + 1))))) {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,54,1691,20, this);
								testId = ((testId) + "endTest");
								egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
							}finally{egl.exitBlock();}
						}
						else {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,56,1724,32, this);
								testId = ((testId) + "INVALIDINDEXFOUND!!!");
								egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
							}finally{egl.exitBlock();}
						}
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,59,1772,16, this);
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
				var eze$Temp5;
				egl.atLine(this.eze$$fileName,63,1828,101, this);
				eze$Temp5 = ((this.libIndex < this.LibraryStartTests.getSize()));
				egl.atLine(this.eze$$fileName,63,1828,101, this);
				if (eze$Temp5) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,64,1874,14, this);
						this.libIndex = ((this.libIndex + 1));
						egl.atLine(this.eze$$fileName,65,1892,30, this);
						egl.checkNull(this.LibraryStartTests, "LibraryStartTests")[this.LibraryStartTests.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : this.libIndex, eze$$signature : "I;"}, false) - 1)]();
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,62,1799,137, this);
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
				egl.atLine(this.eze$$fileName,71,2087,65, this);
				str = (((((("Caught service exception: ") + egl.checkNull(exp).messageID)) + ": ")) + egl.checkNull(exp).message);
				egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,72,2156,591, this);
				if ((exp instanceof egl.eglx.services.ServiceInvocationException)) {
					try{egl.enterBlock();
						var sexp;
						egl.addLocalFunctionVariable("sexp", sexp, "eglx.services.ServiceInvocationException", "sexp");
						egl.atLine(this.eze$$fileName,73,2232,33, this);
						sexp = egl.eglx.lang.EAny.ezeCast({eze$$value : exp, eze$$signature : egl.inferSignature(exp)}, egl.eglx.services.ServiceInvocationException);
						egl.setLocalFunctionVariable("sexp", sexp, "eglx.services.ServiceInvocationException");
						var s1;
						egl.addLocalFunctionVariable("s1", s1, "eglx.lang.EString", "s1");
						egl.atLine(this.eze$$fileName,74,2285,25, this);
						s1 = (("detail1:") + egl.checkNull(sexp).detail1);
						egl.setLocalFunctionVariable("s1", s1, "eglx.lang.EString");
						var s2;
						egl.addLocalFunctionVariable("s2", s2, "eglx.lang.EString", "s2");
						egl.atLine(this.eze$$fileName,75,2330,25, this);
						s2 = (("detail2:") + egl.checkNull(sexp).detail2);
						egl.setLocalFunctionVariable("s2", s2, "eglx.lang.EString");
						var s3;
						egl.addLocalFunctionVariable("s3", s3, "eglx.lang.EString", "s3");
						egl.atLine(this.eze$$fileName,76,2375,25, this);
						s3 = (("detail3:") + egl.checkNull(sexp).detail3);
						egl.setLocalFunctionVariable("s3", s3, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,77,2405,33, this);
						str = ((str) + egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE);
						egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,78,2442,38, this);
						str = ((((str) + s1)) + egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE);
						egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,79,2484,38, this);
						str = ((((str) + s2)) + egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE);
						egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,80,2526,38, this);
						str = ((((str) + s3)) + egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE);
						egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,81,2568,86, this);
						str = (((egl.eglx.lang.EString.plus(((str) + "Original request body: "), egl.checkNull(http.getRequest()).body))) + egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE);
						egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
						egl.atLine(this.eze$$fileName,82,2658,83, this);
						str = (((egl.eglx.lang.EString.plus(((str) + "Raw response body: "), egl.checkNull(http.getResponse()).body))) + egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE);
						egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,84,2750,21, this);
				egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].error(str);
				var testId;
				egl.addLocalFunctionVariable("testId", testId, "eglx.lang.EString", "testId");
				egl.atLine(this.eze$$fileName,85,2790,50, this);
				testId = egl.checkNull(egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames, "testMethodNames")[egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testIndex, eze$$signature : "I;"}, false) - 1)];
				egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,86,2844,23, this);
				this.nextTest();
				egl.atLine(this.eze$$fileName,70,2001,871, this);
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
				egl.atLine(this.eze$$fileName,91,3000,53, this);
				egl.eglx.lang.SysLib.writeStdout((("AssertionFail - ") + egl.checkNull(exp).message));
				egl.atLine(this.eze$$fileName,89,2876,184, this);
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
				egl.atLine(this.eze$$fileName,96,3171,46, this);
				expMsg = (("uncaught exception for: ") + this.getTestIdString());
				egl.setLocalFunctionVariable("expMsg", expMsg, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,97,3222,80, this);
				expMsg = ((expMsg) + ((((((((egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE) + "    => ")) + egl.checkNull(exp).messageID)) + ": ")) + egl.checkNull(exp).message));
				egl.setLocalFunctionVariable("expMsg", expMsg, "eglx.lang.EString");
				egl.atLine(this.eze$$fileName,100,3344,24, this);
				egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].error(expMsg);
				egl.atLine(this.eze$$fileName,94,3065,310, this);
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
					egl.atLine(this.eze$$fileName,104,3456,295, this);
					EzeLabel_eze_CaseLabel_0: egl.atLine(this.eze$$fileName,104,3456,295, this);
					if ((egl.eglx.lang.EAny.ezeCast({eze$$value : bType, eze$$signature : egl.inferSignature(bType)}, egl.eglx.lang.AnyEnumeration) == egl.eglx.lang.EAny.ezeCast({eze$$value : egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEDICATED, eze$$signature : "org.eclipse.edt.eunit.runtime.ServiceBindingType"}, egl.eglx.lang.AnyEnumeration))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,106,3510,29, this);
							if (!egl.debugg) egl.leave();
							return "DEDICATED_BINDING";
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,107,3543,64, this);
							if ((egl.eglx.lang.EAny.ezeCast({eze$$value : bType, eze$$signature : egl.inferSignature(bType)}, egl.eglx.lang.AnyEnumeration) == egl.eglx.lang.EAny.ezeCast({eze$$value : egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEVELOP, eze$$signature : "org.eclipse.edt.eunit.runtime.ServiceBindingType"}, egl.eglx.lang.AnyEnumeration))) {
								try{egl.enterBlock();
									egl.atLine(this.eze$$fileName,108,3580,27, this);
									if (!egl.debugg) egl.leave();
									return "DEVELOP_BINDING";
								}finally{egl.exitBlock();}
							}
							else {
								try{egl.enterBlock();
									egl.atLine(this.eze$$fileName,109,3611,66, this);
									if ((egl.eglx.lang.EAny.ezeCast({eze$$value : bType, eze$$signature : egl.inferSignature(bType)}, egl.eglx.lang.AnyEnumeration) == egl.eglx.lang.EAny.ezeCast({eze$$value : egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEPLOYED, eze$$signature : "org.eclipse.edt.eunit.runtime.ServiceBindingType"}, egl.eglx.lang.AnyEnumeration))) {
										try{egl.enterBlock();
											egl.atLine(this.eze$$fileName,110,3649,28, this);
											if (!egl.debugg) egl.leave();
											return "DEPLOYED_BINDING";
										}finally{egl.exitBlock();}
									}
									else {
										try{egl.enterBlock();
											egl.atLine(this.eze$$fileName,112,3695,48, this);
											if (!egl.debugg) egl.leave();
											return "UNKNOWN Binding Type - NOT supported";
										}finally{egl.exitBlock();}
									}
								}finally{egl.exitBlock();}
							}
						}finally{egl.exitBlock();}
					}
				}
				egl.atLine(this.eze$$fileName,103,3379,379, this);
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