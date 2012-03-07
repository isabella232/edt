define([],function(){
	egl.defineClass('org.eclipse.edt.eunit.runtime', "AssertionFailedException", "eglx.lang", "AnyException", {
		"eze$$fileName" : "org/eclipse/edt/eunit/runtime/LogResult.egl",
			"constructor": function() {
				this.eze$$setInitial();
			}
			,
			"ezeCopy": function(source) {
			}
			,
			"eze$$setEmpty": function() {
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
				var ezert$$2 = new egl.org.eclipse.edt.eunit.runtime.AssertionFailedException();
				return ezert$$2;
			}
			,
			"eze$$getAnnotations": function() {
				if(this.annotations === undefined){
					this.annotations = {};
					this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("AssertionFailedException", null, false);
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
			"toString": function() {
				return "[AssertionFailedException]";
			}
			,
			"eze$$getName": function() {
				return "org.eclipse.edt.eunit.runtime.AssertionFailedException";
			}
			,
			"eze$$getChildVariables": function() {
				var eze$$parent = this;
				var childVars = this.eze$$superClass.prototype.eze$$getChildVariables.call(this);
				return childVars;
			}
		}
	);
});
