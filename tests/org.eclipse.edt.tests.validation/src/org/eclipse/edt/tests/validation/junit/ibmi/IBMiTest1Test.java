package org.eclipse.edt.tests.validation.junit.ibmi;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/ibmi/IBMiTest1.egl
 */
public class IBMiTest1Test extends ValidationTestCase {

	public IBMiTest1Test() {
		super( "EGLSource/ibmi/IBMiTest1.egl", false );
	}

	/*
	 * {@IBMiProgram {}};
	 * 1 validation message is expected.
	 * It is expected to contain "container for the IBMiProgram function f1 is invalid".
	 */
	public void testLine3() {
		List messages = getMessagesAtLine( 3 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "container for the IBMiProgram function f1 is invalid" );
		if( messageWithSubstring == null ) fail( "No message with substring \"container for the IBMiProgram function f1 is invalid\" was issued." );
	}

	/*
	 * {@IBMiProgram {}};
	 * 1 validation message is expected.
	 * It is expected to contain "container for the IBMiProgram function f1 is invalid".
	 */
	public void testLine8() {
		List messages = getMessagesAtLine( 8 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "container for the IBMiProgram function f1 is invalid" );
		if( messageWithSubstring == null ) fail( "No message with substring \"container for the IBMiProgram function f1 is invalid\" was issued." );
	}

