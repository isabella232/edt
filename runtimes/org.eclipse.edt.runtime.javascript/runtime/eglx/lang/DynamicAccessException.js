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
egl.defineClass('eglx.lang', "DynamicAccessException", "eglx.lang", "AnyException", {
	"eze$$fileName" : "eglx/lang/Exceptions.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.key = source.key;
		}
		,
		"eze$$setEmpty": function() {
			this.key = "";
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.eglx.lang.DynamicAccessException();
			ezert$$2.key = ezert$$1.key;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("DynamicAccessException", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("key", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("key");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("key", "key", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[DynamicAccessException]";
		}
		,
		"eze$$getName": function() {
			return "eglx.lang.DynamicAccessException";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			var childVars = this.eze$$superClass.prototype.eze$$getChildVariables.call(this);
			childVars.push({name: "key", value : eze$$parent.key, type : "eglx.lang.EString", jsName : "key"});
			return childVars;
		}
	}
);
