package org.eclipse.edt.tests.validation.junit.expressions;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/expressions/ternary1.egl
 */
public class Ternary1Test extends ValidationTestCase {

	public Ternary1Test() {
		super( "EGLSource/expressions/ternary1.egl", false );
	}

	/*
	 * s = true ? i : ba;
	 * 1 validation message is expected.
	 * It is expected to contain "string and boolean[] are not compatible types in the expression s = ba".
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "string and boolean[] are not compatible types in the expression s = ba" );
		if( messageWithSubstring == null ) fail( "No message with substring \"string and boolean[] are not compatible types in the expression s = ba\" was issued." );
	}

	/*
	 * s = true ? ba : i;
	 * 1 validation message is expected.
	 * It is expected to contain "string and boolean[] are not compatible types in the expression s = ba".
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "string and boolean[] are not compatible types in the expression s = ba" );
		if( messageWithSubstring == null ) fail( "No message with substring \"string and boolean[] are not compatible types in the expression s = ba\" was issued." );
	}

	/*
	 * s = true ? i : i;
	 * 0 validation messages are expected.
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ? (true ? i : ba)
	 * 1 validation message is expected.
	 * It is expected to contain "string and boolean[] are not compatible types in the expression s = ba".
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "string and boolean[] are not compatible types in the expression s = ba" );
		if( messageWithSubstring == null ) fail( "No message with substring \"string and boolean[] are not compatible types in the expression s = ba\" was issued." );
	}

	/*
	 * : (true ? ba : i);
	 * 1 validation message is expected.
	 * It is expected to contain "string and boolean[] are not compatible types in the expression s = ba".
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "string and boolean[] are not compatible types in the expression s = ba" );
		if( messageWithSubstring == null ) fail( "No message with substring \"string and boolean[] are not compatible types in the expression s = ba\" was issued." );
	}

	/*
	 * a = true ? myrec : ba;
	 * 0 validation messages are expected.
	 */
	public void testLine35() {
		List messages = getMessagesAtLine( 35 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = true ? doNotExist : ba;
	 * 1 validation message is expected.
	 * It is expected to contain "doNotExist cannot be resolved.".
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "doNotExist cannot be resolved." );
		if( messageWithSubstring == null ) fail( "No message with substring \"doNotExist cannot be resolved.\" was issued." );
	}

	/*
	 * ? getValue()
	 * 1 validation message is expected.
	 * It is expected to contain "rec and int are not compatible types in the expression myrec = getValue()".
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "rec and int are not compatible types in the expression myrec = getValue()" );
		if( messageWithSubstring == null ) fail( "No message with substring \"rec and int are not compatible types in the expression myrec = getValue()\" was issued." );
	}

	/*
	 * : getBoolean();
	 * 1 validation message is expected.
	 * It is expected to contain "rec and boolean are not compatible types in the expression myrec = getBoolean()".
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "rec and boolean are not compatible types in the expression myrec = getBoolean()" );
		if( messageWithSubstring == null ) fail( "No message with substring \"rec and boolean are not compatible types in the expression myrec = getBoolean()\" was issued." );
	}

	/*
	 * ba = true ? ba.appendElement(true) : ba :: true;
	 * 0 validation messages are expected.
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d = true ? d : f1;
	 * 0 validation messages are expected.
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ba ::= true ? ba[2] : ba :: true;
	 * 0 validation messages are expected.
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ? i
	 * 1 validation message is expected.
	 * It is expected to contain "boolean[] and int are not compatible types in the expression ba ::= i".
	 */
	public void testLine45() {
		List messages = getMessagesAtLine( 45 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "boolean[] and int are not compatible types in the expression ba ::= i" );
		if( messageWithSubstring == null ) fail( "No message with substring \"boolean[] and int are not compatible types in the expression ba ::= i\" was issued." );
	}

	/*
	 * : s;
	 * 1 validation message is expected.
	 * It is expected to contain "boolean[] and string are not compatible types in the expression ba ::= s".
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "boolean[] and string are not compatible types in the expression ba ::= s" );
		if( messageWithSubstring == null ) fail( "No message with substring \"boolean[] and string are not compatible types in the expression ba ::= s\" was issued." );
	}

	/*
	 * ? doNotExist
	 * 1 validation message is expected.
	 * It is expected to contain "doNotExist cannot be resolved.".
	 */
	public void testLine51() {
		List messages = getMessagesAtLine( 51 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "doNotExist cannot be resolved." );
		if( messageWithSubstring == null ) fail( "No message with substring \"doNotExist cannot be resolved.\" was issued." );
	}

	/*
	 * : alsoNotExist);
	 * 1 validation message is expected.
	 * It is expected to contain "alsoNotExist cannot be resolved.".
	 */
	public void testLine52() {
		List messages = getMessagesAtLine( 52 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "alsoNotExist cannot be resolved." );
		if( messageWithSubstring == null ) fail( "No message with substring \"alsoNotExist cannot be resolved.\" was issued." );
	}

