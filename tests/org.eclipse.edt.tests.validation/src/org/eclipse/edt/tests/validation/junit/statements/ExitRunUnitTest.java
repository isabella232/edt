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
package org.eclipse.edt.tests.validation.junit.statements;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/statements/exitRunUnit.egl
 */
public class ExitRunUnitTest extends ValidationTestCase {

	public ExitRunUnitTest() {
		super( "EGLSource/statements/exitRunUnit.egl", false );
	}

	/*
	 * exit program;
	 * 1 validation message is expected.
	 * It is expected to contain "The PROGRAM exit modifier may only be used in a program".
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The PROGRAM exit modifier may only be used in a program" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The PROGRAM exit modifier may only be used in a program\" was issued." );
	}

	/*
	 * exit program;
	 * 2 validation messages are expected.
	 * One message is expected to contain "The PROGRAM exit modifier may only be used in a program".
	 * One message is expected to contain "Unreachable code".
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The PROGRAM exit modifier may only be used in a program" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The PROGRAM exit modifier may only be used in a program\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "Unreachable code" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Unreachable code\" was issued." );
	}

	/*
	 * exit rununit;
	 * 0 validation messages are expected.
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * exit rununit( i );
	 * 0 validation messages are expected.
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * exit program;
	 * 1 validation message is expected.
	 * It is expected to contain "The PROGRAM exit modifier may only be used in a program".
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The PROGRAM exit modifier may only be used in a program" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The PROGRAM exit modifier may only be used in a program\" was issued." );
	}

	/*
	 * exit program;
	 * 2 validation messages are expected.
	 * One message is expected to contain "The PROGRAM exit modifier may only be used in a program".
	 * One message is expected to contain "Unreachable code".
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The PROGRAM exit modifier may only be used in a program" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The PROGRAM exit modifier may only be used in a program\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "Unreachable code" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Unreachable code\" was issued." );
	}

	/*
	 * exit rununit;
	 * 1 validation message is expected.
	 * It is expected to contain "The RUNUNIT exit modifier may not be used in a service".
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The RUNUNIT exit modifier may not be used in a service" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The RUNUNIT exit modifier may not be used in a service\" was issued." );
	}

	/*
	 * exit rununit( i );
	 * 1 validation message is expected.
	 * It is expected to contain "The RUNUNIT exit modifier may not be used in a service".
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The RUNUNIT exit modifier may not be used in a service" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The RUNUNIT exit modifier may not be used in a service\" was issued." );
	}
}
