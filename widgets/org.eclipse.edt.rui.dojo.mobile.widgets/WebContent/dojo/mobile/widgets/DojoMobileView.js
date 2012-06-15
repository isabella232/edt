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
	'dojo.mobile.widgets', 'DojoMobileView',
	'dojo.mobile.widgets', 'DojoMobileContainer',
	'div',
{
	"constructor" : function() {
		this.backText = "";
		this.headerTitle = "";
		this.id = "";
		this.refs = [];
		this._containerWidth = '100%';
		this._scrollBar = true;
		this._class   = '';
		this.selected = false;
		this.toolBar  = null;
		var _this = this;
		require(
			[ 
			  "dojo/mobile/utility/Synchronor",
			  "dojox/mobile/ScrollableView", 
			  "dojox/mobile/SwapView"
			], 
			function( synchronor ){
				_this.synchronor = synchronor;
				_this.renderWhenDojoIsDoneLoading();
			}
		);		
	},
	"_checkPropsAllSet" : function(){
	},
	"createDojoWidget" : function(parent) {
		var _this = this;
		var scrollStyle = '';
		var dependance = [];
		if( this.id )
			parent.id = this.id;
		
		var callback = function( synchronor ){			
			if( scrollStyle.length > 0 ){
				var scrollElement = dependance.length == 1 ? parent : document.createElement('div');
				_this.dojoWidget = new dojox.mobile.ScrollableView({
					   selected: _this.selected || false,
					   scrollDir :  scrollStyle
					},
					scrollElement
				);
				_this.containerWidget = _this.dojoWidget;
				
				if( _this.containerWidget.domNode.childNodes[0] )
					_this.containerWidget.domNode.childNodes[0].style.width = _this._containerWidth;
			}
			
			if( _this.swappable ){
				var swappWidget = new dojox.mobile.SwapView({
					selected: _this.selected || false
				},	parent );
				if( _this.dojoWidget )
					swappWidget.addChild( _this.dojoWidget );
				else
					_this.containerWidget = swappWidget;
				_this.dojoWidget = swappWidget;
				synchronor.trigger( _this, "SYN_READY" );
			}
			else if( !_this.dojoWidget ){
				_this.dojoWidget = new dojox.mobile.View({
					selected: _this.selected || false
				},  parent );
				_this.containerWidget = _this.dojoWidget;
				synchronor.trigger( _this, "SYN_READY" );
			}
			else
				synchronor.trigger( _this, "SYN_READY" );
			
			if(_this.id){
				_this.dojoWidget.set("id", _this.id);
			}
			
			_this._addHeading();
			
			if(_this.refs){
				for(var n=0;n<_this.refs.length;n++){
					_this.refs[n]._setMoveto(_this.getID());
				}
				_this.refs.length = 0;
			} 
			
			_this.dojoWidget.onBeforeTransitionOut = function(moveTo, dir, transition, context, method){
				var toNode = dojo.byId(moveTo);
				if(toNode){
					toNode.style.top = "0px";
				}
			};
			
			_this.synchronor.wait(
				_this.children, "SYN_READY",
				function(){
					_this.setChildren( _this.children, _this.containerWidget );
					if(_this.children){
						for (var n=0; n< _this.children.length; n++) {
							if(_this.children[n].children && _this.children[n].children.length > 0 && _this.children[n].children[0].view){
								var tabcontainer = _this.children[n];
								for (var i=0; i< tabcontainer.children.length; i++) {
									_this.dojoWidget.domNode.appendChild(tabcontainer.children[i].view.eze$$DOMElement);
									if(tabcontainer.children[i].view.dojoWidget)
										tabcontainer.children[i].view.dojoWidget.startup();
								}			
								var sel = tabcontainer.selection
								setTimeout(
										function() {
											tabcontainer.setSelection(sel);
										}, 1 
								);
							}
						}
					}
					
					if( _this.dojoWidget.domNode && _this.dojoWidget.domNode.parentNode)
						_this.dojoWidget.startup();
					else if( _this.containerWidget.domNode && _this.containerWidget.domNode.parentNode )
						 _this.containerWidget.startup();
					
					if( _this._class )
						require( 
							["dojo/_base/sniff"], 
							function( has ){
								setTimeout(
										function() {
											_this.containerWidget.domNode.className = _this.containerWidget.domNode.className
													+ ' '
													+ _this._class;
										}, has("ie") ? 100 : 0);
							}
						);
				}
			);
		}
		
		if( _this.scrollHorizontally )
			scrollStyle = scrollStyle + 'h';
		if( _this.scrollVertically )
			scrollStyle = scrollStyle + 'v';
		if( _this.swappable )
			dependance.push("dojox/mobile/SwapView");
		if( scrollStyle.indexOf('h') != -1  || scrollStyle.indexOf('v') != -1 )
			dependance.push("dojox/mobile/ScrollableView");

		callback( _this.synchronor );
	},
	"_addHeading" : function() {
		var _this = this;
		var containerWidget = _this._getContainerWidget();
		
		if(_this.headerTitle || (_this.toolBar && _this.toolBar.length) ){
			var headStyle = {};
			
			if( _this.headerTitle ) 
				headStyle.label = _this.headerTitle;
			if( _this.backText ) 
				headStyle.back = _this.backText;
			if( _this.moveTo   ) 
				headStyle.moveTo = _this.moveTo;
			
			_this.heading = new dojox.mobile.Heading( headStyle );	
			containerWidget.addChild( _this.heading );
			if( _this.toolBar && _this.toolBar.length && _this.synchronor )
				_this.synchronor.wait( 
					_this.toolBar , "SYN_READY",
					function(){
						for( var i = 0; i < _this.toolBar.length; ++ i )
							if( _this.toolBar[i].dojoWidget )
								_this.heading.addChild( _this.toolBar[i].dojoWidget );
					}
				);
		}
		
	},
	"_show" : function() {
		if(this.eze$$parent){
			return;
		}else{
			this.setLogicalParent(egl.Document);
			this.setParent(egl.Document);
		}
	},
	"setHeaderTitle" : function(headerTitle)  {
		this.headerTitle = headerTitle;
		if(this.dojoWidget && this.headerTitle){
			if(this.heading){
				this.heading.set({"label" : this.headerTitle} );
			}else{
				this._addHeading();
			}
		}
	},
	"getHeaderTitle" : function(){
		return this.headerTitle;
	},
	"setBackText" : function(backText) {
		this.backText = backText;
		if(this.dojoWidget && this.backText){			
			if(this.heading){
				this.heading.set("back", this.backText);
				var divs = this.heading.domNode.getElementsByTagName("div");
				if(divs.length >= 3){
					divs[1].innerHTML = this.backText;
					this.heading.set({"label" : this.headerTitle});
				}else{
			
				}
			}
		}
	},
	"getBackText" : function() {
		return this.backText;
	},
	"setBackView" : function(backView) {
		this.backView = backView;
		if(this.backView){
			this.backView._show();
			this.moveTo = this.backView.getID();
			if( !this.moveTo ){
				this.backView._addRef(this);
			}
		}
		if(this.dojoWidget && this.backView && this.backView.getID()){
			this._setMoveto(this.moveTo);			
		}

	},
	"_setMoveto" : function( moveTo ) {		
		if(this.heading){
			this.heading.set("moveTo", moveTo);
		}else{
			this.moveTo = moveTo;
		}
	},
	"_addRef" : function( view ){
		this.refs.push(view);
	},
	"getBackView" : function() {
		return this.backView;
	},
	"setSelected" : function(s) {
		var _this = this;
		
		_this.selected = s;
		
		function setSelected(){
			if( _this.dojoWidget ){			
				if( s ){
					if( _this.dojoWidget.domNode.parentNode )
						_this.dojoWidget.show();
					else
						_this.dojoWidget.domNode.style.display = "";
					
					// avoids flick
					if( _this.dojoWidget.domNode.style.visibility != "visible" )
						_this.dojoWidget.domNode.style.visibility = "visible";
					
					// work around layout problem when we are switching view
					if( document.createEvent ){
						event = document.createEvent("HTMLEvents"); 
						event.initEvent("resize", false, true); 
						window.dispatchEvent(event); 
					}else{
						window.fireEvent && window.fireEvent("resize");
					}
				}else{
					_this.dojoWidget.domNode.style.display = "none";
				}			
			}
		}
		
		// work around view display competing problem due to different 
		// EGL declaration sequence
		require(
			[ 
			  "dojo/mobile/utility/Synchronor"
			],
			function( synchronor ){
				synchronor.wait( 
					[_this], "SYN_READY",
					function(){
						setSelected();
					}
				);
			}
		);
	},
	"getSelected" : function(){
		return this.selected;
	},
	"showView" : function(){
		this.selected = true;
		if(this.dojoWidget){
			this.showView();
		}
	},
	"setID" : function(id) {
		this.id = null;
		if(id && id!="undefined"){
			this.id = id;
			if(this.dojoWidget){
				this.dojoWidget.set("id", this.id);
			}
		}		
	},
	"getID" : function() {
		if(this.id){
			return this.id;
		}			
		if(this.dojoWidget){
			return this.dojoWidget.id;
		}			
	},
	"removeChildren" : function( containerWidget ){
		var dojoWidget = this._getContainerWidget( containerWidget );
		this._removeChildren( 1, dojoWidget );
	},
	"_appendChild" : function( child, containerWidget ) {
		
		var dojoWidget = this._getContainerWidget( containerWidget );
		var thisNode = null;
		if (!dojoWidget)
			throw egl.createRuntimeException( "Can't append child to this dojoWidget. The dojoWidget has not been created.", [thisNode.tagName]);
		
		thisNode = dojoWidget.domNode;
		if (!child)
			throw egl.createRuntimeException( "Can't append a null child.", [thisNode.tagName]);
		if (child == this)
			throw egl.createRuntimeException( "Can't append the widget itself as a child.", [thisNode.tagName]);
		if(this.childTypes){
			this._checkChildType(child);          
		}		
		var childNode = child.eze$$DOMElement;
		if (!childNode)
			throw egl.createRuntimeException( "The child node does not exist.", [thisNode.tagName]);
		try {
			child.eze$$parent = this;
		}
		catch (e) {
			throw egl.createRuntimeException( "Append child error.", [child.getTagName(), egl.inferSignature(this), this]);
		}
		
		// fix scroll view embedded container problem 
		var hasContained = false;

		for( var i = 0; i < thisNode.childNodes.length; ++ i ){
			if( thisNode.childNodes[i].className 
					&& thisNode.childNodes[i].className.indexOf("mblScrollableViewContainer") != -1)
			{
				thisNode.childNodes[i].appendChild( childNode );
				hasContained = true;
				break;
			}
		}
		
		if( hasContained == false )
			thisNode.appendChild( childNode );
		
		this.childrenChanged();
	},
	"setScrollVertically" : function( status ){
		this.scrollVertically = status;
	},
	"getScrollVertically" : function(){
		return this.scrollVertically || false;
	},
	"setScrollHorizontally" : function( status ){
		this.scrollHorizontally = status;
	},
	"getScrollHorizontally" : function(){
		return this.scrollHorizontally || false;
	},
	"setSwappable" : function( status ){
		this.swappable = status;
	},
	"getSwappable" : function(){
		return this.swappable || false;
	},
	"getClassName" : function(){
		return this._class;
	},
	"setClassName" : function( className ){
		this._class = className;
	},
	"getToolBar" : function(){
		return this.toolBar;
	},
	"setToolBar" : function(v){
		this.toolBar = v;
	}
});
