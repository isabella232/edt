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
		'dojo.mobile.widgets', 'DojoMobileIconContainer',
		'dojo.mobile.widgets', 'DojoMobileContainer',
		'div',
		{ 
			'constructor' : function(){
				var _this = this;
				this.children = [];
				this.acceptChildrenTypes = {
					"dojo.mobile.widgets.DojoMobileIconItem" : true	
				};
				require(
					["dojox/mobile/IconContainer"],
					function( IconContainer ){
						_this.renderWhenDojoIsDoneLoading();
					}
				);
			},
			'createDojoWidget' : function(parent){
				var _this = this;
				_this.dojoWidget = new dojox.mobile.IconContainer(
						{},
						parent
				);
				_this.containerWidget = _this.dojoWidget;
				
				require(
					["dojo/mobile/utility/Synchronor"],
					function( synchronor ){
						synchronor.trigger( _this, "SYN_READY" );
						synchronor.wait(
							_this.children, 'SYN_READY',
							function(){
								if( _this.children )
									_this.setChildren( _this.children, _this.containerWidget, true );
								
								_this.dojoWidget && _this.dojoWidget.startup();
								
								for( var i = 0; i < _this.children.length; ++ i )
									if( _this.children[i].dojoWidget && _this.children[i].dojoWidget.startup )
										_this.children[i].dojoWidget.startup();
								
								_this._fixTerminator();
							}
						);
					}
				);
			},
			
			// fix mblIconItemTerminator & mblIconItem location in icon container
			'_fixTerminator' : function(){
				var _this = this, terminatorElement = null, lastIconItemElement = null;
				if( !_this.dojoWidget ) return;
				
				for( var i = 0; i < _this.dojoWidget.domNode.childNodes.length; ++ i ){
					if( _this.dojoWidget.domNode.childNodes[i].className.indexOf('mblIconItemTerminator')
							!= -1 )
						terminatorElement = _this.dojoWidget.domNode.childNodes[i];
					
					else if( terminatorElement &&
						_this.dojoWidget.domNode.childNodes[i].className.indexOf('mblIconItem') != -1 )						
						lastIconItemElement = i + 1;
				}
				
				if( terminatorElement && lastIconItemElement ){
					
					_this.dojoWidget.domNode.removeChild( terminatorElement );
					-- lastIconItemElement;
					if( lastIconItemElement != _this.dojoWidget.domNode.childNodes.length )
						document.insertBefore( 
								terminatorElement, 
								_this.dojoWidget.domNode.childNodes[lastIconItemElement] 
						);
					else if( lastIconItemElement == _this.dojoWidget.domNode.childNodes.length )
						_this.dojoWidget.domNode.appendChild( terminatorElement );
				}
			},
			'setChildren' : function( children, containerWidget, notfix ){
				egl.dojo.mobile.widgets.DojoMobileContainer.prototype.setChildren.call(
						this, children, containerWidget
				);
				if( notfix )
					this._fixTerminator();
			},
			'removeChildren' : function( containerWidget ){
				var dojoWidget = this._getContainerWidget( containerWidget );
				this._removeChildren( 1, dojoWidget );
			}
		}
);