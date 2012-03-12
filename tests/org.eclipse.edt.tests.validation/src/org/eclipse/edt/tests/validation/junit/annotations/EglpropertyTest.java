package org.eclipse.edt.tests.validation.junit.annotations;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/annotations/eglproperty.egl
 */
public class EglpropertyTest extends ValidationTestCase {

	public EglpropertyTest() {
		super( "EGLSource/annotations/eglproperty.egl", false );
	}

	/*
	 * field1 int {@eglproperty{}};
	 * 2 validation messages are expected.
	 */
	public void testLine2() {
		List messages = getMessagesAtLine( 2 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * field2 int {@eglproperty{getmethod = getit}};
	 * 0 validation messages are expected.
	 */
	public void testLine3() {
		List messages = getMessagesAtLine( 3 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * field3 int {@eglproperty{setmethod = setit}};
	 * 0 validation messages are expected.
	 */
	public void testLine4() {
		List messages = getMessagesAtLine( 4 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * field4 int {@eglproperty{getmethod = getit, setmethod = setit}};
	 * 0 validation messages are expected.
	 */
	public void testLine5() {
		List messages = getMessagesAtLine( 5 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * field5 int {@eglproperty{getmethod = setit, setmethod = getit}};
	 * 4 validation messages are expected.
	 */
	public void testLine6() {
		List messages = getMessagesAtLine( 6 );
		assertEquals( 4, messages.size() );
	}

	/*
	 * field2 = 3;
	 * 1 validation message is expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp = field3;
	 * 1 validation message is expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * field4 = 3;
	 * 0 validation messages are expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = field4;
	 * 0 validation messages are expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = field2 + 1;
	 * 0 validation messages are expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = arr[field2];
	 * 0 validation messages are expected.
	 */
	public void testLine22() {
		List messages = getMessagesAtLine( 22 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = -field2;
	 * 0 validation messages are expected.
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = field3 + 1;
	 * 1 validation message is expected.
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp = arr[field3];
	 * 1 validation message is expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp = -field3;
	 * 1 validation message is expected.
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * recField1 = hand1.field2,
	 * 0 validation messages are expected.
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * recField2 = hand1.field3,
	 * 1 validation message is expected.
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * recfield3 = arr[hand1.field2],
	 * 0 validation messages are expected.
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * recfield4 = arr[hand1.field3],
	 * 1 validation message is expected.
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * recfield4 = -hand1.field2,
	 * 0 validation messages are expected.
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * recfield5 = -hand1.field3
	 * 1 validation message is expected.
	 */
	public void testLine51() {
		List messages = getMessagesAtLine( 51 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * hand1.field2 = 3;
	 * 1 validation message is expected.
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp = hand1.field3;
	 * 1 validation message is expected.
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * hand1.field4 = 3;
	 * 0 validation messages are expected.
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = hand1.field4;
	 * 0 validation messages are expected.
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = hand1.field2 + 1;
	 * 0 validation messages are expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = arr[hand1.field2];
	 * 0 validation messages are expected.
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = -hand1.field2;
	 * 0 validation messages are expected.
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp = hand1.field3 + 1;
	 * 1 validation message is expected.
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp = arr[hand1.field3];
	 * 1 validation message is expected.
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp = -hand1.field3;
	 * 1 validation message is expected.
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * intFieldSet = 3;
	 * 0 validation messages are expected.
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * local = intFieldSet;
	 * 1 validation message is expected.
	 * It is expected to contain "not a get method".
	 */
	public void testLine88() {
		List messages = getMessagesAtLine( 88 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not a get method" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not a get method\" was issued." );
	}

	/*
	 * intFieldGet = 3;
	 * 1 validation message is expected.
	 * It is expected to contain "not a set method".
	 */
	public void testLine90() {
		List messages = getMessagesAtLine( 90 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not a set method" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not a set method\" was issued." );
	}

	/*
	 * local = intFieldGet;
	 * 0 validation messages are expected.
	 */
	public void testLine91() {
		List messages = getMessagesAtLine( 91 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * intFieldSet += 3;
	 * 1 validation message is expected.
	 * It is expected to contain "not a get method".
	 */
	public void testLine93() {
		List messages = getMessagesAtLine( 93 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not a get method" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not a get method\" was issued." );
	}

	/*
	 * intFieldGet += 3;
	 * 1 validation message is expected.
	 * It is expected to contain "not a set method".
	 */
	public void testLine94() {
		List messages = getMessagesAtLine( 94 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not a set method" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not a set method\" was issued." );
	}

	/*
	 * arrFieldSet = [3];
	 * 0 validation messages are expected.
	 */
	public void testLine96() {
		List messages = getMessagesAtLine( 96 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localarr = arrFieldSet;
	 * 1 validation message is expected.
	 * It is expected to contain "not a get method".
	 */
	public void testLine97() {
		List messages = getMessagesAtLine( 97 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not a get method" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not a get method\" was issued." );
	}

	/*
	 * arrFieldGet = [3];
	 * 1 validation message is expected.
	 * It is expected to contain "not a set method".
	 */
	public void testLine99() {
		List messages = getMessagesAtLine( 99 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not a set method" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not a set method\" was issued." );
	}

	/*
	 * localarr = arrFieldGet;
	 * 0 validation messages are expected.
	 */
	public void testLine100() {
		List messages = getMessagesAtLine( 100 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * arrFieldSet ::= 3;
	 * 1 validation message is expected.
	 * It is expected to contain "not a get method".
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not a get method" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not a get method\" was issued." );
	}

	/*
	 * arrFieldGet ::= 3;
	 * 0 validation messages are expected.
	 */
	public void testLine103() {
		List messages = getMessagesAtLine( 103 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * arrFieldSet ::= 3,
	 * 1 validation message is expected.
	 * It is expected to contain "not a get method".
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not a get method" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not a get method\" was issued." );
	}

	/*
	 * arrFieldGet ::= 3
	 * 0 validation messages are expected.
	 */
	public void testLine107() {
		List messages = getMessagesAtLine( 107 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * arrFieldSet ::= 3,
	 * 1 validation message is expected.
	 * It is expected to contain "not a get method".
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not a get method" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not a get method\" was issued." );
	}

	/*
	 * arrFieldGet ::= 3
	 * 0 validation messages are expected.
	 */
	public void testLine113() {
		List messages = getMessagesAtLine( 113 );
		assertEquals( 0, messages.size() );
	}
}
