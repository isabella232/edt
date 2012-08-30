package org.eclipse.edt.tests.validation.junit.expressions;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/expressions/dynamicAccess1.egl
 */
public class DynamicAccess1Test extends ValidationTestCase {

	public DynamicAccess1Test() {
		super( "EGLSource/expressions/dynamicAccess1.egl", false );
	}

	/*
	 * key1 = null
	 * 0 validation messages are expected.
	 */
	public void testLine7() {
		List messages = getMessagesAtLine( 7 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a.x = null;
	 * 0 validation messages are expected.
	 */
	public void testLine11() {
		List messages = getMessagesAtLine( 11 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * this.a.x = null;
	 * 0 validation messages are expected.
	 */
	public void testLine12() {
		List messages = getMessagesAtLine( 12 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * this.a.x.y = null;
	 * 0 validation messages are expected.
	 */
	public void testLine13() {
		List messages = getMessagesAtLine( 13 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a["x"] = null;
	 * 0 validation messages are expected.
	 */
	public void testLine14() {
		List messages = getMessagesAtLine( 14 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a["x"]["y"] = null;
	 * 0 validation messages are expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d.x = null;
	 * 0 validation messages are expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * this.d.x = null;
	 * 0 validation messages are expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * this.d.x.y = null;
	 * 0 validation messages are expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d["x"] = null;
	 * 0 validation messages are expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d["x"]["y"] = null;
	 * 0 validation messages are expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 0, messages.size() );
	}
}
