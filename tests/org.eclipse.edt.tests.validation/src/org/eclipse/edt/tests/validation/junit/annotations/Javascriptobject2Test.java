package org.eclipse.edt.tests.validation.junit.annotations;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/annotations/javascriptobject2.egl
 */
public class Javascriptobject2Test extends ValidationTestCase {

	public Javascriptobject2Test() {
		super( "EGLSource/annotations/javascriptobject2.egl", false );
	}

	/*
	 * jzs etPrivDefConst?[][] = new etPrivDefConst?[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConst etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConst etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConst etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConst etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstA etImpDefConst = new etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstA etExpDefConst = new etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstA etPrivDefConst = new etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstA etNoDefConst = new etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstB etImpDefConst = new etImpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstB etExpDefConst = new etExpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstB etPrivDefConst = new etPrivDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine35() {
		List messages = getMessagesAtLine( 35 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstB etNoDefConst = new etNoDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstC etImpDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstC etExpDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstC etPrivDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstC etNoDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstD etImpDefConst? = new etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstD etExpDefConst? = new etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstD etPrivDefConst? = new etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine45() {
		List messages = getMessagesAtLine( 45 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstD etNoDefConst? = new etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstE etImpDefConst? = new etImpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstE etExpDefConst? = new etExpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstE etPrivDefConst? = new etPrivDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstE etNoDefConst? = new etNoDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine51() {
		List messages = getMessagesAtLine( 51 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstF etImpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstF etExpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstF etPrivDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstF etNoDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstG etImpDefConst[] = new etImpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstG etExpDefConst[] = new etExpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstG etPrivDefConst[] = new etPrivDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstG etNoDefConst[] = new etNoDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstH etImpDefConst[] = new etImpDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstH etExpDefConst[] = new etExpDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstH etPrivDefConst[] = new etPrivDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstH etNoDefConst[] = new etNoDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstI etImpDefConst[] = new etImpDefConst[5];
	 * 0 validation messages are expected.
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstI etExpDefConst[] = new etExpDefConst[5];
	 * 0 validation messages are expected.
	 */
	public void testLine69() {
		List messages = getMessagesAtLine( 69 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstI etPrivDefConst[] = new etPrivDefConst[5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine70() {
		List messages = getMessagesAtLine( 70 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstI etNoDefConst[] = new etNoDefConst[5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstK etImpDefConst?[] = new etImpDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine73() {
		List messages = getMessagesAtLine( 73 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstK etExpDefConst?[] = new etExpDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine74() {
		List messages = getMessagesAtLine( 74 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstK etPrivDefConst?[] = new etPrivDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine75() {
		List messages = getMessagesAtLine( 75 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstK etNoDefConst?[] = new etNoDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine76() {
		List messages = getMessagesAtLine( 76 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstL etImpDefConst[][] = new etImpDefConst[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstL etExpDefConst[][] = new etExpDefConst[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstL etPrivDefConst[][] = new etPrivDefConst[1][5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine80() {
		List messages = getMessagesAtLine( 80 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstL etNoDefConst[][] = new etNoDefConst[1][5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine81() {
		List messages = getMessagesAtLine( 81 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstM etImpDefConst[][] = new etImpDefConst[1][0];
	 * 0 validation messages are expected.
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstM etExpDefConst[][] = new etExpDefConst[1][];
	 * 0 validation messages are expected.
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstM etPrivDefConst[][] = new etPrivDefConst[1][0];
	 * 0 validation messages are expected.
	 */
	public void testLine85() {
		List messages = getMessagesAtLine( 85 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstM etNoDefConst[][] = new etNoDefConst[1][];
	 * 0 validation messages are expected.
	 */
	public void testLine86() {
		List messages = getMessagesAtLine( 86 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstN etImpDefConst?[][] = new etImpDefConst?[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine88() {
		List messages = getMessagesAtLine( 88 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstN etExpDefConst?[][] = new etExpDefConst?[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstN etPrivDefConst?[][] = new etPrivDefConst?[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine90() {
		List messages = getMessagesAtLine( 90 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstN etNoDefConst?[][] = new etNoDefConst?[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine91() {
		List messages = getMessagesAtLine( 91 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConst etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine96() {
		List messages = getMessagesAtLine( 96 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConst etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine97() {
		List messages = getMessagesAtLine( 97 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConst etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine98() {
		List messages = getMessagesAtLine( 98 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConst etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine99() {
		List messages = getMessagesAtLine( 99 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstA etImpDefConst = new etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine101() {
		List messages = getMessagesAtLine( 101 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstA etExpDefConst = new etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstA etPrivDefConst = new etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine103() {
		List messages = getMessagesAtLine( 103 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstA etNoDefConst = new etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine104() {
		List messages = getMessagesAtLine( 104 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstB etImpDefConst = new etImpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstB etExpDefConst = new etExpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine107() {
		List messages = getMessagesAtLine( 107 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstB etPrivDefConst = new etPrivDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine108() {
		List messages = getMessagesAtLine( 108 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstB etNoDefConst = new etNoDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine109() {
		List messages = getMessagesAtLine( 109 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstC etImpDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine111() {
		List messages = getMessagesAtLine( 111 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstC etExpDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstC etPrivDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine113() {
		List messages = getMessagesAtLine( 113 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstC etNoDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine114() {
		List messages = getMessagesAtLine( 114 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstD etImpDefConst? = new etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine116() {
		List messages = getMessagesAtLine( 116 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstD etExpDefConst? = new etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine117() {
		List messages = getMessagesAtLine( 117 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstD etPrivDefConst? = new etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine118() {
		List messages = getMessagesAtLine( 118 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstD etNoDefConst? = new etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine119() {
		List messages = getMessagesAtLine( 119 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstE etImpDefConst? = new etImpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine121() {
		List messages = getMessagesAtLine( 121 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstE etExpDefConst? = new etExpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine122() {
		List messages = getMessagesAtLine( 122 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstE etPrivDefConst? = new etPrivDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine123() {
		List messages = getMessagesAtLine( 123 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstE etNoDefConst? = new etNoDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine124() {
		List messages = getMessagesAtLine( 124 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstF etImpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine126() {
		List messages = getMessagesAtLine( 126 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstF etExpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine127() {
		List messages = getMessagesAtLine( 127 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstF etPrivDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine128() {
		List messages = getMessagesAtLine( 128 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstF etNoDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine129() {
		List messages = getMessagesAtLine( 129 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstG etImpDefConst[] = new etImpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine131() {
		List messages = getMessagesAtLine( 131 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstG etExpDefConst[] = new etExpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine132() {
		List messages = getMessagesAtLine( 132 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstG etPrivDefConst[] = new etPrivDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine133() {
		List messages = getMessagesAtLine( 133 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstG etNoDefConst[] = new etNoDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine134() {
		List messages = getMessagesAtLine( 134 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstH etImpDefConst[] = new etImpDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine136() {
		List messages = getMessagesAtLine( 136 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstH etExpDefConst[] = new etExpDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine137() {
		List messages = getMessagesAtLine( 137 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstH etPrivDefConst[] = new etPrivDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine138() {
		List messages = getMessagesAtLine( 138 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstH etNoDefConst[] = new etNoDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine139() {
		List messages = getMessagesAtLine( 139 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstI etImpDefConst[] = new etImpDefConst[5];
	 * 0 validation messages are expected.
	 */
	public void testLine141() {
		List messages = getMessagesAtLine( 141 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstI etExpDefConst[] = new etExpDefConst[5];
	 * 0 validation messages are expected.
	 */
	public void testLine142() {
		List messages = getMessagesAtLine( 142 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstI etPrivDefConst[] = new etPrivDefConst[5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine143() {
		List messages = getMessagesAtLine( 143 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstI etNoDefConst[] = new etNoDefConst[5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine144() {
		List messages = getMessagesAtLine( 144 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstK etImpDefConst?[] = new etImpDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine146() {
		List messages = getMessagesAtLine( 146 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstK etExpDefConst?[] = new etExpDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine147() {
		List messages = getMessagesAtLine( 147 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstK etPrivDefConst?[] = new etPrivDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine148() {
		List messages = getMessagesAtLine( 148 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstK etNoDefConst?[] = new etNoDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine149() {
		List messages = getMessagesAtLine( 149 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstL etImpDefConst[][] = new etImpDefConst[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine151() {
		List messages = getMessagesAtLine( 151 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstL etExpDefConst[][] = new etExpDefConst[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine152() {
		List messages = getMessagesAtLine( 152 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstL etPrivDefConst[][] = new etPrivDefConst[1][5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine153() {
		List messages = getMessagesAtLine( 153 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstL etNoDefConst[][] = new etNoDefConst[1][5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine154() {
		List messages = getMessagesAtLine( 154 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstM etImpDefConst[][] = new etImpDefConst[1][0];
	 * 0 validation messages are expected.
	 */
	public void testLine156() {
		List messages = getMessagesAtLine( 156 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstM etExpDefConst[][] = new etExpDefConst[1][];
	 * 0 validation messages are expected.
	 */
	public void testLine157() {
		List messages = getMessagesAtLine( 157 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstM etPrivDefConst[][] = new etPrivDefConst[1][0];
	 * 0 validation messages are expected.
	 */
	public void testLine158() {
		List messages = getMessagesAtLine( 158 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstM etNoDefConst[][] = new etNoDefConst[1][];
	 * 0 validation messages are expected.
	 */
	public void testLine159() {
		List messages = getMessagesAtLine( 159 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstN etImpDefConst?[][] = new etImpDefConst?[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine161() {
		List messages = getMessagesAtLine( 161 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstN etExpDefConst?[][] = new etExpDefConst?[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine162() {
		List messages = getMessagesAtLine( 162 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstN etPrivDefConst?[][] = new etPrivDefConst?[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine163() {
		List messages = getMessagesAtLine( 163 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstN etNoDefConst?[][] = new etNoDefConst?[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine164() {
		List messages = getMessagesAtLine( 164 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConst etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine167() {
		List messages = getMessagesAtLine( 167 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConst etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine168() {
		List messages = getMessagesAtLine( 168 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConst etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine169() {
		List messages = getMessagesAtLine( 169 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConst etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine170() {
		List messages = getMessagesAtLine( 170 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstA etImpDefConst = new etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine172() {
		List messages = getMessagesAtLine( 172 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstA etExpDefConst = new etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine173() {
		List messages = getMessagesAtLine( 173 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstA etPrivDefConst = new etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine174() {
		List messages = getMessagesAtLine( 174 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstA etNoDefConst = new etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine175() {
		List messages = getMessagesAtLine( 175 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstB etImpDefConst = new etImpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine177() {
		List messages = getMessagesAtLine( 177 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstB etExpDefConst = new etExpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine178() {
		List messages = getMessagesAtLine( 178 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstB etPrivDefConst = new etPrivDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine179() {
		List messages = getMessagesAtLine( 179 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstB etNoDefConst = new etNoDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine180() {
		List messages = getMessagesAtLine( 180 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstC etImpDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine182() {
		List messages = getMessagesAtLine( 182 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstC etExpDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine183() {
		List messages = getMessagesAtLine( 183 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstC etPrivDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine184() {
		List messages = getMessagesAtLine( 184 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstC etNoDefConst?;
	 * 0 validation messages are expected.
	 */
	public void testLine185() {
		List messages = getMessagesAtLine( 185 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstD etImpDefConst? = new etImpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine187() {
		List messages = getMessagesAtLine( 187 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstD etExpDefConst? = new etExpDefConst;
	 * 0 validation messages are expected.
	 */
	public void testLine188() {
		List messages = getMessagesAtLine( 188 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstD etPrivDefConst? = new etPrivDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine189() {
		List messages = getMessagesAtLine( 189 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstD etNoDefConst? = new etNoDefConst;
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine190() {
		List messages = getMessagesAtLine( 190 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstE etImpDefConst? = new etImpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine192() {
		List messages = getMessagesAtLine( 192 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstE etExpDefConst? = new etExpDefConst();
	 * 0 validation messages are expected.
	 */
	public void testLine193() {
		List messages = getMessagesAtLine( 193 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstE etPrivDefConst? = new etPrivDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine194() {
		List messages = getMessagesAtLine( 194 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstE etNoDefConst? = new etNoDefConst();
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine195() {
		List messages = getMessagesAtLine( 195 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstF etImpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine197() {
		List messages = getMessagesAtLine( 197 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstF etExpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine198() {
		List messages = getMessagesAtLine( 198 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstF etPrivDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine199() {
		List messages = getMessagesAtLine( 199 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstF etNoDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine200() {
		List messages = getMessagesAtLine( 200 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstG etImpDefConst[] = new etImpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine202() {
		List messages = getMessagesAtLine( 202 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstG etExpDefConst[] = new etExpDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine203() {
		List messages = getMessagesAtLine( 203 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstG etPrivDefConst[] = new etPrivDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine204() {
		List messages = getMessagesAtLine( 204 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstG etNoDefConst[] = new etNoDefConst[];
	 * 0 validation messages are expected.
	 */
	public void testLine205() {
		List messages = getMessagesAtLine( 205 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstH etImpDefConst[] = new etImpDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine207() {
		List messages = getMessagesAtLine( 207 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstH etExpDefConst[] = new etExpDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine208() {
		List messages = getMessagesAtLine( 208 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstH etPrivDefConst[] = new etPrivDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine209() {
		List messages = getMessagesAtLine( 209 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstH etNoDefConst[] = new etNoDefConst[0];
	 * 0 validation messages are expected.
	 */
	public void testLine210() {
		List messages = getMessagesAtLine( 210 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstI etImpDefConst[] = new etImpDefConst[5];
	 * 0 validation messages are expected.
	 */
	public void testLine212() {
		List messages = getMessagesAtLine( 212 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstI etExpDefConst[] = new etExpDefConst[5];
	 * 0 validation messages are expected.
	 */
	public void testLine213() {
		List messages = getMessagesAtLine( 213 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstI etPrivDefConst[] = new etPrivDefConst[5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine214() {
		List messages = getMessagesAtLine( 214 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstI etNoDefConst[] = new etNoDefConst[5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine215() {
		List messages = getMessagesAtLine( 215 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstK etImpDefConst?[] = new etImpDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine217() {
		List messages = getMessagesAtLine( 217 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstK etExpDefConst?[] = new etExpDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine218() {
		List messages = getMessagesAtLine( 218 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstK etPrivDefConst?[] = new etPrivDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine219() {
		List messages = getMessagesAtLine( 219 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstK etNoDefConst?[] = new etNoDefConst?[5];
	 * 0 validation messages are expected.
	 */
	public void testLine220() {
		List messages = getMessagesAtLine( 220 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstL etImpDefConst[][] = new etImpDefConst[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine222() {
		List messages = getMessagesAtLine( 222 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstL etExpDefConst[][] = new etExpDefConst[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine223() {
		List messages = getMessagesAtLine( 223 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstL etPrivDefConst[][] = new etPrivDefConst[1][5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine224() {
		List messages = getMessagesAtLine( 224 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etNoDefConstL etNoDefConst[][] = new etNoDefConst[1][5];
	 * 1 validation message is expected.
	 * It is expected to contain "not instantiable".
	 */
	public void testLine225() {
		List messages = getMessagesAtLine( 225 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not instantiable" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not instantiable\" was issued." );
	}

	/*
	 * etImpDefConstM etImpDefConst[][] = new etImpDefConst[1][0];
	 * 0 validation messages are expected.
	 */
	public void testLine227() {
		List messages = getMessagesAtLine( 227 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstM etExpDefConst[][] = new etExpDefConst[1][];
	 * 0 validation messages are expected.
	 */
	public void testLine228() {
		List messages = getMessagesAtLine( 228 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstM etPrivDefConst[][] = new etPrivDefConst[1][0];
	 * 0 validation messages are expected.
	 */
	public void testLine229() {
		List messages = getMessagesAtLine( 229 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstM etNoDefConst[][] = new etNoDefConst[1][];
	 * 0 validation messages are expected.
	 */
	public void testLine230() {
		List messages = getMessagesAtLine( 230 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etImpDefConstN etImpDefConst?[][] = new etImpDefConst?[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine232() {
		List messages = getMessagesAtLine( 232 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etExpDefConstN etExpDefConst?[][] = new etExpDefConst?[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine233() {
		List messages = getMessagesAtLine( 233 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etPrivDefConstN etPrivDefConst?[][] = new etPrivDefConst?[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine234() {
		List messages = getMessagesAtLine( 234 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * etNoDefConstN etNoDefConst?[][] = new etNoDefConst?[1][5];
	 * 0 validation messages are expected.
	 */
	public void testLine235() {
		List messages = getMessagesAtLine( 235 );
		assertEquals( 0, messages.size() );
	}
}
