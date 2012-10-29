package org.eclipse.edt.tests.validation.junit.ibmi;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/ibmi/IBMiCallTests1.egl
 */
public class IBMiCallTests1Test extends ValidationTestCase {

	public IBMiCallTests1Test() {
		super( "EGLSource/ibmi/IBMiCallTests1.egl", false );
	}

	/*
	 * call fp10(i1) using conn;
	 * 1 validation message is expected.
	 * It is expected to contain "The function fp10 must be defined with the IBMiProgram annotation".
	 */
	public void testLine11() {
		List messages = getMessagesAtLine( 11 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function fp10 must be defined with the IBMiProgram annotation" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function fp10 must be defined with the IBMiProgram annotation\" was issued." );
	}

	/*
	 * call fp9(i1);
	 * 1 validation message is expected.
	 * It is expected to contain "An IBM i call statement requires a connection, the using clause and the target function @Resource are missing".
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "An IBM i call statement requires a connection, the using clause and the target function @Resource are missing" );
		if( messageWithSubstring == null ) fail( "No message with substring \"An IBM i call statement requires a connection, the using clause and the target function @Resource are missing\" was issued." );
	}

	/*
	 * call fp9(i1) using conn;
	 * 1 validation message is expected.
	 * It is expected to contain "The argument i1 cannot be passed to the inOut or Out parameter p1 of the function fp9. The types boolean and int are not reference compatible".
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The argument i1 cannot be passed to the inOut or Out parameter p1 of the function fp9. The types boolean and int are not reference compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The argument i1 cannot be passed to the inOut or Out parameter p1 of the function fp9. The types boolean and int are not reference compatible\" was issued." );
	}

	/*
	 * call fp9(i1) using conn returning to fp9;
	 * 1 validation message is expected.
	 * It is expected to contain "A \"returning to\" or \"onexception\" expression is not allowed for a call to a local function".
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A \"returning to\" or \"onexception\" expression is not allowed for a call to a local function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"A \"returning to\" or \"onexception\" expression is not allowed for a call to a local function\" was issued." );
	}

	/*
	 * call fp9(i1) using conn onexception fp9;
	 * 1 validation message is expected.
	 * It is expected to contain "A \"returning to\" or \"onexception\" expression is not allowed for a call to a local function".
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A \"returning to\" or \"onexception\" expression is not allowed for a call to a local function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"A \"returning to\" or \"onexception\" expression is not allowed for a call to a local function\" was issued." );
	}

	/*
	 * call fp9(i1) using conn returns(i2);
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement cannot specify a returns value, because the function fp9 does not return a value.".
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement cannot specify a returns value, because the function fp9 does not return a value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement cannot specify a returns value, because the function fp9 does not return a value.\" was issued." );
	}

	/*
	 * call fp8(i1) using conn returns(i2);
	 * 1 validation message is expected.
	 * It is expected to contain "The return type fp8 of the function fp8 is not compatible with the type time of the returns expression i2 in the Call statement.".
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The return type fp8 of the function fp8 is not compatible with the type time of the returns expression i2 in the Call statement." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The return type fp8 of the function fp8 is not compatible with the type time of the returns expression i2 in the Call statement.\" was issued." );
	}

	/*
	 * call fp8(i1) using conn;
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement must specify a returns expression.".
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a returns expression." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a returns expression.\" was issued." );
	}

	/*
	 * call Service1.fp9(i1) using conn;
	 * 1 validation message is expected.
	 * It is expected to contain "Only a library part can be use as a qualifier.".
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Only a library part can be use as a qualifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Only a library part can be use as a qualifier.\" was issued." );
	}

	/*
	 * call field1(i1) using conn;
	 * 1 validation message is expected.
	 * It is expected to contain "The target of the Call must be a proxy function.".
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The target of the Call must be a proxy function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The target of the Call must be a proxy function.\" was issued." );
	}

	/*
	 * call this.fp9(i1) using conn;
	 * 0 validation messages are expected.
	 */
	public void testLine80() {
		List messages = getMessagesAtLine( 80 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * call fp9(i1) using conn;
	 * 0 validation messages are expected.
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 0, messages.size() );
	}
}
