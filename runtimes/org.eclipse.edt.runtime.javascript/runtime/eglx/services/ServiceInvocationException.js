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
egl.defineClass('eglx.services', "ServiceInvocationException", "eglx.lang", "AnyException", {
	"eze$$fileName" : "eglx/services/ServiceInvocationException.egl",
		"constructor": function() {
		}
		,
		"ezeCopy": function(source) {
			this.source = source.source;
			this.detail1 = source.detail1;
			this.detail2 = source.detail2;
			this.detail3 = source.detail3;
		}
		,
		"eze$$setEmpty": function() {
			this.source = null;
			this.detail1 = "";
			this.detail2 = "";
			this.detail3 = "";
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.eglx.services.ServiceInvocationException();
			ezert$$2.source = ezert$$1.source === null ? null : ezert$$1.source;
			ezert$$2.detail1 = ezert$$1.detail1 === null ? null : ezert$$1.detail1;
			ezert$$2.detail2 = ezert$$1.detail2 === null ? null : ezert$$1.detail2;
			ezert$$2.detail3 = ezert$$1.detail3 === null ? null : ezert$$1.detail3;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("ServiceInvocationException", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("source", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("source");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("source", "source", "?eglx.services.ServiceKind", egl.eglx.services.ServiceKind, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("detail1", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("detail1");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("detail1", "detail1", "S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("detail2", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("detail2");
				this.fieldInfos[2] =new egl.eglx.services.FieldInfo("detail2", "detail2", "S;", String, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("detail3", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("detail3");
				this.fieldInfos[3] =new egl.eglx.services.FieldInfo("detail3", "detail3", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[ServiceInvocationException]";
		}
	}
);
