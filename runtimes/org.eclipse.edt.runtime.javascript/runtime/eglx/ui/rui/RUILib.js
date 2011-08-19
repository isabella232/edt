/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
 egl.defineClass(
	'eglx.ui.rui', 'RUILib',
{
	"constructor" : function() {
		if(this.initialized === undefined || !this.initialized){
			this.invokeListeners = [];
			this.responseListeners = [];
		}
	}
});
egl.eglx.ui.rui.RUILib["sort"] = function(array, sortFunction) {
	if (egl.debug)
		egl.enter("Runtime.sort");
	try {
		array.sort(sortFunction);
	} catch (e) {
		throw "TODO: make an exception for this";//throw egl.createRuntimeException("CRRUI2102E", []);
	}
	if (egl.debug)
		egl.leave("");
};
egl.eglx.ui.rui.RUILib["setTextSelectionEnabled"] = function(value) {
	egl.setEnableTextSelection(value);
};
egl.eglx.ui.rui.RUILib["getTextSelectionEnabled"] = function() {
	return egl.enableSelection;
};
egl.eglx.ui.rui.RUILib["getUserAgent"] = function() {
	return window.navigator.userAgent;
};
egl.eglx.ui.rui.RUILib["setTitle"] = function(value) {
	document.title = value;
};
egl.eglx.ui.rui.RUILib["getTitle"] = function() {
	return document.title;
};
egl.eglx.ui.rui.RUILib["setTheme"] = function(value) {
	egl.Document.body.setClass(value);
};
egl.eglx.ui.rui.RUILib["getTheme"] = function(value) {
	return egl.Document.body.getClass();
};
egl.eglx.ui.rui.RUILib["initialized"] = true;