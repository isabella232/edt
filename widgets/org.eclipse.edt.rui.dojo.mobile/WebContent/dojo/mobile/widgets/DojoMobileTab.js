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
	'dojo.mobile.widgets', 'DojoMobileTab',
	'dojo.mobile.widgets', 'DojoMobileContainer',
	'li',
{
	"constructor" : function() {
		var _this = this;
		this.started  = false;
		this.selected = false;
		this.containerWidget = null;
		this.text = '';
		require(
		[
		 	"dojo/mobile/utility/Synchronor",
		    "dojox/mobile/TabBarButton",
		    "dojox/mobile/View"
		], 
		function( synchronor ){
			_this.synchronor = synchronor;
			_this.renderWhenDojoIsDoneLoading();
		});	 
	},	
	"_setTab" : function() {
		this.setTitle(this.text);
		if(this.icon){
			this.setIcon(this.icon);
		}
	},
	"setText" : function( text ){
		this.text = text;
		if( this.tabViewWidget ){
			this.tabViewWidget.domNode.innerText = text;
		}
	},
	"getText" : function(){
		return this.text || '';
	},
	"createDojoWidget" : function(parent) {
		var _this = this;
		var tabBtnNode  = parent;
		var tabViewNode = document.createElement( 'div' );
		var tabBtnStyle = {};
		this.moveTo = "eze$$innerView" + (++egl._dojoSerial);		

		if( !tabBtnNode || !tabViewNode ) egl.createRuntimeException("ERROR CREATING TAB WIDGET");
	
		this.tabViewWidget = new dojox.mobile.View({
				id		: this.moveTo,
				selected: this.getSelected()
			}, 
			tabViewNode
		);
		
		tabViewNode.innerText = this.text;

		if( _this.icon1 )
			tabBtnStyle.icon1 = this.icon1;
		if( _this.icon2 )
			tabBtnStyle.icon2 = this.icon2;
		if( _this.moveTo )
			tabBtnStyle.moveTo = this.moveTo;
		if( _this.title )
			tabBtnStyle.label = this.title
		else
			tabBtnStyle.label = "eze$$innerView" + egl._dojoSerial
			
		this.tabBtnWidget = new dojox.mobile.TabBarButton(
			tabBtnStyle, 
			tabBtnNode
		);
		
		var defaultCallBack = _this.tabBtnWidget.onClick ;
		_this.tabBtnWidget.onClick = function( value ) {
			if( typeof  defaultCallBack === "function" )
				defaultCallBack.apply( _this.tabBtnWidget, arguments );
			_this.handleEvent( _this.getOnClick(), "onClick" ); 
		};
		
		if( _this.parent && _this.parent.tabBarWidget.getIndexOfChild( _this.children[i].tabBtnWidget ) == -1 ){
			_this.parent.tabBarWidget.addChild( this.tabBtnWidget );
			_this.parent.ContainerNode.appendChild( this.tabViewWidget.domNode );
			_this.tabBtnWidget.startup();
			_this.tabViewWidget.startup();
		}
		
		_this.dojoWidget = _this.tabBtnWidget;
		_this.containerWidget = _this.tabViewWidget;
		_this.synchronor.trigger( _this, 'SYN_READY' );
		
		_this.synchronor.wait(
			_this.children,
			"SYN_READY",
			function(){
				_this.setChildren( _this.children, _this.containerWidget );
			}
		);
		
		_this.started = true;
	},	
	"setTitle" : function(title) {
		this.title = title; 
		if( this.tabBtnWidget )
			this.tabBtnWidget.set({label:this.title});
	},
	"getTitle" : function() {
		return this.title;
	},
	"setIcon1" : function(icon1){
		this.icon1 = icon1;
		if( this.tabBtnWidget )
			this.tabBtnWidget.set({icon1:this.icon1});
		return;
	},
	"setIcon2" : function( icon2 ){
		this.icon2 = icon2;
		if( this.tabBtnWidget )
			this.tabBtnWidget.set({icon2:this.icon2});
		return;
	},
	"getIcon2" : function( icon2 ){
		return this.icon2;
	},
	"getIcon1" : function() {
		return this.icon1;
	},
	"setID" : function(id) {
		if(id && id!="undefined"){
			this.id = id;
		}else{
			this.id = "";
		}
	},
	"removeChildren" : function( containerWidget ){
		var dojoWidget = containerWidget ? containerWidget : this.dojoWidget;
		this._removeChildren( 1, dojoWidget );
	},
	"setChildren" : function( children, containerWidget ) {		
		var dojoWidget = this.containerWidget;
		if ( dojoWidget ) {
			this.removeChildren( dojoWidget );
			if(children){
				for (var n=0; n< children.length; n++) {
					this._appendChild( children[n], dojoWidget );
				}
			}			 
		}
		this.children = children;
	},
	"getChildren" : function(){
		return this.children;
	},
	"setSelected" : function( selected ){
		this.selected = selected ? true : false;
	},
	"getSelected" : function(){
		return this.selected;
	}
});
