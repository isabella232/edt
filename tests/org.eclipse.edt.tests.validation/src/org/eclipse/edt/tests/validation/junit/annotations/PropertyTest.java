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
package org.eclipse.edt.tests.validation.junit.annotations;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/annotations/property.egl
 */
public class PropertyTest extends ValidationTestCase {

	public PropertyTest() {
		super( "EGLSource/annotations/property.egl", false );
	}

	/*
	 * recField1 = et.field2,
	 * 0 validation messages are expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * recField2 = et.field3,
	 * 1 validation message is expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * recfield3 = arr[et.field2],
	 * 0 validation messages are expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * recfield4 = arr[et.field3],
	 * 1 validation message is expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * recfield4 = et.field2,
	 * 0 validation messages are expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * recfield5 = et.field3
	 * 1 validation message is expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * et.field2 = 3;
	 * 1 validation message is expected.
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp = et.field3;
	 * 1 validation message is expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * et.field4 = 3;
	 * 0 validation messages are expected.
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = et.field4;
	 * 0 validation messages are expected.
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = et.field2 + 1;
	 * 0 validation messages are expected.
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = arr[et.field2];
	 * 0 validation messages are expected.
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = -et.field2;
	 * 0 validation messages are expected.
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = et.field3 + 1;
	 * 1 validation message is expected.
	 */
	public void testLine35() {
		List messages = getMessagesAtLine( 35 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp = arr[et.field3];
	 * 1 validation message is expected.
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp = -et.field3;
	 * 1 validation message is expected.
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * field1 int {@property{}};
	 * 0 validation messages are expected.
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * field2 int {@property{getmethod = "getit"}};
	 * 0 validation messages are expected.
	 */
	public void testLine45() {
		List messages = getMessagesAtLine( 45 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * field3 int {@property{setmethod = "setit"}};
	 * 0 validation messages are expected.
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * field4 int {@property{getmethod = "getit", setmethod = "setit"}};
	 * 0 validation messages are expected.
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * field5 int {@property{getmethod = "setit", setmethod = "getit"}};
	 * 0 validation messages are expected.
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * field1 int {@property{}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field2 int {@property{getmethod = "getit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field3 int {@property{setmethod = "setit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field4 int {@property{getmethod = "getit", setmethod = "setit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field5 int {@property{getmethod = "setit", setmethod = "getit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field1 int {@property{}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field2 int {@property{getmethod = "getit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field3 int {@property{setmethod = "setit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field4 int {@property{getmethod = "getit", setmethod = "setit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field5 int {@property{getmethod = "setit", setmethod = "getit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field1 int {@property{}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine74() {
		List messages = getMessagesAtLine( 74 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field2 int {@property{getmethod = "getit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine75() {
		List messages = getMessagesAtLine( 75 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field3 int {@property{setmethod = "setit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine76() {
		List messages = getMessagesAtLine( 76 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field4 int {@property{getmethod = "getit", setmethod = "setit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine77() {
		List messages = getMessagesAtLine( 77 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field5 int {@property{getmethod = "setit", setmethod = "getit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field1 int {@property{}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field2 int {@property{getmethod = "getit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine85() {
		List messages = getMessagesAtLine( 85 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field3 int {@property{setmethod = "setit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine86() {
		List messages = getMessagesAtLine( 86 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field4 int {@property{getmethod = "getit", setmethod = "setit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field5 int {@property{getmethod = "setit", setmethod = "getit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine88() {
		List messages = getMessagesAtLine( 88 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field1 int {@property{}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine94() {
		List messages = getMessagesAtLine( 94 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field2 int {@property{getmethod = "getit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine95() {
		List messages = getMessagesAtLine( 95 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field3 int {@property{setmethod = "setit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine96() {
		List messages = getMessagesAtLine( 96 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field4 int {@property{getmethod = "getit", setmethod = "setit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine97() {
		List messages = getMessagesAtLine( 97 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}

	/*
	 * field5 int {@property{getmethod = "setit", setmethod = "getit"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation @Property can only be specified on fields inside ExternalTypes.".
	 */
	public void testLine98() {
		List messages = getMessagesAtLine( 98 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation @Property can only be specified on fields inside ExternalTypes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation @Property can only be specified on fields inside ExternalTypes.\" was issued." );
	}
}
