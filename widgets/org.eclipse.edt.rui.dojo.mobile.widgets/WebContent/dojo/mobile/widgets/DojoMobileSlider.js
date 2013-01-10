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
	'dojo.mobile.widgets', 'DojoMobileSlider',
	'dojo.mobile.widgets', 'DojoMobileBase',
	'div',
{	
		"constructor" : function() {
			var _this = this;
			require(
				[
				 	"dojo/mobile/utility/Synchronor",
				 	"dojox/mobile/Slider"
				 ], 
				 function( synchronor ){
					_this.synchronor = synchronor;
					_this.renderWhenDojoIsDoneLoading();
				}
			);			
		},
		"createDojoWidget" : function(parent) {
			var eglWidget = this;
			this._mergeArgs({
				type: "range",
				value: this.value ? this.value : 0,
				max: this.maximum ? this.maximum : 100,
				min: this.minimum ? this.minimum : 0,
				step: this.step ? this.step : 1,
				orientation: this.orientation ? this.orientation : "auto"
			});
			this.dojoWidget = new dojox.mobile.Slider(this._args, parent);
			this.dojoWidget.onChange = function(){
				eglWidget.handleEvent(eglWidget.getOnChange(), "onChange"); 
			};
			eglWidget.synchronor.trigger( eglWidget, "SYN_READY" );
		},
		
		"getValue": function(){
			if(this.dojoWidget)
				return this.dojoWidget.value;
			else
				return this.value;
		},
		
		"setValue": function(value){
			if(value > this.maximum)
				this.value = this.maximum;
			else if(value < this.minimum)
				this.value = this.minimum;
			else
				this.value = value;
			
			if(this.dojoWidget){
				this.dojoWidget.set(
					{ "value" : this.value }
				);
			}
		},
		
		"setOrientation": function(orien){
			this.orientation = orien;
			if(this.dojoWidget)
				this.dojoWidget.orientation = orien;
		},
		
		"getOrientation": function(){
			if(this.dojoWidget)
				return this.dojoWidget.orientation;
			else
				return this.orientation;
		}
});