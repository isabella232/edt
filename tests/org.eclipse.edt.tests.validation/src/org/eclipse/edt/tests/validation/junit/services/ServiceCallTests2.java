/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.tests.validation.junit.services;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/callStatement/RemoteFunctionCallTests.egl
 */
public class ServiceCallTests2 extends ValidationTestCase {

	public ServiceCallTests2() {
		super( "EGLSource/services/ServiceCallTests2.egl", false );
	}

	/*
	 * call myserv.srvfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a "returning to" or "returns" function.".
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * call servVar.srvfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a "returning to" or "returns" function.".
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'string' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred") as IHTTP
	 * 0 validation messages are expected.
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using syslib.getResource( "binding:fred") as IHTTP
	 * 0 validation messages are expected.
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc() returns(i);
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine35() {
		List messages = getMessagesAtLine( 35 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc() returns(i);
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 0, messages.size() );
	}


	/*
	 * call myinter.interfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a \"returning to\" or \"returns\" function.h".
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * call intervar.interfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a \"returning to\" or \"returns\" function.".
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" functionThe call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'string' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine45() {
		List messages = getMessagesAtLine( 45 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred") as IHTTP
	 * 0 validation messages are expected.
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc() returns(i);
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call intervar.interfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a \"returning to\" or \"returns\" function.".
	 */
	public void testLine80() {
		List messages = getMessagesAtLine( 80 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * call servVar.srvfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a \"returning to\" or \"returns\" function.".
	 */
	public void testLine81() {
		List messages = getMessagesAtLine( 81 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'string' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred") as IHTTP
	 * 0 validation messages are expected.
	 */
	public void testLine92() {
		List messages = getMessagesAtLine( 92 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc() returns(i);
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine96() {
		List messages = getMessagesAtLine( 96 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine98() {
		List messages = getMessagesAtLine( 98 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call servVar.srvfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine99() {
		List messages = getMessagesAtLine( 99 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a "returning to" or "returns" function.".
	 */
	public void testLine105() {
		List messages = getMessagesAtLine( 105 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * call intervar.interfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a "returning to" or "returns" function.".
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine108() {
		List messages = getMessagesAtLine( 108 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred") as IHTTP
	 * 0 validation messages are expected.
	 */
	public void testLine117() {
		List messages = getMessagesAtLine( 117 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc() returns(i);
	 * 1 validation message is expected.
	 */
	public void testLine121() {
		List messages = getMessagesAtLine( 121 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine123() {
		List messages = getMessagesAtLine( 123 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call intervar.interfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine124() {
		List messages = getMessagesAtLine( 124 );
		assertEquals( 0, messages.size() );
	}
}
