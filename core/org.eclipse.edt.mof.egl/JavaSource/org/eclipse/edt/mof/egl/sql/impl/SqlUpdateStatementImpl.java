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

import org.eclipse.edt.mof.egl.sql.SqlClause;
import org.eclipse.edt.mof.egl.sql.SqlUpdateStatement;

public class SqlUpdateStatementImpl extends SqlReplaceStatementImpl implements SqlUpdateStatement {
	private static int Slot_callClause=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + SqlReplaceStatementImpl.totalSlots();
	}
	
	static {
		int offset = SqlReplaceStatementImpl.totalSlots();
		Slot_callClause += offset;
	}
	@Override
	public SqlClause getCallClause() {
		return (SqlClause)slotGet(Slot_callClause);
	}
	
	@Override
	public void setCallClause(SqlClause value) {
		slotSet(Slot_callClause, value);
	}
	
}
