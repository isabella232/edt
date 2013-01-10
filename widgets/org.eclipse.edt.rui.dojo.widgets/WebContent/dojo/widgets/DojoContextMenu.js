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
	'dojo.widgets', 'DojoContextMenu',
	'dojo.widgets', 'DojoContainer',
	'div',
{
	"constructor" : function() {
		this.setChildType("dojo.widgets.DojoMenuItem");
		this.setRequireWidgetList(["dijit/Menu"]);
	},
	"createDojoWidget" : function() {
		var nodes = [];
		for (var i=0; i < this.targets.length; i++ )
			nodes[i] = this.targets[i].eze$$DOMElement; 
		this._mergeArgs({
			targetNodeIds : nodes
		});
		if(this.id){
			this._args.id = this.id;
		}
		this.dojoWidget = new dijit.Menu(this._args);
		this.dojoMenu = this.dojoWidget;
		if(this.children){
			this.setChildren(this.children);
		}
		this.dojoWidget.startup();
	},
	"_setEvent" : function( htmlEventName, eglEventName, dojoEventName){
		if(htmlEventName == "focus" || htmlEventName == "blur"){
			var eglWidget = this;
			this._args = this._args || {};
			func = function(e) {
				eglWidget.handleEvent(eglWidget["getOn" + eglEventName](), "on" + eglEventName, e);
	        };
	        if(this.dojoWidget){
				this.dojoWidget.set("on"+dojoEventName, func);
			}else{
				this._args["on" + dojoEventName] = func;
			}
		}else{
			egl.dojo.widgets.DojoBase.prototype._setEvent.call(this, htmlEventName, eglEventName, dojoEventName);
		}
	},
	"setTargets" : function( targets ) {
		this.targets = targets;
		this.renderWhenDojoIsDoneLoading(["dijit/Menu"]);
	},
	"getTargets" : function(){
		return this.targets;
	},
	"setChildren" : function(children){		
		if( this.dojoWidget && children ){
			this.removeChildren();
			for (var n=0; n<children.length; n++) {
				var child = children[n];
				if(child.dojoWidget){
					this.dojoMenu.addChild(child.dojoWidget);
				}else{
					child._parent = this;
					child.render();
				}				
			}
		}
		this.children = children;
	},
	"removeChildren" : function(){
		this.children = [];
		if(this.dojoMenu && this.dojoMenu.containerNode.children.length > 0){
			var body = this.dojoMenu.containerNode;
			while(body.children.length > 0){
				body.removeChild(body.firstChild);
			}
		}
	},
	"setBiDiMarkers" : function () {
		if (this.text){
			var isVisual = this.getTextLayout() == "Visual";
			var isReverseDirection = this.getReverseTextDirection() == "Yes";
			this.text = this.setBiDiMarkersStr(this.text,isVisual,isReverseDirection);
		}
		egl.dojo.widgets.DojoContainer.prototype.setBiDiMarkers.call(this);
	}
});
