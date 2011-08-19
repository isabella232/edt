/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

egl.createInvocationException = function( /*string*/ messageID, /*string or array*/ inserts )
{
	if (typeof(inserts) != "string") {
		inserts = egl.getRuntimeMessage( messageID, inserts );
	}
	egl.exceptionThrown = true;
	var args = new Array();
	args.push( [ "messageID", messageID || "" ] );
	args.push( [ "message", inserts || "" ] );
	args.push( [ "name", arguments[ 2 ] || "" ] );
	args.push( [ "returnValue", arguments[ 3 ] || 0 ] );
	return new egl.egl.lang.InvocationException( args );
}

egl.defineClass( "egl.lang", "InvocationException", "egl.lang", "AnyException", {
	
	//InvocationException requires the following additional data:
	//name
	//returnValue
	
	"constructor" : function() {
		//first two arguments are forwarded to superclass constructor
		this.name = "";
		this.returnValue = 0;
		
		if (arguments.length == 4) {
			this.name = arguments[2];
			this.returnValue = arguments[3];
		}
	},
	
	"eze$$getChildVariables" : function() {
		var childVars = this.eze$$superClass.prototype.eze$$getChildVariables.call( this );
		childVars.push({name : "name", value : this.name, type : "eglx.lang.EString"});
		childVars.push({name : "returnValue", value : this.returnValue, type : "eglx.lang.EInt"});
		return childVars;
	}

});
