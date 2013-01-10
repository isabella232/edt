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
	'dojo.mobile.widgets', 'DojoMobileCheckBox',
	'dojo.mobile.widgets', 'DojoMobileBase',
	'div',
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
			var inputElement = document.createElement("input");
			_this.dojoWidget = new dojox.mobile.CheckBox(
					{
						checked : (_this.selected ? _this.selected : false),
						onChange: function(){
							_this.handleEvent(_this.getOnChange(), "onChange"); 
						}
					},
					inputElement);
			parent.appendChild(inputElement);
			var text = (_this.text) ? _this.text : "";
			parent.appendChild(document.createTextNode(text));
			
			_this.synchronor.trigger( _this, "SYN_READY" );

			require( ["dojo/on"], function(on){
				on( parent, "click", function(evt){
					if(_this.dojoWidget){
						if(evt.target == _this.dojoWidget.domNode)
							return;
						var checked = _this.dojoWidget.checked;
						_this.dojoWidget.set('checked', !checked);
					}
				});
			});
		},
		
		"setSelected" : function( status ){
			this.selected = status;
			if( this.dojoWidget ){
				this.dojoWidget.set('checked', status);
			}
			if(this.eze$$DOMElement){
				this.eze$$DOMElement.checked = status;
			}
		},
		
		"getSelected" : function(){
			if(this.dojoWidget)
				return this.dojoWidget.get('checked');
			else
				return this.selected || false; 
		},
		
		"setText" : function(text){
			this.text = text;
			if(this.dojoWidget){
				this.dojoWidget.domNode.nextSibling.textContent = text;
			}
		},
		
		"getText" : function(){
			return this.text;
		}
	}	
);