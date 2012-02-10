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
	'dojo.widgets', 'DojoTextArea',
	'dojo.widgets', 'DojoTextBase',
	'textarea',
{
	"constructor" : function() {
		dojo.require("dijit.form.SimpleTextarea");
		var eglWidget = this;
		setTimeout(function() {
			eglWidget.renderWhenDojoIsDoneLoading();
		}, 1);
	},
	"createDojoWidget" : function(parent) {
		var eglWidget = this;		
		this._mergeArgs({
			cols : this.cols ? ""+this.cols : "100",
			rows : this.rows ? ""+this.rows : "10",
			onClick : function() { 
				eglWidget.handleEvent(eglWidget.getOnClick(), "onClick")
			}
		});
		this._setCommonProp();
		this.dojoWidget = new dijit.form.SimpleTextarea( this._args, parent);
				
		this.dojoWidget.startup();		
	},
	"setNumRows" : function( numRows ) {
		this.rows = numRows;
		if(this.dojoWidget){
			this.dojoWidget.set("rows","" + numRows);
		}
	},
	"getNumRows" : function(){
		return this.rows;
	},
	"setNumColumns" : function( numColumns ) {
		this.cols = numColumns;
		if(this.dojoWidget){
			this.dojoWidget.set("cols","" + numColumns);
		}
	},
	"getNumColumns" : function(){
		return this.cols;
	}
});