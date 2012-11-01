package org.eclipse.edt.tests.validation.junit.services;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/services/ServiceProxyFunctionTests.egl
 */
public class ServiceProxyFunctionTestsTest extends ValidationTestCase {

	public ServiceProxyFunctionTestsTest() {
		super( "EGLSource/services/ServiceProxyFunctionTests.egl", false );
	}

	/*
	 * function f1() {@REST {method=POST}};
	 * 1 validation message is expected.
	 * It is expected to contain "The container for the eglx.rest.Rest function f1 is invalid. The property is only allowed on functions in Programs, Libraries, Services, and Basic Handlers.".
	 */
	public void testLine5() {
		List messages = getMessagesAtLine( 5 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The container for the eglx.rest.Rest function f1 is invalid. The property is only allowed on functions in Programs, Libraries, Services, and Basic Handlers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The container for the eglx.rest.Rest function f1 is invalid. The property is only allowed on functions in Programs, Libraries, Services, and Basic Handlers.\" was issued." );
	}

	/*
	 * function f2() {@EGLService {}};
	 * 1 validation message is expected.
	 * It is expected to contain "The container for the eglx.rest.EglService function f2 is invalid. The property is only allowed on functions in Programs, Libraries, Services, and Basic Handlers.".
	 */
	public void testLine6() {
		List messages = getMessagesAtLine( 6 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The container for the eglx.rest.EglService function f2 is invalid. The property is only allowed on functions in Programs, Libraries, Services, and Basic Handlers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The container for the eglx.rest.EglService function f2 is invalid. The property is only allowed on functions in Programs, Libraries, Services, and Basic Handlers.\" was issued." );
	}

	/*
	 * function f1() {@REST {method=_GET}};
	 * 1 validation message is expected.
	 * It is expected to contain "The container for the eglx.rest.Rest function f1 is invalid. The property is only allowed on functions in Programs, Libraries, Services, and Basic Handlers.".
	 */
	public void testLine10() {
		List messages = getMessagesAtLine( 10 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The container for the eglx.rest.Rest function f1 is invalid. The property is only allowed on functions in Programs, Libraries, Services, and Basic Handlers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The container for the eglx.rest.Rest function f1 is invalid. The property is only allowed on functions in Programs, Libraries, Services, and Basic Handlers.\" was issued." );
	}

	/*
	 * function f2() {@EGLService {}};
	 * 1 validation message is expected.
	 * It is expected to contain "The container for the eglx.rest.EglService function f2 is invalid. The property is only allowed on functions in Programs, Libraries, Services, and Basic Handlers.".
	 */
	public void testLine11() {
		List messages = getMessagesAtLine( 11 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The container for the eglx.rest.EglService function f2 is invalid. The property is only allowed on functions in Programs, Libraries, Services, and Basic Handlers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The container for the eglx.rest.EglService function f2 is invalid. The property is only allowed on functions in Programs, Libraries, Services, and Basic Handlers.\" was issued." );
	}

	/*
	 * {@REST{}}
	 * 1 validation message is expected.
	 * It is expected to contain "An Http method must be specified.".
	 */
	public void testLine22() {
		List messages = getMessagesAtLine( 22 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "An Http method must be specified." );
		if( messageWithSubstring == null ) fail( "No message with substring \"An Http method must be specified.\" was issued." );
	}

