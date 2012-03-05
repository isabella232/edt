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
egl.lastFunctionEntered = "???";
egl.namespaceMap = {};
egl.objectInNamespaceMap = {};
egl.elements = [];
egl.jobs = [];
egl.eze$$userLibs = [];
egl.eze$$rscBundles = {};
egl.eze$$runtimeProperties = {};
egl.cssFiles = {};
egl.eze$$externalJS = {};

window.onunload = function() {
	try { egl.terminateSession(); } catch (e) {}
};

egl.crashTerminateSession = function(){
	if (egl.IE) {		
		egl.commonTerminateSession();		
	}
};

egl.terminateSession = function(){
	if (egl.IE) {
		egl.commonTerminateSession();		
		for (var i = egl.elements.length - 1; i >= 0; i--) {
			var e = egl.elements[i];
			e.style.visibility = "hidden";
			document.body.appendChild(e);
		}
		for (f in egl){
			egl[f]=null;
		}
	}
};

egl.commonTerminateSession = function() {	
	egl.sessionIsValid = false;
	egl.destroyAllElements();	
};

egl.LRO = String.fromCharCode(8237);
egl.RLO = String.fromCharCode(8238);
egl.LRE = String.fromCharCode(8234);
egl.RLE = String.fromCharCode(8235);
egl.PDF = String.fromCharCode(8236);

egl.contextRoot = window.egl__contextRoot;
egl.enableSelection = false;
egl.console = null;
egl.canPrintToConsole = true;
egl.Document = null;
// context key to be assigned from the sever to the browser
egl.contextKey = "";
//
egl.workStartTime = new Date().getTime();

egl.core = function() {};
egl.core.ServiceKind = {
		EGL : 		1,
		WEB : 		2,
		NATIVE : 	3,		
		REST : 		4
};

egl.ui = function() {};
egl.ui.SignKind = {
		LEADING : 	1,
		NONE : 		2,
		PARENS : 	3,
		TRAILING : 	4
};

egl.ui.gateway = function() {};
egl.ui.gateway.EncodingKind = {
		JSON :	1
};


egl.IE = (window.navigator.userAgent.indexOf("MSIE ") >= 0);
egl.IEVersion = -1;
if(egl.IE){
	var ua = navigator.userAgent.toLowerCase();
	egl.IEVersion = ua.match(/msie ([\d.]+)/)[1];
}
egl.IPhone = !egl.IE && (window.navigator.userAgent.indexOf("iPhone") >= 0);
egl.Firefox = !egl.IE && (window.navigator.userAgent.indexOf("Firefox") >= 0);
egl.WebKit = !egl.IE && (window.navigator.userAgent.indexOf("WebKit") >= 0);
egl.XulRunner = !egl.Firefox && !egl.WebKit && (window.navigator.userAgent.indexOf("Gecko") >= 0);
egl.Opera = !egl.Firefox && (window.navigator.userAgent.indexOf("Opera") >= 0);
egl.Chrome = !egl.Opera && (window.navigator.userAgent.indexOf("Chrome") >= 0);

egl.SECS_PER_DAY = 86400;
egl.MS_PER_DAY = 86400000;
egl.US_PER_DAY = 86400000000;
egl.MS_PER_MINUTE = 60000;
//egl.SECS_BETWEEN_1900_1970 = 25568 * egl.SECS_PER_DAY;
egl.DEFAULT_DATE_VALUE_MS = -25568 * egl.MS_PER_DAY;
egl.DEFAULT_DATE_VALUE_US = -25568 * egl.US_PER_DAY;

egl.autoConvertMessages = true;

egl.tzOffsetSec = function(d) {
	d = d || new Date();
	return d.getTimezoneOffset()/*min*/ * 60;
};
egl.tzOffsetMS = function(d) {
	return egl.tzOffsetSec(d) * 1000;
};

if (!Array.prototype.forEach) {
	Array.prototype.forEach = function(f) {
		for (var n=0; n<this.length; n++) {
			f(this[n]);
		}
	};
}

egl.print = function(s) {
	if(egl.canPrintToConsole){
		if (egl.console === null) {
			egl.console = egl.createChild(document.body, "div");
			egl.console.innerHTML = "<hr>";
			egl.console.onmouseover = function (e) {
				egl.consoleCurrentSelectionMode = egl.enableSelection;
				egl.setEnableTextSelection(true);
			};
			egl.console.onmouseout = function (e) {
				egl.setEnableTextSelection(egl.consoleCurrentSelectionMode);
			};

		}
		var tt = egl.createChild(egl.console, "tt");
		tt.innerHTML = s;
	}
};

egl.println = function(s) {
	egl.print(s);
	egl.print("<br>");
	egl.checkWork();
};

if (egl.canPrintToConsole) {
	setInterval(function() {
		if (egl.console && egl.console.nextSibling) {
			document.body.removeChild(egl.console);
			document.body.appendChild(egl.console);
			egl.startVEWidgetTimer();
		}
	}, 1000);
}

egl.parseInt = function(s) {
	try {
		var result = parseInt(s.replace(/px/g,""));
		return isNaN(result) ? 0 : result;
	}
	catch (e) {
		return 0;
	}
};

egl.cachedWidget = null;

