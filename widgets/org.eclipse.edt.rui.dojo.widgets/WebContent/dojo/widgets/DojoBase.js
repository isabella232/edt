/*******************************************************************************
 * Copyright � 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
setTimeout(function() {
	var loader = document.getElementById("egl_loader");
	if (loader) document.body.removeChild(loader);
}, 2000);

egl.startTime = new Date().getTime();
egl.LRO = String.fromCharCode(8237);
egl.RLO = String.fromCharCode(8238);
egl.LRE = String.fromCharCode(8234);
egl.RLE = String.fromCharCode(8235);
egl.PDF = String.fromCharCode(8236); 

egl.startup = function(){
    //
	// DEBUG: enable the next line to see all the modules being loaded by Dojo
	//
	// dojo.connect(dojo, "eval", function(s) { s = s.replace(/&/g, "&amp;"); s = s.replace(/</g, "&lt;"); kkks(s);	});
}

egl._dojoEventNames = {
	"click" : ["click", "Click", "Click"],
	"focus" : ["focus", "FocusGained", "Focus"],
	"blur" : ["blur", "FocusLost", "Blur"],
	"keydown" : ["keydown", "KeyDown", "KeyDown"],
	"keypress" : ["keypress", "KeyPress", "KeyPress"],
	"keyup" : ["keyup", "KeyUp", "KeyUp"],
	"mousedown" : ["mousedown", "MouseDown", "MouseDown"],
	"mouseup" : ["mouseup", "MouseUp", "MouseUp"],
	"mouseover" : ["mouseenter", "MouseOver", "MouseEnter"],
	"mousemove" : ["mousemove", "MouseMove", "MouseMove"],
	"mouseout" : ["mouseleave", "MouseOut", "MouseLeave"]
};
egl._dojoSerial = 0;
egl.defineClass(
	'dojo.widgets', 'DojoBase',
	'eglx.ui.rui', 'Widget',
{
	"render" : function() {	
		this.renderingStep = 1;
		this.eze$$ready = false;
		this.renderingStep = 2;
		var id = this.getID();
		this.destroyAtRender();
		var marker = null;
		var style = this.eze$$DOMElement.style;
		this.renderingStep = 4;		
		this.createDojoWidget(this.eze$$DOMElement);
		this.copyAttribute();		
		if (this.getWidgetOrientation() == "rtl"){
			this.eze$$DOMElement.dir = "rtl";
		} else if (this.getWidgetOrientation() == "ltr"){
			this.eze$$DOMElement.dir = "ltr";
		}
		this.eze$$DOMElement.eze$$widget = this;
		this.setID(id);
		this.renderingStep = 5;
		this.copyStyle(style);
		this.eze$$ready = true;
		this.printStartupMessage();
		egl.startVEWidgetTimer();
		var eglWidget = this;
		dojo.addOnLoad(eglWidget.dojoWidget, function() {
			eglWidget.handleEvent(eglWidget.getOnWidgetLoad(),"onWidgetLoad",null);
		});
	},
	"getDojoWidget" : function() {
		return this.dojoWidget;
	},
	"getOnWidgetLoad" : function() {
		if (this.onWidgetLoad) {
			return this.onWidgetLoad;
		} else {
			return (this.onWidgetLoad = []);
		}
	},
	"reportError" : function(error) {
		var s = "<div style='padding:14px; width:350px; background: yellow; border: 2px solid red;'>"+
		"<font color=red><b>Error found in "+this.eze$$typename + "</b></font><br><br>" +
		"Parent hierarchy = " + this.getAncestry() +
		"<br><br>Could not create Dojo Widget. See "+this.eze$$typename + 
		".createDojoWidget(), rendering step="+this.renderingStep + 
			(error ? ", error=" + error : "") + "</div>";
		this.setInnerHTML(s);	
	},
	"destroyAtRender" : function() {
		this.destroy();
	},
	"printStartupMessage" : function() {
		if (egl.dojo.widgets.DojoDiagnostics && !egl.enableEditing && !egl.debugg && egl.contextAware && !egl.dojoNow) {
		    egl.dojoNow = new Date().getTime();
		    var duration = (egl.dojoNow-egl.startTime);
		    egl.println("<div style='border: 1px solid #555555; background-color: #E5F3FF; width:700px; padding: 9px;'><b>Dojo Statistics: </b>" +
		    		"Total startup (including loading of Dojo) took: <b>"+duration+
		    		"</b>ms.<hr>Dojo provider is: \"" + egl.dojoProvider + 
		    		"\".<br>Read <b>README_FIRST.html</b> in the <b>dojo.runtime</b> " +
		    		"project for more information.<br>" +
		    		"<hr>You are using: "+navigator.userAgent + (document.documentMode ? ", documentMode="+document.documentMode : "") +
		    		"<hr>This message is only printed in Preview mode, and not when you deploy or debug your application. See DojoDiagnostics.egl." +
		    		"</div>"
		    );
		    var http = "http:/";
			if (navigator.userAgent.indexOf("MSIE 6") != -1) 
			   egl.println("<font color=red><b>You are using IE6. For performance and security reasons, upgrade your browser from Internet Explorer 6 to a newer version.");
		}
	},
	"renderWhenDojoIsDoneLoading" : function() {
		var eglWidget = this;
		eglWidget.renderingStep = 0;
		if (egl.IE)
			setTimeout(function() {	
				eglWidget.renderWhenDojoIsDoneLoadingSafely();
			},1);
		else
			eglWidget.renderWhenDojoIsDoneLoadingSafely();
	},
	"renderWhenDojoIsDoneLoadingSafely" : function() {
		var eglWidget = this;
		dojo.addOnLoad(function(){
			try {
				eglWidget.render();
			}
			catch (e) {
				dojo.addOnLoad(function(){	
					try {
						eglWidget.render();
					}
					catch (e) {
						eglWidget.reportError(e.message);
					}
				});
			}
		});
		this.removeLoader();
	},
	"getAncestry" : function() {
		var s = "";
		var element = this.eze$$DOMElement;
		while (element && element != document.body.parentNode) {
			if (element == document.body) 
				s += "document.body";
			else
			if (element.tagName) 
				s += element.tagName + " &gt; ";
			else
				s += "<font color=red><b>NO PARENT</b></font>";
			element = element.parentNode;
		}
		return s;
	},
	"removeLoader" : function() {
		var loader = document.getElementById("egl_loader");
		if (loader)
			document.body.removeChild(loader);
	},
	"copyStyle" : function(style) {
		for (f in style)
			if (style[f] != "")
				try { this.eze$$DOMElement.style[f] = style[f]; } catch (e) { }
		if (this.width) egl.setWidth(this.dojoWidget.domNode, egl.toPX(this.width));
		if (this.height) egl.setHeight(this.dojoWidget.domNode, egl.toPX(this.height));
	},
	"copyAttribute" : function() {
		var newDom = this.dojoWidget.domNode || this.dojoWidget.node;
		if(newDom == this.eze$$DOMElement){
			return;
		}
		try{
			for(var prop in this.eze$$DOMElement){			
				if(newDom[prop]===undefined)					
					newDom[prop] = this.eze$$DOMElement[prop];				
			}
		}catch(any){		
		}	
		this.eze$$DOMElement = newDom;		
		if ( egl.contextAware ) {
			egl.elements.push( newDom );
		}
	},	
	"includeCSS" : function(url) {
		var cssNode = document.createElement('link');
		cssNode.type = 'text/css';
		cssNode.rel = 'stylesheet';
		cssNode.href = url;
		document.getElementsByTagName("head")[0].appendChild(cssNode); 
	},	
	/* @Override widget
	 * Attach EGL event to Dojo event.
	 * When user call .onEvent, this function will be called
	 */
	"installEventHandlers" : function(thisObj,ename,handlers) {	
		var names = egl._dojoEventNames[ename];
		if(names){
			this._setEvent(names[0], names[1], names[2] );
		}				
		return handlers || [];
	},
	"handleEvent" : function(handlers, name, event) {
		var e;
		if(event){
			e = egl.wrapEvent(event || window.event || null, this);
		}else{
			e = new egl.eglx.ui.rui.Event();
			e.widget = this;
		}
		for (var n=0; n<handlers.length; n++) {
			try {
				handlers[n](e);
			}
			catch(e) {
				if (egl.egl.debug && e instanceof egl.egl.debug.DebugTermination){
		    		throw e;
		    	}
				egl.printError("Uncaught exception occurred during handling of "+this.eze$$type+"."+name+" event", e);
			}
		}
	},
	"destroy" : function(){
		dojo.removeAttr(this.eze$$DOMElement,"id");
		try { this.dojoWidget.destroy(); } catch(e) { }
		try { this.dojoWidget.destroyRecursive(); } catch (e) { }
		this.dojoWidget = null;
	},
	"layout" : function(){
		try { this.dojoWidget.layout(); } catch(e) { }
	},
	"assert" : function(test, message) {
		if (!test)
			throw message + ". Race condition may have occurred.";
	},	
	"setData" : function ( data ){
		this.data = data;
		var eglWidget = this;
		setTimeout(function() {
			eglWidget.renderWhenDojoIsDoneLoading();
		}, 1);
	},
	"getData" : function(){
		return this.data;
	},
	"setText" : function(text){
		this.text = text;
	},
	"getText" : function(){
		var isVisual = this.getTextLayout() == "Visual";
		var isReverseDirection = this.getReverseTextDirection() == "Yes";
		if ((isVisual || isReverseDirection) && (this.text.charAt(0)>=egl.LRE && this.text.charAt(0) <= egl.RLO))
			return this.text.substring(1);
		return this.text;
	},
	"setWidth" : function(width){ 
		this.width=width; 
		egl.setWidth(this.eze$$DOMElement, egl.toPX(width)); 
	},
	"setHeight" : function(height){ 
		this.height=height; 
		egl.setHeight(this.eze$$DOMElement, egl.toPX(height)); 
	},
	"setColor" : function(color){ 
		this.color=color; 
		egl.eglx.ui.rui.Widget.prototype.setColor.call(this, color); 
	},
	"setFont" : function(font){ 
		this.font=font; 
		egl.eglx.ui.rui.Widget.prototype.setFont.call(this, font); 
	},
	"focus" : function(){
		if(this.dojoWidget){
			this.dojoWidget.focus();
		}else{
			egl.eglx.ui.rui.Widget.prototype.focus.call(this);
		}
	},
	"getTextLayout" : function() {
		if (this.dojoWidget && this.dojoWidget.textLayoutThis)
			return (this.dojoWidget.textLayoutThis);
		else
		return (this.textLayoutThis);
	},
	
	"setTextLayout" : function(textLayout){
		this.textLayoutThis = textLayout;
		this.setBiDiMarkers();
	},
	"setReverseTextDirection" : function (reverseTextDirection){
		this.reverseTextDirectionThis = reverseTextDirection;			
		this.setBiDiMarkers();
	},
	"getReverseTextDirection" : function (){
		if (this.dojoWidget && this.dojoWidget.reverseTextDirectionThis)
			return (this.dojoWidget.reverseTextDirectionThis);
		else
		return (this.reverseTextDirectionThis);
	},
	"setWidgetOrientation" : function(widgetOrientation) {
		var tagName = this.eze$$getDOMElement().tagName;
		if(tagName == "SELECT" || tagName == "DIV"){
			this.widgetOrientationThis = widgetOrientation.toLowerCase();
		}
		
  	},
	"getWidgetOrientation" : function( ) {
		return this.widgetOrientationThis;
  	}, 
  	"applyBiDiMarkersToWidgetText" : function( ) {
		if (this.text && (this.textLayoutThis || this.reverseTextDirectionThis)){			
			var isVisual = this.textLayoutThis == "Visual";
			var isReverseDirection = this.reverseTextDirectionThis == "Yes";
			this.text = this.setBiDiMarkersStr(this.text,isVisual,isReverseDirection);
		}  		
  	},
	"setBiDiMarkersStr" : function(text, isTextTypeVisual, isTextOrientationRightToLeft) {
		if (!isTextTypeVisual && !isTextOrientationRightToLeft)
			return (text);
		var startMarker = "";
		if(text.charAt(0) >= egl.LRE && text.charAt(0) <= egl.RLO) 
			text = text.substring(1);
		if(isTextTypeVisual) 
			startMarker = (isTextOrientationRightToLeft) ? egl.RLO : egl.LRO;
		else if(this.eze$$getDOMElement().type != "text")
			startMarker = (isTextOrientationRightToLeft) ? egl.RLE : egl.LRE;
		text = startMarker + text;
		return (text);
  	},
  	"eze$$getChildVariables" : function() {
  		var childVars = this.eze$$getBaseChildVariables();
  		if (egl && egl.debugg) {
  			var allNames = [];
  	  		for (var i = 0; i < childVars.length; i++) {
  	  			allNames.push(childVars[i].name);
  	  		}
  	  		
  	  		var contains = function contains(array, value) {
  	  		  var i = array.length;
  	  		  while (i--) {
  	  		    if (array[i] === value) {
  	  		      return true;
  	  		    }
  	  		  }
  	  		  return false;
  	  		}
  	  		
			for (f in this) {
				if ( typeof this[f] == "function" ) continue;
				if ( f.toString().indexOf("eze$$") == 0 ) continue;
				if ( f.toString() == "egl$isWidget" ) continue;
				if (!contains(allNames, f.toString())) {
					childVars.push({name : f.toString(), value : this[f], type : egl.getDebugType(this[f])});
				}
			}
  		}
		return childVars;
  	},
  	"_getProperty" : function( eglPropName, dojoPropName){  		
  		if(this.dojoWidget){
  			return this.dojoWidget.get(dojoPropName);
  		}
  		return this[eglPropName];
  	},
  	"_setProperty" : function( eglPropName, dojoPropName, value){
  		this[eglPropName] = value;
  		if(this.dojoWidget){
  			this.dojoWidget.set(dojoPropName, value);
  		}
  	},
  	"_setEvent" : function( htmlEventName, eglEventName, dojoEventName){
		var eglWidget = this;
		this._args = this._args || {};
		var obj = this._args;
		var func;
		if(htmlEventName == "mouseup"){
			func = function(e) {
				eglWidget.handleEvent(eglWidget["getOn" + eglEventName](), "on" + eglEventName, e);
				if(e.button==2 || e.button==3){
					eglWidget.handleEvent(eglWidget.getOnContextMenu(), "onContextMenu", e);
				}
			};
		}else{
			func = function(e) {
				eglWidget.handleEvent(eglWidget["getOn" + eglEventName](), "on" + eglEventName, e);
	        };
		}			
		if(this.dojoWidget){
			dojo.connect(this.dojoWidget.domNode, "on" + htmlEventName, func);
		}else{
			obj["on" + dojoEventName] = func;
		}
	},
  	"_mergeArgs" : function(obj){
  		this._args = this._args || {};
  		for(prop in obj){
  			this._args[prop] = obj[prop];
  		}
  	}
});
