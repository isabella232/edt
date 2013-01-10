/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.ForStatement;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.MemberName;

public class ForStatementImpl extends LoopStatementImpl implements ForStatement {
	private static int Slot_declarationExpression=0;
	private static int Slot_deltaExpression=1;
	private static int Slot_counterVariable=2;
	private static int Slot_fromExpression=3;
	private static int Slot_toExpression=4;
	private static int Slot_isIncrement=5;
	private static int Slot_label=6;
	private static int totalSlots = 7;
	
	public static int totalSlots() {
		return totalSlots + LoopStatementImpl.totalSlots();
	}
	
	static {
		int offset = LoopStatementImpl.totalSlots();
		Slot_declarationExpression += offset;
		Slot_deltaExpression += offset;
		Slot_counterVariable += offset;
		Slot_fromExpression += offset;
		Slot_toExpression += offset;
		Slot_isIncrement += offset;
		Slot_label += offset;
	}
	@Override
	public DeclarationExpression getDeclarationExpression() {
		return (DeclarationExpression)slotGet(Slot_declarationExpression);
	}
	
	@Override
	public void setDeclarationExpression(DeclarationExpression value) {
		slotSet(Slot_declarationExpression, value);
	}
	
	@Override
	public Expression getDeltaExpression() {
		return (Expression)slotGet(Slot_deltaExpression);
	}
	
	@Override
	public void setDeltaExpression(Expression value) {
		slotSet(Slot_deltaExpression, value);
	}
	
	@Override
	public Expression getCounterVariable() {
		Expression var = (Expression)slotGet(Slot_counterVariable);
		if (var == null) {
			// Assumes that there has to be a declaration if there is no variable
			MemberName mbrName = IrFactory.INSTANCE.createMemberName();
			mbrName.setMember(getDeclarationExpression().getFields().get(0));
			setCounterVariable(mbrName);
			var = mbrName;
		}
		return var;
	}
	
	@Override
	public void setCounterVariable(Expression value) {
		slotSet(Slot_counterVariable, value);
	}
	
	@Override
	public Expression getFromExpression() {
		return (Expression)slotGet(Slot_fromExpression);
	}
	
	@Override
	public void setFromExpression(Expression value) {
		slotSet(Slot_fromExpression, value);
	}
	
	@Override
	public Expression getToExpression() {
		return (Expression)slotGet(Slot_toExpression);
	}
	
	@Override
	public void setToExpression(Expression value) {
		slotSet(Slot_toExpression, value);
	}
	
	@Override
	public Boolean isIncrement() {
		return (Boolean)slotGet(Slot_isIncrement);
	}
	
	@Override
	public void setIsIncrement(Boolean value) {
		slotSet(Slot_isIncrement, value);
	}
	
	@Override
	public String getLabel() {
		return (String)slotGet(Slot_label);
	}
	
	@Override
	public void setLabel(String value) {
		slotSet(Slot_label, value);
	}
	
}
