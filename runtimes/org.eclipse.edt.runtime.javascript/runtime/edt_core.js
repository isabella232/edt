(function(){
	egl = function() { };
	egl.eze$$rscBundles = {};
	egl.eze$$runtimeProperties = {};
	
	if (typeof (XMLHttpRequest) != "undefined") {
		egl.newXMLHttpRequest = function() {
				return new XMLHttpRequest();
			};
			}
	else if (window.ActiveXObject) {
		try {
			new ActiveXObject( "Msxml2.XMLHTTP" ); 
			egl.newXMLHttpRequest = function() {
				return new ActiveXObject( "Msxml2.XMLHTTP" );
			};
		}
		catch( e ) {
			try {
				new ActiveXObject( "Microsoft.XMLHTTP" ); 
				egl.newXMLHttpRequest = function() {
					return new ActiveXObject( "Microsoft.XMLHTTP" );
		 		};
		 	}
			catch (e) {
			}
		}
	};
	if (!egl.newXMLHttpRequest) {
		document.write( "Cannot initialize XMLHttpRequest. This browser is not supported by EGL Rich UI");	
	};
	
	egl__contextKey=(function(){
		var params = location.search.substring(1).split('&');
		for(var i in params){
			var keys = params[i].split('=');
			if(keys.length == 2 && keys[0] == 'contextKey')
				return keys[1];
		}
		return '';
	})();
	//Private params
	var loadFiles = [];
	var includeFiles = [];
	function eze$$loadScript(url, callback){
		var script = document.createElement("script");
		script.type = "text/javascript";
		if (script.readyState){ //IE
			script.onreadystatechange = function(){
				if (script.readyState == "loaded" || script.readyState == "complete"){
					script.onreadystatechange = null;
					callback();
				}
			};
		} else { //Others
			script.onload = function(){
				callback();
			};
			script.onerror = function(){
				document.write("load " + this.src + " fail");
			};
		}
		script.src = "/" + egl__contextRoot + "/" + url + (egl__contextKey ? ("?contextKey=" + egl__contextKey) : "");
		document.getElementsByTagName("head")[0].appendChild(script);
	};
	function eze$$loadScripts(urls, callback){
		var url = urls.shift();
		if(url){
			eze$$loadScript(url, function(){eze$$loadScripts(urls, callback);});
		}else{
			callback();
		}
	};
	function load(path, callback){
		if(typeof(path)=="object" && typeof(path.sort)=="function" && typeof(path.length)=="number"){
			eze$$loadScripts(path, callback);
		}else if(typeof(path)=="string"){
			eze$$loadScript(path, callback);
		}else{
			document.write("Cannot load the path " + path);
		}
	};
	function include(files){
		var xhr = egl.newXMLHttpRequest();
		var htmlString = "";
		for(var i=0; i<files.length; i++){
			var currentFile = files[i];
			xhr.open( 'POST', currentFile, false );
			xhr.send( null );
			var currentFileType = currentFile.indexOf(".") >=0 ? currentFile.substring(currentFile.lastIndexOf(".") + 1, currentFile.length) : "";
			if(currentFileType == "js")
				htmlString += "<script>" + xhr.responseText + "<\/script>";
			else if(currentFileType == "css")
				htmlString += "<style type='text/css'>" + xhr.responseText + "<\/style>";
			else
				htmlString += xhr.responseText;
		}
		document.write(htmlString);
	};
	
	//API
	egl.initParams = function(contextRoot, defaultDD, defaultLocale, nlsCode, shortMask, mediumMask, longMask, currencySymbol, 
			decimalSeparator, groupingSeparator){
		egl__contextRoot=contextRoot;
		egl__defaultRuntimeMessagesLocale=defaultLocale;
		egl__defaultDeploymentDescriptor=defaultDD;
		egl__htmlLocale=defaultLocale;
		egl.localeInfo = {locale : defaultLocale, nlsCode : nlsCode, shortMask : shortMask, mediumMask : mediumMask, 
				longMask : longMask};
		if(currencySymbol != null && decimalSeparator != null && groupingSeparator != null){
			egl.localeInfo.currencySymbol = currencySymbol;
			egl.localeInfo.decimalSeparator = decimalSeparator;
			egl.localeInfo.groupingSeparator = groupingSeparator;
		}
		egl.require("runtime.egl.messages.RuiMessages-" + defaultLocale);
	};
	egl.require = function(file){
		file = file.replace(/\./g, "/");
		loadFiles.push(file + ".js");
	};
	egl.includeBind = function(egldd){
		loadFiles.push(egldd + "-bnd.js");
	};
	egl.loadFile = function(file){
		includeFiles.push(file);
	};
	egl.init = function( initFunc, errorFunc){
		include(includeFiles);
		load(loadFiles, function(){			
			try {
				initFunc();
			} catch (e) {
				egl.crashTerminateSession();
				if(e == "eze$$HandlerLoadErr")
					egl.println('Internal generation error. Found no definition for samples.client.HelloWorldView. Try <b>Project > Clean...</b>');
				else
					egl.printError('Could not render UI', e); throw e;
			}
		});		
	};
	egl.reportHandlerLoadError = function(handler){
		throw "eze$$HandlerLoadErr";
	};
})();
