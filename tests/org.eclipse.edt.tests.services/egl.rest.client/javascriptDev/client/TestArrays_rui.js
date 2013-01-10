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
				try { egl.enter("<init>",this,arguments);
					this.eze$$setEmpty();
					egl.atLine(this.eze$$fileName,3,57,14, this);
					egl.checkNull(this).initialUI = [];
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
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
				try { egl.enter("start",this,arguments);
					var startTS = egl.eglx.lang.ETimestamp.currentTimeStamp("yyyyMMddHHmmssffffff");
					egl.addLocalFunctionVariable("startTS", startTS, "eglx.lang.ETimestamp(yyyyMMddHHmmssffffff)", "startTS");
					egl.atLine(this.eze$$fileName,6,224,27, this);
					egl.client.TestArrays['$inst'].invokeTheTest();
					egl.atLine(this.eze$$fileName,4,160,96, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"toString": function() {
				return "[TestArrays_rui]";
			}
			,
			"eze$$getName": function() {
				return "TestArrays_rui";
			}
			,
			"eze$$getChildVariables": function() {
				var eze$$parent = this;
				return [
				{name: "TestArrays", value : egl.client.TestArrays['$inst'], type : "client.TestArrays", jsName : "egl.client.TestArrays['$inst']"}
				];
			}
		}
	);
});
