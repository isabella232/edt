/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
if(egl.eglx.rest === undefined){
	egl.eglx.rest = {};
}
egl.eglx.rest.invokeEglService = function(http,
										serviceName,
										methodName,
										inData, 
										inDatatypes,
										inFunctionParameterNames,
										returnTypes,
										callbackFunction,
										errorCallbackFunction){
	var eglRpcRestRequest = new egl.eglx.lang.EDictionary();
	eglRpcRestRequest.method = methodName;
	eglRpcRestRequest.params = inData;
	http = egl.eglx.lang.EAny.unbox(http);
	if(!(http instanceof egl.eglx.http.HttpRest)){
		throw egl.createRuntimeException("CRRUI3665E", []);
	}
	if(http instanceof egl.eglx.http.HttpProxy &&
			(http.request.uri === null || http.request.uri.length === 0) &&
			serviceName === null){
		throw egl.createRuntimeException("CRRUI3664E", []);
	}
	else if(http instanceof egl.eglx.http.HttpProxy &&
			(http.request.uri === null || http.request.uri.length === 0) &&
			serviceName !== null){
		http.request.uri = serviceName;	
	}
	egl.eglx.services.$ServiceRT.encodeResquestBody(http.request, eglRpcRestRequest);
	egl.eglx.services.$ServiceRT.internalInvokeService(http, returnTypes, callbackFunction, errorCallbackFunction, null);
};
egl.eglx.rest.invokeService = function(http, 
											firstInDataNotInURL,
											inData, 
											inDatatypes,
											inFunctionParameterNames,
											returnTypes,
											callbackFunction,
											errorCallbackFunction){
	/*
					[str1, str2, eze$Temp1, eze$Temp2],
					["egl.eglx.lang.EString", "egl.eglx.lang.EInt32", "egl.eglx.lang.EInt16", "egl.eglx.lang.EFloat64"],
					["myarg1", "myArg2", "myArg3", "myArg4"],
					["egl.eglx.lang.EInt16", "egl.eglx.lang.EString"],
					"GET",
					//http://www.ibm.com/{myarg1}/{myArg2}?item={myArg4}
					"http://www.ibm.com/" + egl.eglx.http.HttpLib.convertToURLEncoded(egl.eglx.lang.EString.fromEString(str1)) + "/" + egl.eglx.http.HttpLib.convertToURLEncoded(egl.eglx.lang.EString.fromEInt32(str2)) + "?item=" + egl.eglx.http.HttpLib.convertToURLEncoded(egl.eglx.lang.EString.ezeCast(eze$Temp2)),
					egl.formatNONE, "requst charset", " request content types", egl.formatNONE, "response Charset", "response Content Type",
					eze$Temp1, handleF2Resonse, egl.eglx.services.ServiceLib['$inst'].serviceExceptionHandler);

	 */
	http = egl.eglx.lang.EAny.unbox(http);
	if(!(http instanceof egl.eglx.http.HttpRest)){
		throw egl.createRuntimeException("CRRUI3665E", []);
	}
	else if(http instanceof egl.eglx.http.HttpProxy){
		throw egl.createRuntimeException("CRRUI3666E", []);
	}
	http.restType = egl.eglx.rest.ServiceType.TrueRest;
	if(firstInDataNotInURL == null && http.request.userUri != undefined && http.request.userUri){
		if(http.request.uri != undefined && http.request.uri != null){
			for(var idx = 0; idx < inFunctionParameterNames.length; idx++){
				var indexOf = http.request.uri.indexOf(http.request.uri.indexOf("{" + inFunctionParameterNames[idx] + "}"));
				if(idx == 0 && 
						(inFunctionParameterNames[idx].indexOf("http://") != -1 || inFunctionParameterNames[idx].indexOf("https://") != -1)&&
						http.request.uri.indexOf("{" + inFunctionParameterNames[idx] + "}") > -1){
					http.request.uri.replace("{" + inFunctionParameterNames[idx] + "}", egl.toString(inData[idx]));
				}
				else if(http.request.uri.indexOf("{" + inFunctionParameterNames[idx] + "}") > -1){
					http.request.uri.replace("{" + inFunctionParameterNames[idx] + "}", egl.eglx.http.HttpLib.convertToURLEncoded(egl.toString(inData[idx])));
				}
				else{
					firstInDataNotInURL = inData[idx];
				}
			}
		}
	}
	egl.eglx.services.$ServiceRT.encodeResquestBody(http.request, firstInDataNotInURL);
	egl.eglx.services.$ServiceRT.internalInvokeService(http, returnTypes, callbackFunction, errorCallbackFunction, null);
};
egl.eglx.rest.configHttp = function(http, 
									requestConfig,
									responseConfig){
	http = egl.eglx.lang.EAny.unbox(http);
	if(http === undefined || http === null){
		http = new egl.eglx.http.HttpRest;
	}
	else{
		http = http.eze$$clone();
	}
	http.response.status = null;
	http.response.statusMessage = null;
	http.response.body = null;
	if(http.request.uri === null || http.request.uri.length == 0){
		http.request.uri = requestConfig.uri;
	}
	else{
		http.request.uri += requestConfig.uri;
		http.request.userUri = requestConfig.uri.indexOf("http://") != -1 || requestConfig.uri.indexOf("https://") != -1;
	}
	if(http.request.method === null)
		http.request.method = requestConfig.method;
	if(http.request.encoding === null)
		http.request.encoding = requestConfig.encoding;
	if(http.request.charset === null) 
		http.request.charset = requestConfig.charset;
	if(http.request.contentType === null) 
		http.request.contentType = requestConfig.contentType;
	if(http.response.encoding === null)
		http.response.encoding = responseConfig.encoding;
	if(http.response.charset === null)
		http.response.charset = responseConfig.charset;
	if(http.response.contentType === null)
		http.response.contentType = responseConfig.contentType;
	if(http.request.headers === null)
		http.request.headers = new egl.eglx.lang.EDictionary();
	return http;
};
