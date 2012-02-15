package org.eclipse.edt.tests.validation.junit.callStatement;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/callStatement/LocalFunctionCallTests.egl
 */
public class LocalFunctionCallTestsTest extends ValidationTestCase {

	public LocalFunctionCallTestsTest() {
		super( "EGLSource/callStatement/LocalFunctionCallTests.egl", false );
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
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
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
	 * using syslib.getResource("binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
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
	 * It is expected to contain "must be defined with the IBMiProgram annotation".
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be defined with the IBMiProgram annotation" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be defined with the IBMiProgram annotation\" was issued." );
	}

	/*
	 * call this.srvFunc returns (i);
	 * 1 validation message is expected.
	 * It is expected to contain "must be defined with the IBMiProgram annotation".
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be defined with the IBMiProgram annotation" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be defined with the IBMiProgram annotation\" was issued." );
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
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
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
	 * It is expected to contain "type of the using expression must be eglx.jtopen.IBMiConnection".
	 */
	public void testLine110() {
		List messages = getMessagesAtLine( 110 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.jtopen.IBMiConnection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.jtopen.IBMiConnection\" was issued." );
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
	 * It is expected to contain "must be defined with the IBMiProgram annotation".
	 */
	public void testLine118() {
		List messages = getMessagesAtLine( 118 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be defined with the IBMiProgram annotation" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be defined with the IBMiProgram annotation\" was issued." );
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
}
