package org.eclipse.edt.tests.validation.junit.annotations;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/annotations/javaobject2.egl
 */
public class Javaobject2Test extends ValidationTestCase {

	public Javaobject2Test() {
		super( "EGLSource/annotations/javaobject2.egl", false );
	}

	/*
	 * etImpDefConst etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConst etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine22() {
		List messages = getMessagesAtLine( 22 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConst etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConst etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstA etImpDefConst = new etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstA etExpDefConst = new etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstA etPrivDefConst = new etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstA etNoDefConst = new etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstB etImpDefConst = new etImpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstB etExpDefConst = new etExpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstB etPrivDefConst = new etPrivDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "is private".
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "is private" );
		if( messageWithSubstring == null ) fail( "No message with substring \"is private\" was issued." );
	}

	/*
	 * etNoDefConstB etNoDefConst = new etNoDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstC etImpDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstC etExpDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstC etPrivDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstC etNoDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstD etImpDefConst? = new etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstD etExpDefConst? = new etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstD etPrivDefConst? = new etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstD etNoDefConst? = new etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstE etImpDefConst? = new etImpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstE etExpDefConst? = new etExpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstE etPrivDefConst? = new etPrivDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "is private".
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "is private" );
		if( messageWithSubstring == null ) fail( "No message with substring \"is private\" was issued." );
	}

	/*
	 * etNoDefConstE etNoDefConst? = new etNoDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstF etImpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine51() {
		List messages = getMessagesAtLine( 51 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstF etExpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine52() {
		List messages = getMessagesAtLine( 52 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstF etPrivDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstF etNoDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstG etImpDefConst[] = new etImpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstG etExpDefConst[] = new etExpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstG etPrivDefConst[] = new etPrivDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstG etNoDefConst[] = new etNoDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstH etImpDefConst[] = new etImpDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstH etExpDefConst[] = new etExpDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstH etPrivDefConst[] = new etPrivDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstH etNoDefConst[] = new etNoDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstI etImpDefConst[] = new etImpDefConst[5];
	 * 0 validation messages are expected.
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstI etExpDefConst[] = new etExpDefConst[5];
	 * 0 validation messages are expected.
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstI etPrivDefConst[] = new etPrivDefConst[5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstI etNoDefConst[] = new etNoDefConst[5];
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine69() {
		List messages = getMessagesAtLine( 69 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstJ etImpDefConst[] = new etImpDefConst[5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstJ etExpDefConst[] = new etExpDefConst[5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine72() {
		List messages = getMessagesAtLine( 72 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstJ etPrivDefConst[] = new etPrivDefConst[5] ();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine73() {
		List messages = getMessagesAtLine( 73 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstJ etNoDefConst[] = new etNoDefConst[5] ();
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine74() {
		List messages = getMessagesAtLine( 74 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstK etImpDefConst?[] = new etImpDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine76() {
		List messages = getMessagesAtLine( 76 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstK etExpDefConst?[] = new etExpDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine77() {
		List messages = getMessagesAtLine( 77 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstK etPrivDefConst?[] = new etPrivDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstK etNoDefConst?[] = new etNoDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstL etImpDefConst[][] = new etImpDefConst[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine81() {
		List messages = getMessagesAtLine( 81 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstL etExpDefConst[][] = new etExpDefConst[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine82() {
		List messages = getMessagesAtLine( 82 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstL etPrivDefConst[][] = new etPrivDefConst[1][5] ();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstL etNoDefConst[][] = new etNoDefConst[1][5] ();
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstM etImpDefConst[][] = new etImpDefConst[1][0] ();
	 * 0 validation messages are expected.
	 */
	public void testLine86() {
		List messages = getMessagesAtLine( 86 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstM etExpDefConst[][] = new etExpDefConst[1][] ();
	 * 0 validation messages are expected.
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstM etPrivDefConst[][] = new etPrivDefConst[1][0] ();
	 * 0 validation messages are expected.
	 */
	public void testLine88() {
		List messages = getMessagesAtLine( 88 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstM etNoDefConst[][] = new etNoDefConst[1][] ();
	 * 0 validation messages are expected.
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstN etImpDefConst?[][] = new etImpDefConst?[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine91() {
		List messages = getMessagesAtLine( 91 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstN etExpDefConst?[][] = new etExpDefConst?[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine92() {
		List messages = getMessagesAtLine( 92 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstN etPrivDefConst?[][] = new etPrivDefConst?[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine93() {
		List messages = getMessagesAtLine( 93 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstN etNoDefConst?[][] = new etNoDefConst?[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine94() {
		List messages = getMessagesAtLine( 94 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConst etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine99() {
		List messages = getMessagesAtLine( 99 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConst etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine100() {
		List messages = getMessagesAtLine( 100 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConst etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine101() {
		List messages = getMessagesAtLine( 101 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConst etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstA etImpDefConst = new etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine104() {
		List messages = getMessagesAtLine( 104 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstA etExpDefConst = new etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine105() {
		List messages = getMessagesAtLine( 105 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstA etPrivDefConst = new etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstA etNoDefConst = new etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine107() {
		List messages = getMessagesAtLine( 107 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstB etImpDefConst = new etImpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine109() {
		List messages = getMessagesAtLine( 109 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstB etExpDefConst = new etExpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine110() {
		List messages = getMessagesAtLine( 110 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstB etPrivDefConst = new etPrivDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "is private".
	 */
	public void testLine111() {
		List messages = getMessagesAtLine( 111 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "is private" );
		if( messageWithSubstring == null ) fail( "No message with substring \"is private\" was issued." );
	}

	/*
	 * etNoDefConstB etNoDefConst = new etNoDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstC etImpDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine114() {
		List messages = getMessagesAtLine( 114 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstC etExpDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine115() {
		List messages = getMessagesAtLine( 115 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstC etPrivDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine116() {
		List messages = getMessagesAtLine( 116 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstC etNoDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine117() {
		List messages = getMessagesAtLine( 117 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstD etImpDefConst? = new etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine119() {
		List messages = getMessagesAtLine( 119 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstD etExpDefConst? = new etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine120() {
		List messages = getMessagesAtLine( 120 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstD etPrivDefConst? = new etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine121() {
		List messages = getMessagesAtLine( 121 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstD etNoDefConst? = new etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine122() {
		List messages = getMessagesAtLine( 122 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstE etImpDefConst? = new etImpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine124() {
		List messages = getMessagesAtLine( 124 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstE etExpDefConst? = new etExpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine125() {
		List messages = getMessagesAtLine( 125 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstE etPrivDefConst? = new etPrivDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "is private".
	 */
	public void testLine126() {
		List messages = getMessagesAtLine( 126 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "is private" );
		if( messageWithSubstring == null ) fail( "No message with substring \"is private\" was issued." );
	}

	/*
	 * etNoDefConstE etNoDefConst? = new etNoDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine127() {
		List messages = getMessagesAtLine( 127 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstF etImpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine129() {
		List messages = getMessagesAtLine( 129 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstF etExpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine130() {
		List messages = getMessagesAtLine( 130 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstF etPrivDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine131() {
		List messages = getMessagesAtLine( 131 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstF etNoDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine132() {
		List messages = getMessagesAtLine( 132 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstG etImpDefConst[] = new etImpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine134() {
		List messages = getMessagesAtLine( 134 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstG etExpDefConst[] = new etExpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine135() {
		List messages = getMessagesAtLine( 135 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstG etPrivDefConst[] = new etPrivDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine136() {
		List messages = getMessagesAtLine( 136 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstG etNoDefConst[] = new etNoDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine137() {
		List messages = getMessagesAtLine( 137 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstH etImpDefConst[] = new etImpDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine139() {
		List messages = getMessagesAtLine( 139 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstH etExpDefConst[] = new etExpDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine140() {
		List messages = getMessagesAtLine( 140 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstH etPrivDefConst[] = new etPrivDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine141() {
		List messages = getMessagesAtLine( 141 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstH etNoDefConst[] = new etNoDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine142() {
		List messages = getMessagesAtLine( 142 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstI etImpDefConst[] = new etImpDefConst[5];
	 * 0 validation messages are expected.
	 */
	public void testLine144() {
		List messages = getMessagesAtLine( 144 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstI etExpDefConst[] = new etExpDefConst[5];
	 * 0 validation messages are expected.
	 */
	public void testLine145() {
		List messages = getMessagesAtLine( 145 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstI etPrivDefConst[] = new etPrivDefConst[5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine146() {
		List messages = getMessagesAtLine( 146 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstI etNoDefConst[] = new etNoDefConst[5];
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine147() {
		List messages = getMessagesAtLine( 147 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstJ etImpDefConst[] = new etImpDefConst[5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine149() {
		List messages = getMessagesAtLine( 149 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstJ etExpDefConst[] = new etExpDefConst[5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine150() {
		List messages = getMessagesAtLine( 150 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstJ etPrivDefConst[] = new etPrivDefConst[5] ();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine151() {
		List messages = getMessagesAtLine( 151 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstJ etNoDefConst[] = new etNoDefConst[5] ();
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine152() {
		List messages = getMessagesAtLine( 152 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstK etImpDefConst?[] = new etImpDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine154() {
		List messages = getMessagesAtLine( 154 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstK etExpDefConst?[] = new etExpDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine155() {
		List messages = getMessagesAtLine( 155 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstK etPrivDefConst?[] = new etPrivDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine156() {
		List messages = getMessagesAtLine( 156 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstK etNoDefConst?[] = new etNoDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine157() {
		List messages = getMessagesAtLine( 157 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstL etImpDefConst[][] = new etImpDefConst[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine159() {
		List messages = getMessagesAtLine( 159 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstL etExpDefConst[][] = new etExpDefConst[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine160() {
		List messages = getMessagesAtLine( 160 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstL etPrivDefConst[][] = new etPrivDefConst[1][5] ();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine161() {
		List messages = getMessagesAtLine( 161 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstL etNoDefConst[][] = new etNoDefConst[1][5] ();
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine162() {
		List messages = getMessagesAtLine( 162 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstM etImpDefConst[][] = new etImpDefConst[1][0] ();
	 * 0 validation messages are expected.
	 */
	public void testLine164() {
		List messages = getMessagesAtLine( 164 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstM etExpDefConst[][] = new etExpDefConst[1][] ();
	 * 0 validation messages are expected.
	 */
	public void testLine165() {
		List messages = getMessagesAtLine( 165 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstM etPrivDefConst[][] = new etPrivDefConst[1][0] ();
	 * 0 validation messages are expected.
	 */
	public void testLine166() {
		List messages = getMessagesAtLine( 166 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstM etNoDefConst[][] = new etNoDefConst[1][] ();
	 * 0 validation messages are expected.
	 */
	public void testLine167() {
		List messages = getMessagesAtLine( 167 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstN etImpDefConst?[][] = new etImpDefConst?[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine169() {
		List messages = getMessagesAtLine( 169 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstN etExpDefConst?[][] = new etExpDefConst?[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine170() {
		List messages = getMessagesAtLine( 170 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstN etPrivDefConst?[][] = new etPrivDefConst?[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine171() {
		List messages = getMessagesAtLine( 171 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstN etNoDefConst?[][] = new etNoDefConst?[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine172() {
		List messages = getMessagesAtLine( 172 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConst etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine175() {
		List messages = getMessagesAtLine( 175 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConst etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine176() {
		List messages = getMessagesAtLine( 176 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConst etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine177() {
		List messages = getMessagesAtLine( 177 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConst etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine178() {
		List messages = getMessagesAtLine( 178 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstA etImpDefConst = new etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine180() {
		List messages = getMessagesAtLine( 180 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstA etExpDefConst = new etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine181() {
		List messages = getMessagesAtLine( 181 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstA etPrivDefConst = new etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine182() {
		List messages = getMessagesAtLine( 182 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstA etNoDefConst = new etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine183() {
		List messages = getMessagesAtLine( 183 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstB etImpDefConst = new etImpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine185() {
		List messages = getMessagesAtLine( 185 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstB etExpDefConst = new etExpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine186() {
		List messages = getMessagesAtLine( 186 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstB etPrivDefConst = new etPrivDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "is private".
	 */
	public void testLine187() {
		List messages = getMessagesAtLine( 187 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "is private" );
		if( messageWithSubstring == null ) fail( "No message with substring \"is private\" was issued." );
	}

	/*
	 * etNoDefConstB etNoDefConst = new etNoDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine188() {
		List messages = getMessagesAtLine( 188 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstC etImpDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine190() {
		List messages = getMessagesAtLine( 190 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstC etExpDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine191() {
		List messages = getMessagesAtLine( 191 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstC etPrivDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine192() {
		List messages = getMessagesAtLine( 192 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstC etNoDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine193() {
		List messages = getMessagesAtLine( 193 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstD etImpDefConst? = new etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine195() {
		List messages = getMessagesAtLine( 195 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstD etExpDefConst? = new etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine196() {
		List messages = getMessagesAtLine( 196 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstD etPrivDefConst? = new etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine197() {
		List messages = getMessagesAtLine( 197 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstD etNoDefConst? = new etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine198() {
		List messages = getMessagesAtLine( 198 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstE etImpDefConst? = new etImpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine200() {
		List messages = getMessagesAtLine( 200 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstE etExpDefConst? = new etExpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine201() {
		List messages = getMessagesAtLine( 201 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstE etPrivDefConst? = new etPrivDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "is private".
	 */
	public void testLine202() {
		List messages = getMessagesAtLine( 202 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "is private" );
		if( messageWithSubstring == null ) fail( "No message with substring \"is private\" was issued." );
	}

	/*
	 * etNoDefConstE etNoDefConst? = new etNoDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine203() {
		List messages = getMessagesAtLine( 203 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstF etImpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine205() {
		List messages = getMessagesAtLine( 205 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstF etExpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine206() {
		List messages = getMessagesAtLine( 206 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstF etPrivDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine207() {
		List messages = getMessagesAtLine( 207 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstF etNoDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine208() {
		List messages = getMessagesAtLine( 208 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstG etImpDefConst[] = new etImpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine210() {
		List messages = getMessagesAtLine( 210 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstG etExpDefConst[] = new etExpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine211() {
		List messages = getMessagesAtLine( 211 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstG etPrivDefConst[] = new etPrivDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine212() {
		List messages = getMessagesAtLine( 212 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstG etNoDefConst[] = new etNoDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine213() {
		List messages = getMessagesAtLine( 213 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstH etImpDefConst[] = new etImpDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine215() {
		List messages = getMessagesAtLine( 215 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstH etExpDefConst[] = new etExpDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine216() {
		List messages = getMessagesAtLine( 216 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstH etPrivDefConst[] = new etPrivDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine217() {
		List messages = getMessagesAtLine( 217 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstH etNoDefConst[] = new etNoDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine218() {
		List messages = getMessagesAtLine( 218 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstI etImpDefConst[] = new etImpDefConst[5];
	 * 0 validation messages are expected.
	 */
	public void testLine220() {
		List messages = getMessagesAtLine( 220 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstI etExpDefConst[] = new etExpDefConst[5];
	 * 0 validation messages are expected.
	 */
	public void testLine221() {
		List messages = getMessagesAtLine( 221 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstI etPrivDefConst[] = new etPrivDefConst[5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine222() {
		List messages = getMessagesAtLine( 222 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstI etNoDefConst[] = new etNoDefConst[5];
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine223() {
		List messages = getMessagesAtLine( 223 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstJ etImpDefConst[] = new etImpDefConst[5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine225() {
		List messages = getMessagesAtLine( 225 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstJ etExpDefConst[] = new etExpDefConst[5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine226() {
		List messages = getMessagesAtLine( 226 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstJ etPrivDefConst[] = new etPrivDefConst[5] ();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine227() {
		List messages = getMessagesAtLine( 227 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstJ etNoDefConst[] = new etNoDefConst[5] ();
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine228() {
		List messages = getMessagesAtLine( 228 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstK etImpDefConst?[] = new etImpDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine230() {
		List messages = getMessagesAtLine( 230 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstK etExpDefConst?[] = new etExpDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine231() {
		List messages = getMessagesAtLine( 231 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstK etPrivDefConst?[] = new etPrivDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine232() {
		List messages = getMessagesAtLine( 232 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstK etNoDefConst?[] = new etNoDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine233() {
		List messages = getMessagesAtLine( 233 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstL etImpDefConst[][] = new etImpDefConst[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine235() {
		List messages = getMessagesAtLine( 235 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstL etExpDefConst[][] = new etExpDefConst[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine236() {
		List messages = getMessagesAtLine( 236 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstL etPrivDefConst[][] = new etPrivDefConst[1][5] ();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine237() {
		List messages = getMessagesAtLine( 237 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstL etNoDefConst[][] = new etNoDefConst[1][5] ();
	 * 1 validation message is expected.
	 * It is expected to contain "does not have a default constructor".
	 */
	public void testLine238() {
		List messages = getMessagesAtLine( 238 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "does not have a default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"does not have a default constructor\" was issued." );
	}

	/*
	 * etImpDefConstM etImpDefConst[][] = new etImpDefConst[1][0] ();
	 * 0 validation messages are expected.
	 */
	public void testLine240() {
		List messages = getMessagesAtLine( 240 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstM etExpDefConst[][] = new etExpDefConst[1][] ();
	 * 0 validation messages are expected.
	 */
	public void testLine241() {
		List messages = getMessagesAtLine( 241 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstM etPrivDefConst[][] = new etPrivDefConst[1][0] ();
	 * 0 validation messages are expected.
	 */
	public void testLine242() {
		List messages = getMessagesAtLine( 242 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstM etNoDefConst[][] = new etNoDefConst[1][] ();
	 * 0 validation messages are expected.
	 */
	public void testLine243() {
		List messages = getMessagesAtLine( 243 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstN etImpDefConst?[][] = new etImpDefConst?[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine245() {
		List messages = getMessagesAtLine( 245 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstN etExpDefConst?[][] = new etExpDefConst?[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine246() {
		List messages = getMessagesAtLine( 246 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstN etPrivDefConst?[][] = new etPrivDefConst?[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine247() {
		List messages = getMessagesAtLine( 247 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstN etNoDefConst?[][] = new etNoDefConst?[1][5] ();
	 * 0 validation messages are expected.
	 */
	public void testLine248() {
		List messages = getMessagesAtLine( 248 );
		assertEquals( 0, messages.size() );
	}
}
