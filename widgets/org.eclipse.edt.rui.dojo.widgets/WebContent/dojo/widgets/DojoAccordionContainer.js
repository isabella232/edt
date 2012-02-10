/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineWidget(
	'dojo.widgets', 'DojoAccordionContainer',
	'dojo.widgets', 'DojoContainer',
	'div',
	{
	"constructor" : function() {
		this.setChildType("dojo.widgets.DojoContentPane");
		this.selection = 1;
		this.duration = 250;
		this.width = 800;
		this.height = 450;
		dojo.require("dijit.layout.StackContainer");
		dojo.require("dijit.layout.AccordionContainer");
		dojo.require("dijit.layout.ContentPane");
		this.selection = -1;
	},
	"createDojoWidget" : function(parent) {
		this.dojoID = "egl.DojoAccordianContainer" + (++egl._dojoSerial);
		this._mergeArgs({
			duration: this.duration, 
			doLayout: true, 
			id: this.id || this.dojoID,
			style: "width:"+egl.toPX(this.width)+";height:"+egl.toPX(this.height)
		});
		this.dojoWidget = new dijit.layout.AccordionContainer(this._args, parent);
		this.dojoWidget.startup();
		this.addEventHandlers();
		if (this.selection != -1)
			this.setSelection(this.selection);
	},
	"copyStyle" : function(style){
		// When copying style, IE will erase the important style
		if(egl.IE && egl.IEVersion<9){
			cssText = this.dojoWidget.domNode.style.cssText;
			var styles = cssText.split(";");
			for(var i=styles.length-1;i>=0;i--){
				var styleHead = styles[i].replace(/^\s+|\s+$/g, '').substring(0,6).toLowerCase();
				if(styleHead=="border"){
					styles[i] = styles[i] + " !important";
				}
			}
			cssText = styles.join(";");
			this.dojoWidget.domNode.style.cssText = cssText;
		}
	},
	"resize" : function(height, width) {
		if (this.dojoWidget){
			if (height) {
				this.setHeight(height);
			}
			if (width) {
				this.setWidth(width);
			}
			this.dojoWidget.resize();
		}
	},
	"addChild" : function(child, index) {
		egl.dojo.widgets.DojoContainer.prototype.addChild.call(this, child);
	},
	"addEventHandlers" : function() {
		var eglWidget = this;
		dojo.subscribe(this.getID()+"-selectChild", function(child) {
			if (eglWidget.eze$$ready) {
				eglWidget.selection = eglWidget.getChildIndex(child) + 1;
				eglWidget.notifyListeners(child.eglWidget, eglWidget.getOnTabSelected(), "onTabSelected");
			}
		});
	},
	"notifyListeners" : function(widget, handlers, eventName) {
		for (var n=0; n<handlers.length; n++) {
			try {
				handlers[n]({ widget: widget});
			}
			catch (e) {
				egl.printError("DojoAccordionContainer: event handler for "+eventName+" failed.", e);
			}
		}
	},
	"setBorderColor" : function( borderColor){
		this._setImportantStyle("border-color", egl.toPX(borderColor));		
	},
	"setBorderWidth" : function( borderWidth){
		this._setImportantStyle("border-width", egl.toPX(borderWidth));		
	},
	"setBorderTopWidth" : function( borderWidth){
		this._setImportantStyle("border-top-width", egl.toPX(borderWidth));		
	},
	"setBorderRightWidth" : function( borderWidth){
		this._setImportantStyle("border-right-width", egl.toPX(borderWidth));		
	},
	"setBorderLeftWidth" : function( borderWidth){
		this._setImportantStyle("border-left-width", egl.toPX(borderWidth));		
	},
	"setBorderBottomWidth" : function( borderWidth){
		this._setImportantStyle("border-bottom-width", egl.toPX(borderWidth));		
	},
	"setBorderStyle" : function( borderStyle){		
		this._setImportantStyle("border-style", borderStyle);
	},
	"setBorderTopStyle" : function( borderStyle){
		this._setImportantStyle("border-top-style", borderStyle);		
	},
	"setBorderRightStyle" : function( borderStyle){
		this._setImportantStyle("border-right-style", borderStyle);		
	},
	"setBorderLeftStyle" : function( borderStyle){
		this._setImportantStyle("border-left-style", borderStyle);		
	},
	"setBorderBottomStyle" : function( borderStyle){
		this._setImportantStyle("border-bottom-style", borderStyle);		
	},
	"_setImportantStyle" : function(style, value){
		this.eze$$DOMElement.style.cssText = this.eze$$DOMElement.style.cssText + ";" + style + ":" + value + " !important";
	},
	"getSelection" : function(){
		return this.selection;
	},
	"setSelection" : function(index){
		var eglWidget = this;
		this.selection = index;
		setTimeout(function() {
			if (eglWidget.dojoWidget) {
				var contentPanes = eglWidget.dojoWidget.getChildren();
				if (index > 0 && index <= contentPanes.length) {
					eglWidget.selection = index;
					eglWidget.dojoWidget.selectChild(contentPanes[index-1]);
				}
			}
		},1);
	},
	"getOnTabSelected" : function() { return this.onTabSelected || (this.onTabSelected = []); },
	"setOnTabSelected" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; }			
});