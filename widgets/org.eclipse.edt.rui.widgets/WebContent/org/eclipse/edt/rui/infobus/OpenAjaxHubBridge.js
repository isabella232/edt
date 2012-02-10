/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineClass('org.eclipse.edt.rui.infobus', 'OpenAjaxHubBridge',
{
	"publish" : function(name, data) {
		if (egl.enableEditing) return;
		OpenAjax.hub.publish(name, data);		
    },
    "subscribe" : function (name, callback) {
		if (egl.enableEditing) return {};
		return OpenAjax.hub.subscribe(name, 
				function(name,data) { callback(name, egl.boxAny(data))}, 
				null, null, null);
    },
    "unsubscribe" : function (subscription) {
		if (egl.enableEditing) return;
    	return OpenAjax.hub.unsubscribe(subscription);
    }
});
