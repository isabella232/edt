/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineClass(  "egl.ui.gateway", "UIGatewayRecord", "egl.jsrt", "Record", {
	"constructor" : function( )
	{
		this.eze$$XMLRootElementName = "UIGatewayRecord";
		this.eze$$setInitial();
	}
	,
	"eze$$setEmpty": function() {
		this.uiProgramName = "";
		this.data = null;
		this.dataEncoding = 0;
		this.terminated = false;
	}
	,
	"eze$$setInitial": function() {
		this.eze$$setEmpty();
	}
	,
	"eze$$clone": function() {
		var ezert$$1 = this;
		var ezert$$2 = new egl.egl.ui.gateway.UIGatewayRecord();
		ezert$$2.eze$$isNull = this.eze$$isNull;
		ezert$$2.eze$$isNullable = this.eze$$isNullable;
		ezert$$2.uiProgramName = ezert$$1.uiProgramName;
		ezert$$2.data = ezert$$1.data;
		ezert$$2.dataEncoding = ezert$$1.dataEncoding;
		ezert$$2.terminated = ezert$$1.terminated;
		ezert$$2.setNull(ezert$$1.eze$$isNull);
		return ezert$$2;
	}
	,
	"eze$$getName": function() {
		return "egl.ui.gateway.UIGatewayRecord";
	}
	,
	"eze$$getChildVariables": function() {
		return [
		{name: "uiProgramName", value : this.uiProgramName, type : "eglx.lang.EString", jsName : "uiProgramName"},
		{name: "data", value : this.data, type : "eglx.lang.EString", jsName : "data"},
		{name: "dataEncoding", value : this.dataEncoding, type : "egl.ui.gateway.EncodingKind", jsName : "dataEncoding"},
		{name: "terminated", value : this.terminated, type : "eglx.lang.EBoolean", jsName : "terminated"}
		];
	}
	,
	"eze$$getFieldSignatures": function() {
		return [
		{name: "uiProgramName", value : this.uiProgramName, type : "S;"},
		{name: "data", value : this.data, type : "?S;"},
		{name: "dataEncoding", value : this.dataEncoding, type : "Tegl/ui/gateway/encodingkind;"},
		{name: "terminated", value : this.terminated, type : "0;"}
		];
	}
	,
	"eze$$getFORMDataNames": function() {
		return [
		["uiProgramName", "uiProgramName"],
		["data", "data"],
		["dataEncoding", "dataEncoding"],
		["terminated", "terminated"]
		];
	}
	,
	"eze$$getJSONNames": function() {
		return [
		["uiProgramName", "uiProgramName"],
		["data", "data"],
		["dataEncoding", "dataEncoding"],
		["terminated", "terminated"]
		];
	}
	,
	"eze$$fromJson": function( object, ordered ) {
		if (object == null) {
			return;
		}
		this.eze$$setEmpty();
		this.setNull(false);
		if (ordered) {
			var objArray = [];
			for (f in object) {
				objArray.push(object[f]);
			}
			this.uiProgramName = objArray[0];
			this.data = objArray[1];
			this.dataEncoding = egl.convertFloatToInt( objArray[2] );
			this.terminated = objArray[3];
		}
		else {
			egl.eglx.services.$ServiceLib.throwExceptionIfNecessary( object["uiProgramName"], "this.uiProgramName", false );
			this.uiProgramName = object["uiProgramName"];
			egl.eglx.services.$ServiceLib.throwExceptionIfNecessary( object["data"], "this.data", true );
			this.data = object["data"];
			egl.eglx.services.$ServiceLib.throwExceptionIfNecessary( object["dataEncoding"], "this.dataEncoding", false );
			this.dataEncoding = egl.convertFloatToInt( object["dataEncoding"] );
			egl.eglx.services.$ServiceLib.throwExceptionIfNecessary( object["terminated"], "this.terminated", false );
			this.terminated = object["terminated"];
		}
	}
	,
	"eze$$fromXML": function( element, xmlName ) {
		var node, nodelist, value;
		this.eze$$setEmpty();
		if (xmlName == null) {
			xmlName = this.eze$$XMLRootElementName;
		}
		value = egl.eglx.xml.$XmlLib.setSimpleElementField( element,  "uiProgramName" );
		if (value) {
			this.uiProgramName = value;
		}
		value = egl.eglx.xml.$XmlLib.setSimpleElementField( element,  "data" );
		if (value) {
			this.data = value;
		}
		else {
		this.data = null;}
		egl.eglx.xml.$XmlLib.setRecordElementField( element, this.dataEncoding, "dataEncoding" );
		value = egl.eglx.xml.$XmlLib.setSimpleElementField( element,  "terminated" );
		if (value) {
			this.terminated = Boolean(value.toLowerCase() == "true");
		}
	}
	,
	"eze$$toXML": function( namespaces, xmlName ) {
		var prefix;
		if (xmlName == null) {
			prefix = "";
			xmlName = prefix + this.eze$$XMLRootElementName;
		}
		var s = ["<" + xmlName];
		s.push(">");
		s.push(egl.eglx.xml.$XmlLib.writeElement( this.uiProgramName, "uiProgramName"));
		if (this.data != null) {
			s.push(egl.eglx.xml.$XmlLib.writeElement( this.data, "data"));
		}
		s.push( this.dataEncoding.eze$$toXML( namespaces, "dataEncoding" ) );
		s.push(egl.eglx.xml.$XmlLib.writeElement( this.terminated, "terminated"));
		s.push("</" + xmlName + ">");
		return s.join('');
	}
});
