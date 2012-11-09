package org.eclipse.edt.tests.validation.junit.types;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/types/parameterized.egl
 */
public class ParameterizedTest extends ValidationTestCase {

	public ParameterizedTest() {
		super( "EGLSource/types/parameterized.egl", false );
	}

	/*
	 * d1 decimal(4,2);
	 * 0 validation messages are expected.
	 */
	public void testLine15() {
		List messages = getMessagesAtLine( 15 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d2 decimal(4,-2);
	 * 1 validation message is expected.
	 */
	public void testLine16() {
		List messages = getMessagesAtLine( 16 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d3 decimal(-4,2);
	 * 2 validation messages are expected.
	 */
	public void testLine17() {
		List messages = getMessagesAtLine( 17 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * d4 decimal(2,4);
	 * 1 validation message is expected.
	 * It is expected to contain "The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2.".
	 */
	public void testLine18() {
		List messages = getMessagesAtLine( 18 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2.\" was issued." );
	}

	/*
	 * d5 decimal("abc");
	 * 1 validation message is expected.
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d6 decimal("12");
	 * 0 validation messages are expected.
	 */
	public void testLine20() {
		List messages = getMessagesAtLine( 20 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d7 decimal(4);
	 * 0 validation messages are expected.
	 */
	public void testLine21() {
		List messages = getMessagesAtLine( 21 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d8 decimal(4,2,1);
	 * 1 validation message is expected.
	 */
	public void testLine22() {
		List messages = getMessagesAtLine( 22 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d9 decimal();
	 * 0 validation messages are expected.
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d10 decimal(999);
	 * 1 validation message is expected.
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s1 string(0);
	 * 0 validation messages are expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s2 string(10,2);
	 * 1 validation message is expected.
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s3 string("abc");
	 * 1 validation message is expected.
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s4 string("100");
	 * 0 validation messages are expected.
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s5 string();
	 * 0 validation messages are expected.
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s6 string;
	 * 0 validation messages are expected.
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s7 string?;
	 * 0 validation messages are expected.
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b1 bytes(0);
	 * 0 validation messages are expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b2 bytes(10,2);
	 * 1 validation message is expected.
	 */
	public void testLine35() {
		List messages = getMessagesAtLine( 35 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * b3 bytes("abc");
	 * 1 validation message is expected.
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * b4 bytes("100");
	 * 0 validation messages are expected.
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b5 bytes();
	 * 0 validation messages are expected.
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b6 bytes;
	 * 0 validation messages are expected.
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b7 bytes?;
	 * 0 validation messages are expected.
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts1 timestamp;
	 * 1 validation message is expected.
	 */
	public void testLine42() {
		List messages = getMessagesAtLine( 42 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts2 timestamp?;
	 * 0 validation messages are expected.
	 */
	public void testLine43() {
		List messages = getMessagesAtLine( 43 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts3 timestamp("abcd");
	 * 1 validation message is expected.
	 */
	public void testLine44() {
		List messages = getMessagesAtLine( 44 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts4 timestamp("HHmmss");
	 * 0 validation messages are expected.
	 */
	public void testLine45() {
		List messages = getMessagesAtLine( 45 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts5 timestamp("HHss");
	 * 1 validation message is expected.
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts6 timestamp("yyyyMMddHHmmss");
	 * 0 validation messages are expected.
	 */
	public void testLine47() {
		List messages = getMessagesAtLine( 47 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts7 timestamp("yyyyMMddhhmmss");
	 * 1 validation message is expected.
	 */
	public void testLine48() {
		List messages = getMessagesAtLine( 48 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts8 timestamp("yyyymmddHHmmss");
	 * 1 validation message is expected.
	 */
	public void testLine49() {
		List messages = getMessagesAtLine( 49 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts9 timestamp("yyyyMMHHmmss");
	 * 1 validation message is expected.
	 */
	public void testLine50() {
		List messages = getMessagesAtLine( 50 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts10 timestamp("HHss");
	 * 1 validation message is expected.
	 */
	public void testLine51() {
		List messages = getMessagesAtLine( 51 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i1 int;
	 * 0 validation messages are expected.
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i2 int(6);
	 * 1 validation message is expected.
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * bi1 bigint;
	 * 0 validation messages are expected.
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * bi2 bigint(2);
	 * 1 validation message is expected.
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * si1 smallint;
	 * 0 validation messages are expected.
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si2 smallint(2);
	 * 1 validation message is expected.
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * sf1 smallfloat;
	 * 0 validation messages are expected.
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * sf2 smallfloat(2);
	 * 1 validation message is expected.
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * f1 float;
	 * 0 validation messages are expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f2 float(2);
	 * 1 validation message is expected.
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * da1 date;
	 * 0 validation messages are expected.
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * da2 date(2);
	 * 1 validation message is expected.
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * t1 time;
	 * 0 validation messages are expected.
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * t2 time(2);
	 * 1 validation message is expected.
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a1 any;
	 * 1 validation message is expected.
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a2 any?;
	 * 0 validation messages are expected.
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a3 any(2)?;
	 * 1 validation message is expected.
	 */
	public void testLine69() {
		List messages = getMessagesAtLine( 69 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a4 any(2);
	 * 2 validation messages are expected.
	 */
	public void testLine70() {
		List messages = getMessagesAtLine( 70 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * n1 number;
	 * 0 validation messages are expected.
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * n2 number(2);
	 * 1 validation message is expected.
	 */
	public void testLine72() {
		List messages = getMessagesAtLine( 72 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * r1 rec;
	 * 0 validation messages are expected.
	 */
	public void testLine73() {
		List messages = getMessagesAtLine( 73 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * r2 rec("abc");
	 * 1 validation message is expected.
	 */
	public void testLine74() {
		List messages = getMessagesAtLine( 74 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d1 decimal(4,2);
	 * 0 validation messages are expected.
	 */
	public void testLine77() {
		List messages = getMessagesAtLine( 77 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d2 decimal(4,-2);
	 * 1 validation message is expected.
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d3 decimal(-4,2);
	 * 2 validation messages are expected.
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * d4 decimal(2,4);
	 * 1 validation message is expected.
	 * It is expected to contain "The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2.".
	 */
	public void testLine80() {
		List messages = getMessagesAtLine( 80 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2.\" was issued." );
	}

	/*
	 * d5 decimal("abc");
	 * 1 validation message is expected.
	 */
	public void testLine81() {
		List messages = getMessagesAtLine( 81 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d6 decimal("12");
	 * 0 validation messages are expected.
	 */
	public void testLine82() {
		List messages = getMessagesAtLine( 82 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d7 decimal(4);
	 * 0 validation messages are expected.
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d8 decimal(4,2,1);
	 * 1 validation message is expected.
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d9 decimal();
	 * 0 validation messages are expected.
	 */
	public void testLine85() {
		List messages = getMessagesAtLine( 85 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d10 decimal(999);
	 * 1 validation message is expected.
	 */
	public void testLine86() {
		List messages = getMessagesAtLine( 86 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s1 string(0);
	 * 0 validation messages are expected.
	 */
	public void testLine88() {
		List messages = getMessagesAtLine( 88 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s2 string(10,2);
	 * 1 validation message is expected.
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s3 string("abc");
	 * 1 validation message is expected.
	 */
	public void testLine90() {
		List messages = getMessagesAtLine( 90 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s4 string("100");
	 * 0 validation messages are expected.
	 */
	public void testLine91() {
		List messages = getMessagesAtLine( 91 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s5 string();
	 * 0 validation messages are expected.
	 */
	public void testLine92() {
		List messages = getMessagesAtLine( 92 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s6 string;
	 * 0 validation messages are expected.
	 */
	public void testLine93() {
		List messages = getMessagesAtLine( 93 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s7 string?;
	 * 0 validation messages are expected.
	 */
	public void testLine94() {
		List messages = getMessagesAtLine( 94 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b1 bytes(0);
	 * 0 validation messages are expected.
	 */
	public void testLine96() {
		List messages = getMessagesAtLine( 96 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b2 bytes(10,2);
	 * 1 validation message is expected.
	 */
	public void testLine97() {
		List messages = getMessagesAtLine( 97 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * b3 bytes("abc");
	 * 1 validation message is expected.
	 */
	public void testLine98() {
		List messages = getMessagesAtLine( 98 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * b4 bytes("100");
	 * 0 validation messages are expected.
	 */
	public void testLine99() {
		List messages = getMessagesAtLine( 99 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b5 bytes();
	 * 0 validation messages are expected.
	 */
	public void testLine100() {
		List messages = getMessagesAtLine( 100 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b6 bytes;
	 * 0 validation messages are expected.
	 */
	public void testLine101() {
		List messages = getMessagesAtLine( 101 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b7 bytes?;
	 * 0 validation messages are expected.
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts1 timestamp;
	 * 1 validation message is expected.
	 */
	public void testLine104() {
		List messages = getMessagesAtLine( 104 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts2 timestamp?;
	 * 0 validation messages are expected.
	 */
	public void testLine105() {
		List messages = getMessagesAtLine( 105 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts3 timestamp("abcd");
	 * 1 validation message is expected.
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts4 timestamp("HHmmss");
	 * 0 validation messages are expected.
	 */
	public void testLine107() {
		List messages = getMessagesAtLine( 107 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts5 timestamp("HHss");
	 * 1 validation message is expected.
	 */
	public void testLine108() {
		List messages = getMessagesAtLine( 108 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts6 timestamp("yyyyMMddHHmmss");
	 * 0 validation messages are expected.
	 */
	public void testLine109() {
		List messages = getMessagesAtLine( 109 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts7 timestamp("yyyyMMddhhmmss");
	 * 1 validation message is expected.
	 */
	public void testLine110() {
		List messages = getMessagesAtLine( 110 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts8 timestamp("yyyymmddHHmmss");
	 * 1 validation message is expected.
	 */
	public void testLine111() {
		List messages = getMessagesAtLine( 111 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts9 timestamp("yyyyMMHHmmss");
	 * 1 validation message is expected.
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts10 timestamp("HHss");
	 * 1 validation message is expected.
	 */
	public void testLine113() {
		List messages = getMessagesAtLine( 113 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i1 int;
	 * 0 validation messages are expected.
	 */
	public void testLine115() {
		List messages = getMessagesAtLine( 115 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i2 int(6);
	 * 1 validation message is expected.
	 */
	public void testLine116() {
		List messages = getMessagesAtLine( 116 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * bi1 bigint;
	 * 0 validation messages are expected.
	 */
	public void testLine117() {
		List messages = getMessagesAtLine( 117 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * bi2 bigint(2);
	 * 1 validation message is expected.
	 */
	public void testLine118() {
		List messages = getMessagesAtLine( 118 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * si1 smallint;
	 * 0 validation messages are expected.
	 */
	public void testLine119() {
		List messages = getMessagesAtLine( 119 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si2 smallint(2);
	 * 1 validation message is expected.
	 */
	public void testLine120() {
		List messages = getMessagesAtLine( 120 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * sf1 smallfloat;
	 * 0 validation messages are expected.
	 */
	public void testLine121() {
		List messages = getMessagesAtLine( 121 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * sf2 smallfloat(2);
	 * 1 validation message is expected.
	 */
	public void testLine122() {
		List messages = getMessagesAtLine( 122 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * f1 float;
	 * 0 validation messages are expected.
	 */
	public void testLine123() {
		List messages = getMessagesAtLine( 123 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f2 float(2);
	 * 1 validation message is expected.
	 */
	public void testLine124() {
		List messages = getMessagesAtLine( 124 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * da1 date;
	 * 0 validation messages are expected.
	 */
	public void testLine125() {
		List messages = getMessagesAtLine( 125 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * da2 date(2);
	 * 1 validation message is expected.
	 */
	public void testLine126() {
		List messages = getMessagesAtLine( 126 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * t1 time;
	 * 0 validation messages are expected.
	 */
	public void testLine127() {
		List messages = getMessagesAtLine( 127 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * t2 time(2);
	 * 1 validation message is expected.
	 */
	public void testLine128() {
		List messages = getMessagesAtLine( 128 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a1 any;
	 * 1 validation message is expected.
	 */
	public void testLine129() {
		List messages = getMessagesAtLine( 129 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a2 any?;
	 * 0 validation messages are expected.
	 */
	public void testLine130() {
		List messages = getMessagesAtLine( 130 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a3 any(2)?;
	 * 1 validation message is expected.
	 */
	public void testLine131() {
		List messages = getMessagesAtLine( 131 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a4 any(2);
	 * 2 validation messages are expected.
	 */
	public void testLine132() {
		List messages = getMessagesAtLine( 132 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * n1 number;
	 * 0 validation messages are expected.
	 */
	public void testLine133() {
		List messages = getMessagesAtLine( 133 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * n2 number(2);
	 * 1 validation message is expected.
	 */
	public void testLine134() {
		List messages = getMessagesAtLine( 134 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * r1 rec;
	 * 0 validation messages are expected.
	 */
	public void testLine135() {
		List messages = getMessagesAtLine( 135 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * r2 rec("abc");
	 * 1 validation message is expected.
	 */
	public void testLine136() {
		List messages = getMessagesAtLine( 136 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d1 decimal(4,2),
	 * 0 validation messages are expected.
	 */
	public void testLine140() {
		List messages = getMessagesAtLine( 140 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d2 decimal(4,-2),
	 * 1 validation message is expected.
	 */
	public void testLine141() {
		List messages = getMessagesAtLine( 141 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d3 decimal(-4,2),
	 * 2 validation messages are expected.
	 */
	public void testLine142() {
		List messages = getMessagesAtLine( 142 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * d4 decimal(2,4),
	 * 1 validation message is expected.
	 * It is expected to contain "The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2.".
	 */
	public void testLine143() {
		List messages = getMessagesAtLine( 143 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2.\" was issued." );
	}

	/*
	 * d5 decimal("abc"),
	 * 1 validation message is expected.
	 */
	public void testLine144() {
		List messages = getMessagesAtLine( 144 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d6 decimal("12"),
	 * 0 validation messages are expected.
	 */
	public void testLine145() {
		List messages = getMessagesAtLine( 145 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d7 decimal(4),
	 * 0 validation messages are expected.
	 */
	public void testLine146() {
		List messages = getMessagesAtLine( 146 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d8 decimal(4,2,1),
	 * 1 validation message is expected.
	 */
	public void testLine147() {
		List messages = getMessagesAtLine( 147 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d9 decimal(),
	 * 0 validation messages are expected.
	 */
	public void testLine148() {
		List messages = getMessagesAtLine( 148 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d10 decimal(999),
	 * 1 validation message is expected.
	 */
	public void testLine149() {
		List messages = getMessagesAtLine( 149 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s1 string(0),
	 * 0 validation messages are expected.
	 */
	public void testLine151() {
		List messages = getMessagesAtLine( 151 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s2 string(10,2),
	 * 1 validation message is expected.
	 */
	public void testLine152() {
		List messages = getMessagesAtLine( 152 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s3 string("abc"),
	 * 1 validation message is expected.
	 */
	public void testLine153() {
		List messages = getMessagesAtLine( 153 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s4 string("100"),
	 * 0 validation messages are expected.
	 */
	public void testLine154() {
		List messages = getMessagesAtLine( 154 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s5 string(),
	 * 0 validation messages are expected.
	 */
	public void testLine155() {
		List messages = getMessagesAtLine( 155 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s6 string,
	 * 0 validation messages are expected.
	 */
	public void testLine156() {
		List messages = getMessagesAtLine( 156 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s7 string?,
	 * 0 validation messages are expected.
	 */
	public void testLine157() {
		List messages = getMessagesAtLine( 157 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b1 bytes(0),
	 * 0 validation messages are expected.
	 */
	public void testLine159() {
		List messages = getMessagesAtLine( 159 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b2 bytes(10,2),
	 * 1 validation message is expected.
	 */
	public void testLine160() {
		List messages = getMessagesAtLine( 160 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * b3 bytes("abc"),
	 * 1 validation message is expected.
	 */
	public void testLine161() {
		List messages = getMessagesAtLine( 161 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * b4 bytes("100"),
	 * 0 validation messages are expected.
	 */
	public void testLine162() {
		List messages = getMessagesAtLine( 162 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b5 bytes(),
	 * 0 validation messages are expected.
	 */
	public void testLine163() {
		List messages = getMessagesAtLine( 163 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b6 bytes,
	 * 0 validation messages are expected.
	 */
	public void testLine164() {
		List messages = getMessagesAtLine( 164 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b7 bytes?,
	 * 0 validation messages are expected.
	 */
	public void testLine165() {
		List messages = getMessagesAtLine( 165 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts1 timestamp,
	 * 0 validation messages are expected.
	 */
	public void testLine167() {
		List messages = getMessagesAtLine( 167 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts2 timestamp?,
	 * 0 validation messages are expected.
	 */
	public void testLine168() {
		List messages = getMessagesAtLine( 168 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts3 timestamp("abcd"),
	 * 1 validation message is expected.
	 */
	public void testLine169() {
		List messages = getMessagesAtLine( 169 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts4 timestamp("HHmmss"),
	 * 0 validation messages are expected.
	 */
	public void testLine170() {
		List messages = getMessagesAtLine( 170 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts5 timestamp("HHss"),
	 * 1 validation message is expected.
	 */
	public void testLine171() {
		List messages = getMessagesAtLine( 171 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts6 timestamp("yyyyMMddHHmmss"),
	 * 0 validation messages are expected.
	 */
	public void testLine172() {
		List messages = getMessagesAtLine( 172 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts7 timestamp("yyyyMMddhhmmss"),
	 * 1 validation message is expected.
	 */
	public void testLine173() {
		List messages = getMessagesAtLine( 173 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts8 timestamp("yyyymmddHHmmss"),
	 * 1 validation message is expected.
	 */
	public void testLine174() {
		List messages = getMessagesAtLine( 174 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts9 timestamp("yyyyMMHHmmss"),
	 * 1 validation message is expected.
	 */
	public void testLine175() {
		List messages = getMessagesAtLine( 175 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts10 timestamp("HHss"),
	 * 1 validation message is expected.
	 */
	public void testLine176() {
		List messages = getMessagesAtLine( 176 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i1 int,
	 * 0 validation messages are expected.
	 */
	public void testLine178() {
		List messages = getMessagesAtLine( 178 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i2 int(6),
	 * 1 validation message is expected.
	 */
	public void testLine179() {
		List messages = getMessagesAtLine( 179 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * bi1 bigint,
	 * 0 validation messages are expected.
	 */
	public void testLine180() {
		List messages = getMessagesAtLine( 180 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * bi2 bigint(2),
	 * 1 validation message is expected.
	 */
	public void testLine181() {
		List messages = getMessagesAtLine( 181 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * si1 smallint,
	 * 0 validation messages are expected.
	 */
	public void testLine182() {
		List messages = getMessagesAtLine( 182 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si2 smallint(2),
	 * 1 validation message is expected.
	 */
	public void testLine183() {
		List messages = getMessagesAtLine( 183 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * sf1 smallfloat,
	 * 0 validation messages are expected.
	 */
	public void testLine184() {
		List messages = getMessagesAtLine( 184 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * sf2 smallfloat(2),
	 * 1 validation message is expected.
	 */
	public void testLine185() {
		List messages = getMessagesAtLine( 185 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * f1 float,
	 * 0 validation messages are expected.
	 */
	public void testLine186() {
		List messages = getMessagesAtLine( 186 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f2 float(2),
	 * 1 validation message is expected.
	 */
	public void testLine187() {
		List messages = getMessagesAtLine( 187 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * da1 date,
	 * 0 validation messages are expected.
	 */
	public void testLine188() {
		List messages = getMessagesAtLine( 188 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * da2 date(2),
	 * 1 validation message is expected.
	 */
	public void testLine189() {
		List messages = getMessagesAtLine( 189 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * t1 time,
	 * 0 validation messages are expected.
	 */
	public void testLine190() {
		List messages = getMessagesAtLine( 190 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * t2 time(2),
	 * 1 validation message is expected.
	 */
	public void testLine191() {
		List messages = getMessagesAtLine( 191 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a1 any,
	 * 0 validation messages are expected.
	 */
	public void testLine192() {
		List messages = getMessagesAtLine( 192 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a2 any?,
	 * 0 validation messages are expected.
	 */
	public void testLine193() {
		List messages = getMessagesAtLine( 193 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a3 any(2)?,
	 * 1 validation message is expected.
	 */
	public void testLine194() {
		List messages = getMessagesAtLine( 194 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a4 any(2),
	 * 1 validation message is expected.
	 */
	public void testLine195() {
		List messages = getMessagesAtLine( 195 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * n1 number,
	 * 0 validation messages are expected.
	 */
	public void testLine196() {
		List messages = getMessagesAtLine( 196 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * n2 number(2),
	 * 1 validation message is expected.
	 */
	public void testLine197() {
		List messages = getMessagesAtLine( 197 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * r1 rec,
	 * 0 validation messages are expected.
	 */
	public void testLine198() {
		List messages = getMessagesAtLine( 198 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * r2 rec("abc"),
	 * 1 validation message is expected.
	 */
	public void testLine199() {
		List messages = getMessagesAtLine( 199 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa decimal(4,2) ||
	 * 0 validation messages are expected.
	 */
	public void testLine209() {
		List messages = getMessagesAtLine( 209 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa decimal(4,-2) ||
	 * 1 validation message is expected.
	 */
	public void testLine210() {
		List messages = getMessagesAtLine( 210 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa decimal(-4,2) ||
	 * 2 validation messages are expected.
	 */
	public void testLine211() {
		List messages = getMessagesAtLine( 211 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * temp isa decimal(2,4) ||
	 * 1 validation message is expected.
	 * It is expected to contain "The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2.".
	 */
	public void testLine212() {
		List messages = getMessagesAtLine( 212 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2.\" was issued." );
	}

	/*
	 * temp isa decimal("abc") ||
	 * 1 validation message is expected.
	 */
	public void testLine213() {
		List messages = getMessagesAtLine( 213 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa decimal("12") ||
	 * 0 validation messages are expected.
	 */
	public void testLine214() {
		List messages = getMessagesAtLine( 214 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa decimal(4) ||
	 * 0 validation messages are expected.
	 */
	public void testLine215() {
		List messages = getMessagesAtLine( 215 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa decimal(4,2,1) ||
	 * 1 validation message is expected.
	 */
	public void testLine216() {
		List messages = getMessagesAtLine( 216 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa decimal() ||
	 * 0 validation messages are expected.
	 */
	public void testLine217() {
		List messages = getMessagesAtLine( 217 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa decimal(999) ||
	 * 1 validation message is expected.
	 */
	public void testLine218() {
		List messages = getMessagesAtLine( 218 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa string(0) ||
	 * 0 validation messages are expected.
	 */
	public void testLine220() {
		List messages = getMessagesAtLine( 220 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa string(10,2) ||
	 * 1 validation message is expected.
	 */
	public void testLine221() {
		List messages = getMessagesAtLine( 221 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa string("abc") ||
	 * 1 validation message is expected.
	 */
	public void testLine222() {
		List messages = getMessagesAtLine( 222 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa string("100") ||
	 * 0 validation messages are expected.
	 */
	public void testLine223() {
		List messages = getMessagesAtLine( 223 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa string() ||
	 * 0 validation messages are expected.
	 */
	public void testLine224() {
		List messages = getMessagesAtLine( 224 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa string ||
	 * 0 validation messages are expected.
	 */
	public void testLine225() {
		List messages = getMessagesAtLine( 225 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa bytes(0) ||
	 * 0 validation messages are expected.
	 */
	public void testLine227() {
		List messages = getMessagesAtLine( 227 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa bytes(10,2) ||
	 * 1 validation message is expected.
	 */
	public void testLine228() {
		List messages = getMessagesAtLine( 228 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa bytes("abc") ||
	 * 1 validation message is expected.
	 */
	public void testLine229() {
		List messages = getMessagesAtLine( 229 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa bytes("100") ||
	 * 0 validation messages are expected.
	 */
	public void testLine230() {
		List messages = getMessagesAtLine( 230 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa bytes() ||
	 * 0 validation messages are expected.
	 */
	public void testLine231() {
		List messages = getMessagesAtLine( 231 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa bytes ||
	 * 0 validation messages are expected.
	 */
	public void testLine232() {
		List messages = getMessagesAtLine( 232 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa timestamp ||
	 * 0 validation messages are expected.
	 */
	public void testLine234() {
		List messages = getMessagesAtLine( 234 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa timestamp("abcd") ||
	 * 1 validation message is expected.
	 */
	public void testLine235() {
		List messages = getMessagesAtLine( 235 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa timestamp("HHmmss") ||
	 * 0 validation messages are expected.
	 */
	public void testLine236() {
		List messages = getMessagesAtLine( 236 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa timestamp("HHss") ||
	 * 1 validation message is expected.
	 */
	public void testLine237() {
		List messages = getMessagesAtLine( 237 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa timestamp("yyyyMMddHHmmss") ||
	 * 0 validation messages are expected.
	 */
	public void testLine238() {
		List messages = getMessagesAtLine( 238 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa timestamp("yyyyMMddhhmmss") ||
	 * 1 validation message is expected.
	 */
	public void testLine239() {
		List messages = getMessagesAtLine( 239 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa timestamp("yyyymmddHHmmss") ||
	 * 1 validation message is expected.
	 */
	public void testLine240() {
		List messages = getMessagesAtLine( 240 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa timestamp("yyyyMMHHmmss") ||
	 * 1 validation message is expected.
	 */
	public void testLine241() {
		List messages = getMessagesAtLine( 241 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa timestamp("HHss") ||
	 * 1 validation message is expected.
	 */
	public void testLine242() {
		List messages = getMessagesAtLine( 242 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa int ||
	 * 0 validation messages are expected.
	 */
	public void testLine244() {
		List messages = getMessagesAtLine( 244 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa int(6) ||
	 * 1 validation message is expected.
	 */
	public void testLine245() {
		List messages = getMessagesAtLine( 245 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa bigint ||
	 * 0 validation messages are expected.
	 */
	public void testLine246() {
		List messages = getMessagesAtLine( 246 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa bigint(2) ||
	 * 1 validation message is expected.
	 */
	public void testLine247() {
		List messages = getMessagesAtLine( 247 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa smallint ||
	 * 0 validation messages are expected.
	 */
	public void testLine248() {
		List messages = getMessagesAtLine( 248 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa smallint(2) ||
	 * 1 validation message is expected.
	 */
	public void testLine249() {
		List messages = getMessagesAtLine( 249 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa smallfloat ||
	 * 0 validation messages are expected.
	 */
	public void testLine250() {
		List messages = getMessagesAtLine( 250 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa smallfloat(2) ||
	 * 1 validation message is expected.
	 */
	public void testLine251() {
		List messages = getMessagesAtLine( 251 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa float ||
	 * 0 validation messages are expected.
	 */
	public void testLine252() {
		List messages = getMessagesAtLine( 252 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa float(2) ||
	 * 1 validation message is expected.
	 */
	public void testLine253() {
		List messages = getMessagesAtLine( 253 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa date ||
	 * 0 validation messages are expected.
	 */
	public void testLine254() {
		List messages = getMessagesAtLine( 254 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa date(2) ||
	 * 1 validation message is expected.
	 */
	public void testLine255() {
		List messages = getMessagesAtLine( 255 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa time ||
	 * 0 validation messages are expected.
	 */
	public void testLine256() {
		List messages = getMessagesAtLine( 256 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa time(2) ||
	 * 1 validation message is expected.
	 */
	public void testLine257() {
		List messages = getMessagesAtLine( 257 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa any ||
	 * 0 validation messages are expected.
	 */
	public void testLine258() {
		List messages = getMessagesAtLine( 258 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa any(2) ||
	 * 1 validation message is expected.
	 */
	public void testLine259() {
		List messages = getMessagesAtLine( 259 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa number ||
	 * 0 validation messages are expected.
	 */
	public void testLine260() {
		List messages = getMessagesAtLine( 260 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa number(2) ||
	 * 1 validation message is expected.
	 */
	public void testLine261() {
		List messages = getMessagesAtLine( 261 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * temp isa rec ||
	 * 0 validation messages are expected.
	 */
	public void testLine262() {
		List messages = getMessagesAtLine( 262 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * temp isa rec("abc") ||
	 * 1 validation message is expected.
	 */
	public void testLine263() {
		List messages = getMessagesAtLine( 263 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_d1() returns(decimal(4,2)) end
	 * 0 validation messages are expected.
	 */
	public void testLine268() {
		List messages = getMessagesAtLine( 268 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_d2() returns(decimal(4,-2)) end
	 * 1 validation message is expected.
	 */
	public void testLine269() {
		List messages = getMessagesAtLine( 269 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_d3() returns(decimal(-4,2)) end
	 * 2 validation messages are expected.
	 */
	public void testLine270() {
		List messages = getMessagesAtLine( 270 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * function f_d4() returns(decimal(2,4)) end
	 * 1 validation message is expected.
	 * It is expected to contain "The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2.".
	 */
	public void testLine271() {
		List messages = getMessagesAtLine( 271 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2.\" was issued." );
	}

	/*
	 * function f_d5() returns(decimal("abc")) end
	 * 1 validation message is expected.
	 */
	public void testLine272() {
		List messages = getMessagesAtLine( 272 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_d6() returns(decimal("12")) end
	 * 0 validation messages are expected.
	 */
	public void testLine273() {
		List messages = getMessagesAtLine( 273 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_d7() returns(decimal(4)) end
	 * 0 validation messages are expected.
	 */
	public void testLine274() {
		List messages = getMessagesAtLine( 274 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_d8() returns(decimal(4,2,1)) end
	 * 1 validation message is expected.
	 */
	public void testLine275() {
		List messages = getMessagesAtLine( 275 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_d9() returns(decimal()) end
	 * 0 validation messages are expected.
	 */
	public void testLine276() {
		List messages = getMessagesAtLine( 276 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_d10() returns(decimal(999)) end
	 * 1 validation message is expected.
	 */
	public void testLine277() {
		List messages = getMessagesAtLine( 277 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_s1() returns(string(0)) end
	 * 0 validation messages are expected.
	 */
	public void testLine279() {
		List messages = getMessagesAtLine( 279 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_s2() returns(string(10,2)) end
	 * 1 validation message is expected.
	 */
	public void testLine280() {
		List messages = getMessagesAtLine( 280 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_s3() returns(string("abc")) end
	 * 1 validation message is expected.
	 */
	public void testLine281() {
		List messages = getMessagesAtLine( 281 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_s4() returns(string("100")) end
	 * 0 validation messages are expected.
	 */
	public void testLine282() {
		List messages = getMessagesAtLine( 282 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_s5() returns(string()) end
	 * 0 validation messages are expected.
	 */
	public void testLine283() {
		List messages = getMessagesAtLine( 283 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_s6() returns(string) end
	 * 0 validation messages are expected.
	 */
	public void testLine284() {
		List messages = getMessagesAtLine( 284 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_s7() returns(string?) end
	 * 0 validation messages are expected.
	 */
	public void testLine285() {
		List messages = getMessagesAtLine( 285 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_b1() returns(bytes(0)) end
	 * 0 validation messages are expected.
	 */
	public void testLine287() {
		List messages = getMessagesAtLine( 287 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_b2() returns(bytes(10,2)) end
	 * 1 validation message is expected.
	 */
	public void testLine288() {
		List messages = getMessagesAtLine( 288 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_b3() returns(bytes("abc")) end
	 * 1 validation message is expected.
	 */
	public void testLine289() {
		List messages = getMessagesAtLine( 289 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_b4() returns(bytes("100")) end
	 * 0 validation messages are expected.
	 */
	public void testLine290() {
		List messages = getMessagesAtLine( 290 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_b5() returns(bytes()) end
	 * 0 validation messages are expected.
	 */
	public void testLine291() {
		List messages = getMessagesAtLine( 291 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_b6() returns(bytes) end
	 * 0 validation messages are expected.
	 */
	public void testLine292() {
		List messages = getMessagesAtLine( 292 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_b7() returns(bytes?) end
	 * 0 validation messages are expected.
	 */
	public void testLine293() {
		List messages = getMessagesAtLine( 293 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_ts1() returns(timestamp) end
	 * 0 validation messages are expected.
	 */
	public void testLine295() {
		List messages = getMessagesAtLine( 295 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_ts2() returns(timestamp?) end
	 * 0 validation messages are expected.
	 */
	public void testLine296() {
		List messages = getMessagesAtLine( 296 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_ts3() returns(timestamp("abcd")) end
	 * 1 validation message is expected.
	 */
	public void testLine297() {
		List messages = getMessagesAtLine( 297 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_ts4() returns(timestamp("HHmmss")) end
	 * 0 validation messages are expected.
	 */
	public void testLine298() {
		List messages = getMessagesAtLine( 298 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_ts5() returns(timestamp("HHss")) end
	 * 1 validation message is expected.
	 */
	public void testLine299() {
		List messages = getMessagesAtLine( 299 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_ts6() returns(timestamp("yyyyMMddHHmmss")) end
	 * 0 validation messages are expected.
	 */
	public void testLine300() {
		List messages = getMessagesAtLine( 300 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_ts7() returns(timestamp("yyyyMMddhhmmss")) end
	 * 1 validation message is expected.
	 */
	public void testLine301() {
		List messages = getMessagesAtLine( 301 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_ts8() returns(timestamp("yyyymmddHHmmss")) end
	 * 1 validation message is expected.
	 */
	public void testLine302() {
		List messages = getMessagesAtLine( 302 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_ts9() returns(timestamp("yyyyMMHHmmss")) end
	 * 1 validation message is expected.
	 */
	public void testLine303() {
		List messages = getMessagesAtLine( 303 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_ts10() returns(timestamp("HHss")) end
	 * 1 validation message is expected.
	 */
	public void testLine304() {
		List messages = getMessagesAtLine( 304 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_i1() returns(int) end
	 * 0 validation messages are expected.
	 */
	public void testLine306() {
		List messages = getMessagesAtLine( 306 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_i2() returns(int(6)) end
	 * 1 validation message is expected.
	 */
	public void testLine307() {
		List messages = getMessagesAtLine( 307 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_bi1() returns(bigint) end
	 * 0 validation messages are expected.
	 */
	public void testLine308() {
		List messages = getMessagesAtLine( 308 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_bi2() returns(bigint(2)) end
	 * 1 validation message is expected.
	 */
	public void testLine309() {
		List messages = getMessagesAtLine( 309 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_si1() returns(smallint) end
	 * 0 validation messages are expected.
	 */
	public void testLine310() {
		List messages = getMessagesAtLine( 310 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_si2() returns(smallint(2)) end
	 * 1 validation message is expected.
	 */
	public void testLine311() {
		List messages = getMessagesAtLine( 311 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_sf1() returns(smallfloat) end
	 * 0 validation messages are expected.
	 */
	public void testLine312() {
		List messages = getMessagesAtLine( 312 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_sf2() returns(smallfloat(2)) end
	 * 1 validation message is expected.
	 */
	public void testLine313() {
		List messages = getMessagesAtLine( 313 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_f1() returns(float) end
	 * 0 validation messages are expected.
	 */
	public void testLine314() {
		List messages = getMessagesAtLine( 314 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_f2() returns(float(2)) end
	 * 1 validation message is expected.
	 */
	public void testLine315() {
		List messages = getMessagesAtLine( 315 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_da1() returns(date) end
	 * 0 validation messages are expected.
	 */
	public void testLine316() {
		List messages = getMessagesAtLine( 316 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_da2() returns(date(2)) end
	 * 1 validation message is expected.
	 */
	public void testLine317() {
		List messages = getMessagesAtLine( 317 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_t1() returns(time) end
	 * 0 validation messages are expected.
	 */
	public void testLine318() {
		List messages = getMessagesAtLine( 318 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_t2() returns(time(2)) end
	 * 1 validation message is expected.
	 */
	public void testLine319() {
		List messages = getMessagesAtLine( 319 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_a1() returns(any) end
	 * 0 validation messages are expected.
	 */
	public void testLine320() {
		List messages = getMessagesAtLine( 320 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_a2() returns(any?) end
	 * 0 validation messages are expected.
	 */
	public void testLine321() {
		List messages = getMessagesAtLine( 321 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_a3() returns(any(2)) end
	 * 1 validation message is expected.
	 */
	public void testLine322() {
		List messages = getMessagesAtLine( 322 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_n1() returns(number) end
	 * 0 validation messages are expected.
	 */
	public void testLine323() {
		List messages = getMessagesAtLine( 323 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_n2() returns(number(2)) end
	 * 1 validation message is expected.
	 */
	public void testLine324() {
		List messages = getMessagesAtLine( 324 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * function f_r1() returns(rec) end
	 * 0 validation messages are expected.
	 */
	public void testLine325() {
		List messages = getMessagesAtLine( 325 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * function f_r2() returns(rec("abc")) end
	 * 1 validation message is expected.
	 */
	public void testLine326() {
		List messages = getMessagesAtLine( 326 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d1 decimal(4,2);
	 * 0 validation messages are expected.
	 */
	public void testLine334() {
		List messages = getMessagesAtLine( 334 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d2 decimal(4,-2);
	 * 1 validation message is expected.
	 */
	public void testLine335() {
		List messages = getMessagesAtLine( 335 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d3 decimal(-4,2);
	 * 2 validation messages are expected.
	 */
	public void testLine336() {
		List messages = getMessagesAtLine( 336 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * d4 decimal(2,4);
	 * 1 validation message is expected.
	 * It is expected to contain "The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2.".
	 */
	public void testLine337() {
		List messages = getMessagesAtLine( 337 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The decimals value 4 for type eglx.lang.EDecimal must be less than or equal to the length value 2.\" was issued." );
	}

	/*
	 * d5 decimal("abc");
	 * 1 validation message is expected.
	 */
	public void testLine338() {
		List messages = getMessagesAtLine( 338 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d6 decimal("12");
	 * 0 validation messages are expected.
	 */
	public void testLine339() {
		List messages = getMessagesAtLine( 339 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d7 decimal(4);
	 * 0 validation messages are expected.
	 */
	public void testLine340() {
		List messages = getMessagesAtLine( 340 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d8 decimal(4,2,1);
	 * 1 validation message is expected.
	 */
	public void testLine341() {
		List messages = getMessagesAtLine( 341 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * d9 decimal();
	 * 0 validation messages are expected.
	 */
	public void testLine342() {
		List messages = getMessagesAtLine( 342 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * d10 decimal(999);
	 * 1 validation message is expected.
	 */
	public void testLine343() {
		List messages = getMessagesAtLine( 343 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s1 string(0);
	 * 0 validation messages are expected.
	 */
	public void testLine345() {
		List messages = getMessagesAtLine( 345 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s2 string(10,2);
	 * 1 validation message is expected.
	 */
	public void testLine346() {
		List messages = getMessagesAtLine( 346 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s3 string("abc");
	 * 1 validation message is expected.
	 */
	public void testLine347() {
		List messages = getMessagesAtLine( 347 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * s4 string("100");
	 * 0 validation messages are expected.
	 */
	public void testLine348() {
		List messages = getMessagesAtLine( 348 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s5 string();
	 * 0 validation messages are expected.
	 */
	public void testLine349() {
		List messages = getMessagesAtLine( 349 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s6 string;
	 * 0 validation messages are expected.
	 */
	public void testLine350() {
		List messages = getMessagesAtLine( 350 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * s7 string?;
	 * 0 validation messages are expected.
	 */
	public void testLine351() {
		List messages = getMessagesAtLine( 351 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b1 bytes(0);
	 * 0 validation messages are expected.
	 */
	public void testLine353() {
		List messages = getMessagesAtLine( 353 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b2 bytes(10,2);
	 * 1 validation message is expected.
	 */
	public void testLine354() {
		List messages = getMessagesAtLine( 354 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * b3 bytes("abc");
	 * 1 validation message is expected.
	 */
	public void testLine355() {
		List messages = getMessagesAtLine( 355 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * b4 bytes("100");
	 * 0 validation messages are expected.
	 */
	public void testLine356() {
		List messages = getMessagesAtLine( 356 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b5 bytes();
	 * 0 validation messages are expected.
	 */
	public void testLine357() {
		List messages = getMessagesAtLine( 357 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b6 bytes;
	 * 0 validation messages are expected.
	 */
	public void testLine358() {
		List messages = getMessagesAtLine( 358 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * b7 bytes?;
	 * 0 validation messages are expected.
	 */
	public void testLine359() {
		List messages = getMessagesAtLine( 359 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts1 timestamp;
	 * 1 validation message is expected.
	 */
	public void testLine361() {
		List messages = getMessagesAtLine( 361 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts2 timestamp?;
	 * 0 validation messages are expected.
	 */
	public void testLine362() {
		List messages = getMessagesAtLine( 362 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts3 timestamp("abcd");
	 * 1 validation message is expected.
	 */
	public void testLine363() {
		List messages = getMessagesAtLine( 363 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts4 timestamp("HHmmss");
	 * 0 validation messages are expected.
	 */
	public void testLine364() {
		List messages = getMessagesAtLine( 364 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts5 timestamp("HHss");
	 * 1 validation message is expected.
	 */
	public void testLine365() {
		List messages = getMessagesAtLine( 365 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts6 timestamp("yyyyMMddHHmmss");
	 * 0 validation messages are expected.
	 */
	public void testLine366() {
		List messages = getMessagesAtLine( 366 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * ts7 timestamp("yyyyMMddhhmmss");
	 * 1 validation message is expected.
	 */
	public void testLine367() {
		List messages = getMessagesAtLine( 367 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts8 timestamp("yyyymmddHHmmss");
	 * 1 validation message is expected.
	 */
	public void testLine368() {
		List messages = getMessagesAtLine( 368 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts9 timestamp("yyyyMMHHmmss");
	 * 1 validation message is expected.
	 */
	public void testLine369() {
		List messages = getMessagesAtLine( 369 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * ts10 timestamp("HHss");
	 * 1 validation message is expected.
	 */
	public void testLine370() {
		List messages = getMessagesAtLine( 370 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * i1 int;
	 * 0 validation messages are expected.
	 */
	public void testLine372() {
		List messages = getMessagesAtLine( 372 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * i2 int(6);
	 * 1 validation message is expected.
	 */
	public void testLine373() {
		List messages = getMessagesAtLine( 373 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * bi1 bigint;
	 * 0 validation messages are expected.
	 */
	public void testLine374() {
		List messages = getMessagesAtLine( 374 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * bi2 bigint(2);
	 * 1 validation message is expected.
	 */
	public void testLine375() {
		List messages = getMessagesAtLine( 375 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * si1 smallint;
	 * 0 validation messages are expected.
	 */
	public void testLine376() {
		List messages = getMessagesAtLine( 376 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * si2 smallint(2);
	 * 1 validation message is expected.
	 */
	public void testLine377() {
		List messages = getMessagesAtLine( 377 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * sf1 smallfloat;
	 * 0 validation messages are expected.
	 */
	public void testLine378() {
		List messages = getMessagesAtLine( 378 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * sf2 smallfloat(2);
	 * 1 validation message is expected.
	 */
	public void testLine379() {
		List messages = getMessagesAtLine( 379 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * f1 float;
	 * 0 validation messages are expected.
	 */
	public void testLine380() {
		List messages = getMessagesAtLine( 380 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f2 float(2);
	 * 1 validation message is expected.
	 */
	public void testLine381() {
		List messages = getMessagesAtLine( 381 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * da1 date;
	 * 0 validation messages are expected.
	 */
	public void testLine382() {
		List messages = getMessagesAtLine( 382 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * da2 date(2);
	 * 1 validation message is expected.
	 */
	public void testLine383() {
		List messages = getMessagesAtLine( 383 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * t1 time;
	 * 0 validation messages are expected.
	 */
	public void testLine384() {
		List messages = getMessagesAtLine( 384 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * t2 time(2);
	 * 1 validation message is expected.
	 */
	public void testLine385() {
		List messages = getMessagesAtLine( 385 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a1 any;
	 * 1 validation message is expected.
	 */
	public void testLine386() {
		List messages = getMessagesAtLine( 386 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a2 any?;
	 * 0 validation messages are expected.
	 */
	public void testLine387() {
		List messages = getMessagesAtLine( 387 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * a3 any(2)?;
	 * 1 validation message is expected.
	 */
	public void testLine388() {
		List messages = getMessagesAtLine( 388 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a4 any(2);
	 * 2 validation messages are expected.
	 */
	public void testLine389() {
		List messages = getMessagesAtLine( 389 );
		assertEquals( 2, messages.size() );
	}

	/*
	 * n1 number;
	 * 0 validation messages are expected.
	 */
	public void testLine390() {
		List messages = getMessagesAtLine( 390 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * n2 number(2);
	 * 1 validation message is expected.
	 */
	public void testLine391() {
		List messages = getMessagesAtLine( 391 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * r1 rec;
	 * 0 validation messages are expected.
	 */
	public void testLine392() {
		List messages = getMessagesAtLine( 392 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * r2 rec("abc");
	 * 1 validation message is expected.
	 */
	public void testLine393() {
		List messages = getMessagesAtLine( 393 );
		assertEquals( 1, messages.size() );
	}
}
