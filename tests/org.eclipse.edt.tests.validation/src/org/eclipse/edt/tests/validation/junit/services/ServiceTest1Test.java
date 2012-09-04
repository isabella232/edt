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
package org.eclipse.edt.tests.validation.junit.services;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/ibmi/IBMiTest1.egl
 */
public class ServiceTest1Test extends ValidationTestCase {

	public ServiceTest1Test() {
		super( "EGLSource/services/ServiceTest1.egl", false );
	}

	/*
	 * function f1() {@REST {}}; //1 container for the REST function f1 is invalid
	 * 1 validation message is expected.
	 * It is expected to contain "The container for the eglx.rest.rest function f1 is invalid".
	 */
	public void testLine2() {
		List messages = getMessagesAtLine( 2 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The container for the eglx.rest.rest function f1 is invalid" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The container for the eglx.rest.rest function f1 is invalid\" was issued." );
	}

	/*
	 * function f2() {@EGLService {}}; //1 container for the EGLService function f1 is invalid
	 * 1 validation message is expected.
	 * It is expected to contain "The container for the eglx.rest.eglservice function f1 is invalid".
	 */
	public void testLine3() {
		List messages = getMessagesAtLine( 3 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The container for the eglx.rest.eglservice function f2 is invalid" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The container for the eglx.rest.eglservice function f2 is invalid\" was issued." );
	}

	/*
	 * function f1() {@REST {}}; //1 container for the REST function f1 is invalid
	 * 1 validation message is expected.
	 * It is expected to contain "The container for the eglx.rest.rest function f1 is invalid".
	 */
	public void testLine7() {
		List messages = getMessagesAtLine( 7 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The container for the eglx.rest.rest function f1 is invalid" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The container for the eglx.rest.rest function f1 is invalid\" was issued." );
	}

	/*
	 * function f2() {@EGLService {}}; //1 container for the EGLService function f1 is invalid
	 * 1 validation message is expected.
	 * It is expected to contain "The container for the eglx.rest.rest function f1 is invalid".
	 */
	public void testLine8() {
		List messages = getMessagesAtLine( 8 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The container for the eglx.rest.eglservice function f2 is invalid" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The container for the eglx.rest.eglservice function f2 is invalid\" was issued." );
	}

	/*
	 * {@REST{}} //requires http method
	 * 1 validation message is expected.
	 * It is expected to contain "An Http method must be specified".
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "An Http method must be specified" );
		if( messageWithSubstring == null ) fail( "No message with substring \"An Http method must be specified\" was issued." );
	}

	/*
	 * function fp2 (p1 int in)//wrong type
	 * 1 validation message is expected.
	 * It is expected to contain "The resource parameter p1 of the function fp2 which specifies the REST annotation must have a type of String or a part of type Record".
	 */
	public void testLine22() {
		List messages = getMessagesAtLine( 22 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The resource parameter p1 of the function fp2 which specifies the REST annotation must have a type of String or a part of type Record" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The resource parameter p1 of the function fp2 which specifies the REST annotation must have a type of String or a part of type Record\" was issued." );
	}

