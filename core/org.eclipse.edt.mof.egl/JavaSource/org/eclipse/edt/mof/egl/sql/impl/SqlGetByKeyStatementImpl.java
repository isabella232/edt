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
import org.eclipse.edt.mof.egl.sql.SqlGetByKeyStatement;

public class SqlGetByKeyStatementImpl extends SqlIOStatementImpl implements SqlGetByKeyStatement {
	private static int Slot_callClause=0;
	private static int Slot_forUpdateOfClause=1;
	private static int Slot_fromClause=2;
	private static int Slot_groupByClause=3;
	private static int Slot_havingClause=4;
	private static int Slot_orderByClause=5;
	private static int Slot_selectClause=6;
	private static int Slot_whereClause=7;
	private static int Slot_isSingleRowSelect=8;
	private static int Slot_isForUpdate=9;
	private static int totalSlots = 10;
	
	public static int totalSlots() {
		return totalSlots + SqlIOStatementImpl.totalSlots();
	}
	
	static {
		int offset = SqlIOStatementImpl.totalSlots();
		Slot_callClause += offset;
		Slot_forUpdateOfClause += offset;
		Slot_fromClause += offset;
		Slot_groupByClause += offset;
		Slot_havingClause += offset;
		Slot_orderByClause += offset;
		Slot_selectClause += offset;
		Slot_whereClause += offset;
		Slot_isSingleRowSelect += offset;
		Slot_isForUpdate += offset;
	}

	@Override
	public List<SqlClause> getSqlClauses() {
		List<SqlClause> clauses = new ArrayList<SqlClause>();
		if (getSelectClause() != null) clauses.add(getSelectClause());
		if (getFromClause() != null) clauses.add(getFromClause());
		if (getWhereClause() != null) clauses.add(getWhereClause());
		if (getOrderByClause() != null) clauses.add(getOrderByClause());
		if (getGroupByClause() != null) clauses.add(getGroupByClause());
		if (getCallClause() != null) clauses.add(getCallClause());
		if (getForUpdateOfClause() != null) clauses.add(getForUpdateOfClause());
		return clauses;
	}

	@Override
	public SqlClause getCallClause() {
		return (SqlClause)slotGet(Slot_callClause);
	}
	
	@Override
	public void setCallClause(SqlClause value) {
		slotSet(Slot_callClause, value);
	}
	
	@Override
	public SqlClause getForUpdateOfClause() {
		return (SqlClause)slotGet(Slot_forUpdateOfClause);
	}
	
	@Override
	public void setForUpdateOfClause(SqlClause value) {
		slotSet(Slot_forUpdateOfClause, value);
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
	public SqlClause getGroupByClause() {
		return (SqlClause)slotGet(Slot_groupByClause);
	}
	
	@Override
	public void setGroupByClause(SqlClause value) {
		slotSet(Slot_groupByClause, value);
	}
	
	@Override
	public SqlClause getHavingClause() {
		return (SqlClause)slotGet(Slot_havingClause);
	}
	
	@Override
	public void setHavingClause(SqlClause value) {
		slotSet(Slot_havingClause, value);
	}
	
	@Override
	public SqlClause getOrderByClause() {
		return (SqlClause)slotGet(Slot_orderByClause);
	}
	
	@Override
	public void setOrderByClause(SqlClause value) {
		slotSet(Slot_orderByClause, value);
	}
	
	@Override
	public SqlClause getSelectClause() {
		return (SqlClause)slotGet(Slot_selectClause);
	}
	
	@Override
	public void setSelectClause(SqlClause value) {
		slotSet(Slot_selectClause, value);
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
	public Boolean isSingleRowSelect() {
		return (Boolean)slotGet(Slot_isSingleRowSelect);
	}
	
	@Override
	public void setIsSingleRowSelect(Boolean value) {
		slotSet(Slot_isSingleRowSelect, value);
	}
	
	@Override
	public Boolean isForUpdate() {
		return (Boolean)slotGet(Slot_isForUpdate);
	}
	
	@Override
	public void setIsForUpdate(Boolean value) {
		slotSet(Slot_isForUpdate, value);
	}
	
}
