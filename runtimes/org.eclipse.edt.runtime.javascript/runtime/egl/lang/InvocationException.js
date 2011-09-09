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
egl.defineClass('egl.lang', "InvocationException", "egl.jsrt", "Record", {
	"eze$$fileName" : "egl/lang/Exceptions.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.name = source.name;
			this.returnValue = source.returnValue;
		}
		,
		"eze$$setEmpty": function() {
			this.name = "";
			this.returnValue = 0;
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.egl.lang.InvocationException();
			ezert$$2.eze$$isNull = this.eze$$isNull;
			ezert$$2.eze$$isNullable = this.eze$$isNullable;
			ezert$$2.name = ezert$$1.name;
			ezert$$2.returnValue = ezert$$1.returnValue;
			ezert$$2.setNull(ezert$$1eze$$isNull);
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("InvocationException", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("name", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("name");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("name", "name", "S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("returnValue", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("returnValue");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("returnValue", "returnValue", "I;", Number, annotations);
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[InvocationException]";
		}
	}
);
