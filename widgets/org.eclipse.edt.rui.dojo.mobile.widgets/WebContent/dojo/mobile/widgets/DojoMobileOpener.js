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
				  "dojox/mobile/Opener"
				 ],
				 function( synchronor ){
					_this.synchronor = synchronor;
					_this.renderWhenDojoIsDoneLoading();
				 }
			);
		},
		'createDojoWidget' : function( parent ){
			var _this = this;
			
			parent.style.minHeight = "2em";
			
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