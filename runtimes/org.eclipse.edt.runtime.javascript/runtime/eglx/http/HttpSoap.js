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
egl.defineClass('eglx.http', 'HttpSoap',
{
	'eze$$fileName': 'eglx/http/HttpSoap.egl',
	'eze$$runtimePropertiesFile': 'eglx.http.HttpSoap',
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"eze$$setEmpty": function() {
			this.request = new egl.eglx.http.Request();
			this.response = new egl.eglx.http.Response();
			this.responseHeader = null;
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.eglx.http.HttpSoap();
			ezert$$2.request = ezert$$1.request === null ? null : ezert$$1.request.eze$$clone();
			ezert$$2.response = ezert$$1.response === null ? null : ezert$$1.response.eze$$clone();
			ezert$$2.responseHeader = ezert$$1.responseHeader === null ? null : ezert$$1.responseHeader;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("HttpSoap", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("request", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("request");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("request", "request", "Teglx/http/Request;", egl.eglx.http.Request, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("response", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("response");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("response", "response", "Teglx/http/Response;", egl.eglx.http.Response, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("responseHeader", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("responseHeader");
				this.fieldInfos[2] =new egl.eglx.services.FieldInfo("responseHeader", "responseHeader", "Teglx/lang/EAny;", egl.eglx.lang.EAny, annotations);
			}
			return this.fieldInfos;
		}
		,
		"getRequest": function() {
			return this.request;
		}
		,
		"getResponse": function() {
			return this.response;
		}
		,
		"setSoapRequestHeader": function(rec) {
		}
		,
		"getSoapResponseHeader": function() {
			return this.responseHeader;
		}
		,
		"toString": function() {
			return "[HttpSoap]";
		}
		,
		"eze$$getName": function() {
			return "HttpSoap";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			return [
			{name: "request", value : eze$$parent.request, type : "eglx.http.Request", jsName : "request"},
			{name: "response", value : eze$$parent.response, type : "eglx.http.Response", jsName : "response"},
			{name: "responseHeader", value : eze$$parent.responseHeader, type : "eglx.lang.EAny", jsName : "responseHeader"}
			];
		}
	}
);
