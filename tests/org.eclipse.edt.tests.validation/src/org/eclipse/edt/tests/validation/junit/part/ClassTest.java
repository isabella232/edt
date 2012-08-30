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
}
