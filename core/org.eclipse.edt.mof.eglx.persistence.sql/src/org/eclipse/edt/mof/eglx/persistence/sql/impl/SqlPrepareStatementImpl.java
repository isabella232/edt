/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.persistence.sql.impl;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlPrepareStatement;


public class SqlPrepareStatementImpl extends SqlIOStatementImpl implements SqlPrepareStatement {
	private static int Slot_fromExpression=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + SqlIOStatementImpl.totalSlots();
	}
	
	static {
		int offset = SqlIOStatementImpl.totalSlots();
		Slot_fromExpression += offset;
	}
	
	@Override
	public Expression getFromExpression() {
		return (Expression)slotGet(Slot_fromExpression);
	}
	
	@Override
	public void setFromExpression(Expression value) {
		slotSet(Slot_fromExpression, value);
	}
	
}
