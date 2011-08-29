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
egl.defineClass('eglx.services', "ServiceBindingException", "egl.jsrt", "Record", {
	"eze$$fileName" : "eglx/services/ServiceBindingException.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
		}
		,
		"eze$$setEmpty": function() {
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.eglx.services.ServiceBindingException();
			ezert$$2.eze$$isNull = this.eze$$isNull;
			ezert$$2.eze$$isNullable = this.eze$$isNullable;
			ezert$$2.setNull(ezert$$1eze$$isNull);
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("ServiceBindingException", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
			}
			return this.fieldInfos;
		}
		,
		"eze$$resolvePart": function(/*string*/ namespace, /*string*/ localName) {
			if(this.namespaceMap == undefined){
				this.namespaceMap = {};
				this.namespaceMap["##default{ServiceBindingException}"] = "eglx.services.ServiceBindingException";
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
			return "[ServiceBindingException]";
		}
	}
);
