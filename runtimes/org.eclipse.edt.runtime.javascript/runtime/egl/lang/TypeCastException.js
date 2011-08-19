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

egl.createTypeCastException = function( /*string*/ messageID, /*string or array*/ inserts )
{
	if (typeof(inserts) != "string") {
		inserts = egl.getRuntimeMessage( messageID, inserts );
	}
	egl.exceptionThrown = true;
	var args = new Array();
	args.push( [ "messageID", messageID || "" ] );
	args.push( [ "message", inserts || "" ] );
	args.push( [ "castToName", arguments[ 2 ] || "" ] );
	args.push( [ "actualTypeName", arguments[ 3 ] || "" ] );
	return new egl.egl.lang.TypeCastException( args );
}

egl.defineClass( "egl.lang", "TypeCastException", "egl.lang", "AnyException", {
	
	//TypeCastException requires the following additional data:
	//castToName
	//actualTypeName
	
	"eze$$getChildVariables" : function() {
		var childVars = this.eze$$superClass.prototype.eze$$getChildVariables.call( this );
		childVars.push({name : "castToName", value : this.castToName, type : "eglx.lang.EString"});
		childVars.push({name : "actualTypeName", value : this.actualTypeName, type : "eglx.lang.EString"});
		return childVars;
	}
	
});
