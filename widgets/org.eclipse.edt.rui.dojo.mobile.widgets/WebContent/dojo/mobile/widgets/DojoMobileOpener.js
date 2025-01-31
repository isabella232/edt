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
	'dojo.mobile.widgets', 'DojoMobileOpener',
	'dojo.mobile.widgets', 'DojoMobileContainer',
	'div',
	{ 
		'constructor' : function(){
			var _this = this;
			_this.isModal = false;
			_this.children = [];
			require(
				[ 
				  "dojo/mobile/utility/Synchronor",
				  "dojox/mobile/Opener",
				  "dojo/_base/sniff"
				 ],
				 function( synchronor, op, has ){
					_this.synchronor = synchronor;
					
					/**
					 * @Smyle: bring the render step after the initializing steps
					 */
					setTimeout(
						function() {
							_this.renderWhenDojoIsDoneLoading();
						}, 1
					);
				 }
			);
		},
		'createDojoWidget' : function( parent ){
			var _this = this;
			
			parent.style.minHeight = "2em";
			parent.style.zIndex = "10001";
			
			if( _this.isModal ) 
				_this.dojoWidget = new dojox.mobile.Opener({},parent);
			else
				_this.dojoWidget = new dojox.mobile.Overlay({},parent);
			
			_this.containerWidget = _this.dojoWidget;
				
			_this.synchronor.trigger( _this, "SYN_READY" );
			
			_this.synchronor.wait(
				_this.children, "SYN_READY",
				function(){
					if( _this.children )
						_this.setChildren( _this.children );
					if( _this.dojoWidget.startup )
						_this.dojoWidget.startup();
				}
			);
		},
		'removeChildren' : function( containerWidget ){
			var dojoWidget = this._getContainerWidget( containerWidget );
			this._removeChildren( 1, dojoWidget );
		},
		'showOpener' : function( DojoBaseWidget, position ){
			var _this = this;
			try{
				if( _this.dojoWidget )
					_this.dojoWidget.show( 
						DojoBaseWidget ? ( DojoBaseWidget.domNode
							|| ( DojoBaseWidget.dojoWidget && DojoBaseWidget.dojoWidget.domNode ) 
						) : null, [position] );
			}
			catch( e ){
				console.error( e );
			}
		},
		'hideOpener' : function(){
			var _this = this;
			if( _this.dojoWidget )
				_this.dojoWidget.hide();
		},
		'setIsModal' : function( status ){
			this.isModal = status || false;
		},
		'getIsModal' : function(){
			return this.isModal;
		}
	}
);