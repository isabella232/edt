egl.defineWidget( 
		'dojo.mobile.widgets', 'DojoMobileIconItem',
		'dojo.mobile.widgets', 'DojoMobileContainer',
		'li',
		{ 
			'constructor' : function(){
				var _this = this;
				_this.children = [];
				_this.options  = {};
				_this.options.transition = 'slide';
				_this.options.transitionDir = 1;
				_this.options.icon = 'dojo/mobile/images/i-icon-1.png';
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
				
				if( _this.options.moveWidget && _this.options.moveWidget.dojoWidget )
					_this.options.moveTo = _this.options.moveWidget.dojoWidget.id;
				
				_this.dojoWidget = new dojox.mobile.IconItem(
						_this.options, parent
				);
				
				_this.containerWidget = _this.dojoWidget;
				
				if( _this.children )
					_this.setChildren( _this.children );
				
				require(
					["dojo/mobile/utility/Synchronor"],
					function( synchronor ){
						synchronor.trigger( _this, 'SYN_READY' );
					}
				);
			},
			'removeChildren' : function( containerWidget ){
				var dojoWidget = this._getContainerWidget( containerWidget );
				this._removeChildren( 1, dojoWidget );
			},
			'setIcon' : function( icon ){
				this.options.icon = icon;
			},
			'setTargetView' : function( view ){
				if( view && view.dojoWidget ){
					this.options.moveTo = view.dojoWidget.id;
				}
				else
					this.options.moveWidget = view;
			},
			'getIcon' : function(){
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
			'setLabelString' : function( str ){
				this.options.label = str;
			},
			'getLabelString' : function(){
				return this.options.label
			}
		}
);