	/*
	 * function fp2 (p1 int in)
	 * 1 validation message is expected.
	 * It is expected to contain "The resource parameter p1 of the function fp2 which specifies the REST annotation must have a type of String or a part of type Record".
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The resource parameter p1 of the function fp2 which specifies the REST annotation must have a type of String or a part of type Record" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The resource parameter p1 of the function fp2 which specifies the REST annotation must have a type of String or a part of type Record\" was issued." );
	}

	/*
	 * function fp3 (p1 string)
	 * 1 validation message is expected.
	 * It is expected to contain "The parameter p1 for the fuction fp3 which specifies the REST annotation must be defined with the IN modifier.".
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The parameter p1 for the fuction fp3 which specifies the REST annotation must be defined with the IN modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The parameter p1 for the fuction fp3 which specifies the REST annotation must be defined with the IN modifier.\" was issued." );
	}

	/*
	 * function fp4 (p1 string in) returns(boolean)
	 * 1 validation message is expected.
	 * It is expected to contain "The function fp4 with specifies the REST annotation can only return a type of String or a part of type Record.".
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function fp4 with specifies the REST annotation can only return a type of String or a part of type Record." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function fp4 with specifies the REST annotation can only return a type of String or a part of type Record.\" was issued." );
	}

	/*
	 * function fp5(p1 string in) returns(string)
	 * 1 validation message is expected.
	 * It is expected to contain "The value of the responseFormat property must be NONE because the type of the resource parameter or return type of the function is String.".
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value of the responseFormat property must be NONE because the type of the resource parameter or return type of the function is String." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value of the responseFormat property must be NONE because the type of the resource parameter or return type of the function is String.\" was issued." );
	}

	/*
	 * function fp6(p1 string in, p2 string in)
	 * 1 validation message is expected.
	 * It is expected to contain "Only one resource parameter is allowed for the funcion fp6 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate.".
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Only one resource parameter is allowed for the funcion fp6 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Only one resource parameter is allowed for the funcion fp6 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate.\" was issued." );
	}

	/*
	 * {@REST{method=POST, RequestFormat=XML}}
	 * 1 validation message is expected.
	 * It is expected to contain "The value of the requestFormat property must be NONE because the type of the resource parameter or return type of the function is String.".
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value of the requestFormat property must be NONE because the type of the resource parameter or return type of the function is String." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value of the requestFormat property must be NONE because the type of the resource parameter or return type of the function is String.\" was issued." );
	}

	/*
	 * function fp8 (p1 rec1 in)
	 * 1 validation message is expected.
	 * It is expected to contain "The resource parameter p1 of the function fp8 which specifies a requestFormat of _FORM must be a part of type Record or Handler and the record cannot contain other records.".
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The resource parameter p1 of the function fp8 which specifies a requestFormat of _FORM must be a part of type Record or Handler and the record cannot contain other records." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The resource parameter p1 of the function fp8 which specifies a requestFormat of _FORM must be a part of type Record or Handler and the record cannot contain other records.\" was issued." );
	}

	/*
	 * function fp9 (p1 handler1 in)
	 * 1 validation message is expected.
	 * It is expected to contain "The resource parameter p1 of the function fp9 which specifies a requestFormat of _FORM must be a part of type Record or Handler and the record cannot contain other records.".
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The resource parameter p1 of the function fp9 which specifies a requestFormat of _FORM must be a part of type Record or Handler and the record cannot contain other records." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The resource parameter p1 of the function fp9 which specifies a requestFormat of _FORM must be a part of type Record or Handler and the record cannot contain other records.\" was issued." );
	}

	/*
	 * function fp10(p1 string in)
	 * 1 validation message is expected.
	 * It is expected to contain "A Resource parameter is not allowed for the funcion fp10 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate.".
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A Resource parameter is not allowed for the funcion fp10 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate." );
		if( messageWithSubstring == null ) fail( "No message with substring \"A Resource parameter is not allowed for the funcion fp10 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate.\" was issued." );
	}

	/*
	 * function fp11(p1 string in)
	 * 1 validation message is expected.
	 * It is expected to contain "A Resource parameter is not allowed for the funcion fp11 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate.".
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A Resource parameter is not allowed for the funcion fp11 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate." );
		if( messageWithSubstring == null ) fail( "No message with substring \"A Resource parameter is not allowed for the funcion fp11 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate.\" was issued." );
	}

	/*
	 * {@REST{method=_GET, uriTemplate="http://"
	 * 1 validation message is expected.
	 * It is expected to contain "The uri substitution variable p2 does not match any of the defined parameters for the function fp11.".
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The uri substitution variable p2 does not match any of the defined parameters for the function fp11." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The uri substitution variable p2 does not match any of the defined parameters for the function fp11.\" was issued." );
	}

	/*
	 * function fp12(p1 rec1 in)
	 * 1 validation message is expected.
	 * It is expected to contain "The non-resource parameter p1 for the function fp12 which specifies the REST annotation is not assignment compatible with String.".
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The non-resource parameter p1 for the function fp12 which specifies the REST annotation is not assignment compatible with String." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The non-resource parameter p1 for the function fp12 which specifies the REST annotation is not assignment compatible with String.\" was issued." );
	}

	/*
	 * i1 int = 5;
	 * 1 validation message is expected.
	 * It is expected to contain "A proxy function fp13 cannot contain statements.".
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A proxy function fp13 cannot contain statements." );
		if( messageWithSubstring == null ) fail( "No message with substring \"A proxy function fp13 cannot contain statements.\" was issued." );
	}

	/*
	 * {@REST{}}
	 * 1 validation message is expected.
	 * It is expected to contain "An Http method must be specified.".
	 */
	public void testLine75() {
		List messages = getMessagesAtLine( 75 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "An Http method must be specified." );
		if( messageWithSubstring == null ) fail( "No message with substring \"An Http method must be specified.\" was issued." );
	}

	/*
	 * {@REST{method=POST, responseFormat=_FORM}}
	 * 1 validation message is expected.
	 * It is expected to contain "The value _FORM for the responseFormat property is not supported.".
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value _FORM for the responseFormat property is not supported." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value _FORM for the responseFormat property is not supported.\" was issued." );
	}
}
