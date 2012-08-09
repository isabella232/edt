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
import org.eclipse.edt.mof.eglx.persistence.sql.gen.SqlPrepareStatement;


public class SqlPrepareStatementImpl extends SqlIOStatementImpl implements SqlPrepareStatement {
	private static int Slot_sqlStringExpr=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + SqlIOStatementImpl.totalSlots();
	}
	
	static {
		int offset = SqlIOStatementImpl.totalSlots();
		Slot_sqlStringExpr += offset;
	}
	
	@Override
	public Expression getSqlStringExpr() {
		return (Expression)slotGet(Slot_sqlStringExpr);
	}
	
	@Override
	public void setSqlStringExpr(Expression value) {
		slotSet(Slot_sqlStringExpr, value);
	}
	
}