egl.getWidgetAt = function(x, y, ignoreElement) {
	if (egl.cachedWidget) {
		egl.cachedWidget = egl.getWidgetAtCrawl(x, y, egl.cachedWidget.eze$$DOMElement.parentNode, ignoreElement);
	}
	if (!egl.cachedWidget) {
		egl.cachedWidget = egl.getWidgetAtCrawl(x, y, document.body, ignoreElement);
	}
	return egl.cachedWidget;
};

egl.getWidgetAtCrawl = function(x, y, element, ignoreElement) {
	if (!element || element == ignoreElement || element.offsetTop == undefined) {
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
			var widget = egl.getWidgetAtCrawl(x, y, child, ignoreElement);
			if (widget) return widget;
		}
	}
	return (element.eze$$widget);
};

egl.createWidget = function(domElement) {
	if (domElement.eze$$widget)
		return domElement.elg$$widget;
	var result = new egl.eglx.ui.rui.Widget();
	result.eze$$DOMElement = domElement;
	result.eze$$isSynthetic = true;
	return result;		
};

egl.createChild = function(parent, tagName) {
	var child = egl.createElement(tagName);
	parent.appendChild(child);
	return child;
};

egl.createElement = function(tagName) {
	var element;
	if (egl.IE && egl.IEVersion < 9) {
		element = document.createElement('<'+tagName+'>');
	}
	else {
		if (tagName.indexOf(" ") != -1)
		{
			var tag = tagName.substring(0, tagName.indexOf(" "));
			element = document.createElement(tag);
			var subStr = tagName.substring(tagName.indexOf(" ") + 1, tagName.length);
			while (subStr.length >= 1)
			{
				var arg = subStr.substring(0,subStr.indexOf("="));
				subStr = subStr.substring(subStr.indexOf("=") + 1, subStr.length);
				var nextStop = subStr.indexOf(" ") == -1 ? subStr.length : subStr.indexOf(" ");
				var value = subStr.substring(0, nextStop);
				subStr = subStr.substring(nextStop + 1, subStr.length);
				
				arg.trim();
				value.trim();				
				
				element.setAttribute(arg, value);
			}
		}
		else
			element = document.createElement(tagName);
	}
	egl.elements.push(element);
	return element;
};

egl.getX = function(element){
	var x = 0;
	try {
		x = -egl.getBorderLeftWidth(element);
	    while (element && element.offsetParent) {
	        x += element.offsetLeft + egl.getBorderLeftWidth(element);
	        element = element.offsetParent;
	    }
	}
	catch (e) {
	}
	return x;
};

egl.getY = function(element){
	var y = 0;
	try {
		y = -egl.getBorderTopWidth(element);
	    while (element && element.offsetParent){
	        y += element.offsetTop + egl.getBorderTopWidth(element);
	        element = element.offsetParent;
	    }
 	}
	catch (e) {
	}
	return y;
};

egl.getBorderLeftWidth = function(element) {
	var style = element.currentStyle || element.style;
	var w = egl.parseInt(style.borderLeftWidth);
	if (w === 0 && style.borderStyle && style.borderStyle != "none") {w = 4;}
	if (w !== 0 && (!style.borderStyle || style.borderStyle == "none")) {w = 0;}
	return w;
};

egl.getClientX = function(element){
	var scrollX = 0;
    if( egl.Firefox )
        scrollX = document.documentElement.scrollLeft;
    else if( document.body && document.body.scrollLeft )
        scrollX = document.body.scrollLeft;
    else if( document.documentElement && document.documentElement.scrollLeft )
        scrollX = document.documentElement.scrollLeft;
    return egl.getX(element) - scrollX;
};

egl.getBorderTopWidth = function(element) {
	var style = element.currentStyle || element.style;
	var w = egl.parseInt(style.borderTopWidth);
	if (w === 0 && style.borderStyle && style.borderStyle != "none") {w = 4;}
	if (w !== 0 && (!style.borderStyle || style.borderStyle == "none")) {w = 0;}
	return w;
};

egl.getClientY = function(element){
	var scrollY = 0;
    if( egl.Firefox )
        scrollY = document.documentElement.scrollTop;
    else if( document.body && document.body.scrollTop )
        scrollY = document.body.scrollTop;
    else if( document.documentElement && document.documentElement.scrollTop )
        scrollY = document.documentElement.scrollTop;
    return egl.getY(element) - scrollY;
};

egl.toPX = function(num) {
	if (num == "" || isNaN(Number(num))) return num;
	return num + "px";
};

egl.setX = function(element, x) {
    if (element) {
    	try {
    		if (x.prototype != Number.prototype) x = new Number(x);
    		if (document.layers) element.left = egl.toPX(x); else element.style.left = egl.toPX(x);
    	}
    	catch (e) {
    		throw egl.createRuntimeException( "CRRUI1001E", [ e.message, element.tagName+".x", x ] );
    	}
	}
};

egl.setY = function(element, y) {
	if (element) {
		try {
	    	if (y.prototype != Number.prototype) y = new Number(y);
			if (document.layers) element.top = egl.toPX(y); else element.style.top = egl.toPX(y);
    	}
    	catch (e) {
    		throw egl.createRuntimeException( "CRRUI1001E", [ e.message, element.tagName+".y", y ] );
    	}
	}
};

