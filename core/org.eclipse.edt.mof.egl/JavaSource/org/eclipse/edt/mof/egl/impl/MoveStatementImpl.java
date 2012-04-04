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

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.MoveStatement;

public class MoveStatementImpl extends StatementImpl implements MoveStatement {
	private static int Slot_sourceExpr=0;
	private static int Slot_targetExpr=1;
	private static int Slot_modifierExpr=2;
	private static int Slot_modifier=3;
	private static int totalSlots = 4;
	
	public static int totalSlots() {
		return totalSlots + StatementImpl.totalSlots();
	}
	
	static {
		int offset = StatementImpl.totalSlots();
		Slot_sourceExpr += offset;
		Slot_targetExpr += offset;
		Slot_modifierExpr += offset;
		Slot_modifier += offset;
	}
	@Override
	public Expression getSourceExpr() {
		return (Expression)slotGet(Slot_sourceExpr);
	}
	
	@Override
	public void setSourceExpr(Expression value) {
		slotSet(Slot_sourceExpr, value);
	}
	
	@Override
	public LHSExpr getTargetExpr() {
		return (LHSExpr)slotGet(Slot_targetExpr);
	}
	
	@Override
	public void setTargetExpr(LHSExpr value) {
		slotSet(Slot_targetExpr, value);
	}
	
	@Override
	public Expression getModifierExpr() {
		return (Expression)slotGet(Slot_modifierExpr);
	}
	
	@Override
	public void setModifierExpr(Expression value) {
		slotSet(Slot_modifierExpr, value);
	}
	
	@Override
	public int getModifier() {
		return (Integer)slotGet(Slot_modifier);
	}
	
	@Override
	public void setModifier(int value) {
		slotSet(Slot_modifier, value);
	}

}
