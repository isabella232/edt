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
package org.eclipse.edt.mof.egl.impl;

import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.Type;

public class AssignmentImpl extends ExpressionImpl implements Assignment {
	private static int Slot_LHS=0;
	private static int Slot_RHS=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_LHS += offset;
		Slot_RHS += offset;
	}
	@Override
	public LHSExpr getLHS() {
		return (LHSExpr)slotGet(Slot_LHS);
	}
	
	@Override
	public void setLHS(LHSExpr value) {
		slotSet(Slot_LHS, value);
	}
	
	@Override
	public Expression getRHS() {
		return (Expression)slotGet(Slot_RHS);
	}
	
	@Override
	public void setRHS(Expression value) {
		slotSet(Slot_RHS, value);
	}
	
	@Override
	public Type getType() {
		return getLHS().getType();
	}

}
