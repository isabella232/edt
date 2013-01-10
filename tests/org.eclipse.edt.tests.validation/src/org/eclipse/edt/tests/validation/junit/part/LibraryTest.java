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
 * A JUnit test case for the file EGLSource/part/library.egl
 */
public class LibraryTest extends ValidationTestCase {

	public LibraryTest() {
		super( "EGLSource/part/library.egl", false );
	}

	/*
	 * Zi int;
	 * 0 validation messages are expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const Zj int = 5;
	 * 0 validation messages are expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ints int[],
	 * 0 validation messages are expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * m Zmyrec,
	 * 0 validation messages are expected.
	 */
	public void testLine22() {
		List messages = getMessagesAtLine( 22 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * n number in ,
	 * 0 validation messages are expected.
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * n1 number out
	 * 0 validation messages are expected.
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ,dfg Zyyyy
	 * 1 validation message is expected.
	 * It is expected to contain "The type Zyyyy is not a valid type for a data declaration.".
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type Zyyyy is not a valid type for a data declaration." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type Zyyyy is not a valid type for a data declaration.\" was issued." );
	}

	/*
	 * const i int = 5;
	 * 0 validation messages are expected.
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * noparam();
	 * 0 validation messages are expected.
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function noparam() returns (int[])
	 * 0 validation messages are expected.
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function returnsprogram ()returns (Zyyyy)
	 * 1 validation message is expected.
	 * It is expected to contain "The type Zyyyy is not a valid type for a data declaration.".
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type Zyyyy is not a valid type for a data declaration." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type Zyyyy is not a valid type for a data declaration.\" was issued." );
	}

	/*
	 * function numberfunc (f number)
	 * 0 validation messages are expected.
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function mult (ZZp int, ZZp int )
	 * 1 validation message is expected.
	 * It is expected to contain "The same name zzp also appears as variable, parameter, use or constant declaration in part mult.".
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The same name zzp also appears as variable, parameter, use or constant declaration in part mult." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The same name zzp also appears as variable, parameter, use or constant declaration in part mult.\" was issued." );
	}

	/*
	 * i int = 5;
	 * 0 validation messages are expected.
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const j int = 6;
	 * 0 validation messages are expected.
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * library Zmymain
	 * 0 validation messages are expected.
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function returnsprogram ()returns (Zyyyy)
	 * 1 validation message is expected.
	 * It is expected to contain "The type Zyyyy is not a valid type for a data declaration.".
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type Zyyyy is not a valid type for a data declaration." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type Zyyyy is not a valid type for a data declaration.\" was issued." );
	}
}
