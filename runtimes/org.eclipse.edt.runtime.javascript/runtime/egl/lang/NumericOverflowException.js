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
egl.defineClass('egl.lang', "NumericOverflowException", "egl.jsrt", "Record", {
	"eze$$fileName" : "egl/lang/Exceptions.egl",
		"constructor": function() {
			this.eze$$setInitial();
		}
		,
		"ezeCopy": function(source) {
		}
		,
		"eze$$setEmpty": function() {
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.egl.lang.NumericOverflowException();
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("NumericOverflowException", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
			}
			return this.fieldInfos;
		}
		,
		"toString": function() {
			return "[NumericOverflowException]";
		}
	}
);
