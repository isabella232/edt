package org.eclipse.edt.tests.validation.junit.bugs;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/bugs/Bug379176.egl
 */
public class Bug379176Test extends ValidationTestCase {

	public Bug379176Test() {
		super( "EGLSource/bugs/Bug379176.egl", false );
	}

	/*
	 * x1 int = -2147483648;
	 * 0 validation messages are expected.
	 */
	public void testLine3() {
		List messages = getMessagesAtLine( 3 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * x2 int = -2147483649;
	 * 1 validation message is expected.
	 */
	public void testLine4() {
		List messages = getMessagesAtLine( 4 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * y1 int = 2147483648;
	 * 1 validation message is expected.
	 */
	public void testLine5() {
		List messages = getMessagesAtLine( 5 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * y1 int = 2147483647;
	 * 0 validation messages are expected.
	 */
	public void testLine6() {
		List messages = getMessagesAtLine( 6 );
		assertEquals( 0, messages.size() );
	}
}
