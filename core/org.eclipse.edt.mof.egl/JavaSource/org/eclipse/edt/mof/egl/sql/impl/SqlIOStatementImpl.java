/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
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

import java.util.Collections;
import java.util.List;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.impl.IOStatementImpl;
import org.eclipse.edt.mof.egl.sql.SqlClause;
import org.eclipse.edt.mof.egl.sql.SqlIOStatement;


public abstract class SqlIOStatementImpl extends IOStatementImpl implements SqlIOStatement {
	private static int Slot_preparedStatementId=0;
	private static int Slot_resultSetIdentifier=1;
	private static int Slot_intoExpressions=2;
	private static int Slot_hasExplicitSql=3;
	private static int totalSlots = 4;
	
	public static int totalSlots() {
		return totalSlots + IOStatementImpl.totalSlots();
	}
	
	static {
		int offset = IOStatementImpl.totalSlots();
		Slot_preparedStatementId += offset;
		Slot_resultSetIdentifier += offset;
		Slot_intoExpressions += offset;
		Slot_hasExplicitSql += offset;
	}
	
	@Override
	public List<SqlClause> getSqlClauses() {
		return Collections.emptyList();
	}
	
	@Override
	public String getPreparedStatementId() {
		return (String)slotGet(Slot_preparedStatementId);
	}
	
	@Override
	public void setPreparedStatementId(String value) {
		slotSet(Slot_preparedStatementId, value);
	}
	
	@Override
	public String getResultSetIdentifier() {
		return (String)slotGet(Slot_resultSetIdentifier);
	}
	
	@Override
	public void setResultSetIdentifier(String value) {
		slotSet(Slot_resultSetIdentifier, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Expression> getIntoExpressions() {
		return (List<Expression>)slotGet(Slot_intoExpressions);
	}
	
	@Override
	public Boolean hasExplicitSql() {
		return (Boolean)slotGet(Slot_hasExplicitSql);
	}
	
	@Override
	public void setHasExplicitSql(Boolean value) {
		slotSet(Slot_hasExplicitSql, value);
	}

}