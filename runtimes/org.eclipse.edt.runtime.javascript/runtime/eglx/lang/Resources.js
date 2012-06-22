/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineClass(
	'eglx.lang', 'Resources',
{
	"constructor" : function() {		
	}
});

egl.eglx.lang.Resources["getResource"] = function(uri) {
	var ret = undefined;
	var idx = uri.indexOf("resource:");
	if(idx === 0){
		uri = uri.slice(idx + 9);//pick up everything paste resource:
	}
	idx = uri.indexOf("binding:");
	if(idx === -1){
		throw egl.eglx.services.createServiceInvocationException("CRRUI3663E", [uri]);  
	}
	else{
		uri = uri.slice(idx + 8);//pick up everything paste binding:
	}
	var bindingKey;
	idx = uri.indexOf("file:");
	if(idx === -1){
		eglddName = egl__defaultDeploymentDescriptor;
		bindingKey = uri;//uri is the binding key
	}
	else{
		uri = uri.slice(idx + 5);//pick up everything paste file:
		idx = uri.indexOf("#");
		eglddName = uri.slice(0, idx);//pick up everything up to the first # as the file name:
		bindingKey = uri.slice(idx + 1);//pick up everything after the first # as the binding key:
	}
	var binding = egl.eglx.services.$ServiceBinder.getBinding(eglddName.toLowerCase(), bindingKey);
	if(binding instanceof egl.eglx.services.RestBinding){
		ret = new egl.eglx.http.HttpRest();
		ret.request.uri = binding.baseURI;
		ret.restType = egl.eglx.rest.ServiceType.EglRpc;
	}
	else if(binding instanceof egl.eglx.services.DedicatedBinding){
		ret = new egl.eglx.http.HttpProxy();
	}
	if(ret === undefined || ret === null){
		throw egl.eglx.services.createServiceInvocationException("CRRUI3651E", [uri, eglddName]);  
	}
	return ret;
};