	/*
	 * f1 smallint {@as400bin1};
	 * 0 validation messages are expected.
	 */
	public void testLine12() {
		List messages = getMessagesAtLine( 12 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f2 smallint {@as400bin2};
	 * 0 validation messages are expected.
	 */
	public void testLine13() {
		List messages = getMessagesAtLine( 13 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f3 smallint {@as400unsignedbin1};
	 * 0 validation messages are expected.
	 */
	public void testLine14() {
		List messages = getMessagesAtLine( 14 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f4 int {@as400bin4};
	 * 0 validation messages are expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f5 int {@as400unsignedbin2};
	 * 0 validation messages are expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f6 bigint {@as400bin8};
	 * 0 validation messages are expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f7 bigint {@as400unsignedbin4};
	 * 0 validation messages are expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f8 decimal(20) {@as400unsignedbin8};
	 * 0 validation messages are expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f9 date {@as400date};
	 * 0 validation messages are expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fa timestamp("yyyy") {@as400timestamp};
	 * 0 validation messages are expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fb timestamp {@as400timestamp{eglPattern = "yyyy"}}  = "";
	 * 0 validation messages are expected.
	 */
	public void testLine22() {
		List messages = getMessagesAtLine( 22 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fc smallfloat {@as400float4};
	 * 0 validation messages are expected.
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fd float {@as400float8{}};
	 * 0 validation messages are expected.
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fe decimal(10) {@as400decFloat{}};
	 * 0 validation messages are expected.
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ff decimal(10) {@as400packeddecimal{}};
	 * 0 validation messages are expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fg decimal(10) {@as400zoneddecimal{}};
	 * 0 validation messages are expected.
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fh decimal {@as400decFloat{length = 3}} = 0;
	 * 0 validation messages are expected.
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fi decimal {@as400packeddecimal{length = 3, decimals = 2}} = 0;
	 * 0 validation messages are expected.
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fj decimal {@as400zoneddecimal{length = 3, decimals = 2}} = 0;
	 * 0 validation messages are expected.
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fl int[] {@as400array};
	 * 0 validation messages are expected.
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fm int[] {@as400array{returnCountVariable = f1}};
	 * 0 validation messages are expected.
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fn string[] {@as400array{elementTypeAS400Annotation = @as400text{length = 3}}};
	 * 0 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f1 smallint {@as400bin4};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * f2 smallint? {@as400bin2};
	 * 1 validation message is expected.
	 * It is expected to contain "not valid for use with the nullable type".
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not valid for use with the nullable type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not valid for use with the nullable type\" was issued." );
	}

	/*
	 * f3 smallint {@as400text};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * f4 int {@as400bin2};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * f5 int {@as400bin8};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * f6 bigint {@as400bin2};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * f7 bigint {@as400date};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * f8 decimal(19) {@as400unsignedbin8};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine45() {
		List messages = getMessagesAtLine( 45 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * fa timestamp("yyyy") {@as400date};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * fb timestamp {@as400timestamp{eglPattern = "yyddd"}}  = "";
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid pattern".
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid pattern" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid pattern\" was issued." );
	}

	/*
	 * fba timestamp {@as400timestamp{}}  = "";
	 * 1 validation message is expected.
	 * It is expected to contain "eglPattern must be specified".
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "eglPattern must be specified" );
		if( messageWithSubstring == null ) fail( "No message with substring \"eglPattern must be specified\" was issued." );
	}

	/*
	 * fc smallfloat {@as400float8};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * fd float {@as400float4{}};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine51() {
		List messages = getMessagesAtLine( 51 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * fe decimal(10) {@as400decFloat{length = 3}};
	 * 1 validation message is expected.
	 * It is expected to contain "length is not allowed".
	 */
	public void testLine52() {
		List messages = getMessagesAtLine( 52 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "length is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"length is not allowed\" was issued." );
	}

	/*
	 * ff decimal(10) {@as400packeddecimal{decimals = 2}};
	 * 1 validation message is expected.
	 * It is expected to contain "decimals is not allowed".
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "decimals is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"decimals is not allowed\" was issued." );
	}

	/*
	 * fg decimal(10) {@as400zoneddecimal{length = 3, decimals = 2}};
	 * 2 validation messages are expected.
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * fh decimal {@as400decFloat{length = -3}} = 0;
	 * 1 validation message is expected.
	 * It is expected to contain "must be an integer".
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be an integer" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be an integer\" was issued." );
	}

	/*
	 * fi decimal {@as400packeddecimal{length = 3, decimals = 4}} = 0;
	 * 1 validation message is expected.
	 * It is expected to contain "must be an integer".
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be an integer" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be an integer\" was issued." );
	}

	/*
	 * fj decimal {@as400zoneddecimal{length = 33, decimals = 2}} = 0;
	 * 1 validation message is expected.
	 * It is expected to contain "must be an integer".
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be an integer" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be an integer\" was issued." );
	}

	/*
	 * fha decimal {@as400decFloat{}} = 0;
	 * 1 validation message is expected.
	 * It is expected to contain "length must be specified".
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "length must be specified" );
		if( messageWithSubstring == null ) fail( "No message with substring \"length must be specified\" was issued." );
	}

	/*
	 * fib decimal {@as400packeddecimal{}} = 0;
	 * 2 validation messages are expected.
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * fjc decimal {@as400zoneddecimal{}} = 0;
	 * 2 validation messages are expected.
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * fk string {@as400text{length = -3}};
	 * 1 validation message is expected.
	 * It is expected to contain "must be an integer".
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be an integer" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be an integer\" was issued." );
	}

	/*
	 * fka string {@as400text{}};
	 * 1 validation message is expected.
	 * It is expected to contain "length must be specified".
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "length must be specified" );
		if( messageWithSubstring == null ) fail( "No message with substring \"length must be specified\" was issued." );
	}

	/*
	 * fl boolean[] {@as400array};
	 * 1 validation message is expected.
	 * It is expected to contain "is not compatible".
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "is not compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"is not compatible\" was issued." );
	}

	/*
	 * fla int?[] {@as400array{}};
	 * 1 validation message is expected.
	 * It is expected to contain "not valid for use with the nullable type".
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not valid for use with the nullable type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not valid for use with the nullable type\" was issued." );
	}

	/*
	 * fm int[] {@as400array{returnCountVariable = fa}};
	 * 1 validation message is expected.
	 * It is expected to contain "must have a type that is assignment compatible".
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must have a type that is assignment compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must have a type that is assignment compatible\" was issued." );
	}

	/*
	 * fn string[] {@as400array{elementTypeAS400Annotation = @as400text{}}};
	 * 1 validation message is expected.
	 * It is expected to contain "length must be specified".
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "length must be specified" );
		if( messageWithSubstring == null ) fail( "No message with substring \"length must be specified\" was issued." );
	}

	/*
	 * parameterAnnotations = []
	 * 1 validation message is expected.
	 * It is expected to contain "must exactly match the number of parameters".
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must exactly match the number of parameters" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must exactly match the number of parameters\" was issued." );
	}

	/*
	 * parameterAnnotations = ["fred"]
	 * 1 validation message is expected.
	 * It is expected to contain "value specified for the parameterAnnotations".
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "value specified for the parameterAnnotations" );
		if( messageWithSubstring == null ) fail( "No message with substring \"value specified for the parameterAnnotations\" was issued." );
	}

	/*
	 * a int;
	 * 1 validation message is expected.
	 * It is expected to contain "cannot contain statements.".
	 */
	public void testLine95() {
		List messages = getMessagesAtLine( 95 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot contain statements." );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot contain statements.\" was issued." );
	}

	/*
	 * function f4() returns (int)
	 * 1 validation message is expected.
	 * It is expected to contain "cannot return a value".
	 */
	public void testLine98() {
		List messages = getMessagesAtLine( 98 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot return a value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot return a value\" was issued." );
	}

	/*
	 * function f5() returns (string)
	 * 1 validation message is expected.
	 * It is expected to contain "only valid return type is INT".
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "only valid return type is INT" );
		if( messageWithSubstring == null ) fail( "No message with substring \"only valid return type is INT\" was issued." );
	}

	/*
	 * {@IBMiProgram{}}
	 * 1 validation message is expected.
	 * It is expected to contain "nullable type int? is not supported".
	 */
	public void testLine108() {
		List messages = getMessagesAtLine( 108 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "nullable type int? is not supported" );
		if( messageWithSubstring == null ) fail( "No message with substring \"nullable type int? is not supported\" was issued." );
	}

	/*
	 * {@IBMiProgram{}}
	 * 1 validation message is expected.
	 * It is expected to contain "array of nullable types int?[] is not supported".
	 */
	public void testLine113() {
		List messages = getMessagesAtLine( 113 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "array of nullable types int?[] is not supported" );
		if( messageWithSubstring == null ) fail( "No message with substring \"array of nullable types int?[] is not supported\" was issued." );
	}

	/*
	 * {@IBMiProgram{}}
	 * 1 validation message is expected.
	 * It is expected to contain "entry in the array of IBMiProgramParameterAnnotations is required".
	 */
	public void testLine118() {
		List messages = getMessagesAtLine( 118 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "entry in the array of IBMiProgramParameterAnnotations is required" );
		if( messageWithSubstring == null ) fail( "No message with substring \"entry in the array of IBMiProgramParameterAnnotations is required\" was issued." );
	}

	/*
	 * {@IBMiProgram{}}
	 * 1 validation message is expected.
	 * It is expected to contain "type of parameter p1 is not supported".
	 */
	public void testLine122() {
		List messages = getMessagesAtLine( 122 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "type of parameter p1 is not supported" );
		if( messageWithSubstring == null ) fail( "No message with substring \"type of parameter p1 is not supported\" was issued." );
	}

	/*
	 * {@IBMiProgram{}}
	 * 4 validation messages are expected.
	 */
	public void testLine126() {
		List messages = getMessagesAtLine( 126 );
		assertEquals( 4, messages.size() );
	}
}
