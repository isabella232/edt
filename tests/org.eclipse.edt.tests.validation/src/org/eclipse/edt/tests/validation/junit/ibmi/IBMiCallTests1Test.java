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
	 * It is expected to contain "target function has no ibmiProgram".
	 */
	public void testLine11() {
		List messages = getMessagesAtLine( 11 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "target function has no ibmiProgram" );
		if( messageWithSubstring == null ) fail( "No message with substring \"target function has no ibmiProgram\" was issued." );
	}

	/*
	 * call fp9(i1);
	 * 1 validation message is expected.
	 * It is expected to contain "can't resolve connection".
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "can't resolve connection" );
		if( messageWithSubstring == null ) fail( "No message with substring \"can't resolve connection\" was issued." );
	}

	/*
	 * call fp9(i1) using conn;
	 * 1 validation message is expected.
	 * It is expected to contain "wrong data type".
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "wrong data type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"wrong data type\" was issued." );
	}

	/*
	 * call fp9(i1) using conn returning to fp9;
	 * 1 validation message is expected.
	 * It is expected to contain "no callback".
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no callback" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no callback\" was issued." );
	}

	/*
	 * call fp9(i1) using conn onexception fp9;
	 * 1 validation message is expected.
	 * It is expected to contain "no onexception".
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no onexception" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no onexception\" was issued." );
	}

	/*
	 * call fp9(i1) using conn returns(i2);
	 * 1 validation message is expected.
	 * It is expected to contain "fp9 doesn't return anything".
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "fp9 doesn't return anything" );
		if( messageWithSubstring == null ) fail( "No message with substring \"fp9 doesn't return anything\" was issued." );
	}

	/*
	 * call fp8(i1) using conn returns(i2);
	 * 1 validation message is expected.
	 * It is expected to contain "f8 returns an int time is wrong return type".
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "f8 returns an int time is wrong return type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"f8 returns an int time is wrong return type\" was issued." );
	}

	/*
	 * call fp8(i1) using conn;
	 * 1 validation message is expected.
	 * It is expected to contain "f8 returns an int no return".
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "f8 returns an int no return" );
		if( messageWithSubstring == null ) fail( "No message with substring \"f8 returns an int no return\" was issued." );
	}

	/*
	 * call Service1.fp9(i1) using conn;
	 * 1 validation message is expected.
	 * It is expected to contain "target function is a service function".
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "target function is a service function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"target function is a service function\" was issued." );
	}

	/*
	 * call field1(i1) using conn;
	 * 1 validation message is expected.
	 * It is expected to contain "target is a field not a function".
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "target is a field not a function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"target is a field not a function\" was issued." );
	}

	/*
	 * call this.fp9(i1) using conn;
	 * 1 validation message is expected.
	 * It is expected to contain "target function has no ibmiProgram".
	 */
	public void testLine80() {
		List messages = getMessagesAtLine( 80 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "target function has no ibmiProgram" );
		if( messageWithSubstring == null ) fail( "No message with substring \"target function has no ibmiProgram\" was issued." );
	}
}
