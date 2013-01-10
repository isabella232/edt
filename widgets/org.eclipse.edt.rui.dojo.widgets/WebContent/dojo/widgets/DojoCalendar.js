/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
	'dojo.widgets', 'DojoCalendar',
	'dojo.widgets', 'DojoBase',
	'div',
{
	"constructor" : function() {
		this.renderWhenDojoIsDoneLoading(["dijit/_Calendar"]);
	},
	"createDojoWidget" : function(parent) {
		var eglWidget = this;
		this._mergeArgs({
			onChange : function() {
				eglWidget.handleEvent(eglWidget.getOnChange(), "onChange", null);
			},
			onValueSelected :  function() {
				eglWidget.handleEvent(eglWidget.getOnSelect(), "onSelect", null);
			}
		});
		this.eze$$DOMElement.style.borderCollapse = "separate";
		this.dojoWidget = new dijit._Calendar(this._args, parent);		
		this.dojoWidget.startup();
		this.setValue((this.value != null && this.value != "") ? this.value : new Date());
  	},
	"setValue": function(value) {
		this._setProperty("value", "value", value);
	},
	"getValue" : function() {
		return this._getProperty("value", "value");
	}
});
