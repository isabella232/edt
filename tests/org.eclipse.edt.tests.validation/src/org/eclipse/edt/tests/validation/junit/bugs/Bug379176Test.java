/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
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
	 * x3 int = -(2147483648);
	 * 0 validation messages are expected.
	 */
	public void testLine6() {
		List messages = getMessagesAtLine( 6 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * x4 int = -(2147483649);
	 * 1 validation message is expected.
	 */
	public void testLine7() {
		List messages = getMessagesAtLine( 7 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * x5 int = (-2147483648);
	 * 0 validation messages are expected.
	 */
	public void testLine9() {
		List messages = getMessagesAtLine( 9 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * x6 int = (-2147483649);
	 * 1 validation message is expected.
	 */
	public void testLine10() {
		List messages = getMessagesAtLine( 10 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * x7 int = -(-2147483647);
	 * 0 validation messages are expected.
	 */
	public void testLine12() {
		List messages = getMessagesAtLine( 12 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * x8 int = -(-2147483648);
	 * 1 validation message is expected.
	 */
	public void testLine13() {
		List messages = getMessagesAtLine( 13 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * y1 int = 2147483648;
	 * 1 validation message is expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * y2 int = 2147483647;
	 * 0 validation messages are expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * y3 int = +2147483648;
	 * 1 validation message is expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * y4 int = +2147483647;
	 * 0 validation messages are expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * y5 int = +(2147483648);
	 * 1 validation message is expected.
	 */
	public void testLine22() {
		List messages = getMessagesAtLine( 22 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * y6 int = +(2147483647);
	 * 0 validation messages are expected.
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * y7 int = (+2147483648);
	 * 1 validation message is expected.
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * y8 int = (+2147483647);
	 * 0 validation messages are expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 0, messages.size() );
	}
}
