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
	'dojo.widgets', 'DojoTitlePane',
	'dojo.widgets', 'DojoContainer',
	'div',
{
	"constructor" : function() {
		this.setRequireWidgetList(["dijit/TitlePane"]);
		this.open = true;
	},
	"destroyAtRender" : function() {
		this.destroy();
	},
	"copyStyle" : function(style) {
		egl.dojo.widgets.DojoBase.prototype.copyStyle.call(this, style);
		for (var n=0; n<this.children.length; n++) {
			this.content.appendChild(this.children[n].eze$$DOMElement);			
		}
	},
	"createDojoWidget" : function(parent){
		this.content = egl.createChild(this.eze$$DOMElement, "div");
		this._mergeArgs({
			title: this.title || "", 
			open: this.open,
			content: this.content,
			duration: this.duration || 250
		});
		if(this.id){
			this._args.id = this.id;
		}
		this.dojoWidget = new dijit.TitlePane(this._args, parent);
		this.dojoWidget.startup();
		if(this.height){
			this.setHeight(this.height);
		}
	},
	"setHeight" : function(height){
		egl.dojo.widgets.DojoBase.prototype.setHeight.call(this, height);
		if(this.dojoWidget){ // 45 is the height of the title bar
			this.dojoWidget.containerNode.style.height = (parseInt(height) - 45) + "px";
		}
	},
	"getOpen" : function(){
		if (this.dojoWidget)
			this.open = this.dojoWidget.open;
		return this.open;
	},
	"setOpen" : function(open){
		this.open = open;
	},
	"toggle" : function(){
		this.dojoWidget.toggle();
	}

});
