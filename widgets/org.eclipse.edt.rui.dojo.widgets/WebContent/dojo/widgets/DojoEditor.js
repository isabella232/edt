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
egl.defineWidget(
	'dojo.widgets', 'DojoEditor',
	'dojo.widgets', 'DojoBase',
	'div',
{
	widgetOrientationThis: "",
	textLayoutThis: "",
	reverseTextDirectionThis: "",	
	"constructor" : function() {
		this.height = 300;
		this.width = 300;
		this.value = "";
		var eglWidget = this;
		setTimeout(function() {
			eglWidget.renderWhenDojoIsDoneLoading();
		},1);	
		dojo.require("dijit.Editor");		
		dojo.require("dijit.layout._LayoutWidget");
	},
	"createDojoWidget" : function(parent) {
		var eglWidget = this;
		var isVisualMode = (this.textLayoutThis == "Visual");		
		if(isVisualMode || (this.widgetOrientationThis != "")) {
//			dojo.registerModulePath("bidi", "dojo/widgets/bidi");
			dojo.require("bidi.DojoEditorBidi"); 
			this._mergeArgs({
				isVisualMode: (this.textLayoutThis == "Visual"), 
				dir: this.widgetOrientationThis,isTextReversed: (this.reverseTextDirectionThis == "Yes"),
				onChange : function() {
					eglWidget.handleEvent(eglWidget.getOnChange(), "onChange", null);
				}
			});
			this.dojoWidget = new bidi.DojoEditorBidi(this._args, parent);
		} else {
			this._mergeArgs({
				value : this.value,
				disabled: this.disabled || false,
				onChange : function() {
					eglWidget.handleEvent(eglWidget.getOnChange(), "onChange", null);
				}
			});
			this.dojoWidget = new dijit.Editor(this._args, parent);		
		}
		
		this.dojoWidget.startup();
		dojo.addOnLoad(eglWidget.dojoWidget, function() {
			eglWidget.dojoWidget.resize({w: eglWidget.width, h: eglWidget.height });	
		});	
	},
	"setHeight" : function(height) {
		this.height = height;
		if(this.dojoWidget){
			this.dojoWidget.resize({h: this.height});
		}
	},
	"getHeight" : function(){
		return this.height;
	},
	"setWidth" : function(width) {
		this.width = width;
		if(this.dojoWidget){
			this.dojoWidget.resize({w: this.width});
		}
	},
	"getWidth" : function(){
		return this.width;
	},
	"getContents" : function() {
		if (this.dojoWidget) {
			if(this.textLayoutThis == "Visual")
				return this.dojoWidget.getValue(true);
			else			
				return this.dojoWidget.get("value");
		} else{
			return this.value;
		}
	},
	"setContents" : function(value) {
		this.value = value;
		if (this.dojoWidget){
			this.dojoWidget.set("value", value);
		}			
	},
	"setDisabled" : function(disabled){
		this._setProperty("disabled", "disabled", disabled);
	},
	"getDisabled" : function(){
		return this._getProperty("disabled","disabled");
	},
	"setWidgetOrientation" : function(value) {
		this.widgetOrientationThis = value;
	},
	"getWidgetOrientation" : function() {
		return this.widgetOrientationThis;
	},
	"setTextLayout" : function(value){
		this.textLayoutThis = value;
	},
	"getTextLayout" : function(){
		return this.textLayoutThis;
	},
	"setReverseTextDirection" : function(value){
		this.reverseTextDirectionThis = value;	
	},
	"getReverseTextDirection" : function(){
		return this.reverseTextDirectionThis;
	},
	"getValue" : function(){
		return this.getContents();
	},
	"setValue" : function(value){
		this.setContents(value);
	}
});