package org.eclipse.edt.tests.validation.junit.expressions;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/expressions/static.egl
 */
public class StaticTest extends ValidationTestCase {

	public StaticTest() {
		super( "EGLSource/expressions/static.egl", false );
	}

	/*
	 * static i1 int;
	 * 0 validation messages are expected.
	 */
	public void testLine14() {
		List messages = getMessagesAtLine( 14 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * static i2 int = i1;
	 * 0 validation messages are expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * static i3 int = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * static i4 int = 10 * i2;
	 * 0 validation messages are expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * static i5 int = func1();
	 * 0 validation messages are expected.
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * static i6 int = i5 + func1();
	 * 0 validation messages are expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * static i7 int = (new class2).j1;
	 * 0 validation messages are expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * static s2 string = s;
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot make a static reference to non-static field s.".
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot make a static reference to non-static field s." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot make a static reference to non-static field s.\" was issued." );
	}

	/*
	 * static s3 string = func();
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot make a static reference to non-static function func().".
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot make a static reference to non-static function func()." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot make a static reference to non-static function func().\" was issued." );
	}

	/*
	 * static s4 string = "abc" + s;
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot make a static reference to non-static field s.".
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot make a static reference to non-static field s." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot make a static reference to non-static field s.\" was issued." );
	}

	/*
	 * static c1 class2;
	 * 0 validation messages are expected.
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * static j1 int = c1.j1;
	 * 0 validation messages are expected.
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * static j2 int = c2.j1;
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot make a static reference to non-static field c2.".
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot make a static reference to non-static field c2." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot make a static reference to non-static field c2.\" was issued." );
	}

	/*
	 * static j3 int = class2.j2;
	 * 0 validation messages are expected.
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * j4 int = class2.j2;
	 * 0 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * static j5 int = class2.j1;
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot make a static reference to non-static field j1.".
	 */
	public void testLine35() {
		List messages = getMessagesAtLine( 35 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot make a static reference to non-static field j1." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot make a static reference to non-static field j1.\" was issued." );
	}

	/*
	 * j6 int = class2.j1;
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot make a static reference to non-static field j1.".
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot make a static reference to non-static field j1." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot make a static reference to non-static field j1.\" was issued." );
	}

	/*
	 * j7 int = c1.foo();
	 * 0 validation messages are expected.
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * static j8 int = c1.foo();
	 * 0 validation messages are expected.
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * static j9 int = c2.foo();
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot make a static reference to non-static field c2.".
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot make a static reference to non-static field c2." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot make a static reference to non-static field c2.\" was issued." );
	}

	/*
	 * static j10 int = c1.foo2();
	 * 0 validation messages are expected.
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * static j11 int = c2.foo2();
	 * 0 validation messages are expected.
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * j12 int = class2.foo2();
	 * 0 validation messages are expected.
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * j13 int = class2.foo();
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot make a static reference to non-static function foo().".
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot make a static reference to non-static function foo()." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot make a static reference to non-static function foo().\" was issued." );
	}

	/*
	 * static j14 int = func2().j1;
	 * 0 validation messages are expected.
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * static j15 int = func3().j1;
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot make a static reference to non-static function func3().".
	 */
	public void testLine45() {
		List messages = getMessagesAtLine( 45 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot make a static reference to non-static function func3()." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot make a static reference to non-static function func3().\" was issued." );
	}

	/*
	 * staticField = new class2;
	 * 0 validation messages are expected.
	 */
	public void testLine70() {
		List messages = getMessagesAtLine( 70 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * local1 = "abc";
	 * 0 validation messages are expected.
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * local2.j1 = parm1;
	 * 0 validation messages are expected.
	 */
	public void testLine72() {
		List messages = getMessagesAtLine( 72 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * local2.j1 = staticFunc3().j1;
	 * 0 validation messages are expected.
	 */
	public void testLine76() {
		List messages = getMessagesAtLine( 76 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * local2.j1 = class2.j2;
	 * 0 validation messages are expected.
	 */
	public void testLine77() {
		List messages = getMessagesAtLine( 77 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * lib1.field1 = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine80() {
		List messages = getMessagesAtLine( 80 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * lib1.foo();
	 * 0 validation messages are expected.
	 */
	public void testLine81() {
		List messages = getMessagesAtLine( 81 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * for(x int from 1 to 2) SysLib.writestdout(x); end
	 * 0 validation messages are expected.
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * try onException(ex AnyException) SysLib.writestderr(ex.message); end
	 * 0 validation messages are expected.
	 */
	public void testLine85() {
		List messages = getMessagesAtLine( 85 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * while(true) x int = 10; x = x * 2; SysLib.writestdout(x); end
	 * 0 validation messages are expected.
	 */
	public void testLine86() {
		List messages = getMessagesAtLine( 86 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * if (true) x int = 10; x = x * 2; SysLib.writestdout(x); else x int = 10; x = x * 2; SysLib.writestdout(x); end
	 * 0 validation messages are expected.
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * array int[]; foreach(x int from array) x = x * 2; SysLib.writestdout(x); end
	 * 0 validation messages are expected.
	 */
	public void testLine88() {
		List messages = getMessagesAtLine( 88 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * new class2().j1 = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine91() {
		List messages = getMessagesAtLine( 91 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * (local2 as class2).j1 = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine92() {
		List messages = getMessagesAtLine( 92 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * (notStaticField as class2).j1 = 10;
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot make a static reference to non-static field notStaticField.".
	 */
	public void testLine93() {
		List messages = getMessagesAtLine( 93 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot make a static reference to non-static field notStaticField." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot make a static reference to non-static field notStaticField.\" was issued." );
	}

	/*
	 * staticFunc4(notStaticFunc2());
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot make a static reference to non-static function notStaticFunc2().".
	 */
	public void testLine94() {
		List messages = getMessagesAtLine( 94 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot make a static reference to non-static function notStaticFunc2()." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot make a static reference to non-static function notStaticFunc2().\" was issued." );
	}

	/*
	 * notStaticField = new class2;
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot make a static reference to non-static field notStaticField.".
	 */
	public void testLine97() {
		List messages = getMessagesAtLine( 97 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot make a static reference to non-static field notStaticField." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot make a static reference to non-static field notStaticField.\" was issued." );
	}

	/*
	 * notStaticFunc1();
	 * 1 validation message is expected.
	 * It is expected to contain "Cannot make a static reference to non-static function notStaticFunc1().".
	 */
	public void testLine98() {
		List messages = getMessagesAtLine( 98 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "Cannot make a static reference to non-static function notStaticFunc1()." );
		if( messageWithSubstring == null ) fail( "No message with substring \"Cannot make a static reference to non-static function notStaticFunc1().\" was issued." );
	}
}
