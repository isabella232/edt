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
	'dojo.widgets', 'DojoRadioGroup',
	'dojo.widgets', 'DojoBase',
	'div',
{
	"constructor" : function() {
		this.runEventHandlers = function() { }; // turn off EGL basic event handlers
	},
	"setOptions" : function(options, placeholder) {
		this.groupName = "egl.dojoRG_" + (++egl._dojoSerial);
		this.options = options;
		this.inputs = [];
		if(this.container){
			this.removeChildren();
		}
		this.container = egl.createChild(this.eze$$DOMElement, "span");
		for (var n=0; n<this.options.length; n++) {
			var id = this.groupName + "_input_" + n;
			var input = egl.createChild(this.container, "input");
			input.id = id;
			if (!egl.IE && egl.IEVersion < 9) input.setAttribute("type", "radio");
			input.setAttribute("name", this.groupName);
			input.setAttribute("value", this.options[n]);
			this.inputs.push(input);
			if (this.selected == this.options[n])
				input.setAttribute("checked", "checked");
			var label = egl.createChild(this.container, "label");
			if (!this.textLayoutThis && !this.widgetOrientationThis) //none Bidi property is set
				label.innerHTML = options[n];
			else
				label.innerHTML = this.setBiDiMarkers(options[n]);
			
			label.setAttribute("for", id);
			label.style.padding = "3px";
			if (this.vertical)
				egl.createChild(this.container, "br");
		}
		this.container.style.display = "none";
		this.renderWhenDojoIsDoneLoading(["dijit/form/RadioButton"]);
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
		}else if(htmlEventName == "focus" || htmlEventName == "blur"){ // Focus event only happens in the child nodes
			func = function(e) {
				for (var n=eglWidget.radios.length-1; n>=0; n--) {
					if(eglWidget.radios[n].active) return;
				}
				eglWidget.handleEvent(eglWidget["getOn" + eglEventName](), "on" + eglEventName, e);
	        };
		}else{
			func = function(e) {
				eglWidget.handleEvent(eglWidget["getOn" + eglEventName](), "on" + eglEventName, e);
	        };
		}
		var self = this;
		if(htmlEventName == "focus" || htmlEventName == "blur" || htmlEventName == "onkeydown" || htmlEventName == "onkeypress" || htmlEventName == "onkeyup"){
        	if(this.radios){
        		for (var n=this.radios.length-1; n>=0; n--) {
        			require(["dojo/_base/connect"], function(connect){
        				connect.connect(self.radios[n].domNode, "on" + htmlEventName, func);
        			});
    			}
        	}else{
        		obj["on" + dojoEventName] = func;
        	}
		}else{
			require(["dojo/_base/connect"], function(connect){
				connect.connect(self.container, "on" + htmlEventName, func);
			});
		}		
	},
	"getOptions" : function() {
		return this.options;
	},
	"render" : function() {
		if(this._renderSerial && this._renderSerial == this.groupName){
			return;
		}
		this._renderSerial = this.groupName;
		this.destroyAtRender();
		var eglWidget = this;
		this.radios = [];
		for (var n=0; n<this.options.length; n++) {
			this._mergeArgs({
				id: this.groupName + "__" + n,
				checked: (this.selected == this.options[n]),
				name: this.groupName,
				index: n,
				onChange : function() {
					if(this.checked){
						eglWidget.handleEvent(eglWidget.getOnChange(), "onChange", null);
					}					
				}
			});
			this._args.onClick = function(e){
				var input = eglWidget.inputs[this.index];
				eglWidget.selected = input.value;
			}
			this.radios[n] = new dijit.form.RadioButton(this._args, this.inputs[n]);
		}
		if (this.getWidgetOrientation() == "rtl"){
			this.eze$$DOMElement.dir = "rtl";
			this.eze$$DOMElement.align = "right";
		} else if (this.getWidgetOrientation() == "ltr"){
			this.eze$$DOMElement.dir = "ltr";
			this.eze$$DOMElement.align = "left";
		}
		if(this.disabled){
			this.setDisabled(this.disabled);
		}
		this.container.style.display = "inline-block";
	},
	"destroy" : function(){
		if(this.radios && this.radios.length > 0){
			for (var n=0; n<this.radios.length; n++) {
				this.radios[n].destroy();
			}
		}
	},
	"setOpacity" : function(opacity) {
		var style = this.container.style;
		this.opacity = opacity;
		style.opacity = opacity;
		style.display = "inline-block";
		style.filter = (opacity >= 0.90) ? "''" : "alpha(opacity="+(100*opacity)+")";
		style.mozOpacity = opacity; 
	},
	"setSelected" : function(selected) {
		this.selected = selected;
		if (this.container) {
			this.eze$$DOMElement.removeChild(this.container);
			this.setOptions(this.options);
		}
	},
	"getSelected" : function() {
		return this.selected;
	},
	"setDisabled" : function(disabled) {
		this.disabled = disabled;
		if(this.radios){
			for (var n=this.radios.length-1; n>=0; n--) {
				this.radios[n].set("disabled", disabled);
			}
		}
	},
	"getDisabled" : function(){
		return this.disabled;
	},
	"setWidgetOrientation" : function(value) {
		egl.dojo.widgets.DojoBase.prototype.setWidgetOrientation.call(this, value); 
		this.setOptions(this.options, "");
	},
	"setTextLayout" : function(value) {
		egl.dojo.widgets.DojoBase.prototype.setTextLayout.call(this, value); 
		this.setOptions(this.options, "");
	},
	"setBiDiMarkers" : function (string){
		//ther is some 'core' problem in dojo; if all options are set in Hebrew and no Bidi properties are set, radio group looks wrong;
		//this is fixed here, but this code is called ONLY if some Bidi property is set (even if it is equal to defaul value)
		var resultStr;
		var isRTL = this.getWidgetOrientation() == "rtl";
		var isVisual = this.textLayoutThis == "Visual";
		egl.LRM = String.fromCharCode(8206);
		egl.RLM = String.fromCharCode(8207);	
		if (!isRTL)
			if (isVisual)
				resultStr = egl.LRO + string + egl.PDF;
			else
				resultStr = egl.LRE + string + egl.PDF;
		else {
			if (isVisual){
				resultStr = /*egl.RLM +*/ egl.RLO + string + egl.PDF;
			}
			else
				resultStr = egl.RLM + egl.LRM + string + egl.PDF;
		}	
		return resultStr;
	},
	"getValueAsText" : function(){
		return(this.getSelected());
	},
	"setValueAsText" : function(value){
		this.setSelected(value);
	}
});