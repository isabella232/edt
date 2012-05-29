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
	'dojo.widgets', 'DojoComboBox',
	'dojo.widgets', 'DojoBase',
	'select',
{
	"constructor" : function() {
		this.pageSize = -1;
		this.holder = null;
		this.eze$$DOMElement.style.display = "none";
		this.isBidi = false;
		this.isFocused = false;
		this.editable = true;
	},
	"createDojoWidget" : function(parent) {
		this.setBiDiMarkers();
		this.parent = parent;
		this.runEventHandlers = function() { }; // turn off EGL basic event handlers
		var eglWidget = this;
		
		if (!this.holder) {
			this.holder = parent.parentNode;
		} else if (!parent.parentNode) {
			this.holder.appendChild(parent);
		}  
		if (this.data)
			for (var n=0; n<this.data.length; n++)
				egl.createChild(this.eze$$DOMElement, "option").innerHTML = this.data[n]; 
		this._mergeArgs({
			value: this.value == undefined ? this.data[0] : this.value, 
			autoComplete: false,
			selectOnClick: true,
			disabled: this.disabled || false,
			onChange: function() {	
				if(egl.IE){
					setTimeout(function() {
						eglWidget._handleChange();
					},1);
				}else{
					eglWidget._handleChange();
				}
			}
		});
		this._args.onBlur = function() {
			eglWidget.isFocused = false;			
			egl.eglx.ui.rui.RuiLib.setTextSelectionEnabled(false);
			eglWidget.handleEvent(eglWidget.getOnFocusLost(), "onFocusLost", null);
		};
		this._args.onFocus = function() {
			if(egl.IE){
				setTimeout(function() {
					eglWidget._handleFocus();
					dijit.selectInputText(eglWidget.dojoWidget.textbox);
				},1);
			}else{
				eglWidget._handleFocus();
			}
		};
		this._args.onMouseDown = function(e) { 
			egl.eglx.ui.rui.RuiLib.setTextSelectionEnabled(true); 
			eglWidget.handleEvent(eglWidget.getOnMouseDown(), "onMouseDown", e);
		};		
				
		if (this.pageSize != -1) {
			this._args.pageSize = this.pageSize;
		}
		if (this.isBidi == false) {
			this.dojoWidget = new dijit.form.ComboBox(this._args, parent);
		} else {
			this._args.isVisualMode = this.textLayoutThis == "Visual";
			if (this.widgetOrientationThis == "rtl")
				this._args.dir="rtl";
			else
				this._args.dir="ltr";
			this.dojoWidget = new bidi.DojoComboBoxBidi(this._args, parent);
		}
		this.eze$$DOMElement.style.display = "inline-block";
		if(this.isFocused){
			dijit.selectInputText(this.dojoWidget.textbox);
		}
		this.setEditable(this.editable);
		if(egl.IE && egl.IEVersion <= 7 && this.fontWeight){
			this.setFontWeight(this.fontWeight);
		}
		if(egl.IE && egl.IEVersion == 8 && this.opacity ){
			this.setOpacity(this.opacity);
		}
	},
	"_handleChange" : function(){
		this.value = this.dojoWidget.getValue();
		this.handleEvent(this.getOnChange(), "onChange", null);
		if(this.isFocused){
			dijit.selectInputText(this.dojoWidget.textbox);
		}
	},
	"_handleFocus" : function(){
		this.isFocused = true;
		egl.eglx.ui.rui.RuiLib.setTextSelectionEnabled(true);
		this.handleEvent(this.getOnFocusGained(), "onFocusGained", null);		
	},
	"setFontWeight" : function(fontWeight){
		egl.eglx.ui.rui.Widget.prototype.setFontWeight.call(this, fontWeight);
		this.fontWeight = fontWeight;
		if(egl.IE && egl.IEVersion <= 7 && this.dojoWidget ){
			this.dojoWidget.textbox.style.fontWeight = fontWeight;
		}
	},
	"setOpacity" : function(opacity){
		egl.eglx.ui.rui.Widget.prototype.setOpacity.call(this, opacity);
		this.opacity = opacity;
		if(egl.IE && egl.IEVersion == 8 && this.dojoWidget ){
			this.dojoWidget.textbox.style.opacity = opacity;
			this.dojoWidget.textbox.style.filter = (opacity >= 0.90) ? "''" : "alpha(opacity="+(100*opacity)+")";
		}
	},
	"setValue": function(value) {
		this.value = value;
		if (this.dojoWidget)
			this.dojoWidget.setValue(value);
	},
	"getValue" : function() {
		return this.value;
	},
	"setValues": function(values) {	
		if(this.eze$$DOMElement.tagName.toLowerCase()!="select"){
			var container = this.eze$$DOMElement.parentNode;
			this.eze$$DOMElement = egl.createChild(container, "select"); 
			this.values = null;
			this.value = null;
		}
		this.eze$$DOMElement.style.display = "none";
		if(this.values){	
			this.values.length = values.length;
			for(var i=0; i<values.length; i++){
				this.values[i] = values[i];
				this.data[i] = values[i];
			}
			if(this.isFocused && this.dojoWidget){
				dijit.selectInputText(this.dojoWidget.textbox);
			}
		}else{
			this.values = values;			
			this.setData(this.values, ["dijit/form/ComboBox", "bidi.DojoComboBoxBidi"]);
		}
	},
	"getValues" : function() {
		return this.values;
	},
	"setEditable" : function(editable) {
		this.editable = editable;
		if(this.dojoWidget){
			this.dojoWidget.textbox.readOnly = !editable;
		}		
	},
	"getEditable" : function(){
		return this.editable;
	},
	"setDisabled" : function(disabled){
		this._setProperty("disabled", "disabled", disabled);
	},
	"getDisabled" : function(){
		return this._getProperty("disabled","disabled");
	},
	"setReverseTextDirection" : function(reverseTextDirection) {
		//just to override DojoBase's method
	},
	"setBiDiMarkers" : function (){
		if (this.isBidi == false) {
				if (this.reverseTextDirectionThis == "Yes" ||
					this.widgetOrientationThis == "rtl" ||
					this.textLayoutThis == "Visual")
					this.isBidi = true;
		}
		
		if (this.isBidi == true && this.dojoWidget){
			this.eze$$DOMElement.removeChild(this.dojoWidget);
			this.createDojoWidget(this.parent);
		}
	},
	"setValueAsText" : function(value){
		this.setValue(value);
	},
	"getValueAsText" : function(){
		return(this.getValue());
	}
});