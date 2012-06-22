/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineClass( 'eglx.services', 'ServiceBinder', {
	
    "constructor" : function() { 
        if (egl.eglx.services.$ServiceBinder) return egl.eglx.services.$ServiceBinder;
		egl.eglx.services.$ServiceBinder=this; 
        this.bindFiles = [];
    }, 
	"addBindFile" : function(/*String*/ eglddName) {
	    if (!this.bindFileExists(eglddName)) {
			this.loadBindFile(eglddName);
	    }
	},
	"bindFileExists" : function(/*String*/ eglddName) {
		var rc = false;
		for(var n=0; n < this.bindFiles && rc == false; n++) {
			if (this.bindFiles.name == eglddName) {
			   rc = true;
			}			
		}
		return rc;
	},
	"loadBindFile" : function(/*String*/ eglddName) {
		try{
	        var bindFileVar = "eze$$BindFile_" + eglddName;
		    var bindFile = egl[bindFileVar]();
		    this.bindFiles.push(bindFile);
		}
		catch(e){
	    	throw egl.eglx.services.createServiceInvocationException("CRRUI3650E", [eglddName]);
		}
	},
	"getBinding" : function(/*String*/ eglddName, /*String*/ bindingKey) {
		this.addBindFile(eglddName);
	    var bindFile = null;
	    for (var n = 0; n < this.bindFiles.length && bindFile == null; n++) {
	        if (this.bindFiles[n].name == eglddName) {
	           bindFile = this.bindFiles[n];
	        }
	    }
	    if (bindFile == null) {
	    	//can not find eglddName
	    	throw egl.eglx.services.createServiceInvocationException("CRRUI3650E", [eglddName]);
	       return null;
	    }
	    
	    bindingKey = bindingKey.toLowerCase();
		var binding = null;
		for (var n=0; n < bindFile.bindings.length && binding == null; n++) {
			if (bindingKey == bindFile.bindings[n].name.toLowerCase()) {
				binding = bindFile.bindings[n];
				break;
			}			
		}
		if(binding == null){
		    for (var n = 0; n <  bindFile.includes.length; n++) {
		    	binding = this.getBinding(bindFile.includes[n], bindingKey);
		    	if ( binding != null ) {
		    		break;
		    	}
		    }
			if (binding == null){
				//can not find binding key
		    	throw egl.eglx.services.createServiceInvocationException("CRRUI3651E", [bindingKey, eglddName]);
			}
		}
		return binding;
	}
});

egl.defineClass( "eglx.services", "BindFile", {
    "constructor" : function(/*String*/ eglddName) { 
		this.name = eglddName; 
        this.bindings = [];
        this.includes = [];
    }
});

egl.defineClass( "eglx.services", "WebBinding", {
    "constructor" : function(/*String*/ _name,
                             /*String*/ _interface,
                             /*String*/ _wsdlLocation,
                             /*String*/ _wsdlService,
                             /*String*/ _wsdlPort,
                             /*String*/ _uri) {
        this.type = "web";
        this.name = _name;
        this._interface = _interface;
        this.wsdlLocation = _wsdlLocation;
        this.wsdlService = _wsdlService;
        this.wsdlPort = _wsdlPort;
        this.uri = _uri;
    }
    
});

egl.defineClass( "eglx.services", "RestBinding", {
    "constructor" : function(/*String*/ _name,
                             /*String*/ _baseURI,
                             /*String*/ _sessionCookieId) {
        this.type = "rest";
        this.name = _name;
        this.baseURI = _baseURI;
        this.sessionCookieId = _sessionCookieId;
    }
    
});

egl.defineClass( "eglx.services", "DedicatedBinding", {
    "constructor" : function(/*String*/ _name) {
        this.type = "dedicated";
        this.name = _name;
    }
    
});
new egl.eglx.services.ServiceBinder();
