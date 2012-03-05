(function(){
	egl = {};
	egl.initParams = function(contextRoot, defaultDD, defaultLocale, runtimeLocale, nlsCode, shortMask, mediumMask, longMask, currencySymbol, 
			decimalSeparator, groupingSeparator, folders){
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
		require({baseUrl: "../" + egl__contextRoot});
		
	};
	dojoConfig = {
		asyn: true
	};
})();
