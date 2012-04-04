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
(function(){
	egl = {};
	egl.initParams = function(contextRoot, defaultDD, defaultLocale, runtimeLocale, nlsCode, shortMask, mediumMask, longMask, currencySymbol, 
			decimalSeparator, groupingSeparator){
		egl__contextRoot=contextRoot;
		egl__defaultRuntimeMessagesLocale=defaultLocale;
		egl__defaultDeploymentDescriptor=defaultDD;
		egl__htmlLocale=defaultLocale;
		egl__runtimeLocale=runtimeLocale;
		egl__href = document.location.protocol + "//" + document.location.host + "/" + contextRoot;
		egl__contextKey=(function(){
			var params = location.search.substring(1).split('&');
			for(var i in params){
				var keys = null;
				if(typeof(params[i])=="string")
					keys = params[i].split('=');
				if(keys!=null && keys instanceof Array && keys.length == 2 && keys[0] == 'contextKey')
					return keys[1];
			}
			return '';
		})();
		egl.localeInfo = {locale : defaultLocale, nlsCode : nlsCode, shortMask : shortMask, mediumMask : mediumMask, 
				longMask : longMask};
		if(currencySymbol != null && decimalSeparator != null && groupingSeparator != null){
			egl.localeInfo.currencySymbol = currencySymbol;
			egl.localeInfo.decimalSeparator = decimalSeparator;
			egl.localeInfo.groupingSeparator = groupingSeparator;
		}		
	};
	dojoConfig = {
		async: true
	};
})();
