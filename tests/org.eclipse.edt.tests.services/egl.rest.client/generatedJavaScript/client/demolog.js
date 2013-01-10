define(["eglx/ui/rui/eze$$View", "org/eclipse/edt/rui/widgets/GridLayout", "org/eclipse/edt/eunit/runtime/LogResult"],function(){
	egl.loadCSS("css/egl.rest.client.css");
	egl.defineRUIHandler("client", "demolog", {
		"eze$$fileName" : "client/demolog.egl",
		"eze$$runtimePropertiesFile" : "client/demolog",
			"constructor": function() {
				new egl.org.eclipse.edt.eunit.runtime.LogResult();
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
				egl.checkNull(this).initialUI = [egl.eglx.lang.convert(egl.eglx.ui.rui.Widget.fromWidget,this.ui,"org.eclipse.edt.rui.widgets.GridLayout")].setType("[org.eclipse.edt.rui.widgets.GridLayout");
			}
			,
			"eze$$clone": function() {
				var ezert$$1 = this;
				var ezert$$2 = new egl.client.demolog();
				ezert$$2.ui = ezert$$1.ui === null ? null : ezert$$1.ui.eze$$clone();
				return ezert$$2;
			}
			,
			"eze$$getAnnotations": function() {
				if(this.annotations === undefined){
					this.annotations = {};
					this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("demolog", null, false);
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
				var a;
				a = "abc";
				var b;
				b = "abc";
				egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].assertStringEqual1(a, b);
			}
			,
			"toString": function() {
				return "[demolog]";
			}
		}
	);
});
