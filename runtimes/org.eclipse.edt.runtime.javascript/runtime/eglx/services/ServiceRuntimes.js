/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
 
 
//const strings
egl.STATEFULSESSIONID = "egl_statefulsessionid";
egl.HTTP_SESSION_ID_KEY = "egl.gateway.session.id";
egl.HEADER_EGLDEDICATED = "EGLDEDICATED";
egl.HEADER_RESPONSE_CHARSET = "edt.service.response.charset";
egl.HEADER_CONTENT_TYPE = "Content-Type";
egl.HEADER_EGLREST = "EGLREST";
egl.HEADER_EGLSOAP = "EGLSOAP";
egl.CUSTOM_HEADER_EGLSOAP = "CUSTOMEGLSOAPREQUESTHEADER";
egl.CUSTOM_RESPONSE_HEADER_EGLSOAP = "CUSTOMEGLSOAPRESPONSEHEADER";
egl.CUSTOM_DICT_HEADERS = "DictHeaders";
egl.SERVICE_TIMEOUT = "EGL_TIMEOUT";
    

egl.defineClass(
    'eglx.services', 'ServiceRT',
{
    "constructor" : function() {
        if (egl.eglx.services.$ServiceRT) 
            return egl.eglx.services.$ServiceRT;         
        egl.eglx.services.$ServiceRT=this;
        
        this.eze$$proxyURL = '___proxy';        
    },
    
    "setRequestContentTypeHeaderIfNotSet" : function(/*HTTPRequest*/ httpResquest){
        //if it already has content-type in the header, then do not do anything
        //otherwise, set the content type based on the request format
        var hasReqContentType = false;
        if(httpResquest.headers != null){
        	hasReqContentType = egl.containsKey(httpResquest.headers, egl.HEADER_CONTENT_TYPE);
        }
        else{
        	httpResquest.headers = {};
        }
        if(hasReqContentType == false){
        	var contentType = httpResquest.contentType;
        	if(contentType === null){
	        	contentType = "text/html";
	        	if(httpResquest.encoding === egl.eglx.services.Encoding.JSON){
	        		contentType = "application/json";
	        	}
	        	else if(httpResquest.encoding === egl.eglx.services.Encoding.XML){
	        		contentType = "application/xml";   
	        	}
	        	else if(httpResquest.encoding === egl.eglx.services.Encoding._FORM){
	        		contentType = "application/x-www-form-urlencoded";                    
	        	}
        	}
        	if(httpResquest.charset === null){
        		contentType += "; charset=UTF-8";
        	}
        	else{
        		contentType += "; charset=";
        		contentType += httpResquest.charset;
        	}
            egl.valueByKey( httpResquest.headers, egl.HEADER_CONTENT_TYPE, contentType);                    
        }
    },

    "isJsonRPCFormat" : function(/*HttpRequest*/ http){
    	return http instanceof egl.eglx.http.HttpRest &&
    		(http.restType === egl.eglx.rest.ServiceType.EglDedicated ||
    				http.restType === egl.eglx.rest.ServiceType.EglRpc);
    },
    
    /**
     * This is an overloaded function with the following patterns:     
            Function internalInvokeService( handler RUIHandler, uri String, serviceWrapper, parameters Dictionary) end 
            Function internalInvokeService( handler RUIHandler, uri String, serviceWrapper, parameters Dictionary, body String) end        
     */              
    "internalInvokeService" : function(/*Http or HttpSOAP*/ http, 
    									callbackArgs,
							    		callbackFunction,
										errorCallbackFunction,
										/*RUIHandler*/ handler) {
    	//make a shallow copy of some of the properties on the serviceWrapper, 
    	//these copies will be used in the callback function, this way, even if serviceWrapper object got changed by other event
    	//the values used in the callbacks are accurate 
        var requestExp = null;
        try {
            this.setRequestContentTypeHeaderIfNotSet(http.request);           
        }
        catch (e) {
    		requestExp = egl.createAnyException("CRRUI3654E", [e]);    		
        }       
        
        if(http.response.charset !== undefined && http.response.charset !== null){
            egl.valueByKey( http.request.headers, egl.HEADER_RESPONSE_CHARSET, http.response.charset);                    
        }
        if(requestExp != null){
        	egl.eglx.services.$ServiceRT.callErrorCallback(errorCallbackFunction, handler, requestExp, http);
        }
        else{
	        this.doInvokeAsync( 
	            http,
	            function(http) {
	            	var isJSONRPC = egl.eglx.services.$ServiceRT.isJsonRPCFormat(http);
	            	
	            	var anyExp = null;
	                try {                	
	                    if(http.response.status >= 200 && http.response.status <= 299){
	                    	var sessionIdValue = http.response.headers === null ? undefined : egl.findByKey(http.response.headers, egl.STATEFULSESSIONID);
	                    	if(sessionIdValue != null &&  sessionIdValue != undefined){
		                    	var httpSessionIdValue = egl.findByKey(http.response.headers, egl.HTTP_SESSION_ID_KEY);
		                    	if(httpSessionIdValue == undefined){
		                    		httpSessionIdValue = null;
		                    	}
	                    		//try to get the current value
	                    		var currVal = egl.statefulSessionMap[http.request.uri];
	                    		if(currVal != null && currVal != undefined){
	                    			//compare to see if it's the same as the new one
	                    			if(sessionIdValue != currVal.sidVal){
	                    				currVal.oldVal = currVal.sidVal;
	                    				currVal.sidVal = sessionIdValue;
	                    			}
	                    			else //reset old value to null, since oldVal is only used to indicate the session value has changed
	                    				currVal.oldVal = null;
		                    		currVal.httpSessionId = httpSessionIdValue;
	                    		}
	                    		else{
	                    			var sessionIdObj = new Object();
	                    			sessionIdObj.sidVal = sessionIdValue;
	                    			sessionIdObj.oldVal = sessionIdValue;
		                    		sessionIdObj.httpSessionId = httpSessionIdValue;
	                    			egl.statefulSessionMap[http.request.uri] = sessionIdObj;
	                    		}
	                    	}
	                    	
/*	                    	var customSOAPResponseHeaders = egl.findByKey(http.response.headers, egl.CUSTOM_RESPONSE_HEADER_EGLSOAP);
	                    	if(customSOAPResponseHeaders != null &&  customSOAPResponseHeaders != undefined){
	                    		egl.valueByKey(serviceWrapper.getRESTRequestHeader(), egl.CUSTOM_RESPONSE_HEADER_EGLSOAP, customSOAPResponseHeaders,"S;");
	                    	}
*/	                    	
	                    	var encoding = http.response.encoding;
	                    	if(encoding === null || encoding === egl.eglx.services.Encoding.USE_CONTENTTYPE){
	                    		encoding = null;
	                        	var responseContentType;
	                        	if(http.response.headers != null){
	                        		responseContentType = egl.findByKey(http.response.headers, egl.HEADER_CONTENT_TYPE);
	                        	}
	                        	if(responseContentType !== undefined && responseContentType !== null){
	                        		responseContentType = responseContentType.toLowerCase();
	                        		if(responseContentType.indexOf("application/json") != -1){
	                        			encoding = egl.eglx.services.Encoding.JSON;
	                        		}
	                        		else if(responseContentType.indexOf("application/xml") != -1){
	                        			encoding = egl.eglx.services.Encoding.XML;
	                        		}
	                        		else if(responseContentType.indexOf("application/form-urlencoded") != -1){
	                        			encoding = egl.eglx.services.Encoding._FORM;
	                        		}
	                        	}
	                        	if(encoding === null){
	                        		if(responseContentType === undefined){
	                        			responseContentType = "undefined";
	                        		}
	                        		else if(responseContentType === null){
	                        			responseContentType = "null";
	                        		}
	                    			throw egl.createRuntimeException("CRRUI3662E", [responseContentType]);
	                        	}
	                    	}
		                    if(encoding === egl.eglx.services.Encoding.NONE){
		                        if(callbackArgs.length > 0){
		                        	var paramName = callbackArgs[callbackArgs.length-1].paramName;
		                        	try{
	       		                    	if(egl.unboxAny(http.response.body) === null){
	       		                    		egl.eglx.services.ServiceRT.checkSignatureForNull(callbackArgs[callbackArgs.length-1].eglSignature);
	       		                    	}
			                            callbackArgs[callbackArgs.length-1] = http.response.body;
		                			}catch(e){
		                				throw egl.createRuntimeException("CRRUI2109E", [ paramName, e.toString() ]);
		                			}

		                        }
		                    }
		                    else if(encoding === egl.eglx.services.Encoding.JSON){
                            	if(isJSONRPC){
    		                    	var expectedResult = new egl.eglx.lang.EDictionary();
                            		if(callbackArgs.length > 1){ 
                            			var argInstances = new Array();
                            			for ( var idx = 0; idx < callbackArgs.length; idx++) {
                            				argInstances[idx] = new callbackArgs[idx].eglType;
                            			}
	                        			expectedResult.result = argInstances;
	    		                    	egl.eglx.json.JsonLib.convertFromJSON( http.response.body, expectedResult, false );
	    		                    	expectedResult.result = egl.unboxAny(expectedResult.result);
	    		                    	if(expectedResult.result instanceof Array){
	                            			for ( var idx = 0; idx < callbackArgs.length; idx++) {
	                            				var paramName = callbackArgs[idx].paramName;
	                            				try{
		               		                    	if(egl.unboxAny(expectedResult.result[idx]) === null){
		               		                    		egl.eglx.services.ServiceRT.checkSignatureForNull(callbackArgs[idx].eglSignature);
		               		                    	}
		                            				callbackArgs[idx] = egl.unboxAny(expectedResult.result[idx]);
	        		                			}catch(e){
	        		                				throw egl.createRuntimeException("CRRUI2109E", [ paramName, e.toString() ]);
	        		                			}
	                            			}
	    		                    	}
	    		                    	else{
		    		                    	callbackArgs = expectedResult.result;
	    		                    	}
                            		}
                                	else if(callbackArgs.length == 1){
                                		//if there is only one return/out 
                               			expectedResult.result = new callbackArgs[0].eglType;
           		                    	egl.eglx.json.JsonLib.convertFromJSON( http.response.body, expectedResult, false );
	    		                    	expectedResult.result = egl.unboxAny(expectedResult.result);
                        				var paramName = callbackArgs[0].paramName;
                        				try{
	           		                    	if(egl.unboxAny(expectedResult.result) === null){
	           		                    		egl.eglx.services.ServiceRT.checkSignatureForNull(callbackArgs[0].eglSignature);
	           		                    	}
	           		                    	callbackArgs[0] = expectedResult.result;
			                			}catch(e){
			                				throw egl.createRuntimeException("CRRUI2109E", [ paramName, e.toString() ]);
			                			}
                                	}
                            	}
                            	else{
                            		if(callbackArgs.length == 1){
	                            		//if there is only one return/out 
                            			var eglObject = new callbackArgs[0].eglType;
	       		                    	egl.eglx.json.JsonLib.convertFromJSON( http.response.body, eglObject, false );
                        				var paramName = callbackArgs[0].paramName;
                        				try{
	          		                    	if(egl.unboxAny(eglObject) === null){
	           		                    		egl.eglx.services.ServiceRT.checkSignatureForNull(callbackArgs[0].eglSignature);
	           		                    	}
		       		                    	callbackArgs[0] = eglObject;
			                			}catch(e){
			                				throw egl.createRuntimeException("CRRUI2109E", [ paramName, e.toString() ]);
			                			}
	                            	}
                            	}
		                    }
		                    else if(encoding === egl.eglx.services.Encoding.XML){
		                        if(callbackArgs.length > 0){ //the last parameter is the return record
		                        	//execute the function, pass 1, so the 1st parameter isn't null, it will pass the nullable check, 
		                        	//we're assuming it's never null in xml
		                        	//then return the record
		                        	//replace the element in the callbackArgs with the record it returned
                        			var eglObject = new callbackArgs[callbackArgs.length-1].eglType;
		                        	egl.eglx.xml.XmlLib.convertFromXML(http.response.body, eglObject);
                    				var paramName = callbackArgs[callbackArgs.length-1].paramName;
                    				try{
	      		                    	if(egl.unboxAny(eglObject) === null){
	       		                    		egl.eglx.services.ServiceRT.checkSignatureForNull(callbackArgs[callbackArgs.length-1].eglSignature);
	       		                    	}
			                        	callbackArgs[callbackArgs.length-1] = eglObject;
		                			}catch(e){
		                				throw egl.createRuntimeException("CRRUI2109E", [ paramName, e.toString() ]);
		                			}
		                        }
		                    }
		                    else if(encoding === egl.eglx.services.Encoding._FORM){
		                    	anyExp = egl.createAnyException("CRRUI3656E", []);	                    	
		                    }
		                    callbackArgs[callbackArgs.length] = http;
		                    if (anyExp == null && callbackFunction != null){
		                        //if there is a call back function
		                    	if (egl.debugg) {
		                    		egl.debugCallback(handler, callbackFunction, callbackArgs);
		                    	}
		                    	else {
		                    		try {
		                    			callbackFunction.apply(handler, callbackArgs);
		                    		}
		                    		catch (e) {
		                    			var msg = egl.getRuntimeMessage( "CRRUI1070E", [e.message] );
		                    			egl.printError( msg, e);
		                    			//continue throw e
		                    			throw egl.createRuntimeException("CRRUI1070E", [e.message]);
		                    		}
		                    	}
		                    }	                    	                    
	                    }
	                    else{ //inner response is not 2xx, need to call errorcallback
	                    	try{
	                    		anyExp = egl.eglx.services.$ServiceRT.createExceptionFromJsonRPCError(http);                    		
		                	}
		                	catch(jsonError){
		                		//the response.body is either not in a json format 
		                		//or the parsed json object isn't a JSONRPCError object
		                		//error parsing JSONRPCError
		                		anyExp = egl.eglx.services.createServiceInvocationException(
		                				"CRRUI3653E", 
		                				[request.uri], 
		                				egl.eglx.services.$ServiceRT.getServiceKind(http),
		                				http.response.status,
		                				http.response.statusMessage,
		                				http.response.body);
		                	}
	                    }
	                }
	                catch (e2) {
	                	anyExp = egl.eglx.services.createServiceInvocationException(
	                			"CRRUI3655E",
	                			[e2.message],
                				egl.eglx.services.$ServiceRT.getServiceKind(http),
	                			http.response.status ? http.response.status : "",
	                			http.response.statusMessage ? http.response.statusMessage : "",
	                			http.response.body ? http.response.body : "");	                	                	
	                }
	                
                	if(anyExp != null)
                		egl.eglx.services.$ServiceRT.callErrorCallback(errorCallbackFunction, handler, anyExp, http);                	
	            }, 
	            function(exception, http){
	            	egl.eglx.services.$ServiceRT.callErrorCallback(errorCallbackFunction, handler, exception, http);
	            },
	            this
	        );
        }
    },
    
    "createExceptionFromJsonRPCError" : function (http){
		//try to parse the response.body to json
        var exp = null;	
    	var response = new egl.eglx.lang.EDictionary();
        egl.eglx.json.JsonLib.convertFromJSON(http.response.body, response);                    	
        if(response != null && response.error != undefined){
    		var eglExpObj = response.error.error;
    		
    		var expType = eglExpObj.name;
    		//create exception based on jsonErrObj
    		if(expType == "eglx.services.ServiceInvocationException"){
        		exp = egl.eglx.services.createServiceInvocationException(
        				eglExpObj.messageID,
        				eglExpObj.message,
        				egl.eglx.services.$ServiceRT.convertToEnum(eglExpObj.source, egl.eglx.services.ServiceKind),
        				eglExpObj.detail1,
        				eglExpObj.detail2,
        				eglExpObj.detail3);
    		}
    		else{
        		exp = egl.eglx.services.createServiceInvocationException(
        				eglExpObj.messageID,
        				eglExpObj.message,
        				egl.eglx.services.$ServiceRT.getServiceKind(http),
        				expType);		//put the exception type name in detail1	                    			
    		}                    			
        }
        else{
        	throw "jsonError";		
    	}
    	return exp;
    },    
    "callErrorCallback" : function (errCallbackFunc, handler, exception, http){
    	try {
	    	if(errCallbackFunc){
	    		if (egl.debugg) {
	    			egl.debugCallback(handler, errCallbackFunc, [exception, http]);
	    		}
	    		else {
	    			errCallbackFunc.call(handler, exception, http);
	    		}
	    	}
    	}
	    catch (e){
	    	egl.printError( egl.getRuntimeMessage("CRRUI1072E", [e.message] ), e);
	    	throw egl.createRuntimeException("CRRUI1072E", [e.message] );
	    }
	    
	    if(!errCallbackFunc || errCallbackFunc == null || errCallbackFunc == undefined)
	    {	//if no error call back function defined, just throw the exception
	    	egl.printError( egl.getRuntimeMessage("CRRUI1071E", [exception.message] ), exception);
	    	throw egl.createRuntimeException( "CRRUI1071E", [exception.message] ); 	    
    	}
    },
    
    "doInvokeAsync" : function( /*Http/HttpSOAP*/ http, 
    							/*HttpCallback*/ callback, 
    							/*HttpCallback*/ errCallback, 
    							/*handler*/ handler ) {
        this.doInvokeInternal(
        		http, 
        		function(response){
        			egl.startNewWork();
                    callback.call( handler, http );
                }, 
                function(exception, response){
        			egl.startNewWork();
                	errCallback.call(handler, exception, http);
                },
                true);
    },
    "doInvokeInternal" : function( /*Http/HttpSOAP*/ http, 
                                   /*function*/ callback, 
                                   /*function*/ errCallback,                     
                                   /*boolean*/ asynchronous) {
    	if(http instanceof egl.eglx.http.HttpRest){
    		if(http.restType === egl.eglx.rest.ServiceType.EglDedicated){
                egl.valueByKey( http.request.headers, egl.HEADER_EGLDEDICATED, "true");                    
    		}
    	}
    	var request = new egl.eglx.lang.EDictionary();
    	request.uri = http.request.uri;
    	request.headers = http.request.headers;
    	request.body = http.request.body;
    	request.method = http.request.method;
        var requestString = egl.eglx.json.JsonLib.convertToJSON(request);
//        egl.eglx.services.$ServiceLib.callBackResponse.originalRequest = requestString;
        var xhr = egl.newXMLHttpRequest();
        var beginTime = new Date().getTime();
        var proxyURL = '/' + egl.contextRoot + '/' + this.eze$$proxyURL;
        
        if (egl.contextAware) {
        	proxyURL += (proxyURL.indexOf("?") == -1 ? "?" : "&") + "contextKey=" + egl.getContextKey();
        }
        
        xhr.onreadystatechange = function(){
            //4 - the response is complete; you can get the server's response and use it
            if (xhr.readyState == 4) {
            	var eglExp = null;
                try {                    
                	egl.enter("Response for " + http.request.uri, { eze$$typename: "RUI Runtime" });
                    if (xhr.status >= 200 && xhr.status <= 299){
                        try {
                        	egl.enter("Parse JSON ("+xhr.responseText.length+" bytes) from "+ http.request.uri, { eze$$typename: "RUI Runtime" });
                        	var response = new egl.eglx.http.Response();
                        	egl.eglx.json.JsonLib.convertFromJSON(xhr.responseText, response);
                        	http.response.body = response.body;
                        	http.response.status = response.status;
                        	http.response.statusMessage = response.statusMessage;
                        	http.response.headers = response.headers;
                        }
                        finally {
                        	egl.leave();
                        }
                        if (http.response != null) {
                            try {
                            	egl.enter("Handling callback for " + http.request.uri, { eze$$typename: "RUI Runtime" });
                            	egl.eglx.services.$ServiceRT.runCallback(callback, http, beginTime);
                            }
                            finally {
                            	egl.leave();
                            }
                        }
                        else {
                        	eglExp = egl.eglx.services.createServiceInvocationException(
                            		"CRRUI3659E",
                            		[xhr.responseText],
	                				egl.eglx.services.$ServiceRT.getServiceKind(http),
                            		xhr.status,
                            		xhr.statusMessage);
                        }
                    }
                    else {
	                	//things failed on the proxy, need to report error to errorCallback                    	
                        http.response.status = xhr.status || 404;
                        http.response.statusMessage = xhr.statusText;     
                        http.response.body = xhr.responseText;     
                        
                    	//var exp = null;
                    	try{
                    		eglExp = egl.eglx.services.$ServiceRT.createExceptionFromJsonRPCError(http);                    		
	                	}
	                	catch(jsonError){
	                		//the xhr.responseText is either not in a json format 
	                		//or the parsed json object isn't a JSONRPCError object
	                		//error parsing JSONRPCError
	                		if(http.response.status == 404){
	                			eglExp = egl.eglx.services.createServiceInvocationException(
	                					"CRRUI3657E",
	                					[proxyURL],
		                				egl.eglx.services.$ServiceRT.getServiceKind(http),
		                				http.response.status,
		                				http.response.statusMessage,
		                				xhr.responseText);
	                		}
	                		else{	
	                			eglExp = egl.eglx.services.createServiceInvocationException(
		                				"CRRUI3658E", 
		                				[proxyURL, http.request.uri],
		                				egl.eglx.services.$ServiceRT.getServiceKind(http),
		                				http.response.status,
		                				http.response.statusMessage,
		                				xhr.responseText);
	                		}
	                	}
                    }                    
                }
                catch(e) {
                	eglExp = egl.eglx.services.createServiceInvocationException(
                			"CRRUI3660E",
                			[http.request.uri, e.message],
            				egl.eglx.services.$ServiceRT.getServiceKind(http),
                			xhr.status,
                			xhr.statusMessage,
                			xhr.responseText);
                }
                finally {
                	xhr.onreadystatechange = egl.emptyStateChangeFunction;
	                egl.leave();
                }
                
                if(eglExp != null)
                	egl.eglx.services.$ServiceRT.runErrorCallback(errCallback, eglExp, http);
            }
        };
        if(egl.eze$$SetProxyAuth == true){
         	xhr.open("POST", proxyURL, asynchronous, egl.eglx.services.$ServiceLib.eze$$proxyUser,  egl.eglx.services.$ServiceLib.eze$$proxyPwd);
         	egl.eze$$SetProxyAuth = false;		//reset it
        }
        else
        	xhr.open("POST", proxyURL, asynchronous );
        
        //to use php
        //xhr.open( 'POST', '___proxy.php?url='+encodeURIComponent(request.uri), asynchronous );
        xhr.setRequestHeader('Content-Length', requestString.length);
        xhr.setRequestHeader(egl.HEADER_CONTENT_TYPE, 'application/jsonrequest;charset=UTF-8');

        xhr.send( requestString );
    },
    "runCallback" : function(callback, http, beginTime) {
        if (egl.debug) {
            egl.enter("<font color=darkgreen>after&nbsp;"+
                (beginTime ? (new Date().getTime()-beginTime) : 0)+
                "ms,&nbsp;handle&nbsp;service&nbsp;response&nbsp;for&nbsp;"+
                http.request.uri+"</font>", this, []);
        }
        try {
            callback( http );
        }
        finally {
            if (egl.debug) {
                egl.leave("service&nbsp;response&nbsp;for&nbsp;"+http.request.uri, this, []);
            }
        }        
    },
    "runErrorCallback" : function(errCallback, exception, http) {
    	if (errCallback) {
    		errCallback(exception, http);
    	}
    	else {
    		throw egl.createRuntimeException( "CRRUI1071E", [http.request.uri] );
    	}
    },
    "encodeResquestBody": function(httpResquest, resourceParamIn){
        if(httpResquest.body === null){
	    	if(httpResquest.method === egl.eglx.http.HttpMethod._Get || httpResquest.method === egl.eglx.http.HttpMethod._DELETE){
	        	httpResquest.body = egl.toString(resourceParamIn);
	        }
	        else{
	            //need to build the body using resourceParamIn            
	            if(resourceParamIn){
	                //check for the requestFormat
	            	if(httpResquest.encoding === egl.eglx.services.Encoding.JSON){
	                	httpResquest.body = egl.eglx.json.toJSONString(resourceParamIn);
	            	}
	            	else if(httpResquest.encoding === egl.eglx.services.Encoding.XML){
	                	httpResquest.body = egl.eglx.xml.XmlLib.convertToXML(resourceParamIn);
	            	}
	            	else if(httpResquest.encoding === egl.eglx.services.Encoding._FORM){
	                	httpResquest.body = egl.eglx.http.HttpLib.convertToFormData(resourceParamIn);                    
	            	}
	            	else if(httpResquest.encoding === egl.eglx.services.Encoding.NONE){
	                	httpResquest.body = resourceParamIn;
	            	}
	            }
	        }
        }
    	
    },
    "getServiceKind": function(http){
    	if(http instanceof egl.eglx.http.HttpRest){
    		if(http.restType === egl.eglx.rest.ServiceType.EglDedicated){
                return egl.eglx.services.ServiceKind.EGL;                    
    		}
    		else{
                return egl.eglx.services.ServiceKind.REST;                    
    		}
    	}
    },
    "serviceKind" : function(/*Teglx/services/ServiceInvocationException;*/sie) {
    	switch (sie.source) {
    	case egl.eglx.services.ServiceKind.WEB:
    		return "WEB";
    	case egl.eglx.services.ServiceKind.NATIVE:
    		return "NATIVE";
    	case egl.eglx.services.ServiceKind.EGL:
    		return "EGL";
    	case egl.eglx.services.ServiceKind.REST:
    		return "REST";
    	default:
    		return "unknown";
    	}
    },
    "convertToEnum" : function( value, type) {
    	if(typeof value === "string"){
    		value = egl.eglx.lang.EInt32.fromEString(value);
    	}
    	for ( var field in type) {
    		if (type[field] instanceof egl.eglx.lang.Enumeration
    				&& type[field].value == value) {
    			return type[field];
    		}
    	}
    	return undefined;
    }
});

