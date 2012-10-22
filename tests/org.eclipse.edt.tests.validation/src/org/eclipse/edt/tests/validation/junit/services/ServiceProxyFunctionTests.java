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
public class ServiceProxyFunctionTests extends ValidationTestCase {

	public ServiceProxyFunctionTests() {
		super( "EGLSource/services/ServiceProxyFunctionTests.egl", false );
	}

	/*
	 * function f1() {@REST {method=POST}};
	 * 1 validation message is expected.
	 * It is expected to contain "container for the REST function f1 is invalid".
	 */
	public void testLine5() {
		List messages = getMessagesAtLine( 5 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "container for the REST function f1 is invalid" );
		if( messageWithSubstring == null ) fail( "No message with substring \"container for the REST function f1 is invalid\" was issued." );
	}

	/*
	 * function f2() {@EGLService {}};
	 * 1 validation message is expected.
	 * It is expected to contain "container for the EGLService function f1 is invalid".
	 */
	public void testLine6() {
		List messages = getMessagesAtLine( 6 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "container for the EGLService function f1 is invalid" );
		if( messageWithSubstring == null ) fail( "No message with substring \"container for the EGLService function f1 is invalid\" was issued." );
	}

	/*
	 * function f1() {@REST {method=_GET}};
	 * 1 validation message is expected.
	 * It is expected to contain "container for the REST function f1 is invalid".
	 */
	public void testLine10() {
		List messages = getMessagesAtLine( 10 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "container for the REST function f1 is invalid" );
		if( messageWithSubstring == null ) fail( "No message with substring \"container for the REST function f1 is invalid\" was issued." );
	}

	/*
	 * function f2() {@EGLService {}};
	 * 1 validation message is expected.
	 * It is expected to contain "container for the EGLService function f1 is invalid".
	 */
	public void testLine11() {
		List messages = getMessagesAtLine( 11 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "container for the EGLService function f1 is invalid" );
		if( messageWithSubstring == null ) fail( "No message with substring \"container for the EGLService function f1 is invalid\" was issued." );
	}

	/*
	 * {@REST{}}
	 * 1 validation message is expected.
	 * It is expected to contain "requires http method".
	 */
	public void testLine22() {
		List messages = getMessagesAtLine( 22 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "requires http method" );
		if( messageWithSubstring == null ) fail( "No message with substring \"requires http method\" was issued." );
	}

	/*
	 * function fp2 (p1 int in)
	 * 1 validation message is expected.
	 * It is expected to contain "wrong type".
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "wrong type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"wrong type\" was issued." );
	}

	/*
	 * function fp3 (p1 string)
	 * 1 validation message is expected.
	 * It is expected to contain "missing in modifier".
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "missing in modifier" );
		if( messageWithSubstring == null ) fail( "No message with substring \"missing in modifier\" was issued." );
	}

	/*
	 * function fp4 (p1 string in) returns(boolean)
	 * 1 validation message is expected.
	 * It is expected to contain "wrong returns type".
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "wrong returns type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"wrong returns type\" was issued." );
	}

	/*
	 * function fp5(p1 string in) returns(string)
	 * 1 validation message is expected.
	 * It is expected to contain "response format should be null".
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "response format should be null" );
		if( messageWithSubstring == null ) fail( "No message with substring \"response format should be null\" was issued." );
	}

	/*
	 * function fp6(p1 string in, p2 string in)
	 * 2 validation messages are expected.
	 * One message is expected to contain "resources".
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "resources" );
		if( messageWithSubstring == null ) fail( "No message with substring \"resources\" was issued." );
	}

	/*
	 * function fp7 (p1 string in)
	 * 1 validation message is expected.
	 * It is expected to contain "wrong request format".
	 */
	public void testLine45() {
		List messages = getMessagesAtLine( 45 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "wrong request format" );
		if( messageWithSubstring == null ) fail( "No message with substring \"wrong request format\" was issued." );
	}

	/*
	 * function fp8 (p1 rec1 in)
	 * 1 validation message is expected.
	 * It is expected to contain "wrong request format".
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "wrong request format" );
		if( messageWithSubstring == null ) fail( "No message with substring \"wrong request format\" was issued." );
	}

	/*
	 * function fp9 (p1 handler1 in)
	 * 1 validation message is expected.
	 * It is expected to contain "wrong request format".
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "wrong request format" );
		if( messageWithSubstring == null ) fail( "No message with substring \"wrong request format\" was issued." );
	}

	/*
	 * function fp10(p1 string in)
	 * 1 validation message is expected.
	 * It is expected to contain "resources not allowed".
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "resources not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"resources not allowed\" was issued." );
	}

	/*
	 * function fp11(p1 string in)
	 * 1 validation message is expected.
	 * It is expected to contain "substitution does not match".
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "substitution does not match" );
		if( messageWithSubstring == null ) fail( "No message with substring \"substitution does not match\" was issued." );
	}

	/*
	 * function fp12(p1 rec1 in)
	 * 1 validation message is expected.
	 * It is expected to contain "substitution wrong type".
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "substitution wrong type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"substitution wrong type\" was issued." );
	}

	/*
	 * function fp14(p1 string in)
	 * 1 validation message is expected.
	 * It is expected to contain "no http method".
	 */
	public void testLine74() {
		List messages = getMessagesAtLine( 74 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no http method" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no http method\" was issued." );
	}

	/*
	 * function fp15(p1 string in)
	 * 1 validation message is expected.
	 * It is expected to contain "form not allowed".
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "form not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"form not allowed\" was issued." );
	}
}
