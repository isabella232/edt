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
	'eglx.lang', 'SysLib',
{
	"constructor" : function() {		
	}
});

egl.eglx.lang.SysLib.errorsMap = [];

egl.eglx.lang.SysLib["callCmd"] = function(/*string*/ cmdString) {
	throw "TODO: make an exception for this";//throw egl.createRuntimeException("NOIMPL", null);  // TODO sbg Implement
};

egl.eglx.lang.SysLib["startCmd"] = function(/*string*/ cmdString) {
	throw "TODO: make an exception for this";//throw egl.createRuntimeException("NOIMPL", null);  // TODO sbg Implement
};

egl.eglx.lang.SysLib["commit"] = function() {
	throw "TODO: make an exception for this";//throw egl.createRuntimeException("NOIMPL", null);  // TODO sbg Implement
};

egl.eglx.lang.SysLib["rollback"] = function() {
	throw "TODO: make an exception for this";//throw egl.createRuntimeException("NOIMPL", null);  // TODO sbg Implement
};

egl.eglx.lang.SysLib['getProperty'] = function(property, propertyFile) {
	var value;
	if (egl.eze$$runtimeProperties[propertyFile]) {
		value = egl.eze$$runtimeProperties[propertyFile][property];
	}
	
	if (!value && egl.eze$$runtimeProperties["rununit"]) {
		value = egl.eze$$runtimeProperties["rununit"][property];
	}
	
	if (value === undefined) {
		value = null;
	}
	return value;
};

egl.eglx.lang.SysLib["setLocale"] = function(/*string*/ languageCode, /*string*/countryCode, /*string */ variant) {
	throw "TODO: make an exception for this";//throw egl.createRuntimeException("NOIMPL", null);  // TODO sbg Implement
};

egl.eglx.lang.SysLib["sort"] = function(/*array any*/anArray, /*sortFunction delegate*/sortFunction) {
	throw "TODO: make an exception for this";//throw egl.createRuntimeException("NOIMPL", null);  // TODO sbg Implement
};

egl.eglx.lang.SysLib["wait"] = function(/*decimal(9,2)*/ seconds) {
	throw "TODO: make an exception for this";//throw egl.createRuntimeException("NOIMPL", null);  // TODO sbg Implement
};

egl.eglx.lang.SysLib["writeStderr"] = function(/*string*/ text) {
	egl.println(text);
};

egl.eglx.lang.SysLib['writeStdout'] = function	(text) {
	egl.println(text);
};