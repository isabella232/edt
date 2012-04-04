/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
/**
 * HttpLib
 */
egl.defineClass(
    'eglx.http', 'HttpLib',
{
	"constructor" : function() {
	}
});
egl.eglx.http.HttpLib["checkURLEncode"] = function(/*String*/stringIn) {
	if (stringIn.length > 0) {
		var lowercaseStr = stringIn.toLowerCase();
		//if starts with http, do not url encode it
		if (lowercaseStr.indexOf("http") != 0) {
			return egl.eglx.http.HttpLib.convertToURLEncoded(stringIn);
		}
	}
	return stringIn;
};
egl.eglx.http.HttpLib["convertFromURLEncoded"] = function(/*String*/urlEncoded) {
	return decodeURIComponent(urlEncoded);
};
egl.eglx.http.HttpLib["convertToURLEncoded"] = function(/*String*/eglType) {
	return encodeURIComponent(eglType);
};
egl.eglx.http.HttpLib["convertFromFormData"] = function(/*String*/str) {
	var formdataObj = null;
	var pairsArray = str.split('&');
	for ( var i = 0; i < pairsArray.length; i++) {
		var pair = parisArray[i].split('=');
		var key = this.convertFromURLEncoded(pair[0]);
		var val = this.convertFromURLEncoded(pair[1]);
		formdataObj.key = val;
	}
	return formdataObj;
};
egl.eglx.http.HttpLib["convertToFormData"] = function(/*Object*/obj) {
	var formdata = "";
	var needAnd = false;
	for (f in obj) {
		if (!f.match(/^eze\$\$/) && (typeof obj[f] != "function")) {
			if (needAnd)
				formdata += '&';
			var key = this.convertToURLEncoded(obj.eze$$getFORMName(f)); //encode key
			var value = this.convertToURLEncoded(obj[f]); //encode value
			formdata += key;
			formdata += '=';
			formdata += value;
			needAnd = true;
		}
	}
	return formdata;
};

