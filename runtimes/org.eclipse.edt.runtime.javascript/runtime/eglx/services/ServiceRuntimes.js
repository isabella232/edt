/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
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
egl.HEADER_EGLREST = "EGLREST";
egl.HEADER_EGLSOAP = "EGLSOAP";
egl.CUSTOM_HEADER_EGLSOAP = "CUSTOMEGLSOAPREQUESTHEADER";
egl.CUSTOM_RESPONSE_HEADER_EGLSOAP = "CUSTOMEGLSOAPRESPONSEHEADER";
egl.CUSTOM_DICT_HEADERS = "DictHeaders";
egl.SERVICE_TIMEOUT = "EGL_TIMEOUT";
    
egl.defineClass(
    'eglx.services', 'ServiceRefWrapper',
{
    //these could be in the base class
    //String f_type;			//type of service, "web" or "rest"
    //String f_bindingName
    //String f_RESTmethod;
    //int f_requestFormat;
    //int f_responseFormat;
    //function f_httpCallback;
    //function f_errCallback;    
    //Dictionary f_RESTRequestHeader;
    
    //Array of Object f_callbackArgs;

    "constructor" : function(/*String*/ name) {     
    	this.f_type;     
    	this.f_bindingName = name || "";
        this.f_RESTmethod;
        this.f_requestFormat;
        this.f_responseFormat;  
        this.f_httpCallback = function(){};
        this.f_errCallback = function(){};
        this.f_RESTRequestHeader = null;
        this.f_callbackArgs = {};
    },   
    "getRESTRequestHeader" : function(){
        if(!this.f_RESTRequestHeader || this.f_RESTRequestHeader == null)           
            this.f_RESTRequestHeader = egl.createDictionary(true, false);
        return  this.f_RESTRequestHeader;
    },
    "setServiceTimeoutInHeader" : function(/*int*/ serviceTimeout){
        if( serviceTimeout > -1 )
        	egl.valueByKey( this.getRESTRequestHeader(), egl.SERVICE_TIMEOUT, serviceTimeout, "I;");
        else
        	egl.removeElement(this.getRESTRequestHeader(), egl.SERVICE_TIMEOUT);
    },
    "invokeService" : function(/*RUIHandler*/ handler,
					            /*func*/ httpCallback, 
					            /*func*/ errorCallback, 
					            /*int*/ serviceTimeout,			            
					            /*String*/ functionName,						            
					            /*Array of Objects*/ inParamVals,
					            /*Array of signature strings*/ inParamSignatures,
					            /*String*/ paramOrders,
					            /*Array of Functions or Array*/ callbackArgs){
		throw "TODO: make an exception for this";//throw egl.createRuntimeException( "CRRUI3661E", [this.f_bindingName,functionName] );
    }
});
  

