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

egl.createRuntimeExceptionArgs = function( /*string*/ messageID, /*string or array*/ inserts )
{
	if (typeof(inserts) != "string") {
		inserts = egl.getRuntimeMessage( messageID, inserts );
	}
	egl.exceptionThrown = true;
	var args = new Array();
	args.push( [ "messageID", messageID || "" ] );
	args.push( [ "message", inserts || "" ] );
	return args;
};
egl.createRuntimeException = function( /*string*/ messageID, /*string or array*/ inserts )
{
	return new egl.eglx.lang.AnyException(egl.createRuntimeExceptionArgs(messageID, inserts) );
};

/* FUTURE sbg At this time, we don't have a need for RuntimeException;  the thinking is that
 * simply throwing AnyException is sufficient in EDT.  However, I'm leaving this here in case
 * we decide to add it;  note that it would also require changing egl.createRuntimeException
 * accordingly....
egl.defineClass( "eglx.javascript", "RuntimeException", "eglx.lang", "AnyException", {} );
 */  
