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
 * A JUnit test case for the file EGLSource/part/abstract.egl
 */
public class AbstractTest extends ValidationTestCase {

	public AbstractTest() {
		super( "EGLSource/part/abstract.egl", false );
	}

	/*
	 * abstract class c1
	 * 0 validation messages are expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * abstract class c2 extends c1
	 * 0 validation messages are expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * class c3 extends c2
	 * 0 validation messages are expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * abstract externaltype et1 type javaobject
	 * 0 validation messages are expected.
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * abstract externaltype et2 extends et1 type javaobject
	 * 0 validation messages are expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * externaltype et1 extends et2 type javaobject
	 * 0 validation messages are expected.
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * abstract class c4
	 * 0 validation messages are expected.
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * abstract function mustImplement(i int);
	 * 0 validation messages are expected.
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function notAbstract();
	 * 0 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * class c5 extends c4
	 * 1 validation message is expected.
	 * It is expected to contain "The part c5 must implement the inherited abstract function mustImplement( int inOut ) defined in part c4.".
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The part c5 must implement the inherited abstract function mustImplement( int inOut ) defined in part c4." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The part c5 must implement the inherited abstract function mustImplement( int inOut ) defined in part c4.\" was issued." );
	}

	/*
	 * class c6 extends c4
	 * 0 validation messages are expected.
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function mustImplement(i int);
	 * 0 validation messages are expected.
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * abstract function notValidHere(i int);
	 * 1 validation message is expected.
	 * It is expected to contain "The abstract function notValidHere( int inOut ) can only be defined in an abstract part.".
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The abstract function notValidHere( int inOut ) can only be defined in an abstract part." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The abstract function notValidHere( int inOut ) can only be defined in an abstract part.\" was issued." );
	}

	/*
	 * abstract class c7 extends c4
	 * 0 validation messages are expected.
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * abstract class c8
	 * 0 validation messages are expected.
	 */
	public void testLine45() {
		List messages = getMessagesAtLine( 45 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * private abstract function func1(i int);
	 * 1 validation message is expected.
	 * It is expected to contain "The abstract function func1( int inOut ) cannot be private.".
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The abstract function func1( int inOut ) cannot be private." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The abstract function func1( int inOut ) cannot be private.\" was issued." );
	}

	/*
	 * static abstract function func2(i int);
	 * 1 validation message is expected.
	 * It is expected to contain "The abstract function func2( int inOut ) cannot be static.".
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The abstract function func2( int inOut ) cannot be static." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The abstract function func2( int inOut ) cannot be static.\" was issued." );
	}

	/*
	 * a c4;
	 * 1 validation message is expected.
	 * It is expected to contain "The type c4 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.".
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type c4 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type c4 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.\" was issued." );
	}

	/*
	 * b c4?;
	 * 0 validation messages are expected.
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * c c5;
	 * 0 validation messages are expected.
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d c4[];
	 * 0 validation messages are expected.
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * e c4[]?;
	 * 0 validation messages are expected.
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f et1;
	 * 1 validation message is expected.
	 * It is expected to contain "The type et1 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.".
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type et1 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type et1 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.\" was issued." );
	}

	/*
	 * g et1?;
	 * 0 validation messages are expected.
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * h et1[];
	 * 0 validation messages are expected.
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * x = new c4;
	 * 1 validation message is expected.
	 * It is expected to contain "The type c4 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.".
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type c4 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type c4 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.\" was issued." );
	}

	/*
	 * x = new c4[0];
	 * 0 validation messages are expected.
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * x = new c4[10];
	 * 1 validation message is expected.
	 * It is expected to contain "The type c4 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.".
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type c4 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type c4 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.\" was issued." );
	}

	/*
	 * x = new c4[i];
	 * 1 validation message is expected.
	 * It is expected to contain "The type c4 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.".
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type c4 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type c4 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.\" was issued." );
	}

	/*
	 * x = new et1;
	 * 1 validation message is expected.
	 * It is expected to contain "The type et1 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.".
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type et1 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type et1 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.\" was issued." );
	}

	/*
	 * x = new et1[0];
	 * 0 validation messages are expected.
	 */
	public void testLine69() {
		List messages = getMessagesAtLine( 69 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * x = new et1[10];
	 * 1 validation message is expected.
	 * It is expected to contain "The type et1 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.".
	 */
	public void testLine70() {
		List messages = getMessagesAtLine( 70 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type et1 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type et1 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.\" was issued." );
	}

	/*
	 * x = new et1[i];
	 * 1 validation message is expected.
	 * It is expected to contain "The type et1 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.".
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type et1 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type et1 is not instantiable, or it has a private default constructor. Define a nullable reference to the type or explicitly call a constructor.\" was issued." );
	}
}
