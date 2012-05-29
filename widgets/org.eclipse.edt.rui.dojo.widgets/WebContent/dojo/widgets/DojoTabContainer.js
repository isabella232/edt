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
	'dojo.widgets', 'DojoTabContainer',
	'dojo.widgets', 'DojoContainer',
	'div',
	{
	"constructor" : function() {
		this.setChildType("dojo.widgets.DojoContentPane");
		this.width = 800;
		this.height = 450;
		this.setRequireWidgetList(["dijit/layout/StackContainer", "dijit/layout/TabContainer" ,"dijit/layout/ContentPane"]);
		this.selectedChild = -1;
	},
	"createDojoWidget" : function(parent) {
		this.dojoID = "egl.DojoTabContainer" + (++egl._dojoSerial);
		if(this._id == null)
			this._id = this.dojoID;
		this._mergeArgs({
			tabPosition: this.getDojoTabPosition(), 
			doLayout: true, 
			id: this.id || (this._id || this.dojoID),
			style: "width:"+egl.toPX(this.width)+";height:"+egl.toPX(this.height)
		});
		var visibility = parent.style.visibility;
		this.dojoWidget = new dijit.layout.TabContainer(this._args, parent);
		parent.style.visibility = visibility;
		this.dojoWidget.startup();
		this.addEventHandlers();
		if (this.selectedChild != -1)
			this.setSelection(this.selectedChild);
	},
	"destroyAtRender" : function() {
		if(this.dojoWidget && this.dojoWidget.tablist){
			this.dojoWidget.tablist.destroy();			
		}
		egl.dojo.widgets.DojoContainer.prototype.destroyAtRender.call(this);
	},
	"destroy" : function(){
		if(this.dojoWidget){
			this.removeChildren();
			this.dojoWidget.destroy();
		}
	},
	"addChild" : function(child, index) {
		egl.dojo.widgets.DojoContainer.prototype.addChild.call(this, child);
	},
	"addEventHandlers" : function() {
		var eglWidget = this;
		require(["dojo/_base/connect"], function(connect){
			connect.subscribe(eglWidget.dojoID+"-selectChild", function(child) {
					if (eglWidget.eze$$ready) {
						eglWidget.selection = eglWidget.getChildIndex(child) + 1;
						eglWidget.notifyListeners(child.eglWidget, eglWidget.getOnTabSelected(), "onTabSelected");
					}
			});
		});
		
		require(["dojo/_base/connect"], function(connect){
			connect.subscribe(eglWidget.dojoID+"-removeChild", function(child) {
				if (eglWidget.eze$$ready) {
					var index = eglWidget.getChildIndex(child);
					eglWidget.selection = index + 1;
					eglWidget.children.splice(index,1);
					eglWidget.notifyListeners(child.eglWidget, eglWidget.getOnTabRemoved(), "onTabRemoved");
				}
			});
		});
	},
	"notifyListeners" : function(widget, handlers, eventName) {
		for (var n=0; n<handlers.length; n++) {
			try {
				handlers[n]({ widget: widget});
			}
			catch (e) {
				egl.printError("DojoTabContainer: event handler for "+eventName+" failed.", e);
			}
		}
	},
	"getDojoTabPosition" : function() {
		if (this.tabPosition == "top") return "top-h";
		if (this.tabPosition == "bottom") return "bottom-h";
		if (this.tabPosition == "right") return "right-h";
		return "left-h";		
	},
	"getTabPosition" : function(){
		if(this.dojoWidget){
			return this.dojoWidget.get("tabPosition");
		}else{
			return this.tabPosition || "top";
		}
	},
	"setTabPosition" : function(position){
		this.tabPosition = position;
		if(this.dojoWidget){
			this.dojoWidget.set("tabPosition", this.getDojoTabPosition());
			this.setChildren(this.children);
		}
	},
	"getSelection" : function(){
		return this.selection;
	},
	"setSelection" : function(index){
		var eglWidget = this;
		this.selectedChild = index;
		setTimeout(function() {
			if (eglWidget.dojoWidget) {
				var contentPanes = eglWidget.dojoWidget.getChildren();
				if (index > 0 && index <= contentPanes.length) {
					eglWidget.selection = index;
					eglWidget.dojoWidget.selectChild(contentPanes[index-1]);
				}
			}
		},1);
	},
	"getOnTabSelected" : function() { return this.onTabSelected || (this.onTabSelected = []); },
	"getOnTabAdded" : function() { return this.onTabAdded || (this.onTabAdded = []); },
	"getOnTabRemoved" : function() { return this.onTabRemoved || (this.onTabRemoved = []); },
	"setOnTabSelected" : function() { throw egl.eglx.ui.rui.Widget.ErrorMessageForEventHandlers; },
	"removeTab" : function(tabindex){
		if(tabindex < 1 || undefined == this.children || this.children.length < 1 || tabindex > this.children.length){
			return;
		}

		this.children.splice(tabindex-1,1);
		egl.dojo.widgets.DojoContainer.prototype.setChildren.call(this, this.children);
		
		if(tabindex < this.selection){
			this.selection -= 1;
		}else if(tabindex == this.selection){
			this.selection = 1;
		}
	}
});