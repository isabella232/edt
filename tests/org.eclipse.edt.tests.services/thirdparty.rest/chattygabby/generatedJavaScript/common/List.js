egl.defineClass('common', "List", "egl.jsrt", "Record", {
	"eze$$fileName" : "common/ChattyGabbyRecords.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.pstid = source.pstid;
			this.crtts = source.crtts;
			this.usrid = source.usrid;
			this.txt = source.txt;
		}
		,
		"eze$$setEmpty": function() {
			this.pstid = 0;
			this.crtts = "";
			this.usrid = "";
			this.txt = "";
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.common.List();
			ezert$$2.pstid = ezert$$1.pstid;
			ezert$$2.crtts = ezert$$1.crtts;
			ezert$$2.usrid = ezert$$1.usrid;
			ezert$$2.txt = ezert$$1.txt;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("List", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["JsonName"] = new egl.eglx.json.JsonName("PSTID");
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("pstid", null, false, false);
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("pstid", "pstid", "I;", Number, annotations);
				annotations = {};
				annotations["JsonName"] = new egl.eglx.json.JsonName("CRTTS");
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("crtts", null, false, false);
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("crtts", "crtts", "S;", String, annotations);
				annotations = {};
				annotations["JsonName"] = new egl.eglx.json.JsonName("USRID");
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("usrid", null, false, false);
				this.fieldInfos[2] =new egl.eglx.services.FieldInfo("usrid", "usrid", "S;", String, annotations);
				annotations = {};
				annotations["JsonName"] = new egl.eglx.json.JsonName("TXT");
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("txt", null, false, false);
				this.fieldInfos[3] =new egl.eglx.services.FieldInfo("txt", "txt", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[List]";
		}
	}
);
