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
egl.defineClass('eglx.lang', "InvalidIndexException", "eglx.lang", "AnyException", {
	"eze$$fileName" : "eglx/lang/Exceptions.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.index = source.index;
		}
		,
		"eze$$setEmpty": function() {
			this.index = 0;
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.eglx.lang.InvalidIndexException();
			ezert$$2.index = ezert$$1.index;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("InvalidIndexException", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("index", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("index");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("index", "index", "I;", Number, annotations);
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[InvalidIndexException]";
		}
		,
		"eze$$getName": function() {
			return "eglx.lang.InvalidIndexException";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			var childVars = this.eze$$superClass.prototype.eze$$getChildVariables.call(this);
			childVars.push({name: "index", value : eze$$parent.index, type : "eglx.lang.EInt", jsName : "index"});
			return childVars;
		}
	}
);