egl.getWidth = function(element){
	if (!egl.getStyle(element)) return;
	return egl.getStyle(element).width;
};

egl.getHeight = function(element){
	if (!egl.getStyle(element)) return;
	return egl.getStyle(element).height;
};

egl.getWidthInPixels = function(element){
	if (!element) return;
	
	var w = ("" + egl.getWidth(element)).toUpperCase();
	if( w.substr(w.length-2) == "PX" )
	{
		w = w.substr(0, w.length-2);
	}
	
	if (w != "" && !isNaN(w))
		return parseInt(w);
	
	if (!element.offsetWidth || isNaN(element.offsetWidth))
	{
		return element.clientWidth;
	}
	else
	{
		return element.offsetWidth;
	}
};

egl.getHeightInPixels = function(element){
	if (!element) return;
	
	var h = ("" + egl.getHeight(element)).toUpperCase();
	if ( h.substr(h.length-2) == "PX" )
	{
		h = h.substr(0, h.length-2);
	}

	if (h != "" && !isNaN(h))
		return parseInt(h);
	
	if (!element.offsetHeight || isNaN(element.offsetHeight))
	{
		return element.clientHeight;
	}
	else
	{
		return element.offsetHeight;
	}
};

egl.setWidth = function(element, w) {
	try {
		if (element && element.style) {
	        if (element.tagName === "TD" || element.tagName === "IMG")
	            element.width = w;
	        else {
	        	if (typeof(w) == "number") { 
	        		w = Math.max(0, w);
	        	}
	        }
	        element.style.width = w;
		}
	}
	catch (e) {
		throw egl.createRuntimeException( "CRRUI1001E", [ e.message, element.tagName+".width", w ] );
	}

};

egl.setHeight = function(element, h) {
    try {
    	if (element) {
	        if (element.tagName === "TD" || element.tagName === "IMG")
	            element.height = h;
	        else {
	        	if (typeof(h) == "number") { 
	        		h = Math.max(0, h);
	        	}
	        }
	        element.style.height = h;
	    }
	}
	catch (e) {
		throw egl.createRuntimeException( "CRRUI1001E", [ e.message, element.tagName+".height", h ] );
	}

};

egl.setDateSignatures = function(object) {
    if (object instanceof egl.egl.jsrt.Record) {
        var sig = object.eze$$getFieldSignatures();
        for (var n = 0; n < sig.length; n++) {
           if (sig[n].value instanceof Date) {
               sig[n].value.eze$$signature = sig[n].type;
           }
        }
        return;    
    }
    if (object.eze$$InParamSignatures) { 
        // service IN parameters
        var sig = object.eze$$InParamSignatures;
        for (var n = 0; n < sig.length; n++) {
           if (object.params[n] instanceof Date) {
               object.params[n].eze$$signature = sig[n];
           }
        } 
        return;    
    }
    if (object instanceof Array) { 
    	var sig = egl.inferSignature(object);
	    var kind = sig.charAt(1) === '?' ? sig.charAt(2) : sig.charAt(1);
	    if (kind == 'K' || kind == 'L' || kind == 'J') {
	       for (var n = 0; n < object.length; n++) {
	           if (object[n]) {
			       object[n].eze$$signature = kind;
			   }
		   }
		}
		return;
    }
	if (object instanceof egl.eglx.lang.EDictionary) {
		for (f in object) {
			if (!f.match(/^eze\$\$/) && (typeof object[f] != "function") && object[f]!=null) {
			   var sig = object[f].eze$$signature;
			   var kind = sig.charAt(0) === '?' ? sig.charAt(1) : sig.charAt(0);
			   if (kind == 'K' || kind == 'L' || kind == 'J') {
			       object[f].eze$$value.eze$$signature = object[f].eze$$signature;
			   }
			}
		} 
		return;   
    }
};

egl.printError = function(){};

egl.eze$$packages = { };

egl.makePackage = function( /*String*/ pkgname ) {
	//handle case for default (empty) EGL package
	if (pkgname === "") {
		return egl;
	}	
	var elems = pkgname.toLowerCase().split( '.' );
	if (elems.length < 1) return;
	var pkg = egl;
	for ( var n=0; n < elems.length; n++ ) {
		var elem = elems[ n ];
		if ( !pkg[ elem ] ) {
			pkg[ elem ] = {};
		}
		pkg = pkg[ elem ];
	}	
	egl.eze$$packages[pkgname] = pkg;
	return pkg;
};

egl.setClassFunctions = function(/*Class*/ clazz, /*function[]*/ functions) {
	for (name in functions) {
		functions[name].definingClass = clazz.prototype;
		clazz.prototype[name] = functions[name];
	}
};

