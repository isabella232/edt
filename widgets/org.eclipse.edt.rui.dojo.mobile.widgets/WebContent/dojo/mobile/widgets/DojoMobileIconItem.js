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
egl.defineWidget( 
		'dojo.mobile.widgets', 'DojoMobileIconItem',
		'dojo.mobile.widgets', 'DojoMobileBase',
		'li',
		{ 
			'constructor' : function(){
				var _this = this;
				_this.children = [];
				_this.options  = {};
				_this.options.transition = 'slide';
				_this.options.transitionDir = 1;
				_this.options.icon = 'dojo/mobile/images/i-imagePath-1.png';
				_this.options.lazy = true;
				require(
					["dojox/mobile/IconItem"],
					function(){
						_this.renderWhenDojoIsDoneLoading();
					}
				);
			},
			'createDojoWidget' : function( parent ){
				var _this = this;
				
				function callback( synchronor ){
					_this.dojoWidget = new dojox.mobile.IconItem(
							_this.options, parent
					);
					_this.containerWidget = _this.dojoWidget;
					/** @deprecated
					if( _this.children )
						_this.setChildren( _this.children );
					*/
					var defaultCallBack = _this.dojoWidget.onClick ;
					_this.dojoWidget.onClick = function( value ) {
						if( typeof  defaultCallBack === "function" && _this.moveTo )
							defaultCallBack.apply( _this.dojoWidget, arguments );
						_this.handleEvent( _this.getOnClick(), "onClick" ); 
					};
					
					// work around default click action not being called problem if 
					// no moveTo is specified in dojo mobile framework
					if( !_this.moveTo )
						_this.dojoWidget._onClickHandle = _this.dojoWidget.connect(_this.dojoWidget.domNode, "onclick", "onClick");
					
					synchronor.trigger( _this, 'SYN_READY' );
				}
				
				require(
					["dojo/mobile/utility/Synchronor"],
					function( synchronor ){
						if( _this.options.moveWidget && _this.options.moveWidget.dojoWidget ){
							_this.options.moveTo = _this.options.moveWidget.dojoWidget.id;
							callback(synchronor);
						}
						else if( _this.options.moveWidget )
							synchronor.wait( 
								[_this.options.moveWidget], "SYN_READY",
								function(){
									_this.options.moveTo = _this.options.moveWidget.dojoWidget.id;
									callback(synchronor);
								}
							)
						else
							callback( synchronor );
					}
				);
				
				_this.dojoWidget = { "domNode" : parent };
			},
			'removeChildren' : function( containerWidget ){
				var dojoWidget = this._getContainerWidget( containerWidget );
				this._removeChildren( 1, dojoWidget );
			},
			'setImagePath' : function( icon ){
				this.options.icon = icon;
			},
			'setTargetView' : function( view ){
				if( view && view.dojoWidget ){
					this.options.moveTo = view.dojoWidget.id;
				}
				else
					this.options.moveWidget = view;
			},
			'getImagePath' : function(){
				return this.options.icon;
			},
			'getTargetView' : function(){
				return this.options.moveTo;
			},
			'setUrlString' : function( urlStr ){
				this.options.url = urlStr;
			},
			'getUrlString' : function(){
				return this.options.url;
			},
			'setLazy' : function( lazy ){
				this.options.lazy = lazy;
			},
			'getLazy' : function(){
				return this.options.lazy;
			},
			'setText' : function( str ){
				this.options.label = str;
			},
			'getText' : function(){
				return this.options.label
			}
		}
);