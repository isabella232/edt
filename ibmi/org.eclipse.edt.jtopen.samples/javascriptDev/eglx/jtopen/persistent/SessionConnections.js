if (egl.eze$$userLibs) egl.eze$$userLibs.push('eglx.jtopen.persistent.SessionConnections');
else egl.eze$$userLibs = ['eglx.jtopen.persistent.SessionConnections'];
egl.defineRUILibrary('eglx.jtopen.persistent', 'SessionConnections',
{
	'eze$$fileName': 'eglx/jtopen/persistent/SessionConnections.egl',
	'eze$$runtimePropertiesFile': 'eglx.jtopen.persistent.SessionConnections',
		"constructor": function() {
			if(egl.eglx.jtopen.persistent.SessionConnections['$inst']) return egl.eglx.jtopen.persistent.SessionConnections['$inst'];
			egl.eglx.jtopen.persistent.SessionConnections['$inst']=this;
			this.eze$$setInitial();
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
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("SessionConnections", null, false);
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
		"getSessionConnection": function(system, userid, password) {
			try { egl.enter("getSessionConnection",this,arguments);
				egl.addLocalFunctionVariable("system", system, "eglx.lang.EString", "system");
				egl.addLocalFunctionVariable("userid", userid, "eglx.lang.EString", "userid");
				egl.addLocalFunctionVariable("password", password, "eglx.lang.EString", "password");
				egl.atLine(this.eze$$fileName,9,153,93, this);
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
		}
		,
		"createKey": function(system, userid) {
			try { egl.enter("createKey",this,arguments);
				egl.addLocalFunctionVariable("system", system, "eglx.lang.EString", "system");
				egl.addLocalFunctionVariable("userid", userid, "eglx.lang.EString", "userid");
				var sessionid = "";
				egl.addLocalFunctionVariable("sessionid", sessionid, "eglx.lang.EString", "sessionid");
				var request;
				egl.addLocalFunctionVariable("request", request, "javax.servlet.http.HttpServletRequest", "request");
				egl.atLine(this.eze$$fileName,15,401,38, this);
				request = egl.eglx.http.ServletContext.getHttpServletRequest();
				egl.setLocalFunctionVariable("request", request, "javax.servlet.http.HttpServletRequest");
				egl.atLine(this.eze$$fileName,16,443,135, this);
				if ((egl.eglx.lang.NullType.notEquals(request, null))) {
					try{egl.enterBlock();
						var session;
						egl.addLocalFunctionVariable("session", session, "javax.servlet.http.HttpSession", "session");
						egl.atLine(this.eze$$fileName,17,488,20, this);
						session = egl.checkNull(request).getSession();
						egl.setLocalFunctionVariable("session", session, "javax.servlet.http.HttpSession");
						egl.atLine(this.eze$$fileName,18,513,59, this);
						if ((egl.eglx.lang.NullType.notEquals(session, null))) {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,19,537,28, this);
								sessionid = session.getId();
								egl.setLocalFunctionVariable("sessionid", sessionid, "eglx.lang.EString");
							}finally{egl.exitBlock();}
						}
					}finally{egl.exitBlock();}
				}
				egl.atLine(this.eze$$fileName,22,581,48, this);
				if (!egl.debugg) egl.leave();
				return ((((((((sessionid) + ".")) + system)) + ".")) + userid);
				egl.atLine(this.eze$$fileName,22,629,1, this);
				;
				egl.atLine(this.eze$$fileName,13,251,384, this);
				if (!egl.debugg) egl.leave();
			} finally {
				if (!egl.debugg){
				} else { egl.leave(); }
			}
		}
		,
		"toString": function() {
			return "[SessionConnections]";
		}
		,
		"eze$$getName": function() {
			return "SessionConnections";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			return [
			];
		}
	}
);
