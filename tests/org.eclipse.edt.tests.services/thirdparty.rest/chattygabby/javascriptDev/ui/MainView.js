egl.defineRUIHandler("ui", "MainView", {
	"eze$$fileName" : "ui/MainView.egl",
	"eze$$runtimePropertiesFile" : "ui/MainView",
		"constructor": function() {
			egl.loadScript( "org.eclipse.edt.rui.widgets","TextLabel" );
			egl.loadScript( "org.eclipse.edt.rui.widgets","GridLayout" );
			egl.loadScript( "org.eclipse.edt.rui.widgets","Button" );
			egl.loadScript( "org.eclipse.edt.rui.widgets","Div" );
			egl.loadScript( "org.eclipse.edt.rui.widgets","GridLayoutLib" );
			egl.loadScript( "org.eclipse.edt.rui.widgets","TextField" );
			egl.loadScript( "org.eclipse.edt.rui.widgets","GridLayoutData" );
			egl.loadScript( "common","RequestRecord" );
			egl.loadScript( "common","ChattyGabbyRecords" );
			egl.loadCSS( "css/chattygabby.css" );
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
			try { egl.enter("<init>",this,arguments);
				this.eze$$setEmpty();
				egl.atLine(this.eze$$fileName,24,864,11, this);
				this.ui.setColumns(4);
				egl.atLine(this.eze$$fileName,24,877,8, this);
				this.ui.setRows(4);
				egl.atLine(this.eze$$fileName,24,887,15, this);
				this.ui.setCellPadding(4);
				egl.atLine(this.eze$$fileName,24,904,70, this);
				this.ui.setChildren([(function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.GridLayout,"org.eclipse.edt.rui.widgets.GridLayout"), (function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.messageLogArea,"org.eclipse.edt.rui.widgets.Div"), (function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.messageTextField,"org.eclipse.edt.rui.widgets.TextField"), (function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.TextLabel,"org.eclipse.edt.rui.widgets.TextLabel")].setType("[eglx.ui.rui.Widget"));
				var eze$Temp1 = null;
				{
					var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
					egl.atLine(this.eze$$fileName,26,1139,42, this);
					eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
					egl.atLine(this.eze$$fileName,26,1160,7, this);
					eze$SettingTarget1.row = 1;
					egl.atLine(this.eze$$fileName,26,1169,10, this);
					eze$SettingTarget1.column = 1;
					egl.atLine(this.eze$$fileName,26,1139,42, this);
					eze$Temp1 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp1);
				}
				egl.atLine(this.eze$$fileName,26,1126,55, this);
				this.sendButton.setLayoutData({eze$$value : eze$Temp1, eze$$signature : "Torg/eclipse/edt/rui/widgets/gridlayoutdata;"});
				egl.atLine(this.eze$$fileName,26,1183,13, this);
				this.sendButton.setText("Send");
				var eze$Temp2 = null;
				{
					var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
					egl.atLine(this.eze$$fileName,27,1240,42, this);
					eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
					egl.atLine(this.eze$$fileName,27,1261,7, this);
					eze$SettingTarget1.row = 1;
					egl.atLine(this.eze$$fileName,27,1270,10, this);
					eze$SettingTarget1.column = 2;
					egl.atLine(this.eze$$fileName,27,1240,42, this);
					eze$Temp2 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp2);
				}
				egl.atLine(this.eze$$fileName,27,1227,55, this);
				this.setNameButton.setLayoutData({eze$$value : eze$Temp2, eze$$signature : "Torg/eclipse/edt/rui/widgets/gridlayoutdata;"});
				egl.atLine(this.eze$$fileName,27,1284,23, this);
				this.setNameButton.setText("Set Gabby Name");
				var eze$Temp3 = null;
				{
					var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
					egl.atLine(this.eze$$fileName,28,1349,42, this);
					eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
					egl.atLine(this.eze$$fileName,28,1370,7, this);
					eze$SettingTarget1.row = 1;
					egl.atLine(this.eze$$fileName,28,1379,10, this);
					eze$SettingTarget1.column = 3;
					egl.atLine(this.eze$$fileName,28,1349,42, this);
					eze$Temp3 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp3);
				}
				egl.atLine(this.eze$$fileName,28,1336,55, this);
				this.aboutButton.setLayoutData({eze$$value : eze$Temp3, eze$$signature : "Torg/eclipse/edt/rui/widgets/gridlayoutdata;"});
				egl.atLine(this.eze$$fileName,28,1393,15, this);
				this.aboutButton.setText("Button");
				var eze$Temp4 = null;
				{
					var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
					egl.atLine(this.eze$$fileName,29,1451,91, this);
					eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
					egl.atLine(this.eze$$fileName,29,1472,7, this);
					eze$SettingTarget1.row = 2;
					egl.atLine(this.eze$$fileName,29,1481,10, this);
					eze$SettingTarget1.column = 1;
					egl.atLine(this.eze$$fileName,29,1493,47, this);
					eze$SettingTarget1.horizontalAlignment = egl.checkNull(egl.org.eclipse.edt.rui.widgets.GridLayoutLib['$inst']).ALIGN_RIGHT;
					egl.atLine(this.eze$$fileName,29,1451,91, this);
					eze$Temp4 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp4);
				}
				egl.atLine(this.eze$$fileName,29,1438,104, this);
				this.TextLabel.setLayoutData({eze$$value : eze$Temp4, eze$$signature : "Torg/eclipse/edt/rui/widgets/gridlayoutdata;"});
				egl.atLine(this.eze$$fileName,29,1544,19, this);
				this.TextLabel.setText("Chat text:");
				var eze$Temp5 = null;
				{
					var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
					egl.atLine(this.eze$$fileName,30,1613,62, this);
					eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
					egl.atLine(this.eze$$fileName,30,1634,7, this);
					eze$SettingTarget1.row = 2;
					egl.atLine(this.eze$$fileName,30,1643,10, this);
					eze$SettingTarget1.column = 2;
					egl.atLine(this.eze$$fileName,30,1655,18, this);
					eze$SettingTarget1.horizontalSpan = 2;
					egl.atLine(this.eze$$fileName,30,1613,62, this);
					eze$Temp5 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp5);
				}
				egl.atLine(this.eze$$fileName,30,1600,75, this);
				this.messageTextField.setLayoutData({eze$$value : eze$Temp5, eze$$signature : "Torg/eclipse/edt/rui/widgets/gridlayoutdata;"});
				var eze$Temp6 = null;
				{
					var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
					egl.atLine(this.eze$$fileName,31,1717,62, this);
					eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
					egl.atLine(this.eze$$fileName,31,1738,7, this);
					eze$SettingTarget1.row = 3;
					egl.atLine(this.eze$$fileName,31,1747,10, this);
					eze$SettingTarget1.column = 2;
					egl.atLine(this.eze$$fileName,31,1759,18, this);
					eze$SettingTarget1.horizontalSpan = 2;
					egl.atLine(this.eze$$fileName,31,1717,62, this);
					eze$Temp6 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp6);
				}
				egl.atLine(this.eze$$fileName,31,1704,75, this);
				this.messageLogArea.setLayoutData({eze$$value : eze$Temp6, eze$$signature : "Torg/eclipse/edt/rui/widgets/gridlayoutdata;"});
				egl.atLine(this.eze$$fileName,31,1781,26, this);
				this.messageLogArea.setStyle("overflow: scroll");
				egl.atLine(this.eze$$fileName,31,1809,13, this);
				this.messageLogArea.setWidth("360");
				egl.atLine(this.eze$$fileName,31,1824,14, this);
				this.messageLogArea.setHeight("390");
				var eze$Temp7 = null;
				{
					var eze$SettingTarget1 = new egl.org.eclipse.edt.rui.widgets.GridLayoutData();
					egl.atLine(this.eze$$fileName,32,1883,62, this);
					eze$SettingTarget1 = egl.eglx.lang.AnyValue.ezeCopyTo(new egl.org.eclipse.edt.rui.widgets.GridLayoutData(), eze$SettingTarget1);
					egl.atLine(this.eze$$fileName,32,1904,7, this);
					eze$SettingTarget1.row = 1;
					egl.atLine(this.eze$$fileName,32,1913,10, this);
					eze$SettingTarget1.column = 1;
					egl.atLine(this.eze$$fileName,32,1925,18, this);
					eze$SettingTarget1.horizontalSpan = 3;
					egl.atLine(this.eze$$fileName,32,1883,62, this);
					eze$Temp7 = egl.eglx.lang.AnyValue.ezeCopyTo(eze$SettingTarget1, eze$Temp7);
				}
				egl.atLine(this.eze$$fileName,32,1870,75, this);
				this.GridLayout.setLayoutData({eze$$value : eze$Temp7, eze$$signature : "Torg/eclipse/edt/rui/widgets/gridlayoutdata;"});
				egl.atLine(this.eze$$fileName,32,1947,15, this);
				this.GridLayout.setCellPadding(4);
				egl.atLine(this.eze$$fileName,32,1964,8, this);
				this.GridLayout.setRows(1);
				egl.atLine(this.eze$$fileName,32,1974,11, this);
				this.GridLayout.setColumns(3);
				egl.atLine(this.eze$$fileName,32,1987,73, this);
				this.GridLayout.setChildren([(function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.sendButton,"org.eclipse.edt.rui.widgets.Button"), (function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.aboutButton,"org.eclipse.edt.rui.widgets.Button"), (function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.setNameButton,"org.eclipse.edt.rui.widgets.Button")].setType("[org.eclipse.edt.rui.widgets.Button"));
				egl.atLine(this.eze$$fileName,34,2086,16, this);
				this.request.setText("Request");
				egl.atLine(this.eze$$fileName,34,2104,20, this);
				egl.checkNull(this.request).getOnClick().appendElement(new egl.egl.jsrt.Delegate(this, egl.ui.MainView.prototype.getChat2));
				egl.atLine(this.eze$$fileName,36,2146,1, this);
				this.startAtId = 0;
				egl.atLine(this.eze$$fileName,37,2166,2, this);
				this.userId = "";
				egl.atLine(this.eze$$fileName,22,724,27, this);
				egl.checkNull(this).initialUI = [(function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.request,"org.eclipse.edt.rui.widgets.Button"), (function(x){ return x == null ? x : egl.eglx.ui.rui.Widget.fromWidget.apply( this, arguments );})(this.ui,"org.eclipse.edt.rui.widgets.GridLayout")].setType("[eglx.ui.rui.Widget");
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
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
			try { egl.enter("start",this,arguments);
				egl.atLine(this.eze$$fileName,43,2220,154, this);
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
		}
		,
		"userNameSupplied": function(value, e) {
			try { egl.enter("userNameSupplied",this,arguments);
				egl.addLocalFunctionVariable("value", value, "eglx.lang.EString", "value");
				egl.addLocalFunctionVariable("e", e, "eglx.ui.rui.Event", "e");
				egl.atLine(this.eze$$fileName,50,2462,98, this);
				if (((value) != "")) {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,51,2485,15, this);
						this.userId = value;
						egl.atLine(this.eze$$fileName,52,2507,13, this);
						if (!egl.debugg) egl.leave();
						return true;
					}finally{egl.exitBlock();}
				}
				else {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,54,2537,14, this);
						if (!egl.debugg) egl.leave();
						return false;
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,49,2384,184, this);
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
		}
		,
		"getChat2": function(e) {
			try { egl.enter("getChat2",this,arguments);
				egl.addLocalFunctionVariable("e", e, "eglx.ui.rui.Event", "e");
				egl.atLine(this.eze$$fileName,59,2611,10, this);
				this.getChat();
				egl.atLine(this.eze$$fileName,58,2576,53, this);
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
		}
		,
		"getChat": function() {
			try { egl.enter("getChat",this,arguments);
				var encoding;
				egl.addLocalFunctionVariable("encoding", encoding, "eglx.services.Encoding", "encoding");
				egl.atLine(this.eze$$fileName,63,2699,27, this);
				encoding = egl.checkNull(egl.eglx.services.Encoding).JSON;
				egl.setLocalFunctionVariable("encoding", encoding, "eglx.services.Encoding");
				var svc = egl.eglx.lang.SysLib.getResource("chattygabby");
				egl.addLocalFunctionVariable("svc", svc, "common.ChattyGabbyService", "svc");
				var rec = new egl.common.RequestRecord();
				egl.addLocalFunctionVariable("rec", rec, "common.RequestRecord", "rec");
				egl.atLine(this.eze$$fileName,65,2823,24, this);
				rec.action = "loadRecentPst";
				egl.atLine(this.eze$$fileName,65,2848,13, this);
				rec.startAtId = (function(x){ return x == null ? x : egl.eglx.lang.EString.fromEInt16.apply( this, arguments );})(0);
				var eze$Temp9 = null;
				eze$Temp9 = egl.eglx.lang.AnyValue.ezeCopyTo(rec, eze$Temp9);
				egl.atLine(this.eze$$fileName,67,2872,84, this);
				if (egl.enableEditing !== true) {
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
				}
				;
				egl.atLine(this.eze$$fileName,67,2956,1, this);
				;
				egl.atLine(this.eze$$fileName,62,2639,330, this);
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
		}
		,
		"chatsReturned": function(retResult, http) {
			try { egl.enter("chatsReturned",this,arguments);
				egl.addLocalFunctionVariable("retResult", retResult, "common.ChattyGabbyRecords", "retResult");
				egl.addLocalFunctionVariable("http", http, "eglx.http.IHttp", "http");
				egl.atLine(this.eze$$fileName,71,3058,73, this);
				egl.eglx.lang.SysLib.writeStdout((egl.eglx.lang.EString.plus("http.getResponse().body:", egl.checkNull(http.getResponse()).body)));
				egl.atLine(this.eze$$fileName,72,3137,231, this);
				try {
					try{egl.enterBlock();
						egl.atLine(this.eze$$fileName,73,3147,148, this);
						{
							try{egl.enterBlock();
								var i = 0;
								egl.addLocalFunctionVariable("i", i, "eglx.lang.EInt", "i");
								egl.atLine(this.eze$$fileName,73,3147,148, this);
								i = 1;
								egl.setLocalFunctionVariable("i", i, "eglx.lang.EInt");
								while (true) {
									var eze$Temp10 = false;
									var eze$Temp11;
									egl.atLine(this.eze$$fileName,73,3147,148, this);
									eze$Temp11 = ((i <= retResult.list.getSize()));
									egl.atLine(this.eze$$fileName,73,3147,148, this);
									if (eze$Temp11) {
										try{egl.enterBlock();
											egl.atLine(this.eze$$fileName,73,3147,148, this);
											eze$Temp10 = true;
										}finally{egl.exitBlock();}
									}
									if (!eze$Temp10) break;
									do {
										egl.setLocalFunctionVariable("i", i, "eglx.lang.EInt");
										try{egl.enterBlock();
											egl.atLine(this.eze$$fileName,74,3205,84, this);
											this.messageLogArea.setInnerHTML(((((egl.checkNull(this.messageLogArea).getInnerHTML()) + egl.checkNull(retResult.list, "list")[retResult.list.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)].txt)) + "<p>"));
										}finally{egl.exitBlock();}
										egl.atLine(this.eze$$fileName,73,3147,148, this);
									}
									while (false);
									if (!eze$Temp10) break;
									egl.atLine(this.eze$$fileName,73,3147,148, this);
									i = ((i + 1));
									egl.setLocalFunctionVariable("i", i, "eglx.lang.EInt");
								}
							}
							finally{egl.exitBlock();}
						}
					}finally{egl.exitBlock();}
				}
				catch ( eze$Temp14 )
				{
					if (eze$Temp14 instanceof egl.egl.debug.DebugTermination) {
						throw eze$Temp14;
					}
					{
						if (!(eze$Temp14 instanceof egl.eglx.lang.AnyException)) {
							eze$Temp14 = egl.makeExceptionFromCaughtObject(eze$Temp14);
						}
						var ex = eze$Temp14;
						{
							try{egl.enterBlock();
								egl.addLocalFunctionVariable("ex", ex, "eglx.lang.AnyException", "ex");
								egl.atLine(this.eze$$fileName,77,3331,31, this);
								egl.eglx.lang.SysLib.writeStdout(egl.checkNull(ex).message);
							}finally{egl.exitBlock();}
						}
					}
				}
				egl.atLine(this.eze$$fileName,70,2982,394, this);
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
		}
		,
		"chatsException": function(exp) {
			try { egl.enter("chatsException",this,arguments);
				egl.addLocalFunctionVariable("exp", exp, "eglx.lang.AnyException", "exp");
				var se;
				egl.addLocalFunctionVariable("se", se, "eglx.services.ServiceInvocationException", "se");
				egl.atLine(this.eze$$fileName,82,3468,33, this);
				se = egl.eglx.lang.EAny.ezeCast({eze$$value : exp, eze$$signature : egl.inferSignature(exp)}, egl.eglx.services.ServiceInvocationException);
				egl.setLocalFunctionVariable("se", se, "eglx.services.ServiceInvocationException");
				egl.atLine(this.eze$$fileName,83,3508,31, this);
				egl.eglx.lang.SysLib.writeStdout(egl.checkNull(se).detail1);
				egl.atLine(this.eze$$fileName,84,3545,31, this);
				egl.eglx.lang.SysLib.writeStdout(egl.checkNull(se).detail2);
				egl.atLine(this.eze$$fileName,85,3580,31, this);
				egl.eglx.lang.SysLib.writeStdout(egl.checkNull(se).detail3);
				egl.atLine(this.eze$$fileName,81,3386,233, this);
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
		}
		,
		"toString": function() {
			return "[MainView]";
		}
		,
		"eze$$getName": function() {
			return "MainView";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			return [
			{name: "GridLayoutLib", value : egl.org.eclipse.edt.rui.widgets.GridLayoutLib['$inst'], type : "org.eclipse.edt.rui.widgets.GridLayoutLib", jsName : "egl.org.eclipse.edt.rui.widgets.GridLayoutLib['$inst']"},
			{name: "ui", value : eze$$parent.ui, type : "org.eclipse.edt.rui.widgets.GridLayout", jsName : "ui"},
			{name: "sendButton", value : eze$$parent.sendButton, type : "org.eclipse.edt.rui.widgets.Button", jsName : "sendButton"},
			{name: "setNameButton", value : eze$$parent.setNameButton, type : "org.eclipse.edt.rui.widgets.Button", jsName : "setNameButton"},
			{name: "aboutButton", value : eze$$parent.aboutButton, type : "org.eclipse.edt.rui.widgets.Button", jsName : "aboutButton"},
			{name: "TextLabel", value : eze$$parent.TextLabel, type : "org.eclipse.edt.rui.widgets.TextLabel", jsName : "TextLabel"},
			{name: "messageTextField", value : eze$$parent.messageTextField, type : "org.eclipse.edt.rui.widgets.TextField", jsName : "messageTextField"},
			{name: "messageLogArea", value : eze$$parent.messageLogArea, type : "org.eclipse.edt.rui.widgets.Div", jsName : "messageLogArea"},
			{name: "GridLayout", value : eze$$parent.GridLayout, type : "org.eclipse.edt.rui.widgets.GridLayout", jsName : "GridLayout"},
			{name: "request", value : eze$$parent.request, type : "org.eclipse.edt.rui.widgets.Button", jsName : "request"},
			{name: "startAtId", value : eze$$parent.startAtId, type : "eglx.lang.EInt", jsName : "startAtId"},
			{name: "userId", value : eze$$parent.userId, type : "eglx.lang.EString", jsName : "userId"}
			];
		}
	}
);
