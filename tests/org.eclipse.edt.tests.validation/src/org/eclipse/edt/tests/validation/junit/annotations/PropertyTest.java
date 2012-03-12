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
	 * field1 int {@property{}};
	 * 0 validation messages are expected.
	 */
	public void testLine2() {
		List messages = getMessagesAtLine( 2 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * field2 int {@property{getmethod = "getit"}};
	 * 0 validation messages are expected.
	 */
	public void testLine3() {
		List messages = getMessagesAtLine( 3 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * field3 int {@property{setmethod = "setit"}};
	 * 0 validation messages are expected.
	 */
	public void testLine4() {
		List messages = getMessagesAtLine( 4 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * field4 int {@property{getmethod = "getit", setmethod = "setit"}};
	 * 0 validation messages are expected.
	 */
	public void testLine5() {
		List messages = getMessagesAtLine( 5 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * field5 int {@property{getmethod = "setit", setmethod = "getit"}};
	 * 0 validation messages are expected.
	 */
	public void testLine6() {
		List messages = getMessagesAtLine( 6 );
		assertEquals( 0, messages.size() );
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
}