	/*
	 * setValueIn(true ? s[1:2] : s[1:2]);
	 * 0 validation messages are expected.
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ? s[1:2]
	 * 1 validation message is expected.
	 * It is expected to contain "Substring access expressions are not valid as the target of a move or assignment, or as arguments to INOUT or OUT function parameters.".
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Substring access expressions are not valid as the target of a move or assignment, or as arguments to INOUT or OUT function parameters." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Substring access expressions are not valid as the target of a move or assignment, or as arguments to INOUT or OUT function parameters.\" was issued." );
	}

	/*
	 * : s[1:2]);
	 * 1 validation message is expected.
	 * It is expected to contain "Substring access expressions are not valid as the target of a move or assignment, or as arguments to INOUT or OUT function parameters.".
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Substring access expressions are not valid as the target of a move or assignment, or as arguments to INOUT or OUT function parameters." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Substring access expressions are not valid as the target of a move or assignment, or as arguments to INOUT or OUT function parameters.\" was issued." );
	}

	/*
	 * ? s[1:2]
	 * 1 validation message is expected.
	 * It is expected to contain "Substring access expressions are not valid as the target of a move or assignment, or as arguments to INOUT or OUT function parameters.".
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Substring access expressions are not valid as the target of a move or assignment, or as arguments to INOUT or OUT function parameters." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Substring access expressions are not valid as the target of a move or assignment, or as arguments to INOUT or OUT function parameters.\" was issued." );
	}

	/*
	 * : s[1:2]);
	 * 1 validation message is expected.
	 * It is expected to contain "Substring access expressions are not valid as the target of a move or assignment, or as arguments to INOUT or OUT function parameters.".
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Substring access expressions are not valid as the target of a move or assignment, or as arguments to INOUT or OUT function parameters." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Substring access expressions are not valid as the target of a move or assignment, or as arguments to INOUT or OUT function parameters.\" was issued." );
	}

	/*
	 * ? null
	 * 1 validation message is expected.
	 * It is expected to contain "Error in argument null in function invocation setValueInout. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant.".
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Error in argument null in function invocation setValueInout. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Error in argument null in function invocation setValueInout. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant.\" was issued." );
	}

	/*
	 * : null);
	 * 1 validation message is expected.
	 * It is expected to contain "Error in argument null in function invocation setValueInout. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant.".
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Error in argument null in function invocation setValueInout. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Error in argument null in function invocation setValueInout. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant.\" was issued." );
	}

	/*
	 * setValueInNullable(true ? null : null);
	 * 0 validation messages are expected.
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ? myrec{}
	 * 1 validation message is expected.
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * : myrec{});
	 * 1 validation message is expected.
	 */
	public void testLine72() {
		List messages = getMessagesAtLine( 72 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * setValueIn(true ? getValue() : i * 7);
	 * 0 validation messages are expected.
	 */
	public void testLine75() {
		List messages = getMessagesAtLine( 75 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ? getValue()
	 * 1 validation message is expected.
	 * It is expected to contain "Error in argument getValue() in function invocation setValueOut. This type of argument requires that the parameter be defined with the IN modifier.".
	 */
	public void testLine77() {
		List messages = getMessagesAtLine( 77 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Error in argument getValue() in function invocation setValueOut. This type of argument requires that the parameter be defined with the IN modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Error in argument getValue() in function invocation setValueOut. This type of argument requires that the parameter be defined with the IN modifier.\" was issued." );
	}

	/*
	 * : i * 7);
	 * 1 validation message is expected.
	 * It is expected to contain "Error in argument i*7 in function invocation setValueOut. This type of argument requires that the parameter be defined with the IN modifier.".
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Error in argument i*7 in function invocation setValueOut. This type of argument requires that the parameter be defined with the IN modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Error in argument i*7 in function invocation setValueOut. This type of argument requires that the parameter be defined with the IN modifier.\" was issued." );
	}

	/*
	 * ? getValue()
	 * 1 validation message is expected.
	 * It is expected to contain "Error in argument getValue() in function invocation setValueInout. This type of argument requires that the parameter be defined with the IN modifier.".
	 */
	public void testLine80() {
		List messages = getMessagesAtLine( 80 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Error in argument getValue() in function invocation setValueInout. This type of argument requires that the parameter be defined with the IN modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Error in argument getValue() in function invocation setValueInout. This type of argument requires that the parameter be defined with the IN modifier.\" was issued." );
	}

	/*
	 * : i * 7);
	 * 1 validation message is expected.
	 * It is expected to contain "Error in argument i*7 in function invocation setValueInout. This type of argument requires that the parameter be defined with the IN modifier.".
	 */
	public void testLine81() {
		List messages = getMessagesAtLine( 81 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Error in argument i*7 in function invocation setValueInout. This type of argument requires that the parameter be defined with the IN modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Error in argument i*7 in function invocation setValueInout. This type of argument requires that the parameter be defined with the IN modifier.\" was issued." );
	}

	/*
	 * setValueIn(true ? 10 : constRec.i);
	 * 0 validation messages are expected.
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ? 10
	 * 1 validation message is expected.
	 * It is expected to contain "Error in argument 10 in function invocation setValueOut. Constants, literals and literal expressions are not valid for use with parameters defined with the OUT modifier.".
	 */
	public void testLine86() {
		List messages = getMessagesAtLine( 86 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Error in argument 10 in function invocation setValueOut. Constants, literals and literal expressions are not valid for use with parameters defined with the OUT modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Error in argument 10 in function invocation setValueOut. Constants, literals and literal expressions are not valid for use with parameters defined with the OUT modifier.\" was issued." );
	}

	/*
	 * : constRec.i);
	 * 1 validation message is expected.
	 * It is expected to contain "Error in argument constRec.i in function invocation setValueOut. Constants, literals and literal expressions are not valid for use with parameters defined with the OUT modifier.".
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Error in argument constRec.i in function invocation setValueOut. Constants, literals and literal expressions are not valid for use with parameters defined with the OUT modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Error in argument constRec.i in function invocation setValueOut. Constants, literals and literal expressions are not valid for use with parameters defined with the OUT modifier.\" was issued." );
	}

	/*
	 * ? 10
	 * 1 validation message is expected.
	 * It is expected to contain "Error in argument 10 in function invocation setValueInout. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant.".
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Error in argument 10 in function invocation setValueInout. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Error in argument 10 in function invocation setValueInout. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant.\" was issued." );
	}

	/*
	 * : constRec.i);
	 * 1 validation message is expected.
	 * It is expected to contain "Error in argument constRec.i in function invocation setValueInout. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant.".
	 */
	public void testLine90() {
		List messages = getMessagesAtLine( 90 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Error in argument constRec.i in function invocation setValueInout. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Error in argument constRec.i in function invocation setValueInout. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant.\" was issued." );
	}

	/*
	 * ? 10
	 * 1 validation message is expected.
	 * It is expected to contain "Error in argument 10 in function invocation setValueInoutConst. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant.".
	 */
	public void testLine92() {
		List messages = getMessagesAtLine( 92 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Error in argument 10 in function invocation setValueInoutConst. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Error in argument 10 in function invocation setValueInoutConst. For parameters with the INOUT modifier, literals and literal expressions are not valid and argument constants are only valid if the parameter is also constant.\" was issued." );
	}

	/*
	 * : constRec.i);
	 * 0 validation messages are expected.
	 */
	public void testLine93() {
		List messages = getMessagesAtLine( 93 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * setTernaryIn(true ? this : this);
	 * 0 validation messages are expected.
	 */
	public void testLine96() {
		List messages = getMessagesAtLine( 96 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ? super
	 * 1 validation message is expected.
	 * It is expected to contain "The expression \"super\" is not valid as a function argument.".
	 */
	public void testLine98() {
		List messages = getMessagesAtLine( 98 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The expression \"super\" is not valid as a function argument." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The expression \"super\" is not valid as a function argument.\" was issued." );
	}

	/*
	 * : super);
	 * 1 validation message is expected.
	 * It is expected to contain "The expression \"super\" is not valid as a function argument.".
	 */
	public void testLine99() {
		List messages = getMessagesAtLine( 99 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The expression \"super\" is not valid as a function argument." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The expression \"super\" is not valid as a function argument.\" was issued." );
	}

	/*
	 * setStringIn(true ? a : a);
	 * 0 validation messages are expected.
	 */
	public void testLine100() {
		List messages = getMessagesAtLine( 100 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * setArrayIn(true ? a : a);
	 * 0 validation messages are expected.
	 */
	public void testLine101() {
		List messages = getMessagesAtLine( 101 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ? i
	 * 1 validation message is expected.
	 * It is expected to contain "The argument i cannot be passed to the in or out parameter b of the function setBooleanIn. The types int and boolean are not assignment compatible.".
	 */
	public void testLine103() {
		List messages = getMessagesAtLine( 103 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The argument i cannot be passed to the in or out parameter b of the function setBooleanIn. The types int and boolean are not assignment compatible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The argument i cannot be passed to the in or out parameter b of the function setBooleanIn. The types int and boolean are not assignment compatible.\" was issued." );
	}

	/*
	 * : i);
	 * 1 validation message is expected.
	 * It is expected to contain "The argument i cannot be passed to the in or out parameter b of the function setBooleanIn. The types int and boolean are not assignment compatible.".
	 */
	public void testLine104() {
		List messages = getMessagesAtLine( 104 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The argument i cannot be passed to the in or out parameter b of the function setBooleanIn. The types int and boolean are not assignment compatible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The argument i cannot be passed to the in or out parameter b of the function setBooleanIn. The types int and boolean are not assignment compatible.\" was issued." );
	}

	/*
	 * ? this
	 * 1 validation message is expected.
	 * It is expected to contain "The expression \"this\" is not valid for use with parameters defined with the INOUT or OUT modifier.".
	 */
	public void testLine108() {
		List messages = getMessagesAtLine( 108 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The expression \"this\" is not valid for use with parameters defined with the INOUT or OUT modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The expression \"this\" is not valid for use with parameters defined with the INOUT or OUT modifier.\" was issued." );
	}

	/*
	 * : this);
	 * 1 validation message is expected.
	 * It is expected to contain "The expression \"this\" is not valid for use with parameters defined with the INOUT or OUT modifier.".
	 */
	public void testLine109() {
		List messages = getMessagesAtLine( 109 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The expression \"this\" is not valid for use with parameters defined with the INOUT or OUT modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The expression \"this\" is not valid for use with parameters defined with the INOUT or OUT modifier.\" was issued." );
	}

	/*
	 * ? super
	 * 1 validation message is expected.
	 * It is expected to contain "The expression \"super\" is not valid as a function argument.".
	 */
	public void testLine111() {
		List messages = getMessagesAtLine( 111 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The expression \"super\" is not valid as a function argument." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The expression \"super\" is not valid as a function argument.\" was issued." );
	}

	/*
	 * : super);
	 * 1 validation message is expected.
	 * It is expected to contain "The expression \"super\" is not valid as a function argument.".
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The expression \"super\" is not valid as a function argument." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The expression \"super\" is not valid as a function argument.\" was issued." );
	}

	/*
	 * ? dec1
	 * 0 validation messages are expected.
	 */
	public void testLine114() {
		List messages = getMessagesAtLine( 114 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * : dec2);
	 * 1 validation message is expected.
	 * It is expected to contain "The argument dec2 cannot be passed to the inOut or Out parameter d of the function setDecimalOut1. The types decimal(4,2) and decimal are not reference compatible.".
	 */
	public void testLine115() {
		List messages = getMessagesAtLine( 115 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The argument dec2 cannot be passed to the inOut or Out parameter d of the function setDecimalOut1. The types decimal(4,2) and decimal are not reference compatible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The argument dec2 cannot be passed to the inOut or Out parameter d of the function setDecimalOut1. The types decimal(4,2) and decimal are not reference compatible.\" was issued." );
	}

	/*
	 * ? dec1
	 * 1 validation message is expected.
	 * It is expected to contain "The argument dec1 cannot be passed to the inOut or Out parameter d of the function setDecimalOut2. The types decimal and decimal(4,2) are not reference compatible.".
	 */
	public void testLine117() {
		List messages = getMessagesAtLine( 117 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The argument dec1 cannot be passed to the inOut or Out parameter d of the function setDecimalOut2. The types decimal and decimal(4,2) are not reference compatible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The argument dec1 cannot be passed to the inOut or Out parameter d of the function setDecimalOut2. The types decimal and decimal(4,2) are not reference compatible.\" was issued." );
	}

	/*
	 * : dec2);
	 * 0 validation messages are expected.
	 */
	public void testLine118() {
		List messages = getMessagesAtLine( 118 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * setStringOut(true ? a : a);
	 * 0 validation messages are expected.
	 */
	public void testLine119() {
		List messages = getMessagesAtLine( 119 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * setArrayOut(true ? a : a);
	 * 0 validation messages are expected.
	 */
	public void testLine120() {
		List messages = getMessagesAtLine( 120 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ? i
	 * 1 validation message is expected.
	 * It is expected to contain "The argument i cannot be passed to the in or out parameter b of the function setBooleanOut. The types int and boolean are not assignment compatible.".
	 */
	public void testLine122() {
		List messages = getMessagesAtLine( 122 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The argument i cannot be passed to the in or out parameter b of the function setBooleanOut. The types int and boolean are not assignment compatible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The argument i cannot be passed to the in or out parameter b of the function setBooleanOut. The types int and boolean are not assignment compatible.\" was issued." );
	}

	/*
	 * : i);
	 * 1 validation message is expected.
	 * It is expected to contain "The argument i cannot be passed to the in or out parameter b of the function setBooleanOut. The types int and boolean are not assignment compatible.".
	 */
	public void testLine123() {
		List messages = getMessagesAtLine( 123 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The argument i cannot be passed to the in or out parameter b of the function setBooleanOut. The types int and boolean are not assignment compatible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The argument i cannot be passed to the in or out parameter b of the function setBooleanOut. The types int and boolean are not assignment compatible.\" was issued." );
	}

	/*
	 * ? this
	 * 1 validation message is expected.
	 * It is expected to contain "The expression \"this\" is not valid for use with parameters defined with the INOUT or OUT modifier.".
	 */
	public void testLine127() {
		List messages = getMessagesAtLine( 127 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The expression \"this\" is not valid for use with parameters defined with the INOUT or OUT modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The expression \"this\" is not valid for use with parameters defined with the INOUT or OUT modifier.\" was issued." );
	}

	/*
	 * : this);
	 * 1 validation message is expected.
	 * It is expected to contain "The expression \"this\" is not valid for use with parameters defined with the INOUT or OUT modifier.".
	 */
	public void testLine128() {
		List messages = getMessagesAtLine( 128 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The expression \"this\" is not valid for use with parameters defined with the INOUT or OUT modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The expression \"this\" is not valid for use with parameters defined with the INOUT or OUT modifier.\" was issued." );
	}

	/*
	 * ? super
	 * 1 validation message is expected.
	 * It is expected to contain "The expression \"super\" is not valid as a function argument.".
	 */
	public void testLine130() {
		List messages = getMessagesAtLine( 130 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The expression \"super\" is not valid as a function argument." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The expression \"super\" is not valid as a function argument.\" was issued." );
	}

	/*
	 * : super);
	 * 1 validation message is expected.
	 * It is expected to contain "The expression \"super\" is not valid as a function argument.".
	 */
	public void testLine131() {
		List messages = getMessagesAtLine( 131 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The expression \"super\" is not valid as a function argument." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The expression \"super\" is not valid as a function argument.\" was issued." );
	}

	/*
	 * ? s
	 * 0 validation messages are expected.
	 */
	public void testLine133() {
		List messages = getMessagesAtLine( 133 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * : nullableS);
	 * 1 validation message is expected.
	 * It is expected to contain "The argument nullableS cannot be passed to the inOut or Out parameter s of the function setStringInout. The types string? and string are not reference compatible.".
	 */
	public void testLine134() {
		List messages = getMessagesAtLine( 134 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The argument nullableS cannot be passed to the inOut or Out parameter s of the function setStringInout. The types string? and string are not reference compatible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The argument nullableS cannot be passed to the inOut or Out parameter s of the function setStringInout. The types string? and string are not reference compatible.\" was issued." );
	}

	/*
	 * ? s
	 * 1 validation message is expected.
	 * It is expected to contain "The argument s cannot be passed to the inOut or Out parameter s of the function setStringInoutNullable. The types string and string? are not reference compatible.".
	 */
	public void testLine136() {
		List messages = getMessagesAtLine( 136 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The argument s cannot be passed to the inOut or Out parameter s of the function setStringInoutNullable. The types string and string? are not reference compatible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The argument s cannot be passed to the inOut or Out parameter s of the function setStringInoutNullable. The types string and string? are not reference compatible.\" was issued." );
	}

	/*
	 * : nullableS);
	 * 0 validation messages are expected.
	 */
	public void testLine137() {
		List messages = getMessagesAtLine( 137 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ? ba
	 * 1 validation message is expected.
	 * It is expected to contain "The argument ba cannot be passed to the inOut or Out parameter s of the function setStringInout. The types boolean[] and string are not reference compatible.".
	 */
	public void testLine139() {
		List messages = getMessagesAtLine( 139 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The argument ba cannot be passed to the inOut or Out parameter s of the function setStringInout. The types boolean[] and string are not reference compatible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The argument ba cannot be passed to the inOut or Out parameter s of the function setStringInout. The types boolean[] and string are not reference compatible.\" was issued." );
	}

	/*
	 * : ba);
	 * 1 validation message is expected.
	 * It is expected to contain "The argument ba cannot be passed to the inOut or Out parameter s of the function setStringInout. The types boolean[] and string are not reference compatible.".
	 */
	public void testLine140() {
		List messages = getMessagesAtLine( 140 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The argument ba cannot be passed to the inOut or Out parameter s of the function setStringInout. The types boolean[] and string are not reference compatible." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The argument ba cannot be passed to the inOut or Out parameter s of the function setStringInout. The types boolean[] and string are not reference compatible.\" was issued." );
	}

	/*
	 * if (true) return true ? i : ba; end
	 * 1 validation message is expected.
	 * It is expected to contain "The type boolean[] does not match the type string which the function returns.".
	 */
	public void testLine144() {
		List messages = getMessagesAtLine( 144 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type boolean[] does not match the type string which the function returns." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type boolean[] does not match the type string which the function returns.\" was issued." );
	}

	/*
	 * if (true) return true ? ba : i; end
	 * 1 validation message is expected.
	 * It is expected to contain "The type boolean[] does not match the type string which the function returns.".
	 */
	public void testLine145() {
		List messages = getMessagesAtLine( 145 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type boolean[] does not match the type string which the function returns." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type boolean[] does not match the type string which the function returns.\" was issued." );
	}

	/*
	 * if (true) return true ? i : i; end
	 * 0 validation messages are expected.
	 */
	public void testLine146() {
		List messages = getMessagesAtLine( 146 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ? (true ? i : ba)
	 * 1 validation message is expected.
	 * It is expected to contain "The type boolean[] does not match the type string which the function returns.".
	 */
	public void testLine148() {
		List messages = getMessagesAtLine( 148 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type boolean[] does not match the type string which the function returns." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type boolean[] does not match the type string which the function returns.\" was issued." );
	}

	/*
	 * : (true ? ba : i)
	 * 1 validation message is expected.
	 * It is expected to contain "The type boolean[] does not match the type string which the function returns.".
	 */
	public void testLine149() {
		List messages = getMessagesAtLine( 149 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type boolean[] does not match the type string which the function returns." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type boolean[] does not match the type string which the function returns.\" was issued." );
	}

	/*
	 * if (true) return true ? myrec : ba; end
	 * 0 validation messages are expected.
	 */
	public void testLine154() {
		List messages = getMessagesAtLine( 154 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if (true) return true ? doNotExist : ba; end
	 * 1 validation message is expected.
	 * It is expected to contain "doNotExist cannot be resolved.".
	 */
	public void testLine155() {
		List messages = getMessagesAtLine( 155 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "doNotExist cannot be resolved." );
		if( messageWithSubstring == null ) fail( "No message with substring \"doNotExist cannot be resolved.\" was issued." );
	}

	/*
	 * ? getValue()
	 * 1 validation message is expected.
	 * It is expected to contain "The type int does not match the type rec which the function returns.".
	 */
	public void testLine160() {
		List messages = getMessagesAtLine( 160 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type int does not match the type rec which the function returns." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type int does not match the type rec which the function returns.\" was issued." );
	}

	/*
	 * : getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "The type boolean does not match the type rec which the function returns.".
	 */
	public void testLine161() {
		List messages = getMessagesAtLine( 161 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type boolean does not match the type rec which the function returns." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type boolean does not match the type rec which the function returns.\" was issued." );
	}

	/*
	 * if (true) return true ? ba.appendElement(true) : ba :: true; end
	 * 0 validation messages are expected.
	 */
	public void testLine166() {
		List messages = getMessagesAtLine( 166 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * return true ? d : f1;
	 * 0 validation messages are expected.
	 */
	public void testLine170() {
		List messages = getMessagesAtLine( 170 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = (true ? i : ba) as string;
	 * 1 validation message is expected.
	 * It is expected to contain "boolean[] and string are not compatible types in the expression ba as string".
	 */
	public void testLine174() {
		List messages = getMessagesAtLine( 174 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "boolean[] and string are not compatible types in the expression ba as string" );
		if( messageWithSubstring == null ) fail( "No message with substring \"boolean[] and string are not compatible types in the expression ba as string\" was issued." );
	}

	/*
	 * a = (true ? ba : i) as string;
	 * 1 validation message is expected.
	 * It is expected to contain "boolean[] and string are not compatible types in the expression ba as string".
	 */
	public void testLine175() {
		List messages = getMessagesAtLine( 175 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "boolean[] and string are not compatible types in the expression ba as string" );
		if( messageWithSubstring == null ) fail( "No message with substring \"boolean[] and string are not compatible types in the expression ba as string\" was issued." );
	}

	/*
	 * a = (true ? i : i) as string;
	 * 0 validation messages are expected.
	 */
	public void testLine176() {
		List messages = getMessagesAtLine( 176 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = (true ? myrec : ba) as any;
	 * 0 validation messages are expected.
	 */
	public void testLine177() {
		List messages = getMessagesAtLine( 177 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = (true ? doNotExist : ba) as any;
	 * 1 validation message is expected.
	 * It is expected to contain "doNotExist cannot be resolved.".
	 */
	public void testLine178() {
		List messages = getMessagesAtLine( 178 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "doNotExist cannot be resolved." );
		if( messageWithSubstring == null ) fail( "No message with substring \"doNotExist cannot be resolved.\" was issued." );
	}

	/*
	 * ? (true ? i : ba)
	 * 1 validation message is expected.
	 * It is expected to contain "boolean[] and string are not compatible types in the expression ba as string".
	 */
	public void testLine180() {
		List messages = getMessagesAtLine( 180 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "boolean[] and string are not compatible types in the expression ba as string" );
		if( messageWithSubstring == null ) fail( "No message with substring \"boolean[] and string are not compatible types in the expression ba as string\" was issued." );
	}

	/*
	 * : (true ? ba : i)
	 * 1 validation message is expected.
	 * It is expected to contain "boolean[] and string are not compatible types in the expression ba as string".
	 */
	public void testLine181() {
		List messages = getMessagesAtLine( 181 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "boolean[] and string are not compatible types in the expression ba as string" );
		if( messageWithSubstring == null ) fail( "No message with substring \"boolean[] and string are not compatible types in the expression ba as string\" was issued." );
	}

	/*
	 * ? getValue()
	 * 1 validation message is expected.
	 * It is expected to contain "int and rec are not compatible types in the expression getValue() as rec".
	 */
	public void testLine184() {
		List messages = getMessagesAtLine( 184 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "int and rec are not compatible types in the expression getValue() as rec" );
		if( messageWithSubstring == null ) fail( "No message with substring \"int and rec are not compatible types in the expression getValue() as rec\" was issued." );
	}

	/*
	 * : getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "boolean and rec are not compatible types in the expression getBoolean() as rec".
	 */
	public void testLine185() {
		List messages = getMessagesAtLine( 185 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "boolean and rec are not compatible types in the expression getBoolean() as rec" );
		if( messageWithSubstring == null ) fail( "No message with substring \"boolean and rec are not compatible types in the expression getBoolean() as rec\" was issued." );
	}

	/*
	 * ? ba.appendElement(true)
	 * 0 validation messages are expected.
	 */
	public void testLine188() {
		List messages = getMessagesAtLine( 188 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * : ba :: true
	 * 0 validation messages are expected.
	 */
	public void testLine189() {
		List messages = getMessagesAtLine( 189 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = (true ? d : f1) as del1;
	 * 0 validation messages are expected.
	 */
	public void testLine191() {
		List messages = getMessagesAtLine( 191 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = ba :: (true ? true : ba[1]);
	 * 0 validation messages are expected.
	 */
	public void testLine195() {
		List messages = getMessagesAtLine( 195 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = ba :: (true ? true && false : ba :: true);
	 * 0 validation messages are expected.
	 */
	public void testLine196() {
		List messages = getMessagesAtLine( 196 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = ba :: (true ? true ? false : false : false);
	 * 0 validation messages are expected.
	 */
	public void testLine197() {
		List messages = getMessagesAtLine( 197 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = i * (true ? s : i);
	 * 0 validation messages are expected.
	 */
	public void testLine198() {
		List messages = getMessagesAtLine( 198 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = b * (true ? s : i);
	 * 2 validation messages are expected.
	 * One message is expected to contain "No operation is defined for expressions b and s with the * operator in the expression b * s.".
	 * One message is expected to contain "No operation is defined for expressions b and i with the * operator in the expression b * i.".
	 */
	public void testLine199() {
		List messages = getMessagesAtLine( 199 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "No operation is defined for expressions b and s with the * operator in the expression b * s." );
		if( messageWithSubstring == null ) fail( "No message with substring \"No operation is defined for expressions b and s with the * operator in the expression b * s.\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "No operation is defined for expressions b and i with the * operator in the expression b * i." );
		if( messageWithSubstring == null ) fail( "No message with substring \"No operation is defined for expressions b and i with the * operator in the expression b * i.\" was issued." );
	}

	/*
	 * a = s + true ? true : true;
	 * 0 validation messages are expected.
	 */
	public void testLine200() {
		List messages = getMessagesAtLine( 200 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i = i * 76 - (true ? 22 : i - 99) % (false ? getValue() * 88 : i >> 2);
	 * 0 validation messages are expected.
	 */
	public void testLine201() {
		List messages = getMessagesAtLine( 201 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = ba :: (true ? d : true);
	 * 1 validation message is expected.
	 * It is expected to contain "boolean[] and del1 are not compatible types in the expression ba :: d".
	 */
	public void testLine202() {
		List messages = getMessagesAtLine( 202 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "boolean[] and del1 are not compatible types in the expression ba :: d" );
		if( messageWithSubstring == null ) fail( "No message with substring \"boolean[] and del1 are not compatible types in the expression ba :: d\" was issued." );
	}

	/*
	 * for (i from true ? 10 : 20 to true ? 10 : 20 by true ? 10 : 20) end
	 * 0 validation messages are expected.
	 */
	public void testLine206() {
		List messages = getMessagesAtLine( 206 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * for (i from true ? getValue() : getValue() to true ? getValue() : getValue() by true ? getValue() : getValue()) end
	 * 0 validation messages are expected.
	 */
	public void testLine207() {
		List messages = getMessagesAtLine( 207 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * for (i from true ? true ? getValue() : getValue() : true ? getValue() : getValue()
	 * 0 validation messages are expected.
	 */
	public void testLine208() {
		List messages = getMessagesAtLine( 208 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * to true ? true ? getValue() : getValue() : true ? getValue() : getValue()
	 * 0 validation messages are expected.
	 */
	public void testLine209() {
		List messages = getMessagesAtLine( 209 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * by true ? true ? getValue() : getValue() : true ? getValue() : getValue()) end
	 * 0 validation messages are expected.
	 */
	public void testLine210() {
		List messages = getMessagesAtLine( 210 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ? getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement start must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine213() {
		List messages = getMessagesAtLine( 213 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement start must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement start must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * : getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement start must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine214() {
		List messages = getMessagesAtLine( 214 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement start must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement start must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * ? getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement end must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine216() {
		List messages = getMessagesAtLine( 216 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement end must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement end must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * : getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement end must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine217() {
		List messages = getMessagesAtLine( 217 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement end must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement end must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * ? getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement delta must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine219() {
		List messages = getMessagesAtLine( 219 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement delta must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement delta must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * : getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement delta must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine220() {
		List messages = getMessagesAtLine( 220 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement delta must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement delta must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * ? getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement start must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine225() {
		List messages = getMessagesAtLine( 225 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement start must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement start must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * : getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement start must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine226() {
		List messages = getMessagesAtLine( 226 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement start must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement start must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * ? getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement start must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine228() {
		List messages = getMessagesAtLine( 228 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement start must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement start must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * : getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement start must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine229() {
		List messages = getMessagesAtLine( 229 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement start must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement start must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * ? getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement end must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine232() {
		List messages = getMessagesAtLine( 232 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement end must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement end must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * : getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement end must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine233() {
		List messages = getMessagesAtLine( 233 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement end must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement end must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * ? getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement end must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine235() {
		List messages = getMessagesAtLine( 235 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement end must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement end must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * : getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement end must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine236() {
		List messages = getMessagesAtLine( 236 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement end must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement end must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * ? getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement delta must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine239() {
		List messages = getMessagesAtLine( 239 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement delta must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement delta must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * : getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement delta must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine240() {
		List messages = getMessagesAtLine( 240 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement delta must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement delta must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * ? getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement delta must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine242() {
		List messages = getMessagesAtLine( 242 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement delta must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement delta must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * : getBoolean()
	 * 1 validation message is expected.
	 * It is expected to contain "For statement delta must be an integer literal, integer item or numeric expression of integers.".
	 */
	public void testLine243() {
		List messages = getMessagesAtLine( 243 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "For statement delta must be an integer literal, integer item or numeric expression of integers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"For statement delta must be an integer literal, integer item or numeric expression of integers.\" was issued." );
	}

	/*
	 * a = true
	 * 0 validation messages are expected.
	 */
	public void testLine249() {
		List messages = getMessagesAtLine( 249 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = (1 >= 1)
	 * 0 validation messages are expected.
	 */
	public void testLine251() {
		List messages = getMessagesAtLine( 251 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = b
	 * 0 validation messages are expected.
	 */
	public void testLine253() {
		List messages = getMessagesAtLine( 253 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = ba[1]
	 * 0 validation messages are expected.
	 */
	public void testLine255() {
		List messages = getMessagesAtLine( 255 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = (true ? false : true)
	 * 0 validation messages are expected.
	 */
	public void testLine257() {
		List messages = getMessagesAtLine( 257 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = getBoolean()
	 * 0 validation messages are expected.
	 */
	public void testLine259() {
		List messages = getMessagesAtLine( 259 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a = doNotExist
	 * 1 validation message is expected.
	 * It is expected to contain "doNotExist cannot be resolved.".
	 */
	public void testLine262() {
		List messages = getMessagesAtLine( 262 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "doNotExist cannot be resolved." );
		if( messageWithSubstring == null ) fail( "No message with substring \"doNotExist cannot be resolved.\" was issued." );
	}

	/*
	 * a = s
	 * 1 validation message is expected.
	 * It is expected to contain "string and boolean are not compatible types in the expression s".
	 */
	public void testLine264() {
		List messages = getMessagesAtLine( 264 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "string and boolean are not compatible types in the expression s" );
		if( messageWithSubstring == null ) fail( "No message with substring \"string and boolean are not compatible types in the expression s\" was issued." );
	}

	/*
	 * a = a
	 * 1 validation message is expected.
	 * It is expected to contain "any and boolean are not compatible types in the expression a".
	 */
	public void testLine266() {
		List messages = getMessagesAtLine( 266 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "any and boolean are not compatible types in the expression a" );
		if( messageWithSubstring == null ) fail( "No message with substring \"any and boolean are not compatible types in the expression a\" was issued." );
	}

	/*
	 * a = getValue()
	 * 1 validation message is expected.
	 * It is expected to contain "int and boolean are not compatible types in the expression getValue()".
	 */
	public void testLine268() {
		List messages = getMessagesAtLine( 268 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "int and boolean are not compatible types in the expression getValue()" );
		if( messageWithSubstring == null ) fail( "No message with substring \"int and boolean are not compatible types in the expression getValue()\" was issued." );
	}

	/*
	 * a = null
	 * 1 validation message is expected.
	 * It is expected to contain "null and boolean are not compatible types in the expression null".
	 */
	public void testLine270() {
		List messages = getMessagesAtLine( 270 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "null and boolean are not compatible types in the expression null" );
		if( messageWithSubstring == null ) fail( "No message with substring \"null and boolean are not compatible types in the expression null\" was issued." );
	}
}
