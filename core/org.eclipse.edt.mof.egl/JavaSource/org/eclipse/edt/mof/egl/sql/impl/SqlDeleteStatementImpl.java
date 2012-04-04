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

import org.eclipse.edt.mof.egl.sql.SqlClause;
import org.eclipse.edt.mof.egl.sql.SqlDeleteStatement;

public class SqlDeleteStatementImpl extends SqlIOStatementImpl implements SqlDeleteStatement {
	private static int Slot_deleteClause=0;
	private static int Slot_fromClause=1;
	private static int Slot_noCursor=2;
	private static int Slot_whereClause=3;
	private static int totalSlots = 4;
	
	public static int totalSlots() {
		return totalSlots + SqlIOStatementImpl.totalSlots();
	}
	
	static {
		int offset = SqlIOStatementImpl.totalSlots();
		Slot_deleteClause += offset;
		Slot_fromClause += offset;
		Slot_noCursor += offset;
		Slot_whereClause += offset;
	}

	@Override
	public List<SqlClause> getSqlClauses() {
		List<SqlClause> clauses = new ArrayList<SqlClause>();
		if (getDeleteClause() != null) clauses.add(getDeleteClause());
		if (getFromClause() != null) clauses.add(getFromClause());
		if (getWhereClause() != null) clauses.add(getWhereClause());
		return clauses;
	}

	@Override
	public SqlClause getDeleteClause() {
		return (SqlClause)slotGet(Slot_deleteClause);
	}
	
	@Override
	public void setDeleteClause(SqlClause value) {
		slotSet(Slot_deleteClause, value);
	}
	
	@Override
	public SqlClause getFromClause() {
		return (SqlClause)slotGet(Slot_fromClause);
	}
	
	@Override
	public void setFromClause(SqlClause value) {
		slotSet(Slot_fromClause, value);
	}
	
	@Override
	public Boolean noCursor() {
		return (Boolean)slotGet(Slot_noCursor);
	}
	
	@Override
	public void setNoCursor(Boolean value) {
		slotSet(Slot_noCursor, value);
	}
	
	@Override
	public SqlClause getWhereClause() {
		return (SqlClause)slotGet(Slot_whereClause);
	}
	
	@Override
	public void setWhereClause(SqlClause value) {
		slotSet(Slot_whereClause, value);
	}
	
}
