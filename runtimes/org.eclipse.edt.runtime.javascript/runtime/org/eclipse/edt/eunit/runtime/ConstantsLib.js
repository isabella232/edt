if (egl.eze$$userLibs) egl.eze$$userLibs.push('org.eclipse.edt.eunit.runtime.ConstantsLib');
else egl.eze$$userLibs = ['org.eclipse.edt.eunit.runtime.ConstantsLib'];
egl.defineRUILibrary('org.eclipse.edt.eunit.runtime', 'ConstantsLib',
{
	'eze$$fileName': 'org/eclipse/edt/eunit/runtime/ConstantsLib.egl',
	'eze$$runtimePropertiesFile': 'org.eclipse.edt.eunit.runtime.ConstantsLib',
		"constructor": function() {
			if(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']) return egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'];
			egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']=this;
			this.NEWLINE = "";
			egl.atLine(this.eze$$fileName,7,138,6, this);
			this.NEWLINE = "\r\n";
			this.EXIT_PREFIX = "";
			egl.atLine(this.eze$$fileName,8,175,10, this);
			this.EXIT_PREFIX = "STATUS: ";
			this.SPASSED = 0;
			egl.atLine(this.eze$$fileName,10,212,1, this);
			this.SPASSED = 0;
			this.SFAILED = 0;
			egl.atLine(this.eze$$fileName,11,237,1, this);
			this.SFAILED = 1;
			this.SERROR = 0;
			egl.atLine(this.eze$$fileName,12,261,1, this);
			this.SERROR = 2;
			this.SNOT_RUN = 0;
			egl.atLine(this.eze$$fileName,13,287,1, this);
			this.SNOT_RUN = 3;
			this.SBAD = 0;
			egl.atLine(this.eze$$fileName,14,309,1, this);
			this.SBAD = 4;
			this.eze$$setInitial();
		}
		,
		"eze$$setEmpty": function() {
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
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("ConstantsLib", null, false);
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
		"getNEWLINE": function() {
			return NEWLINE;
		}
		,
		"getEXIT_PREFIX": function() {
			return EXIT_PREFIX;
		}
		,
		"getSPASSED": function() {
			return SPASSED;
		}
		,
		"getSFAILED": function() {
			return SFAILED;
		}
		,
		"getSERROR": function() {
			return SERROR;
		}
		,
		"getSNOT_RUN": function() {
			return SNOT_RUN;
		}
		,
		"getSBAD": function() {
			return SBAD;
		}
		,
		"toString": function() {
			return "[ConstantsLib]";
		}
		,
		"eze$$getName": function() {
			return "ConstantsLib";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			return [
			{name: "NEWLINE", value : eze$$parent.NEWLINE, type : "eglx.lang.EString", jsName : "NEWLINE"},
			{name: "EXIT_PREFIX", value : eze$$parent.EXIT_PREFIX, type : "eglx.lang.EString", jsName : "EXIT_PREFIX"},
			{name: "SPASSED", value : eze$$parent.SPASSED, type : "eglx.lang.EInt", jsName : "SPASSED"},
			{name: "SFAILED", value : eze$$parent.SFAILED, type : "eglx.lang.EInt", jsName : "SFAILED"},
			{name: "SERROR", value : eze$$parent.SERROR, type : "eglx.lang.EInt", jsName : "SERROR"},
			{name: "SNOT_RUN", value : eze$$parent.SNOT_RUN, type : "eglx.lang.EInt", jsName : "SNOT_RUN"},
			{name: "SBAD", value : eze$$parent.SBAD, type : "eglx.lang.EInt", jsName : "SBAD"}
			];
		}
	}
);