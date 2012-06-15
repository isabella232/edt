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
	'dojo.mobile.widgets', 'DojoMobileBase',
	'dojo.widgets', 'DojoBase',
	'div',
{
	"renderWhenDojoIsDoneLoading" : function() {
		var eglWidget = this;		
		require(["dojo", "dojox/mobile", "dojox/mobile/deviceTheme"], function(){
			eglWidget.renderingStep = 0;
			eglWidget.renderWhenDojoIsDoneLoadingSafely();
		});		
	},
	"appendChild" : function(child) {
		throw eze$$typename + ": appendChild is not implemented on this dojo widget";
	},
	"appendChildren" : function(children) {
		throw eze$$typename + ": appendChildren is not implemented on this dojo widget";		
	},
	"setChildren" : function(children) {	
		throw eze$$typename + ": setChildren is not implemented on this dojo widget";
	},
	"getChildren" : function() {

	},	
	"removeChild" : function(child) {
		throw eze$$typename + ": removeChild is not implemented on this dojo widget";		
	},
	"removeChildren" : function() {
		throw eze$$typename + ": removeChildren is not implemented on this dojo widget";
	}	
});