new egl.eglx.services.ServiceRT();
egl.eglx.services.ServiceRT["checkSignatureForNull"] = function(eglSignature) {
	if(eglSignature.charAt(0) !== "?"){
		throw egl.createNullValueException( "CRRUI2005E", [] );
	}
};

egl.eglx.services.ServiceRT["checkNull"] = function(fieldInfo) {
	if(fieldInfo !== null && fieldInfo !== undefined ){
		egl.eglx.services.ServiceRT.checkSignatureForNull(fieldInfo.eglSignature);
	}
	return null;
};

egl.emptyStateChangeFunction = function() {};

egl.statefulSessionMap = new Object();
egl.eze$$SetProxyAuth=null;
egl.eglx.services.createServiceInvocationException = function( /*string*/ messageID, /*string or array*/ inserts )
{
	if (typeof(inserts) != "string") {
		inserts = egl.getRuntimeMessage( messageID, inserts );
	}
	egl.exceptionThrown = true;
	var args = new Array();
	args.push( [ "messageID", messageID || "" ] );
	args.push( [ "message", inserts || "" ] );
	var exp = {};
	if(arguments[2] instanceof egl.eglx.services.ServiceKind){
		exp["source"] = arguments[2];                    
	}
	else{
		exp["source"] = undefined;                    
	}
	var detail1 = "";	
	if(arguments[ 3 ])
		detail1 = arguments[ 3 ]+''; //make sure it's String
	var detail2= "";
	if(arguments[ 4 ])
		detail2 = arguments[ 4 ]+''; //make sure it's String
	var detail3= "";
	if(arguments[ 5 ])
		detail3 = arguments[ 5 ]+''; //make sure it's String
	exp["detail1"] = detail1;		
	exp["detail2"] = detail2;		
	exp["detail3"] = detail3;	
	var eglExp = new egl.eglx.services.ServiceInvocationException(args);
	eglExp.ezeCopy(exp);
	return eglExp;
};

