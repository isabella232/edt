package org.eclipse.edt.tests.validation.junit.ibmi;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/ibmi/IBMiProxyFunctionTests.egl
 */
public class IBMiProxyFunctionTestsTest extends ValidationTestCase {

	public IBMiProxyFunctionTestsTest() {
		super( "EGLSource/ibmi/IBMiProxyFunctionTests.egl", false );
	}

	/*
	 * function f1()
	 * 1 validation message is expected.
	 * It is expected to contain "The container for the IBMiProgram function f1 is invalid. IBMiProgram functions are only allowed in Programs, Libraries, Services, and Basic Handlers.".
	 */
	public void testLine4() {
		List messages = getMessagesAtLine( 4 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The container for the IBMiProgram function f1 is invalid. IBMiProgram functions are only allowed in Programs, Libraries, Services, and Basic Handlers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The container for the IBMiProgram function f1 is invalid. IBMiProgram functions are only allowed in Programs, Libraries, Services, and Basic Handlers.\" was issued." );
	}

	/*
	 * function f1()
	 * 1 validation message is expected.
	 * It is expected to contain "The container for the IBMiProgram function f1 is invalid. IBMiProgram functions are only allowed in Programs, Libraries, Services, and Basic Handlers.".
	 */
	public void testLine9() {
		List messages = getMessagesAtLine( 9 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The container for the IBMiProgram function f1 is invalid. IBMiProgram functions are only allowed in Programs, Libraries, Services, and Basic Handlers." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The container for the IBMiProgram function f1 is invalid. IBMiProgram functions are only allowed in Programs, Libraries, Services, and Basic Handlers.\" was issued." );
	}

