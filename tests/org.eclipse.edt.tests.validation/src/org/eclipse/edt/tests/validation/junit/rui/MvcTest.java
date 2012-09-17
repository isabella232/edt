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
}
