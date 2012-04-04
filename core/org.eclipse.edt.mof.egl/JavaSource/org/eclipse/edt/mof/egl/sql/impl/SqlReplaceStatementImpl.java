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
import org.eclipse.edt.mof.egl.sql.SqlReplaceStatement;

public class SqlReplaceStatementImpl extends SqlIOStatementImpl implements SqlReplaceStatement {
	private static int Slot_setClause=0;
	private static int Slot_fromClause=1;
	private static int Slot_updateClause=2;
	private static int Slot_whereClause=3;
	private static int Slot_noCursor=4;
	private static int totalSlots = 5;
	
	public static int totalSlots() {
		return totalSlots + SqlIOStatementImpl.totalSlots();
	}
	
	static {
		int offset = SqlIOStatementImpl.totalSlots();
		Slot_setClause += offset;
		Slot_fromClause += offset;
		Slot_updateClause += offset;
		Slot_whereClause += offset;
		Slot_noCursor += offset;
	}

	@Override
	public List<SqlClause> getSqlClauses() {
		List<SqlClause> clauses = new ArrayList<SqlClause>();
		if (getSetClause() != null) clauses.add(getSetClause());
		if (getFromClause() != null) clauses.add(getFromClause());
		if (getUpdateClause() != null) clauses.add(getUpdateClause());
		if (getWhereClause() != null) clauses.add(getWhereClause());
		return clauses;
	}

	@Override
	public SqlClause getSetClause() {
		return (SqlClause)slotGet(Slot_setClause);
	}
	
	@Override
	public void setSetClause(SqlClause value) {
		slotSet(Slot_setClause, value);
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
	public SqlClause getUpdateClause() {
		return (SqlClause)slotGet(Slot_updateClause);
	}
	
	@Override
	public void setUpdateClause(SqlClause value) {
		slotSet(Slot_updateClause, value);
	}
	
	@Override
	public SqlClause getWhereClause() {
		return (SqlClause)slotGet(Slot_whereClause);
	}
	
	@Override
	public void setWhereClause(SqlClause value) {
		slotSet(Slot_whereClause, value);
	}
	
	@Override
	public Boolean noCursor() {
		return (Boolean)slotGet(Slot_noCursor);
	}
	
	@Override
	public void setNoCursor(Boolean value) {
		slotSet(Slot_noCursor, value);
	}
	
}
