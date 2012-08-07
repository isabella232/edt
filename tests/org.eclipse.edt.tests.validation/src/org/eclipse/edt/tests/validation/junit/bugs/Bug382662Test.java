package org.eclipse.edt.tests.validation.junit.bugs;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/bugs/Bug382662.egl
 */
public class Bug382662Test extends ValidationTestCase {

	public Bug382662Test() {
		super( "EGLSource/bugs/Bug382662.egl", false );
	}

	/*
	 * handler hand1 implements inter1 type ruihandler
	 * 1 validation message is expected.
	 * It is expected to contain "must implement the inherited function foo".
	 */
	public void testLine7() {
		List messages = getMessagesAtLine( 7 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must implement the inherited function foo" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must implement the inherited function foo\" was issued." );
	}

	/*
	 * service serv1 implements inter1 {@myType}
	 * 1 validation message is expected.
	 * It is expected to contain "must implement the inherited function foo".
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must implement the inherited function foo" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must implement the inherited function foo\" was issued." );
	}

	/*
	 * eglclass class1 implements inter1 type myType
	 * 1 validation message is expected.
	 * It is expected to contain "must implement the inherited function foo".
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must implement the inherited function foo" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must implement the inherited function foo\" was issued." );
	}
}
