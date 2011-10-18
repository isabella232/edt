egl.defineRUILibrary('org.eclipse.edt.eunit.runtime', 'TestListMgr',
{
	'eze$$fileName': 'org/eclipse/edt/eunit/runtime/TestListMgr.egl',
	'eze$$runtimePropertiesFile': 'org.eclipse.edt.eunit.runtime.TestListMgr',
		"constructor": function() {
			if(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']) return egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst'];
			egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']=this;
			egl.loadScript( "org.eclipse.edt.eunit.runtime","Status" );
			egl.loadScript( "org.eclipse.edt.eunit.runtime","ConstantsLib" );
			egl.loadScript( "org.eclipse.edt.eunit.runtime","AssertionFailedException" );
			egl.loadScript( "org.eclipse.edt.eunit.runtime","MultiStatus" );
			egl.loadScript( "org.eclipse.edt.eunit.runtime","LogResult" );
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
				this.bindingType = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEDICATED;
				this.testIndex = 1;
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
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("ms", "ms", "Torg/eclipse/edt/eunit/runtime/multistatus;", egl.org.eclipse.edt.eunit.runtime.MultiStatus, annotations);
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
				this.fieldInfos[4] =new egl.eglx.services.FieldInfo("runTestMtds", "runTestMtds", "[org.eclipse.edt.eunit.runtime.runTestMethod", "", annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("testLibName", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("testLibName");
				this.fieldInfos[5] =new egl.eglx.services.FieldInfo("testLibName", "testLibName", "S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("LibraryStartTests", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("LibraryStartTests");
				this.fieldInfos[6] =new egl.eglx.services.FieldInfo("LibraryStartTests", "LibraryStartTests", "[org.eclipse.edt.eunit.runtime.runTestMethod", "", annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("libIndex", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("libIndex");
				this.fieldInfos[7] =new egl.eglx.services.FieldInfo("libIndex", "libIndex", "I;", Number, annotations);
			}
			return this.fieldInfos;
		}
		,
		"nextTest": function() {
			try { egl.enter("nextTest",this,arguments);
				var testId = "";
				egl.addLocalFunctionVariable("testId", testId, "eglx.lang.EString", "testId");
				testId = this.getTestIdString();
				egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
				egl.eglx.lang.SysLib.writeStdout((("Running test: ") + testId));
				this.ms.addStatus(testId);
				var eze$Temp1 = false;
				eze$Temp1 = ((this.testIndex < this.runTestMtds.getSize()));
				if (eze$Temp1) {
					try{egl.enterBlock();
						this.testIndex = ((this.testIndex + 1));
						egl.checkNull(this.runTestMtds, "runTestMtds")[this.runTestMtds.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : this.testIndex, eze$$signature : "I;"},false) - 1)]();
					}finally{egl.exitBlock();}
				}
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
		}
		,
		"getTestIdString": function() {
			try { egl.enter("getTestIdString",this,arguments);
				var testMethodNamesSize = 0;
				egl.addLocalFunctionVariable("testMethodNamesSize", testMethodNamesSize, "eglx.lang.EInt", "testMethodNamesSize");
				testMethodNamesSize = this.testMethodNames.getSize();
				egl.setLocalFunctionVariable("testMethodNamesSize", testMethodNamesSize, "eglx.lang.EInt");
				var testId = "";
				egl.addLocalFunctionVariable("testId", testId, "eglx.lang.EString", "testId");
				testId = ((this.testLibName) + "::");
				egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
				if (((this.testIndex <= testMethodNamesSize))) {
					try{egl.enterBlock();
						testId = ((testId) + egl.checkNull(this.testMethodNames, "testMethodNames")[this.testMethodNames.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : this.testIndex, eze$$signature : "I;"},false) - 1)]);
						egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				else {
					try{egl.enterBlock();
						if (((this.testIndex == ((testMethodNamesSize + 1))))) {
							try{egl.enterBlock();
								testId = ((testId) + "endTest");
								egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
							}finally{egl.exitBlock();}
						}
						else {
							try{egl.enterBlock();
								testId = ((testId) + "INVALIDINDEXFOUND!!!");
								egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
							}finally{egl.exitBlock();}
						}
					}finally{egl.exitBlock();}
				}
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
				var eze$Temp5 = false;
				eze$Temp5 = ((this.libIndex < this.LibraryStartTests.getSize()));
				if (eze$Temp5) {
					try{egl.enterBlock();
						this.libIndex = ((this.libIndex + 1));
						egl.checkNull(this.LibraryStartTests, "LibraryStartTests")[this.LibraryStartTests.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : this.libIndex, eze$$signature : "I;"},false) - 1)]();
					}finally{egl.exitBlock();}
				}
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
		}
		,
		"handleCallBackException": function(exp) {
			try { egl.enter("handleCallBackException",this,arguments);
				egl.addLocalFunctionVariable("exp", exp, "eglx.lang.AnyException", "exp");
				var str = "";
				egl.addLocalFunctionVariable("str", str, "eglx.lang.EString", "str");
				str = (((((("Caught service exception: ") + egl.checkNull(exp).messageID)) + ": ")) + egl.checkNull(exp).message);
				egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
				if (egl.isa(exp, "Teglx/services/serviceinvocationexception;")) {
					try{egl.enterBlock();
						var sexp = null;
						egl.addLocalFunctionVariable("sexp", sexp, "eglx.services.ServiceInvocationException", "sexp");
						sexp = egl.eglx.lang.EAny.ezeCast({eze$$value : exp, eze$$signature : "Teglx/lang/anyexception;"}, egl.eglx.services.ServiceInvocationException);
						egl.setLocalFunctionVariable("sexp", sexp, "eglx.services.ServiceInvocationException");
						var s1 = "";
						egl.addLocalFunctionVariable("s1", s1, "eglx.lang.EString", "s1");
						s1 = (("detail1:") + egl.checkNull(sexp).detail1);
						egl.setLocalFunctionVariable("s1", s1, "eglx.lang.EString");
						var s2 = "";
						egl.addLocalFunctionVariable("s2", s2, "eglx.lang.EString", "s2");
						s2 = (("detail2:") + egl.checkNull(sexp).detail2);
						egl.setLocalFunctionVariable("s2", s2, "eglx.lang.EString");
						var s3 = "";
						egl.addLocalFunctionVariable("s3", s3, "eglx.lang.EString", "s3");
						s3 = (("detail3:") + egl.checkNull(sexp).detail3);
						egl.setLocalFunctionVariable("s3", s3, "eglx.lang.EString");
						str = ((str) + egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE);
						egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
						str = ((((str) + s1)) + egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE);
						egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
						str = ((((str) + s2)) + egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE);
						egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
						str = ((((str) + s3)) + egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE);
						egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
					}finally{egl.exitBlock();}
				}
				var eze$Temp8 = null;
				eze$Temp8 = egl.eglx.lang.EAny.ezeWrap(str);
				egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].error(eze$Temp8);
				str = eze$Temp8.ezeUnbox();
				egl.setLocalFunctionVariable("str", str, "eglx.lang.EString");
				var testId = "";
				egl.addLocalFunctionVariable("testId", testId, "eglx.lang.EString", "testId");
				testId = egl.checkNull(egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames, "testMethodNames")[egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testIndex, eze$$signature : "I;"},false) - 1)];
				egl.setLocalFunctionVariable("testId", testId, "eglx.lang.EString");
				this.nextTest();
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
				var assertMsg = "";
				egl.addLocalFunctionVariable("assertMsg", assertMsg, "eglx.lang.EString", "assertMsg");
				assertMsg = "Assertion failed for: ";
				egl.setLocalFunctionVariable("assertMsg", assertMsg, "eglx.lang.EString");
				var s = new egl.org.eclipse.edt.eunit.runtime.Status();
				egl.addLocalFunctionVariable("s", s, "org.eclipse.edt.eunit.runtime.Status", "s");
				s = egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].getStatus();
				egl.setLocalFunctionVariable("s", s, "org.eclipse.edt.eunit.runtime.Status");
				s.reason = ((assertMsg) + s.reason);
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
				var expMsg = "";
				egl.addLocalFunctionVariable("expMsg", expMsg, "eglx.lang.EString", "expMsg");
				expMsg = (("uncaught exception for: ") + this.getTestIdString());
				egl.setLocalFunctionVariable("expMsg", expMsg, "eglx.lang.EString");
				var eze$Temp9 = null;
				eze$Temp9 = egl.eglx.lang.EAny.ezeWrap(expMsg);
				egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].error(eze$Temp9);
				expMsg = eze$Temp9.ezeUnbox();
				egl.setLocalFunctionVariable("expMsg", expMsg, "eglx.lang.EString");
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
				if ((bType == egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEDICATED)) {
					try{egl.enterBlock();
						if (!egl.debugg) egl.leave();
						return "DEDICATED_BINDING";
					}finally{egl.exitBlock();}
				}
				else {
					try{egl.enterBlock();
						if ((bType == egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEVELOP)) {
							try{egl.enterBlock();
								if (!egl.debugg) egl.leave();
								return "DEVELOP_BINDING";
							}finally{egl.exitBlock();}
						}
						else {
							try{egl.enterBlock();
								if ((bType == egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEPLOYED)) {
									try{egl.enterBlock();
										if (!egl.debugg) egl.leave();
										return "DEPLOYED_BINDING";
									}finally{egl.exitBlock();}
								}
								else {
									try{egl.enterBlock();
										if (!egl.debugg) egl.leave();
										return "UNKNOWN Binding Type - NOT supported";
									}finally{egl.exitBlock();}
								}
							}finally{egl.exitBlock();}
						}
					}finally{egl.exitBlock();}
				}
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
