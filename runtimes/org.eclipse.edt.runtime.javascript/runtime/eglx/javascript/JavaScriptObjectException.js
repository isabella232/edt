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
egl.createJavaScriptObjectException = function( /*string*/ messageID, /*string or array*/ inserts )
{
	if (typeof(inserts) != "string") {
		inserts = egl.getRuntimeMessage( messageID, inserts );
	}
	egl.exceptionThrown = true;
	var args = new Array();
	args.push( [ "messageID", messageID || "" ] );
	args.push( [ "message", inserts || "" ] );
	args.push( [ "name", arguments[ 2 ] || "" ] );
	return new egl.eglx.javascript.JavaScriptObjectException( args );
};
egl.defineClass('eglx.javascript', "JavaScriptObjectException", "eglx.lang", "AnyException", {
	"eze$$fileName" : "eglx/javascript/Exceptions.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
			this.name = source.name;
		}
		,
		"eze$$setEmpty": function() {
			this.name = "";
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.eglx.javascript.JavaScriptObjectException();
			ezert$$2.name = ezert$$1.name;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("JavaScriptObjectException", null, false);
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
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[JavaScriptObjectException]";
		}
		,
		"eze$$getName": function() {
			return "eglx.javascript.JavaScriptObjectException";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			var childVars = this.eze$$superClass.prototype.eze$$getChildVariables.call(this);
			childVars.push({name: "name", value : eze$$parent.name, type : "eglx.lang.EString", jsName : "name"});
			return childVars;
		}
	}
);
