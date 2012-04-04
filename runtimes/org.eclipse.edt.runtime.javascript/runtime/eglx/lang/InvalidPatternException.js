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
egl.defineClass('eglx.lang', "InvalidPatternException", "eglx.lang", "AnyException", {
	"eze$$fileName" : "eglx/lang/Exceptions.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.pattern = source.pattern;
		}
		,
		"eze$$setEmpty": function() {
			this.pattern = "";
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.eglx.lang.InvalidPatternException();
			ezert$$2.pattern = ezert$$1.pattern;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("InvalidPatternException", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("pattern", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("pattern");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("pattern", "pattern", "S;", String, annotations);
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[InvalidPatternException]";
		}
		,
		"eze$$getName": function() {
			return "eglx.lang.InvalidPatternException";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			var childVars = this.eze$$superClass.prototype.eze$$getChildVariables.call(this);
			childVars.push({name: "pattern", value : eze$$parent.pattern, type : "eglx.lang.EString", jsName : "pattern"});
			return childVars;
		}
	}
);
