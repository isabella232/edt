/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.sql.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.egl.sql.SqlAddStatement;
import org.eclipse.edt.mof.egl.sql.SqlClause;

public class SqlAddStatementImpl extends SqlIOStatementImpl implements SqlAddStatement {
	private static int Slot_columnsClause=0;
	private static int Slot_insertIntoClause=1;
	private static int Slot_valuesClause=2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + SqlIOStatementImpl.totalSlots();
	}
	
	static {
		int offset = SqlIOStatementImpl.totalSlots();
		Slot_columnsClause += offset;
		Slot_insertIntoClause += offset;
		Slot_valuesClause += offset;
	}

	@Override
	public List<SqlClause> getSqlClauses() {
		List<SqlClause> clauses = new ArrayList<SqlClause>();
		if (getColumnsClause() != null) clauses.add(getColumnsClause());
		if (getInsertIntoClause() != null) clauses.add(getInsertIntoClause());
		if (getValuesClause() != null) clauses.add(getValuesClause());
		return clauses;
	}

	@Override
	public SqlClause getColumnsClause() {
		return (SqlClause)slotGet(Slot_columnsClause);
	}
	
	@Override
	public void setColumnsClause(SqlClause value) {
		slotSet(Slot_columnsClause, value);
	}
	
	@Override
	public SqlClause getInsertIntoClause() {
		return (SqlClause)slotGet(Slot_insertIntoClause);
	}
	
	@Override
	public void setInsertIntoClause(SqlClause value) {
		slotSet(Slot_insertIntoClause, value);
	}
	
	@Override
	public SqlClause getValuesClause() {
		return (SqlClause)slotGet(Slot_valuesClause);
	}
	
	@Override
	public void setValuesClause(SqlClause value) {
		slotSet(Slot_valuesClause, value);
	}
	
}
