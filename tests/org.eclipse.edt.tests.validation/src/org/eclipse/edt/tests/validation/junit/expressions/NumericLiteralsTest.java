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
	 * a = 2147483647;
	 * 0 validation messages are expected.
	 */
	public void testLine10() {
		List messages = getMessagesAtLine( 10 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = 9223372036854775807I;
	 * 0 validation messages are expected.
	 */
	public void testLine11() {
		List messages = getMessagesAtLine( 11 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = 32767i;
	 * 0 validation messages are expected.
	 */
	public void testLine12() {
		List messages = getMessagesAtLine( 12 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = 3.4f;
	 * 0 validation messages are expected.
	 */
	public void testLine13() {
		List messages = getMessagesAtLine( 13 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = 934.234F;
	 * 0 validation messages are expected.
	 */
	public void testLine14() {
		List messages = getMessagesAtLine( 14 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = 934.234e34;
	 * 0 validation messages are expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = 12345678901234567890123456789012.;
	 * 0 validation messages are expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = .12345678901234567890123456789012;
	 * 0 validation messages are expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = 1234567890123456.7890123456789012;
	 * 0 validation messages are expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 2147483647};
	 * 0 validation messages are expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 9223372036854775807I};
	 * 0 validation messages are expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 32767i};
	 * 0 validation messages are expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 21474.234f};
	 * 0 validation messages are expected.
	 */
	public void testLine22() {
		List messages = getMessagesAtLine( 22 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 21474.234F};
	 * 0 validation messages are expected.
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 21474.234e22};
	 * 0 validation messages are expected.
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 12345678901234567890123456789012.};
	 * 0 validation messages are expected.
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = .12345678901234567890123456789012};
	 * 0 validation messages are expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 1234567890123456.7890123456789012};
	 * 0 validation messages are expected.
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = 2147483648;
	 * 1 validation message is expected.
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a = 32768i;
	 * 1 validation message is expected.
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a = 9223372036854775808I;
	 * 1 validation message is expected.
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a = 1000000000000000000000000000000000000000000000.022f;
	 * 1 validation message is expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a = 999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999F;
	 * 1 validation message is expected.
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a = 999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999e0;
	 * 1 validation message is expected.
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a = .999999999999999999999999999999999;
	 * 1 validation message is expected.
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a = 999999999999999999999999999999999.;
	 * 1 validation message is expected.
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a = 9999999999999999.99999999999999999;
	 * 1 validation message is expected.
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 2147483648};
	 * 1 validation message is expected.
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 32768i};
	 * 1 validation message is expected.
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 9223372036854775808I};
	 * 1 validation message is expected.
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 1000000000000000000000000000000000000000000000.0222f};
	 * 1 validation message is expected.
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999F};
	 * 1 validation message is expected.
	 */
	public void testLine45() {
		List messages = getMessagesAtLine( 45 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999e0};
	 * 1 validation message is expected.
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 12345678901234567890123456789012.3};
	 * 1 validation message is expected.
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = .123456789012345678901234567890123};
	 * 1 validation message is expected.
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * rec = new numlitrec{i = 1234567890123456.78901234567890123};
	 * 1 validation message is expected.
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * testin(1010I, 1010, 10i, 324.34f, 324.243F, 6.7);
	 * 0 validation messages are expected.
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * testinout(1010I, 1010, 10i, 324.34f, 324.243F, 1.1);
	 * 6 validation messages are expected.
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 6, messages.size() );
	}

	/*
	 * testout(1010I, 1010, 10i, 324.34f, 324.243F, 2.3);
	 * 6 validation messages are expected.
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 6, messages.size() );
	}

	/*
	 * for (i int from 10I to 20f by 10i)
	 * 0 validation messages are expected.
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val1 = 10I,
	 * 0 validation messages are expected.
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val1 = 10i,
	 * 0 validation messages are expected.
	 */
	public void testLine69() {
		List messages = getMessagesAtLine( 69 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val1 = 10,
	 * 0 validation messages are expected.
	 */
	public void testLine70() {
		List messages = getMessagesAtLine( 70 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val2 = 10f,
	 * 0 validation messages are expected.
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val2 = 10F,
	 * 0 validation messages are expected.
	 */
	public void testLine72() {
		List messages = getMessagesAtLine( 72 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val3 = 12345678901234567890123456789012.,
	 * 0 validation messages are expected.
	 */
	public void testLine73() {
		List messages = getMessagesAtLine( 73 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val3 = .12345678901234567890123456789012,
	 * 0 validation messages are expected.
	 */
	public void testLine74() {
		List messages = getMessagesAtLine( 74 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val3 = 123456789012.34567890123456789012,
	 * 0 validation messages are expected.
	 */
	public void testLine75() {
		List messages = getMessagesAtLine( 75 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val1 = 100000000000000000000000000000I,
	 * 2 validation messages are expected.
	 */
	public void testLine76() {
		List messages = getMessagesAtLine( 76 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * val1 = 1000000i,
	 * 0 validation messages are expected.
	 */
	public void testLine77() {
		List messages = getMessagesAtLine( 77 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val1 = 1000000000000,
	 * 2 validation messages are expected.
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * val2 = 1000000000000000000000000000000000000000000000000000f,
	 * 0 validation messages are expected.
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val2 = 999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999F,
	 * 0 validation messages are expected.
	 */
	public void testLine80() {
		List messages = getMessagesAtLine( 80 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val3 = 12345678901234567890123456789012.3,
	 * 0 validation messages are expected.
	 */
	public void testLine81() {
		List messages = getMessagesAtLine( 81 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val3 = .123456789012345678901234567890123,
	 * 0 validation messages are expected.
	 */
	public void testLine82() {
		List messages = getMessagesAtLine( 82 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * val3 = 123456789012345678901234567890123.
	 * 0 validation messages are expected.
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 0, messages.size() );
	}
}
