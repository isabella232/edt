package org.eclipse.edt.tests.validation.junit.statements;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/statements/exitStatement.egl
 */
public class ExitStatementTest extends ValidationTestCase {

	public ExitStatementTest() {
		super( "EGLSource/statements/exitStatement.egl", false );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine13() {
		List messages = getMessagesAtLine( 13 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program (1); end
	 * 0 validation messages are expected.
	 */
	public void testLine14() {
		List messages = getMessagesAtLine( 14 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program (1 + 1); end
	 * 0 validation messages are expected.
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program (b + 1); end
	 * 0 validation messages are expected.
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program (b * b); end
	 * 0 validation messages are expected.
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program (intConst); end
	 * 0 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program( b ); end
	 * 0 validation messages are expected.
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program( returnCode ); end
	 * 0 validation messages are expected.
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit case; end
	 * 0 validation messages are expected.
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit for; end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid exit modifier. The for exit modifier may only be used within a for statement block".
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid exit modifier. The for exit modifier may only be used within a for statement block" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid exit modifier. The for exit modifier may only be used within a for statement block\" was issued." );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit if; end
	 * 0 validation messages are expected.
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit case; end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid exit modifier. The case exit modifier may only be used within a case statement block".
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid exit modifier. The case exit modifier may only be used within a case statement block" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid exit modifier. The case exit modifier may only be used within a case statement block\" was issued." );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit ; end
	 * 0 validation messages are expected.
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit while; end
	 * 0 validation messages are expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit if; end
	 * 0 validation messages are expected.
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit for; end
	 * 0 validation messages are expected.
	 */
	public void testLine69() {
		List messages = getMessagesAtLine( 69 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine70() {
		List messages = getMessagesAtLine( 70 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit while; end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid exit modifier. The while exit modifier may only be used within a while statement block".
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid exit modifier. The while exit modifier may only be used within a while statement block" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid exit modifier. The while exit modifier may only be used within a while statement block\" was issued." );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine77() {
		List messages = getMessagesAtLine( 77 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit forEach; end
	 * 0 validation messages are expected.
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit for; end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid exit modifier. The for exit modifier may only be used within a for statement block".
	 */
	public void testLine80() {
		List messages = getMessagesAtLine( 80 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid exit modifier. The for exit modifier may only be used within a for statement block" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid exit modifier. The for exit modifier may only be used within a for statement block\" was issued." );
	}

	/*
	 * if(yes) exit program ("abc" + "def"); end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid return code \"abc\"+\"def\". The return code for an exit program or rununit statement must be an integer expression".
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid return code \"abc\"+\"def\". The return code for an exit program or rununit statement must be an integer expression" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid return code \"abc\"+\"def\". The return code for an exit program or rununit statement must be an integer expression\" was issued." );
	}

	/*
	 * if(yes) exit program (charConst); end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid return code charConst. The return code for an exit program or rununit statement must be an integer expression".
	 */
	public void testLine85() {
		List messages = getMessagesAtLine( 85 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid return code charConst. The return code for an exit program or rununit statement must be an integer expression" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid return code charConst. The return code for an exit program or rununit statement must be an integer expression\" was issued." );
	}

	/*
	 * if(yes) exit program ("A"); end
	 * 1 validation message is expected.
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * if(yes) exit program (a); end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid return code a. The return code for an exit program or rununit statement must be an integer expression".
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid return code a. The return code for an exit program or rununit statement must be an integer expression" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid return code a. The return code for an exit program or rununit statement must be an integer expression\" was issued." );
	}

	/*
	 * if(yes) exit program (a+a); end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid return code a+a. The return code for an exit program or rununit statement must be an integer expression".
	 */
	public void testLine91() {
		List messages = getMessagesAtLine( 91 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid return code a+a. The return code for an exit program or rununit statement must be an integer expression" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid return code a+a. The return code for an exit program or rununit statement must be an integer expression\" was issued." );
	}

	/*
	 * if(yes) exit program (myany); end
	 * 0 validation messages are expected.
	 */
	public void testLine93() {
		List messages = getMessagesAtLine( 93 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine95() {
		List messages = getMessagesAtLine( 95 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit while; end
	 * 0 validation messages are expected.
	 */
	public void testLine103() {
		List messages = getMessagesAtLine( 103 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * continue while;
	 * 0 validation messages are expected.
	 */
	public void testLine104() {
		List messages = getMessagesAtLine( 104 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit while; end
	 * 0 validation messages are expected.
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit if; end
	 * 0 validation messages are expected.
	 */
	public void testLine108() {
		List messages = getMessagesAtLine( 108 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit for; end
	 * 1 validation message is expected.
	 * It is expected to contain "The for exit modifier may only be used within a for statement block.".
	 */
	public void testLine110() {
		List messages = getMessagesAtLine( 110 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The for exit modifier may only be used within a for statement block." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The for exit modifier may only be used within a for statement block.\" was issued." );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine114() {
		List messages = getMessagesAtLine( 114 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 1 validation message is expected.
	 */
	public void testLine121() {
		List messages = getMessagesAtLine( 121 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 1 validation message is expected.
	 */
	public void testLine127() {
		List messages = getMessagesAtLine( 127 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 1 validation message is expected.
	 */
	public void testLine133() {
		List messages = getMessagesAtLine( 133 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 1 validation message is expected.
	 */
	public void testLine139() {
		List messages = getMessagesAtLine( 139 );
		assertEquals( 1, messages.size() );
	}
}
