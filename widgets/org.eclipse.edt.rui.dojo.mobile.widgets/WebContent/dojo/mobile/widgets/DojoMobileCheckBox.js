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
	'dojo.mobile.widgets', 'DojoMobileCheckBox',
	'dojo.mobile.widgets', 'DojoMobileBase',
	'input type=checkbox',
	{
		"constructor" : function(){
			var _this = this;
			require(
					[
					 	"dojo/mobile/utility/Synchronor",
					 	"dojox/mobile/CheckBox"
					 ],
					function( synchronor ){
						_this.synchronor = synchronor;
						_this.renderWhenDojoIsDoneLoading();
					}
			);
		},
		"createDojoWidget" : function(parent){
			var _this = this;
			_this.dojoWidget = new dojox.mobile.CheckBox(
					{
						checked : (_this.checked ? _this.checked : false),
						onChange: function(){
							_this.handleEvent(_this.getOnChange(), "onChange"); 
						}
					},
					parent);
			_this.synchronor.trigger( _this, "SYN_READY" );
		},
		
		"setChecked" : function( status ){
			this.checked = status;
			if( this.dojoWidget ){
				this.dojoWidget.set('checked', status);
			}
			if(this.eze$$DOMElement){
				this.eze$$DOMElement.checked = status;
			}
		},
		
		"getChecked" : function(){
			if(this.dojoWidget)
				return this.dojoWidget.get('checked');
			else
				return this.checked || false; 
		}
	}	
);