egl._defineClass = function(
		/*String*/ packageName,
		/*String*/ className
		// OPTIONAL String superPackageName,
		// OPTIONAL String superClassName,
		// object[] functions
		// trace flag
		) {
	try {
		if (arguments.length == 4) {
			var functions = arguments[2];
			var traceFlag = arguments[3];
		} else {
			var superPackageName = arguments[2];
			var superClassName = arguments[3];
			var functions = arguments[4];
			var traceFlag = arguments[5];
		}
		
		var pkg = egl.makePackage( packageName );
		if ( pkg[className] ) {	return; }
		
		var superclazz;
		
		var clazz = function( ) {
			if (superclazz) {
				superclazz.apply(this, arguments);
			}	
			if (functions.constructor) {
				try {
					functions.constructor.apply( this, arguments );
				}
				catch (e) {
					egl.printError(egl.getRuntimeMessage( "CRRUI2082E", [className]), e);
					throw e;
				}
			}
			this.toString = function() {
				try {
					var dataOutput = "type=" + this.eze$$package + "." + this.eze$$typename;
					if ( this.eze$$DOMElemnt )
					    dataOutput = dataOutput + ", tagName=" + this.eze$$DOMElement.tagName + ",childCount=" + this.eze$$DOMElement.childNodes.length ; 
                    if ( this instanceof egl.eglx.lang.AnyException )
                        dataOutput = dataOutput + ", message=" + this.message;	
                    return "[" + dataOutput + "]";				    		    
				}
				catch (e) {
					return "["+className+"]";
				}
			};
		};
		
		pkg[className] = clazz;
		if (superPackageName) {
			var spkg = egl.makePackage(superPackageName);
			if (!spkg || !spkg[superClassName] ) return;			
			superclazz = spkg[superClassName];
			clazz.prototype = new superclazz();
			clazz.prototype.eze$$superClass = superclazz;
		}

		clazz.prototype.eze$$thisClass = clazz;
		clazz.prototype.eze$$package = packageName;
		clazz.prototype.eze$$typename = className;	
		clazz.prototype.eze$$signature = 
				"T" + packageName.toLowerCase().replace(/\./g,"/") + 
				"/" + className.toLowerCase() + ";";
					
		egl.setClassFunctions(clazz, functions, 
			traceFlag //internal classes not defined by the user?
		);
	}
	catch (e) {
		egl.printError(egl.getRuntimeMessage( "CRRUI2083E", [className]), e);
	}
};

egl.defineClass = function(
	/*String*/ packageName,
	/*String*/ className
	// OPTIONAL String superPackageName,
	// OPTIONAL String superClassName,
	// object[] functions
	) {
	var a = new Array();
	for (var i=0;i<arguments.length;i++) a.push(arguments[i]);
	a.push(true); //internal - no tracing
	egl._defineClass.apply(this,a);
};

egl.defineRUILibrary = function(
	/*String*/ packageName,
	/*String*/ className
	// OPTIONAL String superPackageName,
	// OPTIONAL String superClassName,
	// object[] functions
	) {
	var a = new Array();
	for (var i=0;i<arguments.length;i++) a.push(arguments[i]);
	a.push(false); //user defined - enable tracing
	egl._defineClass.apply(this,a);
	var pkg = egl.makePackage( packageName );
	if(null != pkg && null != pkg[className]){
		pkg[className].eze$isLibrary = true;
	}
};

egl.defineRUIPropertiesLibrary = function(
	/*String*/ packageName,
	/*String*/ className,
	/*String*/ propertiesFile,
	/*function[]*/ functions
	) {
	try {
		var pkg = egl.makePackage( packageName );
		if ( pkg[className] ) {	return; }
		var superPkg = egl.makePackage( 'eglx.ui.rui' );
		var superClassName = 'RUIPropertiesLibrary';
		var clazz = function( derivedClazz ) {
			superPkg[superClassName].call( this, derivedClazz );
			functions.constructor.call( this );	
			this.eze$$initializeProperties(propertiesFile);
		};
		clazz.prototype = new superPkg[superClassName]( clazz );
		pkg[className] = clazz;
		clazz.prototype.eze$$superClass = superPkg[superClassName];	
		clazz.prototype.eze$$thisClass = clazz;	
		clazz.prototype.egl$isWidget = false;
		clazz.prototype.eze$$package = packageName;
		clazz.prototype.eze$$typename = className;
		clazz.prototype.eze$$signature = 
				"T" + packageName.toLowerCase().replace(/\./g,"/") + 
				"/" + className.toLowerCase() + ";";
		egl.setClassFunctions(clazz, functions, 
			true    // these are internal classes not defined by the user
		);
	}
	catch (e) {
		egl.printError(egl.getRuntimeMessage( "CRRUI2084E", [packageName, className]), e);
	}
};

egl.startVEWidgetTimer = function() {
	if (egl.enableEditing && egl.contextAware && egl.getWidgetPositions && !egl.getWidgetPositionsTimer) {
		egl.getWidgetPositionsTimer = setTimeout(function() { 
			egl.getWidgetPositions(); 
			egl.getWidgetPositionsTimer = null;
		}, 500);
	}
};

