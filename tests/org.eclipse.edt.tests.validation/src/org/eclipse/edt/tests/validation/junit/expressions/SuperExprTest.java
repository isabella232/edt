package org.eclipse.edt.tests.validation.junit.expressions;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/expressions/superExpr.egl
 */
public class SuperExprTest extends ValidationTestCase {

	public SuperExprTest() {
		super( "EGLSource/expressions/superExpr.egl", false );
	}

	/*
	 * super{x=5},
	 * 1 validation message is expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * super{document = new document}
	 * 0 validation messages are expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ha h2{super{i = 3}};
	 * 1 validation message is expected.
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * hb h2{super{document = new document}};
	 * 0 validation messages are expected.
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * hc superExpr{super{x = 3}};
	 * 1 validation message is expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * hd superExpr{super{document = new document}};
	 * 0 validation messages are expected.
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * super();
	 * 0 validation messages are expected.
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * super(10);
	 * 2 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * this();
	 * 1 validation message is expected.
	 */
	public void testLine35() {
		List messages = getMessagesAtLine( 35 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * this(10);
	 * 1 validation message is expected.
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * super.getProperty("");
	 * 0 validation messages are expected.
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * this.getProperty("");
	 * 0 validation messages are expected.
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = super.document;
	 * 0 validation messages are expected.
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = this.document;
	 * 0 validation messages are expected.
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * super(10);
	 * 1 validation message is expected.
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * super();
	 * 1 validation message is expected.
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * hy h2{super{i = 3}};
	 * 1 validation message is expected.
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * hz h2{super{document = new document}};
	 * 0 validation messages are expected.
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * super();
	 * 1 validation message is expected.
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * super(x);
	 * 2 validation messages are expected.
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * super.getProperty("");
	 * 0 validation messages are expected.
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * m myrec{a = super.getProperty("")};
	 * 0 validation messages are expected.
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * super{x = 10};
	 * 1 validation message is expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * super();
	 * 1 validation message is expected.
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * super(10);
	 * 2 validation messages are expected.
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * super.go();
	 * 1 validation message is expected.
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * constructor()
	 * 1 validation message is expected.
	 * It is expected to contain "The implicit super constructor A() either does not exist or is not visible. You must explicitly invoke another constructor.".
	 */
	public void testLine85() {
		List messages = getMessagesAtLine( 85 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The implicit super constructor A() either does not exist or is not visible. You must explicitly invoke another constructor." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The implicit super constructor A() either does not exist or is not visible. You must explicitly invoke another constructor.\" was issued." );
	}

	/*
	 * constructor( b boolean )
	 * 1 validation message is expected.
	 * It is expected to contain "The implicit super constructor A() either does not exist or is not visible. You must explicitly invoke another constructor.".
	 */
	public void testLine88() {
		List messages = getMessagesAtLine( 88 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The implicit super constructor A() either does not exist or is not visible. You must explicitly invoke another constructor." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The implicit super constructor A() either does not exist or is not visible. You must explicitly invoke another constructor.\" was issued." );
	}

	/*
	 * super();
	 * 1 validation message is expected.
	 */
	public void testLine95() {
		List messages = getMessagesAtLine( 95 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * super( 3 );
	 * 1 validation message is expected.
	 * It is expected to contain "A constructor for A which matches the supplied arguments could not be found, or is not visible.".
	 */
	public void testLine99() {
		List messages = getMessagesAtLine( 99 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A constructor for A which matches the supplied arguments could not be found, or is not visible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"A constructor for A which matches the supplied arguments could not be found, or is not visible.\" was issued." );
	}

	/*
	 * constructor(s string)
	 * 0 validation messages are expected.
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * super(s);
	 * 0 validation messages are expected.
	 */
	public void testLine103() {
		List messages = getMessagesAtLine( 103 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * super();
	 * 0 validation messages are expected.
	 */
	public void testLine110() {
		List messages = getMessagesAtLine( 110 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * super(s);
	 * 1 validation message is expected.
	 * It is expected to contain "A constructor for D which matches the supplied arguments could not be found, or is not visible.".
	 */
	public void testLine113() {
		List messages = getMessagesAtLine( 113 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A constructor for D which matches the supplied arguments could not be found, or is not visible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"A constructor for D which matches the supplied arguments could not be found, or is not visible.\" was issued." );
	}

	/*
	 * this();
	 * 1 validation message is expected.
	 * It is expected to contain "A constructor for F which matches the supplied arguments could not be found, or is not visible.".
	 */
	public void testLine119() {
		List messages = getMessagesAtLine( 119 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A constructor for F which matches the supplied arguments could not be found, or is not visible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"A constructor for F which matches the supplied arguments could not be found, or is not visible.\" was issued." );
	}

	/*
	 * class H extends G
	 * 1 validation message is expected.
	 * It is expected to contain "The implicit super constructor G() either does not exist or is not visible. You must explicitly define a constructor.".
	 */
	public void testLine126() {
		List messages = getMessagesAtLine( 126 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The implicit super constructor G() either does not exist or is not visible. You must explicitly define a constructor." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The implicit super constructor G() either does not exist or is not visible. You must explicitly define a constructor.\" was issued." );
	}

	/*
	 * class I extends A
	 * 1 validation message is expected.
	 * It is expected to contain "The implicit super constructor A() either does not exist or is not visible. You must explicitly define a constructor.".
	 */
	public void testLine128() {
		List messages = getMessagesAtLine( 128 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The implicit super constructor A() either does not exist or is not visible. You must explicitly define a constructor." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The implicit super constructor A() either does not exist or is not visible. You must explicitly define a constructor.\" was issued." );
	}
}
