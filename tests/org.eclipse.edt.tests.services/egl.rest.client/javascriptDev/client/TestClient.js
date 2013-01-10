define(["org/eclipse/edt/rui/widgets/GridLayout", "org/eclipse/edt/rui/widgets/GridLayoutData", "common/utilities", "eglx/ui/rui/eze$$View", "common/clientlocal", "org/eclipse/edt/eunit/runtime/ServiceBindingType", "eglx/ui/rui/eze$$Event", "org/eclipse/edt/rui/widgets/Button"],function(){
	egl.loadCSS("css/egl.rest.client.css");
	egl.defineRUIHandler("client", "TestClient", {
		"eze$$fileName" : "client/TestClient.egl",
		"eze$$runtimePropertiesFile" : "client/TestClient",
			"constructor": function() {
				new egl.common.utilities();
				new egl.common.clientlocal();
				this.eze$$setInitial();
				this.start();
			}
			,
			"eze$$setEmpty": function() {
				this.ui = new egl.org.eclipse.edt.rui.widgets.GridLayout();
				this.Button = new egl.org.eclipse.edt.rui.widgets.Button();
			}
			,
			"eze$$setInitial": function() {
				try { egl.enter("<init>",this,arguments);
					this.eze$$setEmpty();
					egl.atLine(this.eze$$fileName,17,399,11, this);
					this.ui.setColumns(3);
					egl.atLine(this.eze$$fileName,17,412,8, this);
					this.ui.setRows(4);
					egl.atLine(this.eze$$fileName,17,422,15, this);
					this.ui.setCellPadding(4);
					egl.atLine(this.eze$$fileName,17,439,21, this);
					this.ui.setChildren([egl.eglx.lang.convert(egl.eglx.ui.rui.Widget.fromWidget,this.Button,"org.eclipse.edt.rui.widgets.Button")].setType("[org.eclipse.edt.rui.widgets.Button"));
					var eze$Temp1 = null;
					{
						var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
						egl.atLine(this.eze$$fileName,18,495,41, this);
						eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
						egl.atLine(this.eze$$fileName,18,515,7, this);
						eze$SettingTarget1.row = 1;
						egl.atLine(this.eze$$fileName,18,524,10, this);
						eze$SettingTarget1.column = 2;
						egl.atLine(this.eze$$fileName,18,495,41, this);
						eze$Temp1 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp1);
					}
					egl.atLine(this.eze$$fileName,18,482,54, this);
					this.Button.setLayoutData(egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,eze$Temp1.eze$$clone(),"Torg/eclipse/edt/rui/widgets/gridlayoutdata;"));
					egl.atLine(this.eze$$fileName,18,538,13, this);
					this.Button.setText("Button");
					egl.atLine(this.eze$$fileName,18,553,26, this);
					egl.checkNull(this.Button).getOnClick().appendElement(new egl.egl.jsrt.Delegate(this, egl.client.TestClient.prototype.Button_onClick));
					egl.atLine(this.eze$$fileName,14,269,28, this);
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
				var ezert$$2 = new egl.client.TestClient();
				ezert$$2.ui = ezert$$1.ui === null ? null : ezert$$1.ui.eze$$clone();
				ezert$$2.Button = ezert$$1.Button === null ? null : ezert$$1.Button.eze$$clone();
				return ezert$$2;
			}
			,
			"eze$$getAnnotations": function() {
				if(this.annotations === undefined){
					this.annotations = {};
					this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("TestClient", null, false);
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
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("Button", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("Button");
					this.fieldInfos[1] =new egl.eglx.services.FieldInfo("Button", "Button", "org.eclipse.edt.rui.widgets.Button", egl.org.eclipse.edt.rui.widgets.Button, annotations);
				}
				return this.fieldInfos;
			}
			,
			"start": function() {
				try { egl.enter("start",this,arguments);
					egl.atLine(this.eze$$fileName,20,588,24, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"Button_onClick": function(event) {
				try { egl.enter("Button_onClick",this,arguments);
					egl.addLocalFunctionVariable("event", event, "eglx.ui.rui.Event", "event");
					egl.atLine(this.eze$$fileName,26,725,75, this);
					egl.common.clientlocal['$inst'].testService(egl.checkNull(egl.common.utilities['$inst']).LibraryList, egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEVELOP);
					egl.atLine(this.eze$$fileName,23,622,186, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"toString": function() {
				return "[TestClient]";
			}
			,
			"eze$$getName": function() {
				return "TestClient";
			}
			,
			"eze$$getChildVariables": function() {
				var eze$$parent = this;
				return [
				{name: "utilities", value : egl.common.utilities['$inst'], type : "common.utilities", jsName : "egl.common.utilities['$inst']"},
				{name: "clientlocal", value : egl.common.clientlocal['$inst'], type : "common.clientlocal", jsName : "egl.common.clientlocal['$inst']"},
				{name: "ui", value : eze$$parent.ui, type : "org.eclipse.edt.rui.widgets.GridLayout", jsName : "ui"},
				{name: "Button", value : eze$$parent.Button, type : "org.eclipse.edt.rui.widgets.Button", jsName : "Button"}
				];
			}
		}
	);
});
