/*******************************************************************************
 * Copyright � 2011 IBM Corporation and others.
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
	'dojo.widgets', 'DojoTimeTextBox',
	'dojo.widgets', 'DojoValidationBase',
	'div',
{
	"constructor" : function() {
		this.timePattern = "hh:mm a";
		this.renderWhenDojoIsDoneLoading(["dijit/form/TimeTextBox"]);
	},
	"createDojoWidget" : function(parent) {
		var eglWidget = this;
		this._mergeArgs({
			id : this.id || "egl.DojoTimeTextBox" + (++egl._dojoSerial),
			type : "text", 
			selector : 'time'
		});
		this._setCommonProp();
		this.dojoWidget = new dijit.form.TimeTextBox( this._args, parent);
		this.dojoWidget.constraints.visibleIncrement = this.visibleIncrement || "T01:00";
		this.dojoWidget.constraints.timePattern = this.timePattern;
		this.dojoWidget.constraints.clickableIncrement = this.clickableIncrement || "T00:15";
		this.dojoWidget.constraints.visibleRange = this.visibleRange || "T03:00";
		this.setValidators(this.validators);
		this._setTextboxStyle();
		this.dojoWidget.startup();
		
		if (this.time) 
			this.setValue(this.time);
	},
	"setID" : function(id) {
		if( id == null ) return;
		egl.dojo.widgets.DojoValidationBase.prototype.setID.call(this, id);
		if(!(egl.IE && egl.IEVersion < 8) && this.dojoWidget){
			this.dojoWidget.domNode.setAttribute("widgetid",id);
		}		
	},
	"setValue" : function( time ){
		this.time = time;
		if (this.dojoWidget){
			this.dojoWidget.set('displayedValue',this.convertEGLTimeToDojoTime(time));	
		}
	},
	"convertEGLTimeToDojoTime" : function(eglTime) {
		return eglTime == null? "" : egl.eglx.lang.StringLib.formatTime(eglTime, this.timePattern);
	},
	"getValue" : function(){
        if (this.dojoWidget) {            
            if ( this.dojoWidget.getValue() != null ) {
                this.time = this.dojoWidget.getValue();
            }
            if(this.time == null)
            	return null;
            var time = this.time;            
            try {
                var s = "" + this.twoDigits(time.getHours()) + ":" +
                    this.twoDigits(time.getMinutes()) + ":" +
                    this.twoDigits(time.getSeconds());
               	return egl.eglx.lang.ETime.fromEString(s);
            }
            catch (e) {
                egl.printError("Cannot convert time "+time, e);
            }
        }
        else
            return this.time || new Date();
    },
    "setText" : function( time ){
    	this.setValueAsText(time);
  	},
  	"getText" : function(){
  		return(this.getValueAsText());
  	},
    "twoDigits" : function(value) {
    	value = parseInt(value);
    	return "" + (value < 10 ? "0" : "") + value;			
    },
    "setTimePattern" : function( pattern ){
        this.timePattern = pattern;
        if (this.dojoWidget) {
            this.dojoWidget.constraints.timePattern = pattern;
            this.dojoWidget.set('displayedValue',this.convertEGLTimeToDojoTime(this.time));
        }
    },	
	"getTimePattern" : function(){
		return this.timePattern;
	},
	"setClickableIncrement" : function( increment ){
		this.clickableIncrement = increment;
		if (this.dojoWidget)
			this.dojoWidget.constraints.clickableIncrement = increment;
	},
	"getClickableIncrement" : function(){
		return this.clickableIncrement;
	},
	"setVisibleIncrement" : function( increment ){
		this.visibleIncrement = increment;
		if (this.dojoWidget)
			this.dojoWidget.constraints.visibleIncrement = increment;
	},
	"getVisibleIncrement" : function(){
		return this.visibleIncrement;
	},
	"setVisibleRange" : function( range ){
		this.visibleRange = range;
		if (this.dojoWidget)
			this.dojoWidget.constraints.visibleRange = range;
	},
	"getVisibleRange" : function(){
		return this.visibleRange;
	},
	"getValueAsText" : function(){
		if (this.dojoWidget) {
			if(this.getValue())
				return(egl.eglx.lang.StringLib.formatTime(this.getValue()));
			else
				return "";
        }
        else
            return this.time || new Date();
	},
	"setValueAsText" : function(time){
		if(time == "")
			this.setValue(null);
		else{
			var timeValue;
			try{
				timeValue = egl.eglx.lang.ETime.fromEString(time);
			}catch(e){
				if(e instanceof egl.eglx.lang.TypeCastException){
					try{
						timeValue=egl.eglx.lang.ETime.fromETimestamp(egl.eglx.lang.ETimestamp.fromEString(time), "yyyyMMddhhmmss");
					}
					catch(e){
						if(e instanceof egl.eglx.lang.TypeCastException){
							try{
								timeValue=egl.eglx.lang.ETime.fromETimestamp(egl.eglx.lang.ETimestamp.fromEString(time), "hhmmss");
							}catch(e){
								if(e instanceof egl.eglx.lang.TypeCastException){
									throw egl.createTypeCastException( "CRRUI2017E", [ time, "string", "time" ], "time", "string" );
								}else{
									throw e;
								}
							}
						}else{
							throw e;
						}
					}	
				}else{
					throw e;
				}
			}
			this.setValue(timeValue);
		}
			
	}
});