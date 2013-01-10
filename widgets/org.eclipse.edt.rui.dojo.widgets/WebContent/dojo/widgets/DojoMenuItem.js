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
	'dojo.widgets', 'DojoMenuItem',
	'dojo.widgets', 'DojoContainer',
	'div',
{
	"constructor" : function() {
		this.setChildType("dojo.widgets.DojoMenu");
		require(["dijit/PopupMenuItem", "dijit/MenuItem", "dijit/MenuSeparator"]);
	},
	"createDojoWidget" : function(parent) {
		var eglWidget = this;
		if(this.children && this.children.length ){
			this._mergeArgs({
				label: this.text, 
				disabled: this.disabled,
				popup: (new dijit.Menu())
			});
			if(this.id){
				this._args.id = this.id;
			}
			this.dojoWidget = new dijit.PopupMenuItem(this._args);
		}else if(this.text){
			this._mergeArgs({
				label: this.text,
				id: this.id || "egl.dojoMenuItem" + (++egl._dojoSerial),
				iconClass: this.iconClass || "",
				disabled: this.disabled || false
			});
			this.dojoWidget = new dijit.MenuItem(this._args);		
		}else{
			if(this.id){
				this._args.id = this.id;
			}
			this.dojoWidget = new dijit.MenuSeparator(this._args);
		}
		// The order is very important
		if(this._parent){
			this._parent.dojoMenu.addChild(this.dojoWidget);
		}
		if(this.children && this.children.length){
			this.setChildren(this.children);
		}		
	},
	"destroyAtRender" : function() {
		if(this.dojoWidget){
			if(this.logicalParent){
				this.logicalParent.dojoWidget.containerNode.removeChild(this.eze$$DOMElement);
				this._parent = this.logicalParent;
			}			
			this.dojoWidget.destroy();
		}else{
			dijit.registry.remove(this.id);
		}
	},
	"setChildren" : function(children){
		var oChildren = this.children;
		this.children = children;
		if( this.dojoWidget){
			if(!oChildren || oChildren.length<=0){
				this.render();
			}
			var error = {message: "A popup menu requires exactly one child, which has to be of type dojo.widgets.DojoMenu"};
			if (children.length == 0)
				this.showError("dojo.widgets.DojoMenu", "DojoMenuItem", null, "getSubMenu", error);
			else
			if (this.children[0].eze$$typename != "DojoMenu")
				this.showError("dojo.widgets.DojoMenu", "DojoMenuItem", this.children[0], "getSubMenu", error);
			this.removeChildren();
			this.children[0].logicalParent = this;
			if(this.children[0].dojoMenu){
				this.dojoWidget.set("popup",this.children[0].dojoMenu);
			}else{
				this.children[0]._parent = this;
			}
		}
	},
	"setText" : function(text){
		var oText = this.text;
		this.text = text;
		if(this.dojoWidget){
			if(oText || (this.children && this.children.length > 0) ){
				this.dojoWidget.set("label", text);
			}else{
				this.render();
			}
		}
	},
	"getText" : function(){
		if (this.text.charAt(0)>=egl.LRE && this.text.charAt(0) <= egl.RLO)
			return this.text.substring(1);
		return egl.dojo.widgets.DojoBase.prototype.getText.call(this);
	},
	"setDisabled" : function(disabled) {
		this.disabled = disabled;
		if (this.dojoWidget)
			this.dojoWidget.setDisabled(disabled);
	},
	"getDisabled" : function() {
		return this.disabled;
	},	
	"setBiDiMarkers" : function (textLayout, reverseTextDirection) {
		var isVisual = textLayout == "Visual";
		var isReverseDirection = reverseTextDirection == "Yes";
		if (this.text){
			this.text = this.setBiDiMarkersStr(this.text,isVisual,isReverseDirection);
		}
	}
});
