////////////////////////////////////////////////////////////////////////////////
// This sample is provided AS IS.
// Permission to use, copy and modify this software for any purpose and
// without fee is hereby granted. provided that the name of IBM not be used in
// advertising or publicity pertaining to distribution of the software without
// specific written permission.
//
// IBM DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SAMPLE, INCLUDING ALL
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL IBM
// BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY
// DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER
// IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING
// OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SAMPLE.
////////////////////////////////////////////////////////////////////////////////

egl.defineWidget(
	'dojo.mobile.widgets', 'DojoMobileTabContainer',
	'dojo.mobile.widgets', 'DojoMobileContainer',
	'div',
{
	"constructor" : function() {		
		var _this = this;
		this.selection	= 1;
		this.started 	= false;
		this.segmentedControl = true;
		require(["dojox/mobile/TabBar"], function(){
			_this.renderWhenDojoIsDoneLoading();
		});	
	},	
	"getChildrenLength" : function(){
		if( this.tabBarWidget ){
			var btnWidgets = this.tabBarWidget.getChildren();
			return btnWidgets ? btnWidgets.length : 0;
		}
		return 0;
	},  
	"setChildren" : function(children) {
		var _this = this;
		this.children = children;
		if( this.tabBarWidget && this.children ){
			var i = 0, j = 0, toDelete = true, toDeleteArr = [], toStartup = [];
			var widgetChildren = this.tabBarWidget.getChildren();
			
			this.selection = this.getSelection();
			if( widgetChildren ){
				for( i = 0; i < widgetChildren.length; ++ i ){
					toDelete = true;
					for( j = 0; j < children.length; ++ j )
						if( widgetChildren[i] == children[j].tabBtnWidget ){
							toDelete = false;
							break;
						}
					if( toDelete )
						toDeleteArr.push(i);
				}
				
				var curSel = this.selection - 1;
				
				// get the max unselected index previous to the specified index
				function getNewSel( index ){
					preSel	 = toDeleteArr[index]-1;
					preIndex = index - 1;
					
					if( preIndex < 0 )
						return 0;
					
					if( toDeleteArr[preIndex] != preSel )
						return preSel;
					else
						return getNewSel( preIndex );
				}

				if( toDeleteArr.length > 0 ){
					toDeleteArr.sort();
					for( i = 0; i < toDeleteArr.length; ++ i ){
						if( toDeleteArr[i] == curSel ){
							newSel = getNewSel( i );
							this.selection = ++newSel;
							break;
						} 
					}
					for( i = 0; i < toDeleteArr.length; ++ i ){
						var viewId = widgetChildren[toDeleteArr[i]].get('moveTo');
						var viewWidget = dijit.byId(viewId);
						if( viewWidget ) viewWidget.destroy(false);
						this.tabBarWidget.removeChild( toDeleteArr[i] );
						widgetChildren[toDeleteArr[i]].destroy(false);
						if( this.selection > 1 && toDeleteArr[i] <= this.selection )
							-- this.selection;
					}
				} 
			}
			
			for( i = 0; i < this.children.length; ++ i ){
				if ( children[i].tabBtnWidget && children[i].tabViewWidget ) {
					if( this.tabBarWidget.getIndexOfChild( children[i].tabBtnWidget ) == -1 ){	
						this.tabBarWidget.addChild( children[i].tabBtnWidget, i+1 );
						this.tabContainerNode.appendChild( children[i].tabViewWidget.domNode );
						toStartup.push(i);
						if( i < this.selection )
							++ this.selection;
					}
				}
				else
					children[i].parent = this;
			}		
			
			this.setSelection( this.selection );
			if( toStartup.length > 0 )
				for( i = 0; i < toStartup.length; ++ i )
					children[toStartup[i]].tabViewWidget.startup();
			
			if( this._layout == "Bottom" )
				this.fixBottom();
		}
	}, 
	"addTab" : function(tab, tabindex){
		if( tabindex < 1 || tabindex > this.children.length + 1 )
			egl.createRuntimeException( this.eze$$typename+".addTab : Invalid tab index " + tabIndex );
		
		this.children.splice(tabindex, 0, tab);
		this.setChildren(this.children);
		return;
	},
	"removeTab" : function(tabindex) {
		if( tabindex < 1 || tabindex > this.children.length )
			egl.createRuntimeException( this.eze$$typename+".addTab : Invalid tab index " + tabIndex );

		this.children.splice( tabindex-1, 1 );
		this.setChildren	( this.children );
		return;
	},
	"appendChild" : function(child) {
		this.children = this.children || [];
		this.children.push(child);
		this.setChildren(this.children);

	},
	"appendChildren" : function(children) {
		this.children = this.children || [];
		for(var n=0; n<children.length; n++){
			this.children.push(children[n]);
		}
		this.setChildren(this.children);

	},
	"removeChild" : function(child) {		
		if(!this.children){
			return;
		}
		for (var n=0; n< this.children.length; n++) {
			if(this.children[n] == child){
				this.removeTab(n+1);
				return;
			}
		}
		throw this.eze$$typename+".removeChild: the child is not found.";
	},
	"removeChildren" : function(){
		this.children = [];
		if(this.dojoWidget){
			while(this.dojoWidget.domNode.firstChild){
				this.dojoWidget.domNode.removeChild(this.dojoWidget.domNode.firstChild);
			}
		}
	},
	"createDojoWidget" : function(parent) {
		var _this = this;
		if( this._class ) 
			parent.className = parent.className + ' ' + this._class;
			
		var tabBarNode = document.createElement('ul');
		
		if( !tabBarNode ) egl.createRuntimeException("ERROR IN CREATING TABBAR");
		
		_this.tabContainerNode = parent;	
		_this.containerNode    = parent;
		
		if( _this._layout == "Fixed Bottom" ){
			tabBarNode.style.position = "fixed";
			tabBarNode.style.left = 0;
			tabBarNode.style.bottom = 0;
			tabBarNode.style.right = 0;
			tabBarNode.style.zIndex = "10000";
			_this.tabContainerNode.appendChild( tabBarNode );
		}
		else if( _this._layout == "Fixed Top" ){
			tabBarNode.style.position = "fixed";
			tabBarNode.style.left = 0;
			tabBarNode.style.top = 0;
			tabBarNode.style.right = 0;
			tabBarNode.style.zIndex = "10000";
			_this.tabContainerNode.appendChild( tabBarNode );
		}
		else if( _this._layout != "Bottom" )
			_this.tabContainerNode.appendChild( tabBarNode );
	
		tabBarNode.id = _this.id || "";
		var style = {};
		if( _this.segmentedControl )
			style.barType = "segmentedControl";
		
		_this.tabBarWidget = new dojox.mobile.TabBar( style, tabBarNode );
		_this.dojoWidget = _this.tabBarWidget;
		
		require( 
			["dojo/mobile/utility/Synchronor"],
			function( synchronor ){
				synchronor.trigger( _this, 'SYN_READY' );
				synchronor.wait(
					_this.children, 'SYN_READY',
					function(){
						for( var i = 0; i < _this.children.length; ++ i ){
							if ( _this.children[i].tabBtnWidget && _this.children[i].tabViewWidget ) {
								_this.tabBarWidget.addChild(_this.children[i].tabBtnWidget);
								_this.tabContainerNode.appendChild(_this.children[i].tabViewWidget.domNode);
							}
							else
								_this.children[i].parent = _this;
						} 
						
						if( _this._layout == "Bottom" )
							_this.tabContainerNode.appendChild( tabBarNode );
						
						// initially select
						_this.setSelection( _this.selection );
						
						_this.tabBarWidget.startup();
						for( var i = 0; i < _this.children.length; ++ i )
							if( _this.children[i].tabViewWidget )
								_this.children[i].tabViewWidget.startup();	
						
						// work around dojo mobile's default height calculation						
						if( _this.segmentedControl == false )
							require( 
								["dojo/_base/sniff"], 
								function( has ){
									setTimeout(
										function() {
											_this.tabBarWidget.domNode.style.height = "auto";
										}, 
										has("ie") ? 100 : 0
									);
								}
							);
						
						// visually select
						_this.started = true;
					}
				);
			}
		);
		
		// fix ugly eze$domElement rewritten problem by default copyAttribute
		var oldCopyAttribute = _this.copyAttribute;
		_this.copyAttribute = function(){
			oldCopyAttribute.apply( _this, arguments );
			_this.eze$$DOMElement = _this.containerNode;
		};
	},
	"fixBottom" : function(){
		var _this = this;
		for( var i = 0; i < _this.tabContainerNode.childNodes.length; ++ i )
			if( _this.tabContainerNode.childNodes[i] == _this.tabBarWidget.domNode 
					&& i != (_this.tabContainerNode.childNodes.length-1) ){
				_this.tabContainerNode.removeChild( _this.tabBarWidget.domNode );
				_this.tabContainerNode.appendChild( _this.tabBarWidget.domNode );
				break;
			}
		
	},
	"getSelection" : function(){
		var widgetChildren = this.tabBarWidget.getChildren();
		for( var i = 0; i < widgetChildren.length; ++ i )
			if( dojo.hasClass( widgetChildren[i].domNode, "mblTabButtonSelected" ) ){
				return i+1;
			}
		return 0;
	},	
	// 1 based index selection
	"setSelection": function(selection){
		this.selection = selection;
		selection = selection - 1;
		if( this.tabBarWidget && this.children && this.children.length > 0 ){
			// Initially Select
			if( this.started == false ){
				for( var i = 0; i< this.children.length; i++ ){
					this.children[i].tabViewWidget.domNode.style.visibility = "visible";
					if( i == selection ){
						this.children[i].tabBtnWidget.select();
						this.children[i].tabViewWidget._visible = true;
						this.children[i].tabViewWidget.domNode.style.display = '';
						this.selection = selection + 1;
						continue;
					} 
					this.children[i].tabBtnWidget.deselect();
					this.children[i].tabViewWidget.domNode.style.display = 'none';
					this.children[i].tabViewWidget._visible = false;
				}
				if( this.children.length <= selection ){
					this.children[this.children.length].tabBtnWidget.select();
					this.children[this.children.length].tabViewWidget.domNode.style.display = '';
					this.children[this.children.length].tabViewWidget._visible = true;
					this.selection = this.children.length;
				}
				else if( selection < 0 ){
					this.children[0].tabBtnWidget.select();
					this.children[0].tabViewWidget.domNode.style.display = '';
					this.children[0].tabViewWidget._visible = true;
					this.selection = 1;
				}
			}
			// Visually Select
			else{
				for( var i = 0; i< this.children.length; i++ ){
					this.children[i].tabViewWidget._visible = false;
					this.children[i].tabViewWidget.domNode.style.visibility = "visible";
					this.children[i].tabViewWidget.domNode.style.display = 'none';
				}
				
				if( this.children.length <= selection ){
					this.children[this.children.length].tabViewWidget._visible = true;
					this.children[this.children.length].tabViewWidget.domNode.style.display = '';
					this.children[this.children.length].tabBtnWidget.defaultClickAction();
					this.selection = this.children.length;
				}
				else if( selection < 0 ){
					this.children[0].tabViewWidget._visible = true;
					this.children[0].tabBtnWidget.defaultClickAction();
					this.children[0].tabViewWidget.domNode.style.display = '';
					this.selection = 1;				
				}
				else{
					this.children[selection].tabViewWidget._visible = true;
					this.children[selection].tabBtnWidget.defaultClickAction();
					this.children[selection].tabViewWidget.domNode.style.display = '';
					this.selection = selection + 1;				
				}
			}
		}
	},
	"setID" : function(id) {
		if(id && id!="undefined"){
			this.id = id;
		}else{
			this.id = "";
		}
	},
	"setSegmentedControl" : function( status ){
		this.segmentedControl = status;
	},
	"getSegmentedControl" : function(){
		return this.segmentedControl || false;
	},
	"getClassName" : function(){
		return this._class;
	},
	"setClassName" : function( className ){
		this._class = className;
	},
	"getFixedBottom" : function(){
		return this._fixedBottom;
	},
	"setFixedBottom" : function( status ){
		this._fixedBottom = status;
	},
	"getLayout" : function(){
		return this._layout;
	},
	"setLayout" : function( newLayout ){
		this._layout = newLayout;
	}
});
