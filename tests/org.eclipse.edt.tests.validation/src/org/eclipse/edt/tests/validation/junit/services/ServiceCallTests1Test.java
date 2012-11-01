package org.eclipse.edt.tests.validation.junit.services;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/services/ServiceCallTests1.egl
 */
public class ServiceCallTests1Test extends ValidationTestCase {

	public ServiceCallTests1Test() {
		super( "EGLSource/services/ServiceCallTests1.egl", false );
	}

	/*
	 * call field1(i1) using httpProxy;
	 * 1 validation message is expected.
	 * It is expected to contain "The target of the Call must be a Service or proxy function.".
	 */
	public void testLine13() {
		List messages = getMessagesAtLine( 13 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The target of the Call must be a Service or proxy function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The target of the Call must be a Service or proxy function.\" was issued." );
	}

	/*
	 * call field1(i1) using httpRest;
	 * 1 validation message is expected.
	 * It is expected to contain "The target of the Call must be a Service or proxy function.".
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The target of the Call must be a Service or proxy function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The target of the Call must be a Service or proxy function.\" was issued." );
	}

	/*
	 * call fp8(i1) using httpRest;
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a \"returning to\" or \"returns\" function.".
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * call fp8(i1) using httpProxy;
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a \"returning to\" or \"returns\" function.".
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a \"returning to\" or \"returns\" function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a \"returning to\" or \"returns\" function.\" was issued." );
	}

	/*
	 * call fp8(i1) using httpProxy returning to response1;
	 * 1 validation message is expected.
	 * It is expected to contain "The type fp8 cannot be passed to the parameter p1 of the function response1. It is not assignment compatible with boolean.".
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type fp8 cannot be passed to the parameter p1 of the function response1. It is not assignment compatible with boolean." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type fp8 cannot be passed to the parameter p1 of the function response1. It is not assignment compatible with boolean.\" was issued." );
	}

	/*
	 * call fp13(s1, r1, i1) using httpProxy returning to response6;
	 * 1 validation message is expected.
	 * It is expected to contain "The type int cannot be passed to the parameter p3 of the function response6. It is not assignment compatible with boolean.".
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type int cannot be passed to the parameter p3 of the function response6. It is not assignment compatible with boolean." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type int cannot be passed to the parameter p3 of the function response6. It is not assignment compatible with boolean.\" was issued." );
	}

	/*
	 * call fp7(i1) using httpProxy onexception response1;
	 * 1 validation message is expected.
	 * It is expected to contain "The parameter at position 1 of function response1 must have a type of eglx.lang.anyexception.".
	 */
	public void testLine45() {
		List messages = getMessagesAtLine( 45 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The parameter at position 1 of function response1 must have a type of eglx.lang.anyexception." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The parameter at position 1 of function response1 must have a type of eglx.lang.anyexception.\" was issued." );
	}

	/*
	 * call fp8(i1) using httpProxy returning to response2;
	 * 1 validation message is expected.
	 * It is expected to contain "All of the parameters in \"returning to\" or \"onexception\" function response2 must be defined with the IN modifier.".
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "All of the parameters in \"returning to\" or \"onexception\" function response2 must be defined with the IN modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"All of the parameters in \"returning to\" or \"onexception\" function response2 must be defined with the IN modifier.\" was issued." );
	}

	/*
	 * call fp8(i1) using httpProxy returning to response3;
	 * 1 validation message is expected.
	 * It is expected to contain "All of the parameters in \"returning to\" or \"onexception\" function response3 must be defined with the IN modifier.".
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "All of the parameters in \"returning to\" or \"onexception\" function response3 must be defined with the IN modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"All of the parameters in \"returning to\" or \"onexception\" function response3 must be defined with the IN modifier.\" was issued." );
	}

	/*
	 * call fp8(i1) using httpProxy returning to field1;
	 * 1 validation message is expected.
	 * It is expected to contain "The \"returning to\" or \"onexception\" expression must resolve to a function.".
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The \"returning to\" or \"onexception\" expression must resolve to a function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The \"returning to\" or \"onexception\" expression must resolve to a function.\" was issued." );
	}

	/*
	 * call fp8(i1) using httpProxy returning to response4;
	 * 1 validation message is expected.
	 * It is expected to contain "The \"returning to\" or \"onexception\" function response4 cannot return a type.".
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The \"returning to\" or \"onexception\" function response4 cannot return a type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The \"returning to\" or \"onexception\" function response4 cannot return a type.\" was issued." );
	}

	/*
	 * call lib1.fp1(i1) using httpProxy returning to lib1.response1;
	 * 1 validation message is expected.
	 * It is expected to contain "The function response1 must be defined in the part pgm1.".
	 */
	public void testLine70() {
		List messages = getMessagesAtLine( 70 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function response1 must be defined in the part pgm1." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function response1 must be defined in the part pgm1.\" was issued." );
	}

	/*
	 * call fp7(i1) using httpProxy returning to response5;
	 * 1 validation message is expected.
	 * It is expected to contain "The \"returning to\" or \"onexception\" function response5 requires 0 parameter(s).".
	 */
	public void testLine75() {
		List messages = getMessagesAtLine( 75 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The \"returning to\" or \"onexception\" function response5 requires 0 parameter(s)." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The \"returning to\" or \"onexception\" function response5 requires 0 parameter(s).\" was issued." );
	}

	/*
	 * call fp13(s1, i1, i1) using httpProxy returning to response7;
	 * 1 validation message is expected.
	 * It is expected to contain "The argument i1 cannot be passed to the in or out parameter p2 of the function fp13. The types int and rec1 are not assignment compatible.".
	 */
	public void testLine82() {
		List messages = getMessagesAtLine( 82 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The argument i1 cannot be passed to the in or out parameter p2 of the function fp13. The types int and rec1 are not assignment compatible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The argument i1 cannot be passed to the in or out parameter p2 of the function fp13. The types int and rec1 are not assignment compatible.\" was issued." );
	}

	/*
	 * call fp13(i1) using httpProxy returning to response7;
	 * 1 validation message is expected.
	 * It is expected to contain "Function fp13 requires exactly 3 argument(s)".
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Function fp13 requires exactly 3 argument(s)" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Function fp13 requires exactly 3 argument(s)\" was issued." );
	}

	/*
	 * call HANDLER1.fp1(i1) using httpProxy returning to response5;
	 * 1 validation message is expected.
	 * It is expected to contain "Only a library or service part can be use as a qualifier.".
	 */
	public void testLine92() {
		List messages = getMessagesAtLine( 92 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Only a library or service part can be use as a qualifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Only a library or service part can be use as a qualifier.\" was issued." );
	}

	/*
	 * call fp7(i1) using httpProxy onexception response8;
	 * 1 validation message is expected.
	 * It is expected to contain "The \"returning to\" or \"onexception\" function response8 requires 1 parameter(s).".
	 */
	public void testLine97() {
		List messages = getMessagesAtLine( 97 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The \"returning to\" or \"onexception\" function response8 requires 1 parameter(s)." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The \"returning to\" or \"onexception\" function response8 requires 1 parameter(s).\" was issued." );
	}
}
