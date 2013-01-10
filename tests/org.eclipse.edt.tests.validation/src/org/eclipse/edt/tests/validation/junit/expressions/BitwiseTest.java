/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.tests.validation.junit.expressions;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/expressions/bitwise.egl
 */
public class BitwiseTest extends ValidationTestCase {

	public BitwiseTest() {
		super( "EGLSource/expressions/bitwise.egl", false );
	}

	/*
	 * i = ~si;
	 * 0 validation messages are expected.
	 */
	public void testLine13() {
		List messages = getMessagesAtLine( 13 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si = ~i;
	 * 0 validation messages are expected.
	 */
	public void testLine14() {
		List messages = getMessagesAtLine( 14 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i = ~2343;
	 * 0 validation messages are expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si = ~2;
	 * 0 validation messages are expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i = ~funcIn(~i);
	 * 0 validation messages are expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i = ~funcInout(i);
	 * 0 validation messages are expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = ~"23";
	 * 1 validation message is expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a = ~a;
	 * 1 validation message is expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a = ~s;
	 * 1 validation message is expected.
	 */
	public void testLine22() {
		List messages = getMessagesAtLine( 22 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a = ~r;
	 * 1 validation message is expected.
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * funcIn(~i);
	 * 0 validation messages are expected.
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * funcIn(~si);
	 * 0 validation messages are expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * funcIn(~222);
	 * 0 validation messages are expected.
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * funcInout(~i);
	 * 1 validation message is expected.
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * funcInout(~si);
	 * 1 validation message is expected.
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * funcInout(~222);
	 * 1 validation message is expected.
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i = si << 2;
	 * 0 validation messages are expected.
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i = i << si;
	 * 0 validation messages are expected.
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si = i << 2;
	 * 0 validation messages are expected.
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si = si << i;
	 * 0 validation messages are expected.
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si = getInt() << getSmallint();
	 * 0 validation messages are expected.
	 */
	public void testLine45() {
		List messages = getMessagesAtLine( 45 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = s << 2;
	 * 0 validation messages are expected.
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = 2 << a;
	 * 1 validation message is expected.
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a = 2 << r;
	 * 1 validation message is expected.
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i <<= 2;
	 * 0 validation messages are expected.
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i <<= si;
	 * 0 validation messages are expected.
	 */
	public void testLine51() {
		List messages = getMessagesAtLine( 51 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i <<= i;
	 * 0 validation messages are expected.
	 */
	public void testLine52() {
		List messages = getMessagesAtLine( 52 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i <<= a;
	 * 1 validation message is expected.
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i <<= s;
	 * 0 validation messages are expected.
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i <<= "2";
	 * 0 validation messages are expected.
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i <<= getString();
	 * 0 validation messages are expected.
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i <<= r;
	 * 1 validation message is expected.
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i <<= getRec();
	 * 1 validation message is expected.
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * si <<= 2;
	 * 0 validation messages are expected.
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si <<= i;
	 * 0 validation messages are expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si <<= si;
	 * 0 validation messages are expected.
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si <<= getInt();
	 * 0 validation messages are expected.
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si <<= a;
	 * 1 validation message is expected.
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * si <<= s;
	 * 0 validation messages are expected.
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si <<= "2";
	 * 0 validation messages are expected.
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si <<= r;
	 * 1 validation message is expected.
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * si <<= getRec();
	 * 1 validation message is expected.
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s <<= 2;
	 * 0 validation messages are expected.
	 */
	public void testLine70() {
		List messages = getMessagesAtLine( 70 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a <<= 2;
	 * 1 validation message is expected.
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * r <<= 2;
	 * 1 validation message is expected.
	 */
	public void testLine72() {
		List messages = getMessagesAtLine( 72 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i = si >> 2;
	 * 0 validation messages are expected.
	 */
	public void testLine82() {
		List messages = getMessagesAtLine( 82 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i = i >> si;
	 * 0 validation messages are expected.
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si = i >> 2;
	 * 0 validation messages are expected.
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si = si >> i;
	 * 0 validation messages are expected.
	 */
	public void testLine85() {
		List messages = getMessagesAtLine( 85 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si = getInt() >> getSmallint();
	 * 0 validation messages are expected.
	 */
	public void testLine86() {
		List messages = getMessagesAtLine( 86 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = s >> 2;
	 * 0 validation messages are expected.
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = 2 >> a;
	 * 1 validation message is expected.
	 */
	public void testLine88() {
		List messages = getMessagesAtLine( 88 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a = 2 >> r;
	 * 1 validation message is expected.
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i >>= 2;
	 * 0 validation messages are expected.
	 */
	public void testLine91() {
		List messages = getMessagesAtLine( 91 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i >>= si;
	 * 0 validation messages are expected.
	 */
	public void testLine92() {
		List messages = getMessagesAtLine( 92 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i >>= i;
	 * 0 validation messages are expected.
	 */
	public void testLine93() {
		List messages = getMessagesAtLine( 93 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i >>= a;
	 * 1 validation message is expected.
	 */
	public void testLine94() {
		List messages = getMessagesAtLine( 94 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i >>= s;
	 * 0 validation messages are expected.
	 */
	public void testLine95() {
		List messages = getMessagesAtLine( 95 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i >>= "2";
	 * 0 validation messages are expected.
	 */
	public void testLine96() {
		List messages = getMessagesAtLine( 96 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i >>= getString();
	 * 0 validation messages are expected.
	 */
	public void testLine97() {
		List messages = getMessagesAtLine( 97 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i >>= r;
	 * 1 validation message is expected.
	 */
	public void testLine98() {
		List messages = getMessagesAtLine( 98 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i >>= getRec();
	 * 1 validation message is expected.
	 */
	public void testLine99() {
		List messages = getMessagesAtLine( 99 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * si >>= 2;
	 * 0 validation messages are expected.
	 */
	public void testLine101() {
		List messages = getMessagesAtLine( 101 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si >>= i;
	 * 0 validation messages are expected.
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si >>= si;
	 * 0 validation messages are expected.
	 */
	public void testLine103() {
		List messages = getMessagesAtLine( 103 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si >>= getInt();
	 * 0 validation messages are expected.
	 */
	public void testLine104() {
		List messages = getMessagesAtLine( 104 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si >>= a;
	 * 1 validation message is expected.
	 */
	public void testLine105() {
		List messages = getMessagesAtLine( 105 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * si >>= s;
	 * 0 validation messages are expected.
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si >>= "2";
	 * 0 validation messages are expected.
	 */
	public void testLine107() {
		List messages = getMessagesAtLine( 107 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si >>= r;
	 * 1 validation message is expected.
	 */
	public void testLine108() {
		List messages = getMessagesAtLine( 108 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * si >>= getRec();
	 * 1 validation message is expected.
	 */
	public void testLine109() {
		List messages = getMessagesAtLine( 109 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s >>= 2;
	 * 0 validation messages are expected.
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a >>= 2;
	 * 1 validation message is expected.
	 */
	public void testLine113() {
		List messages = getMessagesAtLine( 113 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * r >>= 2;
	 * 1 validation message is expected.
	 */
	public void testLine114() {
		List messages = getMessagesAtLine( 114 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i = si >>> 2;
	 * 0 validation messages are expected.
	 */
	public void testLine124() {
		List messages = getMessagesAtLine( 124 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i = i >>> si;
	 * 0 validation messages are expected.
	 */
	public void testLine125() {
		List messages = getMessagesAtLine( 125 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si = i >>> 2;
	 * 0 validation messages are expected.
	 */
	public void testLine126() {
		List messages = getMessagesAtLine( 126 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si = si >>> i;
	 * 0 validation messages are expected.
	 */
	public void testLine127() {
		List messages = getMessagesAtLine( 127 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si = getInt() >>> getSmallint();
	 * 0 validation messages are expected.
	 */
	public void testLine128() {
		List messages = getMessagesAtLine( 128 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = s >>> 2;
	 * 0 validation messages are expected.
	 */
	public void testLine129() {
		List messages = getMessagesAtLine( 129 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = 2 >>> a;
	 * 1 validation message is expected.
	 */
	public void testLine130() {
		List messages = getMessagesAtLine( 130 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a = 2 >>> r;
	 * 1 validation message is expected.
	 */
	public void testLine131() {
		List messages = getMessagesAtLine( 131 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i >>>= 2;
	 * 0 validation messages are expected.
	 */
	public void testLine133() {
		List messages = getMessagesAtLine( 133 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i >>>= si;
	 * 0 validation messages are expected.
	 */
	public void testLine134() {
		List messages = getMessagesAtLine( 134 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i >>>= i;
	 * 0 validation messages are expected.
	 */
	public void testLine135() {
		List messages = getMessagesAtLine( 135 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i >>>= a;
	 * 1 validation message is expected.
	 */
	public void testLine136() {
		List messages = getMessagesAtLine( 136 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i >>>= s;
	 * 0 validation messages are expected.
	 */
	public void testLine137() {
		List messages = getMessagesAtLine( 137 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i >>>= "2";
	 * 0 validation messages are expected.
	 */
	public void testLine138() {
		List messages = getMessagesAtLine( 138 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i >>>= r;
	 * 1 validation message is expected.
	 */
	public void testLine139() {
		List messages = getMessagesAtLine( 139 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i >>>= getRec();
	 * 1 validation message is expected.
	 */
	public void testLine140() {
		List messages = getMessagesAtLine( 140 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * si >>>= 2;
	 * 0 validation messages are expected.
	 */
	public void testLine142() {
		List messages = getMessagesAtLine( 142 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si >>>= i;
	 * 0 validation messages are expected.
	 */
	public void testLine143() {
		List messages = getMessagesAtLine( 143 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si >>>= si;
	 * 0 validation messages are expected.
	 */
	public void testLine144() {
		List messages = getMessagesAtLine( 144 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si >>>= getInt();
	 * 0 validation messages are expected.
	 */
	public void testLine145() {
		List messages = getMessagesAtLine( 145 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si >>>= a;
	 * 1 validation message is expected.
	 */
	public void testLine146() {
		List messages = getMessagesAtLine( 146 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * si >>>= s;
	 * 0 validation messages are expected.
	 */
	public void testLine147() {
		List messages = getMessagesAtLine( 147 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si >>>= "2";
	 * 0 validation messages are expected.
	 */
	public void testLine148() {
		List messages = getMessagesAtLine( 148 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si >>>= r;
	 * 1 validation message is expected.
	 */
	public void testLine149() {
		List messages = getMessagesAtLine( 149 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * si >>>= getRec();
	 * 1 validation message is expected.
	 */
	public void testLine150() {
		List messages = getMessagesAtLine( 150 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s >>>= 2;
	 * 0 validation messages are expected.
	 */
	public void testLine152() {
		List messages = getMessagesAtLine( 152 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a >>>= 2;
	 * 1 validation message is expected.
	 */
	public void testLine153() {
		List messages = getMessagesAtLine( 153 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * r >>>= 2;
	 * 1 validation message is expected.
	 */
	public void testLine154() {
		List messages = getMessagesAtLine( 154 );
		assertEquals( 1, messages.size() );
	}
}