egl.defineWidget = function(
			/*String*/   packageName,
			/*String*/   className,
			/*String*/   superPackageName,
			/*String*/   superClassName,
			/*String*/   domElementTypeName,
			/*function[]*/ functions) {
	try {
		var pkg = egl.makePackage( packageName );
		if ( pkg[className] ) {	return; }
		var superPkg = egl.makePackage( superPackageName );
		var clazz = function( derivedClazz ) {
			superPkg[superClassName].call( this, derivedClazz );
			if( !derivedClazz ){
				this.createeze$$DOMElement();
			}	
			functions.constructor.call( this );	
			egl.startVEWidgetTimer();
		};	
		clazz.prototype = new superPkg[superClassName]( clazz );
		clazz.prototype.createeze$$DOMElement = function(){
			if(this.eze$$DOMElement){return;};
			this.eze$$DOMElement = egl.createElement( domElementTypeName );
			this.eze$$DOMElement.eze$$widget = this;
			this.eze$$initializeDOMElement();
		};
		pkg[className] = clazz;
		clazz.prototype.eze$$superClass = superPkg[superClassName];	
		clazz.prototype.eze$$thisClass = clazz;	
		clazz.prototype.egl$isWidget = true;
		clazz.prototype.eze$$package = packageName;
		clazz.prototype.eze$$typename = className;
		clazz.prototype.eze$$signature = 
				"T" + packageName.toLowerCase().replace(/\./g,"/") + 
				"/" + className.toLowerCase() + ";";
		egl.setClassFunctions(clazz, functions, 
			true    // these are internal classes not defined by the user
		);
		clazz.prototype.eze$$initializeDOMElement = function(){
			// call the super
			if(superPkg[superClassName].prototype.eze$$initializeDOMElement){
				superPkg[superClassName].prototype.eze$$initializeDOMElement.call( this );
			}
			
			if(functions.eze$$initializeDOMElement){
				functions.eze$$initializeDOMElement.call( this );
			}
		};
		if (egl.instrumentFunctions)
			egl.instrumentFunctions(className, clazz.prototype);
	}
	catch (e) {
		egl.printError(egl.getRuntimeMessage( "CRRUI2085E", [packageName, className, superPackageName, superClassName]), e);
	}
};

// Don't notify parent widgets
egl.stopEventPropagation = function(e){
	e = e || window.event;
   	e.cancelBubble = true;
	e.returnValue = false;
   	if (e.stopPropagation) e.stopPropagation();
};

egl.getButton = function(e){
	if (e.which)  return e.which;
	if (e.button) return e.button;
	return 0;
};

egl.defineRUIHandler = function(/*String*/   packageName, 
								/*String*/   className, 
								/*function[]*/ functions) {
	try {
		egl.enter("Internal error while defining RUIHandler "+packageName+"."+className);
		var pkg = egl.makePackage( packageName );
		if ( pkg[className] ) {	return; }
		var spkg = egl.makePackage('eglx.ui.rui');
		if (!spkg || !spkg.View ) return;
		var superclazz = spkg.View;
		var clazz = function( derivedClazz ) {
			superclazz.call( this, derivedClazz );
			try{
				egl.enter("<init>", this, arguments);
				egl.handlerStack = egl.handlerStack || [];
				egl.handlerStack.push(this);
				//Call routine to instantiate class variables
				functions.constructor.call( this );
				if(this._layoutWidgets){
					var widgets = this._layoutWidgets;
					for(var i=widgets.length-1;i>=0;i--){
						if(!widgets[i].loaded){
							widgets[i].layout();
							widgets[i].loaded=true;
						}						
					}
				}				
				this._layoutWidgets = null;
				egl.handlerStack.pop();
				document.body.style.marginLeft = 0;
				document.body.style.marginTop = 0;
			}
			finally{
				egl.leave();
			}
		};	
		pkg[className] = clazz;	
		clazz.prototype = new superclazz( clazz );
		clazz.prototype.eze$$superClass = superclazz;
		clazz.prototype.eze$$thisClass = clazz;
		clazz.prototype.eze$$package = packageName;
		clazz.prototype.eze$$typename = className;	
		clazz.prototype.eze$$signature = 
			"T" + packageName.toLowerCase().replace(/\./g,"/") + 
			"/" + className.toLowerCase() + ";";
		egl.setClassFunctions(clazz, functions,
			false   // these are user-defined functions
		);
	}
	catch (e) {
		egl.printError(egl.getRuntimeMessage( "CRRUI2086E", [className]), e);
	}
	finally {
		egl.leave();
	}
};

egl.defineRUIWidget = function (/*String*/   packageName, 
		/*String*/   className, 
		/*function[]*/ functions,
		/*String*/ tagName){
	try {
		egl.enter("Internal error while defining RUIWidget "+packageName+"."+className);
		var pkg = egl.makePackage( packageName );
		if ( pkg[className] ) {	return; }
		var spkg = egl.makePackage('eglx.ui.rui');
		if (!spkg || !spkg.Widget ) {
			throw egl.createRuntimeException( "CRRUI1030E", ['eglx.ui.rui', 'Widget'] );
		}
		var superclazz = spkg.Widget;
		
		var clazz = function( derivedClazz ) {
			try{
				egl.enter("<init>", this, arguments);
				superclazz.call( this );
				var cssClass = null;
				if (tagName) {
					this.setTagName(tagName);
					cssClass = this.getClass();
				}
				this.toString = function() {
					try {
						return "[type="+this.eze$$package+"."+this.eze$$typename+",tagName="+this.eze$$DOMElement.tagName+",childCount="+this.eze$$DOMElement.childNodes.length+"]";
					}
					catch (e) {
						return "["+className+"]";
					}
				};
				functions.constructor.call( this );
				if (this.targetWidget)
					this.eze$$becomeTarget(egl.unboxAny(this.targetWidget));
				if ( (tagName  && this.getClass() == cssClass )|| this.targetWidget )
					this.setClass("EglRui" + className);
				this.eze$$initializeDOMElement();
				egl.startVEWidgetTimer();
			}
			finally {
				egl.leave();
			}
		};
		pkg[className] = clazz;	
		
		clazz.prototype = new superclazz( clazz );
		clazz.prototype.eze$$thisClass = clazz;
		clazz.prototype.egl$isWidget = true;
		clazz.prototype.eze$$package = packageName;
		clazz.prototype.eze$$typename = className;	
		clazz.prototype.eze$$signature = 
				"T" + packageName.toLowerCase().replace(/\./g,"/") + 
				"/" + className.toLowerCase() + ";";
		egl.setClassFunctions(clazz, functions,
			false   // these are user-defined functions
		);
	}
	catch (e) {
		egl.printError(egl.getRuntimeMessage( "CRRUI2087E", [className]), e);
	}
	finally {
		egl.leave();
	}
};

