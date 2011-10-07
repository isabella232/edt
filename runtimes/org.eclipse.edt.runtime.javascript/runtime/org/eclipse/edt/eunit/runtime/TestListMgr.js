egl.defineRUILibrary('org.eclipse.edt.eunit.runtime', 'TestListMgr',
{
	'eze$$fileName': 'org/eclipse/edt/eunit/runtime/TestListMgr.egl',
	'eze$$runtimePropertiesFile': 'org.eclipse.edt.eunit.runtime.TestListMgr',
		"constructor": function() {
			if(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']) return egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst'];
			egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']=this;
			this.eze$$setInitial();
		}
		,
		"eze$$setEmpty": function() {
			this.ms = new egl.org.eclipse.edt.eunit.runtime.MultiStatus();
			this.nextTestIndex = 0;
			this.runTestMtds =  [];
			this.LibraryStartTests =  [];
			this.nextLibIndex = 0;
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
			this.nextTestIndex = 1;
			this.nextLibIndex = 1;
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
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("ms", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("ms");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("ms", "ms", "Torg/eclipse/edt/eunit/runtime/multistatus;", egl.org.eclipse.edt.eunit.runtime.MultiStatus, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("nextTestIndex", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("nextTestIndex");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("nextTestIndex", "nextTestIndex", "I;", Number, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("runTestMtds", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("runTestMtds");
				this.fieldInfos[2] =new egl.eglx.services.FieldInfo("runTestMtds", "runTestMtds", "[org.eclipse.edt.eunit.runtime.runTestMethod", "", annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("LibraryStartTests", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("LibraryStartTests");
				this.fieldInfos[3] =new egl.eglx.services.FieldInfo("LibraryStartTests", "LibraryStartTests", "[org.eclipse.edt.eunit.runtime.runTestMethod", "", annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("nextLibIndex", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("nextLibIndex");
				this.fieldInfos[4] =new egl.eglx.services.FieldInfo("nextLibIndex", "nextLibIndex", "I;", Number, annotations);
			}
			return this.fieldInfos;
		}
		,
		"nextTest": function(testId) {
			this.ms.addStatus(testId);
			var eze$Temp1 = false;
			eze$Temp1 = ((this.nextTestIndex < this.runTestMtds.getSize()));
			if (eze$Temp1) {
				this.nextTestIndex = ((this.nextTestIndex + 1));
				egl.checkNull(this.runTestMtds)[this.runTestMtds.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : this.nextTestIndex, eze$$signature : "I;"},false) - 1)]();
			}
		}
		,
		"nextTestLibrary": function() {
			var eze$Temp3 = false;
			eze$Temp3 = ((this.nextLibIndex < this.LibraryStartTests.getSize()));
			if (eze$Temp3) {
				this.nextLibIndex = ((this.nextLibIndex + 1));
				egl.checkNull(this.LibraryStartTests)[this.LibraryStartTests.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : this.nextLibIndex, eze$$signature : "I;"},false) - 1)]();
			}
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
		"getNextTestIndex": function() {
			return nextTestIndex;
		}
		,
		"setNextTestIndex": function(ezeValue) {
			this.nextTestIndex = ezeValue;
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
		"getLibraryStartTests": function() {
			return LibraryStartTests;
		}
		,
		"setLibraryStartTests": function(ezeValue) {
			this.LibraryStartTests = ezeValue;
		}
		,
		"getNextLibIndex": function() {
			return nextLibIndex;
		}
		,
		"setNextLibIndex": function(ezeValue) {
			this.nextLibIndex = ezeValue;
		}
		,
		"toString": function() {
			return "[TestListMgr]";
		}
	}
);
