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
package org.eclipse.edt.tests.validation.junit.annotations;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/annotations/throws.egl
 */
public class ThrowsTest extends ValidationTestCase {

	public ThrowsTest() {
		super( "EGLSource/annotations/throws.egl", false );
	}

	/*
	 * e.foo();
	 * 1 validation message is expected.
	 * It is expected to contain "The invocation of the function, constructor or the declaration must be enclosed in a Try Statement that specifies AnyException or JavaObjectException in one of its OnException blocks.".
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The invocation of the function, constructor or the declaration must be enclosed in a Try Statement that specifies AnyException or JavaObjectException in one of its OnException blocks." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The invocation of the function, constructor or the declaration must be enclosed in a Try Statement that specifies AnyException or JavaObjectException in one of its OnException blocks.\" was issued." );
	}

	/*
	 * e.foo();
	 * 0 validation messages are expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * e.foo();
	 * 1 validation message is expected.
	 * It is expected to contain "The invocation of the function, constructor or the declaration must be enclosed in a Try Statement that specifies AnyException or JavaObjectException in one of its OnException blocks.".
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The invocation of the function, constructor or the declaration must be enclosed in a Try Statement that specifies AnyException or JavaObjectException in one of its OnException blocks." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The invocation of the function, constructor or the declaration must be enclosed in a Try Statement that specifies AnyException or JavaObjectException in one of its OnException blocks.\" was issued." );
	}

	/*
	 * e.foo();
	 * 0 validation messages are expected.
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * e.foo();
	 * 0 validation messages are expected.
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * e.foo();
	 * 0 validation messages are expected.
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function foo(){@throws};
	 * 0 validation messages are expected.
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function foo(){@throws};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation Throws is only valid for functions and constructors in an ExternalType with subtype JavaObject.".
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation Throws is only valid for functions and constructors in an ExternalType with subtype JavaObject." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation Throws is only valid for functions and constructors in an ExternalType with subtype JavaObject.\" was issued." );
	}
}
