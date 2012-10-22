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
package org.eclipse.edt.tests.validation.junit.ibmi;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/callStatement/LocalFunctionCallTests.egl
 */
public class IBMiCallTests2 extends ValidationTestCase {

	public IBMiCallTests2() {
		super( "EGLSource/ibmi/IBMiCallTests2.egl", false );
	}

	/*
	 * call srvIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call this.srvIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call srvIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call this.srvIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call srvIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call this.srvIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call srvIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call this.srvIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call srvIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function srvIBMiReturn is not compatible with the type date".
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function srvIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function srvIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * call this.srvIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function srvIBMiReturn is not compatible with the type date".
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function srvIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function srvIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred")as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine51() {
		List messages = getMessagesAtLine( 51 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred") as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call srvFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "must be defined with the IBMiProgram annotation".
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be defined with the IBMiProgram annotation" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be defined with the IBMiProgram annotation\" was issued." );
	}

	/*
	 * call this.srvFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "must be defined with the IBMiProgram annotation".
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be defined with the IBMiProgram annotation" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be defined with the IBMiProgram annotation\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * call lib1.libIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine98() {
		List messages = getMessagesAtLine( 98 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call lib1.libIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine100() {
		List messages = getMessagesAtLine( 100 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call lib1.libIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call lib1.libIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine104() {
		List messages = getMessagesAtLine( 104 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call lib1.libIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function libIBMiReturn is not compatible with the type date".
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function libIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function libIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine109() {
		List messages = getMessagesAtLine( 109 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine110() {
		List messages = getMessagesAtLine( 110 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine113() {
		List messages = getMessagesAtLine( 113 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine114() {
		List messages = getMessagesAtLine( 114 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred") as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine117() {
		List messages = getMessagesAtLine( 117 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine118() {
		List messages = getMessagesAtLine( 118 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call lib1.libFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "must be defined with the IBMiProgram annotation".
	 */
	public void testLine121() {
		List messages = getMessagesAtLine( 121 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be defined with the IBMiProgram annotation" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be defined with the IBMiProgram annotation\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine124() {
		List messages = getMessagesAtLine( 124 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine125() {
		List messages = getMessagesAtLine( 125 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * call handIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine151() {
		List messages = getMessagesAtLine( 151 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call this.handIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine152() {
		List messages = getMessagesAtLine( 152 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call handIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine154() {
		List messages = getMessagesAtLine( 154 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call this.handIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine155() {
		List messages = getMessagesAtLine( 155 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call handIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine157() {
		List messages = getMessagesAtLine( 157 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call this.handIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine158() {
		List messages = getMessagesAtLine( 158 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call handIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine160() {
		List messages = getMessagesAtLine( 160 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call this.handIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine161() {
		List messages = getMessagesAtLine( 161 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call handIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function handIBMiReturn is not compatible with the type date".
	 */
	public void testLine163() {
		List messages = getMessagesAtLine( 163 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function handIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function handIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * call this.handIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function handIBMiReturn is not compatible with the type date".
	 */
	public void testLine164() {
		List messages = getMessagesAtLine( 164 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function handIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function handIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine167() {
		List messages = getMessagesAtLine( 167 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine168() {
		List messages = getMessagesAtLine( 168 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine170() {
		List messages = getMessagesAtLine( 170 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine171() {
		List messages = getMessagesAtLine( 171 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine175() {
		List messages = getMessagesAtLine( 175 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine176() {
		List messages = getMessagesAtLine( 176 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine178() {
		List messages = getMessagesAtLine( 178 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine179() {
		List messages = getMessagesAtLine( 179 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred") as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine182() {
		List messages = getMessagesAtLine( 182 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine183() {
		List messages = getMessagesAtLine( 183 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred") as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine185() {
		List messages = getMessagesAtLine( 185 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine186() {
		List messages = getMessagesAtLine( 186 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call handFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "must be defined with the IBMiProgram annotation".
	 */
	public void testLine189() {
		List messages = getMessagesAtLine( 189 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be defined with the IBMiProgram annotation" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be defined with the IBMiProgram annotation\" was issued." );
	}

	/*
	 * call this.handFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "must be defined with the IBMiProgram annotation".
	 */
	public void testLine190() {
		List messages = getMessagesAtLine( 190 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be defined with the IBMiProgram annotation" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be defined with the IBMiProgram annotation\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine193() {
		List messages = getMessagesAtLine( 193 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine194() {
		List messages = getMessagesAtLine( 194 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine196() {
		List messages = getMessagesAtLine( 196 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine197() {
		List messages = getMessagesAtLine( 197 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * call pgmIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine226() {
		List messages = getMessagesAtLine( 226 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call this.pgmIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine227() {
		List messages = getMessagesAtLine( 227 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call pgmIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine229() {
		List messages = getMessagesAtLine( 229 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call this.pgmIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine230() {
		List messages = getMessagesAtLine( 230 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call pgmIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine232() {
		List messages = getMessagesAtLine( 232 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call this.pgmIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine233() {
		List messages = getMessagesAtLine( 233 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call pgmIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine235() {
		List messages = getMessagesAtLine( 235 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call this.pgmIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine236() {
		List messages = getMessagesAtLine( 236 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call pgmIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function pgmIBMiReturn is not compatible with the type date".
	 */
	public void testLine238() {
		List messages = getMessagesAtLine( 238 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function pgmIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function pgmIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * call this.pgmIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function pgmIBMiReturn is not compatible with the type date".
	 */
	public void testLine239() {
		List messages = getMessagesAtLine( 239 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function pgmIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function pgmIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine242() {
		List messages = getMessagesAtLine( 242 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine243() {
		List messages = getMessagesAtLine( 243 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine245() {
		List messages = getMessagesAtLine( 245 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine246() {
		List messages = getMessagesAtLine( 246 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine250() {
		List messages = getMessagesAtLine( 250 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine251() {
		List messages = getMessagesAtLine( 251 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine253() {
		List messages = getMessagesAtLine( 253 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine254() {
		List messages = getMessagesAtLine( 254 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred") as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine257() {
		List messages = getMessagesAtLine( 257 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine258() {
		List messages = getMessagesAtLine( 258 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred") as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine260() {
		List messages = getMessagesAtLine( 260 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine261() {
		List messages = getMessagesAtLine( 261 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call pgmFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "must be defined with the IBMiProgram annotation".
	 */
	public void testLine264() {
		List messages = getMessagesAtLine( 264 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be defined with the IBMiProgram annotation" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be defined with the IBMiProgram annotation\" was issued." );
	}

	/*
	 * call this.pgmFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "must be defined with the IBMiProgram annotation".
	 */
	public void testLine265() {
		List messages = getMessagesAtLine( 265 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be defined with the IBMiProgram annotation" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be defined with the IBMiProgram annotation\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine268() {
		List messages = getMessagesAtLine( 268 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine269() {
		List messages = getMessagesAtLine( 269 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine271() {
		List messages = getMessagesAtLine( 271 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine272() {
		List messages = getMessagesAtLine( 272 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * call lib1.libIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine279() {
		List messages = getMessagesAtLine( 279 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call lib1.libIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine281() {
		List messages = getMessagesAtLine( 281 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call lib1.libIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine283() {
		List messages = getMessagesAtLine( 283 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call lib1.libIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine285() {
		List messages = getMessagesAtLine( 285 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call lib1.libIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function libIBMiReturn is not compatible with the type date".
	 */
	public void testLine287() {
		List messages = getMessagesAtLine( 287 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function libIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function libIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine290() {
		List messages = getMessagesAtLine( 290 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine291() {
		List messages = getMessagesAtLine( 291 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine294() {
		List messages = getMessagesAtLine( 294 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine295() {
		List messages = getMessagesAtLine( 295 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred") as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine298() {
		List messages = getMessagesAtLine( 298 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine299() {
		List messages = getMessagesAtLine( 299 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call lib1.libFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "must be defined with the IBMiProgram annotation".
	 */
	public void testLine302() {
		List messages = getMessagesAtLine( 302 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be defined with the IBMiProgram annotation" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be defined with the IBMiProgram annotation\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine305() {
		List messages = getMessagesAtLine( 305 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine306() {
		List messages = getMessagesAtLine( 306 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * call hand1.handIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine314() {
		List messages = getMessagesAtLine( 314 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call hand1.handIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine316() {
		List messages = getMessagesAtLine( 316 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call hand1.handIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine318() {
		List messages = getMessagesAtLine( 318 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call hand1.handIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine320() {
		List messages = getMessagesAtLine( 320 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call hand1.handIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function handIBMiReturn is not compatible with the type date".
	 */
	public void testLine322() {
		List messages = getMessagesAtLine( 322 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function handIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function handIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine325() {
		List messages = getMessagesAtLine( 325 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine326() {
		List messages = getMessagesAtLine( 326 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine329() {
		List messages = getMessagesAtLine( 329 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine330() {
		List messages = getMessagesAtLine( 330 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using Resources.getResource("binding:fred") as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine333() {
		List messages = getMessagesAtLine( 333 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine334() {
		List messages = getMessagesAtLine( 334 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call hand1.handFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "must be defined with the IBMiProgram annotation".
	 */
	public void testLine337() {
		List messages = getMessagesAtLine( 337 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be defined with the IBMiProgram annotation" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be defined with the IBMiProgram annotation\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine340() {
		List messages = getMessagesAtLine( 340 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine341() {
		List messages = getMessagesAtLine( 341 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}
}
