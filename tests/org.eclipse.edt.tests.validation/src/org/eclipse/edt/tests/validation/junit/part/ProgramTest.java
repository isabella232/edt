package org.eclipse.edt.tests.validation.junit.part;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/part/program.egl
 */
public class ProgramTest extends ValidationTestCase {

	public ProgramTest() {
		super( "EGLSource/part/program.egl", false );
	}

	/*
	 * program ezeProg
	 * 1 validation message is expected.
	 */
	public void testLine13() {
		List messages = getMessagesAtLine( 13 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * program p0()
	 * 1 validation message is expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * program p1
	 * 1 validation message is expected.
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * const a string = "fff";
	 * 0 validation messages are expected.
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const cat string = "cat";
	 * 0 validation messages are expected.
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const cat int = 5;
	 * 1 validation message is expected.
	 * It is expected to contain "The same name cat also appears as variable, parameter, use or constant declaration in Function, Program, or Library p1".
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The same name cat also appears as variable, parameter, use or constant declaration in Function, Program, or Library p1" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The same name cat also appears as variable, parameter, use or constant declaration in Function, Program, or Library p1\" was issued." );
	}

	/*
	 * dog string;
	 * 0 validation messages are expected.
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * dog int;
	 * 1 validation message is expected.
	 * It is expected to contain "The same name dog also appears as variable, parameter, use or constant declaration in Function, Program, or Library p1".
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The same name dog also appears as variable, parameter, use or constant declaration in Function, Program, or Library p1" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The same name dog also appears as variable, parameter, use or constant declaration in Function, Program, or Library p1\" was issued." );
	}

	/*
	 * const mouse string = "mouse";
	 * 0 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * mouse int;
	 * 1 validation message is expected.
	 * It is expected to contain "The same name mouse also appears as variable, parameter, use or constant declaration in Function, Program, or Library p1".
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The same name mouse also appears as variable, parameter, use or constant declaration in Function, Program, or Library p1" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The same name mouse also appears as variable, parameter, use or constant declaration in Function, Program, or Library p1\" was issued." );
	}

	/*
	 * function zxcv ()
	 * 1 validation message is expected.
	 * It is expected to contain "A duplicate function".
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A duplicate function" );
		if( messageWithSubstring == null ) fail( "No message with substring \"A duplicate function\" was issued." );
	}

	/*
	 * function main( i int)
	 * 1 validation message is expected.
	 * It is expected to contain "Main functions cannot contain parameters".
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Main functions cannot contain parameters" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Main functions cannot contain parameters\" was issued." );
	}

	/*
	 * function main ()
	 * 0 validation messages are expected.
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * use mylibx;
	 * 0 validation messages are expected.
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * use mylibx;
	 * 1 validation message is expected.
	 * It is expected to contain "A duplicate use declaration named mylibx is declared in Function, Program, or Library p4".
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A duplicate use declaration named mylibx is declared in Function, Program, or Library p4" );
		if( messageWithSubstring == null ) fail( "No message with substring \"A duplicate use declaration named mylibx is declared in Function, Program, or Library p4\" was issued." );
	}

	/*
	 * program p5
	 * 0 validation messages are expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * type XDRTYHN
	 * 1 validation message is expected.
	 * It is expected to contain "XDRTYHN cannot be resolved".
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "XDRTYHN cannot be resolved" );
		if( messageWithSubstring == null ) fail( "No message with substring \"XDRTYHN cannot be resolved\" was issued." );
	}

	/*
	 * use int;
	 * 1 validation message is expected.
	 * It is expected to contain "The value of use declaration int in program p5 is invalid. You must use an enumeration or a library part.".
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value of use declaration int in program p5 is invalid. You must use an enumeration or a library part." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value of use declaration int in program p5 is invalid. You must use an enumeration or a library part.\" was issued." );
	}

	/*
	 * use enum1;
	 * 0 validation messages are expected.
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 0, messages.size() );
	}
}
