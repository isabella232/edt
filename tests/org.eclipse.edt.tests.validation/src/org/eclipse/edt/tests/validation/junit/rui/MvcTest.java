package org.eclipse.edt.tests.validation.junit.rui;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/rui/mvc.egl
 */
public class MvcTest extends ValidationTestCase {

	public MvcTest() {
		super( "EGLSource/rui/mvc.egl", false );
	}

	/*
	 * i1 int{@currency};
	 * 0 validation messages are expected.
	 */
	public void testLine14() {
		List messages = getMessagesAtLine( 14 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i2 int{@currency{yes}};
	 * 0 validation messages are expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i3 int{@currency{no}};
	 * 0 validation messages are expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i4 int{@currencySymbol};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: currencySymbol.  The value for this property is invalid. The value must be one of the following: a string literal of appropriate length.".
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: currencySymbol.  The value for this property is invalid. The value must be one of the following: a string literal of appropriate length." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: currencySymbol.  The value for this property is invalid. The value must be one of the following: a string literal of appropriate length.\" was issued." );
	}

	/*
	 * i5 int{@currencySymbol{""}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: currencySymbol.  The value for this property is invalid. The value must be one of the following: a string literal of appropriate length.".
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: currencySymbol.  The value for this property is invalid. The value must be one of the following: a string literal of appropriate length." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: currencySymbol.  The value for this property is invalid. The value must be one of the following: a string literal of appropriate length.\" was issued." );
	}

	/*
	 * i6 int{@currencySymbol{"$"}};
	 * 0 validation messages are expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i7 int{@currencySymbol{"USD"}};
	 * 0 validation messages are expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i8 int{@currencySymbol{"USD$"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The length of value USD$ for property currencySymbol exceeds the maximum length of 3.".
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The length of value USD$ for property currencySymbol exceeds the maximum length of 3." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The length of value USD$ for property currencySymbol exceeds the maximum length of 3.\" was issued." );
	}

	/*
	 * s1 string{@currency};
	 * 1 validation message is expected.
	 * It is expected to contain "Property currency must be defined with a numeric type. The type of this item is string.".
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property currency must be defined with a numeric type. The type of this item is string." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property currency must be defined with a numeric type. The type of this item is string.\" was issued." );
	}

	/*
	 * s2 string{@currency{yes}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property currency must be defined with a numeric type. The type of this item is string.".
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property currency must be defined with a numeric type. The type of this item is string." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property currency must be defined with a numeric type. The type of this item is string.\" was issued." );
	}

	/*
	 * s3 string{@currency{no}};
	 * 0 validation messages are expected.
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s4 string{@currencySymbol};
	 * 2 validation messages are expected.
	 * One message is expected to contain "Property currencySymbol must be defined with a numeric type. The type of this item is string.".
	 * One message is expected to contain "Property: currencySymbol.  The value for this property is invalid. The value must be one of the following: a string literal of appropriate length.".
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property currencySymbol must be defined with a numeric type. The type of this item is string." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property currencySymbol must be defined with a numeric type. The type of this item is string.\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "Property: currencySymbol.  The value for this property is invalid. The value must be one of the following: a string literal of appropriate length." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: currencySymbol.  The value for this property is invalid. The value must be one of the following: a string literal of appropriate length.\" was issued." );
	}

	/*
	 * s5 string{@currencySymbol{""}};
	 * 2 validation messages are expected.
	 * One message is expected to contain "Property currencySymbol must be defined with a numeric type. The type of this item is string.".
	 * One message is expected to contain "Property: currencySymbol.  The value for this property is invalid. The value must be one of the following: a string literal of appropriate length.".
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property currencySymbol must be defined with a numeric type. The type of this item is string." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property currencySymbol must be defined with a numeric type. The type of this item is string.\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "Property: currencySymbol.  The value for this property is invalid. The value must be one of the following: a string literal of appropriate length." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: currencySymbol.  The value for this property is invalid. The value must be one of the following: a string literal of appropriate length.\" was issued." );
	}

	/*
	 * s6 string{@currencySymbol{"$"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property currencySymbol must be defined with a numeric type. The type of this item is string.".
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property currencySymbol must be defined with a numeric type. The type of this item is string." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property currencySymbol must be defined with a numeric type. The type of this item is string.\" was issued." );
	}

	/*
	 * s7 string{@currencySymbol{"USD"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property currencySymbol must be defined with a numeric type. The type of this item is string.".
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property currencySymbol must be defined with a numeric type. The type of this item is string." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property currencySymbol must be defined with a numeric type. The type of this item is string.\" was issued." );
	}

	/*
	 * s8 string{@currencySymbol{"USD$"}};
	 * 2 validation messages are expected.
	 * One message is expected to contain "Property currencySymbol must be defined with a numeric type. The type of this item is string.".
	 * One message is expected to contain "The length of value USD$ for property currencySymbol exceeds the maximum length of 3.".
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property currencySymbol must be defined with a numeric type. The type of this item is string." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property currencySymbol must be defined with a numeric type. The type of this item is string.\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "The length of value USD$ for property currencySymbol exceeds the maximum length of 3." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The length of value USD$ for property currencySymbol exceeds the maximum length of 3.\" was issued." );
	}

	/*
	 * i1 int{@dateFormat};
	 * 0 validation messages are expected.
	 */
	public void testLine35() {
		List messages = getMessagesAtLine( 35 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i2 int{@dateFormat{value = "AAAA"}};
	 * 0 validation messages are expected.
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i3 int{@dateFormat{value = "MM-dd-yyyy"}};
	 * 0 validation messages are expected.
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i4 int{@dateFormat{value = "Y"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.".
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.\" was issued." );
	}

	/*
	 * s1 string{@dateFormat};
	 * 0 validation messages are expected.
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s2 string{@dateFormat{value = "AAAA"}};
	 * 0 validation messages are expected.
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s3 string{@dateFormat{value = "MM-dd-yyyy"}};
	 * 0 validation messages are expected.
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s4 string{@dateFormat{value = "Y"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.".
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.\" was issued." );
	}

	/*
	 * ls1 string(10){@dateFormat};
	 * 0 validation messages are expected.
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ls2 string(10){@dateFormat{value = "AAAA"}};
	 * 0 validation messages are expected.
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ls3 string(10){@dateFormat{value = "MM-dd-yyyy"}};
	 * 0 validation messages are expected.
	 */
	public void testLine45() {
		List messages = getMessagesAtLine( 45 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ls4 string(10){@dateFormat{value = "Y"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.".
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.\" was issued." );
	}

	/*
	 * d1 date{@dateFormat};
	 * 0 validation messages are expected.
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d2 date{@dateFormat{value = "AAAA"}};
	 * 0 validation messages are expected.
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d3 date{@dateFormat{value = "MM-dd-yyyy"}};
	 * 0 validation messages are expected.
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d4 date{@dateFormat{value = "Y"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.".
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.\" was issued." );
	}

	/*
	 * dc1 decimal(4){@dateFormat};
	 * 0 validation messages are expected.
	 */
	public void testLine51() {
		List messages = getMessagesAtLine( 51 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * dc2 decimal(4){@dateFormat{value = "AAAA"}};
	 * 0 validation messages are expected.
	 */
	public void testLine52() {
		List messages = getMessagesAtLine( 52 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * dc3 decimal(4){@dateFormat{value = "MM-dd-yyyy"}};
	 * 0 validation messages are expected.
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * dc4 decimal(4){@dateFormat{value = "Y"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.".
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.\" was issued." );
	}

	/*
	 * t1 time{@dateFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type time.".
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type time." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type time.\" was issued." );
	}

	/*
	 * t2 time{@dateFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type time.".
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type time." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type time.\" was issued." );
	}

	/*
	 * t3 time{@dateFormat{value = "MM-dd-yyyy"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type time.".
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type time." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type time.\" was issued." );
	}

	/*
	 * t4 time{@dateFormat{value = "Y"}};
	 * 2 validation messages are expected.
	 * One message is expected to contain "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.".
	 * One message is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type time.".
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type time." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type time.\" was issued." );
	}

	/*
	 * ts1 timestamp?{@dateFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type timestamp.".
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * ts2 timestamp?{@dateFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type timestamp.".
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * ts3 timestamp?{@dateFormat{value = "MM-dd-yyyy"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type timestamp.".
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * ts4 timestamp?{@dateFormat{value = "Y"}};
	 * 2 validation messages are expected.
	 * One message is expected to contain "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.".
	 * One message is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type timestamp.".
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * tsp1 timestamp("yy"){@dateFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type timestamp.".
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * tsp2 timestamp("yy"){@dateFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type timestamp.".
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * tsp3 timestamp("yy"){@dateFormat{value = "MM-dd-yyyy"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type timestamp.".
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * tsp4 timestamp("yy"){@dateFormat{value = "Y"}};
	 * 2 validation messages are expected.
	 * One message is expected to contain "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.".
	 * One message is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type timestamp.".
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * b1 boolean{@dateFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type boolean.".
	 */
	public void testLine69() {
		List messages = getMessagesAtLine( 69 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type boolean." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type boolean.\" was issued." );
	}

	/*
	 * b2 boolean{@dateFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type boolean.".
	 */
	public void testLine70() {
		List messages = getMessagesAtLine( 70 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type boolean." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type boolean.\" was issued." );
	}

	/*
	 * b3 boolean{@dateFormat{value = "MM-dd-yyyy"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type boolean.".
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type boolean." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type boolean.\" was issued." );
	}

	/*
	 * b4 boolean{@dateFormat{value = "Y"}};
	 * 2 validation messages are expected.
	 * One message is expected to contain "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.".
	 * One message is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type boolean.".
	 */
	public void testLine72() {
		List messages = getMessagesAtLine( 72 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type boolean." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type boolean.\" was issued." );
	}

	/*
	 * dcd1 decimal(4,2){@dateFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for field or items with decimals.".
	 */
	public void testLine73() {
		List messages = getMessagesAtLine( 73 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for field or items with decimals." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for field or items with decimals.\" was issued." );
	}

	/*
	 * dcd2 decimal(4,2){@dateFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for field or items with decimals.".
	 */
	public void testLine74() {
		List messages = getMessagesAtLine( 74 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for field or items with decimals." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for field or items with decimals.\" was issued." );
	}

	/*
	 * dcd3 decimal(4,2){@dateFormat{value = "MM-dd-yyyy"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for field or items with decimals.".
	 */
	public void testLine75() {
		List messages = getMessagesAtLine( 75 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for field or items with decimals." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for field or items with decimals.\" was issued." );
	}

	/*
	 * dcd4 decimal(4,2){@dateFormat{value = "Y"}};
	 * 2 validation messages are expected.
	 * One message is expected to contain "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.".
	 * One message is expected to contain "Property: dateFormat.  The dateFormat property is not valid for field or items with decimals.".
	 */
	public void testLine76() {
		List messages = getMessagesAtLine( 76 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for field or items with decimals." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for field or items with decimals.\" was issued." );
	}

	/*
	 * a1 any?{@dateFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type any.".
	 */
	public void testLine77() {
		List messages = getMessagesAtLine( 77 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type any." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type any.\" was issued." );
	}

	/*
	 * a2 any?{@dateFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type any.".
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type any." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type any.\" was issued." );
	}

	/*
	 * a3 any?{@dateFormat{value = "MM-dd-yyyy"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type any.".
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type any." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type any.\" was issued." );
	}

	/*
	 * a4 any?{@dateFormat{value = "Y"}};
	 * 2 validation messages are expected.
	 * One message is expected to contain "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.".
	 * One message is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type any.".
	 */
	public void testLine80() {
		List messages = getMessagesAtLine( 80 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type any." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type any.\" was issued." );
	}

	/*
	 * by1 bytes?{@dateFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type bytes.".
	 */
	public void testLine81() {
		List messages = getMessagesAtLine( 81 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type bytes.\" was issued." );
	}

	/*
	 * by2 bytes?{@dateFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type bytes.".
	 */
	public void testLine82() {
		List messages = getMessagesAtLine( 82 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type bytes.\" was issued." );
	}

	/*
	 * by3 bytes?{@dateFormat{value = "MM-dd-yyyy"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type bytes.".
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type bytes.\" was issued." );
	}

	/*
	 * by4 bytes?{@dateFormat{value = "Y"}};
	 * 2 validation messages are expected.
	 * One message is expected to contain "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.".
	 * One message is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type bytes.".
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type bytes.\" was issued." );
	}

	/*
	 * byl1 bytes(1)?{@dateFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type bytes.".
	 */
	public void testLine85() {
		List messages = getMessagesAtLine( 85 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type bytes.\" was issued." );
	}

	/*
	 * byl2 bytes(1)?{@dateFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type bytes.".
	 */
	public void testLine86() {
		List messages = getMessagesAtLine( 86 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type bytes.\" was issued." );
	}

	/*
	 * byl3 bytes(1)?{@dateFormat{value = "MM-dd-yyyy"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type bytes.".
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type bytes.\" was issued." );
	}

	/*
	 * byl4 bytes(1)?{@dateFormat{value = "Y"}};
	 * 2 validation messages are expected.
	 * One message is expected to contain "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.".
	 * One message is expected to contain "Property: dateFormat.  The dateFormat property is not valid for the type bytes.".
	 */
	public void testLine88() {
		List messages = getMessagesAtLine( 88 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for the type bytes.\" was issued." );
	}
}
