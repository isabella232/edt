egl.defineClass('common', "ChattyGabbyRecords", "egl.jsrt", "Record", {
	"eze$$fileName" : "common/ChattyGabbyRecords.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.list = source.list;
			this.msg = source.msg;
		}
		,
		"eze$$setEmpty": function() {
			this.list =  [];
			this.msg = "";
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.common.ChattyGabbyRecords();
			ezert$$2.list = ezert$$1.list === null ? null : ezert$$1.list;
			ezert$$2.msg = ezert$$1.msg;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("ChattyGabbyRecords", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("list", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("list");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("list", "list", "[Tcommon/list;", egl.common.List, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("msg", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("msg");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("msg", "msg", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[ChattyGabbyRecords]";
		}
	}
);