egl.setEnableTextSelection = function(value) {
    egl.enableSelection = value;
    document.body.style.unselectable = value ? "off" : "on";
    document.body.style.MozUserSelect = value ? "text" : "none";
    document.body.style.webkitUserSelect = value ? "text" : "none";
};

document.onselectstart = function(e) { 
	return egl.enableSelection;
};

egl.onLoadReady = function(f) {
	var p = window.onload;
	if (typeof window.onload != 'function') {
		window.onload = f;
	} else {
		window.onload = function() {
			if (p) { p(); }
			f();
		};
	}
};

egl.stopPropagation = function() { 
	this.cancelBubble = true; 
};

egl.preventDefault = function() { 
	this.returnValue = false; 
};

egl.getEventX = function() { 
	var element = document.body;
	try { element = this.widget.eze$$DOMElement; } catch (e) { }; 
	return (this.clientX || 0) - egl.getClientX(element); 
};
egl.getEventY = function() { 
	var element = document.body;
	try { element = this.widget.eze$$DOMElement; } catch (e) { }; 
	return (this.clientY || 0) - egl.getClientY(element); 
};

egl.wrapEvent = function(e, target) {
	e = e || window.event;
	var element = e.srcElement || e.target;
	if (target) {
		e.widget = target;
		element = target.eze$$DOMElement;
	}
	else {
		while (element && !element.eze$$widget)
			element=element.parentNode;
		if (element)
			e.widget = element.eze$$widget;
	}
	e.stopPropagation =	e.stopPropagation || egl.stopPropagation;
	e.preventDefault = e.preventDefault || egl.preventDefault;
	e.getX = egl.getEventX;
	e.getY = egl.getEventY;
	e.ch = e.keyCode || e.charCode || 0;
	e.mouseButton = e.which ? e.which : 0;
	if (e.screenX == null) e.screenX = 0;
	if (e.screenY == null) e.screenY = 0;
	if (e.clientX == null) e.clientX = 0;
	if (e.clientY == null) e.clientY = 0;
	if (e.pageX == null ){
		e.pageX = e.clientX - egl.getClientX(document.body); 
	}
	if (e.pageY == null ){
		e.pageY = e.clientY - egl.getClientY(document.body); 
	}
	if (e.pageX == null) e.pageX = 0;
	if (e.pageY == null) e.pageY = 0;
	switch (e.button) {
		case 1:
			e.mouseButton = 1;
			break;
		case 4:
			e.mouseButton = 2;
			break;
		case 2:
		case 3:
			e.mouseButton = 3;
			break;
	}
	e.stopPropagation();
	return e;
};

egl.addEventListener = function(
		/*DOMelem*/ elem,
		/*String*/ evtype,
		/*function*/ listener,
		/*object*/ thisObj) {
	
	var handler = function(e) { 
		e = egl.wrapEvent(e, thisObj);
		try {
			if(!egl.debugg) egl.debugStack = [ {functionName: "Event "+evtype, fileName: "Browser", lineNumber: "0" }];
			listener.call(thisObj, e);
			if (egl.IE && egl.IEVersion < 9) {
				try { document.fireEvent("on"+e.type, e); } catch (e) { }
			}
			else {
				eval('try { document.on'+e.type+'(e); } catch (e) { }');
			}
		}
		catch (ee) {
			if (egl.egl.debug && ee instanceof egl.egl.debug.DebugTermination){
				if (ee.msg) egl.println(ee.msg);
			}
			else {
				egl.printError(egl.getRuntimeMessage( "CRRUI1083E", [e.type]), ee);
			}
		}
		finally {
			if (!egl.debugg) egl.leave();
		}
		return true; //shensis I change this since return false blocks the execution of any event handler in IE;
	};
	if ( elem.addEventListener ) {
		// Mozilla, Netscape, FireFox, etc
		elem.addEventListener( evtype, handler, false );
		return true;
	}
	elem[ 'on' + evtype ] = handler;
	if(!elem.eze$$eventNames)
		elem.eze$$eventNames = [];
	elem.eze$$eventNames.push('on'+evtype);	
	return true;
};

if (typeof (XMLHttpRequest) != "undefined") {
	egl.newXMLHttpRequest = function() {
			return new XMLHttpRequest();
		};
		}
