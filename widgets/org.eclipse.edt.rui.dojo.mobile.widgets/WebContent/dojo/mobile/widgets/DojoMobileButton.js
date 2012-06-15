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
	'dojo.mobile.widgets', 'DojoMobileButton',
	'dojo.mobile.widgets', 'DojoMobileBase',
	'button',
	{
		"constructor" : function() {
			var _this = this;
			require(
				[
					 "dojo/mobile/utility/Synchronor",
					 "dojox/mobile/Button"
				], 
				function( synchronor ){
					_this.synchronor = synchronor;
					_this.renderWhenDojoIsDoneLoading();
				}
			);			
		},
		"createDojoWidget" : function(parent) {
			var eglWidget = this;
			parent.innerHTML = this.text || "";
			parent.onClick = function(){
				eglWidget.handleEvent(eglWidget.getOnClick(), "onClick");
			}
			this.domNode = parent;
			this.dojoWidget = new dojox.mobile.Button({ },parent);
			this.dojoWidget.domNode.innerHTML = this.text || "";
			this.dojoWidget.onClick = function() {
				eglWidget.handleEvent(eglWidget.getOnClick(), "onClick");
			};
			this.dojoWidget.onDoubleClick = function() {
				eglWidget.handleEvent(eglWidget.getOnDoubleClick(), "onClick");
			};
			
			eglWidget.synchronor.trigger( eglWidget, "SYN_READY" );
		},
		"setText" : function(text) {
			this.text = text;
			if(this.domNode){
				this.domNode.innerHTML = this.text;
			}
		}
	});