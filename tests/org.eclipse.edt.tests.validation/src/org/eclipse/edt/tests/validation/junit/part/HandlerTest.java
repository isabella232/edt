/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.tests.validation.junit.part;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/part/handler.egl
 */
public class HandlerTest extends ValidationTestCase {

	public HandlerTest() {
		super( "EGLSource/part/handler.egl", false );
	}

	/*
	 * handler validateInterface implements myInterface
	 * 1 validation message is expected.
	 * It is expected to contain "The part validateInterface must implement the inherited function func2() defined in interface myInterface.".
	 */
	public void testLine12() {
		List messages = getMessagesAtLine( 12 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The part validateInterface must implement the inherited function func2() defined in interface myInterface." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The part validateInterface must implement the inherited function func2() defined in interface myInterface.\" was issued." );
	}
}