	/*
	 * function fp3 (p1 string)//missing in modifier
	 * 1 validation message is expected.
	 * It is expected to contain "The parameter p1 for the fuction fp3 which specifies the REST annotation must be defined with the IN modifier.".
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The parameter p1 for the fuction fp3 which specifies the REST annotation must be defined with the IN modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The parameter p1 for the fuction fp3 which specifies the REST annotation must be defined with the IN modifier.\" was issued." );
	}

	/*
	 * function fp4 (p1 string in) returns(boolean)//wrong returns type
	 * 1 validation message is expected.
	 * It is expected to contain "The function fp4 with specifies the REST annotation can only return a type of String or a part of type Record.".
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function fp4 with specifies the REST annotation can only return a type of String or a part of type Record." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function fp4 with specifies the REST annotation can only return a type of String or a part of type Record.\" was issued." );
	}

	/*
	 * function fp4 (p1 string in) returns(string)//response format should be null
		{@REST{method=POST, responseFormat=XML}} 
	 * 1 validation message is expected.
	 * It is expected to contain "The value of the responseFormat property must be NONE because the type of the resource parameter or return type of the function is String".
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value of the responseFormat property must be NONE because the type of the resource parameter or return type of the function is String" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value of the responseFormat property must be NONE because the type of the resource parameter or return type of the function is String.\" was issued." );
	}

	/*
	 * function fp6(p1 string in, p2 string in)
		{@REST{method=POST, responseFormat=XML}} 
	 * 1 validation message is expected.
	 * It is expected to contain "Only one resource parameter is allowed for the funcion fp6 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate".
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Only one resource parameter is allowed for the funcion fp6 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Only one resource parameter is allowed for the funcion fp6 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate\" was issued." );
	}

	/*
	 * 	function fp7 (p1 string in)//wrong request format
		{@REST{method=POST, RequestFormat=XML}}  
	 * 1 validation message is expected.
	 * It is expected to contain "The value of the requestFormat property must be NONE because the type of the resource parameter or return type of the function is String.".
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value of the requestFormat property must be NONE because the type of the resource parameter or return type of the function is String." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value of the requestFormat property must be NONE because the type of the resource parameter or return type of the function is String.\" was issued." );
	}

	/*
	 * function fp9 (p1 rec1 in)//wrong request format
		{@REST{method=POST, RequestFormat=_Form}} 
	 * 1 validation message is expected.
	 * It is expected to contain "The resource parameter p1 of the function fp8 which specifies a requestFormat of _FORM must be a part of type Record or Handler and the record cannot contain other records.".
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The resource parameter p1 of the function fp8 which specifies a requestFormat of _FORM must be a part of type Record or Handler and the record cannot contain other records." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The resource parameter p1 of the function fp8 which specifies a requestFormat of _FORM must be a part of type Record or Handler and the record cannot contain other records.\" was issued." );
	}

	/*
	 * function fp9 (p1 handler1 in)//wrong request format
		{@REST{method=POST, RequestFormat=_Form}} 
	 * 1 validation message is expected.
	 * It is expected to contain "The resource parameter p1 of the function fp9 which specifies a requestFormat of _FORM must be a part of type Record or Handler and the record cannot contain other records.".
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The resource parameter p1 of the function fp9 which specifies a requestFormat of _FORM must be a part of type Record or Handler and the record cannot contain other records." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The resource parameter p1 of the function fp9 which specifies a requestFormat of _FORM must be a part of type Record or Handler and the record cannot contain other records.\" was issued." );
	}

	/*
	 * function fp6(p1 string in)//resources not allowed
		{@REST{method=_GET}} 
	 * 1 validation message is expected.
	 * It is expected to contain "A Resource parameter is not allowed for the funcion fp10 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate.".
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A Resource parameter is not allowed for the funcion fp10 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate." );
		if( messageWithSubstring == null ) fail( "No message with substring \"A Resource parameter is not allowed for the funcion fp10 which specifies the REST annotation. Parameter p1 must be removed or specified in the uriTemplate.\" was issued." );
	}

	/*
	 * function fp6(p1 string in)//resources not allowed
		{@REST{method=_GET, uriTemplate="http://{p2}"}}
	 * 1 validation message is expected.
	 * It is expected to contain "The uri substitution variable p2 does not match any of the defined parameters for the function fp11.".
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The uri substitution variable p2 does not match any of the defined parameters for the function fp11." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The uri substitution variable p2 does not match any of the defined parameters for the function fp11.\" was issued." );
	}

	/*
	 * function fp6(p1 rec1 in)//substitution wrong type
		{@REST{method=_GET, uriTemplate="http://{p1}"}}
	 * 1 validation message is expected.
	 * It is expected to contain "The non-resource parameter p1 for the function fp12 which specifies the REST annotation is not assignment compatible with String.".
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The non-resource parameter p1 for the function fp12 which specifies the REST annotation is not assignment compatible with String." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The non-resource parameter p1 for the function fp12 which specifies the REST annotation is not assignment compatible with String.\" was issued." );
	}

	/*
	 * assignment in the body
	 * 1 validation message is expected.
	 * It is expected to contain "A proxy function {0} cannot contain statements".
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A proxy function fp13 cannot contain statements" );
		if( messageWithSubstring == null ) fail( "No message with substring \"A proxy function fp13 cannot contain statements\" was issued." );
	}

	/*
	 * no http method
	 * 1 validation message is expected.
	 * It is expected to contain "An Http method must be specified.".
	 */
	public void testLine72() {
		List messages = getMessagesAtLine( 72 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "An Http method must be specified." );
		if( messageWithSubstring == null ) fail( "No message with substring \"An Http method must be specified.\" was issued." );
	}

	/*
	 * response form not allowed
	 * 1 validation message is expected.
	 * It is expected to contain "The value _FORM for the responseFormat property is not supported.".
	 */
	public void testLine76() {
		List messages = getMessagesAtLine( 76 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value _FORM for the responseFormat property is not supported." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value _FORM for the responseFormat property is not supported.\" was issued." );
	}
}
