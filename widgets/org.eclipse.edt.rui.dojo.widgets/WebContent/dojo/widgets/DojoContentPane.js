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
	'dojo.widgets', 'DojoContentPane',
	'dojo.widgets', 'DojoContainer',
	'div',
{
	"createDojoWidget" : function(parent) {
		this.renderingStep = 4.1;
		this.assert(dijit.layout.ContentPane, "dijit.layout.ContentPane is undefined");
		this._mergeArgs({
			region: this.region || "",
			title: this.title || "", 
			refreshOnShow: true, 
			closable: this.closable || "", 
			splitter: this.splitter || false
		});
		if(this.id){
			this._args.id = this.id;
		}
		this.dojoWidget = new dijit.layout.ContentPane(this._args, parent);
		this.renderingStep = 4.2;
		try {
			this.setChildren(this.children);
		}
		catch(e) {
			throw "Could not set DojoContentPane children: "+e.message;
		}
	},
	"setChildren" : function(children) {
		this.children = children;
		if (this.dojoWidget) {
			this.assert(this.dojoWidget.domNode, "dijit.layout.ContentPane has no domNode"); 
			this.renderingStep = 4.3;
			for (var n=0; n<children.length; n++)
				this.dojoWidget.domNode.appendChild(children[n].eze$$DOMElement);
			try {
				this.dojoWidget.startup();
			}
			catch (e) {
				throw "dijit.layout.ContentPane could not be started: "+e.message;
			}
		}
		else {
			this.renderWhenDojoIsDoneLoading();
		}
	},
	"appendChild" : function(child) {
		this.dojoWidget.domNode.appendChild(child.eze$$DOMElement);
	},
	"addChild" : function(child, index) {
	},
	"removeAllChildren" : function() {
		while (this.dojoWidget.domNode.firstChild)
			this.dojoWidget.domNode.removeChild(this.dojoWidget.domNode.firstChild);
	},
	"copyStyle" : function(style) {
	},
	"setTitle" : function(val) {
		this.title = val;
		if (this.dojoWidget) {
			this.dojoWidget.attr("title",val);
		}
	},
	"getTitle" : function() {
		return(this.title || "");
	},
	"setRegion" : function(region) {
		this.region = region;
	},
	"setSplitter" : function(split) {
		this.splitter = split;
	},
	"setClosable" : function(closable) {
		this._setProperty("closable", "closable", closable);
	},
	"getClosable" : function() {
		return(this._getProperty("closable", "closable"));
	},
	"setTextLayout" : function(textLayout){
		this.textLayoutThis = textLayout;
	},
	"setReverseTextDirection" : function (reverseTextDirection){
		this.reverseTextDirectionThis = reverseTextDirection;
	},
	"setBiDiMarkers" : function (textLayout, reverseTextDirection) {
		var isVisual = textLayout == "Visual";
		var isReverseDirection = reverseTextDirection == "Yes";
		this.title = this.title || "";
		this.title = this.setBiDiMarkersStr(this.title,isVisual,isReverseDirection);
		this.setTitle(this.title);
	}
	
});