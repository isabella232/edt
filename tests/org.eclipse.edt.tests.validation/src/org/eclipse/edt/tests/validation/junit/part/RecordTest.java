package org.eclipse.edt.tests.validation.junit.part;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/part/record.egl
 */
public class RecordTest extends ValidationTestCase {

	public RecordTest() {
		super( "EGLSource/part/record.egl", false );
	}

	/*
	 * r loop2;
	 * 1 validation message is expected.
	 * It is expected to contain "The record loop2 contains an item whose type results in an illegal recursive loop.".
	 */
	public void testLine3() {
		List messages = getMessagesAtLine( 3 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The record loop2 contains an item whose type results in an illegal recursive loop." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The record loop2 contains an item whose type results in an illegal recursive loop.\" was issued." );
	}

	/*
	 * r loop3;
	 * 1 validation message is expected.
	 * It is expected to contain "The record loop3 contains an item whose type results in an illegal recursive loop.".
	 */
	public void testLine6() {
		List messages = getMessagesAtLine( 6 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The record loop3 contains an item whose type results in an illegal recursive loop." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The record loop3 contains an item whose type results in an illegal recursive loop.\" was issued." );
	}

	/*
	 * r loop1;
	 * 1 validation message is expected.
	 * It is expected to contain "The record loop1 contains an item whose type results in an illegal recursive loop.".
	 */
	public void testLine9() {
		List messages = getMessagesAtLine( 9 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The record loop1 contains an item whose type results in an illegal recursive loop." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The record loop1 contains an item whose type results in an illegal recursive loop.\" was issued." );
	}

	/*
	 * r noloop2;
	 * 0 validation messages are expected.
	 */
	public void testLine13() {
		List messages = getMessagesAtLine( 13 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * x int;
	 * 0 validation messages are expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * r loopSelf;
	 * 1 validation message is expected.
	 * It is expected to contain "The record loopSelf contains an item whose type results in an illegal recursive loop.".
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The record loopSelf contains an item whose type results in an illegal recursive loop." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The record loopSelf contains an item whose type results in an illegal recursive loop.\" was issued." );
	}
}
