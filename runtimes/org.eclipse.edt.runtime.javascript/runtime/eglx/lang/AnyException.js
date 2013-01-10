/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

//factory for AnyException
//required because AnyException is a subclass of Record
//agruments for Record is an array of pairs, representing fields
egl.createAnyException = function( /*string*/ messageID, /*string or array*/ inserts )
{
	if (typeof(inserts) != "string") {
		inserts = egl.getRuntimeMessage( messageID, inserts );
	}

	var args = new Array();
	args.push( [ "messageID", messageID || "" ] );
	args.push( [ "message", inserts || "" ] );
	// uncomment the following line to see exceptions being swallowed
	// egl.println("Exception: "+inserts+" "+this);
	return new egl.eglx.lang.AnyException( args );
};

egl.defineClass( "eglx.lang", "AnyException", "egl.jsrt", "Record", {
	
	//to create an exception in the runtime:
	//	var messageID = "EGLXXXX";
	//	var msgInserts = [ insert1, insert2, ...];
	//	var ex = egl.createNameOfException( messageID, msgInserts );
	//	throw ex;
	//
	//or, more simply:
	//	throw egl.createNameOfException( "EGLXXXX", [ insert1, insert2, ...], value 
	//where "value" is an exception-specific field. There can be more than one value.
	//
	//if you already have the message and don't want to look it up:
	//	throw new egl.createNameOfException( "EGLXXXX", "message" );
	//the second argument is differentiated as being a string, not an array
	//
	//any arguments are optional, since they set exception-specific data
	
	
	"constructor" : function( messageID, inserts ) {
		//subclass of Record
		//call superclass constructor
		this.eze$$isNullable = true;
		this.messageID = this.messageID || "";
		this.message = this.message || "";
	},
	
	"getValue" : function() {
		return this;
	},
	
	"toString" : function() {
		return this.message;
	},
	
	"toJSONString" : function() {
		var s = "{";
		var i = 0;
		for (var f in this) {
			if ( typeof f == "function" ) continue; //ignore functions
			if (i>0) s += ",";
			i++;
			s += "\"" + f + "\":" + this[f];
		}
		return s + "}";
	},
	
	"eze$$getName": function() {
		return "AnyException";
	},
	
	"eze$$getChildVariables": function() {
		var eze$$parent = this;
		return [
		{name: "message", value : eze$$parent.message, type : "eglx.lang.EString", jsName : "message"},
		{name: "messageID", value : eze$$parent.messageID, type : "eglx.lang.EString", jsName : "messageID"}
		];
	}
	
});