egl.defineClass(
	    'eglx.services', 'EGLDedicatedServiceRefWrapper',
	    'eglx.services', 'ServiceRefWrapper',
{        
	    //String fservicesName;   
	    //String f_alias;   
    
    "constructor" : function(/*String*/ variableName, /*String*/ serviceName, /*String*/ alias) {
	        	this.f_bindingName = variableName || "";
	        	this.fservicesName = serviceName || "";
	        	this.f_alias = alias || "";
	        	this.f_type = "egl";
    },    
        
    "invokeService" : function(/*RUIHandler*/ handler,
					            /*func*/ httpCallback, 
					            /*func*/ errorCallback, 
					            /*int*/ serviceTimeout,
					            /*String*/ functionName,						            
					            /*Array of Objects*/ inParamVals,
					            /*Array of signature strings*/ inParamSignatures,
					            /*String*/ paramOrders,
					            /*Array of Functions or Array*/ callbackArgs){
    	
    	this.setServiceTimeoutInHeader(serviceTimeout);
	    this.invokeEGLService(handler, 
	    						httpCallback, 
	    						errorCallback, 
	    						functionName,
	    						inParamVals,
	    						inParamSignatures,
	    						callbackArgs);    		
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

        egl.valueByKey( this.getRESTRequestHeader(), egl.HEADER_EGLDEDICATED, "TRUE", "S;" );  
        
        
        //uses json-rpc     
        this.f_RESTmethod = 'POST';
        this.f_requestFormat = egl.formatJSON;
        this.f_responseFormat = egl.formatJSON;                     
                        
        this.f_httpCallback = httpCallback;
        this.f_errCallback = errorCallback;
        this.f_callbackArgs = callbackArgs;
        var url = "";
        
        //let's build the payload body using json-rpc
        var payloadbodyObj = {
            bindingName: this.f_bindingName,
            method: functionName || "",
            params: inParamVals || [],
            eze$$InParamSignatures: inParamSignatures || [],
            binding:{
        		name: this.f_bindingName,
                type: "EGLBinding",
                serviceName: this.fservicesName,
                alias: this.f_alias,
                protocol: "local"
           }
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

egl.defineClass(
    'eglx.services', 'SOAPServiceRefWrapper',
    'eglx.services', 'ServiceRefWrapper',
{
    //String f_fqInterface;    
    //String f_wsdlLocation;
    //String f_wsdlService;
    //String f_wsdlPort;
    //String f_wsdlUri;
    
    "constructor" : function(/*String*/ FQInterfaceName,
    						 /*String*/ simpleInterfaceName,
                             /*String*/ wsdlLocation, 
                             /*String*/ wsdlService,
                             /*String*/ wsdlPort,
                             /*String*/ uri) {        
        this.f_fqInterface = FQInterfaceName || "";
        this.f_bindingName = simpleInterfaceName || "";
        this.f_wsdlLocation = wsdlLocation || "";
        this.f_wsdlService = wsdlService || "";
        this.f_wsdlPort = wsdlPort || "";
        this.f_wsdlUri = uri || "";
        this.f_type = "web";                 	
        
        this.initOtherMemebers();
    },
        
    "initOtherMemebers" : function(){
        this.f_RESTmethod = 'POST';
        this.f_requestFormat = egl.formatJSON;
        this.f_responseFormat = egl.formatJSON;                    	
    },
        
    "invokeService" : function(/*RUIHandler*/ handler,
					            /*func*/ httpCallback, 
					            /*func*/ errorCallback, 
					            /*int*/ serviceTimeout,			            
					            /*String*/ functionName,						            
					            /*Array of Objects*/ inParamVals,
					            /*Array of signature strings*/ inParamSignatures,
					            /*String*/ paramOrders,
					            /*Array of Functions or Array*/ callbackArgs){
					            
//============================ the following parameters are not used for soap  ============================    	
//					            /*boolean*/ hasXXXRestAnnotation,
//					            /*String*/ resolvedUriTemplate,
//					            /*int*/ requestFormat,
//					            /*int*/ responseFormat,
//					            /*String*/ restMethod,                        
//					            /*String, Dictionary, Record or XMLElement*/ resourceParamIn
//=========================================================================================================    	
    	this.setServiceTimeoutInHeader(serviceTimeout);
    	this.invokeSOAPService(handler, 
	    						httpCallback, 
	    						errorCallback, 
	    						functionName,
	    						inParamVals,
	    						inParamSignatures,
	    						paramOrders,
	    						callbackArgs);    		
    },    
    
    /**
     * handler is the RUIHandler, is used as context for the callback function
     * inParamVals is a array of ordered in and inout parameter values 
     */    
    "invokeSOAPService" : function(
                        /*RUIHandler*/ handler,
                        /*func*/ httpCallback, 
                        /*func*/ errorCallback, 
                        /*String*/ operationName,                        
                        /*Array of Objects*/ inParamVals,
                        /*Array of signature strings*/ inParamSignatures,
                        /*String*/ paramOrders,
                        /*Array of Functions or Array*/ callbackArgs){
        this.f_httpCallback = httpCallback;
        this.f_errCallback = errorCallback;    
        this.f_callbackArgs = callbackArgs;
        
        egl.valueByKey( this.getRESTRequestHeader(), egl.HEADER_EGLSOAP, egl.contextRoot, "S;" );        
        
        //let's build the payload body using json-rpc
        var payloadbodyObj = {
            bindingName: this.f_bindingName,
            method: operationName || "",
            params: inParamVals || [],
            eze$$InParamSignatures: inParamSignatures || [], 
            EGL_PARAMETER_ORDER: paramOrders || "",
            binding:{
                type: "WebBinding",
                interfacename: this.f_fqInterface,
                name: this.f_bindingName,
                wsdlLocation: this.f_wsdlLocation,
                wsdlService: this.f_wsdlService,
                wsdlPort: this.f_wsdlPort,
                uri: this.f_wsdlUri
                }
        };
        
        var body = egl.eglx.json.toJSONString(payloadbodyObj);
        egl.eglx.services.$ServiceRT.internalInvokeService(
                    handler,
                    "",                     //no uri
                    this, 
                    {},                     //pass null for query parameters 
                    body);
        
    }    
});  

egl.defineClass(
    'eglx.services', 'ServiceRT',
{
    "constructor" : function() {
        if (egl.eglx.services.$ServiceRT) 
            return egl.eglx.services.$ServiceRT;         
        egl.eglx.services.$ServiceRT=this;
        
        this.eze$$proxyURL = 'proxy';        
    },
    
    "setRequestContentTypeHeaderIfNotSet" : function(/*HTTPRequest*/ request){
        //if it already has content-type in the header, then do not do anything
        //otherwise, set the content type based on the request format
        var hasReqContentType = false;
        if(request.headers != null){
        	hasReqContentType = egl.containsKey(request.headers, "Content-Type");
        }
        else{
        	request.headers = {};
        }
        if(hasReqContentType == false && request.contentType === null){
        	var contentType = "text/html";
        	if(httpResquest.encoding === egl.eglx.services.Encoding.JSON){
        		contentType = "application/json";
        	}
        	else if(httpResquest.encoding === egl.eglx.services.Encoding.XML){
        		contentType = "application/xml";   
        	}
        	else if(httpResquest.encoding === egl.eglx.services.Encoding.FORM){
        		contentType = "application/x-www-form-urlencoded";                    
        	}
        	if(request.charset === null){
        		contentType += "; charset=UTF-8";
        	}
        	else{
        		request.contentType += "; charset=";
        		contentType += request.charset;
        	}
            
            egl.valueByKey( serviceWrapper.headers, "Content-Type", contentType);                    
        }
    },

    "isJsonRPCFormat" : function(/*HttpRequest*/ request){
    	var isJSONRPC = false;
    	if(request.headers){
            //let's check the header of the request to see if it's soap or low egl call
    		//if so, it uses json-rpc as response
    		var isSOAP = egl.containsKey(request.headers, egl.HEADER_EGLSOAP);
    		var isEGLREST = egl.containsKey(request.headers, egl.HEADER_EGLREST);
    		var isEGLDedicated = egl.containsKey(request.headers, egl.HEADER_EGLDEDICATED);
    		if(isSOAP || isEGLREST || isEGLDedicated ){
    			isJSONRPC = true;
    		}
    	}
    	return isJSONRPC;
    },
    
    /**
     * This is an overloaded function with the following patterns:     
            Function internalInvokeService( handler RUIHandler, uri String, serviceWrapper, parameters Dictionary) end 
            Function internalInvokeService( handler RUIHandler, uri String, serviceWrapper, parameters Dictionary, body String) end        
     */              
    "internalInvokeService" : function(/*HttpRest or HttpSOAP*/ http, 
    									callbackArgs,
							    		callbackFunction,
										errorCallbackFunction,
										serviceKind,
										/*RUIHandler*/ handler) {
    	//make a shallow copy of some of the properties on the serviceWrapper, 
    	//these copies will be used in the callback function, this way, even if serviceWrapper object got changed by other event
    	//the values used in the callbacks are accurate 
        var requestExp = null;
        try {
            this.setRequestContentTypeHeaderIfNotSet(http.eze$$request);           
        }
        catch (e) {
    		requestExp = egl.createAnyException("CRRUI3654E", [e]);    		
        }       
        
        if(requestExp != null){
        	egl.eglx.services.$ServiceRT.callErrorCallback(errCallbackFunc, uriString, handler, requestExp);
        }
        else{
	        this.doInvokeAsync( 
	            http,
	            serviceKind,
	            function(response) {
	            	var isJSONRPC = egl.eglx.services.$ServiceRT.isJsonRPCFormat(http);
	            	http.response.status = response.status;//FIXME
	            	http.response.body = response.body;
	            	
	            	var anyExp = null;
	                try {                	
	                    if(http.response.status >= 200 && http.response.status <= 299){
	                    	var sessionIdValue = egl.findByKey(http.response.headers, egl.STATEFULSESSIONID);
	                    	if(sessionIdValue != null &&  sessionIdValue != undefined){
		                    	var httpSessionIdValue = egl.findByKey(http.response.headers, egl.HTTP_SESSION_ID_KEY);
		                    	if(httpSessionIdValue == undefined){
		                    		httpSessionIdValue = null;
		                    	}
	                    		//try to get the current value
	                    		var currVal = egl.statefulSessionMap[http.eze$$request.uri];
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
	                    			egl.statefulSessionMap[http.eze$$request.uri] = sessionIdObj;
	                    		}
	                    	}
	                    	
	                    	var customSOAPResponseHeaders = egl.findByKey(http.response.headers, egl.CUSTOM_RESPONSE_HEADER_EGLSOAP);
	                    	if(customSOAPResponseHeaders != null &&  customSOAPResponseHeaders != undefined){
	                    		egl.valueByKey(serviceWrapper.getRESTRequestHeader(), egl.CUSTOM_RESPONSE_HEADER_EGLSOAP, customSOAPResponseHeaders,"S;");
	                    	}
	                    	
	                    	var encoding = http.eze$$response.encoding;
	                    	if(encoding === null){
	                    		//FIXME use encoding based on the content type
	                    	}
		                    if(encoding === egl.eglx.services.Encoding.NONE){
		                        if(callbackArgs.length > 0){
		                            callbackArgs[callbackArgs.length-1] = http.response.body;
		                        }
		                    }
		                    else if(encoding === egl.eglx.services.Encoding.JSON){
		                        var jsonObj = egl.eglx.json.JsonLib.convertFromJSON( http.response.body );
		                        for(var i=0; i<callbackArgs.length; i++){
		                        	//replace each of element in the callbackArgs with the object each of the function returns		                        	
	                            	if(isJSONRPC == true){ //the response body is in json-rpc object, get the result property                            		
	                            		//if there is only one return/out 
	                            		if(callbackArgs.length == 1){
	                            			callbackArgs[i] = egl.eglx.services.$ServiceRT.processCallbackArg(callbackArgs[i], jsonObj.result);
	                            		}
	                            		else{ //if(jsonObj.result instanceof Array)  //result must be an array 
	                            			callbackArgs[i] = egl.eglx.services.$ServiceRT.processCallbackArg(callbackArgs[i], jsonObj.result[i]);
	                            		}
	                            	}
	                            	else
	                            		callbackArgs[i] = egl.eglx.services.$ServiceRT.processCallbackArg(callbackArgs[i], jsonObj);	                        		                        	
		                        }	                        
		                    }
		                    else if(encoding === egl.eglx.services.Encoding.XML){
		                        if(callbackArgs.length > 0){ //the last parameter is the return record
		                        	//execute the function, pass 1, so the 1st parameter isn't null, it will pass the nullable check, 
		                        	//we're assuming it's never null in xml
		                        	//then return the record
		                        	//replace the element in the callbackArgs with the record it returned
		                        	callbackArgs[callbackArgs.length-1] = callbackArgs[callbackArgs.length-1](1);
		                        	egl.eglx.xml.$XmlLib.convertFromXML(http.response.body, callbackArgs[0]);
		                        }
		                    }
		                    else if(encoding === egl.eglx.services.Encoding.FORM){
		                    	anyExp = egl.createAnyException("CRRUI3656E", []);	                    	
		                    }
		                    
		                    if (anyExp == null && callbackFunc != null){
		                        //if there is a call back function
		                    	if (egl.debugg) {
		                    		egl.debugCallback(handler, callbackFunc, callbackArgs);
		                    	}
		                    	else {
		                    		try {
		                    			callbackFunc.apply(handler, callbackArgs);
		                    		}
		                    		catch (e) {
		                    			var msg = egl.getRuntimeMessage( "CRRUI1070E", [e.message] );
		                    			egl.printError( msg, e);
		                    			//continue throw e
		                    			throw "TODO: make an exception for this";//throw egl.createRuntimeException("CRRUI1070E", [e.message]);
		                    		}
		                    	}
		                    }	                    	                    
	                    }
	                    else{ //inner response is not 2xx, need to call errorcallback
	                    	try{
	                    		anyExp = egl.eglx.services.$ServiceRT.createExceptionFromJsonRPCError(response.body, serviceKind);                    		
		                	}
		                	catch(jsonError){
		                		//the response.body is either not in a json format 
		                		//or the parsed json object isn't a JSONRPCError object
		                		//error parsing JSONRPCError
		                		anyExp = egl.eglx.services.createServiceInvocationException(
		                				"CRRUI3653E", 
		                				[eze$$request.uri], 
		                				serviceKind,
		                				response.status,
		                				response.statusMessage,
		                				response.body);
		                	}
	                    }
	                }
	                catch (e2) {
	                	anyExp = egl.eglx.services.createServiceInvocationException(
	                			"CRRUI3655E",
	                			[e2.message],
	                			serviceKind,
	                			response.status ? response.status : "",
	                			response.statusMessage ? response.statusMessage : "",
	                			response.body ? response.body : "");	                	                	
	                }
	                
                	if(anyExp != null)
                		egl.eglx.services.$ServiceRT.callErrorCallback(errCallbackFunc, uriString, handler, anyExp, response);                	
	            }, 
	            function(exception, response){
	            	egl.eglx.services.$ServiceRT.callErrorCallback(errCallbackFunc, uriString, handler, exception, response);
	            },
	            this
	        );
        }
    },
    
    "createExceptionFromJsonRPCError" : function (jsonRpcErroString, /*String*/ serviceKind){
		//try to parse the response.body to json
        var exp = null;		
        var jsonObjE = egl.eglx.json.JsonLib.convertFromJSON(jsonRpcErroString);                    	
        if(jsonObjE != null && jsonObjE.error != undefined){
    		var eglExpObj = jsonObjE.error.error;
    		
    		var expType = eglExpObj.name;
    		//create exception based on jsonErrObj
    		if(expType == "eglx.services.ServiceInvocationException"){
        		exp = egl.eglx.services.createServiceInvocationException(
        				eglExpObj.messageID,
        				eglExpObj.message,
        				eglExpObj.source,
        				eglExpObj.detail1,
        				eglExpObj.detail2,
        				eglExpObj.detail3);
    		}
    		else if(expType == "eglx.services.ServiceBindingException"){
    			exp = egl.eglx.services.createServiceBindingException(eglExpObj.messageID, eglExpObj.message);
    		}
    		else{
        		exp = egl.eglx.services.createServiceInvocationException(
        				eglExpObj.messageID,
        				eglExpObj.message,
        				serviceKind,
        				expType);		//put the exception type name in detail1	                    			
    		}                    			
        }
        else{
        	throw "jsonError";		
    	}
    	return exp;
    },    
    "callErrorCallback" : function (errCallbackFunc, uri, handler, exception, http){
    	try {
    		if(response)
	    		egl.eglx.services.$ServiceRT.setStaticRESTResponse(response);      	
	    	
	    	if(errCallbackFunc){
	    		if (egl.debugg) {
	    			egl.debugCallback(handler, errCallbackFunc, exception);
	    		}
	    		else {
	    			errCallbackFunc.call(handler, exception);
	    		}
	    	}
    	}
	    catch (e){
	    	egl.printError( egl.getRuntimeMessage("CRRUI1072E", [e.message] ), e);
	    	throw "TODO: make an exception for this";//throw egl.createRuntimeException("CRRUI1072E", [e.message] );
	    }
	    
	    if(!errCallbackFunc || errCallbackFunc == null || errCallbackFunc == undefined)
	    {	//if no error call back function defined, just throw the exception
	    	egl.printError( egl.getRuntimeMessage("CRRUI1071E", [exception.message] ), exception);
	    	throw "TODO: make an exception for this";//throw egl.createRuntimeException( "CRRUI1071E", [exception.message] ); 	    
    	}
    },
    
    "processCallbackArg" : function(/*Function or Array*/ callbackArg, /*Object*/ resultObj){
    	if(callbackArg instanceof Array){
    		//then the resultObj should also be an array
    		var arrayVal = new Array();
    		if(resultObj){
	    		for(var i=0; i<resultObj.length; i++){
	    			arrayVal.push(egl.eglx.services.$ServiceRT.processCallbackArg(callbackArg[0], resultObj[i]));
	    		}
    		}
    		else{	//array itself could be null
    			arrayVal = null;
    		}
    		return arrayVal;
    	}
    	else if(callbackArg instanceof Function){		
    		//it is a function that returns a new java script object
    		//invoke the function to get the new java script object
			var ret = callbackArg(resultObj);
			
			//ret should be an egl type corresponding java script object (i.e. egl record, egl.javascript.BigDecimal, Date)
			//check to see if it's an egl record
			if(ret != null && (ret instanceof egl.egl.jsrt.Record))  	
				ret.eze$$fromJson(resultObj, true);
			
			//it could be null type is nullable
			return ret;
    	}
    },
    
    "doInvokeAsync" : function( /*HttpRest/HttpSOAP*/ http, 
    							/*String */ serviceKind,
    							/*HttpCallback*/ callback, 
    							/*HttpCallback*/ errCallback, 
    							/*handler*/ handler ) {
        this.doInvokeInternal(
        		http, 
        		serviceKind,
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
    "doInvokeInternal" : function( /*HttpRest/HttpSOAP*/ http, 
    							   /*String*/ serviceCallTypeIn,
                                   /*function*/ callback, 
                                   /*function*/ errCallback,                     
                                   /*boolean*/ asynchronous) {
    	var serviceKind = serviceCallTypeIn;     //copy the value
        var requestString = http.request.body;
//        egl.eglx.services.$ServiceLib.callBackResponse.originalRequest = requestString;
        var xhr = egl.newXMLHttpRequest();
        var beginTime = new Date().getTime();
        var proxyURL = '/' + egl.contextRoot + '/' + this.eze$$proxyURL + '/' + http.eze$$request.uri;
        
        xhr.onreadystatechange = function(){
            //4 - the response is complete; you can get the server's response and use it
            if (xhr.readyState == 4) {
            	var eglExp = null;
                try {                    
                	egl.enter("Response for " + http.eze$$request.uri, { eze$$typename: "RUI Runtime" });
                    if (xhr.status >= 200 && xhr.status <= 299){
                        var json;
                        try {
                        	egl.enter("Parse JSON ("+xhr.responseText.length+" bytes) from "+ http.eze$$request.uri, { eze$$typename: "RUI Runtime" });
                        	json = egl.eglx.json.JsonLib.convertFromJSON(xhr.responseText);
                        }
                        finally {
                        	egl.leave();
                        }
                        if (json != null) {
                            http.response = json;
                            http.response.body = response.body.replace(/&quot;/g, '"');
                            try {
                            	egl.enter("Handling callback for " + http.eze$$request.uri, { eze$$typename: "RUI Runtime" });
                            	egl.eglx.services.$ServiceRT.runCallback(callback, http, beginTime);
                            }
                            finally {
                            	egl.leave();
                            }
                        }
                        else {
                        	http.response.status = xhr.status;
                        	http.response.statusMessage = xhr.statusMessage;
                        	eglExp = egl.eglx.services.createServiceInvocationException(
                            		"CRRUI3659E",
                            		[xhr.responseText],
                            		serviceKind,
                            		xhr.status,
                            		xhr.statusMessage);
                        }
                    }
                    else {
	                	//things failed on the proxy, need to report error to errorCallback                    	
                        http.response.status = xhr.status || 404;
                        http.response.statusMessage = xhr.statusText;     
                        
                    	//var exp = null;
                    	try{
                    		eglExp = egl.eglx.services.$ServiceRT.createExceptionFromJsonRPCError(xhr.responseText, serviceKind);                    		
	                	}
	                	catch(jsonError){
	                		//the xhr.responseText is either not in a json format 
	                		//or the parsed json object isn't a JSONRPCError object
	                		//error parsing JSONRPCError
	                		if(http.response.status == 404){
	                			eglExp = egl.eglx.services.createServiceInvocationException(
	                					"CRRUI3657E",
	                					[proxyURL],
	                					serviceKind,
		                				http.response.status,
		                				http.response.statusMessage,
		                				xhr.responseText);
	                		}
	                		else{	
	                			eglExp = egl.eglx.services.createServiceInvocationException(
		                				"CRRUI3658E", 
		                				[proxyURL, http.eze$$request.uri],
		                				serviceKind,
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
                			[http.eze$$request.uri, e.message],
                			serviceKind,
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
        var method = "GET";
    	if(http.eze$$request.method === egl.eglx.http.HttpMethod.POST){
    		method = "POST";
    	}
    	else if(http.eze$$request.method === egl.eglx.http.HttpMethod._DELETE){
    		method = "DELETE";
    	}
    	else if(http.eze$$request.method === egl.eglx.http.HttpMethod.PUT){
    		method = "PUT";
    	}
        if(egl.eze$$SetProxyAuth == true){
         	xhr.open(method, proxyURL, asynchronous, egl.eglx.services.$ServiceLib.eze$$proxyUser,  egl.eglx.services.$ServiceLib.eze$$proxyPwd);
         	egl.eze$$SetProxyAuth = false;		//reset it
        }
        else
        	xhr.open(method, proxyURL, asynchronous );
        
        //to use php
        //xhr.open( 'POST', '___proxy.php?url='+encodeURIComponent(request.uri), asynchronous );
        xhr.setRequestHeader('Content-Length', requestString.length);
        xhr.setRequestHeader('Content-Type', 'application/jsonrequest;charset=UTF-8');

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
    		throw "TODO: make an exception for this";//throw egl.createRuntimeException( "CRRUI1071E", [http.request.uri] );
    	}
    },
    "encodeResquestBody": function(httpResquest, resourceParamIn){
        if(httpResquest.body === null){
	    	if(httpResquest.ezemethod == "GET" || httpResquest.ezemethod == "DELETE"){
	        	httpResquest.body = egl.toString(resourceParamIn);
	        }
	        else{
	            //need to build the body using resourceParamIn            
	            if(resourceParamIn){
	                //check for the requestFormat
	            	if(httpResquest.ezeencoding === egl.eglx.services.Encoding.JSON){
	                	httpResquest.body = egl.eglx.json.toJSONString(resourceParamIn);
	            	}
	            	else if(httpResquest.ezeencoding === egl.eglx.services.Encoding.XML){
	                	httpResquest.body = egl.eglx.xml.$XmlLib.convertToXML(resourceParamIn);
	            	}
	            	else if(httpResquest.ezeencoding === egl.eglx.services.Encoding.FORM){
	                	httpResquest.body = egl.eglx.http.HttpLib.convertToFormData(resourceParamIn);                    
	            	}
	            	else if(httpResquest.ezeencoding === egl.eglx.services.Encoding.NONE){
	                	httpResquest.body = resourceParamIn;
	            	}
	            }
	        }
        }
    	
    }
});

egl.emptyStateChangeFunction = function() {};

new egl.eglx.services.ServiceRT();
egl.statefulSessionMap = new Object();
egl.eze$$SetProxyAuth=null;
