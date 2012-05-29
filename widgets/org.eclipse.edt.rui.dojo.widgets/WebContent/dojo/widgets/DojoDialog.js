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
	'dojo.widgets', 'DojoDialog',
	'dojo.widgets', 'DojoContainer',
	'div',
{
	"constructor" : function() {
		this.requireList = ["dijit/Dialog"];
		this.setRequireWidgetList(this.requireList);
		this.content = egl.createElement("div");
		this.content.height = "auto";
		this.draggable = true;
		this.isOpen = false;
	},
	
	"createDojoWidget" : function(parent) {
		this.dojoID = "egl.DojoDialog_" + (++egl._dojoSerial);
		for (var n=0; n<this.children.length; n++) {
			this.content.appendChild(this.children[n].eze$$DOMElement);
		}
		var c = [ this.content ];
		
		if (this.buttons != null) {
			var actionBar = document.createElement("div");
			actionBar.className = "dijitDialogPaneActionBar";
		
			for (var n = 0; n < this.buttons.length; n++) {			
				actionBar.appendChild(this.buttons[n].eze$$DOMElement);
			}
			c.push(actionBar);
		}
		this._mergeArgs({
			title : this.title || "", 
			id: this.id || this.dojoID,
			content: c
		});
		this.dojoWidget = new dijit.Dialog(this._args, parent);
		this.dojoWidget.startup();
		if (this.isOpen)
			this.showDialog();
	},
	"getButtons" : function() {
		return this.buttons;		
	},
	"setButtons" : function(buttons) {
		this.buttons = buttons;
	},	
	"copyStyle" : function(style) {
		for (f in style){
			if (style[f] != ""){
				try { this.content.style[f] = style[f]; } catch (e) { }
			}
		}				
		if (this.width) egl.setWidth(this.dojoWidget.domNode, egl.toPX(this.width));
		if (this.height) egl.setHeight(this.dojoWidget.domNode, egl.toPX(this.height));
	},
	"setChildren" : function(children) {
		this.children = children;
	},
	"showDialog" : function() {
		this.isOpen = true;
		if (this.dojoWidget) {
			this.dojoWidget.show();
		}
		else
			this.renderWhenDojoIsDoneLoading(this.requireList);
	},
	"hideDialog" : function() {
		this.isOpen = false;
		if (this.dojoWidget)
			this.dojoWidget.hide();
		else
			this.renderWhenDojoIsDoneLoading(this.requireList);
	},
	"setVisible" : function(visible) {
		egl.dojo.widgets.DojoContainer.prototype.setVisible.call(this, visible);
		if(this.dojoWidget){
			if(visible)
				this.content.style.visibility="";
			else this.content.style.visibility="hidden";
			this.dojoWidget.domNode.style.visibility="";
		}
	},
	"getVisible" : function(){
		if(this.dojoWidget){
			return this.content.style.visibility=="hidden"?false:true;
		}else{
			return egl.dojo.widgets.DojoContainer.prototype.getVisible.call(this);
		}
	}
});