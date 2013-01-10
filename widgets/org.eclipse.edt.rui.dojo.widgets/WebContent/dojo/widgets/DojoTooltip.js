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
	'dojo.widgets', 'DojoTooltip',
	'dojo.widgets', 'DojoBase',
	'div',
{
	"constructor" : function() {
	},
	"createDojoWidget" : function(parent) {
		this._args = {
			connectId: this.target || [],
            label: this.contents || "",
            showDelay: this.delay || 10,
            defaultPosition: this.defaultPosition || []
		};
		this.dojoWidget = new dijit.Tooltip(this._args);
		this.dojoWidget.startup();
	},
	"setTarget" : function(target) {
		this.target = target;
		if(this.dojoWidget){
			this.dojoWidget.set("connectId", target);
		}else{ // The target may not be attached to the Dom, so suspend
			var eglWidget = this;
			setTimeout(function() {
				eglWidget.renderWhenDojoIsDoneLoading(["dijit/Tooltip"]);
			},1);
		}		
	},
	"setContents" : function(contents){
		this.contents = contents;
		if(this.dojoWidget){
			this.dojoWidget.set("lable", this.contents);
		}
	},
	"setDefaultPosition" : function(defaultPosition){
		this.defaultPosition = defaultPosition;
		if(this.dojoWidget){
			this.dojoWidget.set("defaultPosition", this.defaultPosition);
		}
	},
	"setDelay" : function(delay){
		this.delay = delay;
		if(this.dojoWidget){
			this.dojoWidget.set("showDelay", this.delay);
		}
	}
});
