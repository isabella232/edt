/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.tests.validation.junit.part;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/part/class.egl
 */
public class ClassTest extends ValidationTestCase {

	public ClassTest() {
		super( "EGLSource/part/class.egl", false );
	}

	/*
	 * class loop1 extends loop2
	 * 1 validation message is expected.
	 */
	public void testLine12() {
		List messages = getMessagesAtLine( 12 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * class loop2 extends loop3
	 * 1 validation message is expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * class loop3 extends loop1
	 * 1 validation message is expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * class me extends me
	 * 1 validation message is expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * private function f1(p1 int)
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot reduce the visiblity".
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot reduce the visiblity" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot reduce the visiblity\" was issued." );
	}

	/*
	 * function f1(p1 string) returns(int)
	 * 1 validation message is expected.
	 * It is expected to contain "The return type is incompatible".
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The return type is incompatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The return type is incompatible\" was issued." );
	}

	/*
	 * private function f2(p1 string) returns (class1)
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot reduce the visiblity".
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot reduce the visiblity" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot reduce the visiblity\" was issued." );
	}

	/*
	 * function f2(p1 int)
	 * 1 validation message is expected.
	 * It is expected to contain "The return type is incompatible".
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The return type is incompatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The return type is incompatible\" was issued." );
	}

	/*
	 * super.f1();
	 * 1 validation message is expected.
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * super.f2();
	 * 0 validation messages are expected.
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 0, messages.size() );
	}
}
