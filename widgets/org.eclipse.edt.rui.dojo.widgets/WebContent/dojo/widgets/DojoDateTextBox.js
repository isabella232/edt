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
	'dojo.widgets', 'DojoDateTextBox',
	'dojo.widgets', 'DojoValidationBase',
	'div',
{
	"constructor" : function() {
		dojo.require("dijit.form.DateTextBox");
		var eglwidget = this;
		setTimeout(function(){
			eglwidget.renderWhenDojoIsDoneLoading();
		},1)
	},
	"createDojoWidget" : function(parent){
		var eglWidget = this;
  		this._mergeArgs({
			id : this.id || "ddtb"  + (++egl._dojoSerial),
			type : "text", 
			method : "post",
			onMouseOver: function() {
				eglWidget.selectionEnabled = egl.eglx.ui.rui.RuiLib.getTextSelectionEnabled();
				egl.eglx.ui.rui.RuiLib.setTextSelectionEnabled(true);
			}, 
			onMouseOut: function() { 
				egl.eglx.ui.rui.RuiLib.setTextSelectionEnabled(eglWidget.selectionEnabled);
			}
		});
  		this._setCommonProp(); 		
  		this.dojoWidget = new dijit.form.DateTextBox( this._args, parent);
  		// Set min/max from EGL constraints to Dojo constraints
  		this.setConstraints(this.constraints);
  		this.dojoWidget.constraints.formatLength = this.formatLength || "short";
  		this.setValidators(this.validators);
  		this._setTextboxStyle();
  		this.dojoWidget.startup();
  		if (this.date) 
  			this.setValue(this.date);
  	},
  	"clear" : function () {
  		if (this.dojoWidget) {
  			this.dojoWidget.setDisplayedValue("");
  		}
	},  	
  	"setValue" : function( date ){
  		this.date = date;
		if (this.dojoWidget) {
//			var s = this.dojoWidget.format(date, this.dojoWidget.constraints );
//			this.dojoWidget.setDisplayedValue(s);
			this.dojoWidget.set("value", date);
		}
  	},
  	"getValue" : function(){
  		if (this.dojoWidget) {
  			return this.dojoWidget.get("value");
  		}
  		else{
  			return this.date
  		}  			
  	},
  	"setConstraints" : function( constraints ) {		
		this.constraints = constraints;		
		if(this.dojoWidget){
			if(!(this.dojoWidget.constraints)){
				this.dojoWidget.constraints = {};
			}
			if(constraints.min){
				this.dojoWidget.constraints.min = constraints.min.eze$$value;
			}
			if(constraints.max){
				this.dojoWidget.constraints.max = constraints.max.eze$$value;
			}
		}
	},
  	"setFormatLength" : function ( format ) {
  		this.formatLength = format;
		if (this.dojoWidget){
			var value = this.dojoWidget.get("value");
			this.dojoWidget.constraints.formatLength = format;
			this.dojoWidget.set("value", value);
		}			
  	},
  	"getFormatLength" : function(){
  		return this.formatLength;
  	},
  	"getText" : function(){
  		return egl.eglx.lang.EString.fromEDate(this.getValue()); 
  	},
  	"setText" : function(date){
  		if(date == "")
  			this.setValue(null);
  		else
  			this.setValue(egl.eglx.lang.EDate.fromEString(date));
  	},
  	"getValueAsText" : function(){
  		return this.getText();
  	},
  	"setValueAsText" : function(date){
  		this.setText(date);
  	}
});