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
	'dojo.mobile.widgets', 'DojoMobileTextArea',
	'dojo.mobile.widgets', 'DojoMobileBase',
	'textarea',
	{ 
		'constructor' : function(){
			var _this = this;
			require(
					[
					  "dojo/mobile/utility/Synchronor", 
			          "dojox/mobile/ExpandingTextArea",
			          "dojo/_base/sniff"
					],
				 function( synchronor, eta, has ){
					_this.synchronor = synchronor;
					
					/**
					 * @Smyle: bring the render step after the initializing steps
					 */
					setTimeout(
						function() {
							_this.renderWhenDojoIsDoneLoading();
						}, 1
					);
				 }
			);
		},
		'createDojoWidget' : function( parent ){
			var _this = this;
			parent.innerHTML = _this.text || "";
			_this.domNode = parent;
			_this._args = {
				value : _this.text || "",
				readOnly : (_this.readOnly ? _this.readOnly : false),
				maxLength : (_this.maxLength ? _this.maxLength : -1),
				placeHolder : (_this.placeHolder ? _this.placeHolder : "") 
			};
			if( _this.isExpandable ) 
				_this.dojoWidget = new dojox.mobile.ExpandingTextArea(_this._args, parent);
			else
				_this.dojoWidget = new dojox.mobile.TextArea(_this._args, parent);
			
			var cols = (_this.cols > 0) ? _this.cols : 20;
			var rows = (_this.rows > 0) ? _this.rows : 2;
			
			_this.dojoWidget.domNode.innerHTML = _this.text || "";
			_this.dojoWidget.domNode.cols = cols;
			_this.dojoWidget.domNode.rows = rows;
			
			_this.dojoWidget.onChange = function(e){
				_this.handleEvent(_this.getOnChange(), "onChange"); 
			};
			
			_this.synchronor.trigger( _this, "SYN_READY" );
		},
		"setText" : function(text){
			this.text = text;
			if(this.dojoWidget){
				this.dojoWidget.domNode.value = text;
			}
		},
		"getText" : function(){
			if(this.dojoWidget){
				return this.dojoWidget.domNode.value;
			}
			return this.text;
		},
		"setCols" : function(cols){
			if(this.dojoWidget){
				this.domNode.cols = cols;
			}
			this.cols = cols;
		},
		
		"setRows" : function(rows){
			this.rows = rows;
			if(this.dojoWidget){
				this.domNode.rows = rows;
			}
		},
		"setIsExpandable" : function( status ){
			this.isExpandable = status || true;
		},
		"getIsExpandable" : function(){
			return this.isExpandable;
		},
		
		"setReadOnly" : function(readOnly){
			this.readOnly = readOnly;
			if(this.dojoWidget){
				this.dojoWidget.readOnly = readOnly;
			}
		},
		"getReadOnly" : function(){
			if(this.dojoWidget){
				return this.dojoWidget.readOnly;
			}else{
				return this.readOnly;
			}
		},
		"setMaxLength" : function(maxLen){
			this.maxLength = maxLen;
			if(this.dojoWidget){
				this.dojoWidget.maxLength = maxLen;
			}
		},
		"getMaxLength" : function(){
			if(this.dojoWidget){
				return this.dojoWidget.maxLength;
			}else{
				return this.maxLength;
			}
		},
		"setPlaceHolder" : function(defaultValue){
			this.placeHolder = defaultValue;
			if(this.dojoWidget){
				this.dojoWidget.placeHolder = defaultvalue;
			}
		},
		"getPlaceHolder" : function(){
			if(this.dojoWidget){
				return this.dojoWidget.placeHolder;
			}else{
				return this.placeHolder;
			}
		}
	}
);