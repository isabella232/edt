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
	'dojo.widgets', 'DojoTextBase',
	'dojo.widgets', 'DojoBase',
	'div',
{
	"_setCommonProp" : function() {
		this._args = this._args || {};
		if(typeof(this.value)=="string"){
			this._args.value = this.value || "";
		}			
		if( typeof(this.maxLength) == "number" && this.maxLength > 0 ){
			this._args.maxLength = this.maxLength;
		}
		this._args.selectOnClick = this.selectOnClick || false;
		this._args.disabled = this.disabled || false;
		this._args.readOnly = this.readOnly || false;
		if( typeof(this.placeHolder) == "string" ){
			this._args.placeHolder = this.placeHolder;
		}
		if ( typeof(this.textCase) == "string" ){
			switch(this.textCase){
  			case("upper"):
  				this._args.uppercase = true;
  				break;
  			case("lower"):
  				this._args.lowercase = true;
  				break;
  			case("proper"):
  				this._args.propercase = true;
  				break;
  			}
		}
		this._args.trim = this.trim || false;
		var eglWidget = this;
		this._args.onChange = function() {
			eglWidget.handleEvent(eglWidget.getOnChange(), "onChange", null)
		};
		this._args.onFocus = function() {
			eglWidget.selectionEnabled = egl.eglx.ui.rui.RuiLib.getTextSelectionEnabled();
			egl.eglx.ui.rui.RuiLib.setTextSelectionEnabled(true);
			eglWidget.handleEvent(eglWidget.getOnFocusGained(), "onFocusGained", null);
		};
		this._args.onBlur = function() { 
			egl.eglx.ui.rui.RuiLib.setTextSelectionEnabled(eglWidget.selectionEnabled);
			eglWidget.handleEvent(eglWidget.getOnFocusLost(), "onFocusLost", null);
		};		
	},
	"setText" : function( text ){
		this.value = text;
		if(this.dojoWidget){
			this.dojoWidget.set("value",text);
		}
	},
	"getText" : function(){
		if(this.dojoWidget){
			this.value = this.dojoWidget.get("value");
		}
		return this.value? ("" + this.value) : "";
	},
	"setMaxLength" : function( maxLength ) {
  		this.maxLength = maxLength;
  		if (this.dojoWidget){
  			this.dojoWidget.set("maxLength", maxLength);
  		}  			
  	},
  	"getMaxLength" : function() {
  		return this.maxLength || -1;
  	},
  	"setSelectOnClick" : function(selectOnClick) {
  		this.selectOnClick = selectOnClick;
  		if (this.dojoWidget){
  			this.dojoWidget.set("selectOnClick", selectOnClick);
  		}
  	},
  	"isSelectOnClick" : function() {
  		return this.selectOnClick || false;
  	},
  	"setDisabled" : function(disabled) {
  		this.disabled = disabled;
  		if (this.dojoWidget){
  			this.dojoWidget.set("disabled", disabled);
  		}
  	},
  	"isDisabled" : function() {
  		return this.disabled || false;
  	},
  	"getDisabled" : function() {
  		return this.isDisabled();
  	},
  	"setReadOnly" : function(readOnly) {
  		this.readOnly = readOnly;
  		if (this.dojoWidget){
  			this.dojoWidget.set("readOnly", readOnly);
  		}
  	},
  	"isReadOnly" : function() {
  		return this.readOnly || false;
  	},
  	"getReadOnly" : function() {
  		return this.isReadOnly();
  	},
  	"setPlaceHolder" : function( placeHolder ) {
  		this.placeHolder = placeHolder;
  		if (this.dojoWidget){
  			this.dojoWidget.set("placeHolder", placeHolder);
  		}  			
  	},
  	"getPlaceHolder" : function() {
  		return this.placeHolder;
  	},
  	"setTextCase" : function( textCase ) {
  		this.textCase = textCase;
  		if (this.dojoWidget){
  			this.dojoWidget.set("uppercase", false);
  			this.dojoWidget.set("lowercase", false);
  			this.dojoWidget.set("propercase", false);
  			switch(textCase){
  			case("upper"):
  				this.dojoWidget.set("uppercase", true);
  				break;
  			case("lower"):
  				this.dojoWidget.set("lowercase", true);
  				break;
  			case("proper"):
  				this.dojoWidget.set("propercase", true);
  				break;
  			}
  			this.dojoWidget._setValueAttr(this.dojoWidget.get("value"), true);
  		}  			
  	},
  	"getTextCase" : function() {
  		return this.textCase;
  	},
  	"setTrim" : function(trim) {
  		this.trim = trim;
  		if (this.dojoWidget){
  			this.dojoWidget.set("trim", trim);
  		}
  	},
  	"isTrim" : function() {
  		return this.trim || false;
  	},
  	"getTrim" : function() {
  		return this.isTrim();
  	}
});
