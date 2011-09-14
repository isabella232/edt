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
egl.defineClass('org.eclipse.edt.eunit.runtime', "Status", "egl.jsrt", "Record", {
	"eze$$fileName" : "org/eclipse/edt/eunit/runtime/LogResult.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.code = source.code;
			this.reason = source.reason;
		}
		,
		"eze$$setEmpty": function() {
			this.code = 0;
			this.reason = "";
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.org.eclipse.edt.eunit.runtime.Status();
			ezert$$2.eze$$isNull = this.eze$$isNull;
			ezert$$2.eze$$isNullable = this.eze$$isNullable;
			ezert$$2.code = ezert$$1.code;
			ezert$$2.reason = ezert$$1.reason;
			ezert$$2.setNull(ezert$$1eze$$isNull);
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("Status", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("code", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("code");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("code", "code", "I;", Number, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("reason", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("reason");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("reason", "reason", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[Status]";
		}
	}
);