else if (window.ActiveXObject) {
	try {
		new ActiveXObject( "Msxml2.XMLHTTP" ); 
		egl.newXMLHttpRequest = function() {
			return new ActiveXObject( "Msxml2.XMLHTTP" );
		};
	}
	catch( e ) {
		try {
			new ActiveXObject( "Microsoft.XMLHTTP" ); 
			egl.newXMLHttpRequest = function() {
				return new ActiveXObject( "Microsoft.XMLHTTP" );
	 		};
	 	}
		catch (e) {
		}
	}
};
if (!egl.newXMLHttpRequest) {
	egl.printError(egl.getRuntimeMessage( "CRRUI2088E", []), null);	
};

egl.makePackage('egl.rui');

egl.getStyle = function(element) {
	return element ? ( element.currentStyle || element.style ) : null;
};

egl.keepTrying = function(testFunction, actionFunction, delay) {
	function doit() {
		if (testFunction()) {
			actionFunction();
		}
		else {
			setTimeout(function() { 
				doit(); 
			}, delay);
		}
	}
	doit();
};

egl.toString = function(object) {
	try {
		if (typeof(object) == "string") {
			return '<font color=blue>"' + object + '"</font>';
		}
		if (typeof(object) == "number") {
			return String(object);
		}
		if (typeof(object) == "boolean") {
			return Boolean(object);
		}
		if (typeof(object) == "function") {
			return "function";
		}
		if (!object) {
			return "null";
		}
		if (object.tagName) {
			return object.tagName;
		}
		if (object.length) {
			try {
				var s = [];
				s.push("[");
				var needComma;
				for (var n=0; n<object.length; n++) {
					if (needComma) s.push(",");
					s.push(egl.toString(object[n]));
					needComma = true;
				}
				s.push("]");
				return s.join('');
			}
			catch (e) {
			}
		}
		if (object.eze$$package) {
			if (object.eze$$package == "egl.jsrt" && object.eze$$typename == "String") {
				return '<font color=blue>"' + object + '"</font>';
			}
			if (object.eze$$DOMElement) {
				return object.eze$$package +"." + object.eze$$typename +
					"[domElement=" + object.eze$$DOMElement.tagName + 
						",class=" + object.eze$$DOMElement.className +
						",#children=" + object.eze$$DOMElement.childNodes.length + "]";
			}
			else if (object.eze$$package == "eglx.ui.rui" && object.eze$$typename == "Widget") {
				return object.eze$$package + "." + object.eze$$typename + '-<b><font color=red>NO DOM ELEMENT!!!</font></b>';
			}
			else {
				return object.eze$$package + "." + object.eze$$typename;
			}
		}
		if (object.eze$$value)
			return "ANY["+egl.inferSignature(object)+"]";
			
		return egl.inferSignature(object);
	}
	catch (e) {
		return "<font color=red>Cannot call toString on "+object+" due to "+e+"</font>";
	}
};

egl.enter = function() { };
egl.leave = function() { };
egl.lastFile;
egl.lastLine;
egl.atLine = function() { };
egl.addLocalFunctionVariable = function() {};
egl.setLocalFunctionVariable = function() {};
egl.enterBlock = function() {};
egl.exitBlock = function() {};
egl.showEditingFeedback = function() { };
egl.loadScript = function() {};

egl.startNewWork = function() {
	//if (egl.debugg) return;
	//egl.workStartTime = new Date().getTime();
};
egl.checkWork = function() {
	//if (egl.debugg) return;
	//var now = new Date().getTime();
	//if ((now - egl.workStartTime > 20000)) {
	//	if (!confirm("This EGL program has been running for a long time. Press OK to continue. Press Cancel to stop it."))
	//		throw egl.createRuntimeException( "CRRUI2016E", [] );
	//	egl.workStartTime = now; 
	//}
};

egl.startup = function(){
	egl.startNewWork();
};

