/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
	egl.defineClass('org.eclipse.edt.eunit.runtime', "Log", "egl.jsrt", "Record", {
		"eze$$fileName" : "org/eclipse/edt/eunit/runtime/LogResult.egl",
			"constructor": function() {
			}
			,
			"ezeCopy": function(source) {
				this.msg = source.msg;
			}
			,
			"eze$$setEmpty": function() {
				this.msg = "";
			}
			,
			"eze$$setInitial": function() {
				try { egl.enter("<init>",this,arguments);
					this.eze$$setEmpty();
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"eze$$clone": function() {
				var ezert$$1 = this;
				var ezert$$2 = new egl.org.eclipse.edt.eunit.runtime.Log();
				ezert$$2.msg = ezert$$1.msg === null ? null : ezert$$1.msg;
				return ezert$$2;
			}
			,
			"eze$$getFieldSignatures": function() {
				if(this.fieldSigs === undefined){
					this.fieldSigs = [
					{name: "msg", sig: "S;"}];
				}
				return this.fieldSigs;
			}
			,
			"eze$$getAnnotations": function() {
				if(this.annotations === undefined){
					this.annotations = {};
					this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("log", null, false);
				}
				return this.annotations;
			}
			,
			"eze$$getFieldInfos": function() {
				if(this.fieldInfos === undefined){
					var annotations;
					this.fieldInfos = new Array();
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("msg", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("msg");
					this.fieldInfos[0] =new egl.eglx.services.FieldInfo("msg", "msg", "S;", String, annotations);
				}
				return this.fieldInfos;
			}
			,
			"toString": function() {
				return "[Log]";
			}
			,
			"eze$$getName": function() {
				return "org.eclipse.edt.eunit.runtime.Log";
			}
			,
			"eze$$getChildVariables": function() {
				var eze$$parent = this;
				return [
				{name: "msg", value : eze$$parent.msg, type : "eglx.lang.EString", jsName : "msg"}
				];
			}
		}
	);
