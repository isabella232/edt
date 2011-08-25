/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
egl.defineClass(
	'eglx.services', 'ServiceLib',
{
	"constructor" : function() {				
	}
});
egl.eglx.services.ServiceLib["throwExceptionIfNecessary"] = function(/*Object*/object, /*String*/fieldName, /*boolean*/isNullable) {
	if (object === undefined) {
		throw "TODO: make an exception for this";//throw egl.createRuntimeException("CRRUI2105E", [ fieldName ]);
	}

	if (isNullable == false && object === null) {
		throw "TODO: make an exception for this";//throw egl.createRuntimeException("CRRUI2106E", [ fieldName ]);
	}
};
egl.eglx.services.ServiceLib["bindService"] = function(/*String*/bindingKeyName, /*String*/eglddName) {
	if(eglddName === undefined || eglddName === null){
		eglddName = egl__defaultDeploymentDescriptor;
	}
	var binding = egl.eglx.services.$ServiceBinder.getBinding(eglddName.toLowerCase(), bindingKeyName);
	var ret = undefined;
	if(binding instanceof egl.eglx.services.RestBinding){
		ret = new egl.eglx.http.HttpREST();
		ret.request.uri = binding.baseURI;
		ret.invocationType = egl.eglx.rest.RestType.EglRpc;
	}
	return ret;
};
egl.eglx.services.ServiceLib["serviceExceptionCallback"] = function(/*Tegl/lang/AnyException;*/exp, http) {
	var headerMsg = "";
	var bodyMsg = "";
	try {
		var isJSONRPC = egl.eglx.services.$ServiceRT.isJsonRPCFormat(http);

		if (http.request) {
			headerMsg = http.request.uri;
			bodyMsg = http.request.body;
			if (isJSONRPC == true) {
				//the body is in json format
				var d = egl.createDictionary(false, false);
				if (bodyMsg != null && bodyMsg != "") {
					egl.eglx.json.JsonLib.convertFromJSON(bodyMsg, d);
					var methodName = (egl.unboxAny(egl.valueByKey(d, "method"))).toString();
					headerMsg += " on method: " + methodName;
				}
			}
			//else    		//the body could be in xml, string, formdata...
		}
	} catch (e) {
		egl
				.println("Exception occurred while parsing the original request.");
		egl.println(e.message);
	}

	egl.println(" ");
	egl.println("An exception has occurred while calling " + headerMsg);
	egl.println("The body of the request was: " + bodyMsg);
	egl.println("message:" + exp.message);
	if (exp instanceof egl.eglx.services.ServiceInvocationException) {
		var serviceKind = egl.eglx.services.$ServiceRT.serviceKind(exp);
		egl.println("ServiceKind:" + serviceKind);
		egl.println("detail1:" + exp.detail1);
		egl.println("detail2:" + exp.detail2);
		egl.println("detail3:"
				+ exp.detail3.replace(/&/g, "&amp;").replace(/</g,
						"&lt;").replace(/>/g, "&gt;"));
	}
	return;
}; 
/**
 * system variable, delegate for the default service exception callback function
 */
egl.eglx.services.ServiceLib.serviceExceptionHandler = new egl.egl.jsrt.Delegate(this, egl.eglx.services.ServiceLib.serviceExceptionCallback, "serviceExceptionCallback");
