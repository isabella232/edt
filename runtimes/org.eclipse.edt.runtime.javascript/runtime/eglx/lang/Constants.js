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
egl.defineRUILibrary('eglx.lang', 'Constants',
{
	'eze$$fileName': 'eglx/lang/Constants.egl',
	'eze$$runtimePropertiesFile': 'eglx.lang.Constants',
		"constructor": function() {
			if(egl.eglx.lang.Constants['$inst']) return egl.eglx.lang.Constants['$inst'];
			egl.eglx.lang.Constants['$inst']=this;
			this.isoDateFormat = "";
			this.isoDateFormat = "yyyy-MM-dd";
			this.usaDateFormat = "";
			this.usaDateFormat = "MM/dd/yyyy";
			this.eurDateFormat = "";
			this.eurDateFormat = "dd.MM.yyyy";
			this.jisDateFormat = "";
			this.jisDateFormat = "yyyy-MM-dd";
			this.isoTimeFormat = "";
			this.isoTimeFormat = "HH.mm.ss";
			this.usaTimeFormat = "";
			this.usaTimeFormat = "hh:mm a";
			this.eurTimeFormat = "";
			this.eurTimeFormat = "HH.mm.ss";
			this.jisTimeFormat = "";
			this.jisTimeFormat = "HH:mm:ss";
			this.db2TimeStampFormat = "";
			this.db2TimeStampFormat = "yyyy-MM-dd-HH.mm.ss.ffffff";
			this.odbcTimeStampFormat = "";
			this.odbcTimeStampFormat = "yyyy-MM-dd HH:mm:ss.ffffff";
			this.eze$$setInitial();
		}
		,
		"eze$$setEmpty": function() {
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("Constants", null, false);
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
		"getIsoDateFormat": function() {
			return isoDateFormat;
		}
		,
		"getUsaDateFormat": function() {
			return usaDateFormat;
		}
		,
		"getEurDateFormat": function() {
			return eurDateFormat;
		}
		,
		"getJisDateFormat": function() {
			return jisDateFormat;
		}
		,
		"getIsoTimeFormat": function() {
			return isoTimeFormat;
		}
		,
		"getUsaTimeFormat": function() {
			return usaTimeFormat;
		}
		,
		"getEurTimeFormat": function() {
			return eurTimeFormat;
		}
		,
		"getJisTimeFormat": function() {
			return jisTimeFormat;
		}
		,
		"getDb2TimeStampFormat": function() {
			return db2TimeStampFormat;
		}
		,
		"getOdbcTimeStampFormat": function() {
			return odbcTimeStampFormat;
		}
		,
		"toString": function() {
			return "[Constants]";
		}
		,
		"eze$$getName": function() {
			return "Constants";
		}
		,
		"eze$$getChildVariables": function() {
			var eze$$parent = this;
			return [
			{name: "isoDateFormat", value : eze$$parent.isoDateFormat, type : "eglx.lang.EString", jsName : "isoDateFormat"},
			{name: "usaDateFormat", value : eze$$parent.usaDateFormat, type : "eglx.lang.EString", jsName : "usaDateFormat"},
			{name: "eurDateFormat", value : eze$$parent.eurDateFormat, type : "eglx.lang.EString", jsName : "eurDateFormat"},
			{name: "jisDateFormat", value : eze$$parent.jisDateFormat, type : "eglx.lang.EString", jsName : "jisDateFormat"},
			{name: "isoTimeFormat", value : eze$$parent.isoTimeFormat, type : "eglx.lang.EString", jsName : "isoTimeFormat"},
			{name: "usaTimeFormat", value : eze$$parent.usaTimeFormat, type : "eglx.lang.EString", jsName : "usaTimeFormat"},
			{name: "eurTimeFormat", value : eze$$parent.eurTimeFormat, type : "eglx.lang.EString", jsName : "eurTimeFormat"},
			{name: "jisTimeFormat", value : eze$$parent.jisTimeFormat, type : "eglx.lang.EString", jsName : "jisTimeFormat"},
			{name: "db2TimeStampFormat", value : eze$$parent.db2TimeStampFormat, type : "eglx.lang.EString", jsName : "db2TimeStampFormat"},
			{name: "odbcTimeStampFormat", value : eze$$parent.odbcTimeStampFormat, type : "eglx.lang.EString", jsName : "odbcTimeStampFormat"}
			];
		}
	}
);
