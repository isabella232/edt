/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineClass( "eglx.javascript", "Job", {
	"constructor" : function() {
		egl.jobs.push(this);
	},
	"getName" : function() {
		return this.name;
	},
	"setName" : function(/*String*/ name) {
		this.name = name;
	},
	"getRunFunction" : function() {
		return this.runFunction;
	},
	"setRunFunction" : function(/*function*/ runFunction) {
		this.runFunction = runFunction;
	},
	"schedule" : function(/*int*/ms) {
		this.cancel();
		var job = this;
		this.timeout = setTimeout(function() {
			if (job.runFunction)
				try {
					job.runFunction();
				}
				catch(e) {
					if (window.egl) egl.printError("@ Job.schedule("+(ms?ms:"")+")", e);
				}
		}, ms);
	},
	"repeat" : function(/*int*/ms) {
		this.cancel();
		var job = this;
		this.interval = setInterval(function() {
			if (job.runFunction)
				try {
					job.runFunction();
				}
				catch(e) {
					if (window.egl) egl.printError("@ Job.repeat("+(ms?ms:"")+")", e);
				}
		}, ms);
	},
	"cancel" : function() {
		if (this.interval)
			clearInterval(this.interval);
		if (this.timeout)
			clearTimeout(this.timeout);
	},
	"eze$$getChildVariables": function() {
		return [
			{name: "name", value : this.getName(), type : "eglx.lang.EString", getter : "getName", setter : "setName"},
			{name: "runFunction", value : this.getRunFunction(), type : "eglx.javascript.RunFunction", getter : "getRunFunction", setter : "setRunFunction"}
		];
	}
});
