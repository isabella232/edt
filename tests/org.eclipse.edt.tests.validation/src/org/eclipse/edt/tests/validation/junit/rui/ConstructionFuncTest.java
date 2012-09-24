package org.eclipse.edt.tests.validation.junit.rui;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/rui/constructionFunc.egl
 */
public class ConstructionFuncTest extends ValidationTestCase {

	public ConstructionFuncTest() {
		super( "EGLSource/rui/constructionFunc.egl", false );
	}

	/*
	 * handler h1 type ruihandler{onconstructionfunction = start}
	 * 0 validation messages are expected.
	 */
	public void testLine1() {
		List messages = getMessagesAtLine( 1 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * handler w1 type ruiwidget{onconstructionfunction = start}
	 * 0 validation messages are expected.
	 */
	public void testLine5() {
		List messages = getMessagesAtLine( 5 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * handler h2 type ruihandler{onconstructionfunction = lib1.foo}
	 * 1 validation message is expected.
	 * It is expected to contain "The value for property onConstructionFunction must be the name of a function within part h2. Functions defined in other parts are not allowed.".
	 */
	public void testLine13() {
		List messages = getMessagesAtLine( 13 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value for property onConstructionFunction must be the name of a function within part h2. Functions defined in other parts are not allowed." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value for property onConstructionFunction must be the name of a function within part h2. Functions defined in other parts are not allowed.\" was issued." );
	}

	/*
	 * handler w2 type ruiwidget{onconstructionfunction = lib1.foo}
	 * 1 validation message is expected.
	 * It is expected to contain "The value for property onConstructionFunction must be the name of a function within part w2. Functions defined in other parts are not allowed.".
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value for property onConstructionFunction must be the name of a function within part w2. Functions defined in other parts are not allowed." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value for property onConstructionFunction must be the name of a function within part w2. Functions defined in other parts are not allowed.\" was issued." );
	}

	/*
	 * handler h3 type ruihandler{onconstructionfunction = h.func}
	 * 1 validation message is expected.
	 * It is expected to contain "The value for property onConstructionFunction must be the name of a function within part h3. Functions defined in other parts are not allowed.".
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value for property onConstructionFunction must be the name of a function within part h3. Functions defined in other parts are not allowed." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value for property onConstructionFunction must be the name of a function within part h3. Functions defined in other parts are not allowed.\" was issued." );
	}

	/*
	 * handler w3 type ruiwidget{onconstructionfunction = h.func}
	 * 1 validation message is expected.
	 * It is expected to contain "The value for property onConstructionFunction must be the name of a function within part w3. Functions defined in other parts are not allowed.".
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value for property onConstructionFunction must be the name of a function within part w3. Functions defined in other parts are not allowed." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value for property onConstructionFunction must be the name of a function within part w3. Functions defined in other parts are not allowed.\" was issued." );
	}
}
