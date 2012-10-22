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
 * A JUnit test case for the file EGLSource/ibmi/IBMiTest1.egl
 */
public class ServiceCallTests1 extends ValidationTestCase {

	public ServiceCallTests1() {
		super( "EGLSource/services/ServiceCallTests1.egl", false );
	}

//	/*
//	 * call field1(i1) using httpProxy;
//	 * 1 validation message is expected.
//	 * It is expected to contain "wrong target type".
//	 */
//	public void testLine13() {
//		List messages = getMessagesAtLine( 13 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "wrong target type" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"wrong target type\" was issued." );
//	}
//
//	/*
//	 * call field1(i1) using httpRest;
//	 * 1 validation message is expected.
//	 * It is expected to contain "wrong target type".
//	 */
//	public void testLine18() {
//		List messages = getMessagesAtLine( 18 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "wrong target type" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"wrong target type\" was issued." );
//	}
//
//	/*
//	 * call fp8(i1) using httpRest;
//	 * 1 validation message is expected.
//	 * It is expected to contain "missing returning to or returns".
//	 */
//	public void testLine23() {
//		List messages = getMessagesAtLine( 23 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "missing returning to or returns" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"missing returning to or returns\" was issued." );
//	}
//
//	/*
//	 * call fp8(i1) using httpProxy;
//	 * 1 validation message is expected.
//	 * It is expected to contain "missing returning to or returns".
//	 */
//	public void testLine28() {
//		List messages = getMessagesAtLine( 28 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "missing returning to or returns" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"missing returning to or returns\" was issued." );
//	}
//
//	/*
//	 * call fp8(i1) using httpProxy returning to response1;
//	 * 1 validation message is expected.
//	 * It is expected to contain "wrong returning to type".
//	 */
//	public void testLine33() {
//		List messages = getMessagesAtLine( 33 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "wrong returning to type" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"wrong returning to type\" was issued." );
//	}
//
//	/*
//	 * call fp13(s1, r1, i1) using httpProxy returning to response6;
//	 * 1 validation message is expected.
//	 * It is expected to contain "wrong returning to type".
//	 */
//	public void testLine40() {
//		List messages = getMessagesAtLine( 40 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "wrong returning to type" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"wrong returning to type\" was issued." );
//	}
//
//	/*
//	 * call fp7(i1) using httpProxy onexception response1;
//	 * 1 validation message is expected.
//	 * It is expected to contain "wrong type in onexception handler".
//	 */
//	public void testLine45() {
//		List messages = getMessagesAtLine( 45 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "wrong type in onexception handler" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"wrong type in onexception handler\" was issued." );
//	}
//
//	/*
//	 * call fp8(i1) using httpProxy returning to response2;
//	 * 1 validation message is expected.
//	 * It is expected to contain "returning to function has out types".
//	 */
//	public void testLine50() {
//		List messages = getMessagesAtLine( 50 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "returning to function has out types" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"returning to function has out types\" was issued." );
//	}
//
//	/*
//	 * call fp8(i1) using httpProxy returning to response3;
//	 * 1 validation message is expected.
//	 * It is expected to contain "returning to function has inout types".
//	 */
//	public void testLine55() {
//		List messages = getMessagesAtLine( 55 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "returning to function has inout types" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"returning to function has inout types\" was issued." );
//	}
//
//	/*
//	 * call fp8(i1) using httpProxy returning to field1;
//	 * 1 validation message is expected.
//	 * It is expected to contain "returning to is a field not function".
//	 */
//	public void testLine60() {
//		List messages = getMessagesAtLine( 60 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "returning to is a field not function" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"returning to is a field not function\" was issued." );
//	}
//
//	/*
//	 * call fp8(i1) using httpProxy returning to response4;
//	 * 1 validation message is expected.
//	 * It is expected to contain "wrong type in onexception handler".
//	 */
//	public void testLine65() {
//		List messages = getMessagesAtLine( 65 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "wrong type in onexception handler" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"wrong type in onexception handler\" was issued." );
//	}
//
//	/*
//	 * call lib1.fp1(i1) using httpProxy returning to lib1.response1;
//	 * 1 validation message is expected.
//	 * It is expected to contain "callback handler in the wrong part".
//	 */
//	public void testLine70() {
//		List messages = getMessagesAtLine( 70 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "callback handler in the wrong part" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"callback handler in the wrong part\" was issued." );
//	}
//
//	/*
//	 * call fp13(s1, i1, i1) using httpProxy returning to response7;
//	 * 1 validation message is expected.
//	 * It is expected to contain "wrong parameter in the call".
//	 */
//	public void testLine82() {
//		List messages = getMessagesAtLine( 82 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "wrong parameter in the call" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"wrong parameter in the call\" was issued." );
//	}
//
//	/*
//	 * call fp13(i1) using httpProxy returning to response7;
//	 * 1 validation message is expected.
//	 * It is expected to contain "wrong number of parameters in the call".
//	 */
//	public void testLine87() {
//		List messages = getMessagesAtLine( 87 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "wrong number of parameters in the call" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"wrong number of parameters in the call\" was issued." );
//	}
//
//	/*
//	 * call HANDLER1.fp1(i1) using httpProxy returning to response5;
//	 * 1 validation message is expected.
//	 * It is expected to contain "proxy function accessed as a static in the wrong part".
//	 */
//	public void testLine92() {
//		List messages = getMessagesAtLine( 92 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "proxy function accessed as a static in the wrong part" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"proxy function accessed as a static in the wrong part\" was issued." );
//	}
//
//	/*
//	 * call fp7(i1) using httpProxy onexception response8;
//	 * 1 validation message is expected.
//	 * It is expected to contain "bad onexception function".
//	 */
//	public void testLine97() {
//		List messages = getMessagesAtLine( 97 );
//		assertEquals( 1, messages.size() );
//		
//		Object messageWithSubstring = messageWithSubstring( messages, "bad onexception function" );
//		if( messageWithSubstring == null ) fail( "No message with substring \"bad onexception function\" was issued." );
//	}
}
