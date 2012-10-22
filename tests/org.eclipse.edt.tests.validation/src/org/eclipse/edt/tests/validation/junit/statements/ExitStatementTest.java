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
	 * if(yes) exit program (1 + 1); end
	 * 0 validation messages are expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program (b + 1); end
	 * 0 validation messages are expected.
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program (b * b); end
	 * 0 validation messages are expected.
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program (intConst); end
	 * 0 validation messages are expected.
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program( b ); end
	 * 0 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program( returnCode ); end
	 * 0 validation messages are expected.
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit case; end
	 * 0 validation messages are expected.
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit for; end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid exit modifier. The for exit modifier may only be used within a for statement block".
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid exit modifier. The for exit modifier may only be used within a for statement block" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid exit modifier. The for exit modifier may only be used within a for statement block\" was issued." );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine51() {
		List messages = getMessagesAtLine( 51 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit if; end
	 * 0 validation messages are expected.
	 */
	public void testLine52() {
		List messages = getMessagesAtLine( 52 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit case; end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid exit modifier. The case exit modifier may only be used within a case statement block".
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid exit modifier. The case exit modifier may only be used within a case statement block" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid exit modifier. The case exit modifier may only be used within a case statement block\" was issued." );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit ; end
	 * 0 validation messages are expected.
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit while; end
	 * 0 validation messages are expected.
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit if; end
	 * 0 validation messages are expected.
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit for; end
	 * 0 validation messages are expected.
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit while; end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid exit modifier. The while exit modifier may only be used within a while statement block".
	 */
	public void testLine69() {
		List messages = getMessagesAtLine( 69 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid exit modifier. The while exit modifier may only be used within a while statement block" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid exit modifier. The while exit modifier may only be used within a while statement block\" was issued." );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine75() {
		List messages = getMessagesAtLine( 75 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine76() {
		List messages = getMessagesAtLine( 76 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit forEach; end
	 * 0 validation messages are expected.
	 */
	public void testLine77() {
		List messages = getMessagesAtLine( 77 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit for; end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid exit modifier. The for exit modifier may only be used within a for statement block".
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid exit modifier. The for exit modifier may only be used within a for statement block" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid exit modifier. The for exit modifier may only be used within a for statement block\" was issued." );
	}

	/*
	 * if(yes) exit program ("abc" + "def"); end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid return code (\"abc\"+\"def\"). The return code for an exit program or rununit statement must be an integer expression".
	 */
	public void testLine81() {
		List messages = getMessagesAtLine( 81 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid return code (\"abc\"+\"def\"). The return code for an exit program or rununit statement must be an integer expression" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid return code (\"abc\"+\"def\"). The return code for an exit program or rununit statement must be an integer expression\" was issued." );
	}

	/*
	 * if(yes) exit program (charConst); end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid return code (charConst). The return code for an exit program or rununit statement must be an integer expression".
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid return code (charConst). The return code for an exit program or rununit statement must be an integer expression" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid return code (charConst). The return code for an exit program or rununit statement must be an integer expression\" was issued." );
	}

	/*
	 * if(yes) exit program ("A"); end
	 * 1 validation message is expected.
	 */
	public void testLine85() {
		List messages = getMessagesAtLine( 85 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * if(yes) exit program (a); end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid return code (a). The return code for an exit program or rununit statement must be an integer expression".
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid return code (a). The return code for an exit program or rununit statement must be an integer expression" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid return code (a). The return code for an exit program or rununit statement must be an integer expression\" was issued." );
	}

	/*
	 * if(yes) exit program (a+a); end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid return code (a+a). The return code for an exit program or rununit statement must be an integer expression".
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid return code (a+a). The return code for an exit program or rununit statement must be an integer expression" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid return code (a+a). The return code for an exit program or rununit statement must be an integer expression\" was issued." );
	}

	/*
	 * if(yes) exit program (myany); end
	 * 0 validation messages are expected.
	 */
	public void testLine91() {
		List messages = getMessagesAtLine( 91 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine93() {
		List messages = getMessagesAtLine( 93 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit while; end
	 * 0 validation messages are expected.
	 */
	public void testLine101() {
		List messages = getMessagesAtLine( 101 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * continue while;
	 * 0 validation messages are expected.
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit while; end
	 * 0 validation messages are expected.
	 */
	public void testLine104() {
		List messages = getMessagesAtLine( 104 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit case ; end
	 * 1 validation message is expected.
	 * It is expected to contain "The case exit modifier may only be used within a case statement block.".
	 */
	public void testLine105() {
		List messages = getMessagesAtLine( 105 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The case exit modifier may only be used within a case statement block." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The case exit modifier may only be used within a case statement block.\" was issued." );
	}

	/*
	 * if(yes) exit if; end
	 * 0 validation messages are expected.
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit for; end
	 * 1 validation message is expected.
	 * It is expected to contain "The for exit modifier may only be used within a for statement block.".
	 */
	public void testLine108() {
		List messages = getMessagesAtLine( 108 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The for exit modifier may only be used within a for statement block." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The for exit modifier may only be used within a for statement block.\" was issued." );
	}

	/*
	 * if(yes) exit forEach; end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid exit modifier. The forEach exit modifier may only be used within a forEach statement block.".
	 */
	public void testLine110() {
		List messages = getMessagesAtLine( 110 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid exit modifier. The forEach exit modifier may only be used within a forEach statement block." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid exit modifier. The forEach exit modifier may only be used within a forEach statement block.\" was issued." );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 1 validation message is expected.
	 */
	public void testLine119() {
		List messages = getMessagesAtLine( 119 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 1 validation message is expected.
	 */
	public void testLine125() {
		List messages = getMessagesAtLine( 125 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 1 validation message is expected.
	 */
	public void testLine131() {
		List messages = getMessagesAtLine( 131 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 1 validation message is expected.
	 */
	public void testLine137() {
		List messages = getMessagesAtLine( 137 );
		assertEquals( 1, messages.size() );
	}
}
