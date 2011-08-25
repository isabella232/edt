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
egl.defineClass('eglx.http', "HttpResponse", "egl.jsrt", "Record", {
	"eze$$fileName" : "eglx/http/HttpResponse.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.status = source.status;
			this.statusMessage = source.statusMessage;
			this.encoding = source.encoding;
			this.charset = source.charset;
			this.contentType = source.contentType;
			this.headers = source.headers;
			this.body = source.body;
		}
		,
		"eze$$setEmpty": function() {
			this.status = null;
			this.statusMessage = null;
			this.encoding = null;
			this.charset = null;
			this.contentType = null;
			this.headers = null;
			this.body = null;
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.eglx.http.HttpResponse();
			ezert$$2.eze$$isNull = this.eze$$isNull;
			ezert$$2.eze$$isNullable = this.eze$$isNullable;
			ezert$$2.status = ezert$$1.status;
			ezert$$2.statusMessage = ezert$$1.statusMessage;
			ezert$$2.encoding = ezert$$1.encoding;
			ezert$$2.charset = ezert$$1.charset;
			ezert$$2.contentType = ezert$$1.contentType;
			ezert$$2.headers = ezert$$1.headers;
			ezert$$2.body = ezert$$1.body;
			ezert$$2.setNull(ezert$$1.eze$$isNull);
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("HttpResponse", null, false);
			}
			return this.annotations;
		},
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("status", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("status");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("status", "status", "I;", Number, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("statusMessage", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("statusMessage");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("statusMessage", "statusMessage", "S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("encoding", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("encoding");
				this.fieldInfos[2] =new egl.eglx.services.FieldInfo("encoding", "encoding", "eglx.services.Encoding", egl.eglx.services.Encoding, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("charset", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("charset");
				this.fieldInfos[3] =new egl.eglx.services.FieldInfo("charset", "charset", "S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("contentType", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("contentType");
				this.fieldInfos[4] =new egl.eglx.services.FieldInfo("contentType", "contentType", "S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("headers", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("headers");
				this.fieldInfos[5] =new egl.eglx.services.FieldInfo("headers", "headers", "y;", egl.eglx.lang.EDictionary, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("body", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("body");
				this.fieldInfos[6] =new egl.eglx.services.FieldInfo("body", "body", "S;", String, annotations);
			}
			return this.fieldInfos;
		},
		"eze$$resolvePart": function(/*string*/ namespace, /*string*/ localName) {
			if(this.namespaceMap == undefined){
				this.namespaceMap = {};
				this.namespaceMap["##default{HttpResponse}"] = "eglx.http.HttpResponse";
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
			return "[HttpResponse]";
		}
	}
);
