/*******************************************************************************
 * Copyright © 2009, 2013 IBM Corporation and others.
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

egl.original$$doInvokeInternal = egl.eglx.services.$ServiceRT.doInvokeInternal;

egl.eglx.services.$ServiceRT.doInvokeInternal = function( /*HttpRequest*/ request, 
    							   /*String*/ serviceCallTypeIn,
                                   /*function*/ callback, 
                                   /*function*/ errCallback,                     
                                   /*boolean*/ asynchronous) {
	var requestClone = new egl.egl.core.HttpRequest();
	requestClone.uri = request.uri;
	requestClone.body = request.body;
	requestClone.method = request.method;
	requestClone.queryParameters = request.queryParameters;
	requestClone.headers = request.headers;
	var info = new egl.tools.ServiceMonitorInfo();
	info.request = requestClone;
	egl.com.ibm.egl.rui.infobus.InfoBus.$inst.publish("service.monitor.call", info);
	info.startTime = new Date().toString();
	info._start = new Date().getTime();
	
	function getErrorMessage(s) {
		if (s == "egl.core.ServiceInvocationException")
			s = "Server error";
		return s;
	}
	
	egl.original$$doInvokeInternal.call(this, 
		request, 
		serviceCallTypeIn, 
		function(response) { 
			try {
				egl.enter('Handling service response for '+request.uri, this, arguments)
				try {
					var responseClone = new egl.egl.core.HttpResponse();
					responseClone.headers = egl.createDictionary();
					for (f in response.headers) {
						if (egl.isUserField(response.headers, f))
							egl.valueByKey(responseClone.headers, f, response.headers[f], "S;");
					}
					responseClone.body = response.body;
					responseClone.status = response.status;
					info.response = responseClone;
					info.endTime = new Date().toString();
					info.duration = new Date().getTime() - info._start;
					if (response.status >= 200 && response.status <= 300) {
						info.object = egl.boxAny(""+response.body);
						egl.com.ibm.egl.rui.infobus.InfoBus.$inst.publish("service.monitor.success", info);
					}
					else {
						try {
							var msg = response.body.replace(/.*"name" : "/, "").replace(/".*/, "");
							info.errorDetail = getErrorMessage(msg)+": "+response.statusMessage;
						}
						catch (e) {
							info.errorDetail = e.message+" "+response.body.replace(/\\/g, "");
						}
						egl.com.ibm.egl.rui.infobus.InfoBus.$inst.publish("service.monitor.error", info);
					}
				}
				catch(e) {
					egl.printError("Error in service monitor listener", e);
				}
			}
			finally {
				egl.leave();
			}
			callback(response);
	    }, 
		function(exception) { 
			try {
				egl.enter('Handling service error for '+request.uri, this, arguments)
				info.response = egl.eglx.services.$ServiceLib.callBackResponse;
				info.errorDetail = getErrorMessage(egl.toString(exception));
				info.endTime = new Date().toString();
				info.duration = new Date().getTime() - info._start;
				try {
					egl.com.ibm.egl.rui.infobus.InfoBus.$inst.publish("service.monitor.error", info);
				}
				catch(e) {
					egl.printError("Error in service monitor listener", e);
				}
				errCallback(exception);
			}
			finally {
				egl.leave();	
			}
	    }, 
		asynchronous
	);
	
};
