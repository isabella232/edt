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
define(["runtime/edt_runtime_all.js"], function(){
	egl.enableEditing = window.egl__enableEditing;
	egl.contextAware = window.egl__contextAware;
	egl.debugg = window.egl__debugg;
	egl.sessionIsValid = true;
	egl.cssFiles = [];
	egl.canPrintError = true;
	require({baseUrl: "../" + egl__contextRoot});
	egl.defineClass( "egl.debug", "DebugTermination", {
		"constructor" : function(msg) {
			this.msg = msg;
		}
	});

	Array.prototype.eze$$getChildVariables = function Array_getChildVaribles_for_debug_support() {
		var childVars = [];
		for (var n=0; n<this.length; n++) {
			var type = egl.getDebugType ? egl.getDebugType(this[n]) : "";
			childVars[n] = {name : "[" + (n + 1) + "]", value : this[n], type : type};
		}
		return childVars;
	};

	egl.eglx.lang.AnyException.prototype.eze$$DebugValue = function() {
		return this.eze$$getName() + " " + this.message;
	};

	//egl.trace = true;

	egl.binarySearch = function( /*int[]*/ values, /* int */ value ) {
		var left = 0;
		var right = values.length - 1;
		
		while(left <= right){
			var middle = Math.floor((left + right) / 2);
			var currentValue = values[middle];
			
			if( currentValue > value ){
				right = middle - 1;
			}else if( currentValue < value ){
				left = middle + 1;
			}else {
				return middle;
			}
		}
		
		return (-left) - 1;
	};

	egl.canSendEventToIDE = function(){
		return egl.sessionIsValid && egl.contextAware;
	};

	egl.getContextKey = function() {
		return egl.contextKey;
	};

	egl.setContextkey = function(ckey) {
		egl.contextKey = cKey;
	};

	egl.parseURL = function() {
		var fullUrl = new String(window.location);
		var queryIndex = fullUrl.lastIndexOf("?");
		if (queryIndex != -1) {
			var params = fullUrl.substr(queryIndex + 1);
			if (params.indexOf("&") == -1) {
				var cArr = params.split("=");
				//alert(cArr[1]);
				this.contextKey = cArr[1].replace(/#.*/,"");
				//egl.setContextKey(cArr[1]);
				//alert("cKey: " + this.getContextKey());
			} else {
				// assume for now this is the only parameter
			}
		}	
	};

	if(egl.contextAware){
		egl.parseURL();
	}

	//Send a request to the IDE Gateway from the browser
	//A random key is appended to avoid having cached results returned
	egl.loadIDEURL = function(url, handler, synchronous, sendAsContent) {
		if (parseInt(url.indexOf("?")) > 1) {
			egl.loadURL(url + "&key=" + Math.random() + "&contextKey=" + egl.getContextKey(), handler, synchronous, sendAsContent);
		} else {
			egl.loadURL(url + "?key=" + Math.random() + "&contextKey=" + egl.getContextKey(), handler, synchronous, sendAsContent);		
		}
	};

	egl.loadScript = function(packageName, className) {
		if ( egl.ptCrash )
			return;
		var pkg = egl.makePackage( packageName );
		if ( pkg[className] ) {	return; }
		packageName = packageName.replace(/\./g,"/");
		egl.loadURL( "___loadScript?fileName=" + egl__contextRoot + "/" + packageName + "/" + className + ".js", 
				function( responseText ) {
			        responseText = responseText.replace(/\<!--.*\-->/, "" );
			        var index = responseText.indexOf("<script>");
			        if (index != -1)
			        	responseText = responseText.substring(index+8);
			        egl.canPrintToConsole = false;
					try {
						egl.eval( responseText );
			   		}
			   		catch (e) {
			   			if (e.name != "SyntaxError" && e != "missing" && egl && !egl.ptCrash) { // make sure the page is not destructed yet
			   				egl.ptCrash = true;
			   				document.location = document.location;
	   						return;
			   			}
			   		}
			   		if ( !egl.ptCrash )
			   			egl.canPrintToConsole = true;
				}
				, true, null );
	};

	//Send a request to the IDE Gateway from the browser
	egl.loadURL = function(url, handler, synchronous, sendAsContent) {
		if(egl.canSendEventToIDE()){
			var xmlhttp = egl.newXMLHttpRequest();
		    if (xmlhttp) {
		    	function runHandler() {
		    		// make sure the event did not arrive for a document 
	             	// that has been refreshed. In that case, egl==null
	             	if (handler && typeof(egl) !== "undefined") {
	              	handler(xmlhttp.responseText);
	              }
	          }
	          if (synchronous) {
	          	//For synchronous calls, we will automatically invoke the handler when the load returns
			        xmlhttp.onreadystatechange = function() {};
	          }
	          else {
			        xmlhttp.onreadystatechange = function() {
			            if (xmlhttp.readyState==4) {
			            	runHandler();
			            }
			        };
		        }
			    try { 
			    	if (sendAsContent || url.length > 256) {
			    		var parameters;
			    		var index = url.indexOf('?');
			    		if (index == -1) {
			    			parameters = "";
			    		}
			    		else {
			    			parameters = url.substring(index+1);
			    			url = url.substring(0, index);
			    		}
				        xmlhttp.open( 'POST', url, !synchronous );
				   		xmlhttp.setRequestHeader('Content-Length', parameters.length);
				   		xmlhttp.setRequestHeader('Content-Type', 'application/text');
						xmlhttp.send( parameters );
			    	}
			    	else {
				    	xmlhttp.open( 'POST', url, !synchronous );
						xmlhttp.send( null );
					}

					if (synchronous) {
						runHandler();
					}

					setTimeout(function() { 
						           xmlhttp.onreadystatechange = function() { return function() { }; }(); 
					               xmlhttp.abort(); 
					           }, 15000);
			    } catch (e) { 
			    	if (egl.egl.debug && e instanceof egl.egl.debug.DebugTermination){
			    		throw e;
			    	}
		        	egl.printError(egl.getRuntimeMessage( "CRRUI2090E", [url]), e);
		        }
		    }
		}
		else {
			egl.printError(egl.getRuntimeMessage( "CRRUI2091E", [url]), null);
		}
	};

	egl.startHandleIDEEvent = function(){
		if(egl.canSendEventToIDE() && (egl.enableEditing || egl.debugg)){
			egl.handleIDEEvent();
		}
		egl.startHandleIDEEvent = function(){};
	};

	egl.handleIDEEvent = function() {
		if(egl.canSendEventToIDE()){
			egl.loadIDEURL('___getevent', function(event) {
				if("context terminated" != event){
					var print = null;
					try {
						print = egl.print;
						if (event != "") {
							egl.print = function() {};
							eval(event);
						}
			   		}
			   		catch (e) {
			   			egl.print = print;	
			   			if (egl && !egl.ptCrash) { // make sure the page is not destructed yet
			   				if (egl.egl.debug && e instanceof egl.egl.debug.DebugTermination) {
			   					if (e.msg) egl.println(e.msg);
			   				}
			   				else {
			   					if (event.match(/egl.partialTerminateSession().*/))
			   						document.location = document.location;	
			   					else
			   						egl.printError(egl.getRuntimeMessage( "CRRUI2092E", [event]), e);
			   				}
			   			}
			   		}
		   			egl.print = print;	
			   		if (window.setTimeout)
			   			window.setTimeout("if (window.egl) egl.handleIDEEvent()", 10);
				}
			}, false);
		}
	};

	window.onunload = function() {
		try { egl.terminateSession(); } catch (e) {}
	};

	egl.getElementAt = function(x, y, element) {
		if (!element ) {
			return null;
		}
		var eX = egl.getX(element);
		var eY = egl.getY(element);
		var eWidth = egl.getWidthInPixels(element);
		var eHeight = egl.getHeightInPixels(element);
		if (x < eX || x > eX + eWidth || y < eY || y > eY + eHeight) {
			return null;
		}
		for (var child = element.firstChild; child; child = child.nextSibling) {
			if (child.style && child.style.visibility != "hidden") {
				var children = egl.getElementAt(x, y, child);
				if (children) return children;
			}
		}
		return (element);
	};

	egl.doWidgetClick = function( x, y ) {
		var element = egl.getElementAt( x, y, document.body );
		if ( element == null ) {
			return;
		}
	    if (egl.IE && egl.IEVersion < 9 && document.createEventObject) {    //Internet Explorer     
	        var mousedownEvent = document.createEventObject (event);
	        mousedownEvent.button = 1;  // left button is down
	        element.fireEvent ("onclick", mousedownEvent);
	    } else { // Firefox, Safari, Opera, IE9+    
	        var mousedownEvent = document.createEvent("MouseEvent");
	        mousedownEvent.initMouseEvent ("click", true, true, window, 1, 0, 0, 0, 0, false, false, false, false, 0, null);
	        element.dispatchEvent (mousedownEvent);
	    }
	    setTimeout( "egl.getWidgetPositions()", 400 );
	};

	egl.traceStartupTime = window.egl__traceStartupTime;
	egl.traceInternals = window.egl__traceInternals;
	if (egl.debugg){egl.nextDebuggerPoll = new Date().getTime() + 1000;}

	egl.getValueForDebug = function(variableName, functionName, object, args) {
		if (variableName !== "") {
			var value = null;
			try {
				value = "{"+variableName+":";
				if (variableName === "arguments") {
					value += "{ ";
					for (var n=0; n<args.length; n++) {
						if (n>0) value += ", ";
						value += n+":"+egl.eglx.json.toJSONString(args[n],0,1);
					}
					value += "}";
				}
				else {
					value += egl.eglx.json.toJSONString(eval("object."+variableName),0,1);
				}
				value += "}";
			}
			catch (e) {
				value = "{}";
			}
			egl.loadIDEURL("___debugValue?variableName="+encodeURIComponent(variableName)+"&value="+value, 
				function(response) {
					egl.getValueForDebug(response, functionName, object, args);
				}, true);
		}
	};

	egl.debugStack = [];

	egl.getStackString = function() {
		var soFarOnlyInit = true;
		var str = "";
		for (var i=0; i<egl.debugStack.length; i++) {
			if (soFarOnlyInit) {
				if (egl.debugStack[i].functionName != "<init>") {
					soFarOnlyInit = false;
					str = ""; // Remove any <init> frames
				}
			}
			
			var tempStr;
			tempStr = egl.debugStack[i].fileName ? encodeURIComponent(egl.debugStack[i].fileName) : "<<undefined>>";
			tempStr = tempStr.replace(/(,)|(\|)/g, '-');				
			str += tempStr;
			str += ",";

			tempStr = egl.debugStack[i].functionName ? egl.debugStack[i].functionName : "<<undefined>>";
			tempStr = tempStr.replace(/(,)|(\|)/g, '-');
			if ( tempStr.indexOf("new&nbsp;") == 0 ) {
				tempStr = "<init>";
			}
			str += tempStr;
			str += ",";

			str += egl.debugStack[i].lineNumber ? egl.debugStack[i].lineNumber : -1;
			str += ",";
			
			str += i;
			
			//Add in the variables!
			egl.buildVariableInfos(egl.debugStack[i]);
			var varString = egl.getVariableInfoString(egl.debugStack[i]);
			if (varString) {
				str += ",";
				str += varString;
			}
			if(i != egl.debugStack.length - 1) {
				str += "|";
			}
		}
		
		return str;
	};

	egl.getVariableInfoString = function(frame) {
		if (frame.variableInfos.length == 0) {
			return null;
		}
		
		var str = "";
	  
		for (var i=0; i<frame.variableInfos.length; i++) {
			if(i != 0) {
				str += ",";
			}
			var next = frame.variableInfos[i];
			str += next.name + ",";
			str += ("jsName" in next ? (next.jsName || " ") : next.name) + ",";
			str += ("getter" in next ? (next.getter || " ") : " ") + ",";
			str += ("setter" in next ? (next.setter || " ") : " ") + ",";
			str += next.index + ",";
			if (next.type){
				str += egl.tweakTypeForDebug(next.type, next) + ",";
			}else{
				str += " ,";
			}
			
			var children = egl.getVariablesFromObject(next.value);
			str += (children && children.length > 0) ? "1" : "0";
		}	
		return str;
	};

	egl.tweakTypeForDebug = function(type, variableInfo){
		if (!variableInfo) {
			return type;
		}
		
		// Non-null ANYs can obtain the element type info.
		if (type == "any" || type == "number" || type == "number?") {
			var valueObjectType = egl.resolveAnyType(variableInfo.value);
			if (valueObjectType) {
				type += ": " + valueObjectType; // any: string
			}
		}
		
		return type;
	};

	egl.resolveAnyValue = function(any) {
		if (any == null) {
			return null;
		}
		
		// resolve any -> any -> any -> string
		var valueObject = any;
		while (valueObject != null && typeof valueObject === "object" && "eze$$value" in valueObject) {
			valueObject = valueObject.eze$$value;
		}
		return valueObject;
	};

	egl.resolveAnyType = function(any) {
		if (any == null) {
			return null;
		}
		
		// resolve any -> any -> any -> string
		var valueObject = any;
		var currType;
		while (valueObject != null && typeof valueObject === "object" && "eze$$value" in valueObject) {
			currType = valueObject.eze$$signature;
			valueObject = valueObject.eze$$value;
		}
		
		if (currType) {
			return egl.getDebugTypeFromSig(currType);
		}
		return "";
	};

	egl.getDebugType = function(obj) {
		if (obj == null) {
			return "";
		}
		
		return egl.getDebugTypeFromSig(egl.inferSignature(obj));
	};

	egl.getDebugTypeFromSig = function(sig) {
		if (!sig) {
			return "";
		}
		
		var type = egl.typeName(sig);
		switch (type) {
			case "null":
			case "unknown":
				return "";
			default:
				return type;
		}
	};

	egl.buildVariableInfos = function(frame) {
		frame.variableInfos = [];
		frame.variableIndex = 0;
		frame.eze$$localVariableInfos = [];
		frame.localVariableIndex = 0;

		if (frame.obj && frame.obj.eze$$getName) {
			frame.variableInfos[frame.variableIndex] = {name : frame.obj.eze$$getName(), value : frame.obj, index : frame.variableIndex, jsName : "eze$$this"};
			frame.variableIndex++;
		}
		
		if (frame.functionName) {
			frame.variableInfos[frame.variableIndex] = {name: frame.functionName, value : frame, index : frame.variableIndex, jsName : ""};
			frame.variableIndex++;
		}
		
		//Check to see if there are any local function variables
		if (frame.blockStack) {
			for (var i=0; i<frame.blockStack.length; i++) {
				var variables = frame.blockStack[i];
				for (var j=0; j<variables.length; j++) {
					var variable = variables[j];
					variable.index = frame.localVariableIndex;
					frame.eze$$localVariableInfos[frame.localVariableIndex] = variable;
					frame.localVariableIndex++;
				}
			}
		}
	};

	egl.beginWidgetPosition = function() { 
		// called by partial update logic in VE, leave here until we remove that call from the VE
	};

	egl.debugStackSize = 0;

	egl.enter = function(functionName, object, args, varUpdater){
		try {
			var longName = (object ? object.eze$$typename +"." : "")+functionName;
			longName = longName.replace(/</, "&lt;");
			if (!egl.debugg) {
				egl.debugStackSize += 1;
				egl.debugStack.length = egl.debugStackSize;
			}
			egl.debugStack.push( { functionName:functionName, longFunctionName:longName, toString:function(){return "";}, updater:varUpdater } );
			egl.lastFunctionEntered = functionName;
		}
		catch (e) {
			egl.println("Internal error inside egl.enter "+functionName+" "+e.message);
		}
	};

	egl.leave = function() {
		try {
			var stackTop;
			if (egl.debugg) {
				stackTop = egl.debugStack.pop();
			}
			else {
				egl.debugStackSize = Math.max(0, egl.debugStackSize-1);
				stackTop = egl.debugStack[egl.debugStackSize];
			}
			if (stackTop) egl.lastFunctionEntered = stackTop.functionName;
			if (egl.debugg) {
				if (egl.debugStack.length == 0) {
					egl.stepKind = null;
					// When done executing, need to go back to displaying "Running" in Launch view
					egl.loadIDEURL("___debugResume?reason=clientRequest", null, !egl.Firefox);
					
					if (egl.debugCallbacks && egl.debugCallbacks.length > 0) {
						var func = egl.debugCallbacks[0].func;
						var args = egl.debugCallbacks[0].args;
						var handler = egl.debugCallbacks[0].handler;
						
						egl.debugCallbacks.splice(0,1);
						
						if (args instanceof Array) {
							func.apply(handler, args);
						}
						else {
							func.call(handler, args);
						}
					}
				}
			}
		}
		catch (e) {
			egl.println("Internal error inside egl.leave "+functionName+" "+e.message);
		}
	};

	egl.breakpoints = null;
	egl.singleUseBreakpoints = [];
	egl.breakpointManagerEnabled = true;

	egl.addBreakpoint = function(file, line, enabled) {
		if (!egl.breakpoints) {
			return;
		}

		file = decodeURIComponent(file);

		var breakpointsForFile = null;
		for(var i = 0; i < egl.breakpoints.length; i++){
			if(egl.breakpoints[i].key == file){
				breakpointsForFile = egl.breakpoints[i].value;
				break;
			}
		}	
		if(!breakpointsForFile){
			breakpointsForFile = [];
			egl.breakpoints[egl.breakpoints.length] = { key : file, value : breakpointsForFile};
		}
		else {
			for(var i = 0; i < breakpointsForFile.length; i++){
				if (breakpointsForFile[i].line == line) return;//don't add dupes
			}
		}
		breakpointsForFile[ breakpointsForFile.length ] = {line : line, enabled : enabled};
	};

	egl.removeBreakpoint = function(file, line) {
		if (!egl.breakpoints) {
			return;
		}
		
		file = decodeURIComponent(file);
		
		for(var i = 0; i < egl.breakpoints.length; i++){
			if(egl.breakpoints[i].key == file){
				for (var j = 0; j < egl.breakpoints[i].value.length; j++) {
					if (egl.breakpoints[i].value[j].line == line) {
						egl.breakpoints[i].value.splice(j,1);
						if (egl.breakpoints[i].value.length == 0) {
							egl.breakpoints.splice(i,1);
						}
						return;
					}
				}
			}
		}	
	};

	egl.changeBreakpoint = function(file, line, enabled) {
		if (!egl.breakpoints) {
			return;
		}
		
		file = decodeURIComponent(file);
		
		for(var i = 0; i < egl.breakpoints.length; i++){
			if(egl.breakpoints[i].key == file){
				for (var j = 0; j < egl.breakpoints[i].value.length; j++) {
					if (egl.breakpoints[i].value[j].line == line) {
						egl.breakpoints[i].value[j].enabled = enabled;
					}
				}
			}
		}
	};

	egl.addSingleUseBreakpoint = function(file, line) {
		var breakpointsForFile = null;
		for(var i = 0; i < egl.singleUseBreakpoints.length; i++){
			if(egl.singleUseBreakpoints[i].key == file){
				breakpointsForFile = egl.singleUseBreakpoints[i].value;
				break;
			}
		}	
		if(!breakpointsForFile){
			breakpointsForFile = [];
			egl.singleUseBreakpoints[egl.singleUseBreakpoints.length] = {key : file, value : breakpointsForFile};
		}
		else {
			for(var i = 0; i < breakpointsForFile.length; i++){
				if (breakpointsForFile[i] == line) return;//don't add dupes
			}
		}
		breakpointsForFile[ breakpointsForFile.length ] = line;
	};

	egl.isBreakpoint = function(file, line) {
		if (egl.breakpointManagerEnabled) {
			// Check for single-use breakpoint.
			for( var i = 0; i < egl.singleUseBreakpoints.length; i++){
				var key = egl.singleUseBreakpoints[i].key;
				if(key == file){
					for( var j = 0; j < egl.singleUseBreakpoints[i].value.length; j++){
						var nextLine = egl.singleUseBreakpoints[i].value[j];
						if(nextLine == line) {
							// Remove it now that it's been used.
							egl.singleUseBreakpoints[i].value.splice(j,1);
							if(egl.singleUseBreakpoints[i].value.length == 0){
								egl.singleUseBreakpoints.splice(i,1);
							}
							return true;
						}
					}
				}
			}
			
			for( var i = 0; i < egl.breakpoints.length; i++){
				var key = egl.breakpoints[i].key;
				if(key == file){
					for( var j = 0; j < egl.breakpoints[i].value.length; j++){
						var nextBP = egl.breakpoints[i].value[j];
						if(nextBP && nextBP.enabled && nextBP.line == line) {
							return true;
						}
					}
					return false;
				}
			}
		}
		return false;
	};

	egl.breakpointManagerChanged = function(enabled) {
		egl.breakpointManagerEnabled = enabled;
	};

	egl.disconnectDebugger = function() {
		var message;
		if (egl.debugg){
			egl.debugg = false;
			egl.loadIDEURL("___debugTerminate", function(response) {
					message = response;
				}, true);
			egl.suspendReason = null;
			egl.breakpoints = null;
			egl.nextDebuggerPoll = null;
			egl.contextKey = null;
			egl.sessionIsValid = false;
		}
		throw new egl.egl.debug.DebugTermination(message || "");
	};

	egl.suspendReason = null;
	egl.stepKind = null;
	egl.stepStartDepth = 0;
	egl.debugInitialized = false;

	egl.atLine = function(file, line, offset, length, thisObj) {
		if (egl.processingAtLine) {
			return;
		}
		if (!egl.debugg && egl.trace) {
			var f = file.replace(/.\//,"");
			if (egl.lastActiveFile != file) egl.print("<br>At "+f+": ");
			if (egl.lastActiveLine != line) egl.print("<a target=trace href=___openFile?file="+file+"&line="+line+">"+line+":</a> ");
		}
		
		try {
			egl.processingAtLine = true;
			egl.lastActiveFile = file;
			egl.lastActiveLine = line;
		
			var topStack = egl.debugStack[egl.debugStack.length-1];
			if (!egl.debugg) {
				topStack = egl.debugStack[egl.debugStackSize];
			}
			if (topStack) {
				topStack.fileName = file;
				topStack.lineNumber = line;
				topStack.offset = offset;
				topStack.length = length;
				topStack.obj = thisObj;	
			}
			
			if (egl.debugg) {
				
				if(!egl.debugInitialized) {
					egl.debugInitialized = true;
					
					// since this is the first atline we've run, let's send the locale info now.
					var args = "";
					var decimal = egl.getDecimalSymbol();
					var currency = egl.getCurrencySymbol();
					if (decimal) {
						args += "decimalSymbol=" + decimal;
					}
					if (currency) {
						args += (args ? "&" : "") + "currencySymbol=" + currency;
					}
					if (args) {
						egl.loadIDEURL("___localeSettings?" + args, null, true);
					}
					
					// Finally let's init the breakpoint manager enablement
					egl.loadIDEURL("___getBreakpointManagerState", function(response) {
						egl.breakpointManagerEnabled = response == "true";
					}, true);
				}
				
				if(new Date().getTime() > egl.nextDebuggerPoll) {
					egl.loadIDEURL("___getUserDebugRequest", function(response) {
						if(response == "suspend"){
							egl.debugSuspend();
						}
						else if(response == "terminate"){
							egl.disconnectDebugger();
						}
					}, true);
					
					egl.nextDebuggerPoll = new Date().getTime() + 1000;
				}
				
				if(egl.suspend) {
					egl.addSingleUseBreakpoint(file, line);
					egl.suspend = null;
				}
				
				if(!egl.breakpoints) {
					egl.breakpoints = [];
					
					egl.loadIDEURL("___getExistingBreakpoints", function(response) {
						var arr = response.split(",");
						for(var i = 0; i < arr.length; i+=3) {
							egl.addBreakpoint(arr[i], arr[i+1], arr[i+2] == "true");
						}
					}, true);
				}
		
				var bottomStack = egl.debugStack[0];
				if(!bottomStack.fileName && !bottomStack.lineNumber) {
					bottomStack.fileName = file;
					bottomStack.obj = thisObj;
				}
				
				if(egl.skipSuspend && egl.skipSuspend.line == line && egl.skipSuspend.file == file) {
					// We're either stepping over this line or resuming; don't suspend until the line is finished.
					// Do this before isBreakpoint(), in case the line we're stepping over has a breakpoint on it.
				}
				else if(egl.isBreakpoint(file, line)) {
					egl.suspendReason = "breakpoint";
					egl.stepKind = null;
				}
				else if(egl.stepKind) {
					if("over" == egl.stepKind && egl.debugStack.length <= egl.stepStartDepth) {
						egl.suspendReason = "step";
						egl.stepKind = null;
					}
					else if("in" == egl.stepKind) {
						egl.suspendReason = "step";
						egl.stepKind = null;
					}
					else if("out" == egl.stepKind  && egl.debugStack.length < egl.stepStartDepth) {
						egl.suspendReason = "step";
						egl.stepKind = null;
					}
				}
				else if(egl.skipSuspend && egl.debugStack.length <= egl.stepStartDepth) {
					egl.skipSuspend = null;// clear out in case we resumed on a loop
				}
				
				var backgroundRequest = null;
				while(egl.suspendReason) {	
					var ideString;
					if (backgroundRequest) {
						ideString = "___atLine?file="+encodeURIComponent(file)+"&line="+line+"&offset="+offset+"&length="+length;
						backgroundRequest = null;
					}	
					else {
						ideString = "___atLine?file="+encodeURIComponent(file)+"&line="+line+"&offset="+offset+"&length="+length+"&suspendReason="+egl.suspendReason;
					}
					
					egl.loadIDEURL(ideString,
						function(response) {
							if("resume" == response) {
								egl.stepStartDepth = egl.debugStack.length;
								egl.suspendReason = null;
								egl.loadIDEURL("___debugResume?resumeReason=clientRequest", null, true);
								egl.skipSuspend = {line : line, file : file};
							}
							else if("stack" == response) {
								var url = "___debugStack?stack=";
								try {
									url += encodeURIComponent(egl.getStackString());
								} catch(e){}
								egl.loadIDEURL(url, null, true);
							}
							else if(response && response.length > 8 && "stepOver" == response.substring(0, 8)) {
								var arr = response.split(" ");
								egl.stepStartDepth = parseInt(arr[1]) + 1;
								egl.suspendReason = null;
								
								if (egl.stepStartDepth < egl.debugStack.length) {
									// 'step over' on non-top frame, which is the same as step return of 1 frame above
									egl.stepKind = "out";
									egl.stepStartDepth += 1;
								}
								else {
									egl.stepKind = "over";
								}
								egl.loadIDEURL("___debugResume?resumeReason=stepOver", null, true);
								egl.skipSuspend = {line : line, file : file};
							}
							else if("stepIn" == response) {
								egl.stepStartDepth = egl.debugStack.length;
								egl.stepKind = "in";
								egl.suspendReason = null;
								egl.loadIDEURL("___debugResume?resumeReason=stepIn", null, true);
								egl.skipSuspend = {line : line, file : file};
							}
							else if(response && response.length > 7 && "stepOut" == response.substring(0, 7)) {
								var arr = response.split(" ");
								egl.stepStartDepth = parseInt(arr[1]) + 1;
								egl.stepKind = "out";
								egl.suspendReason = null;
								egl.loadIDEURL("___debugResume?resumeReason=stepOut", null, true);
								egl.skipSuspend = {line : line, file : file};
							}
							else if(response && response.length > 9 && "var_Value" == response.substring(0, 9)) {
								var url = "___varValue?value=";
								try {
									url += encodeURIComponent(egl.getVariableValue(response));
								} catch(e){}
								url += "&type=";
								try {
									url += encodeURIComponent(egl.getVariableType(response));
								} catch(e){}
								egl.loadIDEURL(url, null, true, true);
								backgroundRequest = true;
							}
							else if(response && response.length > 13 && "var_Variables" == response.substring(0, 13)) {
								var url = "___varVariables?variables=";
								try {
									url += encodeURIComponent(egl.getDebugVariablesString(response));
								} catch (e) {}
								egl.loadIDEURL(url, null, true, true);
								backgroundRequest = true;
							}
							else if(response && response.length > 12 && "var_SetValue" == response.substring(0, 12)) {
								var url = "___varSetValue?success=";
								try {
									url += egl.setVariableValue(response);
								} catch(e) {
									url += "0";
								}
								egl.loadIDEURL(url, null, true, true);
								backgroundRequest = true;
							}
							else if(response && response.length > 14 && "addBreakpoint" == response.substring(0,13)) {
								var arr = response.substring(14).split(",");
								egl.addBreakpoint(arr[0], arr[1], arr[2] == "true");
								backgroundRequest = true;
							}
							else if(response && response.length > 17 && "removeBreakpoint" == response.substring(0,16)) {
								var arr = response.substring(17).split(",");
								egl.removeBreakpoint(arr[0], arr[1]);
								backgroundRequest = true;
							}
							else if(response && response.length > 17 && "changeBreakpoint" == response.substring(0,16)) {
								var arr = response.substring(17).split(",");
								egl.changeBreakpoint(arr[0], arr[1], arr[2] == "true");
								backgroundRequest = true;
							}
							else if(response && response.length > 21 && "breakpointEnablement" == response.substring(0,20)) {
								egl.breakpointManagerChanged(response.substring() == "true");
								backgroundRequest = true;
							}
							else if("disconnect" == response) {
								egl.disconnectDebugger();
							}
							else if("silentTerminate" == response) {
								throw new egl.egl.debug.DebugTermination("");
							}
						}, 
						true);
				}
			}
		}
		finally {
			egl.processingAtLine = false;
		}
	};

	egl.getVariableValue = function(response) {
		var arr = response.split(" ");
		var frameIndex = parseInt(arr[1]);
		var variableIndex = parseInt(arr[2]);
		var frame = egl.debugStack[frameIndex];
		var value = frame.variableInfos[variableIndex].value;
		
		var varValue;
	    
		try {
			value = egl.resolveAnyValue(value); // ANYs
			if (value != null) {
				if (value instanceof egl.eglx.lang.AnyException) {
					varValue = value.eze$$DebugValue();
				}
				else if (value.eze$$getName || value instanceof egl.eglx.lang.EDictionary) {
					varValue = "";
				}
				else if (value instanceof Date) {
					try {
						var type = frame.variableInfos[variableIndex].type;
						if (type == "eglx.lang.EDate") {
							varValue = egl.eglx.lang.EString.fromEDate(value);
						}
	//unsupported 0.7					else if (type == "eglx.lang.ETime") {
//								varValue = egl.eglx.lang.EString.fromETime(value);
//							}
						else {
							varValue = egl.eglx.lang.EString.fromETimestamp(value);
						}
					}
					catch(e) {
						if (typeof e == "object" && "message" in e) {
							varValue = e.message;
							if ("messageID" in e) {
								varValue = e.messageID + ": " + varValue;
							}
						}
						else {
							varValue = String(e);
						}
					}
				}
				else if (typeof value === "string") {
					varValue = "\"" + value + "\""; // display with quotes
				}
				else if (typeof value === "number") {
					varValue = egl.getFormattedNumber(value);
				}
				else if (value instanceof egl.javascript.BigDecimal) {
					varValue = egl.getFormattedBigDecimal(value, frame.variableInfos[variableIndex].type);
				}
				else {
					varValue = String(value);
				}
			}
			else {
				varValue = "null";
			}
		}
		catch (e) {
			varValue = "--Error rerieving value--";
		}
		
		return varValue;
	};

	egl.getVariableType = function(response) {
		var arr = response.split(" ");
		var frameIndex = parseInt(arr[1]);
		var variableIndex = parseInt(arr[2]);
		var frame = egl.debugStack[frameIndex];
		var type = frame.variableInfos[variableIndex].type;
		
		if (type != null) {
	  		return egl.tweakTypeForDebug(type, frame.variableInfos[variableIndex]);
		}
		else {
			return "";
		}
	};

	egl.evTerminateReloadHandler = function() {
		var packageName, typeName;
		try {
		    egl.canPrintError = false;
			if ( egl.rootHandler ) {
				packageName = egl.rootHandler.eze$$package.replace(/\./g, '/');
				typeName = egl.rootHandler.eze$$typename;
				egl.partialTerminateSession();
			}
			require(["___reloadHandler?key=" + Math.random() + "&contextKey=" + egl.getContextKey() + "&fileName=/" + egl__contextRoot + "/" + packageName + "/" + typeName + ".js"], function(){
				egl.startupInitCallback();
				egl.beginWidgetPosition();
				egl.startVEWidgetTimer();
				egl.canPrintError = true;
			});
		} catch ( e ) {
			document.location = document.location;
		}
	};

	egl.getFormattedNumber = function(value) {
		var tempValue = String(value);
		var decimal = egl.getDecimalSymbol();
		if (decimal && decimal != ".") {
			tempValue = tempValue.replace(".", decimal);
		}
		return tempValue;
	};

	egl.getFormattedBigDecimal = function(value, type) {
		var tempValue = String(value);
		
		var decimal = egl.getDecimalSymbol();
		if (decimal && decimal != ".") {
			tempValue = tempValue.replace(".", decimal);
		}
		
		return tempValue;
	};

	egl.setVariableValue = function(response) {
		try {
			var arr = response.split(" ");
			var frameIndex = parseInt(arr[1]);
			var frame = egl.debugStack[frameIndex];
			if (typeof(frame.updater) === "function") {
				var variableIndex = parseInt(arr[2]);
				var LHS = arr[3];
				var RHS = decodeURIComponent(arr[4]);
				var getter = decodeURIComponent(arr[5]).trim();
				var setter = decodeURIComponent(arr[6]).trim();
				
				frame.variableInfos[variableIndex].value = frame.updater(LHS, RHS, getter, setter);
				return "1";
			}
		} catch (e) {}
		return "0";
	};

	egl.getDebugVariablesString = function(response) {
		var arr = response.split(" ");
		var frameIndex = parseInt(arr[1]);
		var variableIndex = parseInt(arr[2]);
		var frame = egl.debugStack[frameIndex];
		var value = frame.variableInfos[variableIndex].value;
		
		var children = egl.getVariablesFromObject(value);
		if (!children || children.length == 0) {
			return "";
		}
		
		// Array elements should retain type and name info from the parent.
		var type = frame.variableInfos[variableIndex].type;
		if (type && type.indexOf("eglx.lang.EList<") == 0 && type.charAt(type.length-1) == ">") {
			type = type.substring(16, type.length-1);
			for (var i=0; i<children.length; i++) {
				children[i].type = type;
				children[i].name = "[" + (i+1) + "]";
				
				var jsName = frame.variableInfos[variableIndex].jsName;
				if (jsName == null) {
					jsName = frame.variableInfos[variableIndex].name;
				}
				children[i].jsName = jsName + "[" + (i) + "]";
			}
		}
		
	 	var varString = "";
		for (var i=0; i<children.length; i++) {
			var tempString = "";
			try {
				children[i].index = frame.variableIndex;
				frame.variableInfos[frame.variableIndex] = children[i];
				frame.variableIndex++;
				if(i != 0) {
					tempString += ",";
				}
				tempString += children[i].name + ",";
				// jsName might be intentionally blank
				tempString += ("jsName" in children[i] ? (children[i].jsName || " ") : children[i].name) + ",";
				tempString += ("getter" in children[i] ? (children[i].getter || " ") : " ") + ",";
				tempString += ("setter" in children[i] ? (children[i].setter || " ") : " ") + ",";
				tempString += children[i].index + ",";
				if (children[i].type){
					try {
						tempString += egl.tweakTypeForDebug(children[i].type, children[i]) + ",";
					} catch (e) {
						tempString += " ,";
					}
				}else{
					tempString += " ,";
				}
				
				var grandKids;
				try {
					grandKids = egl.getVariablesFromObject(children[i].value);
				}catch (e){}
				tempString += (grandKids && grandKids.length > 0) ? "1" : "0";
				varString += tempString;
			}
			catch (e) {}
		}
		return varString;
	};

	egl.getVariablesFromObject = function(value) {
		value = egl.resolveAnyValue(value);
		if (!value) {
			return null;
		}
		
		if (typeof value === "object" && "eze$$localVariableInfos" in value) {
			return value.eze$$localVariableInfos;
		}
		
		try {
			if (value.eze$$getChildVariables) {
				return value.eze$$getChildVariables();
			}
			else {
				if ((typeof value == "number") ||
						(typeof value == "string") ||
						(typeof value == "function") ||
						value instanceof egl.javascript.BigDecimal) {
					return null;
				}

				var childVars = [];
				for (f in value) {
					if ( typeof value[f] == "function" ) continue;
					if ( f.toString().indexOf("eze$$") == 0 ) continue;
					childVars.push({name : f.toString(), value : value[f], type : egl.getDebugType(value[f])});
				}
				return childVars;
			}
		}
		catch (e) {
			return null;
		}
	};

	egl.addLocalFunctionVariable = function(varName, varValue, varType, varJSName) {
		if (egl.debugg && egl.debugStack.length > 0) {
			var topStack = egl.debugStack[egl.debugStack.length-1];
			var blockStack = topStack.blockStack;
			
			//If there is not a blockStack, create one
			if (!blockStack) {
				blockStack = [];
				topStack.blockStack = blockStack;
			}
			
			//If the blockStack has no entries, add one
			if (blockStack.length == 0) {
				blockStack.push([]);			
			}
			
			//Push the variable onto the current blockStack
			blockStack[blockStack.length - 1].push({name: varName, value : varValue, type : varType, jsName : varJSName});
		}
	};

	egl.setLocalFunctionVariable = function(varName, varValue, varType) {
		if (egl.debugg && egl.debugStack.length > 0) {
			var topStack = egl.debugStack[egl.debugStack.length-1];
			var blockStack = topStack.blockStack;
			if (!blockStack || blockStack.length == 0) {
				return null;
			}
			
			//Start with the current block and work backwards, looking for the variable 
			for (var i=blockStack.length - 1; i>=0; i--) {
				var variables = blockStack[i];
				
				//Loop through the variables in the block, looking for one with the right name
				for (var j=0; j<variables.length; j++) {
				   if (variables[j].name == varName) {
				   		variables[j].value = varValue;
				   		variables[j].type = varType;
				   }
				}		
			}
		}
	};

	egl.enterBlock = function() {
		if (egl.debugg && egl.debugStack.length > 0) {
			var topStack = egl.debugStack[egl.debugStack.length-1];
			var blockStack = topStack.blockStack;
			if (!blockStack) {
				blockStack = [];
				topStack.blockStack = blockStack;
			}
			//Push an array onto the stack. The array will hold the variables for this block
			blockStack.push([]);
		}
	};

	egl.exitBlock = function() {
		if (egl.debugg && egl.debugStack.length > 0) {
			var topStack = egl.debugStack[egl.debugStack.length-1];
			var blockStack = topStack.blockStack;
			if (!blockStack) {
				blockStack = [];
				topStack.blockStack = blockStack;
			}
			blockStack.pop();
		}
	};

	egl.showEditingFeedback = function() {
		document.body.innerHTML = "document being edited...";
	};

	egl.getWidgetPositions = function() {
		var result = [];
		if(egl.canSendEventToIDE()) {
			if(egl.Document.body.getChildren() != null)
				result = egl.getWidgetInfo();
			egl.loadIDEURL("___widgetPositions?value=" + encodeURIComponent(result), null, false, false);
		}
		return result;
	};

	egl.debugCallback = function(handler, func, args){
		// A synchronous XMLHttpRequest will "yield" to an asynchronous
		// XMLHttpRequest callback, so we must control its invocation manually.
		if (egl.debugg) {
			if (egl.debugStack.length == 0) {
				if (args instanceof Array) {
					func.apply(handler, args);
				}
				else {
					func.call(handler, args);
				}
			}
			else {
				if (!egl.debugCallbacks) {
					egl.debugCallbacks = [];
				}
				egl.debugCallbacks.push({handler : handler, func : func, args : args});
			}
		}
	};

	egl.debugSuspend = function(){
		// Called by IDE to suspend next line.
		egl.suspend = true;
	};

	egl.setWidgetLocation = function(widgetRef, variableName, offset, length, isMoveable){
		try {
			widgetRef.eze$$variableName = variableName;
			widgetRef.eze$$offset = offset;
			widgetRef.eze$$length = length;
			widgetRef.eze$$moveable = isMoveable;
		}
		catch (E) { }
		return widgetRef;
	};

	egl.setWidgetMoveable = function(widgetRef, variableName){
		if(widgetRef != null){
			try {
				widgetRef.eze$$variableName = variableName;
				widgetRef.eze$$moveable = true;
			}
			catch (E) {}
		}
	};

	egl.onerror = function(msg, url, line) {
		if (url.match(/http.*/)) {
			url = url.substring(7);
			url = url.substring(url.indexOf('/')+1);
		}
		egl.printError(msg, url+":"+line);
	};

	window.onerror = egl.onerror;

	egl.getFileURL = function(file, functionName, line) {
		var start = "<br>&nbsp;&nbsp;&nbsp;&nbsp;";
		if (functionName === "undefined") return start + "&lt;init&gt;";
		if (!file || !line) return start + functionName+"() [native JavaScript]";
		file = file.replace(/http:\/\/[^\/]*/, '');
		start += functionName+"() at ";
		if (egl.enableEditing  || !egl.contextAware) 
			return start + " line "+line;
		else
			return start + "<b style='cursor:pointer; text-decoration:underline;' "+
					"onclick=\"egl.loadIDEURL('___openFile?file="+
					encodeURIComponent(file)+"&line="+line+"')\"><font color=blue>line "+line+"</font></b>";
	};

	egl.printError = function( /*String*/ description, /*Error*/ e ) {
		if ( !egl.canPrintError ) {
			return;
		}
		try {
			var error = e && e.message ? e.message : e;
			error = error || "unknown error";
			var msg = description;
			if (e && e.stack) {
				var s = e.stack.replace(/@/g,'<br>');
				s = s.replace(/http:\/\/[^\/]*\/[^:]*\//g, '');
						msg += ("<ul>"+s+"</ul>");
			}
			msg += "<br><b><font color=red>" + error + "</font></b><br>";
			if (egl.debugStack) {
				if (egl.debugStack.length > 0)
					msg += egl.getRuntimeMessage( "CRRUI2094E", []) + "<br>";
				else
					msg += egl.getRuntimeMessage( "CRRUI2095E", []);
				for (var n=egl.debugStack.length-1; n>=0; n--) {
					var stackFrame = egl.debugStack[n];
					if (stackFrame) {
						var path = stackFrame.fileName;
						if (!path) {
							path = stackFrame.longFunctionName;
							try {
								path = stackFrame.obj.eze$$package + "."  + stackFrame.longFunctionName;
							} catch (e) { }
						}
						if (stackFrame.lineNumber != 0)
							msg += egl.getFileURL(stackFrame.fileName, path, stackFrame.lineNumber);
					}
				}
				msg += "<br>";
			}
			if (e && e.fileName && e.lineNumber) {
				msg += egl.getFileURL(e.fileName, egl.lastFunctionEntered, e.lineNumber);
			}
			egl.lastActiveLine = null;
			egl.println(msg);
			egl.sessionIsValid = true; // to make sure the error line number links work
		}
		finally {
			egl.exceptionThrown = false;
			if (!egl.debugg) egl.debugStack = [];
		}
	};

	egl.instrumentFunctions = function(className, clazz) {
		if (!window.loadFirebugConsole) { 
			for (name in clazz) {
				var func = clazz[name];
				if (typeof(func) === "function") {
					if (name.indexOf("$") == -1) {
						func = egl.instrumentFunction(className, func, name);
						clazz[name] = func;
					}
				}
			}
		}
	};

	egl.instrumentFunction = function(className, func, functionName) {
		try {
			func.eze$$totalTime = 0;
			var string = ""+func;
			var enterBlock = "{ try { egl.enter('"+className+"."+functionName+"', this, arguments); {";
			var leaveBlock = " }} finally { egl.leave('"+functionName+"', arguments); }}";
			var returnBlock = " { if (!egl.debugg) egl.leave('"+functionName+"', arguments);  $1 }";
			string = string.replace(/([\/]{2}[^\n]*)/g, 	"");
			string = string.replace(/{/, 				enterBlock);
			string = string.replace(/(\sreturn[\s\(][^;]*[;])/g,	returnBlock);
			string = string.replace(/}\s*$/, 			leaveBlock);
			eval( "func="+string );
		}
		catch (e) {
			egl.printError(egl.getRuntimeMessage( "CRRUI2093E", [className+"."+functionName + ":<hr><font color=blue>" +string+"</font><hr>"]), e);
		}
		return func;
	};

	if (egl.egl.ui) {
		egl.instrumentFunctions("Widget", egl.eglx.ui.rui.Widget.prototype);
		egl.instrumentFunctions("MathLib", egl.eglx.lang.MathLib.prototype);
		egl.instrumentFunctions("XmlLib", egl.eglx.xml.XmlLib.prototype);
		egl.instrumentFunctions("HttpLib", egl.eglx.http.HttpLib.prototype);
		egl.instrumentFunctions("JsonLib", egl.eglx.json.JsonLib.prototype);
		egl.instrumentFunctions("ServiceLib", egl.eglx.services.ServiceLib.prototype);
		egl.instrumentFunctions("SysLib", egl.eglx.lang.SysLib.prototype);
		egl.instrumentFunctions("ValidatorWrapper", egl.org.eclipse.edt.rui.mvc.internal.ValidatorWrapper.prototype);
		egl.instrumentFunctions("StringLib", egl.eglx.lang.StringLib.prototype);
		egl.instrumentFunctions("DateTimeLib", egl.eglx.lang.DateTimeLib.prototype);
		egl.instrumentFunctions("SysVar", egl.egl.core.SysVar.prototype);
		egl.instrumentFunctions("RUILib", egl.eglx.ui.rui.RuiLib.prototype);
		egl.instrumentFunctions("Dictionary", egl.eglx.lang.EDictionary.prototype);
		egl.instrumentFunctions("RUIPropertiesLibrary", egl.egl.ui.rui.RUIPropertiesLibrary.prototype);
		egl.instrumentFunctions("Document", egl.eglx.ui.rui.Document.prototype);
		egl.instrumentFunctions("Job", egl.eglx.javascript.Job.prototype);
	//TODO sbg delete?	egl.instrumentFunctions("ServiceRefWrapper", egl.egl.jsrt.ServiceRefWrapper.prototype); 
		//TODO sbg delete?		egl.instrumentFunctions("RESTServiceRefWrapper", egl.egl.jsrt.RESTServiceRefWrapper.prototype);
		//TODO sbg delete?		egl.instrumentFunctions("SOAPServiceRefWrapper", egl.egl.jsrt.SOAPServiceRefWrapper.prototype);
		//TODO sbg delete?			egl.instrumentFunctions("ServiceRT", egl.egl.jsrt.ServiceRT.prototype);
		egl.instrumentFunctions("ServiceBinder", egl.eglx.services.ServiceBinder.prototype);
		egl.instrumentFunctions("JSONParser", egl.eglx.json.JSONParser.prototype);
		egl.instrumentFunctions("RUIHandler", egl.eglx.ui.rui.View.prototype);
//			egl.instrumentFunctions("HttpRequest", egl.egl.core.HttpRequest.prototype);
//			egl.instrumentFunctions("HttpResponse", egl.egl.core.HttpResponse.prototype);
	}

	egl.getTypename = function(object) {
		if ((object instanceof Array) || object.length) { return "array["+object.length+"]"; }
		if (object == document) { return "&lt;document&gt;"; }
		if (object == window) { return "&lt;window&gt;"; }
		if (object.jquery) { return "&lt;jquery object&gt;"; }
		return object.eze$$typename || object.type || object.tagName || typeof(object);
	};

	egl.getOuterWidthInPixels = function(element){
		return egl.getWidthInPixels(element);
	};

	egl.getOuterWidthInPixelsNonIESpecialWidget = function(element){
		if(egl.IE){
			var outerWidth = egl.getWidthInPixels(element)
						+ egl.getStyleValueForIE(element, "paddingLeft")
						+ egl.getStyleValueForIE(element, "paddingRight")
						+ egl.getStyleValueForIE(element, "borderLeftWidth")
						+ egl.getStyleValueForIE(element, "borderRightWidth");
			return outerWidth;
		}else if (egl.WebKit){
			if ( element.nodeName == "SPAN" && element.children && element.children.length == 1 ) {
				element = element.children[0];
			}
		}
		return egl.getOuterWidthInPixels(element);
	};

	egl.getOuterHeightInPixels = function(element){
		return egl.getHeightInPixels(element);
	};

	egl.getOuterHeightInPixelsNonIESpecialWidget = function(element){
		if(egl.IE){
			var outerHeight = egl.getHeightInPixels(element)
							+ egl.getStyleValueForIE(element, "paddingTop")
							+ egl.getStyleValueForIE(element, "paddingBottom")
							+ egl.getStyleValueForIE(element, "borderTopWidth")
							+ egl.getStyleValueForIE(element, "borderBottomWidth");
			return outerHeight;
		} else if (egl.WebKit){
			if ( element.nodeName == "SPAN" && element.children && element.children.length == 1 ) {
				element = element.children[0];
			}
		}
		return egl.getOuterHeightInPixels(element);
	};

	egl.getStyleValueForIE = function(element, key){
		if(!element.currentStyle || !element.currentStyle[key]){
			return 0;
		}
		
		var w = ("" + element.currentStyle[key]).toUpperCase();
		if( w.substr(w.length-2) == "PX" ){
			w = w.substr(0, w.length-2);
		}
		
		if (w != "" && !isNaN(w)){
			return parseInt(w);
		}else{
			return 0;
		}
	};

	/*
	 * This function returns hierarchy and widget information based on the EGL Dom
	 */
	egl.getWidgetInfo = function() {
		// the object that will hold the EGL Dom information
		var result = new Object();
		/*
		 * Object definition of an EGL element
		 */
		var eglElement = function() {
			this.type = "";
			this.moveable = "n";
			this.height = -1;
			this.width = -1;
			this.offset = -1;
			this.len = -1;
			this.x = -1;
			this.y = -1;
			this.varname = "";
			this.package = "";
			this.extrainfo = "";
		};
		eglElement.prototype = {
			/*
			 * Sets the type of EGL element
			 * i.e. VBox, HBox, etc.
			 */
			setType: function(ele) {
				this.type = egl.getTypename(ele);
			},
			/*
			 * Parses the id of the EGL element to determine
			 * the associated offset and length
			 */
			setIdOffsetLength: function(widget) {
				this.offset = parseInt(widget.eze$$offset);
				this.len = parseInt(widget.eze$$length);
				if (isNaN(this.offset) || isNaN(this.len)) {
					this.offset = -1;
					this.len = -1;
				}
			},
			setVariableName: function(widget) {
				if (widget.eze$$variableName != undefined) {
					this.varname = widget.eze$$variableName;
				} else {
					this.varname = "";
				}
			},
			setMoveable: function(widget) {
				if (widget.eze$$moveable != undefined) {
					if (widget.eze$$moveable == true) {
						this.moveable = "y";
					} else {
						this.moveable = "n";
					}
				} else {
					this.moveable = "n";
				}
			},
			setPackageName: function(ele) {
				this.package = ele.eze$$package;
				if (!typeof this.package === 'string') {
					this.package = "";
				}
			},		
			/*
			 * Sets the elements x and y pixel positioning relative to the client window
			 */
			setXY: function(ele) {
				this.x = parseInt(ele.getX());
				this.y = parseInt(ele.getY());
				if (isNaN(this.x) || isNaN(this.y)) {
					this.x = -1;
					this.y = -1;
				}				
			},
			isTable: function(ele){
				var tagName;
				
				if(ele.eze$$DOMElement && ele.eze$$DOMElement.tagName){
					tagName = ele.eze$$DOMElement.tagName.toUpperCase();
				}else if(ele.eze$$DOMElement && ele.eze$$DOMElement.children[0] && ele.eze$$DOMElement.children[0].tagName){
					tagName = ele.eze$$DOMElement.children[0].tagName.toUpperCase();
				}
				
				if(tagName && (tagName == "TABLE" || tagName == "DIV")){
					return true;
				}else{
					return false;
				}
			},
			isIESpecialWidget: function(ele){
				if(egl.IE){
					if(this.package == "dojo.widgets"){
						if(this.type == "DojoBorderContainer" || this.type == "DojoContentPane" || this.type == "DojoCalendar"){
							return true;
						}
					}
					
					if(this.package == "org.eclipse.edt.rui.widgets"){
						if(this.type == "Button" || this.type == "GridLayout" || this.type == "Box" || this.type == "CheckBox" || this.type == "Combo"){
							return true;
						}
					}
				}
				
				return false;
			},
			/*
			 * Set the elements width and height
			 */
			setWidthHeight: function(ele) {
				var clazz = ele.eze$$DOMElement.getAttribute( "class" );
				if ( clazz && clazz.indexOf( "dijitHidden" ) > 0 ) {
					this.width = -1;
					this.height = -1;
				} else {
					if(this.isIESpecialWidget(ele)){
						this.width = egl.getOuterWidthInPixels(ele.eze$$DOMElement);
						this.height = egl.getOuterHeightInPixels(ele.eze$$DOMElement);
					}else{
						this.width = egl.getOuterWidthInPixelsNonIESpecialWidget(ele.eze$$DOMElement);
						this.height = egl.getOuterHeightInPixelsNonIESpecialWidget(ele.eze$$DOMElement);
					}
					if (isNaN(this.width) || isNaN(this.height)) {
						this.width = -1;
						this.height = -1;
					}
				}
			},
			/*
			 * Set the element extra info
			 */
			setExtraInfo: function(ele) {
				this.extrainfo = "";
				if ( this.package == "org.eclipse.edt.rui.widgets" && this.type == "GridLayout" ) {
					if( ele.eze$$DOMElement.childNodes.length <=0 ){
						return;
					}
					var table =  ele.eze$$DOMElement.childNodes[0];
					var rows = table.rows;

					if ( rows.length > 0 ) {
						var tdlen = rows.length;
						this.extrainfo += "LayoutInfo=";
						for ( var i = 0; i < tdlen; i ++ ) {
							var tr = rows[i];
							var len = tr.cells.length;
							for ( tdi = 0; tdi < len; tdi ++ ) {
								var td = tr.cells[tdi];
								this.extrainfo += (td.getAttribute( "row") - 1) + ":" + (td.getAttribute( "column") - 1) + ":" + egl.getX( td ) + ":" + egl.getY( td ) + ":" 
					                           + egl.getOuterWidthInPixels( td ) + ":"+  egl.getOuterHeightInPixels( td ) + ":"
					                           + ( td.getAttribute( "occupied") == "true" ? "1" : "0" ) + ":";
						    }
						}
						this.extrainfo += ";";
					}
				}
				if ( this.package == "dojo.widgets" && this.type == "DojoTabContainer" ) {
					var tabContainer =  ele.eze$$DOMElement;
					if ( tabContainer["children"].length > 0 ) {
						var tabs = tabContainer.children[0];
						if ( tabs.children.length > 0 ) {
							var len = tabs.children.length;
							this.extrainfo += "ClickableAreas=selection:";
							for ( var i = 0; i < len; i ++ ) {
								var tab = tabs.children[i];
								this.extrainfo += egl.getX( tab ) + ":" + egl.getY( tab ) + ":" 
					                      + egl.getOuterWidthInPixels( tab ) + ":"+  egl.getOuterHeightInPixels( tab ) + ":" + (i+1) + ":";
							}
							this.extrainfo += ";";
						}
					}
				}
				if ( this.package == "dojo.widgets" && this.type == "DojoAccordionContainer" ) {
					var accContainer =  ele.eze$$DOMElement;
					if ( accContainer["children"].length > 0 ) {
						this.extrainfo += "ClickableAreas=selection:";
						for ( var i = 0; i < accContainer.children.length; i ++ ) {
							var accordion = accContainer.children[i];
							var accordionTitle = accordion.children[0];
							this.extrainfo += egl.getX( accordionTitle ) + ":" + egl.getY( accordionTitle ) + ":" 
				                           + egl.getOuterWidthInPixels( accordionTitle ) + ":"+  egl.getOuterHeightInPixels( accordionTitle ) + ":" + (i+1) + ":";
						}
						this.extrainfo += ";";
					}
				}
			}

		};
		// defines start element were recursion should start from
		var start = egl.Document.body.getChildren();
		/*
		 * Recursive function that is used to crawl down the EGL Dom
		 * Parameters:
		 * 		r (object): On the first call the final object is passed in.
		 * 					On each subsequent call the nested child object is passed in.
		 * 		ele (EGL element): The EGL element in the Dom
		 */
		function rec(r, ele) {
			ele = egl.unboxAny(ele);
			// given the element (ele) create a new eglElement to be added to the object
			var eglEle = new eglElement();
			eglEle.setType(ele);
			if (typeof(ele.getEGLDOMElement) != "undefined") {
				if( ele.eze$$isSynthetic == undefined) {
					eglEle.setIdOffsetLength(ele);
					eglEle.setVariableName(ele);
					eglEle.setMoveable(ele);
					eglEle.setPackageName(ele);
					eglEle.setXY(ele);
					eglEle.setWidthHeight(ele);
					eglEle.setExtraInfo(ele);
				}
			}
			r['ele'] = eglEle;
			// check to see if this children element exists
			var eleChildren = ele.getChildren();
			if (eleChildren != undefined) {
				// in some cases the children element exists, but is empty
				if (eleChildren.length > 0) {
					// prepare the object to be passed into rec
					r['children'] = new Object();
	                var j = 0;
	                // loop through the children
	                for (var i = 0; i < eleChildren.length; i++) {
	                    var e = eleChildren[i];
	                    // if the element is not editable, don't add it and its children.
	                    if ( isEditableWidget( e ) ) {  
	                        r['children'][j] = new Object();
	                        // perform recursion
	                        rec(r['children'][j],e);
	                        j ++;
	                    }
	                }
				}
			} else {
				// since everything is added to the object in place, base case is simple
				return true;
			}
		}

	    function isEditableWidget(ele) {
	        var offset = parseInt(ele.eze$$offset);
	        var len = parseInt(ele.eze$$length);
	        var type = egl.getTypename(ele);
	        var moveable = false;
			if (ele.eze$$moveable != undefined) {
				moveable = ele.eze$$moveable;
			}
	        if ( type == null || type == "" ) {
	            return false;
	        }
	        if ( ( isNaN(offset) || isNaN(len) || offset < 0 || len <= 0 ) && moveable == false ) {
	            return false;
	        }
	        return true;
	    }

		/*
		 * Some examples, like tab folder had more than 1 starting node??
		 */
	    if ( egl.rootHandler && egl.rootHandler.egl$isWidget && egl.rootHandler.egl$isWidget == true ) { // this is a widget
	    	result[0] = new Object();
	    	rec(result[0], egl.rootHandler.targetWidget);
	    } else if (start.length > 0) {
		    var i = 0;
			for (var j = 0; j < start.length; j++) {
				// begin the recursion passing in the result object and the starting EGL element
	            if ( isEditableWidget( start[j] ) ) { 
	                result[i] = new Object();
	                rec(result[i], start[j]);
	                i ++;
	            }
			}
		} else {
			result[0] = -1;
			result[1] = "empty";
		}
		// covert the result object into a JSON string
		var jsonStr = egl.toJSONString(result, 0, 100);
		return jsonStr;
	};	

	egl.evUpdateWidgetInfo = function() {
		egl.prevJSONPos = egl.getWidgetPositions();
	};
	//
	egl.partialDisconnectedElements = [];
	egl.ptCrash = false;
	egl.partialTerminateSession = function() {
		egl.ptCrash = false;
		egl.sessionIsValid = true;
		egl.prevJSONPos = "";
		egl.prevJSONCount = 0;	
		try {
			/*******************************************************************/
			// call all eze$$widgets that have destroy functions
			for (var i = egl.elements.length - 1; i >= 0; i--) {
				var el = egl.elements[i];
				if (el.eze$$widget != null && typeof el.eze$$widget.destroy == 'function') {
					el.eze$$widget.destroy();
				}
			}
			// now remove left over widgets
			for (var i = egl.elements.length - 1; i >= 0; i--) {
				var el = egl.elements[i];
				if (el != null) {
					egl.doDestroyDomElement(el);
					if (el.parentNode) {
						el.parentNode.removeChild(el);
					} 
					egl.partialDisconnectedElements.push(el);
					el = null;
				}
			}

			for (var i = egl.jobs.length - 1; i >= 0; i--)
				try { egl.jobs[i].cancel(); } catch (e) { }	
			
			egl.elements = [];	
			egl.jobs = [];	
			egl.console = null;
			/*********************************************************************/
			var package = egl.rootHandler.eze$$package;
			var splitPackage = package.split(".");
			var toDestroy = null;
			for (var i = 0; i < splitPackage.length; i++) {
				if (i == 0) {
					toDestroy = egl[splitPackage[i]];
				} else {
					toDestroy = toDestroy[splitPackage[i]];	
				}
			}
			if (toDestroy == null) {
				egl[egl.rootHandler.eze$$typename] = null;
			} else {
				toDestroy[egl.rootHandler.eze$$typename] = null;
			}
			/*********************************************************************/
			// destroy user generated libs
			var uPacks = egl.eze$$userLibs;
			for (var j = 0; j < uPacks.length; j++) {
				var uLib = uPacks[j];
				var uLibSplit = uLib.split(".");
				var uLibDestroy = null;
				for (var k = 0; k < uLibSplit.length; k++) {
					if (k == 0) {
						uLibDestroy = egl[uLibSplit[k]];
					} else {
						uLibDestroy = uLibDestroy[uLibSplit[k]];	
					}
				}
				if (uLibDestroy == null) {
					egl[uLib].$inst = null;
				} else {
					uLibDestroy.$inst = null;
				}			
			}
		} catch (e) {
			egl.ptCrash = true;
			egl.println("Reload: "+e.message + " " + i + " "+ typeof(egl.elements[i]));	
		}
	};

	egl.defaultTerminateSession = egl.terminateSession;

	egl.cleanupDebug = function(){
		if (egl.debugg) {
			try {
				egl.suspendReason = null;
				egl.breakpoints = null;
				egl.nextDebuggerPoll = null;
				egl.loadIDEURL("___windowClosed", null, true);
				egl.debugg = false;
				egl.contextKey = null;
			}
			catch (e) {}
		}
	};

	window.onbeforeunload = function() {
		try {egl.cleanupDebug();}catch(e){}
	};

	egl.terminateSession = function(){
		if (egl.partialDisconnectedElements) {
			for (var n=egl.partialDisconnectedElements.length-1; n>=0; n--) {
				egl.partialDisconnectedElements[n].style.visibility = "hidden";
				document.body.appendChild(egl.partialDisconnectedElements[n]);
			}
		}
		egl.defaultTerminateSession();
	};

	egl.toJSONString = function(object, depth, maxDepth) {
		try {
			if (depth == undefined) {
				depth = 0;
				maxDepth = 13;
			}
			if (maxDepth && depth > maxDepth) {
				return '" Max Recursion '+maxDepth+' reached"';
			}

			if (typeof(object) == "string") {
				return '"' + object + '"';
			}
			if (typeof(object) == "number") {
				return String(object);
			}
			if (typeof(object) == "boolean") {
				return Boolean(object);
			}
			if (!object) {
				return "null";
			}
			if (object.length || object.maxSize == 2147483647) {
				try {
					var s = [];
					s.push("[");
					var needComma = false;
					for (var n=0; n<object.length; n++) {
						if (typeof(object[n]) != 'function') {
							if (needComma) s.push(",");
							s.push(egl.toJSONString(object[n], depth+1, maxDepth));
							needComma = true;
						}
					}
					s.push("]");
					return s.join('');
				}
				catch (e) {
				}
			}

				var s = [];
				s.push("{");
				var needComma = false;
				for (f in object) {
					try {
						if (!f.match(/^eze\$\$/) && (typeof object[f] != "function")) {
							if (needComma) s.push(",");
							s.push('"');
							var key = f;
							s.push(key);
							s.push('":');
							s.push(egl.toJSONString(object[f], depth+1, maxDepth));
							needComma = true;
						}
					} catch (e) {
					}
				}
				s.push("}");
				return s.join('');
		}
		catch (e) {
			return '"Cannot toJSONString '+object+' due to '+e+'"';
		}
	};
	
	egl.eglx.lang.EDictionary.prototype.eze$$getChildVariables = function() {
		var childVars = [];
		for (f in this) {
			if (typeof this[f] == "function") continue;
			if (f.toString().indexOf("eze$$") == 0) continue;
			childVars.push({name : f.toString(), value : this[f], type : "eglx.lang.EAny"}); // dictionary fields are always "Any"
		}
		return childVars; 
	};
});
