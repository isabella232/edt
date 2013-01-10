define(["org/eclipse/edt/rui/widgets/GridLayout", "org/eclipse/edt/eunit/runtime/TestListMgr", "eglx/ui/rui/eze$$View", "org/eclipse/edt/eunit/runtime/ServiceBindingType"],function(){
	egl.loadCSS("css/client.rest.css");
	egl.defineRUIHandler("client", "demo", {
		"eze$$fileName" : "client/demo.egl",
		"eze$$runtimePropertiesFile" : "client/demo",
			"constructor": function() {
				new egl.org.eclipse.edt.eunit.runtime.TestListMgr();
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
					egl.atLine(this.eze$$fileName,14,273,11, this);
					this.ui.setColumns(3);
					egl.atLine(this.eze$$fileName,14,286,8, this);
					this.ui.setRows(4);
					egl.atLine(this.eze$$fileName,14,296,15, this);
					this.ui.setCellPadding(4);
					egl.atLine(this.eze$$fileName,14,313,12, this);
					this.ui.setChildren([]);
					egl.atLine(this.eze$$fileName,11,147,28, this);
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
				var ezert$$2 = new egl.client.demo();
				ezert$$2.ui = ezert$$1.ui === null ? null : ezert$$1.ui.eze$$clone();
				return ezert$$2;
			}
			,
			"eze$$getAnnotations": function() {
				if(this.annotations === undefined){
					this.annotations = {};
					this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("demo", null, false);
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
					egl.atLine(this.eze$$fileName,17,355,54, this);
					egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).bindingType = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEPLOYED;
					var http;
					egl.addLocalFunctionVariable("http", http, "eglx.http.HttpRest", "http");
					egl.atLine(this.eze$$fileName,18,431,53, this);
					http = egl.eglx.lang.EAny.ezeCast(egl.eglx.lang.SysLib.getResource("binding:file:egl_rest#arraysTgt"), egl.eglx.http.HttpRest);
					egl.setLocalFunctionVariable("http", http, "eglx.http.HttpRest");
					var ar =  [];
					egl.addLocalFunctionVariable("ar", ar, "eglx.lang.EList<eglx.lang.EString>", "!ar");
					egl.atLine(this.eze$$fileName,20,509,27, this);
					ar.appendElement(egl.checkNull("index1"));
					egl.atLine(this.eze$$fileName,21,539,27, this);
					ar.appendElement(egl.checkNull("index2"));
					egl.atLine(this.eze$$fileName,22,569,27, this);
					ar.appendElement(egl.checkNull("index3"));
					egl.atLine(this.eze$$fileName,23,602,81, this);
					if (egl.enableEditing !== true) {
						egl.eglx.rest.invokeEglService(egl.eglx.rest.configHttp(http,
								{uri : "", method : egl.eglx.http.HttpMethod.POST, encoding : egl.eglx.services.Encoding.JSON, charset : "UTF-8", contentType : null},
								{encoding : egl.eglx.services.Encoding.JSON, charset : "UTF-8", contentType : null}),
								"services.ArrayPassing",
								"singleIn",
								[ar],
								["Array"],
								["_array"],
								[egl.eglx.lang.EString],
								new egl.egl.jsrt.Delegate(this, egl.client.demo.prototype.showresult), new egl.egl.jsrt.Delegate(this, egl.client.demo.prototype.ex));
					}
					;
					egl.atLine(this.eze$$fileName,16,333,358, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"showresult": function(str) {
				try { egl.enter("showresult",this,arguments);
					egl.addLocalFunctionVariable("str", str, "eglx.lang.EString", "str");
					egl.atLine(this.eze$$fileName,26,736,24, this);
					egl.eglx.lang.SysLib.writeStdout(str);
					egl.atLine(this.eze$$fileName,25,696,72, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"ex": function(ae) {
				try { egl.enter("ex",this,arguments);
					egl.addLocalFunctionVariable("ae", ae, "eglx.lang.AnyException", "ae");
					egl.atLine(this.eze$$fileName,29,810,31, this);
					egl.eglx.lang.SysLib.writeStderr(egl.checkNull(ae).message);
					egl.atLine(this.eze$$fileName,28,773,76, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"toString": function() {
				return "[demo]";
			}
			,
			"eze$$getName": function() {
				return "demo";
			}
			,
			"eze$$getChildVariables": function() {
				var eze$$parent = this;
				return [
				{name: "TestListMgr", value : egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst'], type : "org.eclipse.edt.eunit.runtime.TestListMgr", jsName : "egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']"},
				{name: "ui", value : eze$$parent.ui, type : "org.eclipse.edt.rui.widgets.GridLayout", jsName : "ui"}
				];
			}
		}
	);
});
