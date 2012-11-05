package org.eclipse.edt.tests.validation.junit.statements;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/statements/foreachlib.egl
 */
public class ForeachlibTest extends ValidationTestCase {

	public ForeachlibTest() {
		super( "EGLSource/statements/foreachlib.egl", false );
	}

	/*
	 * forEach(exists int from array1)
	 * 1 validation message is expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * exists int;
	 * 1 validation message is expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * forEach(newitem int from array1)
	 * 0 validation messages are expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * newitem int;
	 * 1 validation message is expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * forEach(newitem int from array3)
	 * 1 validation message is expected.
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * forEach(newitem int from array2)
	 * 0 validation messages are expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * forEach(newitem myrec from array3)
	 * 0 validation messages are expected.
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * forEach(newitem myrec from array2)
	 * 1 validation message is expected.
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * forEach(newitem int[] from array4)
	 * 0 validation messages are expected.
	 */
	public void testLine35() {
		List messages = getMessagesAtLine( 35 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * forEach(newitem smallInt[] from array4)
	 * 0 validation messages are expected.
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * forEach(newitem smallInt[] from array6)
	 * 1 validation message is expected.
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * forEach(newitem int from array4[1])
	 * 0 validation messages are expected.
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * forEach(newitem any[] from array4 as any[][])
	 * 0 validation messages are expected.
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * forEach(newitem any[] from array1)
	 * 1 validation message is expected.
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * forEach(newitem any[] from array5)
	 * 0 validation messages are expected.
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * forEach(newitem string from getArray())
	 * 0 validation messages are expected.
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * forEach(newitem int from exists)
	 * 1 validation message is expected.
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * forEach(exists from array1)
	 * 1 validation message is expected.
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 1, messages.size() );
	}
}
