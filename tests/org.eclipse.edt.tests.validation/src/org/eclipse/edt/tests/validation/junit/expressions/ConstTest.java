package org.eclipse.edt.tests.validation.junit.expressions;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/expressions/const.egl
 */
public class ConstTest extends ValidationTestCase {

	public ConstTest() {
		super( "EGLSource/expressions/const.egl", false );
	}

	/*
	 * globalValue = 20;
	 * 1 validation message is expected.
	 */
	public void testLine23() {
		List messages = getMessagesAtLine( 23 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalReference = [20];
	 * 1 validation message is expected.
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localValue = 20;
	 * 1 validation message is expected.
	 */
	public void testLine25() {
		List messages = getMessagesAtLine( 25 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localReference = [20];
	 * 1 validation message is expected.
	 */
	public void testLine26() {
		List messages = getMessagesAtLine( 26 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmValue = 20;
	 * 1 validation message is expected.
	 */
	public void testLine27() {
		List messages = getMessagesAtLine( 27 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmReference = [20];
	 * 1 validation message is expected.
	 */
	public void testLine28() {
		List messages = getMessagesAtLine( 28 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalOH.constValue1 = 10;
	 * 1 validation message is expected.
	 */
	public void testLine30() {
		List messages = getMessagesAtLine( 30 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalOH.constValue2 = new constRec;
	 * 1 validation message is expected.
	 */
	public void testLine31() {
		List messages = getMessagesAtLine( 31 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalOH.constReference1 = [];
	 * 1 validation message is expected.
	 */
	public void testLine32() {
		List messages = getMessagesAtLine( 32 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalOH.constReference2 = [];
	 * 1 validation message is expected.
	 */
	public void testLine33() {
		List messages = getMessagesAtLine( 33 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localOH.constValue1 = 10;
	 * 1 validation message is expected.
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localOH.constValue2 = new constRec;
	 * 1 validation message is expected.
	 */
	public void testLine35() {
		List messages = getMessagesAtLine( 35 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localOH.constReference1 = [];
	 * 1 validation message is expected.
	 */
	public void testLine36() {
		List messages = getMessagesAtLine( 36 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localOH.constReference2 = [];
	 * 1 validation message is expected.
	 */
	public void testLine37() {
		List messages = getMessagesAtLine( 37 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmOH.constValue1 = 10;
	 * 1 validation message is expected.
	 */
	public void testLine38() {
		List messages = getMessagesAtLine( 38 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmOH.constValue2 = new constRec;
	 * 1 validation message is expected.
	 */
	public void testLine39() {
		List messages = getMessagesAtLine( 39 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmOH.constReference1 = [];
	 * 1 validation message is expected.
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmOH.constReference2 = [];
	 * 1 validation message is expected.
	 */
	public void testLine41() {
		List messages = getMessagesAtLine( 41 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalRec.i = 10;
	 * 1 validation message is expected.
	 */
	public void testLine53() {
		List messages = getMessagesAtLine( 53 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalRec.sub = new subConstRec;
	 * 1 validation message is expected.
	 */
	public void testLine54() {
		List messages = getMessagesAtLine( 54 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalRec.sub.s[1] = 10;
	 * 1 validation message is expected.
	 */
	public void testLine55() {
		List messages = getMessagesAtLine( 55 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localRec.i = 10;
	 * 1 validation message is expected.
	 */
	public void testLine56() {
		List messages = getMessagesAtLine( 56 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localRec.sub = new subConstRec;
	 * 1 validation message is expected.
	 */
	public void testLine57() {
		List messages = getMessagesAtLine( 57 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localRec.sub.s[1] = 10;
	 * 1 validation message is expected.
	 */
	public void testLine58() {
		List messages = getMessagesAtLine( 58 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmRec.i = 10;
	 * 1 validation message is expected.
	 */
	public void testLine59() {
		List messages = getMessagesAtLine( 59 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmRec.sub = new subConstRec;
	 * 1 validation message is expected.
	 */
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmRec.sub.s[1] = 10;
	 * 1 validation message is expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalOH.constValue2.i = 20;
	 * 1 validation message is expected.
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalOH.constValue2.sub.s[88] = "";
	 * 1 validation message is expected.
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localOH.constValue2.i = 20;
	 * 1 validation message is expected.
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localOH.constValue2.sub.s[88] = "";
	 * 1 validation message is expected.
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmOH.constValue2.i = 20;
	 * 1 validation message is expected.
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmOH.constValue2.sub.s[88] = "";
	 * 1 validation message is expected.
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalRec[1] = new constRec;
	 * 0 validation messages are expected.
	 */
	public void testLine82() {
		List messages = getMessagesAtLine( 82 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalRec[1].i = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalRec[1].sub = new subConstRec;
	 * 0 validation messages are expected.
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalRec[1].sub.s[1] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine85() {
		List messages = getMessagesAtLine( 85 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalAny.foo = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine86() {
		List messages = getMessagesAtLine( 86 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localRec[1] = new constRec;
	 * 0 validation messages are expected.
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localRec[1].i = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine88() {
		List messages = getMessagesAtLine( 88 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localRec[1].sub = new subConstRec;
	 * 0 validation messages are expected.
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localRec[1].sub.s[1] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine90() {
		List messages = getMessagesAtLine( 90 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localAny.foo = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine91() {
		List messages = getMessagesAtLine( 91 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmRec[1] = new constRec;
	 * 0 validation messages are expected.
	 */
	public void testLine92() {
		List messages = getMessagesAtLine( 92 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmRec[1].i = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine93() {
		List messages = getMessagesAtLine( 93 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmRec[1].sub = new subConstRec;
	 * 0 validation messages are expected.
	 */
	public void testLine94() {
		List messages = getMessagesAtLine( 94 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmRec[1].sub.s[1] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine95() {
		List messages = getMessagesAtLine( 95 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmAny.foo = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine96() {
		List messages = getMessagesAtLine( 96 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constReference2[1] = new constRec;
	 * 0 validation messages are expected.
	 */
	public void testLine98() {
		List messages = getMessagesAtLine( 98 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constReference2[1].i = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine99() {
		List messages = getMessagesAtLine( 99 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constReference2[1].sub = new subConstRec;
	 * 0 validation messages are expected.
	 */
	public void testLine100() {
		List messages = getMessagesAtLine( 100 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constReference2[1].sub.s[1] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine101() {
		List messages = getMessagesAtLine( 101 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constReference2[1] = new constRec;
	 * 0 validation messages are expected.
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constReference2[1].i = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine103() {
		List messages = getMessagesAtLine( 103 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constReference2[1].sub = new subConstRec;
	 * 0 validation messages are expected.
	 */
	public void testLine104() {
		List messages = getMessagesAtLine( 104 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constReference2[1].sub.s[1] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine105() {
		List messages = getMessagesAtLine( 105 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constReference2[1] = new constRec;
	 * 0 validation messages are expected.
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constReference2[1].i = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine107() {
		List messages = getMessagesAtLine( 107 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constReference2[1].sub = new subConstRec;
	 * 0 validation messages are expected.
	 */
	public void testLine108() {
		List messages = getMessagesAtLine( 108 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constReference2[1].sub.s[1] = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine109() {
		List messages = getMessagesAtLine( 109 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * global1.appendElement(10);
	 * 0 validation messages are expected.
	 */
	public void testLine123() {
		List messages = getMessagesAtLine( 123 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * local1.appendElement(10);
	 * 0 validation messages are expected.
	 */
	public void testLine124() {
		List messages = getMessagesAtLine( 124 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parm1.appendElement(10);
	 * 0 validation messages are expected.
	 */
	public void testLine125() {
		List messages = getMessagesAtLine( 125 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * global2.trim();
	 * 0 validation messages are expected.
	 */
	public void testLine126() {
		List messages = getMessagesAtLine( 126 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * local2.trim();
	 * 0 validation messages are expected.
	 */
	public void testLine127() {
		List messages = getMessagesAtLine( 127 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parm2.trim();
	 * 0 validation messages are expected.
	 */
	public void testLine128() {
		List messages = getMessagesAtLine( 128 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constReference1.appendElement(123);
	 * 0 validation messages are expected.
	 */
	public void testLine130() {
		List messages = getMessagesAtLine( 130 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constString.trim();
	 * 0 validation messages are expected.
	 */
	public void testLine131() {
		List messages = getMessagesAtLine( 131 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constReference1.appendElement(123);
	 * 0 validation messages are expected.
	 */
	public void testLine132() {
		List messages = getMessagesAtLine( 132 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constString.trim();
	 * 0 validation messages are expected.
	 */
	public void testLine133() {
		List messages = getMessagesAtLine( 133 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constReference1.appendElement(123);
	 * 0 validation messages are expected.
	 */
	public void testLine134() {
		List messages = getMessagesAtLine( 134 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constString.trim();
	 * 0 validation messages are expected.
	 */
	public void testLine135() {
		List messages = getMessagesAtLine( 135 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global1 int = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine140() {
		List messages = getMessagesAtLine( 140 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global2 int;
	 * 0 validation messages are expected.
	 */
	public void testLine141() {
		List messages = getMessagesAtLine( 141 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global3 any?;
	 * 0 validation messages are expected.
	 */
	public void testLine142() {
		List messages = getMessagesAtLine( 142 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global4 any;
	 * 1 validation message is expected.
	 */
	public void testLine143() {
		List messages = getMessagesAtLine( 143 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * const global5 any? = 52;
	 * 0 validation messages are expected.
	 */
	public void testLine144() {
		List messages = getMessagesAtLine( 144 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global6 any = 52;
	 * 0 validation messages are expected.
	 */
	public void testLine145() {
		List messages = getMessagesAtLine( 145 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const local1 int = 10;
	 * 0 validation messages are expected.
	 */
	public void testLine148() {
		List messages = getMessagesAtLine( 148 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const local2 int;
	 * 0 validation messages are expected.
	 */
	public void testLine149() {
		List messages = getMessagesAtLine( 149 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const local3 any?;
	 * 0 validation messages are expected.
	 */
	public void testLine150() {
		List messages = getMessagesAtLine( 150 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const local4 any;
	 * 1 validation message is expected.
	 */
	public void testLine151() {
		List messages = getMessagesAtLine( 151 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * const local5 any? = 52;
	 * 0 validation messages are expected.
	 */
	public void testLine152() {
		List messages = getMessagesAtLine( 152 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const local6 any = 52;
	 * 0 validation messages are expected.
	 */
	public void testLine153() {
		List messages = getMessagesAtLine( 153 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global1 int = 10 * 30;
	 * 0 validation messages are expected.
	 */
	public void testLine158() {
		List messages = getMessagesAtLine( 158 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global2 int = global1 xor (1/global1);
	 * 0 validation messages are expected.
	 */
	public void testLine159() {
		List messages = getMessagesAtLine( 159 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global3 int = "1234" >> 20;
	 * 0 validation messages are expected.
	 */
	public void testLine160() {
		List messages = getMessagesAtLine( 160 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global4 date = datetimelib.mdy(10,20,30);
	 * 0 validation messages are expected.
	 */
	public void testLine161() {
		List messages = getMessagesAtLine( 161 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global5 bytes(10) = "abc".toBytes();
	 * 0 validation messages are expected.
	 */
	public void testLine162() {
		List messages = getMessagesAtLine( 162 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global6 int[] = [56, 78, 90];
	 * 0 validation messages are expected.
	 */
	public void testLine163() {
		List messages = getMessagesAtLine( 163 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global7 constRec = 56;
	 * 1 validation message is expected.
	 */
	public void testLine164() {
		List messages = getMessagesAtLine( 164 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * const global8 boolean = "true";
	 * 1 validation message is expected.
	 */
	public void testLine165() {
		List messages = getMessagesAtLine( 165 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * const global9 string = "reeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeally "
	 * 0 validation messages are expected.
	 */
	public void testLine166() {
		List messages = getMessagesAtLine( 166 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * + "long string on multiple "
	 * 0 validation messages are expected.
	 */
	public void testLine167() {
		List messages = getMessagesAtLine( 167 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * + "lines";
	 * 0 validation messages are expected.
	 */
	public void testLine168() {
		List messages = getMessagesAtLine( 168 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const local1 int = 10 * 30;
	 * 0 validation messages are expected.
	 */
	public void testLine172() {
		List messages = getMessagesAtLine( 172 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const local2 int = local1 xor (1/global1);
	 * 0 validation messages are expected.
	 */
	public void testLine173() {
		List messages = getMessagesAtLine( 173 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const local3 int = "1234" >> 20;
	 * 0 validation messages are expected.
	 */
	public void testLine174() {
		List messages = getMessagesAtLine( 174 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const local4 date = datetimelib.mdy(10,20,30);
	 * 0 validation messages are expected.
	 */
	public void testLine175() {
		List messages = getMessagesAtLine( 175 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const local5 bytes(10) = "abc".toBytes();
	 * 0 validation messages are expected.
	 */
	public void testLine176() {
		List messages = getMessagesAtLine( 176 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const local6 int[] = [56, 78, 90];
	 * 0 validation messages are expected.
	 */
	public void testLine177() {
		List messages = getMessagesAtLine( 177 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const local7 constRec = 56;
	 * 1 validation message is expected.
	 */
	public void testLine178() {
		List messages = getMessagesAtLine( 178 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * const local8 boolean = "true";
	 * 1 validation message is expected.
	 */
	public void testLine179() {
		List messages = getMessagesAtLine( 179 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * const local9 string = "reeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeally "
	 * 0 validation messages are expected.
	 */
	public void testLine180() {
		List messages = getMessagesAtLine( 180 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * + "long string on multiple "
	 * 0 validation messages are expected.
	 */
	public void testLine181() {
		List messages = getMessagesAtLine( 181 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * + "lines";
	 * 0 validation messages are expected.
	 */
	public void testLine182() {
		List messages = getMessagesAtLine( 182 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global1 constRec;
	 * 0 validation messages are expected.
	 */
	public void testLine188() {
		List messages = getMessagesAtLine( 188 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global2 int;
	 * 0 validation messages are expected.
	 */
	public void testLine189() {
		List messages = getMessagesAtLine( 189 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global3 string[];
	 * 0 validation messages are expected.
	 */
	public void testLine190() {
		List messages = getMessagesAtLine( 190 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global4 constDelegate?;
	 * 0 validation messages are expected.
	 */
	public void testLine191() {
		List messages = getMessagesAtLine( 191 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * const global5 constDelegate[];
	 * 0 validation messages are expected.
	 */
	public void testLine192() {
		List messages = getMessagesAtLine( 192 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalValue,
	 * 0 validation messages are expected.
	 */
	public void testLine206() {
		List messages = getMessagesAtLine( 206 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalValue,
	 * 0 validation messages are expected.
	 */
	public void testLine207() {
		List messages = getMessagesAtLine( 207 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalValue,
	 * 1 validation message is expected.
	 */
	public void testLine208() {
		List messages = getMessagesAtLine( 208 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalValue,
	 * 0 validation messages are expected.
	 */
	public void testLine209() {
		List messages = getMessagesAtLine( 209 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalValue
	 * 1 validation message is expected.
	 */
	public void testLine210() {
		List messages = getMessagesAtLine( 210 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalValue.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine213() {
		List messages = getMessagesAtLine( 213 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalValue.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine214() {
		List messages = getMessagesAtLine( 214 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalValue.sub.s[1],
	 * 1 validation message is expected.
	 */
	public void testLine215() {
		List messages = getMessagesAtLine( 215 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalValue.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine216() {
		List messages = getMessagesAtLine( 216 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalValue.sub.s[1]
	 * 1 validation message is expected.
	 */
	public void testLine217() {
		List messages = getMessagesAtLine( 217 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalReference[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine220() {
		List messages = getMessagesAtLine( 220 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalReference[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine221() {
		List messages = getMessagesAtLine( 221 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalReference[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine222() {
		List messages = getMessagesAtLine( 222 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalReference[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine223() {
		List messages = getMessagesAtLine( 223 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalReference[1].sub.s[1]
	 * 0 validation messages are expected.
	 */
	public void testLine224() {
		List messages = getMessagesAtLine( 224 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localValue,
	 * 0 validation messages are expected.
	 */
	public void testLine228() {
		List messages = getMessagesAtLine( 228 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localValue,
	 * 0 validation messages are expected.
	 */
	public void testLine229() {
		List messages = getMessagesAtLine( 229 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localValue,
	 * 1 validation message is expected.
	 */
	public void testLine230() {
		List messages = getMessagesAtLine( 230 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localValue,
	 * 0 validation messages are expected.
	 */
	public void testLine231() {
		List messages = getMessagesAtLine( 231 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localValue
	 * 1 validation message is expected.
	 */
	public void testLine232() {
		List messages = getMessagesAtLine( 232 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localValue.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine235() {
		List messages = getMessagesAtLine( 235 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localValue.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine236() {
		List messages = getMessagesAtLine( 236 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localValue.sub.s[1],
	 * 1 validation message is expected.
	 */
	public void testLine237() {
		List messages = getMessagesAtLine( 237 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localValue.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine238() {
		List messages = getMessagesAtLine( 238 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localValue.sub.s[1]
	 * 1 validation message is expected.
	 */
	public void testLine239() {
		List messages = getMessagesAtLine( 239 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localReference[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine242() {
		List messages = getMessagesAtLine( 242 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localReference[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine243() {
		List messages = getMessagesAtLine( 243 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localReference[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine244() {
		List messages = getMessagesAtLine( 244 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localReference[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine245() {
		List messages = getMessagesAtLine( 245 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localReference[1].sub.s[1]
	 * 0 validation messages are expected.
	 */
	public void testLine246() {
		List messages = getMessagesAtLine( 246 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmValue,
	 * 0 validation messages are expected.
	 */
	public void testLine250() {
		List messages = getMessagesAtLine( 250 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmValue,
	 * 0 validation messages are expected.
	 */
	public void testLine251() {
		List messages = getMessagesAtLine( 251 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmValue,
	 * 1 validation message is expected.
	 */
	public void testLine252() {
		List messages = getMessagesAtLine( 252 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmValue,
	 * 0 validation messages are expected.
	 */
	public void testLine253() {
		List messages = getMessagesAtLine( 253 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmValue
	 * 1 validation message is expected.
	 */
	public void testLine254() {
		List messages = getMessagesAtLine( 254 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmValue.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine257() {
		List messages = getMessagesAtLine( 257 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmValue.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine258() {
		List messages = getMessagesAtLine( 258 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmValue.sub.s[1],
	 * 1 validation message is expected.
	 */
	public void testLine259() {
		List messages = getMessagesAtLine( 259 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmValue.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine260() {
		List messages = getMessagesAtLine( 260 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmValue.sub.s[1]
	 * 1 validation message is expected.
	 */
	public void testLine261() {
		List messages = getMessagesAtLine( 261 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmReference[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine264() {
		List messages = getMessagesAtLine( 264 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmReference[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine265() {
		List messages = getMessagesAtLine( 265 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmReference[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine266() {
		List messages = getMessagesAtLine( 266 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmReference[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine267() {
		List messages = getMessagesAtLine( 267 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmReference[1].sub.s[1]
	 * 0 validation messages are expected.
	 */
	public void testLine268() {
		List messages = getMessagesAtLine( 268 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constValue2,
	 * 0 validation messages are expected.
	 */
	public void testLine272() {
		List messages = getMessagesAtLine( 272 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constValue2,
	 * 0 validation messages are expected.
	 */
	public void testLine273() {
		List messages = getMessagesAtLine( 273 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constValue2,
	 * 1 validation message is expected.
	 */
	public void testLine274() {
		List messages = getMessagesAtLine( 274 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalOH.constValue2,
	 * 0 validation messages are expected.
	 */
	public void testLine275() {
		List messages = getMessagesAtLine( 275 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constValue2
	 * 1 validation message is expected.
	 */
	public void testLine276() {
		List messages = getMessagesAtLine( 276 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalOH.constValue2.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine279() {
		List messages = getMessagesAtLine( 279 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constValue2.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine280() {
		List messages = getMessagesAtLine( 280 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constValue2.sub.s[1],
	 * 1 validation message is expected.
	 */
	public void testLine281() {
		List messages = getMessagesAtLine( 281 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalOH.constValue2.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine282() {
		List messages = getMessagesAtLine( 282 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constValue2.sub.s[1]
	 * 1 validation message is expected.
	 */
	public void testLine283() {
		List messages = getMessagesAtLine( 283 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * globalOH.constReference2[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine286() {
		List messages = getMessagesAtLine( 286 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constReference2[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine287() {
		List messages = getMessagesAtLine( 287 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constReference2[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine288() {
		List messages = getMessagesAtLine( 288 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constReference2[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine289() {
		List messages = getMessagesAtLine( 289 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * globalOH.constReference2[1].sub.s[1]
	 * 0 validation messages are expected.
	 */
	public void testLine290() {
		List messages = getMessagesAtLine( 290 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constValue2,
	 * 0 validation messages are expected.
	 */
	public void testLine294() {
		List messages = getMessagesAtLine( 294 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constValue2,
	 * 0 validation messages are expected.
	 */
	public void testLine295() {
		List messages = getMessagesAtLine( 295 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constValue2,
	 * 1 validation message is expected.
	 */
	public void testLine296() {
		List messages = getMessagesAtLine( 296 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localOH.constValue2,
	 * 0 validation messages are expected.
	 */
	public void testLine297() {
		List messages = getMessagesAtLine( 297 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constValue2
	 * 1 validation message is expected.
	 */
	public void testLine298() {
		List messages = getMessagesAtLine( 298 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localOH.constValue2.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine301() {
		List messages = getMessagesAtLine( 301 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constValue2.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine302() {
		List messages = getMessagesAtLine( 302 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constValue2.sub.s[1],
	 * 1 validation message is expected.
	 */
	public void testLine303() {
		List messages = getMessagesAtLine( 303 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localOH.constValue2.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine304() {
		List messages = getMessagesAtLine( 304 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constValue2.sub.s[1]
	 * 1 validation message is expected.
	 */
	public void testLine305() {
		List messages = getMessagesAtLine( 305 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * localOH.constReference2[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine308() {
		List messages = getMessagesAtLine( 308 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constReference2[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine309() {
		List messages = getMessagesAtLine( 309 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constReference2[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine310() {
		List messages = getMessagesAtLine( 310 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constReference2[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine311() {
		List messages = getMessagesAtLine( 311 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * localOH.constReference2[1].sub.s[1]
	 * 0 validation messages are expected.
	 */
	public void testLine312() {
		List messages = getMessagesAtLine( 312 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constValue2,
	 * 0 validation messages are expected.
	 */
	public void testLine316() {
		List messages = getMessagesAtLine( 316 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constValue2,
	 * 0 validation messages are expected.
	 */
	public void testLine317() {
		List messages = getMessagesAtLine( 317 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constValue2,
	 * 1 validation message is expected.
	 */
	public void testLine318() {
		List messages = getMessagesAtLine( 318 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmOH.constValue2,
	 * 0 validation messages are expected.
	 */
	public void testLine319() {
		List messages = getMessagesAtLine( 319 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constValue2
	 * 1 validation message is expected.
	 */
	public void testLine320() {
		List messages = getMessagesAtLine( 320 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmOH.constValue2.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine323() {
		List messages = getMessagesAtLine( 323 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constValue2.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine324() {
		List messages = getMessagesAtLine( 324 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constValue2.sub.s[1],
	 * 1 validation message is expected.
	 */
	public void testLine325() {
		List messages = getMessagesAtLine( 325 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmOH.constValue2.sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine326() {
		List messages = getMessagesAtLine( 326 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constValue2.sub.s[1]
	 * 1 validation message is expected.
	 */
	public void testLine327() {
		List messages = getMessagesAtLine( 327 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * parmOH.constReference2[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine330() {
		List messages = getMessagesAtLine( 330 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constReference2[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine331() {
		List messages = getMessagesAtLine( 331 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constReference2[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine332() {
		List messages = getMessagesAtLine( 332 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constReference2[1].sub.s[1],
	 * 0 validation messages are expected.
	 */
	public void testLine333() {
		List messages = getMessagesAtLine( 333 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * parmOH.constReference2[1].sub.s[1]
	 * 0 validation messages are expected.
	 */
	public void testLine334() {
		List messages = getMessagesAtLine( 334 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p1 int const in,
	 * 0 validation messages are expected.
	 */
	public void testLine344() {
		List messages = getMessagesAtLine( 344 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p2 int const inout,
	 * 0 validation messages are expected.
	 */
	public void testLine345() {
		List messages = getMessagesAtLine( 345 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * p3 int const out
	 * 1 validation message is expected.
	 * It is expected to contain "The const modifier is not allowed to be specified with the out modifier.".
	 */
	public void testLine346() {
		List messages = getMessagesAtLine( 346 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The const modifier is not allowed to be specified with the out modifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"The const modifier is not allowed to be specified with the out modifier.\" was issued." );
	}
}
