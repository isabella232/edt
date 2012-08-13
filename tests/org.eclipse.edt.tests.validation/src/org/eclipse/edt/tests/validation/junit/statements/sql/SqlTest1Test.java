package org.eclipse.edt.tests.validation.junit.statements.sql;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/statements/sql/SqlTest1.egl
 */
public class SqlTest1Test extends ValidationTestCase {

	public SqlTest1Test() {
		super( "EGLSource/statements/sql/SqlTest1.egl", false );
	}

	/*
	 * open rs from ds from ds;
	 * 1 validation message is expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs for myEntity from ds for myEntity;
	 * 1 validation message is expected.
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs using a,b with stmt using b,a;
	 * 1 validation message is expected.
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs with #sql{stuff} with stmt from ds;
	 * 1 validation message is expected.
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add parent to ds to rs;
	 * 1 validation message is expected.
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add parent to ds for parent for myEntity;
	 * 1 validation message is expected.
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get myEntity from rs from ds;
	 * 1 validation message is expected.
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get myEntity using a using b;
	 * 1 validation message is expected.
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get myEntity with #sql{} with a;
	 * 1 validation message is expected.
	 */
	public void testLine69() {
		List messages = getMessagesAtLine( 69 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from ds from ds;
	 * 1 validation message is expected.
	 */
	public void testLine70() {
		List messages = getMessagesAtLine( 70 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from ds with a with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from ds using a using b;
	 * 1 validation message is expected.
	 */
	public void testLine72() {
		List messages = getMessagesAtLine( 72 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from rs using a using b;
	 * 1 validation message is expected.
	 */
	public void testLine73() {
		List messages = getMessagesAtLine( 73 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from ds for parent for parent;
	 * 1 validation message is expected.
	 */
	public void testLine74() {
		List messages = getMessagesAtLine( 74 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from ds with a with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine75() {
		List messages = getMessagesAtLine( 75 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds to rs;
	 * 1 validation message is expected.
	 */
	public void testLine76() {
		List messages = getMessagesAtLine( 76 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds for parent for parent;
	 * 1 validation message is expected.
	 */
	public void testLine77() {
		List messages = getMessagesAtLine( 77 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds with a with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds using a using b;
	 * 1 validation message is expected.
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine84() {
		List messages = getMessagesAtLine( 84 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open a from ds;
	 * 1 validation message is expected.
	 */
	public void testLine85() {
		List messages = getMessagesAtLine( 85 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs from a;
	 * 1 validation message is expected.
	 */
	public void testLine88() {
		List messages = getMessagesAtLine( 88 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs from myEntity;
	 * 1 validation message is expected.
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs with stmt;
	 * 0 validation messages are expected.
	 */
	public void testLine92() {
		List messages = getMessagesAtLine( 92 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs with #sql{stuff} from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine93() {
		List messages = getMessagesAtLine( 93 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs with a;
	 * 1 validation message is expected.
	 */
	public void testLine94() {
		List messages = getMessagesAtLine( 94 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs with #sql{stuff};
	 * 1 validation message is expected.
	 */
	public void testLine97() {
		List messages = getMessagesAtLine( 97 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs using a,b;
	 * 1 validation message is expected.
	 */
	public void testLine98() {
		List messages = getMessagesAtLine( 98 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs using a,b from ds for myentity;
	 * 0 validation messages are expected.
	 */
	public void testLine100() {
		List messages = getMessagesAtLine( 100 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs with stmt for parent;
	 * 1 validation message is expected.
	 */
	public void testLine103() {
		List messages = getMessagesAtLine( 103 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs for myentity from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine106() {
		List messages = getMessagesAtLine( 106 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs for nonEntity from ds;
	 * 1 validation message is expected.
	 */
	public void testLine107() {
		List messages = getMessagesAtLine( 107 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs for parent from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine108() {
		List messages = getMessagesAtLine( 108 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs for child from ds;
	 * 1 validation message is expected.
	 */
	public void testLine110() {
		List messages = getMessagesAtLine( 110 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs for child.parent from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine111() {
		List messages = getMessagesAtLine( 111 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs for a from ds;
	 * 1 validation message is expected.
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs for parent.orphan from ds;
	 * 1 validation message is expected.
	 */
	public void testLine114() {
		List messages = getMessagesAtLine( 114 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs from ds into a ;
	 * 1 validation message is expected.
	 */
	public void testLine115() {
		List messages = getMessagesAtLine( 115 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * prepare ps from ds with #sql{insert into t (c1) values (?)};
	 * 0 validation messages are expected.
	 */
	public void testLine119() {
		List messages = getMessagesAtLine( 119 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * prepare ds from ds with #sql{insert into t (c1) values (?)};
	 * 1 validation message is expected.
	 */
	public void testLine122() {
		List messages = getMessagesAtLine( 122 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * prepare ps from ds with expr;
	 * 0 validation messages are expected.
	 */
	public void testLine125() {
		List messages = getMessagesAtLine( 125 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * prepare ps from ds with expr2;
	 * 1 validation message is expected.
	 */
	public void testLine129() {
		List messages = getMessagesAtLine( 129 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * foreach (a from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine133() {
		List messages = getMessagesAtLine( 133 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * foreach (a, parent.id from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine134() {
		List messages = getMessagesAtLine( 134 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * foreach (parent.id from ds) end
	 * 1 validation message is expected.
	 */
	public void testLine136() {
		List messages = getMessagesAtLine( 136 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * foreach (parent.id from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine138() {
		List messages = getMessagesAtLine( 138 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * foreach (parent.id, child.name from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine139() {
		List messages = getMessagesAtLine( 139 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * foreach (parent.orphan from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine141() {
		List messages = getMessagesAtLine( 141 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * foreach (parent.id, parent.id from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine142() {
		List messages = getMessagesAtLine( 142 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get myEntity from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine146() {
		List messages = getMessagesAtLine( 146 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get myEntity, myEntity from ds;
	 * 1 validation message is expected.
	 */
	public void testLine148() {
		List messages = getMessagesAtLine( 148 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get myEntity from ds with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine149() {
		List messages = getMessagesAtLine( 149 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get myEntity from rs with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine151() {
		List messages = getMessagesAtLine( 151 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get parent.id from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine152() {
		List messages = getMessagesAtLine( 152 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get parent.child from ds;
	 * 1 validation message is expected.
	 */
	public void testLine154() {
		List messages = getMessagesAtLine( 154 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get parent from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine155() {
		List messages = getMessagesAtLine( 155 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get parent.child from ds using a;
	 * 0 validation messages are expected.
	 */
	public void testLine156() {
		List messages = getMessagesAtLine( 156 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get a, b from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine158() {
		List messages = getMessagesAtLine( 158 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get a, b from ds using a with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine159() {
		List messages = getMessagesAtLine( 159 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get basic from ds;
	 * 1 validation message is expected.
	 */
	public void testLine161() {
		List messages = getMessagesAtLine( 161 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get basic from ds using a;
	 * 0 validation messages are expected.
	 */
	public void testLine162() {
		List messages = getMessagesAtLine( 162 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get basic from ds with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine163() {
		List messages = getMessagesAtLine( 163 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get dict from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine165() {
		List messages = getMessagesAtLine( 165 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get array from ds using a;
	 * 0 validation messages are expected.
	 */
	public void testLine166() {
		List messages = getMessagesAtLine( 166 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get invalidArray from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine167() {
		List messages = getMessagesAtLine( 167 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get array from ds into a;
	 * 1 validation message is expected.
	 */
	public void testLine169() {
		List messages = getMessagesAtLine( 169 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get rs from ds with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine171() {
		List messages = getMessagesAtLine( 171 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add myEntity to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine175() {
		List messages = getMessagesAtLine( 175 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add myEntity, myEntity to ds;
	 * 1 validation message is expected.
	 */
	public void testLine176() {
		List messages = getMessagesAtLine( 176 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add a to ds;
	 * 1 validation message is expected.
	 */
	public void testLine178() {
		List messages = getMessagesAtLine( 178 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add a, parent.id to ds;
	 * 1 validation message is expected.
	 */
	public void testLine180() {
		List messages = getMessagesAtLine( 180 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add a to ds for parent;
	 * 0 validation messages are expected.
	 */
	public void testLine181() {
		List messages = getMessagesAtLine( 181 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add a, parent.id to ds for parent;
	 * 0 validation messages are expected.
	 */
	public void testLine182() {
		List messages = getMessagesAtLine( 182 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add parent.id to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine183() {
		List messages = getMessagesAtLine( 183 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add parent.id, child.name to ds;
	 * 1 validation message is expected.
	 */
	public void testLine185() {
		List messages = getMessagesAtLine( 185 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add parent.id, child.name to ds for parent;
	 * 0 validation messages are expected.
	 */
	public void testLine186() {
		List messages = getMessagesAtLine( 186 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add parent.id, parent.id to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine187() {
		List messages = getMessagesAtLine( 187 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add myEntity to ds for myEntity;
	 * 0 validation messages are expected.
	 */
	public void testLine188() {
		List messages = getMessagesAtLine( 188 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add myEntity to rs for myEntity;
	 * 1 validation message is expected.
	 */
	public void testLine190() {
		List messages = getMessagesAtLine( 190 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add myEntity to ds for parent;
	 * 1 validation message is expected.
	 */
	public void testLine192() {
		List messages = getMessagesAtLine( 192 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add parent.id to ds for a;
	 * 1 validation message is expected.
	 */
	public void testLine194() {
		List messages = getMessagesAtLine( 194 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add parent.id to ds for parent.orphan;
	 * 1 validation message is expected.
	 */
	public void testLine196() {
		List messages = getMessagesAtLine( 196 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add parent.id to ds for parent;
	 * 0 validation messages are expected.
	 */
	public void testLine197() {
		List messages = getMessagesAtLine( 197 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add parent.orphan to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine199() {
		List messages = getMessagesAtLine( 199 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add o, parent.orphan to ds;
	 * 1 validation message is expected.
	 */
	public void testLine200() {
		List messages = getMessagesAtLine( 200 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add basic to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine201() {
		List messages = getMessagesAtLine( 201 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add multi to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine203() {
		List messages = getMessagesAtLine( 203 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine207() {
		List messages = getMessagesAtLine( 207 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute from rs;
	 * 1 validation message is expected.
	 */
	public void testLine208() {
		List messages = getMessagesAtLine( 208 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from rs with ps;
	 * 0 validation messages are expected.
	 */
	public void testLine210() {
		List messages = getMessagesAtLine( 210 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute from rs with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine211() {
		List messages = getMessagesAtLine( 211 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from rs with a;
	 * 1 validation message is expected.
	 */
	public void testLine212() {
		List messages = getMessagesAtLine( 212 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from ds with a using b;
	 * 0 validation messages are expected.
	 */
	public void testLine213() {
		List messages = getMessagesAtLine( 213 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute from ds using b;
	 * 0 validation messages are expected.
	 */
	public void testLine214() {
		List messages = getMessagesAtLine( 214 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute using b;
	 * 0 validation messages are expected.
	 */
	public void testLine215() {
		List messages = getMessagesAtLine( 215 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine216() {
		List messages = getMessagesAtLine( 216 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute with #sql{} from jndi;
	 * 0 validation messages are expected.
	 */
	public void testLine217() {
		List messages = getMessagesAtLine( 217 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete from ds;
	 * 1 validation message is expected.
	 */
	public void testLine222() {
		List messages = getMessagesAtLine( 222 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete from ds with a for basic;
	 * 1 validation message is expected.
	 */
	public void testLine224() {
		List messages = getMessagesAtLine( 224 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete from ds with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine225() {
		List messages = getMessagesAtLine( 225 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete from ds with a;
	 * 0 validation messages are expected.
	 */
	public void testLine226() {
		List messages = getMessagesAtLine( 226 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine228() {
		List messages = getMessagesAtLine( 228 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete from rs;
	 * 0 validation messages are expected.
	 */
	public void testLine229() {
		List messages = getMessagesAtLine( 229 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete from rs with a;
	 * 1 validation message is expected.
	 */
	public void testLine231() {
		List messages = getMessagesAtLine( 231 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete from rs with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine233() {
		List messages = getMessagesAtLine( 233 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete from rs using a;
	 * 1 validation message is expected.
	 */
	public void testLine235() {
		List messages = getMessagesAtLine( 235 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from ds for basic with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine238() {
		List messages = getMessagesAtLine( 238 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from ds for parent;
	 * 0 validation messages are expected.
	 */
	public void testLine239() {
		List messages = getMessagesAtLine( 239 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete parent from ds for myEntity;
	 * 1 validation message is expected.
	 */
	public void testLine241() {
		List messages = getMessagesAtLine( 241 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete multi from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine243() {
		List messages = getMessagesAtLine( 243 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete parent from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine244() {
		List messages = getMessagesAtLine( 244 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete parent.id from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine245() {
		List messages = getMessagesAtLine( 245 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete parent.child from ds;
	 * 1 validation message is expected.
	 */
	public void testLine246() {
		List messages = getMessagesAtLine( 246 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete a from ds;
	 * 1 validation message is expected.
	 */
	public void testLine247() {
		List messages = getMessagesAtLine( 247 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent.orphan from ds using a;
	 * 0 validation messages are expected.
	 */
	public void testLine249() {
		List messages = getMessagesAtLine( 249 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete basic from ds;
	 * 1 validation message is expected.
	 */
	public void testLine251() {
		List messages = getMessagesAtLine( 251 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete basic from ds with a;
	 * 0 validation messages are expected.
	 */
	public void testLine252() {
		List messages = getMessagesAtLine( 252 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete basic from ds with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine253() {
		List messages = getMessagesAtLine( 253 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete basic from ds using a;
	 * 0 validation messages are expected.
	 */
	public void testLine254() {
		List messages = getMessagesAtLine( 254 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete dict from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine256() {
		List messages = getMessagesAtLine( 256 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete array from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine258() {
		List messages = getMessagesAtLine( 258 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete invalidArray from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine259() {
		List messages = getMessagesAtLine( 259 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from ds for a;
	 * 1 validation message is expected.
	 */
	public void testLine261() {
		List messages = getMessagesAtLine( 261 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine265() {
		List messages = getMessagesAtLine( 265 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * replace parent to ds using a with b;
	 * 0 validation messages are expected.
	 */
	public void testLine266() {
		List messages = getMessagesAtLine( 266 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * replace parent to ds using a for parent;
	 * 1 validation message is expected.
	 */
	public void testLine268() {
		List messages = getMessagesAtLine( 268 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds with a for parent;
	 * 1 validation message is expected.
	 */
	public void testLine270() {
		List messages = getMessagesAtLine( 270 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds with #sql{} for parent;
	 * 1 validation message is expected.
	 */
	public void testLine272() {
		List messages = getMessagesAtLine( 272 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace basic to ds;
	 * 1 validation message is expected.
	 */
	public void testLine274() {
		List messages = getMessagesAtLine( 274 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace basic to ds with a;
	 * 0 validation messages are expected.
	 */
	public void testLine275() {
		List messages = getMessagesAtLine( 275 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * replace basic to rs with a;
	 * 1 validation message is expected.
	 */
	public void testLine277() {
		List messages = getMessagesAtLine( 277 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace basic to rs using a;
	 * 1 validation message is expected.
	 */
	public void testLine279() {
		List messages = getMessagesAtLine( 279 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace a to ds;
	 * 1 validation message is expected.
	 */
	public void testLine281() {
		List messages = getMessagesAtLine( 281 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent.id to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine282() {
		List messages = getMessagesAtLine( 282 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * replace multi to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine284() {
		List messages = getMessagesAtLine( 284 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * replace dict to ds with a;
	 * 1 validation message is expected.
	 */
	public void testLine286() {
		List messages = getMessagesAtLine( 286 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace array to ds with a;
	 * 1 validation message is expected.
	 */
	public void testLine288() {
		List messages = getMessagesAtLine( 288 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace invalidArray to ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine289() {
		List messages = getMessagesAtLine( 289 );
		assertEquals( 1, messages.size() );
	}
}
