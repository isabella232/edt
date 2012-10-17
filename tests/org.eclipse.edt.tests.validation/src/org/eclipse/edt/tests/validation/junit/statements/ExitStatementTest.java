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
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program (b + 1); end
	 * 0 validation messages are expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program (b * b); end
	 * 0 validation messages are expected.
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program (intConst); end
	 * 0 validation messages are expected.
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program( b ); end
	 * 0 validation messages are expected.
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program( returnCode ); end
	 * 0 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit case; end
	 * 0 validation messages are expected.
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit for; end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid exit modifier. The for exit modifier may only be used within a for statement block".
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid exit modifier. The for exit modifier may only be used within a for statement block" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid exit modifier. The for exit modifier may only be used within a for statement block\" was issued." );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine45() {
		List messages = getMessagesAtLine( 45 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit if; end
	 * 0 validation messages are expected.
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit case; end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid exit modifier. The case exit modifier may only be used within a case statement block".
	 */
	public void testLine51() {
		List messages = getMessagesAtLine( 51 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid exit modifier. The case exit modifier may only be used within a case statement block" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid exit modifier. The case exit modifier may only be used within a case statement block\" was issued." );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine52() {
		List messages = getMessagesAtLine( 52 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit ; end
	 * 0 validation messages are expected.
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit while; end
	 * 0 validation messages are expected.
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit if; end
	 * 0 validation messages are expected.
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit for; end
	 * 0 validation messages are expected.
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit while; end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid exit modifier. The while exit modifier may only be used within a while statement block".
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid exit modifier. The while exit modifier may only be used within a while statement block" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid exit modifier. The while exit modifier may only be used within a while statement block\" was issued." );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine73() {
		List messages = getMessagesAtLine( 73 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine74() {
		List messages = getMessagesAtLine( 74 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit forEach; end
	 * 0 validation messages are expected.
	 */
	public void testLine75() {
		List messages = getMessagesAtLine( 75 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit for; end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid exit modifier. The for exit modifier may only be used within a for statement block".
	 */
	public void testLine76() {
		List messages = getMessagesAtLine( 76 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid exit modifier. The for exit modifier may only be used within a for statement block" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid exit modifier. The for exit modifier may only be used within a for statement block\" was issued." );
	}

	/*
	 * if(yes) exit program ("abc" + "def"); end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid return code (\"abc\"+\"def\"). The return code for an exit program or rununit statement must be an integer expression".
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid return code (\"abc\"+\"def\"). The return code for an exit program or rununit statement must be an integer expression" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid return code (\"abc\"+\"def\"). The return code for an exit program or rununit statement must be an integer expression\" was issued." );
	}

	/*
	 * if(yes) exit program (charConst); end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid return code (charConst). The return code for an exit program or rununit statement must be an integer expression".
	 */
	public void testLine81() {
		List messages = getMessagesAtLine( 81 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid return code (charConst). The return code for an exit program or rununit statement must be an integer expression" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid return code (charConst). The return code for an exit program or rununit statement must be an integer expression\" was issued." );
	}

	/*
	 * if(yes) exit program ("A"); end
	 * 1 validation message is expected.
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * if(yes) exit program (a); end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid return code (a). The return code for an exit program or rununit statement must be an integer expression".
	 */
	public void testLine85() {
		List messages = getMessagesAtLine( 85 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid return code (a). The return code for an exit program or rununit statement must be an integer expression" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid return code (a). The return code for an exit program or rununit statement must be an integer expression\" was issued." );
	}

	/*
	 * if(yes) exit program (a+a); end
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid return code (a+a). The return code for an exit program or rununit statement must be an integer expression".
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid return code (a+a). The return code for an exit program or rununit statement must be an integer expression" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid return code (a+a). The return code for an exit program or rununit statement must be an integer expression\" was issued." );
	}

	/*
	 * if(yes) exit program (myany); end
	 * 0 validation messages are expected.
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit; end
	 * 0 validation messages are expected.
	 */
	public void testLine91() {
		List messages = getMessagesAtLine( 91 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit while; end
	 * 0 validation messages are expected.
	 */
	public void testLine99() {
		List messages = getMessagesAtLine( 99 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * continue while;
	 * 0 validation messages are expected.
	 */
	public void testLine100() {
		List messages = getMessagesAtLine( 100 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit while; end
	 * 0 validation messages are expected.
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit if; end
	 * 0 validation messages are expected.
	 */
	public void testLine104() {
		List messages = getMessagesAtLine( 104 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit for; end
	 * 1 validation message is expected.
	 * It is expected to contain "The for exit modifier may only be used within a for statement block.".
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The for exit modifier may only be used within a for statement block." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The for exit modifier may only be used within a for statement block.\" was issued." );
	}

	/*
	 * if(yes) exit program; end
	 * 0 validation messages are expected.
	 */
	public void testLine110() {
		List messages = getMessagesAtLine( 110 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 1 validation message is expected.
	 */
	public void testLine117() {
		List messages = getMessagesAtLine( 117 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 1 validation message is expected.
	 */
	public void testLine123() {
		List messages = getMessagesAtLine( 123 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 1 validation message is expected.
	 */
	public void testLine129() {
		List messages = getMessagesAtLine( 129 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * if(yes) exit program; end
	 * 1 validation message is expected.
	 */
	public void testLine135() {
		List messages = getMessagesAtLine( 135 );
		assertEquals( 1, messages.size() );
	}
}
