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
				try { egl.enter("<init>",this,arguments);
					this.eze$$setEmpty();
					egl.atLine(this.eze$$fileName,13,250,11, this);
					this.ui.setColumns(3);
					egl.atLine(this.eze$$fileName,13,263,8, this);
					this.ui.setRows(4);
					egl.atLine(this.eze$$fileName,13,273,15, this);
					this.ui.setCellPadding(4);
					egl.atLine(this.eze$$fileName,13,290,12, this);
					this.ui.setChildren([]);
					egl.atLine(this.eze$$fileName,10,120,28, this);
					egl.checkNull(this).initialUI = [egl.eglx.lang.convert(egl.eglx.ui.rui.Widget.fromWidget,this.ui,"org.eclipse.edt.rui.widgets.GridLayout")].setType("[org.eclipse.edt.rui.widgets.GridLayout");
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
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
				try { egl.enter("start",this,arguments);
					var a;
					egl.addLocalFunctionVariable("a", a, "eglx.lang.EString", "a");
					egl.atLine(this.eze$$fileName,16,343,5, this);
					a = "abc";
					egl.setLocalFunctionVariable("a", a, "eglx.lang.EString");
					var b;
					egl.addLocalFunctionVariable("b", b, "eglx.lang.EString", "b");
					egl.atLine(this.eze$$fileName,17,366,5, this);
					b = "abc";
					egl.setLocalFunctionVariable("b", b, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,19,384,35, this);
					egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].assertStringEqual1(a, b);
					egl.atLine(this.eze$$fileName,15,310,117, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"toString": function() {
				return "[demolog]";
			}
			,
			"eze$$getName": function() {
				return "demolog";
			}
			,
			"eze$$getChildVariables": function() {
				var eze$$parent = this;
				return [
				{name: "LogResult", value : egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'], type : "org.eclipse.edt.eunit.runtime.LogResult", jsName : "egl.org.eclipse.edt.eunit.runtime.LogResult['$inst']"},
				{name: "ui", value : eze$$parent.ui, type : "org.eclipse.edt.rui.widgets.GridLayout", jsName : "ui"}
				];
			}
		}
	);
});
