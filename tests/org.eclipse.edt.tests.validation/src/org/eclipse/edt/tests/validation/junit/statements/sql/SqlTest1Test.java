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
	public void testLine60() {
		List messages = getMessagesAtLine( 60 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs for myEntity from ds for myEntity;
	 * 1 validation message is expected.
	 */
	public void testLine61() {
		List messages = getMessagesAtLine( 61 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs using a,b with stmt using b,a;
	 * 1 validation message is expected.
	 */
	public void testLine62() {
		List messages = getMessagesAtLine( 62 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs with #sql{stuff} with stmt from ds;
	 * 1 validation message is expected.
	 */
	public void testLine63() {
		List messages = getMessagesAtLine( 63 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * prepare ps from ds with #sql{} with expr;
	 * 1 validation message is expected.
	 */
	public void testLine64() {
		List messages = getMessagesAtLine( 64 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * foreach (myEntity from rs from rs) end
	 * 1 validation message is expected.
	 */
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add parent to ds to rs;
	 * 1 validation message is expected.
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add parent to ds for parent for myEntity;
	 * 1 validation message is expected.
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get myEntity from rs from ds;
	 * 1 validation message is expected.
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get myEntity using a using b;
	 * 1 validation message is expected.
	 */
	public void testLine69() {
		List messages = getMessagesAtLine( 69 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get myEntity with #sql{} with a;
	 * 1 validation message is expected.
	 */
	public void testLine70() {
		List messages = getMessagesAtLine( 70 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from ds from ds;
	 * 1 validation message is expected.
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from ds with a with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine72() {
		List messages = getMessagesAtLine( 72 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from ds using a using b;
	 * 1 validation message is expected.
	 */
	public void testLine73() {
		List messages = getMessagesAtLine( 73 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete from ds from rs;
	 * 1 validation message is expected.
	 */
	public void testLine74() {
		List messages = getMessagesAtLine( 74 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from rs using a using b;
	 * 1 validation message is expected.
	 */
	public void testLine75() {
		List messages = getMessagesAtLine( 75 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from ds for a for b;
	 * 1 validation message is expected.
	 */
	public void testLine76() {
		List messages = getMessagesAtLine( 76 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from ds with a with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine77() {
		List messages = getMessagesAtLine( 77 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds to rs;
	 * 1 validation message is expected.
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds for parent for parent;
	 * 1 validation message is expected.
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds with a with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine80() {
		List messages = getMessagesAtLine( 80 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds using a using b;
	 * 1 validation message is expected.
	 */
	public void testLine81() {
		List messages = getMessagesAtLine( 81 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine86() {
		List messages = getMessagesAtLine( 86 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open a from ds;
	 * 1 validation message is expected.
	 */
	public void testLine87() {
		List messages = getMessagesAtLine( 87 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs from a;
	 * 1 validation message is expected.
	 */
	public void testLine90() {
		List messages = getMessagesAtLine( 90 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs from myEntity;
	 * 1 validation message is expected.
	 */
	public void testLine91() {
		List messages = getMessagesAtLine( 91 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs with stmt;
	 * 0 validation messages are expected.
	 */
	public void testLine94() {
		List messages = getMessagesAtLine( 94 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs with #sql{stuff} from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine95() {
		List messages = getMessagesAtLine( 95 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs with a;
	 * 1 validation message is expected.
	 */
	public void testLine96() {
		List messages = getMessagesAtLine( 96 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs with #sql{stuff};
	 * 1 validation message is expected.
	 */
	public void testLine99() {
		List messages = getMessagesAtLine( 99 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs using a,b;
	 * 1 validation message is expected.
	 */
	public void testLine100() {
		List messages = getMessagesAtLine( 100 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs using a,b from ds for myentity;
	 * 0 validation messages are expected.
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs with stmt for parent;
	 * 1 validation message is expected.
	 */
	public void testLine105() {
		List messages = getMessagesAtLine( 105 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs for myentity from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine108() {
		List messages = getMessagesAtLine( 108 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs for nonEntity from ds;
	 * 1 validation message is expected.
	 */
	public void testLine109() {
		List messages = getMessagesAtLine( 109 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs for parent from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine110() {
		List messages = getMessagesAtLine( 110 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs for child from ds;
	 * 1 validation message is expected.
	 * It is expected to contain "no @Id".
	 */
	public void testLine111() {
		List messages = getMessagesAtLine( 111 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no @Id" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no @Id\" was issued." );
	}

	/*
	 * open rs for child.parent from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs for a from ds;
	 * 1 validation message is expected.
	 */
	public void testLine113() {
		List messages = getMessagesAtLine( 113 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs for parent.orphan from ds;
	 * 1 validation message is expected.
	 * It is expected to contain "assoc. not supported yet".
	 */
	public void testLine114() {
		List messages = getMessagesAtLine( 114 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "assoc. not supported yet" );
		if( messageWithSubstring == null ) fail( "No message with substring \"assoc. not supported yet\" was issued." );
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
	 * prepare ps with #sql{insert into t (c1) values (?)};
	 * 1 validation message is expected.
	 * It is expected to contain "missing FROM".
	 */
	public void testLine124() {
		List messages = getMessagesAtLine( 124 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "missing FROM" );
		if( messageWithSubstring == null ) fail( "No message with substring \"missing FROM\" was issued." );
	}

	/*
	 * prepare ps from ds;
	 * 1 validation message is expected.
	 * It is expected to contain "missing WITH".
	 */
	public void testLine126() {
		List messages = getMessagesAtLine( 126 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "missing WITH" );
		if( messageWithSubstring == null ) fail( "No message with substring \"missing WITH\" was issued." );
	}

	/*
	 * prepare ps from ds with expr;
	 * 0 validation messages are expected.
	 */
	public void testLine129() {
		List messages = getMessagesAtLine( 129 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * prepare ps from ds with expr2;
	 * 1 validation message is expected.
	 */
	public void testLine133() {
		List messages = getMessagesAtLine( 133 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * foreach (a from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine137() {
		List messages = getMessagesAtLine( 137 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * foreach (a, parent.id from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine138() {
		List messages = getMessagesAtLine( 138 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * foreach (parent.id from ds) end
	 * 1 validation message is expected.
	 * It is expected to contain "wrong data source type".
	 */
	public void testLine139() {
		List messages = getMessagesAtLine( 139 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "wrong data source type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"wrong data source type\" was issued." );
	}

	/*
	 * foreach (from rs) end
	 * 1 validation message is expected.
	 * It is expected to contain "missing target".
	 */
	public void testLine140() {
		List messages = getMessagesAtLine( 140 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "missing target" );
		if( messageWithSubstring == null ) fail( "No message with substring \"missing target\" was issued." );
	}

	/*
	 * foreach (parent.id from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine141() {
		List messages = getMessagesAtLine( 141 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * foreach (parent.id, child.name from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine142() {
		List messages = getMessagesAtLine( 142 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * foreach (parent.orphan from rs) end
	 * 1 validation message is expected.
	 * It is expected to contain "association not yet supported".
	 */
	public void testLine143() {
		List messages = getMessagesAtLine( 143 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "association not yet supported" );
		if( messageWithSubstring == null ) fail( "No message with substring \"association not yet supported\" was issued." );
	}

	/*
	 * foreach (parent.id, parent.id from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine144() {
		List messages = getMessagesAtLine( 144 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * foreach (parent from rs into a) end
	 * 1 validation message is expected.
	 * It is expected to contain "INTO not allowed".
	 */
	public void testLine145() {
		List messages = getMessagesAtLine( 145 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "INTO not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"INTO not allowed\" was issued." );
	}

	/*
	 * get myEntity from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine149() {
		List messages = getMessagesAtLine( 149 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get myEntity, myEntity from ds;
	 * 1 validation message is expected.
	 * It is expected to contain "only 1 non-primitive expr allowed".
	 */
	public void testLine150() {
		List messages = getMessagesAtLine( 150 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "only 1 non-primitive expr allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"only 1 non-primitive expr allowed\" was issued." );
	}

	/*
	 * get myEntity from ds with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine151() {
		List messages = getMessagesAtLine( 151 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get myEntity from rs with #sql{};
	 * 1 validation message is expected.
	 * It is expected to contain "must be sqldatasource".
	 */
	public void testLine152() {
		List messages = getMessagesAtLine( 152 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be sqldatasource" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be sqldatasource\" was issued." );
	}

	/*
	 * get parent.id from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine153() {
		List messages = getMessagesAtLine( 153 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get parent.child from ds;
	 * 1 validation message is expected.
	 * It is expected to contain "no @Id on child".
	 */
	public void testLine154() {
		List messages = getMessagesAtLine( 154 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no @Id on child" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no @Id on child\" was issued." );
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
	 * It is expected to contain "WITH required when not mapped to single table".
	 */
	public void testLine157() {
		List messages = getMessagesAtLine( 157 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "WITH required when not mapped to single table" );
		if( messageWithSubstring == null ) fail( "No message with substring \"WITH required when not mapped to single table\" was issued." );
	}

	/*
	 * get a, b from ds using a with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine158() {
		List messages = getMessagesAtLine( 158 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get basic from ds;
	 * 1 validation message is expected.
	 * It is expected to contain "no @Id".
	 */
	public void testLine159() {
		List messages = getMessagesAtLine( 159 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no @Id" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no @Id\" was issued." );
	}

	/*
	 * get basic from ds using a;
	 * 0 validation messages are expected.
	 */
	public void testLine160() {
		List messages = getMessagesAtLine( 160 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get basic from ds with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine161() {
		List messages = getMessagesAtLine( 161 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get dict from ds using a;
	 * 1 validation message is expected.
	 * It is expected to contain "- dictionary not yet supported".
	 */
	public void testLine162() {
		List messages = getMessagesAtLine( 162 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "- dictionary not yet supported" );
		if( messageWithSubstring == null ) fail( "No message with substring \"- dictionary not yet supported\" was issued." );
	}

	/*
	 * get array from ds using a;
	 * 0 validation messages are expected.
	 */
	public void testLine163() {
		List messages = getMessagesAtLine( 163 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get invalidArray from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine164() {
		List messages = getMessagesAtLine( 164 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get array from ds into a;
	 * 1 validation message is expected.
	 * It is expected to contain "INTO not allowed".
	 */
	public void testLine165() {
		List messages = getMessagesAtLine( 165 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "INTO not allowed" );
		if( messageWithSubstring == null ) fail( "No message with substring \"INTO not allowed\" was issued." );
	}

	/*
	 * get rs from ds with #sql{};
	 * 1 validation message is expected.
	 * It is expected to contain "rs is nullable but has no default constructor".
	 */
	public void testLine166() {
		List messages = getMessagesAtLine( 166 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "rs is nullable but has no default constructor" );
		if( messageWithSubstring == null ) fail( "No message with substring \"rs is nullable but has no default constructor\" was issued." );
	}

	/*
	 * add myEntity to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine170() {
		List messages = getMessagesAtLine( 170 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add myEntity, myEntity to ds;
	 * 1 validation message is expected.
	 */
	public void testLine171() {
		List messages = getMessagesAtLine( 171 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add a to ds;
	 * 1 validation message is expected.
	 * It is expected to contain "need FOR when not mapped to single table".
	 */
	public void testLine172() {
		List messages = getMessagesAtLine( 172 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "need FOR when not mapped to single table" );
		if( messageWithSubstring == null ) fail( "No message with substring \"need FOR when not mapped to single table\" was issued." );
	}

	/*
	 * add a, parent.id to ds;
	 * 1 validation message is expected.
	 * It is expected to contain "need FOR when not mapped to single table".
	 */
	public void testLine173() {
		List messages = getMessagesAtLine( 173 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "need FOR when not mapped to single table" );
		if( messageWithSubstring == null ) fail( "No message with substring \"need FOR when not mapped to single table\" was issued." );
	}

	/*
	 * add a to ds for parent;
	 * 0 validation messages are expected.
	 */
	public void testLine174() {
		List messages = getMessagesAtLine( 174 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add a, parent.id to ds for parent;
	 * 0 validation messages are expected.
	 */
	public void testLine175() {
		List messages = getMessagesAtLine( 175 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add parent.id to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine176() {
		List messages = getMessagesAtLine( 176 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add parent.id, child.name to ds;
	 * 1 validation message is expected.
	 * It is expected to contain "must be single table".
	 */
	public void testLine177() {
		List messages = getMessagesAtLine( 177 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be single table" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be single table\" was issued." );
	}

	/*
	 * add parent.id, child.name to ds for parent;
	 * 1 validation message is expected.
	 * It is expected to contain "must be single table".
	 */
	public void testLine178() {
		List messages = getMessagesAtLine( 178 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be single table" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be single table\" was issued." );
	}

	/*
	 * add parent.id, parent.id to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine179() {
		List messages = getMessagesAtLine( 179 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add myEntity to ds for myEntity;
	 * 0 validation messages are expected.
	 */
	public void testLine180() {
		List messages = getMessagesAtLine( 180 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add myEntity to rs for myEntity;
	 * 1 validation message is expected.
	 * It is expected to contain "no FOR when using result set".
	 */
	public void testLine181() {
		List messages = getMessagesAtLine( 181 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no FOR when using result set" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no FOR when using result set\" was issued." );
	}

	/*
	 * add myEntity to ds for parent;
	 * 1 validation message is expected.
	 * It is expected to contain "for type must match target type".
	 */
	public void testLine182() {
		List messages = getMessagesAtLine( 182 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "for type must match target type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"for type must match target type\" was issued." );
	}

	/*
	 * add parent.id to ds for a;
	 * 1 validation message is expected.
	 * It is expected to contain "invalid FOR type".
	 */
	public void testLine183() {
		List messages = getMessagesAtLine( 183 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "invalid FOR type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"invalid FOR type\" was issued." );
	}

	/*
	 * add parent.id to ds for parent.orphan;
	 * 1 validation message is expected.
	 * It is expected to contain "association not yet supported".
	 */
	public void testLine184() {
		List messages = getMessagesAtLine( 184 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "association not yet supported" );
		if( messageWithSubstring == null ) fail( "No message with substring \"association not yet supported\" was issued." );
	}

	/*
	 * add parent.id to ds for parent;
	 * 0 validation messages are expected.
	 */
	public void testLine185() {
		List messages = getMessagesAtLine( 185 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add parent.orphan to ds;
	 * 1 validation message is expected.
	 * It is expected to contain "association not yet supported".
	 */
	public void testLine186() {
		List messages = getMessagesAtLine( 186 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "association not yet supported" );
		if( messageWithSubstring == null ) fail( "No message with substring \"association not yet supported\" was issued." );
	}

	/*
	 * add o, parent.orphan to ds;
	 * 1 validation message is expected.
	 */
	public void testLine187() {
		List messages = getMessagesAtLine( 187 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add basic to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine188() {
		List messages = getMessagesAtLine( 188 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add multi to ds;
	 * 1 validation message is expected.
	 * It is expected to contain "must be single table".
	 */
	public void testLine189() {
		List messages = getMessagesAtLine( 189 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be single table" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be single table\" was issued." );
	}

	/*
	 * execute from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine193() {
		List messages = getMessagesAtLine( 193 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute from rs;
	 * 1 validation message is expected.
	 */
	public void testLine194() {
		List messages = getMessagesAtLine( 194 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from rs with ps;
	 * 0 validation messages are expected.
	 * One message is expected to contain "(FROM ignored when WITH is sqlstatement)".
	 */
	public void testLine195() {
		List messages = getMessagesAtLine( 195 );
		assertEquals( 0, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "(FROM ignored when WITH is sqlstatement)" );
		if( messageWithSubstring == null ) fail( "No message with substring \"(FROM ignored when WITH is sqlstatement)\" was issued." );
	}

	/*
	 * execute from rs with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine196() {
		List messages = getMessagesAtLine( 196 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from rs with a;
	 * 1 validation message is expected.
	 */
	public void testLine197() {
		List messages = getMessagesAtLine( 197 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from ds with a using b;
	 * 0 validation messages are expected.
	 */
	public void testLine198() {
		List messages = getMessagesAtLine( 198 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute from ds using b;
	 * 0 validation messages are expected.
	 */
	public void testLine199() {
		List messages = getMessagesAtLine( 199 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute using b;
	 * 0 validation messages are expected.
	 */
	public void testLine200() {
		List messages = getMessagesAtLine( 200 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine201() {
		List messages = getMessagesAtLine( 201 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete from ds;
	 * 1 validation message is expected.
	 * It is expected to contain "WITH required".
	 */
	public void testLine205() {
		List messages = getMessagesAtLine( 205 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "WITH required" );
		if( messageWithSubstring == null ) fail( "No message with substring \"WITH required\" was issued." );
	}

	/*
	 * delete from ds with a for basic;
	 * 1 validation message is expected.
	 * It is expected to contain "no FOR When no target".
	 */
	public void testLine206() {
		List messages = getMessagesAtLine( 206 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no FOR When no target" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no FOR When no target\" was issued." );
	}

	/*
	 * delete from ds with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine207() {
		List messages = getMessagesAtLine( 207 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete from ds with a;
	 * 0 validation messages are expected.
	 */
	public void testLine208() {
		List messages = getMessagesAtLine( 208 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete from ds using a;
	 * 1 validation message is expected.
	 * It is expected to contain "WITH required when no target and using sqldatasource".
	 */
	public void testLine209() {
		List messages = getMessagesAtLine( 209 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "WITH required when no target and using sqldatasource" );
		if( messageWithSubstring == null ) fail( "No message with substring \"WITH required when no target and using sqldatasource\" was issued." );
	}

	/*
	 * delete from rs;
	 * 0 validation messages are expected.
	 */
	public void testLine210() {
		List messages = getMessagesAtLine( 210 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete from rs with a;
	 * 1 validation message is expected.
	 * It is expected to contain "no WITH when no target and using sqlresultset".
	 */
	public void testLine211() {
		List messages = getMessagesAtLine( 211 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no WITH when no target and using sqlresultset" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no WITH when no target and using sqlresultset\" was issued." );
	}

	/*
	 * delete from rs with #sql{};
	 * 1 validation message is expected.
	 * It is expected to contain "no WITH when no target and using sqlresultset".
	 */
	public void testLine212() {
		List messages = getMessagesAtLine( 212 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no WITH when no target and using sqlresultset" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no WITH when no target and using sqlresultset\" was issued." );
	}

	/*
	 * delete from rs using a;
	 * 1 validation message is expected.
	 * It is expected to contain "no USING when no target and using sqlresultset".
	 */
	public void testLine213() {
		List messages = getMessagesAtLine( 213 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no USING when no target and using sqlresultset" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no USING when no target and using sqlresultset\" was issued." );
	}

	/*
	 * delete parent from ds for basic with #sql{};
	 * 1 validation message is expected.
	 * It is expected to contain "either FOR or WITH not both".
	 */
	public void testLine215() {
		List messages = getMessagesAtLine( 215 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "either FOR or WITH not both" );
		if( messageWithSubstring == null ) fail( "No message with substring \"either FOR or WITH not both\" was issued." );
	}

	/*
	 * delete parent from ds for parent;
	 * 0 validation messages are expected.
	 */
	public void testLine216() {
		List messages = getMessagesAtLine( 216 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete parent from ds for myEntity;
	 * 1 validation message is expected.
	 * It is expected to contain "FOR type must match target type".
	 */
	public void testLine217() {
		List messages = getMessagesAtLine( 217 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "FOR type must match target type" );
		if( messageWithSubstring == null ) fail( "No message with substring \"FOR type must match target type\" was issued." );
	}

	/*
	 * delete multi from ds;
	 * 1 validation message is expected.
	 * It is expected to contain "must be single table".
	 */
	public void testLine218() {
		List messages = getMessagesAtLine( 218 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be single table" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be single table\" was issued." );
	}

	/*
	 * delete parent from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine219() {
		List messages = getMessagesAtLine( 219 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete parent.id from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine220() {
		List messages = getMessagesAtLine( 220 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete parent.child from ds;
	 * 1 validation message is expected.
	 */
	public void testLine221() {
		List messages = getMessagesAtLine( 221 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete a from ds;
	 * 1 validation message is expected.
	 */
	public void testLine222() {
		List messages = getMessagesAtLine( 222 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent.orphan from ds using a;
	 * 1 validation message is expected.
	 * It is expected to contain "association not yet supported".
	 */
	public void testLine223() {
		List messages = getMessagesAtLine( 223 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "association not yet supported" );
		if( messageWithSubstring == null ) fail( "No message with substring \"association not yet supported\" was issued." );
	}

	/*
	 * delete basic from ds;
	 * 1 validation message is expected.
	 * It is expected to contain "when using sqldatasource and no USING or WITH, the target must have @Id".
	 */
	public void testLine224() {
		List messages = getMessagesAtLine( 224 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "when using sqldatasource and no USING or WITH, the target must have @Id" );
		if( messageWithSubstring == null ) fail( "No message with substring \"when using sqldatasource and no USING or WITH, the target must have @Id\" was issued." );
	}

	/*
	 * delete basic from ds with a;
	 * 0 validation messages are expected.
	 */
	public void testLine225() {
		List messages = getMessagesAtLine( 225 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete basic from ds with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine226() {
		List messages = getMessagesAtLine( 226 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete basic from ds using a;
	 * 0 validation messages are expected.
	 */
	public void testLine227() {
		List messages = getMessagesAtLine( 227 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete dict from ds using a;
	 * 1 validation message is expected.
	 * It is expected to contain "- dictionary not yet supported".
	 */
	public void testLine228() {
		List messages = getMessagesAtLine( 228 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "- dictionary not yet supported" );
		if( messageWithSubstring == null ) fail( "No message with substring \"- dictionary not yet supported\" was issued." );
	}

	/*
	 * delete array from ds using a;
	 * 1 validation message is expected.
	 * It is expected to contain "- array not yet supported in delete".
	 */
	public void testLine229() {
		List messages = getMessagesAtLine( 229 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "- array not yet supported in delete" );
		if( messageWithSubstring == null ) fail( "No message with substring \"- array not yet supported in delete\" was issued." );
	}

	/*
	 * delete invalidArray from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine230() {
		List messages = getMessagesAtLine( 230 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from ds for a;
	 * 1 validation message is expected.
	 * It is expected to contain "FOR is not an entity".
	 */
	public void testLine231() {
		List messages = getMessagesAtLine( 231 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "FOR is not an entity" );
		if( messageWithSubstring == null ) fail( "No message with substring \"FOR is not an entity\" was issued." );
	}

	/*
	 * replace parent to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine235() {
		List messages = getMessagesAtLine( 235 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * replace parent to ds using a with b;
	 * 0 validation messages are expected.
	 */
	public void testLine236() {
		List messages = getMessagesAtLine( 236 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * replace parent to ds using a for parent;
	 * 1 validation message is expected.
	 * It is expected to contain "no FOR".
	 */
	public void testLine237() {
		List messages = getMessagesAtLine( 237 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no FOR" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no FOR\" was issued." );
	}

	/*
	 * replace parent to ds with a for parent;
	 * 1 validation message is expected.
	 * It is expected to contain "no FOR".
	 */
	public void testLine238() {
		List messages = getMessagesAtLine( 238 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no FOR" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no FOR\" was issued." );
	}

	/*
	 * replace parent to ds with #sql{} for parent;
	 * 1 validation message is expected.
	 * It is expected to contain "no FOR".
	 */
	public void testLine239() {
		List messages = getMessagesAtLine( 239 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no FOR" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no FOR\" was issued." );
	}

	/*
	 * replace basic to ds;
	 * 1 validation message is expected.
	 * It is expected to contain "need @Id when missing WITH".
	 */
	public void testLine240() {
		List messages = getMessagesAtLine( 240 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "need @Id when missing WITH" );
		if( messageWithSubstring == null ) fail( "No message with substring \"need @Id when missing WITH\" was issued." );
	}

	/*
	 * replace basic to ds with a;
	 * 0 validation messages are expected.
	 */
	public void testLine241() {
		List messages = getMessagesAtLine( 241 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * replace basic to rs with a;
	 * 1 validation message is expected.
	 * It is expected to contain "no USING or WITH for resultset".
	 */
	public void testLine242() {
		List messages = getMessagesAtLine( 242 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no USING or WITH for resultset" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no USING or WITH for resultset\" was issued." );
	}

	/*
	 * replace basic to rs using a;
	 * 1 validation message is expected.
	 * It is expected to contain "no USING or WITH for resultset".
	 */
	public void testLine243() {
		List messages = getMessagesAtLine( 243 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no USING or WITH for resultset" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no USING or WITH for resultset\" was issued." );
	}

	/*
	 * replace a to ds;
	 * 1 validation message is expected.
	 * It is expected to contain "target not data expr".
	 */
	public void testLine244() {
		List messages = getMessagesAtLine( 244 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "target not data expr" );
		if( messageWithSubstring == null ) fail( "No message with substring \"target not data expr\" was issued." );
	}

	/*
	 * replace parent.id to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine245() {
		List messages = getMessagesAtLine( 245 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * replace multi to ds;
	 * 1 validation message is expected.
	 * It is expected to contain "no multiple tables".
	 */
	public void testLine246() {
		List messages = getMessagesAtLine( 246 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "no multiple tables" );
		if( messageWithSubstring == null ) fail( "No message with substring \"no multiple tables\" was issued." );
	}

	/*
	 * replace dict to ds with a;
	 * 1 validation message is expected.
	 * It is expected to contain "- dictionary not yet supported".
	 */
	public void testLine247() {
		List messages = getMessagesAtLine( 247 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "- dictionary not yet supported" );
		if( messageWithSubstring == null ) fail( "No message with substring \"- dictionary not yet supported\" was issued." );
	}

	/*
	 * replace array to ds with a;
	 * 1 validation message is expected.
	 * It is expected to contain "- array not yet supported in replace".
	 */
	public void testLine248() {
		List messages = getMessagesAtLine( 248 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "- array not yet supported in replace" );
		if( messageWithSubstring == null ) fail( "No message with substring \"- array not yet supported in replace\" was issued." );
	}

	/*
	 * replace invalidArray to ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine249() {
		List messages = getMessagesAtLine( 249 );
		assertEquals( 1, messages.size() );
	}
}
