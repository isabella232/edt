/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
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
	"constructor" : function() {
		var _this = this;
		_this.children = [];
		_this.text = "";
		require( 
			["dojo/mobile/utility/Synchronor"],
			function( synchronor ){
				_this.synchronor = synchronor;
				_this.renderWhenDojoIsDoneLoading();
			}
		);
	},
	"createDojoWidget" : function(parent) {
		var _this = this;
		_this.dom = parent;	
		_this.dojoWidget = new dojox.mobile.ListItem({
			label: "",
			rightText: _this.actionText || "",
			moveTo: _this.moveTo,
			transition: _this.transition || "slide"
		},parent);

		_this.synchronor.trigger( _this, "SYN_READY" );
		
		if(_this.children.length > 0){
			dojo.addClass(_this.dojoWidget.domNode,"mblVariableHeight");
			_this._setIconStyle(true);
		}
		var defaultCallBack = _this.dojoWidget.onClick ;
		_this.dojoWidget.onClick = function( value ) {
			if( typeof  defaultCallBack === "function" )
				defaultCallBack.apply( _this.dojoWidget, arguments );
			_this.handleEvent( _this.getOnClick(), "onClick" ); 
		};
		
		_this.dojoWidget.startup();
		_this.textBox = _this._getTextBox();
		
		if( _this.icon )
			_this.setIcon( _this.icon );
		
		if(_this.text){
			_this.setText(_this.text);
		}
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
			if(!dojo.hasClass(this.dojoWidget.domNode,"mblVariableHeight")){				
				dojo.addClass(this.dojoWidget.domNode,"mblVariableHeight");
				this._setIconStyle(true);
				textBox.innerHTML = "";
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
			}else{

			}		
		}
	},
	"getText" : function() {
		return this.text;
	},
	"setIcon" : function(icon) {
		this.icon = icon;
		if(this.dojoWidget){
			this.dojoWidget.set("icon", this.icon);		
			var iconDom = this.dojoWidget.domNode.firstChild;
			if( iconDom && iconDom.className == "mblListItemIcon" ){
				iconDom.src = this.icon;
			}else{
				iconDom= document.createElement("img");
				iconDom.setAttribute("class", "mblListItemIcon");
				iconDom.src = this.icon;
				this.dojoWidget.domNode.insertBefore(iconDom, this.dojoWidget.domNode.firstChild);
				dojo.removeClass(this.dojoWidget.domNode.getElementsByTagName("a")[0],"mblListItemAnchorNoIcon");
			}
		}
	},
	"getIcon" : function() {
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
