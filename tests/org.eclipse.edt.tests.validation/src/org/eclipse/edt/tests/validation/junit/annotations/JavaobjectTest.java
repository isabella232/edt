package org.eclipse.edt.tests.validation.junit.annotations;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/annotations/javaobject.egl
 */
public class JavaobjectTest extends ValidationTestCase {

	public JavaobjectTest() {
		super( "EGLSource/annotations/javaobject.egl", false );
	}

	/*
	 * p1 int,
	 * 1 validation message is expected.
	 */
	public void testLine3() {
		List messages = getMessagesAtLine( 3 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * p2 int in,
	 * 0 validation messages are expected.
	 */
	public void testLine4() {
		List messages = getMessagesAtLine( 4 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p3 dictionary,
	 * 0 validation messages are expected.
	 */
	public void testLine5() {
		List messages = getMessagesAtLine( 5 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p4 dictionary inout,
	 * 1 validation message is expected.
	 */
	public void testLine6() {
		List messages = getMessagesAtLine( 6 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * p5 int[],
	 * 0 validation messages are expected.
	 */
	public void testLine7() {
		List messages = getMessagesAtLine( 7 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p6 int[] out,
	 * 1 validation message is expected.
	 */
	public void testLine8() {
		List messages = getMessagesAtLine( 8 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * p7 int[] inout
	 * 1 validation message is expected.
	 */
	public void testLine9() {
		List messages = getMessagesAtLine( 9 );
		assertEquals( 1, messages.size() );
	}
}