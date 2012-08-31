/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.tests.validation.junit.part;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/part/function.egl
 */
public class FunctionTest extends ValidationTestCase {

	public FunctionTest() {
		super( "EGLSource/part/function.egl", false );
	}

	/*
	 * Library validateParamsAndDeclarationsLibrary
	 * 0 validation messages are expected.
	 */
	public void testLine12() {
		List messages = getMessagesAtLine( 12 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p1 notEmptyRec,
	 * 0 validation messages are expected.
	 */
	public void testLine14() {
		List messages = getMessagesAtLine( 14 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p2 intItem,
	 * 0 validation messages are expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p3 emptyFlexRec,
	 * 1 validation message is expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * p4 emptyFixedRec,
	 * 1 validation message is expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * p5 cantBeResolved
	 * 1 validation message is expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function main() end
	 * 0 validation messages are expected.
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p1 int,
	 * 0 validation messages are expected.
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p1 int,
	 * 1 validation message is expected.
	 * It is expected to contain "The same name p1 also appears as variable, parameter, use or constant declaration in Function, Program, or Library validateParamsAndDeclarations.".
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The same name p1 also appears as variable, parameter, use or constant declaration in Function, Program, or Library validateParamsAndDeclarations." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The same name p1 also appears as variable, parameter, use or constant declaration in Function, Program, or Library validateParamsAndDeclarations.\" was issued." );
	}

	/*
	 * recParm1 emptyFlexRec,
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid parameter recParm1. There must be at least one structure item in the contents of the record emptyFlexRec.".
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid parameter recParm1. There must be at least one structure item in the contents of the record emptyFlexRec." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid parameter recParm1. There must be at least one structure item in the contents of the record emptyFlexRec.\" was issued." );
	}

	/*
	 * p1 validateParamsAndDeclarationsLibrary,
	 * 1 validation message is expected.
	 * It is expected to contain "The type validateParamsAndDeclarationsLibrary is not a valid type for a data declaration.".
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type validateParamsAndDeclarationsLibrary is not a valid type for a data declaration." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type validateParamsAndDeclarationsLibrary is not a valid type for a data declaration.\" was issued." );
	}

	/*
	 * p2 validatePgm,
	 * 1 validation message is expected.
	 * It is expected to contain "The type validatePgm is not a valid type for a data declaration.".
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type validatePgm is not a valid type for a data declaration." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type validatePgm is not a valid type for a data declaration.\" was issued." );
	}

	/*
	 * p3 validateParms,
	 * 1 validation message is expected.
	 * It is expected to contain "The type validateParms cannot be resolved.".
	 */
	public void testLine51() {
		List messages = getMessagesAtLine( 51 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type validateParms cannot be resolved." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type validateParms cannot be resolved.\" was issued." );
	}

	/*
	 * p4a validateParms[],
	 * 1 validation message is expected.
	 * It is expected to contain "The type validateParms cannot be resolved.".
	 */
	public void testLine52() {
		List messages = getMessagesAtLine( 52 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type validateParms cannot be resolved." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type validateParms cannot be resolved.\" was issued." );
	}

	/*
	 * p5 annot1,
	 * 1 validation message is expected.
	 * It is expected to contain "The type annot1 is not a valid type for a data declaration.".
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type annot1 is not a valid type for a data declaration." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type annot1 is not a valid type for a data declaration.\" was issued." );
	}

	/*
	 * p15 sqlRecord[],
	 * 0 validation messages are expected.
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p16 myService in,
	 * 1 validation message is expected.
	 * It is expected to contain "The type myService is not a valid type for a data declaration.".
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type myService is not a valid type for a data declaration." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type myService is not a valid type for a data declaration.\" was issued." );
	}

	/*
	 * p17 myService out,
	 * 1 validation message is expected.
	 * It is expected to contain "The type myService is not a valid type for a data declaration.".
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type myService is not a valid type for a data declaration." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type myService is not a valid type for a data declaration.\" was issued." );
	}

	/*
	 * p18 myService inout,
	 * 1 validation message is expected.
	 * It is expected to contain "The type myService is not a valid type for a data declaration.".
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type myService is not a valid type for a data declaration." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type myService is not a valid type for a data declaration.\" was issued." );
	}

	/*
	 * p22 int in,
	 * 0 validation messages are expected.
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p23 int out,
	 * 0 validation messages are expected.
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p24 int inout,
	 * 0 validation messages are expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p25 myService[],
	 * 1 validation message is expected.
	 * It is expected to contain "The type myService is not a valid type for a data declaration.".
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type myService is not a valid type for a data declaration." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type myService is not a valid type for a data declaration.\" was issued." );
	}

	/*
	 * p26 myInterface[],
	 * 0 validation messages are expected.
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p28 undeclaredVar,
	 * 1 validation message is expected.
	 * It is expected to contain "The type undeclaredVar cannot be resolved".
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type undeclaredVar cannot be resolved" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type undeclaredVar cannot be resolved\" was issued." );
	}

	/*
	 * p29 undeclaredVar[],
	 * 1 validation message is expected.
	 * It is expected to contain "The type undeclaredVar cannot be resolved".
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type undeclaredVar cannot be resolved" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type undeclaredVar cannot be resolved\" was issued." );
	}

	/*
	 * p31 boolean,
	 * 0 validation messages are expected.
	 */
	public void testLine70() {
		List messages = getMessagesAtLine( 70 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function func(p1 boolean);
	 * 0 validation messages are expected.
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function func2() returns(boolean);
	 * 0 validation messages are expected.
	 */
	public void testLine80() {
		List messages = getMessagesAtLine( 80 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function func(p1 boolean in);
	 * 0 validation messages are expected.
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function func2() returns(boolean);
	 * 0 validation messages are expected.
	 */
	public void testLine85() {
		List messages = getMessagesAtLine( 85 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function func(p1 boolean in);
	 * 0 validation messages are expected.
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function func2() returns(boolean);
	 * 0 validation messages are expected.
	 */
	public void testLine90() {
		List messages = getMessagesAtLine( 90 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function main() returns (int) end
	 * 0 validation messages are expected.
	 */
	public void testLine104() {
		List messages = getMessagesAtLine( 104 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function func() returns (boolean) end
	 * 0 validation messages are expected.
	 */
	public void testLine105() {
		List messages = getMessagesAtLine( 105 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function func2() returns (int[]) end
	 * 0 validation messages are expected.
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function func3() returns (intItem) end
	 * 0 validation messages are expected.
	 */
	public void testLine107() {
		List messages = getMessagesAtLine( 107 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function func4() returns (dictionary) end
	 * 0 validation messages are expected.
	 */
	public void testLine108() {
		List messages = getMessagesAtLine( 108 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function func7() returns (myService) end
	 * 1 validation message is expected.
	 * It is expected to contain "The type myService is not a valid type for a data declaration.".
	 */
	public void testLine109() {
		List messages = getMessagesAtLine( 109 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type myService is not a valid type for a data declaration." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type myService is not a valid type for a data declaration.\" was issued." );
	}

	/*
	 * function func8() returns (myInterface) end
	 * 0 validation messages are expected.
	 */
	public void testLine110() {
		List messages = getMessagesAtLine( 110 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function func9() returns (notEmptyRec) end
	 * 0 validation messages are expected.
	 */
	public void testLine111() {
		List messages = getMessagesAtLine( 111 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function func10() returns (validateReturnProgram) end
	 * 1 validation message is expected.
	 * It is expected to contain "The type validateReturnProgram is not a valid type for a data declaration.".
	 */
	public void testLine113() {
		List messages = getMessagesAtLine( 113 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type validateReturnProgram is not a valid type for a data declaration." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type validateReturnProgram is not a valid type for a data declaration.\" was issued." );
	}

	/*
	 * function func11() returns (annot1) end
	 * 1 validation message is expected.
	 * It is expected to contain "The type annot1 is not a valid type for a data declaration.".
	 */
	public void testLine114() {
		List messages = getMessagesAtLine( 114 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type annot1 is not a valid type for a data declaration." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type annot1 is not a valid type for a data declaration.\" was issued." );
	}

	/*
	 * function func12() returns (undeclaredItem) end
	 * 1 validation message is expected.
	 * It is expected to contain "The type undeclaredItem cannot be resolved".
	 */
	public void testLine116() {
		List messages = getMessagesAtLine( 116 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type undeclaredItem cannot be resolved" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type undeclaredItem cannot be resolved\" was issued." );
	}

	/*
	 * super();
	 * 0 validation messages are expected.
	 */
	public void testLine124() {
		List messages = getMessagesAtLine( 124 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * super();
	 * 1 validation message is expected.
	 */
	public void testLine125() {
		List messages = getMessagesAtLine( 125 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * super("xyz");
	 * 2 validation messages are expected.
	 */
	public void testLine126() {
		List messages = getMessagesAtLine( 126 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * this();
	 * 0 validation messages are expected.
	 */
	public void testLine129() {
		List messages = getMessagesAtLine( 129 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * this();
	 * 1 validation message is expected.
	 */
	public void testLine130() {
		List messages = getMessagesAtLine( 130 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * this(s, i, 456, 789, 123, "aaaa");
	 * 1 validation message is expected.
	 */
	public void testLine133() {
		List messages = getMessagesAtLine( 133 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * this(i, s);
	 * 0 validation messages are expected.
	 */
	public void testLine136() {
		List messages = getMessagesAtLine( 136 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * super(i, s, b, d);
	 * 1 validation message is expected.
	 */
	public void testLine139() {
		List messages = getMessagesAtLine( 139 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * super();
	 * 1 validation message is expected.
	 */
	public void testLine142() {
		List messages = getMessagesAtLine( 142 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * this();
	 * 1 validation message is expected.
	 */
	public void testLine143() {
		List messages = getMessagesAtLine( 143 );
		assertEquals( 1, messages.size() );
	}
}
