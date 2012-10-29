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
package org.eclipse.edt.tests.validation.junit.bugs;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/bugs/Bug376417.egl
 */
public class Bug376417Test extends ValidationTestCase {

	public Bug376417Test() {
		super( "EGLSource/bugs/Bug376417.egl", false );
	}

	/*
	 * fi(iNull);
	 * 1 validation message is expected.
	 * It is expected to contain "not reference compatible".
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not reference compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not reference compatible\" was issued." );
	}

	/*
	 * fiNull(i);
	 * 1 validation message is expected.
	 * It is expected to contain "not reference compatible".
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not reference compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not reference compatible\" was issued." );
	}

	/*
	 * fl(lNull);
	 * 1 validation message is expected.
	 * It is expected to contain "not reference compatible".
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not reference compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not reference compatible\" was issued." );
	}

	/*
	 * flNull(l);
	 * 1 validation message is expected.
	 * It is expected to contain "not reference compatible".
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not reference compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not reference compatible\" was issued." );
	}

	/*
	 * fet(etNull);
	 * 1 validation message is expected.
	 * It is expected to contain "not reference compatible".
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not reference compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not reference compatible\" was issued." );
	}

	/*
	 * fetNull(et);
	 * 1 validation message is expected.
	 * It is expected to contain "not reference compatible".
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not reference compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not reference compatible\" was issued." );
	}

	/*
	 * ft(tNull);
	 * 1 validation message is expected.
	 * It is expected to contain "not reference compatible".
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not reference compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not reference compatible\" was issued." );
	}

	/*
	 * ftNull(t);
	 * 1 validation message is expected.
	 * It is expected to contain "not reference compatible".
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not reference compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not reference compatible\" was issued." );
	}

	/*
	 * fs(sNull);
	 * 1 validation message is expected.
	 * It is expected to contain "not reference compatible".
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not reference compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not reference compatible\" was issued." );
	}

	/*
	 * fsNull(s);
	 * 1 validation message is expected.
	 * It is expected to contain "not reference compatible".
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not reference compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not reference compatible\" was issued." );
	}

	/*
	 * fa(aNull);
	 * 1 validation message is expected.
	 * It is expected to contain "not reference compatible".
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not reference compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not reference compatible\" was issued." );
	}

	/*
	 * faNull(a);
	 * 1 validation message is expected.
	 * It is expected to contain "not reference compatible".
	 */
	public void testLine35() {
		List messages = getMessagesAtLine( 35 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not reference compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not reference compatible\" was issued." );
	}
}
