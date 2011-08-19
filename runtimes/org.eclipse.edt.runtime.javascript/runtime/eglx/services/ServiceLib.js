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
egl.eglx.services.ServiceLib["convertObjectToDictionary"] = function(/*Object*/object, /*Dictionary*/dictionary) {
	for (f in object) {
		var name = f;
		var dictObject = egl.eglx.services.$ServiceLib
				.getDictionaryObject(object[name]);
		egl.valueByKey(dictionary, name, dictObject, egl
				.inferSignature(dictObject));
	}
};
egl.eglx.services.ServiceLib["getDictionaryObject"] = function(/*Object*/dictObject) {
	if (dictObject !== null && typeof dictObject == "object") {
		if (dictObject instanceof Array) {
			var dictArray = new Array();
			for ( var i = 0; i < dictObject.length; i++) {
				dictArray.push(egl.eglx.services.$ServiceLib
						.getDictionaryObject(dictObject[i]));
			}
			return dictArray;
		} else {
			var dict = egl.createDictionary(true, false);
			dict.eze$$fromJson(dictObject);
			return dict;
		}
	} else {
		return dictObject;
	}
};
egl.eglx.services.ServiceLib["bindService"] = function(/*String*/eglddName, /*String*/bindingKeyName) {
	var binding = egl.egl.jsrt.$ServiceBinder.getBinding(eglddName,
			bindingKeyName);
	return binding.createServiceWrapper();
};
egl.eglx.services.ServiceLib["serviceExceptionCallback"] = function(/*Tegl/lang/AnyException;*/exp) {
	var headerMsg = "";
	var bodyMsg = "";
	try {
		var reqObj = egl.eglx.services.$ServiceLib.getOriginalRequest();
		var isJSONRPC = egl.eglx.services.$ServiceRT
				.isJsonRPCFormat(reqObj);

		if (reqObj) {
			headerMsg = reqObj.uri;
			bodyMsg = reqObj.body;
			if (isJSONRPC == true) {
				//the body is in json format
				var d = egl.createDictionary(false, false);
				if (bodyMsg != null && bodyMsg != "") {
					egl.eglx.json.JsonLib.convertFromJSON(bodyMsg, d);
					var methodName = (egl.unboxAny(egl.valueByKey(d,
							"method"))).toString();
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
		var serviceKind = egl.eglx.services.$ServiceLib
				.serviceKind(exp);
		egl.println("ServiceKind:" + serviceKind);
		egl.println("detail1:" + exp.detail1);
		egl.println("detail2:" + exp.detail2);
		egl.println("detail3:"
				+ exp.detail3.replace(/&/g, "&amp;").replace(/</g,
						"&lt;").replace(/>/g, "&gt;"));
	}
	return;
}; 
egl.eglx.services.ServiceLib["serviceKind"] = function(/*Teglx/services/ServiceInvocationException;*/sie) {
	var $result = "";
	switch (sie.source) {
	case egl.core.ServiceKind.WEB:
		$result = "WEB";
		return $result;
		break;
	case egl.core.ServiceKind.NATIVE:
		$result = "NATIVE";
		return $result;
		break;
	case egl.core.ServiceKind.EGL:
		$result = "EGL";
		return $result;
		break;
	case egl.core.ServiceKind.REST:
		$result = "REST";
		return $result;
		break;
	default:
		$result = "unknown";
		return $result;
		break;
	}
	return $result;
};
