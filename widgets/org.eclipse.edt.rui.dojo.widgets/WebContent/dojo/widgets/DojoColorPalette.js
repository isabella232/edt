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
	'dojo.widgets', 'DojoColorPalette',
	'dojo.widgets', 'DojoBase',
	'div',
{
	"constructor" : function() {
		this.renderWhenDojoIsDoneLoading(["dijit/ColorPalette"]);
	},
	"createDojoWidget" : function(parent) {
		var eglWidget = this;
		this._mergeArgs({
			palette: this.small ? "3x4" : "7x10", 
			onChange : function(val) {
				this.value = val;
				eglWidget.handleEvent(eglWidget.getOnChange(), "onChange", null);
			}
		});
		if(this.value){
			this._args.value = this.value;
		}
		this.dojoWidget = new dijit.ColorPalette(this._args, parent);
	},
	"setValue": function(value) {
		this.value = value;
		if (this.dojoWidget)
			this.dojoWidget.set("value", value);
	},
	"getValue" : function() {
		if (this.dojoWidget)
			return this.dojoWidget.value;
		else
			return this.value || "";
	}
});