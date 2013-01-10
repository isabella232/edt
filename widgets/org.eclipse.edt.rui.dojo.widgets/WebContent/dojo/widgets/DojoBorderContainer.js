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
	'dojo.widgets', 'DojoBorderContainer',
	'dojo.widgets', 'DojoContainer',
	'div',
	{
	"constructor" : function() {
		this.setRequireWidgetList(["dijit/layout/BorderContainer", "dijit/layout/ContentPane"]);
		this.setChildType("dojo.widgets.DojoContentPane");
		this.width = 800;
		this.height = 450;
	},
	"createDojoWidget" : function(parent) {
		this.dojoID = "egl.DojoBorderContainer"  + (++egl._dojoSerial);
		this._mergeArgs({
			id: this.id || this.dojoID,
			gutters: this.borders===true?true:false,
			style: "width:"+egl.toPX(this.width)+";height:"+egl.toPX(this.height)
		});
		this.dojoWidget = new dijit.layout.BorderContainer(this._args, parent);
		this.dojoWidget.startup();
	},
	"addChild" : function(child, index) {
		egl.dojo.widgets.DojoContainer.prototype.addChild.call(this, child);
	},
	"setBorders" : function(val) {
		// borders cannot be set dynamically
		if(this.borders!=val){
			this.borders=val;
			if(this.dojoWidget){
				this.setChildren(this.children);
			}			
		}
	},
	"isBorders" : function() {
		return this.borders;
	}
});
