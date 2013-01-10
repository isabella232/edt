/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
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
	public void testLine65() {
		List messages = getMessagesAtLine( 65 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs for myEntity from ds for myEntity;
	 * 1 validation message is expected.
	 */
	public void testLine66() {
		List messages = getMessagesAtLine( 66 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs using a,b with stmt using b,a;
	 * 1 validation message is expected.
	 */
	public void testLine67() {
		List messages = getMessagesAtLine( 67 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs with #sql{stuff} with stmt from ds;
	 * 1 validation message is expected.
	 */
	public void testLine68() {
		List messages = getMessagesAtLine( 68 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add parent to ds to rs;
	 * 1 validation message is expected.
	 */
	public void testLine69() {
		List messages = getMessagesAtLine( 69 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add parent to ds for parent for myEntity;
	 * 1 validation message is expected.
	 */
	public void testLine70() {
		List messages = getMessagesAtLine( 70 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get myEntity from rs from ds;
	 * 1 validation message is expected.
	 */
	public void testLine71() {
		List messages = getMessagesAtLine( 71 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get myEntity using a using b;
	 * 1 validation message is expected.
	 */
	public void testLine72() {
		List messages = getMessagesAtLine( 72 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get myEntity with #sql{} with a;
	 * 1 validation message is expected.
	 */
	public void testLine73() {
		List messages = getMessagesAtLine( 73 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from ds from ds;
	 * 1 validation message is expected.
	 */
	public void testLine74() {
		List messages = getMessagesAtLine( 74 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from ds with a with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine75() {
		List messages = getMessagesAtLine( 75 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from ds using a using b;
	 * 1 validation message is expected.
	 */
	public void testLine76() {
		List messages = getMessagesAtLine( 76 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from rs using a using b;
	 * 1 validation message is expected.
	 */
	public void testLine77() {
		List messages = getMessagesAtLine( 77 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from ds for parent for parent;
	 * 1 validation message is expected.
	 */
	public void testLine78() {
		List messages = getMessagesAtLine( 78 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from ds with a with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine79() {
		List messages = getMessagesAtLine( 79 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds to rs;
	 * 1 validation message is expected.
	 */
	public void testLine80() {
		List messages = getMessagesAtLine( 80 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds for parent for parent;
	 * 1 validation message is expected.
	 */
	public void testLine81() {
		List messages = getMessagesAtLine( 81 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds with a with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine82() {
		List messages = getMessagesAtLine( 82 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds using a using b;
	 * 1 validation message is expected.
	 */
	public void testLine83() {
		List messages = getMessagesAtLine( 83 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine88() {
		List messages = getMessagesAtLine( 88 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open a from ds;
	 * 1 validation message is expected.
	 */
	public void testLine89() {
		List messages = getMessagesAtLine( 89 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs from a;
	 * 1 validation message is expected.
	 */
	public void testLine92() {
		List messages = getMessagesAtLine( 92 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs from myEntity;
	 * 1 validation message is expected.
	 */
	public void testLine93() {
		List messages = getMessagesAtLine( 93 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs with stmt;
	 * 0 validation messages are expected.
	 */
	public void testLine96() {
		List messages = getMessagesAtLine( 96 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs with #sql{stuff} from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine97() {
		List messages = getMessagesAtLine( 97 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs with a;
	 * 1 validation message is expected.
	 */
	public void testLine98() {
		List messages = getMessagesAtLine( 98 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs with #sql{stuff};
	 * 1 validation message is expected.
	 */
	public void testLine101() {
		List messages = getMessagesAtLine( 101 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs using a,b;
	 * 1 validation message is expected.
	 */
	public void testLine102() {
		List messages = getMessagesAtLine( 102 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs using a,b from ds for myentity;
	 * 0 validation messages are expected.
	 */
	public void testLine104() {
		List messages = getMessagesAtLine( 104 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs with stmt for parent;
	 * 1 validation message is expected.
	 */
	public void testLine107() {
		List messages = getMessagesAtLine( 107 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs for myentity from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine110() {
		List messages = getMessagesAtLine( 110 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs for nonEntity from ds;
	 * 1 validation message is expected.
	 */
	public void testLine111() {
		List messages = getMessagesAtLine( 111 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs for parent from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine112() {
		List messages = getMessagesAtLine( 112 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs for child from ds;
	 * 1 validation message is expected.
	 */
	public void testLine114() {
		List messages = getMessagesAtLine( 114 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs for child.parent from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine115() {
		List messages = getMessagesAtLine( 115 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * open rs for a from ds;
	 * 1 validation message is expected.
	 */
	public void testLine116() {
		List messages = getMessagesAtLine( 116 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs for parent.orphan from ds;
	 * 1 validation message is expected.
	 */
	public void testLine118() {
		List messages = getMessagesAtLine( 118 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * open rs from ds into a ;
	 * 1 validation message is expected.
	 */
	public void testLine119() {
		List messages = getMessagesAtLine( 119 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * prepare ps from ds with #sql{insert into t (c1) values (?)};
	 * 0 validation messages are expected.
	 */
	public void testLine123() {
		List messages = getMessagesAtLine( 123 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * prepare ds from ds with #sql{insert into t (c1) values (?)};
	 * 1 validation message is expected.
	 */
	public void testLine126() {
		List messages = getMessagesAtLine( 126 );
		assertEquals( 1, messages.size() );
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
	 */
	public void testLine140() {
		List messages = getMessagesAtLine( 140 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * foreach (parent.id from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine142() {
		List messages = getMessagesAtLine( 142 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * foreach (parent.id, child.name from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine143() {
		List messages = getMessagesAtLine( 143 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * foreach (parent.orphan from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine145() {
		List messages = getMessagesAtLine( 145 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * foreach (parent.id, parent.id from rs) end
	 * 0 validation messages are expected.
	 */
	public void testLine146() {
		List messages = getMessagesAtLine( 146 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get myEntity from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine150() {
		List messages = getMessagesAtLine( 150 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get myEntity, myEntity from ds;
	 * 1 validation message is expected.
	 */
	public void testLine152() {
		List messages = getMessagesAtLine( 152 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get myEntity from ds with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine153() {
		List messages = getMessagesAtLine( 153 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get myEntity from rs with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine155() {
		List messages = getMessagesAtLine( 155 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get parent.id from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine156() {
		List messages = getMessagesAtLine( 156 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get parent.child from ds;
	 * 1 validation message is expected.
	 */
	public void testLine158() {
		List messages = getMessagesAtLine( 158 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get parent from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine159() {
		List messages = getMessagesAtLine( 159 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get parent.child from ds using a;
	 * 0 validation messages are expected.
	 */
	public void testLine160() {
		List messages = getMessagesAtLine( 160 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get a, b from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine162() {
		List messages = getMessagesAtLine( 162 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get a, b from ds using a with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine163() {
		List messages = getMessagesAtLine( 163 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get basic from ds;
	 * 1 validation message is expected.
	 */
	public void testLine165() {
		List messages = getMessagesAtLine( 165 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get basic from ds using a;
	 * 0 validation messages are expected.
	 */
	public void testLine166() {
		List messages = getMessagesAtLine( 166 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get basic from ds with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine167() {
		List messages = getMessagesAtLine( 167 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get dict from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine169() {
		List messages = getMessagesAtLine( 169 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get array from ds using a;
	 * 0 validation messages are expected.
	 */
	public void testLine170() {
		List messages = getMessagesAtLine( 170 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * get invalidArray from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine171() {
		List messages = getMessagesAtLine( 171 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get array from ds into a;
	 * 1 validation message is expected.
	 */
	public void testLine173() {
		List messages = getMessagesAtLine( 173 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * get rs from ds with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine175() {
		List messages = getMessagesAtLine( 175 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add myEntity to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine179() {
		List messages = getMessagesAtLine( 179 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add myEntity, myEntity to ds;
	 * 1 validation message is expected.
	 */
	public void testLine180() {
		List messages = getMessagesAtLine( 180 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add a to ds;
	 * 1 validation message is expected.
	 */
	public void testLine182() {
		List messages = getMessagesAtLine( 182 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add a, parent.id to ds;
	 * 1 validation message is expected.
	 */
	public void testLine184() {
		List messages = getMessagesAtLine( 184 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add a to ds for parent;
	 * 0 validation messages are expected.
	 */
	public void testLine185() {
		List messages = getMessagesAtLine( 185 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add a, parent.id to ds for parent;
	 * 0 validation messages are expected.
	 */
	public void testLine186() {
		List messages = getMessagesAtLine( 186 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add parent.id to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine187() {
		List messages = getMessagesAtLine( 187 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add parent.id, child.name to ds;
	 * 1 validation message is expected.
	 */
	public void testLine189() {
		List messages = getMessagesAtLine( 189 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add parent.id, child.name to ds for parent;
	 * 0 validation messages are expected.
	 */
	public void testLine190() {
		List messages = getMessagesAtLine( 190 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add parent.id, parent.id to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine191() {
		List messages = getMessagesAtLine( 191 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add myEntity to ds for myEntity;
	 * 0 validation messages are expected.
	 */
	public void testLine192() {
		List messages = getMessagesAtLine( 192 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add myEntity to rs for myEntity;
	 * 1 validation message is expected.
	 */
	public void testLine194() {
		List messages = getMessagesAtLine( 194 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add myEntity to ds for parent;
	 * 1 validation message is expected.
	 */
	public void testLine196() {
		List messages = getMessagesAtLine( 196 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add parent.id to ds for a;
	 * 1 validation message is expected.
	 */
	public void testLine198() {
		List messages = getMessagesAtLine( 198 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add parent.id to ds for parent.orphan;
	 * 1 validation message is expected.
	 */
	public void testLine200() {
		List messages = getMessagesAtLine( 200 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add parent.id to ds for parent;
	 * 0 validation messages are expected.
	 */
	public void testLine201() {
		List messages = getMessagesAtLine( 201 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add parent.orphan to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine203() {
		List messages = getMessagesAtLine( 203 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add o, parent.orphan to ds;
	 * 1 validation message is expected.
	 */
	public void testLine204() {
		List messages = getMessagesAtLine( 204 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * add basic to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine205() {
		List messages = getMessagesAtLine( 205 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * add multi to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine207() {
		List messages = getMessagesAtLine( 207 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine211() {
		List messages = getMessagesAtLine( 211 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute from rs;
	 * 1 validation message is expected.
	 */
	public void testLine212() {
		List messages = getMessagesAtLine( 212 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from rs with ps;
	 * 0 validation messages are expected.
	 */
	public void testLine214() {
		List messages = getMessagesAtLine( 214 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute from rs with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine215() {
		List messages = getMessagesAtLine( 215 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from rs with a;
	 * 1 validation message is expected.
	 */
	public void testLine216() {
		List messages = getMessagesAtLine( 216 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * execute from ds with a using b;
	 * 0 validation messages are expected.
	 */
	public void testLine217() {
		List messages = getMessagesAtLine( 217 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute from ds using b;
	 * 0 validation messages are expected.
	 */
	public void testLine218() {
		List messages = getMessagesAtLine( 218 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * execute using b;
	 * 1 validation message is expected.
	 * It is expected to contain "No compiler extensions were found for the execute statement, which has no default behavior. The execute statement cannot be used until an extension has been configured for this statement.".
	 */
	public void testLine219() {
		List messages = getMessagesAtLine( 219 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "No compiler extensions were found for the execute statement, which has no default behavior. The execute statement cannot be used until an extension has been configured for this statement." );
		if( messageWithSubstring == null ) fail( "No message with substring \"No compiler extensions were found for the execute statement, which has no default behavior. The execute statement cannot be used until an extension has been configured for this statement.\" was issued." );
	}

	/*
	 * execute with #sql{};
	 * 1 validation message is expected.
	 * It is expected to contain "No compiler extensions were found for the execute statement, which has no default behavior. The execute statement cannot be used until an extension has been configured for this statement.".
	 */
	public void testLine220() {
		List messages = getMessagesAtLine( 220 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "No compiler extensions were found for the execute statement, which has no default behavior. The execute statement cannot be used until an extension has been configured for this statement." );
		if( messageWithSubstring == null ) fail( "No message with substring \"No compiler extensions were found for the execute statement, which has no default behavior. The execute statement cannot be used until an extension has been configured for this statement.\" was issued." );
	}

	/*
	 * execute with #sql{} from jndi;
	 * 0 validation messages are expected.
	 */
	public void testLine221() {
		List messages = getMessagesAtLine( 221 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete from ds;
	 * 1 validation message is expected.
	 */
	public void testLine226() {
		List messages = getMessagesAtLine( 226 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete from ds with a for basic;
	 * 1 validation message is expected.
	 */
	public void testLine228() {
		List messages = getMessagesAtLine( 228 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete from ds with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine229() {
		List messages = getMessagesAtLine( 229 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete from ds with a;
	 * 0 validation messages are expected.
	 */
	public void testLine230() {
		List messages = getMessagesAtLine( 230 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine232() {
		List messages = getMessagesAtLine( 232 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete from rs;
	 * 0 validation messages are expected.
	 */
	public void testLine233() {
		List messages = getMessagesAtLine( 233 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete from rs with a;
	 * 1 validation message is expected.
	 */
	public void testLine235() {
		List messages = getMessagesAtLine( 235 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete from rs with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine237() {
		List messages = getMessagesAtLine( 237 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete from rs using a;
	 * 1 validation message is expected.
	 */
	public void testLine239() {
		List messages = getMessagesAtLine( 239 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from ds for basic with #sql{};
	 * 1 validation message is expected.
	 */
	public void testLine242() {
		List messages = getMessagesAtLine( 242 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from ds for parent;
	 * 0 validation messages are expected.
	 */
	public void testLine243() {
		List messages = getMessagesAtLine( 243 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete parent from ds for myEntity;
	 * 1 validation message is expected.
	 */
	public void testLine245() {
		List messages = getMessagesAtLine( 245 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete multi from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine247() {
		List messages = getMessagesAtLine( 247 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete parent from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine248() {
		List messages = getMessagesAtLine( 248 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete parent.id from ds;
	 * 0 validation messages are expected.
	 */
	public void testLine249() {
		List messages = getMessagesAtLine( 249 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete parent.child from ds;
	 * 1 validation message is expected.
	 */
	public void testLine250() {
		List messages = getMessagesAtLine( 250 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete a from ds;
	 * 1 validation message is expected.
	 */
	public void testLine251() {
		List messages = getMessagesAtLine( 251 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent.orphan from ds using a;
	 * 0 validation messages are expected.
	 */
	public void testLine253() {
		List messages = getMessagesAtLine( 253 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete basic from ds;
	 * 1 validation message is expected.
	 */
	public void testLine255() {
		List messages = getMessagesAtLine( 255 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete basic from ds with a;
	 * 0 validation messages are expected.
	 */
	public void testLine256() {
		List messages = getMessagesAtLine( 256 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete basic from ds with #sql{};
	 * 0 validation messages are expected.
	 */
	public void testLine257() {
		List messages = getMessagesAtLine( 257 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete basic from ds using a;
	 * 0 validation messages are expected.
	 */
	public void testLine258() {
		List messages = getMessagesAtLine( 258 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * delete dict from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine260() {
		List messages = getMessagesAtLine( 260 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete array from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine262() {
		List messages = getMessagesAtLine( 262 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete invalidArray from ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine263() {
		List messages = getMessagesAtLine( 263 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * delete parent from ds for a;
	 * 1 validation message is expected.
	 */
	public void testLine265() {
		List messages = getMessagesAtLine( 265 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine269() {
		List messages = getMessagesAtLine( 269 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * replace parent to ds using a with b;
	 * 0 validation messages are expected.
	 */
	public void testLine270() {
		List messages = getMessagesAtLine( 270 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * replace parent to ds using a for parent;
	 * 1 validation message is expected.
	 */
	public void testLine272() {
		List messages = getMessagesAtLine( 272 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds with a for parent;
	 * 1 validation message is expected.
	 */
	public void testLine274() {
		List messages = getMessagesAtLine( 274 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent to ds with #sql{} for parent;
	 * 1 validation message is expected.
	 */
	public void testLine276() {
		List messages = getMessagesAtLine( 276 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace basic to ds;
	 * 1 validation message is expected.
	 */
	public void testLine278() {
		List messages = getMessagesAtLine( 278 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace basic to ds with a;
	 * 0 validation messages are expected.
	 */
	public void testLine279() {
		List messages = getMessagesAtLine( 279 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * replace basic to rs with a;
	 * 1 validation message is expected.
	 */
	public void testLine281() {
		List messages = getMessagesAtLine( 281 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace basic to rs using a;
	 * 1 validation message is expected.
	 */
	public void testLine283() {
		List messages = getMessagesAtLine( 283 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace a to ds;
	 * 1 validation message is expected.
	 */
	public void testLine285() {
		List messages = getMessagesAtLine( 285 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace parent.id to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine286() {
		List messages = getMessagesAtLine( 286 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * replace multi to ds;
	 * 0 validation messages are expected.
	 */
	public void testLine288() {
		List messages = getMessagesAtLine( 288 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * replace dict to ds with a;
	 * 1 validation message is expected.
	 */
	public void testLine290() {
		List messages = getMessagesAtLine( 290 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace array to ds with a;
	 * 1 validation message is expected.
	 */
	public void testLine292() {
		List messages = getMessagesAtLine( 292 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * replace invalidArray to ds using a;
	 * 1 validation message is expected.
	 */
	public void testLine293() {
		List messages = getMessagesAtLine( 293 );
		assertEquals( 1, messages.size() );
	}

	/*
	 * a int{@SQLResultSetControl};
	 * 1 validation message is expected.
	 * It is expected to contain "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.".
	 */
	public void testLine297() {
		List messages = getMessagesAtLine( 297 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields." );
		if( messageWithSubstring == null ) fail( "No message with substring \"@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.\" was issued." );
	}

	/*
	 * b int{@SQLResultSetControl{scrollablity = TYPE_FORWARD_ONLY}};
	 * 1 validation message is expected.
	 * It is expected to contain "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.".
	 */
	public void testLine298() {
		List messages = getMessagesAtLine( 298 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields." );
		if( messageWithSubstring == null ) fail( "No message with substring \"@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.\" was issued." );
	}

	/*
	 * c int{@SQLResultSetControl{}};
	 * 1 validation message is expected.
	 * It is expected to contain "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.".
	 */
	public void testLine299() {
		List messages = getMessagesAtLine( 299 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields." );
		if( messageWithSubstring == null ) fail( "No message with substring \"@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.\" was issued." );
	}

	/*
	 * d sqldatasource?{@SQLResultSetControl};
	 * 1 validation message is expected.
	 * It is expected to contain "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.".
	 */
	public void testLine300() {
		List messages = getMessagesAtLine( 300 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields." );
		if( messageWithSubstring == null ) fail( "No message with substring \"@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.\" was issued." );
	}

	/*
	 * e sqlresultset?{@SQLResultSetControl{scrollablity = TYPE_FORWARD_ONLY}};
	 * 0 validation messages are expected.
	 */
	public void testLine301() {
		List messages = getMessagesAtLine( 301 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * f sqlstatement?{@SQLResultSetControl{holdability = CLOSE_CURSORS_AT_COMMIT}};
	 * 0 validation messages are expected.
	 */
	public void testLine302() {
		List messages = getMessagesAtLine( 302 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * rsc1 int{@SQLResultSetControl};
	 * 1 validation message is expected.
	 * It is expected to contain "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.".
	 */
	public void testLine306() {
		List messages = getMessagesAtLine( 306 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields." );
		if( messageWithSubstring == null ) fail( "No message with substring \"@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.\" was issued." );
	}

	/*
	 * rsc2 int{@SQLResultSetControl{scrollablity = TYPE_FORWARD_ONLY}};
	 * 1 validation message is expected.
	 * It is expected to contain "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.".
	 */
	public void testLine307() {
		List messages = getMessagesAtLine( 307 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields." );
		if( messageWithSubstring == null ) fail( "No message with substring \"@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.\" was issued." );
	}

	/*
	 * rsc3 int{@SQLResultSetControl{}};
	 * 1 validation message is expected.
	 * It is expected to contain "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.".
	 */
	public void testLine308() {
		List messages = getMessagesAtLine( 308 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields." );
		if( messageWithSubstring == null ) fail( "No message with substring \"@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.\" was issued." );
	}

	/*
	 * rsc4 sqldatasource?{@SQLResultSetControl};
	 * 1 validation message is expected.
	 * It is expected to contain "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.".
	 */
	public void testLine309() {
		List messages = getMessagesAtLine( 309 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields." );
		if( messageWithSubstring == null ) fail( "No message with substring \"@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.\" was issued." );
	}

	/*
	 * rsc5 sqlresultset?{@SQLResultSetControl{scrollablity = TYPE_FORWARD_ONLY}};
	 * 0 validation messages are expected.
	 */
	public void testLine310() {
		List messages = getMessagesAtLine( 310 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * rsc6 sqlstatement?{@SQLResultSetControl{holdability = CLOSE_CURSORS_AT_COMMIT}};
	 * 0 validation messages are expected.
	 */
	public void testLine311() {
		List messages = getMessagesAtLine( 311 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * rsc1 int{@SQLResultSetControl};
	 * 1 validation message is expected.
	 * It is expected to contain "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.".
	 */
	public void testLine315() {
		List messages = getMessagesAtLine( 315 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields." );
		if( messageWithSubstring == null ) fail( "No message with substring \"@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.\" was issued." );
	}

	/*
	 * rsc2 int{@SQLResultSetControl{scrollablity = TYPE_FORWARD_ONLY}};
	 * 1 validation message is expected.
	 * It is expected to contain "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.".
	 */
	public void testLine316() {
		List messages = getMessagesAtLine( 316 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields." );
		if( messageWithSubstring == null ) fail( "No message with substring \"@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.\" was issued." );
	}

	/*
	 * rsc3 int{@SQLResultSetControl{}};
	 * 1 validation message is expected.
	 * It is expected to contain "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.".
	 */
	public void testLine317() {
		List messages = getMessagesAtLine( 317 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields." );
		if( messageWithSubstring == null ) fail( "No message with substring \"@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.\" was issued." );
	}

	/*
	 * rsc4 sqldatasource?{@SQLResultSetControl};
	 * 1 validation message is expected.
	 * It is expected to contain "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.".
	 */
	public void testLine318() {
		List messages = getMessagesAtLine( 318 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields." );
		if( messageWithSubstring == null ) fail( "No message with substring \"@SQLResultSetControl is only valid on SQLResultSet and SQLStatement fields.\" was issued." );
	}

	/*
	 * rsc5 sqlresultset?{@SQLResultSetControl{scrollablity = TYPE_FORWARD_ONLY}};
	 * 0 validation messages are expected.
	 */
	public void testLine319() {
		List messages = getMessagesAtLine( 319 );
		assertEquals( 0, messages.size() );
	}

	/*
	 * rsc6 sqlstatement?{@SQLResultSetControl{holdability = CLOSE_CURSORS_AT_COMMIT}};
	 * 0 validation messages are expected.
	 */
	public void testLine320() {
		List messages = getMessagesAtLine( 320 );
		assertEquals( 0, messages.size() );
	}
}
