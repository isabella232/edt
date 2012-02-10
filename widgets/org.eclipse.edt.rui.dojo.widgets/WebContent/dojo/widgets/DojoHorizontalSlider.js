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
	'dojo.widgets', 'DojoHorizontalSlider',  	// this class
	'dojo.widgets', 'DojoBase',  			// the super class
	'div',									// dom element type name
{
	"constructor" : function() {
		this.minimum = 0;
		this.maximum = 100;
		this.step = 0;
		dojo.require("dijit.form.Slider");
	},
	"createDojoWidget" : function(parent){
		var eglWidget = this;
		if (this.showLabels && !this.step) {
			var error = egl.createChild(this.eze$$DOMElement, "div");
			error.style.color = "red";
			error.style.whitespace = "nowrap";
			error.innerHTML = "Error: \"showLabels==true\' requires a value for \"step\""
		}
		count = Math.min(count, this.getPixelWidth()/25);
		this.step = this.step || 1;
		var count = Math.round((this.maximum-this.minimum)/this.step - 0.5) + 1;
		while (parent.firstChild)
			parent.removeChild(parent.firstChild);
		if (this.showLabels) {
			new dijit.form.HorizontalRule({
				count: count,
				style: "height:5px",
				container: "bottomDecoration"
			}, egl.createChild(parent, "div"));
			new dijit.form.HorizontalRuleLabels({
				count: count,
				minimum: this.minimum,
				maximum: this.maximum,
				style: "height:1.2em;font-size:75%;",
				constraints: "{pattern:'#'}",
				container: "bottomDecoration"
			}, egl.createChild(parent, "div"));
		}
		this._mergeArgs({
			value: this.data,
			minimum: this.minimum,
			maximum: this.maximum,
			discreteValues: count,
			sliderDuration: 500,
			intermediateChanges: true,
			disabled: this.disabled || false,
			onChange : function() {
				eglWidget.handleEvent(eglWidget.getOnChange(), "onChange", null);
			}
		});
		this.dojoWidget = new dijit.form.HorizontalSlider(this._args, parent);
	},
	"setValue" : function( /*int*/ value) {
		if (this.dojoWidget) 
			this.dojoWidget.setValue(value);
		else {
			this.setData(value);
		}
  	},
   	"getValue" : function() {
		if (this.dojoWidget)
			return this.dojoWidget.value;
		else
			return this.data;
  	},
  	"setDisabled" : function(disabled){
		this._setProperty("disabled", "disabled", disabled);
	},
	"getDisabled" : function(){
		return this._getProperty("disabled","disabled");
	},
  	"reportChange" : function() {
  		var value = this.getValue();
  		if (value != this.lastValue)
  			this.handleEvent(this.getOnChange(), "onChange");
  		this.lastValue = value;
  	}
});
