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
				egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).bindingType = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ServiceBindingType).DEPLOYED;
				var http;
				http = egl.eglx.lang.EAny.ezeCast(egl.eglx.lang.SysLib.getResource("binding:file:egl_rest#arraysTgt"), egl.eglx.http.HttpRest);
				var ar =  [];
				ar.appendElement(egl.checkNull("index1"));
				ar.appendElement(egl.checkNull("index2"));
				ar.appendElement(egl.checkNull("index3"));
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
				;
			}
			,
			"showresult": function(str) {
				egl.eglx.lang.SysLib.writeStdout(str);
			}
			,
			"ex": function(ae) {
				egl.eglx.lang.SysLib.writeStderr(egl.checkNull(ae).message);
			}
			,
			"toString": function() {
				return "[demo]";
			}
		}
	);
});
