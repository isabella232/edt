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
	'dojo.widgets', 'DojoCurrencyTextBox',
	'dojo.widgets', 'DojoValidationBase',  	
	'div',									
{
	"constructor" : function() {
		this.renderWhenDojoIsDoneLoading(["dojo/date/locale", "dijit/form/CurrencyTextBox"]);
	},
	"createDojoWidget" : function(parent){
		var eglWidget = this;
		this._mergeArgs({
			id : this.id || "egl.DCTB" + (++egl._dojoSerial),
			type : "text", 
			method : "post",
			currency : this.currency || "USD"
		});
		this._setCommonProp();
		this.dojoWidget = new dijit.form.CurrencyTextBox( this._args , parent);
		this.setValidators(this.validators);
		this._setTextboxStyle();
		this.dojoWidget.startup();
		if(this.value){
			this.setValue(this.value);
		}
	},
	"setText" : function( text ) {
		this.value = text;
		if(this.dojoWidget){
			this.dojoWidget.set("value",text);
			this.dojoWidget._setValueAttr(this.dojoWidget.get("value"), true);
		}	
	},
	"setValue" : function( text ) {
		this.setText(text);
	},
	"getValue" : function() {
		return(this.getText());
	}
});