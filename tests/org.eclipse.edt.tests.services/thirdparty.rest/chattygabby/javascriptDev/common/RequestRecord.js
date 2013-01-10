egl.defineClass('common', "RequestRecord", "egl.jsrt", "Record", {
	"eze$$fileName" : "common/ChattyGabbyService.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.action = source.action;
			this.startAtId = source.startAtId;
		}
		,
		"eze$$setEmpty": function() {
			this.action = "";
			this.startAtId = "";
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
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.common.RequestRecord();
			ezert$$2.action = ezert$$1.action;
			ezert$$2.startAtId = ezert$$1.startAtId;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("RequestRecord", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("action", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("action");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("action", "action", "S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("startAtId", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("startAtId");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("startAtId", "startAtId", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[RequestRecord]";
		}
		,
		"eze$$getName": function() {
			return "common.RequestRecord";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			return [
			{name: "action", value : eze$$parent.action, type : "eglx.lang.EString", jsName : "action"},
			{name: "startAtId", value : eze$$parent.startAtId, type : "eglx.lang.EString", jsName : "startAtId"}
			];
		}
	}
);
