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
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for types with decimals.".
	 */
	public void testLine73() {
		List messages = getMessagesAtLine( 73 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for types with decimals." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for types with decimals.\" was issued." );
	}

	/*
	 * dcd2 decimal(4,2){@dateFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for types with decimals.".
	 */
	public void testLine74() {
		List messages = getMessagesAtLine( 74 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for types with decimals." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for types with decimals.\" was issued." );
	}

	/*
	 * dcd3 decimal(4,2){@dateFormat{value = "MM-dd-yyyy"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: dateFormat.  The dateFormat property is not valid for types with decimals.".
	 */
	public void testLine75() {
		List messages = getMessagesAtLine( 75 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for types with decimals." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for types with decimals.\" was issued." );
	}

	/*
	 * dcd4 decimal(4,2){@dateFormat{value = "Y"}};
	 * 2 validation messages are expected.
	 * One message is expected to contain "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.".
	 * One message is expected to contain "Property: dateFormat.  The dateFormat property is not valid for types with decimals.".
	 */
	public void testLine76() {
		List messages = getMessagesAtLine( 76 );
		assertEquals( 2, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The character 'Y' is not a valid character in the dateFormat property value.\" was issued." );
		
		messages.remove( messageWithSubstring );
		messageWithSubstring = messageWithSubstring( messages, "Property: dateFormat.  The dateFormat property is not valid for types with decimals." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: dateFormat.  The dateFormat property is not valid for types with decimals.\" was issued." );
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

	/*
	 * i1 int{@timeFormat};
	 * 0 validation messages are expected.
	 */
	public void testLine93() {
		List messages = getMessagesAtLine( 93 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i2 int{@timeFormat{value = "AAAA"}};
	 * 0 validation messages are expected.
	 */
	public void testLine94() {
		List messages = getMessagesAtLine( 94 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i3 int{@timeFormat{value = "HH:mm:ss"}};
	 * 0 validation messages are expected.
	 */
	public void testLine95() {
		List messages = getMessagesAtLine( 95 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s1 string{@timeFormat};
	 * 0 validation messages are expected.
	 */
	public void testLine96() {
		List messages = getMessagesAtLine( 96 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s2 string{@timeFormat{value = "AAAA"}};
	 * 0 validation messages are expected.
	 */
	public void testLine97() {
		List messages = getMessagesAtLine( 97 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s3 string{@timeFormat{value = "HH:mm:ss"}};
	 * 0 validation messages are expected.
	 */
	public void testLine98() {
		List messages = getMessagesAtLine( 98 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ls1 string(10){@timeFormat};
	 * 0 validation messages are expected.
	 */
	public void testLine99() {
		List messages = getMessagesAtLine( 99 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ls2 string(10){@timeFormat{value = "AAAA"}};
	 * 0 validation messages are expected.
	 */
	public void testLine100() {
		List messages = getMessagesAtLine( 100 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ls3 string(10){@timeFormat{value = "HH:mm:ss"}};
	 * 0 validation messages are expected.
	 */
	public void testLine101() {
		List messages = getMessagesAtLine( 101 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * t1 time{@timeFormat};
	 * 0 validation messages are expected.
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * t2 time{@timeFormat{value = "AAAA"}};
	 * 0 validation messages are expected.
	 */
	public void testLine103() {
		List messages = getMessagesAtLine( 103 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * t3 time{@timeFormat{value = "HH:mm:ss"}};
	 * 0 validation messages are expected.
	 */
	public void testLine104() {
		List messages = getMessagesAtLine( 104 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * dc1 decimal(4){@timeFormat};
	 * 0 validation messages are expected.
	 */
	public void testLine105() {
		List messages = getMessagesAtLine( 105 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * dc2 decimal(4){@timeFormat{value = "AAAA"}};
	 * 0 validation messages are expected.
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * dc3 decimal(4){@timeFormat{value = "HH:mm:ss"}};
	 * 0 validation messages are expected.
	 */
	public void testLine107() {
		List messages = getMessagesAtLine( 107 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d1 date{@timeFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type date.".
	 */
	public void testLine110() {
		List messages = getMessagesAtLine( 110 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type date." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type date.\" was issued." );
	}

	/*
	 * d2 date{@timeFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type date.".
	 */
	public void testLine111() {
		List messages = getMessagesAtLine( 111 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type date." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type date.\" was issued." );
	}

	/*
	 * d3 date{@timeFormat{value = "HH:mm:ss"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type date.".
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type date." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type date.\" was issued." );
	}

	/*
	 * ts1 timestamp?{@timeFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type timestamp.".
	 */
	public void testLine113() {
		List messages = getMessagesAtLine( 113 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * ts2 timestamp?{@timeFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type timestamp.".
	 */
	public void testLine114() {
		List messages = getMessagesAtLine( 114 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * ts3 timestamp?{@timeFormat{value = "HH:mm:ss"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type timestamp.".
	 */
	public void testLine115() {
		List messages = getMessagesAtLine( 115 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * tsp1 timestamp("yy"){@timeFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type timestamp.".
	 */
	public void testLine116() {
		List messages = getMessagesAtLine( 116 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * tsp2 timestamp("yy"){@timeFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type timestamp.".
	 */
	public void testLine117() {
		List messages = getMessagesAtLine( 117 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * tsp3 timestamp("yy"){@timeFormat{value = "HH:mm:ss"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type timestamp.".
	 */
	public void testLine118() {
		List messages = getMessagesAtLine( 118 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * b1 boolean{@timeFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type boolean.".
	 */
	public void testLine119() {
		List messages = getMessagesAtLine( 119 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type boolean." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type boolean.\" was issued." );
	}

	/*
	 * b2 boolean{@timeFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type boolean.".
	 */
	public void testLine120() {
		List messages = getMessagesAtLine( 120 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type boolean." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type boolean.\" was issued." );
	}

	/*
	 * b3 boolean{@timeFormat{value = "HH:mm:ss"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type boolean.".
	 */
	public void testLine121() {
		List messages = getMessagesAtLine( 121 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type boolean." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type boolean.\" was issued." );
	}

	/*
	 * dcd1 decimal(4,2){@timeFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for types with decimals.".
	 */
	public void testLine122() {
		List messages = getMessagesAtLine( 122 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for types with decimals." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for types with decimals.\" was issued." );
	}

	/*
	 * dcd2 decimal(4,2){@timeFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for types with decimals.".
	 */
	public void testLine123() {
		List messages = getMessagesAtLine( 123 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for types with decimals." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for types with decimals.\" was issued." );
	}

	/*
	 * dcd3 decimal(4,2){@timeFormat{value = "HH:mm:ss"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for types with decimals.".
	 */
	public void testLine124() {
		List messages = getMessagesAtLine( 124 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for types with decimals." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for types with decimals.\" was issued." );
	}

	/*
	 * a1 any?{@timeFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type any.".
	 */
	public void testLine125() {
		List messages = getMessagesAtLine( 125 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type any." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type any.\" was issued." );
	}

	/*
	 * a2 any?{@timeFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type any.".
	 */
	public void testLine126() {
		List messages = getMessagesAtLine( 126 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type any." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type any.\" was issued." );
	}

	/*
	 * a3 any?{@timeFormat{value = "HH:mm:ss"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type any.".
	 */
	public void testLine127() {
		List messages = getMessagesAtLine( 127 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type any." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type any.\" was issued." );
	}

	/*
	 * by1 bytes?{@timeFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type bytes.".
	 */
	public void testLine128() {
		List messages = getMessagesAtLine( 128 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type bytes.\" was issued." );
	}

	/*
	 * by2 bytes?{@timeFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type bytes.".
	 */
	public void testLine129() {
		List messages = getMessagesAtLine( 129 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type bytes.\" was issued." );
	}

	/*
	 * by3 bytes?{@timeFormat{value = "HH:mm:ss"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type bytes.".
	 */
	public void testLine130() {
		List messages = getMessagesAtLine( 130 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type bytes.\" was issued." );
	}

	/*
	 * byl1 bytes(1)?{@timeFormat};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type bytes.".
	 */
	public void testLine131() {
		List messages = getMessagesAtLine( 131 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type bytes.\" was issued." );
	}

	/*
	 * byl2 bytes(1)?{@timeFormat{value = "AAAA"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type bytes.".
	 */
	public void testLine132() {
		List messages = getMessagesAtLine( 132 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type bytes.\" was issued." );
	}

	/*
	 * byl3 bytes(1)?{@timeFormat{value = "HH:mm:ss"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: timeFormat.  The timeFormat property is not valid for the type bytes.".
	 */
	public void testLine133() {
		List messages = getMessagesAtLine( 133 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: timeFormat.  The timeFormat property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: timeFormat.  The timeFormat property is not valid for the type bytes.\" was issued." );
	}

	/*
	 * i1 int{@fillCharacter};
	 * 0 validation messages are expected.
	 */
	public void testLine137() {
		List messages = getMessagesAtLine( 137 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i2 int{@fillCharacter{"x"}};
	 * 0 validation messages are expected.
	 */
	public void testLine138() {
		List messages = getMessagesAtLine( 138 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i3 int{@fillCharacter{"xyz"}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: fillCharacter.  The value for this property is invalid. The value must be a single character string literal.".
	 */
	public void testLine139() {
		List messages = getMessagesAtLine( 139 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: fillCharacter.  The value for this property is invalid. The value must be a single character string literal." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: fillCharacter.  The value for this property is invalid. The value must be a single character string literal.\" was issued." );
	}

	/*
	 * i4 int{@fillCharacter{1}};
	 * 1 validation message is expected.
	 * It is expected to contain "The value specified for the annotation field or literal array entry value must be a quoted string or a string constant.".
	 */
	public void testLine140() {
		List messages = getMessagesAtLine( 140 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value specified for the annotation field or literal array entry value must be a quoted string or a string constant." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value specified for the annotation field or literal array entry value must be a quoted string or a string constant.\" was issued." );
	}

	/*
	 * i1 int{@MinimumInput};
	 * 0 validation messages are expected.
	 */
	public void testLine144() {
		List messages = getMessagesAtLine( 144 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i2 int{@MinimumInput{"abc"}};
	 * 1 validation message is expected.
	 * It is expected to contain "The value specified for the annotation field or literal array entry value must be an integer literal or an integer constant.".
	 */
	public void testLine145() {
		List messages = getMessagesAtLine( 145 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value specified for the annotation field or literal array entry value must be an integer literal or an integer constant." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value specified for the annotation field or literal array entry value must be an integer literal or an integer constant.\" was issued." );
	}

	/*
	 * i3 int{@MinimumInput{123}};
	 * 0 validation messages are expected.
	 */
	public void testLine146() {
		List messages = getMessagesAtLine( 146 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i4 int{@MinimumInput{-52}};
	 * 1 validation message is expected.
	 * It is expected to contain "The value of property minimumInput must be greater than 0, but -52 was found.".
	 */
	public void testLine147() {
		List messages = getMessagesAtLine( 147 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value of property minimumInput must be greater than 0, but -52 was found." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value of property minimumInput must be greater than 0, but -52 was found.\" was issued." );
	}

	/*
	 * i5 int{@MinimumInput{0}};
	 * 0 validation messages are expected.
	 */
	public void testLine148() {
		List messages = getMessagesAtLine( 148 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i6 int{@MinimumInput{10.5}};
	 * 1 validation message is expected.
	 * It is expected to contain "The value specified for the annotation field or literal array entry value must be an integer literal or an integer constant.".
	 */
	public void testLine149() {
		List messages = getMessagesAtLine( 149 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value specified for the annotation field or literal array entry value must be an integer literal or an integer constant." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value specified for the annotation field or literal array entry value must be an integer literal or an integer constant.\" was issued." );
	}

	/*
	 * i1 int{@ValidatorFunction{valid1}};
	 * 0 validation messages are expected.
	 */
	public void testLine153() {
		List messages = getMessagesAtLine( 153 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i2 int{@ValidatorFunction{value = valid2}};
	 * 0 validation messages are expected.
	 */
	public void testLine154() {
		List messages = getMessagesAtLine( 154 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i3 int{@ValidatorFunction{invalid}};
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid value invalid for validatorFunction. Validator functions must have no parameters.".
	 */
	public void testLine155() {
		List messages = getMessagesAtLine( 155 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid value invalid for validatorFunction. Validator functions must have no parameters." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid value invalid for validatorFunction. Validator functions must have no parameters.\" was issued." );
	}

	/*
	 * i1 int{@ValidationPropertiesLibrary{valPropLib}};
	 * 0 validation messages are expected.
	 */
	public void testLine162() {
		List messages = getMessagesAtLine( 162 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i2 int{@ValidationPropertiesLibrary{basicLib}};
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid value basicLib for validationPropertiesLibrary. The value must be a RUIPropertiesLibrary.".
	 */
	public void testLine163() {
		List messages = getMessagesAtLine( 163 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid value basicLib for validationPropertiesLibrary. The value must be a RUIPropertiesLibrary." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid value basicLib for validationPropertiesLibrary. The value must be a RUIPropertiesLibrary.\" was issued." );
	}

	/*
	 * i3 int{@ValidationPropertiesLibrary{notALibrary}};
	 * 1 validation message is expected.
	 * It is expected to contain "Invalid value notALibrary for validationPropertiesLibrary. The value must be a RUIPropertiesLibrary.".
	 */
	public void testLine164() {
		List messages = getMessagesAtLine( 164 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Invalid value notALibrary for validationPropertiesLibrary. The value must be a RUIPropertiesLibrary." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Invalid value notALibrary for validationPropertiesLibrary. The value must be a RUIPropertiesLibrary.\" was issued." );
	}

	/*
	 * i1 int{@sign{SignKind.leading}};
	 * 0 validation messages are expected.
	 */
	public void testLine173() {
		List messages = getMessagesAtLine( 173 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i2 int{@sign{SignKind.none}};
	 * 0 validation messages are expected.
	 */
	public void testLine174() {
		List messages = getMessagesAtLine( 174 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i3 int{@sign{SignKind.trailing}};
	 * 0 validation messages are expected.
	 */
	public void testLine175() {
		List messages = getMessagesAtLine( 175 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i4 int{@sign{SignKind.parens}};
	 * 0 validation messages are expected.
	 */
	public void testLine176() {
		List messages = getMessagesAtLine( 176 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si1 smallint{@sign{SignKind.leading}};
	 * 0 validation messages are expected.
	 */
	public void testLine177() {
		List messages = getMessagesAtLine( 177 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si2 smallint{@sign{SignKind.none}};
	 * 0 validation messages are expected.
	 */
	public void testLine178() {
		List messages = getMessagesAtLine( 178 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si3 smallint{@sign{SignKind.trailing}};
	 * 0 validation messages are expected.
	 */
	public void testLine179() {
		List messages = getMessagesAtLine( 179 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si4 smallint{@sign{SignKind.parens}};
	 * 0 validation messages are expected.
	 */
	public void testLine180() {
		List messages = getMessagesAtLine( 180 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * bi1 bigint{@sign{SignKind.leading}};
	 * 0 validation messages are expected.
	 */
	public void testLine181() {
		List messages = getMessagesAtLine( 181 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * bi2 bigint{@sign{SignKind.none}};
	 * 0 validation messages are expected.
	 */
	public void testLine182() {
		List messages = getMessagesAtLine( 182 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * bi3 bigint{@sign{SignKind.trailing}};
	 * 0 validation messages are expected.
	 */
	public void testLine183() {
		List messages = getMessagesAtLine( 183 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * bi4 bigint{@sign{SignKind.parens}};
	 * 0 validation messages are expected.
	 */
	public void testLine184() {
		List messages = getMessagesAtLine( 184 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * de1 decimal(4){@sign{SignKind.leading}};
	 * 0 validation messages are expected.
	 */
	public void testLine185() {
		List messages = getMessagesAtLine( 185 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * de2 decimal(4){@sign{SignKind.none}};
	 * 0 validation messages are expected.
	 */
	public void testLine186() {
		List messages = getMessagesAtLine( 186 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * de3 decimal(4){@sign{SignKind.trailing}};
	 * 0 validation messages are expected.
	 */
	public void testLine187() {
		List messages = getMessagesAtLine( 187 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * de4 decimal(4){@sign{SignKind.parens}};
	 * 0 validation messages are expected.
	 */
	public void testLine188() {
		List messages = getMessagesAtLine( 188 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f1 float{@sign{SignKind.leading}};
	 * 0 validation messages are expected.
	 */
	public void testLine190() {
		List messages = getMessagesAtLine( 190 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f2 float{@sign{SignKind.none}};
	 * 0 validation messages are expected.
	 */
	public void testLine191() {
		List messages = getMessagesAtLine( 191 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f3 float{@sign{SignKind.trailing}};
	 * 1 validation message is expected.
	 * It is expected to contain "The value trailing for property sign is incompatible with the type float.".
	 */
	public void testLine192() {
		List messages = getMessagesAtLine( 192 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value trailing for property sign is incompatible with the type float." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value trailing for property sign is incompatible with the type float.\" was issued." );
	}

	/*
	 * f4 float{@sign{SignKind.parens}};
	 * 0 validation messages are expected.
	 */
	public void testLine193() {
		List messages = getMessagesAtLine( 193 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * sf1 smallfloat{@sign{SignKind.leading}};
	 * 0 validation messages are expected.
	 */
	public void testLine194() {
		List messages = getMessagesAtLine( 194 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * sf2 smallfloat{@sign{SignKind.none}};
	 * 0 validation messages are expected.
	 */
	public void testLine195() {
		List messages = getMessagesAtLine( 195 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * sf3 smallfloat{@sign{SignKind.trailing}};
	 * 1 validation message is expected.
	 * It is expected to contain "The value trailing for property sign is incompatible with the type smallfloat.".
	 */
	public void testLine196() {
		List messages = getMessagesAtLine( 196 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The value trailing for property sign is incompatible with the type smallfloat." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The value trailing for property sign is incompatible with the type smallfloat.\" was issued." );
	}

	/*
	 * sf4 smallfloat{@sign{SignKind.parens}};
	 * 0 validation messages are expected.
	 */
	public void testLine197() {
		List messages = getMessagesAtLine( 197 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s1 string{@sign{SignKind.leading}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property sign must be defined with a numeric type. The type of this item is string.".
	 */
	public void testLine198() {
		List messages = getMessagesAtLine( 198 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property sign must be defined with a numeric type. The type of this item is string." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property sign must be defined with a numeric type. The type of this item is string.\" was issued." );
	}

	/*
	 * b1 boolean{@sign{SignKind.leading}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property sign must be defined with a numeric type. The type of this item is boolean.".
	 */
	public void testLine199() {
		List messages = getMessagesAtLine( 199 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property sign must be defined with a numeric type. The type of this item is boolean." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property sign must be defined with a numeric type. The type of this item is boolean.\" was issued." );
	}

	/*
	 * d1 date{@sign{SignKind.leading}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property sign must be defined with a numeric type. The type of this item is date.".
	 */
	public void testLine200() {
		List messages = getMessagesAtLine( 200 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property sign must be defined with a numeric type. The type of this item is date." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property sign must be defined with a numeric type. The type of this item is date.\" was issued." );
	}

	/*
	 * t1 time{@sign{SignKind.leading}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property sign must be defined with a numeric type. The type of this item is time.".
	 */
	public void testLine201() {
		List messages = getMessagesAtLine( 201 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property sign must be defined with a numeric type. The type of this item is time." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property sign must be defined with a numeric type. The type of this item is time.\" was issued." );
	}

	/*
	 * ts1 timestamp?{@sign{SignKind.leading}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property sign must be defined with a numeric type. The type of this item is timestamp.".
	 */
	public void testLine202() {
		List messages = getMessagesAtLine( 202 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property sign must be defined with a numeric type. The type of this item is timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property sign must be defined with a numeric type. The type of this item is timestamp.\" was issued." );
	}

	/*
	 * by1 bytes?{@sign{SignKind.leading}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property sign must be defined with a numeric type. The type of this item is bytes.".
	 */
	public void testLine203() {
		List messages = getMessagesAtLine( 203 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property sign must be defined with a numeric type. The type of this item is bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property sign must be defined with a numeric type. The type of this item is bytes.\" was issued." );
	}

	/*
	 * a1 any?{@sign{SignKind.leading}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property sign must be defined with a numeric type. The type of this item is any.".
	 */
	public void testLine204() {
		List messages = getMessagesAtLine( 204 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property sign must be defined with a numeric type. The type of this item is any." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property sign must be defined with a numeric type. The type of this item is any.\" was issued." );
	}

	/*
	 * r1 signrec{@sign{SignKind.leading}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property sign must be defined with a numeric type. The type of this item is signRec.".
	 */
	public void testLine205() {
		List messages = getMessagesAtLine( 205 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property sign must be defined with a numeric type. The type of this item is signRec." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property sign must be defined with a numeric type. The type of this item is signRec.\" was issued." );
	}

	/*
	 * i int{@isboolean{yes}};
	 * 0 validation messages are expected.
	 */
	public void testLine210() {
		List messages = getMessagesAtLine( 210 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si smallint{@isboolean{yes}};
	 * 0 validation messages are expected.
	 */
	public void testLine211() {
		List messages = getMessagesAtLine( 211 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * bi bigint{@isboolean{yes}};
	 * 0 validation messages are expected.
	 */
	public void testLine212() {
		List messages = getMessagesAtLine( 212 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f float{@isboolean{yes}};
	 * 0 validation messages are expected.
	 */
	public void testLine213() {
		List messages = getMessagesAtLine( 213 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * de1 decimal{@isboolean{yes}};
	 * 0 validation messages are expected.
	 */
	public void testLine214() {
		List messages = getMessagesAtLine( 214 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * de2 decimal(10,2){@isboolean{yes}};
	 * 0 validation messages are expected.
	 */
	public void testLine215() {
		List messages = getMessagesAtLine( 215 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * de3 decimal(10,10){@isboolean{yes}};
	 * 1 validation message is expected.
	 * It is expected to contain "The property isBoolean is invalid for type decimal(10,10), which has no non-decimal digits.".
	 */
	public void testLine216() {
		List messages = getMessagesAtLine( 216 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The property isBoolean is invalid for type decimal(10,10), which has no non-decimal digits." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The property isBoolean is invalid for type decimal(10,10), which has no non-decimal digits.\" was issued." );
	}

	/*
	 * sf smallfloat{@isboolean{yes}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: isBoolean.  The isBoolean property is not valid for the type smallfloat.".
	 */
	public void testLine217() {
		List messages = getMessagesAtLine( 217 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: isBoolean.  The isBoolean property is not valid for the type smallfloat." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: isBoolean.  The isBoolean property is not valid for the type smallfloat.\" was issued." );
	}

	/*
	 * s string{@isboolean{yes}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: isBoolean.  The isBoolean property is not valid for the type string.".
	 */
	public void testLine218() {
		List messages = getMessagesAtLine( 218 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: isBoolean.  The isBoolean property is not valid for the type string." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: isBoolean.  The isBoolean property is not valid for the type string.\" was issued." );
	}

	/*
	 * ls string(10){@isboolean{yes}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: isBoolean.  The isBoolean property is not valid for the type string(10).".
	 */
	public void testLine219() {
		List messages = getMessagesAtLine( 219 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: isBoolean.  The isBoolean property is not valid for the type string(10)." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: isBoolean.  The isBoolean property is not valid for the type string(10).\" was issued." );
	}

	/*
	 * t time{@isboolean{yes}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: isBoolean.  The isBoolean property is not valid for the type time.".
	 */
	public void testLine220() {
		List messages = getMessagesAtLine( 220 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: isBoolean.  The isBoolean property is not valid for the type time." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: isBoolean.  The isBoolean property is not valid for the type time.\" was issued." );
	}

	/*
	 * ts1 timestamp?{@isboolean{yes}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: isBoolean.  The isBoolean property is not valid for the type timestamp.".
	 */
	public void testLine221() {
		List messages = getMessagesAtLine( 221 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: isBoolean.  The isBoolean property is not valid for the type timestamp." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: isBoolean.  The isBoolean property is not valid for the type timestamp.\" was issued." );
	}

	/*
	 * ts2 timestamp("yyyy"){@isboolean{yes}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: isBoolean.  The isBoolean property is not valid for the type timestamp(\"yyyy\").".
	 */
	public void testLine222() {
		List messages = getMessagesAtLine( 222 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: isBoolean.  The isBoolean property is not valid for the type timestamp(\"yyyy\")." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: isBoolean.  The isBoolean property is not valid for the type timestamp(\"yyyy\").\" was issued." );
	}

	/*
	 * d date{@isboolean{yes}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: isBoolean.  The isBoolean property is not valid for the type date.".
	 */
	public void testLine223() {
		List messages = getMessagesAtLine( 223 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: isBoolean.  The isBoolean property is not valid for the type date." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: isBoolean.  The isBoolean property is not valid for the type date.\" was issued." );
	}

	/*
	 * b boolean{@isboolean{yes}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: isBoolean.  The isBoolean property is not valid for the type boolean.".
	 */
	public void testLine224() {
		List messages = getMessagesAtLine( 224 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: isBoolean.  The isBoolean property is not valid for the type boolean." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: isBoolean.  The isBoolean property is not valid for the type boolean.\" was issued." );
	}

	/*
	 * byt bytes{@isboolean{yes}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: isBoolean.  The isBoolean property is not valid for the type bytes.".
	 */
	public void testLine225() {
		List messages = getMessagesAtLine( 225 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: isBoolean.  The isBoolean property is not valid for the type bytes." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: isBoolean.  The isBoolean property is not valid for the type bytes.\" was issued." );
	}

	/*
	 * n number{@isboolean{yes}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: isBoolean.  The isBoolean property is not valid for the type number.".
	 */
	public void testLine226() {
		List messages = getMessagesAtLine( 226 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: isBoolean.  The isBoolean property is not valid for the type number." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: isBoolean.  The isBoolean property is not valid for the type number.\" was issued." );
	}

	/*
	 * rec boolrec{@isboolean{yes}};
	 * 1 validation message is expected.
	 * It is expected to contain "Property: isBoolean.  The isBoolean property is not valid for the type boolrec.".
	 */
	public void testLine227() {
		List messages = getMessagesAtLine( 227 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Property: isBoolean.  The isBoolean property is not valid for the type boolrec." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Property: isBoolean.  The isBoolean property is not valid for the type boolrec.\" was issued." );
	}

	/*
	 * @MVCView{publishHelper = pub0},
	 * 0 validation messages are expected.
	 */
	public void testLine232() {
		List messages = getMessagesAtLine( 232 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * @MVCView{publishHelper = pub1},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub1 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine233() {
		List messages = getMessagesAtLine( 233 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub1 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub1 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishHelper = pub2},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub2 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine234() {
		List messages = getMessagesAtLine( 234 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub2 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub2 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishHelper = pub3},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub3 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine235() {
		List messages = getMessagesAtLine( 235 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub3 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub3 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishHelper = pub4},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub4 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine236() {
		List messages = getMessagesAtLine( 236 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub4 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub4 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishHelper = pub5},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub5 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine237() {
		List messages = getMessagesAtLine( 237 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub5 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub5 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishHelper = pub6},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub6 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine238() {
		List messages = getMessagesAtLine( 238 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub6 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub6 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishHelper = pub7},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub7 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine239() {
		List messages = getMessagesAtLine( 239 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub7 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub7 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishHelper = pub8},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub8 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine240() {
		List messages = getMessagesAtLine( 240 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub8 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub8 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishHelper = pub9},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub9 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine241() {
		List messages = getMessagesAtLine( 241 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub9 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub9 specified for the publishHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishMessageHelper = pub0},
	 * 0 validation messages are expected.
	 */
	public void testLine243() {
		List messages = getMessagesAtLine( 243 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * @MVCView{publishMessageHelper = pub1},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub1 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine244() {
		List messages = getMessagesAtLine( 244 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub1 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub1 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishMessageHelper = pub2},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub2 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine245() {
		List messages = getMessagesAtLine( 245 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub2 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub2 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishMessageHelper = pub3},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub3 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine246() {
		List messages = getMessagesAtLine( 246 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub3 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub3 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishMessageHelper = pub4},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub4 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine247() {
		List messages = getMessagesAtLine( 247 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub4 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub4 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishMessageHelper = pub5},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub5 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine248() {
		List messages = getMessagesAtLine( 248 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub5 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub5 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishMessageHelper = pub6},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub6 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine249() {
		List messages = getMessagesAtLine( 249 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub6 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub6 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishMessageHelper = pub7},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub7 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine250() {
		List messages = getMessagesAtLine( 250 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub7 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub7 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishMessageHelper = pub8},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub8 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine251() {
		List messages = getMessagesAtLine( 251 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub8 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub8 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{publishMessageHelper = pub9},
	 * 1 validation message is expected.
	 * It is expected to contain "The function pub9 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.".
	 */
	public void testLine252() {
		List messages = getMessagesAtLine( 252 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function pub9 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function pub9 specified for the publishMessageHelper property is invalid. It must be defined with a single IN parameter of type String, with no return type.\" was issued." );
	}

	/*
	 * @MVCView{retrieveValidStateHelper = retState0},
	 * 0 validation messages are expected.
	 */
	public void testLine254() {
		List messages = getMessagesAtLine( 254 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * @MVCView{retrieveValidStateHelper = retState1},
	 * 1 validation message is expected.
	 * It is expected to contain "The function retState1 specified for the retrieveValidStateHelper property is invalid. It must be defined with no parameters and a return type of String?.".
	 */
	public void testLine255() {
		List messages = getMessagesAtLine( 255 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function retState1 specified for the retrieveValidStateHelper property is invalid. It must be defined with no parameters and a return type of String?." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function retState1 specified for the retrieveValidStateHelper property is invalid. It must be defined with no parameters and a return type of String?.\" was issued." );
	}

	/*
	 * @MVCView{retrieveValidStateHelper = retState2},
	 * 1 validation message is expected.
	 * It is expected to contain "The function retState2 specified for the retrieveValidStateHelper property is invalid. It must be defined with no parameters and a return type of String?.".
	 */
	public void testLine256() {
		List messages = getMessagesAtLine( 256 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function retState2 specified for the retrieveValidStateHelper property is invalid. It must be defined with no parameters and a return type of String?." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function retState2 specified for the retrieveValidStateHelper property is invalid. It must be defined with no parameters and a return type of String?.\" was issued." );
	}

	/*
	 * @MVCView{retrieveValidStateHelper = retState3},
	 * 1 validation message is expected.
	 * It is expected to contain "The function retState3 specified for the retrieveValidStateHelper property is invalid. It must be defined with no parameters and a return type of String?.".
	 */
	public void testLine257() {
		List messages = getMessagesAtLine( 257 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function retState3 specified for the retrieveValidStateHelper property is invalid. It must be defined with no parameters and a return type of String?." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function retState3 specified for the retrieveValidStateHelper property is invalid. It must be defined with no parameters and a return type of String?.\" was issued." );
	}

	/*
	 * @MVCView{retrieveValidStateHelper = retState4},
	 * 1 validation message is expected.
	 * It is expected to contain "The function retState4 specified for the retrieveValidStateHelper property is invalid. It must be defined with no parameters and a return type of String?.".
	 */
	public void testLine258() {
		List messages = getMessagesAtLine( 258 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function retState4 specified for the retrieveValidStateHelper property is invalid. It must be defined with no parameters and a return type of String?." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function retState4 specified for the retrieveValidStateHelper property is invalid. It must be defined with no parameters and a return type of String?.\" was issued." );
	}

	/*
	 * @MVCView{retrieveViewHelper = retView0},
	 * 0 validation messages are expected.
	 */
	public void testLine260() {
		List messages = getMessagesAtLine( 260 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * @MVCView{retrieveViewHelper = retView1},
	 * 1 validation message is expected.
	 * It is expected to contain "The function retView1 specified for the retrieveViewHelper property is invalid. It must be defined with no parameters and a return type of String.".
	 */
	public void testLine261() {
		List messages = getMessagesAtLine( 261 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function retView1 specified for the retrieveViewHelper property is invalid. It must be defined with no parameters and a return type of String." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function retView1 specified for the retrieveViewHelper property is invalid. It must be defined with no parameters and a return type of String.\" was issued." );
	}

	/*
	 * @MVCView{retrieveViewHelper = retView2},
	 * 1 validation message is expected.
	 * It is expected to contain "The function retView2 specified for the retrieveViewHelper property is invalid. It must be defined with no parameters and a return type of String.".
	 */
	public void testLine262() {
		List messages = getMessagesAtLine( 262 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function retView2 specified for the retrieveViewHelper property is invalid. It must be defined with no parameters and a return type of String." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function retView2 specified for the retrieveViewHelper property is invalid. It must be defined with no parameters and a return type of String.\" was issued." );
	}

	/*
	 * @MVCView{retrieveViewHelper = retView3},
	 * 1 validation message is expected.
	 * It is expected to contain "The function retView3 specified for the retrieveViewHelper property is invalid. It must be defined with no parameters and a return type of String.".
	 */
	public void testLine263() {
		List messages = getMessagesAtLine( 263 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function retView3 specified for the retrieveViewHelper property is invalid. It must be defined with no parameters and a return type of String." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function retView3 specified for the retrieveViewHelper property is invalid. It must be defined with no parameters and a return type of String.\" was issued." );
	}

	/*
	 * @MVCView{retrieveViewHelper = retView4},
	 * 1 validation message is expected.
	 * It is expected to contain "The function retView4 specified for the retrieveViewHelper property is invalid. It must be defined with no parameters and a return type of String.".
	 */
	public void testLine264() {
		List messages = getMessagesAtLine( 264 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The function retView4 specified for the retrieveViewHelper property is invalid. It must be defined with no parameters and a return type of String." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The function retView4 specified for the retrieveViewHelper property is invalid. It must be defined with no parameters and a return type of String.\" was issued." );
	}
}
