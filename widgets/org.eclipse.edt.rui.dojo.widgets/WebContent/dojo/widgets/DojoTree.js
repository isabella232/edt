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
	'dojo.widgets', 'DojoTree',
	'dojo.widgets', 'DojoContainer',
	'div',
{
	"constructor" : function() {
		this.setChildType("dojo.widgets.DojoTreeNode");
		dojo.require("dijit.Tree"); 
		dojo.require("dojo.data.ItemFileWriteStore"); 
		this.eglNodes = {};
		this.showRoot = true;
	},
	"createDojoWidget" : function(parent){
		if(!this.model)
			this.setModel();
		var eglWidget = this;
		this.dojoID = "egl.DojoTree" + (++egl._dojoSerial);
		this._mergeArgs({
			id: this.id || this.dojoID,
			model: this.model,			
			showRoot: this.showRoot,
			_createTreeNode: function(args) {
				var treeNode = eglWidget.eglNodes[args.item.id];
				var node;
				if(treeNode && args.item && !args.item.root){
					if(treeNode.dojoWidget){
						return treeNode.dojoWidget;
					}
					var style = treeNode.eze$$DOMElement.style;
					treeNode._mergeArgs(args);
					treeNode._args.getParent = function(){		               
		                var parent = this.domNode ? dijit.getEnclosingWidget(this.domNode.parentNode) : null;
		                return parent && parent.isContainer ? parent : null;
		            };
					node = new dijit._TreeNode(treeNode._args);					
					treeNode.dojoWidget = node;
					node.set("id", treeNode.id);					
					treeNode.render();
					treeNode.copyStyle(style);
				}else if(!args.item.root){
					egl.printError("DojoTree \""+child.id+"\" Cannot create.");
				}else{
					node = new dijit._TreeNode(args);	
				}		
				return node;				
			}
		});
		this._args.onClick = function(item, node, e){
			eglWidget.selection = item.id && item.id.length > 0 ? item.id[0] : eglWidget.id;			
			eglWidget.handleEvent(eglWidget.getOnClick(), "onClick", e);
			var treeNode = node.domNode.eze$$widget;
			if(treeNode && treeNode.onClick){
				treeNode.handleEvent(treeNode.getOnClick(), "onClick", e);
			}
		};

		if(this.getWidgetOrientation()) {
			this._args.dir=this.getWidgetOrientation();
		}
		
		this.dojoWidget = new dijit.Tree(this._args, parent);

		if (this.textLayoutThis || this.reverseTextDirectionThis){
			this.applyBiDiMarkersToWidgetText();
			this.dojoWidget.rootNode.labelNode.innerHTML = this.text;
			egl.dojo.widgets.DojoContainer.prototype.setBiDiMarkers.call(this);
		}
		
		setTimeout(egl.startVEWidgetTimer, 2000);
		this.dojoWidget.startup();
	},
	"setModel" : function() {
		this.storeData = {
			identifier: 'id',
			label: 'text',
			items: this.getModelData()
		};
		this.store = new dojo.data.ItemFileWriteStore({data: this.storeData});		
		this.model = new dijit.tree.ForestStoreModel({
			store: this.store,
			rootLabel: this.text
		});
	},
	"setChildren" : function(children) {
		egl.eglx.ui.rui.Widget.prototype.removeChildren.call(this);
		this.children = children;
		if(dojo && dojo.data)
			this.setModel();
		var eglWidget = this;
		setTimeout(function() {
			eglWidget.renderWhenDojoIsDoneLoading();
		},1);
	},
	"removeChildren" : function() {
		this.setChildren([]);
	},
	"appendChild" : function(child){
		if (child) {			
			child.parent = this;
			child.setTree(this);
			var eglWidget = this;
			this.store.fetchItemByIdentity({
				identity: eglWidget.id, 
				onItem: function (item) {
					try {
						eglWidget.model.newItem(child.getItemOnly(), item);
						eglWidget.store.save();
					}
					catch (e) {
						egl.printError("Cannot add TreeNode \""+child.id+"\" to \""+eglWidget.id+"\". Duplicate node is not allowed.", e);
					}
				}, 
				onFail: function() {
					egl.printError("Cannot add TreeNode \""+child.id+"\" to \""+eglWidget.id+"\". Duplicate node is not allowed.");
				}
			});	
			this.children.appendElement(child);
			if(child.children && child.children.length){
				for(var i=0; i<child.children.length; i++){
					var t_child = child.children[i];
					child.appendChild(t_child);
				}
			}
		}
	},
	"getIndex" : function(child){
		if(this.children){
			for(var i=0; i<this.children.length; i++){
				if(child.id == this.children[i].id){
					return i+1;
				}
			}
			return -1;
		}
	},
	"removeChild" : function(child){
		if(child){
			if(child.children && child.children.length>0){
				var length = child.children.length;
				for(var i=length-1; i>=0; i--){
					var t_child = child.children[i];
					child.removeChild(t_child,child.getIndex(t_child));
				}
			}
			this.children.removeElement(this.getIndex(child));
			var eglWidget = this;
			this.store.fetchItemByIdentity({
				identity: child.id,
				onItem: function (item) {
					try {
						eglWidget.store.deleteItem(item);
						eglWidget.store.save();						
					}
					catch(e) {
						e.message = "Cannot remove DojoTreeNode \""+child.id+"\" from \""+eglWidget.id+"\"";
						egl.printError("Invalid argument to DojoTreeNode.removeChild", e);
					}
				}
			});
		}
	},
	"getElementById" : function(id) {
		var modelContainsThisItem = null;
		this.store.fetchItemByIdentity({
			identity: id, 
			onItem: function (item) {
				modelContainsThisItem = item;
			}
		});
		if (modelContainsThisItem)
			return this.eglNodes[id];		
		return null;
	},
	"getModelData" : function() {
		var model = []; 
		for (var n=0; n<this.children.length; n++) {
			var child = this.children[n]
            child.setTree(this);
			model.push(child.getItem());
		}
		return model;
	},
	"setText" : function(text){			
		this.text = text;
		this.applyBiDiMarkersToWidgetText();		
		if(this.dojoWidget){
			this.model.rootLabel = text;
			this.dojoWidget.rootNode.set("label",text);
		}
	},
	"getShowRoot" : function(showRoot){
		return this.showRoot;
	},
	"setShowRoot" : function(showRoot){
		this.showRoot = showRoot;
		if(this.dojoWidget){
			this.render();
		}
	}
});