	/*
	 * f1 smallint {@Structbin1};
	 * 0 validation messages are expected.
	 */
	public void testLine14() {
		List messages = getMessagesAtLine( 14 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f2 smallint {@Structbin2};
	 * 0 validation messages are expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f3 smallint {@Structunsignedbin1};
	 * 0 validation messages are expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f4 int {@Structbin4};
	 * 0 validation messages are expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f5 int {@Structunsignedbin2};
	 * 0 validation messages are expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f6 bigint {@Structbin8};
	 * 0 validation messages are expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f7 bigint {@Structunsignedbin4};
	 * 0 validation messages are expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f8 decimal(20) {@Structunsignedbin8};
	 * 0 validation messages are expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f9 date {@Structdate};
	 * 0 validation messages are expected.
	 */
	public void testLine22() {
		List messages = getMessagesAtLine( 22 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fa timestamp("yyyy") {@Structtimestamp};
	 * 0 validation messages are expected.
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fb timestamp {@Structtimestamp{eglPattern = "yyyy"}}  = "";
	 * 0 validation messages are expected.
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fc smallfloat {@Structfloat4};
	 * 0 validation messages are expected.
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fd float {@Structfloat8{}};
	 * 0 validation messages are expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fe decimal(10) {@StructdecFloat{}};
	 * 0 validation messages are expected.
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ff decimal(10) {@Structpackeddecimal{}};
	 * 0 validation messages are expected.
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fg decimal(10) {@Structzoneddecimal{}};
	 * 0 validation messages are expected.
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fh decimal {@StructdecFloat{length = 3}} = 0;
	 * 0 validation messages are expected.
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fi decimal {@Structpackeddecimal{length = 3, decimals = 2}} = 0;
	 * 0 validation messages are expected.
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fj decimal {@Structzoneddecimal{length = 3, decimals = 2}} = 0;
	 * 0 validation messages are expected.
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fl int[] {@Structarray};
	 * 0 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fm int[] {@Structarray{returnCountVariable = f1}};
	 * 0 validation messages are expected.
	 */
	public void testLine35() {
		List messages = getMessagesAtLine( 35 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * fn string[] {@Structarray{elementTypeAnnotation = @Structtext{length = 3}}};
	 * 0 validation messages are expected.
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f1 smallint {@Structbin4};
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
	 * f2 smallint? {@Structbin2};
	 * 1 validation message is expected.
	 * It is expected to contain "not valid for use with the nullable type".
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not valid for use with the nullable type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not valid for use with the nullable type\" was issued." );
	}

	/*
	 * f3 smallint {@Structtext};
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
	 * f4 int {@Structbin2};
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
	 * f5 int {@Structbin8};
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
	 * f6 bigint {@Structbin2};
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
	 * f7 bigint {@Structdate};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * f8 decimal(19) {@Structunsignedbin8};
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
	 * f9 date {@Structtimestamp};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * fa timestamp("yyyy") {@Structdate};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * fb timestamp {@Structtimestamp{eglPattern = "yyddd"}}  = "";
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid pattern".
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid pattern" );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid pattern\" was issued." );
	}

	/*
	 * fba timestamp {@Structtimestamp{}}  = "";
	 * 1 validation message is expected.
	 * It is expected to contain "eglPattern must be specified".
	 */
	public void testLine51() {
		List messages = getMessagesAtLine( 51 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "eglPattern must be specified" );
		if( messageWithSubstring == null ) fail( "No message with substring \"eglPattern must be specified\" was issued." );
	}

	/*
	 * fc smallfloat {@Structfloat8};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine52() {
		List messages = getMessagesAtLine( 52 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * fd float {@Structfloat4{}};
	 * 1 validation message is expected.
	 * It is expected to contain "not compatible with the type".
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not compatible with the type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not compatible with the type\" was issued." );
	}

	/*
	 * fe decimal(10) {@StructdecFloat{length = 3}};
	 * 1 validation message is expected.
	 * It is expected to contain "length is not allowed".
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "length is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"length is not allowed\" was issued." );
	}

	/*
	 * ff decimal(10) {@Structpackeddecimal{decimals = 2}};
	 * 1 validation message is expected.
	 * It is expected to contain "decimals is not allowed".
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "decimals is not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"decimals is not allowed\" was issued." );
	}

	/*
	 * fg decimal(10) {@Structzoneddecimal{length = 3, decimals = 2}};
	 * 2 validation messages are expected.
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * fh decimal {@StructdecFloat{length = -3}} = 0;
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
	 * fi decimal {@Structpackeddecimal{length = 3, decimals = 4}} = 0;
	 * 1 validation message is expected.
	 * It is expected to contain "must be an integer".
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be an integer" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be an integer\" was issued." );
	}

	/*
	 * fj decimal {@Structzoneddecimal{length = 33, decimals = 2}} = 0;
	 * 1 validation message is expected.
	 * It is expected to contain "must be an integer".
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be an integer" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be an integer\" was issued." );
	}

	/*
	 * fha decimal {@StructdecFloat{}} = 0;
	 * 1 validation message is expected.
	 * It is expected to contain "length must be specified".
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "length must be specified" );
		if( messageWithSubstring == null ) fail( "No message with substring \"length must be specified\" was issued." );
	}

	/*
	 * fib decimal {@Structpackeddecimal{}} = 0;
	 * 2 validation messages are expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * fjc decimal {@Structzoneddecimal{}} = 0;
	 * 2 validation messages are expected.
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * fk string {@Structtext{length = -3}};
	 * 1 validation message is expected.
	 * It is expected to contain "must be an integer".
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be an integer" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be an integer\" was issued." );
	}

	/*
	 * fka string {@Structtext{}};
	 * 1 validation message is expected.
	 * It is expected to contain "length must be specified".
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "length must be specified" );
		if( messageWithSubstring == null ) fail( "No message with substring \"length must be specified\" was issued." );
	}

	/*
	 * fl boolean[] {@Structarray};
	 * 1 validation message is expected.
	 * It is expected to contain "is not compatible".
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "is not compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"is not compatible\" was issued." );
	}

	/*
	 * fla int?[] {@Structarray{}};
	 * 1 validation message is expected.
	 * It is expected to contain "not valid for use with the nullable type".
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "not valid for use with the nullable type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"not valid for use with the nullable type\" was issued." );
	}

	/*
	 * fm int[] {@Structarray{returnCountVariable = fa}};
	 * 1 validation message is expected.
	 * It is expected to contain "must have a type that is assignment compatible".
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must have a type that is assignment compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must have a type that is assignment compatible\" was issued." );
	}

	/*
	 * fn string[] {@Structarray{elementTypeAnnotation = @Structtext{}}};
	 * 1 validation message is expected.
	 * It is expected to contain "length must be specified".
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "length must be specified" );
		if( messageWithSubstring == null ) fail( "No message with substring \"length must be specified\" was issued." );
	}

	/*
	 * function f1(p1 decimal)
	 * 1 validation message is expected.
	 * It is expected to contain "An entry in the array of parameterAnnotations is required for parameter p1.".
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "An entry in the array of parameterAnnotations is required for parameter p1." );
		if( messageWithSubstring == null ) fail( "No message with substring \"An entry in the array of parameterAnnotations is required for parameter p1.\" was issued." );
	}

	/*
	 * function f2(p1 int)
	 * 1 validation message is expected.
	 * It is expected to contain "The value fred specified in the parameterAnnotations for parameter p1 is invalid.".
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value fred specified in the parameterAnnotations for parameter p1 is invalid." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value fred specified in the parameterAnnotations for parameter p1 is invalid.\" was issued." );
	}

	/*
	 * a int;
	 * 1 validation message is expected.
	 * It is expected to contain "cannot contain statements.".
	 */
	public void testLine97() {
		List messages = getMessagesAtLine( 97 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot contain statements." );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot contain statements.\" was issued." );
	}

	/*
	 * function f4() returns (int)
	 * 1 validation message is expected.
	 * It is expected to contain "cannot return a value".
	 */
	public void testLine100() {
		List messages = getMessagesAtLine( 100 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "cannot return a value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"cannot return a value\" was issued." );
	}

	/*
	 * function f5() returns (string)
	 * 1 validation message is expected.
	 * It is expected to contain "only valid return type is INT".
	 */
	public void testLine104() {
		List messages = getMessagesAtLine( 104 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "only valid return type is INT" );
		if( messageWithSubstring == null ) fail( "No message with substring \"only valid return type is INT\" was issued." );
	}

	/*
	 * p1 int?)
	 * 1 validation message is expected.
	 * It is expected to contain "The nullable type int? is not supported for parameter p1 in an IMBiProgram function.".
	 */
	public void testLine109() {
		List messages = getMessagesAtLine( 109 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The nullable type int? is not supported for parameter p1 in an IMBiProgram function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The nullable type int? is not supported for parameter p1 in an IMBiProgram function.\" was issued." );
	}

	/*
	 * p2 int?[])
	 * 1 validation message is expected.
	 * It is expected to contain "The array of nullable types int is not supported for parameter p2 in an IMBiProgram function.".
	 */
	public void testLine114() {
		List messages = getMessagesAtLine( 114 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The array of nullable types int is not supported for parameter p2 in an IMBiProgram function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The array of nullable types int is not supported for parameter p2 in an IMBiProgram function.\" was issued." );
	}

	/*
	 * function f8 ( p1 string)
	 * 1 validation message is expected.
	 * It is expected to contain "An entry in the array of parameterAnnotations is required for parameter p1.".
	 */
	public void testLine119() {
		List messages = getMessagesAtLine( 119 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "An entry in the array of parameterAnnotations is required for parameter p1." );
		if( messageWithSubstring == null ) fail( "No message with substring \"An entry in the array of parameterAnnotations is required for parameter p1.\" was issued." );
	}

	/*
	 * function f9 ( p1 boolean)
	 * 1 validation message is expected.
	 * It is expected to contain "The type of parameter p1 is not supported in an IBMiProgram function.".
	 */
	public void testLine123() {
		List messages = getMessagesAtLine( 123 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The type of parameter p1 is not supported in an IBMiProgram function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type of parameter p1 is not supported in an IBMiProgram function.\" was issued." );
	}

	/*
	 * function f10 (p1 rec1)
	 * 4 validation messages are expected.
	 * One message is expected to contain "field1 with the type boolean".
	 * One message is expected to contain "field2 with the type int?".
	 * One message is expected to contain "field3 with the type int?[]".
	 * One message is expected to contain "The type of this field requires that an Struct type annotation be specified for the field".
	 */
	public void testLine127() {
		List messages = getMessagesAtLine( 127 );
		assertEquals( 4, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "field1 with the type boolean" );
		if( messageWithSubstring == null ) fail( "No message with substring \"field1 with the type boolean\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "field2 with the type int?" );
		if( messageWithSubstring == null ) fail( "No message with substring \"field2 with the type int?\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "field3 with the type int?[]" );
		if( messageWithSubstring == null ) fail( "No message with substring \"field3 with the type int?[]\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "The type of this field requires that an Struct type annotation be specified for the field" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The type of this field requires that an Struct type annotation be specified for the field\" was issued." );
	}

	/*
	 * function f11(p1 string, p2 string(10))
	 * 1 validation message is expected.
	 * It is expected to contain "The number of entries specified for parameterAnnotations must exactly match the number of parameters defined for the function f11.".
	 */
	public void testLine131() {
		List messages = getMessagesAtLine( 131 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The number of entries specified for parameterAnnotations must exactly match the number of parameters defined for the function f11." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The number of entries specified for parameterAnnotations must exactly match the number of parameters defined for the function f11.\" was issued." );
	}
}
