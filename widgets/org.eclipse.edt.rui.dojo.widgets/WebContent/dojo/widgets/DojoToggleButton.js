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
	'dojo.widgets', 'DojoToggleButton',
	'dojo.widgets', 'DojoBase',
	'div',
{
	"constructor" : function() {
		this.text = "";
		this.checkedText = "";
		var eglWidget = this;
		setTimeout(function() {
			eglWidget.renderWhenDojoIsDoneLoading();
		},1);		
		dojo.require("dijit.form.Button");	
	},
	"createDojoWidget" : function(parent) {
		this.runEventHandlers = function() { }; // turn off EGL basic event handlers
		var eglWidget = this;
		this._mergeArgs({
			label: this.text,
			disabled: this.disabled || false,
			onChange: function(val){
				if (val){
					this.attr('label', eglWidget.checkedText); 
				}else{
					this.attr('label', eglWidget.text); 
				}
				eglWidget.handleEvent(eglWidget.getOnChange(), "onChange", null);
			}
		});
		this._args._onClick = function(e){
			eglWidget.handleEvent(eglWidget.getOnClick(), "onClick", e);
			if(this.valueNode){
				egl.stopEventPropagation(e);
			}
		};
		this.dojoWidget = new dijit.form.ToggleButton(this._args, parent);
		this.dojoWidget.setDisabled(this.disabled ? true : false);
		this.dojoWidget.domNode.firstChild.style.display = "block";
		if(this.height){
			this.dojoWidget.domNode.firstChild.style.height = ( parseInt(this.height) - 8 ) + "px";
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
	"setHeight" : function(height){
		egl.dojo.widgets.DojoBase.prototype.setHeight.call(this, height);
		if(this.dojoWidget){
			this.dojoWidget.domNode.firstChild.style.height = (parseInt(height)-8) + "px";
		}
	},
	"isChecked" : function() {
		if (this.dojoWidget)
			return this.dojoWidget.attr('checked');
	},
	"setText": function(text) {
		this.text = text;
		this.setBiDiMarkers();
		if (this.dojoWidget)
			this.dojoWidget.setLabel(text);
	},
	"setCheckedText" : function(text) {
		this.checkedText = text;		
	},
	"setDisabled" : function(disabled){
		this._setProperty("disabled", "disabled", disabled);
	},
	"getDisabled" : function(){
		return this._getProperty("disabled","disabled");
	},
	"getText" : function() {
		return this.text;
	},
	"getCheckedText" : function() {
		return this.checkedText;
	},
	"setBiDiMarkers" : function () {
		if (this.textLayoutThis || this.reverseTextDirectionThis){	
			var isVisual = this.textLayoutThis == "Visual";
			var isReverseDirection = this.reverseTextDirectionThis == "Yes";
			if (this.text)
				this.text = this.setBiDiMarkersStr(this.text,isVisual,isReverseDirection);
			if (this.checkedText)
				this.checkedText = this.setBiDiMarkersStr(this.checkedText,isVisual,isReverseDirection);
		}  
	}
});