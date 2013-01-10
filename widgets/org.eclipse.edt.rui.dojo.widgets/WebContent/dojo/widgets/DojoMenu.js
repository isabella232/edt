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
	'dojo.widgets', 'DojoMenu',
	'dojo.widgets', 'DojoContainer',
	'div',
{
	"constructor" : function() {
		this.setChildType("dojo.widgets.DojoMenuItem");
		this.renderWhenDojoIsDoneLoading(["dijit/Menu", "dijit/form/DropDownButton"]);
	},
	"createDojoWidget" : function(parent){
		this.dojoMenu = new dijit.Menu();
		this.dojoMenu.startup();
		this._mergeArgs({
			label: this.text||"",
			disabled: this.disabled || false,
			dropDown: this.dojoMenu
		});
		if(this.id){
			this._args.id = this.id;
		}
		this.dojoWidget = new dijit.form.DropDownButton(this._args,parent);		
		if(this.children){
			this.setChildren(this.children);
		}
		if(this._parent){
			this._parent.dojoWidget.set("popup",this.dojoMenu);
		}
		this.dojoWidget.domNode.firstChild.style.display = "block";
		if(this.width){
			this.setWidth(this.width);
		}
		if(this.height){
			this.setHeight(this.height);
		}
		if(this._args.onMouseDown){
			var self = this;
			require(["dojo/_base/connect"], function(connect){
				  connect.connect(self.dojoWidget._buttonNode, "onmousedown", self._args.onMouseDown);
			});
		}
		this.dojoWidget.startup();
	},
	"setID" : function(id) {
		if(id){
			this._setProperty("id", "id", id);
			this.eze$$DOMElement.id = "widget_" + id;
			if(!(egl.IE && egl.IEVersion < 8) && this.dojoWidget){
				this.dojoWidget.domNode.setAttribute("widgetid",id);
			}	
		}		
	},
	"getID" : function() {
		return this._getProperty("id","id");
	},
	"_setEvent" : function( htmlEventName, eglEventName, dojoEventName){
		if(htmlEventName == "mousedown" || htmlEventName == "focus" || htmlEventName == "blur"){
			var eglWidget = this;
			this._args = this._args || {};
			func = function(e) {
				eglWidget.handleEvent(eglWidget["getOn" + eglEventName](), "on" + eglEventName, e);
	        };
	        if(this.dojoWidget){
	        	if(htmlEventName == "mousedown"){
	        		var self = this;
	    			require(["dojo/_base/connect"], function(connect){
	    				  connect.connect(self.dojoWidget._buttonNode, "on" + htmlEventName, func);
	    			});
	        	}else{
	        		this.dojoWidget.set("on" + dojoEventName, func);
	        	}				
			}else{
				this._args["on" + dojoEventName] = func;
			}
		}else{
			egl.dojo.widgets.DojoBase.prototype._setEvent.call(this, htmlEventName, eglEventName, dojoEventName);
		}
	},
	"setWidth" : function(width){
		egl.dojo.widgets.DojoBase.prototype.setWidth.call(this, width);
		if(this.dojoWidget){
			this.dojoWidget.domNode.firstChild.style.width = (parseInt(width)-10) + "px";
		}
	},
	"setHeight" : function(height){
		egl.dojo.widgets.DojoBase.prototype.setHeight.call(this, height);
		if(this.dojoWidget){
			this.dojoWidget.domNode.firstChild.style.height = (parseInt(height)-8) + "px";
		}
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
	"setText" : function(text){
		this.text = text;
		if(this.dojoWidget){
			this.dojoWidget.set("label", text);
		}
	},
	"setDisabled" : function(disabled){
		this._setProperty("disabled", "disabled", disabled);
	},
	"getDisabled" : function(){
		return this._getProperty("disabled","disabled");
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
