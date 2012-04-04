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
package org.eclipse.edt.mof.egl.impl;

import org.eclipse.edt.mof.egl.ExitStatement;
import org.eclipse.edt.mof.egl.Expression;

public class ExitStatementImpl extends LabelStatementImpl implements ExitStatement {
	private static int Slot_exitStatementType=0;
	private static int Slot_returnExpr=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + LabelStatementImpl.totalSlots();
	}
	
	static {
		int offset = LabelStatementImpl.totalSlots();
		Slot_exitStatementType += offset;
		Slot_returnExpr += offset;
	}
	@Override
	public Integer getExitStatementType() {
		return (Integer)slotGet(Slot_exitStatementType);
	}
	
	@Override
	public void setExitStatementType(Integer value) {
		slotSet(Slot_exitStatementType, value);
	}
	
	@Override
	public Expression getReturnExpr() {
		return (Expression)slotGet(Slot_returnExpr);
	}
	
	@Override
	public void setReturnExpr(Expression value) {
		slotSet(Slot_returnExpr, value);
	}
	
}
