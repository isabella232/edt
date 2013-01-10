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
egl.defineClass('eglx.java', "JavaObjectException", "eglx.lang", "AnyException", {
	"eze$$fileName" : "eglx/java/Exceptions.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.exceptionType = source.exceptionType;
		}
		,
		"eze$$setEmpty": function() {
			this.exceptionType = "";
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.eglx.java.JavaObjectException();
			ezert$$2.exceptionType = ezert$$1.exceptionType;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("JavaObjectException", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("exceptionType", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("exceptionType");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("exceptionType", "exceptionType", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[JavaObjectException]";
		}
		,
		"eze$$getName": function() {
			return "eglx.java.JavaObjectException";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			var childVars = this.eze$$superClass.prototype.eze$$getChildVariables.call(this);
			childVars.push({name: "exceptionType", value : eze$$parent.exceptionType, type : "eglx.lang.EString", jsName : "exceptionType"});
			return childVars;
		}
	}
);
