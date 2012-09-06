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
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call this.srvIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call srvIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call this.srvIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call srvIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call this.srvIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call srvIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call this.srvIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call srvIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function srvIBMiReturn is not compatible with the type date".
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function srvIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function srvIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * call this.srvIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function srvIBMiReturn is not compatible with the type date".
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function srvIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function srvIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine35() {
		List messages = getMessagesAtLine( 35 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection\" was issued." );
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
	 * using syslib.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using syslib.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection\" was issued." );
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
	 * using syslib.getResource("binding:fred")as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using syslib.getResource("binding:fred") as IBMiConnection
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
	 * call srvFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "No compiler extensions were found for the call statement, which has no default behavior" );
		if( messageWithSubstring == null ) fail( "No message with substring \"No compiler extensions were found for the call statement, which has no default behavior\" was issued." );
	}

	/*
	 * call this.srvFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "No compiler extensions were found for the call statement, which has no default behavior" );
		if( messageWithSubstring == null ) fail( "No message with substring \"No compiler extensions were found for the call statement, which has no default behavior\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * returning to callback
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
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * call lib1.libIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine95() {
		List messages = getMessagesAtLine( 95 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call lib1.libIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine97() {
		List messages = getMessagesAtLine( 97 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call lib1.libIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine99() {
		List messages = getMessagesAtLine( 99 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call lib1.libIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine101() {
		List messages = getMessagesAtLine( 101 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call lib1.libIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function libIBMiReturn is not compatible with the type date".
	 */
	public void testLine103() {
		List messages = getMessagesAtLine( 103 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function libIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function libIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine105() {
		List messages = getMessagesAtLine( 105 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection.\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine107() {
		List messages = getMessagesAtLine( 107 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using syslib.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine109() {
		List messages = getMessagesAtLine( 109 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection.\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine111() {
		List messages = getMessagesAtLine( 111 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using syslib.getResource("binding:fred") as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine114() {
		List messages = getMessagesAtLine( 114 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine115() {
		List messages = getMessagesAtLine( 115 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call lib1.libFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine118() {
		List messages = getMessagesAtLine( 118 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "No compiler extensions were found for the call statement, which has no default behavior" );
		if( messageWithSubstring == null ) fail( "No message with substring \"No compiler extensions were found for the call statement, which has no default behavior\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine121() {
		List messages = getMessagesAtLine( 121 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine122() {
		List messages = getMessagesAtLine( 122 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * call handIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine148() {
		List messages = getMessagesAtLine( 148 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call this.handIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine149() {
		List messages = getMessagesAtLine( 149 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call handIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine151() {
		List messages = getMessagesAtLine( 151 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call this.handIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine152() {
		List messages = getMessagesAtLine( 152 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call handIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine154() {
		List messages = getMessagesAtLine( 154 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call this.handIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine155() {
		List messages = getMessagesAtLine( 155 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call handIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine157() {
		List messages = getMessagesAtLine( 157 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call this.handIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine158() {
		List messages = getMessagesAtLine( 158 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call handIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function handIBMiReturn is not compatible with the type date".
	 */
	public void testLine160() {
		List messages = getMessagesAtLine( 160 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function handIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function handIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * call this.handIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function handIBMiReturn is not compatible with the type date".
	 */
	public void testLine161() {
		List messages = getMessagesAtLine( 161 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function handIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function handIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection.".
	 */
	public void testLine163() {
		List messages = getMessagesAtLine( 163 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection.\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine165() {
		List messages = getMessagesAtLine( 165 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine166() {
		List messages = getMessagesAtLine( 166 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection.\" was issued." );
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
	 * using syslib.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine171() {
		List messages = getMessagesAtLine( 171 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection.\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine173() {
		List messages = getMessagesAtLine( 173 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using syslib.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine174() {
		List messages = getMessagesAtLine( 174 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection.\" was issued." );
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
	 * using syslib.getResource("binding:fred") as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine179() {
		List messages = getMessagesAtLine( 179 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine180() {
		List messages = getMessagesAtLine( 180 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using syslib.getResource("binding:fred") as IBMiConnection
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
	 * call handFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "No compiler extensions were found for the call statement, which has no default behavior.".
	 */
	public void testLine186() {
		List messages = getMessagesAtLine( 186 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "No compiler extensions were found for the call statement, which has no default behavior" );
		if( messageWithSubstring == null ) fail( "No message with substring \"No compiler extensions were found for the call statement, which has no default behavior\" was issued." );
	}

	/*
	 * call this.handFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine187() {
		List messages = getMessagesAtLine( 187 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "No compiler extensions were found for the call statement, which has no default behavior" );
		if( messageWithSubstring == null ) fail( "No message with substring \"No compiler extensions were found for the call statement, which has no default behavior\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine190() {
		List messages = getMessagesAtLine( 190 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine191() {
		List messages = getMessagesAtLine( 191 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
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
	 * call pgmIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine223() {
		List messages = getMessagesAtLine( 223 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call this.pgmIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine224() {
		List messages = getMessagesAtLine( 224 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call pgmIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine226() {
		List messages = getMessagesAtLine( 226 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call this.pgmIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine227() {
		List messages = getMessagesAtLine( 227 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call pgmIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine229() {
		List messages = getMessagesAtLine( 229 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call this.pgmIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine230() {
		List messages = getMessagesAtLine( 230 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call pgmIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine232() {
		List messages = getMessagesAtLine( 232 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call this.pgmIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine233() {
		List messages = getMessagesAtLine( 233 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call pgmIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function pgmIBMiReturn is not compatible with the type date".
	 */
	public void testLine235() {
		List messages = getMessagesAtLine( 235 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function pgmIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function pgmIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * call this.pgmIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function pgmIBMiReturn is not compatible with the type date".
	 */
	public void testLine236() {
		List messages = getMessagesAtLine( 236 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function pgmIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function pgmIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine238() {
		List messages = getMessagesAtLine( 238 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection.\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine240() {
		List messages = getMessagesAtLine( 240 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine241() {
		List messages = getMessagesAtLine( 241 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection.\" was issued." );
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
	 * using syslib.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine246() {
		List messages = getMessagesAtLine( 246 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection.\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine248() {
		List messages = getMessagesAtLine( 248 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using syslib.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine249() {
		List messages = getMessagesAtLine( 249 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection.\" was issued." );
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
	 * using syslib.getResource("binding:fred") as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine254() {
		List messages = getMessagesAtLine( 254 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine255() {
		List messages = getMessagesAtLine( 255 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using syslib.getResource("binding:fred") as IBMiConnection
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
	 * call pgmFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "".
	 */
	public void testLine261() {
		List messages = getMessagesAtLine( 261 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "No compiler extensions were found for the call statement, which has no default behavior." );
		if( messageWithSubstring == null ) fail( "No message with substring \"No compiler extensions were found for the call statement, which has no default behavior.\" was issued." );
	}

	/*
	 * call this.pgmFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "No compiler extensions were found for the call statement, which has no default behavior.".
	 */
	public void testLine262() {
		List messages = getMessagesAtLine( 262 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "No compiler extensions were found for the call statement, which has no default behavior." );
		if( messageWithSubstring == null ) fail( "No message with substring \"No compiler extensions were found for the call statement, which has no default behavior.\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine265() {
		List messages = getMessagesAtLine( 265 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine266() {
		List messages = getMessagesAtLine( 266 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
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
	 * call lib1.libIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine276() {
		List messages = getMessagesAtLine( 276 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call lib1.libIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a returns".
	 */
	public void testLine278() {
		List messages = getMessagesAtLine( 278 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a returns" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a returns\" was issued." );
	}

	/*
	 * call lib1.libIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine280() {
		List messages = getMessagesAtLine( 280 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call lib1.libIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine282() {
		List messages = getMessagesAtLine( 282 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call lib1.libIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function libIBMiReturn is not compatible with the type date".
	 */
	public void testLine284() {
		List messages = getMessagesAtLine( 284 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function libIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function libIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection.".
	 */
	public void testLine286() {
		List messages = getMessagesAtLine( 286 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection.\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine288() {
		List messages = getMessagesAtLine( 288 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using syslib.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection.".
	 */
	public void testLine290() {
		List messages = getMessagesAtLine( 290 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection.\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine292() {
		List messages = getMessagesAtLine( 292 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using syslib.getResource("binding:fred") as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine295() {
		List messages = getMessagesAtLine( 295 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine296() {
		List messages = getMessagesAtLine( 296 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call lib1.libFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "No compiler extensions were found for the call statement, which has no default behavior.".
	 */
	public void testLine299() {
		List messages = getMessagesAtLine( 299 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "No compiler extensions were found for the call statement, which has no default behavior." );
		if( messageWithSubstring == null ) fail( "No message with substring \"No compiler extensions were found for the call statement, which has no default behavior.\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine302() {
		List messages = getMessagesAtLine( 302 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine303() {
		List messages = getMessagesAtLine( 303 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * call hand1.handIBMiNoReturn();
	 * 0 validation messages are expected.
	 */
	public void testLine311() {
		List messages = getMessagesAtLine( 311 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call hand1.handIBMiReturn();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a returns expression".
	 */
	public void testLine313() {
		List messages = getMessagesAtLine( 313 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a returns expression" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a returns expression\" was issued." );
	}

	/*
	 * call hand1.handIBMiNoReturn() returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "cannot specify a returns value".
	 */
	public void testLine315() {
		List messages = getMessagesAtLine( 315 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot specify a returns value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot specify a returns value\" was issued." );
	}

	/*
	 * call hand1.handIBMiReturn() returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine317() {
		List messages = getMessagesAtLine( 317 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call hand1.handIBMiReturn() returns (d);
	 * 1 validation message is expected.
	 * It is expected to contain "return type int of the function handIBMiReturn is not compatible with the type date".
	 */
	public void testLine319() {
		List messages = getMessagesAtLine( 319 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "return type int of the function handIBMiReturn is not compatible with the type date" );
		if( messageWithSubstring == null ) fail( "No message with substring \"return type int of the function handIBMiReturn is not compatible with the type date\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection.".
	 */
	public void testLine321() {
		List messages = getMessagesAtLine( 321 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string' is not compatible with the expected type eglx.jtopen.IBMiConnection.\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine323() {
		List messages = getMessagesAtLine( 323 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using syslib.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection.".
	 */
	public void testLine325() {
		List messages = getMessagesAtLine( 325 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.jtopen.IBMiConnection.\" was issued." );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine327() {
		List messages = getMessagesAtLine( 327 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * using syslib.getResource("binding:fred") as IBMiConnection
	 * 0 validation messages are expected.
	 */
	public void testLine330() {
		List messages = getMessagesAtLine( 330 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * returns (i);
	 * 0 validation messages are expected.
	 */
	public void testLine331() {
		List messages = getMessagesAtLine( 331 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call hand1.handFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "No compiler extensions were found for the call statement, which has no default behavior.".
	 */
	public void testLine334() {
		List messages = getMessagesAtLine( 334 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "No compiler extensions were found for the call statement, which has no default behavior." );
		if( messageWithSubstring == null ) fail( "No message with substring \"No compiler extensions were found for the call statement, which has no default behavior.\" was issued." );
	}

	/*
	 * returning to callback
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine337() {
		List messages = getMessagesAtLine( 337 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}

	/*
	 * onexception	callback;
	 * 1 validation message is expected.
	 * It is expected to contain "\"returning to\" or \"onexception\" expression is not allowed".
	 */
	public void testLine338() {
		List messages = getMessagesAtLine( 338 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "\"returning to\" or \"onexception\" expression is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"\"returning to\" or \"onexception\" expression is not allowed\" was issued." );
	}
}
