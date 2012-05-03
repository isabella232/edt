package org.eclipse.edt.tests.validation.junit.annotations;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/annotations/enums.egl
 */
public class EnumsTest extends ValidationTestCase {

	public EnumsTest() {
		super( "EGLSource/annotations/enums.egl", false );
	}

	/*
	 * {targets = [ ElementKind.EnumerationPart]}
	 * 0 validation messages are expected.
	 */
	public void testLine4() {
		List messages = getMessagesAtLine( 4 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * {targets = [ ElementKind.EnumerationEntry]}
	 * 0 validation messages are expected.
	 */
	public void testLine10() {
		List messages = getMessagesAtLine( 10 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * enumeration enum1 {@annot1{value = "test1"}}
	 * 0 validation messages are expected.
	 */
	public void testLine14() {
		List messages = getMessagesAtLine( 14 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * A {@annot2{value = "test2"}},
	 * 0 validation messages are expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * B{@annot2{}} = 16,
	 * 0 validation messages are expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * C = ~7,
	 * 0 validation messages are expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * D = -6,
	 * 0 validation messages are expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * E = +9,
	 * 0 validation messages are expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * F = 10 + 2,
	 * 1 validation message is expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * G = "123"
	 * 1 validation message is expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 1, messages.size() );
	}
}
