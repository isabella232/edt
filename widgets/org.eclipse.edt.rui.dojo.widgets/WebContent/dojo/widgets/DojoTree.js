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
	'dojo.widgets', 'DojoTree',
	'dojo.widgets', 'DojoContainer',
	'div',
{
	"constructor" : function() {
		var _this = this;
		_this.setChildType("dojo.widgets.DojoTreeNode");
		_this.eglNodes = {};
		_this.showRoot = true;
		_this.initRendered = false;
		_this.requireList = ["dijit/Tree", "dojo/data/ItemFileWriteStore", "utility/Synchronor" ];
		require(
			_this.requireList,
			function( tree, ifws, synchronor ){
				_this.synchronor = synchronor;
				_this.renderWhenDojoIsDoneLoading(
						_this.requireList
				);
			}
		);
	},
	"createDojoWidget" : function(parent){
		if( this.synchronor )
			this.synchronor.trigger( this, "SYN_MODULE_LOADED" );
		
		if(!this.model)
			this.setModel( false );

		var eglWidget = this;
		this._mergeArgs({
			id: this.id || this.dojoID,
			model: this.model,
			showRoot: this.showRoot,
			_createTreeNode: function(args){
				var treeNode = eglWidget.eglNodes[args.item.id];
				var node;
				if(treeNode && args.item && !args.item.root){
					if(treeNode.dojoWidget){
						// @Smyle: update indent attribute if there is any indent 
						// change happening, e.g, root hidden, root shown, etc.
						if( treeNode.dojoWidget.get("indent") != args["indent"] )
							treeNode.dojoWidget.set( "indent", args["indent"] );
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
		this.initRendered = true;
	},
	"setModel" : function( toRenderWidget ) {
		var _this = this;
	
		toRenderWidget = toRenderWidget ? true : false;
		
		if( !this.id ){
			_this.dojoID = "egl.DojoTree" + (++egl._dojoSerial);
			_this.id = _this.dojoID;
		}
		
		require(
			[
			 	"utility/Synchronor"
			 ],
			function( synchronor ){
				synchronor.wait(
					[_this], "SYN_MODULE_LOADED",
					function(){
						_this.storeData = {
							identifier : 'id',
							label : 'text',
							items : _this
									.getModelData()
						};
					
						_this.store = new dojo.data.ItemFileWriteStore(
								{
									data : _this.storeData
								});
						_this.model = new dijit.tree.ForestStoreModel(
								{
									store : _this.store,
									rootLabel : _this.text,
									rootId : _this.id
								});
						
						if( toRenderWidget && _this.initRendered )
							_this.renderWhenDojoIsDoneLoading( _this.requireList ); 
					}
				);
			}
		);
	},
	"setChildren" : function(children) {
		var _this = this;
		egl.eglx.ui.rui.Widget.prototype.removeChildren.call(_this);
		_this.children = children;
		_this.setModel( true );
	},
	"removeChildren" : function() {
		this.setChildren([]);
	},
	"appendChild" : function(child){
		if (child) {			
			child.parent = this;
			child.setTree(this);
			var eglWidget = this;
			this.model.getRoot(
				function (item) {
					try {
						eglWidget.model.newItem(child.getItemOnly(), item);
						eglWidget.store.save();
					}
					catch (e) {
						egl.printError("Cannot add TreeNode \""+child.id+"\" to \""+eglWidget.id+"\". Duplicate node is not allowed.", e);
					}
				}, 
				function() {
					egl.printError("Cannot add TreeNode \""+child.id+"\" to \""+eglWidget.id+"\". Duplicate node is not allowed.");
				}
			);	
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
