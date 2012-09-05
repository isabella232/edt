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
public class ServiceCallTest1Test extends ValidationTestCase {

	public ServiceCallTest1Test() {
		super( "EGLSource/services/ServiceCallTests.egl", false );
	}

	/*
	 * call field1(i1) using httpProxy;//wrong target type;
	 * 1 validation message is expected.
	 * It is expected to contain "The target of the Call must be a Service or proxy function".
	 */
	public void testLine10() {
		List messages = getMessagesAtLine( 10 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The target of the Call must be a Service or proxy function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The target of the Call must be a Service or proxy function\" was issued." );
	}

	/*
	 * call field1(i1) using httpRest;//wrong target type;
	 * 1 validation message is expected.
	 * It is expected to contain "The target of the Call must be a Service or proxy function".
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 10 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The target of the Call must be a Service or proxy function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The target of the Call must be a Service or proxy function\" was issued." );
	}

	/*
	 * call fp8(i1) using httpRest;//missing returning to or returns;
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a "returning to" or "returns" function".
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function\" was issued." );
	}

	/*
	 * call fp8(i1) using httpProxy;//missing returning to or returns;
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a "returning to" or "returns" function".
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function\" was issued." );
	}

	/*
	 * call fp8(i1) using httpProxy returning to response1;//wrong returning to type;
	 * 1 validation message is expected.
	 * It is expected to contain "The type rec1 cannot be passed to the parameter p1 of the function response1. It is not assignment compatible with boolean.".
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type rec1 cannot be passed to the parameter p1 of the function response1. It is not assignment compatible with boolean." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type rec1 cannot be passed to the parameter p1 of the function response1. It is not assignment compatible with boolean.\" was issued." );
	}

	/*
	 * call fp13(i1) using httpProxy returning to response6;//wrong returning to type;
	 * 1 validation message is expected.
	 * It is expected to contain "The type int cannot be passed to the parameter p3 of the function response6. It is not assignment compatible with boolean.".
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type int cannot be passed to the parameter p3 of the function response6. It is not assignment compatible with boolean." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type int cannot be passed to the parameter p3 of the function response6. It is not assignment compatible with boolean.\" was issued." );
	}

	/*
	 * call fp7(i1) using httpProxy onexception response1;// wrong type in onexception handler;
	 * 1 validation message is expected.
	 * It is expected to contain "The parameter at position 1 of function response1 must have a type of eglx.lang.AnyException.".
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The parameter at position 1 of function response1 must have a type of eglx.lang.AnyException." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The parameter at position 1 of function response1 must have a type of eglx.lang.AnyException.\" was issued." );
	}

	/*
	 * call fp8(i1) using httpProxy returning to response2;//returning to function has out types
	 * 1 validation message is expected.
	 * It is expected to contain "All of the parameters in "returning to" or "onexception" function response2 must be defined with the IN modifier.".
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "All of the parameters in \"returning to\" or \"onexception\" function response2 must be defined with the IN modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"All of the parameters in \"returning to\" or \"onexception\" function response2 must be defined with the IN modifier.\" was issued." );
	}

	/*
	 * call fp8(i1) using httpProxy returning to response3;//returning to function has inout types
	 * 1 validation message is expected.
	 * It is expected to contain "All of the parameters in "returning to" or "onexception" function response3 must be defined with the IN modifier.".
	 */
	public void testLine52() {
		List messages = getMessagesAtLine( 52 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "All of the parameters in \"returning to\" or \"onexception\" function response3 must be defined with the IN modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"All of the parameters in \"returning to\" or \"onexception\" function response3 must be defined with the IN modifier.\" was issued." );
	}

	/*
	 * call fp8(i1) using httpProxy returning to field1;//returning to is a field not function
	 * 1 validation message is expected.
	 * It is expected to contain "All of the parameters in "The "returning to" or "onexception" expression must resolve to a function.".
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The \"returning to\" or \"onexception\" expression must resolve to a function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The \"returning to\" or \"onexception\" expression must resolve to a function.\" was issued." );
	}

	/*
	 * call fp8(i1) using httpProxy returning to response4;// wrong type in onexception handler
	 * 1 validation message is expected.
	 * It is expected to contain "All of the parameters in "The "returning to" or "onexception" expression must resolve to a function.".
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The \"returning to\" or \"onexception\" function response4 cannot return a type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The \"returning to\" or \"onexception\" function response4 cannot return a type.\" was issued." );
	}

	/*
	 * call lib1.fp1(i1) using httpProxy returning to lib1.response1;//callback handler in the wrong part
	 * 1 validation message is expected.
	 * It is expected to contain "The function response1 must be defined in the part pgm1.".
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function response1 must be defined in the part pgm1." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function response1 must be defined in the part pgm1.\" was issued." );
	}

	/*
	 * call fp7(i1) using httpProxy returning to response5;
	 * 1 validation message is expected.
	 * It is expected to contain "The "returning to" or "onexception" function response5 requires 0 parameter(s).".
	 */
	public void testLine72() {
		List messages = getMessagesAtLine( 72 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The \"returning to\" or \"onexception\" function response5 requires 0 parameter(s)." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The \"returning to\" or \"onexception\" function response5 requires 0 parameter(s).\" was issued." );
	}

	/*
	 * call fp13(s1, i1, i1) using httpProxy returning to response7;//proxy function accessed as a static in the wrong part
	 * 1 validation message is expected.
	 * It is expected to contain "The argument i1 cannot be passed to the in or out parameter p2 of the function fp13. The types int and rec1 are not assignment compatible.".
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The argument i1 cannot be passed to the in or out parameter p2 of the function fp13. The types int and rec1 are not assignment compatible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The argument i1 cannot be passed to the in or out parameter p2 of the function fp13. The types int and rec1 are not assignment compatible.\" was issued." );
	}

	/*
	 * call fp13(i1) using httpProxy returning to response6;//wrong number of parameters in the call
	 * 1 validation message is expected.
	 * It is expected to contain "The number of arguments in the call statement '1' must be the same as the number of parameters '3' in the target function fp13.".
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The number of arguments in the call statement '1' must be the same as the number of parameters '3' in the target function fp13." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The number of arguments in the call statement '1' must be the same as the number of parameters '3' in the target function fp13.\" was issued." );
	}

	/*
	 * call HANDLER1.fp1(i1) using httpProxy returning to response5;//proxy function accessed as a static in the wrong part
	 * 1 validation message is expected.
	 * It is expected to contain "Only a library or service part can be use as a qualifier.".
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Only a library or service part can be use as a qualifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Only a library or service part can be use as a qualifier.\" was issued." );
	}

	/*
	 * call fp8(i1) using httpProxy onexception response8;//bad onexception function
	 * 1 validation message is expected.
	 * It is expected to contain "The "returning to" or "onexception" function response8 requires 1 parameter(s).".
	 */
	public void testLine94() {
		List messages = getMessagesAtLine( 94 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The \"returning to\" or \"onexception\" function response8 requires 1 parameter(s)." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The \"returning to\" or \"onexception\" function response8 requires 1 parameter(s).\" was issued." );
	}
}
