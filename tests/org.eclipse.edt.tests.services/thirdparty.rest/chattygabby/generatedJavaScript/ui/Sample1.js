egl.defineRUIHandler("ui", "Sample1", {
	"eze$$fileName" : "ui/Sample1.egl",
	"eze$$runtimePropertiesFile" : "ui/Sample1",
		"constructor": function() {
			this.eze$$setInitial();
			this.start();
		}
		,
		"eze$$setEmpty": function() {
			this.ui = new egl.org.eclipse.edt.rui.widgets.GridLayout();
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
			this.ui.setColumns(3);
			this.ui.setRows(4);
			this.ui.setCellPadding(4);
			this.ui.setChildren([]);
			egl.checkNull(this).initialUI = [(function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.ui,"org.eclipse.edt.rui.widgets.GridLayout")].setType("[org.eclipse.edt.rui.widgets.GridLayout");
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.ui.Sample1();
			ezert$$2.ui = ezert$$1.ui === null ? null : ezert$$1.ui.eze$$clone();
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("Sample1", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("ui", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("ui");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("ui", "ui", "org.eclipse.edt.rui.widgets.GridLayout", egl.org.eclipse.edt.rui.widgets.GridLayout, annotations);
			}
			return this.fieldInfos;
		}
		,
		"start": function() {
		}
		,
		"toString": function() {
			return "[Sample1]";
		}
	}
);
