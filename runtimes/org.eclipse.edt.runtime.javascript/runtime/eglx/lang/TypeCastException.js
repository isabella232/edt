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
egl.defineClass('eglx.lang', "TypeCastException", "eglx.lang", "AnyException", {
	"eze$$fileName" : "eglx/lang/Exceptions.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.castToName = source.castToName;
			this.actualTypeName = source.actualTypeName;
		}
		,
		"eze$$setEmpty": function() {
			this.castToName = this.castToName || "";
			this.actualTypeName = this.actualTypeName || "";
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.eglx.lang.TypeCastException();
			ezert$$2.castToName = ezert$$1.castToName;
			ezert$$2.actualTypeName = ezert$$1.actualTypeName;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("TypeCastException", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("castToName", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("castToName");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("castToName", "castToName", "S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("actualTypeName", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("actualTypeName");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("actualTypeName", "actualTypeName", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[TypeCastException]";
		}
		,
		"eze$$getName": function() {
			return "eglx.lang.TypeCastException";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			var childVars = this.eze$$superClass.prototype.eze$$getChildVariables.call(this);
			childVars.push({name: "castToName", value : eze$$parent.castToName, type : "eglx.lang.EString", jsName : "castToName"});
			childVars.push({name: "actualTypeName", value : eze$$parent.actualTypeName, type : "eglx.lang.EString", jsName : "actualTypeName"});
			return childVars;
		}
	}
);
