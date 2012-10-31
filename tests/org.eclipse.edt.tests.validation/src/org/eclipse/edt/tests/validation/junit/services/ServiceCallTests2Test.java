package org.eclipse.edt.tests.validation.junit.services;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/services/ServiceCallTests2.egl
 */
public class ServiceCallTests2Test extends ValidationTestCase {

	public ServiceCallTests2Test() {
		super( "EGLSource/services/ServiceCallTests2.egl", false );
	}

	/*
	 * call myserv.srvfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a \"returning to\" or \"returns\" function.".
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * call myserv.srvfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a \"returning to\" or \"returns\" function.".
	 */
	public void testLine22() {
		List messages = getMessagesAtLine( 22 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * call myserv.srvfunc()
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'string(12)' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string(12)' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string(12)' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * call myserv.srvfunc()
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * using Resources.getResource( "binding:fred") as IHTTP
	 * 0 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc() returns(i);
	 * 0 validation messages are expected.
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a \"returning to\" or \"returns\" function.".
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * call myinter.interfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a \"returning to\" or \"returns\" function.".
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * call myinter.interfunc()
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'string(12)' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string(12)' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string(12)' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * call myinter.interfunc()
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * using Resources.getResource( "binding:fred") as IHTTP
	 * 0 validation messages are expected.
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc() returns(i);
	 * 0 validation messages are expected.
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a \"returning to\" or \"returns\" function".
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function\" was issued." );
	}

	/*
	 * call myserv.srvfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a \"returning to\" or \"returns\" function".
	 */
	public void testLine85() {
		List messages = getMessagesAtLine( 85 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function\" was issued." );
	}

	/*
	 * call myserv.srvfunc()
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'string(12)' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string(12)' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string(12)' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * call myserv.srvfunc()
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine91() {
		List messages = getMessagesAtLine( 91 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * using Resources.getResource( "binding:fred") as IHTTP
	 * 0 validation messages are expected.
	 */
	public void testLine97() {
		List messages = getMessagesAtLine( 97 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc() returns(i);
	 * 0 validation messages are expected.
	 */
	public void testLine100() {
		List messages = getMessagesAtLine( 100 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine103() {
		List messages = getMessagesAtLine( 103 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a \"returning to\" or \"returns\" function.".
	 */
	public void testLine109() {
		List messages = getMessagesAtLine( 109 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * call myinter.interfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a \"returning to\" or \"returns\" function.".
	 */
	public void testLine110() {
		List messages = getMessagesAtLine( 110 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * call myinter.interfunc()
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'string(12)' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'string(12)' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'string(12)' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * call myinter.interfunc()
	 * 1 validation message is expected.
	 * It is expected to contain "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.".
	 */
	public void testLine116() {
		List messages = getMessagesAtLine( 116 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The specified using clause type 'any' is not compatible with the expected type eglx.http.IHttp.\" was issued." );
	}

	/*
	 * using Resources.getResource( "binding:fred") as IHTTP
	 * 0 validation messages are expected.
	 */
	public void testLine122() {
		List messages = getMessagesAtLine( 122 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc() returns(i);
	 * 0 validation messages are expected.
	 */
	public void testLine125() {
		List messages = getMessagesAtLine( 125 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine127() {
		List messages = getMessagesAtLine( 127 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine128() {
		List messages = getMessagesAtLine( 128 );
		assertEquals( 0, messages.size() );
	}
}