egl.getCurrencySymbol = function() {
	if (!this.currencySymbol) {
		this.currencySymbol = egl.eglx.lang.SysLib.getProperty("egl.nls.currency", null);
		if (!this.currencySymbol) {
			this.currencySymbol = egl.localeInfo.currencySymbol;
		}
	}
	return this.currencySymbol;
};
egl.getDecimalSymbol = function() {
	if (!this.decimalSymbol) {
		this.decimalSymbol = egl.eglx.lang.SysLib.getProperty("egl.nls.number.decimal", null);
		if (!this.decimalSymbol) {
			this.decimalSymbol = egl.localeInfo.decimalSeparator;
		}
	}
	return this.decimalSymbol;
};
egl.getSeparatorSymbol = function() {
	if (!this.separatorSymbol) {
		this.separatorSymbol = egl.eglx.lang.SysLib.getProperty("egl.nls.number.separator", null);
		if (!this.separatorSymbol) {
			this.separatorSymbol = egl.localeInfo.groupingSeparator;
		}
	}
	return this.separatorSymbol;
};
egl.doDestroyDomElement = function(element) {
	if(element.eze$$widget){
		element.eze$$widget=null;
	}		
	if(element.eze$$eventNames){
		for(var i=0;i<element.eze$$eventNames.length;i++)
			element[element.eze$$eventNames[i]] = null;
		element.eze$$eventNames = null;
	}	
};
egl.destroyDomElement = function(element) {
	if(element.children){
		for (var i=0; i< element.children.length; i++){
			var child = element.children[i];
			egl.destroyDomElement(child);			
		}	
	}	
	egl.doDestroyDomElement(element);
	for(var i=0;i<egl.elements.length;i++){
		if(egl.elements[i] == element){
			egl.elements.splice(i,1);
			break;
		}			
	}	
};
egl._destroyDomElement = function(element) {
	if(element.children){
		for (var i=0; i< element.children.length; i++){
			var child = element.children[i];
			egl._destroyDomElement(child);			
		}	
	}	
	egl.doDestroyDomElement(element);
};
egl.destroyAllElements = function() {
	for (var i = egl.elements.length - 1; i >= 0; i--) {         
		egl._destroyDomElement(egl.elements[i]);
		if (egl.elements[i].parentNode) {
			egl.elements[i].parentNode.removeChild(egl.elements[i]);
		} 
		egl.elements[i]=null;
	}
	egl.elements = [];
};
egl.destroyWidget = function(widget) {
	var element = widget.eze$$DOMElement;
	if(typeof(widget.destroy)=="function"){
		widget.destroy(); //Dojo widgets has own destroy function
	}else{				
		egl.destroyDomElement(element);		
	}
	if(element.parentNode){
		element.parentNode.removeChild(element);
	}
};
egl.destroyWidgetChildren = function(widget) {
	var children = widget.getChildren();
	for (var i=0; i< children.length; i++){
		egl.destroyWidget(children[i]);	
	}	
	widget.removeChildren();
};
egl.initDynamicLoadingHandler = function(pkg, handler){
	var fullHandler = pkg + "." + handler;
	var pkg = egl.makePackage(pkg);
	pkg[handler] = function(){
		var errorMessage ="<br><b><font color=red>" + egl.getRuntimeMessage("CRRUI1158E",[fullHandler]) + "</font></b><br>";
		egl.println(errorMessage);
	};
	pkg[handler]['needDynamicLoading'] = true;
};
//API
egl.init = function( initFunc, errorFunc){
	if(egl.Document.body == null)
		egl.Document.body = egl.createWidget(document.body);
	egl.setEnableTextSelection(false);
	try {		
		initFunc();
	} catch (e) {
		egl.crashTerminateSession();
		if(e.name == "eze$$HandlerLoadErr")
			egl.println(e.message);
		else
			egl.printError('Could not render UI', e); throw e;
	}	
};
egl.reportHandlerLoadError = function(handler){
	var err = new Error ();
    err.name = "eze$$HandlerLoadErr";
    err.message = "Internal generation error. Found no definition for " + handler + ". Try <b>Project > Clean...</b>";
    throw err;
};

egl.eval = function( codes ) {
	if ( egl.IE && egl.IEVersion < 9) {
		window.execScript( codes );
	} else {
	    var geval = function() {
	    	window.eval.call( window, codes );
	    };
	    geval();
	}
};

egl.loadCSS = function(cssFile) {
	if ( egl.ptCrash )
		return;
	if ( egl.cssFiles && egl.cssFiles.length == 0 ) {
		var links = document.body.getElementsByTagName( "link" );
		for ( var i = 0; i < links.length; i ++ ) {
			var href = links[i].getAttribute( "href" );
			if ( href ) {
				egl.cssFiles[href] = href;
			}
		}
	}
	if ( egl.cssFiles[cssFile] ) {	return; }
	egl.cssFiles[cssFile] = cssFile;
	
	var objCSS=document.createElement("link");
	objCSS.setAttribute("rel", "stylesheet");
	objCSS.setAttribute("type", "text/css");
	objCSS.setAttribute("href", cssFile);
	if(typeof objCSS!="undefined")
		document.getElementsByTagName("head")[0].appendChild(objCSS);
};

egl.loadFile = function(include, part){	
	var includeType = include.indexOf(".") >=0 ? include.substring(include.lastIndexOf(".") + 1, include.length) : "";
	if(includeType == "css"){
		egl.loadCSS(include);
		return;
	}
	var xhr = egl.newXMLHttpRequest();
	var htmlString = "";
	var count;
	var divChildren = [];
	xhr.open( 'GET', include, false );
	xhr.send( null );
	if(includeType == "js")
		egl.eval(xhr.responseText);
	else if(includeType == "html"){
		htmlString += xhr.responseText;
		var div = document.createElement("div");
		//IE cannot parse script and css correctly
		div.innerHTML = "div<div>" + htmlString + "</div>";	
		count = div.children[0].children.length;		
		for(var i=0;i<count;i++){
			divChildren.push(div.children[0].children[i]);
		}
		egl.eze$$externalJS[part] = [];
		parseHTML(part);	
	}	
	
	function parseHTML(part){
		for(var i= 0 ; i<count; i++){
			var node = divChildren[i];
			var nodeName = ( node.nodeName || "" ).toLowerCase();
			if(nodeName == "link" || nodeName == "style"){
				document.getElementsByTagName("head")[0].appendChild(node);
			}else if(nodeName !== "script"){
				document.body.appendChild(node);
			}else if(node.src == ""){
				egl.eval(node.text);
			}else{			
				egl.eze$$externalJS[part].push(node.src);
			}
		}
	}	
	
};