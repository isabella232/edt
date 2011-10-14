egl.defineClass('org.eclipse.edt.eunit.runtime', 'MultiStatus',
{
	'eze$$fileName': 'org/eclipse/edt/eunit/runtime/MultiStatus.egl',
	'eze$$runtimePropertiesFile': 'org.eclipse.edt.eunit.runtime.MultiStatus',
		"constructor": function() {
			new egl.org.eclipse.edt.eunit.runtime.ConstantsLib();
			new egl.eglx.rbd.StrLib();
			new egl.org.eclipse.edt.eunit.runtime.LogResult();
			this.eze$$setInitial();
		}
		,
		"eze$$setEmpty": function() {
			this.testCnt = 0;
			this.expectedCnt = 0;
			this.passedCnt = 0;
			this.failedCnt = 0;
			this.errCnt = 0;
			this.badCnt = 0;
			this.notRunCnt = 0;
			this.firstFailedTestName = "";
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.org.eclipse.edt.eunit.runtime.MultiStatus();
			ezert$$2.testCnt = ezert$$1.testCnt;
			ezert$$2.expectedCnt = ezert$$1.expectedCnt;
			ezert$$2.passedCnt = ezert$$1.passedCnt;
			ezert$$2.failedCnt = ezert$$1.failedCnt;
			ezert$$2.errCnt = ezert$$1.errCnt;
			ezert$$2.badCnt = ezert$$1.badCnt;
			ezert$$2.notRunCnt = ezert$$1.notRunCnt;
			ezert$$2.firstFailedTestName = ezert$$1.firstFailedTestName;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("MultiStatus", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("testCnt", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("testCnt");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("testCnt", "testCnt", "I;", Number, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("expectedCnt", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("expectedCnt");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("expectedCnt", "expectedCnt", "I;", Number, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("passedCnt", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("passedCnt");
				this.fieldInfos[2] =new egl.eglx.services.FieldInfo("passedCnt", "passedCnt", "I;", Number, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("failedCnt", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("failedCnt");
				this.fieldInfos[3] =new egl.eglx.services.FieldInfo("failedCnt", "failedCnt", "I;", Number, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("errCnt", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("errCnt");
				this.fieldInfos[4] =new egl.eglx.services.FieldInfo("errCnt", "errCnt", "I;", Number, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("badCnt", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("badCnt");
				this.fieldInfos[5] =new egl.eglx.services.FieldInfo("badCnt", "badCnt", "I;", Number, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("notRunCnt", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("notRunCnt");
				this.fieldInfos[6] =new egl.eglx.services.FieldInfo("notRunCnt", "notRunCnt", "I;", Number, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("firstFailedTestName", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("firstFailedTestName");
				this.fieldInfos[7] =new egl.eglx.services.FieldInfo("firstFailedTestName", "firstFailedTestName", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"addStatus": function(testId) {
			var s = new egl.org.eclipse.edt.eunit.runtime.Status();
			s = egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].getStatus();
			var msg = "";
			msg = ((((testId) + ": ")) + s.reason);
			egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].logStdOut(msg);
			this.testCnt = ((this.testCnt + 1));
			if (((s.code == egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SPASSED))) {
				this.passedCnt = ((this.passedCnt + 1));
			}
			else {
				if (((s.code == egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SFAILED))) {
					this.failedCnt = ((this.failedCnt + 1));
				}
				else {
					if (((s.code == egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SERROR))) {
						this.errCnt = ((this.errCnt + 1));
					}
					else {
						if (((s.code == egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SNOT_RUN))) {
							this.notRunCnt = ((this.notRunCnt + 1));
						}
						else {
							this.badCnt = ((this.badCnt + 1));
						}
					}
				}
			}
			var eze$Temp5 = false;
			eze$Temp5 = ((((s.code != egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SPASSED)) && ((egl.eglx.rbd.StrLib['$inst'].characterLen(this.firstFailedTestName) == 0))));
			if (eze$Temp5) {
				this.firstFailedTestName = testId;
			}
		}
		,
		"toString": function() {
			return "[MultiStatus]";
		}
		,
		"eze$$getName": function() {
			return "MultiStatus";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			return [
			{name: "ConstantsLib", value : egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'], type : "org.eclipse.edt.eunit.runtime.ConstantsLib", jsName : "egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']"},
			{name: "LogResult", value : egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'], type : "org.eclipse.edt.eunit.runtime.LogResult", jsName : "egl.org.eclipse.edt.eunit.runtime.LogResult['$inst']"},
			{name: "StrLib", value : egl.eglx.rbd.StrLib['$inst'], type : "eglx.rbd.StrLib", jsName : "egl.eglx.rbd.StrLib['$inst']"},
			{name: "testCnt", value : eze$$parent.testCnt, type : "eglx.lang.EInt", jsName : "testCnt"},
			{name: "expectedCnt", value : eze$$parent.expectedCnt, type : "eglx.lang.EInt", jsName : "expectedCnt"},
			{name: "passedCnt", value : eze$$parent.passedCnt, type : "eglx.lang.EInt", jsName : "passedCnt"},
			{name: "failedCnt", value : eze$$parent.failedCnt, type : "eglx.lang.EInt", jsName : "failedCnt"},
			{name: "errCnt", value : eze$$parent.errCnt, type : "eglx.lang.EInt", jsName : "errCnt"},
			{name: "badCnt", value : eze$$parent.badCnt, type : "eglx.lang.EInt", jsName : "badCnt"},
			{name: "notRunCnt", value : eze$$parent.notRunCnt, type : "eglx.lang.EInt", jsName : "notRunCnt"},
			{name: "firstFailedTestName", value : eze$$parent.firstFailedTestName, type : "eglx.lang.EString", jsName : "firstFailedTestName"}
			];
		}
	}
);
