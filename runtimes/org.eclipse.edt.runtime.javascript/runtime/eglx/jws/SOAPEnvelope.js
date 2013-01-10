/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineClass('eglx.jws', "SOAPEnvelope", "egl.jsrt", "Record", {
	"eze$$fileName" : "eglx/jws/SOAPEnvelope.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.soapHeader = source.soapHeader;
			this.body = source.body;
		}
		,
		"eze$$setEmpty": function() {
			this.soapHeader = null;
			this.body = null;
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
			this.soapHeader = (function(x){ return x != null ? (x) : null; })(null);
			this.body = (function(x){ return x != null ? (x) : null; })(null);
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.eglx.jws.SOAPEnvelope();
			ezert$$2.eze$$isNull = this.eze$$isNull;
			ezert$$2.eze$$isNullable = this.eze$$isNullable;
			ezert$$2.soapHeader = ezert$$1.soapHeader;
			ezert$$2.body = ezert$$1.body;
			ezert$$2.setNull(ezert$$1eze$$isNull);
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("Envelope", "http://schemas.xmlsoap.org/soap/envelope/", false);
			}
			return this.annotations;
		},
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("soapHeader", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("soapHeader");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("soapHeader", "soapHeader", "Teglx/lang/EAny;", egl.eglx.lang.EAny, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("body", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("body");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("body", "body", "Teglx/lang/EAny;", egl.eglx.lang.EAny, annotations);
			}
			return this.fieldInfos;
		},
		"eze$$resolvePart": function(/*string*/ namespace, /*string*/ localName) {
			if(this.namespaceMap == undefined){
				this.namespaceMap = {};
				this.namespaceMap["http://schemas.xmlsoap.org/soap/envelope/{Envelope}"] = "eglx.jws.SOAPEnvelope";
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
			return "[SOAPEnvelope]";
		}
		,
		"eze$$getName": function() {
			return "eglx.jws.SOAPEnvelope";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			return [
			{name: "soapHeader", value : eze$$parent.soapHeader, type : "eglx.lang.EAny", jsName : "soapHeader"},
			{name: "body", value : eze$$parent.body, type : "eglx.lang.EAny", jsName : "body"}
			];
		}
	}
);
