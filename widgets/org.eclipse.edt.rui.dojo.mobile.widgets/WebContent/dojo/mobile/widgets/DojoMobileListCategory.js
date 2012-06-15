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
	'dojo.mobile.widgets', 'DojoMobileListCategory',
	'dojo.mobile.widgets', 'DojoMobileBase',
	'li',
{
	"constructor" : function(){
		var _this = this;
		this.style = "Rounded Rectangle";
		require(  
			["dojo/mobile/utility/Synchronor"],
			function( synchronor ){
				_this.synchronor = synchronor;
				_this.renderWhenDojoIsDoneLoading();
			}
		);
	},
	"createDojoWidget" : function(parent) {
		var _this = this;
		if( this.style == "Edge to Edge" ){
			this.dojoWidget = new dojox.mobile.EdgeToEdgeCategory({label: this.title },parent);
		}
		else{
			this.dojoWidget = new dojox.mobile.RoundRectCategory({label: this.title },parent);
		}
		
		this.dojoWidget.domNode.style.listStyleType = "none";
		
		_this.synchronor.trigger( _this, "SYN_READY" );
	},
	"setTitle" : function(title) {
		this.title = title;
		if(this.dojoWidget && title){
			this.dojoWidget.set("label", title);
			this.dojoWidget.domNode.innerHTML = title;
		}
	},
	"getTitle" : function() {
		return this.title;
	},
	"setLogicalParent" : function(list) {
		var eglWidget = this;
		this.logicalParent = list;
		if(list.style){
			this.style = list.style;
		}
	},
	"setID" : function(id) {
		if(id && id!="undefined"){
			this.id = id;
		}else{
			this.id = "";
		}
	}
});
