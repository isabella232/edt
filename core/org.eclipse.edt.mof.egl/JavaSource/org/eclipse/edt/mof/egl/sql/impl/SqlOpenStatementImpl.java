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

import java.util.List;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.sql.SqlOpenStatement;

public class SqlOpenStatementImpl extends SqlGetByKeyStatementImpl implements SqlOpenStatement {
	private static int Slot_isHold=0;
	private static int Slot_isScroll=1;
	private static int Slot_resultSet=2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + SqlGetByKeyStatementImpl.totalSlots();
	}
	
	static {
		int offset = SqlGetByKeyStatementImpl.totalSlots();
		Slot_isHold += offset;
		Slot_isScroll += offset;
		Slot_resultSet += offset;
	}
	@Override
	public Boolean isHold() {
		return (Boolean)slotGet(Slot_isHold);
	}
	
	@Override
	public void setIsHold(Boolean value) {
		slotSet(Slot_isHold, value);
	}
	
	@Override
	public Boolean isScroll() {
		return (Boolean)slotGet(Slot_isScroll);
	}
	
	@Override
	public void setIsScroll(Boolean value) {
		slotSet(Slot_isScroll, value);
	}
	
	@Override
	public Expression getResultSet() {
		return (Expression)slotGet(Slot_resultSet);
	}
	
	@Override
	public void setResultSet(Expression value) {
		slotSet(Slot_resultSet, value);
	}
}
