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
	'dojo.widgets', 'DojoTreeNode',
	'dojo.widgets', 'DojoContainer',
	'div',
{
	"render" : function() {	
		this.eze$$ready = false;
		var marker = null;
		this.renderingStep = 4;
		this.copyAttribute();		

		this.eze$$DOMElement.eze$$widget = this;
		this.renderingStep = 5;		
		this.eze$$ready = true;
		this.printStartupMessage();
		egl.startVEWidgetTimer();			
	},
	"getItem" : function() {
		if (!this.item)
			this.item = { id: this.id, text: this.text, parent: this.parent ? this.parent.id : null };
		if (this.children) 
			this.item.children = this.getChildItems();
		return this.item;
	},
	"getItemOnly" : function(){
		this.selfitem = {id : this.id, text:this.text, parent : this.parent ? this.parent.id : null };
		return this.selfitem;
	},
	"getChildItems" : function() {
		var childNodes = [];
		for (var n=0; n<this.children.length; n++) {
			childNodes.push(this.children[n].getItem());
		}
		return childNodes;		
	},
	"setText" : function(text){
		this.text = text;
		if(this.tree && this.tree.store){
			var eglWidget = this;
			var eglTree = eglWidget.tree;
			eglTree.store.fetchItemByIdentity({
				identity: eglWidget.id,
				onItem: function (item) {
					try {
						eglTree.store.setValue(item, "text", text);
						eglTree.store.save();						
					}
					catch(e) {
						e.message = "Cannot change DojoTreeNode \""+eglTree.id+"\" from \""+treeWidget.id+"\"";
						egl.printError("Invalid argument to DojoTreeNode.setText", e);
					}
				}
			});
		}
	},
	"getText" : function(){
		return this.text;
	},
	"setID" : function(id){
		this.id = id;
	},
	"getID" : function(){
		return this.id;
	},
	"contains" : function(child){
		var contain = false;
		
		for(var i=0; i<this.children.length; i++){
			if(child.id == this.children[i].id){
				contain = true;
			}
		}
		 
		return contain;
	},
	"appendChild" : function(child) {
		if (child) {
			if(!this.contains(child)){
				this.children.appendElement(child);
			}
			child.parent = this;
			child.setTree(this.tree);
			var eglWidget = this;
			this.tree.store.fetchItemByIdentity({
				identity: this.id, 
				onItem: function (item) {
					try {
						eglWidget.tree.model.newItem(child.getItemOnly(), item);
						eglWidget.tree.store.save();
					}
					catch (e) {
						egl.printError("Cannot add treenode \""+child.id+"\" to \""+eglWidget.id+"\". Duplicate nodes not allowed.", e);
					}
				}, 
				onFail: function() {
					egl.printError("Cannot add treenode \""+child.id+"\" to \""+eglWidget.id+"\". Duplicate nodes not allowed.");
				}
			});
			
			if(child.children && child.children.length){
				for(var i=0; i<this.children.length; i++){
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
		}
		return -1;
	},
	"removeChild" : function(child) {
		if (child) {
			this.tempchild = child.id;
			if(child.children && child.children.length > 0){
				var length = child.children.length;
				for(var i=child.children.length-1 ; i>=0; i--){
					var t_child = child.children[i];
					child.removeChild(t_child,child.getIndex(t_child));
				}
			}
			
			this.children.removeElement(this.getIndex(child));
			var eglWidget = this;
			this.tree.store.fetchItemByIdentity({
				identity: child.id, 
				onItem: function (item) {
					try {
						eglWidget.tree.store.deleteItem(item);
						eglWidget.tree.store.save();
					}
					catch(e) {
						e.message = "Cannot remove DojoTreeNode \""+child.id+"\" from \""+eglWidget.id+"\"";
						egl.printError("Invalid argument to DojoTreeNode.removeChild", e);
					}
				}
			});
		}
	},
	"removeChildren" : function(){
		var len = this.children.length;
		for(var n = len-1; n >= 0; n--){
			this.removeChild(this.children[n]);
		}
	},
	"setChildren" : function(children) {
		this.children = [];
		for (var n=0; n<children.length; n++) {
			var child = children[n];
			if (this.tree) {
				this.appendChild(child);
			} else {	
				child.parent = this;
				this.children.push(child);
			}
		}		
	},
	"getElementById" : function(id) {
		return this.tree.getElementById(id);
	},
	"getParent" : function() {
		return this.parent;
	},
	"setTree" : function(tree) {
		this.tree = tree;
		this.tree.eglNodes[this.id] = this;
		if (this.children)
			for (var n=0; n<this.children.length; n++)
				this.children[n].setTree(tree);
	}, 
	"setBiDiMarkers" : function (textLayout, reverseTextDirection) {
		if (this.text && (textLayout || reverseTextDirection)) {
			var isVisual = textLayout == "Visual";
			var isReverseDirection = reverseTextDirection == "Yes";			
			this.text = this.setBiDiMarkersStr(this.text,isVisual,isReverseDirection);
			this.setText(this.text);
		}
		if (this.children)
			for (var n=0; n<this.children.length; n++){
				var childReverseTextDirection;
				var childTextlayout;
				if (!this.children[n].reverseTextDirectionThis)
					childReverseTextDirection = reverseTextDirection;
				else
					childReverseTextDirection = this.children[n].reverseTextDirectionThis;
				
				if (!this.children[n].textLayoutThis)
					childTextlayout = textLayout;
				else
					childTextlayout = this.children[n].textLayoutThis;
				
				this.children[n].setBiDiMarkers(childTextlayout,childReverseTextDirection);
			}
	}
});
