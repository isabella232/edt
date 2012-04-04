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
egl.defineClass('eglx.xml', "XMLProcessingException", "eglx.lang", "AnyException", {
	"eze$$fileName" : "eglx/xml/XMLProcessingException.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.detail = source.detail;
		}
		,
		"eze$$setEmpty": function() {
			this.detail = "";
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.eglx.xml.XMLProcessingException();
			ezert$$2.detail = ezert$$1.detail;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("XMLProcessingException", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("detail", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("detail");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("detail", "detail", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[XMLProcessingException]";
		}
		,
		"eze$$getName": function() {
			return "eglx.xml.XMLProcessingException";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			var childVars = this.eze$$superClass.prototype.eze$$getChildVariables.call(this);
			childVars.push({name: "detail", value : eze$$parent.detail, type : "eglx.lang.EString", jsName : "detail"});
			return childVars;
		}
	}
);
