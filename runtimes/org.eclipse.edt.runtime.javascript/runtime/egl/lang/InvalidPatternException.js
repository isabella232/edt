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

egl.createInvalidPatternException = function( /*string*/ messageID, /*string or array*/ inserts )
{
	if (typeof(inserts) != "string") {
		inserts = egl.getRuntimeMessage( messageID, inserts );
	}
	egl.exceptionThrown = true;
	var args = new Array();
	args.push( [ "messageID", messageID || "" ] );
	args.push( [ "message", inserts || "" ] );
	args.push( [ "pattern", arguments[ 2 ] || "" ] );
	return new egl.egl.lang.InvalidPatternException( args );
}

egl.defineClass( "egl.lang", "InvalidPatternException", "egl.lang", "AnyException", {

	//InvalidPatternException requires the following additional data:
	//pattern
	
	"eze$$getChildVariables" : function() {
		var childVars = this.eze$$superClass.prototype.eze$$getChildVariables.call( this );
		childVars.push({name : "pattern", value : this.pattern, type : "eglx.lang.EString"});
		return childVars;
	}
});
