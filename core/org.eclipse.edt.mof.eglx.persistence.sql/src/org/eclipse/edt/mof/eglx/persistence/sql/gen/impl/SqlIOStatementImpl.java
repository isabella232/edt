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
package org.eclipse.edt.mof.eglx.persistence.sql.gen.impl;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.impl.IOStatementImpl;
import org.eclipse.edt.mof.eglx.persistence.sql.gen.SqlActionStatement;


public abstract class SqlIOStatementImpl extends IOStatementImpl implements SqlActionStatement {
	private static int Slot_preparedStatement=0;
	private static int Slot_hasExplicitSql=1;
	private static int Slot_sqlString=2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + IOStatementImpl.totalSlots();
	}
	
	static {
		int offset = IOStatementImpl.totalSlots();
		Slot_preparedStatement += offset;
		Slot_hasExplicitSql += offset;
		Slot_sqlString += offset;
	}
	
	@Override
	public Expression getPreparedStatement() {
		return (Expression)slotGet(Slot_preparedStatement);
	}
	
	@Override
	public void setPreparedStatement(Expression value) {
		slotSet(Slot_preparedStatement, value);
	}
			
	@Override
	public Boolean hasExplicitSql() {
		return (Boolean)slotGet(Slot_hasExplicitSql);
	}
	
	@Override
	public void setHasExplicitSql(Boolean value) {
		slotSet(Slot_hasExplicitSql, value);
	}
	
	@Override
	public String getSqlString() {
		return (String)slotGet(Slot_sqlString);
	}
	
	@Override
	public void setSqlString(String value) {
		slotSet(Slot_sqlString, value);
	}
}
