egl.defineRUIHandler("ui", "Sample1", {
	"eze$$fileName" : "ui/Sample1.egl",
	"eze$$runtimePropertiesFile" : "ui/Sample1",
		"constructor": function() {
			egl.loadScript( "org.eclipse.edt.rui.widgets","GridLayout" );
			egl.loadCSS( "css/chattygabby.css" );
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
				egl.atLine(this.eze$$fileName,12,244,11, this);
				this.ui.setColumns(3);
				egl.atLine(this.eze$$fileName,12,257,8, this);
				this.ui.setRows(4);
				egl.atLine(this.eze$$fileName,12,267,15, this);
				this.ui.setCellPadding(4);
				egl.atLine(this.eze$$fileName,12,284,14, this);
				this.ui.setChildren([]);
				egl.atLine(this.eze$$fileName,10,118,18, this);
				egl.checkNull(this).initialUI = [(function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.ui,"org.eclipse.edt.rui.widgets.GridLayout")].setType("[org.eclipse.edt.rui.widgets.GridLayout");
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
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
			try { egl.enter("start",this,arguments);
				egl.atLine(this.eze$$fileName,14,307,24, this);
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
		}
		,
		"toString": function() {
			return "[Sample1]";
		}
		,
		"eze$$getName": function() {
			return "Sample1";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			return [
			{name: "ui", value : eze$$parent.ui, type : "org.eclipse.edt.rui.widgets.GridLayout", jsName : "ui"}
			];
		}
	}
);
