egl.defineWidget(
	"dojo.mobile.widgets", "DojoMobileDatePicker",
	"dojo.mobile.widgets", "DojoMobileBase",
	"div",
	{
		"constructor" : function(){
			this.year  = null; // in digit
			this.month = null; // in digit
			this.day   = null; // in digit
			this.isSettingMonth = false;
			this.isSettingYear  = false;
			this.isSettingDay   = false;
			this.isSettingValue = false;
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
			var _this = this;
			var now = new Date();		
			_this.dojoWidget = { "domNode" : parent };
			require(
				["dojo/ready"],
				function(ready){
					ready( 
						function(){
							_this.dojoWidget = new dojox.mobile.SpinWheelDatePicker(
									{}, parent
							);
							
							_this.containerWidget = _this.dojoWidget;
						
							_this.containerWidget.startup();
							
							_this.dojoWidget.connect( 
								_this.dojoWidget.slots[0], 
								"onFlickAnimationEnd",
								function(){
									_this.year = _this.dojoWidget.slots[0].getValue();
									_this.isSettingYear = false;
									_this.isSettingValue = _this.isSettingYear 
									|| _this.isSettingMonth || _this.isSettingDay;
								}
							);
							
							_this.dojoWidget.connect( 
								_this.dojoWidget.slots[1], 
								"onFlickAnimationEnd",
								function(){
									_this.month = _this.dojoWidget.slots[1].getValue();
									_this.isSettingMonth = false;
									_this.isSettingValue = _this.isSettingYear 
									|| _this.isSettingMonth || _this.isSettingDay;
								}
							);
							
							_this.dojoWidget.connect( 
								_this.dojoWidget.slots[2], 
								"onFlickAnimationEnd",
								function(){
									_this.day = _this.dojoWidget.slots[2].getValue();
									_this.isSettingDay = false;
									_this.isSettingValue = _this.isSettingYear 
									|| _this.isSettingMonth || _this.isSettingDay;
								}
							);
							
							_this.synchronor.trigger( _this, "SYN_READY" );
						});
					}
			);
		},
		"setYear" : function( status ){
			var _this = this;
			if( status >= 1970 && status < 2038 ){
				_this.isSettingYear = true;
				_this.year  = status;
				require(
					["dojo/mobile/utility/Synchronor"],
					function( synchronor ){
						synchronor.wait( [_this], "SYN_READY",function(){
							if( _this.dojoWidget )
								_this.dojoWidget.slots[0].setValue(_this.year);
						});
					}
				);
			}
		},
		"setMonth" : function( status ){
			var _this = this;
			if( _this.datelocale && status > 0 && status <= 12 ){
				_this.isSettingMonth = true;
				_this.month = _this.datelocale.format( 
					new Date( _this.year, status-1 ),
					{datePattern:"MMM", selector:"date"}
				);
				require(
					["dojo/mobile/utility/Synchronor"],
					function( synchronor ){
						synchronor.wait( 
							[_this], "SYN_READY",
							function(){
								if( _this.dojoWidget )
									_this.dojoWidget.slots[1].setValue(_this.month);
							}
						);
					}
				);
			}
		},
		"setDay" : function( status ){
			var _this = this;
			if( status >= 1 && status <= 31 ){
				_this.isSettingDay = true;
				_this.day = status;
				require(
					["dojo/mobile/utility/Synchronor"],
					function( synchronor ){
						synchronor.wait( 
							[_this], "SYN_READY",
							function(){
								if( _this.dojoWidget )
									_this.dojoWidget.slots[2].setValue(_this.day);
							}
						);
					}
				);
			}
		},
		"setValue" : function( status ){
			var _this = this;
			if( status instanceof Date ){
				_this.value = status;
				_this.isSettingValue = true;
				_this.setYear ( status.getFullYear() );
				_this.setMonth( status.getMonth()    );
				_this.setDay  ( status.getDate() 	 );
			}
		},
		"getValue" : function(){
			var _this = this;
			if( !_this.isSettingValue ){
				var _dateFormatedString = _this.getYear() + " " + _this.getMonth() + " " + _this.getDay(); 
				if( _this.datelocale )
					_this.value = _this.datelocale.parse( 
						_dateFormatedString,
						{
							selector : "date",
							datePattern : "yyyy MMM d"
						}
					);
			}
			return _this.value;	
		},
		// Since this.year, this.month, this.day, this.value doesn't keep update with 
		// user dragging animation, so they need to be handled carefully seperately
		"getYear" : function(){
			if( this.dojoWidget && !this.isSettingYear )
				return this.dojoWidget.slots[0].getValue(); // Display value
			return this.year; // Coded value 
		},
		"getMonth" : function(){
			if( this.dojoWidget && !this.isSettingMonth )
				return this.dojoWidget.slots[1].getValue(); // Display value
			return this.month; // Coded value 
		},
		"getDay" : function(){
			if( this.dojoWidget && !this.isSettingDay )
				return this.dojoWidget.slots[2].getValue(); // Display value
			return this.day; // Coded value 
		},
		"clear" : function(){
			var _this = this;
			require(
				["dojo/mobile/utility/Synchronor"],
				function( synchronor ){
					synchronor.wait( [_this], "SYN_READY",function(){
						var date = new Date();
						
						if( _this.dojoWidget )
							_this.dojoWidget.reset();
						
						_this.year  = date.getFullYear();
						_this.day   = date.getDay();
						_this.value = date;
						
						if( _this.datelocale )
							_this.month = _this.datelocale.format( 
									date,
									{datePattern:"MMM", selector:"date"}
								);
						else
							_this.month = Date.getMonth();
					});
				}
			);
		}
	}
);