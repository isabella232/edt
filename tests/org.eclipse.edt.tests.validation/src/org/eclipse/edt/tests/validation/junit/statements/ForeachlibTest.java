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
	public void testLine14() {
		List messages = getMessagesAtLine( 14 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * exists int;
	 * 1 validation message is expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * forEach(newitem int from array1)
	 * 0 validation messages are expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * newitem int;
	 * 1 validation message is expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * forEach(newitem int from array3)
	 * 1 validation message is expected.
	 */
	public void testLine22() {
		List messages = getMessagesAtLine( 22 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * forEach(newitem int from array2)
	 * 0 validation messages are expected.
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * forEach(newitem myrec from array3)
	 * 0 validation messages are expected.
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * forEach(newitem myrec from array2)
	 * 1 validation message is expected.
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * forEach(newitem int[] from array4)
	 * 0 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * forEach(newitem smallInt[] from array4)
	 * 1 validation message is expected.
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * forEach(newitem int from array4[1])
	 * 0 validation messages are expected.
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * forEach(newitem any[] from array4 as any[][])
	 * 1 validation message is expected.
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * forEach(newitem any[] from array5)
	 * 0 validation messages are expected.
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * forEach(newitem string from getArray())
	 * 0 validation messages are expected.
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * forEach(newitem int from exists)
	 * 1 validation message is expected.
	 */
	public void testLine52() {
		List messages = getMessagesAtLine( 52 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * forEach(exists from array1)
	 * 1 validation message is expected.
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 1, messages.size() );
	}
}
