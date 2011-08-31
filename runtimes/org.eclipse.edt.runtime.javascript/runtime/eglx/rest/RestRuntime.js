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
    'eglx.rest', 'RESTServiceRefWrapper',
    'eglx.services', 'ServiceRefWrapper',
{        
    //String f_baseURI;   
    //String f_statefulSessionIdName;
    //String f_uriTemplate;
    
    "constructor" : function(/*String*/ name, /*String*/ baseURI, /*String*/ eglStatefulSessionIdName) {
    	this.f_bindingName = name || "";
        this.f_baseURI = baseURI || "";
        this.f_statefulSessionIdName = eglStatefulSessionIdName || "JSESSIONID";
        this.f_uriTemplate="";    
        this.f_type = "rest";
        this.f_setSessionCookie = false;		//only used for EGL JSON-RPC service call
    },    
        
    "invokeService" : function(/*RUIHandler*/ handler,
					            /*func*/ httpCallback, 
					            /*func*/ errorCallback, 
					            /*int*/ serviceTimeout,
					            /*String*/ functionName,						            
					            /*Array of Objects*/ inParamVals,
					            /*Array of signature strings*/ inParamSignatures,
					            /*String*/ paramOrders,
					            /*Array of Functions or Array*/ callbackArgs, 
					            
			//the following parameters are only used for Rest service call
					            /*boolean*/ hasXXXRestAnnotation,
					            /*String*/ resolvedUriTemplate,
					            /*int*/ requestFormat,
					            /*int*/ responseFormat,
					            /*String*/ restMethod,                        
					            /*String, Dictionary, Record or XMLElement*/ resourceParamIn){
    	
    	this.setServiceTimeoutInHeader(serviceTimeout);
    	if(hasXXXRestAnnotation == true){
    		this.invokeRESTService(handler, 
				    				httpCallback, 
				    				errorCallback,
				    				resolvedUriTemplate,
				    				requestFormat, responseFormat, restMethod,
				    				resourceParamIn, 
				    				callbackArgs);
    	}
    	else{
    	    this.invokeEGLService(handler, 
    	    						httpCallback, 
    	    						errorCallback, 
    	    						functionName,
    	    						inParamVals,
    	    						inParamSignatures,
    	    						callbackArgs);    		
    	}
    },
    /**
     * handler is the RUIHandler, is used as context for the callback function 
     * inParamVals is a array of ordered in and inout parameter values
     * the request and response uses json-rpc
     */
    "invokeEGLService" : function(
                        /*RUIHandler*/ handler,
                        /*func*/ httpCallback, 
                        /*func*/ errorCallback, 
                        /*String*/ functionName,  
                        /*Array of Objects*/ inParamVals,
                        /*Array of signature strings*/ inParamSignatures,
                        /*Array of Functions or Array*/ callbackArgs){

        egl.valueByKey( this.getRESTRequestHeader(), egl.HEADER_EGLREST, "TRUE", "S;" );  
        
        //set the session id for stateful calls
        egl.valueByKey( this.getRESTRequestHeader(), egl.STATEFULSESSIONID, this.f_statefulSessionIdName, "S;" );                
        var sessionIdObj = egl.findByKey(egl.statefulSessionMap, this.f_baseURI);
        if(sessionIdObj != null && sessionIdObj != undefined){
        	var sessionIdValue = sessionIdObj.sidVal;
        	
        	var cookieValue = egl.findByKey(this.getRESTRequestHeader(), "Cookie");
        	if(cookieValue != null && cookieValue != undefined){	
	        	cookieValue = cookieValue.eze$$value;
        	}
        	else
        		cookieValue = "";
        	
        	if(!this.f_setSessionCookie){        		
        		if(cookieValue != ""){
               		//if there already exists a cookie, append to it
            		cookieValue += "; ";
        		}        			        			
            	var newCookieValue = cookieValue + sessionIdValue;        		
         		
            	egl.valueByKey( this.getRESTRequestHeader(), "Cookie", newCookieValue, "S;");
            	this.f_setSessionCookie = true;        		
        	}
        	else{
        		if(sessionIdObj.oldVal != null && sessionIdObj.oldVal != undefined){
        			//need to parse the existing cookieValue, replace with the new value        			
        			var toBeReplaced = new RegExp(sessionIdObj.oldVal, "i");        			
        			cookieValue = cookieValue.replace(toBeReplaced, sessionIdValue);
        			
                	egl.valueByKey( this.getRESTRequestHeader(), "Cookie", cookieValue, "S;");
                	this.f_setSessionCookie = true;        		        			
        		}
        		//else if sessionIdObj.oldVal == null && this.f_setSessionCookie == true, 
        		//the session cookie is already set in the header, do nothing 
        	}
        	if( sessionIdObj.httpSessionId != null ){
            	egl.valueByKey( this.getRESTRequestHeader(), egl.HTTP_SESSION_ID_KEY, sessionIdObj.httpSessionId, "S;");
        	}
        }        
        
        //uses json-rpc     
        this.f_RESTmethod = 'POST';
        this.f_requestFormat = egl.formatJSON;
        this.f_responseFormat = egl.formatJSON;                     
                        
        this.f_httpCallback = httpCallback;
        this.f_errCallback = errorCallback;
        this.f_callbackArgs = callbackArgs;
        var url = this.f_baseURI;
        
        //let's build the payload body using json-rpc
        var payloadbodyObj = {
            bindingName: this.f_bindingName,
            method: functionName || "",
            params: inParamVals || [],
            eze$$InParamSignatures: inParamSignatures || []
        };
        
        var body = egl.eglx.json.toJSONString(payloadbodyObj);        
        egl.eglx.services.$ServiceRT.internalInvokeService(
                    handler,
                    url,                     //no uri
                    this, 
                    {},                     //pass null for query parameters 
                    body);        
    }    
    
});
egl.eglx.rest.invokeEglService = function(http,
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
					["egl.egl.lang.EString", "egl.egl.lang.EInt32", "egl.egl.lang.EInt16", "egl.egl.lang.EFloat64"],
					["myarg1", "myArg2", "myArg3", "myArg4"],
					["egl.egl.lang.EInt16", "egl.egl.lang.EString"],
					"GET",
					//http://www.ibm.com/{myarg1}/{myArg2}?item={myArg4}
					"http://www.ibm.com/" + egl.eglx.http.HttpLib.convertToURLEncoded(egl.egl.lang.EString.fromEString(str1)) + "/" + egl.eglx.http.HttpLib.convertToURLEncoded(egl.egl.lang.EString.fromEInt32(str2)) + "?item=" + egl.eglx.http.HttpLib.convertToURLEncoded(egl.egl.lang.EString.ezeCast(eze$Temp2)),
					egl.formatNONE, "requst charset", " request content types", egl.formatNONE, "response Charset", "response Content Type",
					eze$Temp1, handleF2Resonse, egl.eglx.services.ServiceLib['$inst'].serviceExceptionHandler);

	 */
	http.invocationType = egl.eglx.services.ServiceType.TrueRest;
	if(firstInDataNotInURL == null && http.request.userUri != undefined && http.request.userUri){
		if(http.request.uri != undefined && http.request.uri != null){
			for(idx = 0; idx < inFunctionParameterNames.length; idx++){
				var indexOf = http.request.uri.indexOf(http.request.uri.indexOf("{" + inFunctionParameterNames[idx] + "}"));
				if(idx == 0 && 
						egl.toString(inFunctionParameterNames[idx].indexOf("http://")) &&
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
	
	if(http === undefined || http === null){
		http = new egl.eglx.http.Http;
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
		http.request.userUri = true;
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
