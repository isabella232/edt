package org.eclipse.edt.tests.validation.junit.annotations;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/annotations/throws.egl
 */
public class ThrowsTest extends ValidationTestCase {

	public ThrowsTest() {
		super( "EGLSource/annotations/throws.egl", false );
	}

	/*
	 * e.foo();
	 * 1 validation message is expected.
	 * It is expected to contain "The invocation of the function, constructor or the declaration must be enclosed in a Try Statement that specifies AnyException or JavaObjectException in one of its OnException blocks.".
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The invocation of the function, constructor or the declaration must be enclosed in a Try Statement that specifies AnyException or JavaObjectException in one of its OnException blocks." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The invocation of the function, constructor or the declaration must be enclosed in a Try Statement that specifies AnyException or JavaObjectException in one of its OnException blocks.\" was issued." );
	}

	/*
	 * e.foo();
	 * 0 validation messages are expected.
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * e.foo();
	 * 1 validation message is expected.
	 * It is expected to contain "The invocation of the function, constructor or the declaration must be enclosed in a Try Statement that specifies AnyException or JavaObjectException in one of its OnException blocks.".
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The invocation of the function, constructor or the declaration must be enclosed in a Try Statement that specifies AnyException or JavaObjectException in one of its OnException blocks." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The invocation of the function, constructor or the declaration must be enclosed in a Try Statement that specifies AnyException or JavaObjectException in one of its OnException blocks.\" was issued." );
	}

	/*
	 * e.foo();
	 * 0 validation messages are expected.
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * e.foo();
	 * 0 validation messages are expected.
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * e.foo();
	 * 0 validation messages are expected.
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function foo(){@throws};
	 * 0 validation messages are expected.
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function foo(){@throws};
	 * 1 validation message is expected.
	 * It is expected to contain "The annotation Throws is only valid for functions and constructors in an ExternalType with subtype JavaObject.".
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The annotation Throws is only valid for functions and constructors in an ExternalType with subtype JavaObject." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The annotation Throws is only valid for functions and constructors in an ExternalType with subtype JavaObject.\" was issued." );
	}
}
