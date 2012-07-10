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
	'dojo.mobile.widgets', 'DojoMobileProgress',
	'dojo.mobile.widgets', 'DojoMobileBase',
	'div',
{
	"constructor" : function(){
		var _this = this;
		this.intervalTime = 0;
		require( 
			["dojo/mobile/utility/Synchronor"],
			function( synchronor ){
				_this.synchronor = synchronor;
				_this.renderWhenDojoIsDoneLoading();
			}
		);
	},
	"createDojoWidget" : function(parent) {
		this.dojoWidget = dojox.mobile.ProgressIndicator.getInstance();
		this.synchronor.trigger( this, "SYN_READY" );
		
		if(this.show || this.intervalTime > 0 )
			this.showProgress();
		if(this.hide)
			this.hideProgress();
	},
	"showProgress" : function(interval) {
		this.show = true;
		if(interval && interval > 0){
			this.intervalTime = interval;
			if(this.dojoWidget)
				this.dojoWidget.interval = interval;
		}
			
		if(this.dojoWidget){
			document.body.appendChild(this.dojoWidget.domNode);
			this.dojoWidget.start();
		}			
	},
	"hideProgress" : function() {
		this.hide = true;
		if(this.dojoWidget){
			this.dojoWidget.stop();
		}		
	},
	/**
	 * @param imagePath string, the url of the progress image animation file
	 */
	"setImage": function(imagePath){
		//TODO match a pattern
		//Sets an indicator icon image file (typically animated GIF).
		if(this.dojoWidget && imagePath)
			this.dojoWidget.setImage(imagePath);
	},
	/**
	 * @param newValue int, if newValue is negative, it will be reset to 0 instead
	 */
	"setUpdateInterval" : function( newValue ){
		var _this = this;
		if( newValue < 0 ) newValue = 0;
		_this.intervalTime = newValue;
		if( _this.dojoWidget )
			_this.dojoWidget.interval = _this.intervalTime;
	},
	"getUpdateInterval" : function(){
		return this.intervalTime ;
	}
});
