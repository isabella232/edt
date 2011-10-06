egl.defineRUILibrary('org.eclipse.edt.eunit.runtime', 'TestAsyncLib',
{
	'eze$$fileName': 'org/eclipse/edt/eunit/runtime/TestAsyncLib.egl',
	'eze$$runtimePropertiesFile': 'org.eclipse.edt.eunit.runtime.TestAsyncLib',
		"constructor": function() {
			if(egl.org.eclipse.edt.eunit.runtime.TestAsyncLib['$inst']) return egl.org.eclipse.edt.eunit.runtime.TestAsyncLib['$inst'];
			egl.org.eclipse.edt.eunit.runtime.TestAsyncLib['$inst']=this;
			this.eze$$setInitial();
		}
		,
		"eze$$setEmpty": function() {
			this.runTestMtds =  [];
			this.LibraryStartTests =  [];
			this.nextLibIndex = 0;
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
			this.nextLibIndex = 1;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("TestAsyncLib", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("runTestMtds", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("runTestMtds");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("runTestMtds", "runTestMtds", "[org.eclipse.edt.eunit.runtime.runTestMethod", "", annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("LibraryStartTests", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("LibraryStartTests");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("LibraryStartTests", "LibraryStartTests", "[org.eclipse.edt.eunit.runtime.runTestMethod", "", annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("nextLibIndex", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("nextLibIndex");
				this.fieldInfos[2] =new egl.eglx.services.FieldInfo("nextLibIndex", "nextLibIndex", "I;", Number, annotations);
			}
			return this.fieldInfos;
		}
		,
		"nextTestLibrary": function() {
			var eze$Temp1 = false;
			eze$Temp1 = ((this.nextLibIndex < this.LibraryStartTests.getSize()));
			if (eze$Temp1) {
				this.nextLibIndex = ((this.nextLibIndex + 1));
				egl.checkNull(this.LibraryStartTests)[this.LibraryStartTests.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : this.nextLibIndex, eze$$signature : "I;"},false) - 1)]();
			}
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
			return "[TestAsyncLib]";
		}
	}
);
