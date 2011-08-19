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
egl.defineClass('eglx.http', "HttpREST", "egl.jsrt", "Record", {
	"eze$$fileName" : "eglx/http/HttpREST.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.request.ezeCopy(source.request);
			this.response.ezeCopy(source.response);
			this.isEglRpc = source.isEglRpc;
		}
		,
		"eze$$setEmpty": function() {
			this.request = new egl.eglx.http.HttpRequest();
			this.response = new egl.eglx.http.HttpResponse();
			this.isEglRpc = false;
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
			this.request.ezeCopy(new egl.eglx.http.HttpRequest());
			this.request.method = egl.eglx.http.HttpMethod.POST;
			this.request.encoding = egl.eglx.services.Encoding.XML;
			this.request.charset = "UTF-8";
			this.response.ezeCopy(new egl.eglx.http.HttpResponse());
			this.response.encoding = egl.eglx.services.Encoding.USE_CONTENTTYPE;
			this.isEglRpc = false;
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.eglx.http.HttpREST();
			ezert$$2.eze$$isNull = this.eze$$isNull;
			ezert$$2.eze$$isNullable = this.eze$$isNullable;
			ezert$$2.request = ezert$$1.request;
			ezert$$2.response = ezert$$1.response;
			ezert$$2.isEglRpc = ezert$$1.isEglRpc;
			ezert$$2.setNull(ezert$$1eze$$isNull);
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("HttpREST", null, false);
			}
			return this.annotations;
		},
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("request", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("request");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("request", "request", "A;", egl.eglx.http.HttpRequest, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("response", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("response");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("response", "response", "A;", egl.eglx.http.HttpResponse, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("isEglRpc", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("isEglRpc");
				this.fieldInfos[2] =new egl.eglx.services.FieldInfo("isEglRpc", "isEglRpc", "0;", Boolean, annotations);
			}
			return this.fieldInfos;
		},
		"eze$$resolvePart": function(/*string*/ namespace, /*string*/ localName) {
			if(this.namespaceMap == undefined){
				this.namespaceMap = {};
				this.namespaceMap["##default{HttpREST}"] = "eglx.http.HttpREST";
			}
			var newObject = null;
			var className = this.namespaceMap[namespace + "{" + localName + "}"];
			if(className != undefined && className != null){
				newObject = instantiate(className, []);
			};
			return newObject;
		}
		,
		"toString": function() {
			return "[HttpREST]";
		}
	}
);
