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
				 	"dojox/mobile/SpinWheelDatePicker",
				 	"dojox/mobile/SpinWheelSlot",
				 	"dojo/mobile/utility/DisplayController"
				],
				function( datelocale, synchronor, dp, swl, dc ){
					_this.synchronor = synchronor;
					_this.datelocale = datelocale;
					
					/**
					 * (1) Resolve the problem that date picker's original startup will conflict 
					 * with the EGL widget framework's startup
					 * (2) Resolve the display:none problem dojox.mobile.SpinWheelDatePicker not support
					 */
					if( !swl.extendedStartup ){
						swl.extend(
							{
								"startup" : function(){
									var parentWidget = this.getParent();
									
									this.adjusted = dc.checkOffsetHeightZeroForDisplayNone(
										parentWidget.domNode, this, true
									);
									
									// @Smyle: add display start guarding handler
									if( this.adjusted )
										dc.makeVisiblityHidden( this );
									
									if( parentWidget.centerPos == 0 && parentWidget.domNode.offsetHeight != 0 )
										parentWidget.centerPos = Math.round(parentWidget.domNode.offsetHeight / 2);
									this.centerPos = parentWidget.centerPos;
									this.inherited(arguments);
									var items = this.panelNodes[1].childNodes;
									this._itemHeight = items[0].offsetHeight;
									this.adjust();
									
									// @Smyle: add display end guarding handler
									if( this.adjusted )
										dc.makeDisplayNone( this );
								},
								"setValue": function(newValue){
									// @Smyle: add display start guarding handler
									if( this.adjusted )
										dc.makeVisiblityHidden( this );
									
									var idx0, idx1;
									var curValue = this.getValue();
									if(!curValue){
										this._penddingValue = newValue;
										return;
									}
									this._penddingValue = undefined;
									var n = this.items.length;
									for(var i = 0; i < n; i++){
										if(this.items[i][1] === String(curValue)){
											idx0 = i;
										}
										if(this.items[i][1] === String(newValue)){
											idx1 = i;
										}
										if(idx0 !== undefined && idx1 !== undefined){
											break;
										}
									}
									var d = idx1 - (idx0 || 0);
									var m;
									if(d > 0){
										m = (d < n - d) ? -d : n - d;
									}else{
										m = (-d < n + d) ? -d : -(n + d);
									}
									var to = this.getPos();
									to.y += m * this._itemHeight;
									this.slideTo(to, 1);
									
									// @Smyle: add display end guarding handler
									if( this.adjusted )
										dc.makeDisplayNone( this );
								}
							}
						);
						swl.extendedStartup = true;
					}
					_this.renderWhenDojoIsDoneLoading();
				}
			);
		},
		"createDojoWidget" : function( parent ){
			var _this = this;
			var datelocale = this.datelocale;
			var monthStr = "";
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
						
							require( 
								["dojo/_base/sniff"], 
								function( has ){
									setTimeout(
										function(){
											ready(
												function(){
													_this.synchronor.trigger( _this, "SYN_READY" );
													_this.containerWidget.startup();
													
													monthStr = datelocale.format( now, {datePattern:"MMM", selector:"date"});
													var values = [ now.getFullYear(), monthStr, now.getDate()];
													if( _this.value instanceof Date ){
														monthStr = datelocale.format(_this.value, {datePattern:"MMM", selector:"date"});
														values = [_this.value.getFullYear(), monthStr, _this.value.getDate()];
													}
													if( _this.month ){
														monthStr = _this.datelocale.format( 
																new Date( 2012, _this.month-1 ),
																{ datePattern:"MMM", selector:"date" }
														);
														values[1] = monthStr;
													}
													if( _this.year ) values[0] = _this.year;
													if( _this.day  ) values[2] = _this.day;
													
													_this.dojoWidget.setValue( values );	
													_this.synchronor.trigger( _this, "SYN_STARTUP" );
												}
											);
										}, has("ie") ? 100 : 0);
								}
							);
							
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
						});
					}
			);
		},
		"setYear" : function( status ){
			var _this = this;
			if( status >= 1970 && status < 2038 ){
				_this.isSettingYear = true;
				_this.year  = status;
				if( _this.dojoWidget )
					_this.dojoWidget.slots[0].setValue(_this.year);
			}
		},
		"setMonth" : function( status ){
			var _this = this;
			// for initial use
			_this.month = status;
			
			if( _this.datelocale && status > 0 && status <= 12 ){
				if( _this.dojoWidget ){
					_this.isSettingMonth = true;
					_this.month = _this.datelocale.format( 
						new Date( _this.year, status-1 ),
						{datePattern:"MMM", selector:"date"}
					);
					_this.dojoWidget.slots[1].setValue(_this.month);
				}
			}
		},
		"setDay" : function( status ){
			var _this = this;
			if( status >= 1 && status <= 31 ){
				_this.isSettingDay = true;
				_this.day = status;
				if( _this.dojoWidget )
					_this.dojoWidget.slots[2].setValue(_this.day);
			}
		},
		"setValue" : function( status ){
			var _this = this;
			if( status instanceof Date ){
				_this.value = status;
				if( _this.dojoWidget ){
					_this.isSettingValue = true;
					_this.setYear ( status.getFullYear() );
					_this.setMonth( status.getMonth()    );
					_this.setDay  ( status.getDate() 	 );
				}
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
					synchronor.wait( [_this], "SYN_STARTUP",function(){
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