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
	'dojo.widgets', 'DojoValidationBase',
	'dojo.widgets', 'DojoTextBase',
	'div',
{
	"_setCommonProp" : function() {
		egl.dojo.widgets.DojoTextBase.prototype._setCommonProp.call(this);
		var eglWidget = this;
		this._args = this._args || {};		
		if (typeof(this.promptMessage) == "string"){
			this._args.promptMessage = this.promptMessage;
		}
		if (typeof(this.invalidMessage) == "string"){
			this._args.invalidMessage = this.invalidMessage;
		}else{
			this.invalidMessage = "$_unset_$";
		}
		if (typeof(this.missingMessage) == "string"){
			this._args.missingMessage = this.missingMessage;
		}
		if ( this.tooltipPosition ){
			this._args.tooltipPosition = this.tooltipPosition;
		}
		this._args.required = this.required === undefined ? true : this.required;

		this.constraints = this.constraints || [];
		this.validators = this.validators || [];
		this._args._refreshState = function(){
			if(eglWidget.controllerMsg){
				eglWidget.controllerMsg = "";				
			}else{
				this.validate(this._focused);
			}
		}
	},
	"setID" : function(id) {
		if(id){
			this._setProperty("id", "id", id);
			this.eze$$DOMElement.id = "widget_" + id;
		}		
	},
	"getID" : function() {
		return this._getProperty("id","id");
	},
	"_setTextboxStyle" : function(){
		if(egl.IE && egl.IEVersion <= 7 && this.fontWeight){
			this.setFontWeight(this.fontWeight);
		}
		if(egl.IE && egl.IEVersion == 8 && this.opacity ){
			this.setOpacity(this.opacity);
		}
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
  	"setPromptMessage" : function( promptMessage ) {
  		this.promptMessage = promptMessage;
  		if (this.dojoWidget){
  			this.dojoWidget.set("promptMessage", promptMessage);
  		}  			
  	},
  	"getPromptMessage" : function() {
  		return this.promptMessage || "";
  	},
  	/* This error message will only be used for once. This method won't change the errorMessage property for DojoDate/Time/CurrencyTextBox */
  	"_setErrorMessage" : function( errorMessage ) {
  		if (this.dojoWidget){
  			this.dojoWidget.set("invalidMessage", errorMessage);
  		}  			
  	},
  	"setErrorMessage" : function( errorMessage ) {
  		this.invalidMessage = errorMessage;  		
  		if (this.dojoWidget){
  			this.dojoWidget.set("invalidMessage", errorMessage);
  		}  			
  	},
  	"getErrorMessage" : function() {
  		return this.invalidMessage || "";
  	},
  	"setInputRequired" : function(required){
		this.required = required;
		if(this.dojoWidget){
			this.dojoWidget.set("required",required);
		}
	},
	"isInputRequired" : function() {
		return this.required;
	},	
  	"setInputRequiredMessage" : function( missingMessage ) {
  		this.missingMessage = missingMessage;
  		if (this.dojoWidget){
  			this.dojoWidget.set("missingMessage", missingMessage);
  		}  			
  	},
  	"getInputRequiredMessage" : function() {
  		return this.missingMessage || "";
  	},
  	"setTooltipPosition" : function(tooltipPosition) {
  		this.tooltipPosition = tooltipPosition;
  		if (this.dojoWidget){
  			this.dojoWidget.set("tooltipPosition", tooltipPosition);
  		}  		
  	},
  	"getTooltipPosition" : function() {
  		return this.tooltipPosition;
  	},
  	"getValidState" : function() {
  		if(this.dojoWidget && !this.dojoWidget.isValid() ){
  			this.dojoWidget.state = "Error";
  			this.dojoWidget._maskValidSubsetError = false;
  			this.dojoWidget._setStateClass();
  			return this.dojoWidget.getErrorMessage(); 			
  		}
  		return null;
  	},
  	"setConstraints" : function( constraints ) {		
		this.constraints = constraints;
	},
	"getConstraints" : function() {
		return this.constraints;
	},
  	"showErrorMessage" : function(msg) {
  		if(this.dojoWidget){
  			this.controllerMsg = msg;
 			var focused = this.dojoWidget._focused;
  			this.dojoWidget._focused = true;
  			this.dojoWidget.displayMessage(msg);
			this.dojoWidget._focused = focused;
  		}
  	},
 	"showErrorIndicator" : function(show) {
  		if(this.dojoWidget){
  			this.dojoWidget.state = "Error";
  			this.dojoWidget._setStateClass();
  		}
  	},
  	"setValidators" : function( validators ) {
		this.validators = validators;
		if(this.dojoWidget){
			var eglWidget = this;
			this.dojoWidget.set("validator", function(value, constraints){				
				var dojoWidget = eglWidget.dojoWidget;
				if(dojoWidget && dojoWidget._isEmpty(value)){
					if(dojoWidget.required ){
						return false;
					}else{
						return true;
					}
				}
				
				if(this.regExpGen && !(new RegExp("^(?:" + this.regExpGen(constraints) + ")?$")).test(value)){
					if(eglWidget.invalidMessage == "$_unset_$"){
						eglWidget.invalidMessage = dojoWidget.messages.invalidMessage;
					}
					eglWidget._setErrorMessage(eglWidget.invalidMessage);
					return false;
				}
				/* EGL constraint has a different type with the internal constraint object */
				this._maskValidSubsetError = false;
				if(eglWidget.validators.length>0 && !(constraints instanceof egl.eglx.lang.EDictionary)){
					constraints = eglWidget.constraints;
				}				
				for(var i=0;i<eglWidget.validators.length;i++){
					var message = eglWidget.validators[i](value, constraints);
					if( message ){						
						eglWidget._setErrorMessage(message);
						return false;
					}
				}				
				return true;
			});
		}
	},
	"getValidators" : function() {
		return this.validators;
	}
});
