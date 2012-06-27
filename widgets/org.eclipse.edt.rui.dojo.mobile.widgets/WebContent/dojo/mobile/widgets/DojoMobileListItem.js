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
	'dojo.mobile.widgets', 'DojoMobileListItem',
	'dojo.mobile.widgets', 'DojoMobileContainer',
	'li',
{
	"constructor" : function(){
		var _eglWidget = this;
		_eglWidget.children = [];
		_eglWidget.text = "";
		require( 
			[
			 	"dojo/mobile/utility/Synchronor",
			 	"dojox/mobile/ListItem",
			 	"dojo/on", 
			 	"dojo/_base/sniff",
			 	"dojo/_base/lang",
			 	"dojo/dom-class",
			 	"dojo/dom-construct",
			 	"dojox/mobile/common"
			 ],
			function( synchronor, li, on, has, lang, domClass, domConstruct, common ){
				_eglWidget.synchronor = synchronor;
				
				/** 
				 * @Smyle: This is a fix to dojox.mobile.ListItem's bug where icon should be 
				 * relocated after it has loaded, otherwise its offset will be miscalculated.
				*/
				if( !li.extendedStartup ){
					li.extend(
						{
							"startup" : function(){
								var _this = this;
								
								if(_this._started){ return; }
								_this.inheritParams();
							
								var parent = _this.getParent();
								if(_this.moveTo || _this.href || _this.url || _this.clickable || (parent && parent.select)){
									_this._onClickHandle = _this.connect(_this.anchorNode, "onclick", "onClick");
								}
								
								_this.setArrow();
	
								if(domClass.contains(_this.domNode, "mblVariableHeight")){
									_this.variableHeight = true;
								}
								
								if(_this.variableHeight){
									domClass.add(_this.domNode, "mblVariableHeight");
								}
								
								_this.set("icon", _this.icon); // _setIconAttr may be called twice but this is necessary for offline instantiation
								if(!_this.checked && _this.checkClass.indexOf(',') !== -1){
									_this.set("checked", _this.checked);
								}
								
								_this.inherited(arguments);
								
								var imgNode = null;
								if( _this.iconNode )
									imgNode = _this.iconNode.getElementsByTagName("IMG")[0];
								if ( imgNode )
									on(
										imgNode,
										"load",
										function() {
											_this.layoutVariableHeight();
										}
									);
								
								// 45 is the default height of dojo mobile list item widget
								if( (_this.domNode.offsetHeight > 45) && !dojo.hasClass(this.domNode,"mblVariableHeight") ){
									dojo.addClass(this.domNode,"mblVariableHeight");
								}
							},
							_setIconAttr: function(icon){
								// if(!this.getParent()){ return; } // icon may be invalid because inheritParams is not called yet
								this.icon = icon;
								var a = this.anchorNode;
								if(!this.iconNode){
									if(icon){
										var ref = this.rightIconNode || this.rightIcon2Node || this.rightTextNode || this.box;
										this.iconNode = domConstruct.create("DIV", {className:"mblListItemIcon"}, ref, "before");
									}
								}else{
									domConstruct.empty(this.iconNode);
								}
								if(icon && icon !== "none"){
									common.createIcon(icon, this.iconPos, null, this.alt, this.iconNode);
									if(this.iconPos){
										domClass.add(this.iconNode.firstChild, "mblListItemSpriteIcon");
									}
									domClass.remove(a, "mblListItemAnchorNoIcon");
								}else{
									domClass.add(a, "mblListItemAnchorNoIcon");
								}
							}
						}
					);
					li.extendedStartup = true;
				}
				_eglWidget.renderWhenDojoIsDoneLoading();
			}
		);
	},
	"createDojoWidget" : function(parent) {
		var _this = this;
		_this.dom = parent;	
		_this.dom.style.minHeight = "44px";
		_this.dojoWidget = new dojox.mobile.ListItem({
			label: _this.text,
			rightText: _this.actionText || "",
			moveTo: _this.moveTo,
			icon : _this.icon,
			transition: _this.transition || "slide"
		},parent);
		
		_this.synchronor.trigger( _this, "SYN_READY" );
		
		_this.textBox = _this._getTextBox();
		_this.setChildren( _this.children, _this.dojoWidget );
		
		if(_this.children.length > 0){
			_this._setIconStyle(true);
		}
		
		var defaultCallBack = _this.dojoWidget.onClick ;
		_this.dojoWidget.onClick = function( value ) {
			if( typeof  defaultCallBack === "function" && _this.moveTo )
				defaultCallBack.apply( _this.dojoWidget, arguments );
			_this.handleEvent( _this.getOnClick(), "onClick" ); 
		};
		
		// work around default click action not being called problem if 
		// no moveTo is specified in dojo mobile framework
		if( !_this.moveTo )
			_this.dojoWidget._onClickHandle = _this.dojoWidget.connect(_this.dojoWidget.anchorNode, "onclick", "onClick");
	},
	"_setIconStyle" : function(hasChildren){		
		var iconDom = this.dojoWidget.domNode.firstChild;
		if( iconDom && iconDom.className == "mblListItemIcon" ){
			if(hasChildren){
				iconDom.style.marginLeft = 0;
				iconDom.style.marginTop = 0;
			}else{
				iconDom.style.marginLeft = "18px";
				iconDom.style.marginTop = "7px";
			}			
		}
	},
	"_getTextBox" : function() {
		if (this.dojoWidget) {
			var divs = this.dojoWidget.domNode.getElementsByTagName("div");
			var textBox;
			for(var n=0;n<divs.length;n++){
				if(divs[n].className == "mblListItemTextBox"){
					textBox = divs[n];
					break;
				}
			}
			if(textBox){
				textBox.innerHTML = "";
			}else{
				throw this.eze$$typename+".setChildren: Cannot find the parent widget";
			}
			return textBox;
		}
	},
	"_appendChild" : function(child) {
		if (this.dojoWidget) {
			var textBox = this.textBox;
			
			/* Depreciated*/
			if(!dojo.hasClass(this.dojoWidget.domNode,"mblVariableHeight")){
				dojo.addClass(this.dojoWidget.domNode,"mblVariableHeight");
				//this._setIconStyle(true);
				//textBox.innerHTML = "";
			}
			
			textBox.appendChild(child.eze$$DOMElement);					
		}
	},
	"_removeChild" : function(child) { 
		var textBox = this.textBox;
		textBox.removeChild(child.eze$$DOMElement);
		if(this.children || this.children.length <= 0){
			this._showText();
		}
	},
	"_showText" : function(){
		if (this.dojoWidget) {
			var textBox = this.textBox;
			if(dojo.hasClass(this.dojoWidget.domNode,"mblVariableHeight")){
				dojo.removeClass(this.dojoWidget.domNode,"mblVariableHeight");
				this._setIconStyle(false);
			}			
			textBox.innerHTML = this.text;	
		}		
	},
	"removeChildren" : function() {
		if(!this.children){
			return;
		}		
		if( this.dojoWidget && this.textBox.childNodes.length > 1){
			for (var n=0; n< this.children.length; n++) {
				this._removeChild(this.children[n]);
			}			
		}
		this.children = [];
		this._showText();
	},
	
	"setText" : function(text) {
		this.text = text;
		if(this.dojoWidget && this.children.length == 0){
			this.dojoWidget.set("label", this.text);
			var divs = this.dojoWidget.domNode.getElementsByTagName("div");
			var textBox;
			for(var n=0;n<divs.length;n++){
				if(divs[n].className == "mblListItemTextBox"){
					textBox = divs[n];
					break;
				}
			}
			if(textBox){
				textBox.innerHTML = this.text;
			}	
		}
	},
	"getText" : function() {
		return this.text;
	},
	"setImagePath" : function(icon) {
		var _this = this;
		_this.icon = icon;
		require(
			["dojo/mobile/utility/Synchronor"],
			function( synchronor ){
				synchronor.wait( 
					[_this], "SYN_READY",
					function(){
						if( _this.dojoWidget )
							_this.dojoWidget.set( {icon:_this.icon} );
						
						/** @Depreciated
						var iconDom = this.dojoWidget.domNode.firstChild;
						imgNode = this.dojoWidget.domNode.find
						if( iconDom && iconDom.className == "mblListItemIcon" ){
							iconDom.src = this.icon;
						}else{
							iconDom= document.createElement("img");
							iconDom.setAttribute("class", "mblListItemIcon");
							iconDom.src = this.icon;
							this.dojoWidget.domNode.insertBefore(iconDom, this.dojoWidget.domNode.firstChild);
							dojo.removeClass(this.dojoWidget.domNode.getElementsByTagName("a")[0],"mblListItemAnchorNoIcon");
						}*/
					}
				);
			}
		);
		
		
		
	},
	"getImagePath" : function() {
		return this.icon;
	},
	"setActionText" : function(actionText) {
		this.actionText = actionText;
		if(this.dojoWidget){
			this.dojoWidget.set("rightText", actionText);
			var divs = this.dojoWidget.domNode.getElementsByTagName("div");
			var rightTextNode;
			for(var n=0;n<divs.length;n++){
				if(divs[n].className == "mblListItemRightText"){
					rightTextNode = divs[n];
					break;
				}
			}
			if(rightTextNode){
				if(actionText){
					rightTextNode.innerHTML = actionText;
				}else{
					rightTextNode.parentNode.removeChild(rightTextNode);
				}
				
			}else{
				var txt = dojo.create("DIV");
				txt.className = "mblRightText";
				txt.innerHTML = actionText;
				this.dojoWidget.domNode.getElementsByTagName("a")[0].appendChild(txt);
			}			
		}
	},
	"getActionText" : function() {
		return this.actionText;
	},
	"_setMoveto" : function( moveTo ) {		
		if(this.dojoWidget){
			this.dojoWidget.set("moveTo", moveTo);
		}
		else
			this.moveTo = moveTo;
	},
	"_getIconNode" : function(){
		var divs = this.dojoWidget.domNode.getElementsByTagName("div");
		var iconNode;
		for(var n=0;n<divs.length;n++){
			if(divs[n].className == "mblListItemRightIcon"){
				iconNode = divs[n];
				break;
			}
		}
		return iconNode;
	},
	"_setActionView" : function() {
		this.actionView._show();
		this.moveTo = this.actionView.getID();
		if( !this.moveTo ){
			this.actionView._addRef(this);
			this.moveTo = "stub";
		}else{
			this._setMoveto(this.moveTo);
		}
	},
	"setActionView" : function(actionView) {
		var _this = this;
		_this.actionView = actionView;
		if(_this.dojoWidget){
			if( _this.actionView ){
				var iconNode = _this._getIconNode();
				if(!iconNode){
					var rightIconNode = dojo.create("div");
					var arrow = dojo.create("div");
					rightIconNode.className = "mblListItemRightIcon";
					arrow.className = "mblDomButtonArrow mblDomButton";
					var a = _this.dojoWidget.domNode.getElementsByTagName("a")[0];
					
					if( a.childNodes.length > 1 )
						a.insertBefore( rightIconNode, a.childNodes[0] );
					else
						a.appendChild( rightIconNode );
					
					rightIconNode.appendChild(arrow);
					arrow.appendChild( dojo.create("div") );
					
					var eglWidget = _this;
					if(eglWidget.onClick){
						_this.dojoWidget.connect(a, "onclick", eglWidget.onClick);
					}else{
						_this.dojoWidget.connect(a, "onclick", eglWidget.dojoWidget.onClick);
					}
				}
				_this._setActionView();
			}else{
				var iconNode = _this._getIconNode();
				if(iconNode){
					_this.onClick = _this.dojoWidget.onClick;
					_this.dojoWidget.onClick = function(){};
					iconNode.parentNode.removeChild(iconNode);
				}
			}
		}else{
			if( _this.actionView ){
				_this._setActionView();
			}
		}

	},
	"getActionView" : function() {
		return this.actionView;
	},
	"setActionTransition" : function(actionTransition) {
		this.transition = actionTransition;
		if(this.dojoWidget && actionTransition){
			this.dojoWidget.set("transition", actionTransition);
		}
	},
	"getActionTransition" : function() {
		return this.transition;
	},
	"setID" : function(id) {
		if(id && id!="undefined"){
			this.id = id;
		}else{
			this.id = "";
		}
	}
});
