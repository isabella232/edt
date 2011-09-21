egl.defineRUILibrary('org.eclipse.edt.eunit.runtime', 'ConstantsLib',
{
	'eze$$fileName': 'org/eclipse/edt/eunit/runtime/ConstantsLib.egl',
	'eze$$runtimePropertiesFile': 'org.eclipse.edt.eunit.runtime.ConstantsLib',
		"constructor": function() {
			if(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']) return egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'];
			egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']=this;
			this.jsrt$SysVar = new egl.egl.core.SysVar();
			this.NEWLINE = "";
			this.NEWLINE = "\r\n";
			this.EXIT_PREFIX = "";
			this.EXIT_PREFIX = "STATUS: ";
			this.SPASSED = 0;
			this.SPASSED = 0;
			this.SFAILED = 0;
			this.SFAILED = 1;
			this.SERROR = 0;
			this.SERROR = 2;
			this.SNOT_RUN = 0;
			this.SNOT_RUN = 3;
			this.SBAD = 0;
			this.SBAD = 4;
			this.eze$$setInitial();
		}
		,
		"eze$$setEmpty": function() {
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
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
	}
);
