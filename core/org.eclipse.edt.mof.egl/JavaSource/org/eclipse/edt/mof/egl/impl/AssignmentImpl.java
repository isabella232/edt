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

import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.NoSuchFunctionError;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class AssignmentImpl extends ExpressionImpl implements Assignment {
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

	@Override
	public String getOperator() {
		return (String)slotGet(Slot_operator);
	}
	
	@Override
	public void setOperator(String opSymbol) {
		slotSet(Slot_operator, opSymbol);
	}

	@Override
	public Operation getOperation() {
		if (slotGet(Slot_operation) == null) {
			try {
				setOperation(resolveOperation());
			} catch (NoSuchFunctionError e) {
				throw new RuntimeException(e);
			}
		}
		return (Operation)slotGet(Slot_operation);
	}
	
	@Override
	public void setOperation(Operation op) {
		slotSet(Slot_operation, op);
	}
	
	protected Operation resolveOperation() {
		String operator = getOperator();
		if (operator.length() > 1 && operator.endsWith("=")) {
			Operation rhsOp = IRUtils.getBinaryOperation(getLHS().getType().getClassifier(), getRHS().getType().getClassifier(),
					operator.substring(0, operator.length() - 1));
			if (rhsOp == null) {
				throw new NoSuchFunctionError();
			}
			return rhsOp;
		}
		return null;
	}
}
