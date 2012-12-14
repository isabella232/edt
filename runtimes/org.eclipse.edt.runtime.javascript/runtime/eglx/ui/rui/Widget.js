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
egl.defineClass(
	'eglx.ui.rui', 'Widget',
	{
	"constructor" : function() {
		this.document = egl.Document;
	},
	"setTagName": function(/*String*/ tagName)
	{
		if (!tagName) return;
		this.eze$$DOMElement = egl.createElement(tagName);
		this.eze$$getDOMElement().eze$$widget = this;
		var parts = tagName.match(/(\w)(\w*)/);
		this.setClass("EglRui" + parts[1].toUpperCase() + parts[2].toLowerCase());
	},
	"getTagName" : function()
	{
		if (this.eze$$DOMElement)
			return this.eze$$getDOMElement().tagName;
		else
			return "";
	},	
	"getAttribute" : function(attribute) {
		var value = this.eze$$DOMElement[attribute];
		if (typeof(value) == "undefined")
			value = this.eze$$DOMElement.getAttribute(attribute);
		return egl.boxAny(value);
	},
	"setAttribute" : function(attribute, value) {
		value = egl.unboxAny(value);
		if (typeof(value) == "object") {
			if ( value instanceof egl.javascript.BigDecimal )
			{
				value = Number(value.toString());
			}
			else
			{
				throw egl.createRuntimeException( "CRRUI2007E", ["Widget.SetAttribute("+attribute+","+value+")"]);
			}
		}
		this.eze$$DOMElement[attribute] = value;
		this.eze$$DOMElement.setAttribute(attribute, value);
	},
	"removeAttribute" : function(attribute){
		ele = this.eze$$getDOMElement();
		ele.removeAttribute(attribute);
	},
	"eze$$getDOMElement" : function() {
		if (!this.eze$$DOMElement) {
			var s=[];
			var needComma = false;
			for (f in this) {
				if (typeof(this[f]) != 'function') {
					s.push("&nbsp;&nbsp;&nbsp;&nbsp;");
					s.push(f);
					s.push(" = ");
					s.push(egl.toString(this[f]));
					s.push("<br>");
					needComma = true;
				}
			}
			throw egl.createRuntimeException( "CRRUI1080E", [s.join('')]);
		}
		return this.eze$$DOMElement;
	},
	"getElementById" : function(id) {
		var result = document.getElementById(id);
		if (result) {
			var widget = result.eze$$widget;
			if (!widget) {
				widget = egl.createWidget(result);
			}
			return widget;
		}
		return null;
	},
	"getElementsByName" : function(name) {
		var result = this.eze$$getDOMElement().getElementsByName(name);
		if (result) {
			var widgets = [];
			for (var n=0; n<result.length; n++) {
				var widget = result[n].eze$$widget;
				if (!widget) {
					widget = egl.createWidget(result[n]);
				}
				widgets.push(widget);
			}
			return widgets;
		}
		return egl.boxAny([]);
	},
	"getElementsByTagName" : function(tagName) {
		var result = this.eze$$getDOMElement().getElementsByTagName(tagName.toUpperCase());
		if (result) {
			var widgets = [];
			for (var n=0; n<result.length; n++) {
				var widget = result[n].eze$$widget;
				if (!widget) {
					widget = egl.createWidget(result[n]);
				}
				widgets.push(widget);
			}
			return widgets;
		}
		return egl.boxAny([]);
	},
	"getWidget" : function()
	{	
		return this;
	},
	"appendChild" : function (/*any*/child)
	{
		try {
			var thisNode = this.eze$$DOMElement;
			if (!child)
				throw egl.createRuntimeException( "CRRUI1050E", [thisNode.tagName]);
			child = egl.unboxAny(child);
			if (child == this)
				throw egl.createRuntimeException( "CRRUI1057E", [thisNode.tagName]);
			var childNode = child.eze$$DOMElement;
			if (!childNode)
				throw egl.createRuntimeException( "CRRUI1051E", [thisNode.tagName]);
			try {
				child.eze$$parent = this;
			}
			catch (e) {
				throw egl.createRuntimeException( "CRRUI1060E", [child.getTagName(), egl.inferSignature(this), this]);
			}
			thisNode.appendChild(childNode);
			if (egl.IE && egl.IEVersion < 9 && thisNode.lastChild != childNode)
				throw "";
			this.childrenChanged();
		}
		catch (e) {
			var route = "this="+thisNode.tagName;
			var parentNode = thisNode.parentNode;
			while (parentNode && parentNode.tagName) {
				if (parentNode == childNode) {
					route = "child="+parentNode.tagName + " >> " + route;
					parentNode = parentNode.parentNode;
					while (parentNode && parentNode.tagName) {
						route = parentNode.tagName + " >> " + route;
						parentNode = parentNode.parentNode;
					}
					document.body.appendChild(childNode);
					throw egl.createRuntimeException( "CRRUI1058E", [thisNode.tagName, childNode.tagName, route]);
				}
				route = parentNode.tagName + " >> " + route;
				parentNode = parentNode.parentNode;
			}
			throw egl.createRuntimeException( "CRRUI1055E", [this.getTagName(),
			      (child&&childNode)?childNode.tagName:egl.toString(child),e.message
			                                                 ]);
		}
	},
	"appendChildren" : function (/*any[]*/ childList)
	{
		childList = egl.unboxAny(childList);;
		for (var n = 0; n < childList.length; n++)
		{
			this.appendChild(childList[n]);
		}
	},
	"setChildren" : function(/*any[]*/ childList)
	{
		childList = egl.unboxAny(childList);
		this.removeChildren();
		for (var n = 0; n < childList.length; n++)
		{
			this.appendChild(childList[n]);
		}
		this.childrenChanged();
	},
	"removeChildren" : function ()
	{
		var element = this.eze$$DOMElement;		
		while (element.hasChildNodes()) {
	    	element.removeChild(element.firstChild);
		}
		this.childrenChanged();
	},
	"removeChild" : function (child)
	{
		try {
			var thisNode = this.eze$$DOMElement;
			if (!child)
				throw egl.createRuntimeException( "CRRUI1150E", [thisNode.tagName]);
			child = egl.unboxAny(child);
			if (child == this)
				throw egl.createRuntimeException( "CRRUI1157E", [thisNode.tagName]);
			if (!childNode && child.initialUI) {
				var ruiHandlerChildren = child.initialUI;  // child is a RUIHandler
				for (var n=0; n<ruiHandlerChildren.length; n++) {
					this.removeChild(egl.unboxAny(ruiHandlerChildren[n]));
				}	
				return;
			}
			var childNode = child.eze$$DOMElement;
			if (!childNode)
				throw egl.createRuntimeException( "CRRUI1151E", [thisNode.tagName]);
			thisNode.removeChild(childNode);
			this.childrenChanged();
		}
		catch (e) {
			throw egl.createRuntimeException( "CRRUI1155E", [this.getTagName(),
			                      (child&&childNode)?childNode.tagName:"null",e.message]);
		}
	},
	"childrenChanged" : function ()
	{
	},
	"getChildren" : function ()
	{
		var children = [];
		var childNodes = this.eze$$getDOMElement().childNodes;
		for (var n=0; n<childNodes.length; n++) {
			var widget = childNodes[n].eze$$widget;
			if (!widget) {
				widget = egl.createWidget(childNodes[n]);
			}
			children.push(widget);
		}
		return children;
	},
	"setInnerHTML" : function(text)
	{
		this.eze$$getDOMElement().innerHTML = text;
	},
	"getInnerHTML" : function()
	{
		return this.eze$$getDOMElement().innerHTML;
	},
	"setInnerText" : function(text)
	{
		if (egl.IE && egl.IEVersion < 9)
			this.eze$$getDOMElement().innerText = text;
		else
			this.eze$$getDOMElement().textContent = text;
	},
	"getInnerText" : function()
	{
		if (egl.IE && egl.IEVersion < 9)
			return this.eze$$getDOMElement().innerText;
		else
			return this.eze$$getDOMElement().textContent;
	},
	"eze$$becomeTarget" : function(/*Widget*/ replacement )
	{
		this.eze$$DOMElement = replacement.eze$$DOMElement;
		this.eze$$getDOMElement().eze$$widget = this;
	},
	"focus" : function(){
		var theWidget = this;
		setTimeout( function(){ 
			try { 
				theWidget.eze$$getDOMElement().focus(); 
			} catch(e) {
			} 
		}, 1);		
	},
	"select" : function(){
		var theWidget = this;
		var enableSelection = egl.enableSelection;
		setTimeout( function(){ 
			egl.setEnableTextSelection(true);
			theWidget.eze$$getDOMElement().select();
			egl.setEnableTextSelection(false);
		}, 1);
		
	},
	"setValue" : function(val){
		this.eze$$getDOMElement().value = val;
	},
	"getValue" : function(){
		return this.eze$$getDOMElement().value;
	},
	"eze$$initializeDOMElement" : function() {
	},
	
	"getOnSelect" : function() { return this.onSelect || this.installEventHandlers(this, 'select', this.onSelect = []); },
	"setOnSelect" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"getOnMouseOut" : function() { return this.onMouseOut || this.installEventHandlers(this, 'mouseout', this.onMouseOut = []); },
	"setOnMouseOut" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"getOnMouseUp" : function() { return this.onMouseUp || this.installEventHandlers(this, 'mouseup', this.onMouseUp = []); },
	"setOnMouseUp" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"getOnMouseOver" : function() { return this.onMouseOver || this.installEventHandlers(this, 'mouseover', this.onMouseOver = []); },
	"setOnMouseOver" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"getOnClick" : function() { return this.onClick || this.installEventHandlers(this, 'click', this.onClick = []); },
	"setOnClick" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"getOnMouseDown" : function() { return this.onMouseDown || this.installEventHandlers(this, 'mousedown', this.onMouseDown = []); },
	"setOnMouseDown" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"getOnScroll" : function() { return this.onScroll || this.installEventHandlers(this, 'scroll', this.onScroll = []); },
	"setOnScroll" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"getOnChange" : function() { return this.onChange || this.installEventHandlers(this, 'change', this.onChange = []); },
	"setOnChange" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"getOnFocusGained" : function() { return this.onFocusGained || this.installEventHandlers(this, 'focus', this.onFocusGained = []); },
	"setOnFocusGained" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"getOnFocusLost" : function() { return this.onFocusLost || this.installEventHandlers(this, 'blur', this.onFocusLost = []); },
	"setOnFocusLost" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"getOnKeyPress" : function() { return this.onKeyPress || this.installEventHandlers(this, 'keypress', this.onKeyPress = []); },
	"setOnKeyPress" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"getOnKeyUp" : function() { return this.onKeyUp || this.installEventHandlers(this, 'keyup', this.onKeyUp = []); },
	"setOnKeyUp" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"getOnKeyDown" : function() { return this.onKeyDown || this.installEventHandlers(this, 'keydown', this.onKeyDown = []); },
	"setOnKeyDown" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"getOnMouseMove" : function() { return this.onMouseMove || this.installEventHandlers(this, 'mousemove', this.onMouseMove = []); },
	"setOnMouseMove" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"getOnContextMenu" : function() { return this.onContextMenu || this.installEventHandlers(this, 'contextmenu', this.onContextMenu = []); },
	"setOnContextMenu" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	
	"getError" : function(){
		return this.error;
	},
	"setError" : function(/*boolean*/ isError){
		this.error = isError;
	},
	"getLayoutData" : function(){
		return this.layoutData;
	},
	"setLayoutData" : function(layoutData){
		this.layoutData = layoutData;
		if (this.eze$$container && this.eze$$container["layout"]){
			if(this.eze$$container["loaded"] || !egl.handlerStack || egl.handlerStack.length == 0){
				this.eze$$container["layout"]();
			}else if(egl.handlerStack.length > 0){
				var currentIndex = egl.handlerStack.length-1;
				var handler = egl.handlerStack[currentIndex];
				handler._layoutWidgets = handler._layoutWidgets || [];
				handler._layoutWidgets.appendElement(this.eze$$container);
			}
		}
	},
	
	"getUserData" :function(){
		return(this.data);
	},
	"setUserData":function(value){
		this.data = value;
	},
	"getID" : function(){
		return this.eze$$getDOMElement().id;
	},
	"setID" : function(/*String*/ id){
		this.eze$$getDOMElement().id = id;
	},
	"eze$$getWidth" : function(){
		return egl.getWidth(this.eze$$DOMElement);
	},
	"eze$$setWidth" : function(/*int*/ width){
		egl.setWidth(this.eze$$DOMElement, width);
	},
	"eze$$getHeight" : function(){
		return egl.getHeight(this.eze$$DOMElement);
	},
	"eze$$setHeight" : function(/*int*/ height){
		egl.setHeight(this.eze$$DOMElement, height);
	},
	"eze$$getTotalHeight" : function(){
		return egl.getTotalHeight(this.eze$$DOMElement);
	},
	"eze$$getTotalWidth" : function(){
		return egl.getTotalWidth(this.eze$$DOMElement);
	},
	"installEventHandlers" : function(thisObj,ename,handlers) {			
		egl.addEventListener(thisObj.eze$$DOMElement, ename,
			function(e) {
				egl.startNewWork();
				thisObj.runEventHandlers(handlers, e);
				return true;
			}, thisObj);
		return handlers;
	},
	"runEventHandlers" : function(handlers, e) {
		if (handlers.length > 0) {
			var clone = [].concat(handlers); // Clone the array first in case a handler modifies it
			try{
				if (egl.debug) {
					var args = [ e ];
					egl.enter("handle "+e.type+" on "+egl.toString(this), this, args);
				}
				for (var n=0; n<clone.length; ++n) {
					clone[n](e);
				}
			}
			finally{
				if (egl.debug) {
					egl.leave("", args);
				}
			}
		}
	},
	"morph" : function(duration, callback, morphFunction) {
		var start = (new Date() - 0);
		var eglWidget = this;
		var timer = setInterval(function() {
			try {
				var now = (new Date() - 0);
				var step = (now-start)/duration;
				if (step >= 0.85) step = 1.0;
				if (morphFunction)
					morphFunction(eglWidget, step);
				if (step >=1.0 && callback) 
					callback();
				if (step>=1.0) 
					clearTimeout(timer);
			}
			catch (e) {
				throw egl.createRuntimeException( "CRRUI1092E", []);
			}	
		}, 1);
	},
	"fadeIn" : function(duration, callback) {
		this.morph(duration, callback, function(widget, opacity) {
			widget.setOpacity(opacity);
		});	
	},
	"fadeOut" : function(duration, callback) {
		this.morph(duration, callback, function(widget, opacity) {
			widget.setOpacity(1.0 - opacity);
		});
	},
	"setOpacity" : function(opacity) {
		this.setOpacity$$internal(opacity);
	},
	"setOpacity$$internal" : function(opacity) {
		var style = this.getStyle();
		this.opacity = opacity;
		style.opacity = opacity;
		style.display = "inline-block";
		style.filter = (opacity >= 0.90) ? "''" : "alpha(opacity="+(100*opacity)+")";
		style.mozOpacity = opacity;
	},	
	"getOpacity" : function(opacity) {
		return this.opacity;
	},	
	"resize" : function(width, height, duration, callback) {
		this.startWidth = this.getPixelWidth();
		this.diffWidth = width - this.startWidth;
		this.startHeight = this.getPixelHeight();
		this.diffHeight = height - this.startHeight;
		this.morph(duration, callback, function(widget, step) {
			widget.setWidth(widget.startWidth + widget.diffWidth * step);
			widget.setHeight(widget.startHeight + widget.diffHeight * step);
		});
	},
	"getOffset" : function(){
		var id = this.eze$$getDOMElement().id;
		return parseInt(id.substring(1, id.indexOf('l')));
	},
	"getLength" : function(){
		var id = this.eze$$getDOMElement().id;
		return parseInt(id.substring(id.indexOf('l') + 1, id.length));
	},
	"getDisabled" : function() {
		return Boolean(this.eze$$getDOMElement().disabled);
	},
	"setDisabled" : function(/*boolean*/ disabled) {
		this.eze$$getDOMElement().disabled = disabled;
	},
	"getClass" : function() {
		return this.eze$$getDOMElement().className;
	},
	"setClass" : function(/*String*/ className) {
		this.eze$$getDOMElement().className = className;
	},
	"getStyle" : function() {
		return this.eze$$getDOMElement().style;
	},
	"setStyle" : function(/*String*/ style) {
		if( style != "" ){
			try {
				var json = "({" + style.replace(/;$/,"").replace(/;/g,",") + "})".replace(/,}/,"}");
				json = json.replace(/:/g, ":'");
				json = json.replace(/,/g, "',");
				json = json.replace(/}/g, "'}");
				json = json.replace(/-/g, "$");
				style = eval(json);
				for (f in style) {
					if (typeof(style[f]) != "function") {
						if (f == "width") 
							this.setWidth(style[f]);
						else if (f == "marginLeft") 
							this.eze$$fixedMarginLeft = style[f];
						else if (f == "height") 
							this.setHeight(style[f]);
						else {
							var fieldName = f.replace(/\$[a-z]/g, function(c) { return c.toUpperCase().substring(1); })
							var value = style[f].replace(/\$/g, "-").replace(/^\s*/,"").replace(/\s*$/,"");
							try {
								this.getStyle()[fieldName]=value;
							}
							catch (e) {								
								egl.printError(egl.getRuntimeMessage( "CRRUI2088E", [fieldName,value]), e);
							}
						}
					}
				}
			}
			catch (e) {
				egl.printError(egl.getRuntimeMessage( "CRRUI2089E", [json]), e);
			}
		}
	},
	"getLayout" : function() {
		return this.eze$$getDOMElement().layout;
	},
	"setLayout" : function(/*int*/ layout) {
		this.eze$$getDOMElement().layout=layout;
	},
 	"toString" : function() {
  		return this.eze$$package + "." + this.eze$$typename;
  	},
	"getTabIndex" : function(){
		return this.eze$$getDOMElement().tabIndex;
	},
	"setTabIndex" : function(/*int*/ idx){
		this.eze$$getDOMElement().tabIndex = idx;
	},
	"getAriaLive" : function(){
		return this.eze$$getDOMElement().getAttribute("aria-live");
	},
	"setAriaLive" : function(/*string*/ liveSetting){
		this.eze$$getDOMElement().setAttribute("aria-live",liveSetting);
	},
	"getAriaRole" : function(){
		return this.eze$$getDOMElement().getAttribute("role");
	},
	"setAriaRole" : function(/*string*/ aRole){
		this.eze$$getDOMElement().setAttribute("role",aRole);
	},
	"getX" : function(){
		return egl.getX(this.eze$$DOMElement);
	},
	"setX" : function(/*int*/ X){
		egl.setX(this.eze$$DOMElement, X);
	},
	"getY" : function(){
		return egl.getY(this.eze$$DOMElement);
	},
	"setY" : function(/*int*/ Y){
		egl.setY(this.eze$$DOMElement, Y);
	},
	"getWidth" : function(){
		return egl.getWidth(this.eze$$DOMElement);
	},
	"setWidth" : function(/*int*/ width){
		egl.setWidth(this.eze$$DOMElement, egl.toPX(width));
	},
	"getColor" : function(){
		return this.getStyle().color;
	},
	"setColor" : function(/*String*/ color){
		this.eze$$setStyleValue("color", color);		
	},
	"getZIndex" : function(){
		return this.getStyle().zIndex;
	},
	"setZIndex" : function(/*int*/ zIndex){
		this.eze$$setStyleValue("zIndex", zIndex);		
	},
	"getVisibility" : function(){
		return this.getStyle().visibility;
	},
	"setVisibility" : function(/*String*/ visibility){
		this.eze$$setStyleValue("visibility", visibility);		
	},
	"getVisible" : function(){
		if(this.getVisibility() == "hidden")
			return false;
		else return true;
	},
	"setVisible" : function(visible){
		if(visible)
			this.setVisibility("visible");
		else this.setVisibility("hidden");
	},
	"getPosition" : function(){
		return this.getStyle().position;
	},
	"setPosition" : function(/*int*/ position){
		this.eze$$setStyleValue("position", position);		
	},
	"getCursor" : function(){
		return this.getStyle().cursor;
	},
	"setCursor" : function(/*int*/ cursor){
		this.eze$$setStyleValue("cursor", cursor);		
	},
	"getFont" : function(){
		return this.getStyle().fontFamily;
	},
	"setFont" : function(/*int*/ fontFamily){
		this.eze$$setStyleValue("fontFamily", fontFamily);		
	},
	"getFontSize" : function(){
		return this.getStyle().fontSize;
	},
	"setFontSize" : function(/*string*/ fontSize){
		this.eze$$setStyleValue("fontSize", egl.toPX(fontSize));		
	},
	"getFontWeight" : function(){
		return this.getStyle().fontWeight;
	},
	"setFontWeight" : function(/*String*/ fontWeight){
		this.eze$$setStyleValue("fontWeight", fontWeight);		
	},
	"getBackground" : function(){
		return this.getStyle().background;
	},
	"setBackground" : function(/*int*/ background){
		this.eze$$setStyleValue("background", background);
	},
	"getBackgroundColor" : function(){
		return this.getStyle().backgroundColor;
	},
	"setBackgroundColor" : function(/*int*/ backgroundColor){
		this.eze$$setStyleValue("backgroundColor", backgroundColor);
	},
	"getHeight" : function(){
		return egl.getHeight(this.eze$$DOMElement);
	},
	"setHeight" : function(/*int*/ height){
		egl.setHeight(this.eze$$DOMElement, egl.toPX(height));
	},
	"setMargin" : function(/*int*/ margin){
		this.eze$$setStyleValue("margin", egl.toPX(margin));
	},
	"getMargin" : function(){
		return egl.parseInt(this.getStyle().margin);
	},
	"setMarginLeft" : function(/*int*/ marginLeft){
		this.eze$$setStyleValue("marginLeft", egl.toPX(marginLeft));
		this.eze$$fixedMarginLeft = marginLeft;
	},
	"getMarginLeft" : function(){
		return egl.parseInt(this.getStyle().marginLeft);
	},
	"setMarginRight" : function(/*int*/ marginRight){
		this.eze$$setStyleValue("marginRight", egl.toPX(marginRight));
	},
	"getMarginRight" : function(){
		return egl.parseInt(this.getStyle().marginRight);
	},
	"setMarginTop" : function(/*int*/ marginTop){
		this.eze$$setStyleValue("marginTop", egl.toPX(marginTop));
	},
	"getMarginTop" : function(){
		return egl.parseInt(this.getStyle().marginTop);
	},
	"setMarginBottom" : function(/*int*/ marginBottom){
		this.eze$$setStyleValue("marginBottom", egl.toPX(marginBottom));
	},
	"getMarginBottom" : function(){
		return egl.parseInt(this.getStyle().marginBottom);
	},
	"setBorderStyle" : function(/*String*/ borderStyle){
		this.eze$$setStyleValue("borderStyle", borderStyle);  
	},
	"getBorderStyle" : function(){
		return this.getStyle().borderStyle;
	},
	"setBorderColor" : function(/*String*/ borderColor){
		this.eze$$setStyleValue("borderColor", borderColor);  
	},
	"getBorderColor" : function(){
		return this.getStyle().borderColor;
	},
	"setBorderWidth" : function(/*int*/ borderWidth){
		this.eze$$setStyleValue("borderWidth", egl.toPX(Math.max(borderWidth,0)));
	},
	"getBorderWidth" : function(){
		var w = egl.parseInt(this.getStyle().borderWidth);
		return w || (this.eze$$getDOMElement().offsetWidth-this.eze$$getDOMElement().clientWidth)/2;
	},
	"setBorderLeftWidth" : function(/*int*/ borderLeftWidth){
		this.eze$$setStyleValue("borderLeftWidth", egl.toPX(Math.max(borderLeftWidth,0)));	   
	},
	"getBorderLeftWidth" : function(){
		var w = egl.parseInt(this.getStyle().borderLeftWidth);
		return w || this.getBorderWidth();
	},
	"setBorderRightWidth" : function(/*int*/ borderRightWidth){
		this.eze$$setStyleValue("borderRightWidth", egl.toPX(Math.max(borderRightWidth,0)));	   
	},
	"getBorderRightWidth" : function(){
		var w =  egl.parseInt(this.getStyle().borderRightWidth);
		return w || this.getBorderWidth();
	},
	"setBorderTopWidth" : function(/*int*/ borderTopWidth){
		this.eze$$setStyleValue("borderTopWidth", egl.toPX(Math.max(borderTopWidth,0)));   
	},
	"getBorderTopWidth" : function(){
		var w = egl.parseInt(this.getStyle().borderTopWidth);
		return w || this.getBorderWidth();
	},
	"setBorderBottomWidth" : function(/*int*/ borderBottomWidth){
		this.eze$$setStyleValue("borderBottomWidth", egl.toPX(Math.max(borderBottomWidth,0)));	   
	},
	"getBorderBottomWidth" : function(){
		var w = egl.parseInt(this.getStyle().borderBottomWidth);
		return w || this.getBorderWidth();
	},	
	"setBorderLeftStyle" : function(/*int*/ borderLeftStyle){
		this.eze$$setStyleValue("borderLeftStyle", borderLeftStyle);	   
	},
	"getBorderLeftStyle" : function(){
		return this.getStyle().borderLeftStyle;
	},
	"setBorderRightStyle" : function(/*int*/ borderRightStyle){
		this.eze$$setStyleValue("borderRightStyle", borderRightStyle);	   
	},
	"getBorderRightStyle" : function(){
		return this.getStyle().borderRightStyle;
	},
	"setBorderTopStyle" : function(/*int*/ borderTopStyle){
		this.eze$$setStyleValue("borderTopStyle", borderTopStyle);   
	},
	"getBorderTopStyle" : function(){
		return this.getStyle().borderTopStyle;
	},
	"setBorderBottomStyle" : function(/*int*/ borderBottomStyle){
		this.eze$$setStyleValue("borderBottomStyle", borderBottomStyle);	   
	},
	"getBorderBottomStyle" : function(){
		return this.getStyle().borderBottomStyle;
	},	
	"setPadding" : function(/*int*/ padding){
		this.eze$$setStyleValue("padding", egl.toPX(padding));	 
	},
	"getPadding" : function(){
		return egl.parseInt(this.getStyle().padding);
	},
	"setPaddingLeft" : function(/*int*/ paddingLeft){
		this.eze$$setStyleValue("paddingLeft", egl.toPX(paddingLeft));   
	},
	"getPaddingLeft" : function(){
		return egl.parseInt(this.getStyle().paddingLeft);
	},
	"setPaddingRight" : function(/*int*/ paddingRight){
		this.eze$$setStyleValue("paddingRight", egl.toPX(paddingRight));
	},
	"getPaddingRight" : function(){
		return egl.parseInt(this.getStyle().paddingRight);
	},
	"setPaddingTop" : function(/*int*/ paddingTop){
		this.eze$$setStyleValue("paddingTop", egl.toPX(paddingTop));
	},
	"getPaddingTop" : function(){
		return egl.parseInt(this.getStyle().paddingTop);
	},
	"setPaddingBottom" : function(/*int*/ paddingBottom){
		this.eze$$setStyleValue("paddingBottom", egl.toPX(paddingBottom));	   
	},
	"getPaddingBottom" : function(){
		return egl.parseInt(this.getStyle().paddingBottom);
	},
	"eze$$setStyleValue" : function(name, value) {
		try {
			this.getStyle()[name] = value;
		}
		catch (e) {
			egl.printError(egl.getRuntimeMessage( "CRRUI2007E", [name+"=\""+value+"\""]), e);
		}
	},   
	"getChildVariables" : function(){
		temp =  this.eze$$getChildVariables();
		returnString = "";
		for(var i=0; i < temp.length; i++ )
		{
			returnString += temp[i].name + " " + temp[i].value;
		}
		return returnString;
	},
	"getLogicalParent" : function(){
		return this.eze$$container;
	},
	"setLogicalParent" : function(container){
		this.eze$$container = egl.unboxAny(container);
	},
	"getParent" : function(){
		var parent = this.eze$$getDOMElement().parentNode;
		var widget = null;
		if (parent) {
			widget = parent.eze$$widget;
			if (!widget) {
				widget = egl.createWidget(parent);
			}
		}
		return widget;
	},
	"setParent" : function(parent){
		try {
			if (parent == egl.Document) parent = egl.Document.body; 
			egl.unboxAny(parent).appendChild(this);
		}
		catch (e) {
			throw egl.createRuntimeException( "CRRUI1060E", [this.getTagName(), egl.inferSignature(parent), parent]);
		}
	},
	"getTargetWidget" : function(){
		return this;
	},
	"setTargetWidget" : function(widget){
		this.eze$$DOMElement = widget.eze$$getDOMElement();
	},
	"setContextMenu" : function (menu){
		this.contextmenu = menu;
		this.eze$$getDOMElement().oncontextmenu = function(e){
			var event = egl.wrapEvent(e);
			event.preventDefault();
			menu.showOptions(event);
			menu.optionsBox.setX(event.x);
			menu.optionsBox.setY(event.y);
		};
	},
	"getContextMenu" : function(){
		return this.contextmenu || null;
	},
	"getEGLDOMElement" : function() {
		return this.eze$$DOMElement;
	}, 
	"setOnStartDrag" : function(callback) {
		this.startDrag = new egl.egl.jsrt.Delegate(this, callback, "Teglx/ui/rui/StartDragFunction;");
		this.eze$$dragging = false;
		this.getOnMouseDown().appendElement(new egl.egl.jsrt.Delegate(this, function(e) {
			e = egl.wrapEvent(e);
			this.eze$$DND_x = e.pageX;
			this.eze$$DND_y = e.pageY;
			egl.dragJob = setTimeout(new egl.egl.jsrt.Delegate(this, this.eze$$DND_enableDrag), 200);
			document.onmousemove = new egl.egl.jsrt.Delegate(this, this.eze$$DND_performDrag);
			document.onmouseup =   new egl.egl.jsrt.Delegate(this, this.eze$$DND_performEndDrag);
			document.ondragstart = new egl.egl.jsrt.Delegate(this, this.eze$$DND_ignoreNativeDrag);
			if ( egl.Firefox && e.widget.eze$$DOMElement.tagName == "IMG" ) {
				e.preventDefault();
			}
		}));
	}, 
	"eze$$DND_enableDrag" : function() {
		try {
			if (this.startDrag( this, this.eze$$DND_x, this.eze$$DND_y))
				this.eze$$dragging = true;
		} catch (e) { 
			egl.printError(egl.getRuntimeMessage( "CRRUI2098E", [this.eze$$getDOMElement().tagName+".onStartDrag"]), e); 
		}
	},
	"eze$$DND_performDrag" : function(e) {
		if (this.eze$$dragging == true && this.drag) {
			e = egl.wrapEvent(e);
			try {
				this.drag( this, egl.getWidgetAt(e.pageX, e.pageY, this.eze$$DOMElement), e.pageX, e.pageY);
			} catch (e) { 
				egl.printError(egl.getRuntimeMessage( "CRRUI2098E", [this.eze$$getDOMElement().tagName+".onDrag"]), e); 
			}
		}
	},
	"eze$$DND_performEndDrag" : function(e) {
		if (this.eze$$dragging == true && this.dropOnTarget) {
			e = egl.wrapEvent(e);
			try {
				this.dropOnTarget( this, egl.getWidgetAt(e.pageX, e.pageY, this.eze$$DOMElement), e.pageX, e.pageY);
			} catch (e) { 
				egl.printError(egl.getRuntimeMessage( "CRRUI2098E", [this.eze$$getDOMElement().tagName+".onDropOnTarget"]), e);
			}
		}
		clearTimeout(egl.dragJob);
		this.eze$$dragging = false;
	},
	"eze$$DND_ignoreNativeDrag" : function(e) {
		var currentEvent = egl.wrapEvent(e);
		currentEvent.preventDefault();
		currentEvent.stopPropagation();
	},
	"getOnStartDrag" : function() {
		if (!this.startDrag)
			throw egl.createRuntimeException( "CRRUI1010E", ["onStartDrag"]); 
		return this.startDrag;
	},
	"getOnDrag" : function() {
		if (!this.drag)
			throw egl.createRuntimeException( "CRRUI1010E", ["onDrag"]); 
		return this.drag;
	}, 
	"setOnDrag" : function(callback) {
		this.drag = new egl.egl.jsrt.Delegate(this, callback, "Teglx/ui/rui/DragFunction;");
	},
	"getOnDropOnTarget" : function() {
		if (!this.dropOnTarget)
			throw egl.createRuntimeException( "CRRUI1010E", ["onDropOnTarget"]); 
		return this.dropOnTarget;
	},
	"setOnDropOnTarget" : function(callback) {
		this.dropOnTarget = callback;
	},
	"getPixelWidth" : function() {
		return Number(egl.getWidthInPixels(this.eze$$DOMElement));
	},
	"getPixelHeight" : function() {
		return Number(egl.getHeightInPixels(this.eze$$DOMElement));
	},
	"setPixelWidth" : function(w) {
		this.setWidth(egl.toPX(w));
	},
	"setPixelHeight" : function(h) {
		this.setHeight(egl.toPX(h));
	},	
	"setDisplayStyle" : function(display ){
		this.getStyle().display  = display;	   
	},
	"getDisplayStyle" : function(){
		return (this.getStyle().display);
	},
  	"setWidgetOrientation" : function(widgetOrientation) {
		var tagName = this.eze$$getDOMElement().tagName;
		if(tagName == "SELECT" || tagName == "DIV" || tagName == "TABLE")
			this.eze$$getDOMElement().dir = widgetOrientation.toLowerCase();
		
		if(widgetOrientation == "RTL" && tagName == "DIV")
			this.eze$$getDOMElement().align = "left";
  	},
	"getWidgetOrientation" : function( ) {
		return this.eze$$getDOMElement().dir.toUpperCase();
  	},
	"setReverseTextDirection" : function(reverseTextDirection) {
		this.reverseTextDirection = (reverseTextDirection == "Yes");			
		this.setBiDiStyles(this.textLayout,this.reverseTextDirection);
  	},
	"getReverseTextDirection" : function( ) {
		return ((this.reverseTextDirection) ? "Yes" : "No");		
  	},
  	"setSymmetricSwap" : function(symmetricSwap) {
		this.symmetricSwap = symmetricSwap;
	},
	"getSymmetricSwap" : function() {
		return this.symmetricSwap;		
  	},
  	"setNumericSwap" : function(numericSwap) {
		this.numericSwap = numericSwap;
	},
	"getNumericSwap" : function() {
		return this.numericSwap;		
  	},  	
	"setTextLayout" : function(textLayout) {
		this.textLayout = (textLayout == "Visual");
		this.setBiDiStyles(this.textLayout,this.reverseTextDirection);
	},
	"getTextLayout" : function() {
		return ((this.textLayout) ? "Visual" : "Logical");		
  	},
  	"setBiDiStyles" : function(isTextTypeVisual, isTextOrientationRightToLeft) {
		if(this.eze$$getDOMElement().tagName == "SELECT") return;
		
		var elementType = this.eze$$getDOMElement().type;
		if(isTextTypeVisual) {
			this.eze$$getDOMElement().style.unicodeBidi = "bidi-override";
			this.eze$$getDOMElement().style.direction =  (isTextOrientationRightToLeft) ? "rtl" : "ltr";
			this.eze$$getDOMElement().dir = (isTextOrientationRightToLeft) ? "rtl" : "ltr";
				
			if(elementType == "text" || elementType == "textarea")
				this.eze$$getDOMElement().style.textAlign = (isTextOrientationRightToLeft) ? "right" : "left";
			
			if(this.eze$$getDOMElement().tagName == "INPUT" && (elementType == "text" || elementType == "button")){				
				this.setBiDiMarkers(isTextTypeVisual, isTextOrientationRightToLeft);
			}
		}
		else {
			this.eze$$getDOMElement().style.unicodeBidi = "";
			this.eze$$getDOMElement().style.direction = "";
			this.eze$$getDOMElement().dir = (isTextOrientationRightToLeft) ? "rtl" : "ltr";
			if(elementType == "text" || elementType == "textarea")
				this.eze$$getDOMElement().style.textAlign = "";

			if(this.eze$$getDOMElement().tagName == "INPUT" && (elementType == "text" || elementType == "button")){
				this.cleanBiDiMarkers();
			}
		}  		
  	},	
  	"setBiDiMarkers" : function(isTextTypeVisual, isTextOrientationRightToLeft) {
  		if (!isTextTypeVisual && !isTextOrientationRightToLeft)
  			return;
  		//"INPUT" "button", "OPTION", "INPUT" "text","DIV"
  		var text, endMarker = "", startMarker = "";
		switch (this.eze$$getDOMElement().tagName)
		{
			case "INPUT" :
				text = this.eze$$getDOMElement().value;
				break;
			case "DIV" :
				text = (egl.IE && egl.IEVersion < 9) ? this.eze$$getDOMElement().innerText : this.eze$$getDOMElement().textContent;
				endMarker = egl.PDF;				
				if(text.charAt(text.length-1) == endMarker)
					text = text.substring(0,text.length-1);
				break;
			case "SPAN":
				text = (egl.IE && egl.IEVersion < 9) ? this.eze$$getDOMElement().innerText : this.eze$$getDOMElement().textContent;
				endMarker = egl.PDF;				
				if(text.charAt(text.length-1) == endMarker)
					text = text.substring(0,text.length-1);
				break;
			default: //"OPTION"
				text = this.eze$$getDOMElement().innerHTML;			
		}
		if(text.charAt(0) >= egl.LRE && text.charAt(0) <= egl.RLO) 
			text = text.substring(1);

		if(isTextTypeVisual) 
			startMarker = (isTextOrientationRightToLeft) ? egl.RLO : egl.LRO;
		else if(this.eze$$getDOMElement().type != "text")
			startMarker = (isTextOrientationRightToLeft) ? egl.RLE : egl.LRE;

		text = startMarker + text + endMarker;
		
		switch (this.eze$$getDOMElement().tagName)
		{
			case "INPUT" :
				this.eze$$getDOMElement().value = text;
				break;
			case "DIV" :
				endMarker = egl.PDF;				
				if (egl.IE && egl.IEVersion < 9) {
					this.eze$$getDOMElement().innerText = text;
				}
				else
					this.eze$$getDOMElement().textContent = text;
					
				break;
			default: //"OPTION"
				this.eze$$getDOMElement().innerHTML = text;			
		}	
  	},
  	"cleanBiDiMarkers" : function() {
  		var isInner = this.eze$$getDOMElement().tagName == "OPTION";
		var text = (isInner) ? this.eze$$getDOMElement().innerHTML : this.eze$$getDOMElement().value;
  		
		if(text.charAt(0) >= egl.LRE && text.charAt(0) <= egl.RLO) {
			if(isInner)
				this.eze$$getDOMElement().innerHTML = text.substring(1);
			else
				this.eze$$getDOMElement().value = text.substring(1);	
		}		
  	},
  	"eze$$getBaseChildVariables": function() {
  		var eze$$parent = this;
		return [
		{name: "id", value : function(){try{return eze$$parent.getID();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "geID", setter : "setID"},
		{name: "logicalParent", value : function(){try{return eze$$parent.getLogicalParent();}catch(e){return null;}}(), type : "eglx.ui.rui.Widget", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getLogicalParent", setter : "setLogicalParent"},
		{name: "parent", value : function(){try{return eze$$parent.getParent();}catch(e){return null;}}(), type : "eglx.ui.rui.Widget", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getParent", setter : "setParent"},
		{name: "tabIndex", value : function(){try{return eze$$parent.getTabIndex();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getTabIndex", setter : "setTabIndex"},
		{name: "ariaLive", value : function(){try{return eze$$parent.getAriaLive();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getAriaLive", setter : "setAriaLive"},
		{name: "ariaRole", value : function(){try{return eze$$parent.getAriaRole();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getAriaRole", setter : "setAriaRole"},
		{name: "disabled", value : function(){try{return eze$$parent.getDisabled();}catch(e){return null;}}(), type : "boolean", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getDisabled", setter : "setDisabled"},
		{name: "error", value : function(){try{return eze$$parent.getError();}catch(e){return null;}}(), type : "boolean", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getError", setter : "setError"},
		{name: "cursor", value : function(){try{return eze$$parent.getCursor();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getCursor", setter : "setCursor"},
		{name: "style", value : function(){try{return eze$$parent.getStyle();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getStyle", setter : "setStyle"},
		{name: "class", value : function(){try{return eze$$parent.getClass();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getClass", setter : "setClass"},
		{name: "visibility", value : function(){try{return eze$$parent.getVisibility();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getVisibility", setter : "setVisibility"},
		{name: "opacity", value : function(){try{return eze$$parent.getOpacity();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOpacity", setter : "setOpacity"},
		{name: "zIndex", value : function(){try{return eze$$parent.getZIndex();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getZIndex", setter : "setZIndex"},
		{name: "x", value : function(){try{return eze$$parent.getX();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getX", setter : "setX"},
		{name: "y", value : function(){try{return eze$$parent.getY();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getY", setter : "setY"},
		{name: "width", value : function(){try{return eze$$parent.getWidth();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getWidth", setter : "setWidth"},
		{name: "height", value : function(){try{return eze$$parent.getHeight();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getHeight", setter : "setHeight"},
		{name: "pixelWidth", value : function(){try{return eze$$parent.getPixelWidth();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getPixelWidth", setter : "setPixelWidth"},
		{name: "pixelHeight", value : function(){try{return eze$$parent.getPixelHeight();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getPixelHeight", setter : "setPixelHeight"},
		{name: "margin", value : function(){try{return eze$$parent.getMargin();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getMargin", setter : "setMargin"},
		{name: "marginLeft", value : function(){try{return eze$$parent.getMarginLeft();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getMarginLeft", setter : "setMarginLeft"},
		{name: "marginRight", value : function(){try{return eze$$parent.getMarginRight();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getMarginRight", setter : "setMarginRight"},
		{name: "marginTop", value : function(){try{return eze$$parent.getMarginTop();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getMarginTop", setter : "setMarginTop"},
		{name: "marginBottom", value : function(){try{return eze$$parent.getMarginBottom();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getMarginBottom", setter : "setMarginBottom"},
		{name: "padding", value : function(){try{return eze$$parent.getPadding();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getPadding", setter : "setPadding"},
		{name: "paddingLeft", value : function(){try{return eze$$parent.getPaddingLeft();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getPaddingLeft", setter : "setPaddingLeft"},
		{name: "paddingRight", value : function(){try{return eze$$parent.getPaddingRight();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getPaddingRight", setter : "setPaddingRight"},
		{name: "paddingTop", value : function(){try{return eze$$parent.getPaddingTop();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getPaddingTop", setter : "setPaddingTop"},
		{name: "paddingBottom", value : function(){try{return eze$$parent.getPaddingBottom();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getPaddingBottom", setter : "setPaddingBottom"},
		{name: "borderColor", value : function(){try{return eze$$parent.getBorderColor();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getBorderColor", setter : "setBorderColor"},
		{name: "borderStyle", value : function(){try{return eze$$parent.getBorderStyle();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getBorderStyle", setter : "setBorderStyle"},
		{name: "borderLeftStyle", value : function(){try{return eze$$parent.getBorderLeftStyle();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getBorderLeftStyle", setter : "setBorderLeftStyle"},
		{name: "borderRightStyle", value : function(){try{return eze$$parent.getBorderRightStyle();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getBorderRightStyle", setter : "setBorderRightStyle"},
		{name: "borderTopStyle", value : function(){try{return eze$$parent.getBorderTopStyle();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getBorderTopStyle", setter : "setBorderTopStyle"},
		{name: "borderBottomStyle", value : function(){try{return eze$$parent.getBorderBottomStyle();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getBorderBottomStyle", setter : "setBorderBottomStyle"},
		{name: "borderWidth", value : function(){try{return eze$$parent.getBorderWidth();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getBorderWidth", setter : "setBorderWidth"},
		{name: "borderLeftWidth", value : function(){try{return eze$$parent.getBorderLeftWidth();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getBorderLeftWidth", setter : "setBorderLeftWidth"},
		{name: "borderRightWidth", value : function(){try{return eze$$parent.getBorderRightWidth();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getBorderRightWidth", setter : "setBorderRightWidth"},
		{name: "borderTopWidth", value : function(){try{return eze$$parent.getBorderTopWidth();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getBorderTopWidth", setter : "setBorderTopWidth"},
		{name: "borderBottomWidth", value : function(){try{return eze$$parent.getBorderBottomWidth();}catch(e){return null;}}(), type : "int", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getBorderBottomWidth", setter : "setBorderBottomWidth"},
		{name: "color", value : function(){try{return eze$$parent.getColor();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getColor", setter : "setColor"},
		{name: "background", value : function(){try{return eze$$parent.getBackground();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getBackground", setter : "setBackground"},
		{name: "backgroundColor", value : function(){try{return eze$$parent.getBackgroundColor();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getBackgroundColor", setter : "setBackgroundColor"},
		{name: "font", value : function(){try{return eze$$parent.getFont();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getFont", setter : "setFont"},
		{name: "fontSize", value : function(){try{return eze$$parent.getFontSize();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getFontSize", setter : "setFontSize"},
		{name: "fontWeight", value : function(){try{return eze$$parent.getFontWeight();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getFontWeight", setter : "setFontWeight"},
		{name: "position", value : function(){try{return eze$$parent.getPosition();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getPosition", setter : "setPosition"},
		{name: "layoutData", value : function(){try{return eze$$parent.getLayoutData();}catch(e){return null;}}(), type : "any", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getLayoutData", setter : "setLayoutData"},
		{name: "targetWidget", value : this.targetWidget, type : "any", attrs : egl.ATTR_IMPLICIT_VAR},
		// Intentionally left out!
		//{name: "innerHTML", value : function(){try{return eze$$parent.getInnerHTML();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR},
		{name: "innerText", value : function(){try{return eze$$parent.getInnerText();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getInnerText", setter : "setInnerText"},
		{name: "children", value : function(){try{return eze$$parent.getChildren();}catch(e){return null;}}(), type : "eglx.ui.rui.Widget[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getChildren", setter : "setChildren"},
		{name: "document", value : this.document, type : "eglx.ui.rui.Document", attrs : egl.ATTR_IMPLICIT_VAR},
		{name: "onChange", value : function(){try{return eze$$parent.getOnChange();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnChange", setter : "setOnChange"},
		{name: "onClick", value : function(){try{return eze$$parent.getOnClick();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnClick", setter : "setOnClick"},
		{name: "onSelect", value : function(){try{return eze$$parent.getOnSelect();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnSelect", setter : "setOnSelect"},
		{name: "onFocusGained", value : function(){try{return eze$$parent.getOnFocusGained();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnFocusGained", setter : "setOnFocusGained"},
		{name: "onFocusLost", value : function(){try{return eze$$parent.getOnFocusLost();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnFocusLost", setter : "setOnFocusLost"},
		{name: "onKeyDown", value : function(){try{return eze$$parent.getOnKeyDown();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnKeyDown", setter : "setOnKeyDown"},
		{name: "onKeyPress", value : function(){try{return eze$$parent.getOnKeyPress();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnKeyPress", setter : "setOnKeyPress"},
		{name: "onKeyUp", value : function(){try{return eze$$parent.getOnKeyUp();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnKeyUp", setter : "setOnKeyUp"},
		{name: "onMouseDown", value : function(){try{return eze$$parent.getOnMouseDown();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnMouseDown", setter : "setOnMouseDown"},
		{name: "onMouseMove", value : function(){try{return eze$$parent.getOnMouseMove();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnMouseMove", setter : "setOnMouseMove"},
		{name: "onMouseOver", value : function(){try{return eze$$parent.getOnMouseOver();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnMouseOver", setter : "setOnMouseOver"},
		{name: "onMouseOut", value : function(){try{return eze$$parent.getOnMouseOut();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnMouseOut", setter : "setOnMouseOut"},
		{name: "onMouseUp", value : function(){try{return eze$$parent.getOnMouseUp();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnMouseUp", setter : "setOnMouseUp"},
		{name: "onScroll", value : function(){try{return eze$$parent.getOnScroll();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnScroll", setter : "setOnScroll"},
		{name: "onContextMenu", value : function(){try{return eze$$parent.getOnContextMenu();}catch(e){return null;}}(), type : "eglx.ui.rui.EventHandler[]", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnContextMenu", setter : "setOnContextMenu"},
		{name: "onStartDrag", value : function(){try{return eze$$parent.getOnStartDrag();}catch(e){return null;}}(), type : "eglx.ui.rui.StartDragFunction", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnStartDrag", setter : "setOnStartDrag"},
		{name: "onDrag", value : function(){try{return eze$$parent.getOnDrag();}catch(e){return null;}}(), type : "eglx.ui.rui.DragFunction", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnDrag", setter : "setOnDrag"},
		{name: "onDropOnTarget", value : function(){try{return eze$$parent.getOnDropOnTarget();}catch(e){return null;}}(), type : "eglx.ui.rui.DropOnTargetFunction", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getOnDropOnTarget", setter : "setOnDropOnTarget"},
		{name: "widgetOrientation", value : function(){try{return eze$$parent.getWidgetOrientation();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getWidgetOrientation", setter : "setWidgetOrientation"},
		{name: "textLayout", value : function(){try{return eze$$parent.getTextLayout();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getTextLayout", setter : "setTextLayout"},
		{name: "reverseTextDirection", value : function(){try{return eze$$parent.getReverseTextDirection();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getReverseTextDirection", setter : "setReverseTextDirection"},
		{name: "symmetricSwap", value : function(){try{return eze$$parent.getSymmetricSwap();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getSymmetricSwap", setter : "setSymmetricSwap"},
		{name: "numericSwap", value : function(){try{return eze$$parent.getNumericSwap();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getNumericSwap", setter : "setNumericSwap"},
		{name: "displayStyle", value : function(){try{return eze$$parent.getDisplayStyle();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getDisplayStyle", setter : "setDisplayStyle"},
		{name: "value", value : function(){try{return eze$$parent.getValue();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getValue", setter : "setValue"},
		{name: "tagName", value : function(){try{return eze$$parent.getTagName();}catch(e){return null;}}(), type : "string", attrs : egl.ATTR_IMPLICIT_VAR, getter : "getTagName", setter : "setTagName"}
		];
	},
	"eze$$getChildVariables" : function () {
		return this.eze$$getBaseChildVariables();
	}
});

egl.eglx.ui.rui.Widget.fromWidget = function(x, y) {
	/* TODO sbg Making this "conversion" a no-op because it's very dubious;  however, it's defined in 
	 * eglx/ui/rui/ExternalTypes.egl so we need to have something....hopefully that will be revisited
	 * and ultimately removed (along with this function).
	 */
	return x;    
};

egl.eglx.ui.rui.Widget.equals = function(x, y) {
	return x == y;    
};

egl.eglx.ui.rui.Widget.notEquals = function(x, y) {
	return x != y;    
};
