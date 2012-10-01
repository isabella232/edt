package org.eclipse.edt.tests.validation.junit.expressions;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/expressions/arrayAccess.egl
 */
public class ArrayAccessTest extends ValidationTestCase {

	public ArrayAccessTest() {
		super( "EGLSource/expressions/arrayAccess.egl", false );
	}

	/*
	 * array[1] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine12() {
		List messages = getMessagesAtLine( 12 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * array[myany] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine13() {
		List messages = getMessagesAtLine( 13 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * array[notArray] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine14() {
		List messages = getMessagesAtLine( 14 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * array2d[1][2] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * array2d[notArray][notArray] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * myAny[22] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * myAny["foo"] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * myAny["foo"]["bar"] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * et["foo"] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * array["foo"] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * array[1,2] = 10;
	 * 1 validation message is expected.
	 * It is expected to contain "Specifying multiple array indices in the array access array[1,2] is not allowed.".
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Specifying multiple array indices in the array access array[1,2] is not allowed." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Specifying multiple array indices in the array access array[1,2] is not allowed.\" was issued." );
	}

	/*
	 * myAny["foo","bar"] = 2;
	 * 1 validation message is expected.
	 * It is expected to contain "Specifying multiple array indices in the array access myAny[\"foo\",\"bar\"] is not allowed.".
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Specifying multiple array indices in the array access myAny[\"foo\",\"bar\"] is not allowed." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Specifying multiple array indices in the array access myAny[\"foo\",\"bar\"] is not allowed.\" was issued." );
	}

	/*
	 * notArray[1] = 10;
	 * 1 validation message is expected.
	 * It is expected to contain "notArray is not an array and cannot be subscripted.".
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "notArray is not an array and cannot be subscripted." );
		if( messageWithSubstring == null ) fail( "No message with substring \"notArray is not an array and cannot be subscripted.\" was issued." );
	}

	/*
	 * array[1][2] = 10;
	 * 1 validation message is expected.
	 * It is expected to contain "array[1] is not an array and cannot be subscripted.".
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "array[1] is not an array and cannot be subscripted." );
		if( messageWithSubstring == null ) fail( "No message with substring \"array[1] is not an array and cannot be subscripted.\" was issued." );
	}

	/*
	 * myAny[new date] = 10;
	 * 1 validation message is expected.
	 * It is expected to contain "The subscript new date in array reference myAny[new date] must be an integer item or integer literal.".
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The subscript new date in array reference myAny[new date] must be an integer item or integer literal." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The subscript new date in array reference myAny[new date] must be an integer item or integer literal.\" was issued." );
	}

	/*
	 * notArray["foo"] = 10;
	 * 1 validation message is expected.
	 * It is expected to contain "notArray cannot be accessed with a string subscript.".
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "notArray cannot be accessed with a string subscript." );
		if( messageWithSubstring == null ) fail( "No message with substring \"notArray cannot be accessed with a string subscript.\" was issued." );
	}

	/*
	 * myRec["foo"] = 10;
	 * 1 validation message is expected.
	 * It is expected to contain "myRec cannot be accessed with a string subscript.".
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "myRec cannot be accessed with a string subscript." );
		if( messageWithSubstring == null ) fail( "No message with substring \"myRec cannot be accessed with a string subscript.\" was issued." );
	}
}
