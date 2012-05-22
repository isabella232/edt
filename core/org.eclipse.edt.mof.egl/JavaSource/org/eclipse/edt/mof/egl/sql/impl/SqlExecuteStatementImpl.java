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

import org.eclipse.edt.mof.egl.ExecuteStatement;
import org.eclipse.edt.mof.egl.sql.SqlClause;
import org.eclipse.edt.mof.egl.sql.SqlExecuteStatement;
import org.eclipse.edt.mof.egl.sql.SqlIOStatement;
import org.eclipse.edt.mof.egl.sql.impl.SqlIOStatementImpl;


public class SqlExecuteStatementImpl extends SqlIOStatementImpl implements SqlExecuteStatement {
	private static int Slot_isDelete=0;
	private static int Slot_isInsert=1;
	private static int Slot_isUpdate=2;
	private static int Slot_sqlClause=3;
	private static int totalSlots = 4;
	
	public static int totalSlots() {
		return totalSlots + SqlIOStatementImpl.totalSlots();
	}
	
	static {
		int offset = SqlIOStatementImpl.totalSlots();
		Slot_isDelete += offset;
		Slot_isInsert += offset;
		Slot_isUpdate += offset;
		Slot_sqlClause += offset;
	}

	@Override
	public List<SqlClause> getSqlClauses() {
		List<SqlClause> clauses = new ArrayList<SqlClause>();
		if (getSqlClause() != null) clauses.add(getSqlClause());
		return clauses;
	}

	@Override
	public Boolean isDelete() {
		return (Boolean)slotGet(Slot_isDelete);
	}
	
	@Override
	public void setIsDelete(Boolean value) {
		slotSet(Slot_isDelete, value);
	}
	
	@Override
	public Boolean isInsert() {
		return (Boolean)slotGet(Slot_isInsert);
	}
	
	@Override
	public void setIsInsert(Boolean value) {
		slotSet(Slot_isInsert, value);
	}
	
	@Override
	public Boolean isUpdate() {
		return (Boolean)slotGet(Slot_isUpdate);
	}
	
	@Override
	public void setIsUpdate(Boolean value) {
		slotSet(Slot_isUpdate, value);
	}
	
	@Override
	public SqlClause getSqlClause() {
		return (SqlClause)slotGet(Slot_sqlClause);
	}
	
	@Override
	public void setSqlClause(SqlClause value) {
		slotSet(Slot_sqlClause, value);
	}
	
}
