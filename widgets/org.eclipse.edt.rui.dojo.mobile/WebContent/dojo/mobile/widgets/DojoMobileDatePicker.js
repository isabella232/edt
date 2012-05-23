egl.defineWidget(
	"dojo.mobile.widgets", "DojoMobileDatePicker",
	"dojo.mobile.widgets", "DojoMobileBase",
	"div",
	{
		"constructor" : function(){
			this.year  = null;
			this.month = null;
			this.day   = null;
			this.datelocale = null;
			var _this = this;
			require(
				[
				 	"dojo/date/locale",
				 	"dojo/mobile/utility/Synchronor",
				 	"dojox/mobile/SpinWheelDatePicker"
				 ],
				function( datelocale, synchronor ){
					_this.synchronor = synchronor;
					_this.datelocale = datelocale;
					_this.renderWhenDojoIsDoneLoading();
				}
			);
		},
		"createDojoWidget" : function( parent ){
			var datelocale = this.datelocale;
			var now = new Date();		
			var _this = this;
			_this.dojoWidget = { "domNode" : parent };
			
			window._dp = _this;
			
			require(
				["dojo/ready"],
				function(ready){
					ready( 
						function(){
							_this.dojoWidget = new dojox.mobile.SpinWheelDatePicker(
									{}, parent
							);
							
							_this.containerWidget = _this.dojoWidget;
							
							_this.synchronor.trigger( _this, "SYN_READY" );
							
							_this.containerWidget.startup();
					});
				}
			);
		},
		"setYear" : function( status ){
			if( status >= 1970 && status < 2038 ){
				this.year  = status;
				if( this.dojoWidget )
					this.dojoWidget.slots[0].setValue(this.year);
			}
		},
		"setMonth" : function( status ){
			if( this.datelocale && status > 0 && status <= 12 ){
				this.month = this.datelocale.format( 
						new Date( this.year, status-1 ), 
						{datePattern:"MMM", selector:"date"} );
				if( this.dojoWidget )
					this.dojoWidget.slots[1].setValue(this.month);
			}
		},
		"setDay" : function( status ){
			if( status >= 1 && status <= 31 ){
				this.day   = status;
				if( this.dojoWidget )
					this.dojoWidget.slots[2].setValue(this.day);
			}
		},
		"getYear" : function(){
			this.year = this.dojoWidget.slots[0].getValue();
			return this.year;
		},
		"getMonth" : function(){
			this.month = this.dojoWidget.slots[1].getValue();
			return this.month;
		},
		"getDay" : function(){
			this.day = this.dojoWidget.slots[2].getValue();
			return this.day;
		}
	}
);