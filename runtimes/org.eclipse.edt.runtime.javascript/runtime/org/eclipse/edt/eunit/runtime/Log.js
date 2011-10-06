egl.defineClass('org.eclipse.edt.eunit.runtime', "Log", "egl.jsrt", "Record", {
	"eze$$fileName" : "org/eclipse/edt/eunit/runtime/LogResult.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.msg = source.msg;
		}
		,
		"eze$$setEmpty": function() {
			this.msg = "";
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.org.eclipse.edt.eunit.runtime.Log();
			ezert$$2.msg = ezert$$1.msg;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("Log", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("msg", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("msg");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("msg", "msg", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[Log]";
		}
	}
);
