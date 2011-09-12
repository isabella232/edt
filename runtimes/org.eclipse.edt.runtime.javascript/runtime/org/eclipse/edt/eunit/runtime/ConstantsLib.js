egl.defineRUILibrary('org.eclipse.edt.eunit.runtime', 'ConstantsLib',
{
	'eze$$fileName': 'org/eclipse/edt/eunit/runtime/ConstantsLib.egl',
	'eze$$runtimePropertiesFile': 'org.eclipse.edt.eunit.runtime.ConstantsLib',
		"constructor": function() {
			if(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']) return egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'];
			egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']=this;
			this.jsrt$SysVar = new egl.egl.core.SysVar();
			this.eze$$setInitial();
		}
		,
		"eze$$setEmpty": function() {
			this.NEWLINE = "";
			this.EXIT_PREFIX = "";
			this.SPASSED = 0;
			this.SFAILED = 0;
			this.SERROR = 0;
			this.SNOT_RUN = 0;
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
		"setNEWLINE": function(ezeValue) {
			this.NEWLINE = ezeValue;
		}
		,
		"getEXIT_PREFIX": function() {
			return EXIT_PREFIX;
		}
		,
		"setEXIT_PREFIX": function(ezeValue) {
			this.EXIT_PREFIX = ezeValue;
		}
		,
		"getSPASSED": function() {
			return SPASSED;
		}
		,
		"setSPASSED": function(ezeValue) {
			this.SPASSED = ezeValue;
		}
		,
		"getSFAILED": function() {
			return SFAILED;
		}
		,
		"setSFAILED": function(ezeValue) {
			this.SFAILED = ezeValue;
		}
		,
		"getSERROR": function() {
			return SERROR;
		}
		,
		"setSERROR": function(ezeValue) {
			this.SERROR = ezeValue;
		}
		,
		"getSNOT_RUN": function() {
			return SNOT_RUN;
		}
		,
		"setSNOT_RUN": function(ezeValue) {
			this.SNOT_RUN = ezeValue;
		}
		,
		"toString": function() {
			return "[ConstantsLib]";
		}
	}
);
