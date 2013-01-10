/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
	'dojo.widgets', 'DojoTextField',
	'dojo.widgets', 'DojoValidationBase',  	
	'div',									
{
	"constructor" : function() {
		this.renderWhenDojoIsDoneLoading(["dijit/form/ValidationTextBox"]);
	},
	"createDojoWidget" : function(parent){	
		this._setCommonProp();
		if(this.required === undefined){
			this._args.required = false;
		}
		this.dojoWidget = new dijit.form.ValidationTextBox( this._args , parent);
		this.setValidators(this.validators);
		this._setTextboxStyle();
		this.dojoWidget.startup();
	}	
});
