package org.eclipse.edt.tests.validation.junit.statements;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/statements/returnStatement.egl
 */
public class ReturnStatementTest extends ValidationTestCase {

	public ReturnStatementTest() {
		super( "EGLSource/statements/returnStatement.egl", false );
	}

	/*
	 * return s;
	 * 0 validation messages are expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return s;
	 * 0 validation messages are expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return b;
	 * 0 validation messages are expected.
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return s;
	 * 1 validation message is expected.
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return getFloat() * 10 + 76.2;
	 * 0 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return 10;
	 * 0 validation messages are expected.
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return t;
	 * 0 validation messages are expected.
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return r;
	 * 0 validation messages are expected.
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return r;
	 * 1 validation message is expected.
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return r.i;
	 * 0 validation messages are expected.
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return r;
	 * 0 validation messages are expected.
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return a;
	 * 0 validation messages are expected.
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return a;
	 * 0 validation messages are expected.
	 */
	public void testLine73() {
		List messages = getMessagesAtLine( 73 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return a;
	 * 0 validation messages are expected.
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return a;
	 * 0 validation messages are expected.
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return 100;
	 * 0 validation messages are expected.
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return e;
	 * 0 validation messages are expected.
	 */
	public void testLine92() {
		List messages = getMessagesAtLine( 92 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return a;
	 * 0 validation messages are expected.
	 */
	public void testLine97() {
		List messages = getMessagesAtLine( 97 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return e;
	 * 0 validation messages are expected.
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return r;
	 * 1 validation message is expected.
	 */
	public void testLine107() {
		List messages = getMessagesAtLine( 107 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return e;
	 * 0 validation messages are expected.
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return a;
	 * 0 validation messages are expected.
	 */
	public void testLine117() {
		List messages = getMessagesAtLine( 117 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return true;
	 * 1 validation message is expected.
	 */
	public void testLine121() {
		List messages = getMessagesAtLine( 121 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return r{i = 2};
	 * 1 validation message is expected.
	 */
	public void testLine126() {
		List messages = getMessagesAtLine( 126 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return null;
	 * 1 validation message is expected.
	 */
	public void testLine130() {
		List messages = getMessagesAtLine( 130 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return null;
	 * 0 validation messages are expected.
	 */
	public void testLine134() {
		List messages = getMessagesAtLine( 134 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return;
	 * 0 validation messages are expected.
	 */
	public void testLine138() {
		List messages = getMessagesAtLine( 138 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return;
	 * 0 validation messages are expected.
	 */
	public void testLine142() {
		List messages = getMessagesAtLine( 142 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return funcForDel1;
	 * 0 validation messages are expected.
	 */
	public void testLine146() {
		List messages = getMessagesAtLine( 146 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return d;
	 * 0 validation messages are expected.
	 */
	public void testLine151() {
		List messages = getMessagesAtLine( 151 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return badFuncForDel1;
	 * 1 validation message is expected.
	 */
	public void testLine155() {
		List messages = getMessagesAtLine( 155 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return funcForDel1;
	 * 0 validation messages are expected.
	 */
	public void testLine159() {
		List messages = getMessagesAtLine( 159 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return s;
	 * 0 validation messages are expected.
	 */
	public void testLine169() {
		List messages = getMessagesAtLine( 169 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return i;
	 * 1 validation message is expected.
	 */
	public void testLine174() {
		List messages = getMessagesAtLine( 174 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return ["a", "b"];
	 * 0 validation messages are expected.
	 */
	public void testLine178() {
		List messages = getMessagesAtLine( 178 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return ["a", "b"];
	 * 0 validation messages are expected.
	 */
	public void testLine182() {
		List messages = getMessagesAtLine( 182 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return ["a", "b"];
	 * 1 validation message is expected.
	 */
	public void testLine186() {
		List messages = getMessagesAtLine( 186 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return a;
	 * 1 validation message is expected.
	 */
	public void testLine191() {
		List messages = getMessagesAtLine( 191 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return a;
	 * 0 validation messages are expected.
	 */
	public void testLine196() {
		List messages = getMessagesAtLine( 196 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return [1,2];
	 * 1 validation message is expected.
	 */
	public void testLine200() {
		List messages = getMessagesAtLine( 200 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return a;
	 * 1 validation message is expected.
	 */
	public void testLine205() {
		List messages = getMessagesAtLine( 205 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return a;
	 * 1 validation message is expected.
	 */
	public void testLine210() {
		List messages = getMessagesAtLine( 210 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return s;
	 * 1 validation message is expected.
	 */
	public void testLine215() {
		List messages = getMessagesAtLine( 215 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return d;
	 * 0 validation messages are expected.
	 */
	public void testLine220() {
		List messages = getMessagesAtLine( 220 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return a;
	 * 0 validation messages are expected.
	 */
	public void testLine225() {
		List messages = getMessagesAtLine( 225 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return a;
	 * 1 validation message is expected.
	 */
	public void testLine230() {
		List messages = getMessagesAtLine( 230 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return d;
	 * 1 validation message is expected.
	 */
	public void testLine235() {
		List messages = getMessagesAtLine( 235 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * return [funcForDel1];
	 * 0 validation messages are expected.
	 */
	public void testLine239() {
		List messages = getMessagesAtLine( 239 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return [d];
	 * 0 validation messages are expected.
	 */
	public void testLine244() {
		List messages = getMessagesAtLine( 244 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return r.s1;
	 * 0 validation messages are expected.
	 */
	public void testLine249() {
		List messages = getMessagesAtLine( 249 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return r.s2;
	 * 1 validation message is expected.
	 */
	public void testLine254() {
		List messages = getMessagesAtLine( 254 );
		assertEquals( 1, messages.size() );
	}
}
