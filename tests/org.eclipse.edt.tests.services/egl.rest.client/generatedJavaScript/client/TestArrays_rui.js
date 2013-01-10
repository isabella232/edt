egl.loadFile("rununit.html","client.TestArrays_rui");
define(["eglx/ui/rui/eze$$View", "client/TestArrays"].concat(egl.eze$$externalJS["client.TestArrays_rui"]),function(){
	egl.defineRUIHandler("client", "TestArrays_rui", {
		"eze$$fileName" : "client/TestArrays_rui.egl",
		"eze$$runtimePropertiesFile" : "client/TestArrays_rui",
			"constructor": function() {
				new egl.client.TestArrays();
				this.eze$$setInitial();
				this.start();
			}
			,
			"eze$$setEmpty": function() {
			}
			,
			"eze$$setInitial": function() {
				this.eze$$setEmpty();
				egl.checkNull(this).initialUI = [];
			}
			,
			"eze$$clone": function() {
				var ezert$$1 = this;
				var ezert$$2 = new egl.client.TestArrays_rui();
				return ezert$$2;
			}
			,
			"eze$$getAnnotations": function() {
				if(this.annotations === undefined){
					this.annotations = {};
					this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("TestArrays_rui", null, false);
				}
				return this.annotations;
			}
			,
			"eze$$getFieldInfos": function() {
				if(this.fieldInfos === undefined){
					var annotations;
					this.fieldInfos = new Array();
				}
				return this.fieldInfos;
			}
			,
			"start": function() {
				var startTS = egl.eglx.lang.ETimestamp.currentTimeStamp("yyyyMMddHHmmssffffff");
				egl.client.TestArrays['$inst'].invokeTheTest();
			}
			,
			"toString": function() {
				return "[TestArrays_rui]";
			}
		}
	);
});
