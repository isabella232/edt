/*******************************************************************************
 * Copyright © 2009, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

new egl.eglx.services.ServiceRT();
new egl.eglx.xml.XmlLib();
new egl.eglx.services.ServiceLib();

egl.defineClass('tools', 'ServiceMonitorRuntime', {
	"constructor" : function() { 
		this.inDevelopmentMode = egl.getValueForDebug;
	},
	"urldecode" : function(s) { 
		return unescape(s).replace(/</g, "&lt;");
	}
});

egl.eglx.services.$ServiceRT.doInvokeAsync = function( /*IHttp*/ http, 
		/*HttpCallback*/ callback, 
		/*HttpCallback*/ errCallback, 
		/*handler*/ handler ) {
	var info = new egl.tools.ServiceMonitorInfo();
	info.request = http.request.eze$$clone();
	egl.org.eclipse.edt.rui.infobus.InfoBus.$inst.publish("service.monitor.call", info);
	info.startTime = new Date().toString();
	info._start = new Date().getTime();
	
	function getErrorMessage(s) {
		if (s == "egl.core.ServiceInvocationException")
			s = "Server error";
		return s;
	}
	this.doInvokeInternal(
		http, 
		function(response){
			egl.startNewWork();
			try{
				try {
					info.response = http.response;
					info.endTime = new Date().toString();
					info.duration = new Date().getTime() - info._start;
					if (http.response.status >= 200 && http.response.status <= 300) {
						info.object = egl.boxAny(""+http.response.body);
						egl.org.eclipse.edt.rui.infobus.InfoBus.$inst.publish("service.monitor.success", info);
					}
					else {
						egl.org.eclipse.edt.rui.infobus.InfoBus.$inst.publish("service.monitor.error", info);
					}
				}
				catch(e) {
					egl.printError("Error in service monitor listener", e);
				}

			}
			finally {}
			callback.call( handler, http );
		}, 
		function(exception, response){
			egl.startNewWork();
			info.response = http.response;
			for (f in http.response.headers) {
				if (egl.isUserField(http.response.headers, f))
					egl.valueByKey(info.response.headers, f, http.response.headers[f], "S;");
			}
			info.errorDetail = getErrorMessage(egl.toString(exception));
			info.endTime = new Date().toString();
			info.duration = new Date().getTime() - info._start;
			try {
				egl.org.eclipse.edt.rui.infobus.InfoBus.$inst.publish("service.monitor.error", info);
			}
			catch(e) {
				egl.printError("Error in service monitor listener", e);
			}
			errCallback.call(handler, exception, http);
		},
		true
	);
};
