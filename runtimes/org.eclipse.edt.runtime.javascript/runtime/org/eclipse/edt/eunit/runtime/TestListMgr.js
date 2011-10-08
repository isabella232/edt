egl.defineRUILibrary('org.eclipse.edt.eunit.runtime', 'TestListMgr',
{
	'eze$$fileName': 'org/eclipse/edt/eunit/runtime/TestListMgr.egl',
	'eze$$runtimePropertiesFile': 'org.eclipse.edt.eunit.runtime.TestListMgr',
		"constructor": function() {
			if(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']) return egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst'];
			egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']=this;
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
			this.eze$$setEmpty();
			this.bindingType = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEDICATED;
			this.testIndex = 1;
			this.libIndex = 1;
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
			var testMethodNamesSize = 0;
			testMethodNamesSize = this.testMethodNames.getSize();
			var testId = "";
			testId = ((this.testLibName) + "::");
			if (((this.testIndex <= testMethodNamesSize))) {
				testId = ((testId) + egl.checkNull(this.testMethodNames)[this.testMethodNames.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : this.testIndex, eze$$signature : "I;"},false) - 1)]);
			}
			else {
				if (((this.testIndex == ((testMethodNamesSize + 1))))) {
					testId = ((testId) + "endTest");
				}
				else {
					testId = ((testId) + "INVALIDINDEXFOUND!!!");
				}
			}
			this.ms.addStatus(testId);
			var eze$Temp3 = false;
			eze$Temp3 = ((this.testIndex < this.runTestMtds.getSize()));
			if (eze$Temp3) {
				this.testIndex = ((this.testIndex + 1));
				egl.checkNull(this.runTestMtds)[this.runTestMtds.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : this.testIndex, eze$$signature : "I;"},false) - 1)]();
			}
		}
		,
		"nextTestLibrary": function() {
			var eze$Temp5 = false;
			eze$Temp5 = ((this.libIndex < this.LibraryStartTests.getSize()));
			if (eze$Temp5) {
				this.libIndex = ((this.libIndex + 1));
				egl.checkNull(this.LibraryStartTests)[this.LibraryStartTests.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : this.libIndex, eze$$signature : "I;"},false) - 1)]();
			}
		}
		,
		"handleCallBackException": function(exp) {
			var str = "";
			str = (((((("Caught service exception: ") + egl.checkNull(exp).messageID)) + ": ")) + egl.checkNull(exp).message);
			var eze$Temp7 = null;
			eze$Temp7 = egl.eglx.lang.EAny.ezeWrap(str);
			egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].error(eze$Temp7);
			str = eze$Temp7.ezeUnbox();
			var testId = "";
			testId = egl.checkNull(egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames)[egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testIndex, eze$$signature : "I;"},false) - 1)];
			this.nextTest();
		}
		,
		"caughtFailedAssertion": function(exp) {
			var assertMsg = "";
			assertMsg = "Assertion failed for: ";
			var s = new egl.org.eclipse.edt.eunit.runtime.Status();
			s = egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].getStatus();
			s.reason = ((assertMsg) + s.reason);
		}
		,
		"getBindingTypeString": function(bType) {
			if ((bType == egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEDICATED)) {
				return "DEDICATED Binding";
			}
			else {
				if ((bType == egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEVELOP)) {
					return "DEVELOP Binding";
				}
				else {
					if ((bType == egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEPLOYED)) {
						return "DEPLOYED Binding";
					}
					else {
						return "UNKNOWN Binding Type - NOT supported";
					}
				}
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
	}
);
