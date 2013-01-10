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
	'dojo.mobile.widgets', 'DojoMobileList',
	'dojo.mobile.widgets', 'DojoMobileContainer',
	'ul',
{
	"constructor" : function() {
		var _this = this;
		_this.style = "Rounded Rectangle";
		_this.acceptChildrenTypes = {
			"dojo.mobile.widgets.DojoMobileListCategory" : true, 
			"dojo.mobile.widgets.DojoMobileListItem" : true
		};
		require( 
			[
			 	"dojo/mobile/utility/Synchronor"
			],
			function( synchronor ){
				_this.synchronor = synchronor;
				_this.renderWhenDojoIsDoneLoading();
			} 
		);
	},
	"createDojoWidget" : function(parent) {
		this.eglParent = parent;
		if(this.style){
			this._createDojoWidget( parent );
		}
	},
	"_createDojoWidget" : function( parent ) {
		var _this = this;
		var parent = _this.eglParent;
		
		if(!_this.style){
			return;
		}
		
		_this.dojoWidget = {
			"domNode" : parent	
		};
		if(_this.style == "Edge to Edge"){
			_this.dojoWidget = new dojox.mobile.EdgeToEdgeList({ },parent);
		}else if(_this.style == "Rounded Rectangle"){
			_this.dojoWidget = new dojox.mobile.RoundRectList({ },parent);
			if(typeof(window.eze$$device) == "string" && window.eze$$device == "Android"){
				_this.dojoWidget.domNode.style.backgroundColor = "transparent";
			}			
		}else{
			throw _this.eze$$typename+".construction: Cannot create Dojo Mobile List with this style " + _this.style;
		}
		_this.containerWidget = _this.dojoWidget;
		
		_this.synchronor.trigger( _this, "SYN_READY" );
		_this.synchronor.wait(
			_this.children,
			"SYN_READY",
			function(){
				_this.setChildren( _this.children, _this.containerWidget );
			}
		);
	},
	'setChildren' : function( children, containerWidget ){
		var _this = this;
		egl.dojo.mobile.widgets.DojoMobileContainer.prototype.setChildren.call(
				this, children, containerWidget
		);
		if( _this.dojoWidget && _this.children && _this.children.length > 0 )
			require(
				["dojo/ready"],
				function( ready ){
					ready( 
						function()
						{
							for( var i = 0; i < _this.children.length; ++ i )
								if( _this.children[i].dojoWidget && _this.children[i].dojoWidget.startup )
									_this.children[i].dojoWidget.startup();					
						}
					);
				}
			);
	},
	"setStyle" : function(style) {
		if(this.dojoWidget){
			if(this.style != style){
				throw this.eze$$typename+".setStyle: Cannot change style after construction";
			}
		}else{			
			this.style = style;
		}		
	},
	"getStyle" : function() {
		return this.style;
	},
	"_appendChild" : function(child) {	
		egl.dojo.mobile.widgets.DojoMobileContainer.prototype._appendChild.call(this, child);	
		child.setLogicalParent(this);
	},	
	"setID" : function(id) {
		if(id && id!="undefined"){
			this.id = id;
		}else{
			this.id = "";
		}
	}
	
});
