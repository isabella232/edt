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
	'dojo.widgets', 'DojoButton',
	'dojo.widgets', 'DojoBase',
	'div',
{
	"constructor" : function() {
		dojo.require("dijit.form.Button");	
		this.setData("Button");
	},
	"createDojoWidget" : function(parent) {
		this.runEventHandlers = function() { }; // turn off EGL basic event handlers
		var eglWidget = this;
		this._mergeArgs({
			label: this.text || ""
		});
		this.dojoWidget = new dijit.form.Button(this._args, parent);
		this.dojoWidget.setDisabled(this.disabled ? true : false);
		if(this.width){
			this.setWidth(this.width);
		}
		if(this.height){
			this.setHeight(this.height);
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
	"setText": function(text) {
		this.text = text;
		this.setBiDiMarkers();
		if (this.dojoWidget)
			this.dojoWidget.setLabel(text);		
	},
	"getDisabled": function() {
		return Boolean(this.disabled);
	},
	"setDisabled": function(disabled) {
		this.disabled = disabled;
		if (this.dojoWidget)
			this.dojoWidget.setDisabled(this.disabled ? true : false);
	},
	"getText" : function() {
		if (this.text.charAt(0)>=egl.LRE && 
			this.text.charAt(0) <= egl.RLO)
			return this.text.substring(1);
		return this.text;
	},
	"setData" : function ( data ){
		egl.dojo.widgets.DojoBase.prototype.setData.call(this, data);
		this.setBiDiMarkers();
	},
	"setWidth" : function(width){
		egl.dojo.widgets.DojoBase.prototype.setWidth.call(this, width);
		if(this.dojoWidget){
			this.dojoWidget.domNode.firstChild.style.width = (parseInt(width)-8) + "px";
		}
	},
	"setHeight" : function(height){
		egl.dojo.widgets.DojoBase.prototype.setHeight.call(this, height);
		if(this.dojoWidget){
			this.dojoWidget.domNode.firstChild.style.height = (parseInt(height)-8) + "px";
		}
	},
	"setBiDiMarkers" : function () {
		this.applyBiDiMarkersToWidgetText();
		if (this.data) {
			this.data = this.text;
		}
	}
});