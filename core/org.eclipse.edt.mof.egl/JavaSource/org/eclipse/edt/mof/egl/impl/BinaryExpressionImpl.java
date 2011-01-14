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
package org.eclipse.edt.mof.egl.impl;

import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;


public class BinaryExpressionImpl extends ExpressionImpl implements BinaryExpression {
	private static int Slot_LHS=0;
	private static int Slot_RHS=1;
	private static int Slot_operator=2;
	private static int Slot_operation=3;
	private static int totalSlots = 4;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_LHS += offset;
		Slot_RHS += offset;
		Slot_operator += offset;
		Slot_operation += offset;
	}
	@Override
	public Expression getLHS() {
		return (Expression)slotGet(Slot_LHS);
	}
	
	@Override
	public void setLHS(Expression value) {
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
	public Operation getOperation() {
		if (slotGet(Slot_operation) == null) {
			setOperation(IRUtils.getBinaryOperation(getLHS().getType().getClassifier(), getRHS().getType().getClassifier(), getOperator()));
		}
		return (Operation)slotGet(Slot_operation);
	}
	
	@Override
	public void setOperation(Operation value) {
		slotSet(Slot_operation, value);
	}
	
	@Override
	public Type getType() {
		return getOperation().getType();
	}

	@Override
	public String getOperator() {
		return (String)slotGet(Slot_operator);
	}
	
	@Override
	public void setOperator(String opSymbol) {
		slotSet(Slot_operator, opSymbol);
	}

}