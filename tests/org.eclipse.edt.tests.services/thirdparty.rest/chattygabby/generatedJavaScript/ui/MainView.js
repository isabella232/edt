egl.defineRUIHandler("ui", "MainView", {
	"eze$$fileName" : "ui/MainView.egl",
	"eze$$runtimePropertiesFile" : "ui/MainView",
		"constructor": function() {
			new egl.org.eclipse.edt.rui.widgets.GridLayoutLib();
			this.eze$$setInitial();
			this.start();
		}
		,
		"eze$$setEmpty": function() {
			this.ui = new egl.org.eclipse.edt.rui.widgets.GridLayout();
			this.sendButton = new egl.org.eclipse.edt.rui.widgets.Button();
			this.setNameButton = new egl.org.eclipse.edt.rui.widgets.Button();
			this.aboutButton = new egl.org.eclipse.edt.rui.widgets.Button();
			this.TextLabel = new egl.org.eclipse.edt.rui.widgets.TextLabel();
			this.messageTextField = new egl.org.eclipse.edt.rui.widgets.TextField();
			this.messageLogArea = new egl.org.eclipse.edt.rui.widgets.Div();
			this.GridLayout = new egl.org.eclipse.edt.rui.widgets.GridLayout();
			this.request = new egl.org.eclipse.edt.rui.widgets.Button();
			this.startAtId = 0;
			this.userId = "";
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
			this.ui.setColumns(4);
			this.ui.setRows(4);
			this.ui.setCellPadding(4);
			this.ui.setChildren([(function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.GridLayout,"org.eclipse.edt.rui.widgets.GridLayout"), (function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.messageLogArea,"org.eclipse.edt.rui.widgets.Div"), (function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.messageTextField,"org.eclipse.edt.rui.widgets.TextField"), (function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.TextLabel,"org.eclipse.edt.rui.widgets.TextLabel")].setType("[eglx.ui.rui.Widget"));
			var eze$Temp1 = null;
			{
				var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
				eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
				eze$SettingTarget1.row = 1;
				eze$SettingTarget1.column = 1;
				eze$Temp1 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp1);
			}
			this.sendButton.setLayoutData({eze$$value : eze$Temp1, eze$$signature : "Torg/eclipse/edt/rui/widgets/gridlayoutdata;"});
			this.sendButton.setText("Send");
			var eze$Temp2 = null;
			{
				var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
				eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
				eze$SettingTarget1.row = 1;
				eze$SettingTarget1.column = 2;
				eze$Temp2 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp2);
			}
			this.setNameButton.setLayoutData({eze$$value : eze$Temp2, eze$$signature : "Torg/eclipse/edt/rui/widgets/gridlayoutdata;"});
			this.setNameButton.setText("Set Gabby Name");
			var eze$Temp3 = null;
			{
				var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
				eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
				eze$SettingTarget1.row = 1;
				eze$SettingTarget1.column = 3;
				eze$Temp3 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp3);
			}
			this.aboutButton.setLayoutData({eze$$value : eze$Temp3, eze$$signature : "Torg/eclipse/edt/rui/widgets/gridlayoutdata;"});
			this.aboutButton.setText("Button");
			var eze$Temp4 = null;
			{
				var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
				eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
				eze$SettingTarget1.row = 2;
				eze$SettingTarget1.column = 1;
				eze$SettingTarget1.horizontalAlignment = egl.checkNull(egl.org.eclipse.edt.rui.widgets.GridLayoutLib['$inst']).ALIGN_RIGHT;
				eze$Temp4 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp4);
			}
			this.TextLabel.setLayoutData({eze$$value : eze$Temp4, eze$$signature : "Torg/eclipse/edt/rui/widgets/gridlayoutdata;"});
			this.TextLabel.setText("Chat text:");
			var eze$Temp5 = null;
			{
				var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
				eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
				eze$SettingTarget1.row = 2;
				eze$SettingTarget1.column = 2;
				eze$SettingTarget1.horizontalSpan = 2;
				eze$Temp5 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp5);
			}
			this.messageTextField.setLayoutData({eze$$value : eze$Temp5, eze$$signature : "Torg/eclipse/edt/rui/widgets/gridlayoutdata;"});
			var eze$Temp6 = null;
			{
				var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
				eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
				eze$SettingTarget1.row = 3;
				eze$SettingTarget1.column = 2;
				eze$SettingTarget1.horizontalSpan = 2;
				eze$Temp6 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp6);
			}
			this.messageLogArea.setLayoutData({eze$$value : eze$Temp6, eze$$signature : "Torg/eclipse/edt/rui/widgets/gridlayoutdata;"});
			this.messageLogArea.setStyle("overflow: scroll");
			this.messageLogArea.setWidth("360");
			this.messageLogArea.setHeight("390");
			var eze$Temp7 = null;
			{
				var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
				eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
				eze$SettingTarget1.row = 1;
				eze$SettingTarget1.column = 1;
				eze$SettingTarget1.horizontalSpan = 3;
				eze$Temp7 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp7);
			}
			this.GridLayout.setLayoutData({eze$$value : eze$Temp7, eze$$signature : "Torg/eclipse/edt/rui/widgets/gridlayoutdata;"});
			this.GridLayout.setCellPadding(4);
			this.GridLayout.setRows(1);
			this.GridLayout.setColumns(3);
			this.GridLayout.setChildren([(function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.sendButton,"org.eclipse.edt.rui.widgets.Button"), (function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.aboutButton,"org.eclipse.edt.rui.widgets.Button"), (function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.setNameButton,"org.eclipse.edt.rui.widgets.Button")].setType("[org.eclipse.edt.rui.widgets.Button"));
			this.request.setText("Request");
			egl.checkNull(this.request).getOnClick().appendElement(new egl.egl.jsrt.Delegate(this, egl.ui.MainView.prototype.getChat2));
			this.startAtId = 0;
			this.userId = "";
			egl.checkNull(this).initialUI = [(function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.request,"org.eclipse.edt.rui.widgets.Button"), (function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.ui,"org.eclipse.edt.rui.widgets.GridLayout")].setType("[eglx.ui.rui.Widget");
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.ui.MainView();
			ezert$$2.ui = ezert$$1.ui === null ? null : ezert$$1.ui.eze$$clone();
			ezert$$2.sendButton = ezert$$1.sendButton === null ? null : ezert$$1.sendButton.eze$$clone();
			ezert$$2.setNameButton = ezert$$1.setNameButton === null ? null : ezert$$1.setNameButton.eze$$clone();
			ezert$$2.aboutButton = ezert$$1.aboutButton === null ? null : ezert$$1.aboutButton.eze$$clone();
			ezert$$2.TextLabel = ezert$$1.TextLabel === null ? null : ezert$$1.TextLabel.eze$$clone();
			ezert$$2.messageTextField = ezert$$1.messageTextField === null ? null : ezert$$1.messageTextField.eze$$clone();
			ezert$$2.messageLogArea = ezert$$1.messageLogArea === null ? null : ezert$$1.messageLogArea.eze$$clone();
			ezert$$2.GridLayout = ezert$$1.GridLayout === null ? null : ezert$$1.GridLayout.eze$$clone();
			ezert$$2.request = ezert$$1.request === null ? null : ezert$$1.request.eze$$clone();
			ezert$$2.startAtId = ezert$$1.startAtId;
			ezert$$2.userId = ezert$$1.userId;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("MainView", null, false);
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
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("sendButton", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("sendButton");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("sendButton", "sendButton", "org.eclipse.edt.rui.widgets.Button", egl.org.eclipse.edt.rui.widgets.Button, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("setNameButton", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("setNameButton");
				this.fieldInfos[2] =new egl.eglx.services.FieldInfo("setNameButton", "setNameButton", "org.eclipse.edt.rui.widgets.Button", egl.org.eclipse.edt.rui.widgets.Button, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("aboutButton", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("aboutButton");
				this.fieldInfos[3] =new egl.eglx.services.FieldInfo("aboutButton", "aboutButton", "org.eclipse.edt.rui.widgets.Button", egl.org.eclipse.edt.rui.widgets.Button, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("TextLabel", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("TextLabel");
				this.fieldInfos[4] =new egl.eglx.services.FieldInfo("TextLabel", "TextLabel", "org.eclipse.edt.rui.widgets.TextLabel", egl.org.eclipse.edt.rui.widgets.TextLabel, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("messageTextField", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("messageTextField");
				this.fieldInfos[5] =new egl.eglx.services.FieldInfo("messageTextField", "messageTextField", "org.eclipse.edt.rui.widgets.TextField", egl.org.eclipse.edt.rui.widgets.TextField, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("messageLogArea", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("messageLogArea");
				this.fieldInfos[6] =new egl.eglx.services.FieldInfo("messageLogArea", "messageLogArea", "org.eclipse.edt.rui.widgets.Div", egl.org.eclipse.edt.rui.widgets.Div, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("GridLayout", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("GridLayout");
				this.fieldInfos[7] =new egl.eglx.services.FieldInfo("GridLayout", "GridLayout", "org.eclipse.edt.rui.widgets.GridLayout", egl.org.eclipse.edt.rui.widgets.GridLayout, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("request", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("request");
				this.fieldInfos[8] =new egl.eglx.services.FieldInfo("request", "request", "org.eclipse.edt.rui.widgets.Button", egl.org.eclipse.edt.rui.widgets.Button, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("startAtId", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("startAtId");
				this.fieldInfos[9] =new egl.eglx.services.FieldInfo("startAtId", "startAtId", "I;", Number, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("userId", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("userId");
				this.fieldInfos[10] =new egl.eglx.services.FieldInfo("userId", "userId", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"start": function() {
		}
		,
		"userNameSupplied": function(value, e) {
			if (((value) != "")) {
				this.userId = value;
				return true;
			}
			else {
				return false;
			}
		}
		,
		"getChat2": function(e) {
			this.getChat();
		}
		,
		"getChat": function() {
			var encoding;
			encoding = egl.checkNull(egl.eglx.services.Encoding).JSON;
			var svc = egl.eglx.lang.SysLib.getResource("chattygabby");
			var rec = new egl.common.RequestRecord();
			rec.action = "loadRecentPst";
			rec.startAtId = (function(x){ return x == null ? x : egl.eglx.lang.EString.fromEInt16.apply( this, arguments );})(0);
			var eze$Temp9 = null;
			eze$Temp9 = egl.eglx.lang.AnyValue.ezeCopyTo(rec, eze$Temp9);
			egl.eglx.rest.invokeService(egl.eglx.rest.configHttp(svc,
					{ method : egl.eglx.http.HttpMethod.PUT, uri : "http://chattygabby.com/cg/chatgabv2"
					, encoding : egl.eglx.services.Encoding._FORM, charset : "UTF-8", contentType : null},
					{encoding : egl.eglx.services.Encoding.XML, charset : null, contentType : null}),
					rec,
					[rec],
					["egl.common.RequestRecord"],
					["formData"],
					[egl.common.ChattyGabbyRecords],
					new egl.egl.jsrt.Delegate(this, egl.ui.MainView.prototype.chatsReturned), new egl.egl.jsrt.Delegate(this, egl.ui.MainView.prototype.chatsException));
			;
			;
		}
		,
		"chatsReturned": function(retResult, http) {
			egl.eglx.lang.SysLib.writeStdout((egl.eglx.lang.EString.plus("http.getResponse().body:", egl.checkNull(http.getResponse()).body)));
			try {
				{
					var i = 0;
					i = 1;
					while (true) {
						var eze$Temp10 = false;
						var eze$Temp11;
						eze$Temp11 = ((i <= retResult.list.getSize()));
						if (eze$Temp11) {
							eze$Temp10 = true;
						}
						if (!eze$Temp10) break;
						do {
							this.messageLogArea.setInnerHTML(((((egl.checkNull(this.messageLogArea).getInnerHTML()) + egl.checkNull(retResult.list)[retResult.list.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)].txt)) + "<p>"));
						}
						while (false);
						if (!eze$Temp10) break;
						i = ((i + 1));
					}
				}
			}
			catch ( eze$Temp14 )
			{
				{
					if (!(eze$Temp14 instanceof egl.eglx.lang.AnyException)) {
						eze$Temp14 = egl.makeExceptionFromCaughtObject(eze$Temp14);
					}
					var ex = eze$Temp14;
					{
						egl.eglx.lang.SysLib.writeStdout(egl.checkNull(ex).message);
					}
				}
			}
		}
		,
		"chatsException": function(exp) {
			var se;
			se = egl.eglx.lang.EAny.ezeCast({eze$$value : exp, eze$$signature : egl.inferSignature(exp)}, egl.eglx.services.ServiceInvocationException);
			egl.eglx.lang.SysLib.writeStdout(egl.checkNull(se).detail1);
			egl.eglx.lang.SysLib.writeStdout(egl.checkNull(se).detail2);
			egl.eglx.lang.SysLib.writeStdout(egl.checkNull(se).detail3);
		}
		,
		"toString": function() {
			return "[MainView]";
		}
	}
);
