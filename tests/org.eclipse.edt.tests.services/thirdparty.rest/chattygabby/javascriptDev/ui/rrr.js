egl.defineClass('ui', "rrr", "egl.jsrt", "Record", {
	"eze$$fileName" : "ui/MainView.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.value = source.value;
		}
		,
		"eze$$setEmpty": function() {
			this.value = "";
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
			var ezert$$2 = new egl.ui.rrr();
			ezert$$2.value = ezert$$1.value;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLValue"] = new egl.eglx.xml.binding.annotation.XMLValue(egl.eglx.xml.binding.annotation.XMLStructureKind.simpleContent);
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("rrr", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("value", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("value");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("value", "value", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[rrr]";
		}
		,
		"eze$$getName": function() {
			return "ui.rrr";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			return [
			{name: "value", value : eze$$parent.value, type : "eglx.lang.EString", jsName : "value"}
			];
		}
	}
);
