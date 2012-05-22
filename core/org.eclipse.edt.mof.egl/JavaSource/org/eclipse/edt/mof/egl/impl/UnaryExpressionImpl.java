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
import org.eclipse.edt.mof.egl.NoSuchFunctionError;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.UnaryExpression;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class UnaryExpressionImpl extends ExpressionImpl implements UnaryExpression {
	private static int Slot_expression=0;
	private static int Slot_operator=1;
	private static int Slot_operation=2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_expression += offset;
		Slot_operator += offset;
		Slot_operation += offset;
	}
	@Override
	public Expression getExpression() {
		return (Expression)slotGet(Slot_expression);
	}
	
	@Override
	public void setExpression(Expression value) {
		slotSet(Slot_expression, value);
	}
	
	@Override
	public String getOperator() {
		return (String)slotGet(Slot_operator);
	}
	
	@Override
	public void setOperator(String value) {
		slotSet(Slot_operator, value);
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
	public void setOperation(Operation value) {
		slotSet(Slot_operation, value);
	}
	
	protected Operation resolveOperation() {
		Operation op = IRUtils.getUnaryOperation(getExpression().getType().getClassifier(), getOperator());
		if (op == null) throw new NoSuchFunctionError();
		return op;
	}
	
	@Override
	public Type getType() {
		return getOperation().getType();
	}
	
	@Override
	public boolean isNullable() {
		return getOperation().isNullable();
	}

}
