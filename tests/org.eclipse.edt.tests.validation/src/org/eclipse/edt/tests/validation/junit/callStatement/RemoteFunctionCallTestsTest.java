package org.eclipse.edt.tests.validation.junit.callStatement;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/callStatement/RemoteFunctionCallTests.egl
 */
public class RemoteFunctionCallTestsTest extends ValidationTestCase {

	public RemoteFunctionCallTestsTest() {
		super( "EGLSource/callStatement/RemoteFunctionCallTests.egl", false );
	}

	/*
	 * call myserv.srvfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
	}

	/*
	 * call servVar.srvfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred") as IHTTP
	 * 0 validation messages are expected.
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc() returns(i);
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
	}

	/*
	 * call myserv.srvfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine35() {
		List messages = getMessagesAtLine( 35 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call servVar.srvfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
	}

	/*
	 * call intervar.interfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred") as IHTTP
	 * 0 validation messages are expected.
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc() returns(i);
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
	}

	/*
	 * call myinter.interfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call intervar.interfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
	}

	/*
	 * call servVar.srvfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
	 */
	public void testLine91() {
		List messages = getMessagesAtLine( 91 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred") as IHTTP
	 * 0 validation messages are expected.
	 */
	public void testLine96() {
		List messages = getMessagesAtLine( 96 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myserv.srvfunc() returns(i);
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine99() {
		List messages = getMessagesAtLine( 99 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
	}

	/*
	 * call myserv.srvfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine101() {
		List messages = getMessagesAtLine( 101 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call servVar.srvfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine108() {
		List messages = getMessagesAtLine( 108 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
	}

	/*
	 * call intervar.interfunc();
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine109() {
		List messages = getMessagesAtLine( 109 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
	}

	/*
	 * using "binding:fred"
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred")
	 * 1 validation message is expected.
	 * It is expected to contain "type of the using expression must be eglx.http.IHTTP".
	 */
	public void testLine116() {
		List messages = getMessagesAtLine( 116 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of the using expression must be eglx.http.IHTTP" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of the using expression must be eglx.http.IHTTP\" was issued." );
	}

	/*
	 * using syslib.getResource( "binding:fred") as IHTTP
	 * 0 validation messages are expected.
	 */
	public void testLine121() {
		List messages = getMessagesAtLine( 121 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call myinter.interfunc() returns(i);
	 * 1 validation message is expected.
	 * It is expected to contain "must specify a \"returning to\" function".
	 */
	public void testLine124() {
		List messages = getMessagesAtLine( 124 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must specify a \"returning to\" function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must specify a \"returning to\" function\" was issued." );
	}

	/*
	 * call myinter.interfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine126() {
		List messages = getMessagesAtLine( 126 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call intervar.interfunc() returning to callback;
	 * 0 validation messages are expected.
	 */
	public void testLine127() {
		List messages = getMessagesAtLine( 127 );
		assertEquals( 0, messages.size() );
	}
}
