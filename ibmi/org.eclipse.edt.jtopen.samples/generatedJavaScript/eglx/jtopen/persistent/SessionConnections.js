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
			this.eze$$setEmpty();
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
		}
		,
		"createKey": function(system, userid) {
			var sessionid = "";
			var request;
			request = egl.eglx.http.ServletContext.getHttpServletRequest();
			if ((egl.eglx.lang.NullType.notEquals(request, null))) {
				var session;
				session = egl.checkNull(request).getSession();
				if ((egl.eglx.lang.NullType.notEquals(session, null))) {
					sessionid = session.getId();
				}
			}
			return ((((((((sessionid) + ".")) + system)) + ".")) + userid);
			;
		}
		,
		"toString": function() {
			return "[SessionConnections]";
		}
	}
);
