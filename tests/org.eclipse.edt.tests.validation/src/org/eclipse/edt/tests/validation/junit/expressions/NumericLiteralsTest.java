package org.eclipse.edt.tests.validation.junit.expressions;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/expressions/numericLiterals.egl
 */
public class NumericLiteralsTest extends ValidationTestCase {

	public NumericLiteralsTest() {
		super( "EGLSource/expressions/numericLiterals.egl", false );
	}

	/*
	 * val1 = 10I,
	 * 0 validation messages are expected.
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val1 = 10i,
	 * 0 validation messages are expected.
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val1 = 10,
	 * 0 validation messages are expected.
	 */
	public void testLine69() {
		List messages = getMessagesAtLine( 69 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val2 = 10f,
	 * 0 validation messages are expected.
	 */
	public void testLine70() {
		List messages = getMessagesAtLine( 70 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val2 = 10F,
	 * 0 validation messages are expected.
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val3 = 12345678901234567890123456789012.,
	 * 0 validation messages are expected.
	 */
	public void testLine72() {
		List messages = getMessagesAtLine( 72 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val3 = .12345678901234567890123456789012,
	 * 0 validation messages are expected.
	 */
	public void testLine73() {
		List messages = getMessagesAtLine( 73 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val3 = 123456789012.34567890123456789012,
	 * 0 validation messages are expected.
	 */
	public void testLine74() {
		List messages = getMessagesAtLine( 74 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val1 = 100000000000000000000000000000I,
	 * 2 validation messages are expected.
	 */
	public void testLine75() {
		List messages = getMessagesAtLine( 75 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * val1 = 1000000i,
	 * 2 validation messages are expected.
	 */
	public void testLine76() {
		List messages = getMessagesAtLine( 76 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * val1 = 1000000000000,
	 * 2 validation messages are expected.
	 */
	public void testLine77() {
		List messages = getMessagesAtLine( 77 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * val2 = 1000000000000000000000000000000000000000000000000000f,
	 * 1 validation message is expected.
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * val2 = 999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999F,
	 * 1 validation message is expected.
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * val3 = 12345678901234567890123456789012.3,
	 * 1 validation message is expected.
	 */
	public void testLine80() {
		List messages = getMessagesAtLine( 80 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * val3 = .123456789012345678901234567890123,
	 * 1 validation message is expected.
	 */
	public void testLine81() {
		List messages = getMessagesAtLine( 81 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * val3 = 123456789012345678901234567890123.
	 * 1 validation message is expected.
	 */
	public void testLine82() {
		List messages = getMessagesAtLine( 82 );
		assertEquals( 1, messages.size() );
	}
}
