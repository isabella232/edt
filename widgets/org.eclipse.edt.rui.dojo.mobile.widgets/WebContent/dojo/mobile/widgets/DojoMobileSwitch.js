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
	'dojo.mobile.widgets', 'DojoMobileSwitch',
	'dojo.mobile.widgets', 'DojoMobileBase',
	'div',
{
	"constructor" : function() {
		var _this = this;
		require(
			[
			 	"dojo/mobile/utility/Synchronor"
			 ], 
			 function( synchronor ){
				_this.synchronor = synchronor;
				_this.renderWhenDojoIsDoneLoading();
			}
		);	
		_this.value = "off";				
	},
	"createDojoWidget" : function(parent) {
		var eglWidget = this;
		this.dojoWidget = new dojox.mobile.Switch({ 
			value: this.value,
			leftLabel: this.left || "ON",
			rightLabel: this.right || "OFF"
		}, parent);
		
		this.synchronor.trigger( this, "SYN_READY" );
		
		if(this.className){				
			dojo.addClass(this.dojoWidget.domNode,this.className);
		}
		this.dojoWidget.onStateChanged = function(value) {
			eglWidget.handleEvent(eglWidget.getOnChange(), "onChange"); 
		};
	},
	"setValue" : function(value) {
		this.value = value ? "on" : "off";
		if(this.dojoWidget){
			if(this.isValue() == value ) return;
	
			var e = { targetTouches : null};				
			this.dojoWidget.onTouchStart(e);
			this.dojoWidget.onClick(null);

		}

	},
	"getValue" : function() {
		return this.isValue();
	},
	"isValue" : function() {
		var result = this.value;
		if (this.dojoWidget) {
			result = this.dojoWidget.get("value");
		}
		return result=="on" ? true : false;
	},
	"setOnText" : function(label) {
		this.left = label;
		if(this.dojoWidget){
			this.dojoWidget.set("leftLabel",label);
			var divs = this.dojoWidget.domNode.getElementsByTagName("div");
			var textBox;
			for(var n=0;n<divs.length;n++){
				if(divs[n].className == "mblSwitchText mblSwitchTextLeft"){
					textBox = divs[n];
					break;
				}
			}
			if(textBox){
				textBox.innerHTML = this.left;
			}else{

			}		
		}
	},
	"getOnText" : function() {
		return this.left;
	},
	"setOffText" : function(label) {
		this.right = label;
		if(this.dojoWidget){
			this.dojoWidget.set("rightLabel",label);
			var divs = this.dojoWidget.domNode.getElementsByTagName("div");
			var textBox;
			for(var n=0;n<divs.length;n++){
				if(divs[n].className == "mblSwitchText mblSwitchTextRight"){
					textBox = divs[n];
					break;
				}
			}
			if(textBox){
				textBox.innerHTML = this.right;
			}else{

			}		
		}
	},
	"getOffText" : function() {
		return this.right;
	},
	"setID" : function(id) {
		if(id && id!="undefined"){
			this.id = id;
		}else{
			this.id = "";
		}
	},
	"setClass" : function(className) {
		this.className = className;
	}
});
