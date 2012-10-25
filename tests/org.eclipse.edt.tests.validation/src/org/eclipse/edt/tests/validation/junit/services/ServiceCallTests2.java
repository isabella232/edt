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

//	/*
//	 * call myserv.srvfunc();
//	 * 1 validation message is expected.
//	 * It is expected to contain "must specify a \"returning to\" function".
//	 */
//	public void testLine21() {
//		List messages = getMessagesAtLine( 21 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
//	}
//
//	/*
//	 * call myserv.srvfunc();
//	 * 1 validation message is expected.
//	 * It is expected to contain "must specify a \"returning to\" function".
//	 */
//	public void testLine22() {
//		List messages = getMessagesAtLine( 22 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
//	}
//
//	/*
//	 * using "binding:fred"
//	 * 1 validation message is expected.
//	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
//	 */
//	public void testLine25() {
//		List messages = getMessagesAtLine( 25 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
//	}
//
//	/*
//	 * using Resources.getResource( "binding:fred")
//	 * 1 validation message is expected.
//	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
//	 */
//	public void testLine29() {
//		List messages = getMessagesAtLine( 29 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
//	}
//
//	/*
//	 * using Resources.getResource( "binding:fred") as IHTTP
//	 * 0 validation messages are expected.
//	 */
//	public void testLine34() {
//		List messages = getMessagesAtLine( 34 );
//		assertEquals( 0, messages.size() );
//	}
//
//	/*
//	 * call myserv.srvfunc() returns(i);
//	 * 1 validation message is expected.
//	 * It is expected to contain "must specify a \"returning to\" function".
//	 */
//	public void testLine37() {
//		List messages = getMessagesAtLine( 37 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
//	}
//
//	/*
//	 * call myserv.srvfunc() returning to callback;
//	 * 0 validation messages are expected.
//	 */
//	public void testLine39() {
//		List messages = getMessagesAtLine( 39 );
//		assertEquals( 0, messages.size() );
//	}
//
//	/*
//	 * call myserv.srvfunc() returning to callback;
//	 * 0 validation messages are expected.
//	 */
//	public void testLine40() {
//		List messages = getMessagesAtLine( 40 );
//		assertEquals( 0, messages.size() );
//	}
//
//	/*
//	 * call myinter.interfunc();
//	 * 1 validation message is expected.
//	 * It is expected to contain "must specify a \"returning to\" function".
//	 */
//	public void testLine46() {
//		List messages = getMessagesAtLine( 46 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
//	}
//
//	/*
//	 * call myinter.interfunc();
//	 * 1 validation message is expected.
//	 * It is expected to contain "must specify a \"returning to\" function".
//	 */
//	public void testLine47() {
//		List messages = getMessagesAtLine( 47 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
//	}
//
//	/*
//	 * using "binding:fred"
//	 * 1 validation message is expected.
//	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
//	 */
//	public void testLine50() {
//		List messages = getMessagesAtLine( 50 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
//	}
//
//	/*
//	 * using Resources.getResource( "binding:fred")
//	 * 1 validation message is expected.
//	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
//	 */
//	public void testLine54() {
//		List messages = getMessagesAtLine( 54 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
//	}
//
//	/*
//	 * using Resources.getResource( "binding:fred") as IHTTP
//	 * 0 validation messages are expected.
//	 */
//	public void testLine59() {
//		List messages = getMessagesAtLine( 59 );
//		assertEquals( 0, messages.size() );
//	}
//
//	/*
//	 * call myinter.interfunc() returns(i);
//	 * 1 validation message is expected.
//	 * It is expected to contain "must specify a \"returning to\" function".
//	 */
//	public void testLine62() {
//		List messages = getMessagesAtLine( 62 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
//	}
//
//	/*
//	 * call myinter.interfunc() returning to callback;
//	 * 0 validation messages are expected.
//	 */
//	public void testLine64() {
//		List messages = getMessagesAtLine( 64 );
//		assertEquals( 0, messages.size() );
//	}
//
//	/*
//	 * call myinter.interfunc() returning to callback;
//	 * 0 validation messages are expected.
//	 */
//	public void testLine65() {
//		List messages = getMessagesAtLine( 65 );
//		assertEquals( 0, messages.size() );
//	}
//
//	/*
//	 * call myserv.srvfunc();
//	 * 1 validation message is expected.
//	 * It is expected to contain "must specify a \"returning to\" function".
//	 */
//	public void testLine84() {
//		List messages = getMessagesAtLine( 84 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
//	}
//
//	/*
//	 * call myserv.srvfunc();
//	 * 1 validation message is expected.
//	 * It is expected to contain "must specify a \"returning to\" function".
//	 */
//	public void testLine85() {
//		List messages = getMessagesAtLine( 85 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
//	}
//
//	/*
//	 * using "binding:fred"
//	 * 1 validation message is expected.
//	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
//	 */
//	public void testLine88() {
//		List messages = getMessagesAtLine( 88 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
//	}
//
//	/*
//	 * using Resources.getResource( "binding:fred")
//	 * 1 validation message is expected.
//	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
//	 */
//	public void testLine92() {
//		List messages = getMessagesAtLine( 92 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
//	}
//
//	/*
//	 * using Resources.getResource( "binding:fred") as IHTTP
//	 * 0 validation messages are expected.
//	 */
//	public void testLine97() {
//		List messages = getMessagesAtLine( 97 );
//		assertEquals( 0, messages.size() );
//	}
//
//	/*
//	 * call myserv.srvfunc() returns(i);
//	 * 1 validation message is expected.
//	 * It is expected to contain "must specify a \"returning to\" function".
//	 */
//	public void testLine100() {
//		List messages = getMessagesAtLine( 100 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
//	}
//
//	/*
//	 * call myserv.srvfunc() returning to callback;
//	 * 0 validation messages are expected.
//	 */
//	public void testLine102() {
//		List messages = getMessagesAtLine( 102 );
//		assertEquals( 0, messages.size() );
//	}
//
//	/*
//	 * call myserv.srvfunc() returning to callback;
//	 * 0 validation messages are expected.
//	 */
//	public void testLine103() {
//		List messages = getMessagesAtLine( 103 );
//		assertEquals( 0, messages.size() );
//	}
//
//	/*
//	 * call myinter.interfunc();
//	 * 1 validation message is expected.
//	 * It is expected to contain "must specify a \"returning to\" function".
//	 */
//	public void testLine109() {
//		List messages = getMessagesAtLine( 109 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
//	}
//
//	/*
//	 * call myinter.interfunc();
//	 * 1 validation message is expected.
//	 * It is expected to contain "must specify a \"returning to\" function".
//	 */
//	public void testLine110() {
//		List messages = getMessagesAtLine( 110 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
//	}
//
//	/*
//	 * using "binding:fred"
//	 * 1 validation message is expected.
//	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
//	 */
//	public void testLine113() {
//		List messages = getMessagesAtLine( 113 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
//	}
//
//	/*
//	 * using Resources.getResource( "binding:fred")
//	 * 1 validation message is expected.
//	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
//	 */
//	public void testLine117() {
//		List messages = getMessagesAtLine( 117 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
//	}
//
//	/*
//	 * using Resources.getResource( "binding:fred") as IHTTP
//	 * 0 validation messages are expected.
//	 */
//	public void testLine122() {
//		List messages = getMessagesAtLine( 122 );
//		assertEquals( 0, messages.size() );
//	}
//
//	/*
//	 * call myinter.interfunc() returns(i);
//	 * 1 validation message is expected.
//	 * It is expected to contain "must specify a \"returning to\" function".
//	 */
//	public void testLine125() {
//		List messages = getMessagesAtLine( 125 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
//	}
//
//	/*
//	 * call myinter.interfunc() returning to callback;
//	 * 0 validation messages are expected.
//	 */
//	public void testLine127() {
//		List messages = getMessagesAtLine( 127 );
//		assertEquals( 0, messages.size() );
//	}
//
//	/*
//	 * call myinter.interfunc() returning to callback;
//	 * 0 validation messages are expected.
//	 */
//	public void testLine128() {
//		List messages = getMessagesAtLine( 128 );
//		assertEquals( 0, messages.size() );
//	}
}
