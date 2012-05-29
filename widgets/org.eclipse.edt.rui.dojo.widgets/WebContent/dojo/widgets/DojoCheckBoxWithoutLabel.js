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
	'dojo.widgets', 'DojoCheckBoxWithoutLabel',
	'dojo.widgets', 'DojoBase',
	'div',
{
	"constructor" : function() {
		this.setData("", ["dijit/form/CheckBox"]);
		//require(["dijit/form/CheckBox"]);
	},
	"createDojoWidget" : function(parent) {
		var eglWidget = this;
		this._mergeArgs({
			checked: this.selected || false,
			disabled: this.disabled || false			
		});
		this._args.onChange = function() { 
			eglWidget.handleEvent(eglWidget.getOnChange(), "onChange", null);
		}
		this.dojoWidget = new dijit.form.CheckBox(this._args, parent);
	},
	"getSelected" : function () {
  		return this.dojoWidget && this.dojoWidget.getValue()!=false;
  	},
  	"setSelected" : function ( selected ) {
  		this.selected = selected;
  		if (this.dojoWidget)
  			this.dojoWidget.setChecked( selected );
  	},
	"getDisabled": function() {
		return Boolean(this.disabled);
	},
	"setDisabled": function(disabled) {
		this.disabled = disabled;
		if (this.dojoWidget){
			this.dojoWidget.set("disabled", disabled);
		}
	}
});
