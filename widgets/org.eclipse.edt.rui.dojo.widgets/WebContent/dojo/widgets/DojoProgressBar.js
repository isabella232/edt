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
	'dojo.widgets', 'DojoProgressBar',
	'dojo.widgets', 'DojoBase',
	'div',
{
	"constructor" : function() {
		dojo.require("dijit.ProgressBar");
	},
	"createDojoWidget" : function(parent){
		var eglWidget = this;
		this._mergeArgs({
			indeterminate: this.indeterminate || false,
			maximum: this.maximum || 100,
			progress: this.progress || 0,
			places: 0,
			onChange : function() {
				eglWidget.handleEvent(eglWidget.getOnChange(), "onChange", null);
			}
		});
		this.dojoWidget = new dijit.ProgressBar(this._args, parent);
		this.dojoWidget.startup();
	},
	"setMaximum" : function( max ){
		this.maximum = max;
		if(this.dojoWidget)
			this.dojoWidget.update({maximum:this.maximum});
	},
	"getMaximum" : function(){
		return this.maximum;
	},
	"setProgress" : function( progress ){
		if(progress<0){
			progress = 0;
		}
		if(progress>100){
			progress = 100;
		}
		this.progress = progress;
		if(this.dojoWidget)
			this.dojoWidget.update({ progress:this.progress});
		else
			this.setData(progress);
	},
	"getProgress" : function(){
		return this.progress;
	},
	"setIndeterminate" : function( v ) {
		this.indeterminate = v;
		if(this.dojoWidget)
			this.dojoWidget.update({ indeterminate:this.indeterminate});		
	